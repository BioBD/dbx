/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package base;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Base {

    public static Log log;
    private String signatureToDifferentiate;
    public Properties propertiesFile;

    public Base() {
        Base.log = new Log();
        this.getPropertiesFromFile();
    }

    public String getSignatureToDifferentiate() {
        return signatureToDifferentiate;
    }

    private void getPropertiesFromFile() {
        try {
            Properties prop = new Properties();
            prop.load(Log.class.getResourceAsStream("database.properties"));
            this.signatureToDifferentiate = String.valueOf(prop.getProperty("signature"));
            this.propertiesFile = prop;
        } catch (IOException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

    protected String removerNl(String frase) {
        String padrao = "\\s{2,}";
        Pattern regPat = Pattern.compile(padrao);
        Matcher matcher = regPat.matcher(frase);
        String res = matcher.replaceAll(" ").trim();
        return res.replaceAll("(\n|\r)+", " ");
    }

}
