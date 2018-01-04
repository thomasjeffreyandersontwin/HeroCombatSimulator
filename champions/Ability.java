package champions;

import champions.AbilityInstanceGroup.AbilityActivationEntry;
import champions.adjustmentPowers.Adjustable;
import champions.adjustmentPowers.AdjustmentClass;
import champions.exception.BattleEventException;
import champions.exceptionWizard.ExceptionWizard;
import champions.filters.AndFilter;
import champions.interfaces.AbilityIterator;
import champions.interfaces.AbilityList;
import champions.interfaces.Advantage;
import champions.interfaces.Alias;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.Debuggable;
import tjava.Filter;
import champions.interfaces.Framework;
import champions.interfaces.IndexIterator;
import champions.interfaces.Limitation;
import champions.interfaces.PAD;
import champions.interfaces.SpecialEffectIterator;
import champions.interfaces.SpecialParameter;
import champions.interfaces.Undoable;
import champions.parameters.ParameterList;
import champions.powers.SpecialParameterDCVModifier;
import champions.powers.SpecialParameterNormallyOn;
import champions.powers.SpecialParameterOCVModifier;
import champions.powers.SpecialParameterWeapon;
import champions.powers.advantageReducedEndurance;
import champions.powers.effectInterruptible;
import champions.powers.powerArmor;
import champions.powers.powerAutomaton;
import champions.powers.powerDamageResistance;
import champions.powers.powerDoesNotBleed;
import champions.powers.powerFlashDefense;
import champions.powers.powerForceField;
import champions.powers.powerKnockbackResistance;
import champions.powers.powerMentalDefense;
import champions.powers.powerNoHitLocations;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;


/**
 * Object which defines all information necessary to activate and use a Hero Power
 * 
 * Ability Instance Groups<P>
 * 
 * Each Ability belongs to an Ability Instance Group.  An Ability Instance Group contains a collection of instances
 * of Abilities, derived from a common base ability. <P>
 * 
 * Each Ability Instance Group has a Base ability.  The base ability contains all the information which defines
 * which abilities belong to the Ability Instance Group and what the currently active ability instances is.  There
 * is no formal Ability Instance Group object or definition.  All information about the group is fully contained
 * in the base ability instance.  The configuration of the base ability is never changed automatically.<P>
 * 
 * Each Ability Instance Group has a Current Ability instance.  The current Ability instance is the instance
 * that will be used if a Target actives an ability.  The base instance can and often is also the current instance.
 * If an absorption power affects an ability, typically it is the current ability that is drained/aided etc.
 * Since the configuration of the base ability is never changed automatically, if the base ability is also the current
 * ability, a new ability instance will be created, and that ability instance will be modified.<P>
 * 
 * The following Value Pairs detail the Ability Instance Group information:<P>
 * Ability.ISBASEINSTANCE => True if instance is base instance, false otherwise.<P>
 * Ability.ISCURRENTINSTANCE => True if instance is the current instance, false otherwise.<p>
 * Ability.CURRENTINSTANCE => Ability Instance which is the current instance.  Only exists in Base instance.<P>
 * Instance[].ABILITY => Array of all instances belonging to Ability Instance Group.  Only exists in Base Instance.<P>
 * 
 * <B>Property Change Events</B><P>
 * The Ability class only fires Property Change Events for a limited selection of Properties.  Changes in the follow
 * properties will trigger Property Change Events to be fired:<P>
 * 
 * <I>Property          Description</I><P>
 * Ability.NAME         Name of the Ability Changed.<P>
 * Ability.ENDCOST      The ENDCost of Ability changed, based upon the current configuration of the Ability.<P>
 * Ability.DESCRIPTION  The Description of the Ability has changed.  HTML description will also be modified.<P>
 * Ability.MOVEDISTANCE The DistanceFromCollision available for a movement power changed. <B>NOT IMPLEMENTED.</B><P>
 * Ability.OCVBONUS     The OCV modifier imparted by using the ability changed. <B>NOT IMPLEMENTED.</B><P>
 * Ability.DCVBONUS     The DCV modifier imported by using the ability changed. <B>NOT IMPLEMENTED.</B><P>
 * Ability.CPCOST       The final CP cost of the ability, as it is currently configured, changed.<P>
 * <P>
 * DAMAGE               The Abilities Damage/Effect Amount changed. <B>NOT IMPLEMENTED.</B><P>
 * ENABLECOLOR          The Enable Color of the Ability Changed. <B>NOT IMPLEMENTED.</B><P>
 * ENABLEMESSAGE        The Enable Tooltip Message Changed. <P>
 * Advantage.INDEXSIZE  The Advantage Count Changed.<P>
 * Limitation.INDEXSIZE The Limitation Count Changed.<P>
 * SpecialEffects.INDEXSIZE The SpecialEffects Count Changed.<P>
 * SpecialParameters.INDEXSIZE The SpecialParameters Count Changed.<P>
 * 
 * <B>XML Import</B><P>
 * Abilities can be imported from an XML stream using a set of standard methods.
 * All the methods assume that the XML stream has been converted to a Document
 * Object Model node tree.<P>
 * 
 * To accomodate different XML Schema, all XML import methods require a 
 * Format string.  The format string indicates which XML schema the XML stream
 * was sourced from.  For example, Hero Designer .hdc files have a format of 
 * "HeroDesigner".  If a method does not recognize/support a specific format,
 * it will sometimes throw a XMLFormatNotRecognizedException.  Classes using the
 * import methods should be prepared to handle these exceptions gracefully.<P>
 * 
 * Abilities can import both when the sourceAlias of the Ability is known and not
 * known.  Several of the import steps can be skipped if the Source is unknown,
 * although some abilities do not react well when this occurs.<P>
 * 
 * The general order of execution for important an ability should be as follows:
 * <OL>
 * <LI>Determine Source of Ability, if possible.
 * <LI>Build format specific ID string from XML, called XMLID.
 * <LI>Call PADRoster.identifyXML(format, XMLID) to check if a template matches
 *     the XMLID.
 * <LI>If template wasn't found, call power.idenfityXML for all powers listed in the
 *     PADRoster to find power name.
 * <LI>Once an power/template name is found, use PADRoster.getNewAbilityInstance
 *     to get ability instance.
 * <LI>Set the name of ability.
 * <LI>Call power.importXML for the power of the ability.
 * <LI>Call power.configurePAD for the power of the ability.
 * <LI>For each Adder (Advantage/Limitation):<OL>
 *      <LI>Call adder.idenfityXML (where adder is possible Advantages/Limitation
 *          from PADRoster) to identify adder.
 *      <LI>Call ability.addPAD( PADRoster.getNew<AdderType>Instance(...) ) to
 *          add the adder to the ability.
 *      <LI>Call ability.importXML(...) to import the adder from XML.
 *      <LI>Call ability.configurePAD(...) for the adder to configure.</OL>
 * <LI>If Target is known, add Ability to Target in appropriate AbilityList.
 * <LI>After all Abilities for Target have been created, call 
 *     ability.finalizeImportXML for all abilities.  This will trigger a call to
 *     finalizeImportXML for all adders.
 * </OL>
 * 
 * As long as all XML imports follow this pattern, all Powers, Advantages,
 * and Limitations should import correctly.  Note, that since the sourceAlias is not
 * set until the Ability is added to the sources's AbilityList, the importXML
 * and configurePAD should try to avoid requiring a sourceAlias up front.  When the
 * ability is added to the target, the abilitySourceSet method will be called 
 * for the power and all adders.  It is at this time that Target specific 
 * parameters can be initialized.<P>
 * 
 * 
 * @author Trevor Walker
 * @version 
 */
public class Ability extends DetailList implements ChampionsConstants, Adjustable, AdjustmentClass, Debuggable {
    static final long serialVersionUID = -5416071516130721743L;
    
    /** Holds value of property enableMessage. */
    private String enableMessage;
    private Color enableColor;
    
    /** The File extension used when Abilities are saved to disk.
     */
    public String fileExtension = "abt";
    
    public static Object[][] specialParameterArray = {
    };
    
    static private Color errorColor = Color.red;
    static private Color warningColor = Color.magenta;
    /** Static variable for gathering performance statistics concerning
     * the number of times the isEnabled method is called.
     *
     * abilityIsEnabled tracks the number of times that isEnabled was
     * called.
     */
    static public  int abilityIsEnabled = 0;
    /** Static variable for gathering performance statistics concerning
     * the number of times the isEnabled method is called.
     *
     * abilityIsEnabledTime tracks the amount of time, in microseconds
     * that isEnabled has taken when called.
     */
    static public long abilityIsEnabledTime = 0;
    
    protected AbilityInstanceGroup instanceGroup;
    protected Ability parent;
    
    public String name;
    protected String instanceDescription;
    
    // Cost parameters
    protected int cpCost = -1;
    protected int apCost = -1;
    protected int realCost = -1;
    
    private int adjustmentCP;
    
    protected int fixedCPCost = 0;
    protected boolean fixedCPEnabled = false;
    
    protected int powerCost = -1;
    protected double advCost;
    protected double advDCCost;
    protected double limCost;
    
    protected int endCost = -1;
    protected boolean costsEndOnlyToActivate = false;
    
    public Power power;
    
    protected Alias<Target> sourceAlias;
    protected boolean autoSource;
    
    protected String primaryENDSource = null;
    protected String secondaryENDSource = null;
    protected String strengthENDSource = null;
    
    protected double endMultiplier = 1.0;
    
    protected List<AbilityActivationEntry> activations;
    
    protected String powerDescription;
    protected String abilityDescription;
    protected String abilityHTMLDescription;
    
    protected boolean generateDefaultEffects = false;
    protected String ctype;
    protected Boolean normallyOn = null;
    protected String atype = null;
    protected String ptype = null;
    
    protected ParameterList powerParameterList;
    /** Amount of time necessary to activate the ability.
     */
    protected String activationTime = null;
    
    protected boolean weapon = false;
    protected Integer minimumStrength = null;
    protected int ocvModifier = 0;
    protected int dcvModifier = 0;
    
    protected List<Advantage> advantages = new ArrayList<Advantage>();
    protected List<Limitation> limitations = new ArrayList<Limitation>();
    protected AbilityList abilityList = null;
    
    protected boolean noNormalDefense = false;
    protected String nndDefense = null;

	public boolean usingMoveThrough;
    
    /** Creates new Ability */
    public Ability() {
        setFireChangeByDefault(false);
        setInstanceGroup( new AbilityInstanceGroup() );
    }
    
    public Ability(boolean createInstanceGroup) {
        setFireChangeByDefault(false);
        if ( createInstanceGroup ) setInstanceGroup( new AbilityInstanceGroup() );
    }
    
    /**
     * Creates new Ability
     * @param aig 
     */
    public Ability(AbilityInstanceGroup aig) {
        setFireChangeByDefault(false);
        setInstanceGroup( aig );
    }
    
    /** Creates new Ability, with the name of <code>name</code>.
     * @param name Name of new Ability.
     */
    public Ability(String name) {
        this();
        setName(name);
    }
    /** Creates new Ability based upon the contents of <code>detailList</code>.
     * @deprecated This should no longer be used.
     * @param detailList DetailList to base new ability on.
     */
    public Ability(DetailList detailList) {
        setFireChangeByDefault(false);
        this.addAll(detailList);
        setInstanceGroup( new AbilityInstanceGroup() );
    }
    
    /** Returns the Name of the Ability.
     *
     * Generally, the name of the Ability is unique to the Target/Character which the ability is
     * associated with.  However, ability objects that are part of the same instance will
     * share the same name.
     *
     * @return Value of property name.
     */
    public String getName() {
        //String name = getStringValue("Ability.NAME" );
        //return name == null ? "(UNNAMED)" : name;
        return name == null ? "(UNNAMED)" : name;
    }
    
    /**
     * Returns the description of the instance.
     * @return 
     */
    public String getInstanceDescription() {
        //String name = getStringValue("Ability.INSTANCEDESCRIPTION" );
        //return name;
        return instanceDescription;
    }
    
    /**
     * Returns a more detailed name of the ability.
     * 
     *  For variations, it is of the form:
     *      InstanceDescription
     * 
     *  For Non-Variations with InstanceDescription:
     *      AbilityName(InstanceDescription)
     * 
     *  Otherwise:
     *      AbilityName
     * @return 
     */
    public String getInstanceName() {
        String abilityName = "";
        String instanceDesc = getInstanceDescription();
            
        if ( isVariationInstance() && instanceDesc != null) {
            abilityName = instanceDesc;
        }
        else {
            String name = getName();
            if ( instanceDesc == null ) {
                abilityName = name;
            } else {
                abilityName = name + "(" + instanceDesc + ")";
            }
        }
        
        return abilityName;
    }
    
    /**
     * Sets the Instance Description.
     * @param name 
     */
    public void setInstanceDescription(String name) {
        if ( instanceDescription == null || instanceDescription.equals(name) == false ) {
            //  add("Ability.INSTANCEDESCRIPTION", name, true, true);
            String old = instanceDescription;
            instanceDescription = name;
            firePropertyChange(this, "Ability.INSTANCEDESCRIPTION", old, instanceDescription);
        }
    }
    
    public String getSpecialInstanceDescription() {
        AbilityInstanceGroup aig = getInstanceGroup();
        
        StringBuffer sb = new StringBuffer();
        
        if ( aig != null ) {
            if ( aig.getBaseInstance() == this ) {
                sb.append("(Base Configuration)");
            }
            else if ( aig.getFramework() == this ) {
                sb.append("(Framework Configuration)");
            }
            else if ( aig.getAdjustedInstance() == this ) {
                sb.append("(Adjusted Configuration [Aid, Drain, or Absorption])");
            }
            else if ( aig.getCurrentInstance() == this ) {
                sb.append("(Current User Configuration)");
            }
        }

        return sb.toString();
    }
    
    public String getNameWithInstance() {
        if ( getInstanceDescription() != null ) {
            return getName() + "(" + getInstanceDescription() + ")" ;
        }
        else {
            return getName();
        }
    }
    
    /**
     * @return  */
    public double getDamageDie() {
        return getDamageDie(null);
    }
    
    /**
     * @param maneuver
     * @return  */
    
    public int DamageDiceOverride=0;
    public double getDamageDie(Ability maneuver) {
    	BattleEvent be=null;	
        return getDamageDieForBattleEvent(be, maneuver);
    }
    
    public double getDamageDieForBattleEvent(BattleEvent be, Ability maneuver)
    {
    	if ( Battle.debugLevel >= 4 ) System.out.println( "Adjusting Dice for: " + getName() );
        Power power = getPower();
        Target source = getSource();
        if ( power != null ) {
        	if(be==null) {
        		be = new BattleEvent( this );
        	}
            be.setSource(source);
            if ( maneuver != null ) {
                be.setManeuver(maneuver);
            }
            try {
            	if(DamageDiceOverride==0) {
            		be.add("Normal.STR",  new Integer( source.getCurrentStat("STR")), true, false);
            	}
            	else {
            		be.add("Normal.STR",  DamageDiceOverride *5, true, false);
                	
            	}
                
                power.adjustDice(be,"");
                
                if ( maneuver != null ) {
                    maneuver.getPower().adjustDice(be,"");
                }
                
                // Run through the sourceAlias Effects to let them adjust the dice
                // First build list, then run through list
                int i,count;
                count = source.getEffectCount();
                Effect[] sourceEffects = new Effect[count];
                for (i=0;i<count;i++) {
                    sourceEffects[i] = source.getEffect(i);
                }
                
                for (i=0;i<count;i++) {
                    if ( source.hasEffect(sourceEffects[i]) ) {
                        sourceEffects[i].adjustDice(be,"");
                    }
                }
                
                ChampionsUtilities.calculateDCs(be);
                
                return be.getDC();
            }
            catch ( BattleEventException bee ) {
            }
        }
        return 0;
    }
    
    /**
     * @return  */
    public String getNameWithDamage() {
        return getNameWithDamage(null);
    }
    
    /**
     * @param maneuver
     * @return  */
    public String getNameWithDamage(Ability maneuver) {
        StringBuffer sb = new StringBuffer();
        String damageString = null;
        
        if ( (Boolean)Preferences.getPreferenceList().getParameterValue("NameWithDamage" ) ) {
            Power power = getPower();
            Target source = getSource();
            if ( power != null ) {
                BattleEvent be = new BattleEvent( this , true);
                be.setSource(source);
                
                if ( maneuver != null ) {
                    be.setManeuver(maneuver, true);
                }
                
                BattleEngine.setupAttackParameters(be);
                
                try {
                    damageString = power.getDamagePrefix(be, maneuver);
                }
                catch ( BattleEventException bee ) {
                }
            }
            
            if ( ! damageString.equals("") ) {
                sb.append(damageString).append(" ");
            }
        }
        
        if ( this.name != null ){
            sb.append(this.name);
        }
        else {
            sb.append("(UNNAMED)");
        }
        
        if ( maneuver != null ) {
            sb.append(" w/").append(maneuver.getName());
        }
        
        return sb.toString();
    }
    
    /**
     * @return  */
    public String getDamageString() {
        return getDamageString(null);
    }
    
    /**
     * @param maneuver
     * @return  */
    public String getDamageString(Ability maneuver) {
        String name = "";

        Target source;
        Power power;
        if ( (power = (Power)getValue("Power.POWER" ) ) != null && (source = getSource() ) != null ) {
            BattleEvent be = new BattleEvent( this , true);
            be.setSource(source);

            if ( maneuver != null ) {
                be.setManeuver(maneuver, true);
            }

            BattleEngine.setupAttackParameters(be);

            try {
                name = power.getDamagePrefix(be, maneuver);
            }
            catch ( BattleEventException bee ) {
            }
        }
        
        return name;
    }
    
    /**
     */
    public void updateAbilityDescription() {
        StringBuffer sb = new StringBuffer();
        String special,name;
        int i,count;
        String desc;
        
        if ( powerDescription != null) {
            sb.append( powerDescription );
            
            if ( (special = getStringValue("Ability.SPECIALEFFECT" ) ) != null ) {
                sb.append(  "(" + special + ")");
            }
            
            count = getAdvantageCount();
            for (i=0;i<count;i++) {
                if ( ( desc = getAdvantage(i).getDescription() ) != null) {
                    sb.append(  ", " + desc );
                }
            }
            count = getLimitationCount();
            Limitation lim;
            for (i=0;i<count;i++) {
                if ( ( desc = getLimitation(i).getDescription() ) != null) {
                    sb.append(  ", " + desc );
                }
            }
            count = getIndexedSize("SpecialParameter");
            for (i=0;i<count;i++) {
                if ( ( desc = getIndexedStringValue(i, "SpecialParameter","DESCRIPTION")) != null) {
                    if ( desc != null && desc.equals("") == false) sb.append(  ", " + desc );
                }
            }
        }
        
        //add( "Ability.HTMLDESCRIPTION", ChampionsUtilities.createWrappedHTMLString(sb.toString(), 80), true, true);
        //add( "Ability.DESCRIPTION", sb.toString(), true, true);
        setDescription(sb.toString());
    }
    
    /**
     * @return  */
    public String getDescription() {
        if ( abilityDescription == null ) {
            updateAbilityDescription();
        }
        
        return abilityDescription;
    }
    
    public void setDescription(String description) {
        if ( abilityDescription == null || abilityDescription.equals(description) == false ) {
            
            String orig = abilityDescription;
                    
            abilityDescription = description;
            
            setHTMLDescription( ChampionsUtilities.createWrappedHTMLString(description, 80) );
            
            firePropertyChange(this, "Ability.DESCRIPTION", orig, abilityDescription);
        }
           
    }
    
    /**
     * @return  */
    public String getHTMLDescription() {
       
        if ( abilityHTMLDescription == null ) {
            updateAbilityDescription();
        }
        
        return abilityHTMLDescription;
    }
    
    public void setHTMLDescription(String desc) {
        abilityHTMLDescription = desc;
    }
    
    /**
     * @param description  */
    public void setPowerDescription(String description) {
        //add( "Power.DESCRIPTION", description, true, false);
        powerDescription = description;
        updateAbilityDescription();
    }
    
    /**
     * @param index
     * @param description  */
//    public void setAdvantageDescription(int index, String description) {
//        addIndexed(index, "Advantage", "DESCRIPTION", description, true, false);
//        updateAbilityDescription();
//    }
    
    /**
     * @param index
     * @param description  */
//    public void setLimitationDescription(int index, String description) {
//        //addIndexed(index, "Limitation", "DESCRIPTION", description, true, false);
//        
//        // The limitation should do this internally
//        getLimitation(index).setDescription( description );
//    }
    
    /**
     * @param index
     * @param description  */
    public void setSpecialParameterDescription(int index, String description) {
        addIndexed(index, "SpecialParameter", "DESCRIPTION", description, true, false);
        updateAbilityDescription();
    }
    
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        if ( this.name != name ) {
            String oldName = this.name;
            //add("Ability.NAME",  name,  true, true);
            this.name = name;
            
            Power p = getPower();
            if ( p != null ) {
                p.abilityNameSet(this,this.name,name);
            }
            
            int count, index;
            count = this.getAdvantageCount();
            for ( index = 0; index < count; index ++ ) {
                Advantage a = (Advantage)this.getAdvantage(index);
                a.abilityNameSet(this,this.name,name);
            }
            
            count = getLimitationCount();
            for ( index = 0; index < count; index ++ ) {
                Limitation lim = getLimitation(index);
                lim.abilityNameSet(this,this.name,name);
            }
            
            updateAbilityDescription();
            
            firePropertyChange(this, "Ability.NAME", oldName, this.name);
        }
        
