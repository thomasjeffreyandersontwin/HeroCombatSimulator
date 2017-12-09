/*
 * AbilityXMLHandler.java
 *
 * Created on March 28, 2004, 11:09 PM
 */
package champions.ioAdapter.heroDesigner;

import champions.Ability;
import champions.CombinedAbility;
import champions.MultipowerFramework;
import champions.PADRoster;
import champions.Power;
import champions.SpecialEffect;
import champions.Target;
import champions.interfaces.AbilityIterator;
import champions.interfaces.AbilityList;
import champions.interfaces.Advantage;
import champions.interfaces.Limitation;
import champions.interfaces.SpecialParameter;
import champions.parameters.ParameterList;
import champions.powers.SpecialParameterENDSource;
import champions.powers.SpecialParameterMultipowerSlot;
import champions.powers.powerENDReserve;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xml.DefaultXMLHandler;
import xml.DefaultXMLParseError;
import xml.XMLHandler;
import xml.XMLParseError;
import xml.XMLParseErrorList;
import xml.XMLParser;
import xml.XMLParserException;

/** Extracts Ability objects from an HeroDesigner XML Ability node.
 *
 * This XML handler knows how to extract an ability from a HeroDesigner
 * XML Element node.  It uses follows the standard XML ability import
 * procedure, as defined in the {@link Ability} class.<P>
 *
 * @author  1425
 */
public class AbilityXMLHandler extends DefaultXMLHandler implements XMLHandler {

    protected static Map adapterMap;

    /** Creates a new instance of AbilityXMLHandler */
    public AbilityXMLHandler() {
        if (adapterMap == null) {
            adapterMap = new HashMap();
        }
    }

    public AbilityXMLHandler(XMLParser parser) {
        super(parser);

        if (adapterMap == null) {
            adapterMap = new HashMap();
        }
    }

    public Object parseNode(Document doc, Node node, Object userData, XMLParseErrorList xmlParseErrorList) throws XMLParserException {
        if (userData instanceof HDInfo == false) {
            throw new XMLParserException("userData must be HDInfo object");
        }

        HDInfo hdInfo = (HDInfo) userData;

        Target target = hdInfo.target;

        AbilityList sublist = target.getAbilityList().findSublist(hdInfo.baseSublist);

        if (sublist == null) {
            // Probably will need to create the list eventually, but for now,
            // lets just put it at the root of the abilities.
            sublist = target.getAbilityList();
        }

        String parentID = findAttribute(node, "PARENTID");

        if (parentID != null) {
            String listName = (String) hdInfo.listToIDMap.get(parentID);
            if (listName != null) {
                sublist = sublist.findSublist(listName);
            }
        }

        Ability ability = importAbility(node, xmlParseErrorList);
        if (ability == null) {
            return null;
        }

        sublist.addAbility(ability);

        // Hack in the ULTRA_SLOT stuff...
        if (sublist.getFramework() instanceof MultipowerFramework) {
            // Check to see if we need a different end source
            Node fixedSlot = node.getAttributes().getNamedItem("ULTRA_SLOT");
            if (fixedSlot != null && fixedSlot.getNodeValue().equals("Yes")) {
                SpecialParameterMultipowerSlot spms = (SpecialParameterMultipowerSlot) ability.findSpecialParameter("Multipower Slot Type");
                if (spms == null) {
                    spms = new SpecialParameterMultipowerSlot();
                    ability.addSpecialParameter(spms);
                }
                ParameterList pl = spms.getParameterList(ability);
                pl.setParameterValue("SlotType", "Fixed Slot");
                ability.reconfigureSpecialParameter(spms, pl, -1);
            }
        }



        return null;
    }

