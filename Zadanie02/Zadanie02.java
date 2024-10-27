import java.util.List;
import java.util.ArrayList;

class Zadanie02 {

    public Zadanie02() {
    }

    public List<Integer> code(int length, int par1, String op, int par2) {
        String a = String.format("%4s", Integer.toBinaryString(length)).replace(' ', '0');
        String b = convertToBinary(par1, length);
        String c = codeOperation(op);
        String d = convertToBinary(par2, length);

        // Jesli ktorakolwiek z liczb nie miesci się w zadanej dlugosci, zwroc pusta liste
        if (b == null || d == null) {
            return new ArrayList<>();
        }

        String binary = a + b + c + d;

        List<Integer> binaryList = new ArrayList<>();
        for (char bit : binary.toCharArray()) {
            binaryList.add(Character.getNumericValue(bit));
        }
        return binaryList;
    }

    public List<Integer> code(int par1, String op, int par2) {
        int length = getMinBits(par1, par2);
        return code(length, par1, op, par2);
    }

    private String codeOperation(String op) {
        switch (op) {
            case "+":
                return "00";
            case "-":
                return "01";
            case "*":
                return "10";
            case "/":
                return "11";
            default:
                return "";
        }
    }

    private String convertToBinary(int number, int length) {
        try {
            // Pierwszy bit to znak (0 dodatnia, 1 ujemna)
            String sign = number >= 0 ? "0" : "1";

            // Konwertujemy wartosc bezwzgledna na format binarny
            String magnitude = String.format("%" + (length-1) + "s",
                    Integer.toBinaryString(Math.abs(number))).replace(' ', '0');

            // Sprawdzamy czy liczba miesci się w zadanej dlugosci (minus bit znaku)
            if (magnitude.length() > length-1) {
                return null;
            }

            return sign + magnitude;
        } catch (Exception e) {
            return null;
        }
    }

    public static int getMinBits(int par1, int par2) {
        // Sprawdzamy ktora z liczb jest wieksza abysmy wiedzieli ile bitow maksymalnie potrzebujemy
        // teraz aby miec minimalna liczbe bitow potrzebujemy znac floor(log2(max_value)) + 1 (+1 bo liczymy od 0)
        // logarytmujemy aby wiedziec ktora potege dwojki potrzebujemy
        // poniewaz nie ma funkcji wbudowanej zwracajacej log2, uzywamy mocy matematyki aby obliczyc go sami

        int absVal1 = Math.abs(par1);
        int absVal2 = Math.abs(par2);

        // sprawdzamy czy potrzebujemy bitu na znak
        boolean needSignBit = (par1 < 0 || par2 < 0);

        int maxValue = Math.max(absVal1, absVal2);

        double log2 = Math.log(maxValue) / Math.log(2);
        int length = (int) Math.floor(log2) + 1;

        // zwracamy dlugosc + 1 bit jesli potrzebujemy go na znak
        return length + (needSignBit ? 1 : 0);
    }
}
