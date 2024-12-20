import java.util.*;
import java.util.stream.Collectors;

public class EKuchnia implements Kuchnia {
    private final Map<Produkt, Integer> spizarnia = new HashMap<>();

    @Override
    public void dodajDoSpizarni(Produkt produkt, int gram) {
        spizarnia.merge(produkt, gram, Integer::sum);
    }

    @Override
    public Set<Skladnik> przeliczPrzepis(Przepis przepis) {
        if (przepis == null || przepis.sklad() == null || przepis.sklad().isEmpty()) {
            return Set.of();
        }

        float maxPorcje = przepis.sklad().stream()
                .map(skladnik -> {
                    Produkt produkt = skladnik.produkt();
                    int potrzebnaIlosc = skladnik.gramow();
                    int dostepnaIlosc = spizarnia.getOrDefault(produkt, 0);

                    return (float) dostepnaIlosc / potrzebnaIlosc;
                })
                .min(Float::compare)
                .orElse(0.0f);

        if (maxPorcje <= 0) {
            return Set.of();
        }

        return przepis.sklad().stream()
                .map(skladnik -> {
                    int przeliczonaIlosc = Math.round(skladnik.gramow() * maxPorcje);
                    return new Skladnik(skladnik.produkt(), przeliczonaIlosc);
                })
                .collect(Collectors.toSet());
    }

    @Override
    public boolean wykonaj(Przepis przepis) {
        if (przepis.sklad().stream()
                .anyMatch(skladnik -> spizarnia.getOrDefault(skladnik.produkt(), 0) < skladnik.gramow())) {
            return false; // Brak wystarczających składników
        }

        przepis.sklad().forEach(skladnik ->
                spizarnia.computeIfPresent(skladnik.produkt(), (key, value) -> {
                    int nowaIlosc = value - skladnik.gramow();
                    return nowaIlosc > 0 ? nowaIlosc : null;
                })
        );

        return true;
    }

    @Override
    public Map<Produkt, Integer> stanSpizarni() {
        return spizarnia.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
}
