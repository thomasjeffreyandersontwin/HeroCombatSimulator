/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */
package champions.powers;

import champions.Ability;
import champions.AbilityAlias;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.LimitationAdapter;
import champions.ProfileTemplate;
import champions.Target;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.LimitationSkillRollNode;
import champions.interfaces.ChampionsConstants;
import champions.parameters.ComboParameter;
import champions.parameters.ParameterList;

/**
 *
 * @author  unknown
 * @version
 *
 * * To Convert from old format limitation, to new format limitation:
 *
 * 1) Add implements ChampionsConstants to class definition.<P>
 * 2) Copy and Fill in Limitation Definition Variables. <P>
 * 3) Move Parameter Information to parameterArray. <P>
 * 4) Delete getParameters method (unless special parameter handling is necessary.<P>
 * 5) Change configurePAD(Ability,DetailList) method to configurePAD(Ability ability, ParameterList parameterList).<P>
 * 6) Edit configurePAD method to use format specified below.<P>
 * 7) Change checkParameter method to checkParameter(Ability ability, <i>int padIndex</i>,
 * String key, Object value, Object oldValue);
 * 8) Edit getConfigSummary method to use parameterList instead of parseParameter methods.<P>
 * 9) Change all instances of parseParameter to getParameterValue.<P>
 * 10) Add getParameterArray method.<P>
 * 11) Add isUnique method.<P>
 * 12) Edit getName method to return limitationName variable.
 * 13) Add patterns array and define import patterns.<P>
 * 14) Add getImportPatterns() method.<P>
 */
public class limitationSkillRoll extends LimitationAdapter implements ChampionsConstants {

    static final long serialVersionUID = -6870520616682472305L;
    static private String[] apModOptions = {"No Active Point Penalty", "-1 per 5 Active Points", "-1 per 10 Active Points", "-1 per 20 Active Points"};
    static private double[] apModCosts = {-0.5, +0.5, 0, -0.25};
    static private int[] apModPerPoint = {Integer.MAX_VALUE, 5, 10, 20};
    // Parameter Definitions
    // Note: The second half of the key must be unique.  During copying, the first half is replaced by
    // "Advantage#" and only the second half is kept.
    static private Object[][] parameterArray = {
        //{"SkillRoll","Limitation#.LEVEL", Integer.class, new Integer(13), "Skill Roll", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Skill", "Limitation#.SKILL", Object.class, "None", "Skill", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"APModifier", "Limitation#.APMOD", String.class, "-1 per 10 Active Points", "Roll Modifier", COMBO_PARAMETER, VISIBLE, ENABLED, REQUIRED, "OPTIONS", apModOptions}
    };
    // Limitation Definition Variables
    public static String limitationName = "Requires a Skill Roll"; // The Name of the Limitation
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    // Import Patterns Definitions
    private static Object[][] patterns = { // { "Requires A Skill Roll \\(-([0-9]*)\\).*", new Object[] { "SkillRoll", Integer.class} },
    //HD
    // { "Requires A Skill Roll.*", new Object[] { "SkillRoll", Integer.class} },
    };

    /** Creates new advCombatModifier */
    public limitationSkillRoll() {
    }

    public String getName() {
        return limitationName;
    }

    public Object[][] getParameterArray() {
        return parameterArray;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        // Fail immediately if ability is null
        if (ability == null) {
            return false;
        }

        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.
        //Integer level = (Integer)parameterList.getParameterValue("SkillRoll");

        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure

        // !!!Limitation has nothing to validate!!!

        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        int index = ability.addLimitationInfo(this, limitationName, parameterList);

        // Copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.  Make sure that these
        // Value/Pairs are unique to the PAD.  Any that are not, should be copied
        // manually.
        // Note that if the advantage is not unique, the values should always be
        // accessed via the parameterList and not directly.
        //parameterList.copyValues(ability,index);

        // Add Extra Value/Pairs used by the Limitation/BattleEngine
        ability.reconfigurePower();

        // Update the Stored Description for this Limitation
        setDescription(getConfigSummary());

        // Return True to indicate success in configuringPAD
        return true;
    }

