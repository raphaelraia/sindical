
import br.com.rtools.utilitarios.Diretorio;
import java.io.File;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rtools2
 */
public class CreateExternal {

    public static void main(String[] args){
        String diretorio = "//192.168.1.102//Sistema//SINDICAL//testestestes";
       try {
            File f = new File(diretorio);
            if(!f.exists()) {
                f.mkdirs();
            } else {
                f.delete();
            }
        } catch (Exception e) {
        }
        
    }
    
    
}
