/*
 * InfoDockingPanel2.java
 *
 * Created on January 15, 2001, 8:45 PM
 */

package champions;

import champions.event.SegmentAdvancedEvent;
import champions.event.SequenceChangedEvent;
import champions.event.TargetSelectedEvent;
import champions.exception.BattleEventException;
import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.BattleListener;
import dockable.DockingFrameListener;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;


/**
 *
 * @author  unknown
 */
public class ControlPanelDockingPanel extends SavedDockingPanel
        implements BattleListener, DockingFrameListener{
    
//    private EligibleModel eligibleModel;
//    private OnDeckModel onDeckModel;
//    private SelectedTargetModel selectedTargetModel;
    
    private BattleSequence battleSequence = new BattleSequence();
    private BattleSequence battleEligible = new BattleSequence();
    
    private Action undoAction, redoAction, playAction, ffAction, showStatsAction, advanceSegmentAction;
    private Action startBattleAction, purgeAction, configureBattleAction;
    /** Creates new form InfoDockingPanel2 */
    
    public ControlPanelDockingPanel() {
        this("controlsDP");
    }
    
    public ControlPanelDockingPanel(String windowID) {
        super(windowID);
        initComponents();
        setName("Battle Controls");
        setFrameName("Hero Combat Simulator");
        
        //      listGroup.setLayout( new ConstrainedScrollPaneLayout( listScroll, listGroup, SwingConstants.HORIZONTAL ));
        // onDeckPanel.setLayout( new ConstrainedFlowLayout( FlowLayout.LEFT, 0, 2, SwingConstants.HORIZONTAL));
        //      eligiblePanel.setLayout( new ConstrainedFlowLayout( FlowLayout.LEFT, 0, 2, SwingConstants.HORIZONTAL));
        
        getDockingFrame().setJMenuBar(jMenuBar1);
        
        //     eligibleModel = new EligibleModel();
        //     onDeckModel = new OnDeckModel();
        
        //      onDeckList.setModel(onDeckModel);
        //      eligiblePanel.setModel(eligibleModel);
        
        
        undoAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if ( Battle.currentBattle != null ) {
                    try {
              
                        Battle.currentBattle.undoCompletedEvent();
                    } catch (BattleEventException ex) {
                        ExceptionWizard.postException(ex);
                    }
                }
            }
        };
        
        undoButton.setAction( undoAction );
        
        redoAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if ( Battle.currentBattle != null ) {
                    try {
                        Battle.currentBattle.redoCompletedEvent();
                    } catch (BattleEventException ex) {
                        ExceptionWizard.postException(ex);
                    }
                }
            }
        };
        redoButton.setAction( redoAction );
        
        startBattleAction = new AbstractAction("Start Battle") {
            public void actionPerformed(ActionEvent e) {
                if ( Battle.currentBattle != null && Battle.currentBattle.isStopped()) {
                    Battle.currentBattle.setStopped(false);
                    BattleEvent sbe = new ConfigureBattleBattleEvent();
                    Battle.currentBattle.addEvent(sbe);
                    //BattleEvent be = new BattleEvent(BattleEvent.ADVANCE_TIME, false);
                    //Battle.currentBattle.addEvent(be);
                }
            }
        };
        startBattleItem.setAction(startBattleAction);
        
        purgeAction = new AbstractAction("Purge Completed Events...") {
            public void actionPerformed(ActionEvent e) {
                Object[] options = { "Purge", "CANCEL" };
                int result = JOptionPane.showOptionDialog(null, "Are you sure you wish to purge old battle events?  Purging will restrict the undoability of old events.", "Purge Events",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, options, options[0]);
                
                if ( result == JOptionPane.OK_OPTION ) {
                    ProgressMonitor pm = new ProgressMonitor(ControlPanelDockingPanel.this, "Purging BattleEvents", "", 0, 1);
                    pm.setMillisToDecideToPopup(500);
                    
                    Battle.currentBattle.purgeCompletedEvents(1);
                    
                    pm.setProgress(1);
                }
                
            }
            
        };
        purgeBattleEvents.setAction(purgeAction);
        
        playAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if ( Battle.debugLevel >= 3 ) System.out.println( "Play Button Time: " + Long.toString(System.currentTimeMillis()) );
                if ( Battle.currentBattle != null ) {
                    if ( Battle.currentBattle.isStopped() ) {
                        Battle.currentBattle.setStopped(false);
                        BattleEvent sbe = new ConfigureBattleBattleEvent();
                        Battle.currentBattle.addEvent(sbe);
                    } else {
                        BattleEvent be = new BattleEvent(BattleEvent.ADVANCE_TIME, false);
                        Battle.currentBattle.addEvent(be);
                    }
                }
            }
        };
        
        playButton.setAction( playAction );
        
        advanceSegmentAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if ( Battle.debugLevel >= 3 ) System.out.println( "Play Button Time: " + Long.toString(System.currentTimeMillis()) );
                if ( Battle.currentBattle != null ) {
                    Chronometer stopTime = (Chronometer)Battle.currentBattle.getTime().clone();
                    stopTime.setTime( stopTime.getTime() + 1 );
                    BattleEvent be = new BattleEvent(stopTime);
                    be.setForcedAdvance(true);
                    Battle.currentBattle.addEvent(be);
                }
            }
        };
        
        advanceSegmentButton.setAction( advanceSegmentAction );
        
        ffAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if ( Battle.currentBattle != null ) {
                    if ( Battle.currentBattle.isStopped() ) {
                        Battle.currentBattle.setStopped(false);
                    }
                    BattleEvent be = new BattleEvent(BattleEvent.ADVANCE_TIME, true);
                    Battle.currentBattle.addEvent(be);
                }
                
            }
        };
        ffButton.setAction( ffAction );
        
        configureBattleAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if ( Battle.currentBattle != null ) {
                    BattleEvent be = new ConfigureBattleBattleEvent(false);
                    Battle.currentBattle.addEvent(be);
                }
            }
            
            public boolean isEnabled() {
                return Battle.currentBattle != null && Battle.currentBattle.isStopped() == false;
            }
        };
        
        configureBattleButton.setAction(configureBattleAction);
        
      /*  showStatsAction = new AbstractAction("View Battle Stats") {
           public void actionPerformed(ActionEvent e) {
                if ( Battle.currentBattle != null ) {
                    JFrame frame = new JFrame("Battle Stats");
                    StatPanel sp = new StatPanel();
                    sp.setBattle( Battle.currentBattle );
                    frame.getContentPane().add(sp);
                    frame.pack();
                    frame.setVisible(true);
                }
       
            }
       
        };
       
        showStats.setAction(showStatsAction); */
        
        setupIcons();
        
        undoButton.setToolTipText( "Undo Event" );
        redoButton.setToolTipText( "Redo Event" );
        playButton.setToolTipText( "Advance to next Active Character" );
        ffButton.setToolTipText( "Advance to next Active Character (Skip Remaining Eligible)" );
        advanceSegmentButton.setToolTipText( "Advance to next Segment" );
        
        Battle.addBattleListener(this);
        
        updateControls();
        
        about.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                AboutDialog ad = new AboutDialog(ControlPanelDockingPanel.this.getDockingFrame(), true);
                ad.setVisible(true);
            }
        });
        
        setMinimizable(true);
    }
    
    public void setupIcons() {
        Icon i;
        
        // Set Icon for Undo
        if ( (i = UIManager.getIcon( "BattleControls.undoIcon" )) != null ) {
            undoButton.setIcon(i);
            undoButton.setPreferredSize( new Dimension( i.getIconWidth(), i.getIconHeight() ));
        }
        if ( (i = UIManager.getIcon( "BattleControls.undoIcon.pressed" )) != null ) undoButton.setPressedIcon(i);
        if ( (i = UIManager.getIcon( "BattleControls.undoIcon.selected" )) != null ) undoButton.setSelectedIcon(i);
        if ( (i = UIManager.getIcon( "BattleControls.undoIcon.disabled" )) != null ) undoButton.setDisabledIcon(i);
        if ( (i = UIManager.getIcon( "BattleControls.undoIcon.rollover" )) != null ) {
            undoButton.setRolloverIcon(i);
            undoButton.setRolloverEnabled(true);
        } else {
            undoButton.setRolloverEnabled(false);
        }
        
        // Set Icon for Redo
        if ( (i = UIManager.getIcon( "BattleControls.redoIcon" )) != null ) {
            redoButton.setIcon(i);
            redoButton.setPreferredSize( new Dimension( i.getIconWidth(), i.getIconHeight() ));
        }
        if ( (i = UIManager.getIcon( "BattleControls.redoIcon.pressed" )) != null ) redoButton.setPressedIcon(i);
        if ( (i = UIManager.getIcon( "BattleControls.redoIcon.selected" )) != null ) redoButton.setSelectedIcon(i);
        if ( (i = UIManager.getIcon( "BattleControls.redoIcon.disabled" )) != null ) redoButton.setDisabledIcon(i);
        if ( (i = UIManager.getIcon( "BattleControls.redoIcon.rollover" )) != null ) {
            redoButton.setRolloverIcon(i);
            redoButton.setRolloverEnabled(true);
        } else {
            redoButton.setRolloverEnabled(false);
        }
        
        // Set Icon for Next Character
        if ( (i = UIManager.getIcon( "BattleControls.advanceCharacterIcon" )) != null ) {
            playButton.setIcon(i);
            playButton.setPreferredSize( new Dimension( i.getIconWidth(), i.getIconHeight() ));
        }
        if ( (i = UIManager.getIcon( "BattleControls.advanceCharacterIcon.pressed" )) != null ) playButton.setPressedIcon(i);
        if ( (i = UIManager.getIcon( "BattleControls.advanceCharacterIcon.selected" )) != null ) playButton.setSelectedIcon(i);
        if ( (i = UIManager.getIcon( "BattleControls.advanceCharacterIcon.disabled" )) != null ) playButton.setDisabledIcon(i);
        if ( (i = UIManager.getIcon( "BattleControls.advanceCharacterIcon.rollover" )) != null ) {
            playButton.setRolloverIcon(i);
            playButton.setRolloverEnabled(true);
        } else {
            playButton.setRolloverEnabled(false);
        }
        
        // Set Icon for Advance Segment
        if ( (i = UIManager.getIcon( "BattleControls.forceAdvanceIcon" )) != null ) {
            ffButton.setIcon(i);
            ffButton.setPreferredSize( new Dimension( i.getIconWidth(), i.getIconHeight() ));
        }
        if ( (i = UIManager.getIcon( "BattleControls.forceAdvanceIcon.pressed" )) != null ) ffButton.setPressedIcon(i);
        if ( (i = UIManager.getIcon( "BattleControls.forceAdvanceIcon.selected" )) != null ) ffButton.setSelectedIcon(i);
        if ( (i = UIManager.getIcon( "BattleControls.forceAdvanceIcon.disabled" )) != null ) ffButton.setDisabledIcon(i);
        if ( (i = UIManager.getIcon( "BattleControls.forceAdvanceIcon.rollover" )) != null ) {
            ffButton.setRolloverIcon(i);
            ffButton.setRolloverEnabled(true);
        } else {
            ffButton.setRolloverEnabled(false);
        }
        
        // Set Icon for Advance Single Segment
        if ( (i = UIManager.getIcon( "BattleControls.advanceSingleSegmentIcon" )) != null ) {
            advanceSegmentButton.setIcon(i);
            advanceSegmentButton.setPreferredSize( new Dimension( i.getIconWidth(), i.getIconHeight() ));
        }
        if ( (i = UIManager.getIcon( "BattleControls.advanceSingleSegmentIcon.pressed" )) != null ) advanceSegmentButton.setPressedIcon(i);
        if ( (i = UIManager.getIcon( "BattleControls.advanceSingleSegmentIcon.selected" )) != null ) advanceSegmentButton.setSelectedIcon(i);
        if ( (i = UIManager.getIcon( "BattleControls.advanceSingleSegmentIcon.disabled" )) != null ) advanceSegmentButton.setDisabledIcon(i);
        if ( (i = UIManager.getIcon( "BattleControls.advanceSingleSegmentIcon.rollover" )) != null ) {
            advanceSegmentButton.setRolloverIcon(i);
            advanceSegmentButton.setRolloverEnabled(true);
        } else {
            advanceSegmentButton.setRolloverEnabled(false);
        }
        
        if ( (i = UIManager.getIcon( "BattleControls.configureBattleIcon" )) != null ) {
            configureBattleButton.setIcon(i);
            configureBattleButton.setPreferredSize( new Dimension( i.getIconWidth(), i.getIconHeight() ));
        }
    }
    
    public void windowClosing(WindowEvent e) {
        CombatSimulator.exit();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        UIManager.put("MenuBar.font", new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        UIManager.put("MenuItem.font", new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        jMenuBar1 = new javax.swing.JMenuBar();
       
        fileMenu = new javax.swing.JMenu();
        fileMenu.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        performance = new javax.swing.JMenuItem();
        preferencesMenu = new javax.swing.JMenuItem();
        stressTestMenu = new javax.swing.JMenuItem();
        
        jSeparator3 = new javax.swing.JSeparator();
        exitMenu = new javax.swing.JMenuItem();
        exitMenu .setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        battleMenu = new javax.swing.JMenu();
        battleMenu.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        
        newBattle = new javax.swing.JMenuItem();
        openBattle = new javax.swing.JMenuItem();
        saveBattle = new javax.swing.JMenuItem();
        saveAsBattle = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        startBattleItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        purgeBattleEvents = new javax.swing.JMenuItem();
        rosterMenu = new javax.swing.JMenu();
        rosterMenu.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        
        newRoster = new javax.swing.JMenuItem();
        openRoster = new javax.swing.JMenuItem();
        characterMenu = new javax.swing.JMenu();
        characterMenu .setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
                
        newCharacter = new javax.swing.JMenuItem();
        
        openCharacter = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        importMenu = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        editProfiles = new javax.swing.JMenuItem();
        
        objectMenu = new javax.swing.JMenu();
        objectMenu.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        
        newObjectMenu = new javax.swing.JMenuItem();
        openObjectMenu = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpMenu.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        
        about = new javax.swing.JMenuItem();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        playButton = new javax.swing.JButton();
        advanceSegmentButton = new javax.swing.JButton();
        ffButton = new javax.swing.JButton();
        configureBattleButton = new javax.swing.JButton();

        fileMenu.setText("File");
        fileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuActionPerformed(evt);
            }
        });

        performance.setText("Performance Monitor...");
        performance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                performanceActionPerformed(evt);
            }
        });

        fileMenu.add(performance);

        preferencesMenu.setText("Preferences...");
        preferencesMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preferencesMenuActionPerformed(evt);
            }
        });

        fileMenu.add(preferencesMenu);

        stressTestMenu.setText("Stress Test...");
        stressTestMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stressTestMenuActionPerformed(evt);
            }
        });

        fileMenu.add(stressTestMenu);

        fileMenu.add(jSeparator3);

        exitMenu.setText("Exit");
        exitMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuActionPerformed(evt);
            }
        });

        fileMenu.add(exitMenu);

        jMenuBar1.add(fileMenu);

        battleMenu.setText("Battle");
        newBattle.setText("New Battle...");
        newBattle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newBattleActionPerformed(evt);
            }
        });

        battleMenu.add(newBattle);

        openBattle.setText("Open Battle...");
        openBattle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openBattleActionPerformed(evt);
            }
        });

        battleMenu.add(openBattle);

        saveBattle.setText("Save Battle");
        saveBattle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBattleActionPerformed(evt);
            }
        });

        battleMenu.add(saveBattle);

        saveAsBattle.setText("Save Battle As...");
        saveAsBattle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsBattleActionPerformed(evt);
            }
        });

        battleMenu.add(saveAsBattle);

        battleMenu.add(jSeparator4);

        startBattleItem.setText("Start Battle");
        battleMenu.add(startBattleItem);

        battleMenu.add(jSeparator5);

        purgeBattleEvents.setText("Purge Completed Battle Events...");
        battleMenu.add(purgeBattleEvents);

        jMenuBar1.add(battleMenu);

        rosterMenu.setText("Roster");
        newRoster.setText("New Roster...");
        newRoster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newRosterActionPerformed(evt);
            }
        });

        rosterMenu.add(newRoster);

        openRoster.setText("Open Roster...");
        openRoster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openRosterActionPerformed(evt);
            }
        });

        rosterMenu.add(openRoster);

        jMenuBar1.add(rosterMenu);

        characterMenu.setText("Character");
        newCharacter.setText("New Character...");
        newCharacter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newCharacterActionPerformed(evt);
            }
        });

        characterMenu.add(newCharacter);

        openCharacter.setText("Open Character...");
        openCharacter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openCharacterActionPerformed(evt);
            }
        });

        characterMenu.add(openCharacter);

        characterMenu.add(jSeparator2);

        importMenu.setText("Import Character...");
        importMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importMenuActionPerformed(evt);
            }
        });

        characterMenu.add(importMenu);

        characterMenu.add(jSeparator1);

        editProfiles.setText("Edit Combat Profiles...");
        editProfiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editProfilesActionPerformed(evt);
            }
        });

        characterMenu.add(editProfiles);

        jMenuBar1.add(characterMenu);

        objectMenu.setText("Objects");
        newObjectMenu.setText("New Object...");
        newObjectMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newObjectMenuActionPerformed(evt);
            }
        });

        objectMenu.add(newObjectMenu);

        openObjectMenu.setText("Open Object...");
        openObjectMenu.setFont(new java.awt.Font(GlobalFontSettings.Font, 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        openObjectMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openObjectMenuActionPerformed(evt);
            }
        });

        objectMenu.add(openObjectMenu);

        jMenuBar1.add(objectMenu);

        helpMenu.setText("Help");
        about.setLabel("About HCS...");
        helpMenu.add(about);

        jMenuBar1.add(helpMenu);

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setName("Info");
        undoButton.setBackground(new java.awt.Color(255, 0, 51));
        undoButton.setBorderPainted(false);
        undoButton.setContentAreaFilled(false);
        undoButton.setMaximumSize(new java.awt.Dimension(64, 64));
        undoButton.setMinimumSize(new java.awt.Dimension(24, 24));
        undoButton.setName("Undo");
        undoButton.setPreferredSize(new java.awt.Dimension(24, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        getContentPane().add(undoButton, gridBagConstraints);

        redoButton.setBackground(new java.awt.Color(51, 255, 51));
        redoButton.setBorderPainted(false);
        redoButton.setContentAreaFilled(false);
        redoButton.setMaximumSize(new java.awt.Dimension(64, 64));
        redoButton.setMinimumSize(new java.awt.Dimension(24, 24));
        redoButton.setName("Redo");
        redoButton.setPreferredSize(new java.awt.Dimension(24, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        getContentPane().add(redoButton, gridBagConstraints);

        playButton.setBackground(new java.awt.Color(255, 0, 51));
        playButton.setBorderPainted(false);
        playButton.setContentAreaFilled(false);
        playButton.setMaximumSize(new java.awt.Dimension(64, 64));
        playButton.setMinimumSize(new java.awt.Dimension(24, 24));
        playButton.setName("Advance");
        playButton.setPreferredSize(new java.awt.Dimension(24, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        getContentPane().add(playButton, gridBagConstraints);

        advanceSegmentButton.setBackground(new java.awt.Color(51, 255, 51));
        advanceSegmentButton.setBorderPainted(false);
        advanceSegmentButton.setContentAreaFilled(false);
        advanceSegmentButton.setMaximumSize(new java.awt.Dimension(64, 64));
        advanceSegmentButton.setMinimumSize(new java.awt.Dimension(24, 24));
        advanceSegmentButton.setName("Advance");
        advanceSegmentButton.setPreferredSize(new java.awt.Dimension(24, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        getContentPane().add(advanceSegmentButton, gridBagConstraints);

        ffButton.setBackground(new java.awt.Color(255, 0, 51));
        ffButton.setBorderPainted(false);
        ffButton.setContentAreaFilled(false);
        ffButton.setMaximumSize(new java.awt.Dimension(64, 64));
        ffButton.setMinimumSize(new java.awt.Dimension(24, 24));
        ffButton.setName("ForceAdvance");
        ffButton.setPreferredSize(new java.awt.Dimension(24, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        getContentPane().add(ffButton, gridBagConstraints);

        configureBattleButton.setBackground(new java.awt.Color(255, 0, 51));
        configureBattleButton.setBorderPainted(false);
        configureBattleButton.setContentAreaFilled(false);
        configureBattleButton.setMaximumSize(new java.awt.Dimension(64, 64));
        configureBattleButton.setMinimumSize(new java.awt.Dimension(24, 24));
        configureBattleButton.setName("ForceAdvance");
        configureBattleButton.setPreferredSize(new java.awt.Dimension(24, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        getContentPane().add(configureBattleButton, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
    private void stressTestMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stressTestMenuActionPerformed

        StressTestDialog std = StressTestDialog.getStressTestDialog();
        if ( std.isVisible() == false ) {
            std.setVisible(true);
        }
    }//GEN-LAST:event_stressTestMenuActionPerformed
    
    private void openObjectMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openObjectMenuActionPerformed
        // Add your handling code here:
        Object o = DetailList.open( new String[]{"hcs","tgt"}, "Open Character/Target", Target.class);
        if ( o != null ) {
            ((Target)o).editTarget();
        }
    }//GEN-LAST:event_openObjectMenuActionPerformed
    
    private void newObjectMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newObjectMenuActionPerformed
        // Add your handling code here:
        ObjectTarget ot = new ObjectTarget("New Object");
        ot.editTarget();
    }//GEN-LAST:event_newObjectMenuActionPerformed
    
    private void editProfilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editProfilesActionPerformed
        // Add your handling code here:
        ProfileEditor pe = ProfileEditor.getDefaultEditor();
        
        pe.show();
    }//GEN-LAST:event_editProfilesActionPerformed
    
    private void openCharacterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openCharacterActionPerformed
        // Add your handling code here:
        Object o = DetailList.open( new String[]{"hcs","tgt"}, "Open Character/Target", Target.class);
        if ( o != null ) {
            ((Target)o).editTarget();
        }
    }//GEN-LAST:event_openCharacterActionPerformed
    
    private void newCharacterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newCharacterActionPerformed
        // Add your handling code here:
        Character c = new Character("New Character");
        c.editTarget();
    }//GEN-LAST:event_newCharacterActionPerformed
    
    private void openBattleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openBattleActionPerformed
        // Add your handling code here:
        if ( Battle.currentBattle != null ) Battle.currentBattle.close();
        try{
            Battle b = Battle.open();
            //Battle.setCurrentBattle(b);
        } catch ( Exception exc ) {
            champions.exceptionWizard.ExceptionWizard.postException(exc);
        }
    }//GEN-LAST:event_openBattleActionPerformed
    
    private void saveAsBattleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsBattleActionPerformed
        // Add your handling code here:
        if ( Battle.currentBattle != null ) {
            try {
                Battle.currentBattle.save(null);
            } catch ( Exception exc ) {
                champions.exceptionWizard.ExceptionWizard.postException(exc);
            }
        }
    }//GEN-LAST:event_saveAsBattleActionPerformed
    
    private void openRosterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openRosterActionPerformed
        Roster roster = Roster.open();
        if ( roster != null ) {
            Battle.currentBattle.addRoster(roster);
            RosterDockingPanel rdp = RosterDockingPanel.getDefaultRosterDockingPanel();
            rdp.addRoster(roster);
        }
    }//GEN-LAST:event_openRosterActionPerformed
    
    private void preferencesMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preferencesMenuActionPerformed
        // Add your handling code here:
        PreferencesWindow pw = new PreferencesWindow();
        pw.setVisible(true);
    }//GEN-LAST:event_preferencesMenuActionPerformed
    
    private void performanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_performanceActionPerformed
        // Add your handling code here:
        PerformanceDockingPanel.showPerformanceMonitor();
    }//GEN-LAST:event_performanceActionPerformed
    
    private void newBattleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newBattleActionPerformed
        // Add your handling code here:
        if ( Battle.currentBattle != null ) Battle.currentBattle.close();
        Battle b = new Battle();
        b.setCurrentBattle(b);
    }//GEN-LAST:event_newBattleActionPerformed
    
    private void saveBattleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBattleActionPerformed
        // Add your handling code here:
        if ( Battle.currentBattle != null ) {
            
            new Thread( new Runnable() {
                public void run() {
                    try {
                        Battle.currentBattle.save();
                    } catch ( Exception exc ) {
                        champions.exceptionWizard.ExceptionWizard.postException(exc);
                    }
                }
            }).start();
            
        }
        
    }//GEN-LAST:event_saveBattleActionPerformed
    
    private void newRosterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newRosterActionPerformed
        // Add your handling code here:
        Roster roster = new Roster();
        Battle.currentBattle.addRoster(roster);
        RosterDockingPanel rdp = RosterDockingPanel.getDefaultRosterDockingPanel();
        rdp.addRoster(roster);
    }//GEN-LAST:event_newRosterActionPerformed
    
  private void importMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importMenuActionPerformed
      // Add your handling code here:
      Character.importCharacter(null);
  }//GEN-LAST:event_importMenuActionPerformed
  
  private void fileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuActionPerformed
      // Add your handling code here:
  }//GEN-LAST:event_fileMenuActionPerformed
  
  private void addRosterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRosterActionPerformed
      // Add your handling code here:
      // Add your handling code here:
      Roster roster = Roster.open();
      if ( roster != null ) {
          Battle.currentBattle.addRoster(roster);
          RosterDockingPanel rdp = RosterDockingPanel.getDefaultRosterDockingPanel();
          rdp.addRoster(roster);
      }
  }//GEN-LAST:event_addRosterActionPerformed
  
  private void exitMenuActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuActionPerformed
      // Add your handling code here:
      CombatSimulator.exit();
  }//GEN-LAST:event_exitMenuActionPerformed
  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem about;
    private javax.swing.JButton advanceSegmentButton;
    private javax.swing.JMenu battleMenu;
    private javax.swing.JMenu characterMenu;
    private javax.swing.JButton configureBattleButton;
    private javax.swing.JMenuItem editProfiles;
    private javax.swing.JMenuItem exitMenu;
    private javax.swing.JButton ffButton;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem importMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JMenuItem newBattle;
    private javax.swing.JMenuItem newCharacter;
    private javax.swing.JMenuItem newObjectMenu;
    private javax.swing.JMenuItem newRoster;
    private javax.swing.JMenu objectMenu;
    private javax.swing.JMenuItem openBattle;
    private javax.swing.JMenuItem openCharacter;
    private javax.swing.JMenuItem openObjectMenu;
    private javax.swing.JMenuItem openRoster;
    private javax.swing.JMenuItem performance;
    private javax.swing.JButton playButton;
    private javax.swing.JMenuItem preferencesMenu;
    private javax.swing.JMenuItem purgeBattleEvents;
    private javax.swing.JButton redoButton;
    private javax.swing.JMenu rosterMenu;
    private javax.swing.JMenuItem saveAsBattle;
    private javax.swing.JMenuItem saveBattle;
    private javax.swing.JMenuItem startBattleItem;
    private javax.swing.JMenuItem stressTestMenu;
    private javax.swing.JButton undoButton;
    // End of variables declaration//GEN-END:variables
    
    public void battleTargetSelected(TargetSelectedEvent e) {
    }
    public void battleSegmentAdvanced(SegmentAdvancedEvent e) {
    }
    public void battleSequenceChanged(SequenceChangedEvent e) {
    }
    public void stateChanged(BattleChangeEvent e) {
        updateControls();
    }
    
    public void updateControls() {
        if ( Battle.currentBattle != null ) {
            if ( Battle.currentBattle.isStopped() == true ) {
                startBattleAction.setEnabled(true);
                playButton.setToolTipText( "Start Battle" );
                undoAction.setEnabled(false);
                redoAction.setEnabled(false);
                playAction.setEnabled(true);
                advanceSegmentAction.setEnabled(false);
                ffAction.setEnabled(false);
                configureBattleButton.setEnabled(false);
            } else if ( Battle.currentBattle.isProcessing() == false ) {
                startBattleAction.setEnabled(false);
                undoAction.setEnabled( Battle.currentBattle.isUndoable() );
                redoAction.setEnabled( Battle.currentBattle.isRedoable() );
                
                // Grab the Sequences...
                Battle.currentBattle.getSequencer().getBattleSequence(battleSequence, 1,false);
                Battle.currentBattle.getSequencer().getBattleEligible(battleEligible);
                
                // Enable the play button whenever the first target in the sequence isn't the active target...
                // This only typically happens when a character has aborted.
                playAction.setEnabled( battleSequence.size() > 0 && Battle.currentBattle.getActiveTarget() != battleSequence.get(0).getTarget() );
                playButton.setToolTipText( "Advance to next Active Character" );
                // Enable the forceFoward button when there are no more characters available in the segment, but there are
                // Held characters.
                if ( battleSequence.size() == 0 ) {
                    String s;
                    Target nextTarget = null;
                    StringBuffer sb;
                    boolean skip;
                    
                    // Generate the furture battleSequence...
                    Battle.currentBattle.getSequencer().getBattleSequence(battleSequence, 1, true);
                    
                    // Check the first battleSequence character's to determine the round which will be advanced to,
                    // then build a list of the characters which will be skipped...
                    advanceSegmentAction.setEnabled( true );
                    
                    // Grab a chronical with time = currentTime + 1;
                    Chronometer nextTime = (Chronometer)Battle.getCurrentBattle().getTime().clone();
                    nextTime.incrementSegment();
                    
                    sb = new StringBuffer();
                    skip = false;
                    
                    if ( nextTime.isTurnEnd() == false ) {
                        
                        for ( int index = battleSequence.size()-1; index >= 0 ; index--) {
                            if ( battleSequence.get(index).getTime().equals(nextTime) ) {
                                Object t = battleSequence.get(index).getTarget();
                                if ( t instanceof Target ) {
                                    nextTarget = (Target)t;
                                    if ( nextTarget != null && battleEligible.containsTarget(nextTarget) ) {
                                        sb.append( nextTarget.getName() );
                                        sb.append( "<P>" );
                                        skip = true;
                                    }
                                }
                            }
                        }
                    }
                    
                    if ( skip ) {
                        s = "Force Advance to next Segment.<P><P>The following Character(s) will lose their held action(s):<P><P>" + sb.toString();
                    } else {
                        s = "Force Advance to next Segment.<P><P>No Character will lose held actions.";
                    }
                    advanceSegmentButton.setToolTipText( ChampionsUtilities.createWrappedHTMLString(s, 40) );
                    
                    
                    // Check the first battleSequence character's to determine the round which will be advanced to,
                    // then build a list of the characters which will be skipped...
                    ffAction.setEnabled( true );
                    
                    
                    sb = new StringBuffer();
                    skip = false;
                    
                    if ( battleSequence.size() > 0 ) {
                        nextTime = battleSequence.get(0).getTime();
                        
                        if ( nextTime.isTurnEnd() == false ) {
                            
                            for ( int index = battleSequence.size() -1; index >= 0 ; index--) {
                                if ( battleSequence.get(index).getTime().equals(nextTime) ) {
                                    Object t = battleSequence.get(index).getTarget();
                                    if ( t instanceof Target ) {
                                        nextTarget = (Target)t;
                                        if ( nextTarget != null && battleEligible.containsTarget(nextTarget) ) {
                                            sb.append( nextTarget.getName() );
                                            sb.append( "<P>" );
                                            skip = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    if ( skip == true ) {
                        s = "Force Advance to next Active Character.<P><P>The following Character(s) will lose their held action(s):<P><P>" + sb.toString();
                    } else {
                        s = "Force Advance to next Active Character.<P><P>No Character will lose held actions.";
                    }
                    ffButton.setToolTipText( ChampionsUtilities.createWrappedHTMLString(s, 40) );
                } else {
                    ffAction.setEnabled( false );
                    advanceSegmentAction.setEnabled( false );
                }
                
                
                configureBattleButton.setEnabled(true);
            } else {
                startBattleAction.setEnabled(false);
                undoAction.setEnabled(false);
                redoAction.setEnabled(false);
                playAction.setEnabled(false);
                ffAction.setEnabled(false);
                configureBattleButton.setEnabled(false);
                //holdAction.setEnabled(false);
            }
        } else {
            startBattleAction.setEnabled(false);
            undoAction.setEnabled(false);
            redoAction.setEnabled(false);
            playAction.setEnabled(false);
            ffAction.setEnabled(false);
            configureBattleButton.setEnabled(false);
            //holdAction.setEnabled(false);
        }
    }
    /** Invoked the first time a window is made visible.
     */
    public void windowOpened(WindowEvent e) {
    }
    /** Invoked when a window has been closed as the result
     * of calling dispose on the window.
     */
    public void windowClosed(WindowEvent e) {
    }
    /** Invoked when a window is changed from a normal to a
     * minimized state. For many platforms, a minimized window
     * is displayed as the icon specified in the window's
     * iconImage property.
     * @see java.awt.Frame#setIconImage
     */
    public void windowIconified(WindowEvent e) {
    }
    /** Invoked when a window is changed from a minimized
     * to a normal state.
     */
    public void windowDeiconified(WindowEvent e) {
    }
    /** Invoked when the window is set to be the user's
     * active window, which means the window (or one of its
     * subcomponents) will receive keyboard events.
     */
    public void windowActivated(WindowEvent e) {
    }
    /** Invoked when a window is no longer the user's active
     * window, which means that keyboard events will no longer
     * be delivered to the window or its subcomponents.
     */
    public void windowDeactivated(WindowEvent e) {
    }
    
  /*  public void initSkin() {
        Object o;
   
        if ( (o = UIManager.get("Info.background") ) != null ) {
            getContentPane().setBackground((Color)o);
        }
   
        if ( (o = UIManager.get("Info.Chronometer.background") ) != null ) {
            chronicalPanel.setBackground((Color)o);
        }
   
        if ( (o = UIManager.get("Info.Control.background") ) != null ) {
            controlPanel.setBackground((Color)o);
        }
   
        if ( (o = UIManager.get("Info.ActiveTarget.background") ) != null ) {
            activePanel.setBackground((Color)o);
        }
   
        if ( (o = UIManager.get("Info.OnDeck.background") ) != null ) {
            onDeckPanel.setBackground((Color)o);
        }
   
        if ( (o = UIManager.get("Info.Eligible.background") ) != null ) {
            eligiblePanel.setBackground((Color)o);
        }
   
        if ( (o = UIManager.get("Info.Chronometer.Text.foreground") ) != null ) {
            chronicalPanel.setForeground((Color)o);
        }
   
        if ( (o = UIManager.get("Info.ActiveTarget.Text.foreground") ) != null ) {
            activePanel.setForeground((Color)o);
        }
   
        if ( (o = UIManager.get("Info.OnDeck.Text.foreground") ) != null ) {
            onDeckPanel.setForeground((Color)o);
        }
   
        if ( (o = UIManager.get("Info.Eligible.Text.foreground") ) != null ) {
            eligiblePanel.setForeground((Color)o);
        }
   
        if ( (o = UIManager.get("Info.Chronometer.border") ) != null ) {
            chronicalPanel.setBorder((Border)o);
        }
   
        if ( (o = UIManager.get("Info.ActiveTarget.border") ) != null ) {
            activePanel.setBorder((Border)o);
        }
   
        if ( (o = UIManager.get("Info.OnDeck.border") ) != null ) {
            onDeckPanel.setBorder((Border)o);
        }
   
        if ( (o = UIManager.get("Info.Eligible.border") ) != null ) {
            eligiblePanel.setBorder((Border)o);
        }
    } */
    
    public void eventNotification(ChangeEvent e) {
        updateControls();
    }
    public void processingChange(BattleChangeEvent event) {
        updateControls();
    }
    public void combatStateChange(ChangeEvent e) {
    }
    
}