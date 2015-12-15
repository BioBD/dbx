/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents.sgbd;

import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class PlanOracle extends Plan {

    public PlanOracle(String plan) {
        super(plan);
    }

    @Override
    public long getCost() {
        return this.getLongValueFromPlanOracle("cost");
    }

    @Override
    public long getNumRow() {
        return this.getLongValueFromPlanOracle("cardinality");
    }

    @Override
    public long getSizeRow() {
        long numRows = this.getLongValueFromPlanOracle("cardinality");
        long sizeTotal = this.getLongValueFromPlanOracle("bytes");
        if (sizeTotal > 0) {
            return numRows / sizeTotal;
        } else {
            return 0;
        }
    }

    private long getLongValueFromPlanOracle(String nameValue) {
        String value = this.getValueFromPlanOracle(nameValue);
        if (value.isEmpty()) {
            value = "0";
        }
        return Long.valueOf(value);
    }

    private String getValueFromPlanOracle(String nameValue) {
        if (!this.getPlan().isEmpty()) {
            String lines[] = this.getPlan().toLowerCase().split("\\r?\\n");
            for (String line : lines) {
                if (line.contains(nameValue.toLowerCase())) {
                    String values[] = line.split("=");
                    return values[1];
                }
            }
        }
        return "";
    }

    @Override
    public ArrayList<SeqScan> getSeqScanOperations() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public float getDuration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