        int count = getChildAbilityCount();
        for(int i = 0; i < count; i++) {
            Ability a = getChildAbility(i);
            a.setName(name);
        }
    }
    
    
    /**
     * 
     * 
     * @param source
     */
    public void setSource(Target source) {
        Target t = getSource();
        if ( t != source ) {
            //add("Ability.SOURCE", new TargetAlias(source), true, false);
            //add("Ability.SOURCE",  source,  true, false);
            if ( source != null ) {
                sourceAlias = new TargetAlias(source);
            }
            else {
                sourceAlias = null;
            }
            
            Power p = getPower();
            if ( p != null ) {
                p.abilitySourceSet(this,t,source);
            }
            
            int count, index;
            count = this.getAdvantageCount(); 
            for ( index = 0; index < count; index ++ ) {
                Advantage a = (Advantage)this.getAdvantage(index);
                a.abilitySourceSet(this,t,source);
            }
            count = getLimitationCount();
            for ( index = 0; index < count; index ++ ) {
                Limitation l = getLimitation(index);
                l.abilitySourceSet(this,t,source);
            }
        }
    }
    
    
    /**
     * Set the ability to be autosourced.
     * 
     * 
     * Every ability needs to have a Target as a sourceAlias of that ability when it
     * is activated.  Typically, we just set it with setSource(Target).
     * However, for the Abilities in the default ability panel (all the generic
     * ones), the sourceAlias is dependent on the current character.
     * setAutoSource(true) will setup the ability to determine its sourceAlias via
     * the currently active character.
     * 
     * You could use setAutoSource for other things, but in general it is
     * fairly dangerous in the long run.  For example, if you trigger an
     * autosourced ability from a character that isn't active, HCS will use the
     * active character.  That isn't a problem right now, since there isn't a
     * way to do that.  However, if you set a normal ability to autoSource, it
     * would be possible.
     * 
     * 
     * 
     * 
     * 
     * @param autoSource
     */
    public void setAutoSource(boolean autoSource) {
        if ( autoSource ) {
            
            this.autoSource = true;
            
            if ( getSource() != null ) {
                Power p = getPower();
                if ( p != null  ) {
                    p.abilitySourceSet(this,getSource(),null);
                }
                
                int count, index;
                count = this.getAdvantageCount();
                for ( index = 0; index < count; index ++ ) {
                    Advantage a = (Advantage)this.getAdvantage(index);
                    a.abilitySourceSet(this,getSource(),null);
                }
                count = getLimitationCount();
                for ( index = 0; index < count; index ++ ) {
                    Limitation l = getLimitation(index);
                    l.abilitySourceSet(this,getSource(),null);
                }
                
                setSource(null);
            }
            
//            add("Ability.AUTOSOURCE", "TRUE",  true, false);
//            remove("Ability.SOURCE");
            
            
            
        }
        else {
//            add("Ability.AUTOSOURCE", "FALSE",  true, false);
            this.autoSource = false;
        }
    }
    
    /**
     * @return  */
    public boolean isAutoSource() {
//        return getBooleanValue("Ability.AUTOSOURCE");
        return this.autoSource;
    }
    
    /**
     * @return  */
    public Target getSource() {
        if ( isAutoSource() ) {
            if ( Battle.currentBattle != null ) {
                return Battle.currentBattle.getActiveTarget();
            }
        }
        else if ( sourceAlias != null ) {
            return sourceAlias.getAliasReferent();
        }
        
        return null;
    }
    
    /**
     *
     * @return  */
    public String toString() {
        return getName();
    }
    
    /**
     * @param sourceAlias
     * @return 
     */
    public boolean isEnabled(Target source) {
        return isEnabled(source, true);
    }
    
    /**
     * 
     * 
     * @param sourceAlias
     * @param checkTime
     * @return 
     */
    public boolean isEnabled(Target source, boolean checkTime) {
        abilityIsEnabled++;
        
        setEnableMessage(null);
        setEnableColor(null);
        //setEnableMessage(null);
        // Make sure this acts upon the Current instance, not the base instance.
        if ( ! isModifiableInstance() && ! isCurrentInstance() ) return getInstanceGroup().getCurrentInstance().isEnabled(source, checkTime);
        
        if ( Battle.currentBattle == null ) {
            setEnableMessage("No Active Battle");
            return false;
        }
        
        if ( Battle.currentBattle.isStopped() ) {
            setEnableMessage("The current battle is stopped");
            return false;
        }
        
//        if ( Battle.currentBattle.isProcessing() ) {
//            setEnableMessage("Battle is currently processing other actions");
//            return false;
//        } 
        
        if ( Battle.currentBattle.getTime().isTurnEnd() ) {
            String s;
            if ( ( s = getStringValue("Ability.ISRECOVERY")) == null || s.equals("FALSE") ) {
                setEnableMessage("Ability Not Usable During Post 12 Recovery");
                return false;
            }
        }
        
        if ( source == null ) {
            if ( isAutoSource() ) {
                source = Battle.currentBattle.getActiveTarget();
            }
            else {
                source = this.sourceAlias.getAliasReferent();
            }
        }

        if(source.getCombatState() == CombatState.STATE_ABORTING && isAttack() && getCType()!="SKILL")
        {
        	return false;
        }
        
        if ( source == null ) {
            setEnableMessage("No Active Character");
            return false;
        }
        
        /*if ( isPersistent() ) {
            if ( isActivated(sourceAlias) == true ) {
                setEnableMessage("Persistent Ability always activated");
                return false;
            }
        }*/
        
        Framework fm = getFramework();
        if ( fm != null ) {
            if ( fm.isFrameworkAbilityEnabled(this, source) == false ) return false;
        }
        
        String effect;
        if ( source.isStunned() || source.isUnconscious() ) {
            String s;
            if ( ( s = getStringValue("Ability.ISRECOVERY")) == null || s.equals("FALSE") ) {
                setEnableMessage("Ability not usable while Stunned or Unconscious");
                return false;
            }
        }

        if ( checkTime ) {
            if ( Battle.currentBattle.getActiveTarget() != source) {
                setEnableMessage("Ability does not belong to active Character");
                return false;
            }
            else if ( source.getCombatState() == CombatState.STATE_FIN && source.isPostTurn() == false  ) {
                setEnableMessage("Character's phase is finished.");
                return false;
            }
            else if ( source.getCombatState() == CombatState.STATE_DELAYED && source.isPostTurn() == false) {
                setEnableMessage("Character is activating an ability.");
                return false;
            }
            else if ( source.getCombatState() == CombatState.STATE_INACTIVE ) {
                setEnableMessage("Character is inactive");
                return false;
            }
            else if ( getActivationTime().equals( "FULLMOVE" )
            && Battle.currentBattle.getTime().isTurnEnd() == false
            && !( source.getCombatState() == CombatState.STATE_HELD
            || source.getCombatState() == CombatState.STATE_ACTIVE
            || source.getCombatState() == CombatState.STATE_ABORTING ) ) {
                setEnableMessage("Character doesn't have enough time left in phase");
                return false;
            }
        }
        
        if ( source.isEgoPhase() && this.isEgoBased() == false ) {
            setEnableMessage("Character can only use EGO based power at this point.");
            return false;
        }
        
        if ( isDelayActivating(source) ) {
            setEnableMessage("This ability has a delayed activation time and is currently being activated.");
            return false;
        }
        /*int end;
        end = sourceAlias.getCurrentStat("END");
        if ( isActivated(sourceAlias) == false && end < calculateENDCost() ) {
            setEnableMessage("Not Enough END");
            return false;
        }*/
        if ( BattleEngine.isENDSourceActive(this, source) == false ) {
            setEnableMessage("The END Source is not available.");
            return false;
        }
        
        if ( isActivated(source) == false && BattleEngine.checkEND(this, source, false, false) <= 0 ) {
            boolean canBurnStun = BattleEngine.canBurnStun(this,  source);
            if ( canBurnStun ) {
                setEnableMessage("Not Enough END.  Will require stun burn to activate.");
                setEnableColor( warningColor );
            }
            else {
                setEnableMessage("Not Enough END and Stun cannot be burned.");
                return false;
            }
        }
        
        if ( getCPCost() < getMaximumCPAllocation()  ) {
            setEnableColor( warningColor );
            setEnableMessage( "<b>Configuration Error:</b> Not all Character Points are allocated for this ability.<br><br>" +
            Integer.toString(getCPCost()) + " configured, " + Integer.toString(getMaximumCPAllocation()) + " available." );
        }
        else if ( getCPCost() > getMaximumCPAllocation() ) {
            setEnableColor( errorColor );
            setEnableMessage( "<b>Configuration Error:</b> Configured Character Points for ability exceed adjust Character Points available.<br><br>" +
            Integer.toString(getCPCost()) + " configured, only " + Integer.toString(getMaximumCPAllocation()) + " available.");
        }
        else if ( isConfigurationValid() == false ) {
            setEnableColor( warningColor );
            setEnableMessage( "<b>Ability Misconfigured:</b> This ability is misconfigured.  Edit the ability to correct the misconfiguration.");
        }
        
        Power p = getPower();
        
        if ( p != null ) {
            if ( p.isEnabled(this,source) == false ) return false;
        }
        
        int index, count;
        count = getAdvantageCount();
        for (index = 0; index < count; index++) {
            Advantage a = (Advantage)getAdvantage(index);
            if ( a.isEnabled(this, index, source) == false ) return false;
        }
        
        count = getLimitationCount();
        for (index = 0; index < count; index++) {
            Limitation l = getLimitation(index);
            if ( l.isEnabled(this, index, source) == false ) return false;
        }
        
        count = source.getEffectCount();
        int i;
        Effect e;
        for (i=0;i<count;i++) {
            e = source.getEffect(i);
            Ability a = this;
            if (e != null && e.isEnabled(a) == false ) return false;
        }
        
        
        if ( source.getCurrentStat("STR") < getSTRMinimum() ) {
            setEnableMessage("STR Minimum not met.");
            setEnableColor( warningColor );
        }
        
        return true;
    }
    
    /**
     * Returns whether the Ability is activated.
     * 
     * If this ability is a non-modifiable instance (base, framework, adjusted)
     * then the current instances will be used instead to determine if the 
     * ability is activated.  Otherwise, the ability passed in will be used.
     * 
     * 
     * @param sourceAlias Target to check for activation.  If null, get the ability sourceAlias.
     * @return True if ability is activated, false otherwise.
     */
    public boolean isActivated(Target source) {
        if ( ! isModifiableInstance() && ! isCurrentInstance()) return getInstanceGroup().getCurrentInstance().isActivated(source);
       /* String active;
        if ( (active = getStringValue("Ability.ACTIVATED")) != null ) {
            if ( active.equals( "TRUE" ) ) {
                return true;
            }
        } */
        if ( source == null && Battle.getCurrentBattle() != null ) {
            source = Battle.currentBattle.getActiveTarget();
        }
        
        Iterator<ActivationInfo> it = getActivations(source);
        while(it.hasNext()) {
            if ( it.next().isActivated() ) return true;
        }
        
        return false;
    }
    
    /**
     * Returns whether the sourceAlias has this ability in a delayed activating state.
     * 
     *  Currently, this only included abilities which were previously 
     *  activated and delayed.  This should not be used during the
     *  the actual activation of the ability.
     * 
     * If this ability is a non-modifiable instance (base, framework, adjusted)
     * then the current instances will be used instead to determine if the 
     * ability is activated.  Otherwise, the ability passed in will be used.
     * 
     * 
     * @param sourceAlias Target to check for activation.  If null, get the ability sourceAlias.
     * @return True if ability is activated, false otherwise.
     */
    public boolean isDelayActivating(Target source) {
        if ( ! isModifiableInstance() && ! isCurrentInstance()) return getInstanceGroup().getCurrentInstance().isActivated(source);
       /* String active;
        if ( (active = getStringValue("Ability.ACTIVATED")) != null ) {
            if ( active.equals( "TRUE" ) ) {
                return true;
            }
        } */
        if ( source == null && Battle.getCurrentBattle() != null ) {
            source = Battle.currentBattle.getActiveTarget();
        }
        
//        ActivationInfo ai;
//        int index = -1;
//        while ( (index = this.findIndexed(index+1, "ActivationInfo","SOURCE", source)) != -1 ) {
//            ai = (ActivationInfo)this.getIndexedValue( index, "ActivationInfo", "ACTIVATIONINFO" );
//            if ( ai.isDelayed() ) return true;
//        }
        
        Iterator<ActivationInfo> it = getActivations(source);
        while(it.hasNext()) {
            if ( it.next().isDelayed() ) return true;
        }
        
        return false;
    }
    
    /** Sets the "Persistence" type of the ability.
     *
     * The persistence type determines how long a power is on and under what
     * conditions it will turn off.  It must be one of the following values:
     * "INSTANT", "CONSTANT", "PERSISTENT", "INHERENT".
     */
//    public void setPType(String ptype, boolean replace) {
//        add("Ability.PTYPE", ptype, replace, false);
//    }
    
    /** Gets the "Persistence" type of the ability.
     *
     * @see setPType
     */
//    public String getPType() {
//        return getStringValue("Ability.PTYPE");
//    }
    
    /** Sets the amount of time it takes to activate this ability.
     *
     * The activation time determines how much time is required to activate 
     * the power, and hence, how much time will be used by the character
     * who activates this power.
     *
     * ActivationTime should be one of the following: "HOLD", "INSTANT", "HALFMOVE",
     * "FULLMOVE", or "ATTACK".  Most are self explanetory.  HOLD causes the ability 
     * put the target into hold combat state.  ATTACK activation time uses the 
     * remainder of a characters phase.
     */
    public void setActivationTime(String activationTime, boolean replace) {
        //add("Ability.TIME", activationTime, replace);
        if ( this.activationTime == null || replace ) {
            this.activationTime = activationTime;
        }
    }
    
    /** Returns the Activation time of the ability.
     *
     */
    public String getActivationTime() {
        //return getStringValue("Ability.TIME");
        return activationTime;
    }
    
    /**
     * @return  */
    public boolean isPersistent() {
        return "PERSISTENT".equals(getPType());
    }
    
    /** Sets the power to be Persistent.
     *
     * This is unilaterally set the power to be persistent, overriding any
     * previous setting.
     */
    public void setPersistent() {
        //add("Ability.PTYPE", "PERSISTENT", true, false);
        setPType("PERSISTENT", true);
    }

    /** Sets the power to be Constant.
     *
     * This is unilaterally set the power to be persistent, overriding any
     * previous setting.
     */
    public void setConstant() {
        //add("Ability.PTYPE", "CONSTANT", true, false);
        setPType("CONSTANT", true);
    }

    
    /**
     * @return  */
    public boolean isConstant() {
        return "CONSTANT".equals(getPType());
    }
    
    /** Sets the power to be constant.
     *
     * The method will set the power to be constant.  If the override argument
     * is true, it will replace the previous setting regardless of what it
     * was.  If override is false, it will only change the instant, constant,
     * persistent level if it was previously a "lower" setting.
     */
    public void setConstant(boolean override) {
        String ptype;
           // add("Ability.PTYPE", "CONSTANT", true, false);
            setPType("CONSTANT", override);
          
    }
    
    /** Returns if this is an instant power.
     *
     * In this case, instant refers to the length of time the power will be 
     * active.  Other settings are constant and persistent.
     *
     * @return  */
    public boolean isInstant() {
        //String ptype = getStringValue("Ability.PTYPE");
        return ptype == null || "INSTANT".equals(ptype);
    }
    
    /** Sets the power to be constant.
     *
     * The method will set the power to be instant.  If the override argument
     * is true, it will replace the previous setting regardless of what it
     * was.  If override is false, it will not change the power if it is 
     * already constant or persistent.
     */
    public void setInstant(boolean override) {
//        if ( override ) {
//            add("Ability.PTYPE", "INSTANT", true, false);
//        }
        setPType("INSTANT", override);
    }
    
    /** Returns whether a power is inherent or not.
     *
     */
    public boolean isInherent() {
       return getBooleanValue("Ability.INHERENT"); 
    }
    
    /** Sets whether a power is inherent.
     *
     */
    public void setInherent(boolean inherent) {
        add("Ability.INHERENT", inherent?"TRUE":"FALSE", true, false);
    }
    
    /** Returns whether a power is inherent or not.
     *
     */
    public boolean isAlwaysOn() {
       return getBooleanValue("Ability.ALWAYSON"); 
    }
    
    /** Sets whether a power is inherent.
     *
     */
    public void setAlwaysOn(boolean alwaysOn) {
        add("Ability.ALWAYSON", alwaysOn?"TRUE":"FALSE", true, false);
    }
    
    /** Returns a cloned copy of the ability.
     *
     * A clone of the ability is not a perfect clone.  Since the clone will not be immediately attached
     * to the same abilityList, it's abilityList will be set to null.
     *
     * The Source target of a cloned ability will be let the same as the original.  However, the cloned
     * copy will not really be attached to the target until it is added to the target.
     */
    
    public Object clone() {
        
        Ability newAbility = createAbilityObject(true);
        
        // Copy Special Stuff...
        Object o;
        //newAbility.add("Ability.NAME", getName(),true, true);
        newAbility.name = name;
        
//        if ( ( o = getValue("Ability.SOURCE") ) != null ) {
//            newAbility.add("Ability.SOURCE", o, true, false);
//        }
        
//        if ( ( o = getValue("Ability.AUTOSOURCE") ) != null ) {
//            newAbility.add("Ability.AUTOSOURCE", o, true, false);
//        }
        newAbility.sourceAlias = sourceAlias;
        newAbility.autoSource = autoSource;
        
        // Don't copy the AbilityList of the original!!!
        
        
        int index, count;
        String key;
        
       /* count = specialParameterArray.length;
        for( index = 0; index < count; index++ ) {
            key = (String)specialParameterArray[index][1];
            if ( ( o = this.getValue(key) ) != null ) {
                newAbility.add(key, o, true, false);
            }
        }*/
        
        // First copy the advantages, disadvantages, special parameter...
        count = getAdvantageCount();
        for(index=0;index<count;index++) {
            //Limitation l = (Limitation)this.getIndexedValue( index, "Limitation","LIMITATION" );
            //newAbility.createIndexed("Limitation","LIMITATION",l);
            Advantage adv = getAdvantage(index);
            adv = adv.clone();
            newAbility.advantages.add(adv);
            adv.setAbility(newAbility);
        }
        
        count = getLimitationCount();
        for(index=0;index<count;index++) {
            //Limitation l = (Limitation)this.getIndexedValue( index, "Limitation","LIMITATION" );
            //newAbility.createIndexed("Limitation","LIMITATION",l);
            Limitation lim = getLimitation(index);
            lim = lim.clone();
            newAbility.limitations.add(lim);
            lim.setAbility(newAbility);
        }
        
        count = getIndexedSize( "SpecialParameter" ) ;
        for ( index=0;index<count;index++ ) {
            SpecialParameter l = (SpecialParameter)getIndexedValue( index, "SpecialParameter", "SPECIALPARAMETER" );
            newAbility.createIndexed("SpecialParameter", "SPECIALPARAMETER",l.clone());
        }
        
        count = getIndexedSize( "SpecialEffect" ) ;
        for ( index=0;index<count;index++ ) {
            SpecialEffect se = (SpecialEffect)getIndexedValue( index, "SpecialEffect", "SPECIALEFFECT" );
            newAbility.createIndexed("SpecialEffect", "SPECIALEFFECT",se);
        }
        
        if ( this.getPower() != null ) {
            Power power;
            
            power = (Power)this.getPower().clone();
            power.configurePAD(newAbility, power.getParameterList(this).clone());
            //power.configurePAD(newAbility, this, -1);
        }
        
        /*
        count = newAbility.getAdvantageCount();
        for(index=0;index<count;index++) {
            Advantage a = (Advantage)newAbility.getAdvantage(index);
            a.configurePAD(newAbility,this,index);
        }
        
        count = newAbility.getLimitationCount();
        for(index=0;index<count;index++) {
            Limitation l = newAbility.getLimitation(index);
            l.configurePAD(newAbility,this,index);
        } */
        
        count = newAbility.getIndexedSize( "SpecialParameter" ) ;
        for ( index=0;index<count;index++ ) {
            SpecialParameter l = (SpecialParameter)newAbility.getIndexedValue( index, "SpecialParameter", "SPECIALPARAMETER" );
            ParameterList pl = l.getParameterList(this, index);
            l.configure(newAbility,pl);
        }
        
        // Calculate the multipliers, and trigger CP and END calculations.
        newAbility.calculateMultiplier();
        
        return newAbility;
    }
    
    /**
     * @param advantage
     * @param name
     * @param parameterList
     * @return  */
    public int addAdvantageInfo(Advantage advantage, String name, ParameterList parameterList) {
//        int index;
//        
//        if ( ( index = findExactIndexed( "Advantage", "ADVANTAGE", advantage) ) == -1 ) {
//            index = createIndexed(  "Advantage","ADVANTAGE",advantage ,  false);
//        }
//        
//        addIndexed( index,  "Advantage", "NAME", name, true, false);
//        addIndexed( index,  "Advantage", "PARAMETERLIST", parameterList, true, false);
//        
//        fireIndexedChanged( "Advantage" );
//        return index;
        
        int index = -1;
        index = findExactAdvantage(advantage);
        
        
        // Add the advantage to the advantages list if it isn't already there.
        if ( index == -1 ) {
            advantages.add(advantage);
            index = advantages.size() - 1;
        }
        
        // Assign this advantage to this ability
        advantage.setAbility(this);
        
        // Setup the ParameterList
        // This should really be done directly now, but since we don't want
        // to change tons of existing code, we will just do it here.
        advantage.setParameterList(parameterList);
        
        // Ignore the name, since the advantage should already know it's own name
        
        fireIndexedChanged( "Advantage" );
        
        
        return index;
    }
    
    /**
     * @param index
     * @return  */
//    public int getAdvantagePriority(int index) {
//        Integer i;
//        if ( (i = this.getIndexedIntegerValue(index, "Advantage","PRIORITY")) == null ) return 2;
//        else return i.intValue();
//    }
    
    /** Sets the Advantage to have a cost of zero.
     *
     * Some advantage, when inherited from framework, have a cost of zero.  
     * This will set whether an advantages real cost should affect the 
     * cost of the ability.
     */
//    public void setAdvantageZeroCost(int index, boolean zeroCost) {
//        addIndexed(index, "Advantage", "ZEROCOST", zeroCost?"TRUE":"FALSE", true);
//        calculateMultiplier();
//    }
    
    /** Returns whether the advantage has zero cost.
     */
//    public boolean isAdvantageZeroCost(int index) {
//        return getIndexedBooleanValue(index, "Advantage", "ZEROCOST");
//    }
    
    
    /**
     * @param index
     * @param priority  */
//    public void setAdvantagePriority(int index, int priority) {
//        addIndexed( index,  "Advantage","PRIORITY", new Integer(priority) , true);
//    }
    
    /** Sets the private status of a Advantage.  
     *
     * This is generally only used for framework Advantage.  If a Advantage
     * is private, it is not passed onto member abilities.  The private status
     * is ignored for non-framework abilities.
     */
//    public void setAdvantagePrivate(int index, boolean isPrivate) {
//        addIndexed(index, "Advantage", "PRIVATE", isPrivate ? "TRUE" : "FALSE", true);
//    }
    
    /** Gets the private status of a limitation.  
     *
     * This is generally only used for framework Advantages.  If a Advantage
     * is private, it is not passed onto member abilities.  The private status
     * is ignored for non-framework abilities.
     */
//    public boolean isAdvantagePrivate(int index) {
//        return getIndexedBooleanValue(index, "Advantage", "PRIVATE");
//    }
    
    /** Returns the number of advantages in the Ability.
     *
     */
    public int getAdvantageCount() {
        return advantages.size();
    }
    
    /** Returns the indicated Advantage.
     *
     */
    public Advantage getAdvantage(int index) {
        return advantages.get(index);
    }
    
    public int findExactAdvantage(Advantage advantage) {
        for(int i = 0; i < advantages.size(); i++) {
            if ( advantages.get(i) == advantage) return i;
        }
        return -1;
    }
    
    /** Search for a advantage via equal().
     *
     *  Two advantages are considered equal if they are of the same class
     *  and they have related parameter lists.  Thus, findAdvantage is useful
     *  for locating related advantages within abilities of the same instance.
     */
    public int findAdvantage(Advantage adv) {
        return advantages.indexOf(adv);
    }
    
    /** Search for a advantage by name.
     *
     *  Finds the first advantage with the name <Code>advantageName</Code>.
     */
    public int findAdvantage(String advantageName) {
        for(int i = 0; i < getAdvantageCount(); i++) {
            if ( getAdvantage(i).getName().equals(advantageName) ) return i;
        }
        return -1;
    }
    
    /** Returns if the ability has a advantage related to <Code>Advantage</Code>.
     *
     * @param advantage advantage to look for.
     * @return True if the ability has a advantage with same class, false otherwise.
     */
    public boolean hasAdvantage(Advantage advantage) {
        return findAdvantage(advantage) != -1;
    }
    
    /**
     * Returns if the ability has a advantage with name <CODE>advantageName</CODE>.
     * @param a 
     * @return True if the ability has a advantage with name <CODE>advantageName</CODE>, false otherwise.
     */
    public boolean hasAdvantage(String advantageName) {
        if ( advantageName == null ) return false;
        return findAdvantage( advantageName ) != -1;
    }
    
//    public int getLimitationIndex(Limitation limitation) {
//        //return findExactIndexed("Limitation", "LIMITATION", limitation);
////        for(int i = 0; i < limitations.size(); i++) {
////            if ( limitations.get(i) == limitation) return i;
////        }
////        return -1;
//        return limitations.indexOf(limitation);
//    }
    
//    public void setAddedByFramework(PAD pad, boolean addedByFramework) {
//        if ( pad instanceof Advantage ) {
////            int index = findExactAdvantage((Advantage)pad);
////            if ( index != -1 ) {
////                addIndexed(index, "Advantage", "ADDEDBYFRAMEWORK", (addedByFramework?"TRUE":"FALSE"), true, false);
////            }
//            Advantage adv = (Advantage)pad;
//            adv.setAddedByFramework(addedByFramework);
//        }
//        else if ( pad instanceof Limitation ) {
//            //int index = getLimitationIndex((Limitation)pad);
//            Limitation lim = (Limitation)pad;
////            if ( index != -1 ) {
////                addIndexed(index, "Limitation", "ADDEDBYFRAMEWORK", (addedByFramework?"TRUE":"FALSE"), true, false);
////            }
//            lim.setAddedByFramework(addedByFramework);
//        }
//    }
    
