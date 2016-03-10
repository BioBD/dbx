package br.com.iqt.exception;

/**
 *
 * @author Arlino
 */
public class HeuristicsSelectedException extends Exception{
    public HeuristicsSelectedException(String message) {
        super(message);
    }
    
    public HeuristicsSelectedException() {
        super("O objeto HeuristicSelected não pode ser nulo");
    }
}
