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

import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 *
 * @author Larry Moore
 */
public final class Frame2 extends javax.swing.JFrame {
    public static final String DELIM="[\\n]",ANTIDELIM="[^\\n]";
    public static final String DELIM2="[,.\\n]",ANTIDELIM2="[^,.\\n]";
    public static boolean play = true;
    final static boolean SIMULATION = false;
    final static String[] langs = {
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
    String language = "en_gb";
    static final HashMap<String, String> la_language = new HashMap<String, String>();
    static final HashMap<String, String> language_la = new HashMap<String, String>();

    static {
        String[] arr;
        for (String s : langs) {
            arr = s.split("\t");
            la_language.put(arr[0], arr[1]);
            language_la.put(arr[1], arr[0]);
        }

    }

    public void refreshFavorites() {
        favorites.removeAll();
        File[] files = new File("CACHE\\").listFiles();
        for (final File f : files) {
            if (f.isDirectory()) {
                JMenuItem item = newLanguageMenuItem(f.getName());
                if (item != null) {
                    favorites.add(item);
                }
            }
        }
    }

    /**
     * Creates new form Frame
     */
    public Frame2() {
        initComponents();
        init();
        refreshTree();
        refreshFavorites();
        setTitle("JGoogle TTS - English");
        
        JMenuItem item = newLanguageMenuItem("English (GB)");
        if (item != null) {
            favorites.add(item);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu5 = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        lst = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jToolBar1 = new javax.swing.JToolBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        lang = new javax.swing.JMenu();
        favorites = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        nowPlaying = new javax.swing.JMenu();

        jMenu5.setText("File");
        jMenuBar2.add(jMenu5);

        jMenu6.setText("Edit");
        jMenuBar2.add(jMenu6);

        jMenuItem4.setText("jMenuItem4");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        jSplitPane2.setDividerLocation(400);
        jSplitPane2.setDividerSize(7);
        jSplitPane2.setResizeWeight(0.5);

        txt.setColumns(20);
        txt.setLineWrap(true);
        txt.setRows(5);
        txt.setText("Thank you for trying JGoogle TTS.\n\nhttp://code.google.com/p/jgoogletexttospeech/\n\nHello, how are you today?\nBonjour, comment allez-vous aujourd'hui?\nHola, ¿cómo estás hoy?\nHallo, wie geht es Ihnen heute?\nHej, hur mår du idag?\n你好\n\n\n\n");
        jScrollPane1.setViewportView(txt);

        jSplitPane2.setLeftComponent(jScrollPane1);

        lst.setBackground(new java.awt.Color(0, 0, 51));
        lst.setForeground(new java.awt.Color(255, 255, 204));
        lst.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(lst);

        jSplitPane2.setRightComponent(jScrollPane2);

        jSplitPane1.setRightComponent(jSplitPane2);

        jScrollPane3.setViewportView(jTree1);

        jSplitPane1.setLeftComponent(jScrollPane3);

        jPanel1.add(jSplitPane1);

        jToolBar1.setRollover(true);

        jMenu1.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Open");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
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

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Play all");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setText("Stop");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem6);

        jMenuBar1.add(jMenu3);

        lang.setText("Language");
        jMenuBar1.add(lang);

        favorites.setText("Favorites");

        jMenuItem7.setText("jMenuItem7");
        favorites.add(jMenuItem7);

        jMenuBar1.add(favorites);

        jMenu4.setText("Help");

        jMenuItem5.setText("About");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem5);

        jMenuBar1.add(jMenu4);

        nowPlaying.setText("[   ]");
        jMenuBar1.add(nowPlaying);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 823, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenu3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu3ActionPerformed
    }//GEN-LAST:event_jMenu3ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    play = true;
                    playMP3s();
                    refreshTree();
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
        } catch (HeadlessException | IOException ex) {
            Logger.getLogger(Frame2.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(Frame2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void lstMouseClicked(final java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstMouseClicked

        if (evt.getClickCount() == 1) {
        } else if (evt.getClickCount() == 2) {

            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new Worker(language).play(new File(((JList) evt.getSource()).getSelectedValue().toString()));
                    
        refreshFavorites();
                }
            });
        }
    }//GEN-LAST:event_lstMouseClicked

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed

