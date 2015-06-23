/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
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
