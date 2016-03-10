package iqt.exception;

public class DbmsException  extends Exception{
    
    public DbmsException(String message) {
        super(message);
    }
    
    public DbmsException() {
        super("Objeto Dbms nulo!");
    }
}
