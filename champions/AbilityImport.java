/*
 * AbilityImport.java
 *
 * Created on June 10, 2001, 12:53 PM
 */

package champions;

import champions.interfaces.*;
import champions.parameters.ParameterList;

import java.util.Iterator;

/**
 *
 * @author  twalker
 * @version
 */
public class AbilityImport extends DetailList {
    
    /** Creates new AbilityImport */
    public AbilityImport(CharacterImport ci) {
        setCharacterImport(ci);
    }
    
    public void addImportLine(String line) {
        if ( line.startsWith( "PTS:" ) ) {
            int sindex, eindex;
            String ptsString;
            boolean counted = true;
            Integer pts;
            if ( (sindex = line.indexOf("(")) != -1 ) {
                eindex = line.indexOf(")");
                ptsString = line.substring( sindex + 1, eindex );
                counted = false;
            }
            else if ( ( sindex = line.indexOf( "u-" ) ) != -1 ) {
                ptsString = line.substring( sindex+2 );
            }
            else if ( ( sindex = line.indexOf( "m-" ) ) != -1 ) {
                ptsString = line.substring( sindex+2 );
            }
            else if ( ( sindex = line.indexOf( "!" ) ) != -1 ) {
                ptsString = line.substring( sindex+1 );
            }
            else {
                ptsString = line.substring( 5 );
            }
            
            try {
                pts = new Integer(ptsString);
            }
            catch ( NumberFormatException nfe ) {
                System.err.println("Caught NumberFormatException while Parsing PTS: " + line );
                pts = new Integer(0);
            }
            
            setCPCost( pts.intValue() );
            add( "AbilityImport","COUNTCP", (counted ? "TRUE" : "FALSE"), true );
        }
        else if ( ChampionsMatcher.matches("Special Effect: (.*)", line) ) {
            createIndexed( "SpecialEffect","NAME", ChampionsMatcher.getMatchedGroup(1), true );
        }
        else if ( ChampionsMatcher.matches("Automatically Activate: (.*)", line) ) {
            add( "AbilityImport", "NORMALLYON", ChampionsMatcher.getMatchedGroup(1), true);
        }
        else {
            createIndexed( "Line","STRING", line);
        }
    }
    
    public String getImportLine(int index) {
        if ( index < getIndexedSize( "Line" )) {
            return getIndexedStringValue(index,"Line","STRING");
        }
        return null;
    }
    
    public void setLineUsed(int index, Object by) {
        if ( index < getIndexedSize( "Line" )) {
            addIndexed(index, "Line", "USED", "TRUE", true);
            addIndexed(index, "Line", "USEDBY", by, true);
        }
    }
    
    public void setLineUnused(int index) {
        if ( index < getIndexedSize( "Line" )) {
            addIndexed(index, "Line", "USED", "FALSE", true);
            removeIndexed(index, "Line", "USEDBY", false);
        }
    }
    
    public boolean isLineUsed(int index) {
        return getIndexedBooleanValue(index, "Line","USED");
    }
    
    public int getUnusedLineCount() {
        int index, count;
        int total = 0;
        count = getImportLineCount();
        for ( index = 0; index < count; index++) {
            if ( isLineUsed(index) == false ) total ++;
        }
        return total;
    }
    
    public int clearUsedLines() {
        int index, count;
        int total = 0;
        count = getImportLineCount();
        for ( index = 0; index < count; index++) {
            addIndexed(index, "Line", "USED", "FALSE", true);
            addIndexed(index, "Line", "USEDBY", null, true);
        }
        return total;
    }
    
    public Object getLineUsedBy(int index) {
        return getIndexedValue(index,"Line","USEDBY");
    }
    
    public int getImportLineCount() {
        return getIndexedSize( "Line" );
    }
    
  /*  public String getName() {
        // Should construct a name based on import information and type
        if ( getAbility() != null ) {
            return getAbility().getName();
        }
        else {
            return getImportLine(0);
        }
    } */
    
    /** Getter for property type.
     * @return Value of property type.
     */
    public String getType() {
        return getStringValue( "AbilityImport.TYPE" );
    }
    
