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
    private final JList jList1;
    private JTextArea jTextArea1;
    private AutoRunThread autorun;
    private Process process;

    public Panel() {
        process = null;
        autorun = new AutoRunThread(this);
        model = new DefaultListModel();
        jList1 = new JList(model);
        jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane jScrollPane1 = new JScrollPane();
        JButton startButton = new JButton();
        JButton removeButton = new JButton();
        JButton upButton = new JButton();
        JButton downButton = new JButton();
        JButton autorunButton = new JButton();
        JButton jButton6 = new JButton();
        JButton jButton7 = new JButton();
        JScrollPane jScrollPane2 = new JScrollPane();
        jScrollPane1.setViewportView(jList1);
        JTextField jTextField1 = new JTextField(autorun.getReeksDelay().toString());
        jTextField1.setColumns(5);
        JTextField jTextField2 = new JTextField(autorun.getAudioDelay().toString());
        jTextField2.setColumns(5);
        JLabel reeksLabel = new JLabel("Montage wachttijd:");
        JLabel jLabel2 = new JLabel("Bindtekst wachttijd:");
        jScrollPane1.setViewportView(jList1);
        jTextArea1 = new JTextArea();
        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        jTextField1.addFocusListener(new FocusListener() {
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

        jTextField2.addFocusListener(new FocusListener() {
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
                if (jList1.getSelectedIndex() == -1 && !model.isEmpty()) {
                    return;
                }
                model.removeElementAt(jList1.getSelectedIndex());
            }
        });

        startButton.setAction(new AbstractAction("Start") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (autorun != null && autorun.isRunning()) {
                    JOptionPane.showMessageDialog(Panel.this, "Autorun loopt ! Er kan geen afzonderlijke reeks worden gestart", "Oeps", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String command = ((File) jList1.getSelectedValue()).getAbsolutePath() + " /2";
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
                if (jList1.getSelectedIndex() != -1 && jList1.getSelectedIndex() != 0) {
                    int current = jList1.getSelectedIndex();
                    Object prev = model.getElementAt(current - 1);
                    Object curr = model.getElementAt(current);
                    model.set(current - 1, curr);
                    model.set(current, prev);
                    jList1.setSelectedIndex(current - 1);
                }
            }
        });

        downButton.setAction(new AbstractAction("Omlaag") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jList1.getSelectedIndex() != -1 && jList1.getSelectedIndex() != model.getSize() - 1) {
                    int current = jList1.getSelectedIndex();
                    Object next = model.getElementAt(current + 1);
                    Object curr = model.getElementAt(current);
                    model.set(current + 1, curr);
                    model.set(current, next);
                    jList1.setSelectedIndex(current + 1);
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

        jButton6.setAction(new AbstractAction("Stop autorun") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (autorun != null) {
                    autorun.forceQuit();
                }
            }
        });

        jButton7.setAction(new AbstractAction("Huidige stoppen") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (process != null) {
                    process.destroy();
                    process = null;
                }
            }
        });

        jList1.setModel(model);
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 604, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(removeButton, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                .addComponent(upButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(startButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(reeksLabel)
                .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTextField2)
                .addComponent(jTextField1))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(autorunButton, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(downButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(removeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(upButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(downButton, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(reeksLabel)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel2)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)))
                .addContainerGap()));

    }

    public synchronized DefaultListModel getModel() {
        return model;
    }

    public synchronized JList getjList1() {
        return jList1;
    }

    public synchronized void log(String message) {
        jTextArea1.append(message);
        jTextArea1.append("\n");
    }

    public void newAutoRunThread() {
        autorun = new AutoRunThread(this);
    }
}
