/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concept;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author su
 */
public class ComDirTest {
    
    public ComDirTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of put_cpt method, of class ComDir.
     */
    @Test
    public void testPut_cpt() {
        System.out.println("put_cpt");
        Concept cpt = new Concept();
        ComDir.put_cpt(cpt);
        assertEquals(true, ComDir.contains_key_in_cpt(cpt.getCid()));
        assertEquals(cpt, ComDir.get_cpt(cpt.getCid()));
    }
}
