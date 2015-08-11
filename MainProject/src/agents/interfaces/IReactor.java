/*
 * Automatic Create Materialized Views
 *    *
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
