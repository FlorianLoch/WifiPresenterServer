package core.commands;

import java.awt.event.KeyEvent;

/**
 * Created by florian on 13.03.15.
 */
public class RightKeyPressCommand extends KeyPressCommand {

    @Override
    public void onCommand() {
        this.sendKeystroke(KeyEvent.VK_RIGHT);
    }

    @Override
    public String getCommand() {
        return "next";
    }

}
