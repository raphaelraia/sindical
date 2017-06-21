
import br.com.rtools.utilitarios.Moeda;
import java.math.BigDecimal;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rtools2
 */
public class TestFloat {
    
    public static void main(String[] args) {
        
        System.err.println(new Float(0.005));
        System.err.println(new BigDecimal(18746666666.04).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        System.err.println(new Float(187774.05));
        
        System.err.println(Moeda.converteR$("187774.04"));
        
        
    }
    
}
