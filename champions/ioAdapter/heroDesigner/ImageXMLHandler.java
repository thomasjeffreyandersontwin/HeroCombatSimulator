/*
 * ImageXMLHandler.java
 *
 * Created on August 13, 2006, 5:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.ioAdapter.heroDesigner;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import xml.Base64Converter;
import xml.DefaultXMLHandler;
import xml.XMLParseErrorList;
import xml.XMLParser;

/**
 *
 * @author 1425
 */
public class ImageXMLHandler extends DefaultXMLHandler {
    
    /** Creates a new instance of ImageXMLHandler */
    public ImageXMLHandler(XMLParser parser) {
        super(parser);
    }
    
    public Object parseNode(Document doc, Node node, Object userData, XMLParseErrorList xmlParseErrorList) {
        
        HDInfo hdInfo = (HDInfo)userData;
        String base64 = node.getTextContent();
        
        try {
    
            byte[] data = Base64Converter.fromBase64(base64.toCharArray());
        
            BufferedImage image = ImageIO.read( new ByteArrayInputStream(data));
            
            if ( hdInfo.target != null ) {
                hdInfo.target.setImage(image);
            }
        }
        catch(IOException ioe) {
            
            
        }
        return null;
    }
}
