/*
 * Dice.java
 *
 * Created on September 13, 2000, 11:49 PM
 */

package champions;


import champions.exception.*;
/**
 *
 * @author  unknown
 * @version
 */
public class Dice extends Object implements java.io.Serializable {
    
    static final long serialVersionUID = -4019254296719115045L;
    
    final static java.util.Random die = new java.util.Random();
    /** Holds value of property stun. */
    private int stun;
    /** Holds value of property body. */
    private int body;
    
    private boolean realized = false;
    private boolean rolled = false;
    
    /** Holds value of property d6. */
    private int d6 = 0;
    /** Holds value of property plus. */
    private int plus = 0;
    /** Holds value of property onehalf. */
    private boolean onehalf = false;
    
    private int[] individualDice;
    private int individualHalf;
    
    /** Creates new Dice */
    public Dice(String size, boolean realize) throws BadDiceException {
        parseDice(size);
        if ( realize ) {
            realizeDice();
        }
    }
    
    public Dice(String size) throws BadDiceException {
        this(size,true);
    }
    
    public Dice() {
        
    }
    
    public Dice(int size, boolean realize) {
        setD6(size);
        if ( realize ) realizeDice();
    }
    
    public Dice(int size) {
        this(size, true);
    }
    
    private void parseDice(String size) throws BadDiceException {
        
    /*    if ( ChampionsMatcher.matches( "([0-9]*)[dD]6", size ) ){
            System.out.println("1: " + ChampionsMatcher.getMatchedGroup(1));
            System.out.println("2: " + ChampionsMatcher.getMatchedGroup(2));
            System.out.println("3: " + ChampionsMatcher.getMatchedGroup(3));
        }
        else if ( ChampionsMatcher.matches( "([0-9]*)[dD]6", size ) ){
            System.out.println("1: " + ChampionsMatcher.getMatchedGroup(1));
            System.out.println("2: " + ChampionsMatcher.getMatchedGroup(2));
            System.out.println("3: " + ChampionsMatcher.getMatchedGroup(3));
        } */
        String base = null;
        String half = null;
        String plus = null;
        
        if ( size == null ) throw new BadDiceException( "Null Dice size");
        
        if ( size.startsWith("-") ) {
            // Everything is null...
        }
        if ( ChampionsMatcher.matches( "([0-9]+)?(.5)?([dD]6)?([+-][0-9]*)?", size ) ){
            base = ChampionsMatcher.getMatchedGroup(1);
            half = ChampionsMatcher.getMatchedGroup(2);
            plus = ChampionsMatcher.getMatchedGroup(4);
        }
        else if ( ChampionsMatcher.matches( "([0-9]+)?(\u00BD)?([dD]6)?([+-][0-9]*)?", size ) ){
            base = ChampionsMatcher.getMatchedGroup(1);
            half = ChampionsMatcher.getMatchedGroup(2);
            plus = ChampionsMatcher.getMatchedGroup(4);
        }
        else {
            throw new BadDiceException();
        }
        
        if ( base == null ) {
            setD6(0);
        }
        else {
            try {
                int d6 = Integer.parseInt(base);
                setD6(d6);
            }
            catch ( NumberFormatException nfe ) {
                throw new BadDiceException();
            }
            
        }
        
        if ( half != null ) {
            setOnehalf(true);
        }
        
        if ( plus != null ) {
            if ( plus.startsWith("+") ) plus = plus.substring(1, plus.length());
            try {
                int plusInt = Integer.parseInt(plus);
                setPlus(plusInt);
            }
            catch ( NumberFormatException nfe ) {
                throw new BadDiceException();
            }
        }
        
    /*    int dposition;
        int plusposition = -1;
        int minposition = -1;
        int halfposition = -1;
        double tempd6;
     
        if ( size == null ) throw ( new BadDiceException() );
     
        dposition = size.indexOf("d");
        if ( dposition == -1 ) {
            dposition = size.indexOf("D");
        }
        if ( dposition == -1 ) {
            dposition = size.length();
        }
     
        plusposition = size.indexOf("+");
        minposition = size.indexOf("-");
        halfposition = size.indexOf("\u00BD");
     
     
        if ( (plusposition != -1 && plusposition < dposition)
        || (minposition != -1 && minposition < dposition)
        || (plusposition > 0 && minposition > 0)) {
            throw new BadDiceException(size);
        }
     
        if ( halfposition != -1 && halfposition > dposition ) {
            throw new BadDiceException(size);
        }
     
        try {
            int index = ( halfposition != -1 ) ? halfposition : dposition;
            tempd6 = Double.parseDouble( size.substring(0, index) );
            if ( (Math.floor(tempd6)) != tempd6 ) {
                if ( (tempd6 - 0.5d) == Math.floor(tempd6) ) {
                    onehalf = true;
                }
                else {
                    throw new BadDiceException(size);
                }
            }
            if ( halfposition != -1 ) {
                tempd6 += 0.5;
            }
            if ( plusposition != -1 ) {
                plus = Integer.parseInt( size.substring(plusposition+1)) ;
            }
            else if ( minposition != -1 ){
                plus = Integer.parseInt( size.substring(minposition)) ;
            }
        }
        catch (Exception exc) {
            throw new BadDiceException(size);
        }
     
        setD6(new Double(tempd6).intValue()); */
    }
    
