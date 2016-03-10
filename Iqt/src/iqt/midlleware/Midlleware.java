package iqt.midlleware;

import iqt.*;
import iqt.exception.SqlInputException;
import br.com.iqt.zql.ParseException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * 
 * @author Arlino
 */
public class Midlleware {
    private Dbms dbms;
    private AgentRewriter ar;
    private HeuristicsSelected heuristicsSelect = null;
    private boolean allSql = false, customSql = false;
    
    /**
     * Instancia um objeto Midlleware com um objeto DBMS.
     * @param dbms
     * Possui as informações de acesso ao SGBD.
     */
    public Midlleware(Dbms dbms) {
        this.dbms = dbms;
        this.ar = new AgentRewriter(dbms);
    }
    
    /**
     * Instancia um objeto Midlleware com um objeto DBMS e um HeuristicsSelected.
     * @param dbms
     * Possui as informações de acesso ao SGBD.
     * @param heuristicsSelect
     * Informa quais heurísticas devem ser utilizadas na reescrita de consultas.
     */
    public Midlleware(Dbms dbms, HeuristicsSelected heuristicsSelect) {
        this.dbms = dbms;
        this.heuristicsSelect = heuristicsSelect;
        this.ar = new AgentRewriter(dbms);
        this.ar.getTransformationList();
    }
    
    /**
     * Instancia um objeto Midlleware com um objeto DBMS e um HeuristicsSelected.
     * @param dbms
     * Possui as informações de acesso ao SGBD.
     * @param allSql
     * 
     * @param customSql
     */
    public Midlleware(Dbms dbms, boolean allSql,boolean customSql) {
        this.dbms = dbms;
        this.ar = new AgentRewriter(dbms);
        this.ar.setInteractivity(allSql, customSql);
        this.ar.getTransformationList();
    }
    
    /**
     * 
     * @param dbms
     * @param interactivity
     */
    public Midlleware(Dbms dbms, boolean interactivity) {
        this.dbms = dbms;
        this.ar = new AgentRewriter(dbms);
        this.ar.setInteractivity(allSql, customSql);
        this.ar.getTransformationList();
    }
    
    /**
     * 
     * @param sql
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws ParseException
     * @throws SqlInputException
     */
    public ResultSet executeQuery(String sql) throws ClassNotFoundException, SQLException, ParseException, SqlInputException{
        sql = this.ar.analyseStatement(sql, heuristicsSelect);
        ResultSet resultSet = ConnectionDbms.executeQuery(dbms, sql);
        return resultSet;
    }
    
    /**
     * 
     * @param sql
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws ParseException
     * @throws SqlInputException
     */
    public int executeUpdate(String sql) throws ClassNotFoundException, SQLException, ParseException, SqlInputException{
        sql = this.ar.analyseStatement(sql, heuristicsSelect);
        int rowCount = ConnectionDbms.executeUpdate(dbms, sql);
        return rowCount;
    }
    
    /**
     * 
     * @return
     */
    public boolean isRewrited(){
        return this.ar.isRewrited();
    }
    
    /**
     * 
     * @return
     */
    public boolean isError(){
        return this.ar.isError();
    }
    
    /**
     * 
     * @return
     */
    public int getCount(){
        return this.ar.getCount();
    }
    
    /**
     * 
     * @return
     */
    public double getTimeMillis(){
        return this.ar.getTimeMillis();
    }
    
    /**
     * 
     * @return
     */
    public double getTimeNano(){
        return this.ar.getTimeNano();
    }
    
    /**
     * 
     * @return
     */
    public double getTimeSecond(){
        return this.ar.getTimeSecond();
    }
    
    /**
     * 
     * @return
     */
    public Collection getSqlsTransformationList(){
        return this.ar.getSqlsTransformationList();
    }
    
    /**
     * 
     * @return
     */
    public Collection<Transformation> getTransformationList(){
        return this.ar.getTransformationList();
    }
}
