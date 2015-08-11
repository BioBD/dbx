/*
 * Automatic Creation Materialized Views
 *    *
 */
package algorithms.mv;

/**
 *
 * @author Rafael
 */
public class ItemBag {

    int id;
    long cost;
    long gain;

    public ItemBag(int id, long cost, long gain) {
        this.id = id;
        this.cost = cost;
        this.gain = gain;
    }

    public int getId() {
        return id;
    }

    public long getCost() {
        return cost;
    }

    public long getGain() {
        return gain;
    }

}
