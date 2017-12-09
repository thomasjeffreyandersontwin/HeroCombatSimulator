/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tjava;

import java.util.Timer;

/** A Timer that is shared by multiple classes.
 *
 * This is just a shared timer instance used in stead of
 * creating multiple timer threads.
 *
 * @author twalker
 */
public class SharedTimer {

    private static Timer sharedDeamonTimer = null;
    private static Timer sharedNonDeamonTimer = null;


    private SharedTimer() {
    }

    public static Timer getSharedTimer(boolean deamon) {
        return deamon ? getSharedDeamonTimer() : getSharedNonDeamonTimer();
    }
    
    /** Returns a shared timer that will not prolong the termination of the application.
     *
     * @return the sharedDeamonTimer
     */
    public static Timer getSharedDeamonTimer() {
       
            if ( sharedDeamonTimer == null ) {
                sharedDeamonTimer = new Timer("Shared Deamon Timer", true);
        }

        return sharedDeamonTimer;
    }

    /** Returns a shared timer that will prolong the termination of the application.
     *
     * @return the sharedNonDeamonTimer
     */
    public static Timer getSharedNonDeamonTimer() {
        if ( sharedNonDeamonTimer == null ) {
                sharedNonDeamonTimer = new Timer("Shared Non-Deamon Timer", false);
        }

        return sharedNonDeamonTimer;
    }

//    public static long triggerDate = 0;
//    public static void main(String[] args) {
//        Runtime.getRuntime().addShutdownHook( new MyShutdownHook());
//
//        System.out.println(String.format("%TT : Start main().", System.currentTimeMillis()));
//
//        Runnable r = new Runnable() {
//
//            @Override
//            public void run() {
//                long runDate = System.currentTimeMillis();
//
//                double secondsSinceTrigger = (runDate - triggerDate) / 1000.0;
//
//                System.out.println(String.format("%TT : Executed run().  Time since trigger = %.2fs.", runDate, secondsSinceTrigger));
//            }
//        };
//
//        DeferredActionTimer t = new DeferredActionTimer("DeferredAction", r, 1500, true);
//        t.setDeamon(true);
//
//        triggerIt(t);
//
//        System.out.println(String.format("%TT : End main().", System.currentTimeMillis()));
//    }
//
//    private static void triggerIt(DeferredActionTimer t) {
//        triggerDate = System.currentTimeMillis();
//        System.out.println(String.format("%TT : Triggering action.", triggerDate));
//        t.triggerAction();
//    }
//
//    private static class MyShutdownHook extends Thread {
//
//        @Override
//        public void run() {
//            System.out.println(String.format("%TT : JVM Shutdown hook called.", System.currentTimeMillis()));
//        }
//
//    }

}
