/*
 * IntegerParameterEditor.java
 *
 * Created on June 14, 2001, 9:58 AM
 */

package champions.parameterEditor;

import champions.parameters.ParameterList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;



/**
 *
 * @author  twalker
 * @version
 */
public class IntegerParameterEditor extends DefaultParameterEditor {
    
    private boolean setup = false;
    
    private JTextField editorField;
    private JButton editorUpButton;
    private JButton editorDownButton;
    private JPanel editorButtonPanel;
    private JPanel editorPanel;
    
    private JLabel rendererValueLabel;
    private JButton rendererUpButton;
    private JButton rendererDownButton;
    private JPanel rendererButtonPanel;
    private JPanel rendererPanel;
    
    private EditorFocusListener editorFocusListener;
    private UpButtonActionListener upButtonActionListener;
    private DownButtonActionListener downButtonActionListener;
    private EditorMouseListener mouseListener;

    public IntegerParameterEditor() {

        setupEditor();

        setEditorDelegate( editorPanel );
        setRendererDelegate( rendererPanel );
    }
    
    /** Creates new IntegerParameterEditor */
    public IntegerParameterEditor(ParameterList parameterList,String parameter) {
        super(parameterList,parameter);
        
        setupEditor();
        
        setEditorDelegate( editorPanel );
        setRendererDelegate( rendererPanel );
        
    }
    
    public void setupEditor() {
        if ( setup ) return;
        
        editorPanel = new JPanel();
        editorPanel.setLayout( new ParameterEditorLayout(SwingConstants.HORIZONTAL) );
        editorPanel.setOpaque(false);
        
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
        //rendererPanel = new DefaultParameterEditor.TestPanel();
        rendererPanel.setLayout( new ParameterEditorLayout(SwingConstants.HORIZONTAL) );
        rendererPanel.setDoubleBuffered(false);
        
        rendererButtonPanel = new JPanel();
        rendererButtonPanel.setDoubleBuffered(false);
        rendererButtonPanel.setOpaque(false);
        rendererValueLabel = new JLabel();
        rendererUpButton = new JButton();
        rendererDownButton = new JButton();
        rendererButtonPanel.setLayout(new BoxLayout(rendererButtonPanel, BoxLayout.Y_AXIS));
        
        rendererValueLabel.setOpaque(false);
        rendererUpButton.setOpaque(false);
        rendererDownButton.setOpaque(false);
        
        rendererValueLabel.setBackground( getBackgroundNonSelectionColor() );
        rendererValueLabel.setForeground( getTextNonSelectionColor() );
       // rendererValueLabel.setBorder( new EtchedBorder(Color.white, new Color(102,102,102)) );
        rendererValueLabel.setBorder( new EmptyBorder(editorField.getInsets()));
        rendererValueLabel.setFont( editorField.getFont() );
        rendererValueLabel.setDoubleBuffered(false);
        
        rendererPanel.add(rendererValueLabel, BorderLayout.CENTER);
        
        rendererUpButton.setPreferredSize(new Dimension(13, 8));
        rendererUpButton.setMaximumSize(new Dimension(13, 8));
        rendererUpButton.setContentAreaFilled(false);
        rendererUpButton.setMinimumSize(new Dimension(13, 8));
        rendererUpButton.setBorderPainted(false);
        rendererUpButton.setDoubleBuffered(false);
        
        rendererButtonPanel.add(rendererUpButton);
        
        rendererDownButton.setPreferredSize(new Dimension(13, 8));
        rendererDownButton.setMaximumSize(new Dimension(13, 8));
        rendererDownButton.setContentAreaFilled(false);
        rendererDownButton.setMinimumSize(new Dimension(13, 8));
        rendererDownButton.setBorderPainted(false);
        rendererDownButton.setDoubleBuffered(false);
        
        rendererButtonPanel.add(rendererDownButton);
        
        rendererPanel.add(rendererButtonPanel,BorderLayout.EAST);
        
        editorFocusListener = new EditorFocusListener();
        upButtonActionListener = new UpButtonActionListener();
        downButtonActionListener = new DownButtonActionListener();
        //mouseListener = new EditorMouseListener();
        
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
        Integer value = null;
        try {
            value = new Integer(stringValue);
        }
        catch ( NumberFormatException nfe) {
            
        }
        return value;
    }
    
