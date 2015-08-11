/*
 * Automatic Creation Materialized Views
 *    *
 */
package mv;

/**
 *
 * @author Rafael
 */
public interface IMaterializedView {

    void setHypoNumRow();

    void setHypoSizeRow();

    void setHypoCost();

    void setCost();

}
