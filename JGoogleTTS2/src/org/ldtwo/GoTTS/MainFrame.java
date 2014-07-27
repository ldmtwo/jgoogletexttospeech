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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicButtonUI;
import static org.ldtwo.GoTTS.G.*;
import static org.ldtwo.GoTTS.Languages.*;
import org.ldtwo.flashcard.FlashCardFrame;
import org.ldtwo.flashcard.QuizPanel;
import org.ldtwo.flashcard.ReviewPanel;

/**
 *
 * @author Larry Moore
 */
public final class MainFrame extends javax.swing.JFrame {
AudioPlayer player=AudioPlayer.getInstance();
    public static boolean disabledEvents = false;
    public static LinkedList<String> recentDeckFilesLinkedList = new LinkedList();
    public static HashMap<String, String[]> recentDeckFilesMap = new HashMap<>();
    public static final LinkedHashSet recentCache = new LinkedHashSet();
    public static final LinkedList<JButton> activeDownloads = new LinkedList();
    static public MainFrame ths;
    Thread downloadUIRefresher = new Thread("downloadUIRefresher-" + Math.random()) {

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(200);
                    synchronized (activeDownloads) {
                        int j = 0;
                        JButton[] arr = activeDownloads.toArray(new JButton[]{});

                        downloads.setListData(arr);
                        //G.refresh(downloads);

                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    };

    /**
     * Creates new form Frame
     */
    private MainFrame() throws Exception {
        ths = this;
        initComponents();
        init();
        player.start();
        downloads.setCellRenderer(new DefaultListCellRenderer() {

            Font font = new Font("helvitica", Font.BOLD, 24);

            @Override
            public Component getListCellRendererComponent(
                    JList list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

//                JLabel label = (JLabel) super.getListCellRendererComponent(
//                        list, value, index, isSelected, cellHasFocus);
//                label.setIcon(imageMap.get((String) value));
//                label.setHorizontalTextPosition(JLabel.RIGHT);
//                label.setFont(font);
                if (!(value instanceof Component)) {
                    return new JLabel(value.toString());
                }

                return (Component) value;
            }

        });
        MainFrame.downloads.setListData(activeDownloads.toArray());
        setSize(1000, 600);
        downloadUIRefresher.start();
        //====================
        //recentFilesComboBox.removeAllItems();

        try {
            Scanner sc = new Scanner(new File(RECENT_DECKS_FILE)).useDelimiter("\\Z");
            String[] files = sc.next().split("\n");
            for (String line : files) {
                String[] arr = line.split("\t");
                System.out.println(Arrays.toString(arr));
                if (arr.length < 1) {
                    continue;
                }
                String fName = arr[0];
                if (fName.trim().length() <= 0) {
                    continue;
                }
                if (recentDeckFilesMap.containsKey(fName)) {
                    recentDeckFilesMap.remove(fName);
                    recentDeckFilesLinkedList.remove(fName);
                }
                recentDeckFilesLinkedList.addLast(fName);
                recentDeckFilesMap.put(fName, arr);
            }

            disabledEvents = true;
            refreshRecentFileList();
            disabledEvents = false;
            int idx = recentFilesComboBox.getSelectedIndex();
            System.out.println("INDEX: " + idx);
        } catch (FileNotFoundException ex) {
            System.err.println("RECENT_DECKS_FILE is missing!");
        } catch (Exception e) {
            System.err.println("RECENT_DECKS_FILE is corrupted!");
            e.printStackTrace();
//            e.printStackTrace();
        }
        //==================
        final java.lang.Runnable languageValidator = new java.lang.Runnable() {

            @Override
            public void run() {

                boolean b = null != LA_LANGUAGE.get(leftLanguage.getText())
                        && null != LA_LANGUAGE.get(rightLanguage.getText());
                btnFlashCards.setEnabled(b);
                btnStage1Review.setEnabled(b);
                btnStage2All.setEnabled(b);
                btnStage3Img.setEnabled(b);
                btnStage4Aud.setEnabled(b);
                btnStage5Aud.setEnabled(b);
                btnStage6Text.setEnabled(b);
                btnStage7Text.setEnabled(b);
                btnStage8Text.setEnabled(b);

            }
        };

        leftLanguage.addKeyListener(new LanguageMenuListener(leftLanguage) {

            @Override
            public void run() {
                languageValidator.run();
                super.run();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                languageValidator.run();
            }

        });
        rightLanguage.addKeyListener(new LanguageMenuListener(rightLanguage) {

            @Override
            public void run() {
                languageValidator.run();
                super.run();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                languageValidator.run();
            }

        });
        languageValidator.run();

        //==================
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
                    MainFrame.this.setTitle(
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
        MainFrame.this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        refreshTree();
        Languages.refreshFavorites(favorites, this);
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
        JMenuItem item = Languages.newLanguageMenuItem("en_gb");
        item.addActionListener(new LanguageChangeAction(this, "en_gb") {
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                if (MainFrame.this == null) {
                    return;
                }
                EditorPanel pan = MainFrame.this.getActiveTab();
                pan.la_ = la;
                MainFrame.this.updateTabTitle(MainFrame.this.getTabTitle(pan), pan, MainFrame.this.tabPane.getSelectedIndex());

            }
        });
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
                        //tabPane.add(p); //TODO: this is a fix, but why?
                        try {
                            tabPane.setSelectedComponent(p);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
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
        String name = LA_LANGUAGE.get(pan.la_);
        int index = tabPane.getTabCount() - 1;
        updateTabTitle(name, pan, index);
        tabPane.setTabComponentAt(index, new TabComponent(tabPane));
    }

    public void updateTabTitle(String name, EditorPanel pan, int index) {
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        btnStage1Review = new javax.swing.JButton();
        btnFlashCards = new javax.swing.JButton();
        btnStage2All = new javax.swing.JButton();
        num2 = new javax.swing.JSpinner();
        num3 = new javax.swing.JSpinner();
        btnStage3Img = new javax.swing.JButton();
        btnStage4Aud = new javax.swing.JButton();
        num4 = new javax.swing.JSpinner();
        btnStage5Aud = new javax.swing.JButton();
        num5 = new javax.swing.JSpinner();
        btnStage6Text = new javax.swing.JButton();
        num6 = new javax.swing.JSpinner();
        btnStage7Text = new javax.swing.JButton();
        num7 = new javax.swing.JSpinner();
        btnStage8Text = new javax.swing.JButton();
        num8 = new javax.swing.JSpinner();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        recentFilesComboBox = new javax.swing.JComboBox();
        btnOpenDeck = new javax.swing.JButton();
        leftLanguage = new javax.swing.JTextField();
        rightLanguage = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        leftScrollPane = new javax.swing.JScrollPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabPane = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        lst = new javax.swing.JList();
        jToolBar1 = new javax.swing.JToolBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        menuFlashCards = new javax.swing.JMenuItem();
        menuReviewCards = new javax.swing.JMenuItem();
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
        jMenuItem9 = new javax.swing.JMenuItem();
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

        jSplitPane1.setDividerLocation(280);
        jSplitPane1.setDividerSize(7);

        Dimension dim=new Dimension(100, 100);
        jTabbedPane1.setPreferredSize(dim);
        jTabbedPane1.setMinimumSize(dim);
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        btnStage1Review.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnStage1Review.setText("1) Review");
        btnStage1Review.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnStage1Review.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnStage1Review.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStage1ReviewActionPerformed(evt);
            }
        });

