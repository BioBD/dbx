package br.com.iqt.util;

import java.util.ArrayList;

/**
 *
 * @author Arlino
 */
public class Replace {
    public static ArrayList<String[]> replaceCommands(String query){
        ArrayList<String[]> list;
        boolean find = false;
        
        int indexOf1, indexOf2, indexOf3, i = 0;
        list = new ArrayList<String[]>();
        indexOf1 = query.indexOf("case");
        while(indexOf1 != -1){
            i++;
            indexOf2 = query.indexOf(",", indexOf1);
            indexOf3 = query.indexOf("from", indexOf1);
            if(indexOf2 == -1 || indexOf2 > indexOf3){
                indexOf2 = indexOf3 - 1;
                find = true;
            }
            String substring = query.substring(indexOf1, indexOf2);
            String replacement = "rplccmmnd1" + i;
            query = query.replace(substring, replacement);
            String[] texts = new String[2];
            texts[0] = substring;
            texts[1] = replacement;
            list.add(texts);
            indexOf1 = query.indexOf("case");
            if(find || indexOf1>indexOf3)
                indexOf1 = -1;
        }
        String[] texts = new String[2];
        texts[0] = query;
        list.add(texts);
        return list;
    }
    
    public static String rereplaceCommands(ArrayList<String[]> list, String query){
        for (String[] strings : list){
            query = query.replace(strings[1], strings[0]);
        }
        return query;
    }
}
