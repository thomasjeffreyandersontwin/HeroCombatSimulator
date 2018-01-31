/*
 * InfoDockingPanel2.java
 *
 * Created on January 15, 2001, 8:45 PM
 */

package champions;

import champions.event.SegmentAdvancedEvent;
import champions.event.SequenceChangedEvent;
import champions.event.TargetSelectedEvent;
import champions.interfaces.BattleListener;
import dockable.DockingFrameListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import javax.swing.*;

import javax.swing.event.ChangeEvent;





/**
 *
 * @author  unknown
 */
public class EligibleDockingPanel extends SavedDockingPanel {
    
    public static EligibleModel eligibleModel;
    //private OnDeckModel onDeckModel;
    //private SelectedTargetModel selectedTargetModel;
    
    //private BattleSequence battleSequence = new BattleSequence();
    //private BattleSequence battleEligible = new BattleSequence();
    
    private Action undoAction, redoAction, playAction, ffAction, showStatsAction, advanceSegmentAction, startBattleAction, purgeAction;
    /** Creates new form InfoDockingPanel2 */
    public EligibleDockingPanel() {
        this("eligibleDP");
    }
    
    public EligibleDockingPanel(String windowID) {
        super(windowID);
        initComponents();
        setName("Eligible Characters");
        setFrameName("Hero Combat Simulator");
        
        // listGroup.setLayout( new ConstrainedScrollPaneLayout( listScroll, listGroup, SwingConstants.HORIZONTAL ));
        // onDeckPanel.setLayout( new ConstrainedFlowLayout( FlowLayout.LEFT, 0, 2, SwingConstants.HORIZONTAL));
        eligiblePanel.setLayout( new ConstrainedFlowLayout( FlowLayout.LEFT, 0, 2, SwingConstants.HORIZONTAL));
        
        //  getDockingFrame().setJMenuBar(jMenuBar1);
        
        eligibleModel = new EligibleModel();
        // onDeckModel = new OnDeckModel();
        
        //onDeckList.setModel(onDeckModel);
        eligiblePanel.setModel(eligibleModel);
//        undoAction = new AbstractAction() {
//            public void actionPerformed(ActionEvent e) {
//                if ( Battle.currentBattle != null ) {
//                    Battle.currentBattle.undoCompletedEvent();
//                }
//            }
//        };
//
//        undoButton.setAction( undoAction );
//
//        redoAction = new AbstractAction() {
//            public void actionPerformed(ActionEvent e) {
//                if ( Battle.currentBattle != null ) {
//                    Battle.currentBattle.redoCompletedEvent();
//                }
//            }
//        };
//        redoButton.setAction( redoAction );
//
//        startBattleAction = new AbstractAction("Start Battle") {
//            public void actionPerformed(ActionEvent e) {
//                if ( Battle.currentBattle != null && Battle.currentBattle.isStopped()) {
//                    Battle.currentBattle.setStopped(false);
//                    BattleEvent be = new BattleEvent(BattleEvent.ADVANCE_TIME, false);
//                    Battle.currentBattle.addEvent(be);
//                }
//            }
//        };
//        startBattleItem.setAction(startBattleAction);
//
//        purgeAction = new AbstractAction("Purge Completed Events...") {
//            public void actionPerformed(ActionEvent e) {
//                Object[] options = { "Purge", "CANCEL" };
//                int result = JOptionPane.showOptionDialog(null, "Are you sure you wish to purge old battle events?  Purging will restrict the undoability of old events.", "Purge Events",
//                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
//                    null, options, options[0]);
//
//                if ( result == JOptionPane.OK_OPTION ) {
//                    ProgressMonitor pm = new ProgressMonitor(InfoDockingPanel2.this, "Purging BattleEvents", "", 0, 1);
//                    pm.setMillisToDecideToPopup(500);
//
//                    Battle.currentBattle.purgeCompletedEvents(1);
//
//                    pm.setProgress(1);
//                }
//
//            }
//
//        };
//        purgeBattleEvents.setAction(purgeAction);
//
//        playAction = new AbstractAction() {
//            public void actionPerformed(ActionEvent e) {
//                if ( Battle.debugLevel >= 3 ) System.out.println( "Play Button Time: " + Long.toString(System.currentTimeMillis()) );
//                if ( Battle.currentBattle != null ) {
//                    if ( Battle.currentBattle.isStopped() ) {
//                        Battle.currentBattle.setStopped(false);
//                    }
//                    BattleEvent be = new BattleEvent(BattleEvent.ADVANCE_TIME, false);
//                    Battle.currentBattle.addEvent(be);
//                }
//            }
//        };
//
//        playButton.setAction( playAction );
//
//        advanceSegmentAction = new AbstractAction() {
//            public void actionPerformed(ActionEvent e) {
//                if ( Battle.debugLevel >= 3 ) System.out.println( "Play Button Time: " + Long.toString(System.currentTimeMillis()) );
//                if ( Battle.currentBattle != null ) {
//                    Chronometer stopTime = (Chronometer)Battle.currentBattle.getTime().clone();
//                    stopTime.setTime( stopTime.getTime() + 1 );
//                    BattleEvent be = new BattleEvent(stopTime);
//                    be.setForcedAdvance(true);
//                    Battle.currentBattle.addEvent(be);
//                }
//            }
//        };
//
//        advanceSegmentButton.setAction( advanceSegmentAction );
//
//        ffAction = new AbstractAction() {
//            public void actionPerformed(ActionEvent e) {
//                if ( Battle.currentBattle != null ) {
//                    if ( Battle.currentBattle.isStopped() ) {
//                        Battle.currentBattle.setStopped(false);
//                    }
//                    BattleEvent be = new BattleEvent(BattleEvent.ADVANCE_TIME, true);
//                    Battle.currentBattle.addEvent(be);
//                }
//
//            }
//        };
//        ffButton.setAction( ffAction );
//
//      /*  showStatsAction = new AbstractAction("View Battle Stats") {
//           public void actionPerformed(ActionEvent e) {
//                if ( Battle.currentBattle != null ) {
//                    JFrame frame = new JFrame("Battle Stats");
//                    StatPanel sp = new StatPanel();
//                    sp.setBattle( Battle.currentBattle );
//                    frame.getContentPane().add(sp);
//                    frame.pack();
//                    frame.setVisible(true);
//                }
//
//            }
//
//        };
//
//        showStats.setAction(showStatsAction); */
//
//        setupIcons();
//
//        undoButton.setToolTipText( "Undo Event" );
//        redoButton.setToolTipText( "Redo Event" );
//        playButton.setToolTipText( "Advance to next Active Character" );
//        ffButton.setToolTipText( "Advance to next Active Character (Skip Remaining Eligible)" );
//        advanceSegmentButton.setToolTipText( "Advance to next Segment" );
//
//        Battle.addBattleListener(this);
//
//        updateControls();
//
//        about.addActionListener(new AbstractAction() {
//            public void actionPerformed(ActionEvent e) {
//                AboutDialog ad = new AboutDialog(InfoDockingPanel2.this.getDockingFrame(), true);
//                ad.setVisible(true);
//            }
//        });
        
        setMinimizable(true);
    }
    
//    public void setupIcons() {
//        Icon i;
//
//        // Set Icon for Undo
//        if ( (i = UIManager.getIcon( "BattleControls.undoIcon" )) != null ) {
//            undoButton.setIcon(i);
//            undoButton.setPreferredSize( new Dimension( i.getIconWidth(), i.getIconHeight() ));
//        }
//        if ( (i = UIManager.getIcon( "BattleControls.undoIcon.pressed" )) != null ) undoButton.setPressedIcon(i);
//        if ( (i = UIManager.getIcon( "BattleControls.undoIcon.selected" )) != null ) undoButton.setSelectedIcon(i);
//        if ( (i = UIManager.getIcon( "BattleControls.undoIcon.disabled" )) != null ) undoButton.setDisabledIcon(i);
//        if ( (i = UIManager.getIcon( "BattleControls.undoIcon.rollover" )) != null ) {
//            undoButton.setRolloverIcon(i);
//            undoButton.setRolloverEnabled(true);
//        }
//        else {
//            undoButton.setRolloverEnabled(false);
//        }
//
//        // Set Icon for Redo
//        if ( (i = UIManager.getIcon( "BattleControls.redoIcon" )) != null ) {
//            redoButton.setIcon(i);
//            redoButton.setPreferredSize( new Dimension( i.getIconWidth(), i.getIconHeight() ));
//        }
//        if ( (i = UIManager.getIcon( "BattleControls.redoIcon.pressed" )) != null ) redoButton.setPressedIcon(i);
//        if ( (i = UIManager.getIcon( "BattleControls.redoIcon.selected" )) != null ) redoButton.setSelectedIcon(i);
//        if ( (i = UIManager.getIcon( "BattleControls.redoIcon.disabled" )) != null ) redoButton.setDisabledIcon(i);
//        if ( (i = UIManager.getIcon( "BattleControls.redoIcon.rollover" )) != null ) {
//            redoButton.setRolloverIcon(i);
//            redoButton.setRolloverEnabled(true);
//        }
//        else {
//            redoButton.setRolloverEnabled(false);
//        }
//
//        // Set Icon for Next Character
//        if ( (i = UIManager.getIcon( "BattleControls.advanceCharacterIcon" )) != null ) {
//            playButton.setIcon(i);
//            playButton.setPreferredSize( new Dimension( i.getIconWidth(), i.getIconHeight() ));
//        }
//        if ( (i = UIManager.getIcon( "BattleControls.advanceCharacterIcon.pressed" )) != null ) playButton.setPressedIcon(i);
//        if ( (i = UIManager.getIcon( "BattleControls.advanceCharacterIcon.selected" )) != null ) playButton.setSelectedIcon(i);
//        if ( (i = UIManager.getIcon( "BattleControls.advanceCharacterIcon.disabled" )) != null ) playButton.setDisabledIcon(i);
//        if ( (i = UIManager.getIcon( "BattleControls.advanceCharacterIcon.rollover" )) != null ) {
//            playButton.setRolloverIcon(i);
//            playButton.setRolloverEnabled(true);
//        }
//        else {
//            playButton.setRolloverEnabled(false);
//        }
//
//        // Set Icon for Advance Segment
//        if ( (i = UIManager.getIcon( "BattleControls.forceAdvanceIcon" )) != null ) {
//            ffButton.setIcon(i);
//            ffButton.setPreferredSize( new Dimension( i.getIconWidth(), i.getIconHeight() ));
//        }
//        if ( (i = UIManager.getIcon( "BattleControls.forceAdvanceIcon.pressed" )) != null ) ffButton.setPressedIcon(i);
//        if ( (i = UIManager.getIcon( "BattleControls.forceAdvanceIcon.selected" )) != null ) ffButton.setSelectedIcon(i);
//        if ( (i = UIManager.getIcon( "BattleControls.forceAdvanceIcon.disabled" )) != null ) ffButton.setDisabledIcon(i);
//        if ( (i = UIManager.getIcon( "BattleControls.forceAdvanceIcon.rollover" )) != null ) {
//            ffButton.setRolloverIcon(i);
//            ffButton.setRolloverEnabled(true);
//        }
//        else {
//            ffButton.setRolloverEnabled(false);
//        }
//
//        // Set Icon for Advance Single Segment
//        if ( (i = UIManager.getIcon( "BattleControls.advanceSingleSegmentIcon" )) != null ) {
//            advanceSegmentButton.setIcon(i);
//            advanceSegmentButton.setPreferredSize( new Dimension( i.getIconWidth(), i.getIconHeight() ));
//        }
//        if ( (i = UIManager.getIcon( "BattleControls.advanceSingleSegmentIcon.pressed" )) != null ) advanceSegmentButton.setPressedIcon(i);
//        if ( (i = UIManager.getIcon( "BattleControls.advanceSingleSegmentIcon.selected" )) != null ) advanceSegmentButton.setSelectedIcon(i);
//        if ( (i = UIManager.getIcon( "BattleControls.advanceSingleSegmentIcon.disabled" )) != null ) advanceSegmentButton.setDisabledIcon(i);
//        if ( (i = UIManager.getIcon( "BattleControls.advanceSingleSegmentIcon.rollover" )) != null ) {
//            advanceSegmentButton.setRolloverIcon(i);
//            advanceSegmentButton.setRolloverEnabled(true);
//        }
//        else {
//            advanceSegmentButton.setRolloverEnabled(false);
//        }
//    }
    
    
    
    
//    public void windowClosing(WindowEvent e) {
//        CombatSimulator.exit();
//    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        listScroll = new javax.swing.JScrollPane();
        listGroup = new javax.swing.JPanel();
        eligiblePanel = new champions.HTMLButtonPanel();

