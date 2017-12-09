/*
 * ATColumns.java
 *
 * Created on November 10, 2007, 8:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.abilityTree2;

import javax.swing.Icon;
import treeTable.TreeTableColumn;

/**
 *
 * @author twalker
 */
public enum ATColumn {
    NAME_COLUMN("Name",175, String.class),
    AUTO_ACTIVATE_COLUMN("Auto-Activate",16),
    END_COLUMN("END",30, Integer.class),
    OCV_COLUMN("OCV",30, Integer.class),
    DCV_COLUMN("DCV",30, Integer.class),
    CP_COLUMN("CP",30, Integer.class),
    LAUNCH_COLUMN("Start/Stop",50),
    BODY_COLUMN("Body", 30, Integer.class),
    STUN_COLUMN("Stun", 30, Integer.class),
    PD_COLUMN("PD/rPD", 30, String.class),
    ED_COLUMN("ED/rED", 30, String.class),
    EFFECTS_COLUMN("Effects", 800),
    RANGE_COLUMN("Range", 30, String.class),
    DC_COLUMN("DCs", 40, String.class),
    DC_EXPLINATION_COLUMN("Explanation", 400, String.class),
    MESSAGE_COLUMN("Message", 400, String.class), // Message column for battle messages
    EXPLOSION_SLIDER("", 200, Integer.class),
    EXPLOSION_DISTANCE("Distance", 30, Integer.class),
    VPP_CURRENT_ABILITY_STATUS("Status", 120, String.class),
    VPP_ABILITY_ACTION("Action", 140, String.class),
    VPP_ADDITIONAL_INFO("Info", 200, String.class),
    AP_COLUMN("AP", 30, Integer.class),
    ENABLED_COLUMN("Enabled", 30, Boolean.class),
    LEVELS_COLUMN("Levels", 30, Integer.class),
    MAX_COLUMNS("MAX",0);
    
    
    private String name;
    private int preferredWidth;
    private Class columnClass;
    
    ATColumn(String name, int preferredWidth) {
        this(name,preferredWidth,null);
    }
    
    ATColumn(String name, int preferredWidth, Class columnClass) {
        this.name = name;
        this.preferredWidth = preferredWidth;
        this.columnClass = columnClass;
    }
    
    public String getName() {
        return name;
    }
    
    public int getPreferredWidth() {
        return preferredWidth;
    }
    
    
    public Class getColumnClass() {
        return columnClass;
    }
    
    public TreeTableColumn createColumn(Icon icon) {
        TreeTableColumn tc = new TreeTableColumn( ordinal() );
        
        tc.setHeaderValue( this.name );
       
        if ( preferredWidth != -1 ) {
            tc.setPreferredWidth(preferredWidth);
            tc.setWidth(preferredWidth);
        }
        else {
            tc.setWidth(85);
        }
        
        tc.setIcon(icon);
        tc.setSortable(true);
        
        return tc;
    }

}
