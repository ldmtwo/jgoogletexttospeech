/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template files, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ldtwo.flashcard;

import java.awt.Color;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.plaf.LabelUI;
import org.ldtwo.GoTTS.AudioPlayer;
import org.ldtwo.GoTTS.MainFrame;

/**
 *
 * @author ldtwo
 */
public class FlashCardFrame extends javax.swing.JFrame {

    DecimalFormat df = new DecimalFormat("0.#");
    public static String path = "C:\\Users\\Larry\\Desktop";
    public boolean useFront = true;
    public Term currentTerm = null;
    public long startTime = System.currentTimeMillis();
    final long SLEEP_DURATION = 15000;
    MainFrame player;
    final LinkedList<Term> deck;

    final String leftLanguage;
    final String rightLanguage;
    
    /**
     * Creates new form CardFrame
     *
     * @throws java.lang.Exception
     */
    public FlashCardFrame(MainFrame f2, File inputFile,String leftLanguage,String rightLanguage) throws Exception {

this.leftLanguage=leftLanguage;
this.rightLanguage=rightLanguage;
//        this.setVisible(false);
//        this.setUndecorated(true);
//        this.setVisible(true);
        player = f2;
        initComponents();
        //grapg dev, syst tray, desktop, disp mode, graph dev

//        GraphicsEnvironment ge = GraphicsEnvironment.                getLocalGraphicsEnvironment();
//        GraphicsDevice[] graphDevArray = ge.getScreenDevices();
//        for (GraphicsDevice gd : graphDevArray) {
//            
//            GraphicsConfiguration[] graphCgfArray = gd.getConfigurations();
//            for (int i = 0; i < graphCgfArray.length; i++) {
//                JFrame f = new JFrame(gd.getDefaultConfiguration());
//                
//                Canvas c = new Canvas(graphCgfArray[i]);
//                Rectangle gcBounds = graphCgfArray[i].getBounds();
//                int xoffs = gcBounds.x;
//                int yoffs = gcBounds.y;
//                f.getContentPane().add(c);
//                f.setLocation((i * 50) + xoffs, (i * 60) + yoffs);
//                f.show();
//                gd.setFullScreenWindow(this);
//            }
//        }
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        KeyAdapter keyAdapter = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent evt) {

            }

        };
//        this.getContentPane().addKeyListener(keyAdapter);
        this.addKeyListener(keyAdapter);
        bottomLbl.setUI(new LabelUI() {

            @Override
            public void paint(Graphics g, JComponent c) {
                super.paint(g, c); //To change body of generated methods, choose Tools | Templates.
                if (bottomLbl.getText().length() <= 0) {

                    return;
                }
                g.setColor(Color.white);
                g.fillRect(0, 0, 1000, 500);
                g.setColor(Color.black);
//                Container parent=bottomLbl;
//                while(parent.getParent()!=null)parent=parent.getParent();
                FontMetrics fm = g.getFontMetrics();
                Rectangle2D rect = fm.getStringBounds(bottomLbl.getText(), g);
                String[] arr = bottomLbl.getText().split("\n");
                if (stats.isSelected()) {
                    for (int i = 0; i < arr.length; i++) {
                        g.drawString(arr[i],
                                50,
                                bottomLbl.getHeight() / 2 + 5 + (i - 2) * 20);
                    }
                } else {

                    g.drawString(arr[0],
                            50,
                            bottomLbl.getHeight() / 2 + 5 + (0) * 20);
                }

                if (jToggleButton1.isSelected()) {
                    final int base = 20;
                    g.setColor(Color.red);
                    g.drawLine(0, base, 1000, base);
                    g.setColor(Color.blue);
                    for (int i = 1; i < 10; i++) {
                        g.drawLine(0, base + i * 20, 1000, base + i * 20);
                    }
                }
            }

        });
        topLbl.setUI(new LabelUI() {

            @Override
            public void paint(Graphics g, JComponent c) {
                super.paint(g, c); //To change body of generated methods, choose Tools | Templates.
                g.setColor(Color.white);
                g.fillRect(0, 0, 1000, 500);
                g.setColor(Color.black);
                Container parent = topLbl;
                while (parent.getParent() != null) {
                    parent = parent.getParent();
                }
                FontMetrics fm = g.getFontMetrics();
                Rectangle2D rect = fm.getStringBounds(topLbl.getText(), g);
                final int base = 40;
                g.drawString(topLbl.getText(), (int) (parent.getWidth() / 2 - rect.getWidth() / 2), 78 + base);
                if (!jToggleButton1.isSelected()) {
                    g.setColor(Color.red);
                    g.drawLine(0, base, 1000, base);
                    g.setColor(Color.blue);
                    for (int i = 1; i < 10; i++) {
                        g.drawLine(0, base + i * 40, 1000, base + i * 40);
                    }
                }
            }

        });
