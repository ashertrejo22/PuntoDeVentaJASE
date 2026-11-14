package org.uacm.puntodeventa.Clases;

public class ConvierteNumeroLetra {

    private static final String[] UNIDADES = {
        "", "UN", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE",
        "DIEZ", "ONCE", "DOCE", "TRECE", "CATORCE", "QUINCE", "DIECISEIS", "DIECISIETE", "DIECIOCHO", "DIECINUEVE", "VEINTE"
    };

    private static final String[] DECENAS = {
        "", "", "VEINTI", "TREINTA", "CUARENTA", "CINCUENTA", "SESENTA", "SETENTA", "OCHENTA", "NOVENTA"
    };

    private static final String[] CENTENAS = {
        "", "CIENTO", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS", "QUINIENTOS", 
        "SEISCIENTOS", "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS"
    };

    public static String convertir(double monto) {
        if (monto < 0) {
            return "MENOS CERO PESOS 00/100 M.N.";
        }

        long parteEntera = (long) monto;
        int centavos = (int) Math.round((monto - parteEntera) * 100);

        if (parteEntera == 0) {
            return "CERO PESOS " + String.format("%02d", centavos) + "/100 M.N.";
        }

        String resultado = convertirNumero(parteEntera);
        
        // Ajuste gramatical para el "UN" al final si aplica
        if (resultado.endsWith("UN")) {
            resultado = resultado + " PESO";
        } else {
            resultado = resultado + " PESOS";
        }

        return String.format("(%s %02d/100 M.N.)", resultado, centavos);
    }

    private static String convertirNumero(long n) {
        if (n == 0) return "";
        if (n == 100) return "CIEN";
        
        if (n <= 20) {
            return UNIDADES[(int) n];
        }
        
        if (n < 30) {
            return "VEINTI" + UNIDADES[(int) (n - 20)];
        }
        
        if (n < 100) {
            long decena = n / 10;
            long unidad = n % 10;
            if (unidad == 0) {
                return DECENAS[(int) decena];
            }
            return DECENAS[(int) decena] + " Y " + UNIDADES[(int) unidad];
        }
        
        if (n < 1000) {
            long centena = n / 100;
            long resto = n % 100;
            String c = CENTENAS[(int) centena];
            if (n == 100) return "CIEN";
            if (resto == 0) return c;
            return c + " " + convertirNumero(resto);
        }
        
        if (n < 1000000) {
            long miles = n / 1000;
            long resto = n % 1000;
            String m = "";
            if (miles == 1) {
                m = "MIL";
            } else {
                m = convertirNumero(miles) + " MIL";
            }
            if (resto == 0) return m;
            return m + " " + convertirNumero(resto);
        }
        
        if (n < 1000000000L) {
            long millones = n / 1000000;
            long resto = n % 1000000;
            String milStr = (millones == 1) ? "UN MILLON" : convertirNumero(millones) + " MILLONES";
            if (resto == 0) return milStr;
            return milStr + " " + convertirNumero(resto);
        }
        
        return "NUMERO FUERA DE RANGO";
    }
}