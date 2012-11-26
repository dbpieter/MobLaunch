package moblaunch;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Pieter De Bruyne
 */
public class MobLaunch {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".exe") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "executable";
            }
        });

        final Panel panel = new Panel();
        JMenuBar menubar = new JMenuBar();
        JMenu startMenu = new JMenu("Start");

        final JFileChooser configFileChooser = new JFileChooser();
        configFileChooser.setMultiSelectionEnabled(false);
        configFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".mcf") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "MobLaucnh config file";
            }
        });
        JFrame frame = new JFrame();

        startMenu.add(new JMenuItem(new AbstractAction("Open exe's") {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.showOpenDialog(fileChooser);
                File[] files = fileChooser.getSelectedFiles();
                for (File file : files) {
                    if (!file.canExecute()) {
                        JOptionPane.showMessageDialog(null, file.getAbsolutePath() + "\n Is geen uitvoerbaar bestand!!!", "Oeps", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    panel.getModel().addElement(file);
                    System.out.println(file.toString());
                }
            }
        }));

        startMenu.add(new JMenuItem(new AbstractAction("Configuratie opslaan") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.showSaveDialog(fileChooser);
                File f = fileChooser.getSelectedFile();
                if (f == null) {
                    return;
                }
                String filePath = f.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".mcf")) {
                    f = new File(filePath + ".mcf");
                }
                if (f.exists()) {
                    f.delete();
                } else {
                    try {
                        f.createNewFile();
                    } catch (IOException ex) {
                        Logger.getLogger(MobLaunch.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                try {
                    FileWriter writer = new FileWriter(f);
                    writer.write("[moblaunch config file]");
                    writer.write('\n');
                    for (Object ob : panel.getModel().toArray()) {
                        File file = (File) ob;
                        writer.write(file.getAbsolutePath());
                        writer.write('\n');
                    }
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(MobLaunch.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }));

        startMenu.add(new JMenuItem(new AbstractAction("Configuratie openen") {
            @Override
            public void actionPerformed(ActionEvent e) {
                configFileChooser.showOpenDialog(configFileChooser);
                File f = configFileChooser.getSelectedFile();
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(f));
                    String line;
                    try {
                        line = reader.readLine();
                        if (line == null || !line.equals("[moblaunch config file]")) {
                            return;
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MobLaunch.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        while ((line = reader.readLine()) != null) {
                            File exe = new File(line);
                            if (exe != null && !exe.exists()) {
                                JOptionPane.showMessageDialog(null, line + "\n Bestaat niet meer !!!", "Oeps", JOptionPane.ERROR_MESSAGE);
                                continue;
                            }
                            if (!exe.canExecute()) {
                                JOptionPane.showMessageDialog(null, line + "\n Is geen uitvoerbaar bestand!!!", "Oeps", JOptionPane.ERROR_MESSAGE);
                                continue;
                            }
                            panel.getModel().addElement(exe);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MobLaunch.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MobLaunch.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }));

        menubar.add(startMenu);
        // frame aanmaken en instellen
        frame.setIconImage(new ImageIcon(MobLaunch.class.getResource("/resources/baricon.png")).getImage());
        frame.setJMenuBar(menubar);
        frame.setContentPane(panel);
        frame.setTitle("MobLaunch");
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
