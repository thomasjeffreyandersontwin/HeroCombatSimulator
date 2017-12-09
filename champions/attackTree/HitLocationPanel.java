/*
 * HitLocationPanel.java
 *
 * Created on December 26, 2001, 11:01 PM
 */

package champions.attackTree;

import champions.*;
import champions.event.PADValueEvent;
import champions.interfaces.AbilityIterator;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.PADValueListener;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.*;


/**
 *
 * @author  twalker
 */
public class HitLocationPanel extends JPanel implements PADValueListener, AttackTreeInputPanel, ChampionsConstants {
    
    static String [][] hitLocationOptions = new String[][] {
        // { "None [ -0 OCV ]", "NONE", "HitLocation.None" },
        { "Head [ -8 OCV ]", "HEAD", "HitLocation.Head" },
        { "Hands [ -6 OCV ]", "HANDS", "HitLocation.Hands" },
        { "Arms [ -5 OCV ]", "ARMS", "HitLocation.Arms" },
        { "Shoulders [ -5 OCV ]", "SHOULDERS", "HitLocation.Shoulders" },
        { "Chest [ -3 OCV ]", "CHEST", "HitLocation.Chest" },
        { "Stomach [ -7 OCV ]", "STOMACH", "HitLocation.Stomach" },
        { "Vitals [ -8 OCV ]", "VITALS", "HitLocation.Vitals" },
        { "Thighs [ -4 OCV ]", "THIGHS", "HitLocation.Thighs" },
        { "Legs [ -6 OCV ]", "LEGS", "HitLocation.Legs" },
        { "Feet [ -8 OCV ]", "FEET", "HitLocation.Feet" }
    };
    
    static String [][] specialHitLocationOptions = new String[][] {
        //  { "None [ -0 OCV ]", "NONE", "HitLocation.None" },
        { "Head Shot [ -4 OCV ]", "HEADSHOT", "HitLocation.HeadShot", "1d6+3" },
        { "High Shot [ -2 OCV ]", "HIGHSHOT", "HitLocation.HighShot", "2d6+1" },
        { "Body Shot [ -1 OCV ]", "BODYSHOT", "HitLocation.BodyShot", "2d6+4" },
        { "Low Shot [ -2 OCV ]", "LOWSHOT", "HitLocation.LowShot", "2d6+7" },
        { "Leg Shot [ -4 OCV ]", "LEGSHOT", "HitLocation.LegShot", "1d6+12" }
    };
    
    static Object[][] hitLocationLookupMap = new Object[][] {
        { new Integer(-121855), "HEAD" },
        { new Integer(-8047580), "HANDS" },
        { new Integer(-66559), "ARMS" },
        { new Integer(-15591454), "SHOULDERS" },
        { new Integer(-16649986), "CHEST" },
        { new Integer(-654850), "STOMACH" },
        { new Integer(-15457174), "VITALS" },
        { new Integer(-8519425), "THIGHS" },
        { new Integer(-8051594), "LEGS" },
        { new Integer(-14383800), "FEET" }
    };
    
    static private HitLocationPanel defaultPanel = null;

	public static HitLocationPanel Panel;
    
    private BufferedImage imageMap;
    
    /** Holds value of property mode. */
    private String mode;
    
    /** Holds value of property hitLocation. */
    private String hitLocation;
    
    /** Holds value of property picture. */
    private String picture;
    
    /** Holds value of property target. */
    private Target target;
    
    /** Creates new form HitLocationPanel */
    public HitLocationPanel() {
        initComponents();
        
        DefaultComboBoxModel cbm;
        int index;
        
        cbm = new DefaultComboBoxModel();
        for (index = 0; index < specialHitLocationOptions.length; index++) {
            cbm.addElement(specialHitLocationOptions[index][0]);
        }
        specialHitLocationCombo.setModel(cbm);
        
        cbm = new DefaultComboBoxModel();
        for (index = 0; index < hitLocationOptions.length; index++) {
            cbm.addElement(hitLocationOptions[index][0]);
        }
        hitLocationCombo.setModel(cbm);
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(hitLocationButton);
        bg.add(noHitLocationButton);
        bg.add(specialHitLocationButton);
        
        setupImageMap();
        
        HitLocationPanel.ImageMapListener iml = new HitLocationPanel.ImageMapListener(this, imageMap);
        hitLocationLabel.addMouseListener( iml );
        hitLocationLabel.addMouseMotionListener( iml );
        
        //        fromBehind.setDescription("Attacked from Behind");
        //        fromBehind.setPropertyName("Attack.FROMBEHIND");
        //        fromBehind.addPADValueListener( this );
        Panel = this;
        
    }
    
