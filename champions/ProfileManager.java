/*
 * ProfileManager.java
 *
 * Created on November 29, 2001, 7:22 PM
 */

package champions;

import champions.exceptionWizard.ExceptionWizard;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.UIManager;


/**
 *
 * @author  twalker
 * @version
 */
public class ProfileManager {
    
    static protected Profile defaultProfile = null;
    
    static protected Map profiles = new TreeMap();
    
    static private boolean initialized = false;
    
    static public boolean getBooleanProfileOption(String profile, String option) {
        Profile p = getProfile(profile);
        return p.getBooleanProfileOption(option);
    }
    
    static public Profile getProfile(String profile) {
        Profile p = (Profile)profiles.get(profile);
        
        if ( p == null ) {
            p = getDefaultProfile();
        }
        
        return p;
    }
    
    static public Object[] getProfileList() {
        return profiles.keySet().toArray();
    }
    
    static public void addProfile(Profile profile) {
        // This just rejects duplicates
        if ( profiles.containsKey( profile.getName() ) == false ) {
            profiles.put(profile.getName(), profile);
        }
    }
    
    static public void removeProfile(Profile profile) {
        profiles.remove(profile.getName());
    }
    
    static public void setDefaultProfile(Profile profile) {
        if ( defaultProfile != profile ) {
        
            if ( defaultProfile != null ) {
                defaultProfile.setDefaultProfile(false);
            }
            defaultProfile = profile;
            if ( defaultProfile != null ) {
                defaultProfile.setDefaultProfile(true);
            }
        }
            
    }
    
    static public Profile getDefaultProfile() {
        if ( defaultProfile == null ) {
            Profile p = new Profile("Default Profile");
            addProfile(p);
            setDefaultProfile(p);
            
        }
        
        return defaultProfile;
    }
    
    static public void initProfileManager() {
        if ( initialized == false ) readProfiles();
        initialized = true;
    }
    
    static private void readProfiles() {
        String directoryName = UIManager.getString("Directory.ProfileDirectory");
        File profileDirectory = new File(directoryName);
        
        if ( profileDirectory.exists() && profileDirectory.isDirectory() ) {
            File[] files = profileDirectory.listFiles();
            for(int index = 0; index < files.length; index++) {
                File f = files[index];
                if ( f.getName().endsWith(".pfl") ) {
                    //  Read the Profile
                    try {
                        FileInputStream fis = new FileInputStream(f);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        Profile p  = (Profile) ois.readObject();
                        
                        if ( p != null ) {
                            addProfile(p);
                            if ( p.isDefaultProfile() ) {
                                setDefaultProfile(p);
                            }
                        }
                    } catch (FileNotFoundException fnfe) {
                    } catch (IOException ioe) {
                    } catch (ClassNotFoundException cnfe) {
                    }
                }
            }
        }
    }
    
    static public void saveProfiles() {
        String directoryName = UIManager.getString("Directory.ProfileDirectory");
        
        File profileDirectory = new File(directoryName);
        if ( profileDirectory.exists() == false ) {
                profileDirectory.mkdirs();
        }
        
        if ( profileDirectory.exists() ) {
            Object[] profiles = getProfileList();
            for(int index = 0; index < profiles.length; index ++) {
                try {
                    Profile p = getProfile((String)profiles[index]);
                    File f = new File(profileDirectory, p.getName() + ".pfl");
                    FileOutputStream fos = new FileOutputStream(f);
                    ObjectOutputStream out = new ObjectOutputStream(fos);
                    
                    out.writeObject(p);
                    out.flush();
                    out.close();
                }
                catch (FileNotFoundException fnfe) {
                    ExceptionWizard.postException(fnfe);
                }
                catch (IOException ioe) {
                    ExceptionWizard.postException(ioe);
                }
            }
            
        }
        
    }
}