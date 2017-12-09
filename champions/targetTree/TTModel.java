/*
 * PADTreeModel.java
 *
 * Created on February 27, 2002, 11:11 PM
 */
package champions.targetTree;

import champions.Target;
import champions.filters.AndFilter;
import champions.filters.NameTargetFilter;
import tjava.Filter;
import treeTable.DefaultTreeTableColumnModel;
import treeTable.DefaultTreeTableModel;
import treeTable.FilterableTreeTableModel;
import treeTable.TreeTable;
import treeTable.TreeTableColumn;
import treeTable.TreeTableColumnModel;

/**
 * TODO: ProxyTreeTable - Switch to filter delegate.
 * @author  twalker
 * @version 
 */
@SuppressWarnings("serial")
public class TTModel extends DefaultTreeTableModel implements FilterableTreeTableModel {

    public static final int COLUMN_NAME = 0;

    public static final int COLUMN_BODY = 1;

    public static final int COLUMN_STUN = 2;

    public static final int COLUMN_PD = 3;

    public static final int COLUMN_ED = 4;

    public static final int COLUMN_END = 5;

    public static final int COLUMN_EFFECTS = 6;

    public static final int COLUMN_OCV = 7;

    public static final int COLUMN_DCV = 8;

    public static final int COLUMN_ECV = 9;

    public static final int MAX_COLUMNS = 10;

    public static final int[] OBSTACLE_COLUMNS = {COLUMN_NAME, COLUMN_BODY, COLUMN_STUN};

    /** Title of column */
    protected String[] columnTitles = {"Target", "BODY", "STUN", "PD", "ED", "END", "Effects", "OCV", "DCV", "ECV"};

    protected String title = columnTitles[0];

    private int[] defaultVisibleColumns = OBSTACLE_COLUMNS;//0xFFFFFFFF; 

    /** Combined targetFilter && nameFilter.
     *
     */
    private Filter<Target> filter;

    private Filter<Target> targetFilter;

    private Filter<Target> nameFilter;

    /** Current Number of columns. */
    protected int columnCount;

    /** Creates new PADTreeModel */
    public TTModel(TTNode root, String title) {
        super(root);
        this.title = title;
        root.setModel(this);
    }

    /**
     * Returns the number ofs availible column.
     */
    public int getColumnCount() {
        return MAX_COLUMNS;
    }

    /**
     * Returns the number of columns the <code>column</code> should span.
     */
    public int getColumnSpan(Object node, int column, TreeTableColumnModel columnModel) {
        if (node instanceof TTNode) {
            return ((TTNode) node).getColumnSpan(column, columnModel);
        }
        else {
            return 1;
        }
    }

    /**
     * Returns the name for column number <code>column</code>.
     */
    public String getColumnName(int column) {
        if (column == 0) {
            if (title == null || title.equals("")) {
                return columnTitles[column];
            }
            return title;
        }
        return columnTitles[column];
    }

    public int getColumnPreferredWidth(int column) {
        switch (column) {
            case COLUMN_NAME:
                return 250;
            case COLUMN_EFFECTS:
                return 200;
            default:
                return 25;
        }
    }

    public int[] getDefaultVisibleColumns() {
        return defaultVisibleColumns;
    }

    public void setDefaultVisibleColumns(int[] defaultVisibleColumns) {
        this.defaultVisibleColumns = defaultVisibleColumns;
    }

    public void setDefaultVisibleColumns(String[] columnNames) {
        int count = 0;
        for (int i = 0; i < columnNames.length; i++) {
            for (int j = 0; j < columnTitles.length; j++) {
                if (columnTitles[j].equals(columnNames[i])) {
                    count++;
                    break;
                }
            }
        }

        int[] columns = new int[count];

        count = 0;
        for (int i = 0; i < columnNames.length; i++) {
            for (int j = 0; j < columnTitles.length; j++) {
                if (columnTitles[j].equals(columnNames[i])) {
                    columns[count++] = j;
                    break;
                }
            }
        }

        setDefaultVisibleColumns(columns);
    }

    public TreeTableColumnModel getColumnModel() {
        TreeTableColumnModel tableColumnModel = new DefaultTreeTableColumnModel();

        for (int i = 0; i < defaultVisibleColumns.length; i++) {
            TreeTableColumn tc = new TreeTableColumn(defaultVisibleColumns[i]);
            tc.setHeaderValue(getColumnName(defaultVisibleColumns[i]));
            int preferredWidth = getColumnPreferredWidth(defaultVisibleColumns[i]);
            if (preferredWidth != -1) {
                tc.setPreferredWidth(preferredWidth);
                tc.setWidth(preferredWidth);
            }
            else {
                tc.setWidth(85);
            }
            tableColumnModel.addColumn(tc);
        }

        //setTableColumnModel(tableColumnModel);
        return tableColumnModel;
    }

    public javax.swing.Icon getIcon(TreeTable treeTable, Object node, boolean isSelected, boolean expanded, boolean leaf, int row) {

        return null;
    }

    public Filter<Target> getFilter() {
        return filter;
    }

    public void createFilter() {
        Filter<Target> filter = nameFilter;

        if (targetFilter != null) {
            if (nameFilter != null) {
                filter = new AndFilter<Target>(targetFilter, nameFilter);
            }
            else {
                filter = targetFilter;
            }
        }

        this.filter = filter;

        if (getRoot() instanceof TTNode) {
            ((TTNode) getRoot()).buildNode();
        }
    }

    public Filter<Target> getTargetFilter() {
        return targetFilter;
    }

    public void setTargetFilter(Filter<Target> targetFilter) {
        if (this.targetFilter != targetFilter) {
            this.targetFilter = targetFilter;
            createFilter();
        }
    }

    public Filter<Target> getNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(Filter<Target> nameFilter) {
        if (this.nameFilter != nameFilter) {
            this.nameFilter = nameFilter;
            createFilter();
        }
    }

    @Override
    public boolean isFilterable() {
        return true;
    }

    @Override
    public boolean isFiltered() {
        return filter != null;
    }



    @Override
    public void setFilterObject(Object filterObject) {
        if (filterObject instanceof String) {
            if (((String) filterObject).equals("") || ((String) filterObject).equals(" ")) {
                setNameFilter(null);
            }
            else {
                setNameFilter(new NameTargetFilter((String) filterObject));
            }
        }
        else {
            setNameFilter(null);
        }
    }

}
