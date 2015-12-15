/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
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
