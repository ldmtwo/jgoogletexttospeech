/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ldtwo.GoTTS;

/**
 *
 * @author ldtwo
 */
public class DeckFile implements Comparable<Object>{
    public final String[] data;

    public DeckFile(String[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return data[0];
    }

    @Override
    public int compareTo(Object o) {
         return data[0].compareToIgnoreCase(o.toString());
    }

    @Override
    public boolean equals(Object o) {
        return data[0].equals(o.toString());
    }

    @Override
    public int hashCode() {
        return data[0].hashCode();
    }
    
}
