/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents.sgbd;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Rafael
 */
public class PlanPostgreSQL extends Plan {

    public PlanPostgreSQL(String plan) {
        super(plan);
    }

    @Override
    public long getCost() {
        if (!this.getPlan().isEmpty()) {
            int ini = this.getPlan().indexOf("..") + 2;
            int end = this.getPlan().substring(ini).indexOf(".") + ini;
            return Long.valueOf(this.getPlan().substring(ini, end));
        }
        return 0;
    }

    @Override
    public long getNumRow() {
        if (!this.getPlan().isEmpty()) {
            int ini = this.getPlan().indexOf("rows=") + 5;
            int end = this.getPlan().substring(ini).indexOf(" ") + ini;
            return Long.valueOf(this.getPlan().substring(ini, end));
        }
        return 0;
    }

    @Override
    public long getSizeRow() {
        if (!this.getPlan().isEmpty()) {
            int ini = this.getPlan().indexOf("width=") + 6;
            int end = this.getPlan().substring(ini).indexOf(")") + ini;
            return Long.valueOf(this.getPlan().substring(ini, end));
        }
        return 0;
    }

    @Override
    public ArrayList<SeqScan> getSeqScanOperations() {

        ArrayList<SeqScan> sso = new ArrayList();
        ArrayList<Filter> filters = new ArrayList();
        //String plan = null;
        String name = null, fType = null;
        String[] attributes = null;
        long seqScanCost = getCost();
        long rows = getNumRow();

        Filter filter;
        SeqScan ss;

        String[] scan = getPlan().split("seq scan on ");

        Pattern rest_name = Pattern.compile("(.*).cost");
        Pattern rest_cols = Pattern.compile("filter:(.*)");
        Pattern rest;

        for (int i = 1; i < scan.length; i++) {
            Matcher nameM, str;

            /* Pega o custo. De qual Sec Scan? */
            seqScanCost = getCost();
            rows = getNumRow();

            nameM = rest_name.matcher(scan[i]);
            while (nameM.find()) {
                name = nameM.group();
            }

            int a = name.indexOf(' ');
            name = name.substring(0, a);

            str = rest_cols.matcher(scan[i]);
            while (str.find()) {
                scan[i] = str.group();
            }

            if (scan[i].matches("(.*)filter(.*)")) {
                //Trata o SeqScan atual
                scan[i] = (scan[i]).replaceAll("[(]|[)]|filter:", "");
                scan[i] = (scan[i]).replaceAll("::\\w+,*", "");
                scan[i] = (scan[i]).replaceAll(" and | or ", ",");

                if (scan[i].matches(" and | or ")) {
                    scan[i] = (scan[i]).replaceAll(" and | or ", ",");
                } else {
                    scan[i] = scan[i] + ",";
                }

                //Cria um array de atributos
                attributes = scan[i].split(",");
                rest_cols = Pattern.compile(".*[|]");

                //Armazena os atributos no ArrayList columns
                for (int j = 0; j < attributes.length; j++) {

                    if (((attributes[j].matches("(.*)[<](.*)")) || (attributes[j].matches("(.*)[>](.*)")) || (attributes[j].matches("(.*)[!](.*)")))) {
                        fType = "theta";
                    } else {
                        fType = "equi";
                    }

                    rest = Pattern.compile("(.*) [!|>|<|=]");
                    str = rest.matcher(attributes[j]);
                    while (str.find()) {
                        attributes[j] = str.group();
                    }
                    rest = Pattern.compile("(.*) ");
                    str = rest.matcher(attributes[j]);
                    while (str.find()) {
                        attributes[j] = str.group();
                    }

                    attributes[j] = (attributes[j]).trim();

                    filter = new Filter();
                    filter.setName(attributes[j]);
                    filter.setFilterType(fType);

                    /*Cria o array list de filters*/
                    filters.add(filter);
                }
            }
            /* adiciona o sec scan na lista */
            ss = new SeqScan(name, filters, seqScanCost);
            ss.setNumberOfRows(rows);
            sso.add(ss);
        }
        return sso;
    }

    /* Retorna o plano hipotético */
    public String hypotheticalPlan(String hp) {
        int quantidade = 0, pos;
        String[] attributes = null;
        String indexName = null;
        StringBuilder bSql = null;
        String copyhp = hp;
        hp = (hp).toLowerCase();

        //Contando o numero de atributos lógicos
        Matcher m = Pattern.compile("( and | or )", Pattern.DOTALL).matcher(hp);
        while (m.find()) {
            quantidade++;
        }

        copyhp = copyhp.replace("\n", "?");
        //Obter os atributos do plano
        if (copyhp.matches("(.*)filter:(.*)")) {
            //Deletando parte desnecessaria do plano
            bSql = new StringBuilder(copyhp);
            pos = copyhp.indexOf("filter:");
            bSql.delete(0, pos + 7);
            copyhp = (bSql.toString()).trim();
           // System.out.print(copyhp);

            //Trata o SeqScan atual
            copyhp = (copyhp).replaceAll("[(]|[)]", "");
            copyhp = (copyhp).replaceAll("::\\w+,*", "");
            copyhp = (copyhp).replaceAll(" and | or ", ",");

            if (copyhp.matches(" and | or ")) {
                copyhp = (copyhp).replaceAll(" and | or ", ",");
            } else {
                copyhp = copyhp + ",";
            }

            //Cria um array de atributos
            attributes = copyhp.split(",");

            for (int i = 0; i < attributes.length; i++) {
                bSql = new StringBuilder(attributes[i]);
                pos = 0;
                if (attributes[i].matches(".*[<].*")) {
                    //Deletando parte desnecessaria do plano
                    pos = attributes[i].indexOf("<");
                    bSql.delete(pos, bSql.length());
                    attributes[i] = (bSql.toString()).trim();

                } else if (attributes[i].matches(".*[>].*")) {
                    //Deletando parte desnecessaria do plano
                    pos = attributes[i].indexOf(">");
                    bSql.delete(pos, bSql.length());
                    attributes[i] = (bSql.toString()).trim();

                } else {
                    pos = attributes[i].indexOf("=");
                    bSql.delete(pos, bSql.length());
                    attributes[i] = (bSql.toString()).trim();
                }
            }
        }

        //Obter o nome do índice
        indexName = attributes[0];
        for (int i = 1; i < attributes.length; i++) {
            indexName += "_" + attributes[i];
        }

        bSql = new StringBuilder(hp);
        pos = hp.indexOf(" (cost=");
        bSql.delete(0, pos);
        hp = (bSql.toString()).trim();

        if (quantidade >= 2) { //prefixo "Index Only Scan using"
            hp = "Index Only Scan using on " + indexName + " " + hp;
        } else { //prefixo "Index Scan using"
            hp = "Index Scan using " + indexName + " " + hp;
        }
        hp = hp.replace("filter: ", "Index Cond: ");

        return hp;
    }

    @Override
    public float getDuration() {
        System.out.println(this.getPlan());
        if ((!this.getPlan().isEmpty()) && (this.getPlan().contains("Execution time"))) {
            int ini = this.getPlan().indexOf("Execution time:") + 15;
            int end = this.getPlan().substring(ini).indexOf("ms") + ini;
            return Float.valueOf(this.getPlan().substring(ini, end));
        } else {
            return 0;
        }
    }
}
