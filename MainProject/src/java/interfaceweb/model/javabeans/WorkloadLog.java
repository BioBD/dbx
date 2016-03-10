package interfaceweb.model.javabeans;

import java.util.ArrayList;
import java.util.List;

public class WorkloadLog {
    
    private int id;
    private String name;
    private List<Object[]> data = new ArrayList<>();
    
    public void addData(Object[] data){
        this.data.add(data);
    }
    
    public int getDataSize(){
        return this.data.size();
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the data
     */
    public List<Object[]> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<Object[]> data) {
        this.data = data;
    }

}