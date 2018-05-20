package console;

import attention.AttnDispatcherLoop;

/**
 *  Sent to ConsoleThread to read a line of text.
 * @author su
 */
public class Msg_ReadFromConsole extends ConsoleMessage {

    /**
     * Constructor.
     * @param sender message loop class that originates this message.
     */
    public Msg_ReadFromConsole(Class<AttnDispatcherLoop> sender) {
        super(sender);
    }
    
}
