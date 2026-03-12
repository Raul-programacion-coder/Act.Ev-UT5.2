import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final String USUARIOS_FILE = "usuarios.txt";
    private static final String NOTAS_DIR = "notas/";

    private static Map<String, String> usuarios = new HashMap<>(); // usuario -> hash
    private static DefaultListModel<Nota> modeloNotas = new DefaultListModel<>();
    private static String usuarioActual = null;

    public static void main(String[] args) {

        cargarUsuarios();
        mostrarLogin();

    }

}
class Nota {
    private String titulo;
    private String contenido;

    public Nota(String t, String c){ titulo=t; contenido=c; }
    public String getTitulo(){ return titulo; }
    public void setTitulo(String t){ titulo=t; }
    public String getContenido(){ return contenido; }
    public void setContenido(String c){ contenido=c; }


    @Override
    public String toString(){ return titulo; }
}