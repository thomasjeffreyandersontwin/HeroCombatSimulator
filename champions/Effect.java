/*
 * EffectAdapter.java
 *
 * Created on October 2, 2000, 4:04 PM
 */
package champions;

import champions.Ability.AbilityChangeUndoable;
import champions.attackTree.AttackTreeNode;
import champions.battleMessage.AbilityCPChangeBattleMessage;
import champions.battleMessage.SimpleBattleMessage;
import champions.battleMessage.StatCPChangeBattleMessage;
import champions.battleMessage.StatChangeBattleMessage;
import champions.battleMessage.StatChangeType;
import champions.battleMessage.LegacyBattleMessage;
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.IndexIterator;
import champions.interfaces.Undoable;
import champions.powers.effectCannotBeStunned;
import champions.powers.effectDoesNotBleed;
import champions.powers.effectDying;
import champions.powers.effectStunned;
import champions.powers.effectUnconscious;
import java.util.Iterator;
import java.util.Vector;
import champions.exceptionWizard.ExceptionWizard;
import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * 
 * @author unknown
 * @version Notes on Subeffects:
 * 
 * As of 1.7.0, there are seven types of Subeffects: AID_RECEIVED, DRAIN, DAMAGE, HEAL, INCREASE, DESCREASE, SET.
 * 
 * AID_RECEIVED: Increases the CharacterPoints allocated to Stat/Ability and
 * adjusts the TotalAid, TotalAdjustment to prepare for fading. Only subeffects which
 * will eventually fade should use type AID_RECEIVED.
 * 
 * AID_RECEIVED has additional parameter Subeffect#.FADEBELOWBASE = TRUE/FALSE.  If True, the aid/adjustment
 * counts will be adjusted so that ALL of the aid will eventually fade.  If false, only extra
 * aid above the base amount will be prepared to fade.
 * 
 * AID_RECEIVED can only have an AffectType of CURRENT.
 * 
 * The default LIMIT for AID_RECEIVED is TARGET_LIMITED.
 * 
 * DRAIN: Decreases the Character Points allocated to the Stat/Ability and adjusts the
 * TtotalAid, TotalAdjustment to prepare for fading.  Only subeffects which will eventually fade
 * should use type DRAIN.
 * 
 * DRAIN can only have an AffectType of CURRENT.
 * 
 * The default LIMIT for DRAIN is TARGET_LIMITED.
 * 
 * DAMAGE: Decreases the Character Points allocated to the Stat/Ability.  Damage is consider
 * instantaneous and non-fadeable.
 * 
 * DAMAGE can have an AffectType of CURRENT.
 * 
 * The default LIMIT for DAMAGE is TARGET_LIMITED.
 * 
 * The DAMAGE subeffect should define ISKILLING equals to TRUE or FALSE.  Default is TRUE.
 * 
 * HEAL: Increases the Character Points allocated to the Stat/Ability.  Heal is consider
 * instantaneous and non-fadeable.
 * 
 * HEAL can have an AffectType of CURRENT.
 * 
 * The default LIMIT for HEAL is BASE_VALUE.
 * 
 * INCREASE: Increases the Character Points allocated to the Stat/Ability.  Increase is consider
 * instantaneous and non-fadeable.
 * 
 * INCREASE can have an AffectType of CURRENT, BASE, or ADJUSTED_BASE.
 * 
 * The default LIMIT for INCREASE is UNLIMITED.
 * 
 * DECREASE: Increases the Character Points allocated to the Stat/Ability.  Decrease is consider
 * instantaneous and non-fadeable.
 * 
 * DECREASE can have an AffectType of CURRENT, BASE, or ADJUSTED_BASE.
 * 
 * The default LIMIT for DECREASE is UNLIMITED
 * 
 * SET: Absolutely sets the Character Points for teh Stat/Ability.
 * 
 * Set can have an AffectType of CURRENT, BASE, or ADJUSTED_BASE.
 * 
 * The LIMIT parameter is ignore with respect to SETs.
 * 
 * RECOVERY: Recovers the specified number of point to a Stat.  Recovery is consider
 * instantaneous and non-fadeable.
 * 
 * The default LIMIT for RECOVERY is BASE_VALUE.
 * 
 * RECOVERY can have an AffectType of CURRENT.
 * 
 * The Following Matrix Defines the possible combinations of different parameters:
 * Type         VersusType               Removable      AffectType
 * -----------------------------------------------------------------------------------------------------------------
 * AID_RECEIVED          STATCP, ABILITY          No             ADJUSTED
 * DRAIN        STATCP, ABILITY          No             ADJUSTED
 * HEAL         STAT                     No             CURRENT
 * DAMAGE       STAT                     No             CURRENT
 * INCREASE     STAT                     Yes            CURRENT, ADJUSTED
 * DECREASE     STAT                     Yes            CURRENT, ADJUSTED
 * SET          STATCP, STAT, ABILITY    No             CURRENT, BASE, ADJUSTED*
 * RECOVERY     STAT                     No             CURRENT
 *   *Base, AdjustedBase only can be used with STATCP and STAT VersusTypes
 * 
 * <P>
 * 
 * <B>Understanding Subeffect Defenses</B>
 * 
 * All subeffects have two defensive parameters, DEFTYPE and DEFSPECIAL.
 * The DEFTYPE specifies which of the three defense types ("PD", "ED", "MD") apply
 * for this subeffect.  The DEFSPECIAL specified how the defense type is applied.<P>
 * 
 * DEFSPECIAL can have the settings "NONE", "NORMAL", "KILLING", "NND", "POWERDEFENSE", and
 * "AVLD".  In the case of "NONE" and "POWERDEFENSE" the DEFTYPE is ignored
 * and special handing is used to determine the amount of defenses.<P>
 * 
 * If DEFSPECIAL is "NORMAL", "KILLING", "NND", "AVLD", DEFTYPE is consulted to determine the
 * total defenses applied.  If DEFTYPE is "NONE" no defenses are applied.  Otherwise
 * DEFTYPE can contain one or more of "PD", "ED", or "MD", seperated by "+" symbols.
 * In the case of a killing attack, the BattleEngine will transform PD and ED damage
 * to be resistent when appropriate.<P>
 * 
 * The method setSubeffectDefense should be used to set the initial subeffect DEFTYPE
 * and DEFSPECIAL.  After that, the DEFTYPE can be adjusted using the
 * addSubeffectDefenseType method.  hasSubeffectDefenseType can be used to determine
 * if a defense type is present for a subeffect.<P>
 */
public class Effect extends DetailList implements ChampionsConstants, Serializable {

    static final long serialVersionUID = -2984005590793526343L;
    static final int DEBUG = 0;
    public static final int NORMAL = 0; // Includes all Effect which are not hidden (Both Critical and Non Critical)
    public static final int HIDDEN = 1; // Includes only the Hidden Effects
    public static final int CRITICAL = 2; // Includes only the Critical Effects
    public static final int NONCRITICAL = 3; // Includes only the Non Critical Effects
    public static final int ALL = 4; // Includes all effects, hidden, non-hidden, critical, non-critical
    protected int effectPriority = 2;
    protected boolean critical = false;
    protected boolean hidden = false;
    protected boolean unique = false;
    public String name;
    protected String ctype;
    protected Color effectColor = null;

    /** Creates new EffectAdapter */
    public Effect(String name) {
        setFireChangeByDefault(false);
        setName(name);
        setCType("INSTANT");
    }

    public Effect(String name, String ctype) {
        setFireChangeByDefault(false);
        setName(name);
        setCType(ctype);
    }

    public Effect(String name, String ctype, boolean unique) {
        setFireChangeByDefault(false);
        setName(name);
        setCType(ctype);
        setUnique(unique);
    }

    public void setName(String name) {
        if (this.name != name) {

            String oldName = this.name;

            this.name = name;

            firePropertyChange(this, "Effect.NAME", oldName, name);
        }
    }

    public void setCType(String ctype) {
        if (this.ctype != ctype) {
            this.ctype = ctype;
        }
    }

    public String getCType() {
        return ctype;
    }

