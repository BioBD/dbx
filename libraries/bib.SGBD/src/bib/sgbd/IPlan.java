/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bib.sgbd;

/**
 *
 * @author Rafael
 */
public interface IPlan {

    long getCost();

    long getNumRow();

    long getSizeRow();

}
