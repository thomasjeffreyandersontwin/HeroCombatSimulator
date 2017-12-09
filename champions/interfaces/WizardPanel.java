/*
 * WizardPanel.java
 *
 * Created on July 6, 2001, 6:17 PM
 */

package champions.interfaces;

import champions.*;
import javax.swing.*;
import champions.exception.*;
/**
 *
 * @author  twalker
 * @version 
 */
public interface WizardPanel {
    public JPanel getPanel();
    public void setWizard(Wizard wizard);
    public void enterPanel() throws WizardException;
    public void exitPanel();
    public void abortPanel();
    public void destroy();
}

