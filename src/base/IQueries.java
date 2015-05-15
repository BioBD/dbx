/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package base;

import drivers.Driver;

/**
 *
 * @author Rafael
 */
public interface IQueries {

    public String getSqlClauseToCaptureCurrentQueries(String database);

    public String getSqlClauseToUpdateQueryTbWorkload();

    public String getSqlClauseToInsertQueryTbWorkload();

    public String getSqlClauseToCheckIfQueryIsAlreadyCaptured();

    public String getSqlQueriesNotAnalized();

    public String getSqlTableNames(String database);

    public String getSqlTableLength();

    public String getSqlClauseToCreateMV(String query, String nameView);

    public String getSqlClauseToInsertDDLCreateMV();

    public String getSqlClauseToUpdateWldAnalyzeCount();

    public String getSqlClauseToIncrementBenefictDDLCreateMV();

    public String getSqlDDLNotAnalizedPredictor();

    public String getSqlClauseToUpdateDDLCreateMVToMaterialization(String value);

    public String getSqlClauseToGetDiskSpaceOccupied();

    public String getSqlDDLNotAnalizedReactor();

    public String getPlanExecution(Driver driver, String query);
}
