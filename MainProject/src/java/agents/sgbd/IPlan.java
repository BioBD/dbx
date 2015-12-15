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
public interface IPlan {

    long getCost();

    long getNumRow();

    long getSizeRow();

    ArrayList<SeqScan> getSeqScanOperations();

    float getDuration();
}
