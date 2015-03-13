package core.commands;

import core.RemoteCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by florian on 13.03.15.
 */
public abstract class KeyPressCommand implements RemoteCommand {
    private static final Logger log = LoggerFactory.getLogger(KeyPressCommand.class);
    protected Robot robot;

    public KeyPressCommand() {
        try {
            this.robot = new Robot();
            log.debug("AWT robot successfully initialized!");
        } catch (AWTException e) {
            log.error("Could not instantiate AWT robot", e);
        }
    }

    protected void sendKeystroke(int keyCode) {
        if (null == this.robot) {
            log.error("Robot has not been initialized - operation aborted!");
            return;
        }

        this.robot.keyPress(keyCode);

        log.debug("Send keystroke " + keyCode + " to system.");
    }

}