//        jLabel1.setLocale(Locale.US);

        {
            deck = getDeck(inputFile);

            deck.addLast(new Term() {

                @Override
                public int skillRating() {
                    return 1000000;
                }

                @Override
                public void run() {
                    Collections.sort(deck);
                    currentTerm = deck.peekFirst();
                    show(currentTerm);
                }

            });
            if (deck.size() > 0) {
                currentTerm = deck.peekFirst();
                show(currentTerm);
            }
        }
        new Thread() {

            @Override
            public void run() {
                while (true) {

                    try {

//                        System.out.printf("sleeping for %s sec (%s)\n", 
//                                Math.max(15000-(System.currentTimeMillis() - startTime),1)/1000.0,
//                                System.currentTimeMillis() - startTime);
                        Thread.sleep(Math.max(15000 - (System.currentTimeMillis() - startTime), 1));
                        if (System.currentTimeMillis() - startTime < SLEEP_DURATION) {
//                        Thread.sleep(500 );
                            continue;
                        }
                        final double p = 0.09, m = 1 - p;
                        final Color red = Color.red;
                        final Color baseColor = scoreZero.getBackground();
                        Color gradient = baseColor;//gradient
                        animation_loop:
                        for (int i = 0; i < 4; i++) {
                            for (int j = 0; j < 40; j++) {
                                gradient = new Color((int) (gradient.getRed() * m + red.getRed() * p),
                                        (int) (gradient.getGreen() * m + red.getGreen() * p),
                                        (int) (gradient.getBlue() * m + red.getBlue() * p));
                                scoreZero.setBackground(gradient);
                                Thread.sleep(1000 / 40);
                            }
                            if (System.currentTimeMillis() - startTime < 15000) {
                                break animation_loop;
                            }
                            for (int j = 0; j < 40; j++) {
                                gradient = new Color((int) (gradient.getRed() * m + baseColor.getRed() * p),
                                        (int) (gradient.getGreen() * m + baseColor.getGreen() * p),
                                        (int) (gradient.getBlue() * m + baseColor.getBlue() * p));
                                scoreZero.setBackground(gradient);
                                Thread.sleep(1000 / 40);
                            }
//                            scoreZero.setBackground(red);
//                            Thread.sleep(100);
//                            scoreZero.setBackground(c);
//                            Thread.sleep(100);
                        }
                        scoreZero.setBackground(baseColor);
                        Thread.sleep((long) (SLEEP_DURATION * 9 / 10));
                    } catch (Exception ex) {
                        Logger.getLogger(FlashCardFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();
        updateScore();

//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice[] graphDevArray = ge.getScreenDevices();
//
//        graphDevArray[0].setFullScreenWindow(this);
    }

    ;
/**input can be null
 * 
 * @param inputFile
 * @return
 * @throws Exception 
 */
    public static LinkedList getDeck(File inputFile) throws Exception {
        final LinkedList<Term> deck = new LinkedList<>();

        File[] files = null;
        if (inputFile == null) {
            JFileChooser fc = new JFileChooser(path);
            fc.setMultiSelectionEnabled(true);
            fc.showOpenDialog(null);
            files = fc.getSelectedFiles();
        } else {
            files = new File[]{inputFile};
        }
        if (files == null) {
            return null;
        }
        Term t = null;
        String line;
        String[] arr;
        for (File file : files) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(
                        new FileInputStream(file), Charset.forName("UTF-8")));
                while ((line = br.readLine()) != null) {
                    arr = line.split("\t");
                    if (arr.length != 2) {
                        continue;
                    }
                    t = new Term(arr[0], arr[1]);
                    deck.addLast(t);
                }
            } catch (Exception ex) {
                Logger.getLogger(FlashCardFrame.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(FlashCardFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return deck;
    }

    public void print(Term t) {
        System.out.print(t.toString());
    }

    public final void show(Term t) {
        bottomLbl.setText("");
        topLbl.setText(useFront ? t.left : t.right);
        startTime = System.currentTimeMillis();
        if (review.isSelected()) {
            bottomLbl.setText((useFront ? currentTerm.right : currentTerm.left)
                    + String.format("\n%2s views, \n%2s sec (last), %2s sec (avg), \n%2s%%, %2s pts",
                            t.views, t.getRecentTime(), t.getAvgTime(),
                            t.getAvgAccuracy(), (int) t.skillRating()));
        }
        bottomLbl.repaint();
//        File f = player.getMP3(topLbl.getText());
//        player.getPlaylistStringForFile(f);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topLbl = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        prevBtn = new javax.swing.JButton();
        scoreZero = new javax.swing.JButton();
        scoreOne = new javax.swing.JButton();
        scoreTwo = new javax.swing.JButton();
        nextBtn = new javax.swing.JButton();
        printScore = new javax.swing.JButton();
        shuffle = new javax.swing.JButton();
        bottomLbl = new javax.swing.JLabel();
        sortBtn = new javax.swing.JButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        stats = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        review = new javax.swing.JCheckBox();
        trim = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        topLbl.setFont(new java.awt.Font("Arial Unicode MS", 0, 36)); // NOI18N
        topLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        topLbl.setText("--");
        topLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        topLbl.setFocusable(false);
        topLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                topLblMouseClicked(evt);
            }
        });

        prevBtn.setText("<<");
        prevBtn.setFocusable(false);
        prevBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevBtnActionPerformed(evt);
            }
        });
        jPanel1.add(prevBtn);

        scoreZero.setBackground(new java.awt.Color(255, 102, 102));
        scoreZero.setText("wrong/unknown");
        scoreZero.setFocusable(false);
        scoreZero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scoreZeroActionPerformed(evt);
            }
        });
        jPanel1.add(scoreZero);

        scoreOne.setBackground(new java.awt.Color(255, 204, 204));
        scoreOne.setText("unsure/half right");
        scoreOne.setFocusable(false);
        scoreOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scoreOneActionPerformed(evt);
            }
        });
        jPanel1.add(scoreOne);

        scoreTwo.setBackground(new java.awt.Color(204, 255, 204));
        scoreTwo.setText("correct");
        scoreTwo.setFocusable(false);
        scoreTwo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scoreTwoActionPerformed(evt);
            }
        });
        jPanel1.add(scoreTwo);

        nextBtn.setText(">>");
        nextBtn.setFocusable(false);
        nextBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextBtnActionPerformed(evt);
            }
        });
        jPanel1.add(nextBtn);

        printScore.setText("Score");
        printScore.setFocusable(false);
        printScore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printScoreActionPerformed(evt);
            }
        });

        shuffle.setText("Randomize");
        shuffle.setFocusable(false);
        shuffle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shuffleActionPerformed(evt);
            }
        });

        bottomLbl.setFont(new java.awt.Font("Arial Unicode MS", 0, 12)); // NOI18N
        bottomLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bottomLbl.setText("--");
        bottomLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        bottomLbl.setFocusable(false);
        bottomLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                bottomLblMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bottomLblMouseExited(evt);
            }
        });

        sortBtn.setText("Prioritize");
        sortBtn.setFocusable(false);
        sortBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortBtnActionPerformed(evt);
            }
        });

        jToggleButton1.setText("Front");
        jToggleButton1.setFocusable(false);
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        stats.setText("Stats");
        stats.setFocusable(false);

        jLabel1.setText("--");
        jLabel1.setFocusable(false);

        review.setText("Review");
        review.setFocusable(false);
        review.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reviewActionPerformed(evt);
            }
        });

        trim.setText("Trim");
        trim.setFocusable(false);
        trim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trimActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(trim)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(review)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(stats)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sortBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shuffle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(printScore))
            .addComponent(topLbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(bottomLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(printScore)
                    .addComponent(shuffle)
                    .addComponent(sortBtn)
                    .addComponent(jToggleButton1)
                    .addComponent(stats)
                    .addComponent(jLabel1)
                    .addComponent(review)
                    .addComponent(trim))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(topLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bottomLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                .addGap(1, 1, 1)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void prevBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevBtnActionPerformed

        Term t = deck.removeLast();
        deck.addFirst(t);
        if (t.skillRating() > 1000) {
            t = deck.removeLast();
            deck.addFirst(t);
        }
        show(t);
        currentTerm = t;
    }//GEN-LAST:event_prevBtnActionPerformed

    private void nextBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextBtnActionPerformed

        Term t = deck.removeFirst();//move old current
        if (trim.isSelected()) {
            if (t.views < 4 && t.getAvgAccuracy() < 98) {
                //can we remove this card?
                //REQ: seen 4+ times and 98% or better
                deck.addLast(t);//to end
            }
        } else {
            deck.addLast(t);
        }
        currentTerm = deck.peekFirst();//get new current
        currentTerm.run();
        show(deck.peekFirst());
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                File f = AudioPlayer.getMP3(topLbl.getText(),useFront? leftLanguage:rightLanguage);
                if (f != null && f.exists()) {
                    AudioPlayer.getInstance().enqueue(f);
                }
            }
        });
        updateScore();

    }//GEN-LAST:event_nextBtnActionPerformed

    private void printScoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printScoreActionPerformed
        //score
        Collections.sort(deck);
        StringBuilder sb = new StringBuilder(4096);
        for (Term t : deck) {
            sb.append(t.toString());
        }
        TextDisplayFrame frame = new TextDisplayFrame();
        frame.txt.setText(sb.toString());
