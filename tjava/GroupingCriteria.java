/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tjava;

import java.util.Collection;

/**
 *
 * @param <T> Type of element grouped by the criteria.
 * @author twalker
 */
public interface GroupingCriteria<T> {
    public Collection<? extends Collection<T>> getGroups(Collection<? extends T> elements);
}
