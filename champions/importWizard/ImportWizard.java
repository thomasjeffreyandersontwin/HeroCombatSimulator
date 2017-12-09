/*
 * ImportWizard.java
 *
 * Created on July 9, 2001, 11:50 PM
 */

package champions.importWizard;

import champions.*;
/**
 *
 * @author  twalker
 * @version 
 */
public class ImportWizard extends Wizard {

    /** Creates new ImportWizard */
    public ImportWizard() {
        super("Character Import Wizard");
        setNextPanel( new champions.importWizard.DestinationPanel());

        pack();
        setSize(500,400);
        
        advancePanel();
        setVisible(true);
    }

}
