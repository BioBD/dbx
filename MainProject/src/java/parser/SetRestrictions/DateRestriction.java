/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.SetRestrictions;

import clusteringAtributte.SetInterval;
import java.util.Date;

/**
 *
 * @author Alain
 */
public abstract class DateRestriction extends SetRestriction{
    public DateRestriction(){
        super();
    }
    public abstract DateRestriction intercept(DateRestriction dat);
    public abstract SetInterval<Date> getTypeDate();
}