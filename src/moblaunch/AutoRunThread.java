package moblaunch;

import java.io.File;
import java.io.IOException;

/*
 *
 * @author Pieter De Bruyne
 *
 */
public class AutoRunThread extends Thread {

    private static final String AUDIOMARKER = "BINDTEKST";
    private Integer audioDelay = 0;
    private Integer reeksDelay = 0;
    private Panel panel;
    private boolean running = false;

    public AutoRunThread(Panel panel) {
        this.panel = panel;
    }

    @Override
    public void run() {
        while (true) {
            String command = ((File) panel.getjList1().getSelectedValue()).getAbsolutePath();
            int lastSlash = command.lastIndexOf('\\');
            String exeName = command.substring(lastSlash + 1, command.length());
            exeName = exeName.concat("\"");
            exeName = "\"".concat(exeName);
            
            int sleeptime = 0;
            synchronized (audioDelay) {
                synchronized (reeksDelay) {
                    if (command.indexOf(AUDIOMARKER) > -1) {
                        panel.log(exeName + " start over " + audioDelay + " seconden");
                        sleeptime = audioDelay * 1000;
                    } else {
                        sleeptime = reeksDelay * 1000;
                        panel.log(exeName + " start over " + reeksDelay + " seconden");
                    }
                }
            }
            command += " /2";
            
            try {
                Thread.sleep(sleeptime);
            } catch (InterruptedException ex) {
                running = false;
                break;
            }
            Process proc;

            try {
                proc = Runtime.getRuntime().exec(command);
            } catch (IOException ex) {
                break;
            }
            panel.log(exeName + " is gestart");
            
            try {
                proc.waitFor();
            } catch (InterruptedException ex) {
                proc.destroy();
                panel.log("Autorun getermineerd!");
                running = false;
                break;
            }
            panel.log(exeName + " is beëindigd");
            
            if (panel.getjList1().getSelectedIndex() == panel.getModel().getSize() - 1) {
                running = false;
                //panel.log("autorun beëindigd");
                break;
            } else {
                panel.getjList1().setSelectedIndex(panel.getjList1().getSelectedIndex() + 1);
            }
        }
        panel.log("autorun beëindigd");
        panel.newAutoRunThread();
        running = false;
    }

    public void go() {
        running = true;
        start();
    }

    void forceQuit() {
        this.interrupt();
    }

    public Integer getAudioDelay() {
        return audioDelay;
    }

    public synchronized void setAudioDelay(Integer audioDelay) {
        this.audioDelay = audioDelay;
    }

    public Integer getReeksDelay() {
        return reeksDelay;
    }

    public synchronized void setReeksDelay(Integer reeksDelay) {
        this.reeksDelay = reeksDelay;
    }

    public boolean isRunning() {
        return running;
    }
}
