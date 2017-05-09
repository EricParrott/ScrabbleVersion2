package com.eric.wordwizard;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Eric on 2/28/2017.
 * Updated 4/12/17
 */

public class Permutations extends MainActivity {
    //data structures definitions
    static Hashtable<Integer, String> dictionary = new Hashtable<>();

    static ArrayList<String> combinations = new ArrayList<>();
    static ArrayList<String> allPermutations = new ArrayList<>();
    static ArrayList<String> matches = new ArrayList<>();
    static ArrayList<String> reorderedMatches = new ArrayList<>();
    static ArrayList<String> matchesWithPoints = new ArrayList<>();

    //returns all combinations given a string
    static void combine(String instr, StringBuffer outstr, int index) {
        for (int i = index; i < instr.length(); i++) {
            outstr.append(instr.charAt(i));
            String combo = outstr.toString();
            combinations.add(combo);
            combine(instr, outstr, i + 1);
            outstr.deleteCharAt(outstr.length() - 1);
        }
    }

    //returns all the permutations of a given string
    public static ArrayList<String> permutation(String s) {
        ArrayList<String> res = new ArrayList<>();
        if (s.length() == 1) {
            res.add(s);
        } else if (s.length() > 1) {
            int lastIndex = s.length() - 1;
            String last = s.substring(lastIndex);
            String rest = s.substring(0, lastIndex);
            res = merge(permutation(rest), last);
        }
        return res;
    }

    //sub-routine of permutation method above
    public static ArrayList<String> merge(ArrayList<String> list, String c) {
        ArrayList<String> res = new ArrayList<>();
        for (String s : list) {
            for (int i = 0; i <= s.length(); ++i) {
                String ps = new StringBuffer(s).insert(i, c).toString();
                res.add(ps);
            }
        }
        return res;
    }
}
