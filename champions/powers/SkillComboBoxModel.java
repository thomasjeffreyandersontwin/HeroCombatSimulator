package champions.powers;

import champions.Ability;
import champions.AbilityAlias;
import champions.Skill;
import champions.Target;
import champions.interfaces.AbilityIterator;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SkillComboBoxModel extends DefaultComboBoxModel implements ChangeListener {

    private Target target;
    private List objects;

    public SkillComboBoxModel(Target target) {
        super();
        setTarget(target);
    }

    private void updateModel() {
        objects = new ArrayList();
    //    objects.add("None");
        if (target != null && target.getAbilityList() != null) {
            AbilityIterator ai = target.getAbilities();
            for (; ai.hasNext();) {
                Ability a = ai.next();
                if (a.getPower() instanceof Skill) {
                    objects.add(new AbilityAlias(target, a));
                }
            }
        }
        fireContentsChanged(this, 0, objects.size());
    }

    public int getSize() {
        return objects == null ? 0 : objects.size();
    }

    public Object getElementAt(int index) {
        
        return objects.get(index);
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        if (this.target != target) {
            if (this.target != null) {
                this.target.getAbilityList().removeChangeListener(this);
            }
            this.target = target;
            if (this.target != null) {
                this.target.getAbilityList().addChangeListener(this);
            }
            updateModel();
        }
    }

    public void stateChanged(ChangeEvent e) {
        updateModel();
    }
}
