/*
 * effectENDReserve.java
 *
 * Created on May 4, 2001, 3:57 PM
 */

package champions.powers;

import champions.*;
import champions.exception.BattleEventException;
import champions.interfaces.ENDSource;

import champions.undoableEvent.ENDSourceUndoable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author  twalker
 * @version 
 */
public class effectENDReserve extends LinkedEffect{
    static final long serialVersionUID = 5295848683348707403L;
    
    static private Object[][] parameterArray = {
        {"EndLevel","Power.ENDLEVEL", Integer.class, new Integer(10)},
        {"RecLevel","Power.RECLEVEL", Integer.class, new Integer(1)}
    };
    /** Creates new effectENDReserve */
    public effectENDReserve(Ability ability) {
        super( ability.getName(), "LINKED" );
        this.setHidden(true);
        setAbility(ability);
        
        Target source = ability.getSource();
        
        targetENDReserve targetENDReserve = new targetENDReserve( source.getName() + "'s " + ability.getName(), this);
        add("Effect.TARGETENDRESERVE",  targetENDReserve,  true);
    }

    
    public boolean addEffect(BattleEvent be, Target target) throws BattleEventException {
        if ( ! super.addEffect(be,target) ) return false;
        
        Object o = getValue("Effect.TARGETENDRESERVE");
        if ( o == null ) return false;
        
        // Add the Reserve to the Roster
        targetENDReserve reserve = (targetENDReserve)o;
        Target source = getAbility().getSource();
        
        Set<Roster> rosters = Battle.currentBattle.getRosters();
        
        Iterator<Roster> i = rosters.iterator();
        while ( i.hasNext() ) {
            Roster r = i.next();
            List<Target> targets = r.getCombatants();
            
            if ( targets.contains( source ) ) {
                // Found the correct roster...
                r.add( (Target)reserve, false );
                be.addRosterEvent(r,(Target)reserve,true);
                
                //reserve.setReserveRoster(r);
                
                break;
            }
        }
        
        // Add the Reserve to the ENDSource of the Source Target
      //  int index = source.findIndexed("ENDSource","NAME",getAbility().getName());
      //  if ( index != -1 ) {
      //      source.addIndexed(index, "ENDSource","ENDSOURCE", reserve, true);
      //  }
        source.addENDSource( getAbility().getName(), reserve);
        be.addUndoableEvent( new ENDSourceUndoable( source, getAbility().getName(), reserve, true) );
        
        return true;
    }
    
    public void removeEffect(BattleEvent be, Target target) throws BattleEventException {
        super.removeEffect(be,target);
        
        Object o = getValue("Effect.TARGETENDRESERVE");
        if ( o == null ) return;
        
        targetENDReserve reserve = (targetENDReserve)o;
        
        Roster roster = target.getRoster(); //reserve.getReserveRoster();
        roster.remove(reserve);
        be.addRosterEvent(roster,reserve,false);
        
        // Remove the Reserve to the ENDSource of the Source Target
        Target source = getAbility().getSource();
        String endSourceName = getAbility().getName();
        int index = source.findIndexed("ENDSource","NAME",endSourceName);
        if ( index != -1 ) {
           // source.removeIndexed(index, "ENDSource","ENDSOURCE");
            ENDSource endSource = source.getENDSource(endSourceName);
            source.removeENDSource( endSourceName );
            
            be.addUndoableEvent( new ENDSourceUndoable(source, endSourceName, endSource, false));
        }
        
    }
    
    public String getDescription() {
        return getAbility().getName() + " END Reserve Link";
    }
    
    public boolean postturn(BattleEvent be, Target t) throws BattleEventException {
        Object o = getValue("Effect.TARGETENDRESERVE");
        if ( o == null ) return false;
        
        targetENDReserve reserve = (targetENDReserve)o;
        
        Integer endLevel = (Integer)getAbility().parseParameter(parameterArray, "EndLevel");
        Integer recLevel = (Integer)getAbility().parseParameter(parameterArray, "RecLevel");
        
     //   double oldCP = reserve.getCurrentStatCP("END");
        CharacteristicPrimary cp = reserve.getCharacteristic("END");
        
        int end = cp.getCurrentStat();
        
        CharacteristicChangeUndoable ccu = new CharacteristicChangeUndoable(reserve,cp);

        end += recLevel.intValue();
        if ( end > endLevel.intValue() ) end = endLevel.intValue();
        reserve.setCurrentStat("END",end);
     //   double new = reserve.getCurrentStatCP("END");
        
        ccu.setFinalValues();
        be.addUndoableEvent(ccu);
        
        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( reserve.getName() + " recovered " + recLevel.toString() + " END. " +
        reserve.getName() + "'s END is currently at " + Integer.toString(end) + ".",
        BattleEvent.MSG_END));
        
        return false;
    }

}
