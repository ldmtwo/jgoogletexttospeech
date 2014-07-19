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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import javax.sound.sampled.*;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import static org.ldtwo.GoTTS.G.*;
import org.ldtwo.flashcard.ImageManager;

/**
 *
 * @author Larry Moore
 */
public class AudioPlayer extends Thread {

    public final static ExecutorService executor = Executors.newFixedThreadPool(8);

    //static public String language;
    static final BlockingQueue<File> queue = new LinkedBlockingQueue<>();
    static private AudioPlayer ths = null;

    public synchronized static AudioPlayer getInstance() {
        if (ths == null) {
            ths = new AudioPlayer();
        }
        return ths;
    }

    private AudioPlayer() {
    }

    static public File playMP3(String query, String lang) {
        return getInstance().enqueue(lang, (query), true);
    }

    static public File getMP3(String query, String language) {
        try {
            System.out.printf("<<< MP3 <<< %s - %s\n", language, query);
            query = query.replaceAll("[?\"]", " ").trim();
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
            String url = "http://translate.google.com/translate_tts?ie=UTF-8&tl=" + language + "&q=" + URLEncoder.encode(query, "UTF-8");
            File file = ImageManager.query2AudioFile(query, language);
            Exception ex = ImageManager.download_httpConnection(url, file);

            if (ex != null) {
                ImageManager.printURLException(ex, url);
            }

            if (ex == null) {
                System.out.printf(">>>>> success URL: %s\n", url);
            } else {
                System.err.printf(">>>>> failed URL: %s\n", url);
            }
            return file;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //every 32 bytes = 16 values = 1 ms
    public static void main(String[] args) {
        AudioPlayer w = new AudioPlayer();
        File f = new File("C:\\Users\\Larry\\Documents\\NetBeansProjects\\JGoogleTTS\\CACHE\\ko\\82.mp3");
        w.play(f);

    }

    public void analyze(File file) {
        try {

            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            AudioInputStream din;
            AudioFormat baseFormat = in.getFormat();
            AudioFormat decodedFormat
                    = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                            baseFormat.getSampleRate(),
                            16,//sample size in bits
                            baseFormat.getChannels(),
                            //mono=2, stereo=4 bytes/frame
                            baseFormat.getChannels() * 2,//frame size = channels*#bytes
                            baseFormat.getSampleRate(),//frame rate
                            false);
            System.out.printf("%s, %s\n", file, decodedFormat);
//            din = AudioSystem.getAudioInputStream(decodedFormat, in);
            // Play now.
            //AudioPlayer.
//                    rawplay(decodedFormat, din
            //,file
//                    );
//            in.close();

            //experiment
//            AudioPlayer.getMic();
            in = AudioSystem.getAudioInputStream(file);
            din = AudioSystem.getAudioInputStream(decodedFormat, in);
            AudioAnalyzer.analyze2(decodedFormat, din, file);
            in.close();

        } catch (Exception ex) {
            System.err.println(file);
            ex.printStackTrace();
        }
    }

    synchronized static private void play(File file) {
        try {

            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            AudioInputStream din;
            AudioFormat baseFormat = in.getFormat();
            AudioFormat decodedFormat
                    = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                            baseFormat.getSampleRate(),
                            16,//sample size in bits
                            baseFormat.getChannels(),
                            //mono=2, stereo=4 bytes/frame
                            baseFormat.getChannels() * 2,//frame size = channels*#bytes
                            baseFormat.getSampleRate(),//frame rate
                            false);
            System.out.printf("%s, %s\n", file, decodedFormat);
            din = AudioSystem.getAudioInputStream(decodedFormat, in);
            // Play now.
            //AudioPlayer.
            playersCount.incrementAndGet();
            rawplay(decodedFormat, din
            //,file
            );

            in.close();

            playersCount.decrementAndGet();
            //experiment
//            AudioPlayer.getMic();
//            in = AudioSystem.getAudioInputStream(file);
//            din = AudioSystem.getAudioInputStream(decodedFormat, in);
//            AudioAnalyzer.analyze(decodedFormat, din, file);
//            in.close();
        } catch (Exception ex) {
            System.err.println(file);
            ex.printStackTrace();
        }
    }

    static byte[] tmpBuffer = new byte[32 * 1000];//10 sec

    static private void rawplay(AudioFormat targetFormat,
            AudioInputStream ais)
            throws IOException, LineUnavailableException {
        SourceDataLine line = getLine(targetFormat);
        if (playersCount.get() <= 1) {
            if (line != null) {
                // Start
                line.start();
                int nBytesRead = 0, nBytesWritten = 0;
                while (nBytesRead != -1 && playersCount.get() <= 1) {
                    while (pause.get()) {
                        G.zzzsleep(300);
                    }
                    if (!play) {
                        break;
                    }
//                G.zzzsleep(1);
                    nBytesRead = ais.read(tmpBuffer, 0, tmpBuffer.length);
                    if (nBytesRead > 0) {
                        int pos = 0;
                        int remaining = nBytesRead;
                        while (remaining > 0) {
                            nBytesWritten = line.write(tmpBuffer, pos, Math.min(4 * 128, remaining));
                            remaining -= nBytesWritten;
                            pos += nBytesWritten;
                            while (pause.get() && playersCount.get() <= 1) {//maybe pause
                                G.zzzsleep(100);
                                if (G.skip) {//maybe skip
                                    G.skip = false;
                                    pos = bound(pos + G.skipDelta, 0, nBytesRead);
                                    remaining = bound(remaining + G.skipDelta, 0, nBytesRead);
                                    G.skipDelta = 0;
                                }
                            }
                        }
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
    }

    static private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
        SourceDataLine res;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

        res = (SourceDataLine) AudioSystem.getLine(info);
        res.open(audioFormat);

        return res;
    }

    byte[] buff = new byte[1024 * 8];

    public File writeToFile(InputStream iss, String fileName,String language) {
        URL u;
        DataInputStream dis;
        FileOutputStream fos;
        String s;
        File tmpFile;
        try {
            tmpFile = new File(AUDIO_PATH + language);
            if (!tmpFile.exists()) {
                tmpFile.mkdirs();
            }
            tmpFile = new File(AUDIO_PATH + language + "\\" + fileName);
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

    final public static int bound(int val, int lowerBound, int upperBound) {
        //don't exceed upper and don't go below lower
        return Math.min(upperBound, Math.max(lowerBound, val));
    }

    final static public double bound(double val, double min, double max) {
        if (Double.isNaN(val)) {
            return min;
        }
        return min > val ? min : max < val ? max : val;
    }

    public File enqueue(String la, String s, boolean b) {
        File f = getMP3(s, la);
        if (b) {
            enqueue( f);
        }
        return f;
    }

    synchronized public void enqueue( File f) {
        queue.add(f);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
                File file = queue.take();
                File ftmp;
                while (queue.size() > 1&&G.ENABLE_DRAIN_QUEUE) {
                    
                    ftmp = queue.take();
                    if(ftmp!=null)file=ftmp;
                }
                
               
                play(file);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}
