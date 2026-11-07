package aplication;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class Main {
    static final int CAPACIDAD_TOTAL = 10;
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
        int opcion = 0;
        while (opcion != 5) {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opción: ", 1, 5);
            if (opcion == 1) {
                registrarEntrada();
            } else if (opcion == 2) {
                registrarSalida();
            } else if (opcion != 5) {
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

        String tipo = leerTipoVehiculo();
        LocalDateTime ingreso = leerFechaHora("Fecha y hora de ingreso:");

        tipos[celda] = tipo;
        ingresos[celda] = ingreso;
        celdasOcupadas = celdasOcupadas + 1;
        celdasLibres = celdasLibres - 1;

        imprimir("Ingreso registrado en la celda " + (celda + 1));
    }

    static void registrarSalida() {
        int celda = leerEntero("Número de celda (1-" + CAPACIDAD_TOTAL + "): ", 1, CAPACIDAD_TOTAL) - 1;
        if (tipos[celda] == null) {
            imprimir("Esa celda está libre.");
            return;
        }

        LocalDateTime ingreso = ingresos[celda];
        LocalDateTime salida = leerFechaHora("Fecha y hora de salida:");
        while (salida.isBefore(ingreso)) {
            imprimir("La salida no puede ser anterior al ingreso.");
            salida = leerFechaHora("Fecha y hora de salida:");
        }

        String tipo = tipos[celda];
        long total = liquidacion(celda + 1, tipo, ingreso, salida);

        if (tipo.equals(CARRO)) {
            recaudoCarros = recaudoCarros + total;
        } else {
            recaudoMotos = recaudoMotos + total;
        }

        tipos[celda] = null;
        ingresos[celda] = null;
        celdasOcupadas = celdasOcupadas - 1;
        celdasLibres = celdasLibres + 1;
    }


    static long contarDiasCompletos(LocalDateTime ingreso, LocalDateTime salida) {
        return ChronoUnit.DAYS.between(ingreso, salida);
    }

    static long calcularHorasRestantes(LocalDateTime cursor, LocalDateTime salida) {
        long minutosRestantes = Duration.between(cursor, salida).toMinutes();
        return (minutosRestantes + 59) / 60;
    }

    static long liquidacion(int numeroCelda, String tipo, LocalDateTime ingreso, LocalDateTime salida) {
        long dias = contarDiasCompletos(ingreso, salida);
        LocalDateTime cursor = ingreso.plusDays(dias);
        long horas = calcularHorasRestantes(cursor, salida);

        if (horas >= UMBRAL_HORAS_DIA) {
            dias = dias + 1;
            horas = 0;
        }

        long tarifaDia;
        long tarifaHora;
        if (tipo.equals(CARRO)) {
            tarifaDia = TARIFA_DIA_CARRO;
            tarifaHora = TARIFA_HORA_CARRO;
        } else {
            tarifaDia = TARIFA_DIA_MOTO;
            tarifaHora = TARIFA_HORA_MOTO;
        }
        long total = dias * tarifaDia + horas * tarifaHora;

        imprimir("");
        imprimir("======= FACTURA =======");
        imprimir("Celda liberada: " + numeroCelda);
        imprimir("Tipo: " + tipo);
        imprimir("Ingreso: " + ingreso.format(FORMATO_FECHA_HORA));
        imprimir("Salida: " + salida.format(FORMATO_FECHA_HORA));
        if (dias > 0) {
            imprimir(dias + " día(s) x $" + tarifaDia + " = $" + (dias * tarifaDia));
        }
        if (horas > 0) {
            imprimir(horas + " hora(s) x $" + tarifaHora + " = $" + (horas * tarifaHora));
        }
        imprimir("TOTAL A PAGAR: $" + total);
        imprimir("=======================");
        return total;
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

    static int leerDigitos(String texto, int inicio, int cantidad) {
        int valor = 0;
        for (int i = inicio; i < inicio + cantidad; i++) {
            char caracter = texto.charAt(i);
            if (caracter < '0' || caracter > '9') {
                return -1;
            }
            valor = valor * 10 + caracter - '0';
        }
        return valor;
    }

    static int leerEntero(String mensaje, int minimo, int maximo) {
        int valor = -1;
        boolean valido = false;
        while (!valido) {
            System.out.print(mensaje);
            String texto = SCANNER.nextLine().trim();
            if (texto.length() > 0 && texto.length() <= 9) {
                valor = leerDigitos(texto, 0, texto.length());
                if (valor >= minimo && valor <= maximo) {
                    valido = true;
                }
            }
            if (!valido) {
                imprimir("Dato inválido. Escriba un número entre " + minimo + " y " + maximo + ".");
            }
        }
        return valor;
    }

    static String leerTipoVehiculo() {
        int opcion = leerEntero("Tipo de vehículo (1 = carro, 2 = moto): ", 1, 2);
        if (opcion == 1) {
            return CARRO;
        } else {
            return MOTO;
        }
    }

    static int diasDelMes(int anio, int mes) {
        if (mes == 2) {
            if ((anio % 4 == 0 && anio % 100 != 0) || anio % 400 == 0) {
                return 29;
            }
            return 28;
        }
        if (mes == 4 || mes == 6 || mes == 9 || mes == 11) {
            return 30;
        }
        return 31;
    }


    static LocalDateTime leerFechaHora(String mensaje) {
        imprimir(mensaje);
        int anio = leerEntero("Año (2000-2100): ", 2000, 2100);
        int mes = leerEntero("Mes (1-12): ", 1, 12);
        int ultimoDia = diasDelMes(anio, mes);
        int dia = leerEntero("Día (1-" + ultimoDia + "): ", 1, ultimoDia);
        int hora = leerEntero("Hora (" + HORA_APERTURA + "-" + HORA_CIERRE + "): ", HORA_APERTURA, HORA_CIERRE);
        int minuto = 0;
        if (hora != HORA_CIERRE) {
            minuto = leerEntero("Minuto (0-59): ", 0, 59);
        }
        return LocalDateTime.of(anio, mes, dia, hora, minuto);
    }








}





