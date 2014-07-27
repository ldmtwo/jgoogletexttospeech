/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ldtwo.flashcard;

import java.io.File;
import java.util.HashSet;
import javax.swing.AbstractButton;

/**
 *
 * @author ldtwo
 */
public class ImageFile implements Comparable<ImageFile>{
     final File file;
     final HashSet<ImageFile> set;
     final String url1,url2;
     boolean enabled=true;
     String log="";
     AbstractButton button=null;
     int priority=50;

    public ImageFile(File file, HashSet<ImageFile> set, String url1, String url2) {
        this.file = file;
        this.set = set;
        this.url1 = url1;
        this.url2 = url2;
    }

//    public ImageFile(File file, String url1, String url2) {
//        this.file = file;
//        this.url1 = url1;
//        this.url2 = url2;
//    }
//
//  
//
//    public ImageFile(File file) {
//        this.file = file;
//    }
     
    @Override
    public String toString() {
        if(file==null)return "null";
        return file.getAbsolutePath();
    }

    @Override
    public int compareTo(ImageFile o) {
        if(this.priority<o.priority)return 1;
        if(this.priority>o.priority)return -1;
         return this.toString().compareToIgnoreCase(o.toString());
    }

    @Override
    public boolean equals(Object o) {
        return toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }
    
}
