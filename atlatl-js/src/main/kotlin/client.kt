import common.types.FirstName
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onBlurFunction
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onSubmitFunction
import kotlinx.serialization.json.JSON
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document

fun main(args: Array<String>) {
    render(document.getElementById("root")) {
        app()
    }
}

interface AppState : RState {
    var firstName: String
    var touched: HashSet<String>
}

data class Errors(
        var firstName: String?
)

class App : RComponent<RProps, AppState>() {
    override fun AppState.init() {
        firstName = ""
        touched = HashSet()
    }

    private fun validate(inputs: String): Errors {
        return if (inputs.length > 1) {
            Errors(firstName = null)
        } else {
            Errors("Name should be longer than one character")
        }
    }

    private fun handleBlur(event: Event) {
        val target = event.target as HTMLInputElement
        setState {
            touched.add(target.id)
        }.also { console.log(target.id) }
    }

    private fun handleFirstNameChange(event: Event) {
        val target = event.target as HTMLInputElement
        setState {
            firstName = target.value
        }
    }

    /*
    Yes, this is an absolute ghastly nightmare of a request function. Yes it needs serious refactoring.
    I left it a mess after fussing around trying to get fetch() to work without success. Ideally, that's
    what I'd prefer to use. This just got the job done in a hurry.
     */
    private fun handleSubmit(event: Event) {
        event.preventDefault()
        if (state.firstName.isNotBlank()) {
            val postData = FirstName(firstName = state.firstName)
            val post = XMLHttpRequest()
            post.open(method = "POST", url = "http://localhost:7000/")
            post.setRequestHeader("Access-Control-Allow-Origin", "*")
            post.send(body = JSON.stringify(postData))
            post.onreadystatechange = { _ ->
                if (post.readyState == XMLHttpRequest.DONE && (post.status in 200..299)) {
                    console.log("Data sent successfully!")
                    console.log(postData)
                }
            }
        } else {
            val post = XMLHttpRequest()
            post.open(method = "GET", url = "http://localhost:7000/")
            post.setRequestHeader("Access-Control-Allow-Origin", "*")
            post.send()
            post.onreadystatechange = { _ ->
                if (post.readyState == XMLHttpRequest.DONE && (post.status in 200..299)) {
                    console.log("Data received successfully!")
                    console.log(post.response)
                }
            }
        }
    }

    override fun RBuilder.render() {
        val errors = validate(inputs = state.firstName)
        form {
            attrs {
                onSubmitFunction = {
                    handleSubmit(it)
                }
            }
            label {
                +"First Name"
                input {
                    attrs {
                        id = "firstName"
                        type = InputType.text
                        placeholder = "Enter first name"
                        value = state.firstName
                        onBlurFunction = {
                            handleBlur(it)
                        }
                        onChangeFunction = {
                            handleFirstNameChange(it)
                        }
                    }
                }
            }
            if (!errors.firstName.isNullOrEmpty() && state.touched.contains("firstName")) {
                p("error-message") {
                    +errors.firstName!!
                }
            }
            button {
                if (state.firstName.isNotBlank()) {
                    +"Sign up as ${state.firstName}"
                } else {
                    +"Sign up"
                }
            }
        }
    }
}




fun RBuilder.app() = child(App::class) {}