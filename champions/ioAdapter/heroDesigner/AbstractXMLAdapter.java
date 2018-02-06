/*
 * DefaultPowerXMLAdapter.java
 *
 * Created on March 29, 2004, 10:07 PM
 */

package champions.ioAdapter.heroDesigner;

import champions.Ability;
import champions.Dice;
import champions.parameters.DiceParameter;
import champions.parameters.DoubleParameter;
import champions.parameters.IntegerParameter;
import champions.parameters.ParameterList;
import champions.interfaces.ChampionsConstants;
import champions.parameters.BooleanParameter;
import champions.parameters.ComboParameter;
import champions.parameters.Parameter;
import champions.parameters.StringParameter;
import java.lang.reflect.Method;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import xml.DefaultXMLHandler;
import xml.DefaultXMLParseError;
import xml.XMLParseError;
import xml.XMLParseErrorList;
import xml.XMLParserException;


/**
 *
 * @author  1425
 */
public abstract class AbstractXMLAdapter implements XMLAdapter, ChampionsConstants {
    
    /** Creates a new instance of DefaultPowerXMLAdapter */
    public AbstractXMLAdapter() {
    }
    
    public boolean identifyXML(String XMLID, Node node) {
        return ( XMLID != null && XMLID.equals(getXMLID()) );
    }
    
    protected XMLParseError importXML(Ability ability, Node node, ParameterList pl) {
        XMLParseErrorList errorList = null;
        
        String[][] ta = getTranslationArray();
        
        if ( pl != null && ta != null ) {
            for(int index = 0; index < ta.length; index++) {
                String xmlAttrName = ta[index][0];
                String parameterName = "";
                
                	parameterName = ta[index][1];
                
                String specialHandler = null;
                String specialData = null;
                if ( ta[index].length >= 3 ) {
                    specialHandler = ta[index][2];
                }
                
                if ( ta[index].length == 4 ) {
                    specialData = ta[index][3];
                }
                
                String finalAttrName = DefaultXMLHandler.getAttributeName(xmlAttrName);
                Node parentNode = DefaultXMLHandler.findNode(node, xmlAttrName);
                
                while ( parentNode != null ) {
                    if ( specialHandler != null ) {
                        try {
                            Method m = this.getClass().getMethod(specialHandler,  new Class[] { Ability.class, Node.class, String.class, ParameterList.class, String.class, String.class });
                            NamedNodeMap attrs = parentNode.getAttributes();
                            Node attr = null;
                            if ( finalAttrName != null ) attr = attrs.getNamedItem(finalAttrName);
                            String value = null;
                            if ( attr != null ) value = attr.getNodeValue();
                            
                            m.invoke(this, new Object[] { ability, parentNode, value, pl, parameterName, specialData });
                        }
                        catch ( NoSuchMethodException e ) {
                            throw new XMLParserException("Custom Handler Method Exception ('" + specialHandler + "', Adapter: " + this + ", parameter " + parameterName +"): Method not found.");
                        }
                        catch ( IllegalAccessException e ) {
                            throw new XMLParserException("Custom Handler Method Exception ('" + specialHandler + "', Adapter: " + this + ", parameter " + parameterName +"): Method should be public.");
                        }
                        catch ( java.lang.reflect.InvocationTargetException e ) {
                            Throwable t = e.getCause();
                            if ( t instanceof XMLParserException ) {
                                if ( errorList == null ) errorList = new XMLParseErrorList();
                                errorList.addXMLParseError( new DefaultXMLParseError(t.getMessage(), 0));
                            }
                            else {
                                throw new XMLParserException("Custom Handler Method Exception ('" + specialHandler + "', Adapter: " + this + ", parameter " + parameterName +"): Invocation Target Exception.");
                            }
                        }
                        /*catch ( Exception e ) {
                            throw new XMLParserException("Custom Handler Method Exception ('" + specialHandler + "', Adapter: " + this + ", parameter " + parameterName +"): Error executing method.");
                        }*/
                    }
                    else {
                        //String parameterType = pl.getParameterType(parameterName);
                        Parameter parameter = pl.getParameter(parameterName);
                        NamedNodeMap attrs = parentNode.getAttributes();
                        Node attr = attrs.getNamedItem(finalAttrName);
                        if ( attr != null ) {
                            String value = attr.getNodeValue();
                            if ( pl.isParameterIndexed(parameterName) ) {
                                pl.addIndexedParameterValue(parameterName, value);
                            }
                            else if ( parameter instanceof ComboParameter ) {
                                pl.setParameterValue(parameterName, value);
                            }
                            else if ( parameter instanceof IntegerParameter ) {
                                pl.setParameterValue(parameterName, new Integer(value));
                            }
                            else if ( parameter instanceof DoubleParameter ) {
                                pl.setParameterValue(parameterName, new Double(value));
                            }
                            else if ( parameter instanceof StringParameter ) {
                                pl.setParameterValue(parameterName, value);
                            }
                            else if ( parameter instanceof BooleanParameter ) {
                                /// ???
                            }
                            else if ( parameter instanceof DiceParameter ) {
                                // if ( Dice.isValid(value) ) pl.setParameterValue(parameterName, value);
                                diceSpecial(ability, parentNode, value, pl, parameterName, "");
                            }
                            else {
                                pl.setParameterValue(parameterName, value);
                            }
                        }
                    }
                    if ( parentNode != node ) {
                        parentNode = DefaultXMLHandler.findNode(node, parentNode, xmlAttrName);
                    }
                    else { 
                        parentNode = null;
                    }
                }
            }
        }
        
        return errorList;
    }
    
