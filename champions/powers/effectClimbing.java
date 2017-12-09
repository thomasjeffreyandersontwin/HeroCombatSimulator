/*
 * effectUnconscious.java
 *
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.*;


import champions.exception.BattleEventException;

import java.awt.Color;

/**
 *
 * /**
 *
 * @author  unknown
 * @version
 */
public class effectClimbing extends LinkedEffect{
   
    static private Object[][] parameterArray = {
        {"OCVPenalty","Climbing.OCVPENALTY", String.class, "OCV x 0.5"},
        {"DCVPenalty","Climbing.DCVPENALTY", String.class, "DCV x 0.5"}
    };
    
    /** Creates new effectUnconscious */
    static private Ability unjam = null;
    
    public effectClimbing(Ability ability) {
        super( ability.getName(), "LINKED", true);
        // super( "Jammed", "PERSISTENT", true);
        this.ability = ability;
        setAbility(ability);
        setUnique(true);
        setCritical(true);
        setEffectColor(new Color(153,0,153));
    }
    
    public boolean addEffect(BattleEvent be, Target t)  throws BattleEventException {
        
        boolean added = super.addEffect(be,t);
        if ( added ) {
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is Climbing!", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is Climbing!", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is Climbing!", BattleEvent.MSG_NOTICE);
        }
        return added;
    }
    
    public void removeEffect(BattleEvent be, Target t)  throws BattleEventException {
        super.removeEffect(be,t);
        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer Climbing!", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(t.getName() + " is no longer Climbing!", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is no longer Climbing!", BattleEvent.MSG_NOTICE);
    }
    

    
    //    public void abilityIsActivating(BattleEvent be, Ability ability) throws BattleEventException {
    //        Ability effectAbility = getAbility();
    //        String ocvpenalty = effectAbility.getStringValue("Climbing.OCVPENALTY");
    //        if (ocvpenalty.equals("OCV x 0.5") ) {
    //            Effect e = new effectCombatMultiplier( "Climbing Penalty" , 0.5, 0.0 );
    //            e.addEffect(be, be.getSource() );
    //        }
    //        else if (ocvpenalty.equals("0 OCV") ) {
    //            be.getSource().get
    //            cvList.addTargetCVSet( "Climbing Penalty", 0);
    //        }
    //    }
    
    
    
    public String getDescription() {
        Ability effectAbility = getAbility();
        String ocvpenalty = effectAbility.getStringValue("Climbing.OCVPENALTY");
        String dcvpenalty = effectAbility.getStringValue("Climbing.DCVPENALTY");
        
        StringBuffer sb = new StringBuffer();
        
        sb.append("Combat Modifiers:\n");
        sb.append(ocvpenalty +"\n");
        sb.append(dcvpenalty +"\n");
        
        return sb.toString();
    }
    
    public void addOCVAttackModifiers( CVList cvList , Ability attack) {
        Ability effectAbility = getAbility();
        String ocvpenalty = effectAbility.getStringValue("Climbing.OCVPENALTY");
        if (ocvpenalty.equals("OCV x 0.5") ) {
            cvList.addSourceCVMultiplier("Climbing Penalty", 0.5);
        }
        else if (ocvpenalty.equals("0 OCV") ) {
            cvList.addSourceCVSet( "Climbing Penalty", 0);
        }
    }
    
    
    
    public void addDCVDefenseModifiers( CVList cvList , Ability attack) {
        
        //Ability eAbility = this.getAbility();
        Ability effectAbility = getAbility().getInstanceGroup().getCurrentInstance();
        
        
        String dcvpenalty = effectAbility.getStringValue("Climbing.DCVPENALTY");
        if (dcvpenalty.equals("-1 to DCV") ) {
            cvList.addTargetCVModifier( "Climbing Penalty", -1);
        }
        else if (dcvpenalty.equals("DCV x 0.5") ) {
            cvList.addTargetCVMultiplier( "Climbing Penalty", 0.5);
        }
        else if (dcvpenalty.equals("0 DCV") ) {
            cvList.addTargetCVSet( "Climbing Penalty", 0);
        }
    }
    
    public void adjustDice(BattleEvent be, String targetGroup) {
        Ability effectAbility = getAbility();
        String dcpenalty = effectAbility.getStringValue("Climbing.DCPENALTY");
        
        if (dcpenalty != null && dcpenalty.equals("TRUE") ) {
            Double dc = be.getDoubleValue("Combat.DC" );
            if ( dc == null ) {
                dc = new Double( -2.0 );
            }
            else {
                dc = new Double( dc.doubleValue() -2.0 );
            }
            
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( getName() + " removed 2 DC(s).", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( getName() + " removed 2 DC(s).", BattleEvent.MSG_NOTICE)); // .addMessage( getName() + " removed 2 DC(s).", BattleEvent.MSG_NOTICE);
            be.add("Combat.DC",  dc , true);
        }
    }
    
}


//    public void addActions(Vector actions) {
//
//        // The Next line creates the anonymous class.  It is anonymous, cause you
//        // are not naming the class anything.
//        //
//        // The anonymous class is a subclass of AbstractAction, which is in turn
//        // a subclass of Action.
//        Ability ability = getAbility();
//        Action assignAction = new AbstractAction("Change Climbing penalties") {
//
//            // Define a method in your anonymous class called actionPerformed.
//            // The GUI code knows that when you add an anonymous class of type
//            // Action to the Vector v, that it can take that anonymous class
//            // and execute the actionPerformed method shown below.
//
//            public void actionPerformed(ActionEvent e)
//            {
//                // Do your work.  This is where all the guts of the anonymous
//                // class have to be located.  Nothing can be outside this
//                // method.
//
////                BattleEvent newbe = new BattleEvent(BattleEvent.REMOVE_EFFECT, effectJammed.this, effectJammed.this.getTarget());
////                if ( Battle.currentBattle != null ) {
////                    Battle.currentBattle.addEvent( newbe );
////                   // newbe.setENDPaid(true);
////
////                }
//
//            Integer roll = be.getActivationInfo().getIntegerValue( "Attack.ROLL" );
//
//            } // <--- This closes the method actionPerformed
//        }; // <--- This closes the anonymous class you just created.
//
//        actions.add(assignAction); //  <--- Add the class to the Vector so the gui knows to
//        //  to display it in the actions menu.
//    }
//

//}