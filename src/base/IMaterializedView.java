/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package base;

/**
 *
 * @author Rafael
 */
public interface IMaterializedView {

    public void setCost();

    public void setHypoNumRow();

    public void setHypoSizeRow();

    public void setHypoCost();
}
