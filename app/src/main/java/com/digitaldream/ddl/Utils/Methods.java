package com.digitaldream.ddl.Utils;

public class Methods {


    public static String capitaliseFirstLetter(String sS) {

        String[] strings = sS.toLowerCase().split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for (String letter : strings) {
            try {
                String words =
                        letter.substring(0, 1).toUpperCase() + letter.substring(1).toLowerCase();
                stringBuilder.append(words).append(" ");
            } catch (Exception sE) {
                sE.printStackTrace();
            }

        }
        return stringBuilder.toString();
    }

    public static String abbreviate(String sS) {

        String[] strings = sS.toLowerCase().split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for (String letter : strings) {
            try {
                String words =
                        letter.substring(0, 1).toUpperCase();
                stringBuilder.append(words);
            } catch (Exception sE) {
                sE.printStackTrace();
            }

        }
        return stringBuilder.toString();
    }
}
