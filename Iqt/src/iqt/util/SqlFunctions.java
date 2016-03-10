package br.com.iqt.util;

/**
 *
 * @author Arlino
 */
public class SqlFunctions {
    
    /**
     * Verifica se uma string dada trata-se de uma agregação.
     * @param aggregation
     * String com o nome de uma agregação passada.
     * @return
     * Retorna <i>true</i> se encontrar uma agregação e <i>false</i> caso contrário.
     */
    public static final boolean isAggregation(String aggregation){
        if(aggregation.equalsIgnoreCase("MAX"))
            return true;
        else
            if(aggregation.equalsIgnoreCase("MIN"))
                return true;
            else
                if(aggregation.equalsIgnoreCase("COUNT"))
                    return true;
                else
                    if(aggregation.equalsIgnoreCase("SUM"))
                        return true;
                    else
                        if(aggregation.equalsIgnoreCase("AVG"))
                            return true;
        return false;
    }
}
