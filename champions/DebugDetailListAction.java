/*
 * DebugDetailListAction.java
 *
 * Created on July 1, 2001, 9:36 PM
 */

package champions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;


public class DebugDetailListAction extends AbstractAction {
    private DetailList detailList;
    public DebugDetailListAction(String name) {
        super(name);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (detailList != null ) {
            detailList.debugDetailList( "DetailList Debugger" );
        }
    }
    
    public boolean isEnabled() {
        return detailList != null;
    }
    
    public void setDetailList(DetailList detailList) {
        this.detailList = detailList;
    }
}
