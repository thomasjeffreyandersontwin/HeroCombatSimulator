/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tjava;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

/**
 *
 * @author twalker
 */
public class DestroyableReferenceQueue implements Runnable {

    private static ReferenceQueue<Destroyable> queue = null;

    private static Thread thread = null;

    public static ReferenceQueue<Destroyable> getReferenceQueue() {
        initialize();

        return queue;
    }

    private static void initialize() {
        if ( thread == null ) {
            thread = new Thread(new DestroyableReferenceQueue());
            thread.setDaemon(true);
            thread.start();

            queue = new ReferenceQueue<Destroyable>();
        }
    }

    private DestroyableReferenceQueue() {
    }

    public void run() {
        while(true) {
            try {
                Reference<? extends Destroyable> ref = queue.remove();
                Destroyable d = ref.get();
                if (d != null) {
                    d.destroy();
                }
            } catch (InterruptedException ex) {

            }
        }
    }
}
