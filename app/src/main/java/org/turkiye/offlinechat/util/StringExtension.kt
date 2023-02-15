package org.turkiye.offlinechat.util

/**
 * Created by ferhat.ozcelik on 12-02-2023.
 */

class StringExtension {
    companion object{
        fun String.capitalize(): String {
            return if (isNotEmpty() && this[0].isLowerCase()) substring(0, 1).toUpperCase() + substring(1) else this
        }
    }
}