    protected Ability importAbility(Node node, XMLParseErrorList xmlParseErrorList) {
        // Create an ability and configure it according to the
        // attributes and adders of the ability.
        //NamedNodeMap attrs = node.getAttributes();
        String powerID = findAttribute(node, "XMLID");
        String abilityName = findAttribute(node, "NAME");
        if (abilityName == null || abilityName.equals("")) {
            abilityName = findAttribute(node, "ALIAS");
        }

        Ability ability = null;
        String powerName = null;

        if ("COMPOUNDPOWER".equals(powerID)) {
            ability = importCompoundAbility(abilityName, node, xmlParseErrorList);
        } else {

            XMLParseErrorList abilityErrorList = new XMLParseErrorList(abilityName + " import errors");

            // Now we want to lookup the XMLID amoung the powers.  We will run through
            // the power list, ask the parser for an PowerXMLAdapter, and then check
            // the Adapter if it matches the XMLID.
            Iterator powerIterator = PADRoster.getAbilityIterator();
            PowerXMLAdapter pxa = null;
            boolean done = false;
            while (!done && powerIterator.hasNext()) {
                powerName = (String) powerIterator.next();
                pxa = getPowerAdapter(powerName);
                if (pxa != null && pxa.identifyXML(powerID, node) == true) {
                    if (ability == null) {
                        ability = PADRoster.getNewAbilityInstance(powerName);
                        done = true; // Remove this line to check for multiple adapters attempting to import the same ability.
                    } else {
                        Ability ability2 = PADRoster.getSharedAbilityInstance(powerName);
                        if (ability.getPower().getClass().equals(ability2.getPower().getClass()) == false) {
                            abilityErrorList.addXMLParseError(new HDImportError(abilityName + ": Ability previously imported as " + ability.getPower().getName() + " also identified as " + powerName, HDImportError.IMPORT_INFO));
                        }
                    }
                }
            }

            if (ability == null) {
                xmlParseErrorList.addXMLParseError(new HDImportError(abilityName + ": Ability type not recognized during import.  XMLID=\"" + powerID + "\"", HDImportError.IMPORT_FAILURE));
                return null;
            }

            ability.setName(abilityName);
            if (importAbilityParameters(node, ability, pxa, abilityErrorList) == false) {
                // We will assume that importAbilityParameters setup the error list appropriately.
                if (abilityErrorList.getXMLParseErrorCount() > 0) {
                    xmlParseErrorList.addXMLParseError(abilityErrorList);
                }
                return null;
            }

            if (abilityErrorList.getXMLParseErrorCount() > 0) {
                xmlParseErrorList.addXMLParseError(abilityErrorList);
            }
        }

        return ability;
    }

