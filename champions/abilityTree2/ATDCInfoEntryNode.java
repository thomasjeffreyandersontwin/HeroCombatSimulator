/*
 * ATDCInfoEntryNode.java
 *
 * Created on February 6, 2008, 11:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.abilityTree2;

/**
 *
 * @author twalker
 */
public class ATDCInfoEntryNode extends ATNode {
    
    public String cause;
    public String dc;
    public String explination;
    
    /** Creates a new instance of ATDCInfoEntryNode */
    public ATDCInfoEntryNode() {
        super(null, null, false);
    }
    
    public ATDCInfoEntryNode(String cause, String dc, String explination) {
        super(null, null, false);
        this.cause = cause;
        this.dc = dc;
        this.explination = explination;
    }

    public Object getValueAt(int column) {
        ATColumn c = ATColumn.values()[column];
        switch(c) {
            case NAME_COLUMN:
                return cause;
            case DC_COLUMN:
                return dc;
            case DC_EXPLINATION_COLUMN:
                return explination;
        }
        
        return null;
    }
    

    
}
