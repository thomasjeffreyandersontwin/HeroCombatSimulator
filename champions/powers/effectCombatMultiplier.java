/*
 * effectDying.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;


import champions.Ability;
import champions.BattleEvent;
import champions.CVList;
import champions.ChampionsUtilities;
import champions.Effect;
import champions.Target;
import champions.battleMessage.CVMultiplerMessage;
import champions.exception.BattleEventException;
/**
 *
 * @author  unknown
 * @version
 */
public class effectCombatMultiplier extends Effect {
    
    static private Object[][] parameterArray = {
        {"OCVBonus","Effect.OCVBONUS", Double.class, new Double(0)},
        {"DCVBonus","Effect.DCVBONUS", Double.class, new Double(0)}
    };
    /** Creates new effectCombatModifier */
    public effectCombatMultiplier(String name, double ocvbonus, double dcvbonus) {
        super( name, "PERSISTENT");
        int index;
        
        add("Effect.OCVBONUS", new Double(ocvbonus),  true);
        add("Effect.DCVBONUS", new Double(dcvbonus),  true);
    }
    
    public boolean addEffect(BattleEvent be, Target target) throws BattleEventException {
        boolean result = super.addEffect(be,target);
        if ( result ) {
            Double ocvbonus = (Double)parseParameter( parameterArray, "OCVBonus" );
        Double dcvbonus = (Double)parseParameter( parameterArray, "DCVBonus" );
            if ( ocvbonus != 1 ) {
                be.addBattleMessage( new CVMultiplerMessage(target, true, "OCV", ocvbonus));
            }
            
            if ( dcvbonus != 1 ) {
                be.addBattleMessage( new CVMultiplerMessage(target, true, "DCV", dcvbonus));
            }
        }
        return result;
    }

    public void removeEffect(BattleEvent be, Target target) throws BattleEventException {
        super.removeEffect(be,target);
        
            Double ocvbonus = (Double)parseParameter( parameterArray, "OCVBonus" );
        Double dcvbonus = (Double)parseParameter( parameterArray, "DCVBonus" );
            if ( ocvbonus != 1 ) {
                be.addBattleMessage( new CVMultiplerMessage(target, false, "OCV", ocvbonus));
            }
            
            if ( dcvbonus != 1 ) {
                be.addBattleMessage( new CVMultiplerMessage(target, false, "DCV", dcvbonus));
            }
    }
    
    public String getDescription() {
        Double ocvbonus = (Double)parseParameter( parameterArray, "OCVBonus" );
        Double dcvbonus = (Double)parseParameter( parameterArray, "DCVBonus" );
        
        StringBuffer sb = new StringBuffer();
        
        sb.append("Combat Multipliers:\n");
        if ( ocvbonus != null && ocvbonus > 0) {
            sb.append("   ").append(ChampionsUtilities.toStringWithFractions(ocvbonus)).append(" OCV\n");
        }
        
        if ( dcvbonus != null && dcvbonus > 0) {
            sb.append("   ").append(ChampionsUtilities.toStringWithFractions(dcvbonus)).append(" DCV\n");
        }
        
        return sb.toString();
    }
    
    public boolean prephase(BattleEvent be, Target t) {
        return true;  // Remove the effect on prephase
    }
    
    public void addDCVDefenseModifiers( CVList cvList , Ability attack) {
        Double dcvbonus = (Double)parseParameter( parameterArray, "DCVBonus" );
        
        if ( dcvbonus.doubleValue() != 0 ) {
            cvList.addTargetCVMultiplier( this.getName(), dcvbonus.doubleValue() );
        }
    }
    
    public void addOCVAttackModifiers( CVList cvList , Ability attack) {
        Double ocvbonus = (Double)parseParameter( parameterArray, "OCVBonus" );
        
        if ( ocvbonus.doubleValue() != 0 ) {
            cvList.addSourceCVMultiplier( this.getName(), ocvbonus.doubleValue() );
        }
    }
}