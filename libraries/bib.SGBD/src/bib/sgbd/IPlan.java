/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bib.sgbd;

import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public interface IPlan {

    public long getCost();

    public long getNumRow();

    public long getSizeRow();

    public ArrayList<SeqScan> getSeqScanOperations();

    public float getDuration();

}
