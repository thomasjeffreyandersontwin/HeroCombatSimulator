/*
 * CombatSimulator.java
 *thisis is a test
 * Created on October 31, 2000, 3:06 PM
 */
package champions;

import champions.attackTree.AttackTreeDockingPanel;
import champions.attackTree.SelectTargetPanel;
import champions.exceptionWizard.ExceptionWizard;
import champions.filters.SublistAbilityFilter;
import dockable.DockingFrame;
import dockable.DockingPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

import VirtualDesktop.FileMessageListenerThread;

/** Main Class of the Hero Combat Simulator.
 * Only one CombatSimulator object may be created per application.
 *
 * @author Trevor Walker
 * @version see version string
 */
public class CombatSimulator {
    /* Contain the overall version of HCS that is displayed by
     * splash screen, used by auto-update, and displayed in the
     * about dialog.
     */
    // Version is now located in the hcs.properties file.

    /** Debug level for the CombatSimulator Class. */
    static public final int DEBUG = 0;
    private static Properties properties;

    /**
     * @return the version
     */
    public static String getVersion() {
        if (properties == null) {
            properties = new Properties();
            try {
                properties.load(CombatSimulator.class.getResourceAsStream("/hcs.properties"));
            } catch (IOException ex) {
                Logger.getLogger(CombatSimulator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return properties.getProperty("application.version");
    }
    private Battle battle;
    private DockingPanel idp,  dledp,  mdp;
    public static DockingPanel controlPanel;
    private static AttackTreeDockingPanel attackTreeDockingPanel;
    private static RosterDockingPanel rosterDockingPanel;
    private static DockingPanel activeTargetDockingPanel;
    private static DockingPanel onDeckDockingPanel;
    private static DockingPanel eligibleDockingPanel;
    private static DockingPanel activeTargetAbilitiesDockingPanel;
    private static DockingPanel defaultAbilitiesDockingPanel;
    private static DockingPanel chronometerDockingPanel;
    static private Object[][] moduleList = {
        {"Initializing", new Integer(1)},
        {"Initializing Preferences", new Integer(1)},
        {"Initializing Battle", new Integer(3)},
        {"Initializing Windows", new Integer(6)},
        {"Initializing Skin", new Integer(1)},
        {"Initializing Powers", new Integer(60)},
        {"Initializing Templates", new Integer(20)},
        {"Initializing Advantages", new Integer(25)},
        {"Initializing Limitations", new Integer(25)},
        {"Initializing Special Parameters", new Integer(10)},
        {"Initializing SFX", new Integer(15)},
        {"Initializing Combat Profiles", new Integer(1)},
        {"Initializing InLine Panels", new Integer(2)},};
    private boolean beta = false;

    /** Creates new CombatSimulator */
    public CombatSimulator() {
        long startTime;

        if (DEBUG == 1) {
            startTime = System.currentTimeMillis();
        }

        if (getVersion().indexOf("Beta") != -1) {
            beta = true;
        }

        initSkin();

        // Show splash
        SplashScreen ss = new SplashScreen(moduleList);
        ss.setVersion(" v" + getVersion());
        ss.showSplash();

        SplashScreen.setModule("Initializing");

        // Touch Preferences to initialize
        SplashScreen.setModule("Initializing Preferences");
        Preferences.getPreferenceList();

        SplashScreen.setModule("Initializing Battle");

        Roster r = null;
        if (beta == false) {
            battle = new Battle();

            try {
                String rosterName = (String) Preferences.getPreferenceList().getParameterValue("AutoLoadRoster");
                if (rosterName != null && !rosterName.isEmpty()) {
                    File f = new File(rosterName);
                    if (f.exists() && f.canRead()) {
                        r = Roster.open(f);
                    }

                    String defaultDir = (String) Preferences.getPreferenceList().getParameterValue("DefaultDirectory");
                    if (defaultDir != null && !defaultDir.isEmpty()) {
                        f = new File(defaultDir, rosterName);
                        if (f.exists() && f.canRead()) {
                            try {
                                r = Roster.open(f);
                            } catch (FileNotFoundException ex) {
                                ex.printStackTrace();
                            } catch (ClassNotFoundException ex) {
                                ex.printStackTrace();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            } catch (Exception exc) {
                //ExceptionWizard.postException(exc);
                // System.out.println( exc.toString() );.
                r = new Roster();
            }

            if (r == null) {
                r = new Roster();
            }

            battle.addRoster(r);
        } else {
            try {
                // Attempt to load the defaults...
                URL defaultPrefs = getClass().getResource("/champions/templates/default.btl");
                ObjectInputStream ois = new ObjectInputStream(defaultPrefs.openStream());
                battle = (Battle) ois.readObject();
            } catch (IOException ioe) {
            } catch (ClassNotFoundException cnfe) {
            }


        }
        if (battle == null) {
            battle = new Battle();
        }

        // Setup the Powers, Advantages, Limitations, etc...
        PADRoster.setup();

        SplashScreen.setModule("Initializing Windows");
        DockingFrame df;
        HashSet hs = new HashSet();

        SplashScreen.setDescription("Initializing Windows (Messages)");
        mdp = new MessageDockingPanel("messageDP");
        df = mdp.getDockingFrame();
        hs.add(df);

        SplashScreen.advangeProgress();

        SplashScreen.setDescription("Initializing Windows (Rosters)");
        rosterDockingPanel = RosterDockingPanel.getDefaultRosterDockingPanel();
        df = getRosterDockingPanel().getDockingFrame();
        hs.add(df);

        // Setup Default roster
        if (r != null) {
            getRosterDockingPanel().addRoster(r);
        }

        SplashScreen.advangeProgress();

        SplashScreen.setDescription("Initializing Windows (Attack Info)");
        attackTreeDockingPanel = new AttackTreeDockingPanel();
        df = getAttackTreeDockingPanel().getDockingFrame();
        hs.add(df);

        SplashScreen.advangeProgress();

        SplashScreen.setDescription("Initializing Windows (Active Character)");
        activeTargetDockingPanel = ActiveTargetPanel.createActiveTargetDockingPanel();
        df = getActiveTargetDockingPanel().getDockingFrame();
        hs.add(df);

        SplashScreen.advangeProgress();

        SplashScreen.setDescription("Initializing Windows (Battle Controls)");
        controlPanel = new ControlPanelDockingPanel();
        df = controlPanel.getDockingFrame();
        hs.add(df);

        SplashScreen.advangeProgress();

        SplashScreen.setDescription("Initializing Windows (On-Deck Characters)");
        onDeckDockingPanel = new OnDeckDockingPanel();
        df = getOnDeckDockingPanel().getDockingFrame();
        hs.add(df);

        SplashScreen.advangeProgress();

        SplashScreen.setDescription("Initializing Windows (Eligible Characters)");
        eligibleDockingPanel = new EligibleDockingPanel();
        df = getEligibleDockingPanel().getDockingFrame();
        hs.add(df);

        SplashScreen.advangeProgress();

        SplashScreen.setDescription("Initializing Windows (Abilities)");
        activeTargetAbilitiesDockingPanel = AbilityPanel.createAbilityDockingPanel();
        df = getActiveTargetAbilitiesDockingPanel().getDockingFrame();
        hs.add(df);

        SplashScreen.advangeProgress();
        defaultAbilitiesDockingPanel = AbilityPanel.createAbilityDockingPanel("Default Abilities", new SublistAbilityFilter("Default Abilities"), false);
        df = getDefaultAbilitiesDockingPanel().getDockingFrame();
        hs.add(df);

        SplashScreen.advangeProgress();

        SplashScreen.setDescription("Initializing Windows (Chronometer)");
        chronometerDockingPanel = ChronometerPanel.createChronometerDockingPanel();
        df = getChronometerDockingPanel().getDockingFrame();
        hs.add(df);

        SplashScreen.advangeProgress();

        // Initialize Combat Profiles
        SplashScreen.setModule("Initializing Combat Profiles");
        ProfileManager.initProfileManager();
        SplashScreen.advangeProgress();

        SplashScreen.setModule("Initializing Skin");
        SplashScreen.advangeProgress();
        initSkin();

        SplashScreen.setModule("Initialization Complete");
        ss.hideSplash();

        SplashScreen.disposeSplash();

        if (DEBUG == 1) {
            long totalTime = System.currentTimeMillis() - startTime;
            System.out.println("Startup Time: " + Long.toString(totalTime));
        }

        // Show all the windows after everything is done.
        Iterator i = hs.iterator();
        while (i.hasNext()) {
            df = (DockingFrame) i.next();
            df.setVisible(true);
        }
        FileMessageListenerThread run = new FileMessageListenerThread(battle);
        run.start();
        
        checkForUpdate();

        ExceptionWizard.addExcludeString("javax.swing.tree.VariableHeightLayoutCache.getRowContainingYLocation");
        ExceptionWizard.addExcludeString("javax.swing.tree.AbstractLayoutCache.getNodeDimensions");
        
        
    }

    /** Exits.  Dah! */
    public static void exit() {
        // Update the stats here...

        Preferences.savePreferenceList();
        DetailList.exitHook();
        ProfileManager.saveProfiles();

        System.exit(0);
    }

    /** gets the bounds for <CODE>what</CODE> from the user preferences.
     * If no bounds exist for <CODE>what</CODE>, returns sane, non-zero
     * default bounds.
     * @param what WindowID to get the bounds for.
     * @return Bounds of WindowID according to current preferences.
     * @deprecated Window positions are no longer controlled by the CombatSimulator
     * object.  All windows should use the SavedDockingPanel whenever
     * possible.
     * @see Dockable.SavedDockingPanel
     */
    public Rectangle getPreferenceBounds(String what) {
        Object o;
        Rectangle bounds;
        if ((o = Preferences.getPreferenceList().getWindowBounds(what)) != null) {
            bounds = (Rectangle) o;
        } else {
            bounds = new Rectangle(0, 0, 100, 100);
        }
        return bounds;
    }

    /** gets the location for <CODE>what</CODE> from the user preferences.
     * If no location exist for <CODE>what</CODE>, returns sane, non-zero
     * default location.
     * @param what WindowID to get the location for.
     * @return location of WindowID according to current preferences.
     * @deprecated Window positions are no longer controlled by the CombatSimulator
     * object.  All windows should use the SavedDockingPanel whenever
     * possible.
     * @see Dockable.SavedDockingPanel
     */
    public Point getPreferenceLocation(String what) {
        Object o;
        Point p;
        if ((o = Preferences.getPreferenceList().getWindowLocation(what)) != null) {
            p = (Point) o;
        } else {
            p = new Point(20, 20);
        }
        return p;
    }

    /** Notifies the CombatSimulator that the layout of it's windows has changed.
     *
     * The layoutChanges that occur will be saved in the preferences when
     * HCS exits.
     * @param e LayoutChange event for the DockingPanel class.
     */
    public void layoutChanged(ChangeEvent e) {
        DockingPanel dp = (DockingPanel) e.getSource();

        String window = null;
        if (dp == mdp) {
            window = "messageDP";
        } else if (dp == idp) {
            window = "infoDP";
        } else if (dp == dledp) {
            window = "dleDP";
        }

        if (window != null) {
            Double d = null;
            Point p = null;
            //System.out.println( "Setting Prefs for " + dp + " to " + dp.getLayoutBounds() );
            Preferences.getPreferenceList().setWindowBounds(window, dp.getLayoutBounds());
            if (dp.getDockingFrame() != null) {
                d = dp.getDockingFrame().getFrameID();
                p = dp.getDockingFrame().getLocation();
                Preferences.getPreferenceList().setWindowID(window, d);
                Preferences.getPreferenceList().setWindowLocation(window, p);
            }
        }
    }

    /** Checks for HCS updates from the HCS main server.
     *
     * This method will check for a new HCS version and download it if the
     * user requests it.  It currently does not truely installs it, but
     * mearly leaves it in the HCS directory.
     */
    public void checkForUpdate() {
        if (beta == true) {
            return;
        }
        if ((Boolean) Preferences.getPreferenceList().getParameterValue("AutoUpdate")) {
            String[] sites = new String[]{"http://hcs.dhis.org/"};
            int i;
            for (i = 0; i < sites.length; i++) {
                try {
                    String newVersion = null;
                    URL url = new URL(sites[i] + "cgi-bin/getCurrentVersion");
                    if (url != null) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                        newVersion = in.readLine();

                        in.close();
                    }
                    if (newVersion != null) {
                        if (newVersion.equals(getVersion()) == false && versionToNumber(getVersion()) < versionToNumber(newVersion)) {
                            Frame frame = null;

                            if (idp != null) {
                                idp.getDockingFrame();
                            }

                            int result = JOptionPane.showConfirmDialog(frame, "Version " + newVersion + " is now available.  Would you like to download it now?", "AutoUpdate", JOptionPane.YES_NO_OPTION);
                            if (result == JOptionPane.YES_OPTION) {
                                URL downloadURL = new URL(sites[i] + "autoupdate/hcs" + newVersion + ".jar");

                                MyFileChooser chooser = MyFileChooser.chooser;

                                MyFileFilter mff = new MyFileFilter("jar");
                                chooser.setFileFilter(mff);

                                chooser.setDialogTitle("Save Location for hcs.jar, version " + newVersion);
                                chooser.setSelectedFile(new File("hcs.jar"));

                                int returnVal = chooser.showSaveDialog(frame);

                                if (returnVal == JFileChooser.APPROVE_OPTION) {
                                    try {
                                        URLConnection uc = downloadURL.openConnection();

                                        ProgressMonitor pm = new ProgressMonitor(frame, "Saving " + downloadURL, "", 0, uc.getContentLength());

                                        BufferedInputStream in = new BufferedInputStream(downloadURL.openStream());

                                        File file = chooser.getSelectedFile();
                                        FileOutputStream fos = new FileOutputStream(file);
                                        BufferedOutputStream osw = new BufferedOutputStream(fos);

                                        byte[] buffer = new byte[1024];
                                        int total = 0;
                                        int position;
                                        while ((position = in.read(buffer, 0, 1024)) != -1) {
                                            osw.write(buffer, 0, position);
                                            total += position;
                                            pm.setProgress(total);
                                        }

                                        pm.close();
                                        in.close();
                                        osw.close();

                                        JOptionPane.showMessageDialog(frame, "Download Completed Successful.  You will need to quit to use new version.", "Download Sucessful", JOptionPane.OK_OPTION);

                                        url = new URL(sites[i] + "cgi-bin/increaseDownloadStat");
                                        if (url != null) {
                                            BufferedReader increaseStat = new BufferedReader(new InputStreamReader(url.openStream()));

                                            increaseStat.close();
                                        }
                                    } catch (Exception exc) {
                                        JOptionPane.showMessageDialog(frame, "Download Failed: " + exc.toString(), "Download Failed", JOptionPane.OK_OPTION);

                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                } catch (Exception exc) {
                    System.out.println("Error in AutoUpdate: " + exc);
                }
            }
        }
    }

    /** Small utility method to convert a version string to a numberic representation.
     *
     * This method takes a string in the form of '1.9.2' and returns an
     * integer with the value equivalent to the integer if the periods where
     * removed.  For example 1.9.2 would return 192.  2.9.3.1 would return
     * 2931.  Generally, it is assumed that string versions have three number
     * corresponding to verion.release.subrelease.  Also, those digits should
     * be between 0 and 9.
     * @param v String to be parsed as a version.
     * @return Integer corresponding to version number.
     */
    public int versionToNumber(String v) {
        int total = 0;
        String stripped = v;
        String sub;
        if (v.indexOf("T") > 0) {
            stripped = v.substring(0, v.indexOf("T"));
        }

        while (stripped.equals("") == false) {
            if (stripped.indexOf(".") >= 0) {
                sub = stripped.substring(0, stripped.indexOf("."));
                stripped = stripped.substring(stripped.indexOf(".") + 1);
            } else {
                sub = stripped;
                stripped = "";
            }

            try {
                total = total * 100 + Integer.parseInt(sub);
            } catch (NumberFormatException nfe) {
                break;
            }
        }

        return total;
    }

    public static Frame getFrame() {
        if (controlPanel != null) {
            return controlPanel.getDockingFrame();
        } else {
            return null;
        }
    }
    static String basicClassName = "javax.swing.plaf.multi.MultiLookAndFeel";
    static String metalClassName = "javax.swing.plaf.metal.MetalLookAndFeel";
    static String motifClassName = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    static String windowsClassName = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    static String skinClassName = "com.l2fprod.gui.plaf.skin.SkinLookAndFeel";
    static String hcsskinClassName = "skin.SkinLookAndFeel";

    /** In charge of initializing all of the UIManager data for all UI components.
     *
     * This method should completely initialize the UIManager.  Eventually,
     * a consistent, extensible manner of doing alternate UIs is envisioned,
     * but since we are lazy that has never happened.
     */
    public void initSkin() {

        ToolTipManager ttm = ToolTipManager.sharedInstance();

        ttm.setInitialDelay(500);
        ttm.setDismissDelay(15000);

        UIManager.put("Directory.ProfileDirectory", "HCSData/Profiles/");
        UIManager.put("File.HCSINI", "HCSprefs.ini");

        UIManager.put("CombatSimulator.defaultFont", new Font("SansSerif", Font.PLAIN, 11));
        UIManager.put("CombatSimulator.boldFont", new Font("SansSerif", Font.BOLD, 11));

        UIManager.put("Checked.DefaultIcon", new ImageIcon(getClass().getResource("/graphics/greenCheckIcon.gif")));
        UIManager.put("X.DefaultIcon", new ImageIcon(getClass().getResource("/graphics/redXIcon.gif")));
        UIManager.put("Stat.DefaultIcon", new ImageIcon(getClass().getResource("/graphics/statIconWhiteBackground.gif")));

        UIManager.put("Save.DefaultIcon", new ImageIcon(getClass().getResource("/graphics/saveIcon.gif")));
        UIManager.put("Open.DefaultIcon", new ImageIcon(getClass().getResource("/graphics/openIcon.gif")));

        UIManager.put("Framework.openIcon", new ImageIcon(getClass().getResource("/graphics/frameworkFolderOpenIcon.gif")));
        UIManager.put("Framework.closedIcon", new ImageIcon(getClass().getResource("/graphics/frameworkFolderClosedIcon.gif")));

        UIManager.put("Framework.DefaultIcon", new ImageIcon(getClass().getResource("/graphics/framework.gif")));
        UIManager.put("Framework.RunningIcon", new ImageIcon(getClass().getResource("/graphics/framework_greenlight.gif")));
        UIManager.put("Framework.StoppedIcon", new ImageIcon(getClass().getResource("/graphics/framework_redlight.gif")));
        UIManager.put("Framework.DisabledIcon", new ImageIcon(getClass().getResource("/graphics/framework_greylight.gif")));
        UIManager.put("Power.DefaultIcon", new ImageIcon(getClass().getResource("/graphics/genericPower.gif")));
        UIManager.put("Advantage.DefaultIcon", new ImageIcon(getClass().getResource("/graphics/genericAdvantage.gif")));
        UIManager.put("Limitation.DefaultIcon", new ImageIcon(getClass().getResource("/graphics/genericLimitation.gif")));
        UIManager.put("SpecialParameter.DefaultIcon", new ImageIcon(getClass().getResource("/graphics/specialParametersIcon.gif")));
        UIManager.put("ImportLine.UsedIcon", new ImageIcon(getClass().getResource("/graphics/importLineUsed.gif")));
        UIManager.put("ImportLine.UnusedIcon", new ImageIcon(getClass().getResource("/graphics/importLineUnused.gif")));
        UIManager.put("SpecialEffect.DefaultIcon", new ImageIcon(getClass().getResource("/graphics/specialEffectsIcon.gif")));
        UIManager.put("SpecialEffect.WaterIcon", new ImageIcon(getClass().getResource("/graphics/specialEffectWaterIcon.gif")));
        UIManager.put("SpecialEffect.MagicIcon", new ImageIcon(getClass().getResource("/graphics/specialEffectMagicIcon.gif")));
        UIManager.put("SpecialEffect.Cold1Icon", new ImageIcon(getClass().getResource("/graphics/specialEffectCold1Icon.gif")));
        UIManager.put("SpecialEffect.ChiIcon", new ImageIcon(getClass().getResource("/graphics/specialEffectChiIcon.gif")));
        UIManager.put("SpecialEffect.Electrical1Icon", new ImageIcon(getClass().getResource("/graphics/specialEffectElectrical1Icon.gif")));
        UIManager.put("SpecialEffect.Luck1Icon", new ImageIcon(getClass().getResource("/graphics/specialEffectLuck1Icon.gif")));
        UIManager.put("SpecialEffect.MagnetismIcon", new ImageIcon(getClass().getResource("/graphics/specialEffectMagnetismIcon.gif")));
        UIManager.put("SpecialEffect.Air1Icon", new ImageIcon(getClass().getResource("/graphics/specialEffectAir1Icon.gif")));
        UIManager.put("SpecialEffect.Air2Icon", new ImageIcon(getClass().getResource("/graphics/specialEffectAir2Icon.gif")));
        UIManager.put("SpecialEffect.Cold2Icon", new ImageIcon(getClass().getResource("/graphics/specialEffectCold2Icon.gif")));
        UIManager.put("SpecialEffect.EarthIcon", new ImageIcon(getClass().getResource("/graphics/specialEffectEarthIcon.gif")));
        UIManager.put("SpecialEffect.Electrical2Icon", new ImageIcon(getClass().getResource("/graphics/specialEffectElectrical2Icon.gif")));
        UIManager.put("SpecialEffect.Luck2Icon", new ImageIcon(getClass().getResource("/graphics/specialEffectLuck2Icon.gif")));
        UIManager.put("SpecialEffect.Luck3Icon", new ImageIcon(getClass().getResource("/graphics/specialEffectLuck3Icon.gif")));
        UIManager.put("SpecialEffect.MutantIcon", new ImageIcon(getClass().getResource("/graphics/specialEffectMutantIcon.gif")));
        UIManager.put("SpecialEffect.PoisonIcon", new ImageIcon(getClass().getResource("/graphics/specialEffectPoisonIcon.gif")));
        UIManager.put("SpecialEffect.PsionicIcon", new ImageIcon(getClass().getResource("/graphics/specialEffectPsionicIcon.gif")));
        UIManager.put("SpecialEffect.SonicIcon", new ImageIcon(getClass().getResource("/graphics/specialEffectSonicIcon.gif")));
        UIManager.put("SpecialEffect.Tech1Icon", new ImageIcon(getClass().getResource("/graphics/specialEffectTech1Icon.gif")));
        UIManager.put("SpecialEffect.Tech2Icon", new ImageIcon(getClass().getResource("/graphics/specialEffectTech2Icon.gif")));
        UIManager.put("SpecialEffect.Tech3Icon", new ImageIcon(getClass().getResource("/graphics/specialEffectTech3Icon.gif")));
        UIManager.put("SpecialEffect.Tech4Icon", new ImageIcon(getClass().getResource("/graphics/specialEffectTech4Icon.gif")));
        UIManager.put("SpecialEffect.TemporalIcon", new ImageIcon(getClass().getResource("/graphics/specialEffectTemporalIcon.gif")));

        UIManager.put("BattleControls.undoIcon", new ImageIcon(getClass().getResource("/graphics/undoEventIcon.gif")));
        UIManager.put("BattleControls.redoIcon", new ImageIcon(getClass().getResource("/graphics/redoEventIcon.gif")));
        UIManager.put("BattleControls.advanceCharacterIcon", new ImageIcon(getClass().getResource("/graphics/advanceCharacterIcon.gif")));
        UIManager.put("BattleControls.advanceSingleSegmentIcon", new ImageIcon(getClass().getResource("/graphics/advanceSingleSegmentIcon.gif")));
        UIManager.put("BattleControls.forceAdvanceIcon", new ImageIcon(getClass().getResource("/graphics/forceSegmentAdvanceIcon.gif")));
        UIManager.put("BattleControls.configureBattleIcon", new ImageIcon(getClass().getResource("/graphics/configureBattleIcon.png")));

        UIManager.put("Tree.openIcon", new ImageIcon(getClass().getResource("/graphics/folderOpenIcon.gif")));
        UIManager.put("Tree.closedIcon", new ImageIcon(getClass().getResource("/graphics/folderClosedIcon.gif")));
        UIManager.put("Tree.forwardSortIcon", new ImageIcon(getClass().getResource("/graphics/sortUpIcon.png")));
        UIManager.put("Tree.backwardSortIcon", new ImageIcon(getClass().getResource("/graphics/sortDownIcon.png")));

        UIManager.put("AbilityTree.criticalIcon", new ImageIcon(getClass().getResource("/graphics/criticalIcon.gif")));
        UIManager.put("AbilityTree.errorIcon", new ImageIcon(getClass().getResource("/graphics/errorIcon.gif")));
        //       UIManager.put( "AbilityTree.questionIcon", new ImageIcon( getClass().getResource("/graphics/small14x14errorIcon.gif") ));
        UIManager.put("AbilityTree.questionIcon", new ImageIcon(getClass().getResource("/graphics/warningIcon.png")));
        //UIManager.put( "AbilityTree.questionIcon", new ImageIcon( getClass().getResource("/graphics/questionIcon.gif") ));
        UIManager.put("AbilityTree.childCriticalIcon", new ImageIcon(getClass().getResource("/graphics/childCriticalIcon.gif")));
        UIManager.put("AbilityTree.childErrorIcon", new ImageIcon(getClass().getResource("/graphics/childErrorIcon.gif")));
        //UIManager.put( "AbilityTree.childQuestionIcon", new ImageIcon( getClass().getResource("/graphics/childQuestionIcon.gif")) );
        UIManager.put("AbilityTree.childQuestionIcon", new ImageIcon(getClass().getResource("/graphics/childWarningIcon.png")));
        UIManager.put("AbilityTree.leftDragIcon", new ImageIcon(getClass().getResource("/graphics/abilityTreeDragLeftArrow.gif")));
        UIManager.put("AbilityTree.rightDragIcon", new ImageIcon(getClass().getResource("/graphics/abilityTreeDragRightArrow.gif")));
        UIManager.put("AbilityTree.createVariationIcon", new ImageIcon(getClass().getResource("/graphics/plus_green.gif")));

        UIManager.put("AbilityTree.endColumnIcon", new ImageIcon(getClass().getResource("/graphics/endColumnIcon.png")));
        UIManager.put("AbilityTree.ocvColumnIcon", new ImageIcon(getClass().getResource("/graphics/ocvColumnIcon.png")));
        UIManager.put("AbilityTree.dcvColumnIcon", new ImageIcon(getClass().getResource("/graphics/dcvColumnIcon.png")));
        UIManager.put("AbilityTree.cpColumnIcon", new ImageIcon(getClass().getResource("/graphics/cpColumnIcon.png")));
        UIManager.put("AbilityTree.autoActivateColumnIcon", new ImageIcon(getClass().getResource("/graphics/autoActivateColumnIcon.png")));

        UIManager.put("AbilityTree.deactivateSelected", new ImageIcon(getClass().getResource("/graphics/redStopButton.png")));
        UIManager.put("AbilityTree.deactivateUnselected", new ImageIcon(getClass().getResource("/graphics/greyStopButton.png")));
        UIManager.put("AbilityTree.activateSelected", new ImageIcon(getClass().getResource("/graphics/greenPlayButton.png")));
        UIManager.put("AbilityTree.activateUnselected", new ImageIcon(getClass().getResource("/graphics/greyPlayButton.png")));

        UIManager.put("ErrorTree.warningIcon", new ImageIcon(getClass().getResource("/graphics/warningIcon.png")));
        UIManager.put("ErrorTree.errorIcon", new ImageIcon(getClass().getResource("/graphics/errorIconCentered.png")));

        UIManager.put("AbilityPanel.defaultPortraitIcon", new ImageIcon(getClass().getResource("/graphics/defaultPortrait.gif")));

        UIManager.put("AbilityTree.headerFont", new Font("SansSerif", Font.PLAIN, 10));
        UIManager.put("AbilityTree.defaultFont", new Font("SansSerif", Font.PLAIN, 11));
        UIManager.put("AbilityTree.sublistCPFont", new Font("Dialog", Font.BOLD, 12));

        UIManager.put("AbilityTree.rosterIcon", new ImageIcon(getClass().getResource("/graphics/rosterIcon.png")));

        //UIManager.put( "AbilityTree.background", new Color(204,204,204) );
        //UIManager.put( "AbilityTree.foreground", Color.black );

        UIManager.put("AbilityButton.runningIcon", new ImageIcon(getClass().getResource("/graphics/green_stoplight3.png")));
        UIManager.put("AbilityButton.stoppedIcon", new ImageIcon(getClass().getResource("/graphics/red_stoplight3.png")));
        UIManager.put("AbilityButton.disabledIcon", new ImageIcon(getClass().getResource("/graphics/grey_stoplight3.png")));
        UIManager.put("AbilityButton.negatvielyAdjustedIcon", new ImageIcon(getClass().getResource("/graphics/negativelyAdjustedIcon.gif")));
        UIManager.put("AbilityButton.positivelyAdjustedIcon", new ImageIcon(getClass().getResource("/graphics/positivelyAdjustedIcon.gif")));
        UIManager.put("AbilityButton.warningIcon", new ImageIcon(getClass().getResource("/graphics/childWarningIcon.png")));
        UIManager.put("AbilityButton.errorIcon", new ImageIcon(getClass().getResource("/graphics/small14x14errorIcon.gif")));
        UIManager.put("AbilityButton.autoActivateOnIcon", new ImageIcon(getClass().getResource("/graphics/autoActivateOnIcon.png")));
        UIManager.put("AbilityButton.autoActivateOffIcon", new ImageIcon(getClass().getResource("/graphics/autoActivateOffIcon.png")));
        UIManager.put("ToggleButton.diagArrowIcon", new ImageIcon(getClass().getResource("/graphics/arrow_diag.gif")));
        UIManager.put("ToggleButton.rightArrowIcon", new ImageIcon(getClass().getResource("/graphics/arrow_down.gif")));
        UIManager.put("ToggleButton.downArrowIcon", new ImageIcon(getClass().getResource("/graphics/arrow_right.gif")));

        UIManager.put("SenseTree.trueIcon", new ImageIcon(getClass().getResource("/graphics/greenCheckIcon.gif")));
        UIManager.put("SenseTree.falseIcon", new ImageIcon(getClass().getResource("/graphics/redXIcon.gif")));

        UIManager.put("HTML.beginPlainText", "<font SIZE=\"-1\" FACE=\"Arial,'Times New Roman',System\">");
        UIManager.put("HTML.endPlainText", "</font>");

        //   UIManager.put( "AbilityEditor.background", new Color(204,204,204) );
        UIManager.put("AbilityEditor.foreground", Color.black);

        UIManager.put("TargetEditor.headerBackground", Color.black);
        UIManager.put("TargetEditor.headerForeground", Color.white);
        UIManager.put("TargetEditor.headerOpenIcon", new ImageIcon(getClass().getResource("/graphics/targetEditorHeaderOpen.gif")));
        UIManager.put("TargetEditor.headerClosedIcon", new ImageIcon(getClass().getResource("/graphics/targetEditorHeaderClosed.gif")));
        UIManager.put("TargetEditor.headerLeadIcon", new ImageIcon(getClass().getResource("/graphics/targetEditorHeaderLead.gif")));
        UIManager.put("TargetEditor.editPortraitIcon", new ImageIcon(getClass().getResource("/graphics/portraitButton.gif")));

        /*UIManager.put("Messages.doneBackgroundColor", new Color(204,204,204));
        UIManager.put("Messages.undoneBackgroundColor", new Color(155,155,155));
        UIManager.put("Messages.doneSelectionColor", new Color(230,230,230));
        UIManager.put("Messages.undoneSelectionColor", new Color(230,230,230));
        UIManager.put("Messages.textSelectionColor", Color.white); */

        UIManager.put("OnDeckList.activeFont", new Font("SansSerif", Font.BOLD, 11));
        UIManager.put("OnDeckList.nonActiveFont", new Font("SansSerif", Font.PLAIN, 11));
        UIManager.put("OnDeckList.activeColor", Color.red);
        UIManager.put("OnDeckList.nonActiveColor", Color.black);

        // AttackTree Icons
        UIManager.put("AttackTree.attackParametersIcon", new ImageIcon(getClass().getResource("/graphics/clipboardIcon.gif")));
        UIManager.put("AttackTree.linkedSetupIcon", new ImageIcon(getClass().getResource("/graphics/clipboardIcon.gif")));
        UIManager.put("AttackTree.throwParameterIcon", new ImageIcon(getClass().getResource("/graphics/clipboardIcon.gif")));

        UIManager.put("AttackTree.selectTargetIcon", new ImageIcon(getClass().getResource("/graphics/targetIcon2.gif")));
        UIManager.put("AttackTree.diceIcon", new ImageIcon(getClass().getResource("/graphics/diceIcon2.gif")));
        UIManager.put("AttackTree.summaryIcon", new ImageIcon(getClass().getResource("/graphics/summaryIcon2.gif")));
        UIManager.put("AttackTree.knockbackEffectIcon", new ImageIcon(getClass().getResource("/graphics/knockbackWallIcon.gif")));
        UIManager.put("AttackTree.toHitIcon", new ImageIcon(getClass().getResource("/graphics/toHitIcon.gif")));
        UIManager.put("AttackTree.blockIcon", new ImageIcon(getClass().getResource("/graphics/blockIcon.png")));
        UIManager.put("AttackTree.perceptionsIcon", new ImageIcon(getClass().getResource("/graphics/perceptionIcon.gif")));
        UIManager.put("AttackTree.defensesIcon", new ImageIcon(getClass().getResource("/graphics/defensesIcon.gif")));
        UIManager.put("AttackTree.appyEffectsIcon", new ImageIcon(getClass().getResource("/graphics/summaryIcon2.gif")));
        UIManager.put("AttackTree.hitLocationIcon", new ImageIcon(getClass().getResource("/graphics/hitLocationIcon.gif")));
        UIManager.put("AttackTree.hexIcon", new ImageIcon(getClass().getResource("/graphics/hexIcon.gif")));
        UIManager.put("AttackTree.obstructionIcon", new ImageIcon(getClass().getResource("/graphics/obstructionIcon.gif")));
        UIManager.put("AttackTree.runningIcon", new ImageIcon(getClass().getResource("/graphics/runningIcon.gif")));
        UIManager.put("AttackTree.mentalEffectsIcon", new ImageIcon(getClass().getResource("/graphics/diceIcon2.gif")));
        UIManager.put("AttackTree.selectPowerIcon", new ImageIcon(getClass().getResource("/graphics/genericPower.gif")));
        UIManager.put("AttackTree.configurePowerIcon", new ImageIcon(getClass().getResource("/graphics/genericPower.gif")));
        UIManager.put("AttackTree.abilitySourceIcon", new ImageIcon(getClass().getResource("/graphics/genericPower.gif")));
        UIManager.put("AttackTree.genericConfigIcon", new ImageIcon(getClass().getResource("/graphics/clipboardIcon.gif")));
        UIManager.put("AttackTree.powerIcon", new ImageIcon(getClass().getResource("/graphics/genericPower.gif")));

        // Adjustmnet Controls
        UIManager.put("AdjustmentControl.addIcon", new ImageIcon(getClass().getResource("/graphics/adjustmentAddIcon.gif")));
        UIManager.put("AdjustmentControl.addAllIcon", new ImageIcon(getClass().getResource("/graphics/adjustmentAddAllIcon.gif")));
        UIManager.put("AdjustmentControl.removeIcon", new ImageIcon(getClass().getResource("/graphics/adjustmentRemoveIcon.gif")));
        UIManager.put("AdjustmentControl.removeAllIcon", new ImageIcon(getClass().getResource("/graphics/adjustmentRemoveAllIcon.gif")));

        // GenericControl Controls
        UIManager.put("GenericControl.addIcon", new ImageIcon(getClass().getResource("/graphics/adjustmentAddIcon.gif")));
        UIManager.put("GenericControl.addAllIcon", new ImageIcon(getClass().getResource("/graphics/adjustmentAddAllIcon.gif")));
        UIManager.put("GenericControl.removeIcon", new ImageIcon(getClass().getResource("/graphics/adjustmentRemoveIcon.gif")));
        UIManager.put("GenericControl.removeAllIcon", new ImageIcon(getClass().getResource("/graphics/adjustmentRemoveAllIcon.gif")));


        UIManager.put("ProfileOption.trueIcon", new ImageIcon(getClass().getResource("/graphics/showPanelIcon.png")));
        UIManager.put("ProfileOption.falseIcon", new ImageIcon(getClass().getResource("/graphics/skipPanelIcon.png")));
        UIManager.put("ProfileOption.defaultTrueIcon", new ImageIcon(getClass().getResource("/graphics/defaultShowPanelIcon.png")));
        UIManager.put("ProfileOption.defaultFalseIcon", new ImageIcon(getClass().getResource("/graphics/defaultSkipPanelIcon.png")));
        UIManager.put("ProfileOption.inheritIcon", new ImageIcon(getClass().getResource("/graphics/defaultSkipPanelIcon.png")));

        UIManager.put("HitLocation.None", new ImageIcon(getClass().getResource("/graphics/noHitLocation.gif")));
        UIManager.put("HitLocation.Head", new ImageIcon(getClass().getResource("/graphics/headHitLocation.gif")));
        UIManager.put("HitLocation.Hands", new ImageIcon(getClass().getResource("/graphics/handsHitLocation.gif")));
        UIManager.put("HitLocation.Arms", new ImageIcon(getClass().getResource("/graphics/armsHitLocation.gif")));
        UIManager.put("HitLocation.Shoulders", new ImageIcon(getClass().getResource("/graphics/shouldersHitLocation.gif")));
        UIManager.put("HitLocation.Chest", new ImageIcon(getClass().getResource("/graphics/chestHitLocation.gif")));
        UIManager.put("HitLocation.Stomach", new ImageIcon(getClass().getResource("/graphics/stomachHitLocation.gif")));
        UIManager.put("HitLocation.Vitals", new ImageIcon(getClass().getResource("/graphics/vitalsHitLocation.gif")));
        UIManager.put("HitLocation.Thighs", new ImageIcon(getClass().getResource("/graphics/thighsHitLocation.gif")));
        UIManager.put("HitLocation.Legs", new ImageIcon(getClass().getResource("/graphics/legsHitLocation.gif")));
        UIManager.put("HitLocation.Feet", new ImageIcon(getClass().getResource("/graphics/feetHitLocation.gif")));
        UIManager.put("HitLocation.HeadShot", new ImageIcon(getClass().getResource("/graphics/headShotHitLocation.gif")));
        UIManager.put("HitLocation.HighShot", new ImageIcon(getClass().getResource("/graphics/highShotHitLocation.gif")));
        UIManager.put("HitLocation.BodyShot", new ImageIcon(getClass().getResource("/graphics/bodyShotHitLocation.gif")));
        UIManager.put("HitLocation.LowShot", new ImageIcon(getClass().getResource("/graphics/lowShotHitLocation.gif")));
        UIManager.put("HitLocation.LegShot", new ImageIcon(getClass().getResource("/graphics/legShotHitLocation.gif")));

        UIManager.put("Editor.upButtonNormal", new ImageIcon(getClass().getResource("/graphics/upButtonNormal.gif")));
        UIManager.put("Editor.upButtonPressed", new ImageIcon(getClass().getResource("/graphics/upButtonPressed.gif")));
        UIManager.put("Editor.downButtonNormal", new ImageIcon(getClass().getResource("/graphics/downButtonNormal.gif")));
        UIManager.put("Editor.downButtonPressed", new ImageIcon(getClass().getResource("/graphics/downButtonPressed.gif")));

        UIManager.put("ProfileOptionPanel.selectedColor", new Color(230, 230, 230));

        UIManager.put("TreeTableUI", "treeTable.BasicTreeTableUI");
        // UIManager.put("DockingSplitPaneUI", "dockable.DockingSplitPaneUI");

        UIManager.put("PopupList.checkedIcon", new ImageIcon(getClass().getResource("/graphics/greenCheckIcon.gif")));
        UIManager.put("PopupList.uncheckedIcon", new ImageIcon(getClass().getResource("/graphics/popupListUncheckedIcon.gif")));

        UIManager.put("MentalEffectList.checkedIcon", new ImageIcon(getClass().getResource("/graphics/greenCheckIcon.gif")));
        UIManager.put("MentalEffectList.uncheckedIcon", new ImageIcon(getClass().getResource("/graphics/popupListUncheckedIcon.gif")));

        UIManager.put("LinkedSetupPanel.checkedIcon", new ImageIcon(getClass().getResource("/graphics/greenCheckIcon.gif")));
        UIManager.put("LinkedSetupPanel.uncheckedIcon", new ImageIcon(getClass().getResource("/graphics/popupListUncheckedIcon.gif")));

        UIManager.put("WindowIcon.attackTreeDP", new ImageIcon(getClass().getResource("/graphics/targetIcon2.gif")));
        UIManager.put("WindowIcon.activeTargetDP", new ImageIcon(getClass().getResource("/graphics/hitLocationIcon.gif")));
        UIManager.put("WindowIcon.abilitiesDP", new ImageIcon(getClass().getResource("/graphics/genericPower.gif")));
        UIManager.put("WindowIcon.abilitiesDP", new ImageIcon(getClass().getResource("/graphics/genericPower.gif")));

        UIManager.put("CombinedAbility.Icon", new ImageIcon(getClass().getResource("/graphics/CombinedAbilityIcon.png")));
        UIManager.put("AttackTree.combinedSetupIcon", new ImageIcon(getClass().getResource("/graphics/CombinedAbilityIcon.png")));

    }

    /** This is the main method.  If you don't understand that, you should
     * probably not be looking at this code anyway.
     * @param args Command line arguments.
     */
    public static void main(String args[]) {
        System.setProperty("sun.awt.exception.handler", "champions.ExceptionHandler");

        try {

            if (Preferences.getBooleanValue("LookAndFeel")) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        try {
            new CombatSimulator();

        } catch (Throwable thrown) {
            // new ExceptionWizard(thrown);
            ExceptionWizard.postException(thrown);
        }
    }

    public static AttackTreeDockingPanel getAttackTreeDockingPanel() {
        return attackTreeDockingPanel;
    }

    public static RosterDockingPanel getRosterDockingPanel() {
        return rosterDockingPanel;
    }

    public static DockingPanel getActiveTargetDockingPanel() {
        return activeTargetDockingPanel;
    }

    public static DockingPanel getOnDeckDockingPanel() {
        return onDeckDockingPanel;
    }

    public static DockingPanel getEligibleDockingPanel() {
        return eligibleDockingPanel;
    }

    public static DockingPanel getActiveTargetAbilitiesDockingPanel() {
        return activeTargetAbilitiesDockingPanel;
    }

    public static DockingPanel getDefaultAbilitiesDockingPanel() {
        return defaultAbilitiesDockingPanel;
    }

    public static DockingPanel getChronometerDockingPanel() {
        return chronometerDockingPanel;
    }
}
