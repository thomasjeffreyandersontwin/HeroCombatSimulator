/*
 * PADAbilityNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.Ability;
import champions.ActivationInfo;
import champions.Battle;
import champions.BattleEvent;
import champions.BattleEventList;
import champions.ChampionsUtilities;
import champions.CombatState;
import champions.CombinedAbility;
import champions.FrameworkAbility;
import champions.MyMenu2;
import champions.Roster;
import tjava.ObjectTransferable;
import champions.Target;
import champions.event.AbilityAddedEvent;
import champions.event.AbilityRemovedEvent;
import champions.event.ActivationStateChangeEvent;
import champions.event.InstanceChangedEvent;
import champions.interfaces.AbilityInstanceGroupListener;
import champions.interfaces.AbilityIterator;
import champions.interfaces.AbilityList;
import champions.interfaces.ENDSource;
import tjava.Filter;
import champions.powers.maneuverMoveThrough;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTable;
import treeTable.DefaultTreeTableModel;
import treeTable.TreeTable;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableCellRenderer;
import treeTable.TreeTableColumnModel;


/**
 *
 * @author  twalker
 * @version
 *
 * PADAbilityNode's hold references to Abilities stored in an Ability list.
 * It should not be used for powers/templates which are cached by the PADRoster
 * shared instance mechanism.
 */
public class ATAbilityNode extends ATNode implements PropertyChangeListener, AbilityInstanceGroupListener {
    
    /** Hold the Editor for the ability button. */
//    protected static AbilityTreeTableCellEditor editor = null;
    
    /** Hold the Renderer for the ability button. */
//    protected static AbilityTreeTableCellEditor renderer = null;
    
    /** Holds the Ability list this ability belongs to.
     *
     * This value can be null.
     */
    protected AbilityList abilityList;
    
    /** Holds value of property ability.
     *
     * Under normal circumstances, this will not be null.  However, in
     * some rare instances it might be, so it should be checked before using.
     */
    public Ability ability;
    
    /** Cache of the Ability name. */
    public String abilityName;
    
    /** Holds the icons */
    static protected Icon runningIcon = null;
    static protected Icon runningWarningIcon = null;
    static protected Icon stoppedIcon = null;
    static protected Icon disabledIcon = null;
    static protected Icon frameworkDisabledIcon = null;
    static protected Icon frameworkRunningIcon = null;
    static protected Icon frameworkStoppedIcon = null;
    
    
    static protected JPanel iconPanel = null;
    static protected Icon errorIcon, warningIcon;
    static protected Icon aidedIcon, drainedIcon;
    
    static private ATAutoActivateTreeTableCellEditor autoActivateEditor;
    static private ATAutoActivateTreeTableCellEditor autoActivateRenderer;
    
    /** Indicates the delete option should be shown.
     */
    protected boolean deleteEnabled = false;
    
    protected static RemoveAbilityAction deleteAction = null;
    protected static DebugAction debugAction = null;
    protected static EditAction editAction = null;
    protected static ActivateAbilityAction activateAbilityAction = null;
    protected static DeactivateAbilityAction deactivateAbilityAction = null;
    protected static ForceActivateAbilityAction forceActivateAbilityAction = null;
    protected static ForceDeactivateAbilityAction forceDeactivateAbilityAction = null;
    protected static CancelActivateAbilityAction cancelActivateAbilityAction = null;
    protected static SaveAction saveAction = null;
    protected static CreateTemplateAction createTemplateAction = null;
    protected static SetCurrentInstanceAction setCurrentInstanceAction = null;
    protected static AbortToAbilityAction abortToAbilityAction = null;
    protected static boolean actionsSetup = false;
    
    protected static DataFlavor abilityFlavor = null;
    
    protected static ATAbilityCellRenderer renderer = new ATAbilityCellRenderer();
    protected static ATAbilityCellRenderer editor = new ATAbilityCellRenderer();
    
    /** Indicates there are actions to be shown. */
    protected boolean abilityHasActions = false;
    
    /** Holds the text for the END label. */
    protected String endText;
    
    /** Holds the color for the END label. */
    protected Color endColor;
    
    protected ImageIcon currentIcon = null;
    protected BufferedImage iconBuffer = null;
    protected Icon lastBaseIcon = null;
    
    protected ATAbilityDefaultActionHandler defaultActionHandler = null;
    
    private boolean abilityRendererEnabled = true;
    
//    /** Creates new PADAbilityNode */
//    public ATAbilityNode() {
//        setupIcons();
//        setupActions();
//        setupFlavors();
//        
//        setupEditors();
//    }
    
    /** Creates new PADAbilityNode */
    public ATAbilityNode(Ability ability, AbilityList abilityList, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        super(nodeFactory, nodeFilter, pruned);
        
        setAbility(ability);
        setAbilityList(abilityList);
        
        setupIcons();
        setupActions();
        setupFlavors();
        setupEditors();
        setupDefaultActionHandler();
        
        buildNode();
    }
    
    protected void setupIcons() {
        if ( runningIcon == null ) runningIcon = UIManager.getIcon("AbilityButton.runningIcon");
        if ( runningWarningIcon == null ) runningWarningIcon = UIManager.getIcon("AbilityButton.runningWarningIcon");
        if ( stoppedIcon == null ) stoppedIcon = UIManager.getIcon("AbilityButton.stoppedIcon");
        if ( disabledIcon == null ) disabledIcon = UIManager.getIcon("AbilityButton.disabledIcon");
        if ( frameworkRunningIcon == null ) frameworkRunningIcon = UIManager.getIcon("Framework.RunningIcon");
        if ( frameworkStoppedIcon == null ) frameworkStoppedIcon = UIManager.getIcon("Framework.StoppedIcon");
        if ( frameworkDisabledIcon == null ) frameworkDisabledIcon = UIManager.getIcon("Framework.DisabledIcon");
        //if ( autoActivateIcon == null ) autoActivateIcon = UIManager.getIcon("AbilityButton.autoActivateIcon");
        
        
        currentIcon = new ImageIcon();
        iconBuffer = new BufferedImage(16,16, BufferedImage.TYPE_INT_ARGB);
        currentIcon.setImage(iconBuffer);
        
        if ( iconPanel == null ) {
            
            iconPanel = new JPanel();
            
            errorIcon = UIManager.getIcon( "AbilityButton.errorIcon" );
            warningIcon = UIManager.getIcon( "AbilityButton.warningIcon" );
            
            aidedIcon = UIManager.getIcon( "AbilityButton.positivelyAdjustedIcon" );
            drainedIcon = UIManager.getIcon( "AbilityButton.negativelyAdjustedIcon" );
        }
    }
    
