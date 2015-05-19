/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package base;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Log {

    private static Properties properties = null;
    private static String debug;
    private static Report report;

    public Log() {
        this.readProperties();
        this.readDebug();
        this.createFileReport();
    }

    private void readDebug() {
        if (Log.debug == null) {
            Log.debug = String.valueOf(Log.properties.getProperty("debug"));
        }
    }

    private void readProperties() {
        try {
            if (Log.properties == null) {
                Log.properties = new Properties();
                Log.properties.load(Log.class.getResourceAsStream("database.properties"));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createFileReport() {
        if (Log.report == null && this.isPrint(3)) {
            Log.report = new Report("report/" + this.getDateTime("dd-MM-yyyy-'at'-hh-mm-ss-a") + "_report.csv");
        }
    }

    public void errorPrint(Object e, String agent) {
        this.print(e, agent);
        throw new UnsupportedOperationException(e.toString());
    }

    public void ddlPrint(String msg, String agent) {
        this.printToFile(msg);
        if (this.isPrint(0)) {
            this.print(msg, agent);
        }
    }

    public void dmlPrint(String msg, String agent) {
        this.printToFile(msg);
        if (this.isPrint(1)) {
            this.print(this.removerNl(msg), agent);
        }
    }

    private void print(Object msg, String agent) {
        System.out.println(this.getDateTime("hh:mm:ss") + " = " + agent + " = " + msg);
    }

    public void msgPrint(Object msg, String agent) {
        this.printToFile(msg);
        if (this.isPrint(2)) {
            this.print(msg, agent);
        }
    }

    private void printToFile(Object msg) {
        if (this.isPrint(3)) {
            this.createFileReport();
            Log.report.add(this.getDateTime("hh:mm:ss"));
            Log.report.add(this.removerNl(String.valueOf(msg)));
            Log.report.ln();
        }

    }

    private boolean isPrint(int pos) {
        return Log.debug.substring(pos, pos + 1).equals("1");
    }

    protected String removerNl(String frase) {
        String padrao = "\\s{2,}";
        Pattern regPat = Pattern.compile(padrao);
        Matcher matcher = regPat.matcher(frase);
        String res = matcher.replaceAll(" ").trim();
        return res.replaceAll("(\n|\r)+", " ");
    }

    private String getDateTime(String format) {
        SimpleDateFormat ft = new SimpleDateFormat(format);
        Date today = new Date();
        return ft.format(today);
    }

    public void title(String msg, String agent) {
        if (this.isPrint(2)) {
            int size = 60 - msg.length();
            String bar = "==";
            for (int i = 0; i < size / 2; ++i) {
                bar += "=";
            }
            this.msgPrint(bar + " " + msg + " " + bar, agent);
        }
    }

    public void endTitle(String agent) {
        this.title("fim", agent);
    }

    public void writeToFile(String text, String name) {
        try (PrintWriter writer = new PrintWriter(name, "UTF-8")) {
            writer.write(text);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
