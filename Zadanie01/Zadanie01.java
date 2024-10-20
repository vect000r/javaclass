class Zadanie01 {
    private static final int BLAD = -2_000_000_000;
    private StringBuilder buffer = new StringBuilder();
    private Integer lastResult = null;
    private boolean resultRetrieved = false;

    public Zadanie01() {
    }


    public void input(int bit) {
        if (bit != 0 && bit != 1) {
            System.out.println("Bit must be 0 or 1");
        }

        buffer.append(bit);

        // Jesli w buforze mamy pelny ciag, to zacznijmy go procesowac
        // jesli nie mamy pelnego ciagu, getTotalLength zwraca Integer.MAX_VALUE i bufor nie jest procesowany
        if (buffer.length() >= getTotalLength()) {
            processBuffer();
        }
    }


    public int wynik() {
        // Zwroc poprzedni wynik jesli jest dostepny i jeszcze nie zwrocony
        if (!resultRetrieved && lastResult != null) {
            resultRetrieved = true;
            return lastResult;
        } else {
            return BLAD; // Wyniku nie ma lub zostal juz zwrocony
        }
    }


    private void processBuffer() {
        if (buffer.length() < getTotalLength()) {
            return; // Nie rob nic jesli nie masz pelnego ciagu
        }

        try {
            int a = Integer.parseInt(buffer.substring(0, 4), 2);
            int b = signedBinary(buffer.substring(4, 4 + a));
            int c = Integer.parseInt(buffer.substring(4 + a, 4 + a + 2), 2);
            int d = signedBinary(buffer.substring(4 + a + 2, 4 + a + 2 + a));

            lastResult = performOperation(b, c, d);
            resultRetrieved = false; // pozwol aby wynik zostal zwrocony
        } catch (Exception e) {
            lastResult = BLAD; // w razie problemow z danymi zwroc BLAD
        }

        // Wyczysc bufor aby byl gotow na nastepny ciag
        buffer.setLength(0);
    }


    private int performOperation(int b, int c, int d) {
        switch (c) {
            case 0: return b + d;
            case 1: return b - d;
            case 2: return b * d;
            case 3: return d != 0 ? b / d : BLAD; // zwroc BLAD jesli dzielenie przez 0
            default: return BLAD;
        }
    }

    // Zamiana liczb w NKB na U1:
    // jesli pierwszy bit jest rowny 1, to mamy liczbe ujemna
    // patrzymy na pozostale bity w stringu, obliczamy ich wartosc w systemie dziesietnym
    // zmieniamy znak
    private int signedBinary(String binary) {
        boolean isNegative = binary.charAt(0) == '1';
        int number = Integer.parseInt(binary.substring(1), 2);
        return isNegative ? -number : number;
    }

    // metoda do obliczania dlugosci ciagu
    private int getTotalLength() {
        if (buffer.length() < 4) return Integer.MAX_VALUE; // za malo bitow aby utworzyc ciag
        int a = Integer.parseInt(buffer.substring(0, 4), 2); // pierwsze 4 bity to a
        return 4 + a + 2 + a; // calkowita dlugosc sekwencji
    }
}