//    public boolean isAddedByFramework(PAD pad) {
//        if ( pad instanceof Advantage ) {
//            int index = findExactAdvantage((Advantage)pad);
//            if ( index != -1 ) {
//                return getIndexedBooleanValue(index, "Advantage", "ADDEDBYFRAMEWORK");
//            }
//        }
//        else if ( pad instanceof Limitation ) {
////            int index = getLimitationIndex((Limitation)pad);
////            if ( index != -1 ) {
////                return getIndexedBooleanValue(index, "Limitation", "ADDEDBYFRAMEWORK");
////            }
//            return ((Limitation)pad).isAddedByFramework();
//        }
//        return false;
//    }
    
    public void setAddedByFramework(SpecialEffect se, boolean addedByFramework) {
        int index = getSpecialEffectIndex(se);
        if ( index != -1 ) {
            addIndexed(index, "SpecialEffect", "ADDEDBYFRAMEWORK", (addedByFramework?"TRUE":"FALSE"), true, false);
        }
    }
    
    public boolean isAddedByFramework(SpecialEffect se) {
            int index = getSpecialEffectIndex(se);
            if ( index != -1 ) {
                return getIndexedBooleanValue(index, "SpecialEffect", "ADDEDBYFRAMEWORK");
            }
        return false;
    }
    
    public int getSpecialEffectIndex(SpecialEffect se) {
        return findIndexed("SpecialEffect", "SPECIALEFFECT",  se);
    }
    
    /** Returns the number of Limitations in the Ability.
     *
     */
    public int getLimitationCount() {
        return limitations.size();
    }
    
    /** Returns the indicated Limitation.
     *
     */
    public Limitation getLimitation(int index) {
        return limitations.get(index);
    }
    
    /** Search for an exact limitation.
     *
     *  Returns the index of the exact limitation attached to an ability.
     */
    public int findExactLimitation(Limitation lim) {
        for(int i = 0; i < getLimitationCount(); i++) {
            if ( getLimitation(i) == lim ) return i;
        }
        return -1;
    }
    
    /** Search for a limitation via equal().
     *
     *  Two limitations are considered equal if they are of the same class
     *  and they have related parameter lists.  Thus, findLimitation is useful
     *  for locating related limitations within abilities of the same instance.
     */
    public int findLimitation(Limitation lim) {
//        for(int i = 0; i < getLimitationCount(); i++) {
//            if ( getLimitation(i).equals(lim) ) return i;
//        }
//        return -1;
        return limitations.indexOf(lim);
    }
    
    /** Search for a limitation by name.
     *
     *  Finds the first limitation with the name <Code>limitationName</Code>.
     */
    public int findLimitation(String limitationName) {
        for(int i = 0; i < getLimitationCount(); i++) {
            if ( getLimitation(i).getName().equals(limitationName) ) return i;
        }
        return -1;
    }
    
    /** Returns if the ability has a limitation related to <Code>Limitation</Code>.
     *
     * @param limitation limitation to look for.
     * @return True if the ability has a limitation with same class, false otherwise.
     */
    public boolean hasLimitation(Limitation limitation) {
       // return hasLimitation(limitation.getName());
        return findLimitation(limitation) != -1;
    }
    
    /**
     * Returns if the ability has a limitation with name <CODE>limitationName</CODE>.
     * @param a 
     * @return True if the ability has a limitation with name <CODE>limitationName</CODE>, false otherwise.
     */
    public boolean hasLimitation(String limitationName) {
        if ( limitationName == null ) return false;
        return findLimitation( limitationName ) != -1;
    }
    
//    public Object getLimitationFinalizer(int index) {
//        //return getIndexedValue(index, "Limitation", "FINALIZER");
//        return getLimitation(index).getFinalizer();
//    }
    
//    public void setLimitationFinalizer(int index, Object finalizer) {
//        //addIndexed(index, "Limitation", "FINALIZER", finalizer);
//        getLimitation(index).setFinalizer(finalizer);
//    }
    
//    public void removeLimitationFinalizer(int index) {
//        //removeIndexed(index, "Limitation", "FINALIZER");
//        getLimitation(index).setFinalizer(null);
//    }
    
//    public boolean getLimitationAddedByFramework(int index) {
////        String s = getIndexedStringValue(index, "Limitation", "ADDEDBYFRAMEWORK");
////        return s != null && s.equals("TRUE");
//        return getLimitation(index).isAddedByFramework();
//    }
    
//    public void setLimitationAddedByFramework(int index, boolean addedByFramework) {
//        //addIndexed(index, "Limitation", "ADDEDBYFRAMEWORK", addedByFramework ? "TRUE" : "FALSE");
//        getLimitation(index).setAddedByFramework(addedByFramework);
//    }
    
    /** Set the ParameterList for a limitation.
     *
     */
//    public void setLimitationParameterList(int index, ParameterList parameterList) {
//        //addIndexed(index, "Limitation", "PARAMETERLIST", parameterList, true);
//        getLimitation(index).setParameterList(parameterList);
//    }
    
    /** Gets the ParameterList for a limitation.
     *
     *  This method returns the parameter list stored for a limitation.
     *  This method should not be used directly to get the parameter list,
     *  as some limitation need to perform special initialization to
     *  parameter lists.
     */
//    public ParameterList getLimitationParameterList(int index) {
//        //return (ParameterList) getIndexedValue(index, "Limitation", "PARAMETERLIST");
//        return getLimitation(index).getParameterList();
//    }
    
    /** Returns the number of SpecialParameters in the Ability.
     *
     */
    public int getSpecialParameterCount() {
        return getIndexedSize("SpecialParameter");
    }
    
    /** Returns the indicated SpecialParameter.
     *
     */
    public SpecialParameter getSpecialParameter(int index) {
        return (SpecialParameter)getIndexedValue(index, "SpecialParameter", "SPECIALPARAMETER");
    }
    
    /** Returns the number of SpecialParameters in the Ability.
     *
     */
    public int getSpecialEffectCount() {
        return getIndexedSize("SpecialEffect");
    }
    
    /** Returns the indicated SpecialEffect.
     *
     */
    public SpecialEffect getSpecialEffect(int index) {
        return (SpecialEffect)getIndexedValue(index, "SpecialEffect", "SPECIALEFFECT");
    }
    
    /**
     * @param limitation
     * @param name
     * @param parameterList
     * @return  */
    public int addLimitationInfo(Limitation limitation, String name, ParameterList parameterList) {
//        int index;
//        
//        if ( ( index = findExactIndexed( "Limitation", "LIMITATION", limitation) ) == -1 ) {
//            index = createIndexed(  "Limitation","LIMITATION",limitation,  false );
//        }
//        
//        addIndexed( index,  "Limitation","NAME",name, true, false);
//        addIndexed( index,  "Limitation","PARAMETERLIST",parameterList, true, false);
//        
//        fireIndexedChanged( "Limitation" );
//        return index;
        
        int index = -1;
        index = findExactLimitation(limitation);
        
        
        // Add the limitation to the limitations list if it isn't already there.
        if ( index == -1 ) {
            limitations.add(limitation);
            index = limitations.size() - 1;
        }

        // Assign this limitation to this ability.
        limitation.setAbility(this);
        // Setup the ParameterList
        // This should really be done directly now, but since we don't want
        // to change tons of existing code, we will just do it here.
        limitation.setParameterList(parameterList);
        
        // Ignore the name, since the limitation should already know it's own name
        fireIndexedChanged( "Limitation" );
        
        
        return index;
    }
    
    /**
     * @param index
     * @return  */
//    public int getLimitationPriority(int index) {
//        //Integer i;
//        //if ( (i = this.getIndexedIntegerValue(index, "Limitation","PRIORITY")) == null ) return 2;
//        //else return i.intValue();
//        return limitations.get(index).getPriority();
//    }
    
    /**
     * @param index
     * @param priority  */
//    public void setLimitationPriority(int index, int priority) {
//        //addIndexed( index,  "Limitation","PRIORITY", new Integer(priority) , true);
//        limitations.get(index).setPriority(priority);
//    }
    
    /** Sets the private status of a limitation.  
     *
     * This is generally only used for framework limitations.  If a limitation
     * is private, it is not passed onto member abilities.  The private status
     * is ignored for non-framework abilities.
     */
//    public void setLimitationPrivate(int index, boolean isPrivate) {
//        //addIndexed(index, "Limitation", "PRIVATE", isPrivate ? "TRUE" : "FALSE", true);
//        limitations.get(index).setPrivateLimitation(isPrivate);
//    }
    
    /** Gets the private status of a limitation.  
     *
     * This is generally only used for framework limitations.  If a limitation
     * is private, it is not passed onto member abilities.  The private status
     * is ignored for non-framework abilities.
     */