        btnFlashCards.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnFlashCards.setText("Flash Cards");
        btnFlashCards.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFlashCards.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnFlashCards.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFlashCardsActionPerformed(evt);
            }
        });

        btnStage2All.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnStage2All.setText("2) All -> All");
        btnStage2All.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnStage2All.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnStage2All.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStage2AllActionPerformed(evt);
            }
        });

        btnStage3Img.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnStage3Img.setText("3) Image -> Audio");
        btnStage3Img.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnStage3Img.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnStage3Img.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStage3ImgActionPerformed(evt);
            }
        });

        btnStage4Aud.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnStage4Aud.setText("4) Audio -> Image");
        btnStage4Aud.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnStage4Aud.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnStage4Aud.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStage4AudActionPerformed(evt);
            }
        });

        btnStage5Aud.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnStage5Aud.setText("5) Audio -> Audio");
        btnStage5Aud.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnStage5Aud.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnStage5Aud.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStage5AudActionPerformed(evt);
            }
        });

        btnStage6Text.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnStage6Text.setText("6) Text -> Audio+Image");
        btnStage6Text.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnStage6Text.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnStage6Text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStage6TextActionPerformed(evt);
            }
        });

        btnStage7Text.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnStage7Text.setText("7) Text -> Audio");
        btnStage7Text.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnStage7Text.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnStage7Text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStage7TextActionPerformed(evt);
            }
        });

        btnStage8Text.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnStage8Text.setText("8) Text -> Image");
        btnStage8Text.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnStage8Text.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        btnStage8Text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStage8TextActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setText("Level 0 = True/False\nLevel x = \nk = x/2; Quiz with 2^k \nchoices. Max = 2^3 = 8.\nif x is odd, use text.");
        jTextArea1.setEnabled(false);
        jTextArea1.setFocusable(false);
        jScrollPane3.setViewportView(jTextArea1);

        recentFilesComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        recentFilesComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                recentFilesComboBoxMouseClicked(evt);
            }
        });
        recentFilesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recentFilesComboBoxActionPerformed(evt);
            }
        });

        btnOpenDeck.setText("Load");
        btnOpenDeck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenDeckActionPerformed(evt);
            }
        });

        leftLanguage.setText("fr");
        leftLanguage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftLanguageActionPerformed(evt);
            }
        });

        rightLanguage.setText("en");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(recentFilesComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnStage2All, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(num2, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnStage3Img, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(num3))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnStage4Aud, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(num4))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnStage5Aud, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(num5))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnStage6Text, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(num6))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnStage7Text, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(num7))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnStage8Text, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(num8))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnFlashCards, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnStage1Review, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(leftLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rightLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnOpenDeck)))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(recentFilesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOpenDeck)
                    .addComponent(leftLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rightLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFlashCards)
                .addGap(4, 4, 4)
                .addComponent(btnStage1Review)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStage2All)
                    .addComponent(num2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStage3Img)
                    .addComponent(num3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStage4Aud)
                    .addComponent(num4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStage5Aud)
                    .addComponent(num5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStage6Text)
                    .addComponent(num6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStage7Text)
                    .addComponent(num7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStage8Text)
                    .addComponent(num8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Learning Plan", jPanel3);

        downloads.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(downloads);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Downloads", jPanel4);

        jPanel2.setLayout(new java.awt.GridLayout(1, 0));
        jPanel2.add(leftScrollPane);

        jTabbedPane1.addTab("Audio Cache", jPanel2);

        jSplitPane1.setLeftComponent(jTabbedPane1);

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

        jPanel1.add(jSplitPane1);

        jToolBar1.setRollover(true);

        fileMenu.setText("File");

        menuFlashCards.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.ALT_MASK));
        menuFlashCards.setText("Flash Cards");
        menuFlashCards.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFlashCardsActionPerformed(evt);
            }
        });
        fileMenu.add(menuFlashCards);

        menuReviewCards.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_MASK));
        menuReviewCards.setText("Review Cards");
        menuReviewCards.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuReviewCardsActionPerformed(evt);
            }
        });
        fileMenu.add(menuReviewCards);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Open");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem1);

        saveCurrentTab.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveCurrentTab.setText("Save");
        saveCurrentTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveCurrentTabActionPerformed(evt);
            }
        });
        fileMenu.add(saveCurrentTab);

        saveAs.setText("Save as ...");
        saveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsActionPerformed(evt);
            }
        });
        fileMenu.add(saveAs);

        saveAllTabs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveAllTabs.setText("Save All");
        saveAllTabs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAllTabsActionPerformed(evt);
            }
        });
        fileMenu.add(saveAllTabs);

        newTabMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newTabMenuItem.setText("New Tab");
        newTabMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newTabMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newTabMenuItem);
        fileMenu.add(jSeparator1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Exit");
        fileMenu.add(jMenuItem2);

        jMenuBar1.add(fileMenu);

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

        jMenuItem9.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_COMMA, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem9.setText("<< Skip");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem9);
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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        if (play) {
            if (pause.get()) {
                    G.pause.set(false);
            } else {
                //error?
            }
            return;
        }
        play = true;
                    G.pause.set(false);
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
        JFileChooser chooser = G.fileChooser;
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
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
//            try {
//                if (br != null) {
//                    br.close();
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
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
                    G.pause.set(false);
        refreshTree();
        Languages.refreshFavorites(favorites, this);

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
                    G.pause.set(false);
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

        boolean b=pause.get();
        pause.set(!b);
        //pause = !pause;
        
                   

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
                    new FlashCardFrame(MainFrame.this, selectedFile(), leftLanguage.getText(), rightLanguage.getText()).setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(FlashCardFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }//GEN-LAST:event_menuFlashCardsActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed

                    G.pause.set(true);
        skip = true;
        skipDelta = -2 * 16000 / 2;//1/2 sec
                    G.pause.set(false);

    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void menuReviewCardsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuReviewCardsActionPerformed
        java.lang.Runnable r = new java.lang.Runnable() {
            public void run() {
                try {
                    JFrame frame = new JFrame(String.format("%s <> %s ", leftLanguage.getText(), rightLanguage.getText()));
                    final ReviewPanel rp = new ReviewPanel(MainFrame.this, frame, null, selectedFile(), leftLanguage.getText(), rightLanguage.getText());
                    WindowAdapter wa = new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            rp.imageDownloader.shutdown();
                            rp.imageDownloader = null;
                        }
                    };
                    frame.addWindowListener(wa);

                } catch (Exception ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        AudioPlayer.executor.submit(r);

    }//GEN-LAST:event_menuReviewCardsActionPerformed

    private void btnStage1ReviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStage1ReviewActionPerformed
        menuReviewCardsActionPerformed(evt);
    }//GEN-LAST:event_btnStage1ReviewActionPerformed

    private void btnFlashCardsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFlashCardsActionPerformed
        menuFlashCardsActionPerformed(evt);
    }//GEN-LAST:event_btnFlashCardsActionPerformed

    private void btnStage2AllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStage2AllActionPerformed
