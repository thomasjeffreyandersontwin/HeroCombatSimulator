/*
 * SplashScreen.java
 *
 * Created on December 24, 2000, 11:27 AM
 */

package champions;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JWindow;
import javax.swing.Timer;

/**
 *
 * @author  unknown
 * @version
 */
public class SplashScreen extends JWindow
implements ActionListener {

    private javax.swing.JLabel splashLabel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JProgressBar progressBar;
    private Timer timer;
    private int count = 0;
    private int progressMax = 0; // Maximum progress count
    private int progressCurrent = 0; // Location of start of current module
    private int progressOffset = 0; // Offset within module
    private int progressModuleMax = 0; // Maximum Offset for current module

    static private String module = "";
    static private SplashScreen ss = null;
    static private Object[][] moduleList = null;
    
    /** Holds value of property setDescription. */
    static private String description;
    
    /** Creates new SplashScreen */
    public SplashScreen(Object[][] moduleList) {

        initComponents();

        timer = new Timer(300, this);

        pack();

        ss = this;
        
        setModuleList(moduleList);
    }
    
    
    public SplashScreen() {

        initComponents();

        timer = new Timer(300, this);

        pack();

        ss = this;
    }

    private void initComponents () {
        ImageIcon icon = new ImageIcon( getClass ().getResource ("/graphics/splash_simple.gif"));
        splashLabel = new javax.swing.JLabel (icon);
        statusLabel = new javax.swing.JLabel ();
        versionLabel = new javax.swing.JLabel ();
        progressBar = new javax.swing.JProgressBar();
        
        getContentPane ().setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        splashLabel.setText ("");
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        getContentPane ().add (splashLabel, gridBagConstraints1);
        
        progressBar.setOpaque(false);
        progressBar.setForeground( new Color(77,177,81));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new Insets(2,5,2,5);
        getContentPane().add(progressBar, gridBagConstraints1);
        
        statusLabel.setText("");
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.insets = new Insets(0,0,0,0);
        getContentPane ().add (statusLabel, gridBagConstraints1);
        
        versionLabel.setText("");
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        getContentPane ().add (versionLabel, gridBagConstraints1);

        versionLabel.setBackground( Color.black );
        versionLabel.setForeground( new Color(77,177,81));
        versionLabel.setFont(new java.awt.Font (GlobalFontSettings.Font, Font.BOLD, (int) (22 * GlobalFontSettings.SizeMagnification)));
        
        

        statusLabel.setBackground( Color.black );
        statusLabel.setForeground( new Color(77,177,81));
        statusLabel.setFont(new java.awt.Font (GlobalFontSettings.Font, Font.BOLD, (int) GlobalFontSettings.SizeMagnification));

        this.getContentPane().setBackground( Color.black );

    }

    public void setVersion ( String version ) {
        this.versionLabel.setText(version+" ");
    }

    public void showSplash() {
        pack();
        centerSplash();
        count = 0;
        statusLabel.setText(" Loading");
        this.setVisible(true);
        timer.start();
    }

    public void hideSplash() {
        this.setVisible(false);
        timer.stop();
    }

    static public void disposeSplash() {
        if ( ss != null ) {
            ss.setVisible(false);
            if ( ss.getParent() != null) {
                ss.getParent().remove(ss);
            }
            ss.timer.removeActionListener(ss);
            ss.dispose();
            ss= null;
        }
    }

    public void centerSplash() {
        Dimension d;
        Point loc;
        Container parent = getParent();

        d = Toolkit.getDefaultToolkit ().getScreenSize ();
        loc = new Point(0,0);

        Dimension m = getSize ();

        d.width -= m.width;
        d.height -= m.height;
        d.width /= 2;
        d.height /= 2;
        int x = (int)loc.getX() + d.width;
        if ( x < 0 ) x = 0;
        int y = (int)loc.getY() + d.height;
        if ( y < 0 ) y = 0;
        setLocation (x,y);
    }

    /** Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        count ++;

        if ( count == 4 ) count = 0;

        update();

    }

    public void update() {
        String s;

        if ( ! description.equals("") ) {
            s = " " + description;
        }
        else {
            s = " Loading";
        }

        for(int i = 0; i < count; i++ ) {
            s = s + ".";
        }

        statusLabel.setText(s);
    }
    
    public void setModuleList(Object[][] list) {
        moduleList = list;
        
        int index;
        int count = 0;
        
        for(index=0;index<moduleList.length;index++) {
            count += ((Integer)moduleList[index][1]).intValue();
        }
        
        setProgressMax( count );
        setProgressCurrent( 0 );
        setProgressVisible(true);
    }

    static public void setModule(String name) {
        if ( ss != null && module != null && module.equals( name ) == false ) {
            
            description = name;
            
            ss.progressCurrent += ss.progressModuleMax;
            ss.progressOffset = 0;
            ss.updateProgress();
            module = name;

            ss.count = 0;
            ss.update();
            
            if ( moduleList != null ) {
                int index;
                int size = 0;
                for(index = 0; index< moduleList.length; index++) {
                    if ( ((String)moduleList[index][0]).equals( module ) ) {
                        size = ((Integer)moduleList[index][1]).intValue();
                        break;
                    }   
                }
                
                ss.progressModuleMax = size;
            }
        }
    }
    
    static public void advangeProgress() {
        if ( ss != null ) {
            if ( ss.progressOffset < ss.progressModuleMax ) {
                ss.progressOffset ++;
                ss.updateProgress();
            }
        }
    }
    
    public void setProgressMax(int max) {
        progressMax = max;
        progressBar.setMaximum(max); 
    }
    
    public void setProgressCurrent(int current) {
        progressCurrent = current;
        progressBar.setValue(progressCurrent + progressOffset); 
    }
    
    public void setProgressVisible(boolean visible) {
        progressBar.setVisible(visible);
    }
    
    public void updateProgress() {
        progressBar.setValue(progressCurrent + progressOffset);
    }
    
    /** Getter for property setDescription.
     * @return Value of property setDescription.
     *
     */
    static public String getDescription() {
        return description;
    }
    
    /** Setter for property setDescription.
     * @param setDescription New value of property setDescription.
     *
     */
    static public void setDescription(String description) {
        if ( ss != null && ss.isVisible() ) {
            SplashScreen.description = description;
            ss.update();
        }
    }
    
}