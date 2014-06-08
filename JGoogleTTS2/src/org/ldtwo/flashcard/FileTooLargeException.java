/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ldtwo.flashcard;

/**
 *
 * @author ldtwo
 */
public class FileTooLargeException extends Exception{

    public FileTooLargeException(long fileSize) {
        super(String.format("file is too large >> %s Kbytes", fileSize/1024));
    }
    public FileTooLargeException(long fileSize, String url) {
        super(String.format("file is too large: %s Kbytes   ====>>>>   %s", fileSize/1024, url));
    }
    
}
