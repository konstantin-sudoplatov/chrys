package console;

/**
 *  Sent to ConsoleThread to print a line of text.
 * @author su
 */
public class Msg_WriteToConsole extends ConsoleMessage {

    public String text;

//    /**
//     * Constructor
//     * @param sender message loop that sends this message
//     * @param text a text to put on the screen. It must contain the \n symbol for a new line, since the text can be multi-lined.
//     */
//    public Msg_WriteToConsole(BaseMessageLoop sender, String text) {
//        super(sender);
//        this.text = text;
//    }

    /**
     * Constructor
     * @param text a text to put on the screen. It must contain the \n symbol for a new line, since the text can be multi-lined.
     */
    public Msg_WriteToConsole(String text) {
        this.text = text;
    }
}
