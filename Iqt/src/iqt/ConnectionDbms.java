package iqt;

import iqt.Dbms;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Arlino 
 */

public class ConnectionDbms{
    public static String message = "";
    
    private static final String driverPostgresql = "org.postgresql.Driver";
    private static final String driverSqlServer = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String driverOracle = "oracle.jdbc.driver.OracleDriver";
    
    public static String getDatabaseName(int dbms){
        switch(dbms){
            case Dbms.POSTGRESQL:
                return "PostgreSQL";
            case Dbms.SQLSERVER:
                return "SQL Server";
            case Dbms.ORACLE:
                return "Oracle";
        }
        return null;
    }
    
    public static boolean isIndex(Dbms dbms, String scheme, String table, String column){
        String driver = "";
        String url = "";
        String sql = "";
        
        switch(dbms.getDbms()){
            case Dbms.POSTGRESQL:
                driver = ConnectionDbms.driverPostgresql;
                url = dbms.getUrl() + dbms.getDatabase();
                sql = "select * from pg_index where indrelid = (select oid from pg_class where upper(relname)= upper('" + table
                        + "') and relkind = 'r' and relnamespace = (select oid from pg_namespace where UPPER(nspname) = "
                        + "UPPER('" + scheme + "'))) and indkey[0] = (select attnum from pg_attribute where upper(attname) = upper('" + column
                        + "') and attrelid = (select oid from pg_class where upper(relname) = upper('" + table + "') and relkind = 'r' "
                        + "and relnamespace = (select oid from pg_namespace where UPPER(nspname) = UPPER('" + scheme + "'))))";
                break;
            case Dbms.SQLSERVER:
                driver = ConnectionDbms.driverSqlServer;
                url = dbms.getUrl() +"database=" + dbms.getDatabase();
                /*
                 * Tentaiva de fazer sql para procurar existencia de indice de uma coluna em uma tabela SQL Server
                 * 
                 select tpch.sys.columns.name,tpch.sys.indexes.name, tpch.sys.tables.name
                 from tpch.sys.columns, tpch.sys.indexes, tpch.sys.tables
                 where tpch.sys.columns.name='c_custkey' and tpch.sys.tables.name = 'customer' and 
                 tpch.sys.indexes.is_disabled = 0 and tpch.sys.indexes.index_id <> 0 and
                 tpch.sys.columns.object_id = tpch.sys.indexes.object_id 
                 and tpch.sys.tables.object_id = tpch.sys.indexes.object_id and 
                 tpch.sys.columns.object_id=tpch.sys.tables.object_id
                 
                sql = "select databaseName.sys.columns.name,databaseName.sys.indexes.name, databaseName.sys.tables.name "
                        + "from databaseName.sys.columns, databaseName.sys.indexes, databaseName.sys.tables where "
                        + "databaseName.sys.columns.name='" + column + "' and databaseName.sys.tables.name = '" 
                        + table + "' and databaseName.sys.indexes.is_disabled = 0 and databaseName.sys.indexes.index_id "
                        + "<> 0 and databaseName.sys.columns.object_id = databaseName.sys.indexes.object_id and "
                        + "databaseName.sys.tables.object_id = databaseName.sys.indexes.object_id and "
                        + "databaseName.sys.columns.object_id=databaseName.sys.tables.object_id";
                sql = sql.replace("databaseName", dbms.getDatabase());
                 * 
                 */
                System.out.println("Procura por coluna com índice em uma determinada tabela ainda não foi implementado para esse SGBD! Classe ConnectionDbms.isIndex()");
                return false;
                //break;
            case Dbms.ORACLE:
                driver = ConnectionDbms.driverOracle;
                url = dbms.getUrl() + "orcl";
                System.out.println("Procura por coluna com índice em uma determinada tabela ainda não foi implementado para esse SGBD! Classe ConnectionDbms.isIndex()");
                return false;
                //break;
        }
        
        try{
            Class.forName(driver);
            Connection connection = (Connection) DriverManager.getConnection(url, dbms.getUser(), dbms.getPassword());
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            boolean value = rs.next();
            stm.close();
            connection.close();
            return value;
        }
        catch (ClassNotFoundException ex){
            System.out.println("(ClassNotFoundException) Erro ao procurar índice. Classe ConectionDbms.isIndex(). Erro: " + ex.getMessage());
            return false;
        }
        catch (SQLException e){
            System.out.println("(SQLException) Erro ao procurar índice. Classe ConectionDbms.isIndex(). Erro: " + e.getMessage());
            System.out.println("Sql = " + sql);
            return false;
        }
    }
    
