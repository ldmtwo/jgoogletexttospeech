/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ldtwo.flashcard;

import java.awt.TextField;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import org.ldtwo.GoTTS.G;
import static org.ldtwo.GoTTS.G.*;

class SocketClient implements java.lang.Runnable {

    byte[] buff = new byte[1024];
    public TextField address, path;
    Thread thread;
    Socket ourSocket;
    File tmpFile;

    public SocketClient(String fname) {
        new File(IMAGE_PATH).mkdirs();
        fname=G.makeValidFileName(fname);
        tmpFile = new File(IMAGE_PATH + fname + ".jpg");
        address = new TextField("", 20);
        path = new TextField("", 20);
        thread = new Thread(this);
    }

    public File getFile() {
        try {
            if (tmpFile.exists() && tmpFile.length() > 1600) {
                return tmpFile;
            }

            ourSocket = new Socket();            
            ourSocket.connect(new InetSocketAddress(address.getText(), 80),1000);
            InputStream inStream = new DataInputStream(ourSocket.getInputStream());
            DataOutputStream outStream = new DataOutputStream(ourSocket.getOutputStream());

            String requestString;
            requestString = "GET " + path.getText() + " HTTP/1.0\r\n"
                    + "User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1779.2 Safari/537.36\r\n"
                    + "Accept: image/*\r\n"
                    + "\r\n";
//            System.out.println(requestString);

            outStream.writeBytes(requestString);
            outStream.flush();

            try {
//                Thread.sleep(150);
                file = writeToFile(inStream, tmpFile.getName());
            } catch (Exception ex) {
//                Thread.sleep(150);
//                System.err.println(requestString);
//                System.err.println(tmpFile);
//                System.err.println(ex.getMessage());
//                ex.printStackTrace();
//                Thread.sleep(150);
            }
            if (file!=null&& file.length() < 2000) {
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
        } catch (UnknownHostException e) {
            System.err.println("Host unreachable. Check your internet connecton!");
        } catch (SocketTimeoutException e) {
            System.err.println("Connection timeout!");
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

    public File writeToFile(InputStream iss, String fileName) {
        FileOutputStream fos;
        File tmpFile = new File(IMAGE_PATH);
        try {
            
            if (!tmpFile.exists()) {
                tmpFile.mkdirs();
            }
            tmpFile = new File(IMAGE_PATH + fileName);
//            System.out.println(tmpFile.getAbsolutePath());
            BufferedReader br = new BufferedReader(new InputStreamReader(iss));
            DataInputStream dis = new DataInputStream(iss);
            boolean httpMeta = true;
            boolean EOF=false;
            try {
                while (httpMeta) {
                    while (dis.readChar() != '\r');
//                httpMeta = (dis.readChar() == '\n')&&(dis.readChar() == '\r')&&(dis.readChar() == '\n');
                    if (dis.readChar() == '\n') {
                        if (dis.readChar() == '\r') {
                            if (dis.readChar() == '\n') {
                                break;//httpMeta=false;
                            }
                        }
                    }
                }
            } catch (EOFException ex) {
                //System.err.println(ex);
            }
            fos = new FileOutputStream(tmpFile);
            int len;
            while ((len = dis.read(buff)) >= 0) {
                fos.write(buff, 0, len);
            }
            try {
                fos.close();
            } catch (IOException ex) {
            }
            return tmpFile;
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
        System.out.println("2getMP3==null");
        return null;
    }
}
