package com.project.trackernity.other

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Cryptography {
    private val encorder: Base64.Encoder = Base64.getEncoder()
    private val decorder: Base64.Decoder = Base64.getDecoder()
    lateinit var ivParam : ByteArray

    @Throws(Exception::class)
    private fun cipher(opmode: Int, secretKey: String): Cipher {
        if (secretKey.length != 32) throw RuntimeException("SecretKey length is not 32 chars")
        val c: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val sk = SecretKeySpec(secretKey.toByteArray(), "AES")
        ivParam = secretKey.substring(0, 16).toByteArray()
        val iv = IvParameterSpec(ivParam) //0~16은 서버와 합의!
        c.init(opmode, sk, iv)
        return c
    }

    private fun bytesToHex(`in`: ByteArray): String? {
        val builder = StringBuilder()
        for (b in `in`) {
            builder.append(String.format("%02x", b))
        }
        return builder.toString()
    }

    fun encrypt(str: String, secretKey: String): String? {
        return try {
            val encrypted: ByteArray = cipher(
                Cipher.ENCRYPT_MODE,
                secretKey
            ).doFinal(str.toByteArray(charset("UTF-8")))

            /////base64/////////
//            String(encorder.encode(encrypted))

            bytesToHex(encrypted)
        } catch (e: Exception) {
            null
        }
    }

    fun getIV():String?{
        return try {
            bytesToHex(ivParam)
        }catch (e:Exception){
            null
        }
    }

    fun decrypt(str: String, secretKey: String): String? {
        return try {
            val byteStr: ByteArray = decorder.decode(str.toByteArray())
            String(cipher(Cipher.DECRYPT_MODE, secretKey).doFinal(byteStr), charset("UTF-8"))
        } catch (e: Exception) {
            null
        }
    }
}