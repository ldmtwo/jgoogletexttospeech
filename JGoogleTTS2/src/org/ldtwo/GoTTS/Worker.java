                /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ldtwo.GoTTS;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sound.sampled.*;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import static org.ldtwo.GoTTS.G.pause;
import static org.ldtwo.GoTTS.G.play;

/**
 *
 * @author Larry Moore
 */
public class Worker {

    final public String language;

    public Worker() {
        language = "en";
    }

    public Worker(String language) {
        this.language = language;
    }

    public File getAndPlay(String query, boolean play) {
        try {
            //System.out.printf("getAndPlay: query=%s\n", query);
            query = query.replaceAll("[?]", " ").trim();
            while (query.contains("  ")) {
                query = query.replace("  ", " ");
            }
            String fname = query;
            if (query.contains("\t")) {
                fname = query.replaceAll("[\\s]", " ").trim();
                String[] arr = query.split("\t");
                query = arr[0];
            //System.out.printf("getAndPlay: query=%s; file=%s\n", query, fname);
            }
            SocketClient socket = new SocketClient(fname);
            socket.path.setText("translate_tts?ie=UTF-8&tl=" + language + "&q=" + URLEncoder.encode(query, "UTF-8"));
            socket.address.setText("translate.google.com");
            File file = socket.getFile();
//            fname = file.getAbsolutePath();
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
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    byte[] tmpBuffer = new byte[64];

    public void play(File file) {
        try {

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
        SourceDataLine line = getLine(targetFormat);
        if (line != null) {
            // Start
            line.start();
            int nBytesRead = 0, nBytesWritten = 0;
            while (nBytesRead != -1) {
                while(pause)G.zzzsleep(300);
                if(!play)break;
//                G.zzzsleep(1);
                nBytesRead = ais.read(tmpBuffer, 0, tmpBuffer.length);
                while(pause)G.zzzsleep(300);
                if (nBytesRead != -1) {
                    nBytesWritten = line.write(tmpBuffer, 0, nBytesRead);
//                    for(int i=0;i<nBytesRead;i+=20)
//                        line.write(tmpBuffer, i, 20);
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
    byte[] buff = new byte[1024 * 8];

    public File writeToFile(InputStream iss, String fileName) {
        URL u;
        DataInputStream dis;
        FileOutputStream fos;
        String s;
        File tmpFile;
        try {
            tmpFile = new File("CACHE\\" + language);
            if (!tmpFile.exists()) {
                tmpFile.mkdirs();
            }
            tmpFile = new File("CACHE\\" + language + "\\" + fileName);
//            if (tmpFile.exists()) {
//                return tmpFile;
//            }

            //tmpFile = File.createTempFile("jgoogle_tts-", ".mp3");
//            tmpFile.deleteOnExit();
            System.out.println(tmpFile.getAbsolutePath());
            fos = new FileOutputStream(tmpFile);
//            dis = new DataInputStream(new BufferedInputStream(iss, 512));
//            BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(iss)));
//            String header = "";
//            String line;
//            boolean inHeader = true;
//            int maxLoops = 10000;
            int len;
            try {
//                while ((line = br.readLine()) != null&&maxLoops-->0) {
//                    System.out.println("> i="+(maxLoops-10000));
//                    if (inHeader) {
//                        header += line + "\r\n";
//                        if (line.length() < 1) {
//                            inHeader = false;
//                        }
//                    } else {
//                        fos.write(line.getBytes());
//                    }
//                    //System.out.print(new String(buff, 0, len));
//                }

                while ((len = iss.read(buff)) >= 0) {
                    fos.write(buff, 0, len);
                }
//                if (maxLoops < 1) {
//                    throw new Exception("ERROR: INF LOOP DETECTED!");
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException iOException) {
            }
//            try {
//                br.close();
//            } catch (IOException iOException) {
//            }
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

    private class SocketClient implements java.lang.Runnable {

        public TextField address, path;
        Thread thread;
        Socket ourSocket;
        File tmpFile;

        public SocketClient(String fname) {
            new File("CACHE\\" + language).mkdirs();
            tmpFile = new File("CACHE\\" + language + "\\" + fname + ".mp3");
            address = new TextField("", 20);
            path = new TextField("", 20);
            thread = new Thread(this);
        }

        public File getFile() {
            try {
                if (tmpFile.exists() && tmpFile.length() > 1600) {
                    return tmpFile;
                }

                ourSocket = new Socket(address.getText(), 80);
                InputStream inStream = new DataInputStream(ourSocket.getInputStream());
                DataOutputStream outStream = new DataOutputStream(ourSocket.getOutputStream());
                //System.out.println("1)   http://translate.google.com/" + path.getText());
//                URI uri = new URI("http://translate.google.com/"+ path.getText().trim().replace(" ", "+"));
//
////                URL u = new URL(uri.toASCIIString());
//               
////                inStream=u.openStream();
                String requestString;
//              URL  url = new URL("http://translate.google.com/"+ path.getText());
//String context = url.getProtocol();
//String hostname = url.getHost();
//String thePath = path.getText().trim();
//int port = 80;
//thePath = thePath.replaceAll("(^/|/$)", ""); // removes beginning/end slash
//String encodedPath = URLEncoder.encode(thePath, "UTF-8"); // encodes unicode characters
//encodedPath = encodedPath.replace("+", "%20"); // change + to %20 (space)
//encodedPath = encodedPath.replace("%2F", "/"); // change %2F back to slash
//requestString = context + "://" + hostname + ":" + port + "/" + encodedPath;






                requestString = "GET http://translate.google.com/" + path.getText() + " HTTP/1.0\r\n"
                        + "User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1779.2 Safari/537.36\r\n"
                        + "Accept: audio/mpeg\r\n"
                        + "\r\n";
//                requestString ="GET "+ uri.toASCIIString() + " HTTP/1.0\r\n" + "\r\n";
//                System.out.println(uri.toASCIIString());
               // System.out.println("2)    " + requestString);
                outStream.writeBytes(requestString);
                outStream.flush();

                file = writeToFile(inStream, tmpFile.getName());
                if (file.length() < 2000) {
                    file.deleteOnExit();
                }

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