    public Dice(int stun, int body) {
        setStun(stun);
        setBody(body);
    }
    
    static int roll(int d6) {
        Dice d;
        
        d = new Dice( d6);
        
        return d.getStun().intValue();
    }
    
    protected void realizeDice() {
        
        int i;
        int roll;
        
        stun = 0;
        body = 0;
        
        realized = true;
        rolled = true;
        
        int count = d6;
        int index = 0;
        if ( count > 0 ) {
            individualDice = new int[count];
            individualHalf = 0;
        }
        else {
            individualDice = null;
        }
        
        if ( d6 == 0 && onehalf == false && plus == 0 ) {
            stun = 0;
            body = 0;
            return;
        }
        
        for ( i = 0; i < d6; i++) {
            roll = Math.abs(die.nextInt()) % 6 + 1;
            stun+=roll;
            individualDice[index++] = roll;
            if ( roll == 6 ) body+=2;
            else if ( roll > 1 ) body+=1;
        }
        
        if ( onehalf ) {
            roll = Math.abs(die.nextInt()) % 6 + 1;
            individualHalf = roll;
            if ( roll <= 2 ) stun += 1;
            else if ( roll <=4 ) stun +=2;
            else stun +=3;
            
            if ( roll >= 4 ) body +=1;
        }
        
        stun += plus;
        
        if (stun <=0 ) stun = 1;
        
    }
    /** Getter for property stun.
     * @return Value of property stun.
     */
    public Integer getStun() {
        if (! realized ) realizeDice();
        return new Integer(stun);
    }
    
    public int getStun(int count) {
        int s = 0;
        int i;
        for (i=0;i<count&&i<d6;i++) {
            s += individualDice[i];
        }
        if ( i < count && onehalf ) {
            if ( individualHalf <= 2 ) s += 1;
            else if ( individualHalf <=4 ) s +=2;
            else s +=3;
        }
        return s;
    }
    /** Setter for property stun.
     * @param stun New value of property stun.
     */
    public void setStun(int stun) {
        realized = true;
        this.stun = stun;
    }
    /** Getter for property body.
     * @return Value of property body.
     */
    public Integer getBody() {
        if (! realized ) realizeDice();
        return new Integer(body);
    }
    
    public int getBody(int count) {
        int b = 0;
        int i;
        for (i=0;i<count&&i<d6;i++) {
            if ( individualDice[i] == 6 ) b+=2;
            else if ( individualDice[i] > 1 ) b+=1;
        }
        if ( i < count && onehalf ) {
            if ( individualHalf >= 4 ) b +=1;
        }
        return b;
    }
    
    public int getSpecificPipTotal(int pip) {
        int t = 0;
        int i;
        for (i=0;i<d6;i++) {
            if ( individualDice[i] == pip ) t+=1;
        }
        return t;
    }

    /** Setter for property body.
     * @param body New value of property body.
     */
    public void setBody(int body) {
        realized = true;
        this.body = body;
    }
    
    public String getValueString() {
        StringBuilder s = new StringBuilder();
        if ( rolled && (Boolean)Preferences.getPreferenceList().getParameterValue("FullDice" ) ) {
            int count = d6;
            int index;
            for ( index=0;index<count;index++ ) {
                s.append( Integer.toString( individualDice[index] ));
                if ( index < count - 1 || onehalf ) s.append(",");
            }
            if ( onehalf ) s.append( Integer.toString(individualHalf)+ "h" );

            s.append( "=" + getStun() + "(" + getBody() + ")");
            return s.toString();
        }
        else {
            return getStun() + "(" + getBody() + ")";
        }
    }
    
