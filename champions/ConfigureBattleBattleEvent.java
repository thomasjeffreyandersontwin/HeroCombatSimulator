/*
 * ConfigureBattleEvent.java
 *
 * Created on November 11, 2007, 12:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions;

import champions.attackTree.ConfigureBattleActivationList;

/**
 *
 * @author twalker
 */
public class ConfigureBattleBattleEvent extends BattleEvent {
    
    private ConfigureBattleActivationList activationList;
    private boolean startOfBattle;
    private Roster roster = null;
    
    /** Creates a new instance of ConfigureBattleEvent */
    public ConfigureBattleBattleEvent() {
        this(true);
    }
    
    public ConfigureBattleBattleEvent(boolean startOfBattle) {
        super( BattleEvent.CONFIGURE_BATTLE, true );
        setStartOfBattle(startOfBattle);
    }
    
    public ConfigureBattleBattleEvent(Roster roster, boolean startOfBattle) {
        super( BattleEvent.CONFIGURE_BATTLE, true );
        setStartOfBattle(startOfBattle);
        setRoster(roster);
    }

    public ConfigureBattleActivationList getActivationList() {
        return activationList;
    }

    public void setActivationList(ConfigureBattleActivationList activationList) {
        this.activationList = activationList;
    }

    public boolean isStartOfBattle() {
        return startOfBattle;
    }

    public void setStartOfBattle(boolean startOfBattle) {
        this.startOfBattle = startOfBattle;
    }

    public Roster getRoster() {
        return roster;
    }

    public void setRoster(Roster roster) {
        this.roster = roster;
    }
    
}
