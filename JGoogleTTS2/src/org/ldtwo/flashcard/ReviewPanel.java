/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ldtwo.flashcard;

import java.awt.Color;
import java.awt.Container;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.ldtwo.GoTTS.Frame2;
import org.ldtwo.GoTTS.G;
import org.ldtwo.GoTTS.Worker;

/**
 * True and false quiz (aud vs img)
 *
 * @author ldtwo
 */
public class ReviewPanel extends javax.swing.JPanel {

    DecimalFormat df = new DecimalFormat("0.#");
    public static String path = "C:\\Users\\Larry\\Desktop";
    public File[] files = null;
    public LinkedList<Term> deck = new LinkedList<>();
    public ArrayList<Term> shuffledDeck = new ArrayList<>();
    public boolean useFront = true;
    public Term currentTerm = null;
    public long startTime = System.currentTimeMillis();
    final long SLEEP_DURATION = 15000;
    static int numOptions = 6;
    final Term[] slots = new Term[16];
    JButton[] buttons;
    //Worker player=new Worker();
    Frame2 player = new Frame2();
    JFrame frame = null;
    int chances = 2;

    class Action extends MouseAdapter implements Runnable, KeyListener {

        @Override
        public void run() {
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

    }

    Action action = new Action() {

        @Override
        public void mouseEntered(MouseEvent e) {
            //player.playMP3(null);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == buttons[0]) {//right     
                //score++
                //play btn[0]
            } else {//wrong
                if (chances-- > 0) {
                    return;
                } else {
                    //fall through
                }
            }
            //play btn[0]
            //score--
            buttons[0].setBackground(Color.green);
            for (int i = 1; i < buttons.length; i++) {
                buttons[i].setBackground(Color.gray);
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {

        }
    };

    final public void resetUI() {

        for (JButton b : buttons) {
            b.setBackground(Color.LIGHT_GRAY);
        }

    }

    /**
     * Creates new form QuizPanel
     *
     * @param f2
     * @param hostFrame
     * @param hostContainer
     * @throws java.lang.Exception
     */
    public ReviewPanel(Frame2 f2, JFrame hostFrame, Container hostContainer) throws Exception {

        player = f2;

        deck = CardFrame.getDeck(
                new File("C:\\Users\\Larry\\Desktop\\French class\\vocab\\chpt 4.tab.txt")
        );
        TestImage.loadDeckWithImages(deck);

        for (Term t : deck) {
            //System.out.printf("%s\n",t.info());
        }

        shuffleDeckArray();
        buttons = new JButton[deck.size()];
        for (JButton b : buttons) {
            if (b != null) {
                b.addMouseListener(action);
            }
        }
        if (hostFrame != null) {
            frame = hostFrame;
        } else if (hostContainer == null) {
            frame = new JFrame();
            frame.setContentPane(this);
            frame.setUndecorated(true);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }//else hostContainer is null
        initComponents();
        btnFullscreen.setVisible(false);

        if (frame != null) {
            //go full screen
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] graphDevArray = ge.getScreenDevices();
            graphDevArray[0].setFullScreenWindow(frame);
            frame.setVisible(true);
        }
        KeyAdapter keyAdapter = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent evt) {

            }

        };
        this.addKeyListener(keyAdapter);

