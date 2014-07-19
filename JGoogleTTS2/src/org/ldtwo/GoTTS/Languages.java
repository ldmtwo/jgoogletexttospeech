/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ldtwo.GoTTS;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import static org.ldtwo.GoTTS.G.AUDIO_PATH;

/**
 *
 * @author ldtwo
 */
public class Languages {
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
    static{
        for(String ss:LANGS){
            String[]part =ss.split("\t");
            LA_LANGUAGE.put(part[0], part[1]);
            LANGUAGE_LA.put(part[1], part[0]);
        }
    }

    static public void refreshFavorites(JComponent favorites, JFrame frame) {
        final MainFrame f2 = frame instanceof MainFrame ? (MainFrame) frame : null;

        favorites.removeAll();
        File dir = new File(AUDIO_PATH);
        if (!dir.exists()) {
            String[] defaultFavLangs = "en_gb,en,es,fr,ko,zh-TW".split(",");
            JMenuItem item;
            favorites.add(new JMenuItem("- - - Using default list (will update upon use) - - -"));
            for (String la : defaultFavLangs) {
                if (la.length() < 1) {
                    continue;
                }
                item = newLanguageMenuItem(la);
                item.addActionListener(new LanguageChangeAction(frame, la) {
                    public void actionPerformed(ActionEvent e) {
                        super.actionPerformed(e);
                        if (f2 == null) {
                            return;
                        }
                        EditorPanel pan = f2.getActiveTab();
                        pan.la_ = la;
                        f2.updateTabTitle(f2.getTabTitle(pan), pan, f2.tabPane.getSelectedIndex());

                    }
                });
                if (item != null) {
                    favorites.add(item);
                }
            }
            return;
        }
        dir.mkdirs();
        try {
            //System.out.println(new File(AUDIO_PATH).getAbsolutePath());
            File[] files = dir.listFiles();
            for (final File f : files) {
                if (f.isDirectory()) {
                    JMenuItem item = newLanguageMenuItem(f.getName());
                    if(item==null){
                        System.err.printf("Invalid language - folder does not match a valid language abbreviation: %s\n", f.getAbsolutePath());
                        continue;
                    }
                item.addActionListener(new LanguageChangeAction(frame, f.getName()) {
                    public void actionPerformed(ActionEvent e) {
                        super.actionPerformed(e);
                        if (f2 == null) {
                            return;
                        }
                        EditorPanel pan = f2.getActiveTab();
                        pan.la_ = la;
                        f2.updateTabTitle(f2.getTabTitle(pan), pan, f2.tabPane.getSelectedIndex());

                    }
                });
                    if (item != null) {//Ex: found as zh-TW
                        favorites.add(item);
                    } else {//Ex: found as Chinese (Traditional)
                        item = newLanguageMenuItemRev(f.getName());
                        if (item != null) {
                            favorites.add(item);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
final static public JMenuItem newLanguageMenuItem(final String la) {
        if (!LA_LANGUAGE.containsKey(la)) {
            System.err.printf("ERROR: LA=%s\n", la);
            return null;
        }
        final String name = LA_LANGUAGE.get(la);
        JMenuItem item = new JMenuItem(name);
        return item;
    }

    final static public JMenuItem newLanguageMenuItemRev(final String language) {
        if (!LANGUAGE_LA.containsKey(language)) {
            return null;
        }
        JMenuItem item = new JMenuItem(language);
        return item;
    }

}
