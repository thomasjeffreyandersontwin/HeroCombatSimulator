package champions;

import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JTextField;
import java.beans.*; //Property change stuff
import java.awt.*;
import java.awt.event.*;

class ChangeNameDialog extends JDialog {
    private String typedText = null;

    private String magicWord;
    private JOptionPane optionPane;

    public String getInputText() {
        return typedText;
    }

    public ChangeNameDialog(Frame aFrame, String oldName, String newName) {
        super(aFrame, true);
        setTitle("Duplicate Target/Character");

        final String msgString1 = "A Character or Target named \"" + oldName + "\" already\n"
        + "exists in the current battle.";
        final String msgString2 = "Would you like to rename the new Character or Target?";
        final JTextField textField = new JTextField(20);
    Object[] array = {msgString1, msgString2, textField};

        final String btnString1 = "Rename";
        final String btnString2 = "Skip "+ oldName;
    Object[] options = {btnString1, btnString2};

        optionPane = new JOptionPane(array,
        JOptionPane.QUESTION_MESSAGE,
        JOptionPane.YES_NO_OPTION,
        null,
        options,
        options[0]);
        setContentPane(optionPane);
        pack();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        textField.setText( newName );
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                /*
                 * Instead of directly closing the window,
                 * we're going to change the JOptionPane's
                 * value property.
                 */
                optionPane.setValue(new Integer(
                JOptionPane.CLOSED_OPTION));
            }
        });

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                optionPane.setValue(btnString1);
            }
        });

        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String prop = e.getPropertyName();

                if (isVisible()
                && (e.getSource() == optionPane)
                && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
                prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
                    Object value = optionPane.getValue();

                    if (value == JOptionPane.UNINITIALIZED_VALUE) {
                        //ignore reset
                        return;
                    }

                    // Reset the JOptionPane's value.
                    // If you don't do this, then if the user
                    // presses the same button next time, no
                    // property change event will be fired.
                    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

                    if (value.equals(btnString1)) {
                        typedText = textField.getText();
                        setVisible(false);

                    } else { // user closed dialog or clicked cancel
                        typedText = null;
                        setVisible(false);
                    }
                }
            }
        });
        
        ChampionsUtilities.centerWindow(this);
    }
}

