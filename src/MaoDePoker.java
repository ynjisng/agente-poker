import java.util.Arrays;
import java.util.stream.Collectors;

public class MaoDePoker {

    public boolean isRoyalFlush(Carta[] jogo) {
        return isFlush(jogo) && containsValores(jogo, new int[]{10, 11, 12, 13, 14});
    }

    public boolean isStraightFlush(Carta[] jogo) {
        return isFlush(jogo) && isSequencia(jogo);
    }

    public boolean isQuadra(Carta[] jogo) {
        return hasSameValue(jogo, 4);
    }

    public boolean isFullHouse(Carta[] jogo) {
        return hasSameValue(jogo, 3) && hasSameValue(jogo, 2);
    }

    public boolean isFlush(Carta[] jogo) {
        return Arrays.stream(jogo).map(carta -> carta.naipe).distinct().count() == 1;
    }

    public boolean isSequencia(Carta[] jogo) {
        int[] valores = Arrays.stream(jogo).mapToInt(carta -> carta.valor).sorted().toArray();
        for (int i = 0; i < valores.length - 1; i++) {
            if (valores[i] + 1 != valores[i + 1]) {
                return false;
            }
        }
        return true;
    }

    public boolean isTrinca(Carta[] jogo) {
        return hasSameValue(jogo, 3);
    }

    public boolean isDoisPares(Carta[] jogo) {
        return countPairs(jogo) == 2;
    }

    public boolean isUmPar(Carta[] jogo) {
        return countPairs(jogo) == 1;
    }

    public boolean isAKSuited(Carta[] jogo) {
        return containsValores(jogo, new int[]{14, 13}) && isFlush(jogo);
    }

    public boolean isAA(Carta[] jogo) {
        return containsValores(jogo, new int[]{14}) && hasSameValue(jogo, 2);
    }

    public boolean isAKsKQsQJsJTs(Carta[] jogo) {
        return containsValores(jogo, new int[]{14, 13, 12, 11, 10}) && isFlush(jogo);
    }

    public boolean isAK(Carta[] jogo) {
        return containsValores(jogo, new int[]{14, 13});
    }

    public boolean isAAKKQQ(Carta[] jogo) {
        return containsValores(jogo, new int[]{14, 13, 12}) && hasSameValue(jogo, 2);
    }

    public boolean isAAKKQQJJ(Carta[] jogo) {
        return containsValores(jogo, new int[]{14, 13, 12, 11}) && hasSameValue(jogo, 2);
    }

    public boolean isCartasMesmoNaipeValeteOuMelhor(Carta[] jogo) {
        return containsValores(jogo, new int[]{11, 12, 13, 14}) && isFlush(jogo);
    }

    public boolean isCartasMesmoNaipe10OuMelhor(Carta[] jogo) {
        return containsValores(jogo, new int[]{10, 11, 12, 13, 14}) && isFlush(jogo);
    }

    public boolean isConectoresMesmoNaipe(Carta[] jogo) {
        return isFlush(jogo) && isSequencia(jogo);
    }

    public boolean isQualquer2CartasDamaOuMelhor(Carta[] jogo) {
        return containsValores(jogo, new int[]{12, 13, 14});
    }

    public boolean isQualquer2CartasValeteOuMelhor(Carta[] jogo) {
        return containsValores(jogo, new int[]{11, 12, 13, 14});
    }

    public boolean isQualquer2Cartas10OuMelhor(Carta[] jogo) {
        return containsValores(jogo, new int[]{10, 11, 12, 13, 14});
    }

    public boolean isCartasConectadas(Carta[] jogo) {
        return isSequencia(jogo);
    }

    public boolean isQualquer2Cartas9OuMelhor(Carta[] jogo) {
        return containsValores(jogo, new int[]{9, 10, 11, 12, 13, 14});
    }

    public boolean isNaoConectadoNemAdequado(Carta[] jogo) {
        boolean naoConectado = !isSequencia(jogo);
        boolean naoAdequado = !isFlush(jogo);
        boolean entre2e9 = Arrays.stream(jogo).allMatch(carta -> carta.valor >= 2 && carta.valor <= 9);
        return naoConectado && naoAdequado && entre2e9;
    }

    public boolean isDesenhosSequenciaAberta(Carta[] jogo) {
        int[] valores = Arrays.stream(jogo).mapToInt(carta -> carta.valor).sorted().toArray();
        for (int i = 0; i < valores.length - 3; i++) {
            if (valores[i + 1] == valores[i] + 1 && valores[i + 2] == valores[i] + 2 && valores[i + 3] == valores[i] + 3) {
                return true;
            }
        }
        return false;
    }

    public boolean isQuatroParaFlush(Carta[] jogo) {
        return Arrays.stream(jogo)
                .collect(Collectors.groupingBy(carta -> carta.naipe, Collectors.counting()))
                .values().stream().anyMatch(count -> count == 4);
    }

    public boolean isSequenciaInterna(Carta[] jogo) {
        int[] valores = Arrays.stream(jogo).mapToInt(carta -> carta.valor).sorted().toArray();
        for (int i = 0; i < valores.length - 3; i++) {
            if (valores[i + 1] == valores[i] + 2 && valores[i + 2] == valores[i + 1] + 1) {
                return true;
            }
        }
        return false;
    }

    public boolean isUmParParaDoisParesOuTrinca(Carta[] jogo) {
        boolean umPar = isUmPar(jogo);
        boolean doisParesOuTrinca = isDoisPares(jogo) || isTrinca(jogo);
        return umPar && doisParesOuTrinca;
    }

    public boolean isOvercards(Carta[] jogo) {
        return Arrays.stream(jogo).allMatch(carta -> carta.valor > 10);
    }

    public boolean isComprandoSet(Carta[] jogo) {
        return isUmPar(jogo) && Arrays.stream(jogo).anyMatch(carta -> carta.valor != Arrays.stream(jogo).mapToInt(c -> c.valor).distinct().findFirst().orElse(0));
    }

    public boolean isAAKKQQJJTT(Carta[] jogo) {
        return containsValores(jogo, new int[]{14, 13, 12, 11, 10}) && hasSameValue(jogo, 2);
    }

    public boolean isCartasConectadas10OuMelhor(Carta[] jogo) {
        boolean todasCartasSao10OuMelhor = Arrays.stream(jogo).allMatch(carta -> carta.valor >= 10);
        boolean saoConectadas = isSequencia(jogo);
        return todasCartasSao10OuMelhor && saoConectadas;
    }

    private boolean containsValores(Carta[] jogo, int[] valoresDesejados) {
        for (int valor : valoresDesejados) {
            if (Arrays.stream(jogo).noneMatch(carta -> carta.valor == valor)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasSameValue(Carta[] jogo, int quantidade) {
        return Arrays.stream(jogo)
                .collect(Collectors.groupingBy(carta -> carta.valor, Collectors.counting()))
                .values().stream().anyMatch(count -> count == quantidade);
    }

    private int countPairs(Carta[] jogo) {
        return (int) Arrays.stream(jogo)
                .collect(Collectors.groupingBy(carta -> carta.valor, Collectors.counting()))
                .values().stream().filter(count -> count == 2).count();
    }
}