/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents.libraries;

import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Log {

    protected static Properties prop = null;
    protected static String debug;
    private static ResourceBundle bundle;
    private static Locale locale;
    private static String lastDebug = "";
    private static int difTime = 0;
    protected static String nameFileLog;
    protected static Gson gson;

    public static String getNameFileLog(String complement) {
        return complement + "_" + nameFileLog;

    }

    public static void setNameFileLog(String nameFileLog) {
        if (Log.nameFileLog == null) {
            Log.nameFileLog = nameFileLog;
        }
    }

    public final void createBundle() {
        if (useBundle()) {
            try {
                if (bundle == null) {
                    locale = new Locale(prop.getProperty("language"), prop.getProperty("country"));
                    bundle = ResourceBundle.getBundle("language/messages", locale);
                }
            } catch (Error err) {
                this.error(err);
            }
        }
    }

    private boolean useBundle() {
        return prop.containsKey("language")
                && prop.containsKey("country");
    }

    public Log(Properties properties) {
        Log.prop = properties;
        readDebug();
        createBundle();
        Log.setNameFileLog(getDateTime("dd-MM-yyyy-'at'-hh-mm-ss-a"));
        gson = new Gson();
        try {
            if ((bundle == null) && (useBundle())) {
                locale = new Locale(prop.getProperty("language"), prop.getProperty("country"));
                bundle = ResourceBundle.getBundle("base/messages", locale);
            }
        } catch (Error err) {
            error(err);
        }
    }

    protected final void readDebug() {
        if (Log.debug == null) {
            Log.debug = String.valueOf(Log.prop.getProperty("debug"));
        }
    }

    protected void print(Object msg) {
        String textToPrint = this.getDateTime("hh:mm:ss") + this.getDifTime(this.getDateTime("hh:mm:ss")) + " = " + msg;
        if (this.isPrint(0)) {
            System.out.println(textToPrint);
        }
        if (this.isPrint(1)) {
            this.writeFile("log", textToPrint);
        }
    }

    protected boolean isPrint(int pos) {
        return Log.debug.substring(pos, pos + 1).equals("1");
    }

    protected String removerNl(String frase) {
        String padrao = "\\s{2,}";
        Pattern regPat = Pattern.compile(padrao);
        Matcher matcher = regPat.matcher(frase);
        String res = matcher.replaceAll(" ").trim();
        return res.replaceAll("(\n|\r)+", " ");
    }

    public final String getDateTime(String format) {
        SimpleDateFormat ft = new SimpleDateFormat(format);
        Date today = new Date();
        return ft.format(today);
    }

    public void title(String msg) {
        if (hasBundle(msg)) {
            msg = bundle.getString(msg);
        }
        if (this.isPrint(1)) {
            int size = 80 - msg.length();
            StringBuilder buf = new StringBuilder();
            buf.append("==");
            for (int i = 0; i < size / 2; ++i) {
                buf.append("=");
            }
            this.print(buf.toString() + " " + msg + " " + buf.toString());
        }
    }

    public void endTitle() {
        this.title("fim");
    }

    public void msg(Object msg) {
        if (hasBundle(msg.toString())) {
            this.print(bundle.getString(msg.toString()));
        } else {
            this.print(msg);
        }
    }

    public void msg(String msg, String bundleMsg) {
        this.print(bundle.getString(bundleMsg) + msg);
    }

    public void error(Object error) {
        if (hasBundle(error.toString())) {
            errorPrint(bundle.getString(error.toString()));
        } else {
            errorPrint(error);
        }
    }

    private void errorPrint(Object e) {
        this.print(e);
        throw new UnsupportedOperationException(e.toString());
    }

    public String getDifTime(String now) {
        if (!now.isEmpty() && !lastDebug.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                Date nowDate = sdf.parse(now);
                Date lastDate = sdf.parse(lastDebug);
                long diff = nowDate.getTime() - lastDate.getTime();
                difTime = (int) (diff / 1000);
            } catch (ParseException ex) {
                Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.setLastDebug(now);
        String result = "= + " + difTime + "s";
        if (difTime < 9) {
            result += "  ";
        } else {
            result += " ";
        }
        return result;
    }

    public void setLastDebug(String last) {
        lastDebug = last;
    }

    public String getLastDebug() {
        return lastDebug;
    }

    public void setDifTime(int difTime) {
        Log.difTime = difTime;
    }

    public void writeFile(String nameFile, String content) {
        try {
            OutputStreamWriter writer;
            boolean append = false;
            switch (nameFile) {
                case "pid":
                    nameFile = nameFile + ".txt";
                    break;
                case "reportexcel":
                    nameFile = prop.getProperty("folderLog") + File.separatorChar + getNameFileLog(nameFile) + ".csv";
                    break;
                case "reportexcelappend":
                    nameFile = prop.getProperty("folderLog") + File.separatorChar + getNameFileLog(nameFile) + ".csv";
                    append = true;
                    break;
                case "log":
                    nameFile = prop.getProperty("folderLog") + File.separatorChar + getNameFileLog(nameFile) + ".txt";
                    append = true;
                    break;
                default:
                    nameFile = prop.getProperty("folderLog") + File.separatorChar + nameFile + ".txt";
                    append = true;
            }
            File file = new File(prop.getProperty("folderLog"));
            if (!file.exists()) {
                file.mkdir();
            }
            file = new File("log");
            if (!file.exists()) {
                file.mkdir();
            }
            writer = new OutputStreamWriter(new FileOutputStream(nameFile, append), "UTF-8");
            BufferedWriter fbw = new BufferedWriter(writer);
            fbw.write(content);
            fbw.newLine();
            fbw.close();
        } catch (IOException ex) {
            this.errorPrint(ex);
        }
    }

    private boolean hasBundle(String msg) {
        if (bundle != null) {
            Enumeration<String> keys = bundle.getKeys();
            while (keys.hasMoreElements()) {
                if (keys.nextElement().equals(msg)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void writePID(long pid) {
        msg("PID: " + pid);
        writeFile("pid", String.valueOf(pid));
    }

    public void archiveLogFiles() {
        File folder = new File(System.getProperty("user.dir") + File.separatorChar + prop.getProperty("folderLog") + File.separatorChar);
        System.out.println(System.getProperty("user.dir") + File.separatorChar + prop.getProperty("folderLog") + File.separatorChar);
        if (!folder.exists()) {
            System.out.println("entrou");
            folder.mkdir();
        }
        File afile[] = folder.listFiles();
        int i = 0;
        for (int j = afile.length; i < j; i++) {
            File arquivos = afile[i];
            String nameFile = arquivos.getName();
            if (nameFile.contains(".txt") || nameFile.contains(".csv") || (nameFile.equals("blackboard.properties") && prop.containsKey("blackboard") && prop.getProperty("blackboard").equals("1"))) {
                String nameFolder = "talk/";
                if (nameFile.contains("_")) {
                    nameFolder += nameFile.substring(nameFile.indexOf("_") + 1, nameFile.indexOf("."));
                } else {
                    nameFolder += nameFile.substring(0, nameFile.indexOf("."));
                }
                new File(nameFolder).mkdir();
                File diretorio = new File(nameFolder);
                File destiny = new File(diretorio, arquivos.getName());
                if (destiny.exists()) {
                    destiny.delete();
                }
                arquivos.renameTo(new File(diretorio, arquivos.getName()));
            }
        }
    }

    public String getJson(Object obj) {
        return gson.toJson(obj);
    }

}
