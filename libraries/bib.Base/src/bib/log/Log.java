/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package bib.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Log {

    private static Properties properties = null;
    private static String debug;
    private static LogFile logFile;

    public Log(Properties properties) {
        Log.properties = properties;
        this.readDebug();
        this.createFileLog();
    }

    private void readDebug() {
        if (Log.debug == null) {
            Log.debug = String.valueOf(Log.properties.getProperty("debug"));
        }
    }

    private void createFileLog() {
        if (Log.logFile == null && this.isPrint(3)) {
            Log.logFile = new LogFile("report/", this.getDateTime("dd-MM-yyyy-'at'-hh-mm-ss-SSS-a") + "_report.csv");
        }
    }

    public void errorPrint(Object e) {
        this.print(e);
        throw new UnsupportedOperationException(e.toString());
    }

    public void ddlPrint(String msg) {
        this.printToFile(msg);
        if (this.isPrint(0)) {
            this.print(msg);
        }
    }

    public void dmlPrint(String msg) {
        this.printToFile(msg);
        if (this.isPrint(1)) {
            this.print(this.removerNl(msg));
        }
    }

    private void print(Object msg) {
        System.out.println(this.getDateTime("hh:mm:ss") + " = " + msg);
    }

    public void msgPrint(Object msg) {
        this.printToFile(msg);
        if (this.isPrint(2)) {
            this.print(msg);
        }
    }

    private void printToFile(Object msg) {
        if (this.isPrint(3)) {
            this.createFileLog();
            Log.logFile.add(this.getDateTime("hh:mm:ss"));
            Log.logFile.add(this.removerNl(String.valueOf(msg)));
            Log.logFile.ln();
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

    public void title(String msg) {
        if (this.isPrint(2)) {
            int size = 60 - msg.length();
            String bar = "==";
            for (int i = 0; i < size / 2; ++i) {
                bar += "=";
            }
            this.msgPrint(bar + " " + msg + " " + bar);
        }
    }

    public void endTitle() {
        this.title("fim");
    }

}
