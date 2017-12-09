/*
 * DetailListListModel.java
 *
 * Created on December 17, 2000, 4:05 PM
 */
package champions;

import champions.interfaces.SenseFilter;
import champions.parameters.ParameterList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;

/**
 *
 * @author  unknown
 * @version
 */
public class SenseListModel extends AbstractListModel implements ListModel, PropertyChangeListener {

    /** Holds value of property detailList. */
    protected ParameterList parameterList;
    protected String parameter;
    /** Indicates all senses in a group should be removed if the group is in parameterList.
     *
     */
    protected boolean removeFullGroups = true;
    protected List options;
    protected SenseFilter filter;
    protected boolean showOnlyGroups = false;

    //    static public String[] SensesOptions = {"Active Sonar","High Range Radio Hearing",
    //    "Infrared Vision","Mental Awareness","Microscopic Vision","N-Ray Vision","Radar Sense"
    //    ,"Radio Hearing","Radio Listen & Transmit","Spatial Awareness","Tracking Scent",
    //    "Ultrasonic Hearing","Ultraviolet Vision","360 Degree Sensing","Normal Sight","Normal Hearing"
    //    ,"Normal Taste","Normal Touch","Normal Smell","Sight Group","Hearing Group","Radio Group",
    //    "Smell/Taste Group","Unusual Senses Group","Mental Group"};
    /** Creates new DetailListListModel */
    public SenseListModel() {
        options = new ArrayList();
    }

    /** Creates new DetailListListModel */
    public SenseListModel(ParameterList pl, String parameter, boolean showOnlyGroups) {
        setShowOnlyGroups(showOnlyGroups);
        options = new ArrayList();
        setParameterList(pl);
        setParameter(parameter);
    }

    /** Returns the length of the list.
     */
    public int getSize() {
        return options.size();
    }

    /** Returns the value at the specified index.
     */
    public Object getElementAt(int index) {
        return options.get(index);
    }

    /** Rebuilds the Options model, as necessary.
     *
     */
    protected void rebuildModel() {
        List oldList = options;
        List newList = new ArrayList();

        Iterator it = PADRoster.getSenses();
        while (it.hasNext()) {
            Sense s = (Sense) it.next();
            if ((filter == null || filter.accept(s)) && parameterListHasSense(s) == false) {
                if (s.isSenseGroup()) {
                    newList.add(s);
                    if (isShowOnlyGroups() == false) {
                        addSenseGroupScensesToList(newList, (SenseGroup) s);
                    }
                }
            }
        }

        if (isShowOnlyGroups() == false) {
            // Now add any senses that we might have missed
            Iterator it2 = PADRoster.getSenses();
            while (it2.hasNext()) {
                Sense s = (Sense) it2.next();
                if ((filter == null || filter.accept(s)) && parameterListHasSense(s) == false) {
                    if (newList.contains(s) == false) {
                        newList.add(s);
                    }
                }
            }
        }

        options = newList;

        int nindex = 0;
        int oindex = 0;

        while (oindex < oldList.size() && nindex < newList.size()) {
            if (oldList.get(oindex) != newList.get(nindex)) {
                Object oldItem = oldList.get(oindex);
                int oldItemIndexInNewList = findObject(newList, oldItem, nindex + 1);
                // if the oldItem is in the list, then it must be an insert
                if (oldItemIndexInNewList != -1) {
                    // We must have inserted elements [nindex,oldItemIndexInNewList)
                    fireIntervalAdded(this, nindex, oldItemIndexInNewList - 1);
                    nindex = oldItemIndexInNewList + 1;
                    oindex++;
                } else {
                    // The first element in old list is not in the new list
                    // so there must have been a delete
                    Object newItem = newList.get(nindex);
                    int newItemIndexInOldList = findObject(oldList, newItem, oindex + 1);
                    if (newItemIndexInOldList != -1) {
                        // everything in [oindex,newItemIndexInOldList) was added
                        fireIntervalRemoved(this, oindex, newItemIndexInOldList - 1);
                        oindex = newItemIndexInOldList + 1;
                        nindex++;
                    } else {
                        fireIntervalRemoved(this, oindex, oldList.size() - 1);
                        oindex = oldList.size();
                        nindex++;
                    }
                }
            } else {
                nindex++;
                oindex++;
            }
        }

        if (oindex < oldList.size() && nindex >= newList.size()) {
            fireIntervalRemoved(this, oindex, oldList.size() - 1);
        } else if (oindex >= oldList.size() && nindex < newList.size()) {
            fireIntervalAdded(this, nindex, newList.size() - 1);
        }

    }