    /** Setter for property type.
     * Type should be one of the following:
     * null - Not yet decided
     * POWER - based off straight power
     * MATCH - based off a matched ability
     * SUBLIST - Place holder for a sublist
     * @param type New value of property type.
     */
    public void setType(String type) {
        add( "AbilityImport.TYPE", type, true);
    }
    
    /** Getter for property CPCost.
     * @return Value of property CPCost.
     */
    public int getCPCost() {
        Integer i = getIntegerValue( "AbilityImport.CPCOST" );
        return (i == null ) ? 0 : i.intValue();
    }
    
    /** Setter for property CPCost.
     * @param CPCost New value of property CPCost.
     */
    public void setCPCost(int CPCost) {
        add( "AbilityImport.CPCOST", new Integer(CPCost),true);
    }
    
    /** Getter for property CPCost.
     * @return Value of property CPCost.
     */
    public String getName() {
        return getStringValue("AbilityImport.NAME");
    }
    
    /** Setter for property CPCost.
     * @param CPCost New value of property CPCost.
     */
    public void setName(String name) {
        add( "AbilityImport.NAME",name,true);
    }
    
    /** Getter for property CPCost.
     * @return Value of property CPCost.
     */
    public String getPowerName() {
        return getStringValue("AbilityImport.POWERNAME");
    }
    
    /** Setter for property CPCost.
     * @param CPCost New value of property CPCost.
     */
    public void setPowerName(String name) {
        add( "AbilityImport.POWERNAME",name,true);
    }
    
    /** Getter for property match.
     * @return Value of property match.
     */
    public Ability getMatch() {
        Object o = getValue("AbilityImport.MATCHINGABILITY");
        return ( o == null ) ? null : (Ability)o;
    }
    
    /** Setter for property match.
     *
     * @param match New value of property match.
     */
    public void setMatch(Ability match) {
        setType("MATCH");
        add("AbilityImport.MATCHINGABILITY", match, true);
        setAbility( generateAbility() );
    }
    
    /** Getter for property match.
     * @return Value of property match.
     */
    public Ability getTemplate() {
        return (Ability)getValue("AbilityImport.TEMPLATEABILITY");
    }
    
    /** Setter for property match.
     *
     * @param match New value of property match.
     */
    public void setTemplate(Ability template) {
        setType("TEMPLATE");
        add("AbilityImport.TEMPLATEABILITY", template, true);
        setAbility( generateAbility() );
    }
    
    public void addPowerGuess(Power power, int likelyhood) {
        int index;
        
        if ( findIndexed("PowerGuess", "POWER", power )  == -1 ) {
            // Not already in list...
            index = createIndexed( "PowerGuess", "POWER", power);
            addIndexed(index, "PowerGuess", "LIKELYHOOD", new Integer(likelyhood));
        }
    }
    
    public Power getBestPowerGuess() {
        int index, count;
        int best = 0;
        Power power = null;
        Integer likelyhood;
        
        count = getIndexedSize("PowerGuess");
        for(index=0;index<count;index++) {
            likelyhood = getIndexedIntegerValue( index, "PowerGuess", "LIKELYHOOD");
            if ( likelyhood.intValue() > best ) {
                power = (Power)getIndexedValue( index, "PowerGuess", "POWER");
                best = likelyhood.intValue();
            }
        }
        return power;
    }
    
    /** Getter for property power.
     * @return Value of property power.
     */
    public Power getPower() {
        Object o = getValue( "AbilityImport.POWER" );
        return (o==null) ? null : (Power) o;
    }
    
    /** Setter for property power.
     * @param power New value of property power.
     */
    public void setPower(Power power) {
        if ( power != getPower() ) {
            if ( power != null ) {
                setType("POWER");
                add( "AbilityImport.POWER", power, true);
            }
            else {
                setType(null);
                remove("AbilityImport.POWER");
            }
            
            
            
            setAbility( generateAbility() );
        }
    }
    
    public void setSublist(String sublist) {
        add("Sublist.NAME", sublist, true);
    }
    
    public String getSublist() {
        return getStringValue("Sublist.NAME");
    }
    
    public boolean isSublist() {
        String type = getType();
        return type != null && type.equals("SUBLIST");
    }
    
    public void determinePower() {
        
    }
    
    private static final int ADV = 1;
    private static final int LIM = 2;
    private static final int SP = 3;
    
