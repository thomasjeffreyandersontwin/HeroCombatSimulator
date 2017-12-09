/*
 * Profile.java
 *
 * Created on November 29, 2001, 7:17 PM
 */

package champions;

/**
 *
 * @author  twalker
 * @version 
 */
public class Profile extends DetailList {

    /** Holds value of property defaultProfile. */
    private boolean defaultProfile = false;
    
    private static final long serialVersionUID = 717433976316423019L;
    
    /** Creates new Profile */
    public Profile() {
        setFireChangeByDefault(false);
    }
    
    public Profile(String name) {
        this();
        setName(name);
    }
    
    public void setName(String name) {
        add("Profile.NAME", name, true);
    }
    
    public String getName() {
        return getStringValue("Profile.NAME");
    }

    public boolean getBooleanProfileOption(String option) {
        String value = getStringValue("ProfileOption." + option);
        return (value==null) ? false : (value.equals("TRUE"));
    }
    
    public boolean isProfileOptionSet(String option) {
        return contains("ProfileOption." + option);
    }
    
    public void setBooleanProfileOption(String option, boolean value) {
        add("ProfileOption." + option, value?"TRUE":"FALSE", true);
    }
    
    public void unsetBooleanProfileOption(String option) {
        remove("ProfileOption." + option);
    }
    
    
    
    /** Getter for property defaultProfile.
     * @return Value of property defaultProfile.
     */
    public boolean isDefaultProfile() {
        return this.defaultProfile;
    }    
    
    /** Setter for property defaultProfile.
     * @param defaultProfile New value of property defaultProfile.
     */
    public void setDefaultProfile(boolean defaultProfile) {
        this.defaultProfile = defaultProfile;
    }
    
    public boolean getProfileOptionIsSet(String option) {
        return contains("ProfileOption." + option);
    }
    
}
