/*
 * ColumnLayoutHeader.java
 *
 * Created on October 22, 2000, 7:33 PM
 */

package champions.interfaces;

import champions.interfaces.Columned;
import champions.*;
/**
 *
 * @author  unknown
 * @version 
 */
public interface ColumnLayoutHeader extends Columned{
    public void setLayoutInfo(int columns, int columnWidth, int hgap);
}
