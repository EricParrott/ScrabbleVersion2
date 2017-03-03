package com.example.eric.scrabbleversion2;

/**
 * Created by Eric on 3/2/2017.
 */


import java.util.Comparator;

/**
 * Created by Eric on 2/28/2017.
 */

public class WordLengthComparator extends Sort implements Comparator<String> {

    public int compare(String str1, String str2) {

        int length1 = str1.length();
        int length2 = str2.length();

        if (length1 > length2) {
            return 1;
        }

        else if (length1 == length2) {
            return 0;
        }

        else {
            return -1;
        }
    }
}
