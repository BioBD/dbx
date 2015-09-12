/*
 * Biblioteca de codigo fonte criada por Rafael Pereira
 * Proibido o uso sem autorizacao formal do autor
 *
 * rpoliveirati@gmail.com
 */
package bib.sgbd;

import java.util.ArrayList;

/**
 *
 * @author josemariamonteiro
 */
public class SeqScan {

    public SeqScan(String name, ArrayList<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    public String name;
    public ArrayList<Column> columns;
    
    
}
