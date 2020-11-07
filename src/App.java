import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;

class App {

    private int tiempoSimulado = 0;
    private int valorDeMilisegundo = 0;
    // valor de intercaambio 0.2
    private int valorIntercambio = 0;
    private double valorIntercambioQuantun = 0;
    private ArrayList<Proceso> procesos = new ArrayList<Proceso>();
    private ArrayList<Proceso> colaDeGantt = new ArrayList<Proceso>();
    private ArrayList<Proceso> colaBloqueados = new ArrayList<Proceso>();
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


    void run () throws IOException {

        do {
            System.out.println("Ingrese el valor del quantum en milisegundos:");
            String valorRead= reader.readLine();
            if (valorRead.matches("^[1-9][0-9]*$")) {
                valorDeMilisegundo = Integer.parseInt(valorRead);
            } else {
                System.out.println("El valor del quatum debe ser entero, positivo y mayor a 0.");
                System.out.println();
            }
        } while (valorDeMilisegundo == 0);

        do {
            System.out.println("Ingrese el valor del intercambio en milisegundos:");
            String valorRead= reader.readLine();
            if (valorRead.matches("^[1-9][0-9]*$")) {
                valorIntercambio = Integer.parseInt(valorRead);
            } else {
                System.out.println("El valor del intercambio debe ser entero, positivo y mayor a 0.");
                System.out.println();
            }
        } while (valorIntercambio == 0);


        valorIntercambioQuantun = ((double)valorIntercambio / (double)valorDeMilisegundo);

        int numeroDeProcesos = 0;
		do {
			System.out.println("Ingrese el numero de procesos:");
			String numOperacionRead= reader.readLine();
			if (numOperacionRead.matches("^[1-5]$")) {
				numeroDeProcesos = Integer.parseInt(numOperacionRead);
			} else {
				System.out.println("El numero de procesos debe de estar entre 1 o 5 y debe ser entero.");
                System.out.println();
			}
		} while (numeroDeProcesos == 0);

		int tiempoDeLlegadaActual = 0;
		for (int i = 0; i < numeroDeProcesos; i++) {
           Proceso proceso = new Proceso();
           String nombre = "P" + i;
           proceso.setNombre(nombre);
           boolean tiempoDeLlegadaAsignado = false;
           if (i == 0) {
               tiempoDeLlegadaAsignado = true;
               proceso.setTiempoDeLlegada(0);
               System.out.println("Se asigno 0 como tiempo de llegada al proceso " + nombre);
               System.out.println();
           }

            while (!tiempoDeLlegadaAsignado) {
                System.out.println("Porfavor Ingresa el tiempo de llegada para el proceso " + nombre);
                String valorRead = reader.readLine();
                if (valorRead.matches("^[0-9]*$")) {
                    int valor = Integer.parseInt(valorRead);
                    if (valor >= tiempoDeLlegadaActual) {
                        proceso.setTiempoDeLlegada(valor);
                        tiempoDeLlegadaActual = valor;
                        tiempoDeLlegadaAsignado = true;
                    } else {
                        System.out.println("El valor del tiempo de llegadas debe ser positivo, entero y mayor o igual a " + tiempoDeLlegadaActual + ".");
                        System.out.println();
                    }

                } else {
                    System.out.println("El valor del tiempo de llegadas debe ser positivo, entero y mayor o igual a " + tiempoDeLlegadaActual + ".");
                    System.out.println();
                }
            }

            boolean quantumsNecesariosAsignado = false;

            do {
                System.out.println("Ingrese los quantums necesarios para el proceso " + nombre);
                String valorRead= reader.readLine();
                if (valorRead.matches("^[1-9][0-9]*$")) {
                    proceso.setQuantumsNecesarios(Integer.parseInt(valorRead));
                    quantumsNecesariosAsignado = true;
                } else {
                    System.out.println("El valor del tiempo de quantums necesarios debe ser positivo, entero y mayor a 0.");
                    System.out.println();
                }
            } while (!quantumsNecesariosAsignado);

            boolean numeroDeEntradasSalidasAsignado = false;

            do {
                System.out.println("Ingrese el numero de entradas y salidas para el proceso " + nombre);
                String valorRead= reader.readLine();
                if (valorRead.matches("^[0-9]*$")) {
                    proceso.setNumeroDeEntradasSalidas(Integer.parseInt(valorRead));
                    numeroDeEntradasSalidasAsignado = true;
                } else {
                    System.out.println("El valor de las entradas y salidas debe ser positivo y entero.");
                    System.out.println();
                }
            } while (!numeroDeEntradasSalidasAsignado);

            boolean gpuAsignado = false;
            while (proceso.getNumeroDeEntradasSalidas() >= 1 && !gpuAsignado) {
                System.out.println("Ingrese el numero de quatumns gpu para el proceso " + nombre);
                String valorRead= reader.readLine();
                if (valorRead.matches("^[1-9][0-9]*$")) {
                    proceso.setQuantumsGpu(Integer.parseInt(valorRead));
                    gpuAsignado = true;
                } else {
                    System.out.println("El valor de quantums gpu debe ser positivo, entero y mayor a 0.");
                    System.out.println();
                }
            }
            procesos.add(proceso);
        }

        Proceso primerProceso = procesos.get(0);
        agregarProcesoAColaDeListos(primerProceso, 0);

        for (int i = 0; i < colaDeGantt.size(); i++) {
            Proceso procesoListo = colaDeGantt.get(i);
            if (procesoListo.getEstado() != 3) {
                this.guardarTiempo(procesoListo, i);
            }
            this.getProcesos();
            if (procesoListo.getQuantumsNecesarios() > 1 && procesoListo.getEstado() != 3) {
                this.agregarProcesoAColaDeListosBajarQuantum(procesoListo, 0);
            }
            this.getProcesosDesdeBloqueado();
            this.getProcesosFueraDeRango(i);

            if (procesoListo.getQuantumsNecesarios() == 1 && procesoListo.getNumeroDeEntradasSalidas() != 0 && procesoListo.getEstado() == 0) {
                this.agregarProcesoAColaDeBloqueados(procesoListo);
            }
        }

        this.colaDeGantt.remove(colaDeGantt.size() -1);
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
        if (iteracion == (colaDeGantt.size() - 1)) {
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
            tiempoSimulado = pr.getTiempoDeLlegada();
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
            tiempoSimulado = pr.getTiempoDeLlegada();
            this.agregarProcesoAColaDeListos(pr, 2);
        }
    }

