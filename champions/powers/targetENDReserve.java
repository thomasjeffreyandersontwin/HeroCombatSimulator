/*
 * targetENDReserve.java
 *
 * Created on May 4, 2001, 4:01 PM
 */

package champions.powers;

import champions.Ability;
import champions.Roster;
import champions.Target;

/**
 *
 * @author  twalker
 * @version
 */
public class targetENDReserve extends Target {
    static final long serialVersionUID = 5295848683348707403L;
    
    static private Object[][] parameterArray = {
        {"EndLevel","Power.ENDLEVEL", Integer.class, new Integer(10)},
        {"RecLevel","Power.RECLEVEL", Integer.class, new Integer(1)}
    };
    
    /** Creates new targetENDReserve */
    public targetENDReserve(String name, effectENDReserve reserve) {
        super();
        setName(name);
        Ability ability = reserve.getAbility();
        
        createCharacteristic("END");
        setBaseStat("END", ((Integer)ability.parseParameter(parameterArray,"EndLevel")).intValue());
        setCurrentStat("END", ((Integer)ability.parseParameter(parameterArray,"EndLevel")).intValue());
        
        createCharacteristic("REC");
        setBaseStat("REC", ((Integer)ability.parseParameter(parameterArray,"RecLevel")).intValue());
        setCurrentStat("REC", ((Integer)ability.parseParameter(parameterArray,"RecLevel")).intValue());
        
        add("Target.ISALIVE",  "FALSE",  true);
        
        add("Target.HASDEFENSES",  "FALSE",  true);
        add("Target.USESHITLOCATION",  "FALSE",  true);
        add("Target.HASSENSES",  "FALSE",  true);
        
        add("Target.CANBEKNOCKEDBACK",  "FALSE",  true);
    }
    
    public void setEffect(effectENDReserve reserve) {
        this.add("Target.ENDRESERVEEFFECT", reserve,true);
    }
    
    public effectENDReserve getAbility() {
        Object o = getValue("Target.ENDRESERVEEFFECT");
        if ( o != null ) return (effectENDReserve)o;
        return null;
    }
    
    public String getENDString(int endCost) {
        if ( endCost == 0 ) return "";
        
       int end = getCurrentStat("END");
       return Integer.toString(endCost) + "/" + Integer.toString(end);
    }
    
    public boolean canBurnStun() {
        return false;
    }
    
    /** Returns the text to place in a tooltip when the mouse is held over the discription.
     *
     *  This can include html text if desired.
     */
    public String getENDTooltip(int endCost) {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<HTML>");
        sb.append(" <B>").append(endCost).append("</B> end to use.<BR>");
        int end = getCurrentStat("END");
        sb.append(" <B>").append(end).append("</B> end remaining.<BR>");
        sb.append("<P>");
        sb.append(" This power use the <I>").append(getName()).append("</I> end reserve.");
        sb.append("</HTML>");
       
       return sb.toString();
    }
}
