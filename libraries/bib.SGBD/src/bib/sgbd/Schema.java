/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
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
