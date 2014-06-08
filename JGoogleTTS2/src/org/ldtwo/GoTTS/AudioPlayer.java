/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ldtwo.GoTTS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import static org.ldtwo.GoTTS.G.pause;
import static org.ldtwo.GoTTS.G.play;

/**
 *
 * @author ldtwo
 */
public class AudioPlayer {

    static byte[] tmpBuffer = new byte[32 * 100];
    
    
    public static void getMic() throws LineUnavailableException {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfos) {
            Mixer m = AudioSystem.getMixer(info);
            Line.Info[] lineInfos = m.getSourceLineInfo();
            for (Line.Info lineInfo : lineInfos) {
                System.out.println(info.getName() + "---" + lineInfo);
                Line line = m.getLine(lineInfo);
                System.out.println("\t-----" + line);
            }
            lineInfos = m.getTargetLineInfo();
            for (Line.Info lineInfo : lineInfos) {
                System.out.println(m + "---" + lineInfo);
                Line line = m.getLine(lineInfo);
                System.out.println("\t-----" + line);

            }

        }
    }

    public static void rawplay(AudioFormat targetFormat,
            AudioInputStream ais, File file)
            throws IOException, LineUnavailableException {
        SourceDataLine line = getLine(targetFormat);
        final ArrayList<Integer> ampArray = new ArrayList<>();
        //double[] alphas={0.0001,0.001,0.005,0.01};
        //for(double alpha:alphas)
        {
            //    LPalpha=alpha;
            if (line != null) {
                // Start
                line.start();
                int nBytesRead = 0, nBytesWritten = 0;
                while (nBytesRead != -1) {
                    while (pause) {
                        G.zzzsleep(300);
                    }
                    if (!play) {
                        break;
                    }
                    nBytesRead = ais.read(tmpBuffer, 0, tmpBuffer.length);
                    while (pause) {
                        G.zzzsleep(300);
                    }
                    if (nBytesRead != -1) {
                        nBytesWritten = line.write(tmpBuffer, 0, nBytesRead);  
                    }//END if (nBytesRead != -1)                    
                }//END while (nBytesRead != -1)              
            }
        }
    }

    private static SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
        SourceDataLine res;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

        res = (SourceDataLine) AudioSystem.getLine(info);
        res.open(audioFormat);

        return res;
    }

}
