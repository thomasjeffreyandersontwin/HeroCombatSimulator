/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champions;

import champions.interfaces.CombatLevelProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author twalker
 */
public class CombatLevelList implements Iterable<CombatLevelListEntry> {
    private boolean modified = false;
    private Target target;
    List<CombatLevelListEntry> combatEntries = new ArrayList<CombatLevelListEntry>();

    public CombatLevelList(Target target) {
        this.target = target;
    }

    public void addCombatLevelProvider(CombatLevelProvider provider, boolean enabled) {
        CombatLevelListEntry entry = getCombatLevelEntry(provider);
        if ( entry == null ) {
            entry = new CombatLevelListEntry(this, provider, enabled);
            combatEntries.add(entry);
        }
        entry.setEnabled(enabled);
    }

    public void removeCombatLevelProvider(CombatLevelProvider provider) {
        CombatLevelListEntry entry = getCombatLevelEntry(provider);
        if ( entry != null ) {
            combatEntries.remove(this);
        }
    }

    public CombatLevelListEntry getCombatLevelEntry(CombatLevelProvider provider) {
        for (CombatLevelListEntry combatLevelEntry : combatEntries) {
            if ( combatLevelEntry.getProvider() == provider) {
                return combatLevelEntry;
            }
        }
        return null;
    }

    public Iterator<CombatLevelListEntry> iterator() {
        return combatEntries.iterator();
    }

    /**
     * @return the modified
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    /**
     * @return the target
     */
    public Target getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(Target target) {
        this.target = target;
    }


}
