/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tsppractica1.pkg0;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.StringTokenizer;

/**
 * @author RamonHernandez
 */
public class FormaLinea {

    short numeroLinea;
    String linea;

    /* Variables de token*/
    StringTokenizer token = null;
    String etq = null;
    String codop = null;
    String oper = null;

    /* Estado de la Linea*/
    String estado = "q0";
    /* bandera si debe de llevar operando*/
    boolean banderaOper = false;

    /**
     * @param numeroLinea es el contructor del numero de la linea
     * @param linea es el contructor del contenido del la linea
     */
    public FormaLinea(short numeroLinea, String linea) {
        this.numeroLinea = numeroLinea;
        this.linea = linea;
    }

    /**
     * Sustrae los comentarios
     */
    public void quitaComentarios() {
        String comentario;
        for (int i = 0; i < linea.length(); i++) {
            if (linea.charAt(i) == ';') {
                comentario = linea.substring(i);
                linea = linea.replaceAll(comentario, " ");
                break;
            }
        }
    }

    /**
     * Separa las lineas segun la cantidad de token
     */
    public void separarLinea() {
        token = new StringTokenizer(linea);
        if (token.countTokens() == 0) {
            etq = null;
            codop = null;
            oper = null;
            estado += "q0";//estado del automata sin error solo finaliza
        } else if (token.countTokens() == 1) {
            if (VerificaEspacioBlanco()) {
                codop = token.nextToken();
                etq = "NULL";
                oper = "NULL";
            } else {
                estado += "r1";//estado del automata donde no existe codop
            }
        } else if (token.countTokens() == 2) {
            if (VerificaEspacioBlanco()) {
                codop = token.nextToken();
                oper = token.nextToken();
                etq = "NULL";
            } else {
                etq = token.nextToken();
                codop = token.nextToken();
                oper = "NULL";
            }
        } else if (token.countTokens() == 3) {
            if (VerificaEspacioBlanco()) {
                estado += "q1";//estado del automata que existe un espacio en blanco y prosigue una etiqueta
            } else {
                etq = token.nextToken();
                codop = token.nextToken();
                oper = token.nextToken();
            }
        } else {
            etq = null;
            codop = null;
            oper = null;
            estado += "q2";//estado del automata donde exeden token 
        }
    }

