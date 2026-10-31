package aplication;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class Main {
    static final int CAPACIDAD_TOTAL = 0;
    static final int TARIFA_HORA_CARRO = 4000;
    static final int TARIFA_DIA_CARRO = 30000;
    static final int TARIFA_HORA_MOTO = 2000;
    static final int TARIFA_DIA_MOTO = 15000;
    static final int UMBRAL_HORAS_DIA = 8;
    static final int HORA_APERTURA = 8;
    static final int HORA_CIERRE = 22;

    static final String CARRO = "CARRO";
    static final String MOTO = "MOTO";

    static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    static final Scanner SCANNER = new Scanner(System.in);

    static String[] tipos = new String[CAPACIDAD_TOTAL];
    static LocalDateTime[] ingresos = new LocalDateTime[CAPACIDAD_TOTAL];

    static int celdasOcupadas = 0;
    static int celdasLibres = CAPACIDAD_TOTAL;
    static long recaudoCarros = 0;
    static long recaudoMotos = 0;



    public static void main(String[] args) {
        inicializarArreglos();
        String opcion = "";
        while (!opcion.equals("5")) {
            mostrarMenu();
            System.out.print("Seleccione una opción: ");
            opcion = SCANNER.nextLine();
            if (opcion.equals("1")) {
                registrarEntrada();
            } else if (!opcion.equals("5")) {
                imprimir("Esa opción todavía no está lista.");
            }
        }
        SCANNER.close();
    }

    static void inicializarArreglos() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            tipos[i] = null;
            ingresos[i] = null;
        }
    }
    static void imprimir(String mensaje) {
        System.out.println(mensaje);
    }

    static void mostrarMenu() {
        imprimir("PARQUEADERO");
        imprimir("1. Registrar entrada");
        imprimir("2. Registrar salida");
        imprimir("3. Reporte de ocupación");
        imprimir("4. Reporte financiero");
        imprimir("5. Salir");
    }

    static void registrarEntrada() {
        int celda = celdaDisponible();
        if (celda == -1) {
            imprimir("El parqueadero está lleno.");
            return;
        }

        System.out.print("Tipo de vehículo (carro o moto): ");
        String tipo = SCANNER.nextLine();
        System.out.print("Fecha y hora de ingreso (yyyy-MM-dd HH:mm): ");
        String texto = SCANNER.nextLine();
        LocalDateTime ingreso = LocalDateTime.parse(texto, FORMATO_FECHA_HORA);

        tipos[celda] = tipo;
        ingresos[celda] = ingreso;
        celdasOcupadas = celdasOcupadas + 1;
        celdasLibres = celdasLibres - 1;

        imprimir("Ingreso registrado en la celda " + (celda + 1));
    }

    static int celdaDisponible() {
        int celda = -1;
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            if (tipos[i] == null && celda == -1) {
                celda = i;
            }
        }
        return celda;
    }





}





