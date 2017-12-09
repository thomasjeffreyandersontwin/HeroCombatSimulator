/*
 * IntegerParameterEditor.java
 *
 * Created on June 14, 2001, 9:58 AM
 */

package champions.parameterEditor;

import champions.Dice;
import champions.parameters.ParameterList;
import champions.exception.BadDiceException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author  twalker
 * @version
 */
public class DiceParameterEditor extends DefaultParameterEditor {
    
    static private boolean setup = false;
    
    private static JPanel editorPanel;
    private static JTextField editorField;
    private static JButton editorUpButton;
    private static JButton editorDownButton;
    private static JPanel editorButtonPanel;
    
    private static JPanel rendererPanel;
    private static JLabel rendererValueLabel;
    private static JButton rendererUpButton;
    private static JButton rendererDownButton;
    private static JPanel rendererButtonPanel;
    
    static private EditorFocusListener editorFocusListener;
    static private UpButtonActionListener upButtonActionListener;
    static private DownButtonActionListener downButtonActionListener;
    
    /** Creates new IntegerParameterEditor */
    public DiceParameterEditor(ParameterList parameterList,String parameter) {
        super(parameterList,parameter);
        
        setupEditor();
        
        setEditorDelegate( editorPanel );
        setRendererDelegate( rendererPanel );
    }
    
    public void setupEditor() {
        if ( setup == true ) return;
        
        editorPanel = new JPanel();
        editorPanel.setOpaque(false);
        editorPanel.setLayout( new ParameterEditorLayout(SwingConstants.HORIZONTAL) );
        
        editorButtonPanel = new JPanel();
        editorButtonPanel.setOpaque(false);
        editorField = new JTextField();
        editorUpButton = new JButton();
        editorDownButton = new JButton();
        editorButtonPanel.setLayout(new BoxLayout(editorButtonPanel, BoxLayout.Y_AXIS));
        
        editorField.setOpaque(false);
        editorUpButton.setOpaque(false);
        editorDownButton.setOpaque(false);
        
        editorField.setBackground( getBackgroundNonSelectionColor() );
        editorField.setForeground( getTextNonSelectionColor() );
        editorPanel.add(editorField, BorderLayout.CENTER);
        
        editorUpButton.setPreferredSize(new Dimension(13, 8));
        editorUpButton.setMaximumSize(new Dimension(13, 8));
        editorUpButton.setContentAreaFilled(false);
        editorUpButton.setMinimumSize(new Dimension(13, 8));
        editorUpButton.setBorderPainted(false);
        
        editorButtonPanel.add(editorUpButton);
        
        editorDownButton.setPreferredSize(new Dimension(13, 8));
        editorDownButton.setMaximumSize(new Dimension(13, 8));
        editorDownButton.setContentAreaFilled(false);
        editorDownButton.setMinimumSize(new Dimension(13, 8));
        editorDownButton.setBorderPainted(false);
        
        editorButtonPanel.add(editorDownButton);
        
        editorPanel.add(editorButtonPanel,BorderLayout.EAST);
        
        // Setup the Renderer
        rendererPanel = new JPanel();
        rendererPanel.setOpaque(false);
        rendererPanel.setLayout( new ParameterEditorLayout(SwingConstants.HORIZONTAL));
        rendererPanel.setDoubleBuffered(false);
        
        rendererButtonPanel = new JPanel();
        rendererButtonPanel.setDoubleBuffered(false);
        rendererButtonPanel.setOpaque(false);
        
        rendererValueLabel = new JLabel();
        rendererValueLabel.setDoubleBuffered(false);
        
        rendererUpButton = new JButton();
        rendererUpButton.setDoubleBuffered(false);
        
        rendererDownButton = new JButton();
        rendererDownButton.setDoubleBuffered(false);
        
        rendererValueLabel.setOpaque(false);
        rendererUpButton.setOpaque(false);
        rendererDownButton.setOpaque(false);
        
        rendererButtonPanel.setLayout(new BoxLayout(rendererButtonPanel, BoxLayout.Y_AXIS));
        
        rendererValueLabel.setBackground( getBackgroundNonSelectionColor() );
        rendererValueLabel.setForeground( getTextNonSelectionColor() );
        rendererValueLabel.setBorder( new EtchedBorder(Color.white, new Color(102,102,102)) );
        
        rendererPanel.add(rendererValueLabel, BorderLayout.CENTER);
        
        rendererUpButton.setPreferredSize(new Dimension(13, 8));
        rendererUpButton.setMaximumSize(new Dimension(13, 8));
        rendererUpButton.setContentAreaFilled(false);
        rendererUpButton.setMinimumSize(new Dimension(13, 8));
        rendererUpButton.setBorderPainted(false);
        
        rendererButtonPanel.add(rendererUpButton);
        
        rendererDownButton.setPreferredSize(new Dimension(13, 8));
        rendererDownButton.setMaximumSize(new Dimension(13, 8));
        rendererDownButton.setContentAreaFilled(false);
        rendererDownButton.setMinimumSize(new Dimension(13, 8));
        rendererDownButton.setBorderPainted(false);
        
        rendererButtonPanel.add(rendererDownButton);
        
        rendererPanel.add(rendererButtonPanel,BorderLayout.EAST);
        
        editorFocusListener = new EditorFocusListener();
        upButtonActionListener = new UpButtonActionListener();
        downButtonActionListener = new DownButtonActionListener();
        
        setupIcons();
        
        setup = true;
    }
    