final HashMap<String,String> rules=new HashMap<>();
rules.put("left-audio", "");
rules.put("right-audio", "");
rules.put("left-text", "");
rules.put("right-text", "");
rules.put("right-image", "");
rules.put("left-image", "");

rules.put("image-count", "2");
rules.put("num-options", "4");

        java.lang.Runnable r = new java.lang.Runnable() {
            public void run() {
                try {
                    JFrame frame = new JFrame(String.format("%s <> %s ", leftLanguage.getText(), rightLanguage.getText()));
                    final QuizPanel pan = new QuizPanel(MainFrame.this, frame, 
                            null, selectedFile(), leftLanguage.getText(), rightLanguage.getText(), rules);
                    WindowAdapter wa = new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            pan.imageDownloader.shutdown();
                            pan.imageDownloader = null;
                        }
                    };
                    frame.addWindowListener(wa);

                } catch (Exception ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        AudioPlayer.executor.submit(r);
        
        
    }//GEN-LAST:event_btnStage2AllActionPerformed

    private void btnStage3ImgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStage3ImgActionPerformed

    }//GEN-LAST:event_btnStage3ImgActionPerformed

    private void btnStage4AudActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStage4AudActionPerformed

    }//GEN-LAST:event_btnStage4AudActionPerformed

    private void btnStage5AudActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStage5AudActionPerformed

    }//GEN-LAST:event_btnStage5AudActionPerformed

    private void btnStage6TextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStage6TextActionPerformed

    }//GEN-LAST:event_btnStage6TextActionPerformed

    private void btnStage7TextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStage7TextActionPerformed

    }//GEN-LAST:event_btnStage7TextActionPerformed

    private void btnStage8TextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStage8TextActionPerformed

    }//GEN-LAST:event_btnStage8TextActionPerformed

    private void btnOpenDeckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenDeckActionPerformed

        try {
            if (importDeckFiles()) {
                return;
            }
            refreshRecentFileList();
            if (recentDeckFilesLinkedList.size() > 0) {
                saveRecentDeckFile();
            }
        } catch (Exception e) {
            System.err.println("recentDecks is missing or corrupted!");
            e.printStackTrace();
        }

    }//GEN-LAST:event_btnOpenDeckActionPerformed

    public boolean importDeckFiles() throws HeadlessException {
        //LinkedHashSet<String> files = new LinkedHashSet();
        //=====================  ADD MORE FILES
        JFileChooser fc = G.fileChooser;
        fc.setMultiSelectionEnabled(true);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileSystemView(FileSystemView.getFileSystemView());
        fc.showOpenDialog(this);
        File[] selected = fc.getSelectedFiles();
        if (selected == null) {
            return true;
        }
        if (selected.length < 1) {
            return true;
        }
        for (File f : selected) {
            String fName = f.getAbsolutePath();
            String[] arr = new String[]{fName, leftLanguage.getText(), rightLanguage.getText()};
            if (recentDeckFilesMap.containsKey(fName)) {
                arr = recentDeckFilesMap.get(fName);
                recentDeckFilesLinkedList.remove(fName);
            }
            recentDeckFilesLinkedList.addFirst(fName);
            recentDeckFilesMap.put(fName, arr);
        }
        if(true){
            for(String s:recentDeckFilesLinkedList){
                System.out.printf("%s, ", s);
            } System.out.println();
        }
        return false;
    }

    synchronized public void refreshRecentFileList() {
        recentFilesComboBox.removeAllItems();
        for (String fName : recentDeckFilesLinkedList) {
            File f = new File(fName);
            if (f.exists() && f.isFile()) {
                recentFilesComboBox.addItem(fName);
            }
        }

        Object item = recentFilesComboBox.getSelectedItem();
        if (item != null) {
            String[] arr = recentDeckFilesMap.get(item.toString());
            if (arr.length < 3) {
                return;
            }
            leftLanguage.setText(arr[1]);
            rightLanguage.setText(arr[2]);
        }
    }

    public boolean saveRecentDeckFile() throws FileNotFoundException, IOException {
        //=====================  SAVE FILE
        FileOutputStream os = new FileOutputStream(RECENT_DECKS_FILE);
        byte[] DELIM = "\n".getBytes();
        byte[] TAB = "\t".getBytes();
        if (recentDeckFilesLinkedList.size() <= 0) {
            return true;
        }
        for (String fName : recentDeckFilesLinkedList) {
            String f = fName;
            if (f == null) {
                continue;
            }
            String[] arr = recentDeckFilesMap.get(f);
            os.write(f.getBytes());
            if (arr != null && arr.length >= 3) {
                os.write(TAB);
                os.write(arr[1].getBytes());
                os.write(TAB);
                os.write(arr[2].getBytes());
            }
            os.write(DELIM);
        }
        os.close();
        return false;
    }

    private void leftLanguageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftLanguageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_leftLanguageActionPerformed

    private void recentFilesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recentFilesComboBoxActionPerformed

        if (disabledEvents) {
            return;
        }

        Object item = recentFilesComboBox.getSelectedItem();
        if (item != null) {
            recentDeckFilesLinkedList.remove(item.toString());
            recentDeckFilesLinkedList.addFirst(item.toString());
        }
        refreshRecentFileList();
        try {
            //save
            if (recentDeckFilesLinkedList.size() > 0) {
                saveRecentDeckFile();
            }
        } catch (Exception e) {
            System.err.println("recentDecks is missing or corrupted!");
            e.printStackTrace();
        }

    }//GEN-LAST:event_recentFilesComboBoxActionPerformed

    private void recentFilesComboBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_recentFilesComboBoxMouseClicked

        Object item = recentFilesComboBox.getSelectedItem();
        if (item != null) {
            recentDeckFilesMap.put(item.toString(), new String[]{
                item.toString(), leftLanguage.getText(), rightLanguage.getText()});
        }

