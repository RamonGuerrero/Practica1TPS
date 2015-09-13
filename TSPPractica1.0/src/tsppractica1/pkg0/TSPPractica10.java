package tsppractica1.pkg0;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * @author RamonHernandez
 * @version TSPPractica1.0"1/sep/15"
 */
public class TSPPractica10 {

    static boolean banderaTermino=false;    //Esta bandera me indica si ya termino la ejecucion para cerrar los Archivos
    static String nuevalinea = System.getProperty("line.separator"); // Esta cadena es un salto de linea
    
    /**
     * @param args the command line arguments
     * @throws IOException  para los archivos de escritura .INST .ERR
     */
    public static void main(String[] args) throws IOException {
        
        /*La funcion del ciclo es verificar que Exista un archivo.ASM*/
        String ruta;
        boolean rutaValida=false;
        File existente = null;
        do {
            ruta=JOptionPane.showInputDialog(null,"Ingresa Ruta: "); //cadena de la ruta donde se localiza el archivo
            if(ruta.contains(".ASM")){
                existente=new File(ruta);
                if(existente.exists()){
                    JOptionPane.showMessageDialog(null,"El archivo se prosesara");
                    rutaValida=true;
                }    
                else
                    JOptionPane.showMessageDialog(null,"El archivo no existe");
            }
            else
                JOptionPane.showMessageDialog(null,"El archivo no es extencion .ASM");
        }while(!rutaValida);
        
        FileReader fr;   //objeto de la clase FileReader para lograr leer el archivo
        BufferedReader entrada;    //objeto de la clase BufferedReader para leer una linea completa del archivo
        
        String rutaERR=ruta; //cadena donde se va a guardar la ruta del Archivo.ERR
        rutaERR = rutaERR.replaceAll(".ASM", ".ERR"); //se replaza .ASM por .ERR
        File archivoERR =new File(rutaERR);// crea el archivo en la rutaERR
        FileWriter frERR =new FileWriter(archivoERR);//abre el archivo para escribir caracter por caracter
        BufferedWriter brERR=new BufferedWriter(frERR);//ayuda a escribir en archivo mediante un buffer
        brERR.write("___________Archivo de Errores(.ERR)________"+nuevalinea+nuevalinea+nuevalinea);
        brERR.write("# Linea ||         ERROR                   "+nuevalinea);
        brERR.write("-------------------------------------------"+nuevalinea+nuevalinea);
        
        String rutaINST=ruta;//cadena donde se va a guardar la ruta del Archivo.INST
        rutaINST= rutaINST.replaceAll(".ASM",".INST");//se remplaza .ASM por .INST
        File archivoINST =new File(rutaINST); //se crea el archivo en la rutaINST
        FileWriter frINST =new FileWriter(archivoINST); // se abre el archivo para escribir caracter por caracter
        BufferedWriter brINST=new BufferedWriter(frINST); //ayuda a escribir en archivo mediante un buffer
        brINST.write("_________Archivo de Instrucciones(.INST)_______"+nuevalinea+nuevalinea+nuevalinea);
        brINST.write("# Linea   || Etiqueta || CODOP    || Operando "+nuevalinea);
        brINST.write("-------------------------------------------"+nuevalinea+nuevalinea);

        try { 
/*  Primer parte: Recorre el archivo de lectura*/
            fr = new FileReader(existente); //se abre el acrhivo en forma de lectura
            entrada = new BufferedReader(fr);
            
            /* Objecto de la Clase FormaLinea*/
            FormaLinea linea;
            
            String lineas= entrada.readLine(); //primer linea del archivo
            short contlineas=0; // contador de las lineas del archivo
            String validas=null;//me va indicar si existio error o fue valida la linea
            String errores=null;
            short contErrores=0,contLineasValidas=0;//contador de lineas validas y lineas con errores
          
            while(lineas!=null){ // ciclo para recorrer todo el archivo de lectura
                contlineas++;
                System.out.println("ya conto");
                linea=new FormaLinea(contlineas,lineas);// se construye el objeto 
                System.out.println("hizo las linea en objeto");
                linea.quitaComentarios();//metodo para quitar los comentarios
                System.out.println("le quito los comentarios a la linea");
                linea.separarLinea();//separa las lineas que no son comentarios y lineas vacias
                System.out.println("Separo la linea en token");
                linea.Validar();//Valida la linea 
                System.out.println("Valido la liena ");
                banderaTermino=linea.esEND();//el metodo retorna si se encontro el comando END
                System.out.println("verifica si existe END");
                linea.Automata();//Recorre los estados de la linea para verificar cual fue el error
                System.out.println("verifica trasaccion de automata");
                validas=linea.ImprimirLineasValidas();//retorna la linea valida con un formato de escritura si la linea no fue valida retorna null
                errores=linea.ImprimirLineaErrores();//retorna la linea con error con un formato de escritura si la linea no contiene errores retorna null
                if(validas!=null && errores==null){//Validacion si es valida la linea
                    System.out.println("!Linea Valida a escribir!");
                    brINST.write(validas+nuevalinea);
                    contLineasValidas++;
                }
                if(errores!=null){//validacion si la linea contiene errores
                    System.out.println("!Linea con error a Escribir en ERR!");
                    brERR.write(errores+nuevalinea);
                    contErrores++;
                }   
                if(banderaTermino==true){//validacion si se encontro el comando END para terminar
                    System.out.println("Se encontro el comando END");
                    break;   
                }
                lineas= entrada.readLine(); // salta de linea en el Archivo de Lectura
                System.out.println("#"+contlineas+" linea saltada");
            }
        if(banderaTermino==false){
            banderaTermino=true;//al final del archivo no se encontro el comadno END escribe algun error
            brERR.write("      ERROR no se encontro el comando END");
            contErrores++;
        }    
        if(contErrores==0)
            brERR.write("      No existen Errores en Archivo.ASM       ");
        if(contLineasValidas==0)
            brINST.write("     No existen Lineas Validas para Ensamblador      ");
        
         fr.close();
         brERR.close();
         brINST.close();
         JOptionPane.showMessageDialog(null,"!Listo!\n Ya se cerraron los archivos");
         
        } catch (FileNotFoundException ex) {// excepcion de Archivo de Lectura
            Logger.getLogger(TSPPractica10.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) { //exepcion de Lectura de l Archivo de Lectura
            Logger.getLogger(TSPPractica10.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
}
