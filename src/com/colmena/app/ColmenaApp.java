package com.colmena.app;

import com.colmena.view.Usuario.LoginForm;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ColmenaApp {
    public static void main(String[] args) {        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
                
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginForm();
            }
        });
    }
}