/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ldtwo.GoTTS;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashSet;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import static org.ldtwo.GoTTS.Languages.*;

/**
 *
 * @author ldtwo
 */
public class LanguageMenuListener extends KeyAndMouseAdapter {

    final JTextComponent txt;

    public LanguageMenuListener(JTextComponent txt) {
        this.txt = txt;
    }


        @Override
        public void mouseClicked(MouseEvent e) {
            Component comp = (Component) e.getSource();//menu.getComponentAt(e.getLocationOnScreen());
            String lang = LANGUAGE_LA.get(txt.getText());
            if (comp instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) comp;
                lang = LANGUAGE_LA.get(item.getText());
            }
            System.out.printf("%s  <>  %s  <>  %s \n", txt.getText(), lang, comp.toString());
            if (lang != null) {
                txt.setText(lang);
            }

        }

        @Override
        public void mousePressed(MouseEvent e) {
            mouseClicked(e);
        }

    final static JPopupMenu menu = new JPopupMenu();

    @Override
    public void keyTyped(KeyEvent e) {    
        SwingUtilities.invokeLater(this);//show/update menu
    }

    public void run() {
        menu.hide();
        menu.removeAll();
        final LinkedHashSet<String> languages = new LinkedHashSet();
        //Languages.LANGUAGE_LA.keySet();
        final LinkedHashSet<String> langs = new LinkedHashSet();
        //Languages.LA_LANGUAGE.keySet();
        String part = txt.getText().toLowerCase();
        for (String s : Languages.LA_LANGUAGE.keySet()) {
            if (s.toLowerCase().contains(part)) {
                String language = LA_LANGUAGE.get(s);
                if (language != null) {
                    languages.add(language);
                }
            }
        }
        for (String s : Languages.LANGUAGE_LA.keySet()) {
            if (s.toLowerCase().contains(part)) {
                languages.add(s);
            }
        }
        int cnt = 15;
        for (String la : languages) {
            if (cnt > 0) {
                JMenuItem item = menu.add(la);
                item.addMouseListener(this);

            }
            cnt--;
        }
        menu.show();
        menu.pack();
        menu.show(txt, txt.getWidth(), txt.getHeight());
        txt.requestFocus();
        G.refresh(menu);
    }
}
