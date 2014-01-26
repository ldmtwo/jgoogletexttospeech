
package org.ldtwo.GoTTS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class FileTree extends JPanel {
    public final JTree root;
    /**
     * Construct a FileTree
     */
    public FileTree(File dir, TreeSelectionListener listener) {
        setLayout(new BorderLayout());

        // Make a tree list with all the nodes, and make it a JTree
        root = new JTree(addNodes(null, dir));

        // Add a listener
        root.addTreeSelectionListener(listener);
        
//        new TreeSelectionListener() {
//            public void valueChanged(TreeSelectionEvent e) {
//                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
//                        .getPath().getLastPathComponent();
//                System.out.println("You selected " + node);
//            }
//        });

        // Lastly, put the JTree into a JScrollPane.
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(root);
        add(BorderLayout.CENTER, scrollpane);
    }

    /**
     * Add nodes from under "dir" into curTop. Highly recursive.
     */
    DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, File dir) {
        String curPath = dir.getPath();
        DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(dir.getName(),true);
        if (curTop != null) { // should only be null at root
            curTop.add(curDir);
        }
//        Vector ol = new Vector();
//        String[] tmp = dir.list();
        System.out.println(dir);
        File[] files=dir.listFiles();
        List<File> list=Arrays.asList(files);
//        for (int i = 0; i < tmp.length; i++) {
//            ol.addElement(tmp[i]);
//        }
        Collections.sort(list);
//        Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);
        File f;
//        Vector files = new Vector();
        // Make two passes, one for Dirs and one for Files. This is #1.
        for (int i = 0; i < list.size(); i++) {
            String thisObject = (String) list.get(i).getName();
            String newPath;
//            if (curPath.equals(".")) {
//                newPath = thisObject;
//            } else {
//                newPath = curPath + File.separator + thisObject;
//            }
             newPath = thisObject;
            if ((f = list.get(i)).isDirectory()) {
                addNodes(curDir, f);
            } else {
                //files.addElement(thisObject);
//                 curDir.add(new DefaultMutableTreeNode(list.get(i).getName()));
            }
        }
        // Pass two: for files.
        for (int fnum = 0; fnum < files.length; fnum++) {
            if(!files[fnum].isDirectory()&&files[fnum].getName().toLowerCase().endsWith(".mp3"))
            curDir.add(new DefaultMutableTreeNode(files[fnum].getName()));
        }
        return curDir;
    }

    public Dimension getMinimumSize() {
        return new Dimension(200, 400);
    }

    public Dimension getPreferredSize() {
        return new Dimension(200, 400);
    }

//    /**
//     * Main: make a Frame, add a FileTree
//     */
//    public static void main(String[] av) {
//
//        JFrame frame = new JFrame("FileTree");
//        frame.setForeground(Color.black);
//        frame.setBackground(Color.lightGray);
//        Container cp = frame.getContentPane();
//
//        if (av.length == 0) {
//            cp.add(new FileTree(new File(".")));
//        } else {
//            cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
//            for (int i = 0; i < av.length; i++) {
//                cp.add(new FileTree(new File(av[i])));
//            }
//        }
//
//        frame.pack();
//        frame.setVisible(true);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    }
}

class WindowCloser extends WindowAdapter {

    public void windowClosing(WindowEvent e) {
        Window win = e.getWindow();
        win.setVisible(false);
        System.exit(0);
    }
}
