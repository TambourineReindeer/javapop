/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Client.ControlPalette;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mom
 */
public abstract class BaseTool implements Tool {

    static Client client;
    static Tool current;
    static Map<ToolType, Tool> tools;
    static ControlPalette controlPalette;

    public static void Initialise(Client c) {
        client = c;
        tools = new HashMap<ToolType, Tool>();
        tools.put(ToolType.RaiseLower, new RaiseLowerTool());
        tools.put(ToolType.Lightning, new LightningTool());

        current = tools.get(ToolType.RaiseLower);
    }

    public static void InitControlPalette(ControlPalette cp) {
        controlPalette = cp;
    }

    public static final void setTool(ToolType t) {
        if (current != tools.get(t)) {
            current = tools.get(t);
            controlPalette.setTool(t);
        }
    }

    public static final void setTool(ToolType t, boolean updateControlPalette) {
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
}
