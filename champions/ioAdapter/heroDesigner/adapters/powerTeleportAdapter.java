/*
 * powerEnergyBlast.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;

/**
 *
 * @author  1425
 */
public class powerTeleportAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "TELEPORTATION";
    private static String[][] translationArray = {
        { "LEVELS", "DistanceFromCollision" },
        { "ADDER(XMLID=POSITIONSHIFT)", "PositionShift", "trueSpecial" },
        { "ADDER(XMLID=IMPROVEDNONCOMBAT).LEVELS", "NoncombatX" },
        { "ADDER(XMLID=INCREASEDMASS).LEVELS", "MassMultiplier" }
    };
    
    /** Returns the XMLID for this Power.
     *
     * Subclass should either override this to return their XMLID or override
     * the identify method to do more complicated identification tasks.
     */
    public String getXMLID() {
        return XMLID;
    }
    
    /** Returns the Translation Array for the PowerAdapter.
     *
     * The Subclass should either override this to return their translationArray
     *or override the importXML method to do more complicated import tasks.
     */
    public String[][] getTranslationArray() {
        return translationArray;
    }
    
}
