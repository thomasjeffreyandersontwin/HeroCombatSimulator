/*
 * StressTest.java
 *
 * Created on October 26, 2007, 10:35 AM
 *
 * Performs a Stress test on the BattleEngine.
 *
 * This class will generate random actions, from the list of available actions
 * for the current character.  Exceptions will be monitored and recorded.  This
 * does not test GUI reliability.
 *
 */

package champions;

import champions.abilityTree2.ATAbilityNode;
import champions.abilityTree2.ATActionNode;
import champions.abilityTree2.ATModel;
import champions.abilityTree2.ATNode;
import champions.abilityTree2.ATSingleTargetTree;
import champions.attackTree.AttackTreeInputPanel;
import champions.attackTree.AttackTreeListener;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.MovementManeuverSetupPanel;
import champions.attackTree.SelectTargetPanel;
import champions.event.SegmentAdvancedEvent;
import champions.event.SequenceChangedEvent;
import champions.event.TargetSelectedEvent;
import champions.interfaces.AbilityIterator;
import champions.interfaces.BattleListener;
import tjava.Filter;
import dockable.DockingPanel;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author twalker
 */
public class StressTest extends Thread implements BattleListener, AttackTreeListener {
    
    private int actionToTake = Integer.MAX_VALUE;
    private int actionCount = 0;
    private long startTime = 0;
    private double actionsPerSecond = 0;
    private boolean stopOnException = false;
    private boolean testSave = true;
    
    private boolean running = false;
    private boolean testingSave = false;
    
    private static JPanel mouseEventPanel = new JPanel();
    
    /** Creates a new instance of StressTest */
    public StressTest() {
        Battle.addBattleListener(this);
        AttackTreePanel.addAttackTreeListener(this);
        
        start();
    }
    
    public void startTest(boolean testSaves) {
        this.testSave = testSaves;
        startTest();
    }
    
    public void startTest() {
        synchronized(this) {
            setActionCount(0);
            setActionsPerSecond(0);
            
            startTime = System.currentTimeMillis();
            
            running = true;
            notifyAll();
        }
    }
    
    public void stopTest() {
        running = false;
    }
    
    
    public void run() {
        synchronized(this) {
            while ( true ) {
                
                while( isRunning() == false ) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                    }
                }
                
                if ( getActionCount() >= actionToTake ) {
                    stopTest();
                } else {
                    if ( Battle.currentBattle.isStopped() ) {
                        startBattle();
                    } else {
                        performAction();
                    }
                }
                
                
                
                if ( testSave && actionCount > 0 && actionCount % 100 == 0 ) {
                    testSave();
                }
                
                updateDialog();
            }
            
        }
    }
    
    private void testSave() {
        try {
            
            testingSave = true;
            
            updateDialog();
            
            File temp = File.createTempFile("stressTest",".btl");
            temp.deleteOnExit();
            
            System.out.println("StressTest: Saving battle to " + temp.getPath());
            
            boolean success = Battle.currentBattle.saveWithBlocking(temp, true);
            if ( success ) {
                System.out.println("StressTest: Battle Save Succeeded.");
            } else {
                System.out.println("StressTest: Battle Save Failed!");
                stopTest();
            }
            
            if ( temp.exists() ) {
                temp.delete();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        testingSave = false;
    }
    
    private void startBattle() {
        waitForBattle();
        
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                Battle battle = Battle.getCurrentBattle();
                if ( battle.isStopped() == true ) {
                    battle.startBattle();
                }
            }
        });
    }
    
    private void performAction() {
        
        waitForBattle();
        
        if ( isRunning() ) {
            Battle battle = Battle.getCurrentBattle();
            Target t = battle.getActiveTarget();
            
            // Use the target tree to figure out the available abilities/actions
            List<StressAction> actions = getActiveTargetActions();
            
            if ( actions.size() > 0 ) {
                int actionIndex = (int)(Math.random() * actions.size());
                
                StressAction sa = actions.get(actionIndex);
                
                
                System.out.println("StressTest (" + getActionCount() + "):  " + t.getName() + " --> " + sa);
                System.out.flush();
                
                sa.performAction();
                
                while( waitForAttackInfo() ) {
                    
                    handleInputPanel();
                }
                
                setActionCount(getActionCount() + 1);
                
            } else {
                try {
                    sleep(100);
                } catch (InterruptedException ex) {
                }
            }
        }
    }
    
    private void waitForBattle() {
        while ( running && Battle.currentBattle.isProcessing() ) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
            
            
        }
        
