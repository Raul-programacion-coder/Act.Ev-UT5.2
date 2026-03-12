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

    private static Map<String, String> usuarios = new HashMap<>();
    private static DefaultListModel<Nota> modeloNotas = new DefaultListModel<>();
    private static String usuarioActual = null;

    public static void main(String[] args) {

        cargarUsuarios();
        mostrarLogin();

    }

    private static void mostrarLogin(){
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


    private static void mostrarGestorNotas(){
        JFrame ventana = new JFrame("Gestor de Notas - Usuario: " + usuarioActual);
        ventana.setSize(900,600);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10,10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Lista de notas
        JList<Nota> listaNotas = new JList<>(modeloNotas);
        JScrollPane scrollLista = new JScrollPane(listaNotas);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Lista de notas"));
        scrollLista.setPreferredSize(new Dimension(200,0));
        panelPrincipal.add(scrollLista, BorderLayout.WEST);

        // Editor
        JPanel panelEditor = new JPanel(new BorderLayout(5,5));
        JTextField campoTitulo = new JTextField();
        campoTitulo.setBorder(BorderFactory.createTitledBorder("Título"));
        JTextArea areaContenido = new JTextArea();
        areaContenido.setLineWrap(true);
        areaContenido.setWrapStyleWord(true);
        JScrollPane scrollContenido = new JScrollPane(areaContenido);
        scrollContenido.setBorder(BorderFactory.createTitledBorder("Contenido"));

        panelEditor.add(campoTitulo, BorderLayout.NORTH);
        panelEditor.add(scrollContenido, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new GridLayout(1,5,5,5));
        JButton btnCrear = new JButton("Crear");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnCerrarSesion = new JButton("Cerrar sesión");

        panelBotones.add(btnCrear);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCerrarSesion);

        panelEditor.add(panelBotones, BorderLayout.SOUTH);
        panelPrincipal.add(panelEditor, BorderLayout.CENTER);

        // Buscador
        JPanel panelBuscador = new JPanel(new BorderLayout(5,5));
        panelBuscador.setBorder(BorderFactory.createTitledBorder("Buscar nota"));
        JTextField campoBuscar = new JTextField();
        panelBuscador.add(campoBuscar, BorderLayout.CENTER);
        panelPrincipal.add(panelBuscador, BorderLayout.NORTH);

        // Logs
        JTextArea areaLogs = new JTextArea(5,50);
        areaLogs.setEditable(false);
        areaLogs.setLineWrap(true);
        areaLogs.setWrapStyleWord(true);
        JScrollPane scrollLogs = new JScrollPane(areaLogs);
        scrollLogs.setBorder(BorderFactory.createTitledBorder("Registro de acciones"));
        panelPrincipal.add(scrollLogs, BorderLayout.SOUTH);

        ventana.add(panelPrincipal);
        ventana.setVisible(true);

        // ================= FUNCIONALIDADES =================

        // Crear
        btnCrear.addActionListener(e -> {
            String titulo = campoTitulo.getText().trim();
            String contenido = areaContenido.getText().trim();
            if(titulo.isEmpty()) { JOptionPane.showMessageDialog(ventana,"Título vacío","Error",JOptionPane.ERROR_MESSAGE); return; }
            Nota nueva = new Nota(titulo, contenido);
            modeloNotas.addElement(nueva);
            guardarNotas(usuarioActual);
            areaLogs.append("Nota creada: " + titulo + "\n");
            limpiarCampos(campoTitulo, areaContenido);
        });

        // Seleccionar nota
        listaNotas.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                Nota n = listaNotas.getSelectedValue();
                if(n != null) {
                    campoTitulo.setText(n.getTitulo());
                    areaContenido.setText(n.getContenido());
                    areaLogs.append("Nota cargada: " + n.getTitulo() + "\n");
                }
            }
        });

        // Editar
        btnEditar.addActionListener(e -> {
            int index = listaNotas.getSelectedIndex();
            if(index == -1) { JOptionPane.showMessageDialog(ventana,"Seleccione una nota","Error",JOptionPane.WARNING_MESSAGE); return; }
            Nota n = modeloNotas.get(index);
            String nuevoTitulo = campoTitulo.getText().trim();
            String nuevoContenido = areaContenido.getText().trim();
            if(nuevoTitulo.isEmpty()) { JOptionPane.showMessageDialog(ventana,"Título vacío","Error",JOptionPane.ERROR_MESSAGE); return; }
            n.setTitulo(nuevoTitulo);
            n.setContenido(nuevoContenido);
            modeloNotas.set(index, n);
            guardarNotas(usuarioActual);
            areaLogs.append("Nota editada: " + nuevoTitulo + "\n");
        });

        // Eliminar
        btnEliminar.addActionListener(e -> {
            int index = listaNotas.getSelectedIndex();
            if(index == -1) { JOptionPane.showMessageDialog(ventana,"Seleccione una nota","Error",JOptionPane.WARNING_MESSAGE); return; }
            Nota n = modeloNotas.get(index);
            modeloNotas.remove(index);
            guardarNotas(usuarioActual);
            areaLogs.append("Nota eliminada: " + n.getTitulo() + "\n");
            limpiarCampos(campoTitulo, areaContenido);
        });

        // Limpiar campos
        btnLimpiar.addActionListener(e -> {
            limpiarCampos(campoTitulo, areaContenido);
            areaLogs.append("Campos limpiados\n");
        });

        // Cerrar sesión
        btnCerrarSesion.addActionListener(e -> {
            guardarNotas(usuarioActual);
            usuarioActual = null;
            modeloNotas.clear();
            ventana.dispose();
            mostrarLogin();
        });

        // Buscar en tiempo real
        campoBuscar.getDocument().addDocumentListener(new DocumentListener() {
            private void filtrar() {
                String filtro = campoBuscar.getText().trim().toLowerCase();
                if(filtro.isEmpty()) {
                    listaNotas.setModel(modeloNotas);
                } else {
                    DefaultListModel<Nota> filtrado = new DefaultListModel<>();
                    for(int i=0;i<modeloNotas.size();i++){
                        Nota n = modeloNotas.get(i);
                        if(n.getTitulo().toLowerCase().contains(filtro)) filtrado.addElement(n);
                    }
                    listaNotas.setModel(filtrado);
                }
            }
            @Override public void insertUpdate(DocumentEvent e){ filtrar(); }
            @Override public void removeUpdate(DocumentEvent e){ filtrar(); }
            @Override public void changedUpdate(DocumentEvent e){ filtrar(); }
        });
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