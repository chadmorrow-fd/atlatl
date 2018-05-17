import common.types.HelloWorld
import io.javalin.Javalin
import kotlinx.serialization.json.JSON

fun main(args: Array<String>) {

    /*
    Create our server app a little like we would with an Express server app.
     */
    val app = Javalin.create().apply {

        /*
        This lets us develop locally without Chrome griping at us about cross-origin nonsense.
        Don't launch this in production or anything.
         */
        enableCorsForOrigin("*")

        /*
        The default was 8080 but I chose 7000 arbitrarily.
         */
        port(7000)
    }.start()

    /*
    Routes should look very very similar to an Express app.

    In this case, I have two to show off the kotlin serialization library a little.
    The HelloWorld() there is a serializable data class common to both the front end and the back end.
    The Post route anticipates getting a different common data class, FirstName().
    I'll start by just printing that out to the server stdout.
     */
    app.get("/") { ctx -> ctx.result(JSON.stringify(HelloWorld())).also { println("sent: ${HelloWorld()}") } }

    app.post("/") { ctx -> println(ctx.body()) }

}

