/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ldtwo.flashcard;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import org.ldtwo.GoTTS.Frame2;
import static org.ldtwo.flashcard.CardFrame.path;

/**
 *
 * @author ldtwo
 */
public class QuizPanel extends javax.swing.JPanel {

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
    Frame2 player;
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
     * @throws java.lang.Exception
     */
    public QuizPanel() throws Exception {

        JFileChooser fc = new JFileChooser(path);
        fc.setMultiSelectionEnabled(true);
        fc.showOpenDialog(null);
        files = fc.getSelectedFiles();
        if (files == null) {
            return;
        }
        Term t = null;
        String line;
        String[] arr;
        for (File file : files) {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), Charset.forName("UTF-8")));
            while ((line = br.readLine()) != null) {
                arr = line.split("\t");
                if (arr.length != 2) {
                    continue;
                }
                t = new Term(arr[0], arr[1]);
                deck.addLast(t);
            }
        }
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
        if (t != null) {
            currentTerm = deck.peekFirst();
            show(currentTerm);
        }
        shuffleDeckArray();
        buttons = new JButton[deck.size()];
        for (JButton b : buttons) {
            b.addMouseListener(action);
        }

        frame = new JFrame();
        frame.setContentPane(this);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        btnFullscreen.setVisible(false);

        //go full screen
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] graphDevArray = ge.getScreenDevices();
        graphDevArray[0].setFullScreenWindow(frame);
        frame.setVisible(true);

        KeyAdapter keyAdapter = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent evt) {

            }

        };
        this.addKeyListener(keyAdapter);

        updateScore();

    }

    public void shuffleDeckArray() {
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
        panBottom = new javax.swing.JPanel();
        panQuestions = new javax.swing.JPanel();
        topLbl = new javax.swing.JLabel();
        panAnswer = new javax.swing.JPanel();

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

        javax.swing.GroupLayout panBottomLayout = new javax.swing.GroupLayout(panBottom);
        panBottom.setLayout(panBottomLayout);
        panBottomLayout.setHorizontalGroup(
            panBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 567, Short.MAX_VALUE)
        );
        panBottomLayout.setVerticalGroup(
            panBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
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
        panQuestions.setLayout(new java.awt.GridLayout());

        topLbl.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        topLbl.setForeground(new java.awt.Color(255, 255, 204));
        topLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        topLbl.setText("- - - - - - - - -");
        panQuestions.add(topLbl);

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
        panAnswer.setLayout(panAnswerLayout);
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
        add(panAnswer, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed

//        if (frame.isUndecorated()) {
        frame.hide();
        frame.dispose();
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnFullscreen;
    private javax.swing.JPanel panAnswer;
    private javax.swing.JPanel panBottom;
    private javax.swing.JPanel panQuestions;
    private javax.swing.JPanel panTop;
    private javax.swing.JLabel topLbl;
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
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new QuizPanel().setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(QuizPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void print(Term t) {
        System.out.print(t.toString());
    }
    GridBagConstraints gbc = new GridBagConstraints();

    public final void show(Term t) {
        topLbl.setText(useFront ? t.left : t.right);
        startTime = System.currentTimeMillis();
        int N = 6;
        gbc.weightx = 1;
        gbc.weighty = 1;
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < N / 2; x++) {
                gbc.gridx = x;
                gbc.gridy = y;
                panAnswer.add(new JButton(x + ", " + y), gbc);
            }
        }
    }

    private void updateScore() {

    }
}
