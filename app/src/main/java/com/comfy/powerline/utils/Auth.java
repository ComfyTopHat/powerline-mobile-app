package com.comfy.powerline.utils;
import android.text.Editable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Auth {
    public static boolean isValid(String pass) {
        int score = 0;
        Pattern pattern = Pattern.compile("[0-9]");
        Matcher matcher = pattern.matcher(pass);
        if(matcher.find()) {
            score = score + 1;
        }
        pattern = Pattern.compile("[a-z]");
        matcher = pattern.matcher(pass);
        if(matcher.find()) {
            score = score + 1;
        }
        pattern = Pattern.compile("[A-Z]");
        matcher = pattern.matcher(pass);
        if(matcher.find()) {
            score = score + 1;
        }
        return score == 3;
    }
}


