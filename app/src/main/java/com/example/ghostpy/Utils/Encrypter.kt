package com.example.ghostpy.Utils

import java.io.InputStream
import java.io.OutputStream
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object Encrypter {
    private val DEFAULT_BUFFER_SIZE = 1024
    private val ALGO_IMG_ENCRYPT = "AES/CBC/PKCS5Padding"
    private val ALGO_SECRET_KEY = "AES"

    @Throws(NoSuchAlgorithmException::class,
    NoSuchPaddingException::class,
    InvalidKeyException::class,
    InvalidAlgorithmParameterException::class)

    fun encryptToFile(keyStr:String,spec:String,input: InputStream,output: OutputStream){
        var output = output
        try {
            val iv = IvParameterSpec(spec.toByteArray(charset("UTF-8")))
            val keySpec = SecretKeySpec(keyStr.toByteArray(charset("UTF-8")),ALGO_SECRET_KEY)
            val c = Cipher.getInstance(ALGO_IMG_ENCRYPT)
            c.init(Cipher.ENCRYPT_MODE,keySpec,iv)
            output = CipherOutputStream(output,c)

            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytesRead:Int = 0
            while (input.read(buffer).also { bytesRead = it } > 0)
                output.write(buffer,0,bytesRead)
        }finally {
            output.close()
        }
    }

    fun decryptToFile(keyStr:String,spec:String,input: InputStream,output: OutputStream){
        var output = output
        try {
            val iv = IvParameterSpec(spec.toByteArray(charset("UTF-8")))
            val keySpec = SecretKeySpec(keyStr.toByteArray(charset("UTF-8")),ALGO_SECRET_KEY)
            val c = Cipher.getInstance(ALGO_IMG_ENCRYPT)
            c.init(Cipher.DECRYPT_MODE,keySpec,iv)
            output = CipherOutputStream(output,c)

            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytesRead:Int = 0
            while (input.read(buffer).also { bytesRead = it } > 0)
                output.write(buffer,0,bytesRead)
        }finally {
            output.close()
        }
    }
}