    public String getSizeString() {
        StringBuilder sb = new StringBuilder();
            if ( d6 >= 0 || onehalf ) {
                if ( d6 >= 0 ) {
                    sb.append(Integer.toString(d6));
                }
                
                if ( onehalf ) {
                    sb.append( "\u00BD");
                }
                
                sb.append( "d6" );
            }
            
            if ( plus > 0 ) {
                sb.append( "+" + Integer.toString(plus) );
            }
            else if ( plus < 0 ) {
                sb.append( Integer.toString(plus) );
            }
            
            return sb.toString();
    }
    
    public String toString() {
        if ( realized ) {
            return getValueString();
        } else {
            return getSizeString();
        }
    }
    
    public int getSizeInThirds() {
        return d6 * 3 + (onehalf?2:0) + plus;
    }
    
    public void setSizeInThrids(int thirds) {
        d6 = thirds / 3;
        if ( thirds - d6 * 3 == 2 ) onehalf = true;
        plus = thirds - getSizeInThirds();
    }
    
    static public boolean isValid(String size) {
        try {
            new Dice(size,false);
        }
        catch(BadDiceException bde) {
            return false;
        }
        return true;
    }
    
    public static String adjustDice(String size, int change) {
        Dice d;
        try {
            d = new Dice(size,false);
        }
        catch (BadDiceException bde) {
            return size;
        }
        
        d.d6 +=change;
        
        if ( d.d6 < 0 ) d.d6 = 0;
        
        return d.toString();
    }
    
    public boolean isRealized() {
        return realized;
    }
    /** Getter for property d6.
     * @return Value of property d6.
     */
    public int getD6() {
        return d6;
    }
    /** Setter for property d6.
     * @param d6 New value of property d6.
     */
    public void setD6(int d6) {
        realized = false;
        individualDice = new int[d6];
        for(int i=0;i<d6;i++) {
            individualDice[i]=0;
        }
        this.d6 = d6;
    }
    /** Getter for property plus.
     * @return Value of property plus.
     */
    public int getPlus() {
        return plus;
    }
    /** Setter for property plus.
     * @param plus New value of property plus.
     */
    public void setPlus(int plus) {
        realized = false;
        this.plus = plus;
    }
    /** Getter for property onehalf.
     * @return Value of property onehalf.
     */
    public boolean isOnehalf() {
        return onehalf;
    }
    /** Setter for property onehalf.
     * @param onehalf New value of property onehalf.
     */
    public void setOnehalf(boolean onehalf) {
        realized = false;
        this.onehalf = onehalf;
    }
    
    public void add( Dice d ) {
        this.d6 += d.d6;
        if ( this.onehalf && d.onehalf ) {
            this.d6 += 1;
        }
        else {
            this.onehalf = this.onehalf || d.onehalf;
        }
        
        this.plus += d.plus;
        
        if ( this.realized )
            realizeDice();
    }
    
    public int compare( Dice dice2 ) {
        double value1 = d6 + (( onehalf ) ? 0.5 : 0);
        double value2 = dice2.d6 + (( dice2.onehalf ) ? 0.5 : 0);
        
        if ( value1 > value2 ) return 1;
        if ( value2 > value1 ) return -1;
        
        // equal
        if ( plus > dice2.plus ) return 1;
        if ( plus < dice2.plus ) return -1;
        return 0;
    }
    
    public Object clone() {
        Dice d = new Dice();
        d.d6 = d6;
        d.onehalf = onehalf;
        d.plus = plus;
        
        if ( realized ) {
            d.stun = stun;
            d.body = body;
            d.realized = true;
        }
        
        return d;
    }
    
    public int getIndividualDie(int count) {
        if ( count <=0 || count > d6 ) {
            return 0;
        }
        else {
            return individualDice[count-1];
        }
    }
    
    public void setIndividualDie(int count, int value) {
        if ( count <=0 || count > d6 ) {
            return;
        }
        else {
            individualDice[count-1] = value;
            addIndividualDie();
        }
    }
    
    protected void addIndividualDie() {
        int s = 0;
        int b = 0;
        for (int i =0;i<d6;i++) {
            s += individualDice[i];
            if ( individualDice[i] == 6 ) b+=2;
            else if ( individualDice[i] > 1 ) b+=1;
        }
        if ( onehalf ) {
            if ( individualHalf <= 2 ) s += 1;
            else if ( individualHalf <=4 ) s +=2;
            else s +=3;
            if ( individualHalf >= 4 ) b +=1;
        }
        s += plus;
        
        setStun(s);
        setBody(b);
    }
    
    public boolean checkSize( String size ) {
        boolean result = false;
        
        try {
            Dice checkDie = new Dice(size, false);
            result = ( d6 == checkDie.getD6() && plus == checkDie.getPlus() );
        }
        catch ( BadDiceException bde ) {
            
        }
        
        return result;
    }
}