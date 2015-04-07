/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br, eric@aluno.puc-rio.br  *
 */
package algorithms.mv;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
/**
 *
 * @author Rafael
 */
public class Knapsack extends algorithms.Algorithms {

    @SuppressWarnings("empty-statement")
    public ArrayList<BigDecimal> exec(ArrayList<ItemBag> itemsBag, int capacityBag) {
        BigDecimal SIZEINT = new BigDecimal("2147483647");
        
        int numberItems = itemsBag.size();
        BigDecimal dividingFactor = BigDecimal.ONE; 
        int[] profit = new int[numberItems + 1];
        int[] weight = new int[numberItems + 1];
        int[] id = new int[numberItems + 1];
        
        
        for (int i = 1; i <= numberItems; i++) {
            if (itemsBag.get(i - 1).gain.compareTo(SIZEINT) >= 0){
                BigDecimal temp = itemsBag.get(i - 1).gain.divide(SIZEINT,2,RoundingMode.DOWN);
                dividingFactor = temp.max(dividingFactor);
            }
            if (itemsBag.get(i - 1).cost.compareTo(SIZEINT) >= 0){
                BigDecimal temp = itemsBag.get(i - 1).gain.divide(SIZEINT,2,RoundingMode.DOWN);
                dividingFactor = temp.max(dividingFactor);
            }     
        }
        
        
        
        for (int i = 1; i <= numberItems; i++) { 
            profit[i] = itemsBag.get(i - 1).gain.divide(dividingFactor,2, RoundingMode.HALF_UP).intValue();
            weight[i] = itemsBag.get(i - 1).cost.divide(dividingFactor, 2,RoundingMode.DOWN).intValue();
            
            id[i] = itemsBag.get(i - 1).id;
        }
        
        capacityBag = capacityBag/dividingFactor.intValue();
        
        int[][] opt = new int[numberItems + 1][capacityBag + 1];
        boolean[][] sol = new boolean[numberItems + 1][capacityBag + 1];

        for (int n = 1; n <= numberItems; n++) {
            for (int w = 1; w <= capacityBag; w++) {
                int option1 = opt[n - 1][w];
                int option2 = Integer.MIN_VALUE;
                if (weight[n] <= w) {
                    option2 = profit[n] + opt[n - 1][w - weight[n]];
                }
                opt[n][w] = Math.max(option1, option2);
                sol[n][w] = (option2 > option1);
            }
        }
        ArrayList<Integer> solutionScalled = new ArrayList<>();
        for (int n = numberItems, w = capacityBag; n > 0; n--) {
            if (sol[n][w]) {
                solutionScalled.add(id[n]);
                w = w - weight[n];
            }
        }
        ArrayList<BigDecimal> solution = new ArrayList<>();
        for (int i: solutionScalled){
            BigDecimal temp = new BigDecimal(String.valueOf(i));
            solution.add(temp.multiply(dividingFactor));
        }
        
        return solution;
    }

}
