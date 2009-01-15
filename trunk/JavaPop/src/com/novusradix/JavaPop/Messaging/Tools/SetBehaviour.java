package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Messaging.*;
import com.novusradix.JavaPop.Server.Player.PeonMode;

/**
 *
 * @author gef
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
