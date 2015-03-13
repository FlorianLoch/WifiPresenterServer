package core.commands;

import java.awt.event.KeyEvent;

/**
 * Created by florian on 13.03.15.
 */
public class LeftKeyPressCommand extends KeyPressCommand {

    @Override
    public void onCommand() {
        this.sendKeystroke(KeyEvent.VK_LEFT);
    }

    @Override
    public String getCommand() {
        return "back";
    }

}
