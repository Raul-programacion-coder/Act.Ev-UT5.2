public class Main {

    public static void main(String[] args) {


    }


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