/*
 * VariablePointPoolFramework.java
 *
 * Created on June 10, 2004, 7:48 PM
 */
package champions;

import champions.exception.BattleEventException;
import champions.interfaces.Framework;
import champions.interfaces.Framework.ReconfigurationMode;
import champions.interfaces.Limitation;
import champions.interfaces.PAD;
import champions.parameters.ComboParameter;
import champions.parameters.ParameterList;
import champions.powers.SkillComboBoxModel;
import champions.powers.advantageCosmic;
import champions.powers.advantageNoSkillRollRequired;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

/**
 *
 * @author  1425
 */
public class VariablePointPoolAbility extends FrameworkAbility {

    static final long serialVersionUID = -5433040536350725953L;
    static protected JMenu setModeMenu;
    static protected JMenuItem defaultMenu,  warningModeMenu,  implicitModeMenu,  explicitModeMenu;
    static protected SetFrameworkModeAction defaultAction,  warningModeAction,  implicitModeAction,  explicitModeAction;

    //   static protected AllocatePointsAction allocatePointsAction = new AllocatePointsAction();
    /** Creates a new instance of VariablePointPoolFramework */
    public VariablePointPoolAbility() {
        setFramework(new VariablePointPoolFramework(this));
        setName("Variable Point Pool");
        addPAD(new powerVariablePointPool(), null);
    }

    /** Creates a new instance of VariablePointPoolFramework */
    public VariablePointPoolAbility(boolean createFramework) {
        if (createFramework) {
            setFramework(new VariablePointPoolFramework(this));
        }
        setName("Variable Point Pool");
        addPAD(new powerVariablePointPool(), null);
    }

    /** Override addPAD to restrict the types of things that can be added.
     *
     * Multipower does not allow advantages applied to the base power.
     */
    public boolean addPAD(PAD pad, ParameterList pl) {
//        if ( pad instanceof Advantage && pad instanceof advantageGenericAdvantage == false) {
//            return false;
//        }

        boolean result = super.addPAD(pad, pl);

        return result;
    }

