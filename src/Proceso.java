import java.util.ArrayList;
import java.util.List;

class Proceso {
    private int tiempoDeLlegada;
    private double quantumsNecesarios;
    private List<EntradaSalida> entradasYsalidas = new ArrayList<EntradaSalida>();
    private int inicio;
    private int terminacion;
    // estado 0 no ha entrado, 1 ya entro, 2 viene de bloqueado y no puede agregar mas procesos a bloqueado, 3 es una interrupcion,4 sifnifica q tiene q hacer otra entrada y salida
    private int estado = 0;
    // nombre unico
    private String nombre;

    public Proceso() {
    }

    public Proceso(int tiempoDeLlegada, double quantumsNecesarios, List<EntradaSalida> entradaSalidas, String nombre, int estado) {
        this.tiempoDeLlegada = tiempoDeLlegada;
        this.quantumsNecesarios = quantumsNecesarios;
        this.entradasYsalidas = entradaSalidas;
        this.nombre = nombre;
        this.estado = estado;
    }

    public int getTiempoDeLlegada() {
        return tiempoDeLlegada;
    }

    public void setTiempoDeLlegada(int tiempoDeLlegada) {
        this.tiempoDeLlegada = tiempoDeLlegada;
    }

    public double getQuantumsNecesarios() {
        return quantumsNecesarios;
    }

    public void setQuantumsNecesarios(double quantumsNecesarios) {
        this.quantumsNecesarios = quantumsNecesarios;
    }

    public List<EntradaSalida> getEntradasYsalidas() {
        return entradasYsalidas;
    }

    public void setEntradasYsalidas(List<EntradaSalida> entradasYsalidas) {
        this.entradasYsalidas = entradasYsalidas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getInicio() {
        return inicio;
    }

    public void setInicio(int inicio) {
        this.inicio = inicio;
    }

    public int getTerminacion() {
        return terminacion;
    }

    public void setTerminacion(int terminacion) {
        this.terminacion = terminacion;
    }
}

class EntradaSalida {
    public int quantumsEntradaSalida  = 0;
    public int quantumsGpu = 0;

    public EntradaSalida(int quantumsEntradaSalida, int quantumsGpu) {
        this.quantumsEntradaSalida = quantumsEntradaSalida;
        this.quantumsGpu = quantumsGpu;
    }

    public EntradaSalida() {
    }
}