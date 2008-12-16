/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Messaging;

/**
 *
 * @author mom
 */
public class GameOver extends Message{

    @Override
    public void execute() {
        client.quit();
    }

}
