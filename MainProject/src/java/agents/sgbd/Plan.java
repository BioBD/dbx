/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents.sgbd;

/**
 *
 * @author Rafael
 */
public abstract class Plan implements IPlan {

    private final String plan;

    public Plan(String plan) {
        this.plan = plan;
    }

    public String getPlan() {
        return plan;
    }

}
