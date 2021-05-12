package com.image.manager.edgeserver.common;

public class NumberUtils {

    public static int getDigits(int value) {
        if(value == 0) {
            return 1;
        }

        int digits = 0;
        while(value > 0) {
            value /= 10;
            digits++;
        }

        return digits;
    }

}
