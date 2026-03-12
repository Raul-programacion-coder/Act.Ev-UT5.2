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

    public static void main(String[] args) {


    }

    private static final String USUARIOS_FILE = "usuarios.txt";
    private static final String NOTAS_DIR = "notas/";

    private static Map<String, String> usuarios = new HashMap<>(); // usuario -> hash
    private static DefaultListModel<Nota> modeloNotas = new DefaultListModel<>();
    private static String usuarioActual = null;
}

private static void mostrarLogin() {
    JFrame login = new JFrame("Login / Registro");
    login.setSize(800, 300);
    login.setLocationRelativeTo(null);
    login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel panel = new JPanel(new GridLayout(4,1,5,5));
    panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

    JTextField campoUsuario = new JTextField();
    JPasswordField campoPass = new JPasswordField();
    JButton btnLogin = new JButton("Iniciar sesión");
    JButton btnRegistro = new JButton("Registrarse");
    JLabel lblMensaje = new JLabel("", SwingConstants.CENTER);

    panel.add(new JLabel("Usuario:"));
    panel.add(campoUsuario);
    panel.add(new JLabel("Contraseña:"));
    panel.add(campoPass);

    JPanel panelBotones = new JPanel(new GridLayout(1,2,5,5));
    panelBotones.add(btnLogin);
    panelBotones.add(btnRegistro);
    panel.add(panelBotones);

    panel.add(lblMensaje);

    login.add(panel);
    login.setVisible(true);

    // Login
    btnLogin.addActionListener(e -> {
        String usuario = campoUsuario.getText().trim();
        String pass = new String(campoPass.getPassword()).trim();
        if(usuario.isEmpty() || pass.isEmpty()) {
            lblMensaje.setText("Usuario o contraseña vacíos");
            return;
        }
        String hash = usuarios.get(usuario);
        if(hash != null && hash.equals(hashPassword(pass))) {
            usuarioActual = usuario;
            login.dispose();
            cargarNotas(usuarioActual);
            mostrarGestorNotas();
        } else {
            lblMensaje.setText("Usuario o contraseña incorrectos");
        }
    });

    // Registro
    btnRegistro.addActionListener(e -> {
        String usuario = campoUsuario.getText().trim();
        String pass = new String(campoPass.getPassword()).trim();
        if(usuario.isEmpty() || pass.isEmpty()) {
            lblMensaje.setText("Usuario o contraseña vacíos");
            return;
        }
        if(usuarios.containsKey(usuario)) {
            lblMensaje.setText("Usuario ya registrado");
            return;
        }
        usuarios.put(usuario, hashPassword(pass));
        guardarUsuarios();
        // Crear archivo de notas vacío
        File dir = new File(NOTAS_DIR);
        if(!dir.exists()) dir.mkdir();
        try { new File(NOTAS_DIR + usuario + ".txt").createNewFile(); } catch(Exception ex) {}
        lblMensaje.setText("Usuario registrado, ya puede iniciar sesión");
    });
}


// ===================== CLASE NOTA =====================
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