    protected void setupEditors() {
        if ( autoActivateEditor == null ) autoActivateEditor = new ATAutoActivateTreeTableCellEditor();
        if ( autoActivateRenderer == null ) autoActivateRenderer = new ATAutoActivateTreeTableCellEditor();
    }
    
    protected void setupActions() {
        if ( actionsSetup == false ) {
            actionsSetup = true;
            
            deleteAction = new RemoveAbilityAction();
            debugAction = new DebugAction();
            editAction = new EditAction();
            activateAbilityAction = new ActivateAbilityAction();
            deactivateAbilityAction = new DeactivateAbilityAction();
            forceActivateAbilityAction = new ForceActivateAbilityAction();
            forceDeactivateAbilityAction = new ForceDeactivateAbilityAction();
            createTemplateAction = new CreateTemplateAction();
            setCurrentInstanceAction = new SetCurrentInstanceAction();
            cancelActivateAbilityAction = new CancelActivateAbilityAction();
            abortToAbilityAction = new AbortToAbilityAction();
            saveAction = new SaveAction();
        }
    }
    
    protected void setupFlavors() {
        try {
            if (abilityFlavor == null ) abilityFlavor = new DataFlavor( DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Ability.class.getName() );
        } catch ( Exception e) {
            System.out.println(e);
        }
    }
    
    protected void setupDefaultActionHandler() {
        defaultActionHandler = new ATAbilityDefaultActionHandler() {
            public void handleDefaultAction(MouseEvent mouseEvent) {
                        if ( ability != null ) {
                        	if ( ability.isAutoSource() == false && Battle.currentBattle != null && Battle.currentBattle.getActiveTarget() != ability.getSource() ) {
                    			popupActivateMenu(mouseEvent.getPoint() );
                    		}
                    		else {
                    			if ( ability.isActivated(ability.getSource()) ) {
                    				if ( ability instanceof CombinedAbility ) {
                    					BattleEvent be =  new BattleEvent(ability,  BattleEvent.DEACTIVATE);
                    					if ( mouseEvent != null && (mouseEvent.getModifiers() & Event.SHIFT_MASK) != 0 ) {
                    						be.setAlwaysShowAttackTree(true);
                    					}
                    					Battle.currentBattle.addEvent(be);
	
                    				} 
                    				else {
                    					Iterator<ActivationInfo> it = ability.getActivations( ability.getSource());
                    					while(it.hasNext()) {
                    						ActivationInfo ai = it.next();
                    						BattleEvent be =  new BattleEvent(  BattleEvent.DEACTIVATE, ai);
                    						Battle.currentBattle.addEvent(be);
                    					}
                    				}
                    			}
                    			else {
                    				//what we care about
                    				BattleEvent be = null;
                    				be = ability.getActivateAbilityBattleEvent(ability, null, null);
                    				if ( mouseEvent != null && (mouseEvent.getModifiers() & Event.SHIFT_MASK) != 0 ) {
                    					be.setAlwaysShowAttackTree(true);
                    				}
                    				
                    				if ( be == null ) be =  new BattleEvent(  ability );
                    				Battle.currentBattle.addEvent( be );
                    					
                    						
                    							
                    									}
                    			}
                    		}
                        }
          
            
        };        
    }
    
    /**
     * Returns the value to be displayed for node <code>node</code>,
     * at column number <code>column</code>.
     * @param column The column of the node for which a value should be returned.
     * @return The Value of the column.
     */
    @Override
    public Object getValueAt(int column) {
        if ( ability == null ) return null;
        ATColumn atc = ATColumn.values()[column];
        switch(atc){
            case NAME_COLUMN:
                return abilityName;
            case END_COLUMN:
                //return Integer.toString(ability.getENDCost());
                return endText;
            case DCV_COLUMN:
                return ChampionsUtilities.toSignedString( ability.getDCVModifier() );
            case OCV_COLUMN:
                return ChampionsUtilities.toSignedString( ability.getOCVModifier() );
            case CP_COLUMN:
                int adjustment = ability.getTotalAdjustmentAmount();
                if ( adjustment != 0 ) {
                    return ChampionsUtilities.toSignedString(adjustment);
                } else {
                    return Integer.toString(ability.getCPCost());
                }
            case AUTO_ACTIVATE_COLUMN:
                return ability.isNormallyOn();
                
            case RANGE_COLUMN:
                return ability.getRange() + "\"";
            case AP_COLUMN:
                return ability.getAPCost();
            default:
                return null;
        }
    }
    
    @Override
    public boolean startDrag(TreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if ( ability != null ) {
            Transferable t = new ObjectTransferable(ability, Ability.class);
            
            Point p = dge.getDragOrigin();
            Rectangle bounds = tree.getPathBounds(path);
            Point offset = new Point(p.x - bounds.x, p.y - bounds.y);
            
            BufferedImage i = ((DefaultTreeTable)tree).buildDragImage(path);
            DefaultTreeTable.startDrag(i, offset);
            
            dge.startDrag(null,i,offset, t, listener);
            
            return true;
        }
        return false;
    }
    
