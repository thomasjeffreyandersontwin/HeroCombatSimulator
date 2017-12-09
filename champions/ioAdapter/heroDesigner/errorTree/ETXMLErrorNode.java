/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.ioAdapter.heroDesigner.errorTree;

import javax.swing.Icon;
import javax.swing.UIManager;
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
public class ETXMLErrorNode extends ETNode {
    
    private static Icon warningIcon;
    private static Icon errorIcon;
    
    private XMLParseError xmlError;
    private int severity;
    
    /** Creates new PADTargetNode */
    public ETXMLErrorNode(XMLParseError error) {
        setupIcons();
        
        setXmlError(error);
        
        buildNode();
        
        
    }
    
    protected void setupIcons() {
        if (warningIcon == null) warningIcon = UIManager.getIcon("ErrorTree.warningIcon");
        if (errorIcon == null) errorIcon = UIManager.getIcon("ErrorTree.errorIcon");
    }
    
    protected void buildNode() {
        removeAndDestroyAllChildren();
        
        if ( xmlError instanceof XMLParseErrorList ) {
            XMLParseErrorList xmlErrorList = (XMLParseErrorList)xmlError;
            
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
    
    
    /**
     * Returns the value to be displayed for node <code>node</code>,
     * at column number <code>column</code>.
     */
    public Object getValueAt(int column) {
        return xmlError.getErrorDescription();
    }
    
    public void destroy() {
        super.destroy();
        
        setXmlError(null);
    }

    public XMLParseError getXmlError() {
        return xmlError;
    }

    public void setXmlError(XMLParseError xmlError) {
        if ( this.xmlError != xmlError ) {
            this.xmlError = xmlError;
            
            if ( this.xmlError != null ) {
                setSeverity( this.xmlError.getErrorSeverity());
            }
        }
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
        
        if ( severity == 0 ) {
            setIcon( warningIcon);
        }
        else {
            setIcon( errorIcon );
        }
    }

 
    

}