    public static boolean isKey(Dbms dbms, String table, String column){
        String driver = "";
        String url = "";
        String sql = "";
        
        switch(dbms.getDbms()){
            case Dbms.POSTGRESQL:
                driver = ConnectionDbms.driverPostgresql;
                url = dbms.getUrl() + dbms.getDatabase();
                sql = "select * from pg_constraint where contype='p' and conrelid = (select oid from pg_class where "
                        + "relname= '" + table + "') and conkey[1] = (select attnum from pg_attribute where attname = "
                        + "'" + column + "' and attrelid = (select oid from pg_class where relname= '" + table + "'))";
                break;
            case Dbms.SQLSERVER:
                driver = ConnectionDbms.driverSqlServer;
                url = dbms.getUrl() +"database=" + dbms.getDatabase();
                System.out.println("Procura por coluna com índice em uma determinada tabela ainda não foi implementado para esse SGBD! Classe ConnectionDbms.isIndex()");
                return false;
                //break;
            case Dbms.ORACLE:
                driver = ConnectionDbms.driverOracle;
                url = dbms.getUrl() + "orcl";
                System.out.println("Procura por coluna com índice em uma determinada tabela ainda não foi implementado para esse SGBD! Classe ConnectionDbms.isIndex()");
                return false;
                //break;
        }
        
        try{
            Class.forName(driver);
            Connection connection = (Connection) DriverManager.getConnection(url, dbms.getUser(), dbms.getPassword());
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            boolean value = rs.next();
            stm.close();
            connection.close();
            return value;
        }
        catch (ClassNotFoundException ex){
            System.out.println("(ClassNotFoundException) Erro ao procurar índice. Classe ConectionDbms.isIndex(). Erro: " + ex.getMessage());
            return false;
        }
        catch (SQLException e){
            System.out.println("(SQLException) Erro ao procurar índice. Classe ConectionDbms.isIndex(). Erro: " + e.getMessage());
            return false;
        }
    }
    
    public static String getRuntime(Dbms dbms, String sql){
        String driver = "";
        String url = "";
        
        switch(dbms.getDbms()){
            case Dbms.POSTGRESQL:
                driver = ConnectionDbms.driverPostgresql;
                url = dbms.getUrl() + dbms.getDatabase();
                break;
            case Dbms.SQLSERVER:
                driver = ConnectionDbms.driverSqlServer;
                url = dbms.getUrl() +"database=" + dbms.getDatabase();
                break;
            case Dbms.ORACLE:
                driver = ConnectionDbms.driverOracle;
                url = dbms.getUrl() + "orcl";
                break;
        }
        
        try{
            Class.forName(driver);
            Connection connection = (Connection) DriverManager.getConnection(url, dbms.getUser(), dbms.getPassword());
            Statement stm = connection.createStatement();
            long startTime = System.currentTimeMillis();
            stm.executeQuery(sql);
            long endTime = System.currentTimeMillis();
            long FinalTime = endTime - startTime;
            stm.close();
            connection.close();
            return (Long.toString(FinalTime) + " ms");
        }
        catch (ClassNotFoundException ex){
            return "Error: " + ex.getMessage();
        }
        catch (SQLException e){
            return "Error: " + e.getMessage();
        }
    }
    
    public static ArrayList getExecutionPlan(Dbms dbms, String sql){
        String driver, url;
        Connection connection;
        Statement statement;
        ResultSet resultSet;
        ArrayList list = new ArrayList();
        
        try{
            switch(dbms.getDbms()){
                case Dbms.POSTGRESQL:
                    driver = ConnectionDbms.driverPostgresql;
                    Class.forName(driver);
                    url = dbms.getUrl() + dbms.getDatabase();
                    sql = "EXPLAIN " + sql;
                    
                    connection = (Connection) DriverManager.getConnection(url, dbms.getUser(), dbms.getPassword());
                    statement = connection.createStatement();
                    
                    resultSet = statement.executeQuery(sql);
                    while (resultSet.next()) {
                        list.add(resultSet.getString(1));
                    }
                    
                    connection.close();
                    return list;
                case Dbms.SQLSERVER:
                    driver = ConnectionDbms.driverSqlServer;
                    Class.forName(driver);
                    url = dbms.getUrl() +"database=" + dbms.getDatabase();
                    
                    connection = (Connection) DriverManager.getConnection(url, dbms.getUser(), dbms.getPassword());
                    statement = connection.createStatement();
                    
                    statement.execute("SET SHOWPLAN_TEXT OFF");
                    statement.execute("SET SHOWPLAN_TEXT ON");
                    
                    resultSet = statement.executeQuery(sql);
                    while (resultSet.next()) {
                        list.add(resultSet.getString(1));
                    }
                    
                    statement.execute("SET SHOWPLAN_TEXT OFF");
                    connection.close();
                    return list;
                case Dbms.ORACLE:
                    driver = ConnectionDbms.driverOracle;
                    Class.forName(driver);
                    url = dbms.getUrl() + "orcl";
                    sql = "select operation, id from v$sql_plan sp, v$sql s where "
                            + "sp.sql_id = s.sql_id and sql_text = '" + sql + "'";
                    
                    connection = (Connection) DriverManager.getConnection(url, dbms.getUser(), dbms.getPassword());
                    statement = connection.createStatement();
                    
                    statement.execute("SET SHOWPLAN_TEXT OFF");
                    statement.execute("SET SHOWPLAN_TEXT ON");
                    
                    resultSet = statement.executeQuery(sql);
                    
                    while (resultSet.next()) {
                        list.add(resultSet.getString("operation"));
                    }
                    
                    connection.close();
                    return list;
            }
        }
        catch (ClassNotFoundException ex){
            ConnectionDbms.message = "Falha na conexão " + ConnectionDbms.getDatabaseName(dbms.getDbms())
                    + ". Erro: " + ex.getMessage();
            return null;
        }
        catch (SQLException e){
            ConnectionDbms.message = "Falha na conexão " + ConnectionDbms.getDatabaseName(dbms.getDbms())
                    + ". Erro: " + e.getMessage();
            return null;
        }
        return null;
    }
    
