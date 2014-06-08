/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ldtwo.GoTTS;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import static org.ldtwo.GoTTS.G.LA_LANGUAGE;
import static org.ldtwo.GoTTS.G.LANGUAGE_LA;

/**
 *
 * @author ldtwo
 */
public class Languages {
    
    static public void refreshFavorites(JMenu favorites, JFrame frame) {
        final Frame2 f2 = frame instanceof Frame2 ? (Frame2) frame : null;

        favorites.removeAll();
        File dir = new File("CACHE\\");
        if (!dir.exists()) {
            String[] defaultfavLangs = "en_gb,en,es,fr,ko,zh-TW".split(",");
            JMenuItem item;
            favorites.add(new JMenuItem("- - - Using default list (will update upon use) - - -"));
            for (String la : defaultfavLangs) {
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
            //System.out.println(new File("CACHE\\").getAbsolutePath());
            File[] files = dir.listFiles();
            for (final File f : files) {
                if (f.isDirectory()) {
                    JMenuItem item = newLanguageMenuItem(f.getName());
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
