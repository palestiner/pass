package com.palestiner.pass.model

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

data class KeyValue(
    val name: String,
    val value: String
) {
    companion object {

        private const val passDataDir = "./pass"
        private const val passDataFile = "$passDataDir/data"

        fun loadPairs(): MutableList<KeyValue> {
            if (!Files.exists(Path.of(passDataDir))) {
                Files.createDirectory(Path.of(passDataDir))
                Files.createFile(Path.of(passDataFile))
            }
            val inputStream: InputStream = FileInputStream(File(passDataFile))
            val props = Properties()
            props.load(inputStream)
            inputStream.close()
            return props.entries.map { KeyValue(it.key as String, it.value as String) }
                .sortedBy { it.name }
                .toMutableList()
        }

        fun savePair(keyValue: KeyValue) {
            File(passDataFile).appendText(
                "$keyValue\n",
                Charsets.UTF_8
            )
        }
    }

    override fun toString(): String {
        return "$name=$value"
    }


    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as KeyValue
        if (name != other.name) return false
        if (value != other.value) return false
        return true
    }
}