    public void determineALs(Ability ability) {
        int index,count;
        int aindex, lindex, sindex;
        Advantage advantage;
        String padName = null;
        Limitation limitation;
        SpecialParameter specialParameter;
        int likelyhood, best;
        int type = 0;
        Iterator i;
        
        // Run through the advantage list and try to identify the Ability
        count = getImportLineCount();
        
        for(index = 0; index < count; index++) {
            if (isLineUsed(index) == false) {
                best = 0;
                
                i = PADRoster.getAdvantageIterator();
                while ( i.hasNext() ) {
                    String name = (String)i.next();
                    advantage = PADRoster.getSharedAdvantageInstance( name );
                    likelyhood = advantage.identifyAdvantage(this,index);
                    if ( likelyhood == 10 ) {
                        best = 10;
                        type = ADV;
                        padName = name;
                        break;
                    }
                    else if ( likelyhood > best ) {
                        best = likelyhood;
                        type = ADV;
                        padName = name;
                    }
                }
                
                if ( best != 10 ) {
                    i = PADRoster.getLimitationIterator();
                    while ( i.hasNext() ) {
                        String name = (String)i.next();
                        limitation = PADRoster.getSharedLimitationInstance( name );
                        likelyhood = limitation.identifyLimitation(this,index);
                        if ( likelyhood == 10 ) {
                            best = 10;
                            type = LIM;
                            padName = name;
                            break;
                        }
                        else if ( likelyhood > best ) {
                            best = likelyhood;
                            type = LIM;
                            padName = name;
                        }
                    }
                }
                
                if ( best != 10 ) {
                    i = PADRoster.getSpecialParameterIterator();
                    while ( i.hasNext() ) {
                        String name = (String)i.next();
                        specialParameter = PADRoster.getSharedSpecialParameterInstance( name );
                        likelyhood = specialParameter.identifySpecialParameter(this,index);
                        if ( likelyhood == 10 ) {
                            best = 10;
                            type = SP;
                            padName = name;
                            break;
                        }
                        else if ( likelyhood > best ) {
                            best = likelyhood;
                            type = SP;
                            padName = name;
                        }
                    }
                }
                
                if ( best > 0 ) {
                    switch ( type ) {
                        case ADV:
                            advantage = PADRoster.getNewAdvantageInstance( padName );
                            ability.addPAD(advantage,null);
                            aindex = ability.findExactAdvantage(advantage);
                            advantage.importAdvantage(ability, aindex, this, index);
                            advantage.configurePAD(ability, advantage.getParameterList());
                            break;
                        case LIM:
                            limitation = PADRoster.getNewLimitationInstance( padName );
                            ability.addPAD(limitation,null);
                            //lindex = ability.findExactIndexed("Limitation", "LIMITATION", limitation);
                            lindex = ability.findExactLimitation(limitation);
                            limitation.importLimitation(ability, lindex, this, index);
                            limitation.configurePAD(ability, limitation.getParameterList());
                            break;
                        case SP:
                            specialParameter = PADRoster.getSharedSpecialParameterInstance( padName );
                            
                            if ( ! specialParameter.isUnique() || ! ability.hasSpecialParameter( specialParameter ) ){
                                specialParameter = PADRoster.getNewSpecialParameterInstance( padName );
                                ability.addSpecialParameter(specialParameter);
                                
                            }
                            else {
                                // Already has a unique specialParameter, so just go get it...
                                specialParameter = ability.findSpecialParameter( padName );
                            }
                                
                            sindex = ability.findExactIndexed("SpecialParameter", "SPECIALPARAMETER", specialParameter);
                            
                            specialParameter.importSpecialParameter(ability, sindex, this, index);
                            specialParameter.configure(ability, specialParameter.getParameterList(ability,sindex));
                            break;
                    }
                }
            }
        }
    }
    
    public Ability getAbility() {
        Object ability = getValue( "AbilityImport.ABILITY" );
        if ( ability == null ) {
            ability = generateAbility();
            add( "AbilityImport.ABILITY", ability, true);
        }
        return (ability != null) ? (Ability)ability : null;
    }
    
    private void setAbility(Ability ability) {
        add( "AbilityImport.ABILITY", ability, true);
    }
    
