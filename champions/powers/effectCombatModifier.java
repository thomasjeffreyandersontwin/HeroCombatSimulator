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
import champions.battleMessage.CVModiferMessage;
import champions.battleMessage.DCSummaryMessage;
import champions.exception.BattleEventException;
import champions.interfaces.CombatLevelProvider;

/**
 *
 * @author  unknown
 * @version
 */
public class effectCombatModifier extends Effect {

    /** CombatLevelProvider to get ocv, dcv, and dc from. */
    protected CombatLevelProvider combatLevelProvider = null;

    /** ocv modifier if combat level provider is null. */
    protected int ocvModifier = 0;
    /** dcv modifier if combat level provider is null. */
    protected int dcvModifier = 0;
    /** dc modifier if combat level provider is null. */
    protected int dcModifier = 0;

    /** indicates the effects should be removed automatically during pre-phase */
    protected boolean removeAutomatically;

    public effectCombatModifier(String name, CombatLevelProvider combatLevelProvider, boolean removeAutomatically) {
        super(name, "PERSISTENT");
        setHidden(false);
        this.combatLevelProvider = combatLevelProvider;
        this.removeAutomatically = removeAutomatically;
    }

    /** Creates new effectCombatModifier */
    public effectCombatModifier(String name, int ocvModifier, int dcvModifier, int dcModifier,boolean removeAutomatically) {
        super(name, "PERSISTENT");
        setHidden(false);

        this.ocvModifier = ocvModifier;
        this.dcvModifier = dcvModifier;
        this.dcModifier = dcModifier;
        this.removeAutomatically = removeAutomatically;
    }

    protected int getOCVModifier() {
        if (combatLevelProvider != null) {
            return combatLevelProvider.getConfiguredOCVModifier();
        } else {
            return ocvModifier;
        }
    }

    protected int getDCVModifier() {
        if (combatLevelProvider != null) {
            return combatLevelProvider.getConfiguredDCVModifier();
        } else {
            return dcvModifier;
        }
    }

    protected int getDCModifier() {
        if (combatLevelProvider != null) {
            return combatLevelProvider.getConfiguredDCModifier();
        } else {
            return dcModifier;
        }
    }

    @Override
    public String getDescription() {
        StringBuffer sb = new StringBuffer();

        sb.append("Combat Modifiers:\n");
        if ( getOCVModifier() != 0 ) {
            sb.append(ChampionsUtilities.toSignedString(getOCVModifier()) + " OCV Level(s)\n");
        }

        if ( getDCVModifier() != 0 ) {
            sb.append(ChampionsUtilities.toSignedString(getDCVModifier()) + " DCV Level(s)\n");
        }

        if ( getDCModifier() != 0 ) {
            if ( getDCModifier() == 1 ) {
                sb.append(ChampionsUtilities.toSignedString(getDCModifier()) + " Damage Class\n");
            }
            else {
                sb.append(ChampionsUtilities.toSignedString(getDCModifier()) + " Damage Classes\n");
            }
        }

        return sb.toString();
    }

    @Override
    public boolean addEffect(BattleEvent be, Target target) throws BattleEventException {
        boolean result = super.addEffect(be, target);
        if (result) {
            int ocvbonus = getOCVModifier();
            if (ocvbonus != 0) {
                be.addBattleMessage(new CVModiferMessage(target, true, "OCV", ocvbonus));
            }

            int dcvbonus = getDCVModifier();
            if (dcvbonus != 0) {
                be.addBattleMessage(new CVModiferMessage(target, true, "DCV", dcvbonus));
            }

        }
        return result;
    }

    @Override
    public void removeEffect(BattleEvent be, Target target) throws BattleEventException {
        super.removeEffect(be, target);

        int ocvbonus = getOCVModifier();
        int dcvbonus = getDCVModifier();
        if (ocvbonus != 0) {
            be.addBattleMessage(new CVModiferMessage(target, false, "OCV", ocvbonus));
        }

        if (dcvbonus != 0) {
            be.addBattleMessage(new CVModiferMessage(target, false, "DCV", dcvbonus));
        }

    }

    public boolean prephase(BattleEvent be, Target t) {
        return removeAutomatically;  // Remove the effect on prephase
    }

    @Override
    public void addDCVDefenseModifiers(CVList cvList, Ability attack) {
        Integer dcvbonus = getDCVModifier();

        if (dcvbonus.intValue() != 0) {
            cvList.addTargetCVModifier(this.getName(), dcvbonus.intValue());
        }

    }

    @Override
    public void addOCVAttackModifiers(CVList cvList, Ability attack) {
        Integer ocvbonus = getOCVModifier();

        if (ocvbonus.intValue() != 0) {
            cvList.addSourceCVModifier(this.getName(), ocvbonus.intValue());
        }
    }

    @Override
    public void adjustDice(BattleEvent be, String targetGroup) {
        int dcbonus = getDCModifier();

        if ( dcbonus != 0) {
            Double dc = be.getDoubleValue("Combat.DC");
            if (dc == null) {
                dc = new Double(dcbonus);
            } else {
                dc = new Double(dc + dcbonus);
            }

            if (dcbonus > 0) {
                be.addBattleMessage(new DCSummaryMessage(getTarget(), dcbonus));
            }
            be.add("Combat.DC", dc, true);
        }
    }
}