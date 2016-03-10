package interfaceweb.model.javabeans;

import java.util.ArrayList;
import java.util.List;

public class Fragmentation {
    private List<String> nomes = new ArrayList<>();
    private List<Double> fragmentation = new ArrayList<>();
    
    public void addNome(String nome){
        this.nomes.add(nome);
    }
    
    public void addFragmentation(double fragmentation){
        this.fragmentation.add(fragmentation);
    }
    
    public List<String> getNomes() {
        return nomes;
    }

    public List<Double> getFragmentation() {
        return fragmentation;
    }
    
    public void setNomes(List<String> nomes){
        this.nomes = nomes;
    }
    
    public void setFragmentation(List<Double> fragmentation){
        this.fragmentation = fragmentation;
    }
    
    public int getNomesSize(){
        return this.nomes.size();
    }
    
    public int getFragmentationSize(){
        return this.fragmentation.size();
    }
}
