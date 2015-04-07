/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package base;

/**
 *
 * @author Rafael
 */
public interface IQueries {

    public String getSqlClauseToGetThePlan(String query);

    public String getSqlClauseToCaptureCurrentQueries(String database);

    public String getSqlClauseToUpdateQueryTbWorkload();

    public String getSqlClauseToInsertQueryTbWorkload();

    public String getSqlClauseToCheckIfQueryIsAlreadyCaptured();

    public String getSqlQueriesNotAnalized();

    public String getSqlTableNames();

    public String getSqlTableLength();

    public String getSqlClauseToCreateMV(String query, String nameView);

    public String getSqlClauseToInsertDDLCreateMV();

    public String getSqlTableFields();

    public String getSqlClauseToUpdateWldAnalyzeCount();

    public String getSqlClauseToIncrementBenefictDDLCreateMV();

    public String getSqlDDLNotAnalizedPredictor();

    public String getSqlClauseToUpdateDDLCreateMVToMaterialization(String value);

    public String getSqlClauseToGetDiskSpaceOccupied();

    public String getSqlDDLNotAnalizedReactor();

}
