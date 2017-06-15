/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chrys;

import attention.AttnDispatcher;
import attention.ConsoleListener;
import concept.ComDir;

/**
 *
 * @author su
 */
public class Chrys {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // create static concepts and put them into ComDir
        ComDir.generate_static_concepts();
        
        // create and give to dispatcher the console bubble
        AttnDispatcher.add_atb(new ConsoleListener());
        
        // start all flows
        AttnDispatcher.start();
        
        // wait for them to finish
        AttnDispatcher.join();
    }
    
}
