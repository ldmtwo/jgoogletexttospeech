/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ldtwo.GoTTS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author ldtwo
 */
public class G {

    public static HashSet<File> openFiles = new HashSet<File>();
    public static LinkedList<EditorPanel> tabList = new LinkedList<EditorPanel>();

    public static final String DELIM = "[\\n]", ANTIDELIM = "[^\\n]";
    public static final String DELIM2 = "[,.\\n]", ANTIDELIM2 = "[^,.\\n]";
    public static boolean play = true;
    public final static boolean SIMULATION = false;
    static final HashMap<String, String> LA_LANGUAGE = new HashMap<String, String>();
    static final HashMap<String, String> LANGUAGE_LA = new HashMap<String, String>();
    public final static String[] LANGS = {
        "af	Afrikaans",
        "sq	Albanian",
        "am	Amharic",
        "ar	Arabic",
        "hy	Armenian",
        "az	Azerbaijani",
        "eu	Basque",
        "be	Belarusian",
        "bn	Bengali",
        "bh	Bihari",
        "bs	Bosnian",
        "br	Breton",
        "bg	Bulgarian",
        "km	Cambodian",
        "ca	Catalan",
        "zh-CN	Chinese (Simplified)",
        "zh-TW	Chinese (Traditional)",
        "co	Corsican",
        "hr	Croatian",
        "cs	Czech",
        "da	Danish",
        "nl	Dutch",
        "en	English (US)",
        "en_gb	English (GB)",
        "en_au	English (AU)",
        "eo	Esperanto",
        "et	Estonian",
        "fo	Faroese",
        "tl	Filipino",
        "fi	Finnish",
        "fr	French",
        "fy	Frisian",
        "gl	Galician",
        "ka	Georgian",
        "de	German",
        "el	Greek",
        "gn	Guarani",
        "gu	Gujarati",
        "xx-hacker	Hacker (Google hoax)",
        "ha	Hausa",
        "iw	Hebrew",
        "hi	Hindi",
        "hu	Hungarian",
        "is	Icelandic",
        "id	Indonesian",
        "ia	Interlingua",
        "ga	Irish",
        "it	Italian",
        "ja	Japanese",
        "jw	Javanese",
        "kn	Kannada",
        "kk	Kazakh",
        "rw	Kinyarwanda",
        "rn	Kirundi",
        "xx-klingon	Klingon (Google hoax)",
        "ko	Korean",
        "ku	Kurdish",
        "ky	Kyrgyz",
        "lo	Laothian",
        "la	Latin",
        "lv	Latvian",
        "ln	Lingala",
        "lt	Lithuanian",
        "mk	Macedonian",
        "mg	Malagasy",
        "ms	Malay",
        "ml	Malayalam",
        "mt	Maltese",
        "mi	Maori",
        "mr	Marathi",
        "mo	Moldavian",
        "mn	Mongolian",
        "sr-ME	Montenegrin",
        "ne	Nepali",
        "no	Norwegian",
        "nn	Norwegian (Nynorsk)",
        "oc	Occitan",
        "or	Oriya",
        "om	Oromo",
        "ps	Pashto",
        "fa	Persian",
        "xx-pirate	Pirate (Google hoax)",
        "pl	Polish",
        "pt-BR	Portuguese (Brazil)",
        "pt-PT	Portuguese (Portugal)",
        "pa	Punjabi",
        "qu	Quechua",
        "ro	Romanian",
        "rm	Romansh",
        "ru	Russian",
        "gd	Scots Gaelic",
        "sr	Serbian",
        "sh	Serbo-Croatian",
        "st	Sesotho",
        "sn	Shona",
        "sd	Sindhi",
        "si	Sinhalese",
        "sk	Slovak",
        "sl	Slovenian",
        "so	Somali",
        "es	Spanish",
        "su	Sundanese",
        "sw	Swahili",
        "sv	Swedish",
        "tg	Tajik",
        "ta	Tamil",
        "tt	Tatar",
        "te	Telugu",
        "th	Thai",
        "ti	Tigrinya",
        "to	Tonga",
        "tr	Turkish",
        "tk	Turkmen",
        "tw	Twi",
        "ug	Uighur",
        "uk	Ukrainian",
        "ur	Urdu",
        "uz	Uzbek",
        "vi	Vietnamese",
        "cy	Welsh",
        "xh	Xhosa",
        "yi	Yiddish",
        "yo	Yoruba",
        "zu	Zulu"};
    //static int activeTabNum=0;

    static {
        String[] arr;
        for (String s : LANGS) {
            arr = s.split("\t");
            LA_LANGUAGE.put(arr[0], arr[1]);
            LANGUAGE_LA.put(arr[1], arr[0]);
        }

    }

    public static boolean saveFile(EditorPanel p) {
        if (p.file == null) {
            JFileChooser fc = new JFileChooser();
            fc.showOpenDialog(null);
            p.file = fc.getSelectedFile();
            if (p.file == null) {
                return false;
            }
        }

        return !textToFile(p.file, p.txt.getText());
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
}
