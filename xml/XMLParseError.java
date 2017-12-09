/*
 * XMLParseError.java
 *
 * Created on September 2, 2005, 3:57 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package xml;

/**
 *
 * @author 1425
 */
public interface XMLParseError {
    
    /** Returns a text description of the Error.
     *
     */
    public String getErrorDescription();
    
    /** Returns the severity of the error.
     *
     * The severity of the error is an integer indicated the severity of the error,
     * with zero being a simple warning and higher number being more severe.  Subclasses
     * are welcome to define exactly what each error level means.
     */
    public int getErrorSeverity();
}
