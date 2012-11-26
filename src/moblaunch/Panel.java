package moblaunch;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/*
 *
 * @author Pieter De Bruyne
 *
 */
public class Panel extends JPanel {

    private DefaultListModel model;
    private final JList exeList;
    private JTextArea logArea;
    private AutoRunThread autorun;
    private Process process;

    public Panel() {
        process = null;
        autorun = new AutoRunThread(this);
        model = new DefaultListModel();
        exeList = new JList(model);
        exeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane jScrollPane1 = new JScrollPane();
        JButton startButton = new JButton();
        JButton removeButton = new JButton();
        JButton upButton = new JButton();
        JButton downButton = new JButton();
        JButton autorunButton = new JButton();
        JButton stopAutorunButton = new JButton();
        JButton stopCurrentButton = new JButton();
        JScrollPane logScrollPane = new JScrollPane();
        jScrollPane1.setViewportView(exeList);
        JTextField showDelayField = new JTextField(autorun.getReeksDelay().toString());
        showDelayField.setColumns(5);
        JTextField audioDelayField = new JTextField(autorun.getAudioDelay().toString());
        audioDelayField.setColumns(5);
        JLabel showLabel = new JLabel("Montage wachttijd:");
        JLabel audioLabel = new JLabel("Bindtekst wachttijd:");
        jScrollPane1.setViewportView(exeList);
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setColumns(20);
        logArea.setRows(5);
        logScrollPane.setViewportView(logArea);

        showDelayField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!e.isTemporary()) {
                    JTextField field = (JTextField) e.getSource();
                    field.setCaretPosition(field.getText().length());
                    field.moveCaretPosition(0);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (autorun != null && !e.isTemporary()) {
                    JTextField field = (JTextField) e.getSource();
                    try {
                        Integer newDelay = Integer.parseInt(field.getText());
                        autorun.setReeksDelay(newDelay);
                    } catch (NumberFormatException ex) {
                        field.setText(autorun.getReeksDelay().toString());
                    }
                }
            }
        });

        audioDelayField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!e.isTemporary()) {
                    JTextField field = (JTextField) e.getSource();
                    field.setCaretPosition(field.getText().length());
                    field.moveCaretPosition(0);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!e.isTemporary()) {
                    if (autorun != null && !e.isTemporary()) {
                        JTextField field = (JTextField) e.getSource();
                        try {
                            Integer newDelay = Integer.parseInt(field.getText());
                            autorun.setAudioDelay(newDelay);
                        } catch (NumberFormatException ex) {
                            field.setText(autorun.getAudioDelay().toString());
                        }
                    }
                }
            }
        });

        removeButton.setAction(new AbstractAction("Verwijder") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (exeList.getSelectedIndex() == -1 && !model.isEmpty()) {
                    return;
                }
                model.removeElementAt(exeList.getSelectedIndex());
            }
        });

        startButton.setAction(new AbstractAction("Start") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (autorun != null && autorun.isRunning()) {
                    JOptionPane.showMessageDialog(Panel.this, "Autorun loopt ! Er kan geen afzonderlijke reeks worden gestart", "Oeps", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String command = ((File) exeList.getSelectedValue()).getAbsolutePath() + " /2";
                try {
                    process = Runtime.getRuntime().exec(command);
                } catch (IOException ex) {
                    System.err.println("Bestand kan niet worden geopend");
                }
            }
        });

        upButton.setAction(new AbstractAction("Omhoog") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (exeList.getSelectedIndex() != -1 && exeList.getSelectedIndex() != 0) {
                    int current = exeList.getSelectedIndex();
                    Object prev = model.getElementAt(current - 1);
                    Object curr = model.getElementAt(current);
                    model.set(current - 1, curr);
                    model.set(current, prev);
                    exeList.setSelectedIndex(current - 1);
                }
            }
        });

        downButton.setAction(new AbstractAction("Omlaag") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (exeList.getSelectedIndex() != -1 && exeList.getSelectedIndex() != model.getSize() - 1) {
                    int current = exeList.getSelectedIndex();
                    Object next = model.getElementAt(current + 1);
                    Object curr = model.getElementAt(current);
                    model.set(current + 1, curr);
                    model.set(current, next);
                    exeList.setSelectedIndex(current + 1);
                }
            }
        });

        autorunButton.setAction(new AbstractAction("Start autorun") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (process != null) {
                    try {
                        process.exitValue();
                    } catch (IllegalThreadStateException ex) {
                        JOptionPane.showMessageDialog(Panel.this, "Er loopt reeds een afzonderlijke reeks, stop deze eerst !", "Oeps", JOptionPane.INFORMATION_MESSAGE);
                    }
                    return;
                }
                if (autorun != null && !autorun.isRunning()) {
                    autorun.go();
                }
            }
        });

        stopAutorunButton.setAction(new AbstractAction("Stop autorun") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (autorun != null) {
                    autorun.forceQuit();
                }
            }
        });

        stopCurrentButton.setAction(new AbstractAction("Huidige stoppen") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (process != null) {
                    process.destroy();
                    process = null;
                }
            }
        });

        exeList.setModel(model);
        jScrollPane1.setViewportView(exeList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 604, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(logScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(removeButton, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                .addComponent(upButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(startButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(showLabel)
                .addComponent(audioLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(audioDelayField)
                .addComponent(showDelayField))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(autorunButton, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                .addComponent(stopAutorunButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(downButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(stopCurrentButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(24, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1)
                .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(autorunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(stopAutorunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(removeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(upButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(downButton, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(showLabel)
                .addComponent(showDelayField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(audioLabel)
                .addComponent(audioDelayField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(stopCurrentButton, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(logScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)))
                .addContainerGap()));

    }

    public synchronized DefaultListModel getModel() {
        return model;
    }

    public synchronized JList getExeList() {
        return exeList;
    }

    public synchronized void log(String message) {
        logArea.append(message);
        logArea.append("\n");
    }

    public void newAutoRunThread() {
        autorun = new AutoRunThread(this);
    }
}
