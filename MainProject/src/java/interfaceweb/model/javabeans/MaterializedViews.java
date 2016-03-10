package interfaceweb.model.javabeans;

import java.util.ArrayList;
import java.util.List;

public class MaterializedViews {

    public List<String> nomes = new ArrayList<>();
    public List<Integer> profit = new ArrayList<>();
    public List<Integer> cost = new ArrayList<>();
    
    
    public void addNome(String nome){
        this.nomes.add(nome);
    }
    
    public void addProfit(int profit){
        this.profit.add(profit);
    }
    
    public void addCost(int cost){
        this.cost.add(cost);
    }
    
    public void replaceNomes(List<String> nomes){
        this.nomes = nomes;
    }
    
    public void replaceProfit(List<Integer> profit){
        this.profit = profit;
    }
    
    public void replaceCost(List<Integer> cost){
        this.cost = cost;
    }
    
  
    /**
     * @return the nomes
     */
    public List<String> getNomes() {
        return nomes;
    }

    /**
     * @param nomes the nomes to set
     */
    public void setNomes(List<String> nomes) {
        this.nomes = nomes;
    }

    /**
     * @return the profit
     */
    public List<Integer> getProfit() {
        return profit;
    }

    /**
     * @param profit the profit to set
     */
    public void setProfit(List<Integer> profit) {
        this.profit = profit;
    }

    /**
     * @return the cost
     */
    public List<Integer> getCost() {
        return cost;
    }

    /**
     * @param cost the cost to set
     */
    public void setCost(List<Integer> cost) {
        this.cost = cost;
    }
     
    public int getNomesSize(){
        return this.nomes.size();
    }
    
    public int getProfitSize(){
        return this.profit.size();
    }
    
    public int getCostSize(){
        return this.profit.size();
    }
    
}
