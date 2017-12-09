/*
 * ConfigureBattleActivationList.java
 *
 * Created on November 21, 2007, 9:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.attackTree;

import champions.Ability;
import champions.Battle;
import champions.Roster;
import champions.Target;
import champions.interfaces.AbilityIterator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author twalker
 */
public class ConfigureBattleActivationList implements Serializable {
    
    protected Map<ConfigureBattleActivationListEntry,ConfigureBattleActivationListAction> activationList = new HashMap<ConfigureBattleActivationListEntry,ConfigureBattleActivationListAction>();
    
    /** Creates a new instance of ConfigureBattleActivationList */
    public ConfigureBattleActivationList() {
    }
    
    public ConfigureBattleActivationListAction getAction(Target target, Ability ability) {
        ConfigureBattleActivationListAction action = activationList.get( new ConfigureBattleActivationListEntry(target,ability) );
        
        if ( action == null ) {
            action = ConfigureBattleActivationListAction.NOACTION;
        }
        
        return action;
    }
    
    public void setAction(Target target, Ability ability, ConfigureBattleActivationListAction action) {
        activationList.put( new ConfigureBattleActivationListEntry(target,ability), action );
    }
    
    public List<ConfigureBattleActivationListTAAEntry> getActionList() {
        
        List<ConfigureBattleActivationListTAAEntry> list = new ArrayList<ConfigureBattleActivationListTAAEntry>();
        
        // Do this to keep them in order...
        Set<Roster> rosters = Battle.currentBattle.getRosters();
        for( Roster roster : rosters ) {
            List<Target> targets = roster.getCombatants();
        
            for(Target target : targets) {
                AbilityIterator ai = target.getAbilities();
                while(ai.hasNext()) {
                    Ability a = ai.next();

                    ConfigureBattleActivationListAction action = getAction(target, a);

                    if ( action != ConfigureBattleActivationListAction.NOACTION ) {
                        list.add( new ConfigureBattleActivationListTAAEntry(target,a,action) );
                    }
                }
            }
        }
        return list;
    }
    
    public enum ConfigureBattleActivationListAction {
        ACTIVATE("Activate"),
        DEACTIVATE("Deactivate"),
        NOACTION("No Action");
                
        protected String text;
        
        ConfigureBattleActivationListAction(String text) {
            this.text = text;
        }
        
        public String toString() {
            return text;
        }
    }
    
    public static class ConfigureBattleActivationListEntry  implements Serializable {
        Target target;
        Ability ability;
        
        ConfigureBattleActivationListEntry(Target target, Ability ability) {
            this.target = target;
            this.ability = ability;
        }
        
        public boolean equals(Object that) {
            ConfigureBattleActivationListEntry e = ConfigureBattleActivationListEntry.class.cast(that);
            return e != null && this.target.equals(e.target) && this.ability.equals(e.ability);
        }

        public int hashCode() {
            int hash = 1;
            if ( target != null ) hash += target.hashCode();
            if ( ability != null ) hash = hash * 31 + ability.hashCode();
            return hash;
        }
    }
    
    public static class ConfigureBattleActivationListTAAEntry  implements Serializable {
        Target target;
        Ability ability;
        ConfigureBattleActivationListAction action;
        
        ConfigureBattleActivationListTAAEntry(Target target, Ability ability, ConfigureBattleActivationListAction action) {
            this.target = target;
            this.ability = ability;
            this.action = action;
        }
    }
}
    

