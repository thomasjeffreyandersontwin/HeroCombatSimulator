/*
 * EffectSummaryMessage.java
 *
 * Created on February 18, 2008, 2:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.ChampionsUtilities;
import champions.Target;
import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class DCSummaryMessage extends AbstractBattleMessageGroup implements SummaryMessage{
    
    protected Target target;
    protected String type;
    protected int dc;
    /**
     * Creates a new instance of EffectSummaryMessage
     */
    public DCSummaryMessage(Target target, String type, int dc) {
        this.target = target;
        this.type = type;
        this.dc = dc;
        
        setMessageIcon( UIManager.getIcon("AttackTree.appyEffectsIcon"));
    }
    
    public DCSummaryMessage(Target target, int dc) {
        this.target = target;
        this.type = null;
        this.dc = dc;
        
        setMessageIcon( UIManager.getIcon("AttackTree.appyEffectsIcon"));
    }

    public String getMessage() {
        return target.getName() + " " + getSummary() + ".";
        
    }

    public String getSummary() {
        
        String dcs = " DC";
        if ( dc > 1 ) dcs = " DCs";
        
        String typeString = "";
        if ( type != null ) typeString = " " + type ;
        
        if ( dc > 0 ) {
            return "gained " + ChampionsUtilities.toSignedString(dc) + typeString + dcs;
        }
        else {
            return "lost " + ChampionsUtilities.toSignedString(dc) + typeString + dcs;
        }
    }


    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof DCSummaryMessage ) {
            DCSummaryMessage esm = (DCSummaryMessage)message2;
            if ( target == esm.getTarget() && ((type == null && esm.type == null) || (type != null && type.equals(esm.type) ) ) ) {
                int value = dc + esm.dc;
                
                if ( dc == 0 ) {
                    return NullSummaryBattleMessage.nullSummaryBattleMessage;
                }
                else {
                    return new DCSummaryMessage(target,type,value);
                }
            }
        }
            
        return null;
    }

    public boolean isRelevant(Target relevantTarget) {
        return relevantTarget == target;
    }
    


    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public void closeGroup() {
    }


}
