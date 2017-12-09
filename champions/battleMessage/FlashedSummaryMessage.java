/*
 * EffectSummaryMessage.java
 *
 * Created on February 18, 2008, 2:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Effect;
import champions.Target;
import javax.swing.UIManager;
import champions.Sense;

/**
 *
 * @author twalker
 */
public class FlashedSummaryMessage extends AbstractBattleMessage implements SummaryMessage{
    
    private Target target;
    private Sense sense;
    private int duration;
    private boolean added;
    /**
     * Creates a new instance of EffectSummaryMessage
     */
    public FlashedSummaryMessage(Target target, Sense sense, int duration) {
        this.target = target;
        this.sense = sense;
        this.duration = duration;
        
        setMessageIcon( UIManager.getIcon("AttackTree.appyEffectsIcon"));
    }


    public String getMessage() {
        return target.getName() + "'s " + getSummary()  + ".";
    }

    public String getSummary() {
        if ( duration > 0 ) {
            
            String seg = " segment";
            if ( duration > 1 ) {
                seg = " segments";
            }
            
            return sense.getSenseName() + " was flashed for " + duration + seg;
        }
        else {
            return sense.getSenseName() + " is no longer flashed";
        }
    }

    


    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof FlashedSummaryMessage ) {
            FlashedSummaryMessage esm = (FlashedSummaryMessage)message2;
            if ( target != esm.getTarget() || sense.equals(esm.sense) == false) return null;
            
            int newDuration = duration = esm.duration;
            
            return new FlashedSummaryMessage(target, sense, newDuration); 
        }
        else {
            return null;
        }
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
    
}
