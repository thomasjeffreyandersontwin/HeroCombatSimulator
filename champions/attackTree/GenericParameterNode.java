/*
 * GenericParameterNode.java
 *
 * Created on January 20, 2002, 9:33 PM
 */

package champions.attackTree;

import champions.DetailList;
import champions.parameters.ParameterList;
import champions.Target;
import champions.parameters.Parameter;
import java.util.Iterator;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class GenericParameterNode extends DefaultAttackTreeNode {
    
    /** Holds value of property parameterList. */
    private ParameterList parameterList;
    
    /** Holds value of property destinationDetailList. */
    private DetailList destinationDetailList;
    
    /** Holds value of property instructions. */
    private String instructions;
    
    /** Creates new DefensesNode */
    public GenericParameterNode(String name, ParameterList pl, DetailList destination, String instructions ) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.attackParametersIcon");
        
        setParameterList(pl);
        setDestinationDetailList(destination);
        setInstructions(instructions);
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        initializeParameters();
        
        if ( nodeRequiresInput() || manualOverride ) {
            
            GenericParameterPanel op = GenericParameterPanel.getDefaultPanel(parameterList, destinationDetailList);
            attackTreePanel.showInputPanel(this,op);
            attackTreePanel.setInstructions(instructions);
            
            acceptActivation = true;
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    public boolean nodeRequiresInput() {
        return true && getAutoBypassTarget().getProfileOptionIsSet(getAutoBypassOption());
    }
    
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        // This node build everything upfront, when it's target is first set.
        return null;
    }
    
    /**
     * Causes node to process an advance and perform any work that it needs to do.
     *
     * This is a method introduced by DefaultAttackTreeNode.  DefaultAttackTreeNode
     * delegates to this method if advanceNode node is called and this is the active
     * node.  This method should do all the work of advanceNode whenever it can, since
     * the next node processing and buildNextChild methods depend on the existing
     * DefaultAttackTreeNode advanceNode method.
     *
     * Returns true if it is okay to leave the node, false if the node should
     * be reactivated to gather more information.
     */
    public boolean processAdvance() {
        
        return true;
    }
    
    public String getAutoBypassOption() {
        return "SHOW_GENERIC_PARAMETER_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }
    
    /** Getter for property parameterList.
     * @return Value of property parameterList.
     */
    public ParameterList getParameterList() {
        return parameterList;
    }
    
    /** Setter for property parameterList.
     * @param parameterList New value of property parameterList.
     */
    public void setParameterList(ParameterList parameterList) {
        this.parameterList = parameterList;
    }
    
    /** Getter for property destinationDetailList.
     * @return Value of property destinationDetailList.
     */
    public DetailList getDestinationDetailList() {
        return destinationDetailList;
    }
    
    /** Setter for property destinationDetailList.
     * @param destinationDetailList New value of property destinationDetailList.
     */
    public void setDestinationDetailList(DetailList destinationDetailList) {
        this.destinationDetailList = destinationDetailList;
    }
    
    /** Getter for property instructions.
     * @return Value of property instructions.
     */
    public String getInstructions() {
        return instructions;
    }
    
    /** Setter for property instructions.
     * @param instructions New value of property instructions.
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    private void initializeParameters() {
        
        if ( parameterList != null && destinationDetailList != null ) {
            int i;
            //int count  = parameterList.getIndexedSize( "parameter" );
            String key;
            Object value;
            
            Iterator<Parameter> it = parameterList.getParameters();
            while(it.hasNext()) {
                Parameter p = it.next();
                // Create the appropriate configuration panels
                key = p.getKey();
                value = parameterList.getParameterValue(p.getName());
                
                // Find the value from the Destination...
                Object existingValue = destinationDetailList.getValue(key);
                if ( existingValue == null ) {
                    destinationDetailList.add( key, value, true);
                }
            }
        }
    }
    
}
