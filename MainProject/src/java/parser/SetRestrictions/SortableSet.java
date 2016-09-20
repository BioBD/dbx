/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.SetRestrictions;

import clusteringAtributte.SetInterval;

/**
 *
 * @author Alain
 */
public abstract class SortableSet extends SetRestriction{
    public abstract SortableSet interception(IntervalSetRestriction restriction);
    public abstract SortableSet interception(UnarySetRestriction restriction);
    public abstract SetInterval<Double> getTypeDouble();
}
