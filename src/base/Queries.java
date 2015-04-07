package base;

public abstract class Queries extends Base implements IQueries {

    public String getSignatureToDifferentiate() {
        return signatureToDifferentiate;
    }

    public void setSignatureToDifferentiate(String signatureToDifferentiate) {
        this.signatureToDifferentiate = signatureToDifferentiate;
    }

    @Override
    public String getSqlClauseToGetThePlan(String query) {
        if (!query.isEmpty()) {
            return this.getSignatureToDifferentiate() + " EXPLAIN " + query + ";";
        }
        return "";
    }

}
