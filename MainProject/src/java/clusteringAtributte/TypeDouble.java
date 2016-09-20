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
public class TypeDouble extends TypeSet<Double>{
    public TypeDouble(Double d){
        super(d);
    }
    @Override
    public String toString(){
        return Double.toString(element);
    }
    @Override
    public double diff(TypeSet<Double> element){
        return this.element - element.getElement();
    }
}
