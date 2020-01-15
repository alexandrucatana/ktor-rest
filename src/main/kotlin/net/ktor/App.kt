package net.ktor

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.ktor.dao.DAOFacadeDatabase
import net.ktor.model.Address
import org.jetbrains.exposed.sql.Database

val dao = DAOFacadeDatabase(
        Database.connect("jdbc:mysql://root:root@localhost:3306/humanbeings_db?useUnicode=true&serverTimezone=UTC",
                driver = "com.mysql.jdbc.Driver"))
fun main() {
    embeddedServer(Netty, port = 8080){
        dao.init()
        install(CallLogging)
        install(ContentNegotiation){
            jackson {}
        }
        routing {
            route("/employees"){
                get {
                    call.respond(dao.getAllAddresses())
                }
                post {
                    val emp = call.receive<Address>()
                    dao.createAddress(emp.name, emp.email, emp.city)
                }
                put {
                    val emp = call.receive<Address>()
                    dao.updateAddress(emp.id, emp.name, emp.email, emp.city)
                }
                delete("/{id}") {
                    val id = call.parameters["id"]
                    if(id != null)
                        dao.deleteAddress(id.toInt())
                }
            }
        }
    }.start(wait = true)
}

