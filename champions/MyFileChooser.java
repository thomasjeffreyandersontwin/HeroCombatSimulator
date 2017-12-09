/*
 * MyFileChooser.java
 *
 * Created on November 7, 2000, 11:44 PM
 */

package champions;


import javax.swing.JFileChooser;

import java.io.File;
/**
 *
 * @author  unknown
 * @version
 */
public class MyFileChooser extends JFileChooser{

    static public MyFileChooser chooser = new MyFileChooser();
    /** Creates new MyFileChooser */
    public MyFileChooser() {
        String directory = (String)Preferences.getPreferenceList().getParameterValue("DefaultDirectory");
        if ( directory != null ) {
            File file = new File ( directory );
            if ( file != null && file.exists() ) {
                setCurrentDirectory( file );
            }
        }
    }

}