    /** Returns the XMLID for this Power.
     *
     * Subclass should either override this to return their XMLID or override
     * the identify method to do more complicated identification tasks.
     */
    public abstract String getXMLID();
    
    /** Returns the Translation Array for the PowerAdapter.
     *
     * The Subclass should either override this to return their translationArray
     *or override the importXML method to do more complicated import tasks.
     */
    public abstract String[][] getTranslationArray();
    
    /** specialHandler for processing Dice with pips and +1 modifiers.
     *
     */
//    public void diceSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName) {
//        String dieName = "";
//        if ( attrValue != null ) {
//            dieName = attrValue;// + "d6";
//        }
//        
//        Node n;
//        if ( ( n = DefaultXMLHandler.findNode(node, "ADDER(XMLID=PLUSONEHALFDIE)")) != null ) {
//            dieName += ".5d6";
//        }
//        else if ( ( n = DefaultXMLHandler.findNode(node, "ADDER(XMLID=PLUSONEPIP)")) != null ) {
//            dieName += "d6+1";
//        }
//        else if ( ( n = DefaultXMLHandler.findNode(node, "ADDER(XMLID=MINUSONEPIP)")) != null ) {
//            int i = Integer.parseInt(attrValue);
//            dieName = Integer.toString(i+1) + "d6-1";
//        }
//        if (!dieName.endsWith("d6")) {
//            dieName += "d6";
//        }
//        if (Dice.isValid( dieName ) ) {
//            pl.setParameterValue(parameterName,  dieName);
//        }
//    }
    
    public void diceSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        String dieName = "";
        if ( attrValue != null ) {
            dieName = attrValue + "d6";
        }
        
        Node n;
        if ( ( n = DefaultXMLHandler.findNode(node, "ADDER(XMLID=PLUSONEHALFDIE)")) != null ) {
            int i = Integer.parseInt(attrValue);
            dieName = Integer.toString(i) + ".5d6";
        }
        else if ( ( n = DefaultXMLHandler.findNode(node, "ADDER(XMLID=PLUSONEPIP)")) != null ) {
            int i = Integer.parseInt(attrValue);
            dieName = Integer.toString(i) + "d6+1";
        }
        else if ( ( n = DefaultXMLHandler.findNode(node, "ADDER(XMLID=MINUSONEPIP)")) != null ) {
            int i = Integer.parseInt(attrValue);
            dieName = Integer.toString(i+1) + "d6-1";
        }
        if (Dice.isValid( dieName ) ) {
            pl.setParameterValue(parameterName,  dieName);
        }
    }
    
    /** specialHandler to set the value of a parameter to the value specified by specialData.
     *
     */
    public void setSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        pl.setParameterValue(parameterName, specialData);
    }
    
    /** specialHandler that sets a boolean value to true if the attribute exists.
     *
     */
    public void trueSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        pl.setParameterValue(parameterName, new Boolean(true));
    }
    
    /** specialHandler that sets a boolean value to false if the attribute exists.
     *
     */
    public void falseSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        pl.setParameterValue(parameterName, new Boolean(false));
    }
    
}