    /** Adds the effect to the target.
     *
     *  Subclasses can override this effect to perform custom actions when the
     *  effect is added.  However, due to the design of the undoable system, the
     *  subclass can NOT rely on the removeEffect to be called when an effect is
     *  undone.  Thus effects with overriden addEffect method, most also post
     *  undoables to the battleEvent to undo/redo anything special they might 
     *  have done.  If this is not done, undoability will not work.
     *
     *  Subclasses should always call the super method first and only perform
     *  the addEffect actions if the super method returns true.
     */
    public boolean addEffect(BattleEvent be, Target target)
            throws BattleEventException {
        String ctype;
        int index;

        boolean result = false;


        List<Effect> removeList = new ArrayList<Effect>();

        ctype = getCType();

        if (isUnique()) {
            if (target.hasEffect(getName())) {
                return false;
            }
        }

        if (ctype.equals("PERSISTENT")) {
            target.addEffectEntry(this);

            add("Effect.TARGET", target);
        } else if (ctype.equals("LINKED")) {
            
            if ( this instanceof LinkedEffect ) {
                LinkedEffect le = (LinkedEffect)this;
                Ability linkedAbility = le.getAbility();
                
                if ( linkedAbility == null ) {
                    // If the linkedAbility is null fall back to unique names...
                    if ( target.hasEffect(getName()) ) {
                        return false;
                    }
                }
                else {
                    List<Effect> effects = target.getEffects();
                    for(Effect effect : effects ) {
                        if ( effect instanceof LinkedEffect ) {
                            LinkedEffect le2 = (LinkedEffect)effect;

                            if ( linkedAbility.equals( le2.getAbility() ) ) {
                                return false;
                            }

                        }
                    }
                }
                
            }
            else {
                // If the LINKED effect isn't truely a LinkedEffect, fall back
                // to using the name to identify the correct effect
                if ( target.hasEffect(getName()) ) {
                    return false;
                }
            }
            
            target.addEffectEntry(this);

            int aindex;
            ActivationInfo ai = be.getActivationInfo();
            if (ai != null) {
                aindex = ai.createIndexed("Effect", "EFFECT", this);
                ai.addIndexed(aindex, "Effect", "TARGET", target);
                add("Effect.AILINK", ai);
            }
            add("Effect.TARGET", target);
        }


        // Everything is good.  Post event for undo
        postEvent(be, target, true);

        // Apply Effects
        applySubEffects(be, target);

        int count, i;
        Effect existingEffect;
        // Process All Target postAddEffect conditions


        count = target.getEffectCount();
        for (i = 0; i < count; i++) {
            existingEffect = target.getEffect(i);
            if (existingEffect.posteffect(be, this, target) == true) {
                removeList.add(existingEffect);
            }
        }

        // Let target respond to getting an effect...
        target.posteffect(be, this);

        Iterator<Effect> it = removeList.iterator();
        while (it.hasNext()) {
            Effect e = it.next();
            if (target.hasEffect(e)) {
                e.removeEffect(be, target);
            }
        }

        result = true;

        return result;
    }

    public void removeEffect(BattleEvent be, Target target)
            throws BattleEventException {
        boolean targetHasEffect = target.hasEffect(this);

        String ctype = getCType();

        if (ctype.equals("PERSISTENT")) {
            if (targetHasEffect) {
                removeSubEffects(be, target);
                target.removeEffectEntry(this);
            }
        } else if (ctype.equals("LINKED")) {
            if (targetHasEffect) {
                ActivationInfo ailink = (ActivationInfo) getValue("Effect.AILINK");
                // Remove Effect from both Target and Ability
                int aindex = ailink.findIndexed("Effect", "EFFECT", this);
                if (aindex != -1) {
                    ailink.removeAllIndexed(aindex, "Effect");
                }

                if (ailink.getIndexedSize("Effect") == 0 && ailink.isActivated() && ailink.hasAbilityLinks()) {
                    // If this doesn't have any AbilityLinks, it is probably in the state of
                    // deactivating anyway, so don't queue a deactivation.
                    // However, if the effect is being removed and there are still ability links
                    // then it probably isn't being deactivated at the time, so deactivate it.
                    if (Battle.currentBattle != null) {
                        BattleEvent newBE = new BattleEvent(BattleEvent.DEACTIVATE, ailink);
                        be.addUndoableEvent(Battle.currentBattle.addEvent(newBE));
                    //be.embedBattleEvent(newBE);
                    }
                }

                removeSubEffects(be, target);

                target.removeEffectEntry(this);
            }
        } else if (ctype.equals("INSTANT")) {
            removeSubEffects(be, target);
        }

        postEvent(be, target, false);

        int count, i;
        Effect existingEffect;
        // Process All Target postAddEffect conditions
        count = target.getEffectCount();
        for (i = 0; i < count; i++) {
            //existingEffect = (Effect) target.getIndexedValue(i,"Effect","EFFECT");
            existingEffect = target.getEffect(i);
            existingEffect.posteffect(be, this, target);
        }

    // Let target respond to getting an effect...
    //target.posteffect(be, this);
    }

    /*public void adjustEffectSublist(Target target, boolean added) {
    int index;
    if ( isCritical() ) {
    if ( added ) {
    index = target.createIndexed( "CriticalEffect","EFFECT", this ,false);
    target.fireIndexedChanged("CriticalEffect");
    }
    else {
    index = target.findIndexed( "CriticalEffect","EFFECT", this);
    if ( index != -1 ) {
    target.removeAllIndexed(index, "CriticalEffect",false);
    target.fireIndexedChanged("CriticalEffect");
    }
    }
    }
    else if ( isHidden() ) {
    if ( added ) {
    index = target.createIndexed( "HiddenEffect","EFFECT", this ,false);
    target.fireIndexedChanged("HiddenEffect");
    }
    else {
    index = target.findIndexed( "HiddenEffect","EFFECT", this);
    if ( index != -1 ) {
    target.removeAllIndexed(index, "HiddenEffect",false);
    target.fireIndexedChanged("HiddenEffect");
    }
    }
    }
    else {
    if ( added ) {
    index = target.createIndexed( "NoncriticalEffect","EFFECT", this ,false);
    target.fireIndexedChanged("NoncriticalEffect");
    }
    else {
    index = target.findIndexed( "NoncriticalEffect","EFFECT", this);
    if ( index != -1 ) {
    target.removeAllIndexed(index, "NoncriticalEffect",false);
    target.fireIndexedChanged("NoncriticalEffect");
    }
    }
    }
    }*/
    /* The following functions are run every time an attack occurs, in the following order:
    getDefenses
    defending character's -> predefense
    attacking character's -> preattack
    advantages/limitation -> prepower
    applyDefenses
    defending character's -> postdefense
    attacking character's -> postattack
    advantages/limitation -> postpower
    
    Additional, effects have priorities from 0 to 5:
    0 can look at subeffects prior to any changes by other effects
    1
    2 normal priority.  Make changes
    3 high priority changes.  After normal effects change things
    4 can change the subeffects any way they want...final say on changes
    5 view final results
     */
    public void preattack(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget)
            throws BattleEventException {
    }

    public void postattack(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget)
            throws BattleEventException {
    }

    public void predefense(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget)
            throws BattleEventException {
    }

    public void postdefense(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget)
            throws BattleEventException {
    }

    /** Executes the prephase for the effect.
     *
     * @return Returns true if the effect should be removed after prephse processing.
     */
    public boolean prephase(BattleEvent be, Target t)
            throws BattleEventException {
        return false;
    }

    public boolean presegment(BattleEvent be, Target t)
            throws BattleEventException {
        return false;
    }

    public boolean postturn(BattleEvent be, Target t)
            throws BattleEventException {
        return false;
    }

    /*  public int addSubeffectInfo(String name, String effectType, String defType, String defSpecial, String versustype, String versus) {
    // Add Effects
    int index = -1;
    
    if ( (index = findIndexed("Subeffect","NAME",name) ) == -1 ) {
    index = createIndexed(   "Subeffect", "NAME", name)  ;
    }
    addIndexed( index,   "Subeffect", "EFFECTTYPE", effectType,  true);
    addIndexed( index,   "Subeffect", "DEFTYPE", defType,  true );
    addIndexed( index,   "Subeffect", "DEFSPECIAL", defSpecial,  true );
    addIndexed( index,   "Subeffect", "VERSUSTYPE", versustype,  true);
    addIndexed( index,   "Subeffect", "VERSUS", versus,  true);
    
    return index;
    } */
    /* public int addSubeffectInfo(String name, String effectType, String defType, String defSpecial, String versustype, String versus, Object value) {
    // Add Effects
    int index = -1;
    
    if ( (index = findIndexed("Subeffect","NAME",name) ) == -1 ) {
    index = createIndexed(   "Subeffect", "NAME", name)  ;
    }
    addIndexed( index,   "Subeffect", "EFFECTTYPE", effectType,  true);
    addIndexed( index,   "Subeffect", "DEFTYPE", defType,  true );
    addIndexed( index,   "Subeffect", "DEFSPECIAL", defSpecial,  true );
    addIndexed( index,   "Subeffect", "VERSUSTYPE", versustype,  true);
    addIndexed( index,   "Subeffect", "VERSUS", versus,  true);
    addIndexed( index,   "Subeffect", "VALUE", value,  true);
    
    return index;
    } */
    /*   public int addSubeffectInfo(String name, String effectType, Ability versus, Object value) {
    // Add Effects
    int index = -1;
    
    if ( (index = findIndexed("Subeffect","NAME",name) ) == -1 ) {
    index = createIndexed(   "Subeffect", "NAME", name)  ;
    }
    addIndexed( index,   "Subeffect", "EFFECTTYPE", effectType,  true);
    addIndexed( index,   "Subeffect", "DEFTYPE", "POWERDEFENSE",  true );
    addIndexed( index,   "Subeffect", "DEFSPECIAL", "NONE",  true );
    addIndexed( index,   "Subeffect", "VERSUSTYPE", "ABILITY",  true);
    addIndexed( index,   "Subeffect", "VERSUS", versus,  true);
    addIndexed( index,   "Subeffect", "VALUE", value,  true);
    
    return index;
    } */
    public int addDamageSubeffect(String name, String stat, double amount, String defType, String defSpecial) {
        int index = -1;

        if ((index = findIndexed("Subeffect", "NAME", name)) == -1) {
            index = createIndexed("Subeffect", "NAME", name);
        }
        addIndexed(index, "Subeffect", "EFFECTTYPE", "DAMAGE", true);
        addIndexed(index, "Subeffect", "DEFTYPE", defType, true);
        addIndexed(index, "Subeffect", "DEFSPECIAL", defSpecial, true);
        addIndexed(index, "Subeffect", "VERSUSTYPE", "STAT", true);
        addIndexed(index, "Subeffect", "VERSUS", stat, true);
        addIndexed(index, "Subeffect", "VALUE", new Double(amount), true);
        addIndexed(index, "Subeffect", "LIMIT", new Double(TARGET_LIMITED), true);

        return index;
    }