//        try {
//            sleep(100);
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
    }
    
    /** Waits for the attackInfo.
     *
     *  Return true, if attackInfo needs response, false otherwise.
     */
    private boolean waitForAttackInfo() {
        boolean result = false;
        
        while ( true ) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
            
            if ( running == false ) break;
            
            if ( Battle.currentBattle.isProcessing() == false ) break;
            
            if ( getCurrentInputPanel() != null &&  getCurrentInputPanel() instanceof AttackTreePanel.AboutPanel == false) {
                result = true;
                break;
            }
            
            
        }
        return result;
    }
    
    private AttackTreeInputPanel getCurrentInputPanel() {
        return AttackTreePanel.getAttackTreePanel().getCurrentPanel();
    }
    
    private ATSingleTargetTree getActiveTargetTree() {
        DockingPanel dp = CombatSimulator.getActiveTargetAbilitiesDockingPanel();
        AbilityPanel ap = (AbilityPanel) dp.getContentPane().getComponent(0);
        ATSingleTargetTree tt = ap.getTargetTree();
        
        return tt;
    }
    
    private List<StressAction> getActiveTargetActions() {
        ATSingleTargetTree tt = getActiveTargetTree();
        
        while( tt.isEnabled() == false ) {
            try {
                wait(100);
            } catch (InterruptedException ex) {
            }
        }
        
        ATModel model = (ATModel) tt.getBaseTreeTableModel();
        ATNode root = (ATNode) model.getRoot();
        
        List<StressAction> list = new ArrayList<StressAction>();
        
        extractActions(root, 5, list);
        
        return list;
    }
    
    private void extractActions(ATNode node, int multiplier, List<StressAction> list) {
        if ( node instanceof ATAbilityNode ) {
            ATAbilityNode an = (ATAbilityNode)node;
            Ability a = an.getAbility();
            if ( a != null && a.getName().equals("Generic Damage/Power") == false && (a.isActivated(null) || a.isEnabled(null)) ) {
                AbilityStressAction ac = new AbilityStressAction(an);
                for(int i = 0; i < multiplier; i++) list.add(ac);
            }
        } else if ( node instanceof ATActionNode ) {
            ATActionNode an = (ATActionNode)node;
            Action a = an.getAction();
            if ( a.isEnabled() ) {
                ActionStressAction ac = new ActionStressAction(an);
                for(int i = 0; i < multiplier; i++) list.add(ac);
            }
        } else {
            for(int i = 0; i < node.getChildCount(); i++) {
                ATNode child = (ATNode) node.getChildAt(i);
                if ( child.getValueAt(0).equals("Default Abilities")) {
                    extractActions(child, 1, list);
                }
                else if ( ((String)child.getValueAt(0)).endsWith("Actions")) {
                    extractActions(child, multiplier * 3, list);
                }
                else {
                    extractActions(child, multiplier, list);
                }
                
            }
        }
    }
    
    public void battleTargetSelected(TargetSelectedEvent event) {
    }
    
    public void battleSegmentAdvanced(SegmentAdvancedEvent event) {
    }
    
    public void battleSequenceChanged(SequenceChangedEvent event) {
    }
    
    public void stateChanged(BattleChangeEvent e) {
    }
    
    public void eventNotification(ChangeEvent event) {
    }
    
    public void combatStateChange(ChangeEvent event) {
    }
    
    public void processingChange(BattleChangeEvent event) {
        synchronized(this) {
            notifyAll();
        }
    }
    
    public void inputNeeded(AttackTreePanel attackTreePanel) {
        synchronized(this) {
            notifyAll();
        }
    }
    
    private void handleInputPanel() {
        AttackTreeInputPanel p = getCurrentInputPanel();
        
        if ( p instanceof SelectTargetPanel ) {
            handleTargetPanel( (SelectTargetPanel) p);
        } else if ( p instanceof MovementManeuverSetupPanel ) {
            handleMoveThroughSetupPanel((MovementManeuverSetupPanel)p );
        } else {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    AttackTreePanel.getAttackTreePanel().advanceNode();
                }
            });
        }
    }
    
    private void handleTargetPanel(final SelectTargetPanel selectTargetPanel) {
        if ( selectTargetPanel.isTargetSelected() == true ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    AttackTreePanel.getAttackTreePanel().advanceNode();
                }
            });
        } else {
            int targetType = selectTargetPanel.getTargetType();
            switch ( targetType ) {
                case SelectTargetPanel.NORMAL_TARGET:
                case SelectTargetPanel.AE_CENTER:
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            Target t = getRandomTarget(selectTargetPanel);
                            selectTargetPanel.selectTarget(t);
                        }
                    });
                    
                    break;
//                case SKILL_TARGET:
//                    //targetSelfButton.setVisible(ability.can("TARGETSELF"));
//                    noMoreTargetsButton.setVisible(false);
//                    noTargetButton.setVisible(false);
//
//                    break;
                case SelectTargetPanel.AE_TARGET:
                case SelectTargetPanel.AE_SELECTIVE_TARGET:
                case SelectTargetPanel.AE_NONSELECTIVE_TARGET:
                    if ( Math.random() < 0.50 ) {
                        // 30% of the select No more targets...
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                selectTargetPanel.selectTarget(null);
                            }
                        });
                    } else {
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                Target t = getRandomTarget(selectTargetPanel);
                                selectTargetPanel.selectTarget(t);
                            }
                        });
                    }
                    break;
