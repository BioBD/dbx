package iqt;

/**
 *
 * @author Arlino
 */
public class Dbms {
    private int dbms;
    private String url;
    private String database;
    private String user;
    private String password;
    
    /**
     * Constantes que definem qual SGBD será instanciado. O campo dbms receberá
     * uma das constantes abaixo.
     */
    public final static int POSTGRESQL = 1;
    public final static int SQLSERVER = 3;
    public final static int ORACLE = 4;

    /**
     * Retorna o nome do banco de dados.
     * @return
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Retorna o ID do DBMS instanciado. Existem as seguintes constantes definidas para
     * esse campo: Dbms.(POSTGRESQL | SQLSERVER | ORACLE).
     * @return
     */
    public int getDbms() {
        return dbms;
    }

    /**
     * Retorna a senha do usuário do SGBD.
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Retorna a porta de acesso ao SGBD.
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * Retorna o usuário de acesso ao SGBD.
     * @return
     */
    public String getUser() {
        return user;
    }

    /**
     * Instancia o objeto com as informações de acesso ao SGBD.
     * @param dbms
     * ID do DBMS instanciado. Existem as seguintes constantes definidas para
     * esse campo: Dbms.(POSTGRESQL | SQLSERVER | ORACLE).
     * @param server
     * Endereço do servidor do SGBD.
     * @param port
     * Porta de acesso ao SGBD.
     * @param database
     * Nome do banco de dados.
     * @param user
     * Usuário de acesso ao SGBD.
     * @param password
     * Senha do usuário de acesso ao SGBD.
     */
    public Dbms(int dbms, String url, String database, String user, String password) {
        this.dbms = dbms;
        this.url = url;
        this.database = database;
        this.user = user;
        this.password = password;
    }
}
