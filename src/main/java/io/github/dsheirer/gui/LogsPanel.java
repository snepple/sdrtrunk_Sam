package io.github.dsheirer.gui;

import io.github.dsheirer.preference.UserPreferences;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import java.awt.Desktop;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.awt.BorderLayout;
import java.io.FilenameFilter;

public class LogsPanel extends JPanel {

    private DefaultListModel<String> mListModel;
    private JList<String> mList;
    private UserPreferences mUserPreferences;
    private String mLogType;
    private Path mLogDirectory;

    public LogsPanel(UserPreferences userPreferences, String logType) {
        mUserPreferences = userPreferences;
        mLogType = logType;

        if (logType.equals("app")) {
            mLogDirectory = mUserPreferences.getDirectoryPreference().getDirectoryApplicationLog();
        } else {
            mLogDirectory = mUserPreferences.getDirectoryPreference().getDirectoryEventLog();
        }

        setLayout(new BorderLayout());

        mListModel = new DefaultListModel<>();
        mList = new JList<>(mListModel);
        mList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadLogs();

        JButton openBtn = new JButton("Open Selected Log in Text Editor");
        openBtn.addActionListener(e -> {
            String selected = mList.getSelectedValue();
            if (selected != null) {
                File logFile = new File(mLogDirectory.toFile(), selected);
                try {
                    Desktop.getDesktop().open(logFile);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadLogs());

        JPanel btnPanel = new JPanel();
        btnPanel.add(refreshBtn);
        btnPanel.add(openBtn);

        add(new JScrollPane(mList), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadLogs() {
        mListModel.clear();
        File dir = mLogDirectory.toFile();
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".log");
                }
            });
            if (files != null) {
                for (File file : files) {
                    mListModel.addElement(file.getName());
                }
            }
        }
    }
}
