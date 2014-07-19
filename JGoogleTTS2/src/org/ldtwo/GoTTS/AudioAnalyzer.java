/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ldtwo.GoTTS;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import static java.lang.Math.*;
import java.util.HashMap;
import javax.imageio.ImageIO;
import sun.awt.image.WritableRasterNative;
import static org.ldtwo.GoTTS.AudioPlayer.*;

/**
 *
 * @author ldtwo
 */
public class AudioAnalyzer {

    static final int SCALE2 = 5;
    static final double SCALE = 1;
    static int OFFSET = (int) 300;
    static double HPalpha = 1;
    static final LowPass lpFilter = new LowPass();
    static final LowPass lpFilter0 = new LowPass(0.4);
    static final LowPass lpFilter1 = new LowPass(0.15);
    static final LowPass lpFilter2 = new LowPass(0.05);
    static final HiPass hpFilter = new HiPass();
    static byte[] tmpBuffer = new byte[32 * 100];

    interface IIRFilter {

        public double next(double x);
    }

    static public class LowPass implements IIRFilter {

        double LPalpha = 0.5;
        double y_old = 0;
        final double calibration_dB = 0;

        private LowPass(double d) {
            LPalpha = d;
        }

        private LowPass() {
        }

        public double next(double x) {
            final double y = LPalpha * x + (1.0 - LPalpha) * y_old;
            y_old = y;
            return y;
        }

        double dbAmplitude(double amplitude) {
            return 20 * Math.log10(amplitude) + calibration_dB;
        }
    }

    static public class HiPass implements IIRFilter {

        double y_old = 0, prev = 0;
        final double calibration_dB = 0;

        @Override
        public double next(double x) {
            double y = next(x, prev);
            prev = x;
            return y;
        }

        double next(double x1, double x2) {
            final double y = y_old * HPalpha + HPalpha * (x2 - x1);//next-current
            y_old = y;
            return y;
        }

        double dbAmplitude(double amplitude) {
            return 20 * Math.log10(amplitude) + calibration_dB;
        }
    }

    public static double[] readPCM(AudioInputStream is) throws IOException {
        double[] arr = new double[(int) is.getFrameLength()];
        int len = 0, val, j = 0, i;
        byte b = 0;
        boolean oddByte = false;
        while ((len = is.read(tmpBuffer, 0, tmpBuffer.length)) >= 0) {
            if (oddByte) {
                i = 1;
                val = ((b << 8) | (tmpBuffer[0] & 0xFF));
                arr[j++] = val;
                oddByte = false;
            } else {
                i = 0;
            }
            for (; i < len - 1; i += 2) {
                if (i >= len) {
                    break;
                }
                //little endian
                val = ((tmpBuffer[i + 1] << 8) | (tmpBuffer[i] & 0xFF));
                arr[j++] = val;
            }
            if (i % 2 == 1) {
                oddByte = true;
                b = tmpBuffer[len - 1];
            }
        }
        return arr;
    }

    public static double[] filter(double[] in, IIRFilter filt) {
        double[] arr = new double[in.length];
        int i = 0;
        filt.next(0);
        for (; i < arr.length; i++) {
            arr[i] = filt.next(in[i]);
        }
        return arr;
    }

