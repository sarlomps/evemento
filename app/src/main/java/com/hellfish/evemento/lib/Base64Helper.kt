package com.hellfish.evemento.lib

import java.io.File
import android.util.Base64

class Base64Helper{
    companion object {
        fun encode(filePath: String): String{
            val bytes = File(filePath).readBytes()
            val base64 = Base64.encodeToString(bytes, android.util.Base64.DEFAULT)
            return base64
        }

        fun decode(base64Str: String, pathFile: String) {
            val imageByteArray = Base64.decode(base64Str, android.util.Base64.DEFAULT)
            File(pathFile).writeBytes(imageByteArray)
        }
    }
}
