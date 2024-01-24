package ppm.gui;

import ppm.core.PasswordEntry;
import ppm.core.PasswordManagerCore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.*;
import java.util.List;

public class PasswordManagerGUI extends JFrame {
    private final PasswordManagerCore passwordManagerCore;
    private final DefaultTableModel tableModel;
    private JTable entryTable;

    public PasswordManagerGUI() {
        passwordManagerCore = new PasswordManagerCore(""); // A secure key will be generated automatically
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Service");
        tableModel.addColumn("Username");

        setTitle("ppm-PrivatePasswordManager by Eduardo J. Barrios");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        setLocationRelativeTo(null);
        setVisible(true);
        refreshEntryTable();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        createMenuBar();

        entryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(entryTable);
        add(scrollPane, BorderLayout.CENTER);

        JToolBar toolBar = new JToolBar();
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");
        JButton viewButton = new JButton("View Password");
        toolBar.add(addButton);
        toolBar.add(deleteButton);
        toolBar.add(viewButton);
        add(toolBar, BorderLayout.NORTH);

        addButton.addActionListener(e -> addPasswordEntry());
        deleteButton.addActionListener(e -> deletePasswordEntry());
        viewButton.addActionListener(e -> showPassword());
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();


        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "ppm\nVersion 1.0\nCreated by Eduardo J. Barrios",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void addPasswordEntry() {
        JTextField serviceField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        final JComponent[] inputs = new JComponent[]{
                new JLabel("Service"),
                serviceField,
                new JLabel("Username"),
                usernameField,
                new JLabel("Password"),
                passwordField
        };
        int result = JOptionPane.showConfirmDialog(this, inputs, "Add Entry", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String service = serviceField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (service.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            passwordManagerCore.addEntry(service, username, password);
            refreshEntryTable();
        }
    }

    private void deletePasswordEntry() {
        int selectedIndex = entryTable.getSelectedRow();
        if (selectedIndex != -1) {
            passwordManagerCore.deleteEntry(selectedIndex);
            refreshEntryTable();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an entry to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshEntryTable() {
        tableModel.setRowCount(0);
        List<PasswordEntry> entries = passwordManagerCore.getEntries();
        for (PasswordEntry entry : entries) {
            tableModel.addRow(new Object[]{entry.getService(), entry.getUsername()});
        }
    }

    private void showPassword() {
        int selectedIndex = entryTable.getSelectedRow();
        if (selectedIndex != -1) {
            PasswordEntry entry = passwordManagerCore.getEntries().get(selectedIndex);
            String decryptedPassword = passwordManagerCore.decryptPassword(entry.getEncryptedPassword());

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());

            JTextArea passwordTextArea = new JTextArea(decryptedPassword);
            passwordTextArea.setWrapStyleWord(true);
            passwordTextArea.setLineWrap(true);
            passwordTextArea.setOpaque(false);
            passwordTextArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(passwordTextArea);
            panel.add(scrollPane, BorderLayout.CENTER);

            JButton copyButton = new JButton("Copy to Clipboard");
            copyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection selection = new StringSelection(decryptedPassword);
                    clipboard.setContents(selection, null);
                    JOptionPane.showMessageDialog(PasswordManagerGUI.this, "Password copied to clipboard.");
                }
            });
            panel.add(copyButton, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, panel, "View Password", JOptionPane.PLAIN_MESSAGE);
        }
    }
}
