/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br, eric@aluno.puc-rio.br  *
 */
package algorithms.mv;

import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class Knapsack {

    @SuppressWarnings("empty-statement")
    public ArrayList<Long> exec(ArrayList<ItemBag> itemsBag, int capacityBag) {
        int numberItems = itemsBag.size();
        int[] profit = new int[numberItems + 1];
        int[] weight = new int[numberItems + 1];
        int[] id = new int[numberItems + 1];

        for (int i = 0; i < numberItems; i++) {
            profit[i] = (int) itemsBag.get(i).gain;
            weight[i] = (int) itemsBag.get(i).cost;
            id[i] = (int) itemsBag.get(i).id;
        }

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
        int sum = 0;
        for (int n = numberItems, w = capacityBag; n > 0; n--) {
            if (sol[n][w]) {
                solutionScalled.add(id[n]);
                sum += weight[n];
                w = w - weight[n];
            }
        }
        ArrayList<Long> solution = new ArrayList<>();
        for (int i : solutionScalled) {
            long temp = (int) i;
            solution.add(temp);
        }
        return solution;
    }

}
