/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.SetRestrictions;

import parser.EnumOperator;

/**
 *
 * @author Alain
 */
public abstract class SetRestriction {
    public abstract boolean matchRestriction(SetRestriction restriction);
    public abstract String toString();
    public abstract String att();
    public double selectivity=-1;
}
