/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ldtwo.GoTTS;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author ldtwo
 */
public class G {
    
    
    public static LinkedList<EditorPanel> tabList=new LinkedList<EditorPanel>();
    
     public static final String DELIM="[\\n]",ANTIDELIM="[^\\n]";
    public static final String DELIM2="[,.\\n]",ANTIDELIM2="[^,.\\n]";
    public static boolean play = true;
   public final static boolean SIMULATION = false;
   public final static String[] langs = {
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
    static final HashMap<String, String> la_language = new HashMap<String, String>();
    static final HashMap<String, String> language_la = new HashMap<String, String>();
    //static int activeTabNum=0;

    static {
        String[] arr;
        for (String s : langs) {
            arr = s.split("\t");
            la_language.put(arr[0], arr[1]);
            language_la.put(arr[1], arr[0]);
        }

    }
}
