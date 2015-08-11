/*
 * Automatic Creation Materialized Views
 *    *
 */
package bib.sgbd;

import bib.base.Base;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class Schema extends Base {

    public ArrayList<Table> tables;

    public Schema() {
        this.tables = new ArrayList<>();

    }

}
