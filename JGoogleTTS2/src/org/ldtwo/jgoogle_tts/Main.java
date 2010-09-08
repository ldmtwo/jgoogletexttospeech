                /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ldtwo.jgoogle_tts;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.net.*;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;

/**
 *
 * @author Larry Moore
 */
public class Main {
public String language="en";

    public Main() {
    }

    public Main(String language) {
        this.language = language;
    }

    public File getAndPlay(String site, String path, boolean play) {
        try {
            SocketClient socket = new SocketClient();
            socket.path.setText("translate_tts?tl="+language+"&q="+path);
            socket.address.setText(site);
            File file = socket.getFile();
            String fname = "";
            fname = file.getAbsolutePath();
                //String arg = "cmd /C start \"title\" \"" + fname + "\"";
                //String []args={"cmd","/C start \"title\" \""+file.getAbsolutePath()+"\""};
                //System.out.println(arg);
                //ProcessBuilder pb=new ProcessBuilder(args);
                //pb.start();
                //proc.waitFor();
                //String arg="start "+file.getAbsolutePath()+"";
            if (play) {
                try {

                    AdvancedPlayer p = new AdvancedPlayer(new FileInputStream(file));
                    p.play();
//                   for(int t=0;t<10000;t++){
//                    p.play(t,t+120);
//                    t+=90;
//                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return file;
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void play(File file) {
        try {
            Player p = new Player(new FileInputStream(file));
            p.play();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

     public File getMP3() {
        URL u;
        InputStream is = null;
        DataInputStream dis;
        FileOutputStream fos;
        String s;
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("jgoogle_tts-" + System.currentTimeMillis(), ".mp3");
            fos = new FileOutputStream(tmpFile);
            u = new URL("http://translate.google.com/translate_tts?tl="+language+"&q=text");
            is = u.openStream();         // throws an IOException
            dis = new DataInputStream(new BufferedInputStream(is));
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = dis.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
//            System.out.println("MP3 @ " + tmpFile.getAbsolutePath());
            return tmpFile;
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
            // System.exit(1);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            // System.exit(1);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
            }
        } // end of 'finally' clause
        return tmpFile;
    }

     public File getMP3(InputStream iss) {
        URL u;
        DataInputStream dis;
        FileOutputStream fos;
        String s;
        File tmpFile;
        try {
            tmpFile = new File(File.listRoots()[0] + "\\temp\\jgoogle_tts-" + System.currentTimeMillis() + ".mp3");
            fos = new FileOutputStream(tmpFile);
            dis = new DataInputStream(new BufferedInputStream(iss));
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = dis.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
            try {
                fos.close();
            } catch (IOException iOException) {
            }
            try {
                dis.close();
            } catch (IOException iOException) {
            }
            if (tmpFile == null) {
                System.out.println("1getMP3==null");
            }
            return tmpFile;
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        System.out.println("2getMP3==null");
        return null;
    }

    static private class SocketClient implements Runnable {

        public TextField address, path;
        Thread thread;
        Socket ourSocket;

        public SocketClient() {

            address = new TextField("", 20);
            path = new TextField("", 20);
            thread = new Thread(this);
        }
        public File getFile() {
            try {
                ourSocket = new Socket(address.getText(), 80);
                DataInputStream inStream //Notice how we kill a few birds with one stone.
                        = new DataInputStream(ourSocket.getInputStream());
                DataOutputStream outStream //Here too!
                        = new DataOutputStream(ourSocket.getOutputStream());
                String requestString = "GET /" + path.getText() + " HTTP/1.0\r\n" + "\r\n";
                outStream.writeBytes(requestString);
                outStream.flush();
                StringBuffer buff = new StringBuffer();
                file = new Main().getMP3(inStream);
              try {
                    inStream.close();
                } catch (IOException iOException) {
                }
                try {
                    outStream.close();
                } catch (IOException iOException) {
                }
                try {
                    ourSocket.close();
                } catch (IOException iOException) {
                }
                if (file == null) {
                    System.out.println("1getFile==null");
                }
                return file;
            } catch (Exception e) {
                System.err.println("Exception with: " + e.getMessage() + "\n" + e.toString());
                e.printStackTrace();
            }
            if (file == null) {
                System.out.println("2getFile==null");
            }
            return file;
        }
        private File file;
        public void run() {
            getFile();
        }
    }
}