        play = false;
        refreshTree();
        refreshFavorites();

        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Frame2().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu favorites;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTree jTree1;
    private javax.swing.JMenu lang;
    private javax.swing.JList lst;
    private javax.swing.JMenu nowPlaying;
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

    List format2(String s) {
        ArrayList list = new ArrayList();
        s = s.toLowerCase().replace("jgoogle", " chay google ")//
                .replace(" tts", " text to speech ").replace("+", " ");
        String[] phrases = s.split(DELIM);
        String[] delims = s.split(ANTIDELIM);
        String ret;
        int i = 0;
        for (String phrase : phrases) {
            ret = "";
            for (char ch : phrase.toCharArray()) {
                if (Character.isLetterOrDigit(ch) || ch == '-') {
                    ret += "" + ch;
                } else {
                    ret += " ";
                }
            }
            while (ret.contains("  ")) {
                while (ret.contains("    ")) {
                    ret = ret.replace("  ", " ");
                }
                ret = ret.replace("  ", " ");
            }
            list.add(ret);
            list.add(delims[i]);
            i++;
        }
        //System.out.println(ret.trim().replace(" ", "+"));
        //return ret.trim().replace(" ", "+");
        return list;
    }

    public File getMP3() {
        return getMP3(txt.getText());
    }

    public File playMP3() {
        return playMP3(txt.getText());
    }

    public void playMP3(File f) {
        if (!f.isDirectory() && f.getName().toLowerCase().endsWith(".mp3")) {
            new Worker(language).play(f);
        }
    }

    public File playMP3(String s) {
        return new Worker(language).getAndPlay((s),
                true);
    }

    public File getMP3(String s) {
        return new Worker(language).getAndPlay((s),
                false);
    }