    private void setupIcons() {
        Icon i = UIManager.getIcon("Editor.upButtonNormal");
        rendererUpButton.setIcon(i);
        editorUpButton.setIcon(i);
        
        i = UIManager.getIcon("Editor.upButtonPressed");
        rendererUpButton.setPressedIcon(i);
        editorUpButton.setPressedIcon(i);
        
        i = UIManager.getIcon("Editor.downButtonNormal");
        rendererDownButton.setIcon(i);
        editorDownButton.setIcon(i);
        
        i = UIManager.getIcon("Editor.downButtonPressed");
        rendererDownButton.setPressedIcon(i);
        editorDownButton.setPressedIcon(i);
    }
    
    public Object getCellEditorValue() {
        String stringValue = editorField.getText();
        Dice dice = null;
        try {
            dice = new Dice(stringValue, false);
        }
        catch ( BadDiceException nfe) {
            
        }
        return (dice != null) ? dice.toString() : null;
    }
    
    public void highlightEditor() {
        editorField.selectAll();
        //editorField.requestFocus();
    }
    
    /** Causes the Editor to request focus for the appropriate Component to start editing.
 * The method should be override when appropriate to focus on the primary editing component.
 */
    public void focusEditor() {

        editorField.requestFocus();

    }
    
    public void setupEditorValue(Object value) {
        editorField.setText( value.toString() );
    }
    
    public void setupRendererValue(Object value) {
        rendererValueLabel.setText( value.toString() );
    }
    
        protected void editorDelegateAdded() {
        int width = parameterList.getParameterWidth(parameter);
        if ( width <= getDefaultWidth() ) width = getDefaultWidth();
        editorField.setPreferredSize(new Dimension(width, 20));
        
        editorFocusListener.setParameterEditor(this);
        editorField.addFocusListener( editorFocusListener );
        
        upButtonActionListener.setParameterEditor(this);
        editorUpButton.addActionListener(upButtonActionListener);
        
        downButtonActionListener.setParameterEditor(this);
        editorDownButton.addActionListener(downButtonActionListener);
        
        defaultActionListener.setParameterEditor(this);
        editorField.addActionListener(defaultActionListener);
    }
    
    protected void editorDelegateRemoved() {
        editorField.removeFocusListener( editorFocusListener );
        
        editorUpButton.removeActionListener(upButtonActionListener);
        
        editorDownButton.removeActionListener(downButtonActionListener);
        
        editorField.removeActionListener(defaultActionListener);
    }
    
    protected void rendererDelegateAdded() {
        int width = parameterList.getParameterWidth(parameter);
        if ( width <= getDefaultWidth() ) width = getDefaultWidth();
        rendererValueLabel.setPreferredSize(new Dimension(width, 20));
    }
    
    protected void rendererDelegateRemoved() {
        
    }
    
    protected int getDefaultWidth() {
        return 40;
    }
    
    
    public class EditorFocusListener extends FocusAdapter {
        private DefaultParameterEditor parameterEditor = null;
        
        public EditorFocusListener() {
            
        }
        
        public void setParameterEditor(DefaultParameterEditor pe) {
            parameterEditor = pe;
        }
        
        public void focusGained(FocusEvent e) {
            if ( parameterEditor != null ) parameterEditor.highlightEditor();
        }
    }
    
    public class UpButtonActionListener extends Object implements ActionListener {
        private DefaultParameterEditor parameterEditor = null;
        
        public UpButtonActionListener() {
            
        }
        
        public void setParameterEditor(DefaultParameterEditor pe) {
            parameterEditor = pe;
        }
        
        public void actionPerformed(ActionEvent e) {
            if ( parameterEditor != null ) {
                               Object value = parameterEditor.getCellEditorValue();
                Dice d = null;
                
                if ( value == null ) {
                    value = parameterEditor.getCurrentValue();
                }
                
                try {
                    d = new Dice((String)value,false);
                    d.setD6( d.getD6() + 1);
                    value = d.toString();
                    parameterEditor.setCurrentValue(value);
                }
                catch ( BadDiceException bde ) {
                }
            }
        }
    }
    
    public class DownButtonActionListener extends Object implements ActionListener {
        private DefaultParameterEditor parameterEditor = null;
        
        public DownButtonActionListener() {
            
        }
        
        public void setParameterEditor(DefaultParameterEditor pe) {
            parameterEditor = pe;
        }
        
        public void actionPerformed(ActionEvent e) {
            if ( parameterEditor != null ) {
                Object value = parameterEditor.getCellEditorValue();
                Dice d = null;
                
                if ( value == null ) {
                    value = parameterEditor.getCurrentValue();
                }
                
                try {
                    d = new Dice((String)value,false);
                    if ( d.getD6() >= 1 ) d.setD6( d.getD6() - 1);
                    value = d.toString();
                    parameterEditor.setCurrentValue(value);
                }
                catch ( BadDiceException bde ) {
                }
            }
        }
    }
}
