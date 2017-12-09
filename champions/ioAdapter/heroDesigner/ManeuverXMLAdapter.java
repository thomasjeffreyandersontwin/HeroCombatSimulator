/*
 * ManeuverXMLAdapter.java
 *
 * Created on May 29, 2004, 11:58 PM
 */

package champions.ioAdapter.heroDesigner;

/**
 *
 * @author  1425
 */
public interface ManeuverXMLAdapter extends PowerXMLAdapter {
    /** Returns an array of strings with possible DISPLAY attr for this maneuver.
     *
     */
    String[] getDisplayArray(); 
}
