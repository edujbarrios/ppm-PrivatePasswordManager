package ppm;

import ppm.gui.PasswordManagerGUI;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(PasswordManagerGUI::new);
    }
}
