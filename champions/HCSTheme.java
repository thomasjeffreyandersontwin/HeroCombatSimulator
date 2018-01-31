/*
 * HCSTheme.java
 *
 * Created on January 14, 2001, 9:53 PM
 */

package champions;

import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import java.awt.*;
import javax.swing.*;
/**
 *
 * @author  unknown
 * @version 
 */
public class HCSTheme extends DefaultMetalTheme {

    
    private final ColorUIResource primary1 = new ColorUIResource(68,154,239);
    //private final ColorUIResource primary1 = new ColorUIResource(23,85,132);
    private final ColorUIResource primary2 = new ColorUIResource(153, 153, 204);
    private final ColorUIResource primary3 = new ColorUIResource(11,88,144);

    private final ColorUIResource secondary1 = new ColorUIResource(102, 102, 102);
    private final ColorUIResource secondary2 = new ColorUIResource(88,174,255);
    private final ColorUIResource secondary3 = new ColorUIResource(23,85,132);

    private FontUIResource controlFont;
    private FontUIResource systemFont;
    private FontUIResource userFont;
    private FontUIResource smallFont;
    
    /** Creates new HCSTheme */
    public HCSTheme() {
    }
    
    public String getName() {
        return "HCSTheme";
    }
    



// these are blue in Metal Default Theme
    protected ColorUIResource getPrimary1() { return primary1; } 
    protected ColorUIResource getPrimary2() { return primary2; }
    protected ColorUIResource getPrimary3() { return primary3; }

// these are gray in Metal Default Theme
    protected ColorUIResource getSecondary1() { return secondary1; }
    protected ColorUIResource getSecondary2() { return secondary2; }
    protected ColorUIResource getSecondary3() { return secondary3; }


    // note the properties listed here can currently be used by people
    // providing runtimes to hint what fonts are good.  For example the bold 
    // dialog font looks bad on a Mac, so Apple could use this property to 
    // hint at a good font.
    //
    // However, we don't promise to support these forever.  We may move 
    // to getting these from the swing.properties file, or elsewhere.

    public FontUIResource getControlTextFont() { 
	if (controlFont == null) {
	    try {		
		controlFont = new FontUIResource(Font.getFont("swing.plaf.metal.controlFont", new Font("Dialog", Font.BOLD, 24)));
	    } catch (Exception e) {
		controlFont = new FontUIResource("Dialog", Font.BOLD, 12);
	    }
	}
	return controlFont;
    }

    public FontUIResource getSystemTextFont() { 
	if (systemFont == null) {
	    try {		
		systemFont = new FontUIResource(Font.getFont("swing.plaf.metal.systemFont", new Font("Dialog", Font.PLAIN, 24)));
	    } catch (Exception e) {
		systemFont =  new FontUIResource("Dialog", Font.PLAIN, 12);
	    }
	}	
	return systemFont;
    }

    public FontUIResource getUserTextFont() { 
	if (userFont == null) {
	    try {		
		userFont = new FontUIResource(Font.getFont("swing.plaf.metal.userFont", new Font("Dialog", Font.PLAIN, 24)));
	    } catch (Exception e) {
		userFont =  new FontUIResource("Dialog", Font.PLAIN, 12);
	    }
	}	
	return userFont;
    }

    public FontUIResource getMenuTextFont() { 
	if (controlFont == null) {
	    try {		
		controlFont = new FontUIResource(Font.getFont("swing.plaf.metal.controlFont", new Font("Dialog", Font.BOLD, 24)));
	    } catch (Exception e) {
		controlFont = new FontUIResource("Dialog", Font.BOLD, 12);
	    }
	}
	return controlFont;
    }

    public FontUIResource getWindowTitleFont() { 
	if (controlFont == null) {
	    try {		
		controlFont = new FontUIResource(Font.getFont("swing.plaf.metal.controlFont", new Font("Dialog", Font.BOLD, 24)));
	    } catch (Exception e) {
		controlFont = new FontUIResource("Dialog", Font.BOLD, 12);
	    }
	}
	return controlFont;
    }
    public FontUIResource getSubTextFont() { 
	if (smallFont == null) {
	    try {		
		smallFont = new FontUIResource(Font.getFont("swing.plaf.metal.smallFont", new Font("Dialog", Font.PLAIN, 20)));
	    } catch (Exception e) {
		smallFont = new FontUIResource("Dialog", Font.PLAIN, 10);
	    }
	}	
	return smallFont;
    }

}