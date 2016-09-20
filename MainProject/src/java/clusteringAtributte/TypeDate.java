/*
 * Biblioteca de codigo fonte criada por Rafael Pereira
 * Proibido o uso sem autorizacao formal do autor
 *
 * rpoliveirati@gmail.com
 */
package clusteringAtributte;

import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Alain
 */
public class TypeDate extends TypeSet<Date>{
    public TypeDate(Date date){
        super(date);
    }
    public String toString(){
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        return " date '"+formatter.format(element)+"'";
    }
    @Override
    public double diff(TypeSet<Date> element){
        return getDateDiff(this.element, element.getElement(), TimeUnit.DAYS);
    }
    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date1.getTime() - date2.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
}
