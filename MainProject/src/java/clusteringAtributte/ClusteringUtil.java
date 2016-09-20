/*
 * Biblioteca de codigo fonte criada por Rafael Pereira
 * Proibido o uso sem autorizacao formal do autor
 *
 * rpoliveirati@gmail.com
 */
package clusteringAtributte;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Alain
 */
public class ClusteringUtil {
    public static ClusterManagerPerAttribute cluster;
    
    public static String dateToString(Date element){
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(element);
    }
    
}
