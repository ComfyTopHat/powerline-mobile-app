package com.comfy.powerline.utils

import java.util.regex.Pattern

object Auth {
    @JvmStatic
    fun isValid(pass: String?): Boolean {
        var score = 0
        var pattern = Pattern.compile("[0-9]")
        var matcher = pattern.matcher(pass)
        if (matcher.find()) {
            score = score + 1
        }
        pattern = Pattern.compile("[a-z]")
        matcher = pattern.matcher(pass)
        if (matcher.find()) {
            score = score + 1
        }
        pattern = Pattern.compile("[A-Z]")
        matcher = pattern.matcher(pass)
        if (matcher.find()) {
            score = score + 1
        }
        return score == 3
    }
}