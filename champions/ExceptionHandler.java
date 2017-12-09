/*
 * ExceptionHandler.java
 *
 * Created on July 7, 2001, 3:28 PM
 */

package champions;

import champions.exceptionWizard.*;
import javax.swing.*;
/**
 *
 * @author  twalker
 * @version 
 */
public class ExceptionHandler extends Object {

       public void handle(final Throwable thrown) {
           System.out.println("An Exception occured:");
           thrown.printStackTrace();
           
           if ( (Boolean)Preferences.getPreferenceList().getParameterValue("ShowExceptions") ) {
               SwingUtilities.invokeLater( new Runnable() {
                   public void run() {
                      // new champions.exceptionWizard.ExceptionWizard(thrown) ;
                       ExceptionWizard.postException(thrown);
                   }
               });
           }
       }
}
