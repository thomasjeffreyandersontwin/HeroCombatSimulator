/*
 * AbstractGenericModifier.java
 *
 * Created on November 9, 2006, 7:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.genericModifiers;

/**
 *
 * @author 1425
 */
public abstract class AbstractGenericModifier implements GenericModifier {
    
    private String name = "";
    private boolean editable = true;
    private boolean active = true;
    
    protected GenericModifierPanel editorPanel;
    
    /** Creates a new instance of AbstractGenericModifier */
    public AbstractGenericModifier() {
        
    }
    
    public AbstractGenericModifier(boolean editable, boolean active) {
        setEditable(editable);
        setActive(active);
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void disassociateWithPanel(GenericModifierPanel editorPanel) {
        if ( this.editorPanel == editorPanel ) {
            this.editorPanel = null;
        }
    }
    
}
