/*
 * StatChangeType.java
 *
 * Created on February 18, 2008, 2:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

/**
 *
 * @author twalker
 */
public enum StatChangeType {
    DAMAGE("took", " "),
    AID_RECEIVED("received", " aid to "),
    HEALING_RECEIVED("received", " healing to "),
    RECOVERY("recovered", " "),
    INCREASE("gained", " to "),
    DECREASE("lost", " from "),
    SET("set", " to "),
    BURNED("burned", " "),
    AID_LOST("lost", " aid from "),
    DRAIN_LOST("lost", " drain from "),
    DRAIN_RECOVERED("recovered", " drain to ");
    
    private String verb;
    private String connector;
    
    StatChangeType(String verb, String connector) {
        this.verb = verb;
        this.connector = connector;
    }

    public String getVerb() {
        return verb;
    }

    public String getConnector() {
        return connector;
    }
}
