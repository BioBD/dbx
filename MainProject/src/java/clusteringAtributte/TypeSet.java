/*
 * Biblioteca de codigo fonte criada por Rafael Pereira
 * Proibido o uso sem autorizacao formal do autor
 *
 * rpoliveirati@gmail.com
 */
package clusteringAtributte;

/**
 *
 * @author Alain
 */
public abstract class TypeSet <T>{
    T element;
    public TypeSet(T element){
        this.element = element;
    }
    public T getElement(){return element;}
    public abstract double diff(TypeSet<T> element);
    public abstract String toString();
    
}
