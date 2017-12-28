/*
 * SingleTargetNode.java
 *
 * Created on November 7, 2001, 10:42 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.Battle;
import champions.BattleEngine;
import champions.BattleEvent;
import champions.DefenseList;
import champions.Target;
import champions.event.TargetSelectedEvent;
import champions.filters.AndFilter;
import champions.filters.ExcludeTargetsFilter;
import champions.interfaces.ChampionsConstants;
import tjava.Filter;
import champions.interfaces.IndexIterator;
import champions.interfaces.TargetListener;
import javax.swing.UIManager;



/**
 *
 * @author  twalker
 * @version
 */
public class SingleTargetNode extends DefaultAttackTreeNode
        implements TargetListener, ChampionsConstants {
    public static AttackTreeNode Node;

	/** Store Target Selected for this node */
    protected Target target;
    
    private Target source;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Holds value of property mode. */
    private int mode;
    
    private boolean firstPass;
    
    private String instructions = null;
    
    private String additionalNotes = null;
    
    /** Indicates this was a preset target that was just cloned.
     *
     */
    private boolean preset = false;
    
    /** Hold the primary Target number, if this is a primary target. */
    private int primaryTargetNumber = -1;
    
    /** Creates new SingleTargetNode */
    public SingleTargetNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.selectTargetIcon");
        
        setMode(NORMAL_TARGET);
        
        firstPass = true;
        Node = this;
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        boolean fixed = false;
        String reason = null;
        
        if(battleEvent== null) {
        	battleEvent =AttackTreePanel.defaultAttackTreePanel.getModel().battleEvent;
        }
        ActivationInfo ai = battleEvent.getActivationInfo();
        
        adjustSource(battleEvent, targetReferenceNumber, getTargetGroup());
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        Target s = ai.getTargetSource(tindex);
        if ( s == null ) s = battleEvent.getSource();
        
        setSource(s);
        
        if ( battleEvent.getAbility().getPower() != null ) {
            battleEvent.getAbility().getPower().adjustTarget(battleEvent, s, targetReferenceNumber, getTargetGroup(), primaryTargetNumber);
        }
        
        if ( battleEvent.getManeuver() != null && battleEvent.getManeuver().getPower() != null ) {
            battleEvent.getManeuver().getPower().adjustTarget(battleEvent, s, targetReferenceNumber, getTargetGroup(), primaryTargetNumber);
        }
        
        // Call the adjustTarget() method to allow parent nodes to
        // fix up the target if necessary.
        adjustTarget(battleEvent, s, targetReferenceNumber, getTargetGroup(), primaryTargetNumber);
        
        // Look up tindex again in case one of the adjust target created the target entry...
        tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        
        
        // Setup the primary target information for other node to use later.
        if ( isPrimaryTargetNode() ) {
            ai.setPrimaryTarget(primaryTargetNumber, getTargetGroup(), targetReferenceNumber);
        }
        
        if ( tindex != -1 ) {
            // Grab the first target found
            setTarget( ai.getTarget(tindex) );
            fixed = ai.getTargetFixed(tindex);
            reason = ai.getTargetFixedReason(tindex);
        }
        
        if ( nodeRequiresInput() || manualOverride == true ) {
            if ( fixed ) {
                InformationPanel ip = InformationPanel.getDefaultPanel( reason );
                attackTreePanel.showInputPanel(this,ip);
                attackTreePanel.setInstructions("Hit Okay to Continue...");
            } else {
                Target source = null;
                
                Filter<Target> filter = null;
                
                switch (mode) {
                    case NORMAL_TARGET:
                    case SKILL_TARGET:
                        filter = battleEvent.getTargetFilter();
                        break;
                    case AE_TARGET:
                    case AE_SELECTIVE_TARGET:
                    case AE_NONSELECTIVE_TARGET:
                        filter = battleEvent.getTargetFilter();
                        // Remove targets already in the target group...
                        ExcludeTargetsFilter f = new ExcludeTargetsFilter();
                        IndexIterator ii = ai.getTargetGroupIterator( getTargetGroup() );
                        while(ii.hasNext()) {
                            int index = ii.nextIndex();
                            Target t = ai.getTarget(index);
                            if ( t != null ) {
                                f.addExcludedTarget(t);
                            }
                        }
                        
                        if ( filter == null ) {
                            filter = f;
                        }
                        else if ( f != null ) {
                            filter = new AndFilter<Target>(filter, f);
                        }
                        
                        break;
                    default:
                        break;
                }
                
//                switch (mode) {
//                    case KNOCKBACK_TARGET:
//                        //source = ai.getKnockbackSourceTarget(getTargetGroup());
//                        break;
//                    default:
//                        source = getSource();
//                        break;
//                }
                source = getSource();
                
                SelectTargetPanel stp = SelectTargetPanel.getSelectTargetPanel(getBattleEvent(), getTargetGroup(), mode, target != null, source, filter);
                if ( additionalNotes != null ) stp.setAdditionalNotes(additionalNotes);
                stp.addTargetListener(this);
                if(attackTreePanel==null) {
                	attackTreePanel = AttackTreePanel.defaultAttackTreePanel;
                }
                attackTreePanel.showInputPanel(this,stp, isTargetRequirementSatisfied());
                if ( instructions == null ) {
                    attackTreePanel.setInstructions("Select Target...");
                } else {
                    attackTreePanel.setInstructions(instructions);
                }
            }
            acceptActivation = true;
        }
        
        if ( AttackTreeModel.DEBUG > 0 )  System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        // Run through the dice and make sure the all have values;
        boolean requiresInput = false;
        
        switch (mode) {
            case NORMAL_TARGET:
            case AE_CENTER:
                requiresInput = ( target == null );
                break;
            case SKILL_TARGET:
                requiresInput = ( target == null );
                break;
            case AE_TARGET:
            case AE_SELECTIVE_TARGET:
            case AE_NONSELECTIVE_TARGET:
                requiresInput = (firstPass && target == null);
                break;
            case KNOCKBACK_TARGET:
                requiresInput = (firstPass && target == null && getAutoBypassValue() );
                break;
            case SECONDARY_TARGET:
                requiresInput = (firstPass && target == null && getAutoBypassValue() );
                break;
        }
        // Before the override can be used, the Dice must be properly setup in processAdvance
        return requiresInput;
    }
    
    private boolean isTargetRequirementSatisfied() {
        boolean satisfied = false;
        
        switch (mode) {
            case NORMAL_TARGET:
                satisfied = ( target != null );
                break;
            case SKILL_TARGET:
                satisfied = ( target != null );
                break;
            case AE_TARGET:
            case AE_SELECTIVE_TARGET:
            case AE_NONSELECTIVE_TARGET:
                satisfied = true;
                break;
            case KNOCKBACK_TARGET:
                satisfied = true;
                break;
            case SECONDARY_TARGET:
                satisfied = true;
                break;
        }
        // Before the override can be used, the Dice must be properly setup in processAdvance
        return satisfied;
    }
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        // This node is completely strange in that it firsts asks for a target, and once one is set, it
        // creates the ToHit and TargetOption nodes immediately.
        
        return null;
    }
    
    /**
     * Causes the node to verify the layout of it's children and fix any discrepencies.
     *
     * checkNodes should guarantee that the current child structure is complete and
     * all nodes which should exist do.
     *
     * The default implementation calls the nextNodeName method to check that the nodes
     * are arranged properly.  If they are not, it deletes all nodes that aren't properly
     * arranged.
     */
    public void checkNodes() {
        // Don't do anything when you check the nodes.  This nodes is built by
        // when the setTarget method is called.
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
        boolean advance = false;
        
        switch (mode) {
            case NORMAL_TARGET:
            case AE_CENTER:
                advance = ( target != null );
                break;
            case SKILL_TARGET:
                advance = ( target != null );
                break;
            case AE_TARGET:
            case AE_SELECTIVE_TARGET:
            case AE_NONSELECTIVE_TARGET:
                advance = true;
                break;
            case KNOCKBACK_TARGET:
                advance = true;
                break;
            case SECONDARY_TARGET:
                advance = true;
                break;
        }
        
        firstPass = false;
        
        return advance;
    }
    
    public String toString() {
        if ( getTarget() == null ) {
            switch ( mode ) {
                case NORMAL_TARGET:
                    return "Select Target";
                case AE_CENTER:
                    return "Select Area Effect Center Target";
                case SKILL_TARGET:
                    return "Skill Roll";
                case AE_TARGET:
                case AE_SELECTIVE_TARGET:
                case AE_NONSELECTIVE_TARGET:
                    return "Select AE Target";
                case KNOCKBACK_TARGET:
                    return "Select Knockback Target";
                case SECONDARY_TARGET:
                    return "Select Secondary Target";
                default:
                    return null;
            }
        } else {
            switch ( mode ) {
                case SECONDARY_TARGET:
                    return "Secondary Target: " + getTarget().getName();
                case KNOCKBACK_TARGET:
                    return "Knockback Target: " + getTarget().getName();
                default:
                    return "Target: " + getTarget().getName();
            }
            
        }
    }
    
    public void setTarget(Target target) {
        if ( this.target != target ) {
            ActivationInfo ai = getBattleEvent().getActivationInfo();
            if ( this.target != null ) {
                ai.removeTarget(targetReferenceNumber, getTargetGroup());
                
                if ( children != null ) {
                    int index;
                    AttackTreeNode atn;
                    for( index = children.size() - 1; index >= 0; index--) {
                        atn = (AttackTreeNode)children.get(index);
                        removeChild(atn);
                    }
                }
            }
            
            this.target = target;
            
            if ( this.target != null ) {
                ai.addTarget(target, getTargetGroup(), targetReferenceNumber);
                   
                // Do all the target initialization here
                int tindex = ai.getTargetIndex(targetReferenceNumber,getTargetGroup());
           
                DefenseList dl = new DefenseList();
                BattleEngine.buildDefenseList(dl,target);
                ai.setDefenseList(tindex, dl);
                
                
                
                buildChildren();
            }
            
            // if ( model != null ) {
            //     model.nodeChanged(this);
            // }
            
            //System.out.println("AI: " + ai.toLongString());
        }
    }
    
    public Target getTarget() {
        return this.target;
    }
    
    public void targetSelected(TargetSelectedEvent e) {
        Target newTarget = e.getTarget();
        
        if ( e.isPreset() ) {
            // This is a preset target, so we should create a clone of it and allow for
            // the target to be renamed/assigned a roster...
            newTarget = (Target)newTarget.clone();
            
        }
        
        if ( getTarget() != newTarget ) {
            setPreset(e.isPreset());
            setTarget(newTarget);
            
        }
    }
    
    public void destroy() {
        setTarget(null);
        
        super.destroy();
    }
    
    public void buildChildren() {
        
        BattleEvent be = getBattleEvent();
        Ability ability = be.getAbility();
        
        if ( isPreset() ) {
            TargetRenameNode trn = new TargetRenameNode("Target Info");
            trn.setTarget(getTarget());
            addChild(trn);
        }
        
        
        TargetOptionNode ton = new TargetOptionNode("Target Options");
        addChild(ton);
        ton.setTargetReferenceNumber(targetReferenceNumber);
        ton.setTarget(getTarget());
        //String getsvs = battleEvent.getActivationInfo().getStringValue( "Attack.SvS" );
        
        // Build a ToHit node and add it
        if ( getMode() == SKILL_TARGET ) {
            TargetSkillRollNode tsrn = new TargetSkillRollNode("Target Skill Roll");
            addChild(tsrn);
            
            ActivationInfo ai = battleEvent.getActivationInfo();
            Integer targetbaseroll = battleEvent.getActivationInfo().getIntegerValue( "Attack.TARGETBASEROLL" );
            if (targetbaseroll == null) {
                ai.add("Attack.TARGETBASEROLL", new Integer(0), true);
            }
        } else {
            ToHitNode thn = new ToHitNode("ToHit");
            thn.setMode(mode);
            addChild(thn);
            // The reference number will allow the ToHit to look up the appropriate Target
            thn.setTargetReferenceNumber(targetReferenceNumber);
            //thn.setTarget(getTarget()); // Do this afterwards, since the BE would be set before the addChild.
        }
        
        if ( battleEvent.isNND() ) {
            NNDDefenseNode nnd = new NNDDefenseNode("NND Defense", targetReferenceNumber);
            addChild(nnd);
        }
        
    }
    
    /** Getter for property targetReferenceNumber.
     * @return Value of property targetReferenceNumber.
     */
    public int getTargetReferenceNumber() {
        return targetReferenceNumber;
    }
    
    /** Setter for property targetReferenceNumber.
     * @param targetReferenceNumber New value of property targetReferenceNumber.
     */
    public void setTargetReferenceNumber(int targetReferenceNumber) {
        this.targetReferenceNumber = targetReferenceNumber;
    }
    
    /** Getter for property mode.
     * @return Value of property mode.
     */
    public int getMode() {
        return mode;
    }
    
    /** Setter for property mode.
     * @param mode New value of property mode.
     */
    public void setMode(int mode) {
        this.mode = mode;
    }
    
    public String getAutoBypassOption() {
        switch ( mode ) {
            case NORMAL_TARGET:
            case AE_CENTER:
                return null;
            case SKILL_TARGET:
                return null;
            case AE_TARGET:
            case AE_SELECTIVE_TARGET:
            case AE_NONSELECTIVE_TARGET:
                return "SHOW_AE_TARGETS_PANEL";
            case KNOCKBACK_TARGET:
                return "SHOW_KNOCKBACK_SECONDARY_TARGET_PANEL";
            case SECONDARY_TARGET:
                return "SHOW_SECONDARY_TARGETS_PANEL";
            default:
                return null;
        }
    }
    
    public Target getAutoBypassTarget() {
        switch ( mode ) {
            case NORMAL_TARGET:
            case AE_CENTER:
                return null;
            case SKILL_TARGET:
                return null;
            case AE_TARGET:
            case AE_SELECTIVE_TARGET:
            case AE_NONSELECTIVE_TARGET:
                return getBattleEvent().getSource();
            case KNOCKBACK_TARGET:
                return getBattleEvent().getSource();
            case SECONDARY_TARGET:
                return getBattleEvent().getSource();
            default:
                return null;
        }
    }
    
    /** Getter for property primaryTargetNumber.
     * @return Value of property primaryTargetNumber.
     *
     */
    public int getPrimaryTargetNumber() {
        return primaryTargetNumber;
    }
    
    /** Setter for property primaryTargetNumber.
     * @param primaryTargetNumber New value of property primaryTargetNumber.
     *
     */
    public void setPrimaryTargetNumber(int primaryTargetNumber) {
        this.primaryTargetNumber = primaryTargetNumber;
    }
    
    public boolean isPrimaryTargetNode() {
        return this.primaryTargetNumber != -1;
    }
    
    public boolean isPreset() {
        return preset;
    }
    
    public void setPreset(boolean preset) {
        this.preset = preset;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public String getAdditionalNotes() {
        return additionalNotes;
    }
    
    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public Target getSource() {
        return source;
    }

    public void setSource(Target source) {
        this.source = source;
    }
    
}
