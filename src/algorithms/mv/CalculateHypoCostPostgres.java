/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package algorithms.mv;

import algorithms.Algorithms;
import base.MaterializedView;

public class CalculateHypoCostPostgres extends Algorithms {

    public long calculateHypoCostPostgres(MaterializedView view, double numPages, long numRows) {
        double temp = numPages;
        long tempRows = numRows;
        long seq_page_cost = Long.valueOf(view.propertiesFile.getProperty("seq_page_cost"));
        long cpu_tuple_cost = Long.valueOf(view.propertiesFile.getProperty("cpu_tuple_cost"));

        temp = temp * seq_page_cost; /* numPages*seq_page_cost */

        tempRows = tempRows * cpu_tuple_cost; /* numRows*cpu_tuple_cost */

        return (long) (temp + tempRows); /* numPages*seq_page_cost + numRows*cpu_tuple_cost */

    }
}