    public Ability generateAbility() {
        Power power;
        Advantage advantage;
        Limitation limitation;
        Object o;
        ParameterList pl;
        String line;
        int index, count;
        
        if ( getType() != null && getType().equals("MATCH") ) {
            Ability matchAbility = getMatch();
            Ability newAbility = (Ability)matchAbility.clone();
            newAbility.setName( getName() );
            
            //    System.out.println( newAbility.dumpDetailList() );
            
            clearUsedLines();
            
            power = matchAbility.getPower();
            if ( power != null ) {
                newAbility.addPAD(power,null);
                pl = power.createParameterList(matchAbility, -1);
                // pl.clearSetValues();
                power.importPower(newAbility, this);
                power.configurePAD(newAbility, power.getParameterList(newAbility,-1));
            }
            
            int aindex;
            count = newAbility.getAdvantageCount();
            for ( index = 0; index < count; index++) {
                Advantage a = (Advantage)newAbility.getAdvantage(index);
                //  newAbility.addPAD(a, null);
                aindex = newAbility.findExactAdvantage(a);
                a.importAdvantage(newAbility, aindex, this, 0);
                a.configurePAD(newAbility, a.getParameterList(newAbility, aindex));
            }
            
            //count = newAbility.getIndexedSize("Limitation");
            count = newAbility.getLimitationCount();
            for ( index = 0; index < count; index++) {
                //Limitation l = (Limitation)newAbility.getIndexedValue(index,"Limitation","LIMITATION");
                Limitation l = newAbility.getLimitation(index);
                //   newAbility.addPAD(l, null);
                //aindex = newAbility.findExactIndexed("Limitation","LIMITATION",l);
                aindex = newAbility.findExactLimitation(l);
                l.importLimitation(newAbility, aindex, this, 0);
                l.configurePAD(newAbility, l.getParameterList(newAbility, aindex));
            }
            
            determineALs(newAbility);
            
            //  newAbility.setSublist( getSublist() );
            
            // newAbility.setSpecialEffect( getStringValue("AbilityImport.SPECIALEFFECT"));
            count = getIndexedSize("SpecialEffect");
            for ( index = 0; index < count; index++) {
                String name =  getIndexedStringValue(index,"SpecialEffect","NAME");
                
                SpecialEffect se = PADRoster.getNewSpecialEffectInstance(name);
                if ( se != null ) {
                    newAbility.addSpecialEffect(se);
                }
            }
            
            // Setup the Normally on Stuff...
            String normallyOn = getStringValue("AbilityImport.NORMALLYON");
            if ( normallyOn != null ) {
                if ( normallyOn.equals("TRUE") ) {
                    newAbility.setNormallyOn(true);
                }
                else {
                    newAbility.setNormallyOn(false);
                }
            }
            
            return newAbility;
        }
        else if ( getType() != null && getType().equals("TEMPLATE") ) {
            Ability template = getTemplate();
            Ability newAbility = (Ability)template.clone();
            
            newAbility.setName( getName() );
            
            clearUsedLines();
            
            power = newAbility.getPower();
            if ( power != null ) {
                newAbility.addPAD(power,null);
                power.importPower(newAbility, this);
                power.configurePAD(newAbility, power.getParameterList(newAbility,-1));
            }
            
            int aindex;
            count = newAbility.getAdvantageCount();
            for ( index = 0; index < count; index++) {
                Advantage a = (Advantage)newAbility.getAdvantage(index);
                //  newAbility.addPAD(a, null);
                aindex = newAbility.findExactAdvantage(a);
                a.importAdvantage(newAbility, aindex, this, 0);
                a.configurePAD(newAbility, a.getParameterList(newAbility, aindex));
            }
            
            //count = newAbility.getIndexedSize("Limitation");
            count = newAbility.getLimitationCount();
            for ( index = 0; index < count; index++) {
                //Limitation l = (Limitation)newAbility.getIndexedValue(index,"Limitation","LIMITATION");
                Limitation l = newAbility.getLimitation(index);
                //    newAbility.addPAD(l, null);
                //aindex = newAbility.findExactIndexed("Limitation","LIMITATION",l);
                aindex = newAbility.findExactLimitation(l);
                l.importLimitation(newAbility, aindex, this, 0);
                l.configurePAD(newAbility, l.getParameterList(newAbility, aindex));
            }
            
            determineALs(newAbility);
            
            count = getIndexedSize("SpecialEffect");
            for ( index = 0; index < count; index++) {
                String name =  getIndexedStringValue(index,"SpecialEffect","NAME");
                
                SpecialEffect se = PADRoster.getNewSpecialEffectInstance(name);
                if ( se != null ) {
                    newAbility.addSpecialEffect(se);
                }
            }
            
            // Setup the Normally on Stuff...
            String normallyOn = getStringValue("AbilityImport.NORMALLYON");
            if ( normallyOn != null ) {
                if ( normallyOn.equals("TRUE") ) {
                    newAbility.setNormallyOn(true);
                }
                else {
                    newAbility.setNormallyOn(false);
                }
            }
            
            return newAbility;
        }
        
        return null;
    }
    
