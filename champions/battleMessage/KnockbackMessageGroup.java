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
public class KnockbackMessageGroup extends AbstractBattleMessageGroup {
    
    /**
     * Creates a new instance of KnockbackSummaryGroup
     */
    public KnockbackMessageGroup() {
        
        setMessageIcon( UIManager.getIcon("AttackTree.knockbackEffectIcon"));
    }

    public void closeGroup() {
    }
    
    protected boolean isSingleton() {
        return children != null && children.size() == 1;
    }

    public String getMessage() {
        if ( isSingleton() ) {
            return children.get(0).getMessage();
        }
        else {
            return "Knockback (" + super.getChildCount() + " targets)";
        }
    }

    public int getChildCount() {
        if ( isSingleton() ) {
            BattleMessage bm = children.get(0);
            if ( bm instanceof BattleMessageGroup ) {
                return ((BattleMessageGroup)bm).getChildCount();
            }
            else {
                return 0;
            }
        }
        else {
            return super.getChildCount();
        }
    }
    
    public BattleMessage getChild(int index) {
        if ( isSingleton() ) {
            BattleMessage bm = children.get(0);
            if ( bm instanceof BattleMessageGroup ) {
                return ((BattleMessageGroup)bm).getChild(index);
            }
            else {
                return null;
            }
        }
        else {
            return super.getChild(index);
        }
    }
    
    public boolean isExpandedByDefault() {
        if ( isSingleton() ) {
            BattleMessage bm = children.get(0);
            if ( bm instanceof BattleMessageGroup ) {
                return ((BattleMessageGroup)bm).isExpandedByDefault();
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    
    


     


    
    
}
