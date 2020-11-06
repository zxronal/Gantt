import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

class App {

    private int tiempoSimulado = 0;
    private int valorDeMilisegundo = 50;
    private int milisegundosPermitidos = 1;
    private ArrayList<Proceso> procesos = new ArrayList<Proceso>();
    private ArrayList<Proceso> colaListos = new ArrayList<Proceso>();
    private ArrayList<Proceso> colaBloqueados = new ArrayList<Proceso>();


    void run () {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        /*TODO
         * tiempo de llegada debe ser incremental y el primero siempre 0
         * quatums nesesarios se le piden al usuario
         * numero de entradas y salidas se le piden al usuario y si las ingresa se le debe de pedir quantumsgpu > 1
         * el nombre lo generas (debe de ser unico)
         * estado debe ser 0 inicialmente
         * como minimo debe de haber un proceso
          */
        this.procesos.add(new Proceso(0,2,2,1,"test1",0));
        this.procesos.add(new Proceso(55,3,0,0,"test2",0));
        this.procesos.add(new Proceso(60,1,1,1,"test3",0));
		/*
		/*
		int modoOperacion = 0;

		do {
			System.out.println("Ingrese el modo de operacion debe ser 1 o 2:");
			String numOperacionRead= reader.readLine();
			if (numOperacionRead.matches("^[1-2]$")) {
				modoOperacion = Integer.parseInt(numOperacionRead);
			} else {
				System.out.println("El valor ingresado debe ser 1 o 2");
			}
		} while (modoOperacion == 0); */

        Proceso primerProceso = procesos.get(0);
        agregarProcesoAColaDeListos(primerProceso, 0);

        for (int i = 0; i < colaListos.size(); i++) {
            Proceso procesoListo = colaListos.get(i);
            this.guardarTiempo(procesoListo);
            this.getProcesos();
            if (procesoListo.getQuantumsNecesarios() > 1) {
                this.agregarProcesoAColaDeListosBajarQuantum(procesoListo, 0);
            }
            this.getProcesosDesdeBloqueado();
            this.getProcesosFueraDeRango(i);

            if (procesoListo.getQuantumsNecesarios() == 1 && procesoListo.getNumeroDeEntradasSalidas() != 0 && procesoListo.getEstado() == 0) {
                this.agregarProcesoAColaDeBloqueados(procesoListo);
            }
        }
        mostrarColaDeListo();
        mostrarDiagramaDeGantt();
        calcularTiempoDeVuelta();
        calcularTiempoDeEspera();
    }

    private void getProcesos() {
        for (int i = 0; i < this.procesos.size(); i++) {
            Proceso p = this.procesos.get(i);
            if (p.getTiempoDeLlegada() <= tiempoSimulado && p.getEstado() == 0) {
                agregarProcesoAColaDeListos(p, 0);
            }
        }
    }

    private void getProcesosDesdeBloqueado() {
        for (int i = 0; i < this.colaBloqueados.size(); i++) {
            Proceso p = this.colaBloqueados.get(i);
            if (p.getTiempoDeLlegada() <= tiempoSimulado && p.getEstado() == 0) {
                agregarProcesoAColaDeListos(p, 2);
            }
        }
    }

    private void getProcesosFueraDeRango(int iteracion) {
        if (iteracion == (colaListos.size() - 1)) {
            getMenorProcesoDisponible();
            getMenorBloqueadoDisponible();
        }
    }

    private void getMenorProcesoDisponible() {
        Proceso pr  = null;
        for (int i = 0; i < this.procesos.size(); i++) {
            Proceso p = this.procesos.get(i);
            if (p.getEstado() == 0 && (pr == null || (pr.getTiempoDeLlegada() > p.getTiempoDeLlegada()))) {
                pr = p;
            }
        }
        if (pr != null) {
            this.agregarProcesoAColaDeListos(pr, 0);
        }
    }

    private void getMenorBloqueadoDisponible() {
        Proceso pr  = null;
        for (int i = 0; i < this.colaBloqueados.size(); i++) {
            Proceso p = this.colaBloqueados.get(i);
            if (p.getEstado() == 0 && (pr == null || (pr.getTiempoDeLlegada() > p.getTiempoDeLlegada()))) {
                pr = p;
            }
        }
        if (pr != null) {
            this.agregarProcesoAColaDeListos(pr, 2);
        }
    }

    private void agregarProcesoAColaDeListos(Proceso proceso, int estado) {
        proceso.setEstado(1);
        colaListos.add(new Proceso(proceso.getTiempoDeLlegada(), proceso.getQuantumsNecesarios(), proceso.getNumeroDeEntradasSalidas(), proceso.getQuantumsGpu(), proceso.getNombre(), estado));
    }

    private void agregarProcesoAColaDeListosBajarQuantum(Proceso proceso, int estado) {
        int tiempoDeLlegada = -1;
        if (proceso.getTerminacion() >= tiempoSimulado) {
            tiempoDeLlegada = proceso.getTerminacion();
        }
        colaListos.add(new Proceso(tiempoDeLlegada, proceso.getQuantumsNecesarios() -1, proceso.getNumeroDeEntradasSalidas(), proceso.getQuantumsGpu(), proceso.getNombre(), estado));
    }

