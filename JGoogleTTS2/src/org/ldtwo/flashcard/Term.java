/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ldtwo.flashcard;

import java.util.Comparator;

/**
 *
 * @author ldtwo
 */
public class Term implements Comparable<Term>, Comparator<Term> {

    public final String left, right;
    public int views = 0;
    public double avgTime = 1, recentTime = 0, avgAccuracy = 0;
    public boolean visible=true;

    public Term(String left, String right) {
        this.left = left.trim();
        this.right = right.trim();
    }

    public Term() {
        this.left = "END OF DECK";
        this.right = "THE END";
    }

    public void run() {

    }

    public int skillRating() {
        double ret = -1;
        try {
            ret = 1000000.0 * (avgAccuracy /(avgTime*avgTime));
        } catch (Exception e) {
        }
        return (int) ret;
    }

    @Override
    public int compareTo(Term x) {
        int a = (int) this.skillRating(), b = (int) x.skillRating();
        if (a > b) {
            return 1;
        } else if (a < b) {
            return -1;
        } else if (this.avgTime < x.avgTime) {
            return 1;
        } else if (this.avgTime > x.avgTime) {
            return -1;
        }
        return 0;
    }

    @Override
    public int compare(Term o1, Term o2) {
        return o1.compareTo(o2);
    }
}
