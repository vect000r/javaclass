import java.util.*;

public class EKuchnia implements Kuchnia {
    private final Map<Produkt, Integer> spizarnia = new HashMap<>();

    @Override
    public void dodajDoSpizarni(Produkt produkt, int gram) {
        spizarnia.merge(produkt, gram, Integer::sum);
        spizarnia.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }

    @Override
    public Set<Skladnik> przeliczPrzepis(Przepis przepis) {
        if (przepis == null || przepis.sklad() == null || przepis.sklad().isEmpty()) {
            return Set.of(); // Niepoprawny lub pusty przepis
        }

        // Oblicz maksymalna liczbe porcji
        float maxPorcje = calculateMaxPortion(przepis, spizarnia);

        // Jesli maksymalna liczba porcji jest mniejsza niz 1, zwroc pusty zbior
        if (maxPorcje < 1) {
            return Set.of();
        }

        // Utworz przeliczony przepis na podstawie wyliczonej liczby porcji
        Set<Skladnik> przeliczonyPrzepis = new HashSet<>();
        for (Skladnik skladnik : przepis.sklad()) {
            przeliczonyPrzepis.add(new Skladnik(
                    skladnik.produkt(),
                    (int) Math.ceil(skladnik.gramow() * maxPorcje)
            ));
        }

        return przeliczonyPrzepis;
    }

    @Override
    public boolean wykonaj(Przepis przepis) {
        Set<Skladnik> przeliczonyPrzepis = przeliczPrzepis(przepis);

        if (przeliczonyPrzepis.isEmpty()) {
            return false; // Nie mozna przygotowac potrawy
        }

        // Odejmij zuzyte skladniki od spizarni
        for (Skladnik skladnik : przeliczonyPrzepis) {
            spizarnia.computeIfPresent(skladnik.produkt(), (produkt, ilosc) -> {
                int nowaIlosc = ilosc - skladnik.gramow();
                return nowaIlosc > 0 ? nowaIlosc : null;
            });
        }
        return true;
    }

    @Override
    public Map<Produkt, Integer> stanSpizarni() {
        // Filtruj produkty o ilości większej od 0
        Map<Produkt, Integer> filteredSpizarnia = new HashMap<>();
        for (Map.Entry<Produkt, Integer> entry : spizarnia.entrySet()) {
            if (entry.getValue() > 0) {
                filteredSpizarnia.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredSpizarnia;
    }


    // Metoda pomocnicza do obliczenia maksymalnej liczby porcji
    public static float calculateMaxPortion(Przepis przepis, Map<Produkt, Integer> spizarnia) {
        if (przepis == null || przepis.sklad() == null || przepis.sklad().isEmpty()) {
            return 0.0f;
        }

        float maxPorcje = Float.MAX_VALUE;
        for (Skladnik skladnik : przepis.sklad()) {
            if (spizarnia.containsKey(skladnik.produkt())) {
                int dostepnaIlosc = spizarnia.get(skladnik.produkt());
                float proporcja = (float) dostepnaIlosc / skladnik.gramow();
                maxPorcje = Math.min(maxPorcje, proporcja);
            } else {
                return 0.0f;
            }


        }
        return  (int) Math.ceil(maxPorcje);
    }
}
