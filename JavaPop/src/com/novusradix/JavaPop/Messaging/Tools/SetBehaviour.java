/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Messaging.*;
import com.novusradix.JavaPop.Server.Player.PeonMode;

/**
 *
 * @author mom
 */
public class SetBehaviour extends Message{

    PeonMode behaviour;
    
    public SetBehaviour(PeonMode m)
    {
        behaviour = m;
    }
    
    @Override
    public void execute() {
    serverPlayer.peonMode = behaviour;
    }
    

}
