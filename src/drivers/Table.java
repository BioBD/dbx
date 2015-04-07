/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package drivers;

import base.Base;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rafael
 */
public class Table extends Base {

    private String name;
    protected ArrayList<String> fields;

    public Table() {
        this.fields = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getFields() {
        return fields;
    }

    public String getFieldsString() {
        return this.fields.toString().replace("[", this.getName() + ".").replace("]", "").replace(", ", ", " + this.getName() + ".");
    }

    public void setFields(List<String> fields) {
        for (String field : fields) {
            if (!this.fields.contains(field)) {
                this.fields.add(field);
            }
        }
    }

}
