/*
 * EffectmessageMessage.java
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
public class DRSummaryMessage extends SimpleBattleMessage implements SummaryMessage {
    
    private double rpd, pd, ed, red;
    private boolean added;
    
    /**
     * Creates a new instance of EffectmessageMessage
     */
    public DRSummaryMessage(Target target, boolean added, double pd, double rpd, double ed, double red) {
        super(target, null);
        
        this.rpd = rpd;
        this.pd = pd;
        this.red = red;
        this.ed = ed;
        this.added = added;
        
        setMessageIcon( UIManager.getIcon("AttackTree.summaryIcon"));
    }

    public String getMessage() {
        return getTarget().getName() + " " + getSummary() + ".";
    }


    public String getSummary() {
        
        StringBuilder sb = new StringBuilder();
        int count = 0;
        if ( pd > 0 ) {
            count++;
        }
        if ( ed > 0 ) {
            count++;
        }
        if ( rpd > 0 ) {
            count++;
        }
        if ( red > 0 ) {
            count++;
        }
        
        int i = 0;
        if ( pd > 0 ) {
            int percent = (int)(pd * 100);
            sb.append(percent).append("% PD");
            i++;
        }
        if ( rpd > 0 ) {
            if ( i > 0 && count != 2 ) sb.append(", ");
            if ( i > 0 && count != 2 && i == count-1) sb.append("& ");
            if ( i > 0 && count == 2 ) sb.append(" & ");
            
            int percent = (int)(rpd * 100);
            sb.append(percent).append("% rPD");
            i++;
        }
        if ( ed > 0 ) {
            if ( i > 0 && count != 2 ) sb.append(", ");
            if ( i > 0 && count != 2 && i == count-1) sb.append("& ");
            if ( i > 0 && count == 2 ) sb.append(" & ");
            
            int percent = (int)(ed * 100);
            sb.append(percent).append("% ED");
            i++;
        }
        
        if ( red > 0 ) {
            if ( i > 0 && count != 2 ) sb.append(", ");
            if ( i > 0 && count != 2 && i == count-1) sb.append("& ");
            if ( i > 0 && count == 2 ) sb.append(" & ");
            
            int percent = (int)(red * 100);
            sb.append(percent).append("% rED");
            i++;
        }
        
        
        
        if ( added ) {
            return "gained " + sb + " damage reduction";
        }
        else {
            return "lost " + sb + " damage reduction";
        }
    }

    public boolean isRelevant(Target relevantTarget) {
        return getTarget() == relevantTarget;
    }
    


    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof DRSummaryMessage) {
            DRSummaryMessage msg = (DRSummaryMessage)message2;
            
            if ( getTarget() == msg.getTarget() && added == msg.added ) {
                double newPD = Math.max(pd, msg.pd);
                double newED = Math.max(ed, msg.ed);
                double newRPD = Math.max(rpd, msg.rpd);
                double newRED = Math.max(red, msg.red);
                
                return new DRSummaryMessage(getTarget(), added, newPD, newRPD, newED, newRED);
            }
        }
        
        return null;
    }


    
}
