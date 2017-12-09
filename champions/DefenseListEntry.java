package champions;

import champions.enums.DefenseType;
import java.io.Serializable;


public class DefenseListEntry implements Serializable {
    protected boolean active = true;
    protected String description = null;
    
    protected int[] modifiers;
    protected double[] multipliers;
    protected int hardenedLevel = 0;
    
    protected DefenseListEntry(String description) {
        this.description = description;
        
        modifiers = new int[DefenseType.values().length];
        multipliers = new double[DefenseType.values().length];
        
        for (int i = 0; i < modifiers.length; i++)modifiers[i] = 0;
        for (int i = 0; i < multipliers.length; i++)multipliers[i] = 1;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getModifier(DefenseType defenseType) {
        return modifiers[defenseType.ordinal()];
    }
    
    public void setModifier(DefenseType defenseType, int modifier) {
        modifiers[defenseType.ordinal()] = modifier;
    }
    
    public double getMultiplier(DefenseType defenseType) {
        return multipliers[defenseType.ordinal()];
    }
    
    public void setMultiplier(DefenseType defenseType, double multiplier) {
        multipliers[defenseType.ordinal()] = multiplier;
    }
    
    public int getHardenedLevel() {
        return hardenedLevel;
    }
    
    public void setHardenedLevel(int hardenedLevel) {
        this.hardenedLevel = hardenedLevel;
    }
}