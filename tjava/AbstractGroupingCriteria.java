/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tjava;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @param <T> Type of element accepted by the grouping criteria.
 * @author twalker
 */
public abstract class AbstractGroupingCriteria<T> implements GroupingCriteria<T> {


    @Override
    public Collection<? extends Collection<T>> getGroups(Collection<? extends T> elements) {
        Map<Object, Set<T>> groups = new LinkedHashMap<Object, Set<T>>();

        for (T element : elements) {
            Object id = getGroupID(element);

            if ( id != null ) {
                Set<T> group = groups.get(id);
                if ( group == null ) {
                    group = new HashSet<T>();
                    groups.put(id, group);
                }

                if ( group.contains(element) == false ) {
                    group.add(element);
                }
            }
        }

        return groups.values();
    }
    
    public abstract Object getGroupID(T element);

}
