package attention;

import console.ConsoleLoop;

/**
 *  Line of text, sent from ConsoleThread to an attention bubble. Actually, to the attention dispatcher, it has to figure out
 * which bubble to send it to, because it's is him, who keeps track of which bubble talks to whom.
 * @author su
 */
public class Msg_ConsoleToAttnCircle extends AttnDispMessage {
    public Class sender;
    public String text;

    /**
     * Constructor
     * @param sender originator of this message.
     * @param text from console to an attention bubble.
     */
    public Msg_ConsoleToAttnCircle(Class<ConsoleLoop> sender, String text) {
        this.sender = sender;
        this.text = text;
    }
}