    /** Search for a pattern in the abilityImport and process the line parameters.
     *
     * searchForParameters will search all of the import lines in the AbilityImport for the specified pattern.
     * Once it finds the first occurance which hasn't been used already, it will process that line according
     * to the parameters array, mark the line as used, and return the line number.
     *
     * Only one line will ever be processed although all lines will be search until the first match is found.
     *
     * @param pattern Pattern to be found.
     * @param parameters Parameters Names and types contained on import line.
     * @param ability Ability being imported into.
     * @param source Limitation/Advantage/Power being imported into.
     * @param pl ParameterList for the Power/Advantage/Limitation.
     * @return line index which match the pattern.
     */
    public int searchForParameters(String pattern, Object[] parameters, Ability ability, Object source, ParameterList pl) {
        boolean found = false;
        
        int index, count, pindex, jindex;
        Class type;
        String parameter;
        String stringValue;
        int intValue;
        String line;
        
        
        count = getImportLineCount();
        for(index=0;index<count;index++) {
            if ( isLineUsed(index) == true ) continue;
            line = getImportLine(index);
            if ( ChampionsMatcher.matches( pattern, line ) ) {
                //String lastMatch = ChampionsMatcher.dumpLastMatch();
                if ( parameters != null ) {
                    jindex = 1;
                    for (pindex = 0; pindex+1 < parameters.length;pindex+=2) {
                        parameter = (String)parameters[pindex];
                        type = (Class)parameters[pindex+1];
                        if ( type.equals(Integer.class) ) {
                            intValue = ChampionsMatcher.getIntMatchedGroup(jindex);
                            pl.setParameterValue( parameter, new Integer(intValue) );
                        }
                        else if ( type.equals(Boolean.class) ) {
                            stringValue = ChampionsMatcher.getMatchedGroup(jindex);
                            if ( stringValue != null && stringValue.equals("") == false ) {
                                pl.setParameterValue( parameter, "TRUE" );
                            }
                            else {
                                pl.setParameterValue( parameter, "FALSE" );
                            }
                        }
                        else {
                            stringValue = ChampionsMatcher.getMatchedGroup(jindex);
                            if ( parameter.startsWith( "!" ) ) {
                                // Special Parameter
                                if ( parameter.equals("!Name") ) {
                                    ability.setName( stringValue );
                                }
                                
                            }
                            else {
                                pl.setParameterValue( parameter, stringValue );
                            }
                        }
                        jindex++;
                    }
                }
                
                setLineUsed(index, source);
                found = true;
                break;
            }
        }
        
        return (found==true) ? index : -1;
    }
    
    
    /** Attempt to match a pattern against a single indicated line and process parameters if the pattern matches.
     *
     * importParametersForLine will attempt to match a single import line in the AbilityImport with the specified pattern.
     * If it matches, it will process that lines parameters, mark the line as used, and return true.  Otherwise, it
     * will return false;
     *
     * @param pattern Pattern to be found.
     * @param parameters Parameters Names and types contained on import line.
     * @param ability Ability being imported into.
     * @param line Line to
     * @param source Limitation/Advantage/Power being imported into.
     * @param pl ParameterList for the Power/Advantage/Limitation.
     * @return line index which match the pattern.
     */
    public boolean importParametersForLine(String pattern, Object[] parameters, Ability ability, int lineIndex, Object source, ParameterList pl) {
        boolean found = false;
        
        int pindex, jindex;
        Class type;
        String parameter;
        String stringValue;
        int intValue;
        String line;
        
        
        if ( isLineUsed(lineIndex) == true ) return false;
        
        line = getImportLine(lineIndex);
        if ( ChampionsMatcher.matches( pattern, line ) ) {
            if ( parameters != null ) {
                jindex = 1;
                for (pindex = 0; pindex+1 < parameters.length;pindex+=2) {
                    parameter = (String)parameters[pindex];
                    type = (Class)parameters[pindex+1];
                    if ( type.equals(Integer.class) ) {
                        intValue = ChampionsMatcher.getIntMatchedGroup(jindex);
                        pl.setParameterValue( parameter, new Integer(intValue) );
                    }
                    else if ( type.equals(Boolean.class) ) {
                        stringValue = ChampionsMatcher.getMatchedGroup(jindex);
                        if ( stringValue != null && stringValue.equals("") == false ) {
                            pl.setParameterValue( parameter, "TRUE" );
                        }
                        else {
                            pl.setParameterValue( parameter, "FALSE" );
                        }
                    }
                    else {
                        stringValue = ChampionsMatcher.getMatchedGroup(jindex);
                        if ( parameter.startsWith( "!" ) ) {
                            // Special Parameter
                            if ( parameter.equals("!Name") ) {
                                ability.setName( stringValue );
                            }
                            
                        }
                        else {
                            pl.setParameterValue( parameter, stringValue );
                        }
                    }
                    jindex++;
                }
            }
            
            setLineUsed(lineIndex, source);
            found = true;
        }
        
        return found;
    }
    
