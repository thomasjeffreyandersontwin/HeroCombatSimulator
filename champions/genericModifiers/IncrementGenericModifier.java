/*
 * BaseGenericModifier.java
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
public class IncrementGenericModifier extends AbstractGenericModifier {

    private int amount;
    /**
     * Creates a new instance of BaseGenericModifier
     */
    public IncrementGenericModifier(String name, int amount) {
        setName(name);
        this.setAmount(amount);
    }
    
    public IncrementGenericModifier(String name, int amount, boolean editable) {
        setName(name);
        setAmount(amount);
        setEditable(editable);
    }

    public int applyModifier(int currentValue) {
        if (isActive() == false ) return currentValue;
        
        return currentValue + getAmount();
    }

    public GenericModifierPanel getEditingPanel() {
        if ( editorPanel == null ) editorPanel = new IncrementGenericModifierPanel(this);
        
        return editorPanel;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    
}