    /** Override the default removeLimitation to sync framework abilities.
     *
     */
    public void removeLimitation(int index) {
        super.removeLimitation(index);

        VariablePointPoolFramework fm = (VariablePointPoolFramework) getFramework();
        int count = fm.getFrameworkAbilityInstanceGroupCount();
        for (int i = 0; i < count; i++) {
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

        VariablePointPoolFramework fm = (VariablePointPoolFramework) getFramework();
        int count = fm.getFrameworkAbilityInstanceGroupCount();
        for (int i = 0; i < count; i++) {
            AbilityInstanceGroup aig = fm.getFrameworkAbilityInstanceGroup(i);
            Ability child = aig.getBaseInstance();
            int limIndex = child.findLimitation(lim);
            if (limIndex != -1) {
                child.reconfigureLimitation(child.getLimitation(limIndex), null, -1);
            }
        }
    }

    /** Override to return Multipower ability.
     *
     * This utility method should create a plain, unconfigured Ability object
     * of the same class as the original.  This object will then be configured
     * by clone or createChildInstance to create a new Ability.
     */
    protected Ability createAbilityObject(boolean createInstanceGroup) {
        return new VariablePointPoolAbility(false);
    }

    /** Returns whether the indicated ability is current active.
     *
     * Active abilities are abilities in the framework that are currently 
     * allowed to be used within the framework.  If an ability is not currently
     * active, it can not be used.
     *
     * The active state of an ability is stored indexed on the baseInstance of
     * an ability.
     */
    public boolean isAbilityConfigured(Ability ability) {
        return false;
    }

    /** Provides hook to add menu items to the Ability right-click menu
     * @param menu
     * @param ability
     * @return true if menus where actually added.
     */
    public boolean invokeMenu(JPopupMenu menu) {
        boolean added = false;

        if (setModeMenu == null) {
            setModeMenu = new JMenu("Set Framework Mode");

            defaultAction = new SetFrameworkModeAction("Simulator Default", ReconfigurationMode.DEFAULT_MODE);
            defaultMenu = new JCheckBoxMenuItem(defaultAction);
            setModeMenu.add(defaultMenu);

            warningModeAction = new SetFrameworkModeAction("Warning Only", ReconfigurationMode.WARNING_ONLY);
            warningModeMenu = new JCheckBoxMenuItem(warningModeAction);
            setModeMenu.add(warningModeMenu);

            implicitModeAction = new SetFrameworkModeAction("Implicit Reconfiguration", ReconfigurationMode.IMPLICIT_RECONFIG);
            implicitModeMenu = new JCheckBoxMenuItem(implicitModeAction);
            setModeMenu.add(implicitModeMenu);

            explicitModeAction = new SetFrameworkModeAction("Explicit Reconfiguration", ReconfigurationMode.EXPLICIT_RECONFIG);
            explicitModeMenu = new JCheckBoxMenuItem(explicitModeAction);
            setModeMenu.add(explicitModeMenu);
        }

        Framework fm = getFramework();

        defaultAction.setFramework(fm);
        warningModeAction.setFramework(fm);
        implicitModeAction.setFramework(fm);
        explicitModeAction.setFramework(fm);

        ReconfigurationMode mode = fm.getFrameworkMode();

        defaultMenu.setSelected(mode == ReconfigurationMode.DEFAULT_MODE);
        warningModeMenu.setSelected(mode == ReconfigurationMode.WARNING_ONLY);
        implicitModeMenu.setSelected(mode == ReconfigurationMode.IMPLICIT_RECONFIG);
        explicitModeMenu.setSelected(mode == ReconfigurationMode.EXPLICIT_RECONFIG);

        menu.add(setModeMenu);
        added = true;


        if (super.invokeMenu(menu) == true) {
            added = true;
        }

        return added;
    }

    public int calculateCPCost() {
        int base = 0;
        double adv = 0;
        double lim = 0;
        int cost = 0;
        int apcost = 0;

        int oldCP = cpCost;


        int poolSize = getPoolSize();
        // Since we know the power, we also know the cost ratio
        // In this case, the power cost is the size of the pool...
        Power p = getPower();
        base = (p == null ? 0 : p.calculateCPCost(this));

        // Store the power cost for reference.
        setPowerCost(ChampionsUtilities.roundValue(base, false));

        adv = getAdvCost();

        lim = getLimCost();

        cost = ChampionsUtilities.roundValue(poolSize + poolSize * 0.5 * (1 + adv) / (1 - lim), false);
        apcost = ChampionsUtilities.roundValue(poolSize + poolSize * 0.5 * (1 + adv), false);

        setAPCost(apcost);
        setCPCost(cost);
        setRealCost(cost);

        if (oldCP != cost) {
            calculateENDCost();
        }

        return cost;
    }

    @Override
    public Vector getActions(Vector v) {

        Framework f = getFramework();

        if (f.getFrameworkMode() == ReconfigurationMode.EXPLICIT_RECONFIG) {

            if (v == null) {
                v = new Vector();
            }

            Action a = new AllocatePointsAction(getSource(), f);
            v.add(a);
        }

        return v;
    }

    public int getPoolSize() {
        Power p = getPower();
        if (p == null) {
            return 0;
        }
        return (Integer) p.getParameterList(this).getParameterValue("PoolSize");
    }

    public static class powerVariablePointPool extends Power {

        static final long serialVersionUID = 5295848483348701301L;
        private static Object[][] parameterArray = {
            {"PoolSize", "Power.POOLSIZE", Integer.class, new Integer(1), "Pool Size", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
            {"Skill", "Power.SKILL", Object.class, null, "Skill", COMBO_PARAMETER, VISIBLE, ENABLED, REQUIRED},};

        // Cost Array - See Power.getCostArray()
        private static Object[][] costArray = {
            {"PoolSize", GEOMETRIC_COST, STATIC_RECONFIG, ALL_RECONFIG, new Integer(3), new Integer(2), new Integer(0), new Integer(0)},};

        // Import Patterns Definitions
        private static Object[][] patterns = {};

        // Known Caveats Array
        private static String[] caveats = {
            "None known."
        };

        // Power Definition Variables
        private static String powerName = "Variable Point Pool"; // The Name of the Power
        private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF"
        private static String persistenceType = "PERSISTENT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
        private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
        private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
        private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
        private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
        private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
        private static int endMultiplier = 0; // Indicates the END Multiplier for this power
        private static boolean dynamic = false;
        private static String description = "Variable Point Pool Framework";

        /** Creates new powerHandToHandAttack */
        public powerVariablePointPool() {
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
            if (ability == null) {
                return false;
            }

            // Always Set the ParameterList to the parameterList
            setParameterList(ability, parameterList);

            // Read in any parameters that will be needed to configure the power or
            // Determine the validity of the power configuration.  Read the parameters
            // from the parameterList, instead of directly from the ability, since the
            // Ability isn't configured yet.
            Integer poolsize = (Integer) parameterList.getParameterValue("PoolSize");

            // Check for the validity of the parameters that will be set.  If the parameters
            // Fail for any reason, return false from the method immediately, indicating a
            // failure to configure
            if (poolsize.intValue() < 1) {
                return false;
            }

            if ( ability.hasAdvantage(advantageCosmic.advantageName) || ability.hasAdvantage(advantageNoSkillRollRequired.advantageName) ) {
                parameterList.setVisible("Skill", false);
            }
            else {
                parameterList.setVisible("Skill", true);
            }

            // Always copy the configuration parameters directly into the ability.  This will
            // take the parameters stored in the parameter list and copy them into the
            // ability using the appropriate keys and values.
            parameterList.copyValues(ability);

            // Start to Actually Configure the Power.
            // The Add Power Info should always be executed to add information to the ability.
            // All of this information should be set in the Power Definition Variables at the
            // top of this file
            ability.addPowerInfo(this, powerName, targetType, persistenceType, activationTime);
            if (attackType != null) {
                ability.addAttackInfo(attackType, damageType);
                ability.add("Ability.PPDC", new Double(pointsPerDC), true);
            }
            ability.setGenerateDefaultEffects(generateDefaultDamage);
            if (endMultiplier != 1) {
                ability.setENDMultiplier(endMultiplier);
            }

            // Add any dice information which is necessary to use this power.
            //  ability.addDiceInfo( "DamageDie", die, "Energy Blast Damage");

            // Add A Damage Class Info

            // Add Extra Value/Pairs used by the Power/BattleEngine
            VariablePointPoolFramework fm = (VariablePointPoolFramework) ability.getFramework();
            if (fm != null && ability.isBaseInstance()) {
                fm.setFrameworkPoolSize(poolsize.intValue());
            }

         

            // Update the Ability Description based on the new configuration
            ability.setPowerDescription(getConfigSummary(ability, -1));

            // Return true to indicate success
            return true;
        }

        /** Returns the Character Power Cost of currently configured parameters.
         * Only the Power cost should be considered by this method.  Advantage/Limitation modifications will
         * be applied later.
         * @param ability Ability to calculate CP cost for.
         * @return integer representing Character Power Cost of the Power for Ability.
         */
//        public int calculateCPCost(Ability ability) {
//            
//        } 
        public String getConfigSummary(Ability ability, int not_used) {
            ParameterList parameterList = getParameterList(ability);
            Integer poolsize = (Integer) parameterList.getParameterValue("PoolSize");


            return "Variable Point Pool, " + poolsize.intValue() + " point pool";
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
            VariablePointPoolFramework fm = (VariablePointPoolFramework) be.getAbility().getFramework();

            // Run through the abilities in the framework, shutting down any active ones.
            int count = fm.getFrameworkAbilityInstanceGroupCount();
            for (int i = 0; i < count; i++) {
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

        @Override
        public void abilitySourceSet(Ability ability, Target oldSource, Target newSource) {
            ParameterList parameterList = getParameterList(ability);
            Object o = parameterList.getParameterValue("Skill");

            if (o instanceof AbilityAlias) {

                Target target = ((AbilityAlias) o).getTarget();

                if (target != null && newSource != target) {
                    parameterList.setParameterValue("Skill", "None");
                } else {
                    String targetName = ((AbilityAlias) o).getTargetName();
                    if (newSource == null || targetName == null || targetName.equals(newSource.getName()) == false) {
                        parameterList.setParameterValue("Skill", "None");
                    }
                }
            }

            ComboParameter p = (ComboParameter) parameterList.getParameter("Skill");

            SkillComboBoxModel oldModel = (SkillComboBoxModel) p.getModel();

            p.setModel(new SkillComboBoxModel(newSource));

            if (oldModel != null) {
                oldModel.setTarget(null);
            }
        }

        @Override
        public ParameterList getParameterList(Ability ability) {
            ParameterList pl = super.getParameterList(ability);

            if (ability != null) {
                ComboParameter p = (ComboParameter) pl.getParameter("Skill");
                if (p.getModel() == null) {
                    p.setModel(new SkillComboBoxModel(ability.getSource()));
                }
            }
            return pl;
        }
    }

    public static class SetFrameworkModeAction extends AbstractAction {

        private Framework framework;
        private ReconfigurationMode mode;

        public SetFrameworkModeAction(String name, ReconfigurationMode mode) {
            super(name);
            this.mode = mode;
        }

        public void actionPerformed(ActionEvent e) {
            if (framework != null) {
                framework.setFrameworkMode(mode);

                if (Battle.currentBattle != null) {
                    Battle.currentBattle.triggerChangeEvent();
                }
            }
        }

        /**
         * Getter for property framework.
         * @return Value of property framework.
         */
        public Framework getFramework() {
            return framework;
        }

        /**
         * Setter for property framework.
         * @param framework New value of property framework.
         */
        public void setFramework(Framework framework) {
            this.framework = framework;
        }
    }

    public static class AllocatePointsAction extends AbstractAction {

        private Target source;
        private Framework framework;

        public AllocatePointsAction(Target source, Framework framework) {
            super("Allocate Pool");
            this.source = source;
            this.framework = framework;
        }

        public void actionPerformed(ActionEvent e) {
            ConfigureFrameworkBattleEvent be = new ConfigureFrameworkBattleEvent(source, framework);
            Battle.currentBattle.addEvent(be);
        }

        /**
         * Getter for property framework.
         * @return Value of property framework.
         */
        public Framework getFramework() {
            return framework;
        }

        /**
         * Setter for property framework.
         * @param framework New value of property framework.
         */
        public void setFramework(Framework framework) {
            this.framework = framework;
        }
    }
}
