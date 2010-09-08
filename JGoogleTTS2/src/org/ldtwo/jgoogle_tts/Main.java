                /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ldtwo.jgoogle_tts;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.io.*;
import java.net.*;
import javazoom.jl.player.Player;

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
            //        File f = null;
            SocketClient socket = new SocketClient();
            //        socket.run();
            socket.theFile.setText("translate_tts?tl="+language+"&q="+path);
            socket.theServer.setText(site);
            //        Thread thread   = new Thread(socket);
            //                thread.start();
            File file = socket.getFile();
            Runtime run = Runtime.getRuntime();
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//            }
            String fname = "";
            fname = file.getAbsolutePath();
//            String arg = "cmd /C start \"title\" \"" + fname + "\"";
//                        String []args={"cmd","/C start \"title\" \""+file.getAbsolutePath()+"\""};
//            System.out.println(arg);
//            Process proc = run.exec(args);

//                                    ProcessBuilder pb=new ProcessBuilder(args);
//                                    pb.start();
//                                    proc.waitFor();
//         String arg="start "+file.getAbsolutePath()+"";
            if (play) {
                try {

                    Player p = new Player(new FileInputStream(file));
                    p.play();
//                                    Process proc=run.exec(arg);
                    //run.addShutdownHook(thread);
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] a) {
        try {
            //        File f = null;
            SocketClient socket = new SocketClient();
            //        socket.run();
            socket.theFile.setText("translate_tts?tl=en&q=help+me+" + (System.currentTimeMillis() % 1000));
            socket.theServer.setText("translate.google.com");
            //        Thread thread   = new Thread(socket);
            //                thread.start();
            File file = socket.getFile();
            Runtime run = Runtime.getRuntime();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            String fname = "";
            fname = file.getAbsolutePath();
            String arg = "cmd /C start \"title\" \"" + fname + "\"";
            String[] args = {"cmd", "/C start \"title\" \"" + file.getAbsolutePath() + "\""};
            System.out.println(arg);
            Process proc = run.exec(args);

            ProcessBuilder pb = new ProcessBuilder(args);
            pb.start();
            proc.waitFor();
//         String arg="start "+file.getAbsolutePath()+"";
            try {

                Player p = new Player(new FileInputStream(file));
                p.play();
//                                    Process proc=run.exec(arg);
                //run.addShutdownHook(thread);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
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
//            String arg = "cmd /C start \"title\" \"" + tmpFile.getAbsolutePath() + "\"";
//            String[] args = {"cmd", "/C start \"title\" \"" + tmpFile.getAbsolutePath() + "\""};
//            System.out.println(arg);
//            Runtime run = Runtime.getRuntime();
            try {
//                    Player p = new Player(new FileInputStream(file));
//                    p.play();
//                    Process proc=run.exec(arg);
//                ProcessBuilder pb = new ProcessBuilder(args);
//                pb.start();
//run.addShutdownHook(thread);
            } catch (Exception ex) {
                ex.printStackTrace();
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

    static void get2() {
        // Listing 1: Create a Socket Object
        Socket ourSocket = null;
        String serverName = "translate.google.com";
        InputStream ourInputStream;
        OutputStream ourOutputStream;
        try {

            ourSocket = new Socket(serverName, 80);
        } catch (UnknownHostException e) {
            System.out.println(" DOES NOT EXIST " + serverName);
        } catch (IOException e) {
            System.out.println(" Unable to open the Socket to " + serverName);
        }

        //Listing 2: Establish input and output streams.

        try {
            ourInputStream = ourSocket.getInputStream();
            ourOutputStream = ourSocket.getOutputStream();
        } catch (IOException e) {
            System.out.println("unable to open streams");
        }

        //Listing 3: Closing the socket.

        try {
            ourSocket.close();
        } catch (IOException e) {
            System.out.println("Exception thrown closing the Socket");
        }
    }
    //Listing 4

    static private class SocketClient implements Runnable {
        /* *******   Data Section    ******** */

        public TextField theServer, theFile;
        TextArea theHTMLCode;
        Thread thread;
        Button search, stopSearch;
        Socket ourSocket;

        /**
         * This is the default constructor for this class. Its job is to initialize
         * the SocketClient object. We construct a Frame Window here. A data entry
         * panel is created and added "North". Here a user will be able to enter a server
         * name and a file to retrieve. If no file name is specified, index.html will be the
         * default file to search for.*/
        public SocketClient() {

            //setTitle("Socket Client Example");
            //Set the frame window title.
            //setLayout(new BorderLayout());
            //Set the layout for the window.
            theServer = new TextField("www.", 20);
            //Create the data entry fields.
            theFile = new TextField("", 20);
            theHTMLCode = new TextArea("", 25, 100);
            theHTMLCode.setEditable(false);
            //Make the TextArea not editable.

            Panel pan = new Panel();
            //Make a panel to contain the data
            pan.setLayout(new FlowLayout());
            //  entry fields and set the layout.
            pan.add(new Label("http://"));
            //Add text labels to the panel as
            pan.add(theServer);
            //  well as the Data Entry Objects.
            pan.add(new Label("/"));
            pan.add(theFile);
            search = new Button("Get the file");
            stopSearch = new Button("Stop");

            pan.add(search);
            pan.add(stopSearch);


//            add("North", pan);
//            //Add the panel North.
//            add("Center", theHTMLCode);
            //Add the TextArea Center.

            thread = new Thread(this);
            //Create a new thread.
//            resize(640, 300);
//            //Resize the Window to an appropriate size.
//            show();
            //Display the Window.
        }

        //Listing 5: HandleEvent Method.
        /**
         * This is the handleEvent method. We provide code for the events triggered
         * from a user pressing a button or destroying the window (Exiting the program).
         * @param Event e
         * @see event*/
//        public boolean handleEvent(Event e) {
//            if (e.target == search) {
//                //If the Search Button was pressed, do this.
//                thread.stop();
//                if (theFile.getText().equals("")) {
//                    //If no file was specified
//                    theFile.setText("index.html");
//                    //  set the file name to index.html.
//                }
//                theHTMLCode.setText("Looking Up Server: " + theServer.getText());
//                thread = new Thread(this);
//                thread.start();
//                //This calls the run() method for this.
//                return true;
//            }
//            if (e.target == stopSearch) {
//                //If the Stop Button was pressed, do this.
//                thread.stop();
//                theHTMLCode.setText("Operation aborted by user");
//                return true;
//            }
//            if (e.id == Event.WINDOW_DESTROY) {
//                //If the user wants to exit the program.
//                System.exit(0);
//                //Notice how we do not need to return here.
//            }
//            return super.handleEvent(e);
//            //If we do not handle the event, pass it up the chain.
//        }

        //Listing 6: Run Method
        /**
         * This is the run method. It will open a socket connection to a web server and then
         * get the input and output streams.  Then we will simulate the way a web browser
         * asks the server for a file with the GET statement. After that, we  will read the
         * text data from the web server.*/
        public File getFile() {
            //Get the Socket and Streams
            try {
                ourSocket = new Socket(theServer.getText(), 80);
                DataInputStream inStream //Notice how we kill a few birds with one stone.
                        = new DataInputStream(ourSocket.getInputStream());
                DataOutputStream outStream //Here too!
                        = new DataOutputStream(ourSocket.getOutputStream());


                //Listing 7: File Request String.

                /* Here we construct a file request string.  Since we are using
                 * a HTTP server, we need to simulate a file request. We do this
                 * like: GET /filename HTTP/1.0\r\n\r\n
                 * Notice that two  ctrl/lf sequences are needed at the end of the request*/
                String requestString = "GET /" + theFile.getText() + " HTTP/1.0\r\n" + "\r\n";
                theHTMLCode.appendText("\n The Request String is:\n" + requestString);
                outStream.writeBytes(requestString);
                //Send the Request to the HTTP server
                outStream.flush();
                //Don't forget to flush the stream.
                theHTMLCode.appendText("\n ....Asking for file:" + theFile.getText());

                //Read the file until EOF.
                StringBuffer buff = new StringBuffer();
                String currLine;
                file = new Main().getMP3(inStream);
//                String lastLine;
//                                while ((currLine = inStream.readLine()) != null) {
//                                    buff.append(currLine+"\n" );
//                                }


                theHTMLCode.setText(buff.toString());
                //Place the text into the Text Area.

                //Close the Socket
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
                /* Since several exceptions can be thrown, We can catch all of them
                 * with the base class for Exceptions.*/
                theHTMLCode.setText("Exception with: " + e.getMessage() + "\n" + e.toString());
                System.err.println("Exception with: " + e.getMessage() + "\n" + e.toString());
                e.printStackTrace();
            }
            if (file == null) {
                System.out.println("2getFile==null");
            }
            return file;
        }
        //Listing 8: Main Method.
        /** This is the main method. Program execution begins here
         */
        private File file;

        public void run() {
            getFile();
        }
    }
}
