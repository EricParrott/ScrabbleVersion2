package com.eric.wordwizard;

import java.util.Comparator;

/**
 * Created by Eric on 2/28/2017.
 * Updated 4/12/17
 */

public class WordScoreComparator extends Sort implements Comparator<String> {
    //this method is used in the string comparator - it returns 1 when string1 > string2,
    //0 when string1 == string2, -1 when string1 < string2.  Collections.sort() calls
    //compare to sort the arraylist by wordscore.
    public int compare(String str1, String str2) {

        int score1 = getWordScore(str1);
        int score2 = getWordScore(str2);

        if (score1 > score2) {
            return -1;
        }

        else if (score1 == score2) {
            return 0;
        }

        else {
            return 1;
        }
    }
}