    private void agregarProcesoAColaDeListos(Proceso proceso, int estado) {
        proceso.setEstado(1);
        colaDeGantt.add(new Proceso(proceso.getTiempoDeLlegada(), proceso.getQuantumsNecesarios(), proceso.getNumeroDeEntradasSalidas(), proceso.getQuantumsGpu(), proceso.getNombre(), estado));
    }

    private void agregarIntercambio(int i) {
        Proceso quantium = new Proceso(tiempoSimulado, valorIntercambioQuantun, 0, 0, "intercambio", 3);
        quantium.setInicio(this.tiempoSimulado);
        this.tiempoSimulado += valorIntercambio;
        quantium.setTerminacion(tiempoSimulado);
        colaDeGantt.add(i+1 ,quantium);
    }

    private void agregarProcesoAColaDeListosBajarQuantum(Proceso proceso, int estado) {
        int tiempoDeLlegada = -1;
        if (proceso.getTerminacion() >= tiempoSimulado) {
            tiempoDeLlegada = proceso.getTerminacion();
        }
        colaDeGantt.add(new Proceso(tiempoDeLlegada, proceso.getQuantumsNecesarios() -1, proceso.getNumeroDeEntradasSalidas(), proceso.getQuantumsGpu(), proceso.getNombre(), estado));
    }

    private void guardarTiempo(Proceso proceso, int i) {
        if (proceso.getTiempoDeLlegada() == -1) {
            proceso.setTiempoDeLlegada(tiempoSimulado);
        }
        proceso.setInicio(this.tiempoSimulado);
        // despues mirar como hacer lo de los verdes
        this.tiempoSimulado += this.valorDeMilisegundo;
        proceso.setTerminacion(tiempoSimulado);
        this.agregarIntercambio(i);
    }

// Mirar lo de quatums adicionales gpu
    private void agregarProcesoAColaDeBloqueados(Proceso proceso) {
        int tiempoDeLlegada = proceso.getTerminacion() + (proceso.getNumeroDeEntradasSalidas() * this.valorDeMilisegundo);
        System.out.println("El proceso " + proceso.getNombre() + "estara bloqueado desde el milisegundo " + tiempoSimulado + " hasta " + tiempoDeLlegada);
        this.colaBloqueados.add(new Proceso(tiempoDeLlegada, proceso.getQuantumsNecesarios() + proceso.getQuantumsGpu() -1, proceso.getNumeroDeEntradasSalidas(), proceso.getQuantumsGpu(), proceso.getNombre(), 0));
        ordenarColaDeBloqueadosPorOrdenDeLLegada();
    }

    private void ordenarColaDeBloqueadosPorOrdenDeLLegada() {
        this.colaBloqueados.sort(new Comparator<Proceso>() {
            @Override
            public int compare(Proceso proceso, Proceso t1) {
                return Integer.compare(proceso.getTiempoDeLlegada(), t1.getTiempoDeLlegada());
            }
        });
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
        for (int i = 0; i < this.colaDeGantt.size(); i++) {
            Proceso p = this.colaDeGantt.get(i);
            if (!p.getNombre().matches("intercambio" )) {
                nombres.append(" | ").append(p.getNombre()).append(" / ").append(p.getQuantumsNecesarios());
            }
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
        for (int i = 0; i < this.colaDeGantt.size(); i++) {
            Proceso p = this.colaDeGantt.get(i);
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
        for (int i = 0; i < this.colaDeGantt.size(); i++) {
            Proceso p = this.colaDeGantt.get(i);
            if (p.getTerminacion() > terminacion && p.getNombre().matches(nombreProceso)) {
                terminacion = p.getTerminacion();
                maxProceso = p;
            }
        }
        return maxProceso;
    }

    private Proceso getProcesoMinTerminal(String nombreProceso) {
        for (int i = 0; i < this.colaDeGantt.size(); i++) {
            Proceso p = this.colaDeGantt.get(i);
            if (p.getNombre().matches(nombreProceso)) {
                return p;
            }
        }
        return null;
    }
}
