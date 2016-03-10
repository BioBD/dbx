package br.com.iqt.util;

/**
 *
 * @author Arlino
 */
public class Convertion {
    
    public static String trueOrFalseToYesOrNo(boolean trueOrFalse){
        if(trueOrFalse)
            return "sim";
        else
            return "não";
            
    }
}
