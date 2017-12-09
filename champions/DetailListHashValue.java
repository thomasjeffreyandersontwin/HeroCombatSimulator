package champions;

public class DetailListHashValue extends Object {
    protected int extent = 10;
    protected Object[] oldValues;
    protected Object[] newValues;
    protected int position;
    protected int arrayLocation;
    
    public DetailListHashValue(int arrayLocation) {
        oldValues = new Object[2];
        newValues = new Object[2];
        position = 0;
        
        this.arrayLocation = arrayLocation;
    }
    
    public void addValues(Object oldValue, Object newValue) {
        if ( position >= oldValues.length ) {
            extendArray();
        }
        oldValues[position] = oldValue;
        newValues[position] = newValue;
        position++;
    }
    
    public Object getOldValue(int index) {
        return oldValues[index];
    }
    
    public Object getNewValue(int index) {
        return newValues[index];
    }
    
    public int getSize() {
        return position;
    }
    
    private void extendArray() {
        Object[] nOldValues = new Object[oldValues.length + extent];
        Object[] nNewValues = new Object[oldValues.length + extent];
        
        int i;
        for ( i=0;i<position;i++) {
            nOldValues[i] = oldValues[i];
            nNewValues[i] = newValues[i];
        }
        
        oldValues = nOldValues;
        newValues = nNewValues;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer("DetailListHashValue [");
        sb.append( "pos " );
        sb.append( Integer.toString(position));
        sb.append( ", len " );
        sb.append( Integer.toString(oldValues.length));
        sb.append( ", pairs " );
        int i;
        for (i=0;i<position;i++) {
            if ( i>0 ) sb.append (", ");
            sb.append("[");
            sb.append(oldValues[i]);
            sb.append(",");
            sb.append(newValues[i]);
            sb.append("]");
        }
        sb.append("]");
        return sb.toString();
    }
    
    public synchronized void reset() {
        int i;
        for (i=0;i<position;i++) {
            oldValues[i]=null;
            newValues[i]=null;
        }
        position = 0;
        
        hashUsed[arrayLocation] = false;
        if ( hashValuesFirstFree == -1 || arrayLocation < hashValuesFirstFree ) {
            hashValuesFirstFree = arrayLocation;
        }
    }
    
    static private DetailListHashValue[] hashValues = new DetailListHashValue[10];
    static private boolean[] hashUsed = new boolean[10];
    static private int hashValuesExtent = 10;
    static private int hashValuesPosition = 0;
    static private int hashValuesFirstFree = -1;
    
    static public synchronized DetailListHashValue getEmptyHashValue() {
        DetailListHashValue dlhv = null;
        if ( hashValuesFirstFree != -1 ) {
            hashUsed[hashValuesFirstFree] = true;
            dlhv = hashValues[hashValuesFirstFree];
            hashValuesFirstFree = -1;
            return dlhv;
        }
        else {
            int i;
            for (i=0;i<hashValuesPosition;i++) {
                if ( hashUsed[i] == false ) {
                    // We found one
                    hashUsed[i] = true;
                    return hashValues[i];
                }
            }
            
            // Check to see if we must extend the arrays
            if ( hashValuesPosition == hashValues.length ) {
                extendHashArray();
            }
            
            // Create a new one at position, and a new one at position + 1
            // return the first new one and mark the second at the new free
            dlhv = new DetailListHashValue(hashValuesPosition);
            hashValues[hashValuesPosition] = dlhv;
            hashUsed[hashValuesPosition] = true;
            hashValuesPosition ++;
            
            hashValues[hashValuesPosition] = new DetailListHashValue(hashValuesPosition);
            hashUsed[hashValuesPosition] = false;
            hashValuesFirstFree = hashValuesPosition;
            hashValuesPosition++;
            
            return dlhv;
        }
    }
    
    private static void extendHashArray() {
        DetailListHashValue[] nHashValues = new DetailListHashValue[hashValues.length + hashValuesExtent];
        boolean[] nHashUsed = new boolean[hashValues.length + hashValuesExtent];
        
        int i;
        for ( i=0;i<hashValuesPosition;i++) {
            nHashValues[i] = hashValues[i];
            nHashUsed[i] = hashUsed[i];
        }
        
        hashValues = nHashValues;
        hashUsed = nHashUsed;
    }
    
    public static String dumpStatus() {
        StringBuffer sb = new StringBuffer("DetailListHashValue Status [");
        sb.append( "pos " );
        sb.append( Integer.toString(hashValuesPosition));
        sb.append( ", len " );
        sb.append( Integer.toString(hashValues.length));
        sb.append( ", ff " );
        sb.append( Integer.toString(hashValuesFirstFree));
        sb.append( "\nArray Dump\n" );
        int i;
        for (i=0;i<hashValuesPosition;i++) {
            sb.append("hashValue[");
            sb.append(i);
            sb.append("] is ");
            sb.append( hashUsed[i] ? "used." : "free.");
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
}
