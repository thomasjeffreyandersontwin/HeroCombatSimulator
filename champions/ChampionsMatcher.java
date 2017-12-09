/*
 * PatternMatcher.java
 *
 * Created on June 22, 2001, 6:19 PM
 */

package champions;

import champions.exceptionWizard.ExceptionWizard;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 * @author  twalker
 * @version
 */
public class ChampionsMatcher extends java.lang.Object {
    
    private static HashMap patternCache = new HashMap();
    private static Matcher lastMatcher = null;
    
    /** Creates new PatternMatcher */
    public ChampionsMatcher() {
    }
    
    public static boolean contains(String pattern, String input) {
        Pattern compiledPattern = null;
        if ( patternCache.containsKey( pattern ) ) {
            compiledPattern = (Pattern)patternCache.get(pattern);
        }
        else {
            try {
                compiledPattern = Pattern.compile(pattern);
                patternCache.put(pattern, compiledPattern);
            }
            catch ( PatternSyntaxException mpe ) {
               ExceptionWizard.postException(mpe);
               return false;
            }
        }
        
        lastMatcher = compiledPattern.matcher( input );
        boolean result = lastMatcher.find();
        
        return result;
    }
    
    public static boolean matches(String pattern, String input) {
        if ( input == null ) input = "";
        Pattern compiledPattern = null;
        if ( patternCache.containsKey( pattern ) ) {
            compiledPattern = (Pattern)patternCache.get(pattern);
        }
        else {
            try {
                compiledPattern = Pattern.compile(pattern);
                patternCache.put(pattern, compiledPattern);
            }
            catch ( PatternSyntaxException mpe ) {
               ExceptionWizard.postException(mpe);
               return false;
            }
        }
        
        lastMatcher = compiledPattern.matcher( input );
        boolean result = lastMatcher.matches();
        
        return result;
    }
    
    public static String getMatchedGroup(int index) {
        if ( lastMatcher != null ) {
            if ( index >= 0 && index < lastMatcher.groupCount() + 1 ) {
                return lastMatcher.group(index);
            }
        }
        return null;
    }
    
    public static int getIntMatchedGroup(int index) {
        if ( lastMatcher != null ) {
            if ( index >= 0 && index < lastMatcher.groupCount() + 1 ) {
                String result = lastMatcher.group(index);
                if ( result != null ) {
                    if ( result.startsWith("+") ) result = result.substring(1);
                    
                    try {
                        return Integer.parseInt( result );
                    }
                    catch ( NumberFormatException nfe ) {
                    }
                }
            }
        }
        return 0;
    }
    
    public static String dumpLastMatch() { 
        if ( lastMatcher != null ){
            StringBuffer sb = new StringBuffer();
            sb.append("Last Matcher:\n");
            sb.append("Pattern: ");
            sb.append( lastMatcher.pattern().pattern() ) ;
            sb.append("\n");
           
            sb.append("Groups: " + lastMatcher.groupCount() +"\n");
            for(int i = 0; i < lastMatcher.groupCount()+1; i++) {
                sb.append(" group" + i + ": " + lastMatcher.group(i) + "\n");
            }
            return sb.toString();
        }
        else {
            return "No Last Match Available.";
        }
    }
}
