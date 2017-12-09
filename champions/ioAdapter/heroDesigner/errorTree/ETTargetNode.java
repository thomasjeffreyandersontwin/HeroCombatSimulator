/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.ioAdapter.heroDesigner.errorTree;

import champions.Target;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import treeTable.DefaultTreeTableModel;
import xml.XMLParseError;
import xml.XMLParseErrorList;


/**
 *
 * @author  twalker
 * @version
 *
 * PADTargetNode's hold references to Abilities stored in an Target list.
 * It should not be used for powers/templates which are cached by the PADRoster
 * shared instance mechanism.
 */
public class ETTargetNode extends ETNode implements PropertyChangeListener {
    
    
    /** Holds value of property target. */
    private Target target;
    
    private XMLParseErrorList xmlErrorList;
    
    private String fileName;
    
    /** Creates new PADTargetNode */
    public ETTargetNode(Target target, XMLParseErrorList errorList) {
        setTarget(target);
        setXmlErrorList(errorList);
        
        buildNode();
    }
    
    protected void buildNode() {
        removeAndDestroyAllChildren();
        
        if ( xmlErrorList != null ) {
            int count = xmlErrorList.getXMLParseErrorCount();
            for(int i = 0; i < count; i++) {
                XMLParseError error = xmlErrorList.getXMLParseError(i);
                
                ETNode node = new ETXMLErrorNode(error);
                add(node);
            }
        }
        
        if ( model instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)model).nodeStructureChanged(this);
        }
            
    }
    
    
    /** Returns the Target associated with node.
     *
     * This should return whatever the node represents.
     *
     * @return null if no Target is associated, such as in the case of a folder.
     */
    public Target getTarget() {
        return target;
    }
    
    /**
     * Returns the value to be displayed for node <code>node</code>,
     * at column number <code>column</code>.
     */
    public Object getValueAt(int column) {
        if ( fileName == null ) {
            return getTarget().getName();
        }
        else {
            return getTarget().getName() + " (" + fileName + ")";
        }
    }
    
    public void destroy() {
        super.destroy();
        
        setTarget(null);
        setXmlErrorList(null);
    }

    public XMLParseErrorList getXmlErrorList() {
        return xmlErrorList;
    }

    public void setXmlErrorList(XMLParseErrorList xmlErrorList) {
        this.xmlErrorList = xmlErrorList;
    }

        /** Setter for property target.
     * @param target New value of property target.
     *
     */
    public void setTarget(Target target) {
        if ( this.target != target ) {
            if ( this.target != null ) {
                this.target.removePropertyChangeListener("Target.NAME", this);
            }
            
            this.target = target;
            
            updateName();
            
            if ( this.target != null ) {
                this.target.addPropertyChangeListener("Target.NAME", this);
            }
        }
        
    }
    
    private void updateName() {
        if ( target != null ) {
            setUserObject(target.getName());
            if ( model instanceof DefaultTreeTableModel ) {
                ((DefaultTreeTableModel)model).nodeChanged(this);
            }
        }
        //Icon icon = p.getIcon();
        // setIcon(icon);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        updateName();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    

}