    public int addHealSubeffect(String name, String type, Object versus, double amount) {
        int index = -1;

        if ((index = findIndexed("Subeffect", "NAME", name)) == -1) {
            index = createIndexed("Subeffect", "NAME", name);
        }
        addIndexed(index, "Subeffect", "EFFECTTYPE", "HEAL", true);
        addIndexed(index, "Subeffect", "DEFTYPE", "NONE", true);
        addIndexed(index, "Subeffect", "DEFSPECIAL", "NORMAL", true);
        addIndexed(index, "Subeffect", "VERSUSTYPE", type, true);
        addIndexed(index, "Subeffect", "VERSUS", versus, true);
        addIndexed(index, "Subeffect", "VALUE", new Double(amount), true);
        addIndexed(index, "Subeffect", "LIMIT", new Double(BASE_VALUE));

        return index;
    }

    public int addRecoverySubeffect(String name, String stat, double amount) {
        int index = -1;

        if ((index = findIndexed("Subeffect", "NAME", name)) == -1) {
            index = createIndexed("Subeffect", "NAME", name);
        }
        addIndexed(index, "Subeffect", "EFFECTTYPE", "RECOVERY", true);
        addIndexed(index, "Subeffect", "DEFTYPE", "NONE", true);
        addIndexed(index, "Subeffect", "DEFSPECIAL", "NORMAL", true);
        addIndexed(index, "Subeffect", "VERSUSTYPE", "STAT", true);
        addIndexed(index, "Subeffect", "VERSUS", stat, true);
        addIndexed(index, "Subeffect", "VALUE", new Double(amount), true);
        addIndexed(index, "Subeffect", "LIMIT", new Double(BASE_VALUE));

        return index;
    }

    public int addIncreaseSubeffect(String name, String stat, double amount, String affectsType) {
        int index = -1;

        if ((index = findIndexed("Subeffect", "NAME", name)) == -1) {
            index = createIndexed("Subeffect", "NAME", name);
        }
        addIndexed(index, "Subeffect", "EFFECTTYPE", "INCREASE", true);
        addIndexed(index, "Subeffect", "DEFTYPE", "NONE", true);
        addIndexed(index, "Subeffect", "DEFSPECIAL", "NORMAL", true);
        addIndexed(index, "Subeffect", "VERSUSTYPE", "STAT", true);
        addIndexed(index, "Subeffect", "VERSUS", stat, true);
        addIndexed(index, "Subeffect", "AFFECTSTYPE", affectsType, true);
        addIndexed(index, "Subeffect", "VALUE", new Double(amount), true);
        addIndexed(index, "Subeffect", "LIMIT", new Double(UNLIMITED));

        return index;
    }

    public int addDecreaseSubeffect(String name, String stat, double amount, String affectsType) {
        int index = -1;

        if ((index = findIndexed("Subeffect", "NAME", name)) == -1) {
            index = createIndexed("Subeffect", "NAME", name);
        }
        addIndexed(index, "Subeffect", "EFFECTTYPE", "DECREASE", true);
        addIndexed(index, "Subeffect", "DEFTYPE", "NONE", true);
        addIndexed(index, "Subeffect", "DEFSPECIAL", "NORMAL", true);
        addIndexed(index, "Subeffect", "VERSUSTYPE", "STAT", true);
        addIndexed(index, "Subeffect", "VERSUS", stat, true);
        addIndexed(index, "Subeffect", "AFFECTSTYPE", affectsType, true);
        addIndexed(index, "Subeffect", "VALUE", new Double(amount), true);
        addIndexed(index, "Subeffect", "LIMIT", new Double(UNLIMITED));

        return index;
    }

    public int addAidSubeffect(String name, String versusType, Object versus, double amount) {
        int index = -1;

        if ((index = findIndexed("Subeffect", "NAME", name)) == -1) {
            index = createIndexed("Subeffect", "NAME", name);
        }
        addIndexed(index, "Subeffect", "EFFECTTYPE", "AID", true);
        addIndexed(index, "Subeffect", "DEFTYPE", "NONE", true);
        addIndexed(index, "Subeffect", "DEFSPECIAL", "NORMAL", true);
        addIndexed(index, "Subeffect", "VERSUSTYPE", versusType, true);
        addIndexed(index, "Subeffect", "VERSUS", versus, true);
        //  addIndexed( index,   "Subeffect", "FADEBELOWBASE", fadeBelowBase ? "TRUE" : "FALSE",  true);
        addIndexed(index, "Subeffect", "VALUE", new Double(amount), true);
        addIndexed(index, "Subeffect", "LIMIT", new Double(TARGET_LIMITED));

        return index;
    }

    public int addDrainSubeffect(String name, String versusType, Object versus, double amount) {
        int index = -1;

        if ((index = findIndexed("Subeffect", "NAME", name)) == -1) {
            index = createIndexed("Subeffect", "NAME", name);
        }
        addIndexed(index, "Subeffect", "EFFECTTYPE", "DRAIN", true);
        addIndexed(index, "Subeffect", "DEFTYPE", "POWERDEFENSE", true);
        addIndexed(index, "Subeffect", "DEFSPECIAL", "POWERDEFENSE", true);
        addIndexed(index, "Subeffect", "VERSUSTYPE", versusType, true);
        addIndexed(index, "Subeffect", "VERSUS", versus, true);
        addIndexed(index, "Subeffect", "VALUE", new Double(amount), true);
        addIndexed(index, "Subeffect", "LIMIT", new Double(TARGET_LIMITED));

        return index;
    }

    public int addSetSubeffect(String name, String versusType, Object versus, double amount, String affectsType) {
        int index = -1;

        if ((index = findIndexed("Subeffect", "NAME", name)) == -1) {
            index = createIndexed("Subeffect", "NAME", name);
        }
        addIndexed(index, "Subeffect", "EFFECTTYPE", "SET", true);
        addIndexed(index, "Subeffect", "DEFTYPE", "NAME", true);
        addIndexed(index, "Subeffect", "DEFSPECIAL", "NORMAL", true);
        addIndexed(index, "Subeffect", "VERSUSTYPE", versusType, true);
        addIndexed(index, "Subeffect", "VERSUS", versus, true);
        addIndexed(index, "Subeffect", "VALUE", new Double(amount), true);
        addIndexed(index, "Subeffect", "AFFECTSTYPE", affectsType, true);
        addIndexed(index, "Subeffect", "LIMIT", new Double(UNLIMITED));

        return index;
    }

    public void setLimit(int index, double limit) {
        addIndexed(index, "Subeffect", "LIMIT", new Double(limit), true);
    }

