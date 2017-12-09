/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champions;

/**
 *
 * @author twalker
 */
public class LinkedEffect extends Effect {
    protected Ability ability;

    public LinkedEffect(String name, String ctype, boolean unique) {
        super(name, ctype, unique);
    }

    public LinkedEffect(String name, String ctype) {
        super(name, ctype);
    }

    public LinkedEffect(String name) {
        super(name, "LINKED");
    }

    public LinkedEffect(String name, String ctype, boolean unique, Ability ability) {
        super(name, ctype, unique);
        this.ability = ability;
    }
    
    public LinkedEffect(Ability ability) {
        super(ability.getName(), "LINKED");
        this.ability = ability;
    }

    public LinkedEffect(String name, String ctype, Ability ability) {
        super(name, ctype);
        this.ability = ability;
    }

    public LinkedEffect(String name, Ability ability) {
        super(name, "LINKED");
        this.ability = ability;
    }

    public Ability getAbility() {
        return ability;
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    
    
}
