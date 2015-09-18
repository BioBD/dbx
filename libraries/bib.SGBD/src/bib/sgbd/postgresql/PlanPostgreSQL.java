/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bib.sgbd.postgresql;

import bib.sgbd.Filter;
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
        ArrayList<Filter> filters = new ArrayList();
        String plan = null, name = null, fType = null;
        String[] attributes;
        long seqScanCost = getCost();
        long rows = getNumRow();
        
        Filter filter;
        SeqScan ss;
        
        String[] scan = plan.split("seq scan on ");
        
        Pattern rest_name = Pattern.compile("(.*).cost");
        Pattern rest_cols = Pattern.compile("filter:(.*)");
        Pattern rest;
        
        for(int i=1; i<scan.length; i++){
            Matcher nameM, str;
            
            /* Pega o custo. De qual Sec Scan? */
            seqScanCost = getCost();
            rows = getNumRow();
            
            nameM = rest_name.matcher(scan[i]);
            while(nameM.find()){
                name = nameM.group();
            }
            
            int a = name.indexOf(' ');
            name = name.substring(0,a);
            
            str = rest_cols.matcher(scan[i]);  
            while(str.find()){
                scan[i] = str.group();
            }

            if(scan[i].matches("(.*)filter(.*)")){
                //Trata o SeqScan atual
                scan[i] = (scan[i]).replaceAll("[(]|[)]|filter:", "");
                scan[i] = (scan[i]).replaceAll("::\\w+,*", "");
                scan[i] = (scan[i]).replaceAll(" and | or ", ",");

                if(scan[i].matches(" and | or ")){
                    scan[i] = (scan[i]).replaceAll(" and | or ", ",");
                }else{
                    scan[i] = scan[i]+",";
                }
                
                //Cria um array de atributos
                attributes = scan[i].split(",");
                rest_cols = Pattern.compile(".*[|]");

                //Armazena os atributos no ArrayList columns
                for(int j=0; j<attributes.length; j++){

                    if( ((attributes[j].matches("(.*)[<](.*)")) || (attributes[j].matches("(.*)[>](.*)")) || (attributes[j].matches("(.*)[!](.*)"))) ){
                        fType = "theta";
                        System.out.println("theta");
                    }else{
                        fType = "equi";
                        System.out.println("equi");
                    }
                    
                    rest = Pattern.compile("(.*) [!|>|<|=]");
                    str = rest.matcher(attributes[j]);
                    while(str.find()){
                        attributes[j] = str.group();
                    }
                    rest = Pattern.compile("(.*) ");
                    str = rest.matcher(attributes[j]);
                    while(str.find()){
                        attributes[j] = str.group();
                    }
                    
                    attributes[j]=(attributes[j]).trim();
                    System.out.println(attributes[j]);
                    
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
}