//                case KNOCKBACK_TARGET:
//                    //targetSelfButton.setVisible(false);
//                    noMoreTargetsButton.setVisible(false);
//                    noTargetButton.setVisible(true);
//                    break;
//                case SECONDARY_TARGET:
//                    //targetSelfButton.setVisible(ability.can("TARGETSELF"));
//
//                    if ( targetSelected == true ) {
//                        noMoreTargetsButton.setText( "Remove Target" );
//                        noMoreTargetsButton.setVisible(true);
//                        noTargetButton.setVisible(false);
//                    } else {
//                        noMoreTargetsButton.setVisible(false);
//                        noTargetButton.setVisible(true);
//                    }
//
//                    break;
            }
        }
    }
    
    
    private void handleMoveThroughSetupPanel(MovementManeuverSetupPanel moveThroughSetupPanel) {
        Filter<Ability> abilityFilter = moveThroughSetupPanel.getMovementAbilityFilter();
        
        List<Ability> movementAbilities = new ArrayList<Ability>();
        
        Target source = moveThroughSetupPanel.getBattleEvent().getSource();
        
        AbilityIterator it = source.getAbilities();
        while(it.hasNext()) {
            Ability a = it.next();
            if ( abilityFilter == null || abilityFilter.includeElement(a) ) {
                movementAbilities.add(a);
            }
        }
        
        if ( movementAbilities.size() == 0 ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    AttackTreePanel.getAttackTreePanel().cancelAttack();
                }
            });
        }
        else {
            int index = (int)(Math.random() * movementAbilities.size());
            Ability m = movementAbilities.get(index);
            moveThroughSetupPanel.setMovementAbility(m);
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    AttackTreePanel.getAttackTreePanel().advanceNode();
                }
            });
        }
    }
    
    private Target getRandomTarget(SelectTargetPanel selectTargetPanel) {
        Filter<Target> filter = selectTargetPanel.getTargetFilter();
        
        Set<Target> targets = Battle.getCurrentBattle().getCombatants();
        Iterator<Target> it = targets.iterator();
        while(it.hasNext()) {
            Target t = it.next();
            if ( filter != null && filter.includeElement(t) == false ) {
                it.remove();
            }
        }
        
        
        Target t = null;
        
        if ( targets.size() > 0 ) {
            int r = (int)(Math.random() * targets.size());
            
            it = targets.iterator();
            for(int i = 0; i <= r; i++ ) {
                t = it.next();
            }
        }
        
        if ( t != null ) {
            System.out.println("    Targetting " + t.getName());
        } else {
            System.out.println("    Unable to select target during stress test (cause unknown)");
        }
        return t;
    }
    
    private void updateDialog() {
        
        
        
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                StressTestDialog std = StressTestDialog.getStressTestDialog();
                std.update();
            }
        });
        
    }
    
    private interface StressAction {
        public void performAction();
    }
    
    private class AbilityStressAction implements StressAction {
        
        ATAbilityNode abilityNode;
        
        public AbilityStressAction(ATAbilityNode abilityNode) {
            this.abilityNode = abilityNode;
        }
        
        public void performAction() {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    abilityNode.triggerDefaultAction( new MouseEvent(mouseEventPanel , MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0,0,0, 1, false));
                }
            });
        }
        
        public String toString() {
            Ability a = abilityNode.getAbility();
            if ( a.isActivated(null) ) {
                return a.getName() + " (Deactivating)";
            } else {
                return a.getName() + " (Activating)";
            }
        }
    }
    
    private class ActionStressAction implements StressAction {
        
        ATActionNode actionNode;
        
        public ActionStressAction(ATActionNode actionNode) {
            this.actionNode = actionNode;
        }
        
        public void performAction() {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    actionNode.triggerDefaultAction( new MouseEvent(mouseEventPanel, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0,0,0, 1, false) );
                }
            });
        }
        
        public String toString() {
            try {
            return (String)actionNode.getAction().getValue(Action.NAME);
            }
            catch (Exception e ) {
                System.out.println("");
            }
            return "Doh!";
        }
    }
    
    public int getActionCount() {
        return actionCount;
    }
    
    public void setActionCount(int actionCount) {
        this.actionCount = actionCount;
        
        long time = System.currentTimeMillis() - startTime;
        if ( time > 0 ) {
            setActionsPerSecond( (double)actionCount / time * 1000);
            
        }
        else {
            setActionsPerSecond( 0 );
        }
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public boolean isTestingSave() {
        return testingSave;
    }

    public double getActionsPerSecond() {
        return actionsPerSecond;
    }

    public void setActionsPerSecond(double actionsPerSecond) {
        this.actionsPerSecond = actionsPerSecond;
    }

    
    
    
}
