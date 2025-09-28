/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package excepciones;

public class DatosInconsistentesException extends Exception {
    public DatosInconsistentesException(String message) {
        super(message);
    }

    public DatosInconsistentesException(String message, Throwable cause) {
        super(message, cause);
    }
}