        setName("Info");
        listGroup.setLayout(new java.awt.BorderLayout());

        eligiblePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 1));

        eligiblePanel.setFont(new java.awt.Font("Dialog", 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        eligiblePanel.setMinimumSize(new java.awt.Dimension(100, 10));
        eligiblePanel.setName("Eligible");
        eligiblePanel.setOpaque(false);
        listGroup.add(eligiblePanel, java.awt.BorderLayout.CENTER);
        eligiblePanel.setBackground(Color.WHITE);

        listScroll.setViewportView(listGroup);

        getContentPane().add(listScroll, java.awt.BorderLayout.CENTER);

    }
    // </editor-fold>//GEN-END:initComponents
    
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
  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private champions.HTMLButtonPanel eligiblePanel;
    private javax.swing.JPanel listGroup;
    private javax.swing.JScrollPane listScroll;
    // End of variables declaration//GEN-END:variables
    
//  public void battleTargetSelected(TargetSelectedEvent e) {
//  }
//  public void battleSegmentAdvanced(SegmentAdvancedEvent e) {
//  }
//  public void battleSequenceChanged(SequenceChangedEvent e) {
//  }
//  public void stateChanged(ChangeEvent e) {
//      updateControls();
//  }
//
//  public void updateControls() {
//      if ( Battle.currentBattle != null ) {
//          if ( Battle.currentBattle.isStopped() == true ) {
//              startBattleAction.setEnabled(true);
//              playButton.setToolTipText( "Start Battle" );
//              undoAction.setEnabled(false);
//              redoAction.setEnabled(false);
//              playAction.setEnabled(true);
//              advanceSegmentAction.setEnabled(false);
//              ffAction.setEnabled(false);
//          }
//          else if ( Battle.currentBattle.isProcessing() == false ) {
//              startBattleAction.setEnabled(false);
//              undoAction.setEnabled( Battle.currentBattle.isUndoable() );
//              redoAction.setEnabled( Battle.currentBattle.isRedoable() );
//
//              // Grab the Sequences...
//              Battle.currentBattle.getSequencer().getBattleSequence(battleSequence, 1,false);
//              Battle.currentBattle.getSequencer().getBattleEligible(battleEligible);
//
//              // Enable the play button whenever the first target in the sequence isn't the active target...
//              // This only typically happens when a character has aborted.
//              playAction.setEnabled( battleSequence.size() > 0 && Battle.currentBattle.getActiveTarget() != battleSequence.get(0).getTarget() );
//              playButton.setToolTipText( "Advance to next Active Character" );
//              // Enable the forceFoward button when there are no more characters available in the segment, but there are
//              // Held characters.
//              if ( battleSequence.size() == 0 ) {
//                  String s;
//                  Target nextTarget = null;
//                  StringBuffer sb;
//                  boolean skip;
//
//                  // Generate the furture battleSequence...
//                  Battle.currentBattle.getSequencer().getBattleSequence(battleSequence, 1, true);
//
//                  // Check the first battleSequence character's to determine the round which will be advanced to,
//                  // then build a list of the characters which will be skipped...
//                  advanceSegmentAction.setEnabled( true );
//
//                  // Grab a chronical with time = currentTime + 1;
//                  Chronometer nextTime = (Chronometer)Battle.getCurrentBattle().getTime().clone();
//                  nextTime.incrementSegment();
//
//                  sb = new StringBuffer();
//                  skip = false;
//
//                  if ( nextTime.isTurnEnd() == false ) {
//
//                      for ( int index = battleSequence.size()-1; index >= 0 ; index--) {
//                          if ( battleSequence.get(index).getTime().equals(nextTime) ) {
//                              Object t = battleSequence.get(index).getTarget();
//                              if ( t instanceof Target ) {
//                                  nextTarget = (Target)t;
//                                  if ( nextTarget != null && battleEligible.containsTarget(nextTarget) ) {
//                                      sb.append( nextTarget.getName() );
//                                      sb.append( "<P>" );
//                                      skip = true;
//                                  }
//                              }
//                          }
//                      }
//                  }
//
//                  if ( skip ) {
//                      s = "Force Advance to next Segment.<P><P>The following Character(s) will lose their held action(s):<P><P>" + sb.toString();
//                  }
//                  else {
//                      s = "Force Advance to next Segment.<P><P>No Character will lose held actions.";
//                  }
//                  advanceSegmentButton.setToolTipText( ChampionsUtilities.createWrappedHTMLString(s, 40) );
//
//
//                  // Check the first battleSequence character's to determine the round which will be advanced to,
//                  // then build a list of the characters which will be skipped...
//                  ffAction.setEnabled( true );
//
//
//                  sb = new StringBuffer();
//                  skip = false;
//
//                  if ( battleSequence.size() > 0 ) {
//                      nextTime = battleSequence.get(0).getTime();
//
//                      if ( nextTime.isTurnEnd() == false ) {
//
//                          for ( int index = battleSequence.size() -1; index >= 0 ; index--) {
//                              if ( battleSequence.get(index).getTime().equals(nextTime) ) {
//                                  Object t = battleSequence.get(index).getTarget();
//                                  if ( t instanceof Target ) {
//                                      nextTarget = (Target)t;
//                                      if ( nextTarget != null && battleEligible.containsTarget(nextTarget) ) {
//                                          sb.append( nextTarget.getName() );
//                                          sb.append( "<P>" );
//                                          skip = true;
//                                      }
//                                  }
//                              }
//                          }
//                      }
//                  }
//
//                          if ( skip == true ) {
//                              s = "Force Advance to next Active Character.<P><P>The following Character(s) will lose their held action(s):<P><P>" + sb.toString();
//                          }
//                          else {
//                              s = "Force Advance to next Active Character.<P><P>No Character will lose held actions.";
//                          }
//                          ffButton.setToolTipText( ChampionsUtilities.createWrappedHTMLString(s, 40) );
//                      }
//                      else {
//                          ffAction.setEnabled( false );
//                          advanceSegmentAction.setEnabled( false );
//                      }
//
//
//
//
//                  }
//                  else {
//                      startBattleAction.setEnabled(false);
//                      undoAction.setEnabled(false);
//                      redoAction.setEnabled(false);
//                      playAction.setEnabled(false);
//                      ffAction.setEnabled(false);
//                      //holdAction.setEnabled(false);
//                  }
//              }
//              else {
//                  startBattleAction.setEnabled(false);
//                  undoAction.setEnabled(false);
//                  redoAction.setEnabled(false);
//                  playAction.setEnabled(false);
//                  ffAction.setEnabled(false);
//                  //holdAction.setEnabled(false);
//              }
//          }
//          /** Invoked the first time a window is made visible.
//           */
//          public void windowOpened(WindowEvent e) {
//          }
//          /** Invoked when a window has been closed as the result
//           * of calling dispose on the window.
//           */
//          public void windowClosed(WindowEvent e) {
//          }
//          /** Invoked when a window is changed from a normal to a
//           * minimized state. For many platforms, a minimized window
//           * is displayed as the icon specified in the window's
//           * iconImage property.
//           * @see java.awt.Frame#setIconImage
//           */
//          public void windowIconified(WindowEvent e) {
//          }
//          /** Invoked when a window is changed from a minimized
//           * to a normal state.
//           */
//          public void windowDeiconified(WindowEvent e) {
//          }
//          /** Invoked when the window is set to be the user's
//           * active window, which means the window (or one of its
//           * subcomponents) will receive keyboard events.
//           */
//          public void windowActivated(WindowEvent e) {
//          }
//          /** Invoked when a window is no longer the user's active
//           * window, which means that keyboard events will no longer
//           * be delivered to the window or its subcomponents.
//           */
//          public void windowDeactivated(WindowEvent e) {
//          }
//
 public void initSkin() {
	getContentPane().setBackground((Color) UIManager.get("AbilityEditor.background"));
}
//        Object o;
//
//        if ( (o = UIManager.get("Info.background") ) != null ) {
//            getContentPane().setBackground((Color)o);
//        }
//
//        if ( (o = UIManager.get("Info.Chronometer.background") ) != null ) {
//            chronicalPanel.setBackground((Color)o);
//        }
//
//        if ( (o = UIManager.get("Info.Control.background") ) != null ) {
//            controlPanel.setBackground((Color)o);
//        }
//
//        if ( (o = UIManager.get("Info.ActiveTarget.background") ) != null ) {
//            activePanel.setBackground((Color)o);
//        }
//
//        if ( (o = UIManager.get("Info.OnDeck.background") ) != null ) {
//            onDeckPanel.setBackground((Color)o);
//        }
//
//        if ( (o = UIManager.get("Info.Eligible.background") ) != null ) {
//            eligiblePanel.setBackground((Color)o);
//        }
//
//        if ( (o = UIManager.get("Info.Chronometer.Text.foreground") ) != null ) {
//            chronicalPanel.setForeground((Color)o);
//        }
//
//        if ( (o = UIManager.get("Info.ActiveTarget.Text.foreground") ) != null ) {
//            activePanel.setForeground((Color)o);
//        }
//
//        if ( (o = UIManager.get("Info.OnDeck.Text.foreground") ) != null ) {
//            onDeckPanel.setForeground((Color)o);
//        }
//
//        if ( (o = UIManager.get("Info.Eligible.Text.foreground") ) != null ) {
//            eligiblePanel.setForeground((Color)o);
//        }
//
//        if ( (o = UIManager.get("Info.Chronometer.border") ) != null ) {
//            chronicalPanel.setBorder((Border)o);
//        }
//
//        if ( (o = UIManager.get("Info.ActiveTarget.border") ) != null ) {
//            activePanel.setBorder((Border)o);
//        }
//
//        if ( (o = UIManager.get("Info.OnDeck.border") ) != null ) {
//            onDeckPanel.setBorder((Border)o);
//        }
//
//        if ( (o = UIManager.get("Info.Eligible.border") ) != null ) {
//            eligiblePanel.setBorder((Border)o);
//        }
//    } */
//
//          public void eventNotification(ChangeEvent e) {
//              updateControls();
//          }
//          public void processingChange(ChangeEvent e) {
//              updateControls();
//          }
//          public void combatStateChange(ChangeEvent e) {
//          }
    
}