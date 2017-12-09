/*
 * HexTarget.java
 *
 * Created on December 15, 2000, 12:29 PM
 */

package champions;

/**
 *
 * @author  unknown
 * @version 
 */
public class HexTarget extends Target {
    boolean adjacent; 
    /** Creates new HexTarget */
    public HexTarget(boolean adjacent) {
        setFireChangeByDefault(false);
        
        this.adjacent = adjacent;
        
        setName("Hex");
        
        add("Target.HASDEFENSES",  "FALSE",  true);
        add("Target.USESHITLOCATION",  "FALSE",  true);
        add("Target.HASSENSES",  "FALSE",  true);
        add("Target.ISALIVE", "FALSE", true);
    }
    
        /** Returns the DCV of the character based upon the current DEX value.
     *
     * This does not take into account any effect on the character.
     */
    public int getBaseDCV() {
        return (adjacent) ? 0 : 3;
    }
    
    /** Returns the OCV of the character based upon the current DEX value.
     *
     * This does not take into account any effect on the character.
     */
    public int getBaseOCV() {
        return (adjacent) ? 0 : 3;
    }
    
    /** Returns the ECV of the character based upon the current DEX value.
     *
     * This does not take into account any effect on the character.
     *
     * According to Hero System rules, a character does not get this ECV unless they
     * have the power MentalDefense.
     */
    public int getBaseECV() {
        return (adjacent) ? 0 : 3;
    }
    public boolean suffersDCVPenaltyDueToSenses(){
        return false;
    }
    
}