/*
 * TargetXMLParser.java
 *
 * Created on March 28, 2004, 11:37 AM
 */

package champions.ioAdapter.heroDesigner;

import xml.DefaultXMLHandler;
import xml.XMLHandler;
import xml.XMLParser;

/** Parses HeroDesigner .hdc XML files to generate a Target (character) object.
 *
 * This XML parser is setup to parse HeroDesigner .hdc XML files to produce 
 * Target objects.  Although this could be used to generate any type of Target
 * right now only character types are defined in HeroDesigner. <P>
 *
 * The General Strategy is as follows:
 * <OL>
 * <LI>The CHARACTER XMLHandler creates a new Character.
 * <LI>The CHARACTER_INFO XMLHandler extracts name and misc info for character.
 * <LI>The CHARACTERISTICS XMLHandler extracts the stat values.
 * <LI>The Ability XMLHandler extract all abilities, skill, disadvantages from
 *     the XML and adds them to the Target.  See {@link AbilityXMLHandler} for
 *     more information.
 * </OL>
 *
 * @author  Trevor Walker
 */
public class TargetXMLParser extends XMLParser {
    
    /** Creates a new instance of TargetXMLParser */
    public TargetXMLParser() {
        //setDefaultXMLHandler( new xml.EchoXMLHandler(this) );
        setDefaultXMLHandler( new DefaultXMLHandler(this));
        
        registerXMLHandler( "#text", null);
        
        registerXMLHandler( "CHARACTER", new CHARACTERXMLHandler(this) );
        registerXMLHandler( "CHARACTER_INFO", new CHARACTER_INFOXMLHandler(this) );
        registerXMLHandler( "IMAGE", new ImageXMLHandler(this) );
        
        // Add in the various types of sublists
        XMLHandler sublistHandler = new SublistXMLHandler(this, "Powers");
        registerXMLHandler( "CHARACTERISTICS", sublistHandler );
        registerXMLHandler( "POWERS", sublistHandler );
        registerXMLHandler( "EXTRADC", sublistHandler );        
        
        sublistHandler = new SublistXMLHandler(this, "Talents");
        registerXMLHandler( "TALENTS", sublistHandler );
        
        sublistHandler = new SublistXMLHandler(this, "Perks");
        registerXMLHandler( "PERKS", sublistHandler );
        
        sublistHandler = new SublistXMLHandler(this, "Skills");
        registerXMLHandler( "SKILLS", sublistHandler );
        registerXMLHandler( "MARTIALARTS", sublistHandler );
        
        sublistHandler = new SublistXMLHandler(this, "Disadvantages");
        registerXMLHandler( "DISADVANTAGES", sublistHandler );
        
        sublistHandler = new SublistXMLHandler(this, "Equipment");
        registerXMLHandler( "EQUIPMENT", sublistHandler );
        
        ListXMLHandler listHandler = new ListXMLHandler(this);
        registerXMLHandler( "LIST", listHandler);
        
        FrameworkXMLHandler frameworkHandler = new FrameworkXMLHandler(this);
        registerXMLHandler( "MULTIPOWER", frameworkHandler);
        registerXMLHandler( "VPP", frameworkHandler);
        registerXMLHandler( "ELEMENTAL_CONTROL", frameworkHandler);
        
        // Register the Crazy Characteristic handlers for all stat names
        XMLHandler characteristicHandler = new CHARACTERISTICXMLHandler(this);
        registerXMLHandler( "STR", characteristicHandler );
        registerXMLHandler( "DEX", characteristicHandler );
        registerXMLHandler( "CON", characteristicHandler );
        registerXMLHandler( "BODY", characteristicHandler );
        registerXMLHandler( "INT", characteristicHandler );
        registerXMLHandler( "EGO", characteristicHandler );
        registerXMLHandler( "PRE", characteristicHandler );
        registerXMLHandler( "COM", characteristicHandler );
        registerXMLHandler( "PD", characteristicHandler );
        registerXMLHandler( "ED", characteristicHandler );
        registerXMLHandler( "SPD", characteristicHandler );
        registerXMLHandler( "REC", characteristicHandler );
        registerXMLHandler( "END", characteristicHandler );
        registerXMLHandler( "STUN", characteristicHandler );
        
        // Register the Ability creation XML tags
        XMLHandler abilityHandler = new AbilityXMLHandler(this);
        registerXMLHandler( "POWER", abilityHandler);
        registerXMLHandler( "RUNNING", new RunningXMLHandler());
        registerXMLHandler( "SWIMMING", new SwimmingXMLHandler());
        registerXMLHandler( "LEAPING", new LeapXMLHandler());
        registerXMLHandler( "PERK", abilityHandler);
        registerXMLHandler( "TALENT", abilityHandler);
        registerXMLHandler( "SKILL", abilityHandler);
        registerXMLHandler( "MANEUVER", abilityHandler);
        registerXMLHandler( "DISAD", abilityHandler);
        registerXMLHandler( "EXTRADC", abilityHandler);
        
        // Skip the TEMPLATE information
        XMLHandler templateHandler = new TEMPLATEXMLHandler(this);
        registerXMLHandler( "TEMPLATE", templateHandler);
    }
    
}
