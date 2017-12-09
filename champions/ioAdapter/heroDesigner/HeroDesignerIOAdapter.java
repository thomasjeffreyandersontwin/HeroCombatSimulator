/*
 * HeroDesignerIOAdapter.java
 *
 * Created on March 29, 2004, 7:02 PM
 */

package champions.ioAdapter.heroDesigner;

import champions.Target;
import champions.interfaces.IOAdapter;
import champions.ioAdapter.heroDesigner.errorTree.ErrorTreeFrame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import xml.XMLParseError;
import xml.XMLParseErrorList;


/**
 *
 * @author  1425
 */
public class HeroDesignerIOAdapter implements IOAdapter {
    
    /** {{ FileExtension, MimeType, Class, Description }} */
    private static Object[][] supportedReadFormats = {
        {"hdc", null, Target.class, "Hero Designer Character File"}
    };
    
    private static Object[][] supportedWriteFormats = {
       // {".hdc", null, Target.class, "Hero Designer Character File"}
    };
    
    private static TargetXMLParser targetXMLParser;
    
    public String getIdentifierString() {
        return "HeroDesigner";
    }
    
    /** Creates a new instance of HeroDesignerIOAdapter */
    public HeroDesignerIOAdapter() {
    }
  
    public String getReadFileExtension(int formatIndex) {
        return (String)supportedReadFormats[formatIndex][0];
    }    
    
    public String getReadMimeType(int formatIndex) {
        return (String)supportedReadFormats[formatIndex][1];
    }
    
    public int getReadFormatCount() {
        return supportedReadFormats.length;
    }
    
    public Class getReadClass(int formatIndex) {
        return (Class)supportedReadFormats[formatIndex][2];
    }
    
    public String getReadFormatDescription(int formatIndex) {
        return (String)supportedReadFormats[formatIndex][3];
    }
    
    public int lookupReadFileExtension(String extension) {
        for(int i = 0; i < supportedReadFormats.length; i++) {
            String ext = getReadFileExtension(i);
            if ( ext != null && ext.equals(extension) ) return i;
        }
        return -1;
    }
    
    public int lookupReadMimeType(String mimeType) {
        for(int i = 0; i < supportedReadFormats.length; i++) {
            String type = getReadMimeType(i);
            if ( type != null && type.equals(mimeType) ) return i;
        }
        return -1;
    }
    
    public String getWriteFileExtension(int formatIndex) {
        return (String)supportedWriteFormats[formatIndex][0];
    }    
    
    public String getWriteMimeType(int formatIndex) {
        return (String)supportedWriteFormats[formatIndex][1];
    }
    
    public int getWriteFormatCount() {
        return supportedWriteFormats.length;
    }
    
    public Class getWriteClass(int formatIndex) {
        return (Class)supportedWriteFormats[formatIndex][2];
    }
    
    public String getWriteFormatDescription(int formatIndex) {
        return (String)supportedWriteFormats[formatIndex][3];
    }
    
    public int lookupWriteFileExtension(String extension) {
        for(int i = 0; i < supportedWriteFormats.length; i++) {
            String ext = getWriteFileExtension(i);
            if ( ext != null && ext.equals(extension) ) return i;
        }
        return -1;
    }
    
    public int lookupWriteMimeType(String mimeType) {
        for(int i = 0; i < supportedWriteFormats.length; i++) {
            String type = getWriteMimeType(i);
            if ( type != null && type.equals(mimeType) ) return i;
        }
        return -1;
    }
    
    public Object readObject(File file, int format) throws IOException, FileNotFoundException {

        InputStream stream = new FileInputStream(file);
        
        if ( format == 0 ) {
            if ( targetXMLParser == null ) targetXMLParser = new TargetXMLParser();

            xml.XMLParseErrorList errorList = new XMLParseErrorList("Character Import Errors");
            Object o = targetXMLParser.parse(stream, errorList);
            
            
            
            if ( errorList.getXMLParseErrorCount() != 0 ) {
                String fileName = file.getName();
                ErrorTreeFrame.displayImportError((Target)o, errorList, fileName);
                printXMLParseErrors(errorList, 0);
            }
            
            return o;
        }
            
        throw new IOException("Unsupported format");
    }
    
    protected void printXMLParseErrors(XMLParseError errors, int depth) {
        for(int i = 0; i < depth; i++ ) {
            System.out.print(" ");
        }
        System.out.println(errors.getErrorDescription());
        
        if ( errors instanceof XMLParseErrorList ) {
            for(int i = 0; i < ((XMLParseErrorList)errors).getXMLParseErrorCount(); i++) {
                printXMLParseErrors(((XMLParseErrorList)errors).getXMLParseError(i), depth + 1);
            }
        }
        else {
            
        }
    }
    
    public void writeObject(OutputStream stream, Object object, int format) throws IOException {
        throw new IOException("Unsupported format");
    }
    
    
    
}
