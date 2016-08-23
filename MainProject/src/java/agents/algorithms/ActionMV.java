/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents.algorithms;

import agents.libraries.Configuration;
import agents.libraries.ConnectionSGBD;
import agents.libraries.Log;
import agents.sgbd.Captor;
import agents.sgbd.MaterializedView;
import agents.sgbd.SQL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author Rafael
 */
public class ActionMV {

    public final Properties config;
    public final Log log;
    public final ConnectionSGBD connection;
    protected final Captor captor;

    public ActionMV() {
        this.config = Configuration.getProperties();
        this.log = new Log(this.config);
        this.connection = new ConnectionSGBD();
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
                this.insertMaterializedViewTask(mvQuery);
            }
            log.endTitle();
        }
    }

    private void insertDDLCreateMV(MaterializedView mvQuery) {
        try {
            String[] queries = config.getProperty("getSqlClauseToInsertDDLCreateMV").split(";");
            if (getIdCandidateView(mvQuery.getDDLCreateMV()) == 0) {
                PreparedStatement preparedStatementInsert = connection.prepareStatement(queries[0]);
                log.msg(config.getProperty("getSqlClauseToInsertDDLCreateMV"));
                preparedStatementInsert.setString(1, mvQuery.getDDLCreateMV());
                preparedStatementInsert.setLong(2, mvQuery.getHypoCreationCost());
                preparedStatementInsert.setLong(3, mvQuery.getHypoGainAC());
                preparedStatementInsert.setString(4, "H");
                mvQuery.setAnalyzeCount(1);
                connection.executeUpdate(preparedStatementInsert);
            }

            PreparedStatement preparedStatementUpdate = connection.prepareStatement(queries[1]);
            preparedStatementUpdate.setInt(1, mvQuery.getId());
            connection.executeUpdate(preparedStatementUpdate);
        } catch (SQLException ex) {
            log.error(ex);
        }

    }

    private void updateDDLCreateMV(MaterializedView mvQuery) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(config.getProperty("getSqlClauseToIncrementBenefictDDLCreateMV"));
            log.msg(config.getProperty("getSqlClauseToIncrementBenefictDDLCreateMV"));
            preparedStatement.setLong(1, mvQuery.getHypoCreationCost());
            preparedStatement.setLong(2, mvQuery.getHypoGainAC());
            preparedStatement.setInt(3, mvQuery.getId());
            connection.executeUpdate(preparedStatement);
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
            if (!listSQL.get(i).getType().equals("Q")) {
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

    private void insertMaterializedViewTask(MaterializedView mvQuery) {
        int idMV = this.getIdCandidateView(mvQuery.getDDLCreateMV());
        if (idMV != 0 && !existTaskViewFromThisWorkload(mvQuery.getId(), idMV)) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(config.getProperty("getSqlInsertTbTaskViews"));
                log.msg(config.getProperty("getSqlInsertTbTaskViews"));
                preparedStatement.setInt(1, idMV);
                preparedStatement.setInt(2, mvQuery.getId());
                connection.executeUpdate(preparedStatement);
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
    }

    private int getIdCandidateView(String hypoMaterializedView) {
        try {
            log.msg(config.getProperty("getSqlSelectIdFromTbCandiateView"));
            PreparedStatement preparedStatement = connection.prepareStatement(config.getProperty("getSqlSelectIdFromTbCandiateView"));
            preparedStatement.setString(1, hypoMaterializedView);
            ResultSet resultIdWorkload = connection.executeQuery(preparedStatement);
            if (resultIdWorkload.next()) {
                return resultIdWorkload.getInt("cmv_id");
            }
        } catch (SQLException ex) {
            log.error(ex);
        }
        return 0;
    }

    private boolean existTaskViewFromThisWorkload(int idWld, int idMV) {
        try {
            log.msg(config.getProperty("getSqlSelectTbTaskView"));
            PreparedStatement preparedStatement = connection.prepareStatement(config.getProperty("getSqlSelectTbTaskView"));
            preparedStatement.setInt(1, idMV);
            preparedStatement.setInt(2, idWld);
            ResultSet resultIdWorkload = connection.executeQuery(preparedStatement);
            if (resultIdWorkload.next()) {
                return true;
            }
        } catch (SQLException ex) {
            log.error(ex);
        }
        return false;
    }

}
