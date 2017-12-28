/*
 * CalledShotNode.java
 *
 * Created on December 26, 2001, 11:32 PM
 */

package champions.attackTree;

import champions.*;
import champions.exception.BadDiceException;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class HitLocationNode extends DefaultAttackTreeNode {
    
    static Object[][] hitLocationOptions =  {
        // { "None [ -0 OCV ]", "NONE", "HitLocation.None" },
        { "HEAD", new Integer(-8) },
        { "HANDS", new Integer(-6) },
        { "ARMS", new Integer(-5)},
        { "SHOULDERS", new Integer(-5) },
        { "CHEST", new Integer(-3) },
        { "STOMACH", new Integer(-7) },
        { "VITALS", new Integer(-8) },
        { "THIGHS", new Integer(-4) },
        { "LEGS", new Integer(-6) },
        { "FEET", new Integer(-8) }
    };
    
    static Object[][] specialHitLocationOptions =  {
        //  { "None [ -0 OCV ]", "NONE", "HitLocation.None" },
        { "HEADSHOT", new Integer(-4), "1d6+3" },
        { "HIGHSHOT", new Integer(-2), "2d6+1" },
        { "BODYSHOT", new Integer(-1), "2d6+4" },
        { "LOWSHOT", new Integer(-2), "2d6+7" },
        { "LEGSHOT", new Integer(-4), "1d6+12" }
    };
    
    static String[] hitLocationLookupTable =  {
        "N/A","N/A","N/A",
        "HEAD","HEAD","HEAD",
        "HANDS",
        "ARMS", "ARMS",
        "SHOULDERS",
        "CHEST","CHEST",
        "STOMACH",
        "VITALS",
        "THIGHS",
        "LEGS","LEGS",
        "FEET","FEET","FEET"
    };

	public static HitLocationNode Node;
    
    /** Store Target Selected for this node */
    protected Target target;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Creates new CalledShotNode */
    public HitLocationNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.hitLocationIcon");
        Node = this;
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        ActivationInfo ai = battleEvent.getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        setTarget( ai.getTarget(tindex) );
        
        if ( nodeRequiresInput() || manualOverride ) {
            HitLocationPanel app = HitLocationPanel.getDefaultPanel(battleEvent, targetReferenceNumber, getTargetGroup(),getTarget());
            if(attackTreePanel==null) {
            	attackTreePanel=AttackTreePanel.defaultAttackTreePanel;
            }
            attackTreePanel.showInputPanel(this,app);
            attackTreePanel.setInstructions("Configure Hit Location for " + getTarget().getName() + "...");
            
            acceptActivation = true;
        }
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        return getAutoBypassTarget().getBooleanProfileOption( getAutoBypassOption());
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
        ActivationInfo ai = battleEvent.getActivationInfo();
        
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        
        // Setup for different preferences
        if ( (Boolean)Preferences.getPreferenceList().getParameterValue("HitLocationRequired") && target.getBooleanValue("Target.USESHITLOCATION") && battleEvent.usesHitLocation()) {
            // Hit Location is required for every attack, so check for a called shot, then figure out the
            // random location, if necessary
            String calledShot = ai.getIndexedStringValue(tindex, "Target", "HITLOCATIONCALLEDSHOT");
            
            int index;
            if ( (index = findIndexOfHitLocation(calledShot)) != -1 ) {
                // This was a called shot.  If it hits, it will hit this location.
                ai.addIndexed(tindex, "Target", "HITLOCATION", calledShot, true);
                
                Integer ocvPenalty = (Integer)hitLocationOptions[index][1];
                CVList cvl = ai.getCVList(tindex);
                cvl.addSourceCVModifier("Called Shot", ocvPenalty.intValue() );
                cvl.removeSourceModifier("Special Location");
            }
            else if ( (index = findIndexOfSpecialHitLocation(calledShot)) != -1 ) {
                // This is an area shot...
                Dice d = (Dice)ai.getIndexedValue(tindex, "Target", "HITLOCATIONROLL");
                if ( d == null ) {
                    // The dice roll was null, so roll it.
                    String size = (String)specialHitLocationOptions[index][2];
                    try {
                        d = new Dice(size,true);
                    }
                    catch (BadDiceException bde) {
                        
                    }
                    ai.addIndexed(tindex, "Target", "HITLOCATIONROLL", d, true);
                }
                
                String hitLocation = hitLocationLookupTable[d.getStun().intValue()];
                
                // This was a called shot.  If it hits, it will hit this location.
                ai.addIndexed(tindex, "Target", "HITLOCATION", hitLocation, true);
                
                Integer ocvPenalty = (Integer)specialHitLocationOptions[index][1];
                CVList cvl = ai.getCVList(tindex);
                cvl.addSourceCVModifier("Special Location", ocvPenalty.intValue() );
                cvl.removeSourceModifier("Called Shot");
            }
            else {
                // Just a normal Random shot...
                // This is an area shot...
                Dice d = (Dice)ai.getIndexedValue(tindex, "Target", "HITLOCATIONROLL");
                if ( d == null ) {
                    // The dice roll was null, so roll it.
                    d = new Dice(3,true);
                }
                ai.addIndexed(tindex, "Target", "HITLOCATIONROLL", d, true);
                
                String hitLocation = hitLocationLookupTable[d.getStun().intValue()];
                
                // This was a called shot.  If it hits, it will hit this location.
                ai.addIndexed(tindex, "Target", "HITLOCATION", hitLocation, true);
                
                CVList cvl = ai.getCVList(tindex);
                cvl.removeSourceModifier("Special Location");
                cvl.removeSourceModifier("Called Shot");
            }
        }
        else {
            // Called are may be allowed, but hit location is not required
            String calledShot = ai.getIndexedStringValue(tindex, "Target", "HITLOCATIONCALLEDSHOT");
            
            int index;
            if ( (index = findIndexOfHitLocation(calledShot)) != -1 ) {
                // This was a called shot.  If it hits, it will hit this location.
                ai.addIndexed(tindex, "Target", "HITLOCATION", calledShot, true);
                
                Integer ocvPenalty = (Integer)hitLocationOptions[index][1];
                CVList cvl = ai.getCVList(tindex);
                cvl.addSourceCVModifier("Called Shot", ocvPenalty.intValue() );
            }
            else {
                // It wasn't a called shot
                ai.addIndexed(tindex, "Target", "HITLOCATION", null, true);
                CVList cvl = ai.getCVList(tindex);
                cvl.removeSourceModifier("Called Shot");
            }
        }
        
        return true;
    }
    
    public void setTarget(Target target) {
     /*   if ( this.target != target ) {
            ActivationInfo ai = getBattleEvent().getActivationInfo();
            if ( this.target != null ) {
                // This is actually an error, since these nodes should be created for one specific target
            }
      */
        this.target = target;
          /*
            if ( this.target != null ) {
                buildChildren();
            }
        } */
    }
    
    public Target getTarget() {
        return this.target;
    }
    
    public void destroy() {
        setTarget(null);
        
        super.destroy();
    }
    
    private int findIndexOfSpecialHitLocation(String location) {
        if ( location != null ) {
            for(int index = 0; index < specialHitLocationOptions.length; index++) {
                if ( location.equals( specialHitLocationOptions[index][0] ) ) {
                    return index;
                }
            }
        }
        return -1;
    }
    
    
    
    private int findIndexOfHitLocation(String location) {
        if ( location != null ) {
            for(int index = 0; index < hitLocationOptions.length; index++) {
                if ( location.equals( hitLocationOptions[index][0] ) ) {
                    return index;
                }
            }
        }
        return -1;
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
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }
    
    public String getAutoBypassOption() {
        return "SHOW_HIT_LOCATION_PANEL";
    }
}