    /** Notifies Node of a drop event occuring in or below the node.
     * The Node can use the handleDrop method to perform actions based on the drop of a DetailList on
     * the Node Jtree.  If the node wishes to handles the event, it should call acceptDrop and dropComplete
     * on the event, then return true to indicate the event was handled.
     * @returns True if event was handled.  False if additional handling
     * should be done.
     * @return True if the event was handled, false if it wasn't.
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param dropPath Indicates the location of the drop in the Tree hierarchy.
     * @param event The Drop event that is being handled.
     */
    @Override
    public boolean handleDrop(TreeTable treeTable, TreePath dropPath, DropTargetDropEvent event) {
        try {
            // System.out.println("Got drop with path: " + dropPath);
            
            Transferable tr = event.getTransferable();
            if ( event.isDataFlavorSupported( abilityFlavor ) ) {
                Ability a = (Ability)tr.getTransferData(abilityFlavor);
                if ( a != ability && a.getInstanceGroup() != null && a.getInstanceGroup() == ability.getInstanceGroup() ) {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    
                    if ( a.isCurrentInstance() ) {
                        a.getInstanceGroup().setCurrentInstance(ability);
                    } else {
                        a.getInstanceGroup().setCurrentInstance(a);
                    }
                    
                    event.dropComplete(true);
                    return true;
                }
            }
            
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(UnsupportedFlavorException ufe) {
            ufe.printStackTrace();
        }
        
        return false;
    }
    
    /** Called to check if a node would handle a drop if it occurred.
     * @return Returns the TreePath after which a feedback line could be drawn indicating where the drop will be placed.
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param dropPath Indicates the location of the drop in the Tree hierarchy.
     * @param event The Drop event that is being handled.
     */
    @Override
    public TreePath willHandleDrop(TreeTable treeTable, TreePath dropPath, DropTargetDragEvent event) {
        if(event.isDataFlavorSupported(abilityFlavor) ) {
            TreePath tp = new TreePath(this.getPath());
            //tp = tp.pathByAddingChild( new DropAsChildPlaceHolder());
            return tp;
        }
        return null;
    }
    
 /*   public void handleDoubleClick(AbilityTreeTable tree, TreePath abilityPath) {
        AbilityTreeNode node = (AbilityTreeNode)abilityPath.getLastPathComponent();
  
        while ( node != null && ! ( node instanceof SublistNode ) ) {
            node = (AbilityTreeNode)node.getParent();
        }
  
        if ( node != null ) {
            //  String sublist = ((SublistNode)node).getSublist();
            Ability a = (Ability)ability.clone();
            // ability.setSublist(sublist);
            ((SublistNode)node).addAbility(a);
        }
    } */
    
    /** Returns the Ability associated with node.
     *
     * This should return whatever the node represents.
     *
     * @return null if no Ability is associated, such as in the case of a folder.
     */
    public Ability getAbility() {
        return ability;
    }
    
    /**
     * Returns the Icon to be used when drawing this node.
     *
     * If the Icon is null, the standard open, closed, leaf icons will be used.
     * @param treeTable The treeTable which is currently displaying the node.
     * @param isSelected Whether this node is currently selected.
     * @param expanded Whether this node is currently expanded.
     * @param leaf Whether this node is currently considered a leaf.
     * @param row The row at which the node is current displayed in the TreeTable.
     * @return An Icon which should be used for this node.  Null if default icons should be used.
     */
    @Override
    public Icon getIcon(TreeTable treeTable, boolean isSelected, boolean expanded, boolean leaf, int row) {
        //return (ability != null) ? ability.getPower().getIcon() : null;
        if ( ability == null ) return null;
        
        if ( icon == null ) {
            icon = createIcon(treeTable);
        }
        return icon;
    }
    
    protected Icon createIcon(TreeTable treeTable) {
        Icon baseIcon;
        
        if ( treeTable != null ) {
            clearIcon(treeTable);
            
            baseIcon = getBaseIcon(treeTable);
            
            lastBaseIcon = baseIcon;
            
            if ( ability.getEnableColor() == Ability.getEnableErrorColor()  ) {
                baseIcon = renderIcon(treeTable, baseIcon, errorIcon);
            } else if ( ability.getEnableColor() == Ability.getEnableWarningColor() ) {
                baseIcon = renderIcon(treeTable, baseIcon, warningIcon);
            }
            
            if ( ability.isAdjustedPositively() ) {
                baseIcon = renderIcon(treeTable, baseIcon, aidedIcon);
            } else if ( ability.isAdjustedNegatively() ) {
                baseIcon = renderIcon(treeTable, baseIcon, drainedIcon);
            }
            
            //        if ( ability.isNormallyOn() ) {
            //            baseIcon = renderIcon(treeTable, baseIcon, autoActivateIcon);
            //        }
        } else {
            baseIcon = null;
        }
        
        return baseIcon;
    }
    
    protected Icon getBaseIcon(TreeTable treeTable) {
        Icon baseIcon;
        if ( ability.getInstanceGroup().getBaseInstance() instanceof FrameworkAbility ) {
            if ( ability.isActivated(ability.getSource()) ) {
                baseIcon = frameworkRunningIcon;
            } else if ( ability.isEnabled(ability.getSource()) == false) {
                baseIcon = frameworkDisabledIcon;
            } else {
                baseIcon = frameworkStoppedIcon;
            }
        } else {
            boolean activated = ability.isActivated(ability.getSource());
            boolean enabled = ability.isEnabled(ability.getSource());
            boolean isActiveCharacter = ( Battle.currentBattle != null && Battle.currentBattle.getActiveTarget() == ability.getSource() );
            if ( activated && isActiveCharacter && ! enabled ) {
                baseIcon = renderIcon(treeTable, runningIcon, errorIcon);
            } else if ( activated ) {
                baseIcon = runningIcon;
            } else if ( isActiveCharacter && ! enabled ) {
                baseIcon = disabledIcon;
            } else {
                baseIcon = stoppedIcon;
            }
        }
        return baseIcon;
    }
    
    private void clearIcon(TreeTable treeTable) {
        JPanel panel = iconPanel;
        
        Graphics2D g = null;
        try {
            g = (Graphics2D)iconBuffer.getGraphics();
            if ( g != null ) {
                if ( treeTable.isOpaque() ) {
                    g.setColor(treeTable.getBackground());
                    g.fillRect(0,0,iconBuffer.getWidth(), iconBuffer.getHeight());
                } else {
                    g.setColor(new Color(0,0,0,0));
                    //g.setComposite(AlphaComposite.Src);
                    g.setComposite(AlphaComposite.Src);
                    g.fill( new Rectangle2D.Double(0,0,iconBuffer.getWidth(), iconBuffer.getHeight()));
                }
            }
            
        } finally {
            if ( g != null ) g.dispose();
        }
    }
    
    private Icon renderIcon(TreeTable treeTable, Icon baseIcon, Icon overlayIcon) {
        JPanel panel = iconPanel;
        
        Graphics2D g = null;
        try {
            g = (Graphics2D) iconBuffer.getGraphics();
            
            if ( g != null ) {
                
                g.setComposite(AlphaComposite.SrcOver);
                
                if (baseIcon != null ) {
                    baseIcon.paintIcon(panel, g, 0, 0);
                }
                
                if ( overlayIcon != null ) {
                    overlayIcon.paintIcon(panel, g, 0, 0);
                }
                
            }
        } finally {
            if ( g != null ) g.dispose();
        }
        
        return currentIcon;
    }
    
    /** Allows the node to provide context menu feedback.
     *
     * invokeMenu is called whenever an event occurs which would cause a context menu to be displayed.
     * If the node has context menu items to display, it should add them to the JPopupMenu <code>popup</code>
     * and return true.
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param path The TreePath which the cursor was over when the context menu
     * event occurred.
     * @param popup The JPopupMenu which is being built.  Always non-null and initialized.
     * @return True if menus where added, False if menus weren't.
     */
    @Override
    public boolean invokeMenu(TreeTable treeTable,TreePath path, JPopupMenu popup) {
        boolean rv = false;
        if ( model != null && ((ATModel)model).isSimpleTree()) {
            if ( path.getLastPathComponent() == this ) {
                if ( ability != null ) {
                    editAction.setAbility(ability);
                    popup.add(editAction);
                    rv = true;
                }
                
                if ( deleteEnabled ) {
                    if ( ability != null && getAbilityList() != null ) {
                        deleteAction.setAbility(ability);
                        deleteAction.setAbilityList(abilityList);
                        popup.add(deleteAction);
                        rv = true;
                    }
                }
                
                if ( Battle.debugLevel >= 0 ) {
                    if ( rv ) popup.addSeparator();
                    
                    
                    debugAction.setAbility(ability);
                    popup.add(debugAction);
                    rv = true;
                }
            }
        } else {
            if ( path.getLastPathComponent() == this ) {
                // This is the more complex version of the menus...
                if ( Battle.currentBattle != null && Battle.currentBattle.isProcessing() == false && Battle.currentBattle.isStopped() == false ) {
                    if ( ability.isDelayActivating(ability.getSource()) ) {
                        // This ability is delayed and currently activiting...
                        // Setup a cancel activating action
                        cancelActivateAbilityAction.setAbility(ability);
                        popup.add( cancelActivateAbilityAction );
                    }
                    if ( ability.isActivated(ability.getSource()) == false ) {
                        if ( ability.isEnabled(ability.getSource()) == true ) {
                            activateAbilityAction.setAbility(ability);
                            popup.add( activateAbilityAction );
                        } else {
                            forceActivateAbilityAction.setAbility(ability);
                            popup.add( forceActivateAbilityAction );
                        }
                    } else {
                        if ( ability.isEnabled(ability.getSource()) == true ) {
                            deactivateAbilityAction.setAbility(ability);
                            popup.add( deactivateAbilityAction );
                        } else {
                            forceDeactivateAbilityAction.setAbility(ability);
                            popup.add( forceDeactivateAbilityAction );
                        }
                    }
                    
                    ability.invokeMenu(popup);
                    
                    if ( (ability.isManeuver() == false || ability.getPower() instanceof maneuverMoveThrough)
                            && (ability.can("USEMELEEMANEUVER") || ability.can("USERANGEDMANEUVER") ) ) {
                        JMenu menu = createManeuverMenu();
                        popup.add(menu);
                    }
                    
                    popup.add( new JSeparator() );
                    
                    
                }
                
                editAction.setAbility(ability);
                popup.add(editAction);
                rv = true;
                
                createTemplateAction.setAbility(ability);
                popup.add(createTemplateAction);
                rv = true;
                
                if ( ! ability.isCurrentInstance() ) {
                    setCurrentInstanceAction.setAbility(ability);
                    popup.add(setCurrentInstanceAction);
                    rv = true;
                }
                
                //editCurrentAction.setEnabled( ability.getInstanceGroup().getBaseInstance() != ability.getInstanceGroup().getCurrentInstance() );
                //popup.add( editCurrentAction ) ;
                
                deleteAction.setAbility(ability);
                deleteAction.setAbilityList(abilityList);
                popup.add(deleteAction);
                rv = true;
                
                if (Battle.debugLevel >= 1) {
                    debugAction.setAbility(ability);
                    popup.add( debugAction );
                }
                
                saveAction.setAbility(ability);
                popup.add( saveAction );
                rv = true;
                
                if ( ability.invokeMenu(popup) ) rv = true;
            }
        }
        return rv;
    }
    
    public JMenu createManeuverMenu() {
        JMenu menu = new MyMenu2("Maneuvers");
        if ( ability == null ) return menu;
        
        AbilityIterator iterator;
        Ability a;
        Action action;
        int mcount = 0;
        
        Target source = ability.getSource();
        
        iterator = source.getAbilities();
        while ( iterator.hasNext() ) {
            a = iterator.nextAbility();
            if ( (a.is("MELEEMANEUVER") && ability.can("USEMELEEMANEUVER"))
            || (a.is("RANGEDMANEUVER") && ability.can("USERANGEDMANEUVER"))) {
                action = new ManeuverAction(ability,a, source);
                JMenuItem mi = new JMenuItem(action);
                mi.enableInputMethods(false);
                menu.add(mi);
                mcount ++;
            }
        }
        
        AbilityList defaults = Battle.getDefaultAbilitiesOld();
        iterator = defaults.getAbilities();
        while ( iterator.hasNext() ) {
            a = iterator.nextAbility();
            if ( (a.is("MELEEMANEUVER") && ability.can("USEMELEEMANEUVER"))
            || (a.is("RANGEDMANEUVER") && ability.can("USERANGEDMANEUVER"))) {
                action = new ManeuverAction(ability,a, source);
                JMenuItem mi = new JMenuItem(action);
                mi.enableInputMethods(false);
                menu.add(mi);
                mcount ++;
            }
        }
        
        if ( mcount == 0 ) {
            JMenuItem mi = new JMenuItem("NONE");
            mi.enableInputMethods(false);
            mi.setEnabled(false);
            menu.add(mi);
        }
        
        menu.enableInputMethods(false);
        return menu;
    }
    
    /** Gets the nodes preferred CellEditor for the column <code>columnIndex</code>.
     *
     * Returns the Nodes preferred CellEditor for the indicated column.  Null can be returned to
     * indicate that a default editor can be used.
     *
     * @param columnIndex The Index of the column being rendered.
     * @return The TreeTableCellEditor to use editing the indicated column of this node.
     */
//    public TreeTableCellEditor getTreeTableCellEditor(int columnIndex) {
//        if ( columnIndex == ATColumns.NAME_COLUMN ) {
//            if ( editor == null ) editor = new AbilityTreeTableCellEditor();
//
//            return editor;
//        }
//        return null;
//    }
    
    /** Gets the nodes preferred CellRenderer for the column <code>columnIndex</code>.
     *
     * Returns the Nodes preferred CellRenderer for the indicated column.  Null can be returned to
     * indicate that a default renderer can be used.
     *
     * @param columnIndex The Index of the column being rendered.
     * @return The TreeTableCellRenderer to use rendering the indicated column of this node.
     */
//    public TreeTableCellRenderer getTreeTableCellRenderer(int columnIndex) {
//        if (  columnIndex != ATColumns.ACTIVATED_COLUMN ) {
//            if ( renderer == null ) renderer = new AbilityTreeTableCellEditor();
//            return renderer;
//        }
//        return null;
//    }
    
    @Override
    public boolean isCellEditable(int columnIndex) {
        return (columnIndex == ATColumn.NAME_COLUMN.ordinal() && !((ATModel)model).isSimpleTree()  && Battle.getCurrentBattle().isProcessing() == false)
        || columnIndex == ATColumn.AUTO_ACTIVATE_COLUMN.ordinal();
    }
    
    /** Sets the ability.
     *
     */
    public void setAbility(Ability ability) {
        setAbility(ability, true);
    }
    
    /** Setter for property ability.
     *
     *  This version allows for manual control of nodeChanged events.
     *
     * @param ability New value of property ability.
     *
     */
    public void setAbility(Ability ability, boolean fireChange) {
        if ( this.ability != ability ) {
            if ( this.ability != null ) {
                this.ability.removePropertyChangeListener("Ability.NAME", this);
                this.ability.removePropertyChangeListener("Ability.INSTANCEDESCRIPTION", this);
                this.ability.removePropertyChangeListener("Ability.CPCOST", this);
                this.ability.removePropertyChangeListener("Ability.ADJUSTED", this);
                
                if ( this.ability.getInstanceGroup() != null) {
                    this.ability.getInstanceGroup().removeAbilityInstanceGroupListener(this);
                }
            }
            
            
            this.ability = ability;
            //buildNode();
            updateNameAndEnd(fireChange);
            
            if ( this.ability != null ) {
                this.ability.addPropertyChangeListener("Ability.NAME", this);
                this.ability.addPropertyChangeListener("Ability.INSTANCEDESCRIPTION", this);
                this.ability.addPropertyChangeListener("Ability.CPCOST", this);
                this.ability.addPropertyChangeListener("Ability.ADJUSTED", this);
                
                if ( this.ability.getInstanceGroup() != null ) {
                    this.ability.getInstanceGroup().addAbilityInstanceGroupListener(this);
                }
            }
        }
        
    }
    
    @Override
    protected void buildNode() {
        boolean structureChanged = false;
        boolean textChanged = false;
        
        if ( getChildCount() > 0 ) {
            removeAndDestroyAllChildren();
            structureChanged = true;
        }
        
        if ( addActions() ) {
            structureChanged = true;
        }
        
        if ( updateEND(false) ) textChanged = true;
        if ( updateName(false) ) textChanged = true;
        
        if ( model instanceof DefaultTreeTableModel ) {
            if ( structureChanged  ) {
                ((DefaultTreeTableModel)model).nodeStructureChanged(this);
            } else if ( textChanged ) {
                ((DefaultTreeTableModel)model).nodeChanged(this);
            }
        }
    }
    
    /** Adds actions available for an ability to the ability list.
     *
     * This method does not remove old actions, so the calling
     * method should take care of that.
     */
    protected boolean addActions() {
        boolean changed = false;
        
        if ( ability != null ) {
            Vector v = ability.getActions(null);

            if ( v != null ) {
                for(int i = 0; i < v.size(); i++) {
                    ATNode node = nodeFactory.createActionNode(ability, (Action)v.get(i), nodeFilter, pruned);
                    if ( node != null ) {
                        add(node);
                        changed = true;
                        abilityHasActions = true;
                    }
                }
            }
        }
        
        return changed;
    }
    
    protected boolean updateNameAndEnd(boolean fireChange) {
        boolean changeOccurred = false;
        
        if ( updateEND(false) ) changeOccurred = true;
        if ( updateName(false) ) changeOccurred = true;
        
        if ( fireChange &&  changeOccurred && model instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)model).nodeChanged(this);
        }
        
