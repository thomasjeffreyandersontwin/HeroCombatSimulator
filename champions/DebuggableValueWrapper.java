package champions;

import champions.interfaces.Debuggable;


public class DebuggableValueWrapper {
    Object object;
    
    public DebuggableValueWrapper(Object object) {
        this.object = object;
    }
    
    public String toString() {
        if (object == null) {
            return "null";
        }
        else            if (object instanceof Debuggable) {
                return ((Debuggable) object).toDebugString();
            }
            else {
                return object.toString();
            }
    }
}