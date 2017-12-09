/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tjava;


import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 *
 * @author twalker
 */
public class Utilities {

    private Utilities() {
    }

    /** Creates an instance of the class specified by className.
     *
     * This method simplifies the creation of new instances of a class given the class name,
     * taking care of all the exception handling and what-not.
     *
     * @param <T> Superclass of the new instance.
     * @param superclass Class representing the superclass of the new instance.
     * @param className Name of the class, fully qualified.
     * @return A new instance of className, or null if the class can't be created for any reason.
     * @throws RuntimeException Although most exceptions are caught by this method, runtime exceptions in the
     * actual constructor will be throw for your handling pleasure.
     */
    public static <T> T newInstance(Class<T> superclass, String className) {

        return newInstance(superclass, className, null, null);
    }

    /** Creates an instance of the class specified by className.
     *
     * This method simplifies the creation of new instances of a class given the class name,
     * taking care of all the exception handling and what-not.
     *
     * @param <T> Superclass of the new instance.
     * @param superclass Class representing the superclass of the new instance.
     * @param className Name of the class, either fully qualified or a class in defaultPackage.
     * @param defaultPackage If non-null, the package to prepend to the className if className isn't found.
     * @return A new instance of className, or null if the class can't be created for any reason.
     * @throws RuntimeException Although most exceptions are caught by this method, runtime exceptions in the
     * actual constructor will be throw for your handling pleasure.
     */
    public static <T> T newInstance(Class<T> superclass, String className, String defaultPackage) {

        return newInstance(superclass, className, defaultPackage, null);
    }

    /** Creates an instance of the class specified by className.
     *
     * This method simplifies the creation of new instances of a class given the class name,
     * taking care of all the exception handling and what-not.
     *
     * @param <T> Superclass of the new instance.
     * @param superclass Class representing the superclass of the new instance.
     * @param className Name of the class, fully qualified.
     * @param parameters Parameters to pass to the constructor, or none if null.
     * @return A new instance of className, or null if the class can't be created for any reason.
     * @throws RuntimeException Although most exceptions are caught by this method, runtime exceptions in the
     * actual constructor will be throw for your handling pleasure.
     */
    public static <T> T newInstance(Class<T> superclass, String className, Object[] parameters) {

        return newInstance(superclass, className, null, new Object[][]{parameters});
    }

    /** Creates an instance of the class specified by className.
     *
     * This method simplifies the creation of new instances of a class given the class name,
     * taking care of all the exception handling and what-not.
     *
     * @param <T> Superclass of the new instance.
     * @param superclass Class representing the superclass of the new instance.
     * @param className Name of the class, either fully qualified or a class in defaultPackage.
     * @param defaultPackage If non-null, the package to prepend to the className if className isn't found.
     * @param parametersArray Array of possible parameters to pass to the constructor.  A compatible constructor
     * will be searched for for each of the member arrays in parametersArray.  The first compatible 
     * constructor found will be used. If null, the default constructor will be used, if it exists.
     * @return A new instance of className, or null if the class can't be created for any reason.
     * @throws RuntimeException Although most exceptions are caught by this method, runtime exceptions in the
     * actual constructor will be throw for your handling pleasure.
     */
    public static <T> T newInstance(Class<T> superclass, String className, String defaultPackage, Object[][] parametersArray) throws RuntimeException {

        T instance = null;
        Constructor constructor = null;

        Class c = getClass(className, defaultPackage);

        if (c == null) {
            ClassNotFoundException e;
            if (defaultPackage == null) {
                e = new ClassNotFoundException(className);
            }
            else {
                e = new ClassNotFoundException(className + " or " + defaultPackage + "." + className);
            }
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        else if (superclass.isAssignableFrom(c) == false) {
            ClassCastException e = new ClassCastException(c.getName() + " can not be cast to a " + superclass.getName());
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }

        if (parametersArray == null) {

            constructor = getConstructor(c, null);

            if (constructor == null) {
                NoSuchMethodException e = new NoSuchMethodException("Default constructor not found for " + c.getName());
                Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, e);
                return null;
            }

            try {
                instance = (T) constructor.newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                throw (RuntimeException) ex.getCause();
            }
        }
        else {
            Object[] parameters = null;
            for (int i = 0; i < parametersArray.length; i++) {
                parameters = parametersArray[i];
                constructor = getConstructor(c, parameters);
                if (constructor != null) {
                    break;
                }
            }

            if (constructor == null) {
                NoSuchMethodException e = new NoSuchMethodException("Compatible constructor not found for " + c.getName() + " and specified parameters");
                Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, e);
                return null;
            }

            try {
                instance = (T) constructor.newInstance(parameters);
            } catch (InstantiationException ex) {
                Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                throw (RuntimeException) ex.getCause();
            }
        }

