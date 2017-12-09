/*
 * BaseGenericModifier.java
 *
 * Created on November 9, 2006, 7:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.genericModifiers;

/**
 *
 * @author 1425
 */
public class BaseGenericModifier extends SetGenericModifier {
    
    /** Creates a new instance of BaseGenericModifier */
    public BaseGenericModifier(int value) {
        super("Base", value);
        setEditable(false);
        setActive(true);
    }
    
}