    protected void applySubEffects(BattleEvent be, Target target)
            throws BattleEventException {
        // if ( Battle.currentBattle.dle != null ) Battle.currentBattle.dle.setDetailList(this);
        int count = getIndexedSize("Subeffect");

        int i;
        String type, versustype;
        Object versus;
        int current, base, adjustedBase;
        int currentCP, baseCP, adjustedBaseCP;
        double amount, limit, originalAmount;
        CharacteristicChangeUndoable undoable;
        boolean silent;
        String stat, affectsType;
        Ability ability;
        Double v = null;
        Double l = null;

        boolean isAlive = target.isAlive();

        for (i = 0; i < count; i++) {
            // First check to see that all the important information is already there
            if ((type = getIndexedStringValue(i, "Subeffect", "EFFECTTYPE")) == null || (v = getIndexedDoubleValue(i, "Subeffect", "VALUE")) == null || (versustype = getIndexedStringValue(i, "Subeffect", "VERSUSTYPE")) == null || (versus = getIndexedValue(i, "Subeffect", "VERSUS")) == null) {
                String name = getIndexedStringValue(i, "Subeffect", "NAME");
                Exception e = new Exception("Malformed Subeffect (name: " + name + ")");
                ExceptionWizard.postException(e);
                continue;
            }

            amount = v.doubleValue();
            l = getIndexedDoubleValue(i, "Subeffect", "LIMIT");
            limit = (l == null) ? UNLIMITED : l.doubleValue();
            silent = getIndexedBooleanValue(i, "Subeffect", "SILENT");
            affectsType = getIndexedStringValue(i, "Subeffect", "AFFECTSTYPE");

            if (versustype.equals("STAT")) {
                // It is versustype a STAT.

                // Set stat for easy access.
                stat = (String) versus;

                // Make sure the target contains the stat, otherwise ignore the subeffect.
                if (!target.hasStat(stat)) {
                    continue;
                }
                boolean figured = getIndexedBooleanValue(i, "Subeffect", "AFFECTSFIGURED");

                // Actually apply the adjustment based on type
                if (type.equals("DAMAGE")) {
                    originalAmount = amount;

                    undoable = target.applyDamageToCurrentStat(stat, amount, limit);
                    // addIndexed(i,"Subeffect","APPLYUNDOINFO", ai, true);
                    addAddUndoable(be, undoable);

                    amount = undoable.getAmount();
                    addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(amount), true);

                    if (amount > 0 && !silent) {
//                        be.addBattleMessage( new LegacyBattleMessage(target.getName() + " took " + Integer.toString((int)amount) + " damage to " + (String)versus + ".  ",
//                                BattleEvent.MSG_MISS);

                        be.addBattleMessage(new StatChangeBattleMessage(target, StatChangeType.DAMAGE, (String) versus, (int) amount));
                    }

                    originalAmount -= amount;
                    if (originalAmount < 0) {
                        originalAmount = 0;
                    }
                    addIndexed(i, "Subeffect", "VALUE", new Double(originalAmount), true);

                    if (amount > 0 && isAlive && (stat).equals("STUN")) {
                        if (target.hasStat("CON")) {
                            // Has con, so lets see if he is stunned
                            int con = target.getCurrentStat("CON");
                            if (amount > con) {
                                processStunned(be, target);
                            }
                        }
                        if (target.getCurrentStat("STUN") <= 0) {
                            processKnockout(be, target);
                        }
                    }
                    if (isAlive && (stat).equals("BODY")) {
                        if (amount > 0 && target.getCurrentStat("BODY") <= 0) {
                            processDying(be, target);
                        }
                    }
                } else if (type.equals("HEAL")) {
                    undoable = target.applyHealToCurrentStat(stat, amount, limit);
                    // addIndexed(i,"Subeffect","APPLYUNDOINFO", ai, true);
                    addAddUndoable(be, undoable);

                    amount = undoable.getAmount();
                    addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(amount), true);

                    if (amount > 0 && !silent) {
//                        be.addBattleMessage( new LegacyBattleMessage(target.getName() + " received " + Integer.toString((int)amount) + " healing to " + stat + ".  ",
//                                BattleEvent.MSG_COMBAT);
                        be.addBattleMessage(new StatChangeBattleMessage(target, StatChangeType.HEALING_RECEIVED, (String) versus, (int) amount));
                    }

                    if (stat.equals("STUN") && target.isUnconscious() && target.getCurrentStat("STUN") >= 1) {
                        processNotUnconscious(be, target);

                    }

                    if (stat.equals("BODY")) {
                        if (target.getCurrentStat("BODY") >= 1) {
                            processNotDying(be, target);
                        }
                    }
                } else if (type.equals("RECOVERY")) {
                    undoable = target.applyHealToCurrentStat(stat, amount, limit);
                    //addIndexed(i,"Subeffect","APPLYUNDOINFO", ai, true);
                    addAddUndoable(be, undoable);

                    amount = undoable.getAmount();
                    addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(amount), true);

                    if (amount > 0 && !silent) {
//                        be.addBattleMessage( new LegacyBattleMessage(target.getName() + " recovered " + Integer.toString((int)amount) + " " + stat + ".",
//                                BattleEvent.MSG_COMBAT);
                        be.addBattleMessage(new StatChangeBattleMessage(target, StatChangeType.RECOVERY, (String) versus, (int) amount));
                    }

                    if (stat.equals("STUN") && target.isUnconscious() && target.getCurrentStat("STUN") >= 1) {
                        processNotUnconscious(be, target);

                    }

                    if (stat.equals("BODY")) {
                        if (target.getCurrentStat("BODY") >= 1) {
                            processNotDying(be, target);
                        }
                    }
                } else if (type.equals("INCREASE")) {
                    if (affectsType.equals(CURRENT_STAT)) {
                        undoable = target.applyIncreaseToCurrentStat(stat, amount, limit);

                    } else if (affectsType.equals(ADJUSTED_STAT)) {
                        undoable = target.applyIncreaseToAdjustedStat(stat, amount, limit, figured);
                    } /*    else if ( affectsType.equals( ADJUSTED_AND_CURRENT_STAT ) ){
                    StatChangeUndoable ai1 = target.applyIncreaseToCurrentStat(stat, amount, limit, figured);
                    StatChangeUndoable ai2 = target.applyIncreaseToAdjustedBaseStat(stat, amount, limit, figured);
                    
                    ai = ai1;
                    ai.setFinalValues(ai2);
                    } */ else {
                        System.out.println("Invalide affectsType on Subeffect.");
                        continue;
                    }

                    if (amount > 0 && !silent) {
//                        be.addBattleMessage( new LegacyBattleMessage(target.getName() + " gained " + Integer.toString((int)amount) + " to " + stat + ".  ",
//                                BattleEvent.MSG_COMBAT);
                        be.addBattleMessage(new StatChangeBattleMessage(target, StatChangeType.INCREASE, (String) versus, (int) amount));

                    }

                    //addIndexed(i,"Subeffect","APPLYUNDOINFO", ai, true);
                    addAddUndoable(be, undoable);

                    amount = undoable.getAmount();
                    addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(amount), true);

                    if (stat.equals("STUN") && target.isUnconscious() && target.getCurrentStat("STUN") >= 1) {
                        processNotUnconscious(be, target);

                    }

