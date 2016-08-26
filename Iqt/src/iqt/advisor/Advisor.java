package iqt.advisor;

import br.com.iqt.exception.NegativeNumberException;
import br.com.iqt.zql.ParseException;
import iqt.AgentRewriter;
import iqt.Dbms;
import iqt.exception.DbmsException;
import iqt.exception.SqlInputException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arlino
 */
public class Advisor {

    private Dbms dbms;
    private boolean allSql = false, customSql = false;
    private String path = "";//path do diretório onde os arquivos com as recomendações de reescrita gerados.
    private long timeInterval = 60 * 1000;//Intevalo de tempo que o agente começar a executar as reescritas
    // no modo on-the-fly (em milissegundos)

    public Advisor(Dbms dbms) {
        this.dbms = dbms;
    }

    public Advisor(Dbms dbms, boolean allSql, boolean customSql) {
        this.dbms = dbms;
        this.allSql = allSql;
        this.customSql = customSql;
    }

    public void setPath(String path) {
        if (path != null && !path.equals("")) {
            path = path.trim();
            if (path.charAt(path.length() - 1) != '/') {
                path += "/";
            }

            this.path = path;
        }
    }

    public void start(long timeInterval) throws InterruptedException, DbmsException, ClassNotFoundException, SQLException, ParseException, SqlInputException, IOException, NegativeNumberException {
        if (timeInterval <= 0) {
            throw new NegativeNumberException();
        }
        this.timeInterval = timeInterval;
        start();
    }

    public void start() throws InterruptedException, DbmsException, ClassNotFoundException, SQLException, ParseException, SqlInputException, IOException {
        if (dbms == null) {
            throw new DbmsException();
        }

        //AgentWorkLoad awl = new AgentWorkLoad(this.dbms);
        AgentRewriter ar = new AgentRewriter(this.dbms);
        ar.setInteractivity(allSql, customSql);

        while (true) {
            Thread.sleep(this.timeInterval);

            String currentDateTime = GregorianCalendar.getInstance().getTime().toString();
            String fileOutName = path + "RewriteReport_" + currentDateTime.replace(" ", "_").replace(':', '.') + ".txt";
            //System.out.println(fileOutName);

            FileWriter out = new FileWriter(fileOutName, false);
            out.write("RECOMENDAÇÕES DE REESCITA GERADAS EM " + currentDateTime.toString() + "\r\n\r\n\r\n");
            //  ArrayList workLoad = awl.getWorkLoad();

            int rewriteCounter = 0;
            /*  for (Object obj : workLoad) {
                String query = obj.toString();

                String newQuery = ar.analyseStatement(query);

                if(ar.isRewrited()){
                    String lineOut = "SQL Original:\r\n" + query;
                    lineOut += "SQL Reescrita:\r\n" + newQuery + "\r\n\r\n";
                    out.write(lineOut);
                    //System.out.println("reescrita=" + newQuery);
                    rewriteCounter++;
                }
            }*/
            //System.out.println("OK");
            out.write("Total de rescritas realizadas = " + rewriteCounter + "\r\n\r\n\r\n");
            out.close();
        }
    }

    public static void main(String args[]) {

        Dbms d = new Dbms(Dbms.POSTGRESQL, "localhost:5432", "sigaa", "postgres", "123456");
        Advisor ar = new Advisor(d);
        try {
            ar.start();
        } catch (InterruptedException ex) {
            Logger.getLogger(AgentRewriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DbmsException ex) {
            Logger.getLogger(AgentRewriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AgentRewriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(AgentRewriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Advisor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SqlInputException ex) {
            Logger.getLogger(Advisor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Advisor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
