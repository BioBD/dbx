/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import bib.base.Base;
import bib.driver.Driver;

/**
 *
 * @author Rafael
 */
public abstract class Agent extends Base implements Runnable {

    protected Driver driver;

    public Agent() {
        this.driver = new Driver();
    }

}
