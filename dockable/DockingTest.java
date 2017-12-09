/*
 * DockingTest.java
 *
 * Created on December 31, 2000, 4:59 PM
 */

package dockable;

import javax.swing.*;

import java.util.*;
import java.awt.Rectangle;
import java.awt.Toolkit;
/**
 *
 * @author  unknown
 * @version
 */
public class DockingTest extends Object implements DockingPanelListener {

    int count5 = 0;
    /** Creates new DockingTest */
    public DockingTest() {
        
        Icon a =  new ImageIcon( Toolkit.getDefaultToolkit().getClass().getResource("/graphics/hitLocationIcon.gif") );
        Icon b =  new ImageIcon( Toolkit.getDefaultToolkit().getClass().getResource("/graphics/targetIcon2.gif") );
        
        
       // UIManager.put("DockingSplitPaneUI", "dockable.DockingSplitPaneUI");
        java.util.Timer timer = new java.util.Timer("GUI Update timer");
        
        
                DockingPanel dp = new DockingPanel("Panel 1");
        dp.getContentPane().add ( new JLabel("DP #1" ) );
        dp.setName( "Panel 1" );
        dp.setLayoutBounds( new Rectangle(0,0,300,150));

        DockingPanel dp2 = new DockingPanel("Panel 2");
        dp2.getContentPane().add ( new JLabel("DP #2" ) );
        dp2.setName( "Panel 2" );
        dp2.setLayoutBounds(new Rectangle(300,0,300,150));

        DockingPanel dp3 = new DockingPanel("Panel 3");
        dp3.getContentPane().add ( new JLabel("DP #3" ) );
        dp3.setName( "Panel 3" );
        dp3.setLayoutBounds(new Rectangle(0,150,200,200));
        dp3.setPanelIcon(b);
        dp3.setMinimizable(true);

        DockingPanel dp4 = new DockingPanel("Panel 4");
        dp4.getContentPane().add ( new JLabel("DP #4" ) );
        dp4.setName( "Panel 4" );
        dp4.setLayoutBounds(new Rectangle(200,150,200,200));

        DockingPanel dp5 = new DockingPanel("Panel 5");
        final JLabel label5 = new JLabel();
        dp5.getContentPane().add ( label5 );
        dp5.setName( "Panel 5" );
        dp5.setLayoutBounds(new Rectangle(400,150,200,200));
        dp5.setMinimizable(true);
        dp5.setPanelIcon(a);
        
        timer.schedule(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        label5.setText( "Count 5: " + (++count5));
                    }
                });
            }
        }, 3000, 1000);
        


     //   TestDocker dt = new TestDocker();

     //   DockingFrame df = dt.dockIntoFrame();
     //   df.setVisible(true);
        /*  HashSet hs = new HashSet();

        hs.add(dp);
        hs.add(dp2);

        DockingFrame.layoutPanels(hs);

        hs = new HashSet();

        hs.add(dp2);
        hs.add(dp3);

        hs.add(dp5);

        DockingFrame.layoutPanels(hs);

        hs = new HashSet();
        hs.add(dp2);
        hs.add(dp);

        hs.add(dp3);
        hs.add(dp4);
        hs.add(dp5);

        DockingFrame.layoutPanels(hs); */

        //  DockingFrame df = new DockingFrame();
        Double id = new Double(5);
        
        dp.addDockingPanelListener(this);
        dp2.addDockingPanelListener(this);
        dp3.addDockingPanelListener(this);
        dp4.addDockingPanelListener(this);
        dp5.addDockingPanelListener(this);

        DockingFrame f = dp.dockIntoFrame(id,null);
        
        f = dp2.dockIntoFrame(id,null);
        f = dp3.dockIntoFrame(id,null);
        f = dp4.dockIntoFrame(id,null);
        f = dp5.dockIntoFrame(id,null);
        
        f.setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main (String args[]) {
    new DockingTest();




        //System.exit(0);
    }

    public void layoutChanged(javax.swing.event.ChangeEvent e) {
        DockingPanel dp = (DockingPanel)e.getSource();
        System.out.println ( "LayoutChange for " + e.getSource() + " to " + dp.getLayoutBounds() );
    }

    public void frameChanged(DockingPanel panel, DockingFrame oldFrame, DockingFrame newFrame) {
    }
}