                    if (stat.equals("BODY")) {
                        if (target.getCurrentStat("BODY") >= 1) {
                            processNotDying(be, target);
                        }
                    }

                } else if (type.equals("DECREASE")) {
                    if (affectsType.equals(CURRENT_STAT)) {
                        undoable = target.applyDecreaseToCurrentStat(stat, amount, limit);
                    } else if (affectsType.equals(ADJUSTED_STAT)) {
                        undoable = target.applyDecreaseToAdjustedStat(stat, amount, limit, figured);
                    } /*      else if ( affectsType.equals( ADJUSTED_AND_CURRENT_STAT ) ){
                    StatChangeUndoable ai1 = target.applyDecreaseToCurrentStat(stat, amount, limit, figured);
                    StatChangeUndoable ai2 = target.applyDecreaseToAdjustedBaseStat(stat, amount, limit, figured);
                    
                    ai = ai1;
                    ai.setFinalValues(ai2);
                    } */ else {
                        System.out.println("Invalide affectsType on Decrease Subeffect.");
                        continue;
                    }

                    if (amount > 0 && !silent) {
//                        be.addBattleMessage( new LegacyBattleMessage(target.getName() + " lost " + Integer.toString((int)amount) + " from " + stat + ".  ",
//                                BattleEvent.MSG_COMBAT);
                        be.addBattleMessage(new StatChangeBattleMessage(target, StatChangeType.DECREASE, (String) versus, (int) amount));
                    }

                    //addIndexed(i,"Subeffect","APPLYUNDOINFO", ai, true);
                    addAddUndoable(be, undoable);

                    amount = undoable.getAmount();
                    addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(amount), true);
                } else if (type.equals("SET")) {
                    affectsType = getIndexedStringValue(i, "Subeffect", "AFFECTSTYPE");
                    if (affectsType.equals(CURRENT_STAT)) {
                        undoable = target.applySetToCurrentStat(stat, amount);

                        //addIndexed(i,"Subeffect","APPLYUNDOINFO", ai, true);
                        addAddUndoable(be, undoable);

                        double change = undoable.getAmount();
                        addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(change), true);
                    } else if (affectsType.equals(ADJUSTED_STAT)) {
                        undoable = target.applySetToAdjustedStat(stat, amount, figured);

                        //addIndexed(i,"Subeffect","APPLYUNDOINFO", ai, true);
                        addAddUndoable(be, undoable);

                        double change = undoable.getAmount();
                        addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(change), true);
                    } else if (affectsType.equals(BASE_STAT)) {
                        undoable = target.applySetToBaseStat(stat, amount);

                        // addIndexed(i,"Subeffect","APPLYUNDOINFO", ai, true);
                        addAddUndoable(be, undoable);

                        double change = undoable.getAmount();
                        addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(change), true);
                    } else {
                        System.out.println("Unknown AffectsType (" + affectsType + " for Set.");
                        continue;
                    }

                    if (!silent) {
//                        be.addBattleMessage( new LegacyBattleMessage(target.getName() + " set " + stat + " to " + Integer.toString((int)amount) + ".",
//                                BattleEvent.MSG_COMBAT);
                        be.addBattleMessage(new StatChangeBattleMessage(target, StatChangeType.SET, (String) versus, (int) amount));
                    }
                } else {
                    if (DEBUG == 1) {
                        System.out.println("Incompatable Subeffect: " + type + ", " + versustype);
                    }
                    continue;
                }

            } else if (versustype.equals("STATCP")) {
                // It is versustype a STAT.

                // Set stat for easy access.
                stat = (String) versus;

                // Make sure the target contains the stat, otherwise ignore the subeffect.
                if (!target.hasStat(stat)) {
                    continue;
                }
                boolean figured = getIndexedBooleanValue(i, "Subeffect", "AFFECTSFIGURED");

                if (type.equals("AID")) {
                    undoable = target.applyAidToAdjustedStatCP(stat, amount, figured);

                    //addIndexed(i,"Subeffect","APPLYUNDOINFO", ai, true);
                    addAddUndoable(be, undoable);

                    amount = undoable.getAmount();
                    addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(amount), true);

                    if (amount > 0 && !silent) {
//                        be.addBattleMessage( new LegacyBattleMessage(target.getName() + " received " + Double.toString(amount) + " character point(s) aid to " + stat + ".  " + target.getName() + " now has " + target.getCurrentStat((String)stat) + " " + stat +
//                                ".", BattleEvent.MSG_COMBAT));
                        be.addBattleMessage(new StatCPChangeBattleMessage(target, StatChangeType.AID_RECEIVED, stat, amount, target.getCurrentStat((String) stat)));
                    } else if (amount < 0 && !silent) {
//                        be.addBattleMessage( new LegacyBattleMessage(target.getName() + " lost " + Double.toString(-1 * amount) + " character point(s) aid from " +
//                                stat + ".  " + target.getName() + " now has " + target.getCurrentStat((String)stat) + " " + stat +
//                                ".", BattleEvent.MSG_COMBAT));
                        be.addBattleMessage(new StatCPChangeBattleMessage(target, StatChangeType.AID_LOST, stat, amount, target.getCurrentStat((String) stat)));
                    }

                    if (stat.equals("STUN") && target.isUnconscious() && target.getCurrentStat("STUN") >= 1) {
                        processNotUnconscious(be, target);

                    }

                    if (stat.equals("BODY")) {
                        if (target.getCurrentStat("BODY") >= 1) {
                            processNotDying(be, target);
                        }
                    }
                } else if (type.equals("DRAIN")) {
                    undoable = target.applyDrainToAdjustedStatCP(stat, amount, figured);

                    //addIndexed(i,"Subeffect","APPLYUNDOINFO", ai, true);
                    addAddUndoable(be, undoable);

                    amount = undoable.getAmount();
                    addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(amount), true);

                    if (amount > 0 && !silent) {
//                        be.addBattleMessage( new LegacyBattleMessage(target.getName() + " lost " + Double.toString(amount) + " character Point(s) drain from " +
//                                versus + ".  " + target.getName() + " now has " + target.getCurrentStat((String)versus) + " " + versus +
//                                ".", BattleEvent.MSG_COMBAT));
                        be.addBattleMessage(new StatCPChangeBattleMessage(target, StatChangeType.DRAIN_LOST, stat, amount, target.getCurrentStat((String) stat)));
                    } else if (amount < 0 && !silent) {
//                        be.addBattleMessage( new LegacyBattleMessage(target.getName() + " recovered " + Double.toString( -1 * amount) + " character Point(s) drain to " +
//                                versus + ".  " + target.getName() + " now has " + target.getCurrentStat((String)versus) + " " + versus +
//                                ".", BattleEvent.MSG_COMBAT));
                        be.addBattleMessage(new StatCPChangeBattleMessage(target, StatChangeType.DRAIN_RECOVERED, stat, amount, target.getCurrentStat((String) stat)));
                    }

                    if (amount > 0 && isAlive && (stat).equals("STUN")) {
                        if (target.hasStat("CON")) {
                            // Has con, so lets see if he is stunned
                            int con = target.getCurrentStat("CON");
                            if (amount > con) {
                                processStunned(be, target);
                            }
                        }
                        if (target.getCurrentStat("STUN") <= 0) {
                            processKnockout(be, target);
                        }
                    }
