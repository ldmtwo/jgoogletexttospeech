package org.ldtwo.flashcard;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOException;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ldtwo.GoTTS.G;
import static org.ldtwo.GoTTS.G.*;
import org.ldtwo.GoTTS.MainFrame;
import static org.ldtwo.GoTTS.MainFrame.activeDownloads;
import static org.ldtwo.GoTTS.MainFrame.recentCache;

public class ImageManager {

    final static int MIN_SET_SIZE = 5;
    final static long MAX_FILE_SIZE = 200 * 1024;//bytes
    final static long TRACKER_DELAY = 5000;//ms
    final static long SCHEDULE_DELAY = 400;//ms
    final static int MIN_IMAGE_SIZE = 7000;//bytes
    final static int CONCURRENT_DOWNLOADS = 128;
    static long bytesDownloaded = 0;
    static long totalBytesDownloaded = 0;
    static long ADDITIONAL_SEARCH_TIME = 0;

    static long lastUpdate = System.currentTimeMillis();

    final static int width = 15, height = 11;
    final static int MAX_IMAGES_PER_QUERY = 4;
    final static int MAX_PAGES = 2;
    static int gsTotal = 0, termTotal = 0, imgTotal = 0, prefetchTotal = 0;
    final static PriorityBlockingQueue<Callable> queue = new PriorityBlockingQueue();
    final static PriorityBlockingQueue<ImageFile> imageQueue = new PriorityBlockingQueue();

    static class SchedulerThread extends Thread implements Comparable<Callable> {

        public SchedulerThread(String threadName) {
            super(threadName);
        }

        @Override
        public int compareTo(Callable o) {
            return this.getPriority() > o.hashCode() ? 1 : this.getPriority() == o.hashCode() ? 0 : -1;

        }

    }

    SchedulerThread scheduler = new SchedulerThread("SchedulerThread") {

        @Override
        public void run() {
            while (true) {
                try {
                    Callable t;

                    while ((t = queue.take()) != null) {
                        prefetchExecutor.submit(t);
                    }
                } catch (InterruptedException ex) {
                    //Thread.currentThread().interrupt();
                    ex.printStackTrace();
                }
            }
        }

    };
    final ExecutorService termExecutor = Executors.newFixedThreadPool(8);
    final LinkedList<Future> termJobs = new LinkedList<>();

    final ExecutorService prefetchExecutor = Executors.newFixedThreadPool(8);
    final LinkedList<Future> prefetchJobs = new LinkedList<>();

    final ExecutorService imageDownloadExecutor = Executors.newFixedThreadPool(CONCURRENT_DOWNLOADS);
    final LinkedList<Future> imageDownloadJobs = new LinkedList<>();

    final ExecutorService gSearchExecutor = Executors.newFixedThreadPool(2);
    final LinkedList<Future> gSearchJobs = new LinkedList<>();

    final public ExecutorService[] executors = {gSearchExecutor, imageDownloadExecutor, termExecutor, prefetchExecutor};
    final LinkedList[] jobListOfLists = {gSearchJobs, imageDownloadJobs, termJobs, prefetchJobs};

    public static File newResponseFile(final String encodedQuery, String extraInfo) {
        return new File(RESPONSE_PATH + G.makeValidFileName(encodedQuery) + "__" + extraInfo + ".xml");
    }

    public static File query2ImageFile(String query, int index) {
        return new File(IMAGE_PATH + G.makeValidFileName(query) + "__" + index + ".jpg");
    }

    public static File query2AudioFile(String query, String lang) {
        new File(AUDIO_PATH + "\\" + lang).mkdirs();
        return new File(AUDIO_PATH + lang + "\\" + G.makeValidFileName(query) + ".mp3");
    }

    public void gSearchPause() throws InterruptedException {
        if (imageDownloadJobs.size() > 10 || imageDownloadJobs.size() % 3 == 0) {
            Thread.sleep(1500);
        }
        Thread.sleep((long) (300 + Math.random() * 400 + imageDownloadJobs.size() * 20 + ADDITIONAL_SEARCH_TIME));
    }

    public static void setHttpConnectionProperties(HttpURLConnection connection) {
        connection.setConnectTimeout(700);
        connection.setReadTimeout(800);
        connection.setDefaultUseCaches(true);
        connection.setDoOutput(true);
        connection.setUseCaches(true);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1779.2 Safari/537.36");
    }

