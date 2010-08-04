package net.sourcewalker.picrename;

import javax.swing.ActionMap;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

public class App extends SingleFrameApplication {

    private MainPanel mainPanel;
    private JMenuBar menuBar;
    private ActionMap actions;
    private AppData data;

    @Override
    protected void startup() {
        data = new AppData();
        actions = getContext().getActionMap(new AppActions(data));

        getMainFrame().setJMenuBar(getMenuBar());
        show(getMainPanel());
    }

    private JMenuBar getMenuBar() {
        if (menuBar == null) {
            menuBar = new JMenuBar();
            JMenu appMenu = new JMenu();
            appMenu.setName("appMenu");
            appMenu.add(new JMenuItem(actions.get("quit")));
            menuBar.add(appMenu);
        }
        return menuBar;
    }

    private MainPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new MainPanel(actions, data);
        }
        return mainPanel;
    }

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }

}
