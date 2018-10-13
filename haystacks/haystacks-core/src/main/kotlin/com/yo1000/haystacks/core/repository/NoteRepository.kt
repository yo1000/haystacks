package com.yo1000.haystacks.core.repository

import com.yo1000.haystacks.core.valueobject.FullyQualifiedName
import com.yo1000.haystacks.core.valueobject.Note
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

interface NoteRepository {
    fun findNoteMap(): Map<FullyQualifiedName, Note>
    fun findNoteMapByFullyQualifiedTableName(fullyQualifiedTableName: FullyQualifiedName): Map<FullyQualifiedName, Note>
    fun save(fullyQualifiedName: FullyQualifiedName, note: Note): Note
}

class PropertiesNoteRepository(
        private val propertiesFilePath: Path,
        private val createOnMissing: Boolean
) : NoteRepository {
    companion object {
        val noteMapLocker = ReentrantLock()
    }

    private var properties: Properties
    private var noteMap: Map<FullyQualifiedName, Note>

    init {
        checkPath(propertiesFilePath, createOnMissing)
        properties = reload(propertiesFilePath)
        noteMap = properties.toNoteMap()
    }

    override fun findNoteMap(): Map<FullyQualifiedName, Note> = noteMap

    override fun findNoteMapByFullyQualifiedTableName(
            fullyQualifiedTableName: FullyQualifiedName): Map<FullyQualifiedName, Note> =
            noteMap.filter { it.key.value.startsWith(fullyQualifiedTableName.value) }

    override fun save(fullyQualifiedName: FullyQualifiedName, note: Note): Note {
        noteMapLocker.withLock {
            properties.setProperty(fullyQualifiedName.value, note.value)
            properties.store(FileOutputStream(propertiesFilePath.toFile()), null)
            properties = reload(propertiesFilePath)
            noteMap = properties.toNoteMap()
        }

        return note
    }

    private fun checkPath(propertiesFilePath: Path, createOnMissing: Boolean) {
        if (Files.exists(propertiesFilePath)) return
        if (createOnMissing) {
            Files.createFile(propertiesFilePath)
            return
        }

        throw FileNotFoundException("$propertiesFilePath is missing")
    }

    private fun reload(propertiesFilePath: Path): Properties = Properties().also { props ->
        FileInputStream(propertiesFilePath.toFile()).use {
            InputStreamReader(it, Charset.defaultCharset()).use {
                props.load(it)
            }
        }
    }

    private fun Properties.toNoteMap(): Map<FullyQualifiedName, Note> = this.map {
        FullyQualifiedName(it.key as String) to Note(it.value as String)
    }.toMap()
}
