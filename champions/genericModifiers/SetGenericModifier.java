/*
 * SetGenericModifier.java
 *
 * Created on November 9, 2006, 6:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.genericModifiers;

/**
 *
 * @author 1425
 */
public class SetGenericModifier extends AbstractGenericModifier {
    
    private int setValue;
    /**
     * Creates a new instance of SetGenericModifier
     */
    public SetGenericModifier(String name, int value) {
        setName(name);
        this.setSetValue(value);
    }

    public int applyModifier(int currentValue) {
        if (isActive() == false ) return currentValue;
                
        return getSetValue();
    }

    public GenericModifierPanel getEditingPanel() {
        if ( editorPanel == null ) editorPanel = new SetGenericModifierPanel(this);
        
        return editorPanel;
    }

    public int getSetValue() {
        return setValue;
    }

    public void setSetValue(int value) {
        this.setValue = value;
    }
    
}