    protected static boolean importAbilityParameters(Node node, Ability ability, PowerXMLAdapter pxa, XMLParseErrorList xmlParseErrorList) {
        XMLParseErrorList abilityErrorList = xmlParseErrorList;

        try {
            Power p = ability.getPower();
            XMLParseError powerError = pxa.importXML(ability, node);


            if (powerError != null) {
                //if ( abilityErrorList == null ) abilityErrorList = new XMLParseErrorList(ability.getName() + " import errors");

                if (powerError instanceof XMLParseErrorList) {
                    for (int j = 0; j < ((XMLParseErrorList) powerError).getXMLParseErrorCount(); j++) {
                        abilityErrorList.addXMLParseError(((XMLParseErrorList) powerError).getXMLParseError(j));
                    }
                } else {
                    abilityErrorList.addXMLParseError(powerError);
                }
            }

            if (p.configurePAD(ability, p.getParameterList(ability, -1)) == false) {
                xmlParseErrorList.addXMLParseError(new HDImportError(ability.getName() + ": Ability configuration failed during import.  Ability skipped.", HDImportError.IMPORT_FAILURE));
                return false;
            }
            ability.add("Power.FINIALIZER", pxa);

            // Check for Special FX tags
            Node sfxNode = node.getAttributes().getNamedItem("SFX");
            if (sfxNode != null) {
                String sfxName = sfxNode.getNodeValue();
                if (sfxName.equals("Default") == false) {
                    SpecialEffect se = PADRoster.getNewSpecialEffectInstance(sfxName);
                    if (se != null) {
                        ability.addSpecialEffect(se);
                    }
                }
            }

            // Check for additional special effects
            sfxNode = null;
            while ((sfxNode = findNode(node, sfxNode, "MODIFIER(XMLID=ADDITIONALSFX).INPUT")) != null) {
                String sfxName = getAttributeValue(sfxNode, "INPUT");
                if (sfxName != null && sfxName.equals("") == false) {
                    SpecialEffect se = PADRoster.getNewSpecialEffectInstance(sfxName);
                    if (se != null) {
                        ability.addSpecialEffect(se);
                    }
                }
            }

            // Check to see if we need a different end source
            Node endNode = node.getAttributes().getNamedItem("USE_END_RESERVE");
            if (endNode != null && endNode.getNodeValue().equals("Yes")) {
                SpecialParameter sp = new SpecialParameterENDSource();
                ability.addSpecialParameter(sp);
            }



            // Now lets run through the Adders and modifiers manually.  I think we
            // can skip all adders, but I am not certain yet...however, we must
            // hit all of the modifiers.
            Node child = node.getFirstChild();
            while (child != null) {
                if ("MODIFIER".equals(child.getNodeName())) {
                    NamedNodeMap mAttrs = child.getAttributes();
                    String modifierID = mAttrs.getNamedItem("XMLID").getNodeValue();

                    Node privateNode = mAttrs.getNamedItem("PRIVATE");
                    String privateModifier = privateNode != null ? privateNode.getNodeValue() : "No";

                    boolean done = false;
                    if (!done) {
                        Iterator i = PADRoster.getAdvantageIterator();
                        ModifierXMLAdapter mxa = null;
                        while (i.hasNext()) {
                            String modifierName = (String) i.next();
//                        Advantage s = PADRoster.getSharedAdvantageInstance(modifierName);
//                        String className = s.getClass().toString();
//                        className = className.substring( className.lastIndexOf(".")+1);

                            mxa = getModifierAdapter(modifierName);
                            if (mxa != null && mxa.identifyXML(modifierID, child) == true) {
                                Advantage a = PADRoster.getNewAdvantageInstance(modifierName);
                                if (ability.addPAD(a, null)) {
                                    //int index = ability.findExactIndexed("Advantage","ADVANTAGE", a);
                                    int index = ability.findExactAdvantage(a);
                                    XMLParseError error = mxa.importXML(ability, a, index, child);
                                    a.configurePAD(ability, a.getParameterList(ability, index));
                                    //ability.addIndexed(index, "Advantage", "FINIALIZER", mxa);
                                    a.setFinalizer(mxa);

                                    a.setPrivate("Yes".equals(privateModifier));

                                    if (error != null) {
                                        if (abilityErrorList == null) {
                                            abilityErrorList = new XMLParseErrorList(ability.getName() + " import errors");
                                        }

                                        if (error instanceof XMLParseErrorList) {
                                            for (int j = 0; j < ((XMLParseErrorList) error).getXMLParseErrorCount(); j++) {
                                                abilityErrorList.addXMLParseError(((XMLParseErrorList) error).getXMLParseError(j));
                                            }
                                        } else {
                                            abilityErrorList.addXMLParseError(error);
                                        }
                                    }
                                }
                                done = true;
                                break;
                            }
                        }
                    }
                    if (!done) {
                        Iterator i = PADRoster.getLimitationIterator();
                        ModifierXMLAdapter mxa = null;
                        while (i.hasNext()) {
                            String modifierName = (String) i.next();
//                        Limitation s = PADRoster.getSharedLimitationInstance(modifierName);
//                        String className = s.getClass().toString();
//                        className = className.substring( className.lastIndexOf(".")+1);

                            mxa = getModifierAdapter(modifierName);
                            if (mxa != null && mxa.identifyXML(modifierID, child) == true) {
                                Limitation lim = PADRoster.getNewLimitationInstance(modifierName);
                                if (ability.addPAD(lim, null)) {
                                    //int index = ability.findExactIndexed("Limitation","LIMITATION", a);
                                    int index = ability.findExactLimitation(lim);
                                    XMLParseError error = mxa.importXML(ability, lim, index, child);
                                    lim.configurePAD(ability, lim.getParameterList(ability, index));
                                    //ability.addIndexed(index, "Limitation", "FINIALIZER", mxa);
                                    lim.setFinalizer(mxa);

                                    // Set the private modifier on Limitation...
                                    lim.setPrivate(("Yes".equals(privateModifier)));

                                    if (error != null) {
                                        if (abilityErrorList == null) {
                                            abilityErrorList = new XMLParseErrorList(ability.getName() + " import errors");
                                        }

                                        if (error instanceof XMLParseErrorList) {
                                            for (int j = 0; j < ((XMLParseErrorList) error).getXMLParseErrorCount(); j++) {
                                                abilityErrorList.addXMLParseError(((XMLParseErrorList) error).getXMLParseError(j));
                                            }
                                        } else {
                                            abilityErrorList.addXMLParseError(error);
                                        }
                                    }
                                }
                                done = true;
                                break;
                            }
                        }
                    }

                    if (!done) {
                        Iterator i = PADRoster.getSpecialParameterIterator();
                        ModifierXMLAdapter mxa = null;
                        while (i.hasNext()) {
                            String modifierName = (String) i.next();
//                        Limitation s = PADRoster.getSharedLimitationInstance(modifierName);
//                        String className = s.getClass().toString();
//                        className = className.substring( className.lastIndexOf(".")+1);

                            mxa = getModifierAdapter(modifierName);
                            if (mxa != null && mxa.identifyXML(modifierID, child) == true) {
                                SpecialParameter a;
                                int index;
                                boolean configure = true;
                                if (ability.hasSpecialParameter(modifierName)) {
                                    a = ability.findSpecialParameter(modifierName);
                                } else {
                                    a = PADRoster.getNewSpecialParameterInstance(modifierName);
                                    configure = ability.addSpecialParameter(a);
                                }

                                if (configure) {
                                    index = ability.getSpecialParameterIndex(a);
                                    XMLParseError error = mxa.importXML(ability, a, index, child);
                                    a.configure(ability, a.getParameterList(ability, index));
                                    ability.addIndexed(index, "SpecialParameter", "FINIALIZER", mxa);

                                    if (error != null) {
                                        if (abilityErrorList == null) {
                                            abilityErrorList = new XMLParseErrorList(ability.getName() + " import errors");
                                        }

                                        if (error instanceof XMLParseErrorList) {
                                            for (int j = 0; j < ((XMLParseErrorList) error).getXMLParseErrorCount(); j++) {
                                                abilityErrorList.addXMLParseError(((XMLParseErrorList) error).getXMLParseError(j));
                                            }
                                        } else {
                                            abilityErrorList.addXMLParseError(error);
                                        }
                                    }
                                }
                                done = true;
                                break;
                            }
                        }
                    }

                    if (!done) {
                        // This is an unrecognized modifier...probably want to just load it
                        // as a generic advantage/limitation...
                        String description = null;
                        String baseCodeString = null;
                        String xmlString = null;

                        Node aliasNode = mAttrs.getNamedItem("ALIAS");
                        if (aliasNode != null) {
                            description = aliasNode.getNodeValue();
                        }

                        Node xmlNode = mAttrs.getNamedItem("XMLID");
                        if (xmlNode != null) {
                            xmlString = xmlNode.getNodeValue();
                        }

                        Node baseCostNode = mAttrs.getNamedItem("BASECOST");
                        if (baseCostNode != null) {
                            baseCodeString = baseCostNode.getNodeValue();
                        }

                        if (description != null && description.equals("") == false && baseCodeString != null && baseCodeString.equals("") == false) {

                            double baseCost = Double.parseDouble(baseCodeString);

                            if (baseCost > 0) {
                                Advantage a = PADRoster.getNewAdvantageInstance("Generic Advantage");

                                if (ability.addPAD(a, null)) {
                                    //int index = ability.findExactIndexed("Advantage","ADVANTAGE", a);
                                    ParameterList pl = a.getParameterList();

                                    pl.setParameterValue("Description", description);
                                    pl.setParameterValue("Multiplier", baseCost);

                                    a.configurePAD(ability, pl);

                                    a.setPrivate("Yes".equals(privateModifier));
                                    //ability.setAdvantagePrivate(index, ("Yes".equals(privateModifier)));
                                    done = true;

                                    if (abilityErrorList == null) {
                                        abilityErrorList = new XMLParseErrorList(ability.getName() + " import errors");
                                    }

                                    DefaultXMLParseError error = new DefaultXMLParseError("Advantage '" + description +
                                            "' not recognized.  Added as +" + baseCost + " generic advantage." +
                                            (xmlString == null ? "" : " XMLID=\"" + xmlString + "\""), 0);
                                    abilityErrorList.addXMLParseError(error);
                                }
                            } else if (baseCost < 0) {
                                Limitation a = PADRoster.getNewLimitationInstance("Generic Limitation");

                                if (ability.addPAD(a, null)) {
                                    //int index = ability.findExactIndexed("Limitation","LIMITATION", a);
                                    int index = ability.findExactLimitation(a);

                                    ParameterList pl = a.getParameterList(ability, index);

                                    pl.setParameterValue("Description", description);
                                    pl.setParameterValue("Multiplier", baseCost);

                                    a.configurePAD(ability, pl);

                                    a.setPrivate(("Yes".equals(privateModifier)));
                                    done = true;

                                    if (abilityErrorList == null) {
                                        abilityErrorList = new XMLParseErrorList(ability.getName() + " import errors");
                                    }

                                    DefaultXMLParseError error = new DefaultXMLParseError("Limitation '" + description +
                                            "' not recognized.  Added as " + baseCost + " generic limitation." +
                                            (xmlString == null ? "" : " XMLID=\"" + xmlString + "\""), 0);
                                    abilityErrorList.addXMLParseError(error);
                                }
                            }
                        }
                    }
                }

                child = child.getNextSibling();
            }

            ability.calculateMultiplier();

//        if ( abilityErrorList != null ) {
//            xmlParseErrorList.addXMLParseError(abilityErrorList);
//        }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    protected PowerXMLAdapter getPowerAdapter(String powerName) {
        if (adapterMap.containsKey(powerName)) {
            return (PowerXMLAdapter) adapterMap.get(powerName);
        } else {
            // Lookup the approprate adapter based on name...
            Power p = PADRoster.getPower(powerName);
            String powerClass = p.getClass().getName();
            int i = powerClass.lastIndexOf('.');
            if (i > 0 && i < powerClass.length() - 1) {
                powerClass = powerClass.substring(i + 1);
                try {
                    String adapterName = "champions.ioAdapter.heroDesigner.adapters." + powerClass + "Adapter";
                    Class c = Class.forName(adapterName);
                    Object o = c.newInstance();
                    if (o != null) {
                        adapterMap.put(powerName, o);
                        return (PowerXMLAdapter) o;
                    }
                } catch (Exception e) {
                    adapterMap.put(powerName, null);
                }
            }
        }
        return null;
    }

    protected static ModifierXMLAdapter getModifierAdapter(String modifierName) {
        ModifierXMLAdapter pxa = (ModifierXMLAdapter) adapterMap.get(modifierName);
        if (pxa == null) {

            String modifierClass = PADRoster.getPADClass(modifierName).getName();
            int i = modifierClass.lastIndexOf('.');
            if (i > 0 && i < modifierClass.length() - 1) {
                modifierClass = modifierClass.substring(i + 1);
                try {
                    String adapterName = "champions.ioAdapter.heroDesigner.adapters." + modifierClass + "Adapter";
                    Class c = Class.forName(adapterName);
                    Object o = c.newInstance();
                    if (o != null) {
                        adapterMap.put(modifierName, o);
                        pxa = (ModifierXMLAdapter) o;
                    }
                } catch (Exception e) {
                }
            }
        }
        return pxa;
    }

    protected static void finalizeAbility(Ability ability, XMLParseErrorList xmlParseErrorList) {

        if (ability instanceof CombinedAbility) {
            CombinedAbility c = (CombinedAbility) ability;
            int count = c.getAbilityCount();
            for (int i = 0; i < count; i++) {
                Ability a = c.getAbility(i);
                finalizeAbility(a, xmlParseErrorList);
            }
        } else {

            XMLParseErrorList abilityErrorList = null;

            PowerXMLAdapter pxa = (PowerXMLAdapter) ability.getValue("Power.FINIALIZER");
            if (pxa != null) {
                XMLParseError error = pxa.finalize(ability);
                ability.remove("Power.FINIALIZER");

                if (error != null) {
                    if (abilityErrorList == null) {
                        abilityErrorList = new XMLParseErrorList(ability.getName() + " import errors");
                    }

                    if (error instanceof XMLParseErrorList) {
                        for (int j = 0; j < ((XMLParseErrorList) error).getXMLParseErrorCount(); j++) {
                            abilityErrorList.addXMLParseError(((XMLParseErrorList) error).getXMLParseError(j));
                        }
                    } else {
                        abilityErrorList.addXMLParseError(error);
                    }
                }
            }

            int count = ability.getAdvantageCount();
            for (int i = 0; i < count; i++) {
                Advantage a = ability.getAdvantage(i);
                //ModifierXMLAdapter mxa = (ModifierXMLAdapter)ability.getIndexedValue(i,"Advantage","FINIALIZER");
                ModifierXMLAdapter mxa = (ModifierXMLAdapter) a.getFinalizer();
                if (mxa != null) {
                    XMLParseError error = mxa.finalize(ability, a, i);
                    //ability.removeIndexed(i,"Advantage","FINIALIZER");
                    a.setFinalizer(null);

                    if (error != null) {
                        if (abilityErrorList == null) {
                            abilityErrorList = new XMLParseErrorList(ability.getName() + " import errors");
                        }

                        if (error instanceof XMLParseErrorList) {
                            for (int j = 0; j < ((XMLParseErrorList) error).getXMLParseErrorCount(); j++) {
                                abilityErrorList.addXMLParseError(((XMLParseErrorList) error).getXMLParseError(j));
                            }
                        } else {
                            abilityErrorList.addXMLParseError(error);
                        }
                    }
                }
            }

            count = ability.getLimitationCount();
            for (int i = 0; i < count; i++) {
                //ModifierXMLAdapter mxa = (ModifierXMLAdapter)ability.getIndexedValue(i,"Limitation","FINIALIZER");
                Limitation lim = ability.getLimitation(i);
                ModifierXMLAdapter mxa = (ModifierXMLAdapter) lim.getFinalizer();
                if (mxa != null) {
                    XMLParseError error = mxa.finalize(ability, lim, i);
                    //ability.removeIndexed(i,"Limitation","FINIALIZER");
                    //ability.removeLimitationFinalizer(i);
                    lim.setFinalizer(null);

                    if (error != null) {
                        if (abilityErrorList == null) {
                            abilityErrorList = new XMLParseErrorList(ability.getName() + " import errors");
                        }

                        if (error instanceof XMLParseErrorList) {
                            for (int j = 0; j < ((XMLParseErrorList) error).getXMLParseErrorCount(); j++) {
                                abilityErrorList.addXMLParseError(((XMLParseErrorList) error).getXMLParseError(j));
                            }
                        } else {
                            abilityErrorList.addXMLParseError(error);
                        }
                    }
                }
            }

            count = ability.getSpecialParameterCount();
            for (int i = 0; i < count; i++) {
                ModifierXMLAdapter mxa = (ModifierXMLAdapter) ability.getIndexedValue(i, "SpecialParameter", "FINIALIZER");
                if (mxa != null) {
                    XMLParseError error = mxa.finalize(ability, ability.getSpecialParameter(i), i);
                    ability.removeIndexed(i, "SpecialParameter", "FINIALIZER");

                    if (error != null) {
                        if (abilityErrorList == null) {
                            abilityErrorList = new XMLParseErrorList(ability.getName() + " import errors");
                        }

                        if (error instanceof XMLParseErrorList) {
                            for (int j = 0; j < ((XMLParseErrorList) error).getXMLParseErrorCount(); j++) {
                                abilityErrorList.addXMLParseError(((XMLParseErrorList) error).getXMLParseError(j));
                            }
                        } else {
                            abilityErrorList.addXMLParseError(error);
                        }
                    }
                }
            }

            // Check to see if there is an END source and if the character has one.
            if (ability.hasSpecialParameter("END Source")) {
                Target source = ability.getSource();
                if (source != null) {
                    Ability endSource = null;
                    AbilityIterator ai = source.getAbilities();
                    while (ai.hasNext()) {
                        Ability a = ai.nextAbility();
                        Boolean namedENDSource = false;
                        if (a.getPower() instanceof powerENDReserve) {
                            //endSource = a;
                            SpecialParameter sp = ability.findSpecialParameter("END Source");
                            int i = ability.findIndexed("SpecialParameter", "SPECIALPARAMETER", sp);
                            ParameterList pl = sp.getParameterList(ability, i);
                            String importENDSource = (String) pl.getParameterValue("ENDSource");
                            if (a.getName().equals(importENDSource)) {
                                namedENDSource = true;
                                endSource = a;
                                break;
                            }
                            if (namedENDSource != true) {
                                endSource = a;
                            }
                        }
                    }

                    // Find the parameter list of the END source...
                    if (endSource != null) {
                        SpecialParameter sp = ability.findSpecialParameter("END Source");
                        int i = ability.findIndexed("SpecialParameter", "SPECIALPARAMETER", sp);
                        if (i != - 1) {
                            ParameterList pl = sp.getParameterList(ability, i);
                            pl.setParameterValue("ENDSource", endSource.getName());
                            ability.setPrimaryENDSource( endSource.getName() );
                            sp.configure(ability, pl);

                        }
                    } else {
                        // We probably need some sort of error here, but I don't know what it would be!
                    }
                }

            }

            if (abilityErrorList != null) {
                xmlParseErrorList.addXMLParseError(abilityErrorList);
            }
        }
    }

    private Ability importCompoundAbility(String abilityName, Node node, XMLParseErrorList xmlParseErrorList) {
        CombinedAbility parentAbility = new CombinedAbility(abilityName);

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if ("POWER".equals(child.getNodeName())) {
                Ability ability = importAbility(child, xmlParseErrorList);
                if (ability != null) {
                    parentAbility.addAbility(ability);
                }
            }
        }

        return parentAbility;
    }
}
