package net.sourcewalker.picrename;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.application.Task.BlockingScope;

public class ReplaceDialog extends JDialog {

    private static final long serialVersionUID = 1539234629156256305L;
    private final App application;
    private final AppData data;
    private ResourceMap res;
    private ActionMap actions;
    private JTextField searchField;
    private JTextField replaceField;

    public ReplaceDialog(App application, AppData data) {
        super(application.getMainFrame());
        this.application = application;
        this.data = data;
        this.actions = application.getContext().getActionMap(this);
        this.res = application.getContext().getResourceMap(ReplaceDialog.class);
        setTitle(res.getString("replaceDialog.title"));
        setModalityType(ModalityType.APPLICATION_MODAL);
        setMinimumSize(new Dimension(300, 100));
        setLocationRelativeTo(getParent());

        JPanel innerPanel = new JPanel();
        innerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        innerPanel.setLayout(new GridBagLayout());
        GridBagConstraints ctr = new GridBagConstraints();

        JLabel searchLabel = new JLabel(res.getString("searchLabel.text"));
        ctr.gridx = 0;
        ctr.gridy = 0;
        ctr.gridheight = 1;
        ctr.gridwidth = 1;
        ctr.anchor = GridBagConstraints.EAST;
        innerPanel.add(searchLabel, ctr);

        searchField = new JTextField();
        ctr.gridx = 1;
        ctr.gridwidth = 3;
        ctr.weightx = 1;
        ctr.fill = GridBagConstraints.HORIZONTAL;
        innerPanel.add(searchField, ctr);

        JLabel replaceLabel = new JLabel(res.getString("replaceLabel.text"));
        ctr.gridx = 0;
        ctr.gridy = 1;
        ctr.gridwidth = 1;
        ctr.weightx = 0;
        ctr.fill = GridBagConstraints.NONE;
        innerPanel.add(replaceLabel, ctr);

        replaceField = new JTextField();
        ctr.gridx = 1;
        ctr.gridwidth = 3;
        ctr.weightx = 1;
        ctr.fill = GridBagConstraints.HORIZONTAL;
        innerPanel.add(replaceField, ctr);

        JButton cancelButton = new JButton(actions.get("cancel"));
        ctr.gridx = 2;
        ctr.gridy = 2;
        ctr.gridwidth = 1;
        ctr.weightx = 0;
        ctr.fill = GridBagConstraints.NONE;
        innerPanel.add(cancelButton, ctr);

        JButton okButton = new JButton(actions.get("startReplace"));
        ctr.gridx = 3;
        innerPanel.add(okButton, ctr);

        setLayout(new BorderLayout());
        add(innerPanel, BorderLayout.CENTER);
    }

    @Action
    public void cancel() {
        setVisible(false);
    }

    @Action(block = BlockingScope.APPLICATION)
    public Task<Void, Void> startReplace() {
        return new ReplaceTextTask(application, this, data,
                searchField.getText(), replaceField.getText());
    }

}