    private int findObject(List list, Object object, int startIndex) {

        for (int i = startIndex; i < list.size(); i++) {
            if (list.get(i) == object) {
                return i;
            }
        }
        return -1;
    }

    private void addSenseGroupScensesToList(List list, SenseGroup senseGroup) {
        Iterator it = PADRoster.getSenses();
        while (it.hasNext()) {
            Sense s = (Sense) it.next();
            if ((filter == null || filter.accept(s)) && parameterListHasSense(s) == false) {
                if (senseGroup.equals(s.getSenseGroup())) {
                    list.add(s);
                }
            }
        }
    }

    protected boolean parameterListHasSense(Sense s) {
        if (parameterList == null || parameter == null) {
            return false;
        }

        int count = parameterList.getIndexedParameterSize(parameter);
        for (int i = 0; i < count; i++) {
            Sense s2 = (Sense) parameterList.getIndexedParameterValue(parameter, i);
            //   if ( s.getSenseName().equals(s2.getSenseName()) ) return true;

            if (removeFullGroups &&
                    s2.isSenseGroup() &&
                    s.isSense() && s.getSenseGroupName() != null &&
                    s.getSenseGroupName().equals(s2.getSenseName())) {
                return true;
            }
        }
        return false;
    }

    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        rebuildModel();
    }

    /**
     * Getter for property parameterList.
     * @return Value of property parameterList.
     */
    public champions.parameters.ParameterList getParameterList() {
        return parameterList;
    }

    /**
     * Setter for property parameterList.
     * @param parameterList New value of property parameterList.
     */
    public void setParameterList(champions.parameters.ParameterList parameterList) {
        if (parameterList != this.parameterList) {
            if (this.parameterList != null && parameter != null) {
                this.parameterList.removePropertyChangeListener(this);
            }

            this.parameterList = parameterList;
            if (parameterList == null || (parameterList != null && parameter != null)) {
                rebuildModel();
            }

            if (this.parameterList != null && parameter != null) {
                this.parameterList.addPropertyChangeListener(this);
            //this.parameterList.addPropertyChangeListener( this);
            }

        }
    }

    /**
     * Getter for property parameter.
     * @return Value of property parameter.
     */
    public java.lang.String getParameter() {
        return parameter;
    }

    /**
     * Setter for property parameter.
     * @param parameter New value of property parameter.
     */
    public void setParameter(java.lang.String parameter) {
        if (this.parameter != parameter && (this.parameter == null || this.parameter.equals(parameter) == false)) {
            if (this.parameter != null && this.parameterList != null) {
                this.parameterList.removePropertyChangeListener(this);
            }

            this.parameter = parameter;

            if (this.parameter != null && this.parameterList != null) {
                this.parameterList.addPropertyChangeListener(this);
                rebuildModel();
            }
        }
    }

    /**
     * Getter for property filter.
     * @return Value of property filter.
     */
    public SenseFilter getFilter() {
        return filter;
    }

    /**
     * Setter for property filter.
     * @param filter New value of property filter.
     */
    public void setFilter(SenseFilter filter) {
        this.filter = filter;
        rebuildModel();
    }

    /**
     * Getter for property removeFullGroups.
     * @return Value of property removeFullGroups.
     */
    public boolean isRemoveFullGroups() {
        return removeFullGroups;
    }

    /**
     * Setter for property removeFullGroups.
     * @param removeFullGroups New value of property removeFullGroups.
     */
    public void setRemoveFullGroups(boolean removeFullGroups) {
        this.removeFullGroups = removeFullGroups;
    }

    /**
     * Setter for property removeFullGroups.
     * @param removeFullGroups New value of property removeFullGroups.
     */
    public void setShowOnlyGroups(boolean showOnlyGroups) {
        this.showOnlyGroups = showOnlyGroups;
    }

    /**
     * Getter for property removeFullGroups.
     * @return Value of property removeFullGroups.
     */
    public boolean isShowOnlyGroups() {
        return showOnlyGroups;
    }
}