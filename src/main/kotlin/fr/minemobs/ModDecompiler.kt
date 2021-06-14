package fr.minemobs

import net.lingala.zip4j.ZipFile
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.io.path.name

class ModDecompiler {

    private fun unzipFolderZip4j(source: File) {
        val zipFile = ZipFile(source)
        if(zipFile.isEncrypted) {
            return
        }
        zipFile.removeFile("pack.mcmeta")
        zipFile.extractAll("uploads/${source.nameWithoutExtension}")
    }

    private fun decompile(modFile: File) : File {
        unzipFolderZip4j(modFile)
        return File("uploads/" + modFile.nameWithoutExtension)
    }

    fun containsMCreator(file: File) : Boolean {
        val folder = decompile(file)
        if(containsMCreatorModInfos(folder)) return true
        return containsMCreatorPackages(folder)
    }

    private fun containsMCreatorModInfos(folder: File): Boolean {
        for (path in Files.walk(Paths.get(folder.toURI())).filter(Files::isRegularFile)) {
            if(path.name.equals("pack.mcmeta", true) || path.name.equals("mods.toml", true)) {
                val lines = FileUtils.readLines(path.toFile(), Charsets.UTF_8)
                return lines.stream().filter { line -> line.contains("mcreator", true) }.collect(Collectors.toList()).isNotEmpty()
            }
        }
        return false
    }

    private fun containsMCreatorPackages(folder: File): Boolean {
        for (path in Files.walk(Paths.get(folder.toURI())).filter(Files::isDirectory)) {
            if(path.name.equals("mcreator", true)) {
                return true
            }
        }
        return false
    }
}
