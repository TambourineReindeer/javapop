/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Client.ControlPalette;
import com.novusradix.JavaPop.Helpers;
import com.novusradix.JavaPop.Server.Player.PeonMode;
import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mom
 */
public abstract class BaseTool implements Tool {

    static Client client;
    static Tool current;
    static HashMap<Class, Tool> tools;
    static ControlPalette controlPalette;

    public static void Initialise(Client c) {
        client = c;
        tools = new HashMap<Class, Tool>();
        
        for(Class t :Helpers.getClasses("com.novusradix.JavaPop.Client.Tools",true))
        {
            if (t.getSuperclass() == BaseTool.class)
            {
                try {
                    tools.put(t, (Tool) t.newInstance());
                } catch (InstantiationException ex) {
                    Logger.getLogger(BaseTool.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(BaseTool.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        current = tools.get(RaiseLowerTool.class);
    }

    public static void InitControlPalette(ControlPalette cp) {
        controlPalette = cp;
    }

    public static final void setTool(Class t) {
        if (current != tools.get(t)) {
            current = tools.get(t);
            controlPalette.setTool(t);
        }
    }

    public static final void setTool(Class t, boolean updateControlPalette) {
        if (current != tools.get(t)) {
            current = tools.get(t);
            if (updateControlPalette) {
                controlPalette.setTool(t);
            }
        }
    }

    public static final Tool getCurrentTool() {
        return current;
    }

    public void SecondaryAction(Point p) {
        setTool(RaiseLowerTool.class);
    }
    
    public static void SetBehaviour(PeonMode m)
    {
        client.setBehaviour(m);
    }
    
    public static Collection<Tool> getAllTools()
    {
        return tools.values();
    }
    
}
