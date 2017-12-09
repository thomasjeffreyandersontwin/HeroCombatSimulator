/*
 * AbilityAction.java
 *
 * Created on January 10, 2001, 11:15 AM
 */

package champions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;


/**
 *
 * @author  trewalke
 * @version
 */
public class AbilityAction extends AbstractAction {
    
    private Ability ability = null;
    private ActivationInfo activationInfo = null;
    /** Holds value of property setType. */
    private int type = -1;
    /** Holds value of property setSource. */
    private Target source = null;
    /** Holds value of property beExtra. */
    private DetailList beExtra = null;
    /** Creates new AbilityAction to Activate Ability
     */
    public AbilityAction(String name, Ability ability) {
        super(name);
        this.ability = ability;
        setType(BattleEvent.ACTIVATE);
    }
    
        /** Creates new AbilityAction to Activate Ability w/Additional parameters
         */
    public AbilityAction(String name, Ability ability, DetailList beExtra) {
        this(name,ability);
        
    }
    
            /** Creates new AbilityAction to perform some action on already activated ability
             */
    public AbilityAction(String name, ActivationInfo ai, int type) {
        super(name);
        setType (type);
    }
    
    public AbilityAction(String name, ActivationInfo ai, int type, DetailList beExtra) {
        this(name,ai,type);
        setBeExtra(beExtra);
    }
    
    /** Getter for property setType.
     * @return Value of property setType.
     */
    public int getType() {
        return type;
    }
    /** Setter for property setType.
     * @param setType New value of property setType.
     */
    public void setType(int type) {
        this.type = type;
    }
    /** Getter for property setSource.
     * @return Value of property setSource.
     */
    public Target getSource() {
        return source;
    }
    /** Setter for property setSource.
     * @param setSource New value of property setSource.
     */
    
    public void setSource(Target source) {
        this.source = source;
    }
    
    public void actionPerformed( ActionEvent e) {
        if ( Battle.currentBattle != null ) {
            if ( ability != null ) {
                BattleEvent be = new BattleEvent( ability );
                if ( source != null ) be.setSource(source);
                if ( beExtra != null ) {
                    be.addAll(beExtra,true);
                }
                
                
                Battle.currentBattle.addEvent(be);
            }
            else if ( activationInfo != null ) {
                BattleEvent be = new BattleEvent( type, activationInfo );
                if ( source != null ) be.setSource(source);
                if ( beExtra != null ) {
                    be.addAll(beExtra,true);
                }
                
                Battle.currentBattle.addEvent(be);
            }
        }
    }
    /** Getter for property beExtra.
     * @return Value of property beExtra.
     */
        public DetailList getBeExtra() {
            return beExtra;
        }
    /** Setter for property beExtra.
     * @param beExtra New value of property beExtra.
     */
        public void setBeExtra(DetailList beExtra) {
            this.beExtra = beExtra;
        }
        
        public boolean isEnabled() {
            if ( ability == null ) return false;
            return ability.isEnabled(null);
        }
}