    public static void analyze2(AudioFormat targetFormat,
            AudioInputStream ais, File file)
            throws IOException, LineUnavailableException {
        final ArrayList<Integer> ampArray = new ArrayList<>();
        final ArrayList<Integer> a0 = new ArrayList<>();
        final ArrayList<Integer> a1 = new ArrayList<>();
        final ArrayList<Integer> a2 = new ArrayList<>();
        //double[] alphas={0.0001,0.001,0.005,0.01};
        //for(double alpha:alphas)
        {
            {
                int nBytesRead = 0, nBytesWritten = 0;
                while (nBytesRead != -1) {
//                G.zzzsleep(1);
                    nBytesRead = ais.read(tmpBuffer, 0, tmpBuffer.length);

                    if (nBytesRead != -1) {
//                        int b = targetFormat.getSampleSizeInBits() / 2;
                        //assume that sample size is evenly divisable by 8
                        int val;
//                        double avg = 0;

//                        if (nBytesRead % 32 != 0) {
//                            System.out.println("Warning: nBytesRead=" + nBytesRead);
//                        }
                        for (int i = 0; i < nBytesRead - 2; i += 2) {
                            double zeroes = 0;
                            //skip ahead
                            while (i < nBytesRead && (tmpBuffer[i + 1] << 8) + tmpBuffer[i] == 0) {
                                i += 2;
                                zeroes++;
//                                ampArray.add(0);
                            }
                            if (zeroes > 10) {
                                System.out.printf("0 for %s ms (%s frames)\n",
                                        1000.0 * zeroes / targetFormat.getSampleRate(),
                                        (int) zeroes);
                            }
                            if (i >= nBytesRead) {
                                break;
                            }
                            //little endian
                            val = ((tmpBuffer[i + 1] << 8) | (tmpBuffer[i] & 0xFF));
//                            val |= (tmpBuffer[i + 1]) << 8;
//                            val |= tmpBuffer[i];
                            //val = (tmpBuffer[i + 1] << 8) + tmpBuffer[i];
                            val = (int) hpFilter.next(val, ((tmpBuffer[i + 3] << 8) | (tmpBuffer[i + 2] & 0xFF)));
                            double val0, val1, val2;
                            val2 = lpFilter2.next(lpFilter2.next(val));//lowest freq
                            val = (int) (val - val2);//remove lowest freq
                            val1 = lpFilter1.next(lpFilter1.next(val));//med freq
                            val = (int) (val - val1);//remove med freq
                            val0 = (int) lpFilter0.next(lpFilter0.next(val));//high freq
                            val = (int) (val - val0);//highest freqs
//                             val0=val1-val0;
//                             val1=val2-val1;
//                            val2=val-val2;
//                            val=(int) (val0+val1+val2);

                            final int filteredValue = (int) val;
                            //if (i % 8 == 0)
                            {
                                ampArray.add(filteredValue);
                                a0.add((int) val0);
                                a1.add((int) val1);
                                a2.add((int) val2);
                            }
//                        System.out.printf("%s (%s, %s), ",
//                                filteredValue, val, (int) lpFilter.dbAmplitude(val));
                            if (i % 32 == 0) {//every 32 bytes = 16 values = 1 ms
                                //avg of 16 values
                                if (i > 0) {//skip first case
//                                System.out.printf("\n\tAVG = %s \n", avg / 16);
                                }
//                                avg = 0;
                            }
//                            avg += val;
                        }
//                    System.out.printf("\n\tAVG = %s \n", avg / 16);

//                    for(int i=0;i<nBytesRead;i+=20)
//                        line.write(tmpBuffer, i, 20);
                    }//END if (nBytesRead != -1)
                }//END while (nBytesRead != -1)
                final double[] mag = FFT.getFrequency(ampArray, targetFormat.getSampleRate());
                JFrame frame1 = new JFrame(file.getAbsolutePath());
                final int MAX_H = 800;
                String title = file.getAbsolutePath();

                final Complex[] fourier = FFT.get(ampArray, targetFormat.getSampleRate());
                JFrame frame2 = plotFFT(title, fourier, MAX_H);

                frame2.dispose();
                {
                    JPanel pan = new JPanel() {
                        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;

                        @Override
                        public void paint(Graphics g) {

                            super.paint(g);

                            for (int i : ampArray) {
                                if (i > max) {
                                    max = i;
                                }
                                if (i < min) {
                                    min = i;
                                }
                            }
                            double range = max - min;
                            g.setColor(Color.black);
                            g.fillRect(0, 0, (int) (ampArray.size() / SCALE), MAX_H);

                            g.setColor(Color.blue);
                            final int barx = MAX_H - (int) (MAX_H * (min) / range) + 10;
                            g.drawLine(0, barx, (int) (ampArray.size() / SCALE), barx);

                            g.setColor(Color.DARK_GRAY);
//                            double x = 0;
                            int y0, y1, y2;
                            int h = 1;
                            for (int x = 0; x < ampArray.size() - 1; x++) {
//                                y = ampArray.get(i) + 300;
//                                y1 = MAX_H - (int) (MAX_H * (ampArray.get(x) - min) / range) + 10;
                                y2 = MAX_H - (int) (MAX_H * (ampArray.get(x + 1) - min) / range) + 10 - OFFSET;
//                                h = Math.abs(ampArray.get(x) - ampArray.get(x + 1)) + 1;
                                g.drawLine((int) (x / SCALE), 0, (int) ((x + 1) / SCALE), y2);
                            }

                            g.setColor(Color.green);
                            for (int x = 0; x < mag.length - 1; x++) {
                                g.drawLine((int) (x / SCALE), 0, (int) ((x + 1) / SCALE), (int) (mag[x]));
                            }

                            g.setColor(Color.red);
                            for (int x = 0; x < ampArray.size() - 1; x++) {
//                                y = ampArray.get(i) + 300;
                                y1 = MAX_H - (int) (MAX_H * (ampArray.get(x) - min) / range) + 10 - OFFSET;
                                y2 = MAX_H - (int) (MAX_H * (ampArray.get(x + 1) - min) / range) + 10 - OFFSET;
//                                h = Math.abs(ampArray.get(x) - ampArray.get(x + 1)) + 1;
                                g.drawLine((int) (x / SCALE), y1, (int) ((x + 1) / SCALE), y2);
                            }
                            g.setColor(Color.CYAN);
                            for (int x = 1; x < a0.size() - 1; x++) {
//                                y = ampArray.get(i) + 300;
                                y1 = MAX_H - (int) (MAX_H * (a0.get(x) / SCALE2 - min) / range) + 10 + 200 - OFFSET;
                                y2 = MAX_H - (int) (MAX_H * (a0.get(x + 1) / SCALE2 - min) / range) + 10 + 200 - OFFSET;
//                                h = Math.abs(ampArray.get(x) - ampArray.get(x + 1)) + 1;
                                g.drawLine((int) (x / SCALE), y1, (int) ((x + 1) / SCALE), y2);
                                if (a0.get(x) - a0.get(x - 1) < 0 && a0.get(x) - a0.get(x + 1) < 0) {

                                    g.setColor(Color.blue);
                                    g.drawLine((int) (x / SCALE), y1, (int) ((x) / SCALE), y1 + 1);
                                    g.setColor(Color.CYAN);
                                }
                                if (a0.get(x) - a0.get(x - 1) > 0 && a0.get(x) - a0.get(x + 1) > 0) {

                                    g.setColor(Color.red);
                                    g.drawLine((int) (x / SCALE), y1 - 1, (int) ((x) / SCALE), y1);
                                    g.setColor(Color.CYAN);
                                }
                            }

                            g.setColor(Color.ORANGE);
                            for (int x = 1; x < a1.size() - 1; x++) {
//                                y = ampArray.get(i) + 300;
                                y1 = MAX_H - (int) (MAX_H * (a1.get(x) / SCALE2 - min) / range) + 10 + 350 - OFFSET;
                                y2 = MAX_H - (int) (MAX_H * (a1.get(x + 1) / SCALE2 - min) / range) + 10 + 350 - OFFSET;
//                                h = Math.abs(ampArray.get(x) - ampArray.get(x + 1)) + 1;
                                g.drawLine((int) (x / SCALE), y1, (int) ((x + 1) / SCALE), y2);
                                if (a1.get(x) - a1.get(x - 1) < 0 && a1.get(x) - a1.get(x + 1) < 0) {

                                    g.setColor(Color.blue);
                                    g.drawLine((int) (x / SCALE), y1, (int) ((x + 1) / SCALE), y2);
                                    g.drawLine((int) (x / SCALE), y1, (int) ((x) / SCALE), y1 + 1);
                                    g.setColor(Color.ORANGE);
                                }
                                if (a1.get(x) - a1.get(x - 1) > 0 && a1.get(x) - a1.get(x + 1) > 0) {

                                    g.setColor(Color.red);
                                    g.drawLine((int) (x / SCALE), y1, (int) ((x + 1) / SCALE), y2);
                                    g.drawLine((int) (x / SCALE), y1 - 1, (int) ((x) / SCALE), y1);
                                    g.setColor(Color.ORANGE);
                                }
                            }

                            g.setColor(Color.lightGray);
                            for (int x = 1; x < a2.size() - 1; x++) {
//                                y = ampArray.get(i) + 300;
                                y0 = MAX_H - (int) (MAX_H * (a2.get(x - 1) / SCALE2 - min) / range) + 10 + 500 - OFFSET;
                                y1 = MAX_H - (int) (MAX_H * (a2.get(x) / SCALE2 - min) / range) + 10 + 500 - OFFSET;
                                y2 = MAX_H - (int) (MAX_H * (a2.get(x + 1) / SCALE2 - min) / range) + 10 + 500 - OFFSET;
//                                h = Math.abs(ampArray.get(x) - ampArray.get(x + 1)) + 1;
                                if (a2.get(x) - a2.get(x - 1) <= 0 && a2.get(x) - a2.get(x + 1) <= 0) {

                                    g.setColor(Color.blue);
//                                    g.drawLine((int) (x / SCALE), y1, (int) ((x ) / SCALE), y1 + 1);
                                    g.drawLine((int) (x / SCALE), y1, (int) ((x + 1) / SCALE), y2);
                                    g.setColor(Color.lightGray);
                                } else if (a2.get(x) - a2.get(x - 1) >= 0 && a2.get(x) - a2.get(x + 1) >= 0) {

                                    g.setColor(Color.red);
//                                    g.drawLine((int) (x / SCALE), y1 - 1, (int) ((x ) / SCALE), y1);
                                    g.drawLine((int) (x / SCALE), y1, (int) ((x + 1) / SCALE), y2);
                                    g.setColor(Color.lightGray);
                                } else {
                                    g.drawLine((int) (x / SCALE), y1, (int) ((x + 1) / SCALE), y2);

                                }
                            }

                        }

                    };
                    JScrollPane jsp = new JScrollPane();
                    jsp.getViewport().add(pan);
                    frame1.setContentPane(jsp);
                    pan.setPreferredSize(new Dimension((int) (ampArray.size() / SCALE + 100), MAX_H + 20));
                    frame1.setSize(new Dimension(1800, 700));
                    frame1.show();
                }
                ais.close();
                try {
                    Thread.sleep(9000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
//                f.dispose();
            }
        }
    }

    public static void analyze(AudioFormat targetFormat,
            AudioInputStream ais, File file)
            throws IOException, LineUnavailableException {
        System.out.printf("Init analyze...\n");
        float frequency = targetFormat.getFrameRate();
        final ArrayList<Double> dataList = new ArrayList<>();
        final ArrayList<Integer> ampArray = new ArrayList<>();
        final ArrayList<Integer> a0 = new ArrayList<>();
        final ArrayList<Integer> a1 = new ArrayList<>();
        final ArrayList<Integer> a2 = new ArrayList<>();
        //double[] alphas={0.0001,0.001,0.005,0.01};
        //for(double alpha:alphas)
        {
            {
                int nBytesRead = 0, nBytesWritten = 0;
                while (nBytesRead != -1) {
//                G.zzzsleep(1);
                    nBytesRead = ais.read(tmpBuffer, 0, tmpBuffer.length);

                    if (nBytesRead != -1) {
//                        int b = targetFormat.getSampleSizeInBits() / 2;
                        //assume that sample size is evenly divisable by 8
                        int val;
//                        double avg = 0;

//                        if (nBytesRead % 32 != 0) {
//                            System.out.println("Warning: nBytesRead=" + nBytesRead);
//                        }
                        for (int i = 0; i < nBytesRead - 2; i += 2) {
                            double zeroes = 0;
                            //skip ahead
                            while (i < nBytesRead && (tmpBuffer[i + 1] << 8) + tmpBuffer[i] == 0) {
                                i += 2;
                                zeroes++;
//                                ampArray.add(0);
                            }
                            if (zeroes > 10) {
                                System.out.printf("0 for %s ms (%s frames)\n",
                                        1000.0 * zeroes / targetFormat.getSampleRate(),
                                        (int) zeroes);
                            }
                            if (i >= nBytesRead) {
                                break;
                            }
                            //little endian
                            val = ((tmpBuffer[i + 1] << 8) | (tmpBuffer[i] & 0xFF));
                            dataList.add((double) val);

//                            val |= (tmpBuffer[i + 1]) << 8;
//                            val |= tmpBuffer[i];
                            //val = (tmpBuffer[i + 1] << 8) + tmpBuffer[i];
                            val = (int) hpFilter.next(val, ((tmpBuffer[i + 3] << 8) | (tmpBuffer[i + 2] & 0xFF)));
                            double val0, val1, val2;
                            val2 = lpFilter2.next(lpFilter2.next(val));//lowest freq
                            val = (int) (val - val2);//remove lowest freq
                            val1 = lpFilter1.next(lpFilter1.next(val));//med freq
                            val = (int) (val - val1);//remove med freq
                            val0 = (int) lpFilter0.next(lpFilter0.next(val));//high freq
                            val = (int) (val - val0);//highest freqs
//                             val0=val1-val0;
//                             val1=val2-val1;
//                            val2=val-val2;
//                            val=(int) (val0+val1+val2);

                            final int filteredValue = (int) val;
                            //if (i % 8 == 0)
                            {
                                ampArray.add(filteredValue);
                                a0.add((int) val0);
                                a1.add((int) val1);
                                a2.add((int) val2);
                            }
//                        System.out.printf("%s (%s, %s), ",
//                                filteredValue, val, (int) lpFilter.dbAmplitude(val));
                            if (i % 32 == 0) {//every 32 bytes = 16 values = 1 ms
                                //avg of 16 values
                                if (i > 0) {//skip first case
//                                System.out.printf("\n\tAVG = %s \n", avg / 16);
                                }
//                                avg = 0;
                            }
//                            avg += val;
                        }
//                    System.out.printf("\n\tAVG = %s \n", avg / 16);

//                    for(int i=0;i<nBytesRead;i+=20)
//                        line.write(tmpBuffer, i, 20);
                    }//END if (nBytesRead != -1)
                }//END while (nBytesRead != -1)

                //rgb2hsi
                final double[] mag = FFT.getFrequency(ampArray, targetFormat.getSampleRate());
                JFrame frame1 = new JFrame(file.getAbsolutePath());
                final int MAX_H = 800;
                String title = file.getAbsolutePath();

                final Complex[] fourier = FFT.get(ampArray, targetFormat.getSampleRate());
                JFrame frame2 = plotFFT(title, fourier, MAX_H);

                frame2.dispose();
                //======================================

//                double range = max - min;
//                g.setColor(Color.black);
//                g.fillRect(0, 0, (int) (ampArray.size() / SCALE), MAX_H);
//
//                g.setColor(Color.blue);
//                final int barx = MAX_H - (int) (MAX_H * (min) / range) + 10;
//                g.drawLine(0, barx, (int) (ampArray.size() / SCALE), barx);
//
//                g.setColor(Color.DARK_GRAY);
                //=============================
                int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
                for (int i : ampArray) {
                    if (i > max) {
                        max = i;
                    }
                    if (i < min) {
                        min = i;
                    }
                }
                double range = max - min;

                final double FREQ = frequency / 512;
                final int len = 2000;
                final int delta = 1;

                final int numElements = dataList.size();
                int pos = 0;
                int[] components = {0, 0, 0};
                double[] outputFFT = new double[len];
                int isReal = 0;

                final double[] data = new double[dataList.size() + len];
                for (int i = 0; i < numElements; i++) {
                    data[i] = dataList.get(i);
                }

                BufferedImage outImage = new BufferedImage(
                        (int) ((data.length + len) / SCALE),
                        len * 3 + OFFSET + 2000, BufferedImage.TYPE_INT_RGB);
                OFFSET += len * 3;
                Graphics2D g2d = outImage.createGraphics();

                HashMap<Integer, Color> colorCache = new HashMap<>();
                String desc = String.format("len=%s, xres=%s, yres=%s, isReal=%s, scale=%s",
                        numElements, numElements / delta, len, isReal, SCALE);
                System.out.println(desc);
                System.out.printf("2^%s\n", (log(((((double) len) / delta) * (double) len) * data.length)) / log(2));
                /*
                 Calculate FFT(len) overlapping
                 */
                System.out.printf("First FFTs...\n");
                FileOutputStream os=new FileOutputStream(    new File("C:\\Users\\Larry\\Documents\\NetBeansProjects\\JGoogleTTS\\" + desc + ".txt"));
                os.write(String.format("%s,%s,%s\n\n", data.length, len, delta).getBytes());
                for (int x = 0; pos < data.length - len; pos += delta, x += delta) {//
                    FFT.fft(data, outputFFT, pos, len);//get fft for window
//                                System.out.println(Arrays.toString(outputFFT));
//                                for (int i = 0; i < len; i++) {
//                                     int c = (int) ((log(outputFFT[i * 2]+3000)/log(1.01))*10   );
////                                    c=255<c?255:(0>c?c:c);//bound  0 <= c <= 255
//                                    System.out.printf("%3s|", (int) c);
//                                }
//                                System.out.println();
                        os.write(String.format("'%s,%s\n",x,Arrays.toString(outputFFT)).getBytes());
                        
                    FFT.fft_flatTopWindow(data, outputFFT, pos, len);
                        os.write(String.format("_%s,%s\n",x,Arrays.toString(outputFFT)).getBytes());
                        
                    for (int y = 0; y < len; y++) {//plot fft
                        int c = (int) (((outputFFT[y ] * 1.5)) + 1003200);
                        Color col = getColor(colorCache, c, g2d, components);
                        g2d.setColor(col);
                        g2d.fillRect((int) (x / SCALE), y * 3 + 50, (int) (delta / SCALE), 3);
//                                    g.drawString(""+c, x, i*15);
                    }
                }
os.close();
                ImageIO.write(outImage, "png",
                        new File("C:\\Users\\Larry\\Documents\\NetBeansProjects\\JGoogleTTS\\" + desc + ".png"));
//                if (true) {
//                    return;
//                }
                //==========================
                {
//                    outImage = new BufferedImage(
//                            (int) (data.length + 1000),
//                            3000, BufferedImage.TYPE_INT_RGB);
//                    g2d = outImage.createGraphics();
                    Graphics g = g2d;

//                    JPanel pan = new JPanel() {
//
//                        @Override
//                        public void paint(Graphics g) {
//
//                            super.paint(g);
                    //======================
                    //=============================
//                            int len = 300;
//                            int pos = 0;
//                            Color color = new Color(0) {
//
//                                @Override
//                                public int getRed() {
//                                    return super.getRed();
//                                }
//
//                                @Override
//                                public int getBlue() {
//                                    return super.getBlue();
//                                }
//
//                                @Override
//                                public int getGreen() {
//                                    return super.getGreen();
//                                }
//                            };
//                            int[] components = {0, 0, 0};
//                            int delta = 20;
//                            double[] outputFFT = new double[len * 2];
//                            for (int x = 0; pos < data.length - len; pos += delta, x += delta) {//
//                                FFT.fft(data, outputFFT, pos, len);//get fft for window
////                                System.out.println(Arrays.toString(outputFFT));
////                                for (int i = 0; i < len; i++) {
////                                     int c = (int) ((log(outputFFT[i * 2]+3000)/log(1.01))*10   );
//////                                    c=255<c?255:(0>c?c:c);//bound  0 <= c <= 255
////                                    System.out.printf("%3s|", (int) c);
////                                }
////                                System.out.println();
//                                for (int i = 0; i < len; i++) {//plot fft
//                                    int c = (int) (((outputFFT[i * 2] / 2)) + 3400);
////                                    c=255<c?255:(0>c?c:c);//bound  0 <= c <= 255
////                                    System.out.printf("%3s|", (int)outputFFT[i]);
//                                    hsi2rgb(c, 1, 1, components);
//                                    try {
//                                        g.setColor(new Color(
//                                                components[0],
//                                                components[1],
//                                                components[2]
//                                        ));
//                                    } catch (Exception e) {
//                                        System.err.printf("new Color(%s)\n", Arrays.toString(components));
//                                        e.printStackTrace();
//                                    }
//                                    g.fillRect((int) (pos / SCALE), i * 2 + 50, (int) (delta / SCALE), 2);
////                                    g.drawString(""+c, x, i*15);
//                                }
//                            }
//                            if (true) {
//                                return;
//                            }
                    //========================
//                            double x = 0;
                    double y0, y1, y2;
                    int h = 1;

                    g.setColor(Color.black);
                    g.fillRect(0, data.length * 3, (int) (ampArray.size() / SCALE), MAX_H);
//
//                    ImageIO.write(outImage, "png",
//                            new File("C:\\Users\\Larry\\Documents\\NetBeansProjects\\JGoogleTTS\\"
//                                    + "7_" + desc + ".png"));

                    final int TXT_SPACING = 8;
//================== MARK FREQUENIES
                    System.out.printf("MARK FREQUENIES...\n");
                    for (float x = 0; x < data.length; x += frequency / 64.0) {

                        for (int y = 1; y <= len / 2; y *= 2) {//plot fft
//                            y2=y<=len/2?y:y-len/2;
                            y2 = y;
                            boolean isEvenPower = (int) (log(y2) / log(2)) % 2 == 0;
//                            g2d.fillRect((int) (x / SCALE), y * 3 + 50, (int) (delta / SCALE), 3);
                            if (isPowerOf(y2, 2)) {
                                g.drawString(String.format("%s hz, %s", (int) (y2 * frequency / len), y),
                                        (int) ((x + 5 + TXT_SPACING + (isEvenPower ? -80 : 0)) / SCALE), y * 3 + 50);
                                g.fillRect((int) (x / SCALE), y * 3 + 50, 10, 1);
                            }

                        }
                        for (int y = len / 2; y > 1; y--) {//plot fft
//                            y2=y<=len/2?y:y-len/2;
                            y2 = len - y;
                            boolean isEvenPower = (int) (log(y) / log(2)) % 2 != 0;
//                            g2d.fillRect((int) (x / SCALE), y * 3 + 50, (int) (delta / SCALE), 3);
                            if (isPowerOf(y, 2)) {
                                g.drawString(String.format("%s hz, %s", (int) (y * frequency / len), y),
                                        (int) ((x + 5 + TXT_SPACING + (isEvenPower ? -80 : 0)) / SCALE), (int) (y2 * 3 + 50));
                                g.fillRect((int) (x / SCALE), (int) (y2 * 3 + 50), 10, 2);
                            }

                        }
//                        if (x % (frequency / 16) != 0) {
//                            g.drawString(String.format("%s s", x / frequency), (int) ((x + TXT_SPACING) / SCALE), y2 + TXT_SPACING);
//                        }
                    }

                    g.setColor(Color.blue);
                    //====================== BLUE MEASURE LINES
                    System.out.printf("BLUE MEASURE LINES...\n");
                    y2 = 3 * len + 60;
                    for (float x = 0; x < data.length; x += frequency / 16.0) {
                        g.fillRect((int) ((x - 1) / SCALE), (int) y2, 3, 1000);
                        g.drawString(String.format("%s sec", x / frequency), (int) ((x + TXT_SPACING) / SCALE), (int) (y2 + TXT_SPACING));
                    }
                    y2 = y2 + 4 * TXT_SPACING;
                    for (float x = 0; x < data.length; x += frequency / 64.0) {
                        g.fillRect((int) ((x) / SCALE), (int) y2, 1, 1000);
                        if (x % (frequency / 16) != 0) {
                            g.drawString(String.format("%s s", x / frequency), (int) ((x + TXT_SPACING) / SCALE), (int) (y2 + TXT_SPACING));
                        }
                    }
                    y2 = y2 + 4 * TXT_SPACING;
                    for (float x = 0; x < data.length; x += frequency / 256.0) {
                        g.fillRect((int) ((x) / SCALE), (int) y2, 1, 1000);
//                        if(x%(frequency/16)!=0)
//                        if(x%(frequency/64)!=0)
                        g.drawString(String.format("%s ms", (int) (10000 * x / frequency) / 10.0), (int) ((x + TXT_SPACING) / SCALE), (int) (y2 + TXT_SPACING));
                    }
                    //======================== 3D graph
                    {
                        System.out.printf("3D graph...\n");
                        double x1, x2, x3, y3, r1, r2, r3, dy;
                        double alpha, beta, gamma, m;//y1,y2
                        int[] xPoints = {0, 0, 0, 0};
                        int[] yPoints = {0, 0, 0, 0};
                        int nPoints = 4;
                        r1 = 3 * len;
                        r2 = 5;
                        r3 = r2 * 1.2;
                        alpha = PI * 15 / 32;
                        beta = -PI / 4;
                        gamma = beta + PI / 9;
                        final double X_STEP_RATE = 1;
                        y1 = 60;
                        //(x1,y1)-->(x2, y2)
                        for (float x = data.length - len; x >= 0; x -= FREQ) {
                            x1 = ((x) / SCALE);
                            x2 = (r1 * cos(alpha) + x1);
                            y2 = (r1 * sin(alpha) + y1);
                            //draw main line
                            g.setColor(Color.yellow);
                            g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
//                            g.setColor(Color.red);
                            FFT.fft(data, outputFFT, (int) x1, len);//get fft for window
                            m = (y2 - y1) / (x2 - x1);
                            for (float dx = 0; dx < len; dx += X_STEP_RATE) {
//                                dy = (3 * (y2 - y1) * (dx / (len)));
//                                x2 = x1 + dx/ SCALE;
//                                y2 = (m * (x1 + dx / SCALE) - m * x1 + y1);
                                x2 = x1 + dx / SCALE / 3.0;
                                y2 = y1 + dx / SCALE * m / 3.0;
//                                for (int y = 0; y < len; y++) {//plot fft
                                double c = (((outputFFT[(int) (dx )])));
                                c = log(c + 1);
//                                c = bound(c, 1, 256);
//                                System.out.println("---\t"+ c);
//cast shadows
                                g.setColor(Color.black);
                                x3 = (x2 + c * r3 * cos(gamma));
                                y3 = (y2 + c * r3 * sin(gamma));
                                xPoints[0] = (int) (x2 + 2 * sin(gamma + PI / 2.2));
                                xPoints[1] = (int) (x3 + 2 * sin(gamma + PI / 2.2));
                                xPoints[2] = (int) (x3 - 2 * sin(gamma + PI / 2.2));
                                xPoints[3] = (int) (x2 - 2 * sin(gamma + PI / 2.2));

                                yPoints[0] = (int) (y2 + 2 * cos(gamma + PI / 2.2));
                                yPoints[1] = (int) (y3 + 2 * cos(gamma + PI / 2.2));
                                yPoints[2] = (int) (y3 - 2 * cos(gamma + PI / 2.2));
                                yPoints[3] = (int) (y2 - 2 * cos(gamma + PI / 2.2));
                                g.fillPolygon(xPoints, yPoints, nPoints);
                                //glow shadow
                                g.setColor(Color.black);
                                x3 = (x2 + c * r3 * cos(beta));
                                y3 = (y2 + c * r3 * sin(beta));
                                xPoints[0] = (int) (x2 - 2 * sin(beta + PI / 2.2));
                                xPoints[1] = (int) (x3 - 2 * sin(beta + PI / 2.2));
                                xPoints[2] = (int) (x3 - 1 * sin(beta + PI / 2.2));
                                xPoints[3] = (int) (x2 - 1 * sin(beta + PI / 2.2));

                                yPoints[0] = (int) (y2 + 2 * cos(beta + PI / 2.2));
                                yPoints[1] = (int) (y3 + 2 * cos(beta + PI / 2.2));
                                yPoints[2] = (int) (y3 - 1 * cos(beta + PI / 2.2));
                                yPoints[3] = (int) (y2 - 1 * cos(beta + PI / 2.2));
                                g.fillPolygon(xPoints, yPoints, nPoints);
                            }
//                            g.fillRect(x1, y1, 3, 1000);
//                            g.drawString(String.format("%s sec", x / frequency), (int) ((x + TXT_SPACING) / SCALE), y2 + TXT_SPACING);
                        }
                        for (float x = data.length - len; x >= 0; x -= FREQ) {
                            x1 = (x / SCALE);
                            x2 = (r1 * cos(alpha) + x1);
                            y2 = (r1 * sin(alpha) + y1);
                            //draw main line
                            g.setColor(Color.yellow);
                            g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
//                            g.setColor(Color.red);
                            FFT.fft(data, outputFFT, (int) x1, len);//get fft for window
                            m = (y2 - y1) / (x2 - x1);
                            for (float dx = 0; dx < len; dx += X_STEP_RATE) {
//                                dy = (3 * (y2 - y1) * (dx / (len)));
//                                x2 = x1 + dx;
//                                y2 = (m * (x1 + dx / SCALE) - m * x1 + y1);

                                x2 = x1 + dx / SCALE / 3.0;
                                y2 = y1 + dx / SCALE * m / 3.0;

//                                for (int y = 0; y < len; y++) {//plot fft
                                double c = (((outputFFT[(int) (dx )])));
                                c = log(c + 1);
//                                c=pow(c,1/3.0);
//                                System.out.println("---\t"+ c);
//                                c = bound(c, 0, 256);
                                Color col = getColor(colorCache, (int) c * 40 + 1003150, g2d, components);
                                //bars
                                g2d.setColor(col);
                                x3 = (x2 + c * r2 * cos(beta));
                                y3 = (y2 + c * r2 * sin(beta));
                                xPoints[0] = (int) (x2 + 1 * sin(beta + PI / 2.2));
                                xPoints[1] = (int) (x3 + 1 * sin(beta + PI / 2.2));
                                xPoints[2] = (int) (x3 - 1 * sin(beta + PI / 2.2));
                                xPoints[3] = (int) (x2 - 1 * sin(beta + PI / 2.2));

                                yPoints[0] = (int) (y2 + 1 * cos(beta + PI / 2.2));
                                yPoints[1] = (int) (y3 + 1 * cos(beta + PI / 2.2));
                                yPoints[2] = (int) (y3 - 1 * cos(beta + PI / 2.2));
                                yPoints[3] = (int) (y2 - 1 * cos(beta + PI / 2.2));
//                                System.out.printf("%s, %s\n", Arrays.toString(xPoints), Arrays.toString(yPoints));
                                g.fillPolygon(xPoints, yPoints, nPoints);
                            }
                        }

                    }
                    ImageIO.write(outImage, "png",
                            new File("C:\\Users\\Larry\\Documents\\NetBeansProjects\\JGoogleTTS\\"
                                    + "6_" + desc + ".png"));
                    //=======================
                    System.out.printf("6...\n");
                    g.setColor(Color.blue);
                    final int barx = MAX_H - (int) (MAX_H * (min) / range) + 10 + OFFSET;
                    g.drawLine(0, barx, (int) (ampArray.size() / SCALE), barx);

                    g.setColor(Color.DARK_GRAY);
//                    for (int x = 0; x < ampArray.size() - 1; x++) {
////                                y = ampArray.get(i) + 300;
////                                y1 = MAX_H - (int) (MAX_H * (ampArray.get(x) - min) / range) + 10;
//                        y2 = MAX_H - (int) (MAX_H * (ampArray.get(x + 1) - min) / range) + 10 + OFFSET;
////                                h = Math.abs(ampArray.get(x) - ampArray.get(x + 1)) + 1;
//                        g.drawLine((int) (x / SCALE), OFFSET, (int) ((x + 1) / SCALE), (int) y2);
//                    }

                    System.out.printf("5...\n");
//                    ImageIO.write(outImage, "png",
//                            new File("C:\\Users\\Larry\\Documents\\NetBeansProjects\\JGoogleTTS\\"
//                                    + "5_" + desc + ".png"));
                    g.setColor(Color.green);
                    for (int x = 0; x < mag.length - 1; x++) {
                        g.drawLine((int) (x / SCALE), 0, (int) ((x + 1) / SCALE), (int) (mag[x]));
                    }
                    System.out.printf("4...\n");
//                    ImageIO.write(outImage, "png",
//                            new File("C:\\Users\\Larry\\Documents\\NetBeansProjects\\JGoogleTTS\\"
//                                    + "4_" + desc + ".png"));
                    g.setColor(Color.red);
                    for (int x = 0; x < ampArray.size() - 1; x += 5) {
//                                y = ampArray.get(i) + 300;
                        y1 = MAX_H - (int) (MAX_H * (ampArray.get(x) - min) / range) + 10 + OFFSET;
                        y2 = MAX_H - (int) (MAX_H * (ampArray.get(x + 1) - min) / range) + 10 + OFFSET;
//                                h = Math.abs(ampArray.get(x) - ampArray.get(x + 1)) + 1;
                        g.drawLine((int) (x / SCALE), (int) y1, (int) ((x + 1) / SCALE), (int) y2);
                    }
                    System.out.printf("3...\n");
//                    ImageIO.write(outImage, "png",
//                            new File("C:\\Users\\Larry\\Documents\\NetBeansProjects\\JGoogleTTS\\"
//                                    + "3_" + desc + ".png"));
                    g.setColor(Color.CYAN);
                    for (int x = 1; x < a0.size() - 1; x += 5) {
//                                y = ampArray.get(i) + 300;
                        y1 = MAX_H - (int) (MAX_H * (a0.get(x) / SCALE2 - min) / range) + 10 + 200 + OFFSET;
                        y2 = MAX_H - (int) (MAX_H * (a0.get(x + 1) / SCALE2 - min) / range) + 10 + 200 + OFFSET;
//                                h = Math.abs(ampArray.get(x) - ampArray.get(x + 1)) + 1;
                        g.drawLine((int) (x / SCALE), (int) y1, (int) ((x + 1) / SCALE), (int) y2);
                        if (a0.get(x) - a0.get(x - 1) < 0 && a0.get(x) - a0.get(x + 1) < 0) {

                            g.setColor(Color.blue);
                            g.drawLine((int) (x / SCALE), (int) y1, (int) ((x) / SCALE), (int) (y1 + 1));
                            g.setColor(Color.CYAN);
                        }
                        if (a0.get(x) - a0.get(x - 1) > 0 && a0.get(x) - a0.get(x + 1) > 0) {

                            g.setColor(Color.red);
                            g.drawLine((int) (x / SCALE), (int) (y1 - 1), (int) ((x) / SCALE), (int) y1);
                            g.setColor(Color.CYAN);
                        }
                    }
                    System.out.printf("2...\n");
//                    ImageIO.write(outImage, "png",
//                            new File("C:\\Users\\Larry\\Documents\\NetBeansProjects\\JGoogleTTS\\"
//                                    + "2_" + desc + ".png"));

                    g.setColor(Color.ORANGE);
                    for (int x = 1; x < a1.size() - 1; x += 5) {
//                                y = ampArray.get(i) + 300;
                        y1 = MAX_H - (int) (MAX_H * (a1.get(x) / SCALE2 - min) / range) + 10 + 350 + OFFSET;
                        y2 = MAX_H - (int) (MAX_H * (a1.get(x + 1) / SCALE2 - min) / range) + 10 + 350 + OFFSET;
//                                h = Math.abs(ampArray.get(x) - ampArray.get(x + 1)) + 1;
                        g.drawLine((int) (x / SCALE), (int) y1, (int) ((x + 1) / SCALE), (int) y2);
                        if (a1.get(x) - a1.get(x - 1) < 0 && a1.get(x) - a1.get(x + 1) < 0) {

                            g.setColor(Color.blue);
                            g.drawLine((int) (x / SCALE), (int) y1, (int) ((x + 1) / SCALE), (int) y2);
                            g.drawLine((int) (x / SCALE), (int) y1, (int) ((x) / SCALE), (int) (y1 + 1));
                            g.setColor(Color.ORANGE);
                        }
                        if (a1.get(x) - a1.get(x - 1) > 0 && a1.get(x) - a1.get(x + 1) > 0) {

                            g.setColor(Color.red);
                            g.drawLine((int) (x / SCALE), (int) y1, (int) ((x + 1) / SCALE), (int) y2);
                            g.drawLine((int) (x / SCALE), (int) (y1 - 1), (int) ((x) / SCALE), (int) y1);
                            g.setColor(Color.ORANGE);
                        }
                    }

                    System.out.printf("1...\n");
//                    ImageIO.write(outImage, "png",
//                            new File("C:\\Users\\Larry\\Documents\\NetBeansProjects\\JGoogleTTS\\"
//                                    + "1_" + desc + ".png"));
                    g.setColor(Color.lightGray);
                    for (int x = 1; x < a2.size() - 1; x += 5) {
//                                y = ampArray.get(i) + 300;
                        y0 = MAX_H - (int) (MAX_H * (a2.get(x - 1) / SCALE2 - min) / range) + 10 + 500 + OFFSET;
                        y1 = MAX_H - (int) (MAX_H * (a2.get(x) / SCALE2 - min) / range) + 10 + 500 + OFFSET;
                        y2 = MAX_H - (int) (MAX_H * (a2.get(x + 1) / SCALE2 - min) / range) + 10 + 500 + OFFSET;
//                                h = Math.abs(ampArray.get(x) - ampArray.get(x + 1)) + 1;
                        if (a2.get(x) - a2.get(x - 1) <= 0 && a2.get(x) - a2.get(x + 1) <= 0) {

                            g.setColor(Color.blue);
//                                    g.drawLine((int) (x / SCALE), y1, (int) ((x ) / SCALE), y1 + 1);
                            g.drawLine((int) (x / SCALE), (int) y1, (int) ((x + 1) / SCALE), (int) y2);
                            g.setColor(Color.lightGray);
                        } else if (a2.get(x) - a2.get(x - 1) >= 0 && a2.get(x) - a2.get(x + 1) >= 0) {

                            g.setColor(Color.red);
//                                    g.drawLine((int) (x / SCALE), y1 - 1, (int) ((x ) / SCALE), y1);
                            g.drawLine((int) (x / SCALE), (int) y1, (int) ((x + 1) / SCALE), (int) y2);
                            g.setColor(Color.lightGray);
                        } else {
                            g.drawLine((int) (x / SCALE), (int) y1, (int) ((x + 1) / SCALE), (int) y2);

                        }
                    }

                    System.out.printf("_...\n");
                    ImageIO.write(outImage, "png",
                            new File("C:\\Users\\Larry\\Documents\\NetBeansProjects\\JGoogleTTS\\"
                                    + "_" + desc + ".png"));
//                        }
//
//                    };
//                    JScrollPane jsp = new JScrollPane();
//                    jsp.getViewport().add(pan);
//                    frame1.setContentPane(jsp);
//                    pan.setPreferredSize(new Dimension((int) (ampArray.size() / SCALE + 100), MAX_H + 20));
//                    frame1.setSize(new Dimension(1800, 700));
//                    frame1.show();
                }
                ais.close();
                try {
                    Thread.sleep(9000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
//                f.dispose();
            }
        }
    }

    public static Color getColor(HashMap<Integer, Color> colorCache, int c, Graphics2D g, int[] components) {

//                                    c=255<c?255:(0>c?c:c);//bound  0 <= c <= 255
//                                    System.out.printf("%3s|", (int)outputFFT[i]);
//                                System.out.println("+++\t"+ ((outputFFT[y * 2 + isReal] * 1.5)));
        if (colorCache.containsKey(c)) {
            return colorCache.get(c);
        } else {
            ;
            try {
                hsi2rgb((c % 360 + 360) % 360, ((c % 360 + 360) % 360) / 360.0 * 1.8, 1, components);
                Color color = new Color(
                        components[0],
                        components[1],
                        components[2]
                );
                colorCache.put(c, color);
                return color;
            } catch (Exception e) {
                System.err.printf("new Color(%s)\n", Arrays.toString(components));
                e.printStackTrace();
            }
        }
        return Color.yellow;
    }

    public static JFrame plotFFT(String title, final Complex[] fourier, final int MAX_H) throws HeadlessException {
        JFrame frame2 = new JFrame(title);
        //Real{min,max}, Imaginary{min,max}
        long[] fftBounds//={Double.MAX_VALUE,Double.MIN_VALUE,
                //  Double.MAX_VALUE,Double.MIN_VALUE};
                = {0, 0, 0, 0};
        for (Complex c : fourier) {
            if (fftBounds[0] < c.re()) {
                fftBounds[0] = (long) c.re();
            }
            if (fftBounds[1] > c.re()) {
                fftBounds[1] = (long) c.re();
            }
            if (fftBounds[2] < c.im()) {
                fftBounds[2] = (long) c.im();
            }
            if (fftBounds[3] > c.im()) {
                fftBounds[3] = (long) c.im();
            }
        }
        System.out.printf("FFT bounds = %s\n", Arrays.toString(fftBounds));
        JPanel fftPan = new JPanel() {
            int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;

            @Override
            public void paint(Graphics g) {

                super.paint(g);

//                        for (int i : ampArray) {
//                            if (i > max) {
//                                max = i;
//                            }
//                            if (i < min) {
//                                min = i;
//                            }
//                        }
//                        double range = max - min;
//
                g.setColor(Color.DARK_GRAY);
                g.fillRect(0, 0, MAX_H * 2, 2 * MAX_H);
                g.setColor(Color.black);
                g.fillRect(MAX_H, MAX_H, MAX_H * 2, 2 * MAX_H);
//
//
//                        g.setColor(Color.blue);
//                        final int barx = MAX_H - (int) (MAX_H * (min) / range) + 10;
//                        g.drawLine(0, barx, (int) (ampArray.size() / SCALE), barx);

                g.setColor(Color.DARK_GRAY);
//                            double x = 0;
                int y1, y2;
                int h = 1;
//                        for (int x = 0; x < ampArray.size() - 1; x++) {
////                                y = ampArray.get(i) + 300;
////                                y1 = MAX_H - (int) (MAX_H * (ampArray.get(x) - min) / range) + 10;
//                            y2 = MAX_H - (int) (MAX_H * (ampArray.get(x + 1) - min) / range) + 10;
////                                h = Math.abs(ampArray.get(x) - ampArray.get(x + 1)) + 1;
//                            g.drawLine((int) (x / SCALE), 0, (int) ((x + 1) / SCALE), y2);
//                        }

//                            g.setColor(Color.green);
//                            for (int x = 0; x < mag.length - 1; x++) {
//                                g.drawLine((int) (x / SCALE), 0, (int) ((x + 1) / SCALE), (int) (mag[x]));
//                            }
                g.setColor(Color.red);
                int x, y;
                for (Complex c : fourier) {
//                                y = ampArray.get(i) + 300;
                    x = (int) (Math.log(c.re()) / 1000) + MAX_H;
                    y = (int) (Math.log(c.im()) / 1000) + MAX_H;
//                            y1 = MAX_H - (int) (MAX_H * (ampArray.get(x) - min) / range) + 10;
//                            y2 = MAX_H - (int) (MAX_H * (ampArray.get(x + 1) - min) / range) + 10;
//                                h = Math.abs(ampArray.get(x) - ampArray.get(x + 1)) + 1;
                    g.drawLine(x, y, x, y + 1);
                }

            }

        };
        JScrollPane jsp1 = new JScrollPane();
        jsp1.getViewport().add(fftPan);
        frame2.setContentPane(jsp1);
        fftPan.setPreferredSize(new Dimension(MAX_H * 2, MAX_H * 2));
        frame2.pack();
        frame2.show();
        frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        return frame2;
    }

    private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
        SourceDataLine res;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

        res = (SourceDataLine) AudioSystem.getLine(info);
        res.open(audioFormat);

        return res;
    }

//        #region Private Static Memebers
    private static int MAX_FRAME_LENGTH = 16000;
    private static double[] gInFIFO = new double[MAX_FRAME_LENGTH];
    private static double[] gOutFIFO = new double[MAX_FRAME_LENGTH];
    private static double[] gFFTworksp = new double[2 * MAX_FRAME_LENGTH];
    private static double[] gLastPhase = new double[MAX_FRAME_LENGTH / 2 + 1];
    private static double[] gSumPhase = new double[MAX_FRAME_LENGTH / 2 + 1];
    private static double[] gOutputAccum = new double[2 * MAX_FRAME_LENGTH];
    private static double[] gAnaFreq = new double[MAX_FRAME_LENGTH];
    private static double[] gAnaMagn = new double[MAX_FRAME_LENGTH];
    private static double[] gSynFreq = new double[MAX_FRAME_LENGTH];
    private static double[] gSynMagn = new double[MAX_FRAME_LENGTH];
    private static int gRover, gInit;
//        #endregion

//        #region Public Static  Methods
    public static void PitchShift(double pitchShift, int numSampsToProcess,
            double sampleRate, double[] indata) {
        PitchShift(pitchShift, numSampsToProcess, (int) 2048, (int) 10, sampleRate, indata);
    }

    public static void PitchShift(double pitchShift, int numSampsToProcess, int fftFrameSize,
            int osamp, double sampleRate, double[] indata) {
        double magn, phase, tmp, window, real, imag;
        double freqPerBin, expct;
        int k, qpd, index, inFifoLatency, stepSize, fftFrameSize2;
        int i;

        double[] outdata = indata;
        /* set up some handy variables */
        fftFrameSize2 = fftFrameSize / 2;
        stepSize = fftFrameSize / osamp;
        freqPerBin = sampleRate / (double) fftFrameSize;
        expct = 2.0 * Math.PI * (double) stepSize / (double) fftFrameSize;
        inFifoLatency = fftFrameSize - stepSize;
        if (gRover == 0) {
            gRover = inFifoLatency;
        }

        /* main processing loop */
        for (i = 0; i < numSampsToProcess; i++) {

            /* As int as we have not yet collected enough data just read in */
            gInFIFO[gRover] = indata[i];
            outdata[i] = gOutFIFO[gRover - inFifoLatency];
            gRover++;

            /* now we have enough data for processing */
            if (gRover >= fftFrameSize) {
                gRover = inFifoLatency;

                /* do windowing and re,im interleave */
                for (k = 0; k < fftFrameSize; k++) {
                    window = -.5 * Math.cos(2.0 * Math.PI * (double) k / (double) fftFrameSize) + .5;
                    gFFTworksp[2 * k] = (double) (gInFIFO[k] * window);
                    gFFTworksp[2 * k + 1] = 0.0F;
                }

                /* ***************** ANALYSIS ******************* */
                /* do transform */
                ShortTimeFourierTransform(gFFTworksp, fftFrameSize, -1);

                /* this is the analysis step */
                for (k = 0; k <= fftFrameSize2; k++) {

                    /* de-interlace FFT buffer */
                    real = gFFTworksp[2 * k];
                    imag = gFFTworksp[2 * k + 1];

                    /* compute magnitude and phase */
                    magn = 2.0 * Math.sqrt(real * real + imag * imag);
                    phase = Math.atan2(imag, real);

                    /* compute phase difference */
                    tmp = phase - gLastPhase[k];
                    gLastPhase[k] = (double) phase;

                    /* subtract expected phase difference */
                    tmp -= (double) k * expct;

                    /* map delta phase into +/- Pi interval */
                    qpd = (int) (tmp / Math.PI);
                    if (qpd >= 0) {
                        qpd += qpd & 1;
                    } else {
                        qpd -= qpd & 1;
                    }
                    tmp -= Math.PI * (double) qpd;

                    /* get deviation from bin frequency from the +/- Pi interval */
                    tmp = osamp * tmp / (2.0 * Math.PI);

                    /* compute the k-th partials' true frequency */
                    tmp = (double) k * freqPerBin + tmp * freqPerBin;

                    /* store magnitude and true frequency in analysis arrays */
                    gAnaMagn[k] = (double) magn;
                    gAnaFreq[k] = (double) tmp;

                }

                /* ***************** PROCESSING ******************* */
                /* this does the actual pitch shifting */
                for (int zero = 0; zero < fftFrameSize; zero++) {
                    gSynMagn[zero] = 0;
                    gSynFreq[zero] = 0;
                }

                for (k = 0; k <= fftFrameSize2; k++) {
                    index = (int) (k * pitchShift);
                    if (index <= fftFrameSize2) {
                        gSynMagn[index] += gAnaMagn[k];
                        gSynFreq[index] = gAnaFreq[k] * pitchShift;
                    }
                }

                /* ***************** SYNTHESIS ******************* */
                /* this is the synthesis step */
                for (k = 0; k <= fftFrameSize2; k++) {

                    /* get magnitude and true frequency from synthesis arrays */
                    magn = gSynMagn[k];
                    tmp = gSynFreq[k];

                    /* subtract bin mid frequency */
                    tmp -= (double) k * freqPerBin;

                    /* get bin deviation from freq deviation */
                    tmp /= freqPerBin;

                    /* take osamp into account */
                    tmp = 2.0 * Math.PI * tmp / osamp;

                    /* add the overlap phase advance back in */
                    tmp += (double) k * expct;

                    /* accumulate delta phase to get bin phase */
                    gSumPhase[k] += (double) tmp;
                    phase = gSumPhase[k];

                    /* get real and imag part and re-interleave */
                    gFFTworksp[2 * k] = (double) (magn * Math.cos(phase));
                    gFFTworksp[2 * k + 1] = (double) (magn * Math.sin(phase));
                }

                /* zero negative frequencies */
                for (k = fftFrameSize + 2; k < 2 * fftFrameSize; k++) {
                    gFFTworksp[k] = 0.0F;
                }

                /* do inverse transform */
                ShortTimeFourierTransform(gFFTworksp, fftFrameSize, 1);

                /* do windowing and add to output accumulator */
                for (k = 0; k < fftFrameSize; k++) {
                    window = -.5 * Math.cos(2.0 * Math.PI * (double) k / (double) fftFrameSize) + .5;
                    gOutputAccum[k] += (double) (2.0 * window * gFFTworksp[2 * k] / (fftFrameSize2 * osamp));
                }
                for (k = 0; k < stepSize; k++) {
                    gOutFIFO[k] = gOutputAccum[k];
                }

                /* shift accumulator */
                //memmove(gOutputAccum, gOutputAccum + stepSize, fftFrameSize * sizeof(double));
                for (k = 0; k < fftFrameSize; k++) {
                    gOutputAccum[k] = gOutputAccum[k + stepSize];
                }

                /* move input FIFO */
                for (k = 0; k < inFifoLatency; k++) {
                    gInFIFO[k] = gInFIFO[k + stepSize];
                }
            }
        }
    }
//        #endregion

//        #region Private Static Methods
    public static void ShortTimeFourierTransform(double[] fftBuffer, int fftFrameSize, int sign) {
        double wr, wi, arg, temp;
        double tr, ti, ur, ui;
        int i, bitm, j, le, le2, k;

        for (i = 2; i < 2 * fftFrameSize - 2; i += 2) {
            for (bitm = 2, j = 0; bitm < 2 * fftFrameSize; bitm <<= 1) {
                if ((i & bitm) != 0) {
                    j++;
                }
                j <<= 1;
            }
            if (i < j) {
                temp = fftBuffer[i];
                fftBuffer[i] = fftBuffer[j];
                fftBuffer[j] = temp;
                temp = fftBuffer[i + 1];
                fftBuffer[i + 1] = fftBuffer[j + 1];
                fftBuffer[j + 1] = temp;
            }
        }
        int max = (int) (Math.log(fftFrameSize) / Math.log(2.0) + .5);
        for (k = 0, le = 2; k < max; k++) {
            le <<= 1;
            le2 = le >> 1;
            ur = 1.0F;
            ui = 0.0F;
            arg = (double) Math.PI / (le2 >> 1);
            wr = (double) Math.cos(arg);
            wi = (double) (sign * Math.sin(arg));
            for (j = 0; j < le2; j += 2) {

                for (i = j; i < 2 * fftFrameSize; i += le) {
                    tr = fftBuffer[i + le2] * ur - fftBuffer[i + le2 + 1] * ui;
                    ti = fftBuffer[i + le2] * ui + fftBuffer[i + le2 + 1] * ur;
                    fftBuffer[i + le2] = fftBuffer[i] - tr;
                    fftBuffer[i + le2 + 1] = fftBuffer[i + 1] - ti;
                    fftBuffer[i] += tr;
                    fftBuffer[i + 1] += ti;

                }
                tr = ur * wr - ui * wi;
                ui = ur * wi + ui * wr;
                ur = tr;
            }
        }
    }
//        #endregion

    /**
     *
     * @param H [0,360]
     * @param S [0,1]
     * @param I 0,1]
     * @param rgb
     */
    final static void hsi2rgb(double H, double S, double I, int[] rgb) {
        int r, g, b;
        H = H % 360;//fmod(H,360); // cycle H around to 0-360 degrees
        H = PI * H / (double) 180; // Convert to radians.
        S = S > 0 ? (S < 1 ? S : 1) : 0; // clamp S and I to interval [0,1]
        I = I > 0 ? (I < 1 ? I : 1) : 0;

        // Math! Thanks in part to Kyle Miller.
        if (H < 2.09439) {
            r = (int) (255 * I / 3 * (1 + S * cos(H) / cos(1.047196667 - H)));
            g = (int) (255 * I / 3 * (1 + S * (1 - cos(H) / cos(1.047196667 - H))));
            b = (int) (255 * I / 3 * (1 - S));
        } else if (H < 4.188787) {
            H = H - 2.09439;
            g = (int) (255 * I / 3 * (1 + S * cos(H) / cos(1.047196667 - H)));
            b = (int) (255 * I / 3 * (1 + S * (1 - cos(H) / cos(1.047196667 - H))));
            r = (int) (255 * I / 3 * (1 - S));
        } else {
            H = H - 4.188787;
            b = (int) (255 * I / 3 * (1 + S * cos(H) / cos(1.047196667 - H)));
            r = (int) (255 * I / 3 * (1 + S * (1 - cos(H) / cos(1.047196667 - H))));
            g = (int) (255 * I / 3 * (1 - S));
        }
        rgb[0] = r;
        rgb[1] = g;
        rgb[2] = b;
    }

    final static void hsi2rgb(double h, double s, double i, double[] color) {
        final double HUE_UPPER_LIMIT = 255;

//    if(Hsi_IsValid(h,s,i)==true)
//    {
        color[0] = 0;//r
        color[1] = 0;//g
        color[2] = 0;//b
        if (h >= 0.0 && h <= (HUE_UPPER_LIMIT / 3.0)) {
            color[1] = (1.0 / 3.0) * (1.0 - s);
            color[0] = (1.0 / 3.0) * ((s * cos(h)) / cos(60.0 - h));
            color[1] = 1.0 - (color[2] + color[0]);
        } else if (h > (HUE_UPPER_LIMIT / 3.0) && h <= (2.0 * HUE_UPPER_LIMIT / 3.0)) {
            h -= (HUE_UPPER_LIMIT / 3.0);
            color[0] = (1.0 / 3.0) * (1.0 - s);
            color[1] = (1.0 / 3.0) * ((s * cos(h)) / cos(60.0 - h));
            color[2] = 1.0 - (color[1] + color[0]);

        } else /* h>240 h<360 */ {
            h -= (2.0 * HUE_UPPER_LIMIT / 3.0);
            color[1] = (1.0 / 3.0) * (1.0 - s);
            color[2] = (1.0 / 3.0) * ((s * cos(h)) / cos(60.0 - h));
            color[0] = 1.0 - (color[1] + color[2]);
        }
//    }
    }

    final static boolean isPowerOf(double val, double base) {
        return (log(val) / log(base) - (int) (log(val) / log(base))) == 0.0;
    }
}
