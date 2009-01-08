package com.novusradix.JavaPop.Client;

/**
 *
 * @author gef
 */
public class GLToolGroupButton extends GLButton{
    
    private static GLToolGroupButton selected;
    
    @Override
    protected boolean isSelected() {
        return selected ==this;
    }

    @Override
    public void select() {
        selected = this;
    }
    

}