    public double calculateMultiplier() {
        ParameterList parameterList = getParameterList();
        Object o = parameterList.getParameterValue("Skill");
        String apOption = (String) parameterList.getParameterValue("APModifier");

        if (o == null || o instanceof AbilityAlias == false) {
            return 0;
        }

        Ability skill = ((AbilityAlias) o).getAliasReferent();

        if (skill == null) {
            return 0;
        }

        double total;

        if (skill instanceof BackgroundSkill) {
            total = 0.25;
        } else {
            total = 0.5;
        }

        for (int i = 0; i < apModOptions.length; i++) {
            if (apModOptions[i].equals(apOption)) {
                total += apModCosts[i];
                break;
            }
        }


        /*      if (levelToInt(level) == 1) {
        if (canburnout.equals("TRUE") ) {
        total = 1.5;
        } else total = 2 ;
        }
        else if (levelToInt(level) >1 && levelToInt(level) <7) {
        if (canburnout.equals("TRUE") ) {
        total = 2 - 0.25 * (levelToInt(level)+1 );
        } else  {
        total = 2 - 0.25 * (levelToInt(level) );
        }
        } else if (levelToInt(level) == 7 ) {
        if (canburnout.equals("TRUE") ) {
        total =0.25;
        } else total = 0.5 ;


        } else if (levelToInt(level) == 8 ) {
        if (canburnout.equals("TRUE") ) {
        total = 0;
        } else total = 0.25;
        }
        if (canjam.equals("TRUE") ) total = total + 0.50; */
        return -total;
    }

    public String getConfigSummary() {
        if (ability == null) {
            return "Error: Null Ability";
        }

        ParameterList parameterList = getParameterList();
        Object o = parameterList.getParameterValue("Skill");
        String apOption = (String) parameterList.getParameterValue("APModifier");

        if (o == null || o instanceof AbilityAlias == false) {
            return "Requires Skill Roll(Unconfigured)";
        }

        Ability skill = ((AbilityAlias) o).getAliasReferent();

        if (skill == null) {
            return "Requires Skill Roll(Unconfigured)";
        }

        String s;

        String apModString = "";

        if (apOption.equals(apModOptions[2]) == false) {
            apModString = ", " + apOption;
        }

        s = "Requires Skill Roll(" + skill.getName() + apModString + ")";

        return s;
    }

    public AttackTreeNode preactivate(BattleEvent be) {

        if (be.getActivationInfo().isActivated() == false) {
            //ParameterList parameterList = getParameterList();

            LimitationSkillRollNode node = new LimitationSkillRollNode("Skill Roll");

            node.setAbility(ability);
            node.setLimitationIndex(ability.findExactLimitation(this));

            return node;
        }

        return null;
    }

    @Override
    public void abilitySourceSet(Ability ability, Target oldSource, Target newSource) {
        ParameterList parameterList = getParameterList();
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
    public ParameterList getParameterList() {
        ParameterList pl = super.getParameterList();

        if (getAbility() != null) {
            ComboParameter p = (ComboParameter) pl.getParameter("Skill");
            if (p.getModel() == null) {
                p.setModel(new SkillComboBoxModel(getAbility().getSource()));
            }
        }
        return pl;
    }

    public int getSkillRoll(Ability abilityBeingActivated) {
        ParameterList pl = getParameterList();

        Object o = parameterList.getParameterValue("Skill");
        String apOption = (String) parameterList.getParameterValue("APModifier");

        if (o == null || o instanceof AbilityAlias == false) {
            return 18;
        }

        Ability skill = ((AbilityAlias) o).getAliasReferent();

        if (skill == null) {
            return 18;
        }

        int skillRoll = skill.getSkillRoll();

        int apPerPenalty = Integer.MAX_VALUE;
        for (int i = 0; i < apModOptions.length; i++) {
            if (apModOptions[i].equals(apOption)) {
                apPerPenalty = apModPerPoint[i];
                break;
            }
        }

        int apPenalty = ChampionsUtilities.roundValue(abilityBeingActivated.getAPCost() / apPerPenalty, false);

        return skillRoll - apPenalty;
    }

    /** Initializes the Limitation when it is first loaded by the Simulator.
     *
     * The <code>initialize</code> method is gauranteed to be called at least once prior to  
     * any use of the Limitation.  However, it is possible that the method would be called
     * multiple times.  If the initialization code can only be run one, the limitation should
     * track whether it has been initialized already.
     */
    public void initialize() {
        ProfileTemplate pt = ProfileTemplate.getDefaultProfileTemplate();

        pt.addOption("Limitation Skill Roll", "SHOW_SKILL_ROLL_PANEL",
                "This Description is located in the limitationSkillRoll.initialize() method.",
                "AttackTree.toHitIcon", null);
    }
}