/*
 * effectDying.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.Effect;
import champions.Target;
import champions.interfaces.Undoable;
import java.io.Serializable;

/**
 *
 * @author  unknown
 * @version
 */
public class effectDeflection extends Effect {
    
    private Ability ability;
    private int deflectionCount;
    private boolean deflectionFailed;

    /** Creates new effectCombatModifier */
    public effectDeflection(String name, Ability ability) {
        super( name, "PERSISTENT");

        setAbility(ability);
        setDeflectionCount(0);
        setDeflectionFailed(false);
    }

   /* public String getDescription() {


        return sb.toString();
    } */

    public boolean prephase(BattleEvent be, Target t) {
        return true;  // Remove the effect on prephase
    }

    /** Getter for property ability.
     * @return Value of property ability.
     */
    public Ability getAbility() {
        return ability;
    }
    
    /** Setter for property ability.
     * @param ability New value of property ability.
     */
    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    
    /** Getter for property DeflectionCount.
     * @return Value of property DeflectionCount.
     */
    public int getDeflectionCount() {
        return deflectionCount;
    }
    
    /** Setter for property DeflectionCount.
     * @param DeflectionCount New value of property DeflectionCount.
     */
    public Undoable setDeflectionCount(int deflectionCount) {
        int oldCount = getDeflectionCount();
        this.deflectionCount = deflectionCount;
        return new DeflectionCountUndoable(this, oldCount, deflectionCount);
    }
    
    /** Getter for property DeflectionFailed.
     * @return Value of property DeflectionFailed.
     */
    public boolean hasDeflectionFailed() {
        return deflectionFailed;
    }
    
    /** Setter for property DeflectionFailed.
     * @param DeflectionFailed New value of property DeflectionFailed.
     */
    public Undoable setDeflectionFailed(boolean deflectionFailed) {
        this.deflectionFailed = deflectionFailed;
        return new DeflectionFailedUndoable(this, deflectionFailed);
    }
    
    public static effectDeflection getDeflectionEffect(Target Deflectioner) {
            effectDeflection eb = null;
            // Grab the effectDeflection Effect
            int index = Deflectioner.getEffectCount() - 1;
            for(; index >= 0; index-- ) {
                Effect effect = Deflectioner.getEffect(index);
                if ( effect instanceof effectDeflection ) {
                    eb = (effectDeflection)effect;
                    break;
                }
            } 
            
            return eb;
    }
    
    
    public class DeflectionFailedUndoable implements Undoable, Serializable {
        private effectDeflection eb;
        private boolean deflectionFailed;
        
        public DeflectionFailedUndoable(effectDeflection eb, boolean deflectionFailed) {
            this.eb = eb;
            this.deflectionFailed = deflectionFailed;
        }
        
        public void redo() {
            eb.setDeflectionFailed(deflectionFailed);
        }
        
        public void undo() {
            eb.setDeflectionFailed(!deflectionFailed);
        }
        
    }
    
    public class DeflectionCountUndoable implements Undoable, Serializable {
        private effectDeflection ed;
        private int oldCount;
        private int newCount;
        
        
        public DeflectionCountUndoable(effectDeflection ed, int oldCount, int newCount) {
            this.ed = ed;
            this.oldCount = oldCount;
            this.newCount = newCount;
        }
        
        public void redo() {
            ed.setDeflectionCount(newCount);
        }
        
        public void undo() {
            ed.setDeflectionCount(oldCount);
        }
        
    }
}