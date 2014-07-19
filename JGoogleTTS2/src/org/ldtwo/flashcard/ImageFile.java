/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ldtwo.flashcard;

import java.io.File;
import javax.swing.AbstractButton;

/**
 *
 * @author ldtwo
 */
public class ImageFile implements Comparable<ImageFile>{
     final File file;
     boolean enabled=true;
     String log="";
     AbstractButton button=null;

    public ImageFile(File file) {
        this.file = file;
    }
     
    @Override
    public String toString() {
        if(file==null)return "null";
        return file.getAbsolutePath();
    }

    @Override
    public int compareTo(ImageFile o) {
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
