/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champions;

import java.awt.Event;
import java.util.EventListener;

/**
 *
 * @author twalker
 */
public interface FrameworkConfigurationListener extends EventListener{
    public void configurationChanged(Event event);
}
