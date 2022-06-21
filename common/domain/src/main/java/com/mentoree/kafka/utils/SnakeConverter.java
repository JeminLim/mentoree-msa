package com.mentoree.kafka.utils;

public class SnakeConverter {

    public static String convertToSnakeCase(String str) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        str = str.replaceAll(regex, replacement).toLowerCase();
        return str;
    }

}