//                    if ( isAlive && (stat).equals("BODY") ) {
//                        if ( amount > 0 && target.getCurrentStat("BODY") <= 0 ) {
//                           processDying(be, target);
//                        }
//                    }
                } else if (type.equals("HEAL")) {
                    undoable = target.applyHealToCurrentStatCP(stat, amount, limit);
                    // addIndexed(i,"Subeffect","APPLYUNDOINFO", ai, true);
                    addAddUndoable(be, undoable);

                    amount = undoable.getAmount();
                    addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(amount), true);

                    if (amount > 0 && !silent) {
//                        be.addBattleMessage( new LegacyBattleMessage(target.getName() + " received " + Integer.toString((int)amount) + " character point(s) of healing to " +
//                                stat + ".  " + target.getName() + " now has " + target.getCurrentStat((String)stat) + " " + stat +
//                                ".", BattleEvent.MSG_COMBAT));
                        be.addBattleMessage(new StatCPChangeBattleMessage(target, StatChangeType.HEALING_RECEIVED, stat, amount, target.getCurrentStat((String) stat)));
                    }

                    if (stat.equals("STUN") && target.isUnconscious() && target.getCurrentStat("STUN") >= 1) {
                        processNotUnconscious(be, target);

                    }

                    if (stat.equals("BODY")) {
                        if (target.getCurrentStat("BODY") >= 1) {
                            processNotDying(be, target);
                        }
                    }
                } else if (type.equals("SET")) {
                    affectsType = getIndexedStringValue(i, "Subeffect", "AFFECTSTYPE");
                    /*     if ( affectsType.equals( CURRENT_STAT ) ) {
                    ai = target.applySetToCurrentStat(stat, amount);
                    
                    addIndexed(i,"Subeffect","APPLYUNDOINFO", ai, true);
                    
                    amount = ai.getAmount();
                    addIndexed(i,"Subeffect","ADJUSTEDAMOUNT", new Double(amount), true);
                    }  */
                    if (affectsType.equals(ADJUSTED_STAT)) {
                        undoable = target.applySetToAdjustedStatCP(stat, amount, figured);

                        //addIndexed(i,"Subeffect","APPLYUNDOINFO", ai, true);
                        addAddUndoable(be, undoable);

                        amount = undoable.getAmount();
                        addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(amount), true);
                    } else if (affectsType.equals(BASE_STAT)) {
                        undoable = target.applySetToBaseStatCP(stat, amount);

                        //addIndexed(i,"Subeffect","APPLYUNDOINFO", ai, true);
                        addAddUndoable(be, undoable);

                        amount = undoable.getAmount();
                        addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(amount), true);
                    } else {
                        System.out.println("Unknown AffectsType (" + affectsType + " for Set.");
                        continue;
                    }

                    if (!silent) {
//                        be.addBattleMessage( new LegacyBattleMessage(target.getName() + " set " + stat + " Character Points to " + Double.toString(amount) + ".",
//                                BattleEvent.MSG_COMBAT));
                        be.addBattleMessage(new StatChangeBattleMessage(target, StatChangeType.SET, stat, (int) amount));
                    }
                } else {
                    if (DEBUG == 1) {
                        System.out.println("Incompatable Subeffect: " + type + ", " + versustype);
                    }
                    continue;
                }
            } else if (versustype.equals("ABILITY")) {
                AbilityChangeUndoable acu;
                ability = (Ability) versus;

                // Make sure the target contains the stat, otherwise ignore the subeffect.
                if (!target.hasAbility(ability)) {
                    continue;                // Grab a copy of the adjustedInstance for this ability...
                }
                ability = ability.getInstanceGroup().getModifiableAdjustedInstance();

                if (type.equals("AID")) {
                    acu = ability.applyAidToAbility((int) amount);

                    addAddUndoable(be, acu);

                    amount = acu.getAdjustmentAmount();
                    addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(amount), true);

                    if (amount > 0 && !silent) {
                        be.addBattleMessage(new AbilityCPChangeBattleMessage(target, StatChangeType.AID_RECEIVED, ability, amount));
                        be.addBattleMessage(new SimpleBattleMessage(target, target.getName() + "'s " + ability.getName() + " is now configured as " + ability.getDescription() + ".  "));
                    } else if (amount < 0 && !silent) {
                        be.addBattleMessage(new AbilityCPChangeBattleMessage(target, StatChangeType.AID_LOST, ability, -1 * amount));
                        be.addBattleMessage(new SimpleBattleMessage(target, target.getName() + "'s " + ability.getName() + " is now configured as " + ability.getDescription() + ".  "));
                    }
                } else if (type.equals("DRAIN")) {
                    acu = ability.applyDrainToAbility((int) amount);

                    addAddUndoable(be, acu);

                    amount = acu.getAdjustmentAmount();

                    // Amount is negative for a drain, so reverse it to get a positive amount...
                    amount *= -1;

                    addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(amount), true);

                    if (amount > 0 && !silent) {
                        be.addBattleMessage(new AbilityCPChangeBattleMessage(target, StatChangeType.DRAIN_LOST, ability, amount));
                        be.addBattleMessage(new SimpleBattleMessage(target, target.getName() + "'s " + ability.getName() + " is now configured as " + ability.getDescription() + ".  "));
                    } else if (amount < 0 && !silent) {
                        be.addBattleMessage(new AbilityCPChangeBattleMessage(target, StatChangeType.DRAIN_RECOVERED, ability, -1 * amount));
                        be.addBattleMessage(new SimpleBattleMessage(target, target.getName() + "'s " + ability.getName() + " is now configured as " + ability.getDescription() + ".  "));
                    }
                } else if (type.equals("SET")) {
                    affectsType = getIndexedStringValue(i, "Subeffect", "AFFECTSTYPE");

                    acu = ability.applySetToAbility((int) amount);

                    addAddUndoable(be, acu);

                    amount = acu.getAdjustmentAmount();
                    addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(amount), true);

                    if (!silent) {
                        be.addBattleMessage(new AbilityCPChangeBattleMessage(target, StatChangeType.SET, ability, amount));
                        be.addBattleMessage(new SimpleBattleMessage(target, target.getName() + "'s " + ability.getName() + " is now configured as " + ability.getDescription() + ".  "));
                    }
                } else {
                    if (DEBUG == 1) {
                        System.out.println("Incompatable Subeffect: " + type + ", " + versustype);
                    }
                    continue;
                }
            }
        }
    }

    /* Removes the changes applied by a subeffect when the parent effect is removed from the target.
     * Only a limited number of subeffects are removable.  Those which are not removable must be
     * counteracted with some game machanic.  For example, damage is not removed when it's effect
     * is removed, but it can be removed by applying a heal or aid effect.
     */
    protected void removeSubEffects(BattleEvent be, Target target) throws BattleEventException {
        //if ( Battle.currentBattle.dle != null ) Battle.currentBattle.dle.setDetailList(this);
        int count = getIndexedSize("Subeffect");

        int i;
        String type, versustype;
        Object versus;
        Double v, l;
        double amount, limit;
        CharacteristicChangeUndoable ai;
        boolean silent, figured;
        String affectsType;


        for (i = 0; i < count; i++) {
            // First check to see that all the important information is already there
            if ((type = getIndexedStringValue(i, "Subeffect", "EFFECTTYPE")) == null || (v = getIndexedDoubleValue(i, "Subeffect", "VALUE")) == null || (versustype = getIndexedStringValue(i, "Subeffect", "VERSUSTYPE")) == null || (versus = getIndexedValue(i, "Subeffect", "VERSUS")) == null) {
                continue;
            }
            amount = v.doubleValue();
            l = getIndexedDoubleValue(i, "Subeffect", "LIMIT");
            limit = (l == null) ? UNLIMITED : l.doubleValue();
            silent = getIndexedBooleanValue(i, "Subeffect", "SILENT");
            affectsType = getIndexedStringValue(i, "Subeffect", "AFFECTSTYPE");
            figured = getIndexedBooleanValue(i, "Subeffect", "AFFECTSFIGURED");

            if (versustype.equals("STAT")) {
                // It is versustype a STAT.  Find the STAT name and current value of Target
                String stat = (String) versus;

                if (!target.hasStat(((String) versus))) {
                    continue;
                }
                if (type.equals("DECREASE")) {
                    if (affectsType.equals(CURRENT_STAT)) {
                        ai = target.applyIncreaseToCurrentStat(stat, amount, limit);

                    } else if (affectsType.equals(ADJUSTED_STAT)) {
                        ai = target.applyIncreaseToAdjustedStat(stat, amount, limit, figured);
                    } /*     else if ( affectsType.equals( ADJUSTED_AND_CURRENT_STAT ) ){
                    StatChangeUndoable ai1 = target.applyIncreaseToCurrentStat(stat, amount, limit);
                    StatChangeUndoable ai2 = target.applyIncreaseToAdjustedBaseStat(stat, amount, limit, figured);
                    
                    ai = ai1;
                    ai.setFinalValues(ai2);
                    } */ else {
                        System.out.println("Invalide affectsType on Subeffect.");
                        continue;
                    }

                    if (amount > 0 && !silent) {
//                        be.addBattleMessage( new LegacyBattleMessage(target.getName() + " gain " + Integer.toString((int)amount) + " to " + stat + ".  ",
//                                BattleEvent.MSG_COMBAT));
                        be.addBattleMessage(new StatChangeBattleMessage(target, StatChangeType.INCREASE, stat, (int) amount));
                    }

                    //addIndexed(i,"Subeffect","REMOVEUNDOINFO", ai, true);
                    addRemoveUndoable(be, ai);

                    amount = ai.getAmount();
                    addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(amount), true);

                    if (stat.equals("STUN") && target.isUnconscious() && target.getCurrentStat("STUN") >= 1) {
                        processNotUnconscious(be, target);

                    }

                    if (stat.equals("BODY")) {
                        if (target.getCurrentStat("BODY") >= 1) {
                            processNotDying(be, target);
                        }
                    }

                } else if (type.equals("INCREASE")) {
                    if (affectsType.equals(CURRENT_STAT)) {
                        ai = target.applyDecreaseToCurrentStat(stat, amount, limit);
                    } else if (affectsType.equals(ADJUSTED_STAT)) {
                        ai = target.applyDecreaseToAdjustedStat(stat, amount, limit, figured);
                    } /*       else if ( affectsType.equals( ADJUSTED_AND_CURRENT_STAT ) ){
                    StatChangeUndoable ai1 = target.applyDecreaseToCurrentStat(stat, amount, limit, figured);
                    StatChangeUndoable ai2 = target.applyDecreaseToAdjustedBaseStat(stat, amount, limit, figured);
                    
                    ai = ai1;
                    ai.setFinalValues(ai2);
                    } */ else {
                        System.out.println("Invalide affectsType on Decrease Subeffect.");
                        continue;
                    }

                    if (amount > 0 && !silent) {
//                        be.addBattleMessage( new LegacyBattleMessage(target.getName() + " lost " + Integer.toString((int)amount) + " from " + stat + ".  ",
//                                BattleEvent.MSG_COMBAT));
                        be.addBattleMessage(new StatChangeBattleMessage(target, StatChangeType.DECREASE, stat, (int) amount));
                    }

                    // addIndexed(i,"Subeffect","REMOVEUNDOINFO", ai, true);
                    addRemoveUndoable(be, ai);

                    amount = ai.getAmount();
                    addIndexed(i, "Subeffect", "ADJUSTEDAMOUNT", new Double(amount), true);
                }

            }

        }
    }

    public void processDying(BattleEvent be, Target t) throws BattleEventException {
        if (!t.isDead() && !t.hasEffect(effectDoesNotBleed.effectName)) {
            new effectDying().addEffect(be, t);
        }
    }

    public void processNotDying(BattleEvent be, Target t)
            throws BattleEventException {
        if (t.isDying()) {
            t.removeEffect(be, "Dying");
        }
    }

    public void processStunned(BattleEvent be, Target t) throws BattleEventException {
        if (!t.isDead() && !t.hasEffect("Stunned") && !t.hasEffect(effectCannotBeStunned.effectName)) {
            new effectStunned().addEffect(be, t);
        }
    }

    public void processKnockout(BattleEvent be, Target t)
            throws BattleEventException {
        if (t.isUnconscious() == false && !t.isDead()) {
            new effectUnconscious().addEffect(be, t);
        }
    }

    public void processNotUnconscious(BattleEvent be, Target t)
            throws BattleEventException {
        t.removeEffect(be, "Unconscious");
    }

    public void postEvent(BattleEvent be, Target target, boolean added) {
        be.addEffectEvent(this, target, added);
    }

    public String toString() {
        String name = getName();
        return "Effect: " + name;
    }

    public void setUnique(boolean unique) {
        //add("Effect.UNIQUE", (unique) ? "TRUE" : "FALSE",   true );
        this.unique = unique;
    }

    public boolean isUnique() {
        return unique;
    }

    public void rollbackEffects(Target target, boolean added) {
        //if ( Battle.currentBattle.dle != null ) Battle.currentBattle.dle.setDetailList(this);
        int count = getIndexedSize("Subeffect");

        int i;
        CharacteristicChangeUndoable ai;

        // The Undoables are embedded directly into the BattleEvent now...
        // This method only corrects links between targets and effects...

        String ctype = this.getCType();
        if (added) {
            if (ctype.equals("PERSISTENT") || ctype.equals("LINKED")) {
                // Was Added Originally.  Now Remove effect info.  Remove the effect definition on the target.
                int index;
                if (target.hasEffect(this)) {
                    target.removeEffectEntry(this);
                }

                if (ctype.equals("LINKED")) {
                    ActivationInfo ailink = (ActivationInfo) getValue("Effect.AILINK");
                    // Remove Effect from both Target and Ability
                    int aindex = ailink.findIndexed("Effect", "EFFECT", this);
                    if (aindex != -1) {
                        ailink.removeAllIndexed(aindex, "Effect");
                    }
                }
            }
        } else { // The effect was originally removed.  You need to put it back.
            if (ctype.equals("PERSISTENT") || ctype.equals("LINKED")) {
                int index;
                if (target.hasEffect(this) == false) {
                    target.addEffectEntry(this);
                }


                if (ctype.equals("LINKED")) {
                    ActivationInfo ailink = (ActivationInfo) getValue("Effect.AILINK");
                    // Add Effect from both Target and Ability
                    int aindex = ailink.findIndexed("Effect", "EFFECT", this);
                    if (aindex == -1) {
                        aindex = ailink.createIndexed("Effect", "EFFECT", this);
                        ailink.addIndexed(aindex, "Effect", "TARGET", target);
                    }
                }
            }
        }
    }

    public void rollforwardEffects(Target target, boolean added) {
        // if ( Battle.currentBattle.dle != null ) Battle.currentBattle.dle.setDetailList(this);
        int count = getIndexedSize("Subeffect");

        int i;
        CharacteristicChangeUndoable ai;

        // The Undoables are embedded directly into the BattleEvent now...
        // This method only corrects links between targets and effects...


        String ctype = this.getCType();
        if (added == false) {
            // Was removed by rollback...roll it forward again.
            if (ctype.equals("PERSISTENT") || ctype.equals("LINKED")) {
                int index;
                if (target.hasEffect(this)) {
                    target.removeEffectEntry(this);
                }

                if (ctype.equals("LINKED")) {
                    ActivationInfo ailink = (ActivationInfo) getValue("Effect.AILINK");
                    // Remove Effect from both Target and Ability
                    int aindex = ailink.findIndexed("Effect", "EFFECT", this);
                    if (aindex != -1) {
                        ailink.removeAllIndexed(aindex, "Effect");
                    }
                }
            }
        } else { // Ability was originally add.  Add it again to rollforward
            if (ctype.equals("PERSISTENT") || ctype.equals("LINKED")) {
                int index;
                if (target.hasEffect(this) == false) {
                    target.addEffectEntry(this);
                }

                if (ctype.equals("LINKED")) {
                    ActivationInfo ailink = (ActivationInfo) getValue("Effect.AILINK");
                    // Remove Effect from both Target and Ability
                    int aindex = ailink.findIndexed("Effect", "EFFECT", this);
                    if (aindex == -1) {
                        aindex = ailink.createIndexed("Effect", "EFFECT", this);
                        ailink.addIndexed(aindex, "Effect", "TARGET", target, true);
                    }
                }
            }
        }

    //this.add("Effect.ROLLEDBACK", "FALSE",  true);
    }

    /** posteffect is called for every effect every time another effect is applied to the target.
     *   ie. if a damage affect is applied to bob, all of bobs current effects get to respond to that
     *   effect being added.
     *
     * @return Returns true if this effect (not the effect being added) should be removed from the target
     * after the current effect is finished being processed.
     */
    public boolean posteffect(BattleEvent be, Effect otherEffect, Target target) throws BattleEventException {
        return false;
    }

    public String getName() {
        //return getStringValue("Effect.NAME");
        return name;
    }

    public Target getTarget() {
        return (Target) getValue("Effect.TARGET");
    }

    public String getDescription() {
        String desc = "";

        int index, count;

        count = getIndexedSize("Subeffect");
        String name, type, versustype;
        Double value;
        Object versus;
        Object o;
        String cp = "";

        if (count > 0) {
            desc = "Subeffects:";
        }

        for (index = 0; index < count; index++) {
            if ((name = getIndexedStringValue(index, "Subeffect", "NAME")) == null || (type = getIndexedStringValue(index, "Subeffect", "EFFECTTYPE")) == null || (versustype = getIndexedStringValue(index, "Subeffect", "VERSUSTYPE")) == null || (value = getIndexedDoubleValue(index, "Subeffect", "VALUE")) == null || (versus = getIndexedValue(index, "Subeffect", "VERSUS")) == null) {
                continue;
            }

            if (versustype.equals("STAT") || versustype.equals("STATCP")) {
                if (versustype.equals("STATCP")) {
                    cp = " CP";
                }
            } else if (versustype.equals("ABILITY")) {
                versus = ((Ability) versus).getName();
                cp = " CP";
            } else {
                continue;
            }

            desc = desc + "\n  " + value.toString() + cp + " " + type + " to " + ((String) versus);

        }
        return desc;
    }

    public void addECVAttackModifiers(CVList cvList, Ability attack) {
    }

    public void addECVDefenseModifiers(CVList cvList, Ability attack) {
    }

    public void addOCVAttackModifiers(CVList cvList, Ability attack) {
    }

    public void addDCVDefenseModifiers(CVList cvList, Ability attack) {
    }

    public int getEffectPriority() {
//        Integer i;
//        if ( (i = this.getIntegerValue("Effect.PRIORITY")) == null ) return 2;
//        else return i.intValue();
        return effectPriority;
    }

    public void setEffectPriority(int priority) {
        this.effectPriority = priority;
    }

    public void addDefenseModifiers(DefenseList dl, String defense) {
    }

    public void abilityIsActivating(BattleEvent be, Ability ability) throws BattleEventException {
    }

    public void skillIsActivating(BattleEvent be, Ability ability) {
    }

    public void adjustDice(BattleEvent be, String targetGroup) throws BattleEventException {
    }
