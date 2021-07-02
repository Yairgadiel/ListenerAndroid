package com.gy.listener.utilities;

import androidx.annotation.NonNull;

public final class TextUtils {

    /**
     * This method converts camelCase and SNAKE_CASE to PascalCase
     * @param input string to convert
     * @return the converted string
     */
    @NonNull
    public static String toPascalCase(@NonNull String input) {
        StringBuilder titleCase = new StringBuilder(input.length());
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c) || c == '_') {
                nextTitleCase = true;
            }
            else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            else {
                c = Character.toLowerCase(c);
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }
}
