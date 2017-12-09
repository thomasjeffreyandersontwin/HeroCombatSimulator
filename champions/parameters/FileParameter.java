/*
 * IntegerParameter.java
 *
 * Created on September 24, 2007, 8:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.parameters;

/**
 *
 * @author twalker
 */
public class FileParameter extends Parameter<String> {
    static final long serialVersionUID = 5123833223342707402L;
    /** Indicates whether this is a file or a directory. */
    private boolean file;
    
    /** Creates a new instance of IntegerParameter */
    public FileParameter(String name, String key, String desc, boolean visible, boolean enabled, boolean required, Object defaultValue) {
        super(name,key,desc,visible,enabled,required,defaultValue);
    }
    
        public void setIncrement(Double increment) {
        this.setIncrement(increment);
    }
    
    public void setOption(String option, Object value) {
        if ( option.equals("FILE") ) {
            if ( value instanceof String ) {
                setFile( "TRUE".equalsIgnoreCase((String)value) );
            }
            else {
                setFile( (Boolean) value);
            }
        }
        else {
            super.setOption(option, value);
        }
    }

    public boolean isFile() {
        return file;
    }

    public void setFile(boolean file) {
        this.file = file;
    }

}
