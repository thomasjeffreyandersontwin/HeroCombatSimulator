/*
 * KnockbackSummaryGroup.java
 *
 * Created on February 18, 2008, 1:19 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Target;
import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class KnockbackSummaryGroup extends SummaryMessageGroup {
    
    /**
     * Creates a new instance of KnockbackSummaryGroup
     */
    public KnockbackSummaryGroup(Target target) {
        super(target);
        
        setMessageIcon( UIManager.getIcon("AttackTree.knockbackEffectIcon"));
    }

    public String getMessage() {
        return super.getMessage();
    }

    public boolean isRelevant(Target relevantTarget) {
        return relevantTarget == target;
    }
     


    
    
}
