/*
 * ElementalControlFramework.java
 *
 * Created on June 10, 2004, 7:48 PM
 */

package champions;

import champions.exception.BattleEventException;
import champions.interfaces.Advantage;
import champions.interfaces.Limitation;
import champions.interfaces.PAD;
import champions.parameters.ParameterList;
import javax.swing.Icon;
import javax.swing.UIManager;

/**
 *
 * @author  1425
 */
public class ElementalControlAbility extends FrameworkAbility {
    static final long serialVersionUID = -1236071536350725953L;
    
    /** Creates a new instance of ElementalControlFramework */
    public ElementalControlAbility() {
        setFramework(new ElementalControlFramework(this) );
        setName("Elemental Control");
        addPAD( new powerElementControl(), null);
    }
    
    public ElementalControlAbility(boolean createFramework) {
        if ( createFramework ) setFramework( new ElementalControlFramework(this) );
        setName("Elemental Control");
        addPAD( new powerElementControl(), null);
        
    }
    
    /** Override addPAD to restrict the types of things that can be added.
     *
     * Elemental Control does not allow advantages applied to the base power.
     */
    public boolean addPAD(PAD pad, ParameterList pl) {
        if ( pad instanceof Advantage ) {
            return false;
        }
        
        boolean result = super.addPAD(pad, pl);
        
        return result;
    }
    
    /** Override the default removeLimitation to sync framework abilities.
     *
     */
    public void removeLimitation(int index) {
        super.removeLimitation(index);
        
        ElementalControlFramework fm = (ElementalControlFramework)getFramework();
        int count = fm.getFrameworkAbilityInstanceGroupCount();
        for(int i = 0; i < count; i++) {
            AbilityInstanceGroup aig = fm.getFrameworkAbilityInstanceGroup(i);
            Ability child = aig.getBaseInstance();
            fm.configureAbilityModifiers(child);
        }
    }
    
    /** Override this to force reconfigurations on framework abilities.
     *
     */
    public void reconfigureLimitation(Limitation lim, ParameterList parameterList, int index) {
        super.reconfigureLimitation(lim, parameterList, index);
        
        ElementalControlFramework fm = (ElementalControlFramework)getFramework();
        int count = fm.getFrameworkAbilityInstanceGroupCount();
        for(int i = 0; i < count; i++) {
            AbilityInstanceGroup aig = fm.getFrameworkAbilityInstanceGroup(i);
            Ability child = aig.getBaseInstance();
            child.reconfigureLimitation(lim, null, -1);
        }
    }
    
    /** Override to force FrameworkAbility's to override this themselves.
     *
     * This utility method should create a plain, unconfigured Ability object
     * of the same class as the original.  This object will then be configured
     * by clone or createChildInstance to create a new Ability.
     */
    protected Ability createAbilityObject(boolean createInstanceGroup) {
        return new ElementalControlAbility( false );
    }
    
    
    public static class powerElementControl extends Power  {
        static final long serialVersionUID = 5295848483348702301L;
    
