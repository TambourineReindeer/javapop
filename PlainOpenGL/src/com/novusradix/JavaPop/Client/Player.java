/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client;

/**
 *
 * @author mom
 */
public class Player {

    private int mana;
    public BaseTool currentTool;
   
    public Client client;

    public Player(Client c) {
        client = c;
    }
}