//    public boolean isLimitationPrivate(int index) {
//        //return getIndexedBooleanValue(index, "Limitation", "PRIVATE");
//        return limitations.get(index).isPrivateLimitation();
//    }
    
    /**
     * @param specialParameter
     * @param name
     * @return  */
    public int addSpecialParameterInfo(SpecialParameter specialParameter, String name) {
        int index;
        
        if ( ( index = findExactIndexed( "SpecialParameter", "SPECIALPARAMETER", specialParameter) ) == -1 ) {
            index = createIndexed(  "SpecialParameter", "SPECIALPARAMETER", specialParameter,  false);
        }
        
        addIndexed( index,  "SpecialParameter","NAME",name, true, false);
        
    //fireIndexedChanged( "SpecialParameter" );
        return index;
    }
    
    /**
     * @param power
     * @param name
     * @param atype
     * @param ptype
     * @param time  */
    public void addPowerInfo(Power power, String name, String atype, String ptype, String time) {
        addPowerInfo(power, name, atype, ptype, time, null);
    }
    
    /**
     * @param power
     * @param name
     * @param atype
     * @param ptype
     * @param time
     * @param description  */
    public void addPowerInfo(Power power, String name, String atype, String ptype, String activationTime, String description) {
        if ( "POWER".equals( getCType() ) == false ) {
            setCType("POWER");
            //add("Ability.ATYPE", atype, false, false);
            setAType(atype, false);
            //add("Ability.PTYPE", ptype, false, false);
            setPType(ptype, false);
            //add("Power.DESCRIPTION", (description == null ? "" : description), true);
            powerDescription = (description == null ? "" : description);
            
            //add("Ability.TIME", time, false, false);
            setActivationTime(activationTime, false);

            add("Power.NAME", name, true);
            //add("Power.POWER", power, true, false);
            this.power = power;

            if ( ptype.equals( "PERSISTENT" )) {
                //add("Ability.NORMALLYON","TRUE", false, false);
                if ( hasSpecialParameter( SpecialParameterNormallyOn.specialParameterName ) == false ) {
                    setNormallyOn(true);
                }
            }
            add("Ability.CANTARGETSELF", "TRUE", false, false);
        }
    }
    
    /**
     * @param name
     * @param size
     * @param description  */
    public void addDiceInfo(String name, String size, String description) {
        int index;
        if ( (index = findIndexed( "Die", "NAME", name)) == -1 ) {
            index = createIndexed(  "Die","NAME",name) ;
        }
        addIndexed(index,"Die", "SIZE", size, true);
        addIndexed(index,"Die", "DESCRIPTION", description, true);
    }
    
    /**
     * @param name
     * @param size
     * @param description
     * @param diceType
     * @param stunLabel
     * @param bodyLabel  */
    public void addDiceInfo(String name, String size, String description, String diceType, String stunLabel, String bodyLabel) {
        int index;
        if ( (index = findIndexed( "Die", "NAME", name)) == -1 ) {
            index = createIndexed(  "Die","NAME",name) ;
        }
        addIndexed(index,"Die", "SIZE", size, true);
        addIndexed(index,"Die", "DESCRIPTION", description, true);
        addIndexed(index,"Die", "TYPE", diceType, true);
        addIndexed(index,"Die", "STUNLABEL", stunLabel, true);
        addIndexed(index,"Die", "BODYLABEL", bodyLabel, true);
    }
    
    public void removeDiceInfo(String name) {
        int index;
        if ( (index = findIndexed( "Die", "NAME", name)) == -1 ) {
            removeAllIndexed(index, "Die");
        }
    }
    
    /**
     * @param mtype
     * @param ktype  */
    public void addAttackInfo(String mtype, String ktype ) {
        add("Ability.MTYPE", mtype,  true, false);
        add("Ability.KTYPE", ktype,  true, false);
        if ( mtype.equals( "RANGED" ) ) {
            setIs( "RANGED", false );
            setCan( "USERANGEDMANEUVER", false);
        }
        
        if ( mtype.equals( "MELEE" ) ) {
            add("Ability.STRADDSTODC", "TRUE",  true, false);
            setCan( "USEMELEEMANEUVER", false);
        }
    }
    
    /** Removes the specified advantage from the ability.
     *
     * This method will remove the specified advantage from the ability.  The method will call
     * the advantages remove function, then completely reconfigure the power, advantages, and limitations
     * of the ability.  Finally, CP costs will be recalculated.
     */
    public void removeAdvantage(Advantage a) {
        int index = findExactAdvantage(a);
        if ( index != -1 ) {
            removeAdvantage(index);
        }
    }
    
    /** Removes the specified advantage from the ability.
     *
     * This method will remove the specified advantage from the ability.  The method will call
     * the advantages remove function, then completely reconfigure the power, advantages, and limitations
     * of the ability.  Finally, CP costs will be recalculated.
     */
    public void removeAdvantage(int index) {
        Advantage adv = advantages.get(index);
        
        if ( adv != null ) {
            //ParameterList pl = lim.getParameterList(this, index);
            ParameterList pl = adv.getParameterList();
            
            int ccount = getChildAbilityCount();
            
            // Remove this advantages from all of the children of this ability
            for(int cindex = 0; cindex < ccount; cindex++) {
                Ability child = getChildAbility(cindex);
                int acount = child.getAdvantageCount();
                for(int aindex = 0; aindex < acount; aindex++) {
                    Advantage a2 = child.getAdvantage(aindex);
                    
                    if ( a2.getClass().equals(adv.getClass()) && 
                        a2.getParameterList(child, aindex).getParent() == pl ) {
                            child.removeAdvantage(aindex);
                            break;
                    }
                }
            }
            
            // Remove the advantage
            adv.removeAdvantage(this, index);
            
            // Set the ability to null
            adv.setAbility(null);
            
            // Remove the Info from the Ability
            advantages.remove(index);
            
            //this.removeAllIndexed(index, "Advantage");
            //fireIndexedChanged("Advantage");
            
            // Reconfigure the ability (this will cause a CP cost calculation)
            reconfigure();
        }
    }
    
    /** Removes the specified limitation from the ability.
     *
     * This method will remove the specified limitation from the ability.  The method will call
     * the limitation's remove function, then completely reconfigure the power, advantages, and limitations
     * of the ability.  Finally, CP costs will be recalculated.
     */
    public void removeLimitation(Limitation l) {
        //int index = findExactIndexed("Limitation", "LIMITATION", l);
        int index = findExactLimitation(l);
        
        if ( index != -1 ) {
            removeLimitation(index);
        }
        
    }
    
    /** Removes the specified Limitation from the ability.
     *
     * This method will remove the specified Limitation from the ability.  The method will call
     * the Limitation's remove function, then completely reconfigure the power, advantages, and limitations
     * of the ability.  Finally, CP costs will be recalculated.
     */
    public void removeLimitation(int index) {
        //Limitation l = getLimitation(index);
        Limitation lim = limitations.get(index);
        
        if ( lim != null ) {
            //ParameterList pl = lim.getParameterList(this, index);
            ParameterList pl = lim.getParameterList();
            
            int ccount = getChildAbilityCount();
            
            // Remove this limitations from all of the children of this ability
            for(int cindex = 0; cindex < ccount; cindex++) {
                Ability child = getChildAbility(cindex);
                int acount = child.getLimitationCount();
                for(int aindex = 0; aindex < acount; aindex++) {
                    Limitation a2 = child.getLimitation(aindex);
                    
                    if ( a2.getClass().equals(lim.getClass()) && 
                        a2.getParameterList(child, aindex).getParent() == pl ) {
                            child.removeLimitation(aindex);
                            break;
                    }
                }
            }
            
            // Remove the limitation
            lim.removeLimitation(this, index);
            
            // Set the ability to null
            lim.setAbility(null);
            
            // Remove the Info from the Ability
            limitations.remove(index);
            
            //this.removeAllIndexed(index, "Limitation");
            //fireIndexedChanged("Limitation");
            
            // Reconfigure the ability (this will cause a CP cost calculation)
            reconfigure();
        }
    }
    
    /** Removes the specified Special Parameter from the ability.
     *
     * This method will remove the specified Special Parameter from the ability.  The method will call
     * the Special Parameters remove function, then completely reconfigure the power, advantages, and limitations
     * of the ability.
     */
    public void removeSpecialParameter(SpecialParameter a) {
        int index = findExactIndexed("SpecialParameter", "SPECIALPARAMETER", a);
        if ( index != -1 ) {
            removeSpecialParameter(index);
        }
    }
    
    /** Removes the specified SpecialParameter from the ability.
     *
     * This method will remove the specified SpecialParameter from the ability.  The method will call
     * the SpecialParameter remove function, then completely reconfigure the power, advantages, and limitations
     * of the ability.
     */
    public void removeSpecialParameter(int index) {
        SpecialParameter a = (SpecialParameter)getIndexedValue(index, "SpecialParameter", "SPECIALPARAMETER");
        
        if ( a != null ) {
            // If the children have this modifier, remove it from them.
            ParameterList pl = a.getParameterList(this, index);
            int ccount = getChildAbilityCount();
            
            for(int cindex = 0; cindex < ccount; cindex++) {
                Ability child = getChildAbility(cindex);
                int acount = child.getSpecialParameterCount();
                for(int aindex = 0; aindex < acount; aindex++) {
                    SpecialParameter a2 = child.getSpecialParameter(aindex);
                    
                    if ( a2.getClass().equals(a.getClass()) && 
                        a2.getParameterList(child, aindex).getParent() == pl ) {
                            child.removeSpecialParameter(aindex);
                            break;
                    }
                }
            }
            
            a.remove(this, index);
            
            // Remove the Info from the Ability
            this.removeAllIndexed(index, "SpecialParameter");
            
            // Reconfigure the ability (this will cause a CP cost calculation
            reconfigure();
        }
    }
    
    /**
     * @return  */
    public boolean isAttack() {
        return contains("Ability.MTYPE" );
    }
    
    /**
     * @return  */
    public boolean isMeleeAttack() {
        Object mtype = getValue("Ability.MTYPE");
        return (mtype != null && mtype.equals("MELEE")) ? true : false;
    }
    
    /**
     * @return  */
    public boolean isRangedAttack() {
        return getBooleanValue("Ability.ISRANGED");
    }
    
    /**
     * @return  */
    public boolean hasRangeModifier() {
        String mod = getStringValue("Ability.HASRANGEMODIFIER");
        if ( mod == null ) return isRangedAttack();
        
        return mod.equals("TRUE");
    }
    
    /** Indicates that ability is a Throw ability.
     *
     * This indicates that a large object is being thrown.  This should not
     * be used for small, strength propelled, ability based attacks (such as
     * a throwing ax or something similar).  Use RKA for those.  This generally
     * for throwing large objects (People, car, tanks, etc...).
     *
     * Throws are considered a whole different attack type and can not, generally
     * have modifiers attached to them (such as AoE, Penetrating, etc...).
     * 
     * @return  */
    public boolean isThrow() {
        return getBooleanValue("Ability.ISTHROW");
    }
    
    /** Sets that this is a Throw ability.
     *
     * This indicates that a large object is being thrown.  This should not
     * be used for small, strength propelled, ability based attacks (such as
     * a throwing ax or something similar).  Use RKA for those.  This generally
     * for throwing large objects (People, car, tanks, etc...).
     *
     * Throws are considered a whole different attack type and can not, generally
     * have modifiers attached to them (such as AoE, Penetrating, etc...).
     */
    public void setIsThrow(boolean isThrow) {
        add("Ability.ISTHROW", isThrow ? "TRUE" : "FALSE", true);
    }
    
    /** Indicates that ability is a Throw ability.
     *
     * @return  */
    public boolean isGrab() {
        return getBooleanValue("Ability.ISGRAB");
    }
    
    /** Sets that this is a Grab ability..
     */
    public void setIsGrab(boolean isGrab) {
        add("Ability.ISGRAB", isGrab ? "TRUE" : "FALSE", true);
    }
    
    public boolean hasClassOfMindDamagePenalty() {
        String string= getStringValue("Ability.OTHERCLASSOFMINDDAMAGEPENALTY");
        if ( string == null ) return false;
        
        return string.equals("TRUE");
    }
    
    public void setHasRangeModifier(boolean r) {
        this.add("Ability.HASRANGEMODIFIER", r?"TRUE":"FALSE", true);
    }
    
    /**
     * @return  */
    public boolean isKillingAttack() {
        Object ktype = getValue("Ability.KTYPE");
        return (ktype != null && ktype.equals("KILLING")) ? true : false;
    }
    
    /**
     * @return  */
    public boolean isNormalAttack() {
        Object ktype = getValue("Ability.KTYPE");
        return (ktype != null && ktype.equals("NORMAL")) ? true : false;
    }
    
    public boolean isNND() {
        return noNormalDefense;
    }
    
    public void setNND(boolean noNormalDefense) {
        this.noNormalDefense = noNormalDefense;
    }
    
    public String getNNDDefense() {
        return nndDefense;
    }
    
    public void setNNDDefense(String nndDefense) {
        this.nndDefense = nndDefense;
    }
    
    /**
     * @return  */
    public boolean isDefense() {
        Power p = getPower();
        
        return p.getClass().equals( powerArmor.class )
        || p.getClass().equals( powerDamageResistance.class )
        || p.getClass().equals( powerForceField.class )
        || p.getClass().equals( powerMentalDefense.class )
        || p.getClass().equals( powerFlashDefense.class )
        || p.getClass().equals( powerKnockbackResistance.class ) 
        || p.getClass().equals( powerAutomaton.class ) 
        || p.getClass().equals( powerNoHitLocations.class ) 
        || p.getClass().equals( powerDoesNotBleed.class ) ;
        // || p.getClass().equals( champions.powers.powerLackOfWeakness.class );
    }
    
    /**
     * @return  */
    public boolean strengthAddsToDC() {
        return getBooleanValue("Ability.STRADDSTODC");
    }
    
    public void setStrengthAddsToDC(boolean value) {
       add("Ability.STRADDSTODC", value?"TRUE":"FALSE",true);;
    }
    
    
    /**
     * @param self  */
    public void setTargetSelf(boolean self) {
        //add("Ability.ATYPE", ( self ) ? "SELF" : "SINGLE",  true, false);
        setAType( ( self ) ? "SELF" : "SINGLE", true );
    }
    
    /**
     * @return  */
    public boolean isTargetSelf() {
        return getAType().equals("SELF") || getAType().equals("QUICKSELF");
    }
    
    public void setAType(String atype, boolean replace) {
        if ( replace || this.atype == null ) {
            this.atype = atype;
        }
    }
    
    public void clearAType() {
        atype = null;
    }
    
    public String getAType() {
        return (atype == null ? "SELF" : atype);
    }
    
    public void setPType(String ptype, boolean replace) {
        if ( replace || this.ptype == null ) {
            this.ptype = ptype;
        }
    }
    
    public void clearPType() {
        ptype = null;
    }
    
    public String getPType() {
        return ptype;
    }
    
    
    public boolean getDoesKnockback() {
        return getBooleanValue("Ability.DOESKNOCKBACK");
    }
    
    public void setDoesKnockback(boolean doesKnockback) {
        add("Ability.DOESKNOCKBACK", doesKnockback ? "TRUE" : "FALSE", true, false);
    }
    
    
    /**
     * @param usesHitLocation  */
    public void setUsesHitLocation(boolean usesHitLocation) {
        add("Ability.USESHITLOCATION", usesHitLocation?"TRUE":"FALSE", true, false);
    }
    
    public boolean setPower(Power power, ParameterList pl) {
        return addPAD(power, pl);
    }
    
    /**
     * @param pad
     * @param d
     * @return  */
    public boolean addPAD(PAD pad, ParameterList pl) {
        if ( pad instanceof Power && this.power != null ) {
            return false;
        }
        
        if ( pad instanceof Advantage && ((Advantage)pad).isUnique() && hasAdvantage(pad.getName()) ) {
            return false;
        }
        
        if ( pad instanceof Limitation ) {
        		if(((Limitation)pad).isUnique() && hasLimitation(pad.getName()))  {
        			return false;
        		}
        }
        if ( pl == null ) pl = pad.createParameterList(null,  -1);
        boolean value = pad.configurePAD( this, pl);

        if ( value && ( pad instanceof Power || pad instanceof Advantage || pad instanceof Limitation ) ) calculateMultiplier();
        
        if ( pad instanceof Advantage ) {
            int index = findExactAdvantage((Advantage)pad);
            // If the advantage was added above, then we need to it to the 
            // children abilities...
            if ( index != -1 ) {
                // Add the pad to the children...
                int count = getChildAbilityCount();
                for(int i = 0; i < count; i++) {
                    Ability child = getChildAbility(i);
                    
                    Advantage childAdv = ((Advantage)pad).clone();
                    ParameterList pl2 = new ParameterList(pl);
                    child.addPAD(childAdv, pl2);
                }
            }
        }
        else if ( pad instanceof Limitation ) {
            int index = findExactLimitation((Limitation)pad);
            // If the limitation was added above, then we need to it to the 
            // children abilities...
            if ( index != -1 ) {
                // Add the pad to the children...
                int count = getChildAbilityCount();
                for(int i = 0; i < count; i++) {
                    Ability child = getChildAbility(i);
                    
                    Limitation childLim = ((Limitation)pad).clone();
                    ParameterList pl2 = new ParameterList(pl);
                    child.addPAD(childLim, pl2);
                }
            }
        }
        
        return value;
    }
    
    /**
     * @param sp
     * @return  */
    public boolean addSpecialParameter(SpecialParameter sp) {
        if ( sp.isUnique() && findIndexed("SpecialParameter", "SPECIALPARAMETER", sp) != -1 ) {
            return false;
        }
        
        boolean value = true;
        
        value = sp.configure(this);
        
        int index = findExactIndexed("SpecialParameter", "SPECIALPARAMETER",  sp);
        if ( index != -1 ) {
            ParameterList pl = sp.getParameterList(this, index);
            // Add the pad to the children...
            int count = getChildAbilityCount();
            for(int i = 0; i < count; i++) {
                Ability child = getChildAbility(i);
                child.addSpecialParameter(sp);
                ParameterList pl2 = new ParameterList(pl);
                int index2 = child.findExactIndexed("SpecialParameter", "SPECIALPARAMETER", sp);
                child.reconfigureSpecialParameter(sp, pl2, index2); 
            }
        }
        
        fireIndexedChanged("SpecialParameter");
        
        return value;
    }
    
    /**
     * @param reason
     * @param interruptLevel
     * @param dcvMultiplier
     * @param isDelayExclusive
     * @param delayTime
     * @param delayCount
     * @param delayTillLast
     * @return  */
    public int addDelayInfo(String reason, int interruptLevel, double dcvMultiplier, boolean isDelayExclusive, int delayTime, int delayCount, boolean delayTillLast) {
        int index = findIndexed("Delay", "REASON", reason);
        if ( index == -1 ) {
            index = createIndexed("Delay", "REASON", reason);
        }
        
        addIndexed(index, "Delay", "INTERRUPTLEVEL", new Integer(interruptLevel),  true);
        addIndexed(index, "Delay", "DCVMULTIPLIER",  new Double(dcvMultiplier),  true);
        
        addIndexed(index, "Delay", "DELAYEXCLUSIVE", isDelayExclusive?"TRUE":"FALSE",  true);
        addIndexed(index, "Delay", "DELAYTIME",  new Integer(delayTime) ,  true);
        addIndexed(index, "Delay", "DELAYCOUNT",  new Integer(delayCount) ,  true);
        addIndexed(index, "Delay", "DELAYTILLLAST", delayTillLast?"TRUE":"FALSE",  true);
        
        return index;
    }
    
    /**
     * @param reason  */
    public void removeDelayInfo(String reason) {
        int index = findIndexed("Delay", "REASON", reason);
        if ( index != -1 ) {
            removeAllIndexed(index, "Delay");
        }
    }
    
    /** Calculates the Advantage/Limitation multiplier for the ability.
     * calculateMultiplier() calculates the Advantage Multiplier and Limitation Multiplier
     * as the Ability is currently configured.  These Multipliers are cached via the
     * Ability.ADVCOST and Ability.LIMCOST v/p, respectively.  A third multiplier, the
     * Advantages which affect DC calculations, is also calculated and cached in the
     * Ability.ADVDCCOST v/p.
     *
     * If any of the multipliers are different then thier cached values, calculateCPCost()
     * will be called to update the Cached CP cost of the ability. As a side effect,
     * calculateCPCost will call calculateENDCost to update the cached END cost.
     */
    public void calculateMultiplier() {
        int index, count;
        double advmultiplier, advdcmultiplier, limmultiplier;
        double cost;
        
        double oldAdvDC, oldAdv, oldLim;
        boolean recalcCP = false;
        
        oldAdv = getAdvCost();
        oldAdvDC = getAdvDCCost();
        oldLim = getLimCost();
        
        //System.out.println("Calculated Multiplier for: " + this );
        advmultiplier = 0;
        advdcmultiplier = 0;
        limmultiplier = 0;
        
        advmultiplier = getPower().getAdvantageMultiplier(this);
        limmultiplier = getPower().getLimitationMultiplier(this);
        
        count = getAdvantageCount();
        
        for ( index = 0; index < count; index ++ ) {
            Advantage a = getAdvantage(index);
            if ( a.isZeroCost() ) continue; // Skip zero cost adv.
            cost = a.calculateMultiplier(this,index);
            if ( cost > 0 ) {
                advmultiplier += cost;
                if ( a.affectsDC(this, index) ) {
                    advdcmultiplier += cost;
                }
            }
            else {
                // Negative Cost returned, so this is actually a limitation in disguise
                limmultiplier += cost;
            }
        }
        
        if ( oldAdv != advmultiplier || oldAdvDC != advdcmultiplier ){
            recalcCP = true;
        }
        
        count = getLimitationCount();
        for ( index = 0; index < count; index ++ ) {
            Limitation l = getLimitation(index);
            cost = l.calculateMultiplier(this,index);
            if ( cost > 0 ) {
                advmultiplier += cost;
            }
            else {
                // Negative Cost returned, so this is actually a limitation in disguise
                limmultiplier += cost;
            }
        }
        
        if ( oldLim != limmultiplier ) {
            recalcCP = true;
        }
        
        setAdvCost(advmultiplier);
        setAdvDCCost(advdcmultiplier);
        setLimCost(limmultiplier);
        
        if ( recalcCP == true ) {
            calculateCPCost();
        }
    }
    
    /**
     * @param windowName  */
    public void editAbility(String windowName) {
        JFrame f = new JFrame(windowName);
        
        final AbilityInstanceEditor aie = new AbilityInstanceEditor();
        aie.setBaseAbility(this);
        
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(aie, BorderLayout.CENTER);
        
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        f.addWindowListener( new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                aie.destroy();
            }
        });
        
        f.pack();
        f.setVisible(true);
    }
    
    /** Getter for property enableMessage.
     * The enable message is set directly due to the constant updates that occur to the enable message.
     * @return Value of property enableMessage.
     */
    public String getEnableMessage() {
        return enableMessage;
        
    }
    /** Setter for property enableMessage.
     * @param enableMessage New value of property enableMessage.
     */
    public void setEnableMessage(String enableMessage) {
        if ( (this.enableMessage != null &&  this.enableMessage.equals(enableMessage) == false)
        || ( this.enableMessage == null && enableMessage != null ) ) {
            String oldMessage = this.enableMessage;
            
           // if ( enableMessage != null && enableMessage.startsWith("<HTML>") == false ) {
           //     enableMessage = ChampionsUtilities.createWrappedHTMLString(enableMessage, 80);
           // }
            
            this.enableMessage = enableMessage;
            
            if ( enableMessage != null ) {
                firePropertyChange(this, "ENABLEMESSAGE", oldMessage, enableMessage);
            }
        }
    }
    
    /** Getter for property enableMessage.
     * The enable color is set directly in the ability due to the constant updates that occur to the enable color.
     * @return Value of property enableMessage.
     */
    public Color getEnableColor() {
        return enableColor;
        //return getStringValue("Ability.ENABLEMESSAGE");
    }
    /** Setter for property enableMessage.
     * @param enableMessage New value of property enableMessage.
     */
    public void setEnableColor(Color enableColor) {
        this.enableColor = enableColor;
        //add("Ability.ENABLEMESSAGE", enableMessage, true, false);
    }
    
    /** Returns the color to use for enable warnings.
     *
     */
    public static Color getEnableWarningColor() {
        return warningColor;
    }
    
    /** Returns the color to use for enable warnings.
     *
     */
    public static Color getEnableErrorColor() {
        return errorColor;
    }
    
    /** Calculates the END cost of the ability as it is configured in this instance.
     * calculateENDCost calculates the END of the ability as it is configured.
     * This END is cache via the Ability.ENDCOST v/p.
     *
     * Do not use this function to get the END cost of the ability.  Use getENDCost() as
     * it is more efficient.
     */
    public int calculateENDCost() {
        
        double adv,lim,cost;
        Double endMultiplier;
        
        Power p = getPower();
        if ( p == null ) {
            return 0;
        }
        
        // Grab the BASE END for the power.  This should be the current CP allocated to the
        // Power
        double base = (double)p.calculateENDCost(this);

        adv = getAdvCost();
        
        // Figure out the cost of Reduced end if applicable.
        double reducedCost = 0;
        int rindex = findAdvantage( advantageReducedEndurance.advantageName );
        
        if ( rindex != -1 ) {
            Advantage a = (Advantage)getAdvantage(rindex);
            reducedCost = a.calculateMultiplier(this, rindex);
        }
        
        // Adjust Advantage Cost by Reduced END cost
        adv -= reducedCost;
        
        // Calculate the ActiveCost of the Power (minus Reduced END)
        cost = ChampionsUtilities.roundValue(base * ( 1 + adv ), false);
        
        // Divide activePoints by 10 to find base END cost
        cost = ChampionsUtilities.roundValue(cost/10.0, false);
        
        // Now apply END multipliers (from IncreasedEND/ReducedEND)
        //if ( (endMultiplier = this.getDoubleValue("Ability.ENDMULTIPLIER")) == null ) {
        //    endMultiplier = new Double(1);
        //}
        endMultiplier = getENDMultiplier();
        
        
        cost = cost * endMultiplier;
        cost = ChampionsUtilities.roundValue(cost, false );
        
        if ( cost == 0 && endMultiplier.doubleValue() != 0 ) {
            cost = 1;
        }
        
        
        setENDCost( (int)cost );
        
        return (int)cost;
    }
    
    
    /** Returns the END Cost of the ability.
     *
     * This version of getENDCost assumes that the ability is first being
     * activated.  If you need to discriminate between first activation
     * and continuing END cost, use the getENDCost(boolean) version.
     */
    public int getENDCost() {
        return getENDCost(false);
    }
    
    /** Returns the END Cost of the ability.
     *
     * The @param continuing determines where the activation END or the
     * continuing END is returned.  In most cases, this will be the 
     * same. 
     */
    public int getENDCost(boolean continuing) {
        if ( continuing && isCostsENDOnlyToActivate() ) return 0;
        
        if ( endCost == -1 ) {
            return calculateENDCost();
        }
            
        return endCost;
    }
    
    /** Calculates the CP cost of the ability as it is configured in this instance.
     * calculateCPCost calculates the actual cost of the ability as it is configured.
     * This cost is cache via the Ability.CPCOST v/p.  If the cost is different then
     * the previously cached value, calculateEND is called to update the cached END
     * cost also.
     *
     * Do not use this function to get the CP cost of the ability.  Use getCPCost() as
     * it is more efficient.
     */
    
    public int calculateCPCost() {
        int base = 0;
        double advCost;
        double limCost;
        int real = 0;
        int cp = 0;
        int ap = 0;
        
        int oldcp = this.cpCost;
        
        if ( isFixedCPEnabled() ) {
            base = getFixedCPCost();
        }
        else {
            Power p = getPower();
            base = ( p==null )? 0 : p.calculateCPCost(this);
        }
        // Store the power cost for reference.
        setPowerCost(base);
        
        Double d;
        
        //d = getDoubleValue("Ability.ADVCOST" );
        //advCost = (d==null)?0: d.doubleValue();
        advCost = getAdvCost();
        
        //d = getDoubleValue("Ability.LIMCOST" );
        //limCost = (d==null)?0: d.doubleValue();
        limCost = getLimCost();
        
        cp = ChampionsUtilities.roundValue( base * ( 1 + advCost ) / ( 1 - limCost ), false); 
        
        if ( isVariationInstance() ) {
            real = 0;
        }
        else {
            Framework fm =  getFramework();
        
            if ( fm != null  ) {
                real = fm.calculateCost(this, base, advCost, limCost);
            }
            else {
                real = cp;
            }
        }
        
        ap = ChampionsUtilities.roundValue( base * ( 1 + advCost ), false);
        
        setAPCost(ap);
        setCPCost(cp);
        setRealCost(real);
        
        if ( oldcp != cp ) {
            calculateENDCost();
        }
        
        return cp;
    }
    
    /** Returns the total amount of advantages applied to the ability.
     * 
     */
    public double getAdvantagesMultiplier() {
        return advCost;
    }
    
    /** Returns the total amount of limitations applied to the ability.
     * 
     */
    public double getLimitationsMultiplier() {
        return limCost;
    }
    
    /**
     * Returns the CP cost of the ability, as it is currently configured.
     * 
     * The getCPCost method returns the true cost of the ability, using
     * the standard PowerCost * ( 1 + Advantage ) / ( 1 - Limitations )
     * formula.  It does not take into account any framework cost calculates
     * or anything else.  It is just the straight forward calculation.<P>
     * 
     * The getRealCost() method can be used to obtain the amount that is
     * actually paid by the character to have the ability.  This typically
     * only has meaning when talking about the base instances of an ability,
     * since this is the only one that the character pays for.  The RealCost
     * is affected by the framework that the ability belongs to, if any.<P>
     * 
     * The getAPCost() method returns the ActivePoint cost of the ability,
     * as currently configured.<P>
     * 
     * 
     * @return real cost of the ability, as currently configured.
     */
    public int getCPCost() {
        if ( cpCost == -1 ) {
            return calculateCPCost();
        }
        else {
            return cpCost;
        }
    }
    
    /**
     * Returns the Active Point cost of the ability, as it is currently configured.
     * 
     * The getCPCost method returns the true cost of the ability, using
     * the standard PowerCost * ( 1 + Advantage ) / ( 1 - Limitations )
     * formula.  It does not take into account any framework cost calculates
     * or anything else.  It is just the straight forward calculation.<P>
     * 
     * The getRealCost() method can be used to obtain the amount that is
     * actually paid by the character to have the ability.  This typically
     * only has meaning when talking about the base instances of an ability,
     * since this is the only one that the character pays for.  The RealCost
     * is affected by the framework that the ability belongs to, if any.<P>
     * 
     * The getAPCost() method returns the ActivePoint cost of the ability,
     * as currently configured.<P>
     * 
     * 
     * @return Active Point of the ability, as currently configured.
     */
    public int getAPCost() {
        if ( apCost == -1 ) {
            calculateCPCost();
        }
        return apCost;
    }
    
    /**
     * Returns the real CP cost of the ability, as it is currently configured.
     * 
     * The getCPCost method returns the true cost of the ability, using
     * the standard PowerCost * ( 1 + Advantage ) / ( 1 - Limitations )
     * formula.  It does not take into account any framework cost calculates
     * or anything else.  It is just the straight forward calculation.<P>
     * 
     * The getRealCost() method can be used to obtain the amount that is
     * actually paid by the character to have the ability.  This typically
     * only has meaning when talking about the base instances of an ability,
     * since this is the only one that the character pays for.  The RealCost
     * is affected by the framework that the ability belongs to, if any.<P>
     * 
     * The getAPCost() method returns the ActivePoint cost of the ability,
     * as currently configured.<P>
     * 
     * 
     * @return Active Point of the ability, as currently configured.
     */
    public int getRealCost() {
        if ( realCost == -1 ) {
            calculateCPCost();
        }
        return realCost;
    }
    
    /** Returns the Cost of the Power itself, as currently configured.
     *
     * This cost does not include any advantage, limitation, or framework
     * modifications.
     */
    public int getPowerCost() {
        if ( powerCost == -1 ) {
            calculateCPCost();
        }
        return powerCost;
    }
    
    /** Returns the Active DC cost of the Ability, as currently configured.
     *
     * The ActiveDC cost is the Active Point cost of the ability, using only
     * the advantages that affect the damage class of the ability.  Advantages
     * are marked as either affectDC or not to determine which advantages are
     * considered in this calculation.<P>
     *
     * The ActionDC cost is used in damage calculations when maneuvers are used
     * along with abilities.
     */
    public int getActiveDCCost() {
       
        double cost;
        Power p = getPower();
        if ( p == null ) {
            return 0;
        }
        
        int base = getPowerCost();
        
        cost = base * ( 1 + advCost );
        
        return (int)cost;
    }
    
    /**
     * @return  */
    public String getFileExtension() {
        return "abt";
    }
    
    /**
     * @return  */
    public boolean isDelayed() {
        if ( Battle.currentBattle == null || Battle.currentBattle.getTime().isTurnEnd() )  return false;
        
        return getDelayTime(null) > 0;
    }
    
    /**
     * @return  */
    public boolean isDelayExclusive() {
        if ( Battle.currentBattle == null || Battle.currentBattle.getTime().isTurnEnd() )  return false;
        
        int index = getIndexedSize("Delay") - 1;
        for(; index >= 0; index--) {
            if ( getIndexedBooleanValue(index, "Delay", "DELAYEXCLUSIVE") ) {
                return true;
                
            }
        }
        
        return false;
    }
    
    /**
     * 
     * 
     * @param sourceAlias
     * @return 
     */
    public long getDelayTime(Target source) {
        
        long seconds;
        long maxSeconds = 0;
        
        int index = getIndexedSize("Delay") - 1;
        for(; index >= 0; index--) {
            Integer delayTime = getIndexedIntegerValue(index, "Delay", "DELAYTIME");
            Integer delayCount = getIndexedIntegerValue(index, "Delay", "DELAYCOUNT");
            
            seconds = ChampionsUtilities.calculateSeconds(delayTime, delayCount, source);
            
            if ( maxSeconds < seconds ) {
                maxSeconds = seconds;
            }
        }
        
        return maxSeconds;
    }
    
    /**
     * 
     * 
     * @param currentTime
     * @param sourceAlias
     * @return 
     */
    public Chronometer getDelayTime(Chronometer currentTime, Target source) {
        Chronometer newTime = (Chronometer)currentTime.clone();
        
        long seconds;
        long maxSeconds = getDelayTime(source);
        
        if ( maxSeconds > 0 ) {
            newTime.setTime( newTime.getTime() + maxSeconds);
        }
        
        return newTime;
    }
    /** Indicates the the DelayTime for the Ability is counted in Phases.
     *
     * Indicates the the DelayTime for the Ability is counted in Phases.  This indicates that the
     * delayDex for the Delay Event should be set to SEQUENCE_BEFORE_TARGET, since phases end just prior
     * to a targets next turn.
     */
    public boolean isDelayAPhase() {
        int maxDelayTime = 0;
        
        int index = getIndexedSize("Delay") - 1;
        for(; index >= 0; index--) {
            Integer delayTime = getIndexedIntegerValue(index, "Delay", "DELAYTIME");
            
            if ( maxDelayTime < delayTime.intValue() ) maxDelayTime = delayTime.intValue();
        }
        
        return maxDelayTime == TIME_ONE_PHASE;
    }
    
    /**
     * @return  */
    public boolean isDelayTillLast() {
        if ( Battle.currentBattle == null || Battle.currentBattle.getTime().isTurnEnd() )  return false;
        
        int index = getIndexedSize("Delay") - 1;
        for(; index >= 0; index--) {
            if ( getIndexedBooleanValue(index, "Delay", "DELAYTILLLAST") ) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * @return  */
    public int getDelayInturruptLevel() {
        int maxInterrupt = 0;
        
        int index = getIndexedSize("Delay") - 1;
        for(; index >= 0; index--) {
            Integer i = getIndexedIntegerValue(index, "Delay", "INTERRUPTLEVEL");
            if ( maxInterrupt < i.intValue() ) maxInterrupt = i.intValue();
        }
        
        return maxInterrupt;
    }
    
    /**
     * @return  */
    public double getDelayDCVMultiplier() {
        double minMultiplier = 1;
        
        int index = getIndexedSize("Delay") - 1;
        for(; index >= 0; index--) {
            Double i = getIndexedDoubleValue(index, "Delay", "DCVMULTIPLIER");
            if ( minMultiplier > i.doubleValue() ) minMultiplier = i.doubleValue();
        }
        
        return minMultiplier;
    }
    
    /**
     * 
     * 
     * @param sourceAlias
     * @return 
     */
    public int getDelayDex(Target source) {
        if ( isDelayTillLast() ) {
            return SEQUENCE_END_OF_SEGMENT;
        }
        else if ( isDelayAPhase() ) {
            return SEQUENCE_BEFORE_TARGET;
        }
        else if ( ! isDelayTillLast() && source != null && source.hasStat("DEX") ) {
            return source.getCurrentStat("DEX");
        }
        return 0;
    }
    
    /**
     * @return  */
    public String getDelayReason() {
        StringBuffer sb = new StringBuffer();
        
        int index = getIndexedSize("Delay") - 1;
        for(; index >= 0; index--) {
            sb.append( getIndexedStringValue(index, "Delay", "REASON") );
            if ( index > 0 ) sb.append( ", ");
        }
        
        return sb.toString();
    }
    
    /**
     * @param delayEvent
     * @return  */
    public Effect getDelayEffect(BattleEvent delayEvent) {
        int interruptLevel = getDelayInturruptLevel();
        double dcvMultiplier = getDelayDCVMultiplier();
        
        int dcvPenalty= 0;
        if (dcvMultiplier == 1 ) dcvPenalty = getDCVModifier();
        
        String description = "Activating " + getName() + "(" + getDelayReason() + ")";
        
        return new effectInterruptible( description, this, delayEvent, interruptLevel, dcvMultiplier, dcvPenalty);
    }
    
    /**
     * @return  */
    public boolean isTriggered() {
        return getBooleanValue("Ability.ISTRIGGERED");
    }
    
    /**
     * @return  */
    public Chronometer getTriggerTime() {
        return null;
    }
    
    /**
     * @return  */
    public int getTriggerDex() {
        return 0;
    }
    
    /**
     * @return  */
    public Effect getTriggerEffect() {
        return null;
    }
    
    public boolean isCostsENDOnlyToActivate() {
        return costsEndOnlyToActivate;
    }
    
    public void setCostsENDOnlyToActivate(boolean costsOnlyToActivate) {
        this.costsEndOnlyToActivate = costsOnlyToActivate;
    }
    
    /**
     * @param ctype  */
    public void setCType( String ctype ) {
        //add("Ability.CTYPE",  ctype ,  true, false);
        this.ctype = ctype;
    }
    
    public String getCType() {
        //return getStringValue("Ability.CTYPE");
        return ctype;
    }
    
    /**
     * @return  */
    public boolean isPower() {
        String s;
        //return ( (s = getStringValue("Ability.CTYPE")) != null && s.equals("POWER") );
        return ctype != null && ctype.equals("POWER");
    }
    
    /**
     * @return  */
    public boolean isSkill() {
        String s;
        //return ( (s = getStringValue("Ability.CTYPE")) != null && s.equals("SKILL") ) || getPower() instanceof Skill;
        return getPower() instanceof Skill || (ctype != null && ctype.equals("SKILL"));
    }
    
    /**
     * @return  */
    public boolean isDisadvantage() {
        String s;
        //return ( (s = getStringValue("Ability.CTYPE")) != null && s.equals("DISADVANTAGE") ) || getPower() instanceof Disadvantage;
        return getPower() instanceof Disadvantage || (ctype != null && ctype.equals("DISADVANTAGE"));
    }
    /**
     * @return  */
    public boolean isPerk() {
        String s;
        //return ( (s = getStringValue("Ability.CTYPE")) != null && s.equals("DISADVANTAGE") ) || getPower() instanceof Disadvantage;
        return getPower() instanceof Perk || (ctype != null && ctype.equals("PERK"));
    }
    
    
    /**
     * @return  */
    public boolean isTalent() {
        String s;
        //return ( (s = getStringValue("Ability.CTYPE")) != null && s.equals("TALENT") );
        return (ctype != null && ctype.equals("TALENT"));
    }
    
    public boolean requiresMentalPanel() {
        return getBooleanValue("Ability.REQUIRESMENTALEFFECT");
    }
    
    public void setRequiresMentalPanel(boolean b) {
        add("Ability.REQUIRESMENTALEFFECT", b?"TRUE":"FALSE", true);
    }
    
    /**
     * @return  */
    public boolean isMovementPower() {
        return getBooleanValue("Ability.ISMOVEMENT");
    }
    
    public void setMovementPower(boolean movementPower) {
        add("Ability.ISMOVEMENT", movementPower ? "TRUE" : "FALSE", true);
    }
    
    /**
     * @return  */
    public boolean isEgoBased() {
        return getBooleanValue("Ability.ISEGOBASED");
    }
    
    public void setEgoBased(boolean egoBased) {
        add("Ability.ISEGOBASED", egoBased ? "TRUE" : "FALSE", true);
    }
    
    /**
     *This set of method allows setting certain abilities to not allow a forced activation roll
     *Currently dependence can force an activation on abilities as one of its options
     *
     **/
    public boolean isDisallowForcedActivation() {
        return getBooleanValue("Ability.DISALLOWFORCEDACTIVATION");
    }
    
    public void setDisallowForcedActivation(boolean allowForcedActivation) {
        add("Ability.DISALLOWFORCEDACTIVATION", allowForcedActivation ? "TRUE" : "FALSE", true);
    }
    
    
    
    
    /**
     * @return  */
    public boolean isManeuver() {
        return is("MELEEMANEUVER") || is("RANGEDMANEUVER");
    }
    
    /**
     * @return  */
    public Power getPower() {
        // (Power)getValue("Power.POWER");
        return power;
    }
    
    /**
     * @param what
     * @return  */
    public boolean is( String what ) {
        return getBooleanValue("Ability.IS" + what );
    }
    
    /**
     * @param what
     * @return  */
    public boolean can( String what ) {
        return getBooleanValue("Ability.CAN" + what );
    }
    
    /**
     * @param what
     * @param replace  */
    public void setIs(String what, boolean replace ) {
        add("Ability.IS" + what,"TRUE",  replace, false);
    }
    
    /**
     * @param what
     * @param replace  */
    public void setCan( String what, boolean replace ) {
        add("Ability.CAN" + what,"TRUE",  replace, false);
    }
    
    /**
     * @param what
     * @param replace  */
    public void setIsnot(String what, boolean replace) {
        add("Ability.IS" + what,"FALSE",  replace, false);
    }
    
    /**
     * @param what
     * @param replace  */
    public void setCannot( String what, boolean replace) {
        add("Ability.CAN" + what,"FALSE",  replace, false);
    }
    
    /** Creates an Activate BattleEvent for the indicated Ability. 
     *
     * This method is run when the user clicks on the ability to activate the ability.
     * It should create the default action ability.  For most powers, this 
     * will be an activate battle event.
     *
     * If the user wishes to use a maneuver with this ability, the maneuver's 
     * getActivateAbilityBattleEvent will first be called.  If it returns null, this 
     * method will be called.  If it returns non-null, this method will be skipped
     * and it will be assumed that the manuever is taking care of the 
     * activation.  
     *
     * Generally maneuvers will allow the power to build the activation event,
     * but either may be required to perform the action.
     *
     * This method will not be called if the ability is already activated.
     */
    public BattleEvent getActivateAbilityBattleEvent(Ability ability, Ability maneuver, Target source) {
        if ( getPower() != null ) return getPower().getActivateAbilityBattleEvent(ability, maneuver, source);
        else return null;
    }
    
    /** Creates an Activate BattleEvent for the indicated Maneuver. 
     *
     * This method is run when the user clicks on the ability to activate the ability
     * with a maneuver.  It should create the default action maneuver.  
     * For most powers, this will be an activate battle event.
     *
     * If the user wishes to use a maneuver with this ability, the maneuver's 
     * getActivateManeuverBattleEvent will first be called.  If it returns null, this 
     * method will be called.  If it returns non-null, this method will be skipped
     * and it will be assumed that the manuever is taking care of the 
     * activation.  
     *
     * Generally maneuvers will allow the power to build the activation event,
     * but either may be required to perform the action.
     *
     * This method will not be called if the ability is already activated.
     */
    public BattleEvent getActivateManeuverBattleEvent(Ability ability, Ability maneuver, Target source) {
        if ( getPower() != null ) return getPower().getActivateManeuverBattleEvent(ability, maneuver, source);
        else return null;
    }
    
    
    
 
    
    /** Forces a reconfiguration of the ability.
     *
     * Reconfigure() will force a complete reconfiguration of the ability.  It does not rebuild the ability,
     * rather, causes configurePAD to be called for the power and all advantages/limitations.
     *
     * This should not be called from within a configurePAD of the power or an advantage/limitation, as it will
     * cause an infinite recursive call.
     */
    public void reconfigure() {
        ParameterList pl;
        reconfigurePower();
        
        int index,count;
        count = getAdvantageCount() ;
        for ( index=0;index<count;index++ ) {
            Advantage a = (Advantage)getAdvantage(index);
            pl = a.getParameterList(this, index);
            reconfigureAdvantage(a,pl,index);
        }
        
        count = getLimitationCount() ;
        for ( index=0;index<count;index++ ) {
            Limitation l = getLimitation(index);
            pl = l.getParameterList(this, index);
            reconfigureLimitation(l,pl, index);
        }
        
        count = getIndexedSize( "SpecialParameter" ) ;
        for ( index=0;index<count;index++ ) {
            SpecialParameter l = (SpecialParameter)getIndexedValue( index, "SpecialParameter", "SPECIALPARAMETER" );
            pl = l.getParameterList(this, index);
            reconfigureSpecialParameter(l,pl,index);
        }
        
        calculateMultiplier();
        calculateCPCost();
        // calculateENDCost();
    }
    
    /** Forces a reconfiguration of the abilities power.
     *
     * This method will force the power of the ability to be reconfigured.  This 
     * method can be used from within an advantage/limitation configurePAD call, 
     * if it knows that the power will need to be reconfigured due to changes it 
     * has made.<P>
     *
     * This method should never be called from within the power's configurePAD 
      *code since it will lead to and infinite recursive loop.<P>
     *
     */
    public void reconfigurePower() {
        reconfigurePower(null, null);
    }
    
    /** Forces a reconfiguration of the abilities power.
     *
     * This method will force the power of the ability to be reconfigured.  This 
     * method can be used from within an advantage/limitation configurePAD call, 
     * if it knows that the power will need to be reconfigured due to changes it 
     * has made.<P>
     *
     * This method should never be called from within the power's configurePAD 
     * code since it will lead to and infinite recursive loop.<P>
     *
     * 
     */
    public void reconfigurePower(Power p, ParameterList parameterList) {
        if ( p == null ) p = getPower();
        if ( p != null ) {
            if ( parameterList == null ) parameterList = p.getParameterList(this, -1);

            //p.reconfigurePower(this);
            p.configurePAD(this, parameterList);
            calculateCPCost();
            //calculateMultiplier();

            int count = getChildAbilityCount();
            for(int index = 0; index < count; index++) {
                Ability child = getChildAbility(index);
                child.reconfigurePower();
                child.calculateCPCost();
            }
        }
    }
    
    /** Forces a reconfiguration of the specified advantage.
     *
     * parameterList and index can be specified.  However, if they are not, 
     * they will be looked up appropriately.
     * 
     * @todo Fix to allow for indexes to change on child PAD lists.
     */
    public void reconfigureAdvantage(Advantage adv, ParameterList parameterList, int index) {
        if ( index == -1 ) {
            index = findExactAdvantage(adv);
        }
        // If this advantage doesn't really exist here, just bail...
        if ( index == -1 ) return; 
        if ( parameterList == null ) parameterList = adv.getParameterList(this, index);
        adv.configurePAD(this, parameterList);
        
        int count = getChildAbilityCount();
        for(int cindex = 0; cindex < count; cindex++) {
            Ability child = getChildAbility(cindex);
            
            // The advantage could be (and probably is) different from the parent,
            // so look through the available advantages and only reconfigure
            // the one that has the correct parameterList parent.  Try the 
            // index from above first, since most of the time they will be the
            // same...
            Advantage adv2;
            if ( index < child.getAdvantageCount() ) {
                adv2 = child.getAdvantage(index);
                ParameterList pl2 = adv2.getParameterList(child, index);
                if ( pl2.getParent() == parameterList ) {
                    child.reconfigureAdvantage(adv2, pl2, index);
                    child.calculateMultiplier();
                }
            }
        }
    }
    
    /** Forces a reconfiguration of the specified limitation.
     *
     * parameterList and index can be specified.  However, if they are not, 
     * they will be looked up appropriately.
     * 
     * @todo Fix to allow for indexes to change on child PAD lists.
     */
    public void reconfigureLimitation(Limitation lim, ParameterList parameterList, int index) {
        if ( index == -1 ) {
            //index = findExactIndexed("Limitation", "LIMITATION", lim);
            index = findLimitation(lim);
        }
        // If this limitation doesn't really exist here, just bail...
        if ( index == -1 ) return; 
        if ( parameterList == null ) parameterList = lim.getParameterList(this, index);
        // Look up the actual limitation, so that we know it belongs to this ability.
        lim = getLimitation(index);
        lim.configurePAD(this, parameterList);
        calculateMultiplier();

        int count = getChildAbilityCount();

        for(int cindex = 0; cindex < count; cindex++) {
            Ability child = getChildAbility(cindex);
            
            // The limitation could be (and probably is) different from the parent,
            // so look through the available limitations and only reconfigure
            // the one that has the correct parameterList parent.  Try the 
            // index from above first, since most of the time they will be the
            // same...
            Limitation adv2;
            if ( index < child.getLimitationCount() ) {
                adv2 = child.getLimitation(index);
                ParameterList pl2 = adv2.getParameterList(child, index);
                if ( pl2.getParent() == parameterList ) {
                    child.reconfigureLimitation(adv2, pl2, index);
                    
                }
            }
        }
    }
    
    public void reconfigureSpecialParameter(SpecialParameter sp, ParameterList parameterList, int index) {
        if ( index == -1 ) {
            index = findExactIndexed("SpecialParameter", "SPECIALPARAMETER", sp);
        }
        // If this special parameter doesn't really exist here, just bail...
        if ( index == -1 ) return; 
        if ( parameterList == null ) parameterList = sp.getParameterList(this, index);
        sp.configure(this, parameterList);
        
        int count = getChildAbilityCount();
        for(int cindex = 0; cindex < count; cindex++) {
            Ability child = getChildAbility(cindex);
            
            // The special parameter could be (and probably is) different from the parent,
            // so look through the available special parameters and only reconfigure
            // the one that has the correct parameterList parent.  Try the 
            // index from above first, since most of the time they will be the
            // same...
            SpecialParameter sp2;
            if ( index < child.getSpecialParameterCount() ) {
                sp2 = child.getSpecialParameter(index);
                ParameterList pl2 = sp2.getParameterList(child, index);
                if ( pl2.getParent() == parameterList ) {
                    child.reconfigureSpecialParameter(sp2, pl2, index);
                    child.calculateMultiplier();
                }
            }
        }    
    }
    
    public void reconfigure(PAD pad) {
        reconfigure(pad, null, -1);
    }
    
    public void reconfigure(PAD pad, ParameterList pl) {
        reconfigure(pad, pl, -1);
    }
    
    public void reconfigure(PAD pad, ParameterList pl, int index) {
        if ( pad instanceof Power ) {
            reconfigurePower((Power)pad,pl);
        }
        else if ( pad instanceof Advantage ) {
            reconfigureAdvantage((Advantage)pad, pl, index);
        }
        else if ( pad instanceof Limitation ) {
            reconfigureLimitation((Limitation)pad, pl, index);
        }
    }
    
    /** Childs a list of all the Actions an ability is current able to take.
     *
     * If vector v is null, a new vector will be created.
     */
     public Vector getActions(Vector v) {
        if ( v == null ) v = new Vector();
         
        if ( getPower() != null ) getPower().addActions(v, this);
        
        for(int index = 0; index < getAdvantageCount(); index++ ) {
            Advantage adv = getAdvantage(index);
            adv.addActions(v, this, index);
        }
        
        for(int index = 0; index < getLimitationCount(); index++ ) {
            Limitation lim = getLimitation(index);
            lim.addActions(v, this, index);
        }
        
        return v;
    } 
    
    /**
     * @return  */
    public String getDefense() {
        String def = getStringValue("Power.DEFENSE");
        if ( def != null ) return def;
        return "PD";
    }
    
    /** Returns the Value need to succeed at a skill roll.
     *
     * This is only valid if the power is a skill.
     *
     * The target must be specified for skill rolls that are based upon
     * stats of a target.
     * @param target Target to base skill roll upon.
     * @return Skill Roll needed.  MIN_INT if invalid.
     */
    public int getSkillRoll(Target target) {
        //        original code:
        //        if ( isSkill() == false ) return Integer.MIN_VALUE;
        //
        //        Skill s = (Skill)getPower();
        //        System.out.println(s);
        //        return s.getSkillRoll(this, target);
        
        if (isDisadvantage() == true) {
            Disadvantage obj = (Disadvantage)getPower();
            return obj.getSkillRoll(this, target);
        }
        else if (isPerk() == true) {
            Perk obj = (Perk)getPower();
            return obj.getSkillRoll(this, target);
        }        
        else if (isSkill() == true) {
            Skill obj = (Skill)getPower();
            if ( getBooleanValue("Power.CRAMMED") ) {
                return 8;
            }
            else {
                return obj.getSkillRoll(this, target);
            }
            
        }
        return Integer.MIN_VALUE;
    }
    
    /** Returns the Value need to succeed at a skill roll.
     *
     * This is only valid if the power is a skill.
     *
     * The target must be specified for skill rolls that are based upon
     * stats of a target.
     * @param target Target to base skill roll upon.
     * @return Skill Roll needed.  MIN_INT if invalid.
     */
    public int getSkillRoll() {
        Target t = getSource();
        return ( t == null ) ? Integer.MIN_VALUE : getSkillRoll(t);
    }
    
    /** Called Prior to writing of the detailList.
     *
     * An cleanup that needs to be done prior to writing, such as removing
     * non-serializable objects from the detaillist, should be done here.
     *
     * For abilities, this calls Ability.prepareToSave() just prior to
     * writing.  Put customizations into prepareToSave, not prewrite.
     */
    protected void preWrite() {
        // Don't remove the sourceAlias of the ability here, since this is often
        // used to save the target as a whole (with abilities).  In this case,
        // you don't want to remove the sourceAlias of the abilities.
        
        // Call prepareToSave to allow the ability to do custom saving.
        prepareToSave();
        
        if ( parent != null ) parent.prepareToSave();
    }
    
    /** Saves the Ability to the current File.
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void save() throws FileNotFoundException, IOException {
        // Before saving via the save method, remove the sourceAlias of the
        // ability.
       // this.remove( "Ability.SOURCE" );
        
        super.save();
    }
    
    /** Called Prior to writing of the Ability.
     *
     * An cleanup that needs to be done prior to writing, such as removing
     * non-serializable objects from the Ability, should be done here.
     *
     * For abilities, this is called by Ability.preWrite() just prior to
     * writing.  Put customizations into prepareToSave, not prewrite.
     */
    public void prepareToSave() {
        if ( getPower() != null ) getPower().prepareToSave(this,-1);
        int index,count;
        
        count = getAdvantageCount();
        for(index=0;index<count;index++) {
            Advantage a = (Advantage)getAdvantage(index);
            a.prepareToSave(this,index);
        }
        
        count = getLimitationCount();
        for(index=0;index<count;index++) {
            Limitation a = getLimitation(index);
            a.prepareToSave(this,index);
        }
    }
    
    /**
     * @param required  */
    public void setAutoHit(boolean required) {
        add("Ability.ISAUTOHIT", required ? "TRUE" : "FALSE", true , false);
    }
    
    /**
     * @return  */
    public boolean isAutoHit() {
        return getBooleanValue("Ability.ISAUTOHIT" );
    }
    
    /**
     * @param desc
     * @param value
     * @return  */
    public int addKnockbackModifier( String desc, int value ) {
        int index = findIndexed("KnockbackRoll", "DESCRIPTION", desc);
        if ( index == -1 ) {
            index = createIndexed(   "KnockbackRoll", "TYPE", "MODIFIER", false ) ;
            addIndexed(index,  "KnockbackRoll", "DESCRIPTION", desc, true,false);
        }
        addIndexed(index,  "KnockbackRoll", "VALUE", new Integer(value), true,false);
        addIndexed(index,  "KnockbackRoll", "ACTIVE", "TRUE", true,false);
        return index;
    }
    
    /**
     * @param desc  */
    public void removeKnockbackModifier( String desc ) {
        int index = findIndexed("KnockbackRoll", "DESCRIPTION", desc);
        if ( index != -1 ) {
            removeAllIndexed(index, "KnockbackRoll");
        }
    }
    
    /**
     * @param b  */
    public void setGenerateDefaultEffects(boolean b) {
        //add("Ability.GENERATEDEFAULTEFFECTS", (b ? "TRUE" :"FALSE"),true,false);
        generateDefaultEffects = b;
        if ( b == true ) setUsesHitLocation(b);
    }
    
    /**
     * @return  */
    public boolean getGenerateDefaultEffects() {
        //return getBooleanValue("Ability.GENERATEDEFAULTEFFECTS");
        return generateDefaultEffects;
    }
    
//    /**
//     * 
//     * 
//     * @param sourceAlias
//     * @return 
//     */
//    public ActivationInfo findActivationInfo(Target source) {
//        int count, index;
//        ActivationInfo ai;
//        
//        if ( ! isModifiableInstance() && ! isCurrentInstance() ) return getInstanceGroup().getCurrentInstance().findActivationInfo(source);
//        
//        count = this.getIndexedSize("ActivationInfo");
//        index = -1;
//        while ( (index = this.findIndexed(index+1, "ActivationInfo", "SOURCE", source)) != -1 ) {
//            ai = (ActivationInfo)this.getIndexedValue( index, "ActivationInfo", "ACTIVATIONINFO");
//            if ( ai.isActivated() || ai.isDelayed() ) return ai;
//        }
//        return null;
//    } 
    
//    /**
//     * 
//     * 
//     * @param startIndex Index to start search, -1 for beginning of AI records.
//     * @param sourceAlias
//     * @return Index of next activated AI.  -1 if there are none.
//     */
//     public int findActivationInfo(Target source, int startIndex) {
//        int count, index;
//        ActivationInfo ai;
//        
//        if ( ! isModifiableInstance() && ! isCurrentInstance() ) return getInstanceGroup().getCurrentInstance().findActivationInfo(source, startIndex);
//        
//        count = this.getIndexedSize("ActivationInfo");
//        index = startIndex;
//        while ( (index = this.findIndexed(index, "ActivationInfo", "SOURCE", source)) != -1 ) {
//            ai = (ActivationInfo)this.getIndexedValue( index, "ActivationInfo", "ACTIVATIONINFO");
//            if ( ai.isActivated() ) return index;
//            index++;
//        }
//        return -1;
//    } 
    
    /** Return the number of Activations currently associated with this ability.
     *
     */
    public int getActivationCount() {
        return getInstanceGroup().getActivationCount();
    }
    
    public ActivationInfo getActivationInfo(int index) {
        //return (ActivationInfo)this.getIndexedValue( index, "ActivationInfo", "ACTIVATIONINFO");
        return getInstanceGroup().getActivationInfo(index);
    }
    
    public Target getActivationSource(int index) {
        return getInstanceGroup().getActivationSource(index);
    }
    
    /** Iterates through all the activations.
     *
     *  Note:  This will not allow you to check the source of the activation.
     */
    public Iterator<ActivationInfo> getActivations() {
        return getInstanceGroup().getActivations();
    }
    
    /** Iterates through all of the activations by the given source.
     *
     *  If source is null, all activations will be returned.
     */
    public Iterator<ActivationInfo> getActivations(Target source) {
        return getInstanceGroup().getActivations(source);
    }
    
    /** Adds an Activation entry.
     *
     */
    protected void addActivation(ActivationInfo activationInfo, Target source) {
        getInstanceGroup().addActivation(activationInfo, source);
    }
    
    /** Removes an Activation entry.
     *
     */
    protected void removeActivation(ActivationInfo activationInfo) {
        getInstanceGroup().removeActivation(activationInfo);
    }
    
    protected boolean hasActivation(ActivationInfo activationInfo) {
        return getInstanceGroup().hasActivation(activationInfo);
    }
    
    /**
     * @return  */
    public boolean isNormallyOn() {
        if ( getParentAbility() != null ) {
            return getParentAbility().isNormallyOn();
        }
        else {
            return (normallyOn == null ? false : normallyOn);
        }
    }
    
    /**
     * @param value  */
    public void setNormallyOn(boolean value) {
        if ( getParentAbility() != null ) {
            getParentAbility().setNormallyOn(value);
        }
        else {
        
            if ( normallyOn == null || normallyOn.equals(value) == false ) {
                normallyOn = value;

                SpecialParameter sp = null;
                sp = findSpecialParameter("Automatically Activate Ability");
                if ( value == true && sp == null ) {
                    sp = new SpecialParameterNormallyOn();
                    addSpecialParameter( sp );
                }

                if ( sp != null ) {
                    int index = findIndexed("SpecialParameter", "SPECIALPARAMETER", sp);
                    ParameterList pl = sp.getParameterList(this, index);
                    pl.setParameterValue("NormallyOn", value?"TRUE":"FALSE");
                    sp.configure(this, pl);
                }



            }
        }
    }
    
    public void clearNormallyOn() {
        if ( getParentAbility() != null ) {
            getParentAbility().clearNormallyOn();
        }
        else {
            normallyOn = null;
        }
    }
    
    public void setChargeContinuingEND(boolean value) {
        add("Ability.CHARGECONTINUINGEND", value?"TRUE":"FALSE", true, false);
    }
    /** Returns value of Charge Continuing END.
     *
     * Return True if the value pair isn't set.
     */
    public boolean getChargeContinuingEND() {
        String value = getStringValue("Ability.CHARGECONTINUINGEND");
        return value == null ? true : value.equals("TRUE");
    }
    
    public void resetChargeContinuingEND() {
        remove("Ability.CHARGECONTINUINGEND");
    }
    
    /**
     * @param ocvModifier  */
    public void setOCVModifier(int ocvModifier) {
        if ( this.ocvModifier != ocvModifier ) {
            this.ocvModifier = ocvModifier;

            SpecialParameter sp = null;
            sp = findSpecialParameter("OCV Modifier");
            if ( ocvModifier != 0 && sp == null ) {
                addSpecialParameter( new SpecialParameterOCVModifier() );
                sp = findSpecialParameter("OCV Modifier");
            }

            if ( sp != null ) {
                int index = findIndexed("SpecialParameter", "SPECIALPARAMETER", sp);
                ParameterList pl = sp.getParameterList(this, index);
                pl.setParameterValue("OCVModifier", new Integer(ocvModifier) );
                sp.configure(this, pl);
            }
        }
    }
    
    /**
     * @return  */
    public int getOCVModifier() {
        //Integer i = getIntegerValue("Ability.OCVBONUS");
        //return (i==null)?0:i.intValue();
        return ocvModifier;
    }
    
    /**
     * @param dcvModifier  */
    public void setDCVModifier(int dcvModifier) {
        if ( this.dcvModifier != dcvModifier ) {
            this.dcvModifier = dcvModifier;
            
            SpecialParameter sp = null;
            sp = findSpecialParameter("DCV Modifier");
            if ( dcvModifier != 0 && sp == null ) {
                addSpecialParameter( new SpecialParameterDCVModifier() );
                sp = findSpecialParameter("DCV Modifier");
            }

            if ( sp != null ) {
                int index = findIndexed("SpecialParameter", "SPECIALPARAMETER", sp);
                ParameterList pl = sp.getParameterList(this, index);
                pl.setParameterValue("DCVModifier", new Integer(dcvModifier) );
                sp.configure(this, pl);
            }
        }
    }
    
    /**
     * @return  */
    public int getDCVModifier() {
        //Integer i = getIntegerValue("Ability.DCVBONUS");
        //return (i==null)?0:i.intValue();
        return dcvModifier;
    }
    
    /** Compares two abilities to determine if they really reference the same ability.
     *
     * Currently, the abilities are not checked deeply.  Only the name and power class
     * are check.  No parameters of the ability are actually check.
     * Thus, if two abilities are named the same and build from the same powers, but
     * are configured differently, they will still be determined to be equal.
     *
     *
     * @param o Object to compare with.
     * @return Result of equals comparison.
     */
    public boolean equals(Object o) {
        if ( this == o ) return true;
        
        if ( ! (o instanceof Ability) ) return false;
        
        Ability a = (Ability)o;
        
        if ( this.getName().equals(a.getName()) == false ) return false;
        
        if ( this.getPower() == null || a.getPower() == null) return false;
        if ( this.getPower().getClass() != a.getPower().getClass() ) return false;
        
        return true;
    }
    
    /**
     * Return true if abilities are configure in the same way. 
     * 
     * This method performs an deep check of the configuration of the ability
     * with another ability to determine if they are configured exactly the
     * same.  Currently, the order of Advantages, Limitations, Special Parameters
     * must be the same, or the configuration are considered different.
     * 
     * This method does not compare the Name of the ability, but it does
     * compare the sourceAlias of the ability and the autoSource parameter.
     * 
     * 
     * 
     * @param ability Ability to compare to.
     * @return True if they have the same configuration, false otherwise.
     */
    public boolean compareConfiguration(Ability that) {
        if ( that == null ) return false;
        
        if ( this == that ) return true;
        
        // Check sources...
        if ( isAutoSource() != that.isAutoSource() ) return false;
        if ( getSource() != that.getSource() ) return false;
        
        // Compare powers
        Power power = this.getPower();
        if ( power == null && that.getPower() != null ) return false;
        if ( power != null ) {
            // Compare power configuration
            if ( power.compareConfiguration(this, that) == false) return false; 
        }
        
        // Compare Advantages...assume in the same order
        int thisCount = getAdvantageCount();
        int thatCount = that.getAdvantageCount();
        
        if ( thisCount != thatCount ) return false;
        
        for(int index = 0; index < thisCount; index++) {
            //if ( this.getAdvantage(index).equals(that.getAdvantage(index)) == false) return false;
            if ( this.getAdvantage(index).compareConfiguration(this, index, that, index) == false ) return false;
        }
        
        // Compare Limitations...assume in the same order
        thisCount = getLimitationCount();
        thatCount = that.getLimitationCount();
        
        if ( thisCount != thatCount ) return false;
        
        for(int index = 0; index < thisCount; index++) {
            //if ( this.getLimitation(index).equals(that.getLimitation(index)) == false) return false;
            if ( this.getLimitation(index).compareConfiguration(this, index, that, index) == false ) return false;
        }
    
        // Compare SpecialParameters...assume in the same order
        thisCount = getSpecialParameterCount();
        thatCount = that.getSpecialParameterCount();

        if ( thisCount != thatCount ) return false;

        for(int index = 0; index < thisCount; index++) {
            //if ( this.getSpecialParameter(index).equals(that.getSpecialParameter(index)) == false) return false;
            if ( this.getSpecialParameter(index).compareConfiguration(this, index, that, index) == false ) return false;
        }
        
        // Compare Special Effect...order independent...
        thisCount = getSpecialEffectCount();
        thatCount = that.getSpecialEffectCount();

        if ( thisCount != thatCount ) return false;

        for(int index = 0; index < thisCount; index++) {
            if ( that.hasSpecialEffect(this.getSpecialEffect(index).getName()) == false ) return false;
        }
        for(int index = 0; index < thatCount; index++) {
            if ( this.hasSpecialEffect(that.getSpecialEffect(index).getName()) == false ) return false;
        }
        
        return true;
    }
    
    public boolean isConfigurationValid() {
        
        ParameterList pl;
        // Compare powers
        Power power = this.getPower();
        if ( power != null ) {
           pl = power.getParameterList(this);
           if ( pl.isValid() == false ) return false;
        }
        
        int thisCount = getAdvantageCount();
        
        for(int index = 0; index < thisCount; index++) {
            Advantage a = getAdvantage(index);
            pl = a.getParameterList(this, index);
            if ( pl.isValid() == false ) return false;
        }
        
        // Compare Limitations...assume in the same order
        thisCount = getLimitationCount();
        
        for(int index = 0; index < thisCount; index++) {
            Limitation a = getLimitation(index);
            pl = a.getParameterList(this, index);
            if ( pl.isValid() == false ) return false;
        }
        
        return true;
    }
    
    /**
     * @param sourceName  */
    public void setPrimaryENDSource(String sourceName) {
        //add("Primary.ENDSOURCE",  sourceName, true, false);
        this.primaryENDSource = sourceName;
    }
    
    /**
     * @param sourceName  */
    public void setSecondaryENDSource(String sourceName) {
        //add("Secondary.ENDSOURCE",  sourceName, true, false);
        this.secondaryENDSource = sourceName;
    }
    
    /**
     * @param sourceName  */
    public void setStrengthENDSource(String sourceName) {
        //add("Strength.ENDSOURCE",  sourceName, true, false);
        this.strengthENDSource = sourceName;
    }
    
    /**
     * @return  */
    public String getPrimaryENDSource() {
        //return getStringValue("Primary.ENDSOURCE");
        return this.primaryENDSource;
    }
    
    /**
     * @return  */
    public String getSecondaryENDSource() {
        //return getStringValue("Secondary.ENDSOURCE");
        return this.secondaryENDSource;
    }
    
    /**
     * @return  */
    public String getStrengthENDSource() {
        //return getStringValue("Strength.ENDSOURCE");
        return this.strengthENDSource;
    }
    
    /**
     * @return  */
    public double getENDMultiplier() {
//        Double d = getDoubleValue("Ability.ENDMULTIPLIER");
//        if ( d == null ) return 1;
//        return d.doubleValue();
        return endMultiplier;
    }
    
    /**
     * @return  */
    public int getRange() {
        int range = 0;
        if ( isMovementPower() ) {
            Power power = getPower();
            range = power.getMovementDistance(this);
        }
        else {
            
        }
        
        return range;
    }
    
    public int getSTRMinimum() {
        //Integer i = getIntegerValue("Minimum.STR");
        return ( minimumStrength == null ) ? -10000 : minimumStrength;
    }
    
    public void setSTRMinimum(int minimum) {
        this.minimumStrength = minimum;
    }
    
    public void removeSTRMinimum() {
        this.minimumStrength = null;
    }
    
    public boolean isSTRMiniumSet() {
        return this.minimumStrength != null;
    }
    
    /** Sets the ENDMultiplier for the abilities Power.
     *
     * The ENDMultiplier is used to determine the END cost of the ability.
     * This method sets the ENDMultiplier for the Power of the Ability.
     * The ENDMultiplier set by the power should be either 1 or 0 and
     * is only relavent when no advantage/limitation affecting END cost
     * are applied to the power.
     *
     * To adjust the ENDMultiplier from an advantage/limitation, use the
     * extended setENDMultiplier(double, PAD) version of the method.
     */
    public void setENDMultiplier(double d) {
        
        add("Power.ENDMULTIPLIER", new Double(d), true, false);
        
        double oldEndMultiplier = getENDMultiplier();
        calculateENDMultiplier();
        
        // Remove the END Cost to update the total ability END cost
        if ( oldEndMultiplier != getENDMultiplier() ) {
            endCost = -1;
        }
    }
    
    /** Sets the ENDMultiplier for a particular advantage or limitation.
     *
     * The ENDMultiplier is used to determine the END cost of the ability.
     *
     * ENDMultiplier's set by this method override the power set
     * ENDMultiplier.  However, multiple ENDMultipliers set by seperate
     * limitations and advantages will by multiplied to determine the
     * final end multiplier.
     *
     * To adjust the ENDMultiplier from the Power, use the
     * extended setENDMultiplier(double) version of the method.
     */
    public void setENDMultiplier(double d, PAD pad) {
        int index;
        if ( ( index = findIndexed("ENDMultiplier", "SOURCE", pad) ) == -1 ) {
            index = createIndexed( "ENDMultiplier", "SOURCE", pad);
        }
        addIndexed(index,"ENDMultiplier","MULTIPLIER", new Double(d),true);
        
        double oldEndMultiplier = getENDMultiplier();
        calculateENDMultiplier();
        
        // Remove the END Cost to update the total ability END cost
        if ( oldEndMultiplier != getENDMultiplier() ) {
            endCost = -1;
            calculateENDCost();
        }
    }
    
    /**
     * @param pad  */
    public void removeENDMultiplier(PAD pad) {
        int index;
        if ( ( index = findIndexed("ENDMultiplier", "SOURCE", pad) ) != -1 ) {
            removeAllIndexed(index, "ENDMultiplier");
            
            double oldEndMultiplier = getENDMultiplier();
            calculateENDMultiplier();
            
            // Remove the END Cost to update the total ability END cost
            if ( oldEndMultiplier != getENDMultiplier() ) {
                // If the cached END multiplier changed, remove the ENDcost to force a recalculation on
                // the new getEND call.
                endCost = -1;
            }
        }
    }
    
    /**
     */
    public void calculateENDMultiplier() {
        int index, count;
        double multiplier = 1;
        Double d;
        
        count = getIndexedSize("ENDMultiplier");
        if ( count == 0 ) {
            d = getDoubleValue("Power.ENDMULTIPLIER");
            multiplier = ( d == null ) ? 1 : d.doubleValue();
        }
        else {
            for ( index = 0; index < count; index++) {
                d = getIndexedDoubleValue(index,"ENDMultiplier","MULTIPLIER");
                if ( d != null ) multiplier *= d.doubleValue();
            }
        }
        
        //add("Ability.ENDMULTIPLIER", new Double(multiplier), true, false);
        endMultiplier = multiplier;
    }
    
    /** Attempt to match AbilityImport against previously matched import.
     * This method is called to determine if a AbilityImport has match this power
     * during a previous import.  If matchImport returns true, the CharacterImport
     * assumes that Ability is an previous match and does not look for
     * further matches.
     * @return whether there is a match between a previous import and
     * this the AbilityImport
     * @param ai AbilityImport trying to be imported
     * @param ability previously created Ability against which to compare for
     * import match.
     */
    
    public boolean matchImport(AbilityImport ai) {
        Ability aiAbility = ai.getAbility();
        return aiAbility != null && this.getName().equals( ai.getName() ) && this.getPower() != null &&
        this.getPower().equals( aiAbility.getPower() );
    }
    
    /**
     * @return  */
    public String dumpDetailList() {
        return super.toString();
    }
    
    /** Indicates the Power is Linked.
     *
     * The is Linked indicates the power is linked to other
     * powers and that this power can only be triggered if the other
     * power is already running or if the are triggered together.
     *
     * Note, isLinked should only be true on the power that is forces other
     * powers to trigger, ie this power can not be fired without causing
     * the others to fire.
     *
     * Powers marked with isLinked should contain a list of power names
     * the are linked to, under the Linked#.NAME index, where ABILITY
     * is the Name of the Power.
     */
    public boolean isLinked() {
        return getBooleanValue("Ability.ISLINKED");
    }
    
    /** Indicates the Power is Linked.
     *
     * The is Linked indicates the power is linked to other
     * powers and that those other powers will be triggered whenever
     * this power is executed.
     *
     * Note, isLinked should only be true on the power that is forces other
     * powers to trigger, ie this power can not be fired without causing
     * the others to fire.
     *
     * Powers marked with isLinked should contain a list of power names
     * the are linked to, under the Linked#.NAME index, where ABILITY
     * is the Name of the Power.
     */
    public void setLinked(boolean b) {
        add("Ability.ISLINKED", b?"TRUE":"FALSE", true);
    }
    
    /** Adds a Linked Power to this Ability.
     *
     * Linked Power are ones that must be triggered when this
     * power is triggered.
     *
     * The list of power which must be triggered when this one
     * is triggered is kept in the Linked# index.  Linked#.NAME
     * should contain the names of the powers this power is linked.
     *
     * Optionally, a preference can be set as to the order of execution
     * for the various linked powers.  This is stored under the
     * Linked#.PRIORITY value pair.  Lower is better.
     *
     * Also, this Ability can have a priority, stored at Linked.PRIORITY.
     * This prority only applies when comparing this ability to other abilities
     * in it's linked list.
     */
    public int addLinkedAbility(Ability ability) {
        return addLinkedAbility(ability, 0 );
    }
    
    /** Adds a Linked Power to this Ability.
     *
     * Linked Power are ones that must be triggered when this
     * power is triggered.
     *
     * The list of power which must be triggered when this one
     * is triggered is kept in the Linked# index.  Linked#.NAME
     * should contain the names of the powers this power is linked.
     *
     * Optionally, a preference can be set as to the order of execution
     * for the various linked powers.  This is stored under the
     * Linked#.PRIORITY value pair.  Lower is better.
     *
     * Also, this Ability can have a priority, stored at Linked.PRIORITY.
     * This prority only applies when comparing this ability to other abilities
     * in it's linked list.
     */
    public int addLinkedAbility(Ability ability, int priority) {
        return addLinkedAbility(ability.getName(),priority );
    }
    
    /** Adds a Linked Power to this Ability.
     *
     * Linked Power are ones that must be triggered when this
     * power is triggered.
     *
     * The list of power which must be triggered when this one
     * is triggered is kept in the Linked# index.  Linked#.NAME
     * should contain the names of the powers this power is linked.
     *
     * Optionally, a preference can be set as to the order of execution
     * for the various linked powers.  This is stored under the
     * Linked#.PRIORITY value pair.  Lower is better.
     *
     * Also, this Ability can have a priority, stored at Linked.PRIORITY.
     * This prority only applies when comparing this ability to other abilities
     * in it's linked list.
     */
    public int addLinkedAbility(String abilityName, int priority) {
        int i = findIndexed("Linked",  "NAME", abilityName);
        if ( i == -1 ) {
            i = createIndexed("Linked", "NAME", abilityName);
            addIndexed(i, "Linked","PRIORITY", new Integer(priority), true);
        }
        
        return i;
    }
    
    public void removeLinkedAbility(Ability ability) {
        int i = findIndexed("Linked",  "NAME", ability.getName());
        if ( i != -1 ) {
            removeAllIndexed(i, "Linked");
        }
    }
    
    public void removeLinkedAbility(String abilityName) {
        int i = findIndexed("Linked",  "NAME", abilityName);
        if ( i != -1 ) {
            removeAllIndexed(i, "Linked");
        }
    }
    
    public void removeLinkedAbility(int index) {
        
        removeAllIndexed(index, "Linked");
    }
    
    public void setLinkedAbilityPriority(Ability ability, int priority) {
        int i = findIndexed("Linked",  "NAME", ability.getName());
        if ( i != -1 ) {
            addIndexed(i, "Linked","PRIORITY", new Integer(priority), true);
        }
    }
    
    public int getLinkedAbilityCount() {
        return getIndexedSize("Linked");
    }
    
    public int findLinkedAbility(Ability ability) {
        return  findIndexed("Linked",  "NAME", ability.getName());
    }
    
    public String getLinkedAbilityName(int index) {
        return getIndexedStringValue(index, "Linked", "NAME" );
    }
    
    public int getLinkedAbilityPriority(int index) {
        Integer i = getIndexedIntegerValue(index, "Linked", "PRIORITY");
        return i==null?0:i.intValue();
    }
    
    /** Sets the Priority of this Ability with respected to it's linked list.
     */
    public void setLinkedPriority(int priority) {
        add("Linked.PRIORITY", new Integer(priority), true);
    }
    
    /** Returns this ability priority with respect to other's in it's linked
     * list.
     */
    public int getLinkedPriority(int index) {
        Integer i = getIntegerValue("Linked.PRIORITY");
        return i==null?0:i.intValue();
    }
    
    /**
     * @param a
     * @return  */
//    public boolean hasAdvantage(Advantage a) {
//        if ( a == null ) return false;
//        
//        int count,index;
//        Advantage b;
//        String name = a.getName();
//        count = getIndexedSize("Advantage");
//        for(index=0;index<count;index++) {
//            b = (Advantage)getAdvantage(index);
//            if ( name.equals(b.getName()) ) return true;
//        }
//        return false;
//    }
    
    /**
     * @param a
     * @return  */
//    public boolean hasAdvantage(String advantageName) {
//        if ( advantageName == null ) return false;
//        
//        int count,index;
//        Advantage b;
//        count = getIndexedSize("Advantage");
//        for(index=0;index<count;index++) {
//            b = (Advantage)getAdvantage(index);
//            if ( advantageName.equals(b.getName()) ) return true;
//        }
//        return false;
//    }
    
    /** Return the index of the first advantage with name, -1 if the advantage does not exist...
     * @param a
     * @return  */
    public int getAdvantageIndex(String advantageName) {
        if ( advantageName == null ) return -1;
        
        int count,index;
        Advantage b;
        count = getAdvantageCount();
        for(index=0;index<count;index++) {
            b = (Advantage)getAdvantage(index);
            if ( advantageName.equals(b.getName()) ) return index;
        }
        return -1;
    }
    
    
    
    /**
     * @param a
     * @return  */
    public boolean hasSpecialParameter(SpecialParameter a) {
        if ( a == null ) return false;
        
        int count,index;
        SpecialParameter b;
        String name = a.getName();
        count = getIndexedSize("SpecialParameter");
        for(index=0;index<count;index++) {
            b = (SpecialParameter)getIndexedValue(index,"SpecialParameter","SPECIALPARAMETER");
            if ( name.equals(b.getName()) ) return true;
        }
        return false;
    }
    
    /**
     * @param name
     * @return  */
    public boolean hasSpecialParameter(String name) {
        if ( name == null ) return false;
        
        int count,index;
        SpecialParameter b;
        count = getIndexedSize("SpecialParameter");
        for(index=0;index<count;index++) {
            b = (SpecialParameter)getIndexedValue(index,"SpecialParameter","SPECIALPARAMETER");
            if ( b.getName().equals(name) ) return true;
        }
        return false;
    }
    
    /**
     * @param name
     * @return  */
    public SpecialParameter findSpecialParameter(String name) {
        int count,index;
        SpecialParameter b;
        count = getIndexedSize("SpecialParameter");
        for(index=0;index<count;index++) {
            b = (SpecialParameter)getIndexedValue(index,"SpecialParameter","SPECIALPARAMETER");
            if ( b.getName().equals(name) ) return b;
        }
        return null;
    }
    
    public int getSpecialParameterIndex(SpecialParameter sp) {
        return findIndexed("SpecialParameter","SPECIALPARAMETER", sp);
    }
    
    /**
     * @return  */
    public static Object[][] getSpecialParameterArray() {
        return specialParameterArray;
    }
    
    /* Ability Instance Group Manipulation Functions
     */
    
    
    /** Returns whether this is the Base Ability of an Ability Instance Group.
     * @return True if Ability is Base Instance
     */
    public boolean isBaseInstance() {
        AbilityInstanceGroup aig = getInstanceGroup();
        return aig != null ? (aig.getBaseInstance() == this) : true;
    }
    
    /** Returns whether this is the Framework Ability of an Ability Instance Group.
     * @return True if Ability is Base Instance
     */
    public boolean isFrameworkInstance() {
        AbilityInstanceGroup aig = getInstanceGroup();
        return aig != null ? (aig.getFrameworkInstance() == this) : true;
    }
    
    /** Returns whether this Ability is the Currently Active Instance of an Ability
     * Instance Group.
     * @return True if Ability is Currently Active Instance
     */
    public boolean isCurrentInstance() {
        AbilityInstanceGroup aig = getInstanceGroup();
        return aig != null ? (aig.getCurrentInstance() == this) : true;
    }
//    
//    /** Returns whether <CODE>anotherAbility</CODE> belongs to this Abilities
//     * Instance Group.
//     * @return True if anotherAbility is member of this Ability
//     * Instance Group.
//     * @param anotherAbility Ability which is being checked for instance group
//     * membership.
//     */
//    public boolean isInstanceOf(Ability anotherAbility) {
//        if ( isBaseInstance() == false ) {
//            return getBaseInstance().isInstanceOf(anotherAbility);
//        }
//        else {
//            return ( findExactIndexed( "Instance", "ABILITY", anotherAbility ) != -1 );
//        }
//    }
//    
//    /** Returns the Base Ability Instance of the Ability Instance Group this ability
//     * belongs to.
//     * @return Base Ability of Instance Group.
//     */
//    public Ability getBaseInstance() {
//        return (Ability)getValue("Ability.BASEINSTANCE");
//    }
//    
//    /** Returns the Current Ability Instance of the Ability Instance Group this ability
//     * belongs to.
//     * @return Base Ability of Instance Group.
//     */
//    public Ability getCurrentInstance() {
//        if ( isBaseInstance() == false ) {
//            return getBaseInstance().getCurrentInstance();
//        }
//        else {
//            return (Ability)getValue("Ability.CURRENTINSTANCE");
//        }
//        
//    }
//    
//    /** Return the number of instances of this ability, including the base.
//     *
//     */
//    public int getInstanceCount() {
//        return getIndexedSize("Instance");
//    }
//    
//    /** Returns the <CODE>instanceNumber</CODE> ability of this Ability's Ability
//     * Instance Group.
//     * @param instanceNumber Index of the instance to be returned.
//     * @return Ability which is at index location <CODE>instanceNumber</CODE>.
//     * NULL if <CODE>instanceNumber</CODE> is out of range.
//     */
//    public Ability getInstance(int instanceNumber) {
//        return (Ability) getIndexedValue(instanceNumber, "Instance", "ABILITY" );
//    }
//    
//    /** Creates a new Ability based on the Base Ability of this ability's Ability
//     * Instance Group.  All Instance Group Information is updated appropriately.
//     * @return New Ability instance based on Base Ability Instance.
//     */
//    public Ability createNewInstanceFromBase() {
//        Ability newAbility = (Ability)getBaseInstance().createChildInstance();
//        getBaseInstance().mergeIntoInstanceGroup(newAbility);
//        return newAbility;
//    }
//    
//    /** Creates a new Ability based on the Current Ability of this ability's Ability
//     * Instance Group.  All Instance Group Information is updated appropriately.
//     * @return New Ability instance based on Base Ability Instance.
//     */
//    public Ability createNewInstanceFromCurrent() {
//        Ability newAbility = (Ability)getCurrentInstance().createChildInstance();
//        getBaseInstance().mergeIntoInstanceGroup(newAbility);
//        return newAbility;
//    }
//    
//    /** Sets the Current Ability of this Ability's Ability Instance Group.
//     * This does not have to be the base ability of the Abiltiy Instance Group.
//     * All Instance information contained in the base ability will be updated
//     * appropriately.
//     * @param ability Ability which is now the new Current Ability.
//     * <CODE>ability</CODE> must be an instance member of
//     * this Ability's Ability Instance Group.  It can be the
//     * Base ability.
//     */
//    public void setCurrentInstance(Ability ability) {
//        Ability oldCurrent = getCurrentInstance();
//        Ability base = getBaseInstance();
//        if ( ability == null ) ability = base;
//        
//        if ( oldCurrent != ability  ) {
//            // This section of code needs to be synchronized like hell since it is possible
//            // and very likely that the event thread will be looking for the Current instance
//            // will the BattleEngine is modifying the current instance.  Since,
//            // setting the current instance is a 2 or 3 step process, a race condition exists.
//            synchronized( base ) {
//                synchronized( ability ) {
//                    if ( oldCurrent != null ) {
//                        synchronized( oldCurrent ) {
//                            oldCurrent.add( "Ability.ISCURRENTINSTANCE", "FALSE", true, false);
//                            base.add( "Ability.CURRENTINSTANCE", ability, true, false);
//                            ability.add( "Ability.ISCURRENTINSTANCE", "TRUE", true, false);
//                        }
//                    }
//                    else {
//                        base.add( "Ability.CURRENTINSTANCE", ability, true, false);
//                        ability.add( "Ability.ISCURRENTINSTANCE", "TRUE", true, false);
//                    }
//                }
//            }
//        }
//    }
//    
//    /** Splits an Ability from it's current Ability Instance Group creating a new group with this ability as base.
//     * A new Ability Instance Group will be created and this ability will be set
//     * to be the base and current instance of the new Ability Instance Group.
//     * The Ability is checked to make sure it is not currently a member of an instance Group.
//     * If the Ability is a member of a current group, it will be removed from it's current group.
//     */
//    public void splitFromInstanceGroup() {
//        if ( this.isBaseInstance() ) {
//            // This should only happen when there is only one instance in the instance group
//            if ( getIndexedSize("Instance") > 1 ) {
//                ExceptionWizard.postException( new Exception( "WARNING: Base Ability Instance Split from Group.\n" + this.dumpDetailList() ));
//                
//            }
//            removeAll("Instance");
//        }
//        else if ( this.getBaseInstance() != null ) {
//            if ( this.getParentAbility() == getBaseInstance()) {
//                this.setParentAbility(null);
//            }
//            getBaseInstance().removeInstance(this);
//        }
//        
//        makeAbilityBaseInstance();
//    }
//    
//    /** Make necessary changes to Ability to make it the Base of a new Abiltiy Instance Group.
//     *
//     * The proper Value/Pairs are added to the ability to make it a base instance.
//     */
//    protected void makeAbilityBaseInstance() {
//        synchronized (this) {
//            add("Ability.ISBASEINSTANCE", "TRUE", true, false);
//            add("Ability.BASEINSTANCE", this, true, false);
//            
//            addInstance(this);
//            setCurrentInstance(this);
//        }
//    }
//    
//    /**
//     */
//    protected void mergeIntoInstanceGroup(Ability newAbility) {
//        if ( newAbility.isBaseInstance() ) {
//            // This should only happen when there is only one instance in the instance group
//            if ( newAbility.getIndexedSize("Instance") > 1 ) {
//                ExceptionWizard.postException( new Exception( "WARNING: Base Ability Instance Split from Group.\n" + newAbility.dumpDetailList() ));
//            }
//            
//            newAbility.add("Ability.ISBASEINSTANCE", "FALSE", true, false);
//            newAbility.add("Ability.ISCURRENTINSTANCE", "FALSE", true, false);
//            newAbility.remove("Ability.CURRENTINSTANCE");
//            newAbility.removeAll("Instance");
//        }
//        else if ( this.getBaseInstance() != null ) {
//            getBaseInstance().removeInstance(this);
//        }
//        
//        newAbility.add("Ability.BASEINSTANCE", this, true, false);
//        addInstance(newAbility);
//    }
//    
//    /** Removes <CODE>ability</CODE> from this Ability's Ability Instance Group.
//     * Once the ability is removed, it should be made a base Ability or destroyed.
//     * @param ability
//     */
//    protected Undoable removeInstance(Ability ability) {
//        Undoable u = null;
//        
//        if ( isBaseInstance() == false ) {
//            u = getBaseInstance().removeInstance(ability);
//        }
//        else {
//            int index;
//            boolean wasBase = ability.isCurrentInstance() ;
//            if ( (index = findExactIndexed( "Instance", "ABILITY", ability )) != -1 ) {
//                removeIndexed(index ,"Instance", "ABILITY");
//            }
//            
//            if ( wasBase) {
//                setCurrentInstance(this);
//            }
//            
//            Ability parent = ability.getParentAbility();
//            if ( parent != null ) {
//                ability.setParentAbility(null);
//            }
//            u = new RemoveAbilityInstanceUndoable(this, ability, wasBase, parent);
//        }
//        
//        return u;
//    }
//    
//    /** Deletes an instance from it's Instance Group.
//     *
//     * This method should be used to clean up excess instances that are no longer necessary.
//     * Calling this on the base instance does nothing.
//     */
//    public Undoable removeInstanceFromInstanceGroup() {
//        Undoable u = null;
//        
//        if ( isBaseInstance() == false ) {
//            u = getBaseInstance().removeInstance(this);
//        }
//        else {
//            System.out.println("Error: Attempt to removeInstanceFromInstanceGroup performed on base instance of " + this.getName());
//        }
//        
//        return u;
//    }
//    
//    /** Adds <CODE>ability</CODE> to this Ability's Ability Instance Group.
//     * @param ability
//     */
//    protected void addInstance(Ability ability) {
//        if ( isBaseInstance() == false ) {
//            getBaseInstance().addInstance(ability);
//        }
//        else {
//            if ( findExactIndexed( "Instance", "ABILITY", ability ) == -1 ) {
//                createIndexed( "Instance", "ABILITY", ability );
//            }
//        }
//    }
    
    public AbilityInstanceGroup getInstanceGroup() {
        //return (AbilityInstanceGroup)getValue("Ability.INSTANCEGROUP");
        return instanceGroup;
    }
    
    /** Sets the instance group of the ability.
     *
     * This method handles all modification necessary to change the instance
     * group of an ability, including making calls to addInstance/removeInstance
     * on the new/old instance groups.
     *
     * This does not provide redoability.  If that is needed, the method
     * removeInstanceFromInstanceGroup() should be used instead.
     */
    public void setInstanceGroup(AbilityInstanceGroup instanceGroup) {
        if ( this.instanceGroup != instanceGroup ) {
            if ( this.instanceGroup != null ) {
                this.instanceGroup.removeInstance(this);
            }
            
            //add("Ability.INSTANCEGROUP", aig, true);
            this.instanceGroup = instanceGroup;
            
            if ( instanceGroup != null ) {
                instanceGroup.addInstance(this);
            }
        }
    }
    
    /** Removes the Ability from it's instance group. 
     * 
     * This method provides a wrapper for setInstanceGroup(null) which
     * provides redoability of the removal.
     */
    public Undoable removeInstanceFromInstanceGroup() {
        // This method provides a wrapper
        boolean wasCurrent = isCurrentInstance();
        Ability parent = getParentAbility();
        AbilityInstanceGroup oldGroup = getInstanceGroup();
        
        setInstanceGroup(null);
        
        return new Ability.RemoveAbilityInstanceUndoable(oldGroup, this, wasCurrent, parent);
        
    }
    
    /** Returns a new instance of this ability, which is linked to this one.
     *
     * The parameters lists of the child will be linked to this ability.  Also,
     * the Child.ABILITY index of this ability will be set so reconfiguration
     * information can be propogated.
     *
     * This is totally a hack and don't let anyone tell you different. Man,
     * this is really a bunch of crap and I can't imagine a more awkward and
     * difficult way to do this.  I am personally ashamed of this code and I 
     * truly believe that anyone who reads it is dumber for having done so.
     */
    
    public Ability createChildInstance() {
        Ability newAbility = createAbilityObject(false);
        
        // Copy Special Stuff...
        Object o;
        //newAbility.add("Ability.NAME", getName(),true, true);
        newAbility.name = name;
        
//        if ( ( o = getValue("Ability.SOURCE") ) != null ) {
//            newAbility.add("Ability.SOURCE", o, true, false);
//        }
//        
//        if ( ( o = getValue("Ability.AUTOSOURCE") ) != null ) {
//            newAbility.add("Ability.AUTOSOURCE", o, true, false);
//        }
        newAbility.sourceAlias = sourceAlias;
        newAbility.autoSource = autoSource;
        
        // Don't copy the AbilityList of the original!!!
        int index, count;
        String key;
        
        // First copy the advantages, disadvantages, special parameter...
//        count = this.getAdvantageCount();
//        for(index=0;index<count;index++) {
//            Advantage a = (Advantage)this.getAdvantage(index);
//            newAbility.createIndexed("Advantage","ADVANTAGE",a);
//        }
        
//        count = getLimitationCount();
//        for(index=0;index<count;index++) {
//            Limitation l = (Limitation)this.getIndexedValue( index, "Limitation","LIMITATION" );
//            newAbility.createIndexed("Limitation","LIMITATION",l);
//        }
        
        count = getIndexedSize( "SpecialParameter" ) ;
        for ( index=0;index<count;index++ ) {
            SpecialParameter l = (SpecialParameter)getIndexedValue( index, "SpecialParameter", "SPECIALPARAMETER" );
            newAbility.createIndexed("SpecialParameter", "SPECIALPARAMETER",l);
        }
        
        count = getIndexedSize( "SpecialEffect" ) ;
        for ( index=0;index<count;index++ ) {
            SpecialEffect se = (SpecialEffect)getIndexedValue( index, "SpecialEffect", "SPECIALEFFECT" );
            newAbility.createIndexed("SpecialEffect", "SPECIALEFFECT",se);
        }
        
        if ( this.getPower() != null ) {
            Power power;
            if ( newAbility.getPower() == null ) {
                power = (Power)this.getPower().clone();
            
            }
            else {
                power = newAbility.getPower();
            }
                        
            ParameterList parent = getPower().getParameterList(this,-1);
            ParameterList pl = new ParameterList(parent);
            power.configurePAD(newAbility, pl);
            if(newAbility.getPower() == null) {
            	newAbility.setPower(power, pl);
            }
        }
        
        count = getAdvantageCount();
        for(index=0;index<count;index++) {
            Advantage adv = getAdvantage(index);
            Advantage childAdv = adv.clone();
            ParameterList pl = new ParameterList( adv.getParameterList() );
            
            newAbility.addPAD(childAdv,pl);
            if(newAbility.getAdvantageIndex(adv.getName())==0) {
            	newAbility.addAdvantageInfo(childAdv, childAdv.getName(), pl);
            }
        }
        
        count = getLimitationCount();
        for(index=0;index<count;index++) {
            Limitation lim = getLimitation(index);
            Limitation childLim = lim.clone();
            ParameterList pl = new ParameterList( lim.getParameterList() );
            
            newAbility.addPAD(childLim,pl);
        }
        
        count = getIndexedSize( "SpecialParameter" ) ;
        for ( index=0;index<count;index++ ) {
            SpecialParameter l = (SpecialParameter)getIndexedValue( index, "SpecialParameter", "SPECIALPARAMETER" );
            ParameterList pl = new ParameterList(l.getParameterList(this, index));
            l.configure(newAbility,pl);
        }
        
        // Calculate the multipliers, and trigger CP and END calculations.
        newAbility.calculateMultiplier();
        
        newAbility.setParentAbility(this);
        newAbility.setInstanceGroup(this.getInstanceGroup());
        newAbility.setFramework(this.getFramework());
        
        
        newAbility.calculateCPCost();
        
        return newAbility;
    }
    
    /** Creates an unconfigured object of the correct class for use in cloning.
     *
     * This utility method should create a plain, unconfigured Ability object
     * of the same class as the original.  This object will then be configured
     * by clone or createChildInstance to create a new Ability.
     */
    protected Ability createAbilityObject(boolean createInstanceGroup) {
        return new Ability(createInstanceGroup);
    }
    
    /** Returns the Parent of this ability.
     *
     */
    public Ability getParentAbility() {
        //return (Ability)getValue("Ability.PARENT");
        return parent;
    }
    
    /** Set the Parent of the Ability.
     *
     * This method should be used to set the parent of the ability.  It will
     * take care of calling addChildAbility and removeChildAbility against the
     * old and new parents, if necessary.
     *
     * NOTE: All related abilties must be in the same instance group.  However,
     * due to synchronization issues, an ability parent should be set prior to
     * including it in an instance and the ability should be removed from the
     * instance prior to being removed from the parent.
     *
     * @todo Merge parameter list to parent when setting a parent.
     */
    protected void setParentAbility(Ability parent) {
        if ( this.parent != parent && parent != this) {
            if( this.parent != null ) {
                this.parent.removeChildAbility(this);
                seperateParameterListsFromParent();
                
                // Fix the normally on setting...
                normallyOn = this.parent.normallyOn;
            }
        
            //add("Ability.PARENT", parent, true);
            this.parent = parent;

            if ( this.parent != null ) {
                this.parent.addChildAbility(this);
                mergeParameterListsToParent();
            }
        }
    }
    
    private void seperateParameterListsFromParent() {
        int count, index;
        
        if ( getPower() != null ) getPower().getParameterList(this, -1).setParent(null);
        
        count = this.getAdvantageCount();
        for(index=0;index<count;index++) {
            Advantage a = (Advantage)this.getAdvantage(index);
            a.getParameterList().setParent(null);
        }
        
        count = getLimitationCount();
        for(index=0;index<count;index++) {
            //Limitation l = (Limitation)this.getIndexedValue( index, "Limitation","LIMITATION" );
            Limitation l = getLimitation(index);
            l.getParameterList().setParent(null);
        }
        
        count = getIndexedSize( "SpecialParameter" ) ;
        for ( index=0;index<count;index++ ) {
            SpecialParameter l = (SpecialParameter)getIndexedValue( index, "SpecialParameter", "SPECIALPARAMETER" );
            l.getParameterList(this,index).setParent(null);
        }
    }
    
    private void mergeParameterListsToParent() {
        int count, index;
        
        Ability parent = getParentAbility();
        
        if ( parent != null ) {
            if ( getPower() != null ) getPower().getParameterList(this, -1).setParent(parent.getPower().getParameterList(parent, -1));

            count = this.getAdvantageCount();
            for(index=0;index<count;index++) {
                Advantage adv = getAdvantage(index);
                Advantage parentAdv = parent.getAdvantage(index);
                
                adv.getParameterList().setParent(parentAdv.getParameterList());
            }

            count = getLimitationCount();
            for(index=0;index<count;index++) {
                //Limitation l = (Limitation)this.getIndexedValue( index, "Limitation","LIMITATION" );
                //l.getParameterList(this,index).setParent(l.getParameterList(parent,index));
                Limitation l = getLimitation(index);
                Limitation parentLim = parent.getLimitation(index);
                
                l.getParameterList().setParent(parentLim.getParameterList());
            }

            count = getIndexedSize( "SpecialParameter" ) ;
            for ( index=0;index<count;index++ ) {
                SpecialParameter l = (SpecialParameter)getIndexedValue( index, "SpecialParameter", "SPECIALPARAMETER" );
                l.getParameterList(this,index).setParent(l.getParameterList(parent,index));
            }
        }
    }
    
    /** Adds the ability as a child of this ability.  
     *
     * Do not call this method directly.  Only setParentAbility of the child 
     * should normally use this ability.
     *
     * Childern of this ability should have thier configurePAD methods run 
     * whenever a configurePAD is run against these abilities.
     */
    protected void addChildAbility(Ability child) {
        int index = findExactIndexed("Child", "ABILITY", child);
        if ( index == -1 ) {
            createIndexed("Child", "ABILITY", child);
        }
    }
    
    /** Remove the child ability.
     *
     * Only setParentAbility should call this method.
     */
    protected void removeChildAbility(Ability child) {
        int index = findExactIndexed("Child", "ABILITY", child);
        if ( index != -1 ) {
            removeIndexed(index, "Child", "ABILITY");
        }
    }
    
    /** Returns whether indicated ability is a child of this ability.
     *
     */
    protected boolean isChildAbility(Ability child) {
        return findExactIndexed("Child", "ABILITY", child) != -1;
    }
    
    /** Returns the number of child abilities this ability has.
     *
     */
    protected int getChildAbilityCount() {
        return getIndexedSize("Child");
    }
    
    /** Returns the indicated child ability.
     *
     */
    protected Ability getChildAbility(int index) {
        return (Ability) getIndexedValue(index, "Child", "ABILITY");
    }
    
    /** Returns if this is a variation for the instance group.
     *
     *  Variations are ability which have different sfx and advantages.
     *  This should only occur when the base ability has variable advantage
     *  or variable sfx.
     */
    public boolean isVariationInstance() {
        if ( getInstanceGroup() == null ) return false;
        return getInstanceGroup().isVariationInstance(this);
    }
    
    /** Shuts down all activated ActivationInfo attached to the Ability.
     *
     * The shutdowns will be embedded into the provided be.
     *
     * @param forceAll If true, indicates that all abilities should be shut down 
     * no matter what state they are in.  If false, uncontrolled/independent 
     * abilities will remain active until the shut down themselves.
     */
    public void shutdownActivated(BattleEvent be, boolean forceAll) throws BattleEventException{
        int count = getActivationCount();
        for(int i = 0; i < count; i++) {
            ActivationInfo ai = getActivationInfo(i);
            if ( ai.isActivated() ) {
                // Currently there is no easy way embed the event if there is an
                // existing attack tree...so just queue a shutdown of all the 
                // Activations
                Battle.currentBattle.addEvent( new BattleEvent(BattleEvent.DEACTIVATE, ai) );
            }
        }
    }
    
    
    /** Sets the Current Adjusted CP cost of this Ability
     * If the ability is the base of an instance group, a new instance of the ability if created.
     * If the ability is also the current instance of the instance group, the newly created instance
     * will become the instance groups current instance.
     *
     * The Ability.ADJUSTEDCPCOST and Ability.ADJUSTEDPOWERCOST contain the information
     * regarding the current adjusted CP cost of the ability.
     *
     * The Ability will automatically be reconfigured to accommodate the newly adjusted CP costs.
     */
  /*  public void setAdjustedCPCost(int cost) {
        
        // First check to see if this is the base instance of an Ability instance group
        if ( isBaseInstance() || isFrameworkInstance() ) {
            ExceptionWizard.postException( new Exception("A non-modifiable instance (base or framework) is attempting to be adjusted."));
        }
        // It wasn't the base, so we can continue adjusting things
        add("Ability.ADJUSTEDCPCOST", new Integer(cost), true, false);

        // Grab the Advantage and Limitation multipliers
        Double d;
        double adv, lim;
        if ( (d = getDoubleValue("Ability.ADVCOST" )) != null ) {
            adv = d.doubleValue();
        }
        else {
            adv = 0;
        }

        if ( (d = getDoubleValue("Ability.LIMCOST" )) != null ) {
            lim = d.doubleValue();
        }
        else {
            lim = 0;
        }

        // Perform the backwards calculate to generate the maximum power cost
        double powerCost = cost * ( 1 + -1 * lim ) / ( 1 + adv );
        add("Ability.ADJUSTEDPOWERCOST", new Double(powerCost), true, false);

        // Actually Reconfigure Power
        if ( getPower() != null ) getPower().reconfigurePower(this);
        
        // Might need to notify the children here...
        for(int i = 0; i < getChildAbilityCount(); i++) {
            getChildAbility(i).parentAbilityConfigurationChanged();
        }
    }*/
    
    protected void parentAbilityConfigurationChanged() {
       // calculateCPCost();
        reconfigure();
    }
    
    /** Indicates the parent ability was adjusted by an adjustment power.
     *
     */
    protected void parentAbilityAdjusted() {
        // Start by reconfiguring the power to update the power and
        // update the cost of the power
        reconfigurePower();
        
        if ( getPowerCost() != Math.round( getMaximumPowerAllocation() ) && getPower() != null) {
            getPower().reconfigurePower(this);
        }
        
        firePropertyChange(this, "Ability.ADJUSTED", new Integer(Integer.MIN_VALUE), new Integer( Integer.MAX_VALUE));
        
        for(int i = 0; i < getChildAbilityCount(); i++) {
            //getChildAbility(i).parentAbilityConfigurationChanged();
            getChildAbility(i).parentAbilityAdjusted();
        }
    }
    
    /** Returns the number of points allocated for power configuration after adjustments.
     *
     */
    public double getMaximumPowerAllocation() {
        
        Power p = getPower();
        
        double adv = getAdvantagesMultiplier();
        double lim = getLimitationsMultiplier();
        
        int maxAllocation = getMaximumCPAllocation();
        
        return maxAllocation * ( 1 + -1 * lim ) / ( 1 + adv );
    }
    
    /** Returns the number of character points which can be used to configure the ability.
     *
     * This methods calculates the number of character points which can be used to 
     * allocate the ability legally.  This is based upon the number of CP configured
     * in the parent ability (if any) and the adjustments currently applied to this
     * ability.
     */
    public int getMaximumCPAllocation() {
        Integer o;
        Ability parent = getParentAbility();
        if ( parent != null ) {
            int allocatedParentCP = parent.getMaximumCPAllocation();
            int adjustmentAmount = getAdjustmentAmount();
            return allocatedParentCP + adjustmentAmount;
        }
        else {
            // The parent is null, so this must be the base instance...
            // just return the actual cp
            return getCPCost();
        }
    }
    
    /** Returns the amount of adjustment applied to this specific instance of the ability.
     *
     * This is not cummulative with parent adjustments.  Use getTotalAdjustmentAmount do
     * determine that.
     */
    public int getAdjustmentAmount() {
//        Integer o = getIntegerValue("Ability.CPADJUSTMENT");
//        return ( o != null ) ? o.intValue() : 0;
        
        return adjustmentCP;
    }
    
    /** Sets the amount of adjustment applied to this specific instance of the ability.
     *
     * This is not cummulative with parent adjustments.  Only the adjustment applied
     * to this specific instance is considered.
     */
    public void setAdjustmentAmount(int adjustment) {
        if ( getAdjustmentAmount() != adjustment ) {
            // First check to see if this is the base instance of an Ability instance group
            if ( isBaseInstance() || isFrameworkInstance() ) {
                ExceptionWizard.postException( new Exception("A non-modifiable instance (base or framework) is attempting to be adjusted."));
            }

            //add("Ability.CPADJUSTMENT", new Integer(adjustment), true);
            adjustmentCP = adjustment;

                    // Grab the Advantage and Limitation multipliers
            Double d;
            double adv = getAdvantagesMultiplier();
            double lim = getLimitationsMultiplier();
            
            // Perform the backwards calculate to generate the maximum power cost
       /*     int maxAllocation = getMaximumCPAllocation();
            double powerCost = maxAllocation * ( 1 + -1 * lim ) / ( 1 + adv );
            add("Ability.ADJUSTEDPOWERCOST", new Double(powerCost), true, false); */

            // Actually Reconfigure Power
            if ( getPower() != null ) getPower().reconfigurePower(this);

            // Might need to notify the children here...
            for(int i = 0; i < getChildAbilityCount(); i++) {
                //getChildAbility(i).parentAbilityConfigurationChanged();
                getChildAbility(i).parentAbilityAdjusted();
            }

            firePropertyChange(this, "Ability.ADJUSTED", new Integer(Integer.MIN_VALUE), new Integer( Integer.MAX_VALUE));
        }
    }
    
    /** Returns the Current Adjusted Power Cost.
     * The AdjustedPowerCost is the total number of Character points the power can use
     * and still stay beneath the adjust CP cost once all advantages and disadvantages are
     * applied.
     *
     * Theoretically, this number could be a fraction, so it is returned as a double.
     */
    /*public double getAdjustedPowerCost() {
        Double o;
        o = getDoubleValue("Ability.ADJUSTEDPOWERCOST");
        return ( o != null ) ? o.doubleValue() : (double)getPowerCost();
    }*/
    
    /** Returns an Ability which is valid to be adjusted by adjustment powers.
     * The base instance of Ability Instance Group can not be adjusted.
     * getAdjustableAbility() makes sure that the ability being worked
     * with is actually valid for adjustment (ie, it is not the base).
     */
    public Ability getAdjustableInstance() {
        // If it is the base and current create a new instance.
        // If it is just the base, just return the current.
        // If it is not the base, just return this.
        return getInstanceGroup().getModifiableAdjustedInstance();
    }
    
    /**
     * @param amount
     * @return  */
    public AbilityChangeUndoable applyAidToAbility(int amount) {
        if ( isBaseInstance() || isFrameworkInstance() ) return getAdjustableInstance().applyAidToAbility(amount);
        
        int oldCP = getAdjustmentAmount();
        
        setAdjustmentAmount( oldCP + amount );
        
        fireTargetAdjustedEvent();
        
        return new Ability.AbilityChangeUndoable(this, oldCP, oldCP + amount);
    }
    
    /**
     * @param amount
     * @return  */
    public AbilityChangeUndoable applyDrainToAbility(int amount) {
        if ( isBaseInstance() || isFrameworkInstance() ) return  getAdjustableInstance().applyDrainToAbility(amount);
        
        int oldCP = getAdjustmentAmount();
        
        setAdjustmentAmount( oldCP - amount );
        
        fireTargetAdjustedEvent();
        
        return new Ability.AbilityChangeUndoable(this, oldCP, oldCP - amount);
    }
    
    /**
     * @param amount
     * @return  */
    public AbilityChangeUndoable applySetToAbility(int amount) {
        if ( isBaseInstance() || isFrameworkInstance() ) return getAdjustableInstance().applySetToAbility(amount);
        
        int oldCP = getAdjustmentAmount();
        
        setAdjustmentAmount( amount );
        
        fireTargetAdjustedEvent();
        
        return new Ability.AbilityChangeUndoable(this, oldCP, amount);
    }
    
    /** Fires off a targetAdjusted event to the appropriate target.
     *
     *  Not that this method runs the event synchronously, in the 
     * thread that triggered the event. 
     */
    protected void fireTargetAdjustedEvent() {
        if ( getSource() != null ) getSource().targetAdjusted();
    }
    

    
    /** Returns if this ability is currently adjusted positively.
     *
     * In this case, adjusted positively is consider to mean adjusted positively
     * by aid, absorption, or transfer.
     *
     * An ability is considered adjusted positively if the sum total of adjustments
     * to this ability and its parent is positive.
     */
    public boolean isAdjustedPositively() {
        return getTotalAdjustmentAmount() > 0;
    }
    
    /** Returns if this ability is currently adjusted negatively.
     *
     * In this case, adjusted negatively is consider to mean adjusted negatively
     * by drain or transfer.
     *
     * An ability is considered adjusted negatively if the sum total of adjustments
     * to this ability and its parent is negative.
     */
    public boolean isAdjustedNegatively() {
        return getTotalAdjustmentAmount() < 0;
    }
    
    /** This method returns the relative adjustment, due to adjustment powers, applied to this ability.
     *
     * This method sums all of the adjustments currently applied to this ability,
     * including adjustments that occurred to all parents and grandparents.
     */
    public int getTotalAdjustmentAmount() {
        Ability parent = getParentAbility();
        if ( parent == null ) return 0;
        
        return ( parent.getTotalAdjustmentAmount() + getAdjustmentAmount() );
    }
    
    /** Returns an Ability which is valid to be modified with state informtion.
     * If this is the Base instance of an Ability Instance Group, the current
     * instance is returned.  Otherwise, this is returned.
     */
    public Ability getModifiableInstance() {
        // If it is the base and current create a new instance.
        // If it is just the base, just return the current.
        // If it is not the base, just return this.
        if ( ! isModifiableInstance() ){
            if ( getInstanceGroup().getCurrentInstance().isModifiableInstance() ) {
                return getInstanceGroup().getCurrentInstance();
            }
            else {
                Ability ability = getInstanceGroup().createNewInstance();
                getInstanceGroup().setCurrentInstance(ability);
                return ability;
            }
        }
        else {
            return this;
        }
    }
    
    public boolean wasCreatedAutomatically() {
        return getBooleanValue("Ability.CREATEDAUTOMATICALLY");
    }
    
    public void setCreatedAutomatically(boolean createdAutomatically) {
        add("Ability.CREATEDAUTOMATICALLY", createdAutomatically ? "TRUE" : "FALSE", true, false);
    }
    
//    public void activationStateChanged(ActivationInfo ai, String oldState, String newState) {
//        AbilityInstanceGroup aig = getInstanceGroup();
//        if ( aig != null ) aig.activationStateChanged(this, ai, oldState, newState);
//    }
    
    /** Returns where this instance of the ability can be modified by the battleEngine.
     *
     * The base, framework, and adjusted instances are not considered modifiable.
     * Actually, the adjusted instance is modified by battle engine in cases
     * of adjustment effects, but it is not modified for any other reason.
     */
    public boolean isModifiableInstance() {
        AbilityInstanceGroup aig = getInstanceGroup();
        return aig != null ? aig.isModifiableInstance(this) : true;
    }
    
    /** Provides hook to add menu items to the Ability right-click menu
     * @param menu
     * @param ability
     * @return true if menus where actually added.
     */
    public boolean invokeMenu(JPopupMenu menu) {
        boolean added = false;
        
        if ( getPower() != null ) {
            if ( getPower().invokeMenu(menu, this) == true ) added = true;
        }
        
        int index, count;
        count = getAdvantageCount();
        for(index = 0; index < count; index ++) {
            Advantage a = (Advantage)getAdvantage(index);
            if ( a != null ) {
                if ( a.invokeMenu(menu, this, index) == true ) added = true;
            }
        }
        
        count = getLimitationCount();
        for(index = 0; index < count; index ++) {
            Limitation a = getLimitation(index);
            if ( a != null ) {
                if ( a.invokeMenu(menu, this, index) == true ) added = true;
            }
        }
        
        return added;
    }
    
    
    /**
     * @return  */
    public boolean isInstanceActivated() {
//        ActivationInfo ai;
//        IndexIterator ii = getIteratorForIndex("ActivationInfo");
//        int index = -1;
//        while ( ii.hasNext() ) {
//            index = ii.nextIndex();
//            ai = (ActivationInfo)this.getIndexedValue( index, "ActivationInfo", "ACTIVATIONINFO" );
//            if ( ai.isActivated() ) return true;
//        }
        Iterator<ActivationInfo> it = getActivations();
        while(it.hasNext()) {
            if ( it.next().isActivated() ) return true;
        }
        return false;
    }
    
    /**
     * @return  */
    public boolean isInstanceAdjusted() {
        if ( getCPCost() != getMaximumCPAllocation() ) return true;
        else if ( getParentAbility() == null ) return false;
        else if ( getCPCost() != getParentAbility().getCPCost()) return true;
        return false;    
    }
    /**
     * @param attackType
     * @param defense  */
    public void setIsNND(String attackType, String defense) {
        add( "Ability.ISNND", "TRUE", true, false);
        add( "NND.ATTACKTYPE", attackType, true, false);
        add( "NND.DEFENSE", defense, true, false);
    }
    
    /**
     * @param al  */
    public void setAbilityList(AbilityList al) {
//        if ( al != getAbilityList() ) {
//            add("Ability.ABILITYLIST", al, true, false);
//        }
        this.abilityList = al;
    }
    
    /**
     * @return  */
    public AbilityList getAbilityList() {
        //return (AbilityList)getValue("Ability.ABILITYLIST");
        return abilityList;
    }
    
    /** Sets the Framework for the ability.
     *
     * This method should always be called on the Ability.  The Ability version
     * will call setFramework on the AbilityInstanceGroup, which will in turn
     * inform the framework itself that it wants to be part of the framework.
     *
     * The Ability instance group will inform all of the instance ability that
     * a framework has been set via the frameworkSet method.
     */
    public void setFramework(Framework framework) {
        if ( getInstanceGroup() != null ) getInstanceGroup().setFramework(framework);
    }
    
    public Framework getFramework() {
        if ( getInstanceGroup() == null ) return null;
        return getInstanceGroup().getFramework();
    }
    
    public void frameworkSet() {
        calculateCPCost();
    }
    
    /**
     * @param specialEffect  */
    public void addSpecialEffect(SpecialEffect specialEffect) {
        if ( hasSpecialEffect( specialEffect.getName() ) == false ) {
            int index = createIndexed( "SpecialEffect", "SPECIALEFFECT", specialEffect, true);

            // Add the pad to the children...
            int count = getChildAbilityCount();
            for(int i = 0; i < count; i++) {
                Ability child = getChildAbility(i);
                child.addSpecialEffect(specialEffect);
            }
        }
    }
    
    /**
     * @param specialEffect  */
    public void removeSpecialEffect(SpecialEffect specialEffect) {
        int index = findIndexed("SpecialEffect", "SPECIALEFFECT", specialEffect);
        if ( index != -1 ) {
            // Add the pad to the children...
            int count = getChildAbilityCount();
            for(int i = 0; i < count; i++) {
                Ability child = getChildAbility(i);
                child.removeSpecialEffect(specialEffect);
            }
            
            removeAllIndexed(index, "SpecialEffect", true);
        }
    }
    
    /**
     * @param name
     * @return  */
    public boolean hasSpecialEffect(String name) {
        SpecialEffectIterator i = getSpecialEffectIterator();
        while ( i.hasNext() ) {
            SpecialEffect se = i.nextSpecialEffect();
            if (se.getName() != null && se.getName().equals(name) ) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @param name
     * @return  */
    public SpecialEffect findSpecialEffect(String name) {
        SpecialEffectIterator i = getSpecialEffectIterator();
        while ( i.hasNext() ) {
            SpecialEffect se = i.nextSpecialEffect();
            if (se.getName() != null && se.getName().equals(name) ) {
                return se;
            }
        }
        return null;
    }
    
    /**
     * @return  */
    public SpecialEffectIterator getSpecialEffectIterator() {
        return new MySpecialEffectsIterator(this);
    }
    
    /** Getter for property requiresTarget.
     *
     * The requiresTarget property indicates that a target is required to use this
     * ability.  Occasionally, there will be abilities that do not require targets,
     * such as some movement powers.
     *
     * If the "Ability.REQUIRESTARGET" is not set, TRUE will be returned.
     *
     * @return Value of property requiresTarget.
     */
    public boolean isRequiresTarget() {
        String rt = getStringValue("Ability.REQUIRESTARGET");
        return ( rt == null || rt.equals("TRUE") );
    }
    
    /** Setter for property requiresTarget.
     * @param requiresTarget New value of property requiresTarget.
     */
    public void setRequiresTarget(boolean requiresTarget) {
        add("Ability.REQUIRESTARGET", (requiresTarget?"TRUE":"FASLE"), true);
    }
    
    /**
     * Finalizes the Import of an Ability from an XML sourceAlias.
     * 
     * This method should be called after the Ability has been added to it's
     * sourceAlias (if one exists).  If called with a null sourceAlias, then the 
     * ability (and the power and adders) can assume that it will not be
     * attached to a specific sourceAlias.
     */
    public void finalizeImportXML(String format) {
        
    }

    public ArrayList<Adjustable> getAdjustablesForTarget(Target target, ArrayList<Adjustable> list) {
        AbilityIterator ai = target.getAbilities();
        while ( ai.hasNext() ) {
            Ability targetAbility = ai.nextAbility();
            
            if ( targetAbility.equals(this) ) {
                // Should we break here?
                // Can there be multiple ones?
                if ( list == null ) list = new ArrayList<Adjustable>();
                list.add(targetAbility);
            }
        }
        return list;
    }

    public void displayDebugWindow() {
        debugDetailList( this.toDebugString() + "@" + Integer.toHexString(hashCode()));
    }

    public String toDebugString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append( getNameWithInstance() );
        sb.append( " [" );
        
        AbilityInstanceGroup aig = getInstanceGroup();
        
        sb.append(Integer.toHexString(hashCode())).append(", ");
        sb.append("IG=").append( Integer.toHexString(aig.hashCode()) );
        
        sb.append(",<");
        
        if ( aig.getBaseInstance() == this ) {
            sb.append("B");
        }
        
        if ( aig.getFrameworkInstance() == this ) {
            sb.append("F");
        }
        
        if ( aig.getAdjustedInstance() == this ) {
            sb.append("A");
        }
        
        if ( aig.getCurrentInstance() == this ) {
            sb.append("C");
        }
        
        sb.append(">]");
        
        return sb.toString();
        
        
    }
    
    /* Special Debug Versions of addPropertyChangeListener */
    /* public void addPropertyChangeListener(String property, java.beans.PropertyChangeListener l) {
        super.addPropertyChangeListener(property, l);
        System.out.println("Ability.addPropertyChangeListener: adding PCL for prop \"" + property + "\", listener: " + l.getClass() + ".");
    }
     
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        super.addPropertyChangeListener(l);
        System.out.println("Ability.addPropertyChangeListener: adding PCL for all properties, listener: " + l.getClass() + ".");
    } */
    
//    private static class LimitationInfo implements Cloneable {
//        
//        protected Limitation limitation;
//        protected String name;
//        protected Object finalizer;
//        protected boolean addedByFramework;
//        protected String description;
//        protected double multiplier;
//        protected int priority;
//        protected boolean privateLimitation;
//        
//        public LimitationInfo(Limitation limitation) {
//            this.limitation = limitation;
//        }
//        
//        public LimitationInfo clone() {
//            LimitationInfo that = new LimitationInfo(limitation);
//            that.name = name;
//            that.finalizer = finalizer;
//            that.addedByFramework = addedByFramework;
//            that.description = description;
//            that.multiplier = multiplier;
//            that.priority = priority;
//            that.privateLimitation = privateLimitation;
//            
//            return that;
//        }
//        
//    }
    
    
    
    private class MySpecialEffectsIterator implements SpecialEffectIterator, Serializable {
        private Ability ability;
        
        private IndexIterator iterator;
        
        /**
         * @param ability  */
        public MySpecialEffectsIterator(Ability ability) {
            this.ability = ability;
            
            initializeIterator();
        }
        
        private void initializeIterator() {
            iterator = ability.getIteratorForIndex( "SpecialEffect" );
        }
        
        /**
         * Returns <tt>true</tt> if the iteration has more elements. (In other
         * words, returns <tt>true</tt> if <tt>next</tt> would return an element
         * rather than throwing an exception.)
         *
         * @return <tt>true</tt> if the iterator has more elements.
         */
        public boolean hasNext() {
            return iterator.hasNext();
        }
        
        /**
         * Returns the next element in the interation.
         *
         * @return the next element in the iteration.
         * @exception NoSuchElementException iteration has no more elements.
         */
        public Object next() {
            return nextSpecialEffect();
        }
        
        /** Returns the next Ability.
         */
        public SpecialEffect nextSpecialEffect() {
            int index = iterator.nextIndex();
            return (SpecialEffect)ability.getIndexedValue(index, "SpecialEffect", "SPECIALEFFECT");
        }
        
        /**
         *
         * Removes from the underlying collection the last element returned by the
         * iterator (optional operation).  This method can be called only once per
         * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
         * the underlying collection is modified while the iteration is in
         * progress in any way other than by calling this method.
         *
         * @exception UnsupportedOperationException if the <tt>remove</tt>
         * 		  operation is not supported by this Iterator.
         *
         * @exception IllegalStateException if the <tt>next</tt> method has not
         * 		  yet been called, or the <tt>remove</tt> method has already
         * 		  been called after the last call to the <tt>next</tt>
         * 		  method.
         */
        public void remove() {
            // First check to see if this a sublist member...
            throw new UnsupportedOperationException();
        }
        
    }
    
    /**
     */
    public class AbilityChangeUndoable implements Undoable, Serializable {
        private Ability ability;
        private int oldAmount;
        private int newAmount;
        
        /**
         * @param ability
         * @param oldAmount
         * @param newAmount  */
        public AbilityChangeUndoable(Ability ability, int oldAmount, int newAmount) {
            this.ability = ability;
            this.oldAmount = oldAmount;
            this.newAmount = newAmount;
        }
        
        /**
         */
        public void redo() {
            ability.setAdjustmentAmount(newAmount);
        }
        
        /**
         */
        public void undo() {
            ability.setAdjustmentAmount(oldAmount);
        }
        
        /**
         * @return  */
        public double getAdjustmentAmount() {
            return newAmount - oldAmount;
        }
    }
    
    /**
     */
    public class RemoveAbilityInstanceUndoable implements Undoable, Serializable {
        private AbilityInstanceGroup instanceGroup;
        private Ability ability;
        private boolean wasCurrentInstance;
        private Ability oldParent;
        
        /**
         * @param baseAbility
         * @param ability
         * @param wasCurrentInstance  */
        public RemoveAbilityInstanceUndoable(AbilityInstanceGroup instanceGroup, Ability ability, boolean wasCurrentInstance, Ability oldParent ) {
            this.instanceGroup = instanceGroup;
            this.ability = ability;
            this.wasCurrentInstance = wasCurrentInstance;
            this.oldParent = oldParent;
        }
        
        /**
         */
        public void redo() {
            ability.setInstanceGroup(null);
            ability.setParentAbility(null);
        }
        
        /**
         */
        public void undo() {
            if ( instanceGroup != null ) {
                
                ability.setParentAbility(oldParent);
                ability.setInstanceGroup(instanceGroup);
                
                if ( wasCurrentInstance ) {
                    instanceGroup.setCurrentInstance(ability);
                }
            }
        }
    }
    
//    /**
//     */
//    public class SetParentUndoable implements Undoable, Serializable {
//        private Ability oldParent;
//        private Ability newParent;
//        private Ability child;
//        
//        /**
//         * @param baseAbility
//         * @param ability
//         * @param wasCurrentInstance  */
//        public SetParentUndoable(Ability child, Ability oldParent, Ability newParent) {
//            this.child = child;
//            this.oldParent = oldParent;
//            this.newParent = newParent;
//        }
//        
//        /**
//         */
//        public void redo() {
//            int index;
//            child.setParentAbility(newParent);
//        }
//        
//        /**
//         */
//        public void undo() {
//            child.setParentAbility(oldParent);
//        }
//    }


    protected void setCPCost(int cpCost) {
        if ( this.cpCost != cpCost) {
            int oldValue = this.cpCost;
            this.cpCost = cpCost;
            firePropertyChange(this, "Ability.CPCOST", oldValue, cpCost);
        }
    }

    protected void setAPCost(int apCost) {
        if ( this.apCost != apCost) {
            int oldValue = this.apCost;
            this.apCost = apCost;
            firePropertyChange(this, "Ability.APCOST", oldValue, apCost);
        }
    }

  /*  protected int getRealCost() {
        return realCost;
    } */

    /** Sets the actual cost the ability cost the player.
     *
     * The actual cost is equal to the real cost in most cases.  However,
     * if the ability is in a framework, the actual cost may be the cost to have
     * the power within the framework.
     *
     * @param actualCost
     */

    protected void setRealCost(int realCost) {
        if ( this.realCost != realCost) {
            int oldValue = this.realCost;
            this.realCost = realCost;
            firePropertyChange(this, "Ability.REALCOST", oldValue, realCost);
        }
    } 

    public int getFixedCPCost() {
        return fixedCPCost;
    }

    public void setFixedCPCost(int fixedCPCost) {
        this.fixedCPCost = fixedCPCost;
    }

    public boolean isFixedCPEnabled() {
        return fixedCPEnabled;
    }

    public void setFixedCPEnabled(boolean fixedCPEnabled) {
        this.fixedCPEnabled = fixedCPEnabled;
    }

    protected void setPowerCost(int powerCost) {
        this.powerCost = powerCost;
    }

    protected double getAdvCost() {
        return advCost;
    }

    protected void setAdvCost(double advCost) {
        this.advCost = advCost;
    }

    protected double getLimCost() {
        return limCost;
    }

    protected void setLimCost(double limCost) {
        this.limCost = limCost;
    }

    protected double getAdvDCCost() {
        return advDCCost;
    }

    protected void setAdvDCCost(double advDCCost) {
        this.advDCCost = advDCCost;
    }


    protected void setENDCost(int endCost) {
        if ( this.endCost != endCost) {
            int oldValue = this.endCost;
            this.endCost = endCost;
            firePropertyChange(this, "Ability.ENDCOST", oldValue, endCost);
        }
    }
    
    /** Returns a Filter that accepts only acceptable targets.
     *
     */
    public Filter<Target> getTargetFilter() {
        Filter<Target> filter = null;
        
        filter = power.getTargetFilter(this);
        
        int index,count;
        count = getAdvantageCount() ;
        for ( index=0;index<count;index++ ) {
            Advantage a = (Advantage)getAdvantage(index);
            Filter<Target> t2 = a.getTargetFilter(this, index);
            if ( filter == null ) {
                filter = t2;
            }
            else if ( t2 != null ) {
                filter = new AndFilter<Target>(filter, t2);
            }
        }
        
        count = getLimitationCount() ;
        for ( index=0;index<count;index++ ) {
            Limitation l = getLimitation(index);
            Filter<Target> t2 = l.getTargetFilter(this, index);
            if ( filter == null ) {
                filter = t2;
            }
            else if ( t2 != null ) {
                filter = new AndFilter<Target>(filter, t2);
            }
        }
        
        return filter;
    }
    
    public ParameterList getPowerParameterList() {
        return powerParameterList;
    }

    public void setPowerParameterList(ParameterList powerParameterList) {
        this.powerParameterList = powerParameterList;
    }
    
//    protected static class AbilityActivationEntry implements Serializable {
//        protected ActivationInfo activationInfo;
//        protected Target source;
//
//        public AbilityActivationEntry(ActivationInfo activationInfo, Target source) {
//            this.activationInfo = activationInfo;
//            this.source = source;
//        }
//
//        public ActivationInfo getActivationInfo() {
//            return activationInfo;
//        }
//
//        public void setActivationInfo(ActivationInfo activationInfo) {
//            this.activationInfo = activationInfo;
//        }
//
//        public Target getSource() {
//            return source;
//        }
//
//        public void setSource(Target source) {
//            this.source = source;
//        }
//        
//        public boolean equals(Object that) {
//            return that != null && (this==that || this.activationInfo == that);
//        }
//    }
//    
//    protected class AbilityActivationsIterator implements Iterator<ActivationInfo> {
//
//        protected ActivationInfo next;
//        protected Target source;
//        protected Iterator<AbilityActivationEntry> iterator;
//        
//        public AbilityActivationsIterator() {
//            this(null);
//        }
//        
//        public AbilityActivationsIterator(Target source) {
//            if ( activations != null ) {
//                iterator = activations.iterator();
//            }
//            
//            this.source = source;
//        }
//        
//        public boolean hasNext() {
//            loadNext();
//            return next != null;
//        }
//
//        public ActivationInfo next() {
//            loadNext();
//            
//            ActivationInfo ai = next;
//            next = null;
//            
//            return ai;
//        }
//
//        public void remove() {
//            throw new UnsupportedOperationException();
//        }
//        
//        protected void loadNext() {
//            if ( next == null && iterator != null) {
//                while(iterator.hasNext()) {
//                    AbilityActivationEntry aae = iterator.next();
//                    if ( source == null || aae.getSource() == source ) {
//                        next = aae.getActivationInfo();
//                        break;
//                    }
//                }
//            }
//        }
//    }

    public boolean isWeapon() {
        return weapon;
    }

    public void setWeapon(boolean weapon) {
        
        
        if ( this.weapon != weapon ) {
            this.weapon = weapon;
            
            SpecialParameter sp = null;
            sp = findSpecialParameter(SpecialParameterWeapon.specialParameterName);
            if ( weapon == true && sp == null ) {
                sp = new SpecialParameterWeapon();
                addSpecialParameter( sp );
            }

            if ( sp != null ) {
                int index = findIndexed("SpecialParameter", "SPECIALPARAMETER", sp);
                ParameterList pl = sp.getParameterList(this, index);
                pl.setParameterValue("Weapon", weapon);
                sp.configure(this, pl);
            }
        
        }
    }



    
}