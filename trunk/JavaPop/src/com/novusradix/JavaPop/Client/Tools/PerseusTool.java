package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Client.GLToolButton;
import com.novusradix.JavaPop.Messaging.Tools.Hero;
import com.novusradix.JavaPop.Messaging.Tools.Hero.Type;

/**
 *
 * @author gef
 */
public class PerseusTool extends Tool {

    public PerseusTool(ToolGroup tg, Client c) {
        super(tg, c);
    }

    @Override
    public void Select() {
        client.sendMessage(new Hero(Type.PERSEUS));
        GLToolButton.selectDefault();
    }

    

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Perseus.png";
    }

    public String getToolTip() {
        return "Perseus";
    }

}