    private void setupImageMap() {
        URL url = Toolkit.getDefaultToolkit().getClass().getResource("/graphics/hitLocationMap.gif");
        Image source = Toolkit.getDefaultToolkit().getImage(url);
        
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(source, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException e) {
        }
        
        imageMap = new BufferedImage(source.getWidth(this), source.getHeight(this), BufferedImage.TYPE_INT_ARGB);
        imageMap.getGraphics().drawImage(source, 0, 0, null);
        
    }
    
    static public HitLocationPanel getDefaultPanel(BattleEvent battleEvent, int targetReferenceNumber, String targetGroup, Target target) {
        if ( defaultPanel == null ) defaultPanel = new HitLocationPanel();
        
        defaultPanel.setBattleEvent(battleEvent);
        defaultPanel.setTargetReferenceNumber(targetReferenceNumber);
        defaultPanel.setTargetGroup(targetGroup);
        defaultPanel.setTarget(target);
        
        
        return defaultPanel;
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        hitLocationLabel = new javax.swing.JLabel();
        selectLocationGroup = new javax.swing.JPanel();
        noHitLocationButton = new javax.swing.JRadioButton();
        hitLocationButton = new javax.swing.JRadioButton();
        hitLocationCombo = new javax.swing.JComboBox();
        specialHitLocationButton = new javax.swing.JRadioButton();
        specialHitLocationCombo = new javax.swing.JComboBox();
        rollGroup = new javax.swing.JPanel();
        randomLocationRoll = new champions.PADDiceValueEditor();
        infoGroup = new javax.swing.JPanel();
        fromBehind = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        hitLocationLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/graphics/noHitLocation.gif")));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        add(hitLocationLabel, gridBagConstraints);

        selectLocationGroup.setLayout(new java.awt.GridBagLayout());

        noHitLocationButton.setText("Random Hit Location");
        noHitLocationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noHitLocationButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        selectLocationGroup.add(noHitLocationButton, gridBagConstraints);

        hitLocationButton.setText("Hit Location");
        hitLocationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hitLocationButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        selectLocationGroup.add(hitLocationButton, gridBagConstraints);

        hitLocationCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hitLocationComboActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        selectLocationGroup.add(hitLocationCombo, gridBagConstraints);

        specialHitLocationButton.setText("Special Hit Location");
        specialHitLocationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                specialHitLocationButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        selectLocationGroup.add(specialHitLocationButton, gridBagConstraints);

        specialHitLocationCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                specialHitLocationComboActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        selectLocationGroup.add(specialHitLocationCombo, gridBagConstraints);

        rollGroup.setLayout(new java.awt.BorderLayout());

        rollGroup.setBorder(new javax.swing.border.TitledBorder("Random Location Roll"));
        randomLocationRoll.setDescription("Hit Location");
        randomLocationRoll.setDiceType("STUNONLY");
        randomLocationRoll.setStunLabel("Total");
        rollGroup.add(randomLocationRoll, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        selectLocationGroup.add(rollGroup, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(selectLocationGroup, gridBagConstraints);

        infoGroup.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        fromBehind.setText("Attacked  from Behind");
        fromBehind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromBehindActionPerformed(evt);
            }
        });

