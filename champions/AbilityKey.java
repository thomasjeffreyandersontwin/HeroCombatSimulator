/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package champions;

/**
 *
 * @author twalker
 */
public class AbilityKey {

    protected Ability ability;

    public AbilityKey(Ability ability) {
        this.ability = ability;
    }

    @Override
    public String toString() {
        return "Key: " + ability;
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof AbilityKey && ability == ((AbilityKey) obj).ability;
    }

    @Override
    public int hashCode() {
        return ability == null ? 27 : ability.hashCode();
    }

    public Ability getAbility() {
        return ability;
    }
}


