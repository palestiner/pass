package com.palestiner.pass.service

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class Encryptor {

    private val initVector = "RandomInitVector"

    fun encrypt(key: String, value: String): String {
        try {
            val iv = IvParameterSpec(initVector.toByteArray(charset("UTF-8")))
            val skeySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")
            val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
            val encrypted: ByteArray = cipher.doFinal(value.toByteArray())
            return Base64.getEncoder().encodeToString(encrypted)
        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }
    }

    fun decrypt(key: String, encrypted: String): String {
        try {
            val iv = IvParameterSpec(initVector.toByteArray(charset("UTF-8")))
            val skeySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
            val original = cipher.doFinal(Base64.getDecoder().decode(encrypted))
            return String(original)
        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }
    }
}
