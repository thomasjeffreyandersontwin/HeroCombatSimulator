/*
 * ExceptionWizard.java
 *
 * Created on July 7, 2001, 3:21 PM
 */

package champions.exceptionWizard;

import champions.Wizard;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author  twalker
 * @version 
 */
public class ExceptionWizard extends Wizard {

    protected  static ExceptionWizard currentExceptionWizard = null;
    protected static List<String> excludeList = new ArrayList<String>();
    
    /** Creates new ExceptionWizard */
    public ExceptionWizard(Throwable exception) {
        super("Exception Occurred");
        
        addException(exception);
        
        setNextPanel( new ViewExceptionPanel() );
        
        pack();
        setSize(500,400);
        
        advancePanel();
        
        setVisible(true);
    }
    
    public static void postException(Throwable exception) {
        if ( isExcluded(exception) == false ) {
            if ( currentExceptionWizard != null ) {
                currentExceptionWizard.addException(exception);
            }
            else {
                new ExceptionWizard(exception);
            }
        }
    }
    
    public void addException(Throwable exception) {
        parameters.createIndexed( "Exception","THROWABLE", exception, true);
    }
    
    public static boolean isExcluded(Throwable exception) {
        StackTraceElement[] elements = exception.getStackTrace();

        if ( elements != null ) {
            for(StackTraceElement element : elements) {
                String string = element.toString();

                for(String excludeString : excludeList) {
                    if ( string.contains(excludeString) ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void addExcludeString(String excludeString) {
        excludeList.add(excludeString);
    }
}