    public static void main(String[] args) {
        try {
            String ss = "http://www.google.com/images?oe\\u003dutf8\\u0026ie\\u003dutf8\\u0026source\\u003duds\\u0026start\\u003d0\\u0026hl\\u003den\\u0026q\\u003dapple";
            char[] chars = ss.toCharArray();
            int i = 0;
            for (int j = 0; j < chars.length; j++) {
                char ch = chars[j];
                if (ch == '\\') {
                    String num = "#";
                    j++;
                    num += chars[++j];
                    num += chars[++j];
                    num += chars[++j];
                    num += chars[++j];
                    System.out.println(num);
                    int val = Integer.decode(num);
                    chars[i++] = (char) val;
                } else {
                    chars[i++] = ch;
                }
            }
            System.out.println(new String(chars, 0, i));
            //new ImageManager();
        } catch (Exception ex) {
            Logger.getLogger(ImageManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static Future[] listToArraySnapshot(LinkedList<Future> gSearchJobs) {
        int size = gSearchJobs.size();
        Future[] arr = new Future[size];
        int i = 0;
        Iterator<Future> iter = gSearchJobs.iterator();
        while (iter.hasNext() && i < size) {
            Future f = iter.next();
            if (i < size && f != null) {
                arr[i++] = f;
            }
        }
        return arr;
    }

    public ImageManager() throws Exception {

        for (int i = 0; i < CONCURRENT_DOWNLOADS/2; i++) {
            imageDownloadExecutor.submit(new Runnable() {

                @Override
                public void run() {
                    Exception ex;
                    while (true) {
                        try {
                            ImageFile f = null;
                            // synchronized (imageQueue) 
                            {
                                f = imageQueue.take();
                            }
                            String u1 = f.url1, u2 = f.url2;
                            int notZero = 100;
                            while (u1.indexOf('%') >= 0 && notZero-- > 0) {
                                u1 = URLDecoder.decode(u1);
                            }
                            while (u2.indexOf('%') >= 0 && notZero-- > 0) {
                                u2 = URLDecoder.decode(u2);
                            }
                            if (notZero <= 0) {
                                System.err.println("ERROR: INF LOOP DETECTED!\n\t" + u1 + "\n\t" + u2);
                            }
                            ex = download(f.file, u1, u2);

                            if (ex != null) {
                                printURLException(ex, u2);
                                //throw ex;
                            } else {
                                f.set.add(f);
                            }
//                        testImageFileValidity(outputFile);
////                            JButton btn = new JButton();
//                        set.add(imgf);       
                        } catch (Exception ex1) {
                            Logger.getLogger(ImageManager.class.getName()).log(Level.SEVERE, null, ex1);
                        }

                    }
                }
            });
        }

//        LinkedList<Term> deck = CardFrame.getDeck(
//                new File("C:\\Users\\Larry\\Desktop\\French class\\vocab\\chpt 4.tab.txt")
//        );
//        loadDeckWithImages(deck);
//
//        for (Term t : deck) {
//            //System.out.printf("%s\n",t.info());
//        }
    }
    Thread tracker = new Thread() {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(TRACKER_DELAY);
                    long curTime = System.currentTimeMillis();
                    long bytesDownloaded_ = bytesDownloaded;
                    bytesDownloaded = 0;
                    totalBytesDownloaded += bytesDownloaded_;
                    System.out.printf("DOWNLOADS REMAINING: "
                            + "Search Queries=%2si %sd, "
                            + "Terms=%2si %sd, "
                            + "Image Downloads = %si %sd, "
                            + "Cache Error Check = %si %sd, \r\n >  >  >  >  >  "
                            + "%s KB/s, "
                            + "%s KB total downloaded"
                            + "\t\t%s\n",
                            gSearchJobs.size(), gsTotal - gSearchJobs.size(),
                            termJobs.size(), termTotal - termJobs.size(),
                            imageDownloadJobs.size(), imgTotal - imageDownloadJobs.size(),
                            prefetchJobs.size(), prefetchTotal - prefetchJobs.size(),
                            1000 * bytesDownloaded_ / 1024 / (curTime - lastUpdate), totalBytesDownloaded / 1024,
                            new Date());
                    lastUpdate = curTime;
                } catch (Exception ex) {
                }
            }
        }
    };

    public void shutdown() {
        try {
            System.out.println("--------------------------------\nShutting down!");
            for (ExecutorService exec : executors) {
                exec.shutdownNow();
                exec.awaitTermination(1, TimeUnit.SECONDS);
            }
            tracker.stop();
            scheduler.stop();
        } catch (InterruptedException ex) {
        }
    }

