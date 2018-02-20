/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.AbilityAction;
import champions.Battle;
import champions.BattleEngine;
import champions.BattleEvent;
import champions.CVList;
import champions.DefaultAbilityList;
import champions.Dice;
import champions.Effect;
import champions.PADRoster;
import champions.Power;
import champions.Roster;
import champions.SpecialEffect;
import champions.Target;
import champions.battleMessage.GenericSummaryMessage;
import champions.exception.BadDiceException;
import champions.exception.BattleEventException;
import champions.interfaces.AbilityList;
import champions.interfaces.Limitation;
import champions.interfaces.Undoable;
import champions.ioAdapter.heroDesigner.adapters.disadvantageVulnerabilityAdapter;
import champions.parameters.ParameterList;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;


/**
 *
 * @author  unknown
 * @version
 *
 * Effect Hover is now Dynamic.
 */




public class effectEntangle extends Effect {
    static private Ability unknockdown = null;
    
    public effectEntangle( Ability ability, Dice dice ) {
        super( ability.getName(), "PERSISTENT" );
        
        int def;
        int body;      
        // String die = (String)ability.ge(parameterArray, "EntangleDie");
        String die = ability.getStringValue("Power.ENTANGLEDIE");
        
        // Figure out the defenses
        try {
            Dice d = new Dice( die );
            def = d.getD6();
        } catch (BadDiceException bde) {
            def = 0;
        }
        
        body = dice.getBody().intValue();       
        champions.powers.TargetEntangle targetEntangle = new TargetEntangle( this, body, def );
        
                
        
        add("Effect.TARGETENTANGLE",  targetEntangle,  true);
        
        if(ability.findAdvantage("Takes No Damage from Attacks") >-1l)
        {
        	targetEntangle.setTakesNoDamageFromAttack(true);
        }
        else {
        	targetEntangle.setTakesNoDamageFromAttack(false);
        }
        if(ability.findAdvantage("Entangle And Character Both Take Damage") >-1l)
        {
        	targetEntangle.setBothTakesDamageFromAttack(true);
        }
        else {
        	targetEntangle.setBothTakesDamageFromAttack(false);
        }
       
    }
    
    public boolean addEffect(BattleEvent be, Target target) throws BattleEventException {
        if ( ! super.addEffect(be,target) ) return false;
        
        Object o = getValue("Effect.TARGETENTANGLE");
        if ( o == null ) return false;
        
        TargetEntangle targetEntangle = (TargetEntangle)o;
        targetEntangle.setEntangleTarget(target);
        
        targetEntangle.setName(Battle.currentBattle.getUniqueName( targetEntangle.getName() ));
        
        Set<Roster> rosters = Battle.currentBattle.getRosters();
        
        Iterator<Roster> i = rosters.iterator();
        while ( i.hasNext() ) {
            Roster r = i.next();
            List<Target> targets = r.getCombatants();
            
            if ( targets.contains( target ) ) {
                // Found the correct roster...
                r.add( targetEntangle, false );
                be.addRosterEvent(r,targetEntangle,true);
                
                targetEntangle.setEntangleRoster(r);
                
                break;
            }
        }
        
        be.addBattleMessage( new GenericSummaryMessage(target, "has been entangled"));
        
        Undoable u = target.addObstruction(targetEntangle);
        if ( u != null ) be.addUndoableEvent(u);
        
        return true;
    }
    
    public void removeEffect(BattleEvent be, Target target) throws BattleEventException {
        super.removeEffect(be,target);
        
        Target t = getTargetEntangle();
        if ( t != null ) {
            Undoable u = target.removeObstruction(t);
            if ( u != null ) be.addUndoableEvent(u);
        }
        be.addBattleMessage( new GenericSummaryMessage(target, "is no longer entangled"));
    }
    
    public String getDescription() {
        Object target = getValue("Effect.TARGET");
        Object targetEntangle = getValue("Effect.TARGETENTANGLE");
        if ( target == null|| targetEntangle == null ) return "Effect misconfigured.";
        
        String desc = ((Target)target).getName() + " is entangled by " + this.getName() + ".\n";
        
        
        int body = ((Target)targetEntangle).getCurrentStat("BODY");
        int pd = ((Target)targetEntangle).getCurrentStat("PD");
        int ed = ((Target)targetEntangle).getCurrentStat("ED");
        
        desc = desc  + "Entange Stats:\n" + " Body: " + Integer.toString(body) + "\n";
        desc = desc  + " PD: " + Integer.toString(pd) + "\n";
        desc = desc   + " ED: " + Integer.toString(ed) + "\n";
        
        return desc;
        
    }
    
    public void addDCVDefenseModifiers(CVList cvList, Ability attack) {
        cvList.addTargetCVSet( "Entangled", 0 );
    }
    
    public TargetEntangle getTargetEntangle() {
        return (TargetEntangle)getValue("Effect.TARGETENTANGLE");
    }
    public void addActions(Vector actions) {
        
        Ability breakEntangle = new Ability("Break Entangle");
        breakEntangle.addPAD( new powerBreakEntangle(), null);
        AbilityAction action = new AbilityAction( "Break " + this.getName() + " Entangle", breakEntangle );
        action.setType(  BattleEvent.ACTIVATE );
        action.setSource( this.getTarget() );
        //dl = new DetailList();
        //dl.add("BattleEvent.ENTANGLETARGET", "TEST", true);
        //action.setBeExtra( dl );
        actions.add(action);
        
    }
}





