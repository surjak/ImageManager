package com.image.manager.edgeserver.common;

public class NumberUtils {

    public static int getDigits(int value) {
        if(value == 0) {
            return 1;
        }

        return (int) Math.floor(Math.log10(value) + 1);
    }

}
