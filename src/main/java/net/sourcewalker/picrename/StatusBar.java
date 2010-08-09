package net.sourcewalker.picrename;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

public class StatusBar extends JPanel implements PropertyChangeListener {

    private static final long serialVersionUID = 8410892979071774612L;

    private JLabel label;
    private ResourceMap res;
    private int thumbsRemaining;

    public StatusBar(AppData data) {
        super();
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(3, 5, 3, 0));

        res = Application.getInstance().getContext()
                .getResourceMap(StatusBar.class);

        label = new JLabel(res.getString("status.idle"));
        add(label, BorderLayout.CENTER);

        data.getThumbnailWorker().addPropertyChangeListener("pending", this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        thumbsRemaining = (Integer) evt.getNewValue();
        if (thumbsRemaining > 0) {
            label.setText(String.format(res.getString("status.thumbnails"),
                    thumbsRemaining));
        } else {
            label.setText(res.getString("status.idle"));
        }
    }

}
