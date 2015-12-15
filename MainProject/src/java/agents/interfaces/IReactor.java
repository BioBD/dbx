/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents.interfaces;

/**
 *
 * @author Rafael
 */
public interface IReactor {

    public void getLastTuningActionsNotAnalyzed();

    public void executeTuningActions();

    public void updateStatusTuningActions();

}
