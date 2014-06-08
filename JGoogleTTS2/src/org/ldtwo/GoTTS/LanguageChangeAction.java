/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ldtwo.GoTTS;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import static org.ldtwo.GoTTS.G.LANGUAGE_LA;
import static org.ldtwo.GoTTS.G.LA_LANGUAGE;

/**
 *
 * @author ldtwo
 */
public class LanguageChangeAction implements ActionListener {

    final String language;
    final String la;//abbreviation
    final JFrame frame;

    public LanguageChangeAction(String language, JFrame frame) {
        this.language = language;
        this.la = LANGUAGE_LA.get(language);
        this.frame = frame;
    }

    public LanguageChangeAction(JFrame frame, String langAbbreviation) {
        this.la = langAbbreviation;
        this.language = LA_LANGUAGE.get(la);
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame.setTitle("JGoogle TTS - " + language);

    }

}
