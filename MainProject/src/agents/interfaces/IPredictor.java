/*
 * Automatic Create Materialized Views
 *    *
 */
package agents.interfaces;

/**
 *
 * @author Rafael
 */
public interface IPredictor {

    public void getLastExecutedDDL();

    public void analyzeDDLCaptured();

    public void updateTuningActions();

}
