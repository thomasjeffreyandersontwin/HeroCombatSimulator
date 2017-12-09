/*
 * AbilityNode.java
 *
 * Created on June 11, 2001, 7:32 PM
 */
package champions.abilityTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.Battle;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.DetailList;
import tjava.ObjectTransferable;
import champions.Power;
import champions.SpecialEffect;
import champions.Target;
import champions.event.AbilityAddedEvent;
import champions.event.AbilityRemovedEvent;
import champions.event.ActivationStateChangeEvent;
import champions.event.InstanceChangedEvent;
import champions.event.PADValueEvent;
import champions.interfaces.AbilityInstanceGroupListener;
import champions.interfaces.AbilityList;
import champions.interfaces.Advantage;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.Limitation;
import champions.interfaces.PAD;
import champions.interfaces.PADValueListener;
import champions.interfaces.SpecialParameter;
import champions.parameterEditor.AbstractParameterEditor;
import champions.parameters.Parameter;
import champions.parameters.ParameterList;
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
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import tjava.GlobalClipboard;
import tjava.SharedPopupAction;
import treeTable.DefaultTreeTableCellRenderer;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableCellRenderer;

/**
 *
 * @author  twalker
 * @version
 */
public class AbilityNode extends AbilityTreeNode
        implements PropertyChangeListener, PADValueListener, ChampionsConstants, AbilityInstanceGroupListener {

    public static Object[][] parameterArray = {
        {"Name", "Ability.NAME", String.class, "(UNNAMED)", "Name", STRING_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "FIELDWIDTH", new Integer(200)}, //    {"SpecialEffect","Ability.SPECIALEFFECT", String.class, "", "Special Effect", STRING_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "FIELDWIDTH", new Integer(150)}
    };
    public static Object[][] parameterArrayWithInstance = {
        //{"Name","Ability.NAME", String.class, "(UNNAMED)", "Name", STRING_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "FIELDWIDTH", new Integer(200)},
        {"Instance", "Ability.INSTANCEDESCRIPTION", String.class, "", "Instance Name", STRING_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "FIELDWIDTH", new Integer(200)}, //    {"SpecialEffect","Ability.SPECIALEFFECT", String.class, "", "Special Effect", STRING_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "FIELDWIDTH", new Integer(150)}
    };
    /** Holds value of property source. */
    protected DetailList source;
    /** Holds value of property ability. */
    protected Ability ability;
    // Hold Data Flavors
    static protected DataFlavor powerFlavor,  advantageFlavor,  limitationFlavor,  specialEffectFlavor,  specialParameterFlavor,  abilityFlavor;
    //  private static DefaultParameterEditor editor;
    /** Holds value of property parameterList.
     * The parameterList holds a ParameterList which contains information on the name/special effects of the ability
     */
    protected ParameterList parameterList;
    /** Holds value of property power. */
    protected Power power;
    /** Holds value of property powerParameterList.
     * The powerParameterList holds the parameter necessary to configure the power.
     */
    protected ParameterList powerParameterList;
    /** Indicates Ability can be deleted from list. */
    protected boolean deleteEnabled = true;
    /** Indicates Ability can be activated. */
    protected boolean activateEnabled = true;
    /** Indicates that the save option is enabled. */
    protected boolean saveEnabled = true;
    /** Holds Static Action Object for common actions */
    protected static AbilityNode.SaveAsAction saveAction;
    protected static AbilityNode.RemoveAbilityAction removeAction;
    protected static AbilityNode.RemoveVariationAction removeVariationAction;
    protected static AbilityNode.DebugAction debugAction;
    protected static AbilityNode.ActivateAbilityAction activateAbilityAction;
    protected static AbilityNode.DeactivateAbilityAction deactivateAbilityAction;
    protected static JMenuItem removeMenuItem;
    protected static JMenuItem removeVariationMenuItem;
    protected static JMenuItem saveMenuItem;
    protected static JMenuItem debugMenuItem;
    protected static JMenuItem activateAbilityMenuItem;
    protected static JMenuItem deactivateAbilityMenuItem;
    protected static InputMap inputMap;
    protected static ActionMap actionMap;
    protected static DefaultTreeTableCellRenderer ptsRenderer;
    static private AutoActivateTreeTableCellEditor autoActivateEditor;
    static private AutoActivateTreeTableCellEditor autoActivateRenderer;
    // private static AbilityNode.CopyAction copyAction;

    /** Creates new AbilityNode */
    public AbilityNode(AbilityTreeTableModel model, DetailList source, MutableTreeNode parent, Ability ability) {
        setModel(model);
        setSource(source);
        setParent(parent);
        setAbility(ability);

        setupFlavors();

        setupActions();

        setupKeyBindings();

        setupRenderer();

        updateChildren();

        setExpandDuringDrag(false);
    }

    protected void setupFlavors() {
        try {
            if (abilityFlavor == null) {
                abilityFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Ability.class.getName());
            }
            if (powerFlavor == null) {
                powerFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Power.class.getName());
            }
            if (advantageFlavor == null) {
                advantageFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Advantage.class.getName());
            }
            if (limitationFlavor == null) {
                limitationFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=" + Limitation.class.getName());
            }
            if (specialEffectFlavor == null) {
                specialEffectFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=" + SpecialEffect.class.getName());
            }
            if (specialParameterFlavor == null) {
                specialParameterFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=" + SpecialParameter.class.getName());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setupActions() {
        if (saveAction == null) {
            saveAction = new SaveAsAction();
            saveMenuItem = new JMenuItem(saveAction);
        //saveMenuItem.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_DELETE ) );
        }
        if (debugAction == null) {
            debugAction = new DebugAction();
            debugMenuItem = new JMenuItem(debugAction);
            debugMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK));
        }
        if (removeAction == null) {
            removeAction = new RemoveAbilityAction();
            removeMenuItem = new JMenuItem(removeAction);
            removeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        }
        if (removeVariationAction == null) {
            removeVariationAction = new RemoveVariationAction();
            removeVariationMenuItem = new JMenuItem(removeVariationAction);
        //removeVariationMenuItem.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_DELETE ,0) );
        }
        if (activateAbilityAction == null) {
            activateAbilityAction = new ActivateAbilityAction();
            activateAbilityMenuItem = new JMenuItem(activateAbilityAction);
        //activateAbilityMenuItem.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_DELETE ) );
        }
        if (deactivateAbilityAction == null) {
            deactivateAbilityAction = new DeactivateAbilityAction();
            deactivateAbilityMenuItem = new JMenuItem(deactivateAbilityAction);
        //deactivateAbilityMenuItem.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_DELETE ) );
        }
    }

    public void setupKeyBindings() {
        if (inputMap == null) {
            inputMap = new InputMap();
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "RemoveAbility");
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "RemoveAbility");
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK), "DebugAbility");
        }

        if (actionMap == null) {
            actionMap = new ActionMap();
            actionMap.put("RemoveAbility", removeAction);
            actionMap.put("DebugAbility", debugAction);
        }

    }

    public void setupRenderer() {
        if (ptsRenderer == null) {
            ptsRenderer = new DefaultTreeTableCellRenderer();
            ptsRenderer.setRendererFont(UIManager.getFont("AbilityTree.abilityCPFont"));
            ptsRenderer.setHorizontalAlignment(SwingConstants.TRAILING);
        }


        if (autoActivateEditor == null) {
            autoActivateEditor = new AutoActivateTreeTableCellEditor();
        }
        if (autoActivateRenderer == null) {
            autoActivateRenderer = new AutoActivateTreeTableCellEditor();
        }

    }

    /** Getter for property source.
     * @return Value of property source.
     */
    public DetailList getSource() {
        return source;
    }

    /** Setter for property source.
     * @param source New value of property source.
     */
    public void setSource(DetailList source) {
        this.source = source;
    }

    /** Getter for property ability.
     * @return Value of property ability.
     */
    public Ability getAbility() {
        return ability;
    }

    /** Setter for property ability.
     * @param ability New value of property ability.
     */
    public void setAbility(Ability ability) {
        if (ability != this.ability) {
            if (this.ability != null) {
                this.ability.removePropertyChangeListener(this);
                this.ability.getInstanceGroup().removeAbilityInstanceGroupListener(this);
            }

            this.ability = ability;


            if (this.ability != null) {
                if (ability.getInstanceGroup() == null || !ability.isModifiableInstance()) {
                    ParameterList pl = new ParameterList(parameterArray);
                    pl.setParameterValue("Name", ability.getName());
                    setParameterList(pl);
                } else {
                    ParameterList pl = new ParameterList(parameterArrayWithInstance);
                    pl.setParameterValue("Instance", ability.getInstanceName());
                    setParameterList(pl);
                }
                setPower(ability.getPower());
                this.ability.addPropertyChangeListener(this);
                this.ability.getInstanceGroup().addAbilityInstanceGroupListener(this);
            } else {
                setParameterList(null);
            }
        }
    }

    /** Getter for property power.
     * @return Value of property power.
     */
    public Power getPower() {
        return power;
    }

    /** Setter for property power.
     * @param power New value of property power.
     */
    public void setPower(Power power) {
        if (this.power != power) {
            this.power = power;
            if (ability != null && power != null) {
                setPowerParameterList(power.getParameterList(ability, -1));
            } else {
                setPowerParameterList(null);
            }
        }
    }

    /** Getter for property powerParameterList.
     * @return Value of property powerParameterList.
     */
    public ParameterList getPowerParameterList() {
        return powerParameterList;
    }

    /** Setter for property powerParameterList.
     * @param powerParameterList New value of property powerParameterList.
     */
    public void setPowerParameterList(ParameterList powerParameterList) {
        if (this.powerParameterList != powerParameterList) {
            if (this.powerParameterList != null) {
                this.powerParameterList.removePropertyChangeListener(this);
            }
            this.powerParameterList = powerParameterList;
            if (this.powerParameterList != null) {
                this.powerParameterList.addPropertyChangeListener(this);
            }
        }
    }

    /** Getter for property powerParameterList.
     * @return Value of property powerParameterList.
     */
    public ParameterList getParameterList() {
        return parameterList;
    }

    /** Setter for property powerParameterList.
     * @param powerParameterList New value of property powerParameterList.
     */
    public void setParameterList(ParameterList parameterList) {
        if (this.parameterList != parameterList) {
            if (this.parameterList != null) {
                this.parameterList.removePropertyChangeListener(this);
            }

            this.parameterList = parameterList;
            updateChildren();

            if (this.parameterList != null) {
                this.parameterList.addPropertyChangeListener(this);
            }
        }
    }

    public Icon getIcon(JTree tree, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        return (power != null) ? power.getIcon() : null;
    }

    public String toString() {
        if (getAbility() != null) {
            String name = getAbility().getName();

            String instanceDesc = getAbility().getInstanceDescription();
            if (instanceDesc != null && instanceDesc.equals("") == false) {
                if (getAbility().isVariationInstance()) {
                    name = getAbility().getInstanceDescription();
                } else {
                    name += "(" + getAbility().getInstanceDescription() + ")";
                }
            }
            return name;
        } else {
            return "";
        }
    }

    public TreeCellEditor getTreeCellEditor(JTree tree) {
        //   parameterList.setParameterValue( "Name", getAbility().getName() );
        //   editor.setParameterList(parameterList);

        //   return editor;
        return null;
    }

    public TreeCellRenderer getTreeCellRenderer(JTree tree) {
        return null;
    }

    public int getNodeStatus() {
        if (getAbility() != null) {
            if (getAbility().getPower() == null) {
                return CRITICAL_STATUS;
            }
        }
        return OKAY_STATUS;
    }

    /** Notifies Node of a drop event occuring in or below the node.
     * The Node can use the handleDrop method to perform actions based on the drop of a DetailList on
     * the Node Jtree.  If the node wishes to handles the event, it should call acceptDrop and dropComplete
     * on the event, then return true to indicate the event was handled.
     * @returns
     */
    public boolean handleDrop(JTree tree, TreePath dropPath, DropTargetDropEvent event) {
        try {
            // System.out.println("Got drop with path: " + dropPath);

            Transferable tr = event.getTransferable();
            PAD pad = null;
            if (event.isDataFlavorSupported(advantageFlavor)) {
                pad = (PAD) tr.getTransferData(advantageFlavor);
                if (((Advantage) pad).isUnique() && ability.hasAdvantage((Advantage) pad)) {
                    return false;
                }

                if (pad != null) {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                    ability.addPAD(pad, null);
                    updateName();

                    event.dropComplete(true);
                    return true;
                }
            } else if (event.isDataFlavorSupported(limitationFlavor)) {
                pad = (PAD) tr.getTransferData(limitationFlavor);
                if (((Limitation) pad).isUnique() && ability.hasLimitation(pad.getName())) {
                    return false;
                }

                if (pad != null) {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                    ability.addPAD(pad, null);
                    updateName();

                    event.dropComplete(true);
                    return true;
                }
            } else if (event.isDataFlavorSupported(powerFlavor) && ability.getPower() == null) {
                pad = (PAD) tr.getTransferData(powerFlavor);

                if (pad != null) {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                    ability.addPAD(pad, null);
                    updateName();

                    event.dropComplete(true);
                    return true;
                }
            } else if (event.isDataFlavorSupported(specialEffectFlavor)) {
                SpecialEffect se = (SpecialEffect) tr.getTransferData(specialEffectFlavor);
                if (se != null && ability.hasSpecialEffect(se.getName()) == false) {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                    ability.addSpecialEffect(se);

                    event.dropComplete(true);
                    return true;
                }
            } else if (event.isDataFlavorSupported(specialParameterFlavor)) {
                SpecialParameter specialParameter = (SpecialParameter) tr.getTransferData(specialParameterFlavor);
                if (((SpecialParameter) specialParameter).isUnique() && ability.hasSpecialParameter((SpecialParameter) specialParameter)) {
                    return false;
                }

                if (specialParameter != null) {
                    event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                    ability.addSpecialParameter(specialParameter);
                    updateName();

                    event.dropComplete(true);
                    return true;
                }
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (UnsupportedFlavorException ufe) {
            ufe.printStackTrace();
        }

        return false;
    }

    public TreePath willHandleDrop(JTree tree, TreePath dropPath, DropTargetDragEvent event) {
        if (event.isDataFlavorSupported(advantageFlavor) || event.isDataFlavorSupported(limitationFlavor) || (event.isDataFlavorSupported(powerFlavor) && ability.getPower() == null) || event.isDataFlavorSupported(specialEffectFlavor) || event.isDataFlavorSupported(specialParameterFlavor)) {
            TreePath tp = new TreePath(this.getPath());
            tp = tp.pathByAddingChild(new DropAsChildPlaceHolder());
            return tp;
        }
        return null;
    }

    public boolean startDrag(AbilityTreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if (getAbility() != null) {
            Point p = dge.getDragOrigin();
            Rectangle bounds = tree.getPathBounds(path);
            Point offset = new Point(p.x - bounds.x, p.y - bounds.y);

            BufferedImage i = tree.buildDragImage(path);
            AbilityTreeTable.startDrag(i, offset);

            dge.startDrag(null, i, offset, new ObjectTransferable(getAbility(), Ability.class), listener);
            return true;
        }
        return false;
    }

    /** Called to destroy the object.  All references should be set to null.
     * Once destroy has been called, the object will not be used anymore.
     */
    public void destroy() {
        // Check the Children...
        if (children != null) {
            Iterator i = children.iterator();
            while (i.hasNext()) {
                Object next = i.next();
                if (next instanceof ParameterNode) {
                    ((ParameterNode) next).getParameterEditor().removePADValueListener(this);
                }
            }
        }

        super.destroy();

        setAbility(null);
        setSource(null);
        setPower(null);
    }

    public void updateName() {
        if (parameterList != null && ability != null) {
            if (parameterList.contains("Name")) {
                parameterList.setParameterValue("Name", ability.getName());
            }
            if (parameterList.contains("Instance") && ability.getInstanceDescription() != null) {
                parameterList.setParameterValue("Instance", ability.getInstanceDescription());
            }
        }
        if (ability != null && model != null) {
            model.nodeChanged(this);
        }
    }

    public void updateChildren() {
        // System.out.println("AbilityNode update Children");
        boolean fireChange = false;
        boolean found;
        // Make sure the abilities in the source match the abilityNodes that are childern
        Vector newChildren = new Vector();
        ArrayList<AbilityTreeNode> nodesAdded = new ArrayList<AbilityTreeNode>();
        int aindex, acount;
        int nindex, ncount;
        int total = 0;
        PAD pad;
        PADNode pn;
        ParameterNode pan;
        //SpecialEffectNode sen;
        String parameterName;


        ncount = (children != null) ? children.size() : 0;

        if (ability == null || parameterList == null) {
            if (children != null) {
                children.clear();
            }
            return;
        }

        boolean variation = ability.isVariationInstance();

        // Add the name/special effect parameter Nodes
        //acount = parameterList.getIndexedSize("parameter");
        //for (aindex = 0; aindex < acount; aindex++) {
        Iterator<Parameter> it = parameterList.getParameters();
        while (it.hasNext()) {
            Parameter p = it.next();
            parameterName = p.getName();
            if (parameterList.isVisible(parameterName) == false) {
                continue;
            }
            found = false;
            for (nindex = 0; nindex < ncount; nindex++) {
                if (children.get(nindex) instanceof ParameterNode) {
                    pan = (ParameterNode) children.get(nindex);
                    if (pan.getParameterList() == parameterList && pan.getParameter().equals(parameterName)) {
                        found = true;
                        // Move the ability node from the childern list to the newChildern list
                        newChildren.add(pan);
                        if (nindex != total) {
                            fireChange = true;
                        }
                        children.set(nindex, null);
                        break;
                    }
                }
            }


            if (found == false) {
                pan = new ParameterNode(model, ability, parameterList, this, parameterName);

                pan.setTree(tree);
                pan.getParameterEditor().addPADValueListener(this);
                newChildren.add(pan);
                fireChange = true;
            }
            total++;
        }

        // Check that the Special Parameter Node is there
        // Try to find it in the children array.
        if (variation == false || ability.hasAdvantage("Variable Special Effect")) {
            SpecialEffectsNode sen;
            found = false;
            for (nindex = 0; nindex < ncount; nindex++) {
                if (children.get(nindex) instanceof SpecialEffectsNode) {
                    sen = (SpecialEffectsNode) children.get(nindex);
                    if (sen.getAbility() == ability) {
                        found = true;
                        // Move the ability node from the childern list to the newChildern list
                        newChildren.add(sen);
                        if (nindex != total) {
                            fireChange = true;
                        }
                        children.set(nindex, null);
                        break;
                    }
                }
            }

            if (found == false) {
                sen = new SpecialEffectsNode(model, this, ability);
                sen.setTree(tree);
                newChildren.add(sen);
                fireChange = true;
            }
            total++;
        }

        if (variation == false) {
            // Add the Power parameter nodes
            if (powerParameterList != null) {
                it = powerParameterList.getParameters();
                while (it.hasNext()) {
                    Parameter p = it.next();
                    parameterName = p.getName();
                    if (powerParameterList.isVisible(parameterName) == false) {
                        continue;
                    }
                    found = false;
                    for (nindex = 0; nindex < ncount; nindex++) {
                        if (children.get(nindex) instanceof ParameterNode) {
                            pan = (ParameterNode) children.get(nindex);
                            if (pan.getParameterList() == powerParameterList && pan.getParameter().equals(parameterName)) {
                                if (variation) {
                                    pan.setEnabled(false);
                                }
                                found = true;
                                // Move the ability node from the childern list to the newChildern list
                                newChildren.add(pan);
                                if (nindex != total) {
                                    fireChange = true;
                                }
                                children.set(nindex, null);
                                break;
                            }
                        }
                    }


                    if (found == false) {
                        pan = new ParameterNode(model, ability, powerParameterList, this, parameterName);
                        if (variation) {
                            pan.setEnabled(false);
                        }
                        pan.getParameterEditor().addPADValueListener(this);
                        pan.setTree(tree);
                        newChildren.add(pan);
                        fireChange = true;
                    }
                    total++;
                }
            }



        } // End of Variation if
        else {
            if (ability.hasAdvantage("Variable Special Effect") && powerParameterList != null) {
                String defenseParameterName = powerParameterList.findParameterKey("Power.DEFENSE");

                if (defenseParameterName != null && powerParameterList.isVisible(defenseParameterName)) {
                    found = false;
                    for (nindex = 0; nindex < ncount; nindex++) {
                        if (children.get(nindex) instanceof ParameterNode) {
                            pan = (ParameterNode) children.get(nindex);
                            if (pan.getParameterList() == powerParameterList && pan.getParameter().equals(defenseParameterName)) {
                                found = true;
                                // Move the ability node from the childern list to the newChildern list
                                newChildren.add(pan);
                                if (nindex != total) {
                                    fireChange = true;
                                }
                                children.set(nindex, null);
                                break;
                            }
                        }
                    }


                    if (found == false) {
                        pan = new ParameterNode(model, ability, powerParameterList, this, defenseParameterName);

                        pan.setTree(tree);
                        pan.getParameterEditor().addPADValueListener(this);
                        newChildren.add(pan);
                        fireChange = true;
                    }
                    total++;
                }
            }
        }

        if (variation && ability.hasAdvantage("Variable Advantage")) {

            VariableAdvantageNode va;
            int vaIndex = ability.findAdvantage("Variable Advantage");

            found = false;
            for (nindex = 0; nindex < ncount; nindex++) {
                if (children.get(nindex) instanceof VariableAdvantageNode) {
                    va = (VariableAdvantageNode) children.get(nindex);
                    found = true;
                    // Move the ability node from the childern list to the newChildern list
                    newChildren.add(va);
                    if (nindex != total) {
                        fireChange = true;
                    }
                    children.set(nindex, null);
                    break;
                }
            }

            if (found == false) {
                va = new VariableAdvantageNode(model, this, ability, vaIndex);
                va.setTree(tree);
                newChildren.add(va);
                nodesAdded.add(va);
                fireChange = true;
            }
            total++;
        }

        // Check for all the advantages
        acount = ability.getAdvantageCount();
        for (aindex = 0; aindex < acount; aindex++) {
            boolean addIt = true;
            pad = ability.getAdvantage(aindex);
            // Try to find it in the children array.

            if (variation) {
                Ability parent = ability.getParentAbility();
                for (int pindex = 0; pindex < parent.getAdvantageCount(); pindex++) {
                    if (parent.getAdvantage(pindex).getParameterList(parent, pindex) == pad.getParameterList(ability, aindex).getParent()) {
                        // This is a copy form the parent, so ignore it...
                        addIt = false;
                        break;
                    }
                }
            }



            if (addIt) {
                found = false;
                for (nindex = 0; nindex < ncount; nindex++) {
                    if (children.get(nindex) instanceof PADNode) {
                        pn = (PADNode) children.get(nindex);
                        if (pn.getPad() == pad) {
                            found = true;
                            // Move the ability node from the childern list to the newChildern list
                            newChildren.add(pn);
                            if (nindex != total) {
                                fireChange = true;
                            }
                            children.set(nindex, null);
                            break;
                        }
                    }
                }

                if (found == false) {
                    pn = new PADNode(model, ability, this, pad, aindex);
                    pn.setTree(tree);
                    newChildren.add(pn);
                    nodesAdded.add(pn);
                    fireChange = true;
                }
                total++;
            }
        }

        // Check for all the limitations
        acount = ability.getLimitationCount();
        for (aindex = 0; aindex < acount; aindex++) {
            boolean addIt = true;
            pad = (PAD) ability.getLimitation(aindex);
            // Try to find it in the children array.
            if (variation) {
                Ability parent = ability.getParentAbility();
                for (int pindex = 0; pindex < parent.getLimitationCount(); pindex++) {
                    if (parent.getLimitation(pindex).getParameterList(parent, pindex) == pad.getParameterList(ability, aindex)) {
                        // This is a copy form the parent, so ignore it...
                        addIt = false;
                        break;
                    }
                }

            }


            if (addIt) {
                found = false;
                for (nindex = 0; nindex < ncount; nindex++) {
                    if (children.get(nindex) instanceof PADNode) {
                        pn = (PADNode) children.get(nindex);
                        if (pn.getPad() == pad) {
                            found = true;
                            // Move the ability node from the childern list to the newChildern list
                            newChildren.add(pn);
                            if (nindex != total) {
                                fireChange = true;
                            }
                            children.set(nindex, null);
                            break;
                        }
                    }
                }

                if (found == false) {
                    pn = new PADNode(model, ability, this, pad, aindex);
                    pn.setTree(tree);
                    newChildren.add(pn);
                    nodesAdded.add(pn);
                    fireChange = true;
                }
                total++;
            }
        }

        if (variation == false) {
            // Check for all the SpecialParameter
            SpecialParameter specialParameter;
            SpecialParameterNode spn;
            acount = ability.getIndexedSize("SpecialParameter");
            for (aindex = 0; aindex < acount; aindex++) {
                specialParameter = (SpecialParameter) ability.getIndexedValue(aindex, "SpecialParameter", "SPECIALPARAMETER");
                // Try to find it in the children array.
                found = false;
                for (nindex = 0; nindex < ncount; nindex++) {
                    if (children.get(nindex) instanceof SpecialParameterNode) {
                        spn = (SpecialParameterNode) children.get(nindex);
                        if (spn.getSpecialParameter() == specialParameter) {
                            found = true;
                            // Move the ability node from the childern list to the newChildern list
                            newChildren.add(spn);
                            if (nindex != total) {
                                fireChange = true;
                            }
                            children.set(nindex, null);
                            break;
                        }
                    }
                }

                if (found == false) {
                    spn = new SpecialParameterNode(model, ability, this, specialParameter, aindex);
                    spn.setTree(tree);
                    newChildren.add(spn);
                    fireChange = true;
                }
                total++;
            }
        }

        // Check if the ability has a variable advantage or variable effect...
        if (variation == false && (ability.hasAdvantage("Variable Advantage") || ability.hasAdvantage("Variable Special Effect"))) {
            // add the appropriate variable advantage nodes
            if (ability.getInstanceGroup().getVariationCount() == 0) {
                // There are no variations, so make sure a CreateVariableInstanceNode exists
                found = false;
                for (nindex = 0; nindex < ncount; nindex++) {
                    if (children.get(nindex) instanceof CreateVariableInstanceNode) {
                        CreateVariableInstanceNode cvin = (CreateVariableInstanceNode) children.get(nindex);
                        found = true;
                        newChildren.add(cvin);
                        if (nindex != total) {
                            fireChange = true;
                        }
                        children.set(nindex, null);
                        break;
                    }
                }

                if (found == false) {
                    CreateVariableInstanceNode cvin = new CreateVariableInstanceNode(model, this, ability);
                    cvin.setTree(tree);
                    newChildren.add(cvin);
                    fireChange = true;
                }
                total++;
            } else {
                found = false;
                for (nindex = 0; nindex < ncount; nindex++) {
                    if (children.get(nindex) instanceof VariationListNode) {
                        VariationListNode cvin = (VariationListNode) children.get(nindex);
                        found = true;
                        newChildren.add(cvin);
                        if (nindex != total) {
                            fireChange = true;
                        }
                        children.set(nindex, null);
                        break;
                    }
                }

                if (found == false) {
                    VariationListNode cvin = new VariationListNode(model, ability.getInstanceGroup(), this);
                    cvin.setTree(tree);
                    newChildren.add(cvin);
                    fireChange = true;
                }
                total++;
            }
        }

        Vector oldChildren = children;
        children = newChildren;

        // Now that everything is done, anything level not-null in oldChildren should be destroyed
        // and references to it released.
        if (oldChildren != null) {
            for (nindex = 0; nindex < oldChildren.size(); nindex++) {
                if (oldChildren.get(nindex) != null) {
                    ((AbilityTreeNode) oldChildren.get(nindex)).destroy();
                    oldChildren.set(nindex, null);
                    fireChange = true;
                }
            }
        }

        if (fireChange && model != null) {
            model.nodeStructureChanged(this);

            if (tree != null && tree.isExpanded(new TreePath(this.getPath()))) {
                AbilityTreeNode child;
                TreePath tp;

                int index;
                for (index = 0; index < this.getChildCount(); index++) {
                    child = (AbilityTreeNode) this.getChildAt(index);
                    if (child instanceof SpecialParameterNode || child instanceof SpecialEffectsNode) {
                        continue;
                    }
                    tp = new TreePath(child.getPath());
                    tree.expandPath(tp);
                }

                if (nodesAdded.size() == 1) {
                    tp = new TreePath(nodesAdded.get(0).getPath());
                    tree.setSelectionPath(tp);
                    nodesAdded.get(0).scrollToVisible();
                }
            }
        }
    }

    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();

        if (evt.getSource() == ability) {
            if (property.equals("Advantage.INDEXSIZE")) {
                updateChildren();
            } else if (property.equals("Limitation.INDEXSIZE")) {
                updateChildren();
            } else if (property.equals("Ability.NAME")) {
                updateName();
            } else if (property.equals("Ability.INSTANCEDESCRIPTION")) {
                updateName();
            } else if (property.equals("Ability.CPCOST")) {
                updateName();
            } else if (property.startsWith("SpecialEffect")) {
                updateChildren();
            } else if (property.startsWith("SpecialParameter.INDEXSIZE")) {
                updateChildren();
            }
        } else if (evt.getSource() == parameterList) {
            if (evt.getPropertyName().equals("Name.VALUE")) {
                ability.setName((String) evt.getNewValue());
            }
            if (evt.getPropertyName().equals("Instance.VALUE")) {
                ability.setInstanceDescription((String) evt.getNewValue());
            }
        } else if (evt.getSource() == powerParameterList && evt.getPropertyName().endsWith("VISIBLE")) {
            updateChildren();
        }
    }

    public void PADValueChanged(PADValueEvent evt) {
        ParameterList pl = ((AbstractParameterEditor) evt.getSource()).getParameterList();
        if (pl == parameterList) {
            String key = evt.getKey();
            if (key.equals("Ability.NAME")) {
                getAbility().setName((String) evt.getValue());
            } else if (key.equals("Ability.INSTANCEDESCRIPTION")) {
                getAbility().setInstanceDescription((String) evt.getValue());
            } else {
                getAbility().add(key, evt.getValue(), true);
            }
        } else if (pl == powerParameterList) {
            ability.reconfigurePower();
            ability.calculateMultiplier();
            ability.calculateCPCost();

            updateName();
        }
    }

    public boolean PADValueChanging(PADValueEvent evt) {
        ParameterList pl = ((AbstractParameterEditor) evt.getSource()).getParameterList();
        if (pl == parameterList) {
            // Should check if name is unique
            return true;
        } else if (pl == powerParameterList) {
            return power.checkParameter(ability, -1, evt.getKey(), evt.getValue(), evt.getOldValue());
        }
        return true;
    }

    public boolean invokeMenu(JPopupMenu popup, JTree tree, TreePath path) {

        boolean last = (path.getLastPathComponent() == this);
        boolean variation = ability.isVariationInstance();

        Target abilitySource = ability.getSource();
        if (last && abilitySource != null) {
            if (isActivateEnabled()) {
                if (ability.isActivated(abilitySource) == false) {
                    if (ability.isEnabled(null) == true) {
                        activateAbilityAction.setAbility(ability);
                        activateAbilityMenuItem.setEnabled(activateAbilityAction.isEnabled());
                        popup.add(activateAbilityMenuItem);
                    } else {
                        activateAbilityAction.setAbility(ability);
                        activateAbilityMenuItem.setEnabled(activateAbilityAction.isEnabled());
                        popup.add(activateAbilityMenuItem);
                    }
                } else {
                    if (ability.isEnabled(null) == true) {
                        deactivateAbilityAction.setAbility(ability);
                        deactivateAbilityMenuItem.setEnabled(deactivateAbilityAction.isEnabled());
                        popup.add(deactivateAbilityMenuItem);
                    } else {
                        deactivateAbilityAction.setAbility(ability);
                        deactivateAbilityMenuItem.setEnabled(deactivateAbilityAction.isEnabled());
                        popup.add(deactivateAbilityMenuItem);
                    }
                }
            }
        }

        //removeAction.setTarget(source);
        if (isDeleteEnabled()) {
            if (variation) {
                removeVariationAction.setAbility(ability);
                removeVariationMenuItem.setEnabled(removeVariationAction.isEnabled());
                popup.add(removeVariationMenuItem);
            } else {
                removeAction.setAbility(ability);
                removeMenuItem.setEnabled(removeAction.isEnabled());
                popup.add(removeMenuItem);
            }
        }

        if (last && isSaveEnabled() && variation == false) {
            saveAction.setAbility(ability);
            saveMenuItem.setEnabled(saveAction.isEnabled());
            popup.add(saveMenuItem);
        }


        if (last && Battle.debugLevel >= 1) {
            debugAction.setAbility(ability);
            debugMenuItem.setEnabled(debugAction.isEnabled());
            popup.add(debugMenuItem);
        }

        return true;
    }

    public boolean canCopyOrCutNode() {
        return true;
    }

    public void copyOrCutNode(boolean cut) {
        Transferable t = new ObjectTransferable(getAbility(), Ability.class);
        GlobalClipboard.setContents(t, null);

        if (cut && source != null) {
            TreePath path = null;
            JTree oldTree = tree;
            boolean expand = false;

            if (tree != null) {
                TreeNode[] nodes = getPath();

                expand = tree.isExpanded(new TreePath(nodes));

                int index = getParent().getIndex(this) + 1;
                // Select the node below (which will move up one position) by default.
                // Index is prior to the node being removed...
                if (index == getParent().getChildCount()) {
                    // I am last...so select the one above me
                    index = index - 2;
                }

                if (index >= 0) {
                    // If there are nodes left in the abilityList, select it...
                    TreeNode newNode = getParent().getChildAt(index);
                    nodes[nodes.length - 1] = newNode;
                } else {
                    // Just select the parent if we deleted all of the abilities...
                    TreeNode[] newNodes = new TreeNode[nodes.length - 1];
                    for (int i = 0; i < nodes.length - 1; i++) {
                        newNodes[i] = nodes[i];
                    }
                    nodes = newNodes;
                }

                path = new TreePath(nodes);

            }

            if (source instanceof Target) {
                ability.getAbilityList().removeAbility(ability);

            }

            if (path != null) {
                // Use the oldTree we recorded, since the removeAbility destroyed us...
                if (expand) {
                    oldTree.expandPath(path);
                }

                oldTree.setSelectionPath(path);
            }
        }
    }

    // You should be able to paste AbilityLists and Abilities
    public boolean canPasteData(Transferable t) {
        return t != null && t.isDataFlavorSupported(abilityFlavor) && ability.getAbilityList() != null;
    }

    public void pasteData(Transferable t) {
        if (t != null && t.isDataFlavorSupported(abilityFlavor)) {
            Ability newAbility;
            try {
                newAbility = (Ability) t.getTransferData(abilityFlavor);

                AbilityList abilityList = ability.getAbilityList();
                int index = abilityList.getAbilityIndex(ability);

                if (index != -1) {
                    if (tree != null && tree.isEditing()) {
                        tree.stopEditing();
                    }

                    String newName = newAbility.getName();
                    // This is a new ability.
                    newAbility = (Ability) newAbility.clone();

                    if (abilityList.getSource() != null) {
                        newName = abilityList.getSource().getUniqueAbilityName(newAbility);
                    }

                    newAbility.setName(newName);

                    abilityList.addAbility(newAbility, index + 1);
                }

                if (tree != null) {
                    TreeNode[] nodes = getPath();

                    TreeNode newNode = getParent().getChildAt(getParent().getIndex(this) + 1);

                    nodes[nodes.length - 1] = newNode;

                    TreePath path = new TreePath(nodes);
                    tree.expandPath(path);
                    tree.setSelectionPath(path);
                }

            } catch (UnsupportedFlavorException ufe) {
                ufe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

    }

    public TreeTableCellRenderer getTreeTableCellRenderer(int columnIndex) {
        TreeTableCellRenderer renderer = null;

        switch (columnIndex) {
            case AbilityTreeTableModel.ABILITY_TREE_ENDCOLUMN:
            case AbilityTreeTableModel.ABILITY_TREE_PTSCOLUMN:
            case AbilityTreeTableModel.ABILITY_TREE_APCOLUMN:
            case AbilityTreeTableModel.ABILITY_TREE_REALCOLUMN:
                renderer = ptsRenderer;
                break;
            case AbilityTreeTableModel.ABILITY_TREE_AUTOCOLUMN:
                renderer = autoActivateRenderer;
                break;
        }

        return renderer;
    }

    public TreeTableCellEditor getTreeTableCellEditor(int columnIndex) {
        TreeTableCellEditor renderer = null;

        switch (columnIndex) {
            case AbilityTreeTableModel.ABILITY_TREE_AUTOCOLUMN:
                renderer = autoActivateEditor;
                break;
        }

        return renderer;
    }

    public Object getValue(int columnIndex) {
        Object v = null;

        if (ability != null) {
            switch (columnIndex) {
                case AbilityTreeTableModel.ABILITY_TREE_ENDCOLUMN:
                    v = Integer.toString(ability.getENDCost());
                    break;
                case AbilityTreeTableModel.ABILITY_TREE_PTSCOLUMN:
                    v = Integer.toString(ability.getCPCost());
                    break;
                case AbilityTreeTableModel.ABILITY_TREE_APCOLUMN:
                    v = Integer.toString(ability.getAPCost());
                    break;
                case AbilityTreeTableModel.ABILITY_TREE_REALCOLUMN:
                    v = Integer.toString(ability.getRealCost());
                    break;
                case AbilityTreeTableModel.ABILITY_TREE_AUTOCOLUMN:
                    v = ability.isNormallyOn();
            }
        }

        return v;

    }

    /** Returns the Text for the ToolTip for the node which the cursor is currently over.
     */
    public String getToolTipText(int column) {
        if (column == AbilityTreeTableModel.ABILITY_TREE_AUTOCOLUMN) {
            if (ability.isNormallyOn()) {
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
        } else {
            String toolTip = "<B>" + ability.getName() + "</B><P>" + ability.getDescription();
            return ChampionsUtilities.createWrappedHTMLString(toolTip, 40);
        }
    }

    /** Returns the Nodes custom input map.
     * This method should return a custom input map of action the Node can perform and has key bindings for.
     * When this node is selected, the AbilityTree will load this input map and bind the keys appropriately.
     *
     * MenuItems returned by invokeMenu which have key bindings should populate an input map and return it.
     *
     * This is also the point that Actions should be updated to respond to this particular node.
     */
    public InputMap getInputMap() {

        // Setup the Static Action to refer to this particulars nodes configuration
        //removeAction.setTarget(source);
        removeAction.setAbility(ability);
        debugAction.setAbility(ability);

        return inputMap;
    }

    /** Returns the Nodes custom action map.
     * This method should return a custom action map of action the Node can perform and has key bindings for.
     * When this node is selected, the AbilityTree will load this action map and bind the keys appropriately.
     *
     * MenuItems returned by invokeMenu which have key bindings should populate an action map and return it.
     */
    public ActionMap getActionMap() {
        return actionMap;
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

    /** Getter for property activateEnabled.
     * @return Value of property activateEnabled.
     *
     */
    public boolean isActivateEnabled() {
        return activateEnabled;
    }

    public boolean isEditable(int column) {
        switch (column) {
            case AbilityTreeTableModel.ABILITY_TREE_AUTOCOLUMN:
                return true;
        }
        return false;
    }

    /** Setter for property activateEnabled.
     * @param activateEnabled New value of property activateEnabled.
     *
     */
    public void setActivateEnabled(boolean activateEnabled) {
        this.activateEnabled = activateEnabled;
    }

    /** Getter for property saveEnabled.
     * @return Value of property saveEnabled.
     *
     */
    public boolean isSaveEnabled() {
        return saveEnabled;
    }

    /** Setter for property saveEnabled.
     * @param saveEnabled New value of property saveEnabled.
     *
     */
    public void setSaveEnabled(boolean saveEnabled) {
        this.saveEnabled = saveEnabled;
    }

    public void abilityAdded(AbilityAddedEvent evt) {
    }

    public void abilityRemove(AbilityRemovedEvent evt) {
    }

    public void instanceChanged(InstanceChangedEvent evt) {
        if (evt.getType() == InstanceChangedEvent.VARIATION_INSTANCES) {
            updateChildren();
        }
    }

    public void activationStateChanged(ActivationStateChangeEvent evt) {
    }

    public void toggleAutoActivate() {
        ability.setNormallyOn(!ability.isNormallyOn());
    }

    protected static class RemoveAbilityAction extends SharedPopupAction {

        private Ability ability;

        public RemoveAbilityAction() {
            super("Delete Ability");
        }

        public void actionPerformed(ActionEvent e) {

            if (ability != null) {
                int result = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to remove the ability " + ability.getName() + "?",
                        "Remove Ability?",
                        JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    if (ability.isAutoSource() == false && ability.getSource() != null) {
                        // This was previously commented out, so there may be a problem with
                        // this code.  However, I added the code about to make sure that this isn't
                        // an autosourced ability and the target is non-null.
                        ability.getSource().removeAbility(ability);
                    } else {
                        ability.getAbilityList().removeAbility(ability);
                    }
                }
            }
        }

        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }

    protected static class RemoveVariationAction extends SharedPopupAction {

        private Ability ability;

        public RemoveVariationAction() {
            super("Delete Variation of Ability");
        }

        public void actionPerformed(ActionEvent e) {

            if (ability != null) {
                int result = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to remove the variation " + ability.getInstanceDescription() + " of " + ability.getName() + "?",
                        "Remove Variation?",
                        JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    ability.getInstanceGroup().removeInstance(ability);
                //((Target)target).removeAbility(ability);
                }
            }
        }

        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }

    protected static class SaveAsAction extends SharedPopupAction {

        private Ability ability;

        public SaveAsAction() {
            super("Save Ability As...");
        }

        public void actionPerformed(ActionEvent e) {
            if (ability != null) {
                try {
                    Ability clone = (Ability) ability.clone();
                    clone.setSource(null);
                    clone.save(null);
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

    protected static class DebugAction extends SharedPopupAction {

        private Ability ability;

        public DebugAction() {
            super("Debug Ability...");
        }

        public void actionPerformed(ActionEvent e) {
            if (ability != null) {
                ability.debugDetailList("Ability Debugger");
            }
        }

        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }

    protected static class ActivateAbilityAction extends SharedPopupAction {

        private Ability ability;

        public ActivateAbilityAction() {
            super("Activate Ability...");
        }

        public void actionPerformed(ActionEvent e) {
            if (ability != null && Battle.currentBattle != null) {
                BattleEvent be = new BattleEvent(ability);


                Battle.currentBattle.addEvent(be);
            }
        }

        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }

    protected static class DeactivateAbilityAction extends SharedPopupAction {

        private Ability ability;
        private DetailList source;

        public DeactivateAbilityAction() {
            super("Deactivate Ability...");
        }

        public void actionPerformed(ActionEvent e) {
            if (ability != null && Battle.currentBattle != null) {
                ActivationInfo ai;
//                if ( ( ai = ability.findActivationInfo( ability.getSource() ) ) != null ) {
//                    Battle.currentBattle.addEvent( new BattleEvent(  BattleEvent.DEACTIVATE, ai));
//                }
                Iterator<ActivationInfo> it = ability.getActivations(ability.getSource());
                while (it.hasNext()) {
                    Battle.currentBattle.addEvent(new BattleEvent(BattleEvent.DEACTIVATE, it.next()));
                }
            }
        }

        public void setAbility(Ability ability) {
            this.ability = ability;
        }
    }
}
