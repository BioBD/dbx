/*
 * Automatic Create Materialized Views
 *    *
 */
package agents.interfaces;

public interface IObserverMV {

    public void run();

    public void captureQueries();

    public int getIdWorkload(String query);

    public boolean isQueryValid(String query);

    public boolean isQueryGeneratedBySGBD(String query);

    public void getLastExecutedQueries();

    public boolean isQueryAlreadyCaptured(String query);

    public boolean isQueryGeneratedByACMV(String query);

    public void updateQueryData(int queryId, String query);

    public void insertQueryTbWorkload(String query);

    public void insertWorkload();

    public void processQueries();

    public String getTypeQuery(String query);

    public void getQueriesNotAnalized();

    public void executeAgrawal();

    public void executeDefineView();

    public int getTableLength(String tableName);
}
