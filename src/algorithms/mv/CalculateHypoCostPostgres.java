/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  * 
 */
package algorithms.mv;

import algorithms.Algorithms;
import base.MaterializedVision;
import java.math.BigInteger;
import java.math.BigDecimal;

public class CalculateHypoCostPostgres extends Algorithms {
    public BigInteger calculateHypoCostPostgres ( MaterializedVision view, BigInteger numPages , BigInteger numRows ) {
        BigDecimal temp = new BigDecimal(numPages);
        BigDecimal tempRows = new BigDecimal(numRows);
        BigDecimal seq_page_cost = new BigDecimal(view.propertiesFile.getProperty("seq_page_cost"));
        BigDecimal cpu_tuple_cost = new BigDecimal(view.propertiesFile.getProperty("cpu_tuple_cost"));
        
        temp = temp.multiply(seq_page_cost); /* numPages*seq_page_cost */
        tempRows = tempRows.multiply(cpu_tuple_cost); /* numRows*cpu_tuple_cost */
        return temp.add(tempRows).toBigInteger(); /* numPages*seq_page_cost + numRows*cpu_tuple_cost */      
        
    }
}
           