//        frame.pack();   
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 30);
        frame.show();

    }//GEN-LAST:event_printScoreActionPerformed

    private void shuffleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shuffleActionPerformed
        //randomize 3*O(n)
        Collections.sort(deck);
//System.out.println("\n\n\n\n\n\n");
//        for(Term t: deck){
//            t.println();
//        }
        Term t = deck.removeLast();
        Collections.shuffle(deck);
        Collections.shuffle(deck);
        Collections.shuffle(deck);
        deck.addLast(t);
        currentTerm = deck.peekFirst();
        show(currentTerm);
    }//GEN-LAST:event_shuffleActionPerformed


    private void scoreZeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scoreZeroActionPerformed
        Term t = currentTerm;
        t.setLastAccuracy(0);
        long curTime = System.currentTimeMillis() - startTime;
        t.setLastTime(curTime);
        t.views++;
        nextBtnActionPerformed(evt);

    }//GEN-LAST:event_scoreZeroActionPerformed

    private void scoreOneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scoreOneActionPerformed
        Term t = currentTerm;
        t.setLastAccuracy(2);
        long curTime = System.currentTimeMillis() - startTime;
        t.setLastTime(curTime);
        t.views++;
        nextBtnActionPerformed(evt);

    }//GEN-LAST:event_scoreOneActionPerformed

    private void scoreTwoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scoreTwoActionPerformed
        Term t = currentTerm;
        t.setLastAccuracy(5);
        long curTime = System.currentTimeMillis() - startTime;
        t.setLastTime(curTime);
        t.views++;
        nextBtnActionPerformed(evt);

    }//GEN-LAST:event_scoreTwoActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed

        if (evt.getKeyChar() == '1') {
            scoreZeroActionPerformed(null);
        } else if (evt.getKeyChar() == '2') {
            scoreOneActionPerformed(null);
        } else if (evt.getKeyChar() == '3') {
            scoreTwoActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
            nextBtnActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
            prevBtnActionPerformed(null);
        } else if (evt.getKeyChar() == 'r' || evt.getKeyCode() == KeyEvent.VK_TAB) {
            Term t = currentTerm;

            //if (review.isSelected()) 
            {
                bottomLbl.setText((useFront ? currentTerm.right : currentTerm.left)
                        + String.format("\n%2s views, \n%2s sec (last), %2s sec (avg), \n%2s%%, %2s pts",
                                t.views, t.getRecentTime(), t.getAvgTime(),
                                t.getAvgAccuracy(), (int) t.skillRating()));
            }
//            else {
//                bottomLbl.setText("");
//            }
        }


    }//GEN-LAST:event_formKeyPressed

    private void bottomLblMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bottomLblMouseEntered
        Term t = currentTerm;
        bottomLbl.setText((useFront ? currentTerm.right : currentTerm.left)
                + String.format("\n%2s views, \n%2s sec (last), %2s sec (avg), \n%2s%%, %2s pts",
                        t.views, t.getRecentTime(), t.getAvgTime(),
                        t.getAvgAccuracy(), (int) t.skillRating()));


    }//GEN-LAST:event_bottomLblMouseEntered

    private void bottomLblMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bottomLblMouseExited
        if (!review.isSelected()) {
            bottomLbl.setText("");
        }
    }//GEN-LAST:event_bottomLblMouseExited

    private void sortBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortBtnActionPerformed

        Collections.sort(deck);
        currentTerm = deck.peekFirst();
        show(currentTerm);
    }//GEN-LAST:event_sortBtnActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        if (jToggleButton1.isSelected()) {
            jToggleButton1.setText("Back");
            useFront = false;
            bottomLbl.setText("");
        } else {
            jToggleButton1.setText("Front");
            useFront = true;
            bottomLbl.setText("");
        }