    public void playMP3s() {
        final LinkedList files = new LinkedList();
//        Pattern pat = Pattern.compile("([\r\n\\,\\;?!]|[\\,\\.\\;?!][\\s]|[^\r\n\\,\\;?!]+)");
        Pattern pat = Pattern.compile("([\r\n]|[^\r\n]+)");
        Matcher m = pat.matcher(txt.getText());
        String s = txt.getText();
        int a = 0, b;

        DefaultListModel listModel = new DefaultListModel();/*{

         @Override
         public Object getElementAt(int i) {
         try {
         return files.get(i);
         } catch (Exception e) {
         return null;
         }
         }

         @Override
         public int size() {
         return files.size(); //To change body of generated methods, choose Tools | Templates.
         }

         @Override
         public Object get(int i) {
         return files.get(i); //To change body of generated methods, choose Tools | Templates.
         }
            
         };*/
        lst.setModel(listModel);
        final boolean[] playing = {true};
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);//delay initial playing
                } catch (InterruptedException ex) {
                }
                while (play) {
                    try {

                        if (files.size() > 0) {
                            Object[] playlist = null;
                            synchronized (files) {
                                playlist = files.toArray(new Object[]{});
                                for (Object f : playlist) {
                                    files.removeFirst();
                                }
                            }
                            for (Object f : playlist) {

                                while (!play) {
                                    return;
                                }
                                if (f instanceof File) {
                                    try {
                                        try {
                                            lst.setSelectedValue(getPlaylistStringForFile(((File) f)), true);
                                        } catch (Exception e) {
                                            System.out.println("ERROR: list file=" + ((File) f).getAbsolutePath());
                                        }
                                        playMP3((File) f);
                                        Thread.sleep(500);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else if (f instanceof Long) {
                                    try {
                                        Thread.sleep((Long) f);
                                    } catch (InterruptedException ex) {
                                        //Logger.getLogger(Frame2.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                } else {
                                    play = false;
                                    return;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread t = new Thread(runnable);
        t.start();
        String str;
        char ch;
        File file;
        while (/* a < s.length() */m.find()) {

            str = m.group();
            if (str.length() == 1 || str.length() == 2) {
                ch = str.charAt(0);
                // synchronized (files) 
                {
                    if (ch == ' ') {
                        files.addLast(10L);
                    } else if (ch == '\r') {
                        files.addLast(300L);
                    } else if (ch == '\n') {
                        files.addLast(300L);
                    } else if (ch == '\t') {
                        files.addLast(100L);
                    } else if (ch == '.' || ch == '?' || ch == '!') {
                        files.addLast(300L);
                    } else if (ch == ';') {
                        files.addLast(200L);
                    } else if (ch == '-') {
                        files.addLast(20L);
                    } else if (ch == ',') {
                        files.addLast(100L);
                    } else {
                        file = SIMULATION ? new File(str) : getMP3(str);
                        files.addLast(20L);
                        files.addLast(file);
                        files.addLast(20L);
                        listModel.addElement(getPlaylistStringForFile(file));
                        lst.invalidate();
                        lst.repaint();
                        lst.validate();
                    }
                }
            } else if (str.trim().length() > 1) {
                //str=format(str);
                file = SIMULATION ? new File(str) : getMP3(str);
                //synchronized (files) 
                {
                    files.addLast(file);
                    listModel.addElement(getPlaylistStringForFile(file));
                    lst.invalidate();
                    lst.repaint();
                    lst.validate();
                }
            }
        }
        synchronized (files) {
            files.addLast("END");
        }

    }

    void init() {
        JMenu[] subMenus = new JMenu[18];
        String[] left, right;
        for (int x = 0; x < subMenus.length; x++) {
//            subMenus[x] = new JMenu("       " + (char) ('A' + x) + "       ");
            left = langs[langs.length * x / subMenus.length].split("\t");
            right = langs[Math.min(langs.length, langs.length * (x + 1) / subMenus.length - 1)].split("\t");
            subMenus[x] = new JMenu(String.format("%-8s       %8s",
                    left[1].substring(0, Math.min(3, left[1].length())),
                    right[1].substring(0, Math.min(3, right[1].length()))).replace(" ", "."));
            //System.out.println(subMenus[x]);
            lang.add(subMenus[x]);
        }
        int i = 1;
        for (String l : langs) {
            final String[] str = l.split("\t");
            JMenuItem item = newLanguageMenuItem(str[0]);
            subMenus[Math.min(
                    i++ * subMenus.length / langs.length,
                    subMenus.length - 1)].add(item);
        }
    }

    void setLanguage(String language) {
        this.language = language;
    }

    private void refreshTree() {
        TreeSelectionListener listener = new TreeSelectionListener() {
            
            @Override
            public void valueChanged(final TreeSelectionEvent e) {
                Runnable runnable = new Runnable() {
                    public void run() {
                        try {
                            //JTree node = ((FileTree) e.getSource()).root;
                            Object[] path = e.getNewLeadSelectionPath().getPath();
                            String str = "";
                            int i = 0;
                            for (Object s : path) {
                                str += s;
                                if (i < path.length - 1) {
                                    str += "\\";
                                }
                                i++;
                            }
                            System.out.println("PLAY: " + str);
                            playMP3(new File(str));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                new Thread(runnable).start();
            }
        };
        
        FileTree tree = new FileTree(new File("CACHE\\"), listener);
        jScrollPane3.getViewport().add(tree);
        //tree.addMouseListener(listener);
    }

    public JMenuItem newLanguageMenuItem(final String la) {
        if (!la_language.containsKey(la)) {
            return null;
        }
        JMenuItem item = new JMenuItem(la_language.get(la));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setLanguage(la);
                setTitle("JGoogle TTS - " + la_language.get(la));
            }
        });
        return item;
    }public JMenuItem newLanguageMenuItemRev(final String language) {
        if (!language_la.containsKey(language)) {
            return null;
        }
        JMenuItem item = new JMenuItem(language);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setLanguage(language_la.get(language));
                setTitle("JGoogle TTS - " + language);
            }
        });
        return item;
    }

    public String getPlaylistStringForFile(File file) {
        return String.format("%-20s  %5s KB", file.getName(), file.length() / 1024);
    }
}