    public void highlightEditor() {
        editorField.selectAll();
      //  editorField.requestFocus();
    }
    
        /** Causes the Editor to request focus for the appropriate Component to start editing.
 * The method should be override when appropriate to focus on the primary editing component.
 */
    public void focusEditor() {

        editorField.requestFocus();

    }
    
    public void setupEditorValue(Object value) {
        editorField.setText( value == null ? "" : value.toString() );
        
    }
    
    public void setupRendererValue(Object value) {
        rendererValueLabel.setText( value == null ? "" : value.toString() );
    }
    
    protected void editorDelegateAdded() {
        int width = 0;
        if ( parameterList != null ) width = parameterList.getParameterWidth(parameter);
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
        
       // mouseListener.setParameterEditor(this);
        //editorField.addMouseListener(mouseListener);
    }
    
    protected void editorDelegateRemoved() {
        editorField.removeFocusListener( editorFocusListener );
        
        editorUpButton.removeActionListener(upButtonActionListener);
        
        editorDownButton.removeActionListener(downButtonActionListener);
        
        editorField.removeActionListener(defaultActionListener);
        
       // editorField.removeMouseListener(mouseListener);
    }
    
    protected void rendererDelegateAdded() {
        int width = 0;
        if ( parameterList != null ) width = parameterList.getParameterWidth(parameter);
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
           // System.out.println("I got the focus!");
        } 
        
     /*   public void focusLost(FocusEvent e) {
           // if ( parameterEditor != null ) parameterEditor.requestEditingStop();
            if ( parameterEditor != null ) parameterEditor.editNextNode();
        } */
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
                int newValue;
                newValue = (value == null) ? ((Integer)parameterEditor.getCurrentValue()).intValue() + 1 : ((Integer)value).intValue() + 1 ;
                parameterEditor.setCurrentValue( new Integer(newValue));
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
                int newValue;
                newValue = (value == null) ? ((Integer)parameterEditor.getCurrentValue()).intValue() - 1 : ((Integer)value).intValue() - 1 ;
                parameterEditor.setCurrentValue( new Integer(newValue));
            }
        }
    }
    
    public class EditorMouseListener implements MouseListener {
        private DefaultParameterEditor parameterEditor = null;
        
        public EditorMouseListener() {
            
        }
        
        public void setParameterEditor(DefaultParameterEditor pe) {
            parameterEditor = pe;
        }
        
        public void mousePressed(MouseEvent e) {
            if ( parameterEditor != null ) {
                
                System.out.println("TextBox got MouseEvent: " + e);
                if ( ((JTextField)e.getSource()).hasFocus() == false ) ((JTextField)e.getSource()).requestFocus();
            }
        } 
        
        /**
         * Invoked when the mouse exits a component.
         */
        public void mouseExited(MouseEvent e) {
                        if ( parameterEditor != null ) {
            //    System.out.println("TextBox got MouseEvent: " + e);
            }
        }
        
        /**
         * Invoked when a mouse button has been released on a component.
         */
        public void mouseReleased(MouseEvent e) {
                        if ( parameterEditor != null ) {
                System.out.println("TextBox got MouseEvent: " + e);
            }
        }
        
        /**
         * Invoked when the mouse has been clicked on a component.
         */
        public void mouseClicked(MouseEvent e) {
                        if ( parameterEditor != null ) {
                            if ( e.getClickCount() > 1 ) 
             System.out.println("TextBox got MouseEvent: " + e);
            }
        }
        
        /**
         * Invoked when the mouse enters a component.
         */
        public void mouseEntered(MouseEvent e) {
                        if ( parameterEditor != null ) {
                System.out.println("TextBox got MouseEvent: " + e);
            }
        }
        
     /*   public void focusLost(FocusEvent e) {
           // if ( parameterEditor != null ) parameterEditor.requestEditingStop();
            if ( parameterEditor != null ) parameterEditor.editNextNode();
        } */
    }
    
}
