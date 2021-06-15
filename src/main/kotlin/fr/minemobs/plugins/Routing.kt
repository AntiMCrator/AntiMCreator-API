package fr.minemobs.plugins

import fr.minemobs.ModDecompiler
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import kotlin.concurrent.thread

fun Application.configureRouting() {

    val logger : Logger = Logger.getLogger("AntiMCreator")

    routing {
        var fileName: String
        post("/upload") {
            try {
                val multipartData = call.receiveMultipart()
                multipartData.forEachPart { part ->
                    when(part) {
                        is PartData.FileItem -> {
                            fileName = part.originalFileName as String
                            val fileBytes = part.streamProvider().readBytes()
                            val parent = File("uploads")
                            if(!parent.exists()) {
                                parent.mkdir()
                            }
                            val file = File(parent, fileName)
                            file.createNewFile()
                            file.writeBytes(fileBytes)
                            call.respondText(ModDecompiler().containsMCreator(file).toString())
                            thread(start = true) {
                                Thread.sleep(TimeUnit.SECONDS.toMillis(10))
                                FileUtils.forceDelete(file)
                                FileUtils.forceDelete(File(parent, file.nameWithoutExtension))
                                logger.info("File deleted")
                            }
                            return@forEachPart
                        }
                        else -> {
                            call.respondText("Mod file not found", status = HttpStatusCode.NotFound)
                        }
                    }
                }
            } catch (e: Exception) {
                call.respondText("No file has been provided")
                logger.info(e.message)
            }
        }
    }
}

fun forceDelete(file: File) {
    if (file.isDirectory) {
        file.deleteRecursively()
    } else {
        val filePresent = file.exists()
        if (!file.delete()) {
            if (!filePresent) {
                throw FileNotFoundException("File does not exist: $file")
            }
            throw IOException("Unable to delete file: $file")
        }
    }
}