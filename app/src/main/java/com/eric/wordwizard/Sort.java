package com.eric.wordwizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.eric.wordwizard.Permutations.matchesWithPoints;

/**
 * Created by Eric on 2/28/2017.
 * Updated 4/12/17
 */

public class Sort {

    public static void sortAlphabetically(ArrayList<String> playableWords) {
        Collections.sort(playableWords);
    }

    public static void sortByWordScore(ArrayList<String> playableWords) {
        Collections.sort(playableWords, new WordScoreComparator());	//see my WordScoreComparator class
    }

    public static void sortByWordLength(ArrayList<String> playableWords) {
        Collections.sort(playableWords, new WordLengthComparator());
    }

    //converts all strings in arraylist to equivalent uniform lowercase string
    public static void stringListToLowerCase(ArrayList<String> playableWords) {
        for (int i=0; i<playableWords.size(); i++) {
            playableWords.set(i, playableWords.get(i).toLowerCase());
        }
    }

    //method that generates new arraylist that tacks on the point value of the string to the string
    public static ArrayList<String> addScoresToList(ArrayList<String> playableWords) {
        matchesWithPoints = new ArrayList<>(playableWords);
        for(int i = 0; i< matchesWithPoints.size(); i++) {
            matchesWithPoints.set(i, matchesWithPoints.get(i) +
                    " (" + getWordScore(matchesWithPoints.get(i)) + ")");
        }
        return matchesWithPoints;
    }

    //get score for word so as can be sorted by that score later on
    public static int getWordScore(String word) {
        //initialize word score to zero
        int score=0;
        //for length of the word
        for (int i=0; i<word.length(); i++) {
            //get each character in the word and add its point value to the score variable
            //the ascii values are for lowercase so make sure the input word has been converted
            //to lowercase before calling the method otherwise it will break.
            int charAt = (int)word.toLowerCase().charAt(i);
            switch (charAt) {
                case 97:   score += 1;  break;		//if 'a' append 1 pt to word score
                case 98:   score += 3;  break;		//if 'b' append 3 pts to word score
                case 99:   score += 3;  break;		//etc
                case 100:  score += 2;  break;
                case 101:  score += 1;  break;
                case 102:  score += 4;  break;
                case 103:  score += 2;  break;
                case 104:  score += 4;  break;
                case 105:  score += 1;  break;
                case 106:  score += 8;  break;
                case 107:  score += 5;  break;
                case 108:  score += 1;  break;
                case 109:  score += 3;  break;
                case 110:  score += 1;  break;
                case 111:  score += 1;  break;
                case 112:  score += 3;  break;
                case 113:  score += 10; break;
                case 114:  score += 1;  break;
                case 115:  score += 1;  break;
                case 116:  score += 1;  break;
                case 117:  score += 1;  break;
                case 118:  score += 4;  break;
                case 119:  score += 4;  break;
                case 120:  score += 8;  break;
                case 121:  score += 4;  break;
                case 122:  score += 10; break;
                default:   score = 0;   break;
            }
        }
        return score;
    }

    //this chunk of code is used in Main to filter out any results that contain chars not in letterbank--
    public static Set<Character> stringToCharacterSet(String s) {
        Set<Character> set = new HashSet<>();
        for (char c : s.toCharArray()) {
            set.add(c);
        }
        return set;
    }

    public static boolean containsAllChars(String container, String containee) {
        return stringToCharacterSet(container).containsAll(stringToCharacterSet(containee));
    }
}