    /** Getter for property characterImport.
     * @return Value of property characterImport.
     */
    public CharacterImport getCharacterImport() {
        return (CharacterImport)getValue("AbilityImport.CHARACTERIMPORT");
    }
    
    /** Setter for property characterImport.
     * @param characterImport New value of property characterImport.
     */
    public void setCharacterImport(CharacterImport characterImport) {
        add( "AbilityImport.CHARACTERIMPORT", characterImport, true, false);
    }
    
    public String toString() {
        return "AbilityImport: " + getName();
    }
    
    public void finishImport(Character character) {
        Ability newAbility = getAbility();
        if ( newAbility == null ) return;
        
            Power power = newAbility.getPower();
            if ( power != null ) {
                newAbility.addPAD(power,null);
                boolean result = power.finalizeImport(newAbility, this);
                if ( result ) power.configurePAD(newAbility, power.getParameterList(newAbility,-1));
            }
            
            int aindex;
            int count = newAbility.getAdvantageCount();
            for (int index = 0; index < count; index++) {
                Advantage a = (Advantage)newAbility.getAdvantage(index);
                //  newAbility.addPAD(a, null);
                aindex = newAbility.findExactAdvantage(a);
                boolean result = a.finalizeImport(newAbility, aindex, this);
                if ( result ) a.configurePAD(newAbility, a.getParameterList(newAbility, aindex));
            }
            
            //count = newAbility.getIndexedSize("Limitation");
            count = newAbility.getLimitationCount();
            for (int index = 0; index < count; index++) {
                //Limitation l = (Limitation)newAbility.getIndexedValue(index,"Limitation","LIMITATION");
                Limitation l = newAbility.getLimitation(index);
                //    newAbility.addPAD(l, null);
                //aindex = newAbility.findExactIndexed("Limitation","LIMITATION",l);
                aindex = newAbility.findExactLimitation(l);
                boolean result = l.finalizeImport(newAbility, aindex, this);
                if ( result ) l.configurePAD(newAbility, l.getParameterList(newAbility, aindex));
            }
            
            count = getIndexedSize("SpecialParameter");
            for (int index = 0; index < count; index++) {
                SpecialParameter l = (SpecialParameter)newAbility.getIndexedValue(index,"SpecialParameter","SPECIALPARAMETER");
                //    newAbility.addPAD(l, null);
                aindex = newAbility.findExactIndexed("SpecialParameter","SPECIALPARAMETER",l);
                boolean result = l.finalizeImport(newAbility, aindex, this);
                if ( result ) l.configure(newAbility, l.getParameterList(newAbility, aindex));
            }
    }
    
}