        return instance;
    }

    public static Class getClass(String className) {
        return getClass(className, null);
    }

    public static Class getClass(String className, String defaultPackage) {
        Class c = null;

        try {
            c = Class.forName(className);
        } catch (ClassNotFoundException ex) {
        }

        if (c == null && defaultPackage != null) {
            try {
                c = Class.forName(defaultPackage + "." + className);
            } catch (ClassNotFoundException ex) {
            }
        }

        return c;
    }

    /** Returns the constructor for aClass which takes the indicated parameters.
     *
     * This method just wraps the getConstructor call to handle all the exceptions.
     *
     * @param aClass Class to lookup constructor for.
     * @param parameters Parameters to lookup, null for the default constructor.
     * @return The constructor or null if one can't be found.
     */
    public static Constructor getConstructor(Class aClass, Object[] parameters) {

        Constructor[] cs = aClass.getConstructors();
        Constructor c = null;

        for (int i = 0; i < cs.length; i++) {
            Constructor constructor = cs[i];

            Class[] paramClasses = constructor.getParameterTypes();

            if ((parameters == null || parameters.length == 0) && paramClasses.length == 0) {
                c = constructor;
                break;
            }
            else if (parameters != null && parameters.length == paramClasses.length) {
                boolean parametersMatch = true;
                for (int j = 0; j < paramClasses.length; j++) {
                    Class formalParam = paramClasses[j];
                    Object actualParam = parameters[j];
                    Class primativeClass = getPrimitiveClass(actualParam);
                    if (actualParam != null && formalParam.isInstance(actualParam) == false && (primativeClass == null || formalParam.isAssignableFrom(primativeClass) == false)) {
                        parametersMatch = false;
                        break;
                    }
                }

                if (parametersMatch) {
                    c = constructor;
                    break;
                }
            }
        }
        return c;
    }

    public static Class getPrimitiveClass(Object anObject) {
        return anObject == null ? null : getPrimitiveClass(anObject.getClass());
    }

    public static Class getPrimitiveClass(Class aClass) {
        if (aClass == Integer.class) {
            return int.class;
        }
        else if (aClass == Double.class) {
            return double.class;
        }
        else if (aClass == Boolean.class) {
            return boolean.class;
        }
        else if (aClass == Byte.class) {
            return byte.class;
        }
        else if (aClass == Short.class) {
            return short.class;
        }
        else if (aClass == Long.class) {
            return long.class;
        }
        else if (aClass == Float.class) {
            return float.class;
        }
        else if (aClass == Character.class) {
            return char.class;
        }
        return null;
    }

    public static Rectangle2D inverseTransform(AffineTransform transform, Rectangle2D originalRectangle) {
        // Calculate the clip in world coordinates...
        Point2D upperLeftClip = new Point2D.Double(originalRectangle.getX(), originalRectangle.getY());
        Point2D lowerRightClip = new Point2D.Double(originalRectangle.getX() + originalRectangle.getWidth(), originalRectangle.getY() + originalRectangle.getHeight());

        Point2D upperLeftTransformedClip = new Point2D.Double();
        Point2D lowerRightTransformedClip = new Point2D.Double();
        try {
            transform.inverseTransform(upperLeftClip, upperLeftTransformedClip);
            transform.inverseTransform(lowerRightClip, lowerRightTransformedClip);
        } catch (NoninvertibleTransformException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }


        return new Rectangle2D.Double(upperLeftTransformedClip.getX(), upperLeftTransformedClip.getY(), lowerRightTransformedClip.getX() - upperLeftTransformedClip.getX(), lowerRightTransformedClip.getY() - upperLeftTransformedClip.getY());
    }

    public static Rectangle2D transform(AffineTransform transform, Rectangle2D originalRectangle) {
        // Calculate the clip in world coordinates...
        Point2D upperLeftClip = new Point2D.Double(originalRectangle.getX(), originalRectangle.getY());
        Point2D lowerRightClip = new Point2D.Double(originalRectangle.getX() + originalRectangle.getWidth(), originalRectangle.getY() + originalRectangle.getHeight());

        Point2D upperLeftTransformedClip = new Point2D.Double();
        Point2D lowerRightTransformedClip = new Point2D.Double();

        transform.transform(upperLeftClip, upperLeftTransformedClip);
        transform.transform(lowerRightClip, lowerRightTransformedClip);

        return new Rectangle2D.Double(upperLeftTransformedClip.getX(), upperLeftTransformedClip.getY(), lowerRightTransformedClip.getX() - upperLeftTransformedClip.getX(), lowerRightTransformedClip.getY() - upperLeftTransformedClip.getY());
    }

    public static Rectangle inverseTransform(AffineTransform transform, Rectangle originalRectangle) {
        // Calculate the clip in world coordinates...
        Point2D upperLeftClip = new Point2D.Double(originalRectangle.getX(), originalRectangle.getY());
        Point2D lowerRightClip = new Point2D.Double(originalRectangle.getX() + originalRectangle.getWidth(), originalRectangle.getY() + originalRectangle.getHeight());

        Point2D upperLeftTransformedClip = new Point2D.Double();
        Point2D lowerRightTransformedClip = new Point2D.Double();
        try {
            transform.inverseTransform(upperLeftClip, upperLeftTransformedClip);
            transform.inverseTransform(lowerRightClip, lowerRightTransformedClip);
        } catch (NoninvertibleTransformException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return new Rectangle((int) Math.round(upperLeftTransformedClip.getX()), (int) Math.round(upperLeftTransformedClip.getY()), (int) Math.ceil(lowerRightTransformedClip.getX() - upperLeftTransformedClip.getX()), (int) Math.ceil(lowerRightTransformedClip.getY() - upperLeftTransformedClip.getY()));
    }

    public static Rectangle transform(AffineTransform transform, Rectangle originalRectangle) {
        // Calculate the clip in world coordinates...
        Point2D upperLeftClip = new Point2D.Double(originalRectangle.getX(), originalRectangle.getY());
        Point2D lowerRightClip = new Point2D.Double(originalRectangle.getX() + originalRectangle.getWidth(), originalRectangle.getY() + originalRectangle.getHeight());

        Point2D upperLeftTransformedClip = new Point2D.Double();
        Point2D lowerRightTransformedClip = new Point2D.Double();

        transform.transform(upperLeftClip, upperLeftTransformedClip);
        transform.transform(lowerRightClip, lowerRightTransformedClip);

        return new Rectangle((int) Math.round(upperLeftTransformedClip.getX()), (int) Math.round(upperLeftTransformedClip.getY()), (int) Math.ceil(lowerRightTransformedClip.getX() - upperLeftTransformedClip.getX()), (int) Math.ceil(lowerRightTransformedClip.getY() - upperLeftTransformedClip.getY()));
    }

    public static String getEscapedString(String unescapedString, char[] charactersToEscape, char escapeCharacter) {
        String escapedString = unescapedString;

        if (unescapedString != null) {

            escapedString = escapedString.replace(String.valueOf(escapeCharacter), String.valueOf(escapeCharacter) + String.valueOf(escapeCharacter));

            for (char c : charactersToEscape) {
                if (c != escapeCharacter) {
                    String replaceString = String.valueOf(escapeCharacter) + String.valueOf(c);

                    if (c == '\n') {
                        replaceString = String.valueOf(escapeCharacter) + "n";
                    }

                    escapedString = escapedString.replace(String.valueOf(c), replaceString);
                }
            }

        }
        return escapedString;
    }

    public static String getUnescapedString(String escapedString, char[] charactersToEscape, char escapeCharacter) {

        String unescapedString = escapedString;

        if (escapedString != null) {

            String ecEscaped = getRegexEscapedString(escapeCharacter);

            for (char c : charactersToEscape) {
                if (escapeCharacter != c) {
                    String cEscaped = getRegexEscapedString(c);

                    if (c == '\n') {
                        cEscaped = "n";
                    }

                    String regex = String.format("(?<!%1$s)((?:%1$s%1$s)*)%1$s%2$s", ecEscaped, cEscaped);
                    String replaceString = "$1" + Matcher.quoteReplacement(String.valueOf(c));

                    unescapedString = unescapedString.replaceAll(regex, replaceString);
                }
            }

            // Remove the escape character quoting...
            String regex = String.format("%1$s%1$s", ecEscaped);
            String replaceString = Matcher.quoteReplacement(String.valueOf(escapeCharacter));

            unescapedString = unescapedString.replaceAll(regex, replaceString);

        }
        return unescapedString;
    }

    /** Returns a String that has been escaped to have no mean within a regular expression.
     *
     *
     * @param character
     * @return
     */
    public static String getRegexEscapedString(char character) {
        String cEscape = "";

        switch (character) {
            case '\\':
            case '.':
            case '[':
            case '^':
            case '$':
            case '?':
            case '*':
            case '+':
            case '|':
            case '(':
            case ')':
                cEscape = "\\" + character;
                break;
            default:
                cEscape = String.valueOf(character);
        }

        return cEscape;
    }

    public static Method getMethod(Class aClass, String name, Object... parameters) {

        Method[] methods = aClass.getMethods();
        Method m = null;

        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];

            if (method.getName().equals(name)) {
                Class[] paramClasses = method.getParameterTypes();

                if ((parameters == null || parameters.length == 0) && paramClasses.length == 0) {
                    m = method;
                    break;
                }
                else if (parameters != null && parameters.length == paramClasses.length) {
                    boolean parametersMatch = true;
                    for (int j = 0; j < paramClasses.length; j++) {
                        Class formalParam = paramClasses[j];
                        Object actualParam = parameters[j];
                        Class primativeClass = getPrimitiveClass(actualParam);
                        if (actualParam != null && formalParam.isInstance(actualParam) == false && (primativeClass == null || formalParam.isAssignableFrom(primativeClass) == false)) {
                            parametersMatch = false;
                            break;
                        }
                    }

                    if (parametersMatch) {
                        m = method;
                        break;
                    }
                }
            }
        }
        return m;
    }

    public static double[] toPrimative(Double[] array) {
        return toPrimativeDoubleArray(array);
    }

    /** Returns a double[] with the values from <code>array</code>.
     *
     * The <code>array</code> argument must be an array and must contain only
     * Number objects.
     *
     * @param array Array containing only Number objects.
     * @return A double[] with the double values of the objects in <code>array</code>.
     */
    public static double[] toPrimativeDoubleArray(Object array) {

        if (array.getClass().isArray() == false) {
            throw new IllegalArgumentException("toPrimativeDoubleArray(Object) requires an array object but got " + array.getClass() + ".");
        }

        int length = Array.getLength(array);

        double[] newArray = new double[length];

        for (int i = 0; i < length; i++) {
            newArray[i] = ((Number) Array.get(array, i)).doubleValue();
        }

        return newArray;
    }



    /** Returns a double[] with the values from <code>array</code>.
     *
     * The <code>array</code> argument must be an array and must contain only
     * Number objects.
     *
     * @param array Array containing only Number objects.
     * @return A double[] with the double values of the objects in <code>array</code>.
     */
    public static Double[] toDoubleArray(double[] array) {
        int length = Array.getLength(array);

        Double[] newArray = new Double[length];

        for (int i = 0; i < length; i++) {
            newArray[i] = Double.valueOf(array[i]);
        }

        return newArray;
    }

    public static <E> E createClone(E object) throws CloneNotSupportedException {
        try {
            Method m = object.getClass().getMethod("clone");
            if (m != null) {
                return (E) m.invoke(object);
            }
        } catch (NoSuchMethodException ex) {
            throw new CloneNotSupportedException(ex.toString());
        } catch (SecurityException ex) {
            throw new CloneNotSupportedException(ex.toString());
        } catch (IllegalAccessException ex) {
            throw new CloneNotSupportedException(ex.toString());
        } catch (IllegalArgumentException ex) {
            throw new CloneNotSupportedException(ex.toString());
        } catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) ex.getTargetException();
            }
            else {
                throw new CloneNotSupportedException(ex.toString());
            }
        }

        throw new CloneNotSupportedException();
    }

    public static String toCommaSeparateList(Iterable i, String prefix, String suffix) {

        StringBuilder sb = new StringBuilder();
        sb.append(prefix);

        int index = 0;
        for (Object object : i) {
            if (index++ > 0) {
                sb.append(", ");
            }
            sb.append(object);
        }
        sb.append(suffix);
        return sb.toString();

    }
   
}