        return changeOccurred;
    }
    
    protected boolean updateName(boolean fireChange) {
        boolean changeOccurred = false;
        
        if ( ability == null ) {
            abilityName = "";
        } else {
            String damageString = ability.getDamageString();
            String instanceName = ability.getInstanceName();
            
            if ( damageString != null && damageString.equals("") == false) {
                abilityName = damageString + " " + instanceName;
            } else {
                abilityName = instanceName;
            }
        }
        
        if ( abilityName.equals(getUserObject()) == false ) {
            setUserObject(abilityName);
            changeOccurred = true;
        }
        
        if ( fireChange &&  changeOccurred && model instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)model).nodeChanged(this);
        }
        
        return changeOccurred;
    }
    
    protected boolean updateEND(boolean fireChange) {
        boolean changeOccurred = false;
        
        if (ability == null || ability.getSource() == null) {
            changeOccurred = setEndText("", null);
        } else {
            String primaryENDSource = ability.getPrimaryENDSource() ;
            // Print change information
            Target source = ability.getSource();
            ENDSource esc = source.getENDSource(primaryENDSource);
            if ( esc == null ) {
                changeOccurred = setEndText("", null);
            } else {
                int endCost = ability.getENDCost();
                if ( endCost == 0 ) {
                    changeOccurred = setEndText( "0", null);
                } else {
                    Color color = null;
                    int shots = esc.checkEND(endCost, false);
                    if ( shots == 0 ) {
                        color = Color.RED;
                    }
                    
                    changeOccurred = setEndText( esc.getENDString( endCost ), color);
                }
            }
        }
        
        if ( fireChange &&  changeOccurred && model instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)model).nodeChanged(this);
        }
        
        return changeOccurred;
    }
    
    /**
     * Setter for property endText.
     * @param endText New value of property endText.
     */
    public boolean setEndText(java.lang.String endText, Color color) {
        if ( endText.equals(this.endText) == false || this.endColor != color) {
            this.endText = endText;
            this.endColor = color;
            return true;
        }
        return false;
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        updateNameAndEnd(true);
    }
    
    /** Tells the node to update it's information and the information of it children.
     *
     * This typically will involve only cosmetic changes and not structural
     * changes.  Use it to update cached values that need to change after
     * ability's have been activated/deactivate or the battleEngine has done
     * some processing.
     */
    @Override
    public void updateTree() {
        //icon = createIcon(getTree());
        
        updateEND(true);
        updateIcon(true);
        
        
        super.updateTree();
    }
    
    public void updateIcon(boolean fireChange) {
        
        icon = null;
        if ( fireChange ) {
            Icon newBase = getBaseIcon( getTree() );
            if (  newBase != lastBaseIcon) {
                if ( getModel() instanceof DefaultTreeTableModel ) {
                    ((DefaultTreeTableModel)getModel()).nodeChanged(this);
                }
            }
        }
    }
    
    @Override
    public int getColumnSpan(int modelIndex, TreeTableColumnModel columnModel) {
        int span = 1;
        
        if ( modelIndex == ATColumn.NAME_COLUMN.ordinal() && columnModel != null) {
            // Allow the ability column to expand to take up as much room as possible...
            int columnIndex = columnModel.getColumnIndex(modelIndex);
            if ( columnIndex != -1 ) {
                for(int i = columnIndex+1; i < columnModel.getColumnCount();i++) {
                    int mi = columnModel.getColumn(i).getModelIndex();
                    if (  mi == ATColumn.AUTO_ACTIVATE_COLUMN.ordinal() || mi == ATColumn.END_COLUMN.ordinal()
                    || mi == ATColumn.DCV_COLUMN.ordinal() || mi == ATColumn.OCV_COLUMN.ordinal()  
                    || mi == ATColumn.LAUNCH_COLUMN.ordinal() || mi == ATColumn.RANGE_COLUMN.ordinal()
                    || mi == ATColumn.VPP_CURRENT_ABILITY_STATUS.ordinal() || mi == ATColumn.VPP_ABILITY_ACTION.ordinal()
                    || mi == ATColumn.VPP_ADDITIONAL_INFO.ordinal() ) {
                        break;
                    }
                    span++;
                }
            }
        }
        
        return span;
    }
    
    
    
    /** Getter for property deleteEnabled.
     * @return Value of property deleteEnabled.
     *
     */
    public boolean isDeleteEnabled() {
        return deleteEnabled;
    }
    
    /** Setter for property deleteEnabled.
     * @param deleteEnabled New value of property deleteEnabled.
     *
     */
    public void setDeleteEnabled(boolean deleteEnabled) {
        this.deleteEnabled = deleteEnabled;
    }
    
   
    
    /** Setter for property abilityList.
     * @param abilityList New value of property abilityList.
     *
     */
    public void setAbilityList(AbilityList abilityList) {
        this.abilityList = abilityList;
    }
    /** Tells the Node that it should trigger it's default action.
     *
     */
    @Override
    public void triggerDefaultAction(MouseEvent mouseEvent) {
        if ( defaultActionHandler != null ) defaultActionHandler.handleDefaultAction(mouseEvent);
        
    }
    
    
    
    /** Getter for property abilityList.
     * @return Value of property abilityList.
     *
     */
    public AbilityList getAbilityList() {
        return abilityList;
    }
    
    protected void popupActivateMenu(Point point) {
        JPopupMenu menu = new JPopupMenu("Activate Ability");
        
        if ( ability != null && ability.getSource() != null   && ability.isActivated(ability.getSource()) == false ) {
        
            
            abortToAbilityAction.setAbility(ability);
            abortToAbilityAction.setTarget(ability.getSource());
            menu.add( new JMenuItem( abortToAbilityAction) );
            
            forceActivateAbilityAction.setAbility(ability);
            menu.add( new JMenuItem(forceActivateAbilityAction) );
        
        }
        else { 
            forceDeactivateAbilityAction.setAbility(ability);
            menu.add( new JMenuItem(forceDeactivateAbilityAction) );
        }
        menu.show(getTree(), point.x, point.y);
    }
    
    /** Tells the renderer/editor what color the node thinks is appropriate for the column.
     *
     */
    @Override
    public Color getColumnColor(int column) {
        if ( ability == null ) return null;
        Color c = null;
        if ( column == ATColumn.NAME_COLUMN.ordinal() ) {
            c = ability.getEnableColor();
            if ( c == null ) c = new Color(0,0,238);
        } else if ( column == ATColumn.CP_COLUMN.ordinal() ) {
            
            if ( ability.isAdjustedPositively() ) {
                c = new Color(0,255,0);
            } else if ( ability.isAdjustedNegatively() ) {
                c = new Color(255,0,0);
            }
        } else if ( column == ATColumn.END_COLUMN.ordinal() ) {
            return endColor;
        }
        return c;
    }
    
    @Override
    public boolean isEnabled() {
        if ( ability == null ) return false;
        if ( Battle.getCurrentBattle() == null || (Battle.currentBattle.isProcessing() == true || Battle.currentBattle.isStopped()) )
            return false;
        
        if ( ability.isActivated(ability.getSource()) )
            return true;
        if ( ability.isEnabled(ability.getSource()) )
            return true;
        return false;
        //return true;
    }
    
    /** Returns the Tool text for the node. */
    @Override
    public String getToolTipText(int column) {
        if ( ability == null ) return null;
        
        if ( column == ATColumn.END_COLUMN.ordinal() ) {
            String endSourceName = ability.getPrimaryENDSource();
            Target target = ability.getSource();
            
            if ( endSourceName != null && target != null ) {
                ENDSource s = target.getENDSource(endSourceName);
                if ( s != null ) {
                    String endDesc = s.getENDTooltip( ability.getENDCost() );
                    if ( endDesc != null ) return endDesc;
                }
            }
        } else if ( column == ATColumn.AUTO_ACTIVATE_COLUMN.ordinal() ) {
            if ( ability.isNormallyOn() ) {
                String tooltip = "This ability is marked as auto activate.  " +
                        "It will be activated at the beginning of this characters phase automatically." +
                        "<br><br>Click here to turn auto activate off for this ability.";
                return ChampionsUtilities.createWrappedHTMLString(tooltip, 60);
            } else {
                String tooltip = "This ability is not marked as auto activate.  " +
                        "It will not be activated at the beginning of this characters phase automatically." +
                        "<br><br>Click here to turn auto activate on for this ability.";
                return ChampionsUtilities.createWrappedHTMLString(tooltip, 60);
            }
        }
        
        String tip = "<B>" + ability.getName() + "</B><br><br>" + ability.getDescription();
        
        if ( ability.getEnableMessage() != null ) {
            
            tip += "<br><br>" + ability.getEnableMessage();
        }
        
//        if ( ability.isConfigurationValid() ) {
//            tip += "<br><br><b>Ability Misconfigured</b>:  This ability appears to be misconfigured.  " +
//                    "Right-click on the ability and select \"Edit Ability...\" to correct the " +
//                    "misconfiguration.";
//        }
        
        if ( ability.isAutoSource() ) {
            tip += "<br><br><b>AutoSource</b>:  This ability is auto-sourced.  The source of the ability will " +
                    "always be the currently active character.";
        }
        
        
        return ChampionsUtilities.createWrappedHTMLString(tip, 60);
    }
    
    /**
     * Setter for property endText.
     * @param endText New value of property endText.
     */
