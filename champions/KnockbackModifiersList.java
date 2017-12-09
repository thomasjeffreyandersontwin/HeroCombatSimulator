/*
 * CVList.java
 *
 * Created on November 13, 2000, 7:01 PM
 */

package champions;

/**
 *
 * @author  unknown
 * @version
 */
public class KnockbackModifiersList extends DetailList {
    

    public KnockbackModifiersList() {
        setFireChangeByDefault(false);
    }
    
    public void setName( String name ) {
        add("Target.NAME",  name ,  true, false);
    }
    
    public String getName() {
        return getStringValue("Target.NAME");
    }
    
    public void setKnockbackRollBase(int base) {
        // add (   "Base" + ".BASECV", new Integer(base) , true, false);
        int index = addKnockbackRollModifier("Base Knockback Roll", base);
        addIndexed(index,"KnockbackRoll","BASE","TRUE",true,false);
    }
    
    public int addKnockbackRollModifier(String desc, int value ) {
        // int index = createIndexed(KnockbackRoll, "TYPE", "MOD", false ) ;
        int index;
        
        if ( (index = findIndexed("KnockbackRoll", "DESCRIPTION", desc)) == -1 ) {
            index = createIndexed("KnockbackRoll","DESCRIPTION", desc);
            addIndexed(index,  "KnockbackRoll", "ACTIVE", "TRUE", true, false);
        }
        
        //addIndexed(index,  KnockbackRoll, "DESCRIPTION", desc, true, false);
        addIndexed(index,  "KnockbackRoll", "MODIFIER", new Integer(value), true, false);
        
        fireIndexedChanged("KnockbackRoll");
        
        return index;
    }
    
    public int getKnockbackRoll() {
        int finalKnockbackRoll = 0;
        int count, index;
        boolean active;
        Integer value;
        
        
        // Figure out the final finalKnockbackRoll
        //  if ( ( i = getIntegerValue( KnockbackRoll + ".BASECV" )) == null ) i = new Integer(0);
        //  finalKnockbackRoll = i.intValue();
        
        count = getIndexedSize( "KnockbackRoll" );
        
        // Find the modifiers...
        for(index=0;index<count;index++) {
            active = getIndexedBooleanValue(index, "KnockbackRoll", "ACTIVE");
            value = getIndexedIntegerValue(index, "KnockbackRoll", "MODIFIER");
            if ( active && value != null) {
                finalKnockbackRoll += value.intValue();
            }
        }
        
 /*       for(index=0;index<count;index++) {
            if ( (type = getIndexedStringValue(index,KnockbackRoll,"TYPE")) != null && type.equals("MULTIPLE") ) {
                value = getIndexedValue( index,KnockbackRoll,"VALUE" );
                active = getIndexedBooleanValue( index, KnockbackRoll, "ACTIVE" );
  
                if ( active ) {
                    //System.out.println( ((Double)value).toString() );
                    finalKnockbackRoll *= ((Double)value).doubleValue();
                }
            }
        }
  
        for(index=0;index<count;index++) {
            if ( (type = getIndexedStringValue(index,KnockbackRoll,"TYPE")) != null && type.equals("SET") ) {
                value = getIndexedValue( index,KnockbackRoll,"VALUE" );
                active = getIndexedBooleanValue( index, KnockbackRoll, "ACTIVE" );
  
                if ( active ) {
                    //System.out.println( ((Double)value).toString() );
                    finalKnockbackRoll = ((Integer)value).intValue();
                }
            }
        } */
        
        return ( finalKnockbackRoll < 0 ) ? 0 : finalKnockbackRoll;
    }
    
}