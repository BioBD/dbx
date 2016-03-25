/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents.libraries;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Configuration {

    private static Properties prop;

    public Configuration() {
        this.getPropertiesFromFile();
    }

    private void getPropertiesFromFile() {
        if (Configuration.prop == null) {
            try {
                Configuration.prop = new Properties();
                Properties propTemp = new Properties();
                String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
                path = path.substring(0, path.indexOf("build")) + "parameters";
                System.out.println(path);
                File folder = new File(path);
                if (folder.exists()){
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
                } else {
                    System.out.println("Folder " + path + " não foi encontrada.");
                }
            } catch (IOException e) {
                System.err.print(e);
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

    public String getProperty(String key) {
        return prop.getProperty(key);
    }

    public boolean containsKey(String key) {
        return prop.containsKey(key);
    }

}