    public static boolean testConnection(Dbms dbms){
        String driver = "";
        String url = "";
        
        switch(dbms.getDbms()){
            case Dbms.POSTGRESQL:
                driver = ConnectionDbms.driverPostgresql;
                url = dbms.getUrl() + dbms.getDatabase();
                break;
            case Dbms.SQLSERVER:
                driver = ConnectionDbms.driverSqlServer;
                url = dbms.getUrl() +"database=" + dbms.getDatabase();
                break;
            case Dbms.ORACLE:
                driver = ConnectionDbms.driverOracle;
                url = dbms.getUrl() + "orcl";
                break;
        }
        
        try{
            Class.forName(driver);
            Connection connection = (Connection) DriverManager.getConnection(url, dbms.getUser(), dbms.getPassword());
            connection.close();
            return true;
        }
        catch (ClassNotFoundException ex){
            ConnectionDbms.message = "Falha na conexão " + ConnectionDbms.getDatabaseName(dbms.getDbms()) 
                                    + ". Erro: " + ex.getMessage();
            return false;
        }
        catch (SQLException e){
            ConnectionDbms.message = "Falha na conexão " + ConnectionDbms.getDatabaseName(dbms.getDbms()) 
                                    + ". Erro: " + e.getMessage();
            return false;
        }
    }
    
    public static ResultSet executeQuery(Dbms dbms, String sql) throws ClassNotFoundException, SQLException{
        String driver = "";
        String url = "";
        
        switch(dbms.getDbms()){
            case Dbms.POSTGRESQL:
                driver = ConnectionDbms.driverPostgresql;
                url = dbms.getUrl() + dbms.getDatabase();
                break;
            case Dbms.SQLSERVER:
                driver = ConnectionDbms.driverSqlServer;
                url = dbms.getUrl() +"database=" + dbms.getDatabase();
                break;
            case Dbms.ORACLE:
                driver = ConnectionDbms.driverOracle;
                url = dbms.getUrl() + "orcl";
                break;
        }
        
        Class.forName(driver);
        Connection connection = (Connection) DriverManager.getConnection(url, dbms.getUser(), dbms.getPassword());
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        connection.close();
        return resultSet;
    }
    
    public static ResultSet executeQuery(Connection connection , String sql) throws SQLException{
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        return resultSet;
    }
    
    public static int executeUpdate(Dbms dbms, String sql) throws ClassNotFoundException, SQLException{
        String driver = "";
        String url = "";
        
        switch(dbms.getDbms()){
            case Dbms.POSTGRESQL:
                driver = ConnectionDbms.driverPostgresql;
                url = dbms.getUrl() + dbms.getDatabase();
                break;
            case Dbms.SQLSERVER:
                driver = ConnectionDbms.driverSqlServer;
                url = dbms.getUrl() +"database=" + dbms.getDatabase();
                break;
            case Dbms.ORACLE:
                driver = ConnectionDbms.driverOracle;
                url = dbms.getUrl() + "orcl";
                break;
        }
        
        Class.forName(driver);
        Connection connection = (Connection) DriverManager.getConnection(url, dbms.getUser(), dbms.getPassword());
        Statement statement = connection.createStatement();
        int rowCount = statement.executeUpdate(sql);
        connection.close();
        return rowCount;
    }
    
    public static int executeUpdate(Connection connection, String sql) throws SQLException{
        Statement statement = connection.createStatement();
        int rowCount = statement.executeUpdate(sql);
        return rowCount;
    }
   
   /* public static void main(String[] args)
    {
        
        System.out.println(ConnectionDbms.isKey(new Dbms(Dbms.POSTGRESQL, "localhost", "5432", "sigaa", "postgres", "123456"), "curriculo", "id_curriculo"));
        //System.out.println(ConnectionDbms.isIndex(new Dbms(Dbms.POSTGRESQL, "localhost", "5432", "sqs", "postgres", "123456"), "query", "text"));
        //System.out.println(ConnectionDbms.getRuntime(new Dbms(Dbms.POSTGRESQL, "localhost", "5432", "tpch", "postgres", "123456"), "select * from customer"));
        //System.out.println(ConnectionDbms.getRuntime(HeuristicSet.SQLSERVER, "select * from customer", "localhost", "1433", "tpch", "sa", "123456"));
        
    }*/
}  