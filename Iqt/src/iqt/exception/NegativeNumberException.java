package br.com.iqt.exception;

/**
 *
 * @author Arlino
 */
public class NegativeNumberException  extends Exception{
    public NegativeNumberException(String message) {
        super(message);
    }
    
    public NegativeNumberException() {
        super("Número negativo não permitido para esse parâmetro!");
    }
}