// TODO add your handling code here:
    }//GEN-LAST:event_recentFilesComboBoxMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run_() {
                try {
                    try {

                        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//                        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//                            if ("Nimbus".equals(info.getName())) {
//                                UIManager.setLookAndFeel(info.getClassName());
//                                break;
//                            }
//                        }
                    } catch (Exception e) {
                        // If Nimbus is not available, you can set the GUI to another look and feel.
                    }
                    new MainFrame().setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(1);
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFlashCards;
    private javax.swing.JButton btnOpenDeck;
    private javax.swing.JButton btnStage1Review;
    private javax.swing.JButton btnStage2All;
    private javax.swing.JButton btnStage3Img;
    private javax.swing.JButton btnStage4Aud;
    private javax.swing.JButton btnStage5Aud;
    private javax.swing.JButton btnStage6Text;
    private javax.swing.JButton btnStage7Text;
    private javax.swing.JButton btnStage8Text;
    private javax.swing.JMenuItem delayDec;
    private javax.swing.JMenuItem delayInc;
    public static final javax.swing.JList downloads = new javax.swing.JList();
    private javax.swing.JMenu favorites;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem fontDec;
    private javax.swing.JMenuItem fontInc;
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
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenu lang;
    private javax.swing.JTextField leftLanguage;
    private javax.swing.JScrollPane leftScrollPane;
    private javax.swing.JList lst;
    private javax.swing.JMenuItem menuFlashCards;
    private javax.swing.JMenuItem menuReviewCards;
    private javax.swing.JMenuItem newTabMenuItem;
    private javax.swing.JSpinner num2;
    private javax.swing.JSpinner num3;
    private javax.swing.JSpinner num4;
    private javax.swing.JSpinner num5;
    private javax.swing.JSpinner num6;
    private javax.swing.JSpinner num7;
    private javax.swing.JSpinner num8;
    private javax.swing.JMenuItem pauseItem;
    private javax.swing.JMenuItem randomPlayItem;
    private javax.swing.JComboBox recentFilesComboBox;
    private javax.swing.JTextField rightLanguage;
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

//    public File getMP3() {
//        return getMP3(getActiveTab().txt.getText(), getActiveTab().la_);
//    }
//
//    public File playMP3() {
//        return playMP3(getActiveTab().txt.getText(), getActiveTab().la_);
//    }
//
//    public void playMP3(File f) {
//        if (!f.isDirectory() && f.getName().toLowerCase().endsWith(".mp3")) {
//            AudioPlayer.getInstance().enqueue(getActiveTab().la_, f);
//        }
//    }
//
//    public File playMP3(String s, String la) {
//        return AudioPlayer.getInstance().enqueue(la,s, true);
//    }
//
//    public File getMP3(String s, String la) {
//        return AudioPlayer.getMP3(s, la);
//    }

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
                                try { 
                                    File file=((File) f);
                                    if (!file.isDirectory() && file.getName().toLowerCase().endsWith(".mp3")) {
            AudioPlayer.getInstance().enqueue(file);
        }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                zzzsleep(delay);
                            } else if (f instanceof Long) {
                                //zzzsleep((Long) f);//scripted delay
                            } else if (!random) {
                                play = false;
                    G.pause.set(false);
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
                    } else if ("\r\n.?!".indexOf(ch) >= 0) {
                        files.addLast(300L);
                    } else if ("\t,".indexOf(ch) >= 0) {
                        files.addLast(100L);
                    } else if (ch == ';') {
                        files.addLast(200L);
                    } else if (ch == '-') {
                        files.addLast(20L);
                    } else {
                        file = SIMULATION ? new File(str) :  AudioPlayer.getMP3(str, getActiveTab().la_);
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
                file = SIMULATION ? new File(str) :  AudioPlayer.getMP3(str, getActiveTab().la_);
                try {
                    synchronized (files) {
                        files.addLast(file);
                        listModel.addElement(getPlaylistStringForFile(file));
                        lst.invalidate();
//                    lst.setEnabled(false);
                        lst.repaint();
                        lst.validate();

//                    lst.setEnabled(true);
                    }
                } catch (Exception e) {
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
            JMenuItem item = Languages.newLanguageMenuItem(str[0]);
            item.addActionListener(new LanguageChangeAction(this, str[0]) {
                public void actionPerformed(ActionEvent e) {
                    super.actionPerformed(e);
                    if (MainFrame.this == null) {
                        return;
                    }
                    EditorPanel pan = MainFrame.this.getActiveTab();
                    pan.la_ = la;
                    MainFrame.this.updateTabTitle(MainFrame.this.getTabTitle(pan), pan, MainFrame.this.tabPane.getSelectedIndex());

                }
            });
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
                        //JTree node = ((FileTree) e.getSource()).root;
                        Object[] path = e.getNewLeadSelectionPath().getPath();
                        String str = "CACHE\\";
                        int i = 0;
                        for (Object s : path) {//for each part of 'tree' path
                            str += s;
                            if (i < path.length - 1) {//if this is not the last iteration
                                str += "\\";
                            }
                            i++;
                        }
                        System.out.println("PLAY: " + str);
                        AudioPlayer.getInstance().enqueue( new File(str));
                    }
                };
                new Thread(runnable).start();
            }
        };

        try {
            FileTree tree;
            if (!new File(AUDIO_PATH).exists()) {
                tree = new FileTree(new File("."), listener);
            } else if (!new File(CACHE_PATH).exists()) {
                tree = new FileTree(new File(CACHE_PATH), listener);

            } else {
                tree = new FileTree(new File(CACHE_PATH), listener);
            }
//            leftScrollPane.getViewport().removeAll();
            leftScrollPane.getViewport().add(tree);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //tree.addMouseListener(listener);
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

    public File selectedFile() {
        return new File(recentFilesComboBox.getSelectedItem().toString());
    }
    private static final MouseListener buttonMouseListener = new BorderChangeMouseAdapter();
}
