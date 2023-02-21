package WebScraper;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Webscraper {

    public static List<String> urlsToVist = new ArrayList<String>();
    public static HashSet<String> hasVisted = new HashSet<String>();
    public static final String START_POINT = "https://en.wikipedia.org/wiki/Main_Page";
    public static final Integer MAX_VISTS = 100_000;
    public static HashMap<Integer,String> urlID = new HashMap<Integer, String>();
    public static Integer idCount = 0;

    public static void main(String[] args) {
        urlsToVist.add(START_POINT);
        while(!urlsToVist.isEmpty() && idCount < MAX_VISTS) {
            vistNext();
            idCount++;
        }
        saveHashMap();
    }

    public static void display(String url) {
        System.out.println("["+(idCount)+"/"+MAX_VISTS+"] Scraping " + url);
    }

    public static void vistNext() {
        String url = urlsToVist.get(0);
        if(idCount % 10 == 0) {
            display(url);
        }
        urlsToVist.remove(url);
        String html = getHTML(url);
        extractLinks(html);
        saveHTML(url,html);
    }

    public static String getHTML(String link) {
        URL url;
        InputStream is = null;
        BufferedReader br;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            url = new URL(link);
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));
            
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (MalformedURLException mue) {
             mue.printStackTrace();
        } catch (IOException ioe) {
             ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }
        return sb.toString();
    }

    public static void extractLinks(String html) {
        for (String a : getMatches(html,"(?i)<a([^>]+)>(.+?)</a>")) {
            for (String link : getMatches(a, "\\s*(?i)href\\s*=\\s*(\\\"([^\"]*\\\")|'[^']*'|([^'\">\\s]+))")) {
                if(link.contains("https://")) {
                    String newLink = link.substring(7,link.length()-1);
                    if(!hasVisted.contains(newLink) && !urlsToVist.contains(newLink)) {
                        urlsToVist.add(newLink);
                    }
                }
            }
        }
    }

    public static void saveHTML(String name, String html) {
        urlID.put(idCount, name);
        String path = ("output/"+idCount+".html");
        File f = new File(path);
        try {
            f.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(path), 32768);
            out.write(html);
            out.close();
        } catch (IOException e) {
            System.out.println("Failed Writing file! ["+path+"]");
            e.printStackTrace();
        }
    }

    public static String[] getMatches(String line, String query) {
        String[] matches = Pattern.compile(query,Pattern.DOTALL)
        .matcher(line)
        .results()
        .map(MatchResult::group)
        .toArray(String[]::new);
        return matches;
    } 

    public static void saveHashMap() {
        String path = ("output/filemap.txt");
        File f = new File(path);
        try {
            f.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(path), 32768);
            for (int i = 0; i < idCount; i++) {
                out.write(urlID.get(i)+"\n");
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Failed Writing file! ["+path+"]");
            e.printStackTrace();
        }
    }
}

