/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;

/**
 *
 * @author mom
 */
public abstract class Tool {
    static Client client;
    
    public abstract void PrimaryAction(int x, int y);
    public abstract void SecondaryAction(int x, int y);
    public static void Initialise(Client c)
    {
        client = c;
    }
}
