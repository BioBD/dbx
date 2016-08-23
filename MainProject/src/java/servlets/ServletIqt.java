/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio    &&    DC - UFC  *
 */
package servlets;

import agents.libraries.Configuration;
import agents.sgbd.Captor;
import agents.sgbd.SQL;
import br.com.iqt.zql.ParseException;
import iqt.AgentRewriter;
import iqt.ConnectionDbms;
import iqt.Dbms;
import iqt.HeuristicsSelected;
import iqt.exception.SqlInputException;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.DiskFileUpload;

@WebServlet(name = "ServletIqt", urlPatterns = {"/ServletIqt"})
public class ServletIqt extends HttpServlet {

    private AgentRewriter agentRewriter;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        Properties config = Configuration.getProperties();
        Dbms dbms = new Dbms(Dbms.POSTGRESQL, config.getProperty("urlPostgres"), config.getProperty("databaseName"), config.getProperty("userPostgres"), config.getProperty("pwdPostgres"));
        agentRewriter = new AgentRewriter(Dbms.POSTGRESQL);

        String executar = request.getParameter("executar");
        String reescrever = request.getParameter("reescrever");
        String plano = request.getParameter("plano");
        String file = request.getParameter("file");
        String workload = request.getParameter("workload");
        Captor captor = new Captor();

        if (executar != null) {
            float time = captor.getTimeDurationFromSQL(executar);
            out.println("Time: " + time);

        } else if (reescrever != null) {
            boolean allToSubquery = Boolean.parseBoolean(request.getParameter("allToSubquery"));
            boolean temporaryTable = Boolean.parseBoolean(request.getParameter("temporaryTable"));
            boolean someToSubquery = Boolean.parseBoolean(request.getParameter("someToSubquery"));
            boolean moveFunction = Boolean.parseBoolean(request.getParameter("moveFunction"));
            boolean removeGroupby = Boolean.parseBoolean(request.getParameter("removeGroupby"));
            boolean removeDistinct = Boolean.parseBoolean(request.getParameter("removeDistinct"));
            boolean anyToSubquery = Boolean.parseBoolean(request.getParameter("anyToSubquery"));
            boolean atithmetcExpression = Boolean.parseBoolean(request.getParameter("atithmetcExpression"));
            boolean havingToWhere = Boolean.parseBoolean(request.getParameter("havingToWhere"));
            boolean inToJoin = Boolean.parseBoolean(request.getParameter("inToJoin"));
            boolean orToUnion = Boolean.parseBoolean(request.getParameter("orToUnion"));

            HeuristicsSelected heuristicsSelect = new HeuristicsSelected();

            String sqlOut, message = null;

            heuristicsSelect.setAllToSubquerySelected(allToSubquery);
            heuristicsSelect.setAnyToSubquerySelected(anyToSubquery);
            heuristicsSelect.setHavingToWhereSelected(havingToWhere);
            heuristicsSelect.setInToJoinSelected(inToJoin);
            heuristicsSelect.setMoveAtithmetcExpressionSelected(atithmetcExpression);
            heuristicsSelect.setMoveFunctionSelected(moveFunction);
            heuristicsSelect.setOrToUnionSelected(orToUnion);
            heuristicsSelect.setRemoveDistinctSelected(removeDistinct);
            heuristicsSelect.setRemoveGroupbySelected(removeGroupby);
            heuristicsSelect.setSomeToSubquerySelected(someToSubquery);
            heuristicsSelect.setTemporaryTableToSubQuerySelected(temporaryTable);

            try {
                sqlOut = agentRewriter.analyseStatement(reescrever, heuristicsSelect);
                out.println("<textarea id=\"form-main-query-result-body\" class=\"form-control\" name=\"txt-query-result\">" + sqlOut + "</textarea>");

            } catch (ParseException ex) {
                Logger.getLogger(ServletIqt.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SqlInputException ex) {
                Logger.getLogger(ServletIqt.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (plano != null) {
            ArrayList executionPlan = ConnectionDbms.getExecutionPlan(dbms, plano);
            if (executionPlan != null) {
                out.println("PLANO DE EXECUÇÃO DA CONSULTA ORIGINAL\n");
                for (Iterator it = executionPlan.iterator(); it.hasNext();) {
                    String object = it.next().toString();

                    out.println(object + "");
                }
            }

        } else if (file != null) {
            DiskFileUpload fu = new DiskFileUpload();

            try {
                fu.setSizeMax(Long.MAX_VALUE);
                fu.setSizeThreshold(4096);

                BufferedReader reader = new BufferedReader(new FileReader(config.getProperty("filesPathIqt") + file));
                StringBuilder sb = new StringBuilder();
                String line;
                int cont = 1;
                String subsql = null;

                sb.append("<table class=\"table table-bordered table-hover\">");
                while ((line = reader.readLine()) != null) {

                    sb.append("<tr id=\"start-row\" onclick=\"showRow(texto" + cont + ")\">");
                    sb.append("<td>" + cont + "</td>");

                    if (line.length() > 65) {
                        subsql = line.substring(0, 50) + "...";
                    } else {
                        subsql = line;
                    }

                    sb.append("<td>" + subsql + "</td>");
                    sb.append("</tr>");

                    sb.append("<td id=\"texto" + cont + "\" colspan=\"4\" class=\"hidden-row table-bordered desclicado\"  onclick=\"selectRow(textosql" + cont + ")\">");
                    sb.append("<table class=\"hidden-table\">");
                    sb.append("<tr><td><b>Selecionar Consulta</b></td></tr>");
                    sb.append("<tr >");
                    sb.append("<td id=\"textosql" + cont + "\">" + line + "</td></tr>");
                    sb.append("</table></td>");

                    cont++;
                }
                sb.append("</table>");
                out.println(sb);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else if (workload != null) {
            ArrayList<SQL> sqlList = captor.getWorkloadFromTBWorkloadToIqt();
            SQL sql = null;
            String html = null;
            String subsql = null;
            html = "<table class=\"table table-bordered table-hover\">";
            for (int i = 0; i < sqlList.size(); i++) {

                sql = sqlList.get(i);

                html += "<tr id=\"start-row\" onclick=\"showRow(texto" + (i + 1) + ")\">";
                html += "<td>" + (i + 1) + "</td>";

                if ((sql.getSql()).length() > 65) {
                    subsql = (sql.getSql()).substring(0, 50) + "...";
                } else {
                    subsql = (sql.getSql());
                }

                html += "<td>" + subsql + "</td>";
                html += "</tr>";

                html += "<td id=\"texto" + (i + 1) + "\" colspan=\"4\" class=\"hidden-row table-bordered desclicado\"  onclick=\"selectRow(textosql" + (i + 1) + ")\">";
                html += "<table class=\"hidden-table\">";
                html += "<tr><td><b>Selecionar Consulta</b></td></tr>";
                html += "<tr >";
                html += "<td id=\"textosql" + (i + 1) + "\">" + sql.getSql() + "</td></tr>";
                html += "</table></td>";
            }
            html += "</table>";
            System.out.println(html);
            out.println(html);
        }
    }
}
