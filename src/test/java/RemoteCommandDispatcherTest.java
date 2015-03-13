import core.RemoteCommand;
import core.RemoteCommandDispatcher;
import org.junit.Test;

import static org.junit.Assert.*;

public class RemoteCommandDispatcherTest {

    @Test
    public void testCommandDispatching() {
        RemoteCommandDispatcher instance = new RemoteCommandDispatcher();
        int[] called = new int[2];

        instance.addCommand(new RemoteCommand() {
            @Override
            public void onCommand() {
                called[0]++;
            }

            @Override
            public String getCommand() {
                return "hello";
            }
        });

        instance.addCommand(new RemoteCommand() {
            @Override
            public void onCommand() {
                called[1]++;
            }

            @Override
            public String getCommand() {
                return "helloWORLD";
            }
        });

        instance.onMessage("\\hello");
        instance.onMessage("\\hello world"); //expect: cut off at space
        instance.onMessage("\\helloworld"); //expect: command is converted to lowercase before check on equality is done

        assertArrayEquals(new int[] {2, 1}, called);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddSameCommandTwice() {
        RemoteCommandDispatcher instance = new RemoteCommandDispatcher();

        instance.addCommand(new RemoteCommand() {
            @Override
            public void onCommand() {

            }

            @Override
            public String getCommand() {
                return "foo";
            }
        });

        instance.addCommand(new RemoteCommand() {
            @Override
            public void onCommand() {

            }

            @Override
            public String getCommand() {
                return "foo";
            }
        });
    }


}