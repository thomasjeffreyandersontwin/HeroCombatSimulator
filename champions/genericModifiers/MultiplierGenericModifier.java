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
public class MultiplierGenericModifier extends AbstractGenericModifier {
    
    private double multiplier;
    /**
     * Creates a new instance of SetGenericModifier
     */
    public MultiplierGenericModifier(String name, double multiplier) {
        setName(name);
        setMultiplier(multiplier);
    }
    
    public MultiplierGenericModifier(String name, double multiplier, boolean editable) {
        setName(name);
        setMultiplier(multiplier);
        setEditable(editable);
    }

    public int applyModifier(int currentValue) {
        if (isActive() == false ) return currentValue;
        
        return (int)Math.round(currentValue * multiplier);
    }

    public GenericModifierPanel getEditingPanel() {
        if ( editorPanel == null ) editorPanel = new MultiplierGenericModifierPanel(this);
        
        return editorPanel;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
    
}