    private void guardarTiempo(Proceso proceso) {
        if (proceso.getTiempoDeLlegada() == -1) {
            proceso.setTiempoDeLlegada(tiempoSimulado);
        }
        proceso.setInicio(this.tiempoSimulado);
        // despues mirar como hacer lo de los verdes
        this.tiempoSimulado += this.valorDeMilisegundo;
        proceso.setTerminacion(tiempoSimulado);
    }

// Mirar lo de quatums adicionales gpu
    private void agregarProcesoAColaDeBloqueados(Proceso proceso) {
        int tiempoDeLlegada = tiempoSimulado + (proceso.getNumeroDeEntradasSalidas() * this.valorDeMilisegundo);
        System.out.println("El proceso " + proceso.getNombre() + "estara bloqueado desde el milisegundo " + tiempoSimulado + " hasta " + tiempoDeLlegada);
        this.colaBloqueados.add(new Proceso(tiempoDeLlegada, proceso.getQuantumsNecesarios() + proceso.getQuantumsGpu() -1, proceso.getNumeroDeEntradasSalidas(), proceso.getQuantumsGpu(), proceso.getNombre(), 0));
    }

    private void calcularTiempoDeVuelta() {
        System.out.println();
        Float promedioVuelta = (float) 0.0;
        dibujarRayitas(60);
        System.out.println("TIEMPO DE VUELTA");
        dibujarRayitas(60);
        for (int i = 0; i < this.procesos.size(); i++) {
            Proceso p = this.procesos.get(i);
            Proceso maxP = getProcesoMaxTerminal(p.getNombre());
            Float vuelta = (float) (maxP.getTerminacion() - (p.getNumeroDeEntradasSalidas() * valorDeMilisegundo) - p.getTiempoDeLlegada());
            System.out.println("TV" + p.getNombre() + "= " + vuelta + " Milisegundos");
            promedioVuelta += vuelta;
        }
        System.out.println("Tiempo medio de vuelta= " + (promedioVuelta/this.procesos.size()) + " Milisegundos");
        dibujarRayitas(60);
    }

    private void calcularTiempoDeEspera() {
        System.out.println();
        Float promedioEspera = (float) 0.0;
        dibujarRayitas(60);
        System.out.println("TIEMPO DE ESPERA");
        dibujarRayitas(60);
        for (int i = 0; i < this.procesos.size(); i++) {
            Proceso p = this.procesos.get(i);
            Proceso minP = getProcesoMinTerminal(p.getNombre());
            Float espera = (float) (minP.getInicio() - p.getTiempoDeLlegada());
            System.out.println("W" + p.getNombre() + "= " + espera + " Milisegundos");
            promedioEspera += espera;
        }
        System.out.println("Tiempo promedio de espera= " + (promedioEspera/this.procesos.size()) + " Milisegundos");
        dibujarRayitas(60);
    }

    private void mostrarColaDeListo() {
        System.out.println();
        StringBuilder nombres = new StringBuilder();
        for (int i = 0; i < this.colaListos.size(); i++) {
            Proceso p = this.colaListos.get(i);
            nombres.append(" | ").append(p.getNombre()).append(" / ").append(p.getQuantumsNecesarios());
         }
        nombres.append(" |");
        nombres.deleteCharAt(0);
        int numeroDeRayas = nombres.length();
        dibujarRayitas(numeroDeRayas);
        System.out.println("COLA DE PROCESOS EN ESTADO DE LISTO");
        dibujarRayitas(numeroDeRayas);
        System.out.println(nombres.toString());
        dibujarRayitas(numeroDeRayas);
    }

    private void mostrarDiagramaDeGantt() {
        System.out.println();
        StringBuilder nombres = new StringBuilder();
        for (int i = 0; i < this.colaListos.size(); i++) {
            Proceso p = this.colaListos.get(i);
            nombres.append(" | ").append(p.getInicio()).append(" / ").append(p.getNombre()).append(" / ").append(p.getTerminacion());
        }
        nombres.append(" |");
        nombres.deleteCharAt(0);
        int numeroDeRayas = nombres.length();
        dibujarRayitas(numeroDeRayas);
        System.out.println("DIAGRAMA DE GANTT");
        dibujarRayitas(numeroDeRayas);
        System.out.println(nombres.toString());
        dibujarRayitas(numeroDeRayas);
    }

    private void dibujarRayitas(int numeroDeRayas) {
        for (int i = 0; i < numeroDeRayas; i++) {
            System.out.print("-");
        }
        System.out.println();
    }


    private Proceso getProcesoMaxTerminal(String nombreProceso) {
        Proceso maxProceso = null;
        int terminacion = 0;
        for (int i = 0; i < this.colaListos.size(); i++) {
            Proceso p = this.colaListos.get(i);
            if (p.getTerminacion() > terminacion && p.getNombre().matches(nombreProceso)) {
                terminacion = p.getTerminacion();
                maxProceso = p;
            }
        }
        return maxProceso;
    }

    private Proceso getProcesoMinTerminal(String nombreProceso) {
        for (int i = 0; i < this.colaListos.size(); i++) {
            Proceso p = this.colaListos.get(i);
            if (p.getNombre().matches(nombreProceso)) {
                return p;
            }
        }
        return null;
    }
}