
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
// 100962.57
// 125499.73
// 117001.62
public class TestFloat {

    public static void main(String[] args) {

        // System.err.println(new Float(0.005));
        // System.err.println(new BigDecimal(18746666666.04).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        // System.err.println(Moeda.converteR$("187774.04000"));
//        System.err.println(new Double(99999.00006));
//        System.err.println(new Float(125499.73));
//        System.err.println(new Float(117001.62));
//        System.err.println(new Double(185533.57));
//
//        BigDecimal bd = new BigDecimal(5);
//        BigDecimal bd2 = new BigDecimal(5);
//
//        bd2.add(bd);
//        bd2.subtract(bd);
//        bd2.divide(bd);
//        bd2.multiply(bd);
          System.err.println(Moeda.converteStringToDouble("1.000.000,326574"));

    }

}
