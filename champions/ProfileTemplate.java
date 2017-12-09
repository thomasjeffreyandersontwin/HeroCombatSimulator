/*
 * ProfileInfo.java
 *
 * Created on May 30, 2002, 11:06 AM
 */

package champions;

import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;


/**
 *
 * @author  Trevor Walker
 */
public class ProfileTemplate extends DetailList {
    
    private static ProfileTemplate defaultProfile = null;
    
    /** Creates a new instance of ProfileInfo */
    public ProfileTemplate() {
        setFireChangeByDefault(false);
    }
    
    public static ProfileTemplate getDefaultProfileTemplate() {
        if ( defaultProfile == null ) {
            defaultProfile = new ProfileTemplate();
            defaultProfile.buildDefaultProfileTemplate();
        }
        
        return defaultProfile;
    }
    
    public void addOption(String name, String id, String description, String iconUIKey, String imageName) {
        int index;
        
        index = findIndexed("Option", "NAME", name);
        if ( index == -1 ) {
            index = createIndexed("Option", "NAME", name);
        }
        
        addIndexed(index, "Option", "ID", id);
        addIndexed(index, "Option", "DESCRIPTION", description);
        addIndexed(index, "Option", "ICONKEY", iconUIKey);
        addIndexed(index, "Option", "IMAGENAME", imageName);
        
        if ( imageName != null ) {
            URL url = getClass().getResource(imageName);
            if ( url != null ) {
                ImageIcon ii = new ImageIcon(url);
                addIndexed(index, "Option", "IMAGE", ii);
            }
        }
    }
    
    public void removeOption(String name) {
        int index;
        
        index = findIndexed("Option", "NAME", name);
        if ( index != -1 ) {
            removeAllIndexed(index, "Option");
        }
    }
    
    
    public int getOptionIndexByName(String name) {
        return findIndexed("Option", "NAME", name);
    }
    
    public int getOptionIndexByID(String id) {
        return findIndexed("Option", "ID", id);
    }
    
    
    public int getOptionCount() {
        return getIndexedSize("Option");
    }
    
    public String getOptionName(int index) {
        return getIndexedStringValue(index, "Option", "NAME");
    }
    
    public String getOptionID(int index) {
        return getIndexedStringValue(index, "Option", "ID");
    }
    
    public String getOptionDescription(int index) {
        return getIndexedStringValue(index, "Option", "DESCRIPTION");
    }
    
    public ImageIcon getOptionImage(int index) {
        return (ImageIcon)getIndexedValue(index, "Option", "IMAGE");
    }
    
    public Icon getOptionIcon(int index) {
        String key = getIndexedStringValue(index, "Option", "ICONKEY");
        return UIManager.getIcon(key);
    }
    