//    public void addActions(Vector actions) {
//    }
    public void addActions(Vector actions) throws BattleEventException {
    }

    public void addKnockbackModifiers(BattleEvent be, KnockbackModifiersList kml, String knockbackGroup) {
    }

    /** Set the Effect to be hidden.
     * Effects can be either hidden, critical, or niether.  Hidden and critical
     * effects attributes should be set prior to being added to a target, since
     * the hidden and critical counts are cached in the target.
     * @param hidden true indicates hidden, false indicates not hidden.
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        //add( "Effect.ISHIDDEN", (hidden)?"TRUE":"FALSE", true);
        if (hidden == true) {
            setCritical(false);
        }
    }

    public boolean isHidden() {
        //return getBooleanValue( "Effect.ISHIDDEN" );
        return hidden;
    }

    /** Sets effect type to Critical.
     * Effects can be either hidden, critical, or neither.  Hidden and critical
     * effects attributes should be set prior to being added to a target, since
     * the hidden and critical counts are cached in the target.
     * @param critical
     */
    public void setCritical(boolean critical) {
        this.critical = critical;
        //add( "Effect.ISCRITICAL", (critical)?"TRUE":"FALSE",true );
        if (critical == true) {
            setHidden(false);
        }
    }

    public boolean isCritical() {
        //return getBooleanValue( "Effect.ISCRITICAL" );
        return critical;
    }

    public void setEffectColor(Color c) {
        //add( "Effect.COLOR", c, true);
        this.effectColor = c;
    }

    public Color getEffectColor() {
        //Object o = getValue("Effect.COLOR");
        //return ( o == null ) ? null : (Color) o;
        if (effectColor != null) {
            return effectColor;
        }
        return Color.BLACK;
    }

    /** Indicates if there are subeffects which Remain to be applied against a target.
     *
     */
    public boolean damageSubeffectsRemain() {
        boolean result = false;

        int sindex;
        double value;

        IndexIterator ii = getSubeffectIterator();
        while (ii.hasNext()) {
            sindex = ii.nextIndex();
            if (getSubeffectEffectType(sindex).equals("DAMAGE") && getSubeffectValue(sindex) > 0) {
                result = true;
                break;
            }
        }

        return result;
    }

    public IndexIterator getSubeffectIterator() {
        return getIteratorForIndex("Subeffect");
    }

    public int getSubeffectIndex(String subeffectName) {
        return findIndexed("Subeffect", "NAME", subeffectName);
    }

    final public String getSubeffectEffectType(int sindex) {
        return getIndexedStringValue(sindex, "Subeffect", "EFFECTTYPE");
    }

    final public String getSubeffectDefenseType(int sindex) {
        String s = getIndexedStringValue(sindex, "Subeffect", "DEFTYPE");
        return (s == null) ? "NONE" : s;
    }

    final public String getSubeffectDefenseSpecial(int sindex) {
        String s = getIndexedStringValue(sindex, "Subeffect", "DEFSPECIAL");
        return (s == null) ? "NONE" : s;
    }

    /** Returns the VersusType of the subeffect.
     *
     * The versus type can be either "STAT" or "ABILITY" or "STATCP".
     * In the case of "STAT"/"STATCP" the Subeffect VersusObject value should be
     * a string representing a stat name.  In the case of Ability it should
     * be an Ability object.
     */
    final public String getSubeffectVersusType(int sindex) {
        return getIndexedStringValue(sindex, "Subeffect", "VERSUSTYPE");
    }

    /** Returns the VersusObject of the subeffect.
     *
     * Subeffect VersusObject value should be the object which the subeffect affects
     * and is depended on the VersusType.
     * In the case of "STAT"/"STATCP" the Subeffect VersusObject value should be
     * a string representing a stat name.  In the case of Ability it should
     * be an Ability object.
     */
    final public Object getSubeffectVersusObject(int sindex) {
        return getIndexedStringValue(sindex, "Subeffect", "VERSUS");
    }

    final public void setSubeffectValue(int sindex, double amount) {
        addIndexed(sindex, "Subeffect", "VALUE", new Double(amount), true);
    }

    final public double getSubeffectValue(int sindex) {
        Double d = getIndexedDoubleValue(sindex, "Subeffect", "VALUE");
        return (d != null) ? d.doubleValue() : 0;
    }

    /** Sets the Amount of Defenses that were applied to damage from this effect.
     *
     */
    final public void setSubeffectDefensesApplied(int sindex, double amount) {
        addIndexed(sindex, "Subeffect", "DEFENSESAPPLIED", new Double(amount), true);
    }

    /** Gets the Amount of Defenses that were applied to damage from this effect.
     *
     */
    final public double getSubeffectDefensesApplied(int sindex) {
        Double d = getIndexedDoubleValue(sindex, "Subeffect", "DEFENSESAPPLIED");
        return (d != null) ? d.doubleValue() : 0;
    }

    final public double getSubeffectAdjustedAmount(int sindex) {
        Double d = getIndexedDoubleValue(sindex, "Subeffect", "ADJUSTEDAMOUNT");
        return (d != null) ? d.doubleValue() : 0;
    }

    final public double getSubeffectAbsorbedAmount(int sindex) {
        Double d = getIndexedDoubleValue(sindex, "Subeffect", "ABSORBEDAMOUNT");
        return (d != null) ? d.doubleValue() : 0;
    }

    final public void setSubeffectAbsorbedAmount(int sindex, double amount) {
        addIndexed(sindex, "Subeffect", "ABSORBEDAMOUNT", new Double(amount), true);
    }

    final public void setSubeffectDefense(int sindex, String special, String type) {
        addIndexed(sindex, "Subeffect", "DEFTYPE", type, true);
        addIndexed(sindex, "Subeffect", "DEFSPECIAL", special, true);
    }

    /** Adds a type of defense to the subeffect.
     *
     * This method adds a type of defense (PD, ED, MD) to a subeffect.  If the
     * subeffect already has that defense, it does nothing.  If it has
     * type "NONE" set, it switches it to the type.  If it has an existing type
     * it sets it to use both types.  Adding type NONE does not change the
     * defense type.
     */
    final public void addSubeffectDefenseType(int sindex, String type) {
        String current = getIndexedStringValue(sindex, "Subeffect", "DEFTYPE");
        if (current == null || current.equals("NONE")) {
            addIndexed(sindex, "Subeffect", "DEFTYPE", type, true);
        } else if (current.indexOf(type) == -1) {
            addIndexed(sindex, "Subeffect", "DEFTYPE", current + "+" + type, true);
        }
    }

    /** Checks if a subeffect has the listed defense type.
     *
     */
    final public boolean hasSubeffectDefenseType(int sindex, String type) {
        String current = getIndexedStringValue(sindex, "Subeffect", "DEFTYPE");
        return (current.indexOf(type) != -1);
    }

    /** Returns the Value to Limit the subeffect by.
     *
     * This value can be either UNLIMITED, BASE_VALUE, TARGET_LIMITED or a
     * real value.  BASE_VALUE limited according to the characteristic/abilities
     * base value. TARGET_LIMITED limits according to a characteristics minimum
     * value.
     */
    final public double getSubeffectLimit(int sindex) {
        Double d = getIndexedDoubleValue(sindex, "Subeffect", "LIMIT");
        return (d != null) ? d.doubleValue() : 0;
    }

    /** Sets the Value to Limit the subeffect by.
     *
     * This value can be either UNLIMITED, BASE_VALUE, TARGET_LIMITED or a
     * real value.  BASE_VALUE limited according to the characteristic/abilities
     * base value. TARGET_LIMITED limits according to a characteristics minimum
     * value.
     */
    final public void setSubeffectLimit(int sindex, double limit) {
        addIndexed(sindex, "Subeffect", "LIMIT", new Double(limit), true);
    }

    final public int getSubeffectCount() {
        return getIndexedSize("Subeffect");
    }

    final protected void addAddUndoable(BattleEvent be, Undoable undoable) {
        // int count = createIndexed("AddUndoable", "UNDOABLE", undoable);
        // return count;
        be.addUndoableEvent(undoable);
    }

    final protected void addRemoveUndoable(BattleEvent be, Undoable undoable) {
        // int count = createIndexed("RemoveUndoable", "UNDOABLE", undoable);
        // return count;
        be.addUndoableEvent(undoable);
    }

    /**  Gets the AttackTreeNode to insert into the Attack Tree.
     *
     * This method presents each effect an oppertunity to inject nodes into the attack tree.
     *
     * This method is called by the attack process once for each effect a hit target has.  The returned
     * node will be inserted into the attack tree prior to the any of the other Effect processing methods
     * (predefense, postdefense, etc.) are called.
     */
    public AttackTreeNode getTargetEffectNode(BattleEvent be, Target target, String TargetGroup, int refNumber) {
        return null;
    }

    /**  Gets the AttackTreeNode to insert into the Attack Tree.
     *
     * This method presents each effect an oppertunity to inject nodes into the attack tree.
     *
     * This method is called by the attack process once for each source effect for each hit target.  The returned
     * node will be inserted into the attack tree prior to the any of the other Effect processing methods
     * (predefense, postdefense, etc.) are called.
     */
    public AttackTreeNode getSourceEffectNode(BattleEvent be, Target target, String TargetGroup, int refNumber) {
        return null;
    }

    /** Search Effect for Body Damage and returns the total amount in all subeffects.
     *
     */
    public int getTotalBodyDamage() {
        int totalBody = 0;

        IndexIterator ii = getSubeffectIterator();
        while (ii.hasNext()) {
            int index = ii.nextIndex();
            if (getSubeffectEffectType(index).equals(DAMAGE) && getSubeffectVersusObject(index).equals("BODY")) {
                totalBody += Math.round(getSubeffectValue(index));
            }
        }
        return totalBody;
    }

    /** Search Effect for Stun Damage and returns the total amount in all subeffects.
     *
     */
    public int getTotalStunDamage() {
        int totalBody = 0;

        IndexIterator ii = getSubeffectIterator();
        while (ii.hasNext()) {
            int index = ii.nextIndex();
            if (getSubeffectEffectType(index).equals(DAMAGE) && getSubeffectVersusObject(index).equals("STUN")) {
                totalBody += Math.round(getSubeffectValue(index));
            }
        }
        return totalBody;
    }

    public AttackTreeNode preactivate(BattleEvent be, Ability ability) {
        return null;
    }

    /** Returns whether this ability is currently enabled given the current state of the BattleEngine.
     * Only Power specific conditions should be checked.
     * @param ability Ability to be check.
     * @return True if ability is currently usable.
     */
    public boolean isEnabled(Ability a) {
        return true;
    }

    /** Triggers the Battle to remove the effect at the next possible oppertunity.
     *
     *  This will add a battleEngine event to remove the effect, with full undoability.
     */
    public void triggerRemoval() {
        BattleEvent be = new BattleEvent(BattleEvent.REMOVE_EFFECT, this, this.getTarget());
        Battle.currentBattle.addEvent(be);
    }
}