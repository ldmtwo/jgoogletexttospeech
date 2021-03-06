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
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Future;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.ldtwo.GoTTS.MainFrame;
import org.ldtwo.GoTTS.G;
import org.ldtwo.GoTTS.Languages;
import org.ldtwo.GoTTS.AudioPlayer;
import static org.ldtwo.flashcard.ImageManager.imageQueue;

/**
 * True and false quiz (aud vs img)
 *
 * @author ldtwo
 */
public class ReviewPanel extends javax.swing.JPanel {

    DecimalFormat df = new DecimalFormat("0.#");
    public File[] files = null;
    public LinkedList<Term> deck = new LinkedList<>();
    public ArrayList<Term> shuffledDeck = new ArrayList<>();
    public boolean useFront = true;
    public Term currentTerm = null;
    public long startTime = System.currentTimeMillis();
    final long SLEEP_DURATION = 15000;
    private static int numOptions = 6;
    final Term[] slots = new Term[16];
    JButton[] buttons;
    //Worker player=new AudioPlayer();
    MainFrame player = MainFrame.ths;
    JFrame frame = null;
    int chances = 2;
    JPopupMenu languageMenu = new JPopupMenu();
    final String leftLanguage;
    final String rightLanguage;
    public ImageManager imageDownloader;

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
     * @param inputFile
     * @param leftLanguage
     * @param rightLanguage
     * @throws java.lang.Exception
     */
    public ReviewPanel(MainFrame f2, JFrame hostFrame, Container hostContainer, File inputFile,
            String leftLanguage, String rightLanguage) throws Exception {
        this.leftLanguage = leftLanguage;
        this.rightLanguage = rightLanguage;

        player = f2;

        deck = FlashCardFrame.getDeck(inputFile);
        shuffleDeckArray();
        imageDownloader = new ImageManager();
        imageDownloader.loadDeckWithImages(deck);
int i=0;
        for (Term t : deck) {
            //System.out.printf("%s\n",t.info());
           
        //synchronized (imageQueue) 
        {
            for (ImageFile f : t.getLeftSet()) {

                //f=imageQueue.take();
                imageQueue.remove(f);
                f.priority = i+1;
                imageQueue.add(f);
                
            }
        }
        //synchronized (imageQueue) 
        {
            for (ImageFile f : t.getRightSet()) {

                //f=imageQueue.take();
                imageQueue.remove(f);
                f.priority = i;
                imageQueue.add(f);
                
            }
        }
                i+=2;
        }

        buttons = new JButton[deck.size()];
        for (JButton b : buttons) {
            if (b != null) {
                b.addMouseListener(action);
            }
        }
        frame = hostFrame;
        //frame=new JFrame();
        if (frame != null) {
            frame.setContentPane(this);
            frame.setUndecorated(true);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        } else if (frame == null && hostContainer != null) { //hostContainer is not null
            hostContainer.add(this);
        }
        initComponents();

        btnFullscreen.setVisible(false);

        if (frame != null) {
            frame.pack();
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
        rightActionPerformed(null);
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
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
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

        jTextField1.setText("jTextField1");

        jTextField2.setText("jTextField2");

        javax.swing.GroupLayout menuPanelLayout = new javax.swing.GroupLayout(menuPanel);
        menuPanel.setLayout(menuPanelLayout);
        menuPanelLayout.setHorizontalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuPanelLayout.createSequentialGroup()
                .addContainerGap(287, Short.MAX_VALUE)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(117, 117, 117))
        );
        menuPanelLayout.setVerticalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPanelLayout.createSequentialGroup()
                .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 11, Short.MAX_VALUE))
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
                .addContainerGap(472, Short.MAX_VALUE)
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
        panQuestions.setMaximumSize(new java.awt.Dimension(32767, 100));
        panQuestions.setName(""); // NOI18N
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
        WindowListener[] listeners = frame.getWindowListeners();
        for (WindowListener l : listeners) {
            frame.removeWindowListener(l);
        }
        frame.hide();
        frame.dispose();
        frame = new JFrame();
        for (WindowListener l : listeners) {
            frame.addWindowListener(l);
        }
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
        WindowListener[] listeners = frame.getWindowListeners();
        for (WindowListener l : listeners) {
            frame.removeWindowListener(l);
        }
        frame.hide();
        frame.dispose();
        JFrame f2 = new JFrame();
        for (WindowListener l : listeners) {
            f2.addWindowListener(l);
        }
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
        if (deck.size() <= 0) {
            return;
        }
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

        t=deck.peek();
        
            for (ImageFile f : t.getRightSet()) {
                if(!imageQueue.remove(f))continue;
                f.priority = 1;
                imageQueue.add(f);
            }
            for (ImageFile f : t.getLeftSet()) {
                if(!imageQueue.remove(f))continue;
                f.priority = 2;
                imageQueue.add(f);
            }
        
        
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
//                    JButton btn = ImageManager.imageFile2jButton(width, width * 3 / 4, f);
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
    public javax.swing.JButton btnExit;
    public javax.swing.JButton btnFullscreen;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JPanel menuPanel;
    public javax.swing.JPanel panAnswers;
    public javax.swing.JPanel panBottom;
    public javax.swing.JPanel panQuestions;
    private javax.swing.JPanel panTop;
    protected javax.swing.JLabel question;
    private javax.swing.JButton right;
    // End of variables declaration//GEN-END:variables

    public void print(Term t) {
        System.out.print(t.toString());
    }
    GridBagConstraints gbc = new GridBagConstraints();
    Thread audiPlayFuture = null;

    public void playAudio(Term t) {
        if (audiPlayFuture != null) {
            audiPlayFuture.stop();
        }
        final String label = useFront ? t.left : t.right;
        final String reverse = !useFront ? t.left : t.right;
        question.setText(String.format("<html>%s<br>%s", label, reverse));
        Runnable run = new Runnable() {

            @Override
            public void run() {
                try {
                    String query1 = G.withoutOptions(label);
                    File mp3 = AudioPlayer.getMP3(query1, leftLanguage);
                    G.play = true;
                    G.pause.set(false);
                    AudioPlayer.getInstance().enqueue(mp3);
                    String query2 = G.withOptions(label);
                    if (!query1.equals(query2)) {
                        Thread.sleep(500);
                        File f = AudioPlayer.getMP3(query2, leftLanguage);
                        AudioPlayer.getInstance().enqueue(f);
                    }
                    //other side
                    query1 = G.withoutOptions(reverse);
                    mp3 = AudioPlayer.getMP3(query1, rightLanguage);
                    G.play = true;
                    G.pause.set(false);
                    AudioPlayer.getInstance().enqueue(mp3);
                    query2 = G.withOptions(reverse);
                    if (!query1.equals(query2)) {
                        Thread.sleep(500);
                        File f = AudioPlayer.getMP3(query2, rightLanguage);
                        AudioPlayer.getInstance().enqueue(f);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        audiPlayFuture = new Thread(run);
        audiPlayFuture.start();
    }

    public void show(Term t) {
        startTime = System.currentTimeMillis();
        playAudio(t);

        int N = 6;
        gbc.weightx = 1;
        gbc.weighty = 1;
        final HashSet<ImageFile> set;// = useFront ? t.leftSet : t.rightSet;
        set = new HashSet(t.getLeftSet());
        set.addAll(t.getRightSet());
        //synchronized (imageQueue) 
        {
            for (ImageFile f : set) {
                if(!imageQueue.remove(f))continue;
                f.priority = 0;
                imageQueue.add(f);
            }
        }
        N = set.size();
        Iterator<ImageFile> iter = set.iterator();
        int width = question.getWidth() * 3 / 16;

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < N / 2.0; x++) {
                gbc.gridx = x;
                gbc.gridy = y;
                try {
                    ImageFile o = iter.next();
                    if (o == null) {
                        continue;
                    }
                    //if (o instanceof File)
                    {
                        final File f = o.file;
                        System.out.printf("%s\t%s\n", f.exists(), f);
                        try {
                            JToggleButton btn = ImageManager.imageFile2jButton_x(width, width * 3 / 4, f);
                            panAnswers.add(btn, gbc);
//                    gbc.gridy = (gbc.gridy + 1) % 2;
//                    gbc.gridx = (gbc.gridx + 1) % 2;
//                            btn.addMouseListener(action);
//                            btn.addMouseMotionListener(action);
//                            btn.addKeyListener(action);

//                            btn.addMouseListener(new MouseAdapter() {
//                                
//                                @Override
//                                public void mouseClicked(MouseEvent e) {
//                                    if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
//                                        synchronized (set) {
//                                            set.remove(f);
//                                        }
//                                    }
//                                }
//                                
//                            });
                        } catch (Exception e) {
                            System.out.printf("%s: %s\n", e.getMessage(), f);
                            // e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                }
//                panAnswers.add(new JButton(x + ", " + y), gbc);

            }
        }
        System.out.printf("[[[[[%s]]]]]]\n\n", (System.currentTimeMillis() - startTime));
    }

    public void updateScore() {

    }
}