    private void buildDefaultProfileTemplate() {
        // Attack Description Panel for Continuing Attacks
        addOption( "Initial Description Panel for Attacks", "SHOW_ACTIVATING_ATTACK_DESCRIPTION_PANEL", "Put Description Here",
                "AttackTree.attackParametersIcon", null);
        
        // Attack Description Panel for Continuing Attacks
        addOption( "Initial Description Panel for Non-Attacks", "SHOW_ACTIVATING_NONATTACK_DESCRIPTION_PANEL", "Put Description Here",
                "AttackTree.attackParametersIcon", null);
        
        // Attack Description Panel for Continuing Attacks
        addOption( "Continuing Description Panel for Attacks", "SHOW_CONTINUING_ATTACK_DESCRIPTION_PANEL", "Put Description Here",
                "AttackTree.attackParametersIcon", null);
        
        // Attack Description Panel for Continuing Attacks
        addOption( "Continuing Description Panel for Non-Attacks", "SHOW_CONTINUING_NONATTACK_DESCRIPTION_PANEL", "Put Description Here",
                "AttackTree.attackParametersIcon", null);
        
        // Attack Parameter Panel
        addOption( "Attack Parameters Panel", "SHOW_ATTACK_PARAMETERS_PANEL", "Put Description Here",
                "AttackTree.attackParametersIcon", null);
        
        // Obstruction Panel
        addOption( "Perceptions Panel", "SHOW_PERCEPTION_PANEL", "Put Description Here",
                "AttackTree.perceptionsIcon", null);
        
        // Attack Block Panel
        addOption( "Block Panel", "SHOW_BLOCK_PANEL", "Put Description Here",
                "AttackTree.blockIcon", null);
        
        // Attack Defenses Panel
        addOption( "Block Panel", "SHOW_SELECTABLE_DEFENSES_PANEL", "Put Description Here",
                "AttackTree.defensesIcon", null);
        
        // Obstruction Panel
        addOption( "Obstructions Panel", "SHOW_OBSTRUCTION_PANEL", "Put Description Here",
                "AttackTree.obstructionIcon", null);
        
        // Mental Effect Panel
        addOption( "Mental Effects Panel", "SHOW_MENTAL_EFFECT_PANEL", "Put Description Here",
                "AttackTree.mentalEffectsIcon", null);
        
        addOption( "Hit Location Panel", "SHOW_HIT_LOCATION_PANEL", "The Hit Location Panel provides the GM with the ability to select a " +
                "specific Hit Location.  Hit Locations will automatically adjust the OCV of the attacking Character according to the location.",
                "AttackTree.hitLocationIcon", null );
        
        addOption( "ToHit Panel", "SHOW_TOHIT_PANEL", "The ToHit Panel contains information and controls for determining if a " +
                "character hits another character with a specific attack.  The ToHit panel allows the GM to view Combat Values of both the " +
                "attacker and the defender, allows adjustment to those values, and provides the GM with the option to enter a manually rolled " +
                "ToHit for Player Characters.<P><P>" +
                "When set to Show, the ToHit Panel will always be show for Characters using this profile.  When set to Hide, the ToHit panel " +
                "will always be skipped and all ToHit rolls for Characters using this profile will be rolled automatically.",
                "AttackTree.toHitIcon", "/graphics/profileToHitImage.gif");
        
        addOption( "Linked Setup Panel", "SHOW_LINKED_SETUP_PANEL", "Put Description Here",
                "AttackTree.linkedSetupPanel", null);
        
        // Skill Panel
        addOption( "Skill Roll", "SHOW_SKILL_PANEL", "Put Description Here",
                "AttackTree.summaryIcon", null);
        
        // Skill vs. Skill Panel
        addOption( "Target Skill Roll", "SHOW_TARGETSKILL_PANEL", "Put Description Here",
                "AttackTree.summaryIcon", null);
        
        // Missed Info Panel
        addOption( "Miss Info Panel", "SHOW_MISSED_INFO_PANEL", "Put Description Here",
                "AttackTree.summaryIcon", null);
        
        // Attack  Secondary Target Panel
        addOption( "Secondary Target Panel", "SHOW_SECONDARY_TARGETS_PANEL", "Put Description Here",
                "AttackTree.selectTargetIcon", null);
        
        // Attack  Secondary Target Panel
        addOption( "Area Effect Target Panel", "SHOW_AE_TARGETS_PANEL", "Put Description Here",
                "AttackTree.selectTargetIcon", null);
        
        // Attack Effect Dice Roll Panel
        addOption( "Dice Roll Panel", "SHOW_EFFECT_ROLL_PANEL", "Put Description Here",
                "AttackTree.diceIcon", null);
        
        // Generic Results Panel, used to display applied effects for normal attacks, move-through, move-by, etc...
        addOption( "Effect Results Panel", "SHOW_APPLY_EFFECTS_PANEL", "Put Description Here",
                "AttackTree.summaryIcon", null);
        
        addOption( "Effect Results Panel (Postturn)", "SHOW_APPLY_EFFECTS_POSTTURN_PANEL", "Put Description Here",
                "AttackTree.summaryIcon", null);
        
        // Attack Knockback Roll Panel
        addOption( "Knockback Roll Panel", "SHOW_KNOCKBACK_AMOUNT_ROLL_PANEL", "Put Description Here",
                "AttackTree.knockbackEffectIcon", null);
        
        // Attack Knockback Effect Panel
        addOption( "Knockback Effect Panel", "SHOW_KNOCKBACK_EFFECT_PANEL", "Put Description Here",
                "AttackTree.knockbackEffectIcon", null);
        
        // Attack Knockback Secondary Target Panel
        addOption( "Secondary Knockback Target Panel", "SHOW_KNOCKBACK_SECONDARY_TARGET_PANEL", "Put Description Here",
                "AttackTree.selectTargetIcon", null);
        
        // Generic Results Panel, used to display applied effects for normal attacks, move-through, move-by, etc...
        addOption( "Knockback Damage Results Panel", "SHOW_KNOCKBACK_APPLY_EFFECTS_PANEL", "Put Description Here",
                "AttackTree.summaryIcon", null);
        
        // Movement Input Panel
        addOption( "Movement Distance Panel", "SHOW_MOVEMENT_DISTANCE_PANEL", "Put Description Here",
                "AttackTree.summaryIcon", null);
        
        addOption( "Configure Battle Description Panel", "SHOW_CONFIGURE_BATTLE_DESCRIPTION_PANEL", "Panel describing the use and " +
                "usage of the configure battle dialogs", "AttackTree.attackParametersIcon", null);
        
        addOption( "Configure Battle Setup Panel", "SHOW_CONFIGURE_BATTLE_SETUP_PANEL", "Panel to select the abilities " +
                "that will be initially enabled or disable at the start of a battle or when the configure battle menu option is selected.",
                "AttackTree.configurePowerIcon", null);
        
        addOption( "NND Defense Panel", "SHOW_NND_PANEL", "Panel used to select whether a target has NND defense.",
                "AttackTree.defenseIcon", null);
        
        addOption( "Explosion Distance Panel", "SHOW_EXPLOSION_DISTANCE_PANEL", "Panel to set the distance of possible target from the center of an explosion.",
                "AttackTree.configurePowerIcon", null);
        
    }
}
