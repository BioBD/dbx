/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bib.sgbd.postgresql;

import bib.sgbd.Column;
import bib.sgbd.Plan;
import bib.sgbd.SeqScan;
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
        ArrayList<Column> columns = new ArrayList();
        Column col;
        String plan, name = null;
        String[] attributes;
        long seqScanCost = 0;

        //Deixa o texto em caixa baixa e retira os espaços
        plan = ((this.getPlan()).toLowerCase()).replaceAll(" ", "");
        //Gera um vetor, tal que o seu tamanha é o número de SeqScan do plano
        String[] scan = plan.split("seqscanon");

        Pattern rest_name = Pattern.compile("(.*).cost");
        Pattern rest_cols = Pattern.compile("filter:(.*)");

        for (int i = 1; i < scan.length; i++) {
            Matcher nameM, str;

            nameM = rest_name.matcher(scan[i]);
            while (nameM.find()) {
                name = nameM.group();
            }

            //Refinando a string
            name = name.substring(0, (name.length() - 5));

            //Matcher filter = rest_cols.matcher(plan);
            str = rest_cols.matcher(scan[i]);
            while (str.find()) {
                scan[i] = str.group();
            }

            //Trata o SeqScan atual
            scan[i] = (scan[i]).replaceAll("[(]|[)]| |filter:", "");
            scan[i] = (scan[i]).replaceAll("::\\w+,*", "");
            scan[i] = (scan[i]).replaceAll("and|or", ",");

            //Cria um array de atributos
            attributes = scan[i].split(",");
            rest_cols = Pattern.compile(".*[|]");

            //Armazena os atributos no ArrayList columns
            for (int j = 0; j < attributes.length; j++) {
                attributes[j] = attributes[j].replaceFirst("(>|<|=)", "|");
                str = rest_cols.matcher(attributes[j]);
                while (str.find()) {
                    attributes[j] = str.group();
                }
                attributes[j] = (attributes[j]).replaceAll("[|]", "");
                col = new Column();
                col.setName(attributes[j]);
                columns.add(col);
            }
            //Monta um objeto e o adiciona no ArrayList<SecScan>
            //TODO: Thayson: Preencher o atributo numberOfRows de SeqScan
            //TODO Thayson: Preencher o atributo filterType de Filter: equi para igualdade e theta para os demais
//            seqScanCost = getSeqScanCost(name);
            //TODO: Thayson ajustar aqui
            //SeqScan objSeq = new SeqScan(name, columns, seqScanCost);
            //sso.add(objSeq);
        }

        return sso;
    }

}
