/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package algorithms.mv;

import agents.ObserverMV;
import algorithms.Algorithms;
import static base.Base.log;
import base.MaterializedVision;
import base.SQL;
import java.util.ArrayList;
import java.math.BigInteger;

/**
 *
 * @author Rafael
 */
public class Agrawal extends Algorithms {

    private final ObserverMV observer;
    private ArrayList<MaterializedVision> capturedQueries;
    private final int treshold;
    private int maxTables;

    public Agrawal(ObserverMV observer) {
        this.observer = observer;
        this.treshold = Integer.parseInt(this.propertiesFile.getProperty("threshold"));
    }

    public ArrayList<MaterializedVision> getWorkloadSelected(ArrayList<MaterializedVision> capturedQueries) {
        try {
            this.capturedQueries = capturedQueries;
            int i = 1;
            ArrayList<SQL> tables = new ArrayList<>();
            ArrayList<ArrayList<SQL>> S = new ArrayList<>();
            ArrayList<SQL> G = this.getTableSubsetBySize(i, tables);
            S.add(G);
            while (i < this.maxTables && !G.isEmpty()) {
                i++;
                G = this.getTableSubsetBySize(i, S.get(i - 2));
                if (!G.isEmpty()) {
                    S.add(G);
                }
            }
        } catch (Exception e) {
            log.errorPrint(e, this.getClass().toString());
        }
        return this.capturedQueries;
    }

    public int TS_Weight(ArrayList<String> tables) {
        int num_tuples = 0;
        for (String table : tables) {
            num_tuples += observer.getTableLength(table);
        }
        return num_tuples;
    }

    public BigInteger TS_Cost(SQL query) {
        BigInteger temp = new BigInteger(query.getCapture_count().toString());
        
        temp = temp.multiply(query.getCost());
        return temp;
        
    }

    public ArrayList<SQL> getTableSubsetBySize(int size, ArrayList<SQL> tablesCheck) {
        ArrayList<SQL> tableSubset = new ArrayList<>();
        for (SQL workload : this.capturedQueries) {
            BigInteger temp = new BigInteger(String.valueOf(this.treshold));
            if ((workload.getTablesQuery().size() == size) && (this.TS_Cost(workload).compareTo( temp ) >= 0 )) {
                if (tablesCheck.isEmpty()) {
                    this.maxTables = workload.getSchemaDataBase().tables.size();
                    tableSubset.add(workload);
                } else {
                    for (SQL table : tablesCheck) {
                        if (workload.haveTableInTableQuery(table.getTablesQuery())) {
                            tableSubset.add(workload);
                        }
                    }

                }
            }
        }
        return tableSubset;
    }

}
