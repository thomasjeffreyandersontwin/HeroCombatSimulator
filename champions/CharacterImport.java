/*
 * CharacterImport.java
 *
 * Created on June 10, 2001, 12:44 PM
 */

package champions;

import java.io.*;
import java.util.*;

import champions.interfaces.*;
/**
 *
 * @author  twalker
 * @version
 */
public class CharacterImport extends DetailList {
    
    /** Holds value of property importFile. */
    private File importFile;
    
/** Holds value of property inputStream. */
    private InputStream inputStream;
    
    /** Holds value of property lineReader. */
    private BufferedReader bufferedReader;
    
    /** Holds the most recently read line */
    private String lastLine;
    
    /** Creates new CharacterImport */
    public CharacterImport(Character character, File importFile) throws FileNotFoundException {
        setImportFile(importFile);
        setCharacter(character);
    }
    
        /** Creates new CharacterImport */
    public CharacterImport(Character character, InputStream is) {
        setInputStream(is);
        setCharacter(character);
    }
    
        /** Creates new CharacterImport */
    public CharacterImport(File importFile) throws FileNotFoundException {
        setImportFile(importFile);
    }
    
        /** Creates new CharacterImport */
    public CharacterImport(InputStream is) {
        setInputStream(is);
    }
    
    public CharacterImport(Reader reader) {
        setBufferedReader( new BufferedReader(reader) );
    }
    
    /** Getter for property character.
     * @return Value of property character.
     */
    public Character getCharacter() {
        Object o = getValue ("CharacterImport.TARGET");
        return (o == null ) ? null : (Character)o;
    }
    
    /** Setter for property character.
     * @param character New value of property character.
     */
    public void setCharacter(Character character) {
        add( "CharacterImport.TARGET", character, true);
    }
    
    /** Getter for property importFile.
     * @return Value of property importFile.
     */
    public File getImportFile() {
        return importFile;
    }
    
    /** Setter for property importFile.
     * @param importFile New value of property importFile.
     */
    public void setImportFile(File importFile) throws FileNotFoundException {
        this.importFile = importFile;
        FileInputStream is = new FileInputStream(importFile);
        setInputStream(is);
    }
    
    /** Getter for property inputStream.
     * @return Value of property inputStream.
     */
    public InputStream getInputStream() {
        return inputStream;
    }
    
    /** Setter for property inputStream.
     * @param inputStream New value of property inputStream.
     */
    public void setInputStream(InputStream inputStream) {
        
        this.inputStream = inputStream;
        setBufferedReader( new BufferedReader( new InputStreamReader( inputStream ) ) );
    }
    
