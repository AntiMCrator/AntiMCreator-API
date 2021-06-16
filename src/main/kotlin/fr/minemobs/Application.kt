package fr.minemobs

import fr.minemobs.plugins.configureRouting
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8001) {
        configureRouting()
        install(ContentNegotiation) {
            json()
        }
    }.start(wait = true)
}