    /**
     * Valida CODOP con expresion regular y un recorrido de cadena
     *
     * @return true si el CODOP es valido false si no es valido
     */
    public boolean validarCodop() {
        Pattern patronCodop = Pattern.compile("^[a-zA-Z]{1}[a-zA-Z(\\.)?]{0,4}$");
        Matcher comparaCodop = patronCodop.matcher(codop);
        if (comparaCodop.matches()) {
            byte contPuntos = 0;
            for (byte i = 0; i < codop.length(); i++) {
                if (codop.charAt(i) == '.') {
                    contPuntos++;
                }
            }
            if (contPuntos <= 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Valida ETIQUETA con la expresion regular
     *
     * @return true cuando es valida la ETIQUETA false si es invalida
     */
    public boolean validarEtiqueta() {
        Pattern patronEtiqueta = Pattern.compile("^[a-zA-Z]{1}[\\w]{0,7}$");
        Matcher comparaEtiqueta = patronEtiqueta.matcher(etq);
        return comparaEtiqueta.matches();
    }

    /**
     * Valida el OPERANDO con la expresion regular
     *
     * @return true cuando es valido el OPERANDO false si es invalido
     */
    public boolean validarOperando() {
        Pattern patronOper = Pattern.compile("^.+");
        Matcher comparaOper = patronOper.matcher(oper);
        return comparaOper.matches();
    }

    /**
     * Metodo donde va validando los token y si existe error añade un estado del
     * automata
     */
    public void Validar() {
        if (etq != null && codop != null && oper != null) {
            boolean banderaEtq = validarEtiqueta();
            boolean banderaCodop = validarCodop();
            boolean banderaOper = validarOperando();
            if (banderaEtq && banderaCodop && banderaOper) {
                estado += "qf";
                System.out.println("linea al parecer valida");
            } else {
                estado += "q3";// estado del automata donde Error en comandos invalido
                System.out.println("Error en linea algun comando invalido");
                if (!banderaEtq) {
                    estado += "q4";//Error el comando de etiqueta invalido
                }
                if (!banderaCodop) {
                    estado += "q5";//Error el comando de codop invalido
                }
                if (!banderaOper) {
                    estado += "q6";//Error el comando operando invalido
                }
            }
        } else {
            if ("q0q0".equals(estado)) {
                System.out.println("!Esta linea esta Vacia y es valida");
            } else {
                System.out.println("Hay algun error");
            }
        }
    }

    /**
     * El metodo verifica si se encuntra el comando END
     *
     * @return true si se encontro el comand o END false la linea no contiene el
     * comando
     */
    public boolean esEND() {
        if (etq != null && codop != null && oper != null) {
            Pattern patronEND = Pattern.compile("^[    ]*[e|E]{1}[n|N]{1}[d|D]{1}[     ]*$");
            Matcher comparaEND = patronEND.matcher(codop);
            if (comparaEND.matches()) {
                estado += "q9";
                return true;//Estado de fin del ensamblador
            }
        }
        return false;
    }

    /**
     * El metodo recorre la cadena de los estados y verifica en que estados
     * recorrio la linea
     *
     * @return un formato de impresion segun el estado de la linea
     */
    public String Automata() {
        String saltoLinea = System.getProperty("line.separator");
        String imprimirError = "E", imprimirValido = "V";
        String aux = estado.substring(2, 4);
        if (null != aux) {
            switch (aux) {
                case "q0":
                    imprimirError = "L";
                    break;
                case "r1":
                    imprimirError += "    ERROR No existe Codigo de Operacion" + saltoLinea;
                    break;
                case "q1":
                    imprimirError += "    ERROR en ETIQUETA no es valido el primer caracter un Espacio Blanco" + saltoLinea;
                    break;
                case "q2":
                    imprimirError += "    ERROR exedente de tokenes" + saltoLinea;
                    break;
                case "q3":
                    imprimirError += "    ERROR un comando Invalido" + saltoLinea;
                    for (int i = 4; i < estado.length(); i += 2) {
                        aux = estado.substring(i, i + 2);
                        if (null != aux) {
                            switch (aux) {
                                case "q4":
                                    imprimirError += "    ERROR ETIQUETA invalida" + saltoLinea;
                                    break;
                                case "q5":
                                    imprimirError += "    ERROR CODIGO de OPERACION invalido" + saltoLinea;
                                    break;
                                case "q6":
                                    imprimirError += "    ERROR OPERANDO invalido" + saltoLinea;
                                    break;
                            }
                        }
                    }
                    break;
                case "q9":
                    imprimirValido += "    se encontro el comando END valido " + saltoLinea;
                    return imprimirValido;
                case "qf":
                    imprimirValido += "   no existe error !Linea Valida!" + saltoLinea;
                    return imprimirValido;
            }
        }
        return imprimirError;
    }

    /**
     * Metodo donde verifica la linea si comienza con espacio en Blanco o
     * Tabulador
     *
     * @return true si el primer caracter de la linea es espacio en Blanco false
     * el primer caracter de linea es difente de Espacio en Blanco
     */
    public boolean VerificaEspacioBlanco() {
        return linea.charAt(0) == ' ' || linea.charAt(0) == '\t';
    }

    /**
     * Separa la linea valida con un fomato de escritura para el archivo.INST
     *
     * @return la linea valida con un formato de impresion si la linea no es
     * valida retorna un null
     */
    public String ImprimirLineasValidas() {
        String cadena = Automata();
        String a = null;
        if (cadena.charAt(0) == 'V') {
            byte numCaracteres = 10;
            int longEtq = etq.length() + 1;
            int longCodop = codop.length() + 1;
            String convNum = Integer.toString(numeroLinea);
            int longnum = convNum.length() + 2;
            a = "# " + convNum;
            for (; longnum < numCaracteres; longnum++) {
                a += " ";
            }
            a += "|| " + etq;
            for (; longEtq < numCaracteres; longEtq++) {
                a += " ";
            }
            a += "|| " + codop;
            for (; longCodop < numCaracteres; longCodop++) {
                a += " ";
            }
            a += "|| " + oper;
        }
        return a;
    }

    /**
     * se toma una linea con Errores y la separa con un formato para escribir en
     * archivo .ERR
     *
     * @return String la linea con error con un formato de impresion si la linea
     * no contiene errores retorna null
     */
    public String ImprimirLineaErrores() {
        String cadena = Automata();
        String b = null;
        if (cadena.charAt(0) == 'E') {
            byte numCaracteres = 10;
            String convNum = Integer.toString(numeroLinea);
            int longnum = convNum.length() + 2;
            b = "# " + convNum;
            for (; longnum < numCaracteres; longnum++) {
                b += " ";
            }
            b += "||" + cadena;
        }
        return b;
    }
}
