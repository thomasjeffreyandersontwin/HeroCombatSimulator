/*
 * Chronometer.java
 *
 * Created on September 13, 2000, 10:01 PM
 */

package champions;

/**
 *
 * @author  unknown
 * @version
 */
public class Chronometer extends Object implements Cloneable, java.io.Serializable, Comparable<Chronometer> {
    
    private long time;
    private boolean turnend;
    
    static private boolean[][] speedChart = {
        {false, false, false, false, false, false, true , false, false, false, false, false},
        {false, false, false, false, false, true , false, false, false, false, false, true },
        {false, false, false, true , false, false, false, true , false, false, false, true },
        {false, false, true , false, false, true , false, false, true , false, false, true },
        {false, false, true , false, true , false, false, true , false, true , false, true },
        {false, true , false, true , false, true , false, true , false, true , false, true },
        {false, true , false, true , false, true , true , false, true , false, true , true },
        {false, true , true , false, true , true , false, true , true , false, true , true },
        {false, true , true , true , false, true , true , true , false, true , true , true },
        {false, true , true , true , true , true , false, true , true , true , true , true },
        {false, true , true , true , true , true , true , true , true , true , true , true },
        {true, true , true , true , true , true , true , true , true , true , true , true } };
    
    /** Creates new Chronometer */
    public Chronometer() {
        setTime(0);
    }
    
    public Chronometer(long time) {
        setTime(time);
    }
    
    public Chronometer(long days, long hours, long minutes, long turns, long segments) {
        setTime( days,  hours,  minutes,  turns,  segments);
    }
    
    public long getTime() {
        return time;
    }
    
    public void setTime(long t) {
        setTime(t, turnend);
    }
    
    public void setTime(long t, boolean turnend) {
        time = t;
        this.turnend = turnend;
    }
    
    public void setTime(long days, long hours, long minutes, long turns, long segments) {
        time = days * 86400 + hours * 3600 + minutes * 60 + turns * 12 + segments;
        turnend = false;
    }
    
    public int getSegment() {
       // if ( time == 0 )
      //      return 12;
      //  else
            return (int)(time - 1) % 12 + 1;
    }
    
    public long getTurn() {
       // if ( time == 0 )
       //     return 0;
      //  else
            return ( (time-1) / 12) % 5;
    }
    
    public long getAbsoluteTurn() {
      //  if ( time == 0 )
      //      return 0;
      //  else
            return ( (time-1) / 12);
    }
    
    public long getMinute() {
        return ( time - 1)/ 60 % 60;
    }
    
    public long getHour() {
        return (time - 1) / 3600 % 24;
    }
    
    public long getDay() {
        return (time - 1) / 84600;
        
    }
    
    public void incrementSegment(){
        if ( turnend == true ) {
            turnend = false;
            time++;
        } else if ( time == 0 || getSegment() == 12 ) {
            turnend = true;
        } else {
            time++;
        }
    }
    
    public void incrementSegment(long seconds){
        time += seconds;
    }
    
    public boolean isTurnEnd() {
        return turnend;
    }
    
    public void setTurnEnd(boolean t) {
        turnend = t;
    }
    
    public boolean isActivePhase(int spd) {
        if ( turnend == true ) return false;
        
        if ( spd <=0 || spd >= 13 ) return false;
        
        return speedChart[spd-1][(int)getSegment()-1];
    }
    
    public Chronometer clone() {
        Chronometer clone = new Chronometer();
        clone.setTime( this.getTime() );
        clone.setTurnEnd( this.isTurnEnd() );
        
        return clone;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        if ( time < 0 ) {
            sb.append("Illegal Time: ").append(time);
        } else {
            
            if ( getDay() > 0 ) {
                sb.append( "Day " );
                sb.append( Long.toString( getDay() ) );
                sb.append(", Hour ");
                sb.append(Long.toString( getHour() ) );
                sb.append(", Minute " );
                sb.append( Long.toString( getMinute() ) );
                sb.append( ", ");
            } else if ( getHour() > 0 ) {
                sb.append("Hour ");
                sb.append(Long.toString( getHour() ) );
                sb.append(", Minute ");
                sb.append(Long.toString( getMinute() ));
                sb.append(", ");
            } else if ( getMinute() > 0 ) {
                sb.append("Minute ");
                sb.append(Long.toString( getMinute() ));
                sb.append(", ");
            }
            
            sb.append("Turn ");
            sb.append(Long.toString( getTurn() ));
            sb.append(", Seg ");
            sb.append(Long.toString( getSegment() ));
            
            if ( isTurnEnd() ) {
                sb.append( "(POST 12 RECOVER)");
            }
        }
//        sb.append("[");
//        sb.append( time );
//        sb.append("s]");
        
        return sb.toString();
    }
    
    public void add(long seconds) {
        time += seconds;
    }
    
    public boolean equals(Object o) {
        if ( o==null || o.getClass() != Chronometer.class ) return false;
        Chronometer c = (Chronometer)o;
        return ( time == c.time && turnend == c.turnend );
    }
    
    public int compareTo(Chronometer that) {
        if ( that == null ) throw new NullPointerException();
        else if ( time < that.time ) return -1;
        else if ( time > that.time ) return 1;
        else if ( turnend == true && that.turnend == false ) return 1;
        else if ( turnend == false && that.turnend == true ) return -1;
        else return 0;
    }
    
    /** Returns a new Chronometer which is set the Prior Post12 segment.
     */
    public Chronometer getLeastTurnEndSegment(long secondIncrease) {
        Chronometer newChronometer = (Chronometer)this.clone();
        newChronometer.incrementSegment(secondIncrease);
        if ( newChronometer.isTurnEnd() == false ) {
            long newTime = newChronometer.getAbsoluteTurn() * 12;
            newChronometer.setTime( newTime, true );
        }
        return newChronometer;
    }
    
    /** Return is this is a legal time.
     *
     * The time is illegal if it has been adjusted negatively.
     */
    public boolean isLegal() {
        return time >= 0;
    }
    
    /** Finds the next segment that a target with indicated speed will be active.
     *
     * Return -1 if Target will never be eligible for an action, given the indicated speed.
     */
    static public int nextActiveSegment(int speed, Chronometer currentTime) {
        int segment = currentTime.getSegment();
        
        int i = segment + 1 ;
        if ( i > 12 ) i = 1;
        
        while ( i != segment ) {
            if ( isActiveSegment(speed, i) ) return i;
            i++;
            if ( i > 12 ) i = 1;
        }
        
        return -1;
    }
    
    static public boolean isActiveSegment(int spd, int segment) {
        if ( spd <=0 || spd >= 13 ) return false;
        
        return speedChart[spd-1][(int)segment-1];
    }
}