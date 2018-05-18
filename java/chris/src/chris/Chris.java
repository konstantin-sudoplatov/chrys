package chris;

/**
 * @author su
 */
public class Chris {

    /**
     * Main.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Glob.initialize_application();
        
        Glob.master_loop.run();
        
        Glob.terminate_application();
    }
    
}
