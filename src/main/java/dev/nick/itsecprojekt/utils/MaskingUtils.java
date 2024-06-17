package dev.nick.itsecprojekt.utils;

public class MaskingUtils {

    public static String anonymize(String email) {
        if (email == null || email.isEmpty()) {
            return "unknown";
        }

        int index = email.indexOf('@');
        if (index <= 1) {
            return "***" + email.substring(index);
        }

        return email.substring(0, 1) + "***" + email.substring(index - 1);
    }
}