/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ldtwo.flashcard;

import java.io.File;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import org.ldtwo.GoTTS.G;

/**
 *
 * @author ldtwo
 */
public class Term implements Comparable<Term>, Comparator<Term> {
    
    static final DecimalFormat df = new DecimalFormat("0.#");
//moving average weights
    final double alpha = 0.4;//% time
    final double beta = 0.7;//% accuracy
    public final String left, right;
    public HashSet leftSet=new HashSet(), rightSet=new HashSet();
    public int views = 0;
    private double avgTime = 1, recentTime = 0, avgAccuracy = 0;
    public boolean visible = true;

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

    public int getAvgAccuracy() {
        return (int) (avgAccuracy*20);
    }

    public void setLastAccuracy(int i) {
        avgAccuracy = (1 - beta) * avgAccuracy + i*beta;
    }

    public double getAvgTime() {
        return avgTime / 1000;
    }

    public double getRecentTime() {
        return recentTime / 1000;
    }

    public int skillRating() {
        double ret = -1;
        try {
            ret = 200.0 * (avgAccuracy / (Math.log(2000 + avgTime) / Math.log(1.7)));
        } catch (Throwable e) {
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

    void setLastTime(long curTime) {
        avgTime = (1 - alpha) * recentTime + alpha * curTime;
        recentTime = curTime;
    }

    void println() {
        System.out.printf("%30s  %30s\n",left,right );
    }

    public String info() {
        return String.format("%38s == %-37s  ::::  %s images <---> %s images ",
                "\"" + G.makeValidFileName(left) + "\"", "\"" + G.makeValidFileName(right) + 
                        "\"", leftSet.size(), rightSet.size());
    }
    @Override
    public String toString() {
        return String.format("%38s == %-37s: %2s views, %3s sec (last), %3s sec (avg), %3s%%, %3s pts\n",
                "\"" + left + "\"", "\"" + right + "\"", views,
                df.format(getRecentTime()), df.format(getAvgTime()),
                getAvgAccuracy(), skillRating());
    }
    public static String toHTML(Collection<Term> list){
        String out="";
        try {
            synchronized(list){
                list=new ArrayList(list);
            }
            for (Term t : list) {
                String files = "";
                for (Object o : t.leftSet) {
                    File f = (File) o;
                    files += String.format("\t\t<IMG width=\"100\" height=\"150\" src=\"file:///%s\"/>\n", URLEncoder.encode(f.getAbsolutePath(), "UTF-8"));
                }
                String left = String.format("<LI>%s \n%s</LI>", t.left, files);
                files = "";
                for (Object o : t.rightSet) {
                    File f = (File) o;
                    files += String.format("\t\t<IMG width=\"100\" height=\"150\" src=\"file:///%s\"/>\n", URLEncoder.encode(f.getAbsolutePath(), "UTF-8"));
                }
                String right = String.format("<LI>%s \n%s</LI>", t.right, files);
                String ss = String.format("<P><UL>%s\n%s</UL></P>", left, right);
                out += ss + "\n";
            }
        } catch (Exception ex) {ex.printStackTrace();
        }
        return out;
    }
}
