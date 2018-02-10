/*
 * powerEnergyBlast.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.HDImportError;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import xml.XMLParseError;

/**
 *
 * @author  1425
 */
public class powerUnidentifiedAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "UNIDENTIFIED";
    private static String[][] translationArray = {
    		 { "LEVELS", "Amount" },
    		 { "NAME", "Name" },
    };
    
    private boolean basePower = false;
    
    public powerUnidentifiedAdapter() {
        super();
    }
    
    public powerUnidentifiedAdapter(boolean basePower) {
        super();
        
        this.basePower = basePower;
    }
    
    public String getXMLID() {
        return XMLID;
    }
    

    public String[][] getTranslationArray() {
        
        return translationArray;
    }
    
    public XMLParseError importXML(Ability ability, Node node) {
        XMLParseError result = super.importXML(ability, node);
        NamedNodeMap m = node.getAttributes();
        String name  = m.getNamedItem("XMLID").getNodeValue();
        Integer amount = new Integer(m.getNamedItem("LEVELS").getNodeValue());
        ParameterList pl = ability.getPowerParameterList();
        
        pl.setParameterValue("Name", name);
        pl.setParameterValue("Amount", amount);
        if ( (result == null || result.getErrorSeverity() < HDImportError.IMPORT_FAILURE)  && basePower ) {
            pl = ability.getPower().getParameterList(ability, -1);
            pl.setParameterValue("Base", "TRUE");
            ability.reconfigurePower();
        }
        
        return result;
    }
    
    
    
}
