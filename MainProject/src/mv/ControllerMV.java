/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mv;

import algorithms.mv.Agrawal;
import algorithms.mv.DefineView;
import bib.base.Base;
import static bib.base.Base.log;
import static bib.base.Base.prop;
import bib.driver.Driver;
import bib.sgbd.Captor;
import bib.sgbd.SQL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class ControllerMV extends Base {

    protected final Captor captor;
    protected Driver driver;

    public ControllerMV() {
        this.driver = new Driver();
        this.captor = new Captor();
    }

    public void persistDDLCreateMV(ArrayList<MaterializedView> MVCandiates) {
        if (!MVCandiates.isEmpty()) {
            log.title("Persist ddl create MV");
            for (MaterializedView mvQuery : MVCandiates) {
                if (!mvQuery.getHypoMaterializedView().isEmpty()) {
                    mvQuery.print();
                    if (mvQuery.getAnalyzeCount() == 0) {
                        this.insertDDLCreateMV(mvQuery);
                    } else {
                        this.updateDDLCreateMV(mvQuery);
                    }
                }
            }
            log.endTitle();
        }
    }

    private void insertDDLCreateMV(MaterializedView mvQuery) {
        try {
            String ddlCreateMV = mvQuery.getDDLCreateMV();
            String[] queries = prop.getProperty("getSqlClauseToInsertDDLCreateMV").split(";");
            PreparedStatement preparedStatementInsert = driver.prepareStatement(queries[0]);
            log.msg(prop.getProperty("getSqlClauseToInsertDDLCreateMV"));
            preparedStatementInsert.setInt(1, mvQuery.getId());
            preparedStatementInsert.setString(2, ddlCreateMV);
            preparedStatementInsert.setLong(3, mvQuery.getHypoCreationCost());
            preparedStatementInsert.setDouble(4, mvQuery.getHypoGainAC());
            preparedStatementInsert.setString(5, "H");
            mvQuery.setAnalyzeCount(1);
            driver.executeUpdate(preparedStatementInsert);
            PreparedStatement preparedStatementUpdate = driver.prepareStatement(queries[1]);
            preparedStatementUpdate.setInt(1, mvQuery.getId());
            driver.executeUpdate(preparedStatementUpdate);
        } catch (SQLException ex) {
            log.error(ex);
        }

    }

    private void updateDDLCreateMV(MaterializedView mvQuery) {
        try {
            PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToIncrementBenefictDDLCreateMV"));
            log.msg(prop.getProperty("getSqlClauseToIncrementBenefictDDLCreateMV"));
            preparedStatement.setLong(1, mvQuery.getHypoCreationCost());
            preparedStatement.setDouble(2, mvQuery.getHypoGainAC());
            preparedStatement.setInt(3, mvQuery.getId());
            driver.executeUpdate(preparedStatement);
        } catch (SQLException ex) {

            log.error(ex);
        }
    }

    public ArrayList<MaterializedView> getPlanDDLViews(ArrayList<MaterializedView> MVCandiates) {
        for (MaterializedView MVCandiate : MVCandiates) {
            if (MVCandiate.getType().equals("Q")) {
                MVCandiate.setHypoPlan(captor.getPlanExecution(MVCandiate.getHypoMaterializedView()));
            }
        }
        return MVCandiates;
    }

    public ArrayList<MaterializedView> getQueriesNotAnalizedForVMHypotetical(ArrayList<MaterializedView> listSQL) {
        for (int i = 0; i < listSQL.size(); i++) {
            if (listSQL.get(i).getType().equals("Q")) {
                listSQL.remove(i);
            }
        }
        return listSQL;
    }

    public void evaluateMV(ArrayList<SQL> sqlList) {
        ArrayList<MaterializedView> MVCandiates = new ArrayList<>();
        for (SQL sql : sqlList) {
            MaterializedView currentQuery = new MaterializedView();
            currentQuery.copy(sql);
            currentQuery.setSchemaDataBase(captor.getSchemaDataBase());
            MVCandiates.add(currentQuery);
        }
        DefineView defineView = new DefineView();
        Agrawal agrawal = new Agrawal();
        MVCandiates = this.getQueriesNotAnalizedForVMHypotetical(MVCandiates);
        MVCandiates = agrawal.getWorkloadSelected(MVCandiates);
        MVCandiates = defineView.getWorkloadSelected(MVCandiates);
        MVCandiates = this.getPlanDDLViews(MVCandiates);
        this.persistDDLCreateMV(MVCandiates);
    }

}
