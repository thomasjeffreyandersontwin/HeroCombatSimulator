/*
 * Detail.java
 *
 * Created on September 16, 2000, 1:11 PM
 */

package champions;

import java.util.*;
import java.lang.*;
/**
 *
 * @author  unknown
 * @version
 */
public class Detail extends Object
implements Comparable, Cloneable {

    /** Holds value of property name. */
    private String name;
    /** Holds value of property type. */
    private String type;
    /** Holds value of property value. */
    private Object value;
    /** Creates new Detail */
    public Detail(String name, String type, Object value) {
        setName(name);
        setType(type);
        setValue(value);
    }


    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return name;
    }
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    /** Getter for property type.
     * @return Value of property type.
     */
    public String getType() {
        return type;
    }
    /** Setter for property type.
     * @param type New value of property type.
     */
    public void setType(String type) {
        this.type = type;
    }
    /** Getter for property value.
     * @return Value of property value.
     */
    public Object getValue() {
        return value;
    }
    /** Setter for property value.
     * @param value New value of property value.
     */
    public void setValue(Object value) {
        this.value = value;
    }

    public String getKey() {
        return getName() + "." + getType();
    }

    public boolean isType(String type) {
        return this.type.equals(type);
    }

    public boolean isName(String name) {
        return this.name.equals(name);
    }

    public boolean isDetail(String name, String type) {
        return isName(name) && isType(type);
    }

    public boolean isDetail(Detail detail) {
        return isName(detail.getName()) && isType(detail.getType());
    }

    public String toString() {
        if ( value != null )
        return getName() + "." + getType() + "=" + getValue().toString();
        else
        return getName() + "." + getType() + "=" + "null";
    }

    /**
     * Indicates whether some other object is &quot;equal to&quot; this
     * Comparator.  This method must obey the general contract of
     * <tt>Object.equals(Object)</tt>.  Additionally, this method can return
     * <tt>true</tt> <i>only</i> if the specified Object is also a comparator
     * and it imposes the same ordering as this comparator.  Thus,
     * <code>comp1.equals(comp2)</code> implies that <tt>sgn(comp1.compare(o1,
     * o2))==sgn(comp2.compare(o1, o2))</tt> for every object reference
     * <tt>o1</tt> and <tt>o2</tt>.<p>
     *
     * Note that it is <i>always</i> safe <i>not</i> to override
     * <tt>Object.equals(Object)</tt>.  However, overriding this method may,
     * in some cases, improve performance by allowing programs to determine
     * that two distinct Comparators impose the same order.
     *
     * @param   obj   the reference object with which to compare.
     * @return  <code>true</code> only if the specified object is also
     * 		a comparator and it imposes the same ordering as this
     * 		comparator.
     * @see     java.lang.Object#equals(java.lang.Object)
     * @see java.lang.Object#hashCode()
     */
    public boolean equals(Object obj) {
        if ( obj.getClass() != Detail.class ) {
            return false;
        }
        return (this.getKey().equals( ((Detail)obj).getKey() ) 
        && this.getValue() == ((Detail)obj).getValue());

    }
    /** Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     *
     * The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)<p>
     *
     * The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.<p>
     *
     * Finally, the implementer must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.<p>
     *
     * It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this effect should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * @param   o the Object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     * 		is less than, equal to, or greater than the specified object.
     *
     * @throws ClassCastException if the specified object's type prevents it
     *        from being compared to this Object.
     */
    public int compareTo(Object o) {
        if ( o.getClass() != Detail.class) {
            throw new ClassCastException();
        }
        return this.getKey().compareTo( ((Detail)o).getKey());
    }
    
    static public Object castType(String type, Object data) {
        return data;
    }
    
    public Object clone () {
        return new Detail( getName(), getType(), getValue() );
    }
}