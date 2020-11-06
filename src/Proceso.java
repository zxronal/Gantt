class Proceso {
    private int tiempoDeLlegada;
    private double quantumsNecesarios;
    private int numeroDeEntradasSalidas = 0;
    private int quantumsGpu;
    private int inicio;
    private int terminacion;
    // estado 0 no ha entrado, 1 ya entro
    private int estado = 0;
    // nombre unico
    private String nombre;

    public Proceso() {
    }

    public Proceso(int tiempoDeLlegada, double quantumsNecesarios, int numeroDeEntradasSalidas, int quantumsGpu, String nombre, int estado) {
        this.tiempoDeLlegada = tiempoDeLlegada;
        this.quantumsNecesarios = quantumsNecesarios;
        this.numeroDeEntradasSalidas = numeroDeEntradasSalidas;
        this.quantumsGpu = quantumsGpu;
        this.nombre = nombre;
        this.estado = estado;
    }
/*
    public Proceso(int quantumsNecesarios, int numeroDeEntradasSalidas, int quantumsGpu, String nombre, int estado) {
        this.quantumsNecesarios = quantumsNecesarios;
        this.numeroDeEntradasSalidas = numeroDeEntradasSalidas;
        this.quantumsGpu = quantumsGpu;
        this.nombre = nombre;

    }
*/
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

    public int getNumeroDeEntradasSalidas() {
        return numeroDeEntradasSalidas;
    }

    public void setNumeroDeEntradasSalidas(int numeroDeEntradasSalidas) {
        this.numeroDeEntradasSalidas = numeroDeEntradasSalidas;
    }

    public int getQuantumsGpu() {
        return quantumsGpu;
    }

    public void setQuantumsGpu(int quantumsGpu) {
        this.quantumsGpu = quantumsGpu;
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