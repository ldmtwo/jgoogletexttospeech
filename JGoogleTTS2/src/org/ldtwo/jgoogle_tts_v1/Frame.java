/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Frame.java
 *
 * Created on Sep 6, 2010, 11:38:07 PM
 */
package org.ldtwo.jgoogle_tts_v1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author Larry Moore
 */
public final class Frame extends javax.swing.JFrame {

    String language = "en";

    /**
     * Creates new form Frame
     */
    public Frame() {
        initComponents();
        init();

        setTitle("JGoogle TTS - English");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        lang = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        txt.setColumns(20);
        txt.setLineWrap(true);
        txt.setRows(5);
        txt.setText("Thank you for trying JGoogle TTS.\n\nhttp://code.google.com/p/jgoogletexttospeech/\n\nHello, how are you today?\nBonjour, comment allez-vous aujourd'hui?\nHola, ¿cómo estás hoy?\nHallo, wie geht es Ihnen heute?\nHej, hur mår du idag?\n\n\n\n");
        jScrollPane1.setViewportView(txt);

        jPanel1.add(jScrollPane1);

        jMenu1.setText("File");

        jMenuItem1.setText("Open");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Exit");
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenu2.setEnabled(false);
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Command");
        jMenu3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu3ActionPerformed(evt);
            }
        });

        jMenuItem3.setText("Play all");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenuBar1.add(jMenu3);

        lang.setText("Language");
        jMenuBar1.add(lang);

        jMenu4.setText("Help");

        jMenuItem5.setText("About");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem5);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenu3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu3ActionPerformed
    }//GEN-LAST:event_jMenu3ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    playMP3s();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        JOptionPane.showMessageDialog(this,
                "Developed by L. Moore. \n"
                + "License: GNU GPLv3\n"
                + "http://code.google.com/p/jgoogletexttospeech");
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        BufferedReader br = null;
        try {
            File f = null;
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(this);
            f = chooser.getSelectedFile();
            if (f == null) {
                return;
            }
            StringBuilder buf = new StringBuilder();
            br = new BufferedReader(new FileReader(f));
            while (true) {
                String s = br.readLine();
                if (s == null) {
                    break;
                }
                buf.append(s);
            }
            txt.setText(buf.toString());
        } catch (Exception ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_jMenuItem1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Frame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenu lang;
    private javax.swing.JTextArea txt;
    // End of variables declaration//GEN-END:variables

    String format(String s) {
        s = s.toLowerCase().replace("jgoogle", " chay google ")//
                .replace(" tts", " text to speech ").replace("+", " ");
        String ret = "";
        for (char ch : s.toCharArray()) {
            if (Character.isLetterOrDigit(ch)) {
                ret += "" + ch;
            } else {
                ret += " ";
            }
        }
        while (ret.contains("  ")) {
            ret = ret.replace("  ", " ");
        }
        //System.out.println(ret.trim().replace(" ", "+"));
        return ret.trim().replace(" ", "+");
    }

    public File getMP3() {

        return new Worker(language).getAndPlay(format(txt.getText()),
                false);
    }

    public File playMP3() {

        return new Worker(language).getAndPlay(format(txt.getText()),
                true);
    }

    public void playMP3(File f) {
        new Worker(language).play(f);
    }

    public File playMP3(String s) {
        return new Worker(language).getAndPlay(format(s),
                true);
    }

    public File getMP3(String s) {
        return new Worker(language).getAndPlay(format(s),
                false);
    }

    public void playMP3s() {
        LinkedList<File> files = new LinkedList<File>();
        String s = " " + txt.getText() + " ";
        s = format(s);
        int a = 0, b;
        while (a < s.length()) {

            b = s.indexOf("+", a + 70);
//            int nextIdx=b;
//            do{
//                nextIdx=s.indexOf("+", b+1);
//                if(nextIdx-a<90)
//                    b=nextIdx;
//                else break;
//            }while(true);
            //b = s.indexOf(" ", a + 80);

            String txt;
            try {
                if (a >= 0) {
                    if (b >= 0 && a < b) {
                        txt = s.substring(a, b);
                    } else {
                        txt = s.substring(a);
                    }
                } else {
                    break;
                }
                a += b - a;
                files.add(getMP3(txt));

            } catch (Exception e) {
                System.err.println("a=" + a + "; b=" + b);
                e.printStackTrace();
            }
        }
        for (File f : files) {
            playMP3(f);
        }
    }

    void init() {
        String[] langs = {
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
            "en	English",
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
            "xx-hacker	 Hacker",
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
            "xx-klingon	Klingon",
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
            "xx-pirate	 Pirate",
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
        JMenu[] subMenus = new JMenu[26];
        for (int x = 0; x < subMenus.length; x++) {
            subMenus[x] = new JMenu("       " + (char) ('A' + x) + "       ");
            lang.add(subMenus[x]);
        }
        for (String l : langs) {
            final String[] str = l.split("\t");
            JMenuItem item = new JMenuItem(str[1]);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setLanguage(str[0]);
                    setTitle("JGoogle TTS - " + str[1]);
                }
            });
            subMenus[str[1].trim().charAt(0) - 'A'].add(item);
        }
    }

    void setLanguage(String language) {
        this.language = language;
    }
}
