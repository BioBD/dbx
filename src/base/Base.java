/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package base;

import java.io.IOException;
import java.util.Properties;

public class Base {

    public static Log log;
    private String signatureToDifferentiate;
    public Properties prop;

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
            this.prop = prop;
            switch (this.prop.getProperty("sgbd")) {
                case "2":
                    prop.load(Log.class.getResourceAsStream("database_postgresql.properties"));
                    this.prop.putAll(prop);
                    break;
                case "3":
                    prop.load(Log.class.getResourceAsStream("database_sqlserver.properties"));
                    this.prop.putAll(prop);
                    break;

            }

        } catch (IOException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

}
