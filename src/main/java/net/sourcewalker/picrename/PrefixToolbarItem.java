package net.sourcewalker.picrename;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PrefixToolbarItem extends JPanel implements DocumentListener {

    private static final long serialVersionUID = -9189555800781004236L;

    private final AppData data;
    private JLabel prefixLabel;
    private JTextField prefixField;

    protected boolean changing = false;

    public PrefixToolbarItem(AppData data) {
        this.data = data;
        setLayout(new PrefixToolbarItemLayout());

        prefixLabel = new JLabel();
        prefixLabel.setName("prefixLabel");
        add(prefixLabel);
        prefixField = new JTextField();
        prefixField.setName("prefixField");
        prefixField.setText(data.getPrefix());
        prefixField.getDocument().addDocumentListener(this);
        add(prefixField);

        data.addPropertyChangeListener("prefix", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (!changing) {
                    prefixField.setText((String) evt.getNewValue());
                }
            }
        });
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        prefixUpdated();
    }

    private void prefixUpdated() {
        changing = true;
        data.setPrefix(prefixField.getText());
        this.validate();
        changing = false;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        prefixUpdated();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        prefixUpdated();
    }

    public class PrefixToolbarItemLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            JLabel label = PrefixToolbarItem.this.prefixLabel;
            JTextField field = PrefixToolbarItem.this.prefixField;
            return new Dimension(label.getWidth() + field.getWidth() + 15,
                    Math.max(label.getHeight(), field.getHeight()));
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return preferredLayoutSize(parent);
        }

        @Override
        public void layoutContainer(Container parent) {
            JLabel label = PrefixToolbarItem.this.prefixLabel;
            Dimension labelSize = label.getPreferredSize();
            JTextField field = PrefixToolbarItem.this.prefixField;
            Dimension fieldSize = field.getPreferredSize();
            int heightDiff = (int) (Math.abs(fieldSize.getHeight()
                    - labelSize.getHeight()) / 2);
            label.setBounds(5, 5 + heightDiff, (int) labelSize.getWidth(),
                    (int) labelSize.getHeight());
            field.setBounds((int) labelSize.getWidth() + 10, 5,
                    (int) Math.max(fieldSize.getWidth() + 5, 50),
                    (int) fieldSize.getHeight());
        }

    }

}
