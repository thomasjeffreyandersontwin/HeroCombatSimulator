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
public class effectBlock extends Effect {

    /** Creates new effectCombatModifier */
    public effectBlock(String name, Ability ability) {
        super( name, "PERSISTENT");

        setAbility(ability);
        setBlockCount(0);
        setBlockFailed(false);
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
        return (Ability)getValue("Effect.ABILITY");
    }
    
    /** Setter for property ability.
     * @param ability New value of property ability.
     */
    public void setAbility(Ability ability) {
        add("Effect.ABILITY", ability, true);
    }
    
    /** Getter for property blockCount.
     * @return Value of property blockCount.
     */
    public int getBlockCount() {
        Integer i = getIntegerValue("Effect.BLOCKCOUNT");
        return (i==null)?0:i.intValue();
    }
    
    /** Setter for property blockCount.
     * @param blockCount New value of property blockCount.
     */
    public Undoable setBlockCount(int blockCount) {
        int oldCount = getBlockCount();
        add("Effect.BLOCKCOUNT", new Integer(blockCount), true);
        return new BlockCountUndoable(this, oldCount, blockCount);
    }
    
    /** Getter for property blockFailed.
     * @return Value of property blockFailed.
     */
    public boolean hasBlockFailed() {
        return getBooleanValue("Effect.BLOCKFAILED");
    }
    
    /** Setter for property blockFailed.
     * @param blockFailed New value of property blockFailed.
     */
    public Undoable setBlockFailed(boolean blockFailed) {
        add("Effect.BLOCKFAILED", blockFailed?"TRUE":"FALSE", true);
        return new BlockFailedUndoable(this, blockFailed);
    }
    
    public static effectBlock getBlockEffect(Target blocker) {
            effectBlock eb = null;
            // Grab the effectBlock Effect
            int index = blocker.getEffectCount() - 1;
            for(; index >= 0; index-- ) {
                Effect effect = blocker.getEffect(index);
                if ( effect instanceof effectBlock ) {
                    eb = (effectBlock)effect;
                    break;
                }
            } 
            
            return eb;
    }
    
    
    public class BlockFailedUndoable implements Undoable, Serializable {
        private effectBlock eb;
        private boolean blockFailed;
        
        public BlockFailedUndoable(effectBlock eb, boolean blockFailed) {
            this.eb = eb;
            this.blockFailed = blockFailed;
        }
        
        public void redo() {
            add("Effect.BLOCKFAILED", blockFailed?"TRUE":"FALSE", true);
        }
        
        public void undo() {
            add("Effect.BLOCKFAILED", (!blockFailed)?"TRUE":"FALSE", true);
        }
        
    }
    
    public class BlockCountUndoable implements Undoable, Serializable {
        private effectBlock eb;
        private int oldCount;
        private int newCount;
        
        
        public BlockCountUndoable(effectBlock eb, int oldCount, int newCount) {
            this.eb = eb;
            this.oldCount = oldCount;
            this.newCount = newCount;
        }
        
        public void redo() {
            add("Effect.BLOCKCOUNT", new Integer(newCount), true);
        }
        
        public void undo() {
            add("Effect.BLOCKCOUNT", new Integer(oldCount), true);
        }
        
    }
}