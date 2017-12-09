/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champions;

import champions.interfaces.CombatLevelProvider;

/**
 *
 * @author twalker
 */
public class CombatLevelListEntry {
    protected CombatLevelList combatLevelList;
    private CombatLevelProvider provider;
    private boolean enabled;

    public CombatLevelListEntry(CombatLevelList list, CombatLevelProvider provider, boolean enabled) {
        this.combatLevelList = list;
        this.provider = provider;
        this.enabled = enabled;
    }

    /**
     * @return the provider
     */
    public CombatLevelProvider getProvider() {
        return provider;
    }

    /**
     * @param provider the provider to set
     */
    public void setProvider(CombatLevelProvider provider) {
        this.provider = provider;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void markAsModified() {
        if ( combatLevelList != null ) {
            combatLevelList.setModified(true);
        }
    }
    
}