        updateScore();

    }

    private void shuffleDeckArray() {
        //get shuffled array from original deck
        shuffledDeck = new ArrayList(deck);
        Collections.shuffle(shuffledDeck);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panTop = new javax.swing.JPanel();
        btnExit = new javax.swing.JButton();
        btnFullscreen = new javax.swing.JButton();
        menuPanel = new javax.swing.JPanel();
        panBottom = new javax.swing.JPanel();
        right = new javax.swing.JButton();
        panQuestions = new javax.swing.JPanel();
        question = new javax.swing.JLabel();
        panAnswers = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 0, 0));
        setLayout(new java.awt.GridBagLayout());

        panTop.setBackground(new java.awt.Color(173, 173, 225));
        panTop.setMinimumSize(new java.awt.Dimension(63, 31));
        java.awt.GridBagLayout jPanel1Layout = new java.awt.GridBagLayout();
        jPanel1Layout.columnWidths = new int[] {15};
        jPanel1Layout.rowHeights = new int[] {15};
        jPanel1Layout.columnWeights = new double[] {1.0};
        jPanel1Layout.rowWeights = new double[] {1.0};
        panTop.setLayout(jPanel1Layout);

        btnExit.setBackground(new java.awt.Color(255, 51, 51));
        btnExit.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnExit.setText("X");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 99;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        panTop.add(btnExit, gridBagConstraints);

        btnFullscreen.setBackground(new java.awt.Color(0, 0, 0));
        btnFullscreen.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        btnFullscreen.setForeground(new java.awt.Color(255, 255, 255));
        btnFullscreen.setText("|=|");
        btnFullscreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFullscreenActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 98;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        panTop.add(btnFullscreen, gridBagConstraints);

        javax.swing.GroupLayout menuPanelLayout = new javax.swing.GroupLayout(menuPanel);
        menuPanel.setLayout(menuPanelLayout);
        menuPanelLayout.setHorizontalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 461, Short.MAX_VALUE)
        );
        menuPanelLayout.setVerticalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 31, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panTop.add(menuPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 100, 0);
        add(panTop, gridBagConstraints);

        panBottom.setBackground(new java.awt.Color(133, 139, 180));
        panBottom.setMaximumSize(new java.awt.Dimension(32767, 50));
        panBottom.setMinimumSize(new java.awt.Dimension(0, 30));
        panBottom.setPreferredSize(new java.awt.Dimension(611, 30));

        right.setText(">>");
        right.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panBottomLayout = new javax.swing.GroupLayout(panBottom);
        panBottom.setLayout(panBottomLayout);
        panBottomLayout.setHorizontalGroup(
            panBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panBottomLayout.createSequentialGroup()
                .addContainerGap(393, Short.MAX_VALUE)
                .addComponent(right)
                .addGap(125, 125, 125))
        );
        panBottomLayout.setVerticalGroup(
            panBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panBottomLayout.createSequentialGroup()
                .addGap(0, 7, Short.MAX_VALUE)
                .addComponent(right))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panBottom, gridBagConstraints);

        panQuestions.setBackground(new java.awt.Color(36, 37, 48));
        panQuestions.setLayout(new java.awt.GridLayout(1, 0));

        question.setFont(new java.awt.Font("Arial Unicode MS", 0, 36)); // NOI18N
        question.setForeground(new java.awt.Color(255, 255, 204));
        question.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        question.setText("- - - - - - - - -");
        panQuestions.add(question);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 5.0;
        gridBagConstraints.insets = new java.awt.Insets(40, 10, 10, 10);
        add(panQuestions, gridBagConstraints);

        java.awt.GridBagLayout panAnswerLayout = new java.awt.GridBagLayout();
        panAnswerLayout.columnWidths = new int[] {2};
        panAnswerLayout.rowHeights = new int[] {2};
        panAnswerLayout.columnWeights = new double[] {1.0};
        panAnswerLayout.rowWeights = new double[] {1.0};
        panAnswers.setLayout(panAnswerLayout);

        jButton1.setText("jButton1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panAnswers.add(jButton1, gridBagConstraints);

        jButton2.setText("jButton2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panAnswers.add(jButton2, gridBagConstraints);

        jButton3.setText("jButton3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panAnswers.add(jButton3, gridBagConstraints);

        jButton4.setText("jButton4");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panAnswers.add(jButton4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 5.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 40, 20);
        add(panAnswers, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed

        if (frame == null) {
            return;
        }
//        if (frame.isUndecorated()) {
        frame.hide();
        frame.dispose();
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(1200, 700);
        frame.setSize(1200, 700);
        frame.setContentPane(this);
        btnExit.setVisible(false);
        btnFullscreen.setVisible(true);
        frame.setVisible(true);

//            frame.pack();
        //            this.hide();
        //         this.setEnabled(false);
        //        this.setUndecorated(true);
        //        this.show();
        //         this.setEnabled(true);
        //            this.dispose();
//        } else {
//            System.exit(0);
//            
//        }
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnFullscreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFullscreenActionPerformed

        if (frame == null) {
            return;
        }
        frame.hide();
        frame.dispose();
        JFrame f2 = new JFrame();
        f2.setContentPane(frame.getContentPane());
        frame = f2;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.setUndecorated(true);
        btnExit.setVisible(true);
        btnFullscreen.setVisible(false);
        //go full screen
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] graphDevArray = ge.getScreenDevices();
        graphDevArray[0].setFullScreenWindow(frame);
        frame.setVisible(true);


    }//GEN-LAST:event_btnFullscreenActionPerformed

    private void rightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightActionPerformed

        Term t = deck.removeFirst();
        deck.addLast(t);
        if (t == null) {

            try {
                File f = new File("C:\\Users\\Larry\\Desktop\\French class\\vocab\\chpt 3.html");
                String str = Term.toHTML(deck);
                FileOutputStream os = new FileOutputStream(f);
                os.write(str.getBytes());
                os.close();
            } catch (Exception ex) {
            }
            return;
        }
        panAnswers.removeAll();
        show(t);

//        question.setText(t.left);
//        Collection set = t.leftSet;
//        gbc.gridx = 0;
//        gbc.fill = GridBagConstraints.BOTH;
//        gbc.gridy = 1;
//        gbc.weightx = 1;
//        gbc.weighty = 1;
//        int width=question.getWidth()*3/16;
//        for (Object o : set) {
//            if (o instanceof File) {
//                File f = (File) o;
//                try {
//                    JButton btn = TestImage.imageFile2jButton(width, width * 3 / 4, f);
//                    panAnswers.add(btn);
//                    gbc.gridy = (gbc.gridy + 1) % 2;
//                    gbc.gridx = (gbc.gridx + 1) % 2;
//                    btn.addMouseListener(action);
//                    btn.addMouseMotionListener(action);
//                    btn.addKeyListener(action);
//                } catch (Exception e) {
//                }
//            }
//        }
        panAnswers.updateUI();
        panAnswers.invalidate();
        panAnswers.repaint();
        panAnswers.validate();
    }//GEN-LAST:event_rightActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnFullscreen;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JPanel menuPanel;
    private javax.swing.JPanel panAnswers;
    private javax.swing.JPanel panBottom;
    private javax.swing.JPanel panQuestions;
    private javax.swing.JPanel panTop;
    private javax.swing.JLabel question;
    private javax.swing.JButton right;
    // End of variables declaration//GEN-END:variables

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Quiz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Quiz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Quiz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Quiz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    new ReviewPanel().setVisible(true);
//                } catch (Exception ex) {
//                    Logger.getLogger(ReviewPanel.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        });
    }

    public void print(Term t) {
        System.out.print(t.toString());
    }
    GridBagConstraints gbc = new GridBagConstraints();

    public final void show(Term t) {
        final String label = useFront ? t.left : t.right;
        final String reverse = !useFront ? t.left : t.right;
        question.setText(String.format("<html>%s<br>%s", label, reverse));
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    String query1 = G.withoutOptions(label);
                    File mp3 = Worker.getMP3(query1, "fr");
                    G.play = true;
                    G.pause = false;
                    Worker.play(mp3);
                    String query2 = G.withOptions(label);
                    if (query1.equals(query2)) {
                        Thread.sleep(500);
                        File f = Worker.getMP3(query2, "fr");
                        Worker.play(f);
                    }
                    //other side
                    query1 = G.withoutOptions(reverse);
                    mp3 = Worker.getMP3(query1, "en");
                    G.play = true;
                    G.pause = false;
                    Worker.play(mp3);
                    query2 = G.withOptions(reverse);
                    if (query1.equals(query2)) {
                        Thread.sleep(500);
                        File f = Worker.getMP3(query2, "en");
                        Worker.play(f);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        startTime = System.currentTimeMillis();
        int N = 6;
        gbc.weightx = 1;
        gbc.weighty = 1;
        final HashSet set = useFront ? t.leftSet : t.rightSet;
        N = set.size();
        Iterator iter = set.iterator();
        int width = question.getWidth() * 3 / 16;

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < N / 2; x++) {
                gbc.gridx = x;
                gbc.gridy = y;
                try {
                    Object o = iter.next();
                    if (o == null) {
                        continue;
                    }
                    if (o instanceof File) {
                        final File f = (File) o;
                        try {
                            JButton btn = TestImage.imageFile2jButton(width, width * 3 / 4, f);
                            panAnswers.add(btn, gbc);
//                    gbc.gridy = (gbc.gridy + 1) % 2;
//                    gbc.gridx = (gbc.gridx + 1) % 2;
                            btn.addMouseListener(action);
                            btn.addMouseMotionListener(action);
                            btn.addKeyListener(action);
                            btn.addMouseListener(new MouseAdapter() {

                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
                                        synchronized (set) {
                                            set.remove(f);
                                        }
                                    }
                                }

                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                }
//                panAnswers.add(new JButton(x + ", " + y), gbc);

            }
        }
    }

    private void updateScore() {

    }
}
