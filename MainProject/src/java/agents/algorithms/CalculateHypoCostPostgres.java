/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents.algorithms;

public class CalculateHypoCostPostgres extends Algorithm {

    public long calculateHypoCostPostgres(double numPages, long numRows) {
        double temp = numPages;
        long tempRows = numRows;
        long seq_page_cost = Long.valueOf(config.getProperty("seq_page_cost"));
        long cpu_tuple_cost = Long.valueOf(config.getProperty("cpu_tuple_cost"));
        temp = temp * seq_page_cost; /* numPages*seq_page_cost */

        tempRows = tempRows * cpu_tuple_cost; /* numRows*cpu_tuple_cost */

        return (long) (temp + tempRows); /* numPages*seq_page_cost + numRows*cpu_tuple_cost */

    }
}