    /** Getter for property lineReader.
     * @return Value of property lineReader.
     */
    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }
    
    /** Setter for property lineReader.
     * @param lineReader New value of property lineReader.
     */
    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }
    
    public void importCharacter() {
        // First Parse all of the Input
        parseImportFile();
        
        // Determine the powers based on the powerName
        identifyAbilities();
        
        // Start Trying to Figure out what Ability is what power
        // First Try to match abilities that have been imported before this.
        matchExistingAbilities();
    }
    
    public void parseImportFile() {
        // Parse the Actual File and break it into Stats and AbilityImports
        try {
            while ( getNextLine() != null ) {
                if ( lastLine.equals("") ) {
                    continue;
                }
                else if ( lastLine.startsWith( "Name: " )) {
                    this.add( "CharacterImport.NAME", lastLine.substring(6),true);
                    //    System.out.println("Set Name to \'" + lastLine.substring(6) + "\'");
                }
                else if ( lastLine.equals( "*STATS*" ) ) {
                    parseStats();
                }
                else if ( lastLine.startsWith( "*ABILITIES: " ) ) {
                    String sublist = lastLine.substring(12, lastLine.length()-1);
                    parseAbilities(sublist);
                }
                else {
                    System.out.println("Unrecognized Line: \'" + lastLine + "\'");
                }
            }
        }
        catch ( IOException ioe) {
            System.err.println("Error Reading File: " + ioe.toString() );
        }
    }
    
    public String getNextLine() throws IOException {
        if ( bufferedReader != null ) {
            lastLine = bufferedReader.readLine();
            return lastLine;
        }
        lastLine = null;
        return null;
    }
    
    public void parseStats() throws IOException {
        while ( getNextLine() != null ) {
            if ( lastLine.equals( "*ENDSTATS*" ) ) {
                break;
            }
            else {
                parseStat();
            }
        }
    }
    
    public void parseStat() {
        String stat;
        String valueString;
        int index;
        Integer value;
        
        // Grab the Stat name
        stat = lastLine.substring(0,3);
        if ( (index = stat.indexOf(" ")) != -1 ) {
            stat = stat.substring(0,index);
        }
        
        if ( stat.equals( "BOD" )) {
            stat = "BODY";
        }
        else if ( stat.equals("STN") ) {
            stat = "STUN";
        }
        
        index = lastLine.indexOf("/",5);
        valueString = lastLine.substring(5, index == -1 ? lastLine.length() : index );
        
        try {
            value =  new Integer(valueString);
            //add( stat + ".STAT", value, true);
            int statIndex = createIndexed( "Stat", "NAME", stat);
            addIndexed(statIndex, "Stat","VALUE",value);
            //       System.out.println("Got Stat: " + stat + " = " + value);
        }
        catch ( NumberFormatException nfe ) {
            System.err.println("Caught NumberFormatException while Parsing Stat" + stat +":");
            System.out.println(lastLine);
            int statIndex = createIndexed( "Stat", "NAME", stat);
            addIndexed(statIndex, "Stat","VALUE", new Integer(0));
        }
    }
    
    public void parseAbilities(String sublist) throws IOException {
        sublist = createSublist(sublist, null);
        int start = getIndexedSize("AbilityImport");
        while ( getNextLine() != null ) {
            if (parseAbility(sublist) ) break;
        }
        
        // After Parsing the Power, immediately create the hierarchy
       // createAbilityStructure(sublist, start, Integer.MAX_VALUE);
    }
    
    public boolean parseAbility(String sublist) throws IOException {
        AbilityImport ai = null;
        int index;
        String s;
        Vector sublistStack = new Vector();
        String currentSublist = sublist;
        sublistStack.add(currentSublist);
        
        while ( lastLine != null ) {
            if ( lastLine.equals( "" ) || lastLine.equals("MULTIPOWER:")) {
                // Just ignore it.
            }
            else if ( lastLine.equals( "BEGINPOWER:" ) ) {
                ai = new AbilityImport(this);
                this.createIndexed( "AbilityImport","ABILITYIMPORT", ai);
            }
            else if ( lastLine.equals("ENDPOWER:" ) ) {
                //break;
            }
            else if ( lastLine.startsWith("*ENDABILITIES" ) ) {
                return true; // Indicate we are done reading this bunch of abilities.
            }
            else if ( lastLine.equals("ENDSUBLIST") ) {
                sublistStack.remove( sublistStack.size() - 1);
                currentSublist = (String)sublistStack.lastElement();
            }
            else if ( lastLine.startsWith( "Power:" ) ) {
                s = lastLine.substring(7);
                ai.setPowerName(s);
                ai.setSublist(currentSublist);
            }
            else if ( lastLine.startsWith( "Sublist:" ) ) {
                ai.setType("SUBLIST");
                s = lastLine.substring(9);
                
                ai.setSublist(currentSublist);
                
                currentSublist = createSublist(s, currentSublist);
                ai.setName(currentSublist);
                sublistStack.add(currentSublist);
            }
            else if ( lastLine.startsWith( "Name:" ) ) {
                s = lastLine.substring(6);
                ai.setName(s);
            }
       /*     else if ( lastLine.startsWith( "PTS:" ) ) {
                s = lastLine.substring(5);
                int cost = 0;
                try {
                    cost = Integer.parseInt( s );
                }
                catch ( NumberFormatException nfe ) {
                }
                ai.setCPCost(cost);
            } */
            else {
                ai.addImportLine(lastLine);
            }
            getNextLine();
        }
        
        return false;
    }
    
    /** Assembles the hierarchy of Sublists based on point totals
     *
     * @return Index of next, non-catagorized, abilityImport
     */
    public int createAbilityStructure(String sublist, int start, int points) {
        // Look for Abilities with Only one line, set their type to be SUBLIST
        int index,count;
        AbilityImport ai;
        String newSublist;
        String aiSublist;
        int pts;
        
        int total = 0;
        
        count = this.getIndexedSize( "AbilityImport" );
        for(index = start; index<count && total < points; index++) {
            ai = (AbilityImport)getIndexedValue(index,"AbilityImport","ABILITYIMPORT");
            ai.setSublist(sublist);
            if ( ai.isSublist() ) {
                newSublist = ai.getName();
                newSublist = createSublist(newSublist, sublist); // newSublist name may have changed to become unique
                pts = ai.getCPCost();
                index = createAbilityStructure(newSublist, index+1, pts);
                total += pts;
                index --; // Adjust the index one back cause the loop will bump it one forward on the next round
            }
            else {
                // It isn't a sublist add cost to total
                total += ai.getCPCost();
            }
        }
        return index;
    }
    
    public void matchExistingAbilities() {
        Character character = getCharacter();
        if ( character != null ) {
            int tindex, tcount; // Character Indexes
            int iindex, icount; // Import Indexed
            Ability ability;
            AbilityImport ai;
            
            tcount = character.getIndexedSize("Ability");
            for(tindex=0; tindex<tcount; tindex++) {
                ability = (Ability)character.getIndexedValue( tindex, "Ability", "ABILITY");
                
                // Run through the new abilities and try to figure out what is what
                icount = this.getIndexedSize("AbilityImport");
                for(iindex = 0; iindex < icount; iindex ++ ) {
                    ai = (AbilityImport)this.getIndexedValue( iindex, "AbilityImport", "ABILITYIMPORT" );
                    if ( ai.getType() == null || ai.getType().equals("SUBLIST")) {
                        // Skip any where the type is null or SUBLIST
                        continue;
                    }
                    if ( ability.matchImport(ai) == true ) {
                        // We have a match
                        ai.setMatch(ability);
                        break;
                    }
                }
            }
        }
    }
    
    public void identifyAbilities() {
        // Go through and use the Power.identifyPower methods to try and guess the power type
        int index,count;
        int pindex;
        AbilityImport ai;
        Power power;
        Ability template;
        int likelyhood;
        
        count = getIndexedSize("AbilityImport");
        for(index=0;index<count;index++) {
            ai = (AbilityImport)getIndexedValue(index, "AbilityImport", "ABILITYIMPORT");
            
            // Run through the powers list and try to identify the Ability
            Iterator i = PADRoster.getAbilityIterator();
            while ( i.hasNext() ) {
                String abilityName = (String)i.next();
                template = PADRoster.getSharedAbilityInstance( abilityName );
                power = template.getPower();
                likelyhood = power.identifyPower(template, ai);
                if ( likelyhood > 0 ) {
                    ai.setTemplate( template );
                    break;
                }
            }
        }
    }
    
    
    /** Add a sublist to the AbilityImport to track hierarchy
     *
     */
    public String createSublist(String sublist, String parent) {
        int index;
        String name = sublist;
        int i = 1;
        while ( findSublist(name) != -1 ) {
            i++;
            name = sublist + " (" + Integer.toString(i) + ")";
        }
        
        index = createIndexed( "Sublist", "NAME", name );
        addIndexed( index, "Sublist", "IMPORTNAME", name, true);
        
        if ( parent != null && findSublist(parent) != -1 ) {
            addIndexed( index, "Sublist", "PARENT", parent, true );
        }
        
        return name;
        
    }
    
    /** Search for a specific sublist in AbilityImport
     */
    public int findSublist(String sublist) {
        return  this.findIndexed( "Sublist", "NAME", sublist );
    }
    
    public Character createCharacter() {
        Character character;
        if ( getCharacter() == null ) {
            character = new champions.Character();
            setCharacter(character);
        }
        else {
            character = getCharacter();
        }
        
        mergeCharacter(character);
        return character;
    }
    
    public void mergeCharacter(Character character) {
        String name = getStringValue("CharacterImport.NAME");
        character.setName(name);
        
        mergeStats(character);
        mergeSublists(character);
        mergeAbilities(character);
    }
    
    protected void mergeStats(Character character) {
        int index, count;
        String stat;
        Integer value;
        count = getIndexedSize("Stat");
        for(index=0;index<count;index++) {
            stat = getIndexedStringValue(index,"Stat","NAME");
            value = getIndexedIntegerValue(index,"Stat","VALUE");
            if ( stat.equals("OCV") || stat.equals("DCV") || stat.equals("ECV") ) continue;
            
            character.setBaseStat(stat, value.intValue());
            character.setAdjustedStat(stat, value.intValue());
            character.setCurrentStat(stat, value.intValue());
        }
    }
    
    protected void mergeSublists(Character character) {
        int index,count;
        AbilityImport ai;
        Ability oldAbility, newAbility;
        String name, importName, parent;
        int sublistIndex;
        
        AbilityList rootList = character.getAbilityList();
        AbilityList parentList = null;
        AbilityList childList;
        
        // First Import the Sublists
        count = getIndexedSize("Sublist");
        for(index=0;index<count;index++) {
            name = getIndexedStringValue(index,"Sublist","NAME");
            importName = getIndexedStringValue(index,"Sublist","IMPORTNAME");
            parent = getIndexedStringValue(index,"Sublist","PARENT");
            
            // First Find all of the lists...creating them if they don't exist.
            childList = rootList.findSublist(name);
            
            if ( childList == null ) {
                childList = new DefaultAbilityList(name);
            }
            
            addIndexed(index, "Sublist", "ABILITYLIST", childList, true);
        }
                
        for(index=0;index<count;index++) {
            name = getIndexedStringValue(index,"Sublist","NAME");
            importName = getIndexedStringValue(index,"Sublist","IMPORTNAME");
            parent = getIndexedStringValue(index,"Sublist","PARENT");
            childList = (AbilityList)getIndexedValue(index,"Sublist","ABILITYLIST");
         
            parentList = null;
            if ( parent == null ) {
                parentList = rootList;
            }
            else {
                // Look up the parent list
                int sindex;
                for(sindex=0;sindex<count;sindex++) {
                    name = getIndexedStringValue(sindex,"Sublist","NAME");
                    if ( name.equals(parent) ) {
                        parentList = (AbilityList)getIndexedValue(sindex,"Sublist","ABILITYLIST");
                        break;
                    }
                }
            }
            
            if ( parentList == null ) {
                throw new NullPointerException("Parent AbilityList is null.");
            }
            else {
                parentList.addSublist(childList);
            }
        }
    }
    
    protected void mergeAbilities(Character character) {
        int index,count;
        AbilityImport ai;
        Ability oldAbility, newAbility;
        String type, sublist;
        AbilityList al;
        int aindex;
        
        // First Remove all Existing Abilities
        //count = character.getIndexedSize("Ability");
        //for(index = count - 1;index >= 0;index--) {
        AbilityIterator i = character.getAbilities();
        while (i.hasNext()) {
            oldAbility = (Ability)i.nextAbility();
           // oldAbility.getAbilityList().removeAbility(oldAbility);
            i.remove();
            //character.removeAbility(oldAbility);
        }
        
        // Now Import the Abilities
        count = getIndexedSize("AbilityImport");
        for(index=0;index<count;index++) {
            ai = (AbilityImport)getIndexedValue(index,"AbilityImport","ABILITYIMPORT");
            type = ai.getType();
            if ( type != null && ( type.equals("TEMPLATE") || type.equals("MATCH") ) ) {
                newAbility = ai.getAbility();
                //character.addAbility(newAbility);
                al = character.findSublist( ai.getSublist() );
                al.addAbility(newAbility);
            }
        }
        
        character.addDefaultAbilities();
        
        count = getIndexedSize("AbilityImport");
        for(index=0;index<count;index++) {
            ai = (AbilityImport)getIndexedValue(index,"AbilityImport","ABILITYIMPORT");
            type = ai.getType();
            if ( type != null && ( type.equals("TEMPLATE") || type.equals("MATCH") ) ) {
                ai.finishImport(character);
            }
        }
    }
    
}
