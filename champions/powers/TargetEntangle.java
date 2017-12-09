/*
 * targetENDReserve.java
 *
 * Created on May 4, 2001, 4:01 PM
 */

package champions.powers;

import champions.Target;
import champions.Ability;
import champions.Roster;
import champions.Target;
import champions.*;
import champions.exception.BadDiceException;
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.Undoable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author  twalker
 * @version
 */
public class TargetEntangle extends Target {
    static final long serialVersionUID = 5295848683348707403L;
    

    
    /** Creates new targetENDReserve */
    public TargetEntangle(Effect effect, int body, int def) {
        super();
        setEntangleEffect(effect);
        
        createCharacteristic("BODY");
        setBaseStat("BODY",body);
        setCurrentStat("BODY", body);
        
        createCharacteristic("PD");
        setBaseStat("PD", def);
        setCurrentStat("PD", def);
        
        createCharacteristic("rPD");
        setBaseStat("rPD", def);
        setCurrentStat("rPD", def);
        
        createCharacteristic("ED");
        setBaseStat("ED", def);
        setCurrentStat("ED", def);
        
        createCharacteristic("rED");
        setBaseStat("rED", def);
        setCurrentStat("rED", def);
        
        add("Target.ISALIVE",  "FALSE",  true);
        add("Target.CANBEKNOCKEDBACK",  "FALSE",  true);
        add("Target.USESHITLOCATION",  "FALSE",  true);
        add("Target.HASSENSES",  "FALSE",  true);
        add("Target.ISOBSTRUCTION", "TRUE", true);
        
        add("Target.HASDEFENSES",  "TRUE",  true);
    }
    
    public int getMinimumStat(String stat) {
        int minimum;
        
        if ( stat.equals("BODY") || stat.equals("STUN") ) {
            minimum = 0;
        } else {
            minimum = super.getMinimumStat(stat);
        }
        
        return minimum;
    }
    
    public void setEntangleTarget(Target t) {
        add("Entangle.TARGET",  t, true );
        setName( t.getName() + " Entangle" );
    }
    
    public void setEntangleEffect(Effect e) {
        add("Entangle.EFFECT",  e, true );
    }
    
    public void setEntangleRoster(Roster e) {
        add("Entangle.ROSTER",  e, true );
    }
    
    public void posteffect(BattleEvent be, Effect newEffect) throws BattleEventException {
        Ability ability = be.getAbility();
        if ( getCurrentStat("BODY") <= 0 ) {
            int bodyEffect = newEffect.findIndexed("Subeffect","VERSUS","BODY");
            
            if ( bodyEffect == -1 ) return;
            // Double value = newEffect.getIndexedDoubleValue( bodyEffect, "Subeffect", "VALUE" );
            double value = newEffect.getSubeffectAdjustedAmount(bodyEffect);
            int startBody = getCurrentStat("BODY") + (int)value;
            if ( value > startBody * 2 ) {
                be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Damage to " + this.getName() + " did twice remaining body.  No action used.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Damage to " + this.getName() + " did twice remaining body.  No action used.", BattleEvent.MSG_NOTICE)); // .addMessage( "Damage to " + this.getName() + " did twice remaining body.  No action used.", BattleEvent.MSG_NOTICE);
                be.setActivationTime("INSTANT");
            } else if ( value > startBody  ) {
                be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Damage to " + this.getName() + " did remaining body.  Half action used.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Damage to " + this.getName() + " did remaining body.  Half action used.", BattleEvent.MSG_NOTICE)); // .addMessage( "Damage to " + this.getName() + " did remaining body.  Half action used.", BattleEvent.MSG_NOTICE);
                be.setActivationTime("HALFMOVE");
            }
            
            triggerRemove(be);
        }
    }
    
    public void triggerRemove(BattleEvent be) throws BattleEventException {
        Target target = (Target)getValue("Entangle.TARGET");
        Effect effect = (Effect)getValue("Entangle.EFFECT");
        Roster roster = (Roster)getValue("Entangle.ROSTER");
        
        if ( roster != null ) {
            ((Roster)roster).removeTarget(be, this);
            //be.addRosterEvent((Roster)roster,this,false);
        }
        
        if ( effect == null || target == null ) return;
        
        if ( target.hasEffect(effect) ) {
            effect.removeEffect(be, target);
        }
        
        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " is no longer entangled.", BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " is no longer entangled.", BattleEvent.MSG_NOTICE )); // .addMessage( target.getName() + " is no longer entangled.", BattleEvent.MSG_NOTICE );
        //if ( Battle.currentBattle != null ) Battle.currentBattle.addCompletedEvent(be);
    }
}