    public void loadDeckWithImages(final LinkedList<Term> deck) throws InterruptedException, ExecutionException {
        //Collections.shuffle(deck);
        scheduler.start();
        new File(IMAGE_PATH).mkdirs();
        for (final Term t : deck) {
            Callable c = new Callable() {
                public Object call() {
                    try {
                        t.setRightSet(locateImageFiles(t.right, null));
                        t.setLeftSet(locateImageFiles(t.left, null));
                        try {
                            Callable r = new Callable() {
                                public Object call() {
                                    try {
                                        t.setRightSet(getImages(t.right, null, t.getRightSet()));
                                        t.setLeftSet(getImages(t.left, null, t.getLeftSet()));
                                        //t.leftSet = locateImageFiles(t.left, null);
                                    } catch (Throwable thr) {
                                        thr.printStackTrace();
                                    }
                                    return t;
                                }
                            };
                            if (true) {
                                termTotal++;
                                termJobs.addLast(termExecutor.submit(r));
                            } else {
                                throw new InterruptedException();
                            }
                        } catch (InterruptedException e) {
                        }
                        //t.leftSet = locateImageFiles(t.left, null);
                    } catch (Throwable thr) {
                    }
                    return t;
                }
            };
//            queue.offer(c);
            prefetchTotal++;
            prefetchJobs.add(prefetchExecutor.submit(c));
        }

        tracker.start();

        //BLOCK ON PREFETCHER  
//        while (!prefetchJobs.isEmpty() //&& prefetchJobs.peekFirst().isDone()
//                ) {
//            Future f = prefetchJobs.removeFirst();
//            f.get();
//
//        }
        try {
            File f = new File("C:\\Users\\Larry\\Desktop\\French class\\vocab\\chpt 3.html");
            String str = Term.toHTML(deck);
            FileOutputStream os = new FileOutputStream(f);
            os.write(str.getBytes());
            os.close();
        } catch (Exception ex) {
        }
        System.out.println();
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    Object o;

                    Thread.sleep(2000);
                    System.out.println("\n--------------------------------\nWAITING");
                    while (true) {
                        for (LinkedList<Future> jobList : jobListOfLists) {
                            while (!jobList.isEmpty() && jobList.peekFirst().isDone()) {
                                Future f = jobList.removeFirst();
                                o = f.get();
                                if (o != null && o instanceof Term) {
                                    Term t = (Term) o;
                                    //System.out.printf("Term:    %s\n", t.info());
                                }
                            }
                        }
                        Thread.sleep(1);
                        int sum = 0;
                        for (LinkedList<Future> jobList : jobListOfLists) {
                            sum += jobList.size();
                        }
                        if (sum <= 0) {
                            break;
                        }
                    }
                    System.out.println("DONE\n--------------------------------\nShutting down!");
                    for (ExecutorService exec : executors) {
                        exec.shutdown();
                        exec.awaitTermination(999, TimeUnit.DAYS);
                    }
                    tracker.stop();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    File f = new File("C:\\Users\\Larry\\Desktop\\French class\\vocab\\chpt 3.html");
                    String str = Term.toHTML(deck);
                    FileOutputStream os = new FileOutputStream(f);
                    os.write(str.getBytes());
                    os.close();
                } catch (Exception ex) {
                }
            }
        };
        new Thread(runnable).start();

        while (prefetchJobs.size() >= prefetchTotal) {
            Thread.sleep(200);
        }
        Thread.sleep(3000);
    }

    public static HashSet<ImageFile> locateImageFiles(final String query, JPanel pan) {
        HashSet<ImageFile> set = new HashSet();
        int count = 0;
        //test cached results for validity
        for (int i = 0; i < MAX_IMAGES_PER_QUERY * MAX_PAGES; i++) {
            final File file = query2ImageFile(query, i);
//            System.out.printf("<?>  FILE: %s\t\t%s\n", file, file.exists());
            if (file.exists() && file.length() <= MAX_FILE_SIZE && file.length() > MIN_IMAGE_SIZE) {
                try {
                    testImageFileValidity(file);
                    set.add(new ImageFile(file, set, "NOT_URL", "NOT_URL"));
                    count++;
                } catch (Exception ex) {
                    System.out.printf("!!!  INVALID FILE: %s\n", file);
                }
            }
        }
        return set;
    }

    /**
     * pan can be null
     *
     * @param query
     * @param pan if not null, adds images to this
     * @param set
     * @return Collection of JComponents
     */
    public HashSet<ImageFile> getImages(final String query, final JPanel pan, final HashSet<ImageFile> set) {

        try {

            new File(RESPONSE_PATH).mkdirs();
            int count = 0;
            //test cached results for validity
//            for (int i = 0; i < MAX_IMAGES_PER_QUERY * MAX_PAGES; i++) {
//                final File file = query2ImageFile(query, i);
//                if (file.exists()) {
//                    count++;
//                }
//            }
//            if (set.size() < MIN_SET_SIZE) {
//                if (count > 0) {
//                    System.out.printf("    %11s  >>>>>>   %s of %s valid\n", query, set.size(), count);
//                }
//            }
            if (set.size() > MIN_SET_SIZE) {//if enough valid results, then quit
                return set;
            }
            final String encodedQuery = G.encode2URL(query.replace("/", " or "));
            //System.out.printf("\tENCODE:\t%s >>> %s \n", query, encodedQuery);
            LinkedList<Future> jobs = new LinkedList<>();
            Callable c = new Callable() {
                public Object call() {
                    File outputFile = newResponseFile(encodedQuery, "0-3");
                    try {
                        queryToResponseFile(encodedQuery, query, outputFile, "");//talk to google or get recent result
                        interpretResponseAndDownload(outputFile, encodedQuery, query, set, pan, 0);
                        ADDITIONAL_SEARCH_TIME = ADDITIONAL_SEARCH_TIME * 85 / 100;
                    } catch (FileNotFoundException | JSONException ex) {
                        ADDITIONAL_SEARCH_TIME += 1000;
                        try {
                            Thread.sleep(70000);//retry in 70 sec
                            queryToResponseFile(encodedQuery, query, outputFile, "");//talk to google or get recent result
                            interpretResponseAndDownload(outputFile, encodedQuery, query, set, pan, 0);

                        } catch (JSONException ex2) {
                            System.err.printf("JSON Error: %s, %s\n", encodedQuery, outputFile);
                            ex.printStackTrace();
                            System.exit(-1);
                        } catch (Exception ex2) {
                            ex2.printStackTrace();
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return "";
                }
            };
            Future sf = gSearchExecutor.submit(c);
            jobs.add(sf);
            gsTotal++;
            gSearchJobs.addLast(sf);
//            Thread.sleep(50);
//            Callable c2 = new Callable() {
//                public Object call() {
//                    try {
//                        File outputFile = newResponseFile(query, "4-7");
//                        //talk to google or get recent result
//                        //may or may not block depending on cache
//                        queryToResponseFile(encodedQuery, query, outputFile, "&start=4&label=2");
//                        interpretResponseAndDownload(outputFile, encodedQuery, query, set, pan, MAX_IMAGES_PER_QUERY);
//                    } catch (Exception ex) {
//                    }
//                    return "";
//                }
//            };
//            sf = gSearchExecutor.submit(c2);
//            jobs.add(sf);
//            gsTotal++;
//            gSearchJobs.addLast(sf);
//join/collect on queries
            for (Future fut : jobs) {
                fut.get();
            }
            return set;
//        } catch (UnknownHostException ex) {
//            try {
//                FileOutputStream os = new FileOutputStream("UnknownHosts.txt", true);
//                os.write(String.format("%s\n", ex.toString()).getBytes());
//                os.close();
//            } catch (IOException ex2) {
//            }

        } catch (RejectedExecutionException ex) {
        } catch (InterruptedException ex) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashSet();
    }

    public void interpretResponseAndDownload(File outputFile, final String encodedQuery, final String query, HashSet<ImageFile> set, JPanel pan, final int indexOffset) throws FileNotFoundException, JSONException {
        String content = new Scanner(outputFile).useDelimiter("\\Z").next();
        JSONObject json = new JSONObject(content);
        JSONArray results = null;
        results = json.getJSONObject("responseData").getJSONArray("results");
        //JPanel pan = new JPanel();
        downLoadJSONResults(results, encodedQuery, query, set, pan, indexOffset);
    }

    synchronized public File queryToResponseFile(final String encodedQuery, final String query, final File outputFile, String extraQueryInfo) throws FileNotFoundException, IOException, MalformedURLException, InterruptedException {

        if (outputFile.exists() && outputFile.length() > 1024) {
            return outputFile;
        }
        gSearchPause();
//        URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?v=1.0"
//                + extraQueryInfo + "&q=" + encodedQuery);
        String url = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0"
                + "&q=" + encodedQuery + extraQueryInfo;
        new File(RESPONSE_PATH).mkdirs();
        Exception ex = download_httpConnection(url, outputFile);
        if (ex != null) {
            printURLException(ex, url);
        }

//        URLConnection connection = url.openConnection();
//        //connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1779.2 Safari/537.36");
//
//        FileOutputStream fis = new FileOutputStream(outputFile);
//        byte[] buff = new byte[4096];
//        int len;
//        InputStream is = connection.getInputStream();
//        while ((len = is.read(buff)) >= 0) {
//            fis.write(buff, 0, len);
//        }
//        is.close();
//        fis.close();
        return outputFile;
    }

    public static boolean testImageFileValidity(final File file) throws IOException {
        return null != ImageIO.read(file);
    }

    public static JToggleButton imageFile2jButton_check(int width, int height, final File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        Image img = image.getScaledInstance(width, height, Image.SCALE_FAST);
        BufferedImage bad = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = bad.createGraphics();
        g.setColor(Color.red);
        g.drawImage(img, 0, 0, null);
        //\u2713
        g.setFont(Font.decode("Arial-BOLD-200"));
        g.drawString("\u2713", width / 2 - 75, height / 2 + 75);
        JToggleButton btn;// = new JButton(new ImageIcon(img));
        ImageIcon front = new ImageIcon(img);
        btn = new JToggleButton(front);
        //btn.setRolloverEnabled(true);
        ImageIcon back = new ImageIcon(bad);
        btn.setPressedIcon(front);
        btn.setSelectedIcon(back);
        //btn.setRolloverIcon(back);
        //btn.setRolloverSelectedIcon(front);
        img.flush();
        g.dispose();
        return btn;
    }

    public static JToggleButton imageFile2jButton_x(int width, int height, final File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        Image img = image.getScaledInstance(width, height, Image.SCALE_FAST);
        BufferedImage bad = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        ImageObserver imgObs = new ImageObserver() {

            @Override
            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                return false;
            }
        };
        Graphics2D g = bad.createGraphics();
        g.setColor(Color.red);
        g.drawImage(img, 0, 0, null);
        //\u2713
        g.setFont(Font.decode("Arial-BOLD-200"));
        g.drawString("X", width / 2 - 75, height / 2 + 75);
        JToggleButton btn;// = new JButton(new ImageIcon(img));
        ImageIcon front = new ImageIcon(img);
        btn = new JToggleButton(front);
        //btn.setRolloverEnabled(true);
        ImageIcon back = new ImageIcon(bad);
        btn.setPressedIcon(front);
        btn.setSelectedIcon(back);
        //btn.setRolloverIcon(back);
        //btn.setRolloverSelectedIcon(front);
        img.flush();
        g.dispose();
        return btn;
    }

    public void downLoadJSONResults(final JSONArray results, final String encodedQuery, final String query,
            final HashSet<ImageFile> set, final JPanel pan, final int indexOffset) {
        LinkedList<Future<String>> localJobs = new LinkedList<>();
        int len = results.length();
        for (int i = 0; i < len; i++) {
            final int i_ = i;
            Callable c = new Callable() {

                @Override
                public String call() throws Exception {
                    Exception ex = null;
                    if (set.size() > MIN_SET_SIZE) {//if enough valid results, then quit
                        return "";
                    }
                    JSONObject obj = results.getJSONObject(i_);
                    String title = "<html> " + obj.getString("content");
                    String imageUrl = obj.getString("url");
                    String unescapedUrl = obj.getString("unescapedUrl");
                    final File outputFile = query2ImageFile(query, indexOffset + i_);
                    ImageFile imgf = new ImageFile(outputFile, set, imageUrl, unescapedUrl);
                    if (true) {
                        //synchronized (imageQueue) 
                        {
                            //f=imageQueue.take();
                            imageQueue.add(imgf);
                        }
                        return "";
                    }
                    if (set.contains(imgf)) {
                        return "";
                    }
                    String ss = "";
                   //  = downloadResult(title, imageUrl, unescapedUrl, i_, query, hashes, set, pan);

                    //if (outputFile.exists())
                    try {
//                        ex = download(outputFile, imageUrl, unescapedUrl);
//                        if (ex != null) {
//                            throw ex;
//                        }
//                        testImageFileValidity(outputFile);
////                            JButton btn = new JButton();
//                        set.add(imgf);
                        //ss = "";
                    } catch (java.awt.color.CMMException ex2) {
                    } catch (IllegalArgumentException ex2) {
                        //System.err.printf(">>>>> failed - bad image: %s\t%s \n", unescapedUrl, ex2);
                    } catch (Exception exception) {
                        ex = exception;
                        //System.err.printf("%s:   oXo %s ---->>> %s\n", ex, unescapedUrl, outputFile);
                        for (int tries = 3; tries > 0; tries--) {
                            try {
                                if (imageUrl.compareToIgnoreCase(unescapedUrl) == 0) {
                                    break;
                                }
                                //System.out.printf("\n>>> decoding URL: %s\n", imageUrl);
//                                    System.out.printf("decoding URL: %s\n", visibleUrl);
                                if (imageUrl.indexOf('%') >= 0) {
                                    imageUrl = URLDecoder.decode(imageUrl);
                                }
                                if (unescapedUrl.indexOf('%') >= 0) {
                                    unescapedUrl = URLDecoder.decode(unescapedUrl);
                                }
                                //unescapedUrl = URLDecoder.decode(unescapedUrl);
//                                    System.out.printf(">>>> trying URL: %s\n", imageUrl);
//                                    System.out.printf("trying URL: %s\n", visibleUrl);
                                //ss = downloadResult(title, imageUrl, unescapedUrl, i_, query, hashes, set, pan);

                                ex = download(outputFile, imageUrl, unescapedUrl);

//                            if(ex!=null)throw ex;
                                if (ex != null) {
                                    continue;
                                }
                                testImageFileValidity(outputFile);
                                //JButton btn = new JButton();
                                set.add(imgf);
                                //System.out.printf(">>>>> success URL: %s\t\t%s\n", imageUrl, unescapedUrl);
//                                    System.out.printf("success URL: %s\n", visibleUrl);
                                return ss;
                                //break;
                            } catch (Exception e) {
                                Thread.yield();
//                                    System.out.printf("failed URL: %s\n", visibleUrl);
                            }
                        }

//                            ss += "\n";
//                            ss += URLDecoder.decode(obj.getString("url"));
//                            ss += String.format("\n%s\n%s", outputFile, query);
//                        ex.printStackTrace();
                    }

                    if (ex != null) {
                        printURLException(ex, unescapedUrl);
                        //System.err.printf(">>>>> failed URL: %s\t\t%s\n", imageUrl, unescapedUrl);
                        //throw ex;
                    }
                    return ss;
                }

            };
            Future fut = imageDownloadExecutor.submit(c);
            localJobs.addLast(fut);
            imageDownloadJobs.addLast(fut);
            imgTotal++;
        }

        int count = 0;
        //test cached results for validity
//        for (int i = 0; i < 4; i++) {
//        }
        while (!localJobs.isEmpty()) {
            try {
                String ss = localJobs.removeFirst().get();
                System.out.print(ss);
            } catch (InterruptedException ex) {
                Logger.getLogger(ImageManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(ImageManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static public Exception download(File outputFile, String imageUrl, String unescapedUrl) throws Exception {

        Exception ex = download_httpConnection(unescapedUrl, outputFile);
        if (ex != null) {
            if (ex instanceof FileTooLargeException) {
                return ex;
            }
            //System.err.printf(" X %s ---->>> %s\n", imageUrl, outputFile);
//                ex = download_directStream(unescapedUrl, outputFile);
        }
        if (ex != null) {
            //System.err.printf(" X %s ---->>> %s\n", imageUrl, outputFile);
            ex = download_httpConnection(imageUrl, outputFile);
        }
//            if (ex != null) {
//                //System.err.printf(" X %s ---->>> %s\n", imageUrl, outputFile);
//                ex = download_directStream(imageUrl, outputFile);
//            }
        if (ex != null) {
            //System.err.printf(" X %s ---->>> %s\n", imageUrl, outputFile);
        }

        if (ex != null) {
            int idx = -1;
            String newUrl = unescapedUrl.toLowerCase();
            if (idx < 0) {
                idx = newUrl.indexOf(".jpg");
            }
            if (idx < 0) {
                idx = newUrl.indexOf(".png");
            }
            if (idx < 0) {
                idx = newUrl.indexOf(".gif");
            }
            if (idx >= 0) {
                newUrl = newUrl.substring(0, idx + 4);
                ex = download_httpConnection(newUrl, outputFile);
            }
            if (imageUrl.compareToIgnoreCase(newUrl) != 0) {
                System.err.printf("LastTry(substr): [pass==%s] %s ---->>> %s\n", ex == null, imageUrl, newUrl);
            }
        }
        return ex;

    }

    public static void printURLException(Exception ex, String url) {
        if (ex instanceof UnknownHostException) {
            System.err.printf("Host is down: %s\n", url);
        } else if (ex instanceof FileNotFoundException) {
            System.err.printf("Bad URL or missing: %s\n", url);
        } else if (ex instanceof SocketTimeoutException) {
            System.err.printf("Read or connection time out: %s\n", url);
        } else if (ex instanceof IIOException) {
            System.err.printf("%s: %s\n", ex.getMessage(), url);
        } else if (ex instanceof FileTooLargeException) {
            //System.out.printf("Config warning - %s: %s\n", ex.getMessage(), url);
        } else if (ex instanceof IOException && ex.toString().contains("403")) {
            System.err.printf("403 Permission denied: %s\n", url);
        } else if (ex instanceof IOException && ex.toString().contains("404")) {
            System.err.printf("404 Not found: %s\n", url);
        } else if (ex instanceof IOException && ex.toString().contains("400")) {
            System.err.printf("400 Bad URL: %s\n", url);
        } else if (ex instanceof IOException && ex.toString().contains("500")) {
            System.err.printf("500 Internal error: %s\n", url);
        } else if (ex instanceof IOException && ex.toString().contains("301")) {
            System.err.printf("301 Moved/redirect: %s\n", url);
        } else if (ex instanceof SSLProtocolException) {
            System.err.printf("Bad protocol: %s\n", url);
        } else if (ex instanceof SSLHandshakeException) {
            System.err.printf("Protocol error: %s\n", url);
        } else if (ex instanceof ConnectException) {
            System.err.printf("Connection timed out: %s\n", url);
        } else {
            ex.printStackTrace();
        }
    }

    static public Exception download_directStream(String urlStr, File outputFile) {
        try {
            URL url = new URL(urlStr);

            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(outputFile);
            long len = fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            bytesDownloaded += len;
            //System.out.printf("downloaded(direct): %s\n", urlStr);
        } catch (Exception ex) {
            return ex;
        }
        return null;
    }
    public static final Font font6 = Font.decode("Arial Unicode-8");
    public static final Font font12 = Font.decode("Arial Unicode-12");

    static final MouseAdapter dlButtonListener = new MouseAdapter() {

        @Override
        public void mouseEntered(MouseEvent e) {
            ((JComponent) e.getSource()).setFont(font12);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            ((JComponent) e.getSource()).setFont(font6);
        }

    };

    static public Exception download_httpConnection(String urlStr, File outputFile) {
        try {
            JButton item = new JButton(outputFile.getAbsolutePath());
            item.addMouseMotionListener(dlButtonListener);
            item.setOpaque(true);
            
            item.setBackground(Color.gray);
            item.setFont(font6);
            item.setAlignmentY(-1);
            item.setHorizontalAlignment(SwingConstants.LEFT);
            item.setHorizontalTextPosition(SwingConstants.LEFT);
            synchronized (activeDownloads) {
                if (!recentCache.contains(outputFile)) {
                    activeDownloads.addFirst(item);
//                    MainFrame.downloads.setListData(activeDownloads.toArray());
//                    G.refresh(MainFrame.downloads);
//                    MainFrame.recentCache.add(item);
                }
            }
            if (outputFile.exists()) {
                return null;
            }
            item.setBackground(Color.red);
            item.updateUI();
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            setHttpConnectionProperties(connection);
            int responseCode = 0;
            responseCode = connection.getResponseCode();

            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = connection.getHeaderField("Content-Disposition");
                String contentType = connection.getContentType();
                long contentLength = connection.getContentLengthLong();
                if (contentLength > MAX_FILE_SIZE) {
                    return new FileTooLargeException(contentLength, urlStr);
                }
                if (disposition != null) {
                    // extracts file name from header field
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10,
                                disposition.length() - 1);
                    }
                } else {
                    // extracts file name from URL
                    fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1,
                            urlStr.length());
                }

//                System.out.println("Content-Type = " + contentType);
//                System.out.println("Content-Disposition = " + disposition);
//                System.out.println("Content-Length = " + contentLength);
//                System.out.println("fileName = " + fileName);
                // opens input stream from the HTTP connection
                InputStream inputStream = connection.getInputStream();

                // opens an output stream to save into file
                FileOutputStream outputStream = new FileOutputStream(outputFile);

                item.setBackground(Color.orange);
                item.updateUI();
                int bytesRead;
                int total = 0;
                byte[] buffer = new byte[128 * 1024];
                int loops = 500;
                while ((bytesRead = inputStream.read(buffer)) != -1 && loops-- > 0) {
                    outputStream.write(buffer, 0, bytesRead);
                    total += bytesRead;
                    bytesDownloaded += bytesRead;
                    if (loops < 400) {
                        //delay 0 ms (500...400), 1 ms (399...300), ..., 6 ms(99...0)
                        Thread.sleep(6 - loops / 100);//6 or greater --> 1 or greater
                    }
                }
                if (loops < 10) {
                    System.err.printf("download_httpConnection: large file? %s loops, %s bytes, %s, %s\n",
                            loops, total, urlStr, outputFile);
                }
                outputStream.close();
                inputStream.close();

                item.setBackground(Color.green);
                item.updateUI();
                //System.out.printf("downloaded(http): %s\n", urlStr);

            } else {
                return new IOException("Server replied HTTP code: " + responseCode);
            }
            connection.disconnect();
        } catch (SSLHandshakeException ex) {
            return ex;
        } catch (SocketTimeoutException ex) {
            return ex;
        } catch (Exception ex) {
            //ex.printStackTrace();
            return ex;
        }
        return null;
    }

//    public static String downloadResult(String title, String imageUrl, String visibleUrl, int i, final String query,
//            HashSet<int[]> hashes, HashSet set, JPanel pan) {
//        try {//TODO simplify this function
//
//            final File file = new File(IMAGE_PATH + query + "__" + i + ".jpg");
//            testImageFileValidity( file);
//            if (!file.exists() || file.length() < MIN_IMAGE_SIZE) {
//                //System.out.printf(" <<<<    attempting to retriev{URL stream} <<>> result[%s]: %s\n", i,query);
//                FileOutputStream os = null;
//                InputStream is = null;
//                try {
//                    os = new FileOutputStream(file);
//                    is = new URL(imageUrl).openStream();
//                    int len;
//                    byte[] buff = new byte[1024 * 4];
//
//                    while ((len = is.read(buff)) >= 0) {
//                        os.write(buff, 0, len);
//                    }
//                    os.close();
//                    is.close();
//                } catch (ConnectException ex) {
////                        if (ex.getMessage().contains("Connection refused: connect")) {
////                            Thread.sleep(150);
////                            System.err.printf(" XXXX      %s\n\t%s (%s) ==> %s\n", ex.getMessage(),query,i,title);
////                            Thread.sleep(150);
////                        } else  {
////                            System.err.println(imageUrl);
////                            ex.printStackTrace();
////                        }
//                } catch (FileNotFoundException ex) {
////                        System.err.printf(" XXXX      Failed to retrieve:    %s\n", imageUrl);
//
//                } catch (IOException ex) {
////                        if (ex.getMessage().contains("HTTP response code: 403")) {
////                            Thread.sleep(150);
////                            System.err.printf(" XXXX      %s\n\t%s (%s) ==> %s\n", ex.getMessage(),query,i,title);
////                            Thread.sleep(150);
////                        } else  {
////                            System.err.println(imageUrl);
////                            ex.printStackTrace();
////                        }
//                } finally {
//                    try {
//                        os.close();
//                        is.close();
//                    } catch (Exception e) {
//                    }
//                }
//                try {
//                    image = ImageIO.read(file);
//                } catch (Exception exception) {
////                    file.renameTo(new File(file.getParent() + "+" + file.getName()));
//                    Thread.sleep(15);
//                }
//            }
//            if (!file.exists() || file.length() < 7000) {
////                    System.out.printf(" <<<<    attempting to retriev{GET HTTP/1.0} <<>> result[%s]: %s\n",i, query);
//                SocketClient soc = new SocketClient(query + "__" + i);
//                soc.address.setText(visibleUrl);
//
//                soc.path.setText(URLDecoder.decode(imageUrl, "UTF-8"));
//                File file2 = null;
//                try {
//                    file2 = soc.getFile();
//                    file2.renameTo(file);
//                } catch (NullPointerException e) {
////                    System.err.printf("%s\n%s\n%s\n", imageUrl, file, file2);
////                    e.printStackTrace();
//                }
//                try {
//                    image = ImageIO.read(file);
//                } catch (Exception exception) {
////                    file.renameTo(new File(file.getParent() + "\\+++" + file.getName()));
////                        file.delete();
////                        Thread.sleep(15);
//                }
//            }
//            if (!file.exists() || file.length() < 1000) {
//                if (file.length() == 0) {
//                    file.deleteOnExit();
//                }
////                file.renameTo(new File(file.getParent() + "\\+++++" + file.getName()));
////                    System.err.printf(" XXXX    Failed{invalid or not recieved} <<>> result[%s]: %s\n", i, query);
//                return imageUrl;
//            }
//            image = ImageIO.read(file);
//            if (image == null) {
////                file.renameTo(new File(file.getParent() + "\\++++" + file.getName()));
////                    System.err.printf(" XXXX    Failed{bad format} <<>> result[%s]: %s\n", i, query);
//                return imageUrl;
//            }
//            long h;
//            ImageFile img = image.getScaledInstance(15, 11, ImageFile.SCALE_FAST);
//            BufferedImage img2 = toBufferedImage(img);
//            int[] data = new int[10 * 10 * 4];
//            data = img2.getRaster().getPixels(0, 0, 10, 10, data);
//            float f;
//            int k = 0;
//            long minH = Long.MAX_VALUE;
//            for (int[] otherArray : hashes) {
//                h = -100;
////                    for (int j = 0; j < otherArray.length; j++) {
////                        h += (otherArray[j] - data[j]) * (otherArray[j] - data[j]);
////
////                    }
//                k++;
//                if (h < minH) {
//                    minH = h;
//                }
////                    System.out.printf("%s\t\t h=%s, k=%s,    %s\n", i, h, k, data.length);
//            }
////                for (int d : data) {
////
////                    h = -h * 31 + (int) (d / 64);
////                }
//            //if (minH > 5000)
//            {
//                JButton btn = new JButton(new ImageIcon(image.getScaledInstance(150, 110, ImageFile.SCALE_FAST)));
//                set.add(btn);
//                if (pan != null) {
//                    pan.add(btn);
//                }
////                    hashes.add(data);
//            }
//        } catch (Exception ex2) {
//            System.err.println("------------- Loop error not caught");
//            ex2.printStackTrace();
//        }
//        return imageUrl;
//    }
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        // Return the buffered image
        return bimage;
    }

}
