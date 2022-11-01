package com.palestiner.pass.model

import com.palestiner.pass.service.Encryptor
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.writeLines

data class KeyValue(
    val key: String,
    val value: String
) {

    companion object {

        private val passDataDir = "${System.getenv("HOME")}/pass"
        private val passDataFile = "$passDataDir/data"
        private val secretKey = System.getenv("PASS_SK")
        private val encryptor = Encryptor()

        fun loadPairs(): MutableList<KeyValue> {
            if (!Files.exists(Path.of(passDataDir))) {
                Files.createDirectory(Path.of(passDataDir))
                Files.createFile(Path.of(passDataFile))
            }
            val props = Properties()
            FileInputStream(File(passDataFile)).use {
                props.load(it)
            }
            return props.entries.map {
                val value = if (secretKey != null) encryptor.decrypt(secretKey, it.value as String)
                else it.value as String
                KeyValue(it.key as String, value)
            }
                .sortedBy { it.key }
                .toMutableList()
        }

        fun savePair(keyValue: KeyValue) {
            val value = if (secretKey != null) encryptor.encrypt(secretKey, keyValue.value)
            else keyValue.value
            val result = KeyValue(keyValue.key, value)
            File(passDataFile).appendText("$result\n", Charsets.UTF_8)
        }

        fun saveState(pairs: MutableList<KeyValue>) {
            Path.of(passDataFile).writeLines(pairs.map { it.toString() })
        }
    }

    override fun toString(): String {
        return "$key=$value"
    }


    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as KeyValue
        if (key != other.key) return false
        if (value != other.value) return false
        return true
    }
}
