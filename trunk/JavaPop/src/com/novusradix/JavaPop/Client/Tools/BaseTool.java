/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Client.ControlPalette;
import com.novusradix.JavaPop.Server.Player.PeonMode;
import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mom
 */
public abstract class BaseTool implements Tool {

    static Client client;
    static Tool current;
    static HashMap<Class, Tool> tools;
    static HashMap<String, Tool> types;
    static ControlPalette controlPalette;

    public static void Initialise(Client c) {
        client = c;
        tools = new HashMap<Class, Tool>();
        types = new HashMap<String, Tool>();
        tools.put(RaiseLowerTool.class, new RaiseLowerTool());
        tools.put(MoveAnkhTool.class, new MoveAnkhTool());
        tools.put(EarthquakeTool.class, new EarthquakeTool());
        tools.put(LightningTool.class, new LightningTool());
        tools.put(TidalWaveTool.class, new TidalWaveTool());
        tools.put(VolcanoTool.class, new VolcanoTool());
        for (Tool t : tools.values()) {
            types.put(t.getType(), t);
        /* doesn't work with web start :o(
        for (Class t : Helpers.getClasses("com.novusradix.JavaPop.Client.Tools", true)) {
        if (t.getSuperclass() == BaseTool.class) {
        try {
        Tool tool = (Tool) t.newInstance();
        tools.put(t, tool);
        types.put(tool.getType(), tool);
        } catch (InstantiationException ex) {
        Logger.getLogger(BaseTool.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
        Logger.getLogger(BaseTool.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        }
        }
         */
        }
        current = tools.get(RaiseLowerTool.class);
    }

    public static void InitControlPalette(ControlPalette cp) {
        controlPalette = cp;
    }

    public static final void setTool(Class t) {
        if (current != tools.get(t)) {
            current = tools.get(t);
        }
    }

    public static final Tool getCurrentTool() {
        return current;
    }

    public void SecondaryAction(Point p) {
        setToolDefault();
    }

    public static void SetBehaviour(PeonMode m) {
        client.setBehaviour(m);
    }

    public static Collection<Tool> getAllTools() {
        return tools.values();
    }

    public static Map<String, Tool> getToolsByType() {
        return types;
    }

    public void setToolDefault() {
        current = tools.get(RaiseLowerTool.class);
        if (controlPalette != null) {
            controlPalette.setDefaultTool();
        }
    }
}
