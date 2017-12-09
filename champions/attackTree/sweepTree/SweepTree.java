/*
 * TargetTree.java
 *
 * Created on February 27, 2002, 11:16 PM
 */

package champions.attackTree.sweepTree;

import champions.SweepBattleEvent;
import champions.interfaces.ChampionsConstants;
import java.awt.Color;
import javax.swing.UIManager;
import treeTable.DefaultTreeTable;
import treeTable.TreeTableHeader;



/**
 *
 * @author  twalker
 * @version
 */
public class SweepTree extends DefaultTreeTable implements ChampionsConstants {
    
    /** Holds value of property target. */
    private SweepBattleEvent battleEvent;
    
    /** Holds the name you want for the title. */
    private String title;
    

    
    /** Creates new TargetTree */
    public SweepTree(String title) {
        this.title = title;
        
        setupModel();
        setupMouseListener();
        
        TreeTableHeader tth = getTreeTableHeader();
        tth.getColumnModel().getColumn(0).setPreferredWidth(200);
        
        this.setRootVisible(false);
        
        setupUI();
    }
    
    public SweepTree() {
        this("Abilities");
    }
    
    public void updateUI() {
        super.updateUI();
        
        setupUI();
    }
    
    private final void setupUI() {
        Color c;
        c = UIManager.getColor( "AbilityEditor.foreground" );
        if ( c != null ) this.setForeground(c);
        c = UIManager.getColor( "AbilityEditor.background" );
        if ( c != null ) this.setBackground(c);
    }
    
    protected void setupModel() {
      //  if ( padTreeModel == null ) {
            SWTNode root = new SWTBattleEventNode(battleEvent);
            SWTModel model = new SWTModel(root, title);
            this.setTreeTableModel(model);
      //  }
    }
    
    protected void setupMouseListener() {
  /*      addMouseListener( new MouseAdapter() {
            
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && getAbilityTreeTable() != null && getTarget() != null ) {
                    //int index = PADListListener.this.list.locationToIndex(e.getPoint());
                    TreePath clickPath = TargetTree.this.getSelectionPath();
                    if (clickPath != null && clickPath.getLastPathComponent() instanceof STNode ) {
                        ((STNode)clickPath.getLastPathComponent()).handleDoubleClick(abilityPath);
                    }
                }
            }
        }); */
    }

    /** Getter for property title.
     * @return Value of property title.
     *
     */
    public java.lang.String getTitle() {
        return title;
    }
    
    /** Setter for property title.
     * @param title New value of property title.
     *
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }
    
    /** Getter for property battleEvent.
     * @return Value of property battleEvent.
     *
     */
    public champions.SweepBattleEvent getBattleEvent() {
        return battleEvent;
    }
    
    /** Setter for property battleEvent.
     * @param battleEvent New value of property battleEvent.
     *
     */
    public void setBattleEvent(champions.SweepBattleEvent battleEvent) {
        this.battleEvent = battleEvent;
        
        SWTBattleEventNode root = (SWTBattleEventNode)getProxyTreeTableModel().getRoot();
        if ( root != null ) {
            root.setBattleEvent(battleEvent);
        }
    }
    
}
