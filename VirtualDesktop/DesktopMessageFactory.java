package VirtualDesktop;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.javafx.scene.paint.GradientUtils.Parser;

public class DesktopMessageFactory {
	public JSONParser _parser = new JSONParser();
	private FileReader reader;
	
	public JSONObject ParseMessage() throws Exception{
		JSONObject message;
	
		try {
			reader = new  FileReader("C:\\\\HeroVirtualTableTop\\\\Installed\\\\EventInfo\\\\AbilityActivatedFromDesktop.event");
			
			message = (JSONObject)_parser.parse(reader);
			reader.close();
			
			return message;
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		
	}

}
