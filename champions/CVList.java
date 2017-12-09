/*
 * CVList.java
 *
 * Created on November 13, 2000, 7:01 PM
 */

package champions;

import champions.interfaces.*;
/**
 *
     * @author  unknown
 * @version
 */
public class CVList extends DetailList
        implements ChampionsConstants {
    static final long serialVersionUID = 1973921950928728800L;
    
    private String sourceIndex = "Source";
    private String targetIndex = "Target";
    
    /** Holds value of property type. */
    private int type;
    
    /** Creates new CVList */
    public CVList() {
        setFireChangeByDefault(false);
    }
    
    public void setSourceName( String name ) {
        add(sourceIndex + ".NAME",  name ,  true);
    }
    
    public String getSourceName() {
        return getStringValue(sourceIndex + ".NAME");
    }
    
    public void setTargetName( String name ) {
        add(targetIndex + ".NAME",  name ,  true);
    }
    
    public String getTargetName() {
        return getStringValue(targetIndex + ".NAME");
    }
    
    public void setSourceCVBase(int base) {
        add(sourceIndex + ".BASECV",  new Integer(base) , true);
    }
    
    public int addSourceCVModifier( String desc, int value) {
        return addSourceCVModifier(desc, value, "TRUE" );
    }
    
    public int addSourceCVModifier( String desc, int value, String active ) {
        int index;
        if ( (index = findIndexed(sourceIndex,"DESCRIPTION",desc) ) == -1 ) {
            index = createIndexed(   sourceIndex, "TYPE", "MOD" ) ;
            addIndexed(index,  sourceIndex, "DESCRIPTION", desc, true);
            addIndexed(index,  sourceIndex, "ACTIVE" , active , true);
        }
        
        if ( getIndexedBooleanValue(index, sourceIndex, "MANUAL") == false ) {
            addIndexed(index,  sourceIndex, "VALUE", new Integer(value), true);
        }
        
        return index;
    }
    
    //I didn't end up using this so we can remove if necessary
    public int addSourceCVMultiplier( String desc, double value ) {
        return addSourceCVMultiplier( desc, value, "TRUE" );
    }
    
    public int addSourceCVMultiplier( String desc, double value,String active ) {
        int index;
        if ( (index = findIndexed(sourceIndex,"DESCRIPTION",desc) ) == -1 ) {
            index = createIndexed(   sourceIndex, "TYPE", "MULTIPLE" ) ;
            addIndexed(index,  sourceIndex, "DESCRIPTION", desc, true);
            addIndexed(index,  sourceIndex, "ACTIVE", active , true);
        }
        
        if ( getIndexedBooleanValue(index, sourceIndex, "MANUAL") == false ) {
            addIndexed(index,  sourceIndex, "VALUE", new Double(value), true);
        }
        
        return index;
    }
    
    
    
    public int addSourceCVSet( String desc, int value ) {
        int index;
        if ( (index = findIndexed(sourceIndex,"DESCRIPTION",desc) ) == -1 ) {
            index = createIndexed(   sourceIndex, "TYPE", "SET" ) ;
            addIndexed(index,  sourceIndex, "DESCRIPTION", desc, true);
            addIndexed(index,  sourceIndex, "ACTIVE", "TRUE", true);
        }
        
        if ( getIndexedBooleanValue(index, sourceIndex, "MANUAL") == false ) {
            addIndexed(index,  sourceIndex, "VALUE", new Integer(value), true);
        }
        
        return index;
    }
    
    public int addRangeCVModifier( String desc, int value ) {
        int index;
        if ( (index = findIndexed(sourceIndex,"DESCRIPTION",desc) ) == -1 ) {
            index = createIndexed(   sourceIndex, "TYPE", "MOD" ) ;
            addIndexed(index,  sourceIndex, "DESCRIPTION", desc, true);
            addIndexed(index,  sourceIndex, "ACTIVE", "TRUE", true);
            addIndexed(index,  sourceIndex, "SPECIAL", "RANGE", true);
        }
        
        if ( getIndexedBooleanValue(index, sourceIndex, "MANUAL") == false ) {
            addIndexed(index,  sourceIndex, "VALUE", new Integer(value), true);
        }
        
        return index;
    }

        public int addConcealmentCVModifier( String desc, int value ) {
        int index;
        if ( (index = findIndexed(sourceIndex,"DESCRIPTION",desc) ) == -1 ) {
            index = createIndexed(   sourceIndex, "TYPE", "MOD" ) ;
            addIndexed(index,  sourceIndex, "DESCRIPTION", desc, true);
            addIndexed(index,  sourceIndex, "ACTIVE", "TRUE", true);
            addIndexed(index,  sourceIndex, "SPECIAL", "CONCEALMENT", true);
        }

        if ( getIndexedBooleanValue(index, sourceIndex, "MANUAL") == false ) {
            addIndexed(index,  sourceIndex, "VALUE", new Integer(value), true);
        }

        return index;
    }
    
    public int addThrowCVModifier( String desc, int value ) {
        int index;
        if ( (index = findIndexed(sourceIndex,"DESCRIPTION",desc) ) == -1 ) {
            index = createIndexed(   sourceIndex, "TYPE", "MOD" ) ;
            addIndexed(index,  sourceIndex, "DESCRIPTION", desc, true);
            addIndexed(index,  sourceIndex, "ACTIVE", "TRUE", true);
            addIndexed(index,  sourceIndex, "SPECIAL", "THROW", true);
        }
        
        if ( getIndexedBooleanValue(index, sourceIndex, "MANUAL") == false ) {
            addIndexed(index,  sourceIndex, "VALUE", new Integer(value), true);
        }
        
        return index;
    }
    
    public int overrideSourceCVModifier( String desc, int value ) {
        int index;
        if ( (index = findIndexed(sourceIndex,"DESCRIPTION",desc) ) == -1 ) {
            index = createIndexed(   sourceIndex, "TYPE", "MOD" ) ;
            addIndexed(index,  sourceIndex, "DESCRIPTION", desc, true);
            addIndexed(index,  sourceIndex, "ACTIVE", "TRUE", true);
        }
        
        addIndexed(index,  sourceIndex, "VALUE", new Integer(value), true);
        addIndexed(index, sourceIndex, "MANUAL", "TRUE", true);
        
        return index;
    }
    
    public int overrideSourceCVMultiplier( String desc, double value ) {
        int index;
        if ( (index = findIndexed(sourceIndex,"DESCRIPTION",desc) ) == -1 ) {
            index = createIndexed(   sourceIndex, "TYPE", "MULTIPLE" ) ;
            addIndexed(index,  sourceIndex, "DESCRIPTION", desc, true);
            addIndexed(index,  sourceIndex, "ACTIVE", "TRUE", true);
        }
        
        addIndexed(index,  sourceIndex, "VALUE", new Double(value), true);
        addIndexed(index, sourceIndex, "MANUAL", "TRUE", true);
        
        return index;
    }
    
    
    
    public int overrideSourceCVSet( String desc, int value ) {
        int index;
        if ( (index = findIndexed(sourceIndex,"DESCRIPTION",desc) ) == -1 ) {
            index = createIndexed(   sourceIndex, "TYPE", "SET" ) ;
            addIndexed(index,  sourceIndex, "DESCRIPTION", desc, true);
            addIndexed(index,  sourceIndex, "ACTIVE", "TRUE", true);
        }
        
        addIndexed(index,  sourceIndex, "VALUE", new Integer(value), true);
        addIndexed(index, sourceIndex, "MANUAL", "TRUE", true);
        
        return index;
    }
    
    public int overrideRangeCVModifier( String desc, int value ) {
        int index;
        if ( (index = findIndexed(sourceIndex,"DESCRIPTION",desc) ) == -1 ) {
            index = createIndexed(   sourceIndex, "TYPE", "MOD" ) ;
            addIndexed(index,  sourceIndex, "DESCRIPTION", desc, true);
            addIndexed(index,  sourceIndex, "ACTIVE", "TRUE", true);
            addIndexed(index,  sourceIndex, "SPECIAL", "RANGE", true);
        }
        
        addIndexed(index,  sourceIndex, "VALUE", new Integer(value), true);
        addIndexed(index, sourceIndex, "MANUAL", "TRUE", true);
        
        return index;
    }
    
    public void setTargetCVBase(int base) {
        add(targetIndex + ".BASECV",  new Integer(base) , true);
    }
    
    public int addTargetCVModifier( String desc, int value ) {
        int index;
        if ( (index = findIndexed(targetIndex,"DESCRIPTION",desc) ) == -1 ) {
            index = createIndexed(   targetIndex, "TYPE", "MOD" ) ;
            addIndexed(index,  targetIndex, "DESCRIPTION", desc, true);
            addIndexed(index,  targetIndex, "ACTIVE", "TRUE", true);
        }
        if ( getIndexedBooleanValue(index, targetIndex, "MANUAL") == false ) {
            addIndexed(index,  targetIndex, "VALUE", new Integer(value), true);
        }
        
        return index;
    }
    
    public int addTargetCVMultiplier( String desc, double value ) {
        return addTargetCVMultiplier(desc,value, "TRUE");
    }
    
    public int addTargetCVMultiplier( String desc, double value, String active ) {
        int index;
        if ( (index = findIndexed(targetIndex,"DESCRIPTION",desc) ) == -1 ) {
            index = createIndexed(   targetIndex, "TYPE", "MULTIPLE" ) ;
            addIndexed(index,  targetIndex, "DESCRIPTION", desc, true);
            addIndexed(index,  targetIndex, "ACTIVE", active, true);
        }
        
        if ( getIndexedBooleanValue(index, targetIndex, "MANUAL") == false ) {
            addIndexed(index,  targetIndex, "VALUE", new Double(value), true);
        }
        
        return index;
    }
    
    public int addTargetCVSet( String desc, int value ) {
        int index;
        if ( (index = findIndexed(targetIndex,"DESCRIPTION",desc) ) == -1 ) {
            index = createIndexed(   targetIndex, "TYPE", "SET" ) ;
            addIndexed(index,  targetIndex, "DESCRIPTION", desc, true);
            addIndexed(index,  targetIndex, "ACTIVE", "TRUE", true);
        }
        
        if ( getIndexedBooleanValue(index, targetIndex, "MANUAL") == false ) {
            addIndexed(index,  targetIndex, "VALUE", new Integer(value), true);
        }
        
        return index;
    }
    
    public int overrideTargetCVModifier( String desc, int value ) {
        int index;
        if ( (index = findIndexed(targetIndex,"DESCRIPTION",desc) ) == -1 ) {
            index = createIndexed(   targetIndex, "TYPE", "MOD" ) ;
            addIndexed(index,  targetIndex, "DESCRIPTION", desc, true);
            addIndexed(index,  targetIndex, "ACTIVE", "TRUE", true);
        }
        
        addIndexed(index,  targetIndex, "VALUE", new Integer(value), true);
        addIndexed(index, targetIndex, "MANUAL", "TRUE", true);
        
        return index;
    }
    
    public int overrideTargetCVMultiplier( String desc, double value ) {
        int index;
        if ( (index = findIndexed(targetIndex,"DESCRIPTION",desc) ) == -1 ) {
            index = createIndexed(   targetIndex, "TYPE", "MULTIPLE" ) ;
            addIndexed(index,  targetIndex, "DESCRIPTION", desc, true);
            addIndexed(index,  targetIndex, "ACTIVE", "TRUE", true);
        }
        
        addIndexed(index,  targetIndex, "VALUE", new Double(value), true);
        addIndexed(index, targetIndex, "MANUAL", "TRUE", true);
        
        return index;
    }
    
    public int overrideTargetCVSet( String desc, int value ) {
        int index;
        if ( (index = findIndexed(targetIndex,"DESCRIPTION",desc) ) == -1 ) {
            index = createIndexed(   targetIndex, "TYPE", "SET" ) ;
            addIndexed(index,  targetIndex, "DESCRIPTION", desc, true);
            addIndexed(index,  targetIndex, "ACTIVE", "TRUE", true);
        }
        
        addIndexed(index,  targetIndex, "VALUE", new Integer(value), true);
        addIndexed(index, targetIndex, "MANUAL", "TRUE", true);
        
        return index;
    }
    
    public void removeSourceModifier(String desc) {
        int index = findIndexed(sourceIndex, "DESCRIPTION", desc);
        if ( index != -1 ) {
            removeAllIndexed(index, sourceIndex);
        }
    }
    
    public void removeTargetModifier(String desc) {
        int index = findIndexed(targetIndex, "DESCRIPTION", desc);
        if ( index != -1 ) {
            removeAllIndexed(index, targetIndex);
        }
    }
    
    public int getSourceCV() {
        int ocv;
        Integer i;
        int count, index;
        String type;
        boolean active;
        Object value;
        
        
        // Figure out the final ocv
        if ( ( i = getIntegerValue(sourceIndex + ".BASECV" )) == null ) i = new Integer(0);
        ocv = i.intValue();
        
        count = getIndexedSize( sourceIndex );
        
        // Find the modifiers...
        for(index=0;index<count;index++) {
            if ( (type = getIndexedStringValue(index,sourceIndex,"TYPE")) != null && type.equals("MOD") ) {
                value = getIndexedValue( index,sourceIndex,"VALUE" );
                active = getIndexedBooleanValue( index, sourceIndex, "ACTIVE" );
                
                if ( active ) {
                    ocv += ((Integer)value).intValue();
                }
            }
        }
        
        for(index=0;index<count;index++) {
            if ( (type = getIndexedStringValue(index,sourceIndex,"TYPE")) != null && type.equals("MULTIPLE") ) {
                value = getIndexedValue( index,sourceIndex,"VALUE" );
                active = getIndexedBooleanValue( index, sourceIndex, "ACTIVE" );
                
                if ( active ) {
                    //System.out.println( ((Double)value).toString() );
                    ocv = ChampionsUtilities.roundValue(ocv * ((Double)value).doubleValue(),true);
                }
            }
        }
        
        for(index=0;index<count;index++) {
            if ( (type = getIndexedStringValue(index,sourceIndex,"TYPE")) != null && type.equals("SET") ) {
                value = getIndexedValue( index,sourceIndex,"VALUE" );
                active = getIndexedBooleanValue( index, sourceIndex, "ACTIVE" );
                
                if ( active ) {
                    //System.out.println( ((Double)value).toString() );
                    ocv = ((Integer)value).intValue();
                }
            }
        }
        
        return ocv;
    }
    
    public int getTargetCV() {
        int dcv;
        Integer i;
        int count, index;
        String type;
        boolean active;
        Object value;
        // Figure out the final dcv
        if ( ( i = getIntegerValue(targetIndex + ".BASECV" )) == null ) i = new Integer(0);
        
        dcv = i.intValue();
        
        count = getIndexedSize( targetIndex );
        
        // Find the modifiers...
        for(index=0;index<count;index++) {
            if ( (type = getIndexedStringValue(index,targetIndex,"TYPE")) != null && type.equals("MOD") ) {
                value = getIndexedValue( index,targetIndex,"VALUE" );
                active = getIndexedBooleanValue( index, targetIndex, "ACTIVE" );
                
                if ( active ) {
                    dcv += ((Integer)value).intValue();
                }
            }
        }
        
        for(index=0;index<count;index++) {
            if ( (type = getIndexedStringValue(index,targetIndex,"TYPE")) != null && type.equals("MULTIPLE") ) {
                value = getIndexedValue( index,targetIndex,"VALUE" );
                active = getIndexedBooleanValue( index, targetIndex, "ACTIVE" );
                
                if ( active ) {
                    //System.out.println( ((Double)value).toString() );
                    dcv = ChampionsUtilities.roundValue(dcv * ((Double)value).doubleValue(), true);
                }
            }
        }
        
        for(index=0;index<count;index++) {
            if ( (type = getIndexedStringValue(index,targetIndex,"TYPE")) != null && type.equals("SET") ) {
                value = getIndexedValue( index,targetIndex,"VALUE" );
                active = getIndexedBooleanValue( index, targetIndex, "ACTIVE" );
                
                if ( active ) {
                    //System.out.println( ((Double)value).toString() );
                    dcv = ((Integer)value).intValue();
                }
            }
        }
        
        
        return dcv;
    }
    
    public void setInitialized( boolean initialized ) {
        add( "CVList.INITIALIZED",  initialized ? "TRUE" : "FALSE" ,  true);
    }
    
    public boolean isInitialized() {
        return getBooleanValue("CVList.INITIALIZED");
    }
    
    /** Getter for property type.
     * @return Value of property type.
     */
    public int getType() {
        return type;
    }
    
    /** Setter for property type.
     * @param type New value of property type.
     */
    public void setType(int type) {
        this.type = type;
    }
    
    public void swapSourceAndTarget() {
        String temp = sourceIndex;
        sourceIndex = targetIndex;
        targetIndex = temp;
    }
    
    public String getSourceIndex() {
        return sourceIndex;
    }
    
    public String getTargetIndex() {
        return targetIndex;
    }
    
    public int getSourceCount() {
        return getIndexedSize(sourceIndex);
    }
    
    public int getSourceValue(int index) {
        Integer i = getIndexedIntegerValue(index, sourceIndex, "VALUE");
        return i == null ? 0 : i.intValue();
    }
    
    public String getSourceDescription(int index) {
        return getIndexedStringValue(index, sourceIndex, "DESCRIPTION");
    }
    
    public String getSourceType(int index) {
        return getIndexedStringValue(index, sourceIndex, "TYPE");
    }
    
}