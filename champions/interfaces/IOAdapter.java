/*
 * IOInterface.java
 *
 * Created on March 29, 2004, 6:31 PM
 */

package champions.interfaces;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/** Plug-in Interface to perform IO operators for one or more classes.
 *
 * Each IOAdapter generally knows how to read and write one or more class types
 * in a specific format.
 *
 * A format is composed of a file extention, mime type, a class type, and a 
 * description string.  Each type should be associated with exactly 
 * on file extension and one mime type.  The file extension or mime type can be
 * null for a type, but one should always be set.  Classes and Descriptions can be 
 * duplicated with different types, if necessary.  If the file extension is not 
 * enough to determine the Class contained in that file, the most common ancestor
 * should be returned as the class.<P>
 *
 * All IOAdapters should be careful to support a default constructor.  The
 * PADRoster uses the default constructor to construct the IOAdapter for the
 * IOAdapter map.<P>
 *
 * @author  Trevor Walker
 */
public interface IOAdapter {
    /** Returns the IOAdapter identifier string.
     *
     */
    public String getIdentifierString();
    
    /** Attempts to read an object from the provided input stream.
     *
     * This method attempt to read an object to an input stream.  The method
     * should construct an object, if possible, from the stream.
     *
     * @exception IOException Thown if the Stream is not in the correct format.
     *
     * @return Object created from stream.
     */
    public Object readObject(File file, int format) throws IOException, FileNotFoundException;
    
    /** Attempts to write an object to the provided output stream.
     *
     * This method should write the object to the output stream. 
     *
     * @exception IOException thrown if the object fails to write for any reason.
     *
     */
    public void writeObject(OutputStream stream, Object object, int format) throws IOException;
    
    /** Returns the number of supported formats.

     */
    public int getReadFormatCount();
    
    /** Returns the file extensions of the indicated type. 
     *
     */
    public String getReadFileExtension(int formatIndex);
    
    /** Returns the mime type of the indicated type. 
     *
     */
    public String getReadMimeType(int formatIndex);
    
    /** Return the class of the indicated type.
     *
     */
    public Class getReadClass(int formatIndex);
    
    /** Returns the Description for the indicated type.
     *
     */
    public String getReadFormatDescription(int formatIndex);
    
    /** Looks up the file extention and returns the type index.
     */
    public int lookupReadFileExtension(String extension);
    
    /** Looks up the mine type and returns the type index.
     */
    public int lookupReadMimeType(String mimeType);
    
    /** Returns the number of supported formats.
     *
     */
    public int getWriteFormatCount();
    
    /** Returns the file extensions of the indicated type. 
     *
     */
    public String getWriteFileExtension(int formatIndex);
    
    /** Returns the mime type of the indicated type. 
     *
     */
    public String getWriteMimeType(int formatIndex);
    
    /** Return the class of the indicated type.
     *
     */
    public Class getWriteClass(int formatIndex);
    
    /** Returns the Description for the indicated type.
     *
     */
    public String getWriteFormatDescription(int formatIndex);
    
    /** Looks up the file extention and returns the type index.
     */
    public int lookupWriteFileExtension(String extension);
    
    /** Looks up the mine type and returns the type index.
     */
    public int lookupWriteMimeType(String mimeType);
}