        infoGroup.add(fromBehind);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(infoGroup, gridBagConstraints);

    }//GEN-END:initComponents
    
    private void fromBehindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromBehindActionPerformed
        // Add your handling code here:
        setFromBehind(fromBehind.isSelected());
        
        
        
    }//GEN-LAST:event_fromBehindActionPerformed

	public void setFromBehind(Boolean fromBehind) {
		ActivationInfo ai = getBattleEvent().getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        
        CVList cvl = ai.getCVList(tindex);
        
        if (  fromBehind!=null && fromBehind) {
            ai.addIndexed(tindex, "Target", "FROMBEHIND", "TRUE", true);
            //cvl.addTargetCVModifier( "From Behind", -3);
            cvl.addTargetCVMultiplier( "From Behind", 0.5);
        }
        else {
            ai.addIndexed(tindex, "Target", "FROMBEHIND", "FALSE", true);
            cvl.removeTargetModifier("From Behind");
        }
	}
    
    private void specialHitLocationComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_specialHitLocationComboActionPerformed
        // Add your handling code here:
        int index = specialHitLocationCombo.getSelectedIndex();
        setHitLocation( specialHitLocationOptions[index][1] );
    }//GEN-LAST:event_specialHitLocationComboActionPerformed
    
    private void specialHitLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_specialHitLocationButtonActionPerformed
        // Add your handling code here:
        int index = specialHitLocationCombo.getSelectedIndex();
        setHitLocation( specialHitLocationOptions[index][1] );
    }//GEN-LAST:event_specialHitLocationButtonActionPerformed
    
    private void hitLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hitLocationButtonActionPerformed
        // Add your handling code here:
        int index = hitLocationCombo.getSelectedIndex();
        setHitLocation( hitLocationOptions[index][1] );
    }//GEN-LAST:event_hitLocationButtonActionPerformed
    
    private void noHitLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noHitLocationButtonActionPerformed
        // Add your handling code here:
        setHitLocation( "NONE" );
    }//GEN-LAST:event_noHitLocationButtonActionPerformed
    
    private void hitLocationComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hitLocationComboActionPerformed
        // Add your handling code here:
        int index = hitLocationCombo.getSelectedIndex();
        setHitLocation( hitLocationOptions[index][1] );
    }//GEN-LAST:event_hitLocationComboActionPerformed
    
    /** Getter for property mode.
     * @return Value of property mode.
     */
    public String getMode() {
        return mode;
    }
    
    /** Setter for property mode.
     * @param mode New value of property mode.
     */
    public void setMode(String mode) {
        this.mode = mode;
    }
    
    /** Getter for property hitLocation.
     * @return Value of property hitLocation.
     */
    public String getHitLocation() {
        return hitLocation;
    }
    
    /** Setter for property hitLocation.
     * @param hitLocation New value of property hitLocation.
     */
    public void setHitLocation(String hitLocation) {
        if ( hitLocation == null ) {
            setHitLocation("NONE");
        }
        else if ( hitLocation.equals(this.hitLocation) == false ) {
            this.hitLocation = hitLocation;
            
            if ( hitLocation.equals("NONE") ) {
                noHitLocationButton.setSelected(true);
                setPicture("HitLocation.None");
                setDiceSize("3d6");
                setDiceNecessary(true);
            }
            else if ( hitLocation.indexOf("SHOT") != -1 ) {
                specialHitLocationButton.setSelected(true);
                int index = findIndexOfSpecialHitLocation(hitLocation);
                setPicture( specialHitLocationOptions[index][2] );
                specialHitLocationCombo.setSelectedIndex(index);
                setDiceSize(specialHitLocationOptions[index][3]);
                setDiceNecessary(true);
            }
            else {
                hitLocationButton.setSelected(true);
                int index = findIndexOfHitLocation(hitLocation);
                setPicture( hitLocationOptions[index][2] );
                hitLocationCombo.setSelectedIndex(index);
                setDiceNecessary(false);
            }
        }
    }
    
    
    
    public void showPanel(AttackTreePanel atip) {
        ActivationInfo ai = battleEvent.getActivationInfo();
        
        int tindex = ai.getTargetIndex(targetReferenceNumber, targetGroup);
        
        String hl = ai.getIndexedStringValue(tindex, "Target", "HITLOCATIONCALLEDSHOT");
        setHitLocation(hl);
        
        // Setup panel for different preferences
        if ( (Boolean)Preferences.getPreferenceList().getParameterValue("HitLocationRequired") ) {
            
            Dice d = (Dice)ai.getIndexedValue(tindex, "Target", "HITLOCATIONROLL");
            randomLocationRoll.setDice(d);
            
            String autoroll = ai.getIndexedStringValue(tindex, "Target", "HITLOCATIONAUTOROLL");
            randomLocationRoll.setAutoroll( (autoroll == null || autoroll.equals("TRUE") ) );
            
            noHitLocationButton.setText("Random Hit Location");
            specialHitLocationButton.setVisible(true);
            specialHitLocationCombo.setVisible(true);
            rollGroup.setVisible(true);
        }
        else {
            noHitLocationButton.setText("No Hit Location");
            specialHitLocationButton.setVisible(false);
            specialHitLocationCombo.setVisible(false);
            rollGroup.setVisible(false);
        }
        String frombehind = ai.getIndexedStringValue(tindex, "Target", "FROMBEHIND");
        if (frombehind != null && frombehind.equals("TRUE") ) {
            fromBehind.setSelected(true);
        }
        else {
            fromBehind.setSelected(false);
        }
        
        if (target.hasEffect("Defense Maneuver")) {
            fromBehind.setVisible(false);
        }
        else {
            fromBehind.setVisible(true);
        }
        
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void hidePanel() {
        ActivationInfo ai = battleEvent.getActivationInfo();
        
        int tindex = ai.getTargetIndex(targetReferenceNumber, targetGroup);
        
        ai.addIndexed(tindex, "Target", "HITLOCATIONCALLEDSHOT", hitLocation, true);
        
        if ( (Boolean)Preferences.getPreferenceList().getParameterValue("HitLocationRequired") ) {
            ai.addIndexed(tindex, "Target", "HITLOCATIONROLL", randomLocationRoll.getDice(), true);
            ai.addIndexed(tindex, "Target", "HITLOCATIONAUTOROLL", randomLocationRoll.isAutoroll() ? "TRUE" : "FALSE", true);
        }
    }
    
    /** Getter for property picture.
     * @return Value of property picture.
     */
    public String getPicture() {
        return picture;
    }
    
    /** Setter for property picture.
     * @param picture New value of property picture.
     */
    public void setPicture(String picture) {
        this.picture = picture;
        Icon p = UIManager.getIcon(picture);
        if ( p != null ) {
            hitLocationLabel.setIcon(p);
        }
    }
    
    private int findIndexOfSpecialHitLocation(String location) {
        for(int index = 0; index < specialHitLocationOptions.length; index++) {
            if ( location.equals( specialHitLocationOptions[index][1] ) ) {
                return index;
            }
        }
        return -1;
    }
    
    
    
    private int findIndexOfHitLocation(String location) {
        for(int index = 0; index < hitLocationOptions.length; index++) {
            if ( location.equals( hitLocationOptions[index][1] ) ) {
                return index;
            }
        }
        return -1;
    }
    
    private String lookupHitLocation(int colorIndex) {
        Integer lookupColor;
        for(int index = 0; index < hitLocationLookupMap.length; index++) {
            lookupColor = (Integer)hitLocationLookupMap[index][0];
            if ( lookupColor.intValue() == colorIndex ) {
                return (String)hitLocationLookupMap[index][1];
            }
        }
        return "NONE";
    }
    
    /** Getter for property diceSize.
     * @return Value of property diceSize.
     */
    public String getDiceSize() {
        return diceSize;
    }
    
    /** Setter for property diceSize.
     * @param diceSize New value of property diceSize.
     */
    public void setDiceSize(String diceSize) {
        this.diceSize = diceSize;
        randomLocationRoll.setDiceSize(diceSize);
    }
    
    /** Getter for property diceNecessary.
     * @return Value of property diceNecessary.
     */
    public boolean isDiceNecessary() {
        return diceNecessary;
    }
    
    /** Setter for property diceNecessary.
     * @param diceNecessary New value of property diceNecessary.
     */
    public void setDiceNecessary(boolean diceNecessary) {
        this.diceNecessary = diceNecessary;
        randomLocationRoll.setEnabled(diceNecessary);
    }
    
    /** Getter for property targetReferenceNumber.
     * @return Value of property targetReferenceNumber.
     */
    public int getTargetReferenceNumber() {
        return targetReferenceNumber;
    }
    
    /** Setter for property targetReferenceNumber.
     * @param targetReferenceNumber New value of property targetReferenceNumber.
     */
    public void setTargetReferenceNumber(int targetReferenceNumber) {
        this.targetReferenceNumber = targetReferenceNumber;
    }
    
    /** Getter for property battleEvent.
     * @return Value of property battleEvent.
     */
    public BattleEvent getBattleEvent() {
        return battleEvent;
    }
    
    /** Setter for property battleEvent.
     * @param battleEvent New value of property battleEvent.
     */
    public void setBattleEvent(BattleEvent battleEvent) {
        this.battleEvent = battleEvent;
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public final Target getTarget() {
        return target;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public final void setTarget(Target target) {
        this.target = target;
        //updateSkillRoll();
    }
    
    
    /** Getter for property targetGroup.
     * @return Value of property targetGroup.
     */
    public String getTargetGroup() {
        return targetGroup;
    }
    
    /** Setter for property targetGroup.
     * @param targetGroup New value of property targetGroup.
     */
    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }
    
    public void PADValueChanged(PADValueEvent evt) {
    }
    
    public boolean PADValueChanging(PADValueEvent evt) {
        return true;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox hitLocationCombo;
    private javax.swing.JPanel rollGroup;
    private javax.swing.JPanel infoGroup;
    private javax.swing.JRadioButton hitLocationButton;
    private javax.swing.JPanel selectLocationGroup;
    private javax.swing.JRadioButton specialHitLocationButton;
    private javax.swing.JComboBox specialHitLocationCombo;
    private javax.swing.JLabel hitLocationLabel;
    private javax.swing.JCheckBox fromBehind;
    private javax.swing.JRadioButton noHitLocationButton;
    private champions.PADDiceValueEditor randomLocationRoll;
    // End of variables declaration//GEN-END:variables
    
    /** Holds value of property diceSize. */
    private String diceSize;
    
    /** Holds value of property diceNecessary. */
    private boolean diceNecessary;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Holds value of property battleEvent. */
    private BattleEvent battleEvent;
    
    /** Holds value of property targetGroup. */
    private String targetGroup;
    
    private class ImageMapListener extends MouseAdapter implements MouseMotionListener {
        private BufferedImage imageMap;
        private HitLocationPanel hlp;
        
        public ImageMapListener(HitLocationPanel panel, BufferedImage imageMap) {
            this.imageMap = imageMap;
            hlp = panel;
        }
        
        public void mousePressed(MouseEvent e) {
            Point p = e.getPoint();
            int color = imageMap.getRGB( (int)p.getX(), (int)p.getY() );
            
            String hitLocation = lookupHitLocation(color);
            setHitLocation(hitLocation);
        }
        
        /**
         * Invoked when a mouse button is pressed on a component and then
         * dragged.  Mouse drag events will continue to be delivered to
         * the component where the first originated until the mouse button is
         * released (regardless of whether the mouse position is within the
         * bounds of the component).
         */
        public void mouseDragged(MouseEvent e) {
            Point p = e.getPoint();
            
            if ( hitLocationLabel.contains(p) ) {
                int color = imageMap.getRGB( (int)p.getX(), (int)p.getY() );
                
                String hitLocation = lookupHitLocation(color);
                setHitLocation(hitLocation);
            }
        }
        
        /**
         * Invoked when the mouse button has been moved on a component
         * (with no buttons no down).
         */
        public void mouseMoved(MouseEvent e) {
        }
        
    }
    
    public boolean isDefenseManeuverEnabled(Target target) {
        
        Ability DefenseManeuver = PADRoster.getSharedAbilityInstance("Defense Maneuver");
        
        ActivationInfo ai = battleEvent.getActivationInfo();
        //Target source = Battle.currentBattle.getActiveTarget();
        
        AbilityIterator aiter = target.getSkills();
        
        
        while (aiter.hasNext() ) { // Check to see if there is another item
            // Rip next ability from the iterator.
            // This a is guaranteed to have a power which is actually a skill
            // since we used the getSkills.  If we use getAbilities() we will
            // actually get both skills and powers.
            Ability a = aiter.nextAbility();
            if (a.isActivated(target)) {
                System.out.println("DM is active");
                
            }
            if ( a.equals(DefenseManeuver)&& a.isEnabled(target, false) ) {
                return true;
            }
            
            
        }
        return false;
    }
    
}
