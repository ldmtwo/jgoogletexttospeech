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
import javax.sound.sampled.*;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

/**
 *
 * @author Larry Moore
 */
public class Worker2 {

    public String language = "en_gb";

    public Worker2() {
    }

    public Worker2(String language) {
        this.language = language;
    }

    public File getAndPlay(String query, boolean play) {
        try {
            //URI uri = new URI("http://translate.google.com/translate_tts?tl=zh-TW&q=你好");
            //URL u = new URL(uri.toASCIIString());
            SocketClient socket = new SocketClient(query);
            socket.path.setText("translate_tts?tl=" + language + "&q=" + query);
            socket.address.setText("translate.google.com");
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
                    String data = "";
                    FileInputStream is = new FileInputStream(file);
                    byte[] b = new byte[(int) file.length()];
                    is.read(b);
                    data += new String(b);
                    if (data.contains("html")) {
                        JOptionPane.showMessageDialog(null, new JLabel(data));
                    }
//                    AdvancedPlayer p = new AdvancedPlayer(new FileInputStream(file));
//                    p.play();

                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();
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
            Logger.getLogger(Worker2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void play(File file) {
        try {

            String data = "";
            FileInputStream is = new FileInputStream(file);
            byte[] b = new byte[(int) file.length()];
            is.read(b);
            data += new String(b);
//System.out.println(data);
            data += new String(b);
            if (data.contains("html")) {
                JOptionPane.showMessageDialog(null,
                        "An error ocurred retrieving the file");
                System.exit(1);
            }


            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            AudioInputStream din;
            AudioFormat baseFormat = in.getFormat();
            AudioFormat decodedFormat =
                    new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false);
            din = AudioSystem.getAudioInputStream(decodedFormat, in);
            // Play now.
            rawplay(decodedFormat, din);
            in.close();


        } catch (Exception ex) {
            System.err.println(file);
            ex.printStackTrace();
        }
    }

    private void rawplay(AudioFormat targetFormat,
            AudioInputStream ais)
            throws IOException, LineUnavailableException {
        byte[] data = new byte[4096];
        SourceDataLine line = getLine(targetFormat);
        if (line != null) {
            // Start
            line.start();
            int nBytesRead = 0, nBytesWritten = 0;
            while (nBytesRead != -1) {
                nBytesRead = ais.read(data, 0, data.length);
                if (nBytesRead != -1) {
                    nBytesWritten = line.write(data, 0, nBytesRead);
                }
            }
            // Stop
            line.drain();
            line.stop();
            line.close();
            ais.close();
        }
    }

    private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
        SourceDataLine res;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        res = (SourceDataLine) AudioSystem.getLine(info);
        res.open(audioFormat);
        return res;
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
            u = new URL("http://translate.google.com/translate_tts?tl=" + language + "&q=text");
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

    static public File writeToFile(InputStream iss, String fileName) {
        URL u;
        DataInputStream dis;
        FileOutputStream fos;
        String s;
        File tmpFile;
        try {
            tmpFile = new File("CACHE");
            if (!tmpFile.exists()) {
                tmpFile.mkdirs();
            }
            tmpFile = new File("CACHE\\" + fileName);
            if (tmpFile.exists() && tmpFile.length()>0) {
                return tmpFile;
            }

            //tmpFile = File.createTempFile("jgoogle_tts-", ".mp3");
            //tmpFile.deleteOnExit();
            System.out.println(tmpFile.getAbsolutePath());
            fos = new FileOutputStream(tmpFile);
            dis = new DataInputStream(new BufferedInputStream(iss));
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = dis.read(buff)) != -1) {
                fos.write(buff, 0, len);
                System.out.print(new String(buff, 0, len));
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


        File tmpFile;
        private SocketClient(String q) {
            tmpFile = new File("CACHE\\" + q + ".mp3");
            address = new TextField("", 20);
            path = new TextField("", 20);
            thread = new Thread(this);
        }

        public File getFile() {
            try {
                if (tmpFile.exists()) {
                    return tmpFile;
                }
                
                ourSocket = new Socket(address.getText(), 80);
                DataInputStream inStream //Notice how we kill a few birds with one stone.
                        = new DataInputStream(ourSocket.getInputStream());
                DataOutputStream outStream //Here too!
                        = new DataOutputStream(ourSocket.getOutputStream());
                String requestString = "GET http://translate.google.com/" + path.getText() + " HTTP/1.0";
                System.out.println(requestString);
                outStream.writeBytes(requestString);
                outStream.flush();
                
                file = writeToFile(inStream, tmpFile.getName());

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
            file = getFile();
        }
    }
}
