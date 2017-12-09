/*
 * DiceRollInfo.java
 *
 * Created on November 9, 2006, 12:03 AM
 *
 * This class tracks information about dice rolls.  More
 * specifically, it tracks the roll and misc. info such as
 * if this was an autoroll.
 */

package champions;

import champions.enums.DiceType;
import champions.exception.BadDiceException;

/**
 *
 * @author 1425
 */
public class DiceRollInfo {
    
    private Dice dice;
    private boolean autoroll;
    private String size;
    
    private DiceType type;
    private String description = "";
    
    /** Creates a new instance of DiceRollInfo */
    public DiceRollInfo(String description, String size) throws BadDiceException {
        setDescription(description);
        setSize(size);
        setAutoroll(true);
        setType(DiceType.STUNANDBODY);
        
        Dice dice = new Dice(size, true);
        setDice(dice);
    }
    
    public DiceRollInfo(String description, int size) {
        setDescription(description);
        setSize(size + "d6");
        setAutoroll(true);
        setType(DiceType.STUNANDBODY);
        
        Dice dice = new Dice(size, true);
        setDice(dice);
    }

    public Dice getDice() {
        return dice;
    }

    public void setDice(Dice dice) {
        this.dice = dice;
    }

    public boolean isAutoroll() {
        return autoroll;
    }

    public void setAutoroll(boolean autoroll) {
        this.autoroll = autoroll;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public DiceType getType() {
        return type;
    }

    public void setType(DiceType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String toString() {
        return "DiceRollInfo [" + getSize() + ", " + getType() + ", " + isAutoroll() + ", Dice = " + getDice() + "]";
    }
    
}