        private static Object[][] parameterArray = {
            {"PoolSize","Power.POOLSIZE", Integer.class, new Integer(1), "Pool Size", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        };

        // Cost Array - See Power.getCostArray()
        private static Object[][] costArray = {
            { "PoolSize", GEOMETRIC_COST, DYNAMIC_RECONFIG, ALL_RECONFIG, new Integer(1), new Integer(1), new Integer(0), new Integer(0) },
       };

        // Import Patterns Definitions
        private static Object[][] patterns = {

        };

        // Known Caveats Array
        private static String[] caveats = {
            "None known."
        };

        // Power Definition Variables
        private static String powerName = "Elemental Control"; // The Name of the Power
        private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF"
        private static String persistenceType = "PERSISTENT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
        private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
        private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
        private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
        private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
        private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
        private static int endMultiplier = 0; // Indicates the END Multiplier for this power
        private static boolean dynamic = true;
        private static String description = "Elemental Control Framework";

        /** Creates new powerHandToHandAttack */
        public powerElementControl()  {
        }

        /* Returns an array which can be used to create the parameterList.
         */
        public Object[][] getParameterArray() {
            return parameterArray;
        }

        /** Get the English name of the PAD.
         * @return name of PAD
         */
        public String getName() {
            return powerName;
        }
        
        

        /** Configures the ability according to the parameters in parameterList.
         * The parameterList should be stored with the ability for configuration
         * later on. If an existing parameterList alread exists, it should be
         * replaced with this one.
         *
         * All value/pairs should be copied into the ability for direct access.
         */
        public boolean configurePAD(Ability ability, ParameterList parameterList) {
            // Fail immediately if ability is null
            if ( ability == null ) return false;

            // Always Set the ParameterList to the parameterList
            setParameterList(ability,parameterList);

            // Read in any parameters that will be needed to configure the power or
            // Determine the validity of the power configuration.  Read the parameters
            // from the parameterList, instead of directly from the ability, since the
            // Ability isn't configured yet.
            Integer poolsize = (Integer)parameterList.getParameterValue("PoolSize");

            // Check for the validity of the parameters that will be set.  If the parameters
            // Fail for any reason, return false from the method immediately, indicating a
            // failure to configure
            if ( poolsize.intValue() < 1 ) {
                return false;
            }

            // Always copy the configuration parameters directly into the ability.  This will
            // take the parameters stored in the parameter list and copy them into the
            // ability using the appropriate keys and values.
            parameterList.copyValues(ability);

            // Start to Actually Configure the Power.
            // The Add Power Info should always be executed to add information to the ability.
            // All of this information should be set in the Power Definition Variables at the
            // top of this file
            ability.addPowerInfo( this, powerName, targetType, persistenceType, activationTime);
            if ( attackType != null ) {
                ability.addAttackInfo( attackType,damageType );
                ability.add("Ability.PPDC", new Double(pointsPerDC), true);
            }
            ability.setGenerateDefaultEffects(generateDefaultDamage);
            if ( endMultiplier != 1 ) ability.setENDMultiplier(endMultiplier);

            // Add any dice information which is necessary to use this power.
            //  ability.addDiceInfo( "DamageDie", die, "Energy Blast Damage");

            // Add A Damage Class Info

            // Add Extra Value/Pairs used by the Power/BattleEngine
            ElementalControlFramework fm = (ElementalControlFramework)ability.getFramework();
            if ( fm != null && ability.isBaseInstance() ) {
                fm.setFrameworkPoolSize( poolsize.intValue() );
            }

            //remove if/ese when there is a better mechanism for spreading and beam limitation

            // Update the Ability Description based on the new configuration
            ability.setPowerDescription( getConfigSummary(ability, -1));

            // Return true to indicate success
            return true;
        }

        /** Returns the Character Power Cost of currently configured parameters.
         * Only the Power cost should be considered by this method.  Advantage/Limitation modifications will
         * be applied later.
         * @param ability Ability to calculate CP cost for.
         * @return integer representing Character Power Cost of the Power for Ability.
         */
     /*   public int calculateCPCost(Ability ability) {
            ParameterList pl = getParameterList(ability);
            String die = (String)pl.getParameterValue("DamageDie");
            try {
                Dice d = new Dice( die );
                return d.getD6() * 5;
            }
            catch (BadDiceException bde) {
                return 0;
            }
        } */

        public String getConfigSummary(Ability ability, int not_used) {
            ParameterList parameterList = getParameterList(ability);
            Integer poolsize = (Integer)parameterList.getParameterValue("PoolSize");


            return "Elemental Control, " + (poolsize.intValue() * 2) + "-point powers";
        }

        /** Attempt to identify power
         * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
         * determine the correct power to assign to it.
         */
        public int identifyPower(Ability template, AbilityImport ai) {
            String power = ai.getPowerName();

//            if ( power != null && ( power.equals( "ENERGYBLAST" ) || power.equals( "Energy Blast" ) )){
//                return 10;
//            }
            return 0;
        }
        
        public int calculateENDCost(Ability ability) {
            return 0;
        }
        
        public void shutdownPower(BattleEvent be, Target source) throws BattleEventException {
           ElementalControlFramework fm = (ElementalControlFramework)be.getAbility().getFramework();
           
           // Run through the abilities in the framework, shutting down any active ones.
           int count = fm.getFrameworkAbilityInstanceGroupCount();
           for(int i = 0; i < count; i++) {
               AbilityInstanceGroup aig = fm.getFrameworkAbilityInstanceGroup(i);
               aig.shutdownActivatedInstances(be, false);
           }
        }

        /** Returns the patterns necessary to import the Power from CW.
         * The Object[][] returned should be in the following format:
         * patterns = Object[][] {
         *  { "PATTERN" , new Object[] { "PARAMETER1", parameter1.class, "PARAMETER2", parameter2.class, ... },
         *  ...
         *  }
         *
         * PATTERN should be a regular expression pattern.  For every PARAMETER* where should be one
         * parathesis group in the expression. The PARAMETERS sub-array can be null, if the line has no parameter
         * and is just informational.
         *
         * By default, the importPower will check each line of the getImportPatterns() array and if a match is
         * found, the specified parameters will be set in the powers parameter list.  It is assumed that each
         * PATTERN will only occur once.  If the pattern can occur multiple times in a valid import, a custom
         * importPower method will have to be used.
         */

        public Object[][] getImportPatterns() {
            return patterns;
        }
        
        public Object[][] getCostArray(Ability ability) {
            return costArray;
        }

        /** Returns a Description of the Power
         */
        public String getDescription() {
            return description;
        }

        /** Returns whether power can be dynamcially reconfigured.
         */
        public boolean isDynamic() {
            return dynamic;
        }

        /**
         * Returns a String[] of Caveats about the Power
         * Power uses this method to automatically build the getCaveats()
         * String.  The Strings returns by getCaveatArray() will be assembled into
         * list form and returned via getCaveats().
         * 
         * Return an empty array if there are no known caveats for this power.
         */
        public String[] getCaveatArray() {
            return caveats;
        }
        
        public Icon getIcon() {
            return UIManager.getIcon("Framework.DefaultIcon");
        }
    }
    
}
