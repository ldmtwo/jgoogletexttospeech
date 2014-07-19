/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ldtwo.GoTTS;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import static org.ldtwo.GoTTS.Languages.*;

/**
 *
 * @author ldtwo
 */
public class G {

    public final static String RECENT_DECKS_FILE = ".recentDecks.txt";
   public final static String IMAGE_PATH = "CACHE\\IMAGES\\";
   public final static String RESPONSE_PATH = "CACHE\\RESPONSES\\";
   public final static String AUDIO_PATH = "CACHE\\AUDIO\\";
   public final static String CACHE_PATH = "CACHE\\";
    
    
    public static boolean CONFIRM_CLOSE = false;
    public static final int[] FONT_SIZES = {5, 8, 10, 11, 12, 14, 18, 24, 36, 48};
    public final static Random RAND = new SecureRandom();
    public static HashSet<File> openFiles = new HashSet<>();
    public static LinkedList<EditorPanel> tabList = new LinkedList<>();
    public static long delay = 1;
    public static final String DELIM = "[\\n]", ANTIDELIM = "[^\\n]";
    public static final String DELIM2 = "[,.\\n]", ANTIDELIM2 = "[^,.\\n]";
    public static boolean play = false;
    public final static AtomicBoolean pause = new AtomicBoolean(false);
    public final static AtomicInteger playersCount=new AtomicInteger(0);
    public static boolean skip = false;
    public static int skipDelta = 0;
    public final static boolean SIMULATION = false;
    static boolean ENABLE_DRAIN_QUEUE=true;
  
    //static int activeTabNum=0;

    static {
        String[] arr;
        for (String s : LANGS) {
            arr = s.split("\t");
            LA_LANGUAGE.put(arr[0], arr[1]);
            LANGUAGE_LA.put(arr[1], arr[0]);
        }

    }

    public static boolean saveFile(EditorPanel p, boolean askForFileName) {
        if (p.file == null || askForFileName) {
            int selectedIndex = MainFrame.ths.tabPane.getSelectedIndex();
            MainFrame.ths.tabPane.setSelectedComponent(p);//temporarily switch to current tab
            JFileChooser fc = G.fileChooser;
            fc.showSaveDialog(null);
            p.file = fc.getSelectedFile();
            if (p.file == null) {
                return false;
            }
            MainFrame.ths.tabPane.setSelectedIndex(selectedIndex);
        }
        if (textToFile(p.file, p.txt.getText())) {//write file
            return false;
        }
        p.modified = false;
        p.updateTab();
        return true;
    }

    public static boolean textToFile(File file, String text) {
        try {
            PrintStream out = new PrintStream(file);
            out.append(text);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(G.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        }
        return false;
    }

    public static boolean textToFile(String fname, String text) {
        try {
            PrintStream out = new PrintStream(fname);
            out.append(text);
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(G.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        }
        return false;
    }

    public static void zzzsleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException ex) {

        }
    }

    public static boolean isValidName(String text) {
        Pattern pattern = Pattern.compile(
                "# Match a valid Windows filename (unspecified file system).          \n"
                + "^                                # Anchor to start of string.        \n"
                + "(?!                              # Assert filename is not: CON, PRN, \n"
                + "  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n"
                + "    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n"
                + "    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n"
                + "  )                              # LPT6, LPT7, LPT8, and LPT9...     \n"
                + "  (?:\\.[^.]*)?                  # followed by optional extension    \n"
                + "  $                              # and end of string                 \n"
                + ")                                # End negative lookahead assertion. \n"
                + "[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n"
                + "[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n"
                + "$                                # Anchor to end of string.            ",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);
        Matcher matcher = pattern.matcher(text);
        boolean isMatch = matcher.matches();
        return isMatch;
    }

        
    public static final String makeValidFileName(String fname) {
        if (G.isValidName(fname)) {
            return fname;
        }
        fname = fname.replace("/", " or ");
        fname = fname.replace("\\", " or ");
        fname = fname.replaceAll("[:*?|<>]", "_");
        if (G.isValidName(fname)) {
            return fname;
        }
        fname=fname.replaceAll("[^a-zA-Z0-9_+.()!@#$%^&*]", "_");
        fname=fname.replace(" ", "_");
        while(fname.contains("__"))fname=fname.replace("__", "_");
        return fname.trim();//TODO test this
    }

    private static final String makeValidQuery(String str) { 
        if (G.isValidName(str)) {
            return str;
        }
        final String VALID_SET="()-";
        //while(str.startsWith("\""))str=str.substring(1);
        //while(str.endsWith("\""))str=str.substring(0,str.length()-1);
        //str = str.replace("/", " or ");
        //str = str.replace("\\", " or ");
        char[] chars=str.toCharArray();
        int i=0;
        for(char ch: chars){
            if(Character.isLetterOrDigit(ch))chars[i++]=ch;
            else if(Character.isWhitespace(ch))chars[i++]=' ';
            else if(VALID_SET.indexOf(ch)>=0)chars[i++]=ch;
        }
        str=new String(chars,0,i);
        
        //str = str.replaceAll("[,.:*?|<>]", " ");
//        if (G.isValidName(str)) {
//            return str;
//        }
//        str=str.replaceAll("[^a-zA-Z0-9_+.()!@#$%^&*]", " ");
        //while(str.contains("  "))str=str.replace("  ", " ");
        return str.trim();//TODO test this
    }
    public static final String encode2URL(String str) {
        
        try {
            str=G.makeValidQuery(str);
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(G.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "UnsupportedEncodingException: G.java";//TODO test this
    }

    public static String withOptions(String label) {
        String ret=label.replaceAll("[\\(\\)]", "");
        return ret;
    }

    public static String withoutOptions(String label) {
        String ret=label.replaceAll("\\([^\\(\\)]*\\)", "");//  ([^()]*)
        return ret;
    }
   final static  JFileChooser fileChooser=new JFileChooser(".");
   
   public static final void refresh(JComponent c){
       
        c.updateUI();
        c.invalidate();
        c.repaint();
        c.validate();
   }
}
