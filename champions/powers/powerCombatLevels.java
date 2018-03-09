/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */
package champions.powers;

import champions.Ability;
import champions.AbilityListModel;
import champions.BattleEvent;
import champions.DetailList;
import champions.Effect;
import champions.PADDialog;
import champions.Power;
import champions.Target;
import champions.event.PADValueEvent;
import champions.exception.BattleEventException;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.PADValueListener;
import champions.parameters.ListParameter;
import champions.parameters.Parameter;
import champions.parameters.ParameterList;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;


public class powerCombatLevels extends Power implements ChampionsConstants {

    static final long serialVersionUID = 5295848683348607403L;
    static private String[] whenArray = {
        "Overall (Always Active)",
        "Overall (Only when Doing Something)",
        "Normal (According To Ability List)"
    };
    static private String[] identifyArray = {
        // "Single Weapon Attack",
        "Single Attack",
        // "Three Maneuvers",
        "Tight Group",
        "Martial Arts",
        "HTH Combat",
        // "Hand-To-Hand Combat",
        "Ranged Combat",
        "HTH and Ranged Combat",
        //  "DCV vs. All Attacks",
        "DCV Against All Attacks",
        "All Combat",};
    static private String[] attackTypeArray = {
        "Melee",
        "Ranged",};
    static private String[] senseGroupArray = {
        "None", "All Sight", "All Hearing", "All Radio", "All Smell", "All Unusual", "All Mental", "Hearing Group"
    };
    static private Object[][] parameterArray = {
        {"Level", "Power.LEVELS", Integer.class, new Integer(1), "Levels", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"LevelType", "Power.LEVELTYPE", String.class, "Single Attack", "Combat Level Type", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", identifyArray},
        {"AttackType", "Power.ATTACKTYPE", String.class, "Melee", "Attack Type", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", attackTypeArray},
        {"Overall", "Power.OVERALL", String.class, "Normal (According To Ability List)", "Type", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", whenArray},
        {"CanUseCL", "CanUseCL*.ABILITY", String.class, null, "Abilities Combat Levels are active for", LIST_PARAMETER, VISIBLE, ENABLED, REQUIRED},
        {"OCVLevel", "Ability.ASSIGNEDOCV", Integer.class, new Integer(0), "Assigned OCV levels", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"DCVLevel", "Ability.ASSIGNEDDCV", Integer.class, new Integer(0), "Assigned DCV levels ", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"DCLevel", "CombatLevel.ASSIGNEDDC", Integer.class, new Integer(0), "Assigned DC levels", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},};
    // Power Definition Variables
    private static String powerName = "Combat Levels"; // The Name of the Power
    private static String targetType = "SELF"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "PERSISTENT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "INSTANT"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = -1; // The Point cost of a single Damage Class of this power.
    private static String attackType = null; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = null; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power
    // Import Patterns Definitions
    private static Object[][] patterns = {
        //        { "\\+([0-9]*) (level w/.*)", new Object[] { "Level", Integer.class, "AbilityName", String.class}}
        {"\\+([0-9]*) level w/.*", new Object[]{"Level", Integer.class}},
        {"\\+([0-9]*) Level w/.*", new Object[]{"Level", Integer.class}},
        {"\\+([0-9]*) Combat with .*", new Object[]{"Level", Integer.class}},
        {"CLs assigned to OCV: (.*)", new Object[]{"OCVLevel", Integer.class}},
        {"CLs assigned to DCV: (.*)", new Object[]{"DCVLevel", Integer.class}},
        {"CLs assigned to DC: (.*)", new Object[]{"DCLevel", Integer.class}},
        {"Assigned to OCV: (.*)", new Object[]{"OCVLevel", Integer.class}},
        {"Assigned to DCV: (.*)", new Object[]{"DCVLevel", Integer.class}},
        {"Assigned to DC: (.*)", new Object[]{"DCLevel", Integer.class}},
        {"Combat Level Type: (.*)", new Object[]{"Overall", String.class}},
        {"\\+([0-9]*) (with any single attack)", new Object[]{"Level", Integer.class, "LevelType", String.class}},
        {"\\+([0-9]*) (with any three maneuvers or a tight group of attacks)", new Object[]{"Level", Integer.class, "LevelType", String.class}},
        {"\\+([0-9]*) (with HTH Combat)", new Object[]{"Level", Integer.class, "LevelType", String.class}},
        {"\\+([0-9]*) (with Ranged Combat)", new Object[]{"Level", Integer.class, "LevelType", String.class}},
        {"\\+([0-9]*) (with DCV)", new Object[]{"Level", Integer.class, "LevelType", String.class}},
        {"\\+([0-9]*) (with All Combat)", new Object[]{"Level", Integer.class, "LevelType", String.class}},
        //HeroDesigner (import pattern for levels -kjr)
        {"LEVELS: ([0-9]*)", new Object[]{"Level", Integer.class}},
        {"(with any single attack with one specific weapon)", new Object[]{"LevelType", String.class}},
        {"(with any single attack)", new Object[]{"LevelType", String.class}},
        {"(with any single Strike)", new Object[]{"LevelType", String.class}},
        {"(with any Strike)", new Object[]{"LevelType", String.class}},
        {"(with Martial Maneuvers)", new Object[]{"LevelType", String.class}},
        {"(with Magic)", new Object[]{"LevelType", String.class}},
        {"(with any three maneuvers or a tight group of attacks)", new Object[]{"LevelType", String.class}},
        {"(with HTH Combat)", new Object[]{"LevelType", String.class}},
        {"(with Ranged Combat)", new Object[]{"LevelType", String.class}},
        {"(with DCV)", new Object[]{"LevelType", String.class}},
        {"(with All Combat)", new Object[]{"LevelType", String.class}},
        {"ROLL:.*", null},};

    public powerCombatLevels() {
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
        

        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.
        Integer level = (Integer) parameterList.getParameterValue("Level");
        String overall = (String) parameterList.getParameterValue("Overall");
        String leveltype = (String) parameterList.getParameterValue("LevelType");
        Integer ocv = (Integer) parameterList.getParameterValue("OCVLevel");
        Integer dcv = (Integer) parameterList.getParameterValue("DCVLevel");
        Integer dc = (Integer) parameterList.getParameterValue("DCLevel");

        int  lvl = (int) parameterList.getParameterValue("Level");
        if(ocv+dcv+dc < lvl)
        {
        	int delta = lvl -(ocv+dcv+dc);
        	ocv+=delta;
        }
        parameterList.setParameterValue("OCVLevel", ocv);
        parameterList.setParameterValue("DCVLevel", dcv);
        parameterList.setParameterValue("DCLevel", new Integer(dc));

       // updateLevelType(ability, parameterList);
        setParameterList(ability, parameterList);
        
        String name = ability.getName();
        if (!ability.getName().startsWith("CSL: ") && !ability.getName().startsWith("Combat Skill Levels")) {
            ability.setName("CSL: " + name);
        }


        //        ability.add("Ability.NAME", level + " Levels w/ " + name , true);

        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure

        updateLevelType(ability, parameterList);
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

        // Update the Ability Description based on the new configuration
        ability.setPowerDescription(getConfigSummary(ability, -1));

        
   //      
         
      //   ability.add("Ability.ASSIGNEDOCV", 0, true);
       //  ability.add("Ability.ASSIGNEDDCV", lvl, true);
        //	 ability.add("CombatLevel.ASSIGNEDDC", 0, true);
   
        // Return true to indicate success
        return true;
    }

    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        Integer level = (Integer) parameterList.getParameterValue("Level");
        String overall = (String) parameterList.getParameterValue("Overall");
        String leveltype = (String) parameterList.getParameterValue("LevelType");

        if (leveltype.equals("Single Attack")) {
            String canUseList = getCanUseCLString(parameterList);
            return "Combat Levels (+" + level + " with Single Attack: " + canUseList + ")";
        } else if (leveltype.equals("Tight Group")) {
            String canUseList = getCanUseCLString(parameterList);
            return "Combat Levels (+" + level + " with Tight Group: " + canUseList + ")";
        } else if (leveltype.equals("Martial Arts")) {
            String canUseList = getCanUseCLString(parameterList);
            return "Combat Levels (+" + level + " with Tight Group: " + canUseList + ")";
        } else if (leveltype.equals("HTH Combat")) {
            //String canUseList = getCanUseCLString(parameterList);
            return "Combat Levels (+" + level + " with Hand-to-Hand)";
        }
        else if (leveltype.equals("HTH Combat")) {
            //String canUseList = getCanUseCLString(parameterList);
            return "Combat Levels (+" + level + " with Ranged and Hand-to-Hand)" ;
        }
       else if (leveltype.equals("Ranged Combat")) {
            return "Combat Levels (+" + level + " with Ranged)";
        } else if (leveltype.equals("DCV Against All Attacks")) {
            return "Combat Levels (+" + level + " DCV Against All Attacks)";
        } else if (leveltype.equals("All Combat")) {
            return "Combat Levels (+" + level + " All Combat)";
        }

        return "Combat Levels (CONFIGURATION ERROR)";
    }

    protected String getCanUseCLString(ParameterList pl) {
        StringBuffer sb = new StringBuffer();
        int count = pl.getIndexedParameterSize("CanUseCL");
        for (int i = 0; i < count; i++) {
            Ability a = (Ability) pl.getIndexedParameterValue("CanUseCL", i);
            sb.append(a.getName());
            if (i + 1 < count) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public void triggerPower(BattleEvent be, Ability ability, DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        Effect effect = new effectCombatLevel(ability.getName(), ability);

        effectList.createIndexed("Effect", "EFFECT", effect);
    }

    public void updateLevelType(Ability ability, ParameterList parameterList) {
        String leveltype = (String) parameterList.getParameterValue("LevelType");
        int levels = ((Integer) parameterList.getParameterValue("Level")).intValue();
        int ocv = ((Integer) parameterList.getParameterValue("OCVLevel")).intValue();
        int dcv = ((Integer) parameterList.getParameterValue("DCVLevel")).intValue();
        int dc = ((Integer) parameterList.getParameterValue("DCLevel")).intValue();

        int assignedLevels = ocv + dcv + dc * 2;

        if (leveltype.equals("Single Attack") || leveltype.equals("Single Weapon Attack")) {
            parameterList.setVisible("AttackType", false);
            parameterList.setVisible("CanUseCL", true);
            parameterList.setVisible("DCVLevel", false);
            parameterList.setVisible("OCVLevel", false);
            parameterList.setVisible("DCLevel", false);

            parameterList.setParameterValue("OCVLevel", new Integer(levels));
            parameterList.setParameterValue("DCVLevel", new Integer(0));
            parameterList.setParameterValue("DCLevel", new Integer(0));
        } else if (leveltype.equals("Tight Group") || leveltype.equals("Group of Attacks") || leveltype.equals("Three Maneuvers")) {
            parameterList.setVisible("AttackType", false);
            parameterList.setVisible("CanUseCL", true);

            parameterList.setVisible("DCVLevel", true);
            parameterList.setVisible("OCVLevel", true);
            parameterList.setVisible("DCLevel", true);

            if (levels != assignedLevels) {
                while (levels < assignedLevels) {
                    if (dc > 0) {
                        dc--;
                    } else if (dcv > 0) {
                        dcv--;
                    } else if (ocv > 0) {
                        ocv--;
                    }
                    assignedLevels = ocv + dcv + dc * 2;
                }

                parameterList.setParameterValue("OCVLevel", new Integer(ocv));
                parameterList.setParameterValue("DCVLevel", new Integer(dcv));
                parameterList.setParameterValue("DCLevel", new Integer(dc));
            }
        } else if (leveltype.equals("Martial Arts")) {
            parameterList.setVisible("AttackType", false);
            parameterList.setVisible("CanUseCL", false);

            parameterList.setVisible("DCVLevel", true);
            parameterList.setVisible("OCVLevel", true);
            parameterList.setVisible("DCLevel", true);

            if (levels != assignedLevels) {
                while (levels < assignedLevels) {
                    if (dc > 0) {
                        dc--;
                    } else if (dcv > 0) {
                        dcv--;
                    } else if (ocv > 0) {
                        ocv--;
                    }
                    assignedLevels = ocv + dcv + dc * 2;
                }

                parameterList.setParameterValue("OCVLevel", new Integer(ocv));
                parameterList.setParameterValue("DCVLevel", new Integer(dcv));
                parameterList.setParameterValue("DCLevel", new Integer(dc));
            }
        } else if (leveltype.equals("HTH Combat") || leveltype.equals("Hand-To-Hand Combat")) {
            parameterList.setVisible("AttackType", false);
            parameterList.setVisible("CanUseCL", false);
            parameterList.setParameterValue("AttackType", "Melee");

            parameterList.setVisible("DCVLevel", true);
            parameterList.setVisible("OCVLevel", true);
            parameterList.setVisible("DCLevel", true);

            if (levels != assignedLevels) {
                while (levels < assignedLevels) {
                    if (dc > 0) {
                        dc--;
                    } else if (dcv > 0) {
                        dcv--;
                    } else if (ocv > 0) {
                        ocv--;
                    }
                    assignedLevels = ocv + dcv + dc * 2;
                }

                parameterList.setParameterValue("OCVLevel", new Integer(ocv));
                parameterList.setParameterValue("DCVLevel", new Integer(dcv));
                parameterList.setParameterValue("DCLevel", new Integer(dc));
            }
        } else if (leveltype.equals("HTH and Ranged Combat")) {
            parameterList.setVisible("AttackType", false);
            parameterList.setVisible("CanUseCL", false);
            
            parameterList.setParameterValue("AttackType", "Melee");

            parameterList.setVisible("DCVLevel", true);
            parameterList.setVisible("OCVLevel", true);
            parameterList.setVisible("DCLevel", true);

            if (levels != assignedLevels) {
                while (levels < assignedLevels) {
                    if (dc > 0) {
                        dc--;
                    } else if (dcv > 0) {
                        dcv--;
                    } else if (ocv > 0) {
                        ocv--;
                    }
                    assignedLevels = ocv + dcv + dc * 2;
                }

                parameterList.setParameterValue("OCVLevel", new Integer(ocv));
                parameterList.setParameterValue("DCVLevel", new Integer(dcv));
                parameterList.setParameterValue("DCLevel", new Integer(dc));
            }
        }else if (leveltype.equals("Ranged Combat")) {
            parameterList.setVisible("AttackType", false);
            parameterList.setVisible("CanUseCL", false);
            parameterList.setParameterValue("AttackType", "Ranged");

            parameterList.setVisible("DCVLevel", true);
            parameterList.setVisible("OCVLevel", true);
            parameterList.setVisible("DCLevel", true);

            if (levels != assignedLevels) {
                while (levels < assignedLevels) {
                    if (dc > 0) {
                        dc--;
                    } else if (dcv > 0) {
                        dcv--;
                    } else if (ocv > 0) {
                        ocv--;
                    }
                    assignedLevels = ocv + dcv + dc * 2;
                }

                parameterList.setParameterValue("OCVLevel", new Integer(ocv));
                parameterList.setParameterValue("DCVLevel", new Integer(dcv));
                parameterList.setParameterValue("DCLevel", new Integer(dc));
            }
        } else if (leveltype.equals("DCV vs. All Attacks") || leveltype.equals("DCV Against All Attacks")) {
            parameterList.setVisible("AttackType", false);
            parameterList.setVisible("CanUseCL", false);

            parameterList.setVisible("DCVLevel", false);
            parameterList.setVisible("OCVLevel", false);
            parameterList.setVisible("DCLevel", false);

            parameterList.setParameterValue("OCVLevel", new Integer(0));
            parameterList.setParameterValue("DCVLevel", new Integer(levels));
            parameterList.setParameterValue("DCLevel", new Integer(0));
        } else if (leveltype.equals("All Combat")) {
            parameterList.setVisible("AttackType", false);
            parameterList.setVisible("CanUseCL", false);

            parameterList.setVisible("DCVLevel", true);
            parameterList.setVisible("OCVLevel", true);
            parameterList.setVisible("DCLevel", true);

            if (levels != assignedLevels) {
                while (levels < assignedLevels) {
                    if (dc > 0) {
                        dc--;
                    } else if (dcv > 0) {
                        dcv--;
                    } else if (ocv > 0) {
                        ocv--;
                    }
                    assignedLevels = ocv + dcv + dc * 2;
                }

                parameterList.setParameterValue("OCVLevel", new Integer(ocv));
                parameterList.setParameterValue("DCVLevel", new Integer(dcv));
                parameterList.setParameterValue("DCLevel", new Integer(dc));
            }
        }

    }

    public int calculateCPCost(Ability ability) {
        ParameterList parameterList = getParameterList(ability);
        Integer level = (Integer) parameterList.getParameterValue("Level");
        String overall = (String) parameterList.getParameterValue("Overall");
        String leveltype = (String) parameterList.getParameterValue("LevelType");

        if (leveltype.equals("Single Weapon Attack")) {
            return level.intValue() * 1;
        } else if (leveltype.equals("Single Attack")) {
            return level.intValue() * 2;
        } else if (leveltype.equals("3 Maneuvers")) {
            return level.intValue() * 3;
        } else if (leveltype.equals("Three Maneuvers")) {
            return level.intValue() * 3;
        } else if (leveltype.equals("Tight Group")) {
            return level.intValue() * 3;
        } else if (leveltype.equals("HTH Combat")) {
            return level.intValue() * 5;
        } else if (leveltype.equals("Ranged Combat")) {
            return level.intValue() * 5;
        } else if (leveltype.equals("Group of Attacks")) {
            return level.intValue() * 5;
        } else if (leveltype.equals("DCV Against All Attacks")) {
            return level.intValue() * 5;
        } else if (leveltype.equals("All Combat")) {
            return level.intValue() * 8;
        }
        return 0;
    }

    // Special Handling is necessary to configure the ability.
    public ParameterList getParameterList(Ability ability, int not_used) {
        ParameterList pl = super.getParameterList(ability, not_used);
        ListParameter param = (ListParameter) pl.getParameter("CanUseCL");
        if (param.getModel() == null) {
            param.setModel(new AbilityListModel((ability.getSource() == null ? null : ability.getSource().getAbilityList())));
        }
        return pl;
    }

    public void abilitySourceSet(Ability ability, Target oldSource, Target newSource) {
        ParameterList pl = super.getParameterList(ability, -1);
        ListParameter param = (ListParameter) pl.getParameter("CanUseCL");
        param.setModel(new AbilityListModel((newSource == null ? null : newSource.getAbilityList())));

    }

    public boolean invokeMenu(JPopupMenu popup, final Ability ability) {
        Action assignAction = new AbstractAction("Adjust Levels for " + ability.getName()) {

            public void actionPerformed(ActionEvent e) {
                final Ability currentAbility = ability.getInstanceGroup().getCurrentInstance();
                
                
                Integer ocv = currentAbility.getIntegerValue("Ability.ASSIGNEDOCV");
                Integer dcv = currentAbility.getIntegerValue("Ability.ASSIGNEDDCV");
                Integer dc = currentAbility.getIntegerValue("CombatLevel.ASSIGNEDDC");

                ParameterList parameterList = getParameterList(ability);
                final Integer level = (Integer) parameterList.getParameterValue("Level");

                ParameterList pl = new ParameterList();
                pl.addIntegerParameter("OCV", "Ability.ASSIGNEDOCV", "OCV Levels", ocv);
                pl.addIntegerParameter("DCV", "Ability.ASSIGNEDDCV", "DCV Levels", dcv);
                pl.addIntegerParameter("DC", "CombatLevel.ASSIGNEDDC", "Damage Classes", dc);

                PADDialog pd = new PADDialog(null);
                PADValueListener pvl = new PADValueListener() {

                    public void PADValueChanged(PADValueEvent evt) {
                    }

                    public boolean PADValueChanging(PADValueEvent evt) {
                        Integer nocv = currentAbility.getIntegerValue("Ability.ASSIGNEDOCV");
                        Integer ndcv = currentAbility.getIntegerValue("Ability.ASSIGNEDDCV");
                        Integer ndc = currentAbility.getIntegerValue("CombatLevel.ASSIGNEDDC");
                        String leveltype = currentAbility.getStringValue("Power.LEVELTYPE");

                        if (evt.getKey().equals("Ability.ASSIGNEDOCV")) {
                            nocv = (Integer) evt.getValue();
                        } else if (evt.getKey().equals("Ability.ASSIGNEDDCV")) {
                            ndcv = (Integer) evt.getValue();
                        } else if (evt.getKey().equals("CombatLevel.ASSIGNEDDC")) {
                            ndc = (Integer) evt.getValue();
                        }

                        if (nocv == null) {
                            nocv = new Integer(0);
                        }
                        if (ndcv == null) {
                            ndcv = new Integer(0);
                        }
                        if (ndc == null) {
                            ndc = new Integer(0);
                        }

                        if (leveltype.equals("Single Attack")) {
                            if (ndcv.intValue() > 0 || ndc.intValue() > 0) {
                                return false;
                            }
                        }

                        return nocv.intValue() + ndcv.intValue() + ndc.intValue() * 2 <= level.intValue();
                    }
                };
                int result = pd.showPADDialog("Adjust Levels", pl, currentAbility, pvl);

                if (result == JOptionPane.CANCEL_OPTION) {
                    currentAbility.add("Ability.ASSIGNEDOCV", ocv, true);
                    currentAbility.add("Ability.ASSIGNEDDCV", dcv, true);
                    currentAbility.add("CombatLevel.ASSIGNEDDC", dc, true);
                }
            }
        };

        popup.add(assignAction);
        return false;
    }

    public void addActions(Vector v, final Ability ability) {
        Action assignAction = new AbstractAction("Adjust Levels for " + ability.getName()) {

            public void actionPerformed(ActionEvent e) {
                final Ability currentAbility = ability.getInstanceGroup().getCurrentInstance();
                Integer ocv = currentAbility.getIntegerValue("Ability.ASSIGNEDOCV");
                Integer dcv = currentAbility.getIntegerValue("Ability.ASSIGNEDDCV");
                Integer dc = currentAbility.getIntegerValue("CombatLevel.ASSIGNEDDC");

                ParameterList parameterList = getParameterList(ability);
                final Integer level = (Integer) parameterList.getParameterValue("Level");

                ParameterList pl = new ParameterList();
                pl.addIntegerParameter("OCV", "Ability.ASSIGNEDOCV", "OCV Levels", ocv);
                pl.addIntegerParameter("DCV", "Ability.ASSIGNEDDCV", "DCV Levels", dcv);
                pl.addIntegerParameter("DC", "CombatLevel.ASSIGNEDDC", "Damage Classes", dc);

                PADDialog pd = new PADDialog(null);
                PADValueListener pvl = new PADValueListener() {

                    public void PADValueChanged(PADValueEvent evt) {
                        ParameterList pl = currentAbility.getPower().getParameterList(currentAbility, -1);

                        if (evt.getKey().equals("Ability.ASSIGNEDOCV")) {
                            Integer value = (Integer) evt.getValue();
                            pl.setParameterValue("OCVLevel", value);
                            currentAbility.reconfigurePower();
                        } else if (evt.getKey().equals("Ability.ASSIGNEDDCV")) {
                            Integer value = (Integer) evt.getValue();
                            pl.setParameterValue("DCVLevel", value);
                            currentAbility.reconfigurePower();
                        } else if (evt.getKey().equals("CombatLevel.ASSIGNEDDC")) {
                            Integer value = (Integer) evt.getValue();
                            pl.setParameterValue("DCLevel", value);
                            currentAbility.reconfigurePower();
                        }
                    }

                    public boolean PADValueChanging(PADValueEvent evt) {
                        Integer nocv = currentAbility.getIntegerValue("Ability.ASSIGNEDOCV");
                        Integer ndcv = currentAbility.getIntegerValue("Ability.ASSIGNEDDCV");
                        Integer ndc = currentAbility.getIntegerValue("CombatLevel.ASSIGNEDDC");
                        String leveltype = currentAbility.getStringValue("Power.LEVELTYPE");
                        if (evt.getKey().equals("Ability.ASSIGNEDOCV")) {
                            nocv = (Integer) evt.getValue();
                        } else if (evt.getKey().equals("Ability.ASSIGNEDDCV")) {
                            ndcv = (Integer) evt.getValue();
                        } else if (evt.getKey().equals("CombatLevel.ASSIGNEDDC")) {
                            ndc = (Integer) evt.getValue();
                        }


                        if (nocv == null) {
                            nocv = new Integer(0);
                        }
                        if (ndcv == null) {
                            ndcv = new Integer(0);
                        }
                        if (ndc == null) {
                            ndc = new Integer(0);
                        }

                        if (leveltype.equals("Single Attack")) {
                            if (ndcv.intValue() > 0 || ndc.intValue() > 0) {
                                return false;
                            }
                        }

                        return nocv.intValue() + ndcv.intValue() + ndc.intValue() * 2 <= level.intValue();
                    }
                };
                int result = pd.showPADDialog("Assign Combat Levels", pl, currentAbility, pvl);

                if (result == JOptionPane.CANCEL_OPTION) {
                    currentAbility.add("Ability.ASSIGNEDOCV", ocv, true);
                    currentAbility.add("Ability.ASSIGNEDDCV", dcv, true);
                    currentAbility.add("CombatLevel.ASSIGNEDDC", dc, true);
                }
            }
        };

        v.add(assignAction);
    }

    /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public boolean checkParameter(Ability ability, int index, String key, Object value, Object oldValue) {
        ParameterList parameterList = getParameterList(ability);
        Integer level = (Integer) parameterList.getParameterValue("Level", key, value);
        String overall = (String) parameterList.getParameterValue("Overall");
        Integer ocv = (Integer) parameterList.getParameterValue("OCVLevel", key, value);
        Integer dcv = (Integer) parameterList.getParameterValue("DCVLevel", key, value);
        Integer dc = (Integer) parameterList.getParameterValue("DCLevel", key, value);

        if ("Power.LEVELS".equals(key)) {
            if (((Integer) value).intValue() < 0) {
                return false;
            }
        } else if ("Ability.ASSIGNEDOCV".equals(key)) {
            if (((Integer) value).intValue() < 0) {
                return false;
            }
            int oldAssigned = dc.intValue() * 2 + dcv.intValue() + ((Integer) oldValue).intValue();
            int assigned = dc.intValue() * 2 + dcv.intValue() + ((Integer) value).intValue();
            if (oldAssigned <= level.intValue() && assigned > level.intValue()) {
                return false;
            }
        } else if ("Ability.ASSIGNEDDCV".equals(key)) {
            if (((Integer) value).intValue() < 0) {
                return false;
            }
            int oldAssigned = dc.intValue() * 2 + ((Integer) oldValue).intValue() + ocv.intValue();
            int assigned = dc.intValue() * 2 + ((Integer) value).intValue() + ocv.intValue();
            if (oldAssigned <= level.intValue() && assigned > level.intValue()) {
                return false;
            }
        } else if ("CombatLevel.ASSIGNEDDC".equals(key)) {
            if (((Integer) value).intValue() < 0) {
                return false;
            }
            int oldAssigned = ((Integer) oldValue).intValue() * 2 + dcv.intValue() + ocv.intValue();
            int assigned = ((Integer) value).intValue() * 2 + dcv.intValue() + ocv.intValue();
            if (oldAssigned <= level.intValue() && assigned > level.intValue()) {
                return false;
            }
        }

        return true;
    }

    public static int getConfiguredOCVModifier(Ability ability) {
        ParameterList pl = ability.getPowerParameterList();
        return pl.getParameterIntValue("OCVLevel");
    }

    public static int getConfiguredDCVModifier(Ability ability) {
        ParameterList pl = ability.getPowerParameterList();
        return pl.getParameterIntValue("DCVLevel");
    }

    public static int getConfiguredDCModifier(Ability ability) {
        ParameterList pl = ability.getPowerParameterList();
        return pl.getParameterIntValue("DCLevel");
    }

    public static boolean isDCVEditable(Ability ability) {
        String leveltype = (String) ability.getPowerParameterList().getParameterValue("LevelType");
        if (leveltype.equals("Single Attack") ) {
            return false;
        }
        else {
            return true;
        }

    }

    public static boolean isDCEditable(Ability ability) {
        String leveltype = (String) ability.getPowerParameterList().getParameterValue("LevelType");
        if (leveltype.equals("Single Attack") ) {
            return false;
        }
        else {
            return true;
        }
    }

    public static boolean setConfiguredModifiers(Ability ability, int ocv, int dcv, int dc) {
        ParameterList pl = ability.getPowerParameterList();
        int levels = pl.getParameterIntValue("Level");

        if (ocv + dcv + dc * 2 <= levels) {
            pl.setParameterValue("OCVLevel", ocv);
            pl.setParameterValue("DCVLevel", dcv);
            pl.setParameterValue("DCLevel", ocv);
            return true;
        } else {
            return false;
        }
    }
}
