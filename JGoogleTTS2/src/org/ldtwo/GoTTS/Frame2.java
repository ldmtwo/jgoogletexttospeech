/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Frame.java
 *
 * Created on Sep 6, 2010, 11:38:07 PM
 */
package org.ldtwo.GoTTS;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.media.Time;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicButtonUI;
import static org.ldtwo.GoTTS.G.*;
import org.ldtwo.flashcard.CardFrame;

/**
 *
 * @author Larry Moore
 */
public final class Frame2 extends javax.swing.JFrame {

    static public Frame2 ths;

    public void refreshFavorites() {
        favorites.removeAll();
        File dir = new File("CACHE\\");
        if (!dir.exists()) {
            String[] defaultfavLangs = "en_gb,en,es,fr,ko,zh-TW".split(",");
            JMenuItem item;
            favorites.add(new JMenuItem("- - - Using default list (will update upon use) - - -"));
            for (String lang_ : defaultfavLangs) {
                if (lang_.length() < 1) {
                    continue;
                }
                item = newLanguageMenuItem(lang_);
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

    /**
     * Creates new form Frame
     */
    public Frame2() throws Exception {
        ths = this;
        initComponents();
        init();
        setSize(1000, 600);
        final Properties langProp = new Properties();
        try {
            langProp.load(new FileInputStream("lang_hist.txt"));
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: lang_hist.txt not loaded!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {

                EditorPanel pan = getActiveTab();
                int idx = tabPane.indexOfTab(pan.tabName);
                if (idx >= 0) {
                    ths.setTitle(
                            tabPane.getTitleAt(idx)
                    );
                }
            }
        });

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {

                super.componentResized(e); //To change body of generated methods, choose Tools | Templates.
                tabPane.setPreferredSize(new Dimension(200, 0));

            }

        });
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        ths.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        refreshTree();
        refreshFavorites();
        setTitle("JGoogle TTS - English");
        try {
            String[] files = new Scanner(new File(".lastOpened.txt")).useDelimiter("\\A").next().split("\n");
            for (String f : files) {
                EditorPanel pan = openFile(new File(f));
                if (pan != null) {
                    pan.la_ = langProp.getProperty(f, "en_gb");
                    updateTabTitle(getTabTitle(pan), pan, tabPane.getSelectedIndex());
                }
            }
        } catch (Exception e) {
            System.err.println("lastopened is missing or corrupted!");
            e.printStackTrace();
//            e.printStackTrace();
        }

        if (1 > tabPane.getTabCount()) {
            openFile(new File("default text.txt"));//!!
            addPanel();
        }
        JMenuItem item = newLanguageMenuItem("English (GB)");
        if (item != null) {
            favorites.add(item);
        }
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                int result = !CONFIRM_CLOSE ? JOptionPane.YES_OPTION
                        : JOptionPane.showConfirmDialog(null, "Are you sure you want to close GoTTS?");
                if (result == JOptionPane.YES_OPTION) {
                    for (EditorPanel p : G.tabList) {
                        if (p.file != null) {
                            if (!p.modified) {
                                continue;
                            }
                        }
                        String name = p.file == null ? p.tabName : p.file.getName();
                        tabPane.setSelectedComponent(p);
                        result = JOptionPane.showConfirmDialog(null,
                                "Would you like to save this? " + name);
                        if (result == JOptionPane.YES_OPTION) {
                            //save file
                            saveFile(p, false);
                        } else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
                            return;
                        }
                    }
                    String fileList = "";
                    Properties prop = new Properties(langProp);

                    for (EditorPanel p : G.tabList) {
                        if (p.file != null) {
                            fileList += p.file.getAbsolutePath() + "\n";
                            prop.put(p.file.getAbsolutePath(), p.la_);
                        }
                    }
                    textToFile(".lastOpened.txt", fileList);

                    try {
                        prop.store(new FileOutputStream("lang_hist.txt"),
                                "This file contains the last used language for every file. Ex: en,en_gb,en_au,fr,zh-TW,...");
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }

                    super.windowClosing(e);
                    System.exit(0);
                }
            }

        });
    }

    public EditorPanel getActiveTab() {
        EditorPanel pan;
        if (G.tabList.size() < 1) {
            addPanel();
        }
        pan = G.tabList.get(tabPane.getSelectedIndex());
        return pan;
    }

    public void addPanel() {
        EditorPanel pan = new EditorPanel();
        G.tabList.addLast(pan);
        tabPane.add(pan);
        String name = G.LA_LANGUAGE.get(pan.la_);
        int index = tabPane.getTabCount() - 1;
        updateTabTitle(name, pan, index);
        tabPane.setTabComponentAt(index, new TabComponent(tabPane));
    }

    private void updateTabTitle(String name, EditorPanel pan, int index) {
        boolean found = false;
        for (EditorPanel p : G.tabList) {
            if (p.tabName.equals(name)) {
                found = true;
                break;
            }
        }

        if (found) {
            int i = 2;
            String tryName = null;
            while (found) {
                tryName = name + " (" + i++ + ")";
                found = false;
                for (EditorPanel p : G.tabList) {
                    if (p.tabName.equals(tryName)) {
                        found = true;
                        break;
                    }
                }
            }
            name = tryName;
        }
        pan.tabName = name;
        tabPane.setTitleAt(index, name);
        tabPane.setSelectedIndex(index);
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
        jScrollPane5 = new javax.swing.JScrollPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabPane = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        lst = new javax.swing.JList();
        leftScrollPane = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jToolBar1 = new javax.swing.JToolBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuFlashCards = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        saveCurrentTab = new javax.swing.JMenuItem();
        saveAs = new javax.swing.JMenuItem();
        saveAllTabs = new javax.swing.JMenuItem();
        newTabMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        randomPlayItem = new javax.swing.JMenuItem();
        pauseItem = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        delayInc = new javax.swing.JMenuItem();
        delayDec = new javax.swing.JMenuItem();
        fontInc = new javax.swing.JMenuItem();
        fontDec = new javax.swing.JMenuItem();
        lang = new javax.swing.JMenu();
        favorites = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();

        jMenu5.setText("File");
        jMenuBar2.add(jMenu5);

        jMenu6.setText("Edit");
        jMenuBar2.add(jMenu6);

        jMenuItem4.setText("jMenuItem4");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setDividerSize(7);

        jSplitPane2.setDividerLocation(600);
        jSplitPane2.setDividerSize(7);
        jSplitPane2.setResizeWeight(1.0);
        jSplitPane2.setToolTipText("");
        jSplitPane2.setPreferredSize(new java.awt.Dimension(800, 132));
        jSplitPane2.setRightComponent(jScrollPane5);

        tabPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabPane.setTabPlacement(javax.swing.JTabbedPane.RIGHT);
        tabPane.setToolTipText("");
        tabPane.setFont(new java.awt.Font("Arial Unicode MS", 0, 12)); // NOI18N
        tabPane.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                tabPaneMouseWheelMoved(evt);
            }
        });
        jScrollPane6.setViewportView(tabPane);

        jSplitPane2.setLeftComponent(jScrollPane6);

        lst.setBackground(new java.awt.Color(0, 0, 51));
        lst.setForeground(new java.awt.Color(255, 255, 204));
        lst.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lst.setPreferredSize(new java.awt.Dimension(50, 0));
        jScrollPane2.setViewportView(lst);

        jSplitPane2.setRightComponent(jScrollPane2);

        jSplitPane1.setRightComponent(jSplitPane2);

        jTree1.setPreferredSize(new java.awt.Dimension(50, 64));
        leftScrollPane.setViewportView(jTree1);

        jSplitPane1.setLeftComponent(leftScrollPane);

        jPanel1.add(jSplitPane1);

        jToolBar1.setRollover(true);

        jMenu1.setText("File");

        menuFlashCards.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.ALT_MASK));
        menuFlashCards.setText("Flash Cards");
        menuFlashCards.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFlashCardsActionPerformed(evt);
            }
        });
        jMenu1.add(menuFlashCards);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Open");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        saveCurrentTab.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveCurrentTab.setText("Save");
        saveCurrentTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveCurrentTabActionPerformed(evt);
            }
        });
        jMenu1.add(saveCurrentTab);

        saveAs.setText("Save as ...");
        saveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsActionPerformed(evt);
            }
        });
        jMenu1.add(saveAs);

        saveAllTabs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveAllTabs.setText("Save All");
        saveAllTabs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAllTabsActionPerformed(evt);
            }
        });
        jMenu1.add(saveAllTabs);

        newTabMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newTabMenuItem.setText("New Tab");
        newTabMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newTabMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(newTabMenuItem);
        jMenu1.add(jSeparator1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Exit");
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("[0 sec]");
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

        randomPlayItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        randomPlayItem.setText("Random Loop");
        randomPlayItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomPlayItemActionPerformed(evt);
            }
        });
        jMenu3.add(randomPlayItem);

        pauseItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, java.awt.event.InputEvent.CTRL_MASK));
        pauseItem.setText("Pause");
        pauseItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseItemActionPerformed(evt);
            }
        });
        jMenu3.add(pauseItem);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setText("Stop");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem6);
        jMenu3.add(jSeparator2);

        delayInc.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_EQUALS, java.awt.event.InputEvent.CTRL_MASK));
        delayInc.setText("Delay ++");
        delayInc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delayIncActionPerformed(evt);
            }
        });
        jMenu3.add(delayInc);

        delayDec.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, java.awt.event.InputEvent.CTRL_MASK));
        delayDec.setText("Delay --");
        delayDec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delayDecActionPerformed(evt);
            }
        });
        jMenu3.add(delayDec);

        fontInc.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_EQUALS, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        fontInc.setText("Font Size ++");
        fontInc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontIncActionPerformed(evt);
            }
        });
        jMenu3.add(fontInc);

        fontDec.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        fontDec.setText("Font Size --");
        fontDec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontDecActionPerformed(evt);
            }
        });
        jMenu3.add(fontDec);

        jMenuBar1.add(jMenu3);

        lang.setText("Language");
        jMenuBar1.add(lang);

        favorites.setText("Favorites");

        jMenuItem7.setText("jMenuItem7");
        favorites.add(jMenuItem7);

        jMenuBar1.add(favorites);

        jMenu4.setText("Help");

        jMenuItem8.setText("Demo");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem8);

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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1098, Short.MAX_VALUE)
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
            public void run_() {
                try {
                    playAction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
        new Thread(runnable).start();
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    public void playAction() {
        play = true;
        pause = false;
        playMP3s(false);
        refreshTree();
    }

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        JOptionPane.showMessageDialog(this,
                "Developed by L. Moore. \n"
                + "License: GNU GPLv3\n"
                + "http://code.google.com/p/jgoogletexttospeech");
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        BufferedReader br = null;

        File file;
        JFileChooser chooser = new JFileChooser();
        chooser.showSaveDialog(this);
        file = chooser.getSelectedFile();
        if (file == null) {
            return;
        }
        openFile(file);

    }

    public EditorPanel openFile(File file) {
        BufferedReader br = null;
        try {
            file = file.getAbsoluteFile();
            if (G.openFiles.contains(file)) {
                //JOptionPane.showMessageDialog(null, "Error: You cannot have a file opened twice!", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            if (!file.exists()) {
                return null;
            }
            addPanel();
            EditorPanel pan = getActiveTab();
//            StringBuilder buf = new StringBuilder();
//            br = new BufferedReader(new FileReader(file), 512);
//            while (true) {
//                String s = br.readLine();
//                if (s == null) {
//                    break;
//                }
//                buf.append(s).append("\n");
//            }
            G.openFiles.add(file);
            pan.file = file;
            updateTabTitle(getTabTitle(pan), pan, tabPane.getSelectedIndex());
            pan.txt.setText(new Scanner(file).useDelimiter("\\A").next());
            pan.monitor = true;
            return pan;
        } catch (Exception ex) {
            Logger.getLogger(Frame2.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
//            try {
//                if (br != null) {
//                    br.close();
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(Frame2.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        return null;
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    public String getTabTitle(EditorPanel pan) {
        if (pan.file == null) {
            return pan.getLanguage();
        }
        return String.format("%s [%s]", pan.file.getName(), pan.getLanguage());
    }

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        stopAction();

    }

    public void stopAction() {
        play = false;
        pause = false;
        refreshTree();
        refreshFavorites();

        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void newTabMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newTabMenuItemActionPerformed
//CTRL+N = new tab
        addPanel();


    }//GEN-LAST:event_newTabMenuItemActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed

        addPanel();
        getActiveTab().txt.setText("Bonjour, comment allez-vous aujourd'hui?");
        getActiveTab().la_ = "fr";
        updateTabTitle("French/Francais", getActiveTab(), tabPane.getSelectedIndex());
        addPanel();
        getActiveTab().txt.setText("Hola, ¿cómo estás hoy? \n"
                + "¿Has oído el caso de ese fugitivo que secuestró un autobús de turistas japoneses?\n"
                + "La policía tiene 5.000 fotos suyas.");
        getActiveTab().la_ = "es";
        updateTabTitle("Spanish/Espanol", getActiveTab(), tabPane.getSelectedIndex());
        addPanel();
        getActiveTab().txt.setText("你好\n请听我说。\n了吗？");
        getActiveTab().la_ = "zh-TW";
        updateTabTitle("Chinese/Mandarin", getActiveTab(), tabPane.getSelectedIndex());
        addPanel();
        getActiveTab().txt.setText("Hallo, wie geht es Ihnen heute?");
        getActiveTab().la_ = "de";
        updateTabTitle("German/Deutch", getActiveTab(), tabPane.getSelectedIndex());
        addPanel();
        getActiveTab().txt.setText("With his windows down and his system up\n"
                + "So, will the real Shady please stand up?\n"
                + "'Cause I'm Slim Shady, yes I'm the real Shady of course!\n"
                + "All you other Slim Shadys are just imitating\n"
                + "So won't the real Slim Shady please stand up,\n"
                + "Please stand up, please stand up?\n"
                + "Alrighty? You can sit down now. That was rhetorical. \n"
                + "\n"
                + "You can play this again by simultaniously pressing CONTROL and P\n"
                + " or by choosing PLAY from the COMMAND menu.\n"
                + "\n"
                + "If you want to hear an American or Australian voice,\n"
                + "you may do so in the language menu.");
        getActiveTab().la_ = "en_gb";
        updateTabTitle("English/UK Hood", getActiveTab(), tabPane.getSelectedIndex());
        playMP3s(false);
// TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void randomPlayItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomPlayItemActionPerformed

        Runnable runnable = new Runnable() {
            public void run_() {
                try {
                    playRandAction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();

// TODO add your handling code here:
    }//GEN-LAST:event_randomPlayItemActionPerformed

    public void playRandAction() {
        play = true;
        pause = false;
        playMP3s(true);
        refreshTree();
    }

    private void delayIncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delayIncActionPerformed

        if (delay < 2000) {
            delay += 250;
        } else if (delay < 30000) {
            delay += 1000;
        }
        jMenu2.setText(String.format("[%s sec]", delay / 1000.0));

// TODO add your handling code here:
    }//GEN-LAST:event_delayIncActionPerformed

    private void delayDecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delayDecActionPerformed

        if (delay < 2500) {
            delay -= 250;
        } else {
            delay -= 1000;
        }
        if (delay < 1) {
            delay = 1;
        }
        jMenu2.setText(String.format("[%s sec]", delay / 1000.0));
        // TODO add your handling code here:
    }//GEN-LAST:event_delayDecActionPerformed

    private void pauseItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseItemActionPerformed

        pause = !pause;

    }//GEN-LAST:event_pauseItemActionPerformed

    private void tabPaneMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_tabPaneMouseWheelMoved

// TODO add your handling code here:
    }//GEN-LAST:event_tabPaneMouseWheelMoved
    public void tabPaneMouseScroll(java.awt.event.MouseWheelEvent evt) {
        if (evt.isControlDown()) {
            if (evt.getUnitsToScroll() > 0) {
                fontIncActionPerformed(null);
            } else {
                fontDecActionPerformed(null);
            }
        }
    }


    private void fontIncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontIncActionPerformed
        fontIncAction();

    }

    public void fontIncAction() {
        EditorPanel p = getActiveTab();
        p.fontSize = Math.min(p.fontSize + 1, FONT_SIZES.length - 1);
        p.txt.setFont(new Font("Arial Unicode", Font.PLAIN, FONT_SIZES[p.fontSize]));

        p.invalidate();
        p.repaint();
        p.validate();
        System.out.printf("Font+: %s -  %s [%s]\n", p.tabName, FONT_SIZES[p.fontSize], p.fontSize);

    }//GEN-LAST:event_fontIncActionPerformed

    private void fontDecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontDecActionPerformed
        fontDecAction();

    }

    public void fontDecAction() {
        EditorPanel p = getActiveTab();
        p.fontSize = Math.max(p.fontSize - 1, 0);
        p.txt.setFont(new Font("Arial Unicode", Font.PLAIN, FONT_SIZES[p.fontSize]));
        p.invalidate();
        p.repaint();
        p.validate();
        System.out.printf("Font-: %s -  %s [%s]\n", p.tabName, FONT_SIZES[p.fontSize], p.fontSize);

    }//GEN-LAST:event_fontDecActionPerformed

    private void saveCurrentTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveCurrentTabActionPerformed

        saveFile(getActiveTab(), false);

    }//GEN-LAST:event_saveCurrentTabActionPerformed

    private void saveAllTabsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAllTabsActionPerformed
        for (EditorPanel p : tabList) {
            if (p.modified || p.file == null) {
                saveFile(p, false);
            }
        }
    }//GEN-LAST:event_saveAllTabsActionPerformed

    private void saveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsActionPerformed

        saveFile(getActiveTab(), true);

    }//GEN-LAST:event_saveAsActionPerformed

    private void menuFlashCardsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFlashCardsActionPerformed

        java.awt.EventQueue.invokeLater(new java.lang.Runnable() {
            public void run() {
                try {
                    new CardFrame().setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(CardFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });        
    }//GEN-LAST:event_menuFlashCardsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run_() {
                try {
                    try {
                        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                            if ("Nimbus".equals(info.getName())) {
                                UIManager.setLookAndFeel(info.getClassName());
                                break;
                            }
                        }
                    } catch (Exception e) {
                        // If Nimbus is not available, you can set the GUI to another look and feel.
                    }
                    new Frame2().setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(Frame2.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(1);
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem delayDec;
    private javax.swing.JMenuItem delayInc;
    private javax.swing.JMenu favorites;
    private javax.swing.JMenuItem fontDec;
    private javax.swing.JMenuItem fontInc;
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
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTree jTree1;
    private javax.swing.JMenu lang;
    private javax.swing.JScrollPane leftScrollPane;
    private javax.swing.JList lst;
    private javax.swing.JMenuItem menuFlashCards;
    private javax.swing.JMenuItem newTabMenuItem;
    private javax.swing.JMenuItem pauseItem;
    private javax.swing.JMenuItem randomPlayItem;
    private javax.swing.JMenuItem saveAllTabs;
    private javax.swing.JMenuItem saveAs;
    private javax.swing.JMenuItem saveCurrentTab;
    public javax.swing.JTabbedPane tabPane;
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
        return getMP3(getActiveTab().txt.getText());
    }

    public File playMP3() {
        return playMP3(getActiveTab().txt.getText());
    }

    public void playMP3(File f) {
        if (!f.isDirectory() && f.getName().toLowerCase().endsWith(".mp3")) {
            new Worker(getActiveTab().la_).play(f);
        }
    }

    public File playMP3(String s) {
        return new Worker(getActiveTab().la_).getAndPlay((s), true);
    }

    public File getMP3(String s) {
        return new Worker(getActiveTab().la_).getAndPlay((s), false);
    }

    public void playMP3s(final boolean random) {
        System.out.println("STARTING..." + new Date().toLocaleString());
        final LinkedList files = new LinkedList();
        final HashSet files2 = new HashSet();
//        Pattern pat = Pattern.compile("([\r\n\\,\\;?!]|[\\,\\.\\;?!][\\s]|[^\r\n\\,\\;?!]+)");
        Pattern pat = Pattern.compile("([\r\n]|[^\r\n]+)");
        Matcher m = pat.matcher(getActiveTab().txt.getText());
        String s = getActiveTab().txt.getText();
        int a = 0, b;

        DefaultListModel listModel = new DefaultListModel();
        listModel.ensureCapacity(1000);

        lst.setModel(listModel);
        Runnable consumerThread = new Runnable() {
            Object[] playlist;

            @Override
            public void run_() {
                zzzsleep(500);//delay initial playing
                while (play) {
                    if (files.size() <= 0) {
                        zzzsleep(10);//wait for producer
                        continue;
                    }
                    synchronized (files) {
                        if (random) {
                            Collections.shuffle(files, RAND);
                        }
                        playlist = files.toArray(new Object[]{});
                        files2.addAll(files);
                        for (Object f : playlist) {
                            files.removeFirst();
                        }
                    }
                    for (Object f : playlist) {
                        if (!play) {//TODO: why did I do this?
                            return;
                        }
                        try {
                            if (f instanceof File) {
                                try {
                                    lst.setSelectedValue(getPlaylistStringForFile(((File) f)), true);
                                } catch (Exception e) {
                                    System.out.println("ERROR: list file=" + ((File) f).getAbsolutePath());
                                }
                                playMP3((File) f);
                                zzzsleep(delay);
                            } else if (f instanceof Long) {
                                //zzzsleep((Long) f);//scripted delay
                            } else if (!random) {
                                play = false;
                                pause = false;
                                lst.invalidate();
                                lst.repaint();
                                lst.validate();
                                System.out.println("stopped!");
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }//END for each item in playlist
                    if (random && files.size() <= 0) {
                        files.addAll(files2);
                    }
                }//END while (play)
            }//END run()
        };//runnable consumer thread
        Thread t = new Thread(consumerThread);
        t.start();
        String str;
        char ch;
        File file;
        while (/* a < s.length() */m.find() && play) {

            str = m.group();
            if (str.length() == 1 || str.length() == 2) {
                ch = str.charAt(0);
                synchronized (files) {
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
//                        lst.invalidate();
                        listModel.addElement(getPlaylistStringForFile(file));
//                        lst.repaint();
//                        lst.validate();
                    }
                }
            } else if (str.trim().length() > 1) {
                //str=format(str);
                file = SIMULATION ? new File(str) : getMP3(str);
                synchronized (files) {
                    files.addLast(file);
                    listModel.addElement(getPlaylistStringForFile(file));
                    lst.invalidate();
//                    lst.setEnabled(false);
                    lst.repaint();
                    lst.validate();

//                    lst.setEnabled(true);
                }
            }//END if
            zzzsleep(10);
        }//END while
        synchronized (files) {
            files.addLast("END");
        }
        System.out.println("done!  " + new Date().toLocaleString());
    }

    void init() {
        JMenu[] subMenus = new JMenu[18];
        String[] left, right;
        for (int x = 0; x < subMenus.length; x++) {
//            subMenus[x] = new JMenu("       " + (char) ('A' + x) + "       ");
            left = LANGS[LANGS.length * x / subMenus.length].split("\t");
            right = LANGS[Math.min(LANGS.length, LANGS.length * (x + 1) / subMenus.length - 1)].split("\t");
            subMenus[x] = new JMenu(String.format("%-8s       %8s",
                    left[1].substring(0, Math.min(3, left[1].length())),
                    right[1].substring(0, Math.min(3, right[1].length()))).replace(" ", "."));
            //System.out.println(subMenus[x]);
            lang.add(subMenus[x]);
        }
        int i = 1;
        for (String l : LANGS) {
            final String[] str = l.split("\t");
            JMenuItem item = newLanguageMenuItem(str[0]);
            subMenus[Math.min(
                    i++ * subMenus.length / LANGS.length,
                    subMenus.length - 1)].add(item);

        }
    }

    void setLanguage(String language) {
        getActiveTab().la_ = language;
    }

    private void refreshTree() {
        TreeSelectionListener listener = new TreeSelectionListener() {

            @Override
            public void valueChanged(final TreeSelectionEvent e) {
                Runnable runnable = new Runnable() {
                    public void run_() {
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

        try {
            FileTree tree;
            if (!new File("CACHE\\").exists()) {
                tree = new FileTree(new File("."), listener);
            } else {
                tree = new FileTree(new File("CACHE\\"), listener);
            }
//            leftScrollPane.getViewport().removeAll();
            leftScrollPane.getViewport().add(tree);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //tree.addMouseListener(listener);
    }

    public JMenuItem newLanguageMenuItem(final String la) {
        if (!LA_LANGUAGE.containsKey(la)) {
            return null;
        }
        JMenuItem item = new JMenuItem(LA_LANGUAGE.get(la));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setLanguage(la);
                String name = LA_LANGUAGE.get(la);
                setTitle("JGoogle TTS - " + name);
                EditorPanel pan = getActiveTab();
                pan.la_ = la;
                updateTabTitle(getTabTitle(pan), pan, tabPane.getSelectedIndex());

            }
        });
        return item;
    }

    public JMenuItem newLanguageMenuItemRev(final String language) {
        if (!LANGUAGE_LA.containsKey(language)) {
            return null;
        }
        JMenuItem item = new JMenuItem(language);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setLanguage(LANGUAGE_LA.get(language));
                setTitle("JGoogle TTS - " + language);
            }
        });
        return item;
    }

    public String getPlaylistStringForFile(File file) {
        return String.format("%5s KB  %s", file.length() / 1024, file.getName());
    }

    public class TabComponent extends JPanel {

        private final JTabbedPane pane;

        public TabComponent(final JTabbedPane pan) {
            //unset default FlowLayout' gaps
            super(new GridBagLayout());
            if (pan == null) {
                throw new NullPointerException("TabbedPane is null");
            }
//            this.setSize(100, 14);
//            this.setPreferredSize(new Dimension(100, 14));
            this.pane = pan;
            setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.weightx = 100;
            gbc.gridx = 1;
            gbc.gridwidth = 100;
            gbc.ipadx = 0;
            gbc.fill = GridBagConstraints.BOTH;
//            gbc.insets=new Insets(0, 0, 0, 0);
            //make JLabel read titles from JTabbedPane
            JLabel label = new JLabel() {
                public String getText() {
                    int i = pan.indexOfTabComponent(TabComponent.this);
                    if (i != -1) {
                        return pan.getTitleAt(i);
                    }
                    return null;
                }
            };
            label.setFont(new Font("Arial Unicode", Font.PLAIN, 11));
            label.setPreferredSize(new Dimension(120, 15));
            add(label, gbc);
            gbc.gridx = 2;
            //add more space between the label and the button
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            //tab button
            JButton button = new TabButton();

            gbc.weightx = 1;
            gbc.gridx = 101;
            gbc.gridwidth = 1;
            add(button, gbc);
            //add more space to the top of the component
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

//            setBackground(Color.red);
            this.getInsets().set(0, 0, 0, 0);
            label.getInsets().set(0, 0, 0, 0);
            button.getInsets().set(0, 0, 0, 0);
        }

        private class TabButton extends JButton implements ActionListener {

            public TabButton() {
                super("\u00D7");
                setForeground(Color.DARK_GRAY);
                setFont(new Font("Arial Unicode", Font.BOLD, 22));
                int size = 17;
                setPreferredSize(new Dimension(size, size));
                setToolTipText("close this tab");
                //Make the button looks the same for all Laf's
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run_() {
                        setUI(new BasicButtonUI());//way too slow
                    }
                });
                //Make it transparent
                setContentAreaFilled(false);
                //No need to be focusable
                setFocusable(false);
                setBorder(BorderFactory.createEtchedBorder());
                setBorderPainted(false);
                //Making nice rollover effect
                //we use the same listener for all buttons
                addMouseListener(buttonMouseListener);
                setRolloverEnabled(true);
                //Close the proper tab by clicking the button
                addActionListener(this);
            }

            public void actionPerformed(ActionEvent e) {
                int i = pane.indexOfTabComponent(TabComponent.this);
                if (i != -1) {
                    EditorPanel pan = (EditorPanel) pane.getComponentAt(i);
                    G.tabList.remove(pan);
                    G.openFiles.remove(pan.file);

                    pane.remove(i);
                }
            }

            //we don't want to update UI for this button
            public void updateUI() {
            }

            //paint the cross
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                Graphics2D g2 = (Graphics2D) g.create();
//                //shift the image for pressed buttons
//                if (getModel().isPressed()) {
//                    g2.translate(1, 1);
//                }
//                g2.setStroke(new BasicStroke(2));
//                g2.setColor(Color.BLACK);
//                if (getModel().isRollover()) {
//                    g2.setColor(Color.MAGENTA);
//                }
//                int delta = 6;
//                g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
//                g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
//                g2.dispose();
//            }
        }

    }
    private static final MouseListener buttonMouseListener = new BorderChangeMouseAdapter();
}