//    public void setEndText(java.lang.String endText, Color color) {
//
//        this.endText = endText;
//        this.endColor = color;
//
//        if ( getModel() instanceof DefaultTreeTableModel ) {
//            ((DefaultTreeTableModel)getModel()).nodeChanged(this);
//        }
//    }
    
    @Override
    public TreeTableCellEditor getTreeTableCellEditor(int columnIndex) {
        if ( columnIndex == ATColumn.AUTO_ACTIVATE_COLUMN.ordinal() ) {
            return autoActivateEditor;
        } else if ( abilityRendererEnabled && columnIndex == ATColumn.NAME_COLUMN.ordinal() ) {
            return editor;
        }
        
        return null;
    }
    
    @Override
    public TreeTableCellRenderer getTreeTableCellRenderer(int columnIndex) {
        if ( columnIndex == ATColumn.AUTO_ACTIVATE_COLUMN.ordinal() ) {
            return autoActivateRenderer;
        } else if ( abilityRendererEnabled && columnIndex == ATColumn.NAME_COLUMN.ordinal() ) {
            return renderer;
        }
        
        return null;
    }
    
    public void toggleAutoActivate() {
        if ( ability != null ) {
            ability.setNormallyOn(!ability.isNormallyOn());
        }
    }
    
    @Override
    public void destroy() {
        super.destroy();
        
        setAbilityList(null);
        setAbility(null, false);
    }
    
    @Override
    public void abilityAdded(AbilityAddedEvent evt) {
    }
    
    @Override
    public void abilityRemove(AbilityRemovedEvent evt) {
    }
    
    @Override
    public void instanceChanged(InstanceChangedEvent evt) {
    }
    
    @Override
    public void activationStateChanged(ActivationStateChangeEvent evt) {
        updateNameAndEnd(true);
    }
    
    @Override
    public int getPreferredSortOrder() {
        if ( ability != null && ability instanceof FrameworkAbility ) {
            return -1;
        }
        
        return super.getPreferredSortOrder();
        
    }

    public boolean isAbilityRendererEnabled() {
        return abilityRendererEnabled;
    }

    public void setAbilityRendererEnabled(boolean abilityRendererEnabled) {
        this.abilityRendererEnabled = abilityRendererEnabled;
    }
    
    static protected class EditAction extends AbstractAction {
        private Ability ability;
        public EditAction() {
            super("Edit Ability...");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (ability != null ) {
                ability.editAbility(ability.getName());
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }
    
    static protected class DebugAction extends AbstractAction {
        private Ability ability;
        public DebugAction() {
            super("Debug Ability...");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (ability != null ) {
                ability.debugDetailList( "Ability Debugger" );
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }
    
    static protected class RemoveAbilityAction extends AbstractAction {
        private Ability ability;
        private AbilityList abilityList;
        public RemoveAbilityAction() {
            super("Delete Ability...");
        }
        
        @Override
        public void actionPerformed(ActionEvent e ) {
            
            if ( ability != null && abilityList != null ) {
                int result = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to remove the ability " + ability.getName() + "?",
                        "Remove Ability?",
                        JOptionPane.OK_CANCEL_OPTION);
                
                if ( result == JOptionPane.OK_OPTION ) {
                    
                    //((Target)target).removeAbility(ability);
                    if ( ability.isActivated(ability.getSource()) ) {
                        Iterator<ActivationInfo> it = ability.getActivations( ability.getSource());
                        while(it.hasNext()) {
                            ActivationInfo ai = it.next();
                            BattleEvent be =  new BattleEvent(  BattleEvent.DEACTIVATE, ai);
                            
                            //   be.setShowAttackTree(true);
                            Battle.currentBattle.addEvent(be);
                        }
                    }
                    abilityList.removeAbility(ability);
                }
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
        
        /** Getter for property abilityList.
         * @return Value of property abilityList.
         *
         */
        public AbilityList getAbilityList() {
            return abilityList;
        }
        
        /** Setter for property abilityList.
         * @param abilityList New value of property abilityList.
         *
         */
        public void setAbilityList(AbilityList abilityList) {
            this.abilityList = abilityList;
        }
    }
    
    static protected class ActivateAbilityAction extends AbstractAction {
        private Ability ability;
        public ActivateAbilityAction() {
            super("Activate Ability");
        }
        
        @Override
        public void actionPerformed(ActionEvent e ) {
            
            if ( ability != null ) {
                BattleEvent be = null;
                be = ability.getActivateAbilityBattleEvent(ability, null, null);
                if ( be == null ) be =  new BattleEvent(  ability );
                
                Battle.currentBattle.addEvent( be );
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }
    
    static protected class ForceActivateAbilityAction extends AbstractAction {
        private Ability ability;
        public ForceActivateAbilityAction() {
            super("Force Ability to Activate");
        }
        
        @Override
        public void actionPerformed(ActionEvent e ) {
            
            if ( ability != null ) {
                BattleEvent be = null;
                be = ability.getActivateAbilityBattleEvent(ability, null, null);
                if ( be == null ) be =  new BattleEvent(  ability );
                
                Battle.currentBattle.addEvent( be );
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }
    
    static protected class DeactivateAbilityAction extends AbstractAction {
        private Ability ability;
        public DeactivateAbilityAction() {
            super("Deactivate Ability");
        }
        
        @Override
        public void actionPerformed(ActionEvent e ) {
            
            if ( ability != null ) {
                Iterator<ActivationInfo> it = ability.getActivations( ability.getSource());
                while(it.hasNext()) {
                    ActivationInfo ai = it.next();
                    BattleEvent be = new BattleEvent( BattleEvent.DEACTIVATE, ai);
                    Battle.currentBattle.addEvent(be);
                }
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }
    
    static public class ForceDeactivateAbilityAction extends AbstractAction {
        private Ability ability;
        public ForceDeactivateAbilityAction() {
            super("Force Ability to Deactivate");
        }
        
        @Override
        public void actionPerformed(ActionEvent e ) {
            
            if ( ability != null ) {
                Iterator<ActivationInfo> it = ability.getActivations( ability.getSource());
                while(it.hasNext()) {
                    ActivationInfo ai = it.next();
                    BattleEvent be =  new BattleEvent(  BattleEvent.DEACTIVATE, ai);
                    Battle.currentBattle.addEvent(be);
                }
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }
    
    static public class CancelActivateAbilityAction extends AbstractAction {
        private Ability ability;
        public CancelActivateAbilityAction() {
            super("Cancel Delayed Activation");
        }
        
        @Override
        public void actionPerformed(ActionEvent e ) {
            
            if ( ability != null ) {
                Iterator<ActivationInfo> it = ability.getActivations( ability.getSource());
                while(it.hasNext()) {
                    ActivationInfo ai = it.next();
                    BattleEvent newEvent = new BattleEvent(BattleEvent.DEACTIVATE, ai);
                    Battle.getCurrentBattle().addEvent(newEvent);
                }
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }
    
    static public class SaveAction extends AbstractAction {
        private Ability ability;
        public SaveAction() {
            super("Save Ability As...");
        }
        
        @Override
        public void actionPerformed(ActionEvent e ) {
            
            if (ability != null ) {
                try {
                    ability.save(null);
                } catch (Exception exc) {
                    JOptionPane.showMessageDialog(null,
                            "An Error Occurred while saving:\n" +
                            exc.toString(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }
    
    static public class ManeuverAction extends AbstractAction {
        Ability ability;
        Ability maneuver;
        Target source;
        public ManeuverAction(Ability ability, Ability maneuver, Target source ) {
            this.ability = ability;
            this.maneuver = maneuver;
            this.source = source;
            
            putValue( Action.NAME,  ability.getNameWithDamage(maneuver) );
        }
        
        @Override
        public void actionPerformed(ActionEvent evt) {
            if ( ability != null && maneuver != null ) {
                BattleEvent be = null;
                be = maneuver.getActivateManeuverBattleEvent(ability, maneuver, null);
                if ( be == null ) be = ability.getActivateAbilityBattleEvent(ability, maneuver, null);
                
                if ( be != null ) Battle.currentBattle.addEvent(be);
            }
        }
    }
    
    static public class CreateTemplateAction extends AbstractAction {
        private Ability ability;
        public CreateTemplateAction() {
            super("Create Template...");
        }
        
        @Override
        public void actionPerformed(ActionEvent e ) {
            
            if (ability != null ) {
                Ability newAbility = ability.getInstanceGroup().createNewInstance();
                
                newAbility.setInstanceDescription("100%");
                newAbility.getInstanceGroup().setCurrentInstance(newAbility);
                newAbility.editAbility(newAbility.getName());
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }
    
    static public class SetCurrentInstanceAction extends AbstractAction {
        private Ability ability;
        public SetCurrentInstanceAction() {
            super("Set Current Template");
        }
        
        @Override
        public void actionPerformed(ActionEvent e ) {
            
            if (ability != null ) {
                ability.getInstanceGroup().setCurrentInstance(ability);
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }
    
    static public class AbortToAbilityAction extends AbstractAction {
        private Ability ability;
        private Target target;
        
        public AbortToAbilityAction() {
            super("Abort to Ability");
        }
        
        @Override
        public void actionPerformed(ActionEvent e ) {
            
            if ( ability != null && target != null && target.isAbortable()) {
                BattleEvent be = new BattleEvent(BattleEvent.ACTIVE_TARGET, target);
                be.addCombatStateEvent(target, target.getCombatState(), CombatState.STATE_ABORTING );
                target.setCombatState( CombatState.STATE_ABORTING );
                Battle.currentBattle.addEvent(be);
                
                be = null;
                be = ability.getActivateAbilityBattleEvent(ability, null, null);
                if ( e != null && (e.getModifiers() & Event.SHIFT_MASK) != 0 ) {
                    be.setAlwaysShowAttackTree(true);
                }

                if ( be == null ) be =  new BattleEvent(  ability );

                Battle.currentBattle.addEvent( be );
            }
        }
        
        @Override
        public boolean isEnabled() {
            return ability != null && target != null  && target.isAbortable() && ability.isActivated(target) == false;
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
            putValue(Action.NAME, "Abort to " + ability.getName());
        }
        
        public void setTarget(Target target) {
            this.target = target;
        }
    }
    
    public interface ATAbilityDefaultActionHandler {
        
        public void handleDefaultAction(MouseEvent e);
    }
    
}
