package com.kickstartlab.android.jayonpickup;

import java.util.Random;

/**
 * Created by awidarto on 1/12/14.
 */
public class Gen {

    Random random = new Random();
    private static final String _CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    private static final int RANDOM_STR_LENGTH = 12;

    public String randomAlpha(int length){
        StringBuffer randStr = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = getRandomPos();
            char ch = _CHAR.charAt(number);
            randStr.append(ch);
        }
        return randStr.toString();
    }

    private int getRandomPos() {
        int randomInt = 0;
        randomInt = random.nextInt(_CHAR.length());
        if (randomInt - 1 == -1) {
            return randomInt;
        } else {
            return randomInt - 1;
        }
    }
}