//          bottomLbl.setText(useFront ? currentTerm.right : currentTerm.left);

        currentTerm = deck.peekFirst();
        show(currentTerm);
        topLbl.invalidate();
        bottomLbl.invalidate();
        topLbl.validate();
        bottomLbl.validate();
        topLbl.repaint();
        bottomLbl.repaint();


    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        Runnable resizeRunnable = null;
        resizeRunnable = new Runnable() {

            @Override
            public void run() {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(FlashCardFrame.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                if(resizeRunnable!=this)return;
                bottomLbl.setLocation(topLbl.getWidth() / 2 - bottomLbl.getWidth() / 2, bottomLbl.getY());
            }
        };
        SwingUtilities.invokeLater(resizeRunnable);

    }//GEN-LAST:event_formComponentResized

    private void reviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reviewActionPerformed
        Term t = currentTerm;

        if (review.isSelected()) {
            bottomLbl.setText((useFront ? currentTerm.right : currentTerm.left)
                    + String.format("\n%2s views, \n%2s sec (last), %2s sec (avg), \n%2s%%, %2s pts",
                            t.views, t.getRecentTime(), t.getAvgTime(),
                            t.getAvgAccuracy(), (int) t.skillRating()));
        } else {
            bottomLbl.setText("");
        }
    }//GEN-LAST:event_reviewActionPerformed

    private void topLblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_topLblMouseClicked

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                File f = AudioPlayer.getMP3(topLbl.getText(),useFront? leftLanguage:rightLanguage);
                AudioPlayer.getInstance().enqueue(f);
            }
        });
        
    }//GEN-LAST:event_topLblMouseClicked

    private void trimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trimActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_trimActionPerformed

    public void updateScore() {
        Runnable runnable = new Runnable() {
            public void run() {
                double cnt = 0;
                double pts = 0;
                try {
                    double skill;
                    for (Term t : deck) {
                        skill = t.skillRating();
                        if (skill > 10000) {
                            continue;
                        }
                        cnt += t.getAvgAccuracy();
                        pts += skill;
                    }
                    cnt = cnt / (deck.size() - 1);
                    pts = pts / (deck.size() - 1);
                    jLabel1.setText(String.format(" %4s cards  %3s%%  @ %3s pts",
                            deck.size() - 1, df.format(cnt), df.format(pts)));
                } catch (Exception e) {
                    jLabel1.setText(String.format(" %4s cards", deck.size() - 1));
                }
            }
        };
        SwingUtilities.invokeLater(runnable);
    }
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(FlashCardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(FlashCardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(FlashCardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(FlashCardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    new FlashCardFrame().setVisible(true);
//                } catch (Exception ex) {
//                    Logger.getLogger(FlashCardFrame.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bottomLbl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JButton nextBtn;
    private javax.swing.JButton prevBtn;
    private javax.swing.JButton printScore;
    private javax.swing.JCheckBox review;
    private javax.swing.JButton scoreOne;
    private javax.swing.JButton scoreTwo;
    private javax.swing.JButton scoreZero;
    private javax.swing.JButton shuffle;
    private javax.swing.JButton sortBtn;
    private javax.swing.JCheckBox stats;
    private javax.swing.JLabel topLbl;
    private javax.swing.JCheckBox trim;
    // End of variables declaration//GEN-END:variables
}
