/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package bib.base;

import bib.log.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Base {

    public static Log log;
    public static Properties prop;

    public Base() {
        this.getPropertiesFromFile();
        Base.log = new Log(Base.prop);
    }

    private void getPropertiesFromFile() {
        if (Base.prop == null) {
            try {
                Base.prop = new Properties();
                Properties propTemp = new Properties();
                File folder = new File("parameters");
                if (folder.exists()) {
                    System.out.println("Reading parameters.");
                    File[] listOfFiles = folder.listFiles();
                    for (int i = 0; i < listOfFiles.length; i++) {
                        File file = listOfFiles[i];
                        if (file.isFile() && file.getName().endsWith(".properties")) {
                            InputStream targetStream = new FileInputStream(file);
                            propTemp.load(targetStream);
                            prop.putAll(propTemp);
                            System.out.println("File: " + file.getName());
                        }
                    }
                }
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    public String clearNameFile(String nameFile) {
        String name = nameFile.toLowerCase().replace("http://", "");
        name = name.toLowerCase().replace("/", ".");
        name = name.toLowerCase().replace("?", "_");
        name = name.toLowerCase().replace(" ", "_");
        name = name.replaceAll("[^\\p{ASCII}]", "");
        return name;
    }

    protected String removerNl(String frase) {
        String padrao = "\\s{2,}";
        Pattern regPat = Pattern.compile(padrao);
        Matcher matcher = regPat.matcher(frase);
        String res = matcher.replaceAll(" ").trim();
        return res.replaceAll("(\n|\r)+", " ");
    }

    public void setProperty(String key, String value) {
        prop.setProperty(key, value);
    }
}
