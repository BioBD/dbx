/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package algorithms.mv;
import java.math.BigDecimal;

/**
 *
 * @author Rafael
 */
public class ItemBag {

    int id;
    BigDecimal cost;
    BigDecimal gain;

    public ItemBag(int id, BigDecimal cost, BigDecimal gain) {
        this.id = id;
        this.cost = cost;
        this.gain = gain;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public BigDecimal getGain() {
        return gain;
    }

}
