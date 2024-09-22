import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AgenteProbabilistico implements Agente {
    private final double stack;  // Stack do jogador (fichas restantes)
    private double valorParaPagar;  // Valor que o jogador precisa pagar na rodada
    private double poteAtual;  // Tamanho atual do pote
    private double oddsMao;  // Odds da mão (oportunidade de ganhar com a mão atual)

    // Mapa de odds para diferentes mãos de póquer
    private final Map<String, Double> oddsMap = new HashMap<>();

    // Construtor inicializando com stack inicial e odds map
    public AgenteProbabilistico() {
        this.stack = 1000;  // Cada jogador começa com 1000 fichas
        this.valorParaPagar = 0;
        this.poteAtual = 0;
        this.oddsMao = 0.50;  // Valor padrão, será atualizado conforme a mão

        // Inicializa as odds de diferentes mãos
        inicializarOdds();
    }

    // Metodo que calcula as pot odds
    public double calcularPotOdds(double valorParaPagar, double poteAtual) {
        return valorParaPagar / (poteAtual + valorParaPagar);
    }

    // Metodo que compara as pot odds com as odds da mão
    public boolean deveApostar(double oddsMao, double potOdds) {
        // Se as odds da mão forem maiores ou iguais às pot odds, vale a pena apostar
        return oddsMao >= potOdds;
    }

    // Metodo que calcula o valor ideal de aposta (baseado num percentual do pote)
    public double calcularValorAposta(double poteAtual, double percentualPote) {
        return poteAtual * percentualPote;
    }

    private double determinarFatorAposta(double oddsMao) {
        if (oddsMao < 0.1) {  // Mão muito forte
            return 1.0;  // Apostar 100% do pote
        }
        if (oddsMao < 1) {  // Mão forte
            return 0.75;  // Apostar 75% do pote
        }
        if (oddsMao < 5) {  // Mão média
            return 0.50;  // Apostar 50% do pote
        }
        if (oddsMao < 10) {  // Mão marginal
            return 0.25;  // Apostar 25% do pote
        }
        // Mão fraca
        return 0.05;  // Apostar 5% do pote ou o mínimo
    }

    @Override
    public int getPrimeiraAposta(Carta[] jogo) {
        // Define as odds da mão antes de calcular a aposta
        this.oddsMao = definirOddsMao(jogo);

        // A primeira aposta é determinada pela análise da mão
        this.poteAtual = 350;  // Supondo o valor do pote na primeira rodada
        this.valorParaPagar = 10;  // Mínimo da primeira aposta é 10

        // Calcula as pot odds
        double potOdds = calcularPotOdds(valorParaPagar, poteAtual);
        System.out.println("Pot Odds: " + potOdds);

        // Decide se deve apostar com base nas odds da mão e nas pot odds
        if (deveApostar(oddsMao, potOdds)) {
            // Aposta um valor baseado em 50% do pote (ajuste conforme necessário)
            double fatorAposta = determinarFatorAposta(oddsMao);

            // Calcula o valor da aposta com base no fator determinado
            double valorAposta = calcularValorAposta(poteAtual, fatorAposta);
            System.out.println("Você deve apostar: " + valorAposta);
            this.valorParaPagar = Math.min(valorAposta, stack);
            return (int) this.valorParaPagar;  // Não aposta mais do que o stack
        } else {
            System.out.println("Não vale a pena apostar. Aposta mínima de 10.");
            return 10;  // Aposta mínima
        }
    }

    @Override
    public boolean getSegundaAposta(int apostaMaisAlta) {
        // Atualiza o pote com a aposta mais alta
        this.poteAtual += apostaMaisAlta;
        this.valorParaPagar = apostaMaisAlta; // O valor a ser pago é igual à aposta mais alta

        // Calcula pot odds considerando o que o jogador precisa pagar relativamente ao total no pote
        double potOdds = calcularPotOdds(valorParaPagar, poteAtual);
        System.out.println("Pot Odds para segunda aposta: " + potOdds);

        // Calcula a decisão de apostar novamente com base nas odds da mão e nas pot odds
        boolean deveContinuar = deveApostar(oddsMao, potOdds);
        System.out.println("Você deve apostar? " + deveContinuar);

        // Se decidir continuar, calcula um valor de aposta ideal baseado nas odds
        if (deveContinuar) {
            double fatorAposta = determinarFatorAposta(oddsMao);
            double valorAposta = calcularValorAposta(poteAtual, fatorAposta);
            System.out.println("Valor ideal da aposta: " + valorAposta);

            // Aqui você pode decidir o que fazer com o valor da aposta, por exemplo, retornar ou armazenar
            // Para simplificação, vamos retornar true se decidir apostar
            return true;
        }

        return false;
        //return apostaMaisAlta < this.valorParaPagar;
        // Se não decidir continuar a apostar, retornar false
    }


    // Função para verificar se a mão é um Royal Flush
    private boolean isRoyalFlush(Carta[] jogo) {
        return isFlush(jogo) && containsValores(jogo, new int[]{10, 11, 12, 13, 14});
    }

    // Função para verificar se a mão é um Straight Flush
    private boolean isStraightFlush(Carta[] jogo) {
        return isFlush(jogo) && isSequencia(jogo);
    }

    // Função para verificar se a mão é uma Quadra
    private boolean isQuadra(Carta[] jogo) {
        return hasSameValue(jogo, 4);
    }

    // Função para verificar se a mão é um Full House (Trinca + Par)
    private boolean isFullHouse(Carta[] jogo) {
        return hasSameValue(jogo, 3) && hasSameValue(jogo, 2);
    }

    // Função para verificar se a mão é um Flush (todas as cartas com o mesmo naipe)
    private boolean isFlush(Carta[] jogo) {
        return Arrays.stream(jogo).map(carta -> carta.naipe).distinct().count() == 1;
    }

    // Função para verificar se a mão é uma Sequência (Straight)
    private boolean isSequencia(Carta[] jogo) {
        int[] valores = Arrays.stream(jogo).mapToInt(carta -> carta.valor).sorted().toArray();
        for (int i = 0; i < valores.length - 1; i++) {
            if (valores[i] + 1 != valores[i + 1]) {
                return false;
            }
        }
        return true;
    }

    // Função para verificar se a mão é uma Trinca (Três cartas com o mesmo valor)
    private boolean isTrinca(Carta[] jogo) {
        return hasSameValue(jogo, 3);
    }

    // Função para verificar se a mão tem Dois Pares
    private boolean isDoisPares(Carta[] jogo) {
        return countPairs(jogo) == 2;
    }

    // Função para verificar se a mão tem Um Par
    private boolean isUmPar(Carta[] jogo) {
        return countPairs(jogo) == 1;
    }

    // Função para verificar se a mão é AKs (Ás e Rei do mesmo naipe)
    private boolean isAKSuited(Carta[] jogo) {
        return containsValores(jogo, new int[]{14, 13}) && isFlush(jogo);
    }

    // Função para verificar se a mão é AA (Par de Áses)
    private boolean isAA(Carta[] jogo) {
        return containsValores(jogo, new int[]{14}) && hasSameValue(jogo, 2);
    }

    // Funções para as mãos como "AKs, KQs, QJs, etc."
    private boolean isAKsKQsQJsJTs(Carta[] jogo) {
        return containsValores(jogo, new int[]{14, 13, 12, 11, 10}) && isFlush(jogo);
    }

    private boolean isAK(Carta[] jogo) {
        return containsValores(jogo, new int[]{14, 13});
    }

    private boolean isAAKKQQ(Carta[] jogo) {
        return containsValores(jogo, new int[]{14, 13, 12}) && hasSameValue(jogo, 2);
    }

    private boolean isAAKKQQJJ(Carta[] jogo) {
        return containsValores(jogo, new int[]{14, 13, 12, 11}) && hasSameValue(jogo, 2);
    }

    // Auxiliares para checar outras mãos conectadas ou específicas
    private boolean isCartasMesmoNaipeValeteOuMelhor(Carta[] jogo) {
        return containsValores(jogo, new int[]{11, 12, 13, 14}) && isFlush(jogo);
    }

    private boolean isCartasMesmoNaipe10OuMelhor(Carta[] jogo) {
        return containsValores(jogo, new int[]{10, 11, 12, 13, 14}) && isFlush(jogo);
    }

    private boolean isConectoresMesmoNaipe(Carta[] jogo) {
        return isFlush(jogo) && isSequencia(jogo);
    }

    private boolean isQualquer2CartasDamaOuMelhor(Carta[] jogo) {
        return containsValores(jogo, new int[]{12, 13, 14});
    }

    private boolean isQualquer2CartasValeteOuMelhor(Carta[] jogo) {
        return containsValores(jogo, new int[]{11, 12, 13, 14});
    }

    private boolean isQualquer2Cartas10OuMelhor(Carta[] jogo) {
        return containsValores(jogo, new int[]{10, 11, 12, 13, 14});
    }

    private boolean isCartasConectadas(Carta[] jogo) {
        return isSequencia(jogo);
    }

    private boolean isQualquer2Cartas9OuMelhor(Carta[] jogo) {
        return containsValores(jogo, new int[]{9, 10, 11, 12, 13, 14});
    }

    // Funções Auxiliares
    private boolean containsValores(Carta[] jogo, int[] valoresDesejados) {
        // Verifica se a mão contém os valores desejados
        for (int valor : valoresDesejados) {
            if (Arrays.stream(jogo).noneMatch(carta -> carta.valor == valor)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasSameValue(Carta[] jogo, int quantidade) {
        // Verifica se existe um número de cartas com o mesmo valor
        return Arrays.stream(jogo)
                .collect(Collectors.groupingBy(carta -> carta.valor, Collectors.counting()))
                .values().stream().anyMatch(count -> count == quantidade);
    }

    private int countPairs(Carta[] jogo) {
        // Conta quantos pares existem na mão
        return (int) Arrays.stream(jogo)
                .collect(Collectors.groupingBy(carta -> carta.valor, Collectors.counting()))
                .values().stream().filter(count -> count == 2).count();
    }

    // Verifica se a mão é "Não conectado nem adequado, pelo menos um 2-9"
    private boolean isNaoConectadoNemAdequado(Carta[] jogo) {
        // Verifica se nenhuma das cartas está conectada ou com o mesmo naipe e se todas as cartas têm valor entre 2 e 9
        boolean naoConectado = !isSequencia(jogo);
        boolean naoAdequado = !isFlush(jogo);
        boolean entre2e9 = Arrays.stream(jogo).allMatch(carta -> carta.valor >= 2 && carta.valor <= 9);
        return naoConectado && naoAdequado && entre2e9;
    }

    // Verifica se a mão tem "Desenho de sequência aberta"
    private boolean isDesenhosSequenciaAberta(Carta[] jogo) {
        int[] valores = Arrays.stream(jogo).mapToInt(carta -> carta.valor).sorted().toArray();
        // Verifica se há quatro cartas em sequência e uma carta fora
        for (int i = 0; i < valores.length - 3; i++) {
            if (valores[i + 1] == valores[i] + 1 && valores[i + 2] == valores[i] + 2 && valores[i + 3] == valores[i] + 3) {
                return true;
            }
        }
        return false;
    }

    // Verifica se a mão tem "Quatro para um flush"
    private boolean isQuatroParaFlush(Carta[] jogo) {
        // Verifica se há 4 cartas do mesmo naipe
        return Arrays.stream(jogo)
                .collect(Collectors.groupingBy(carta -> carta.naipe, Collectors.counting()))
                .values().stream().anyMatch(count -> count == 4);
    }

    // Verifica se a mão tem "Sequência interna"
    private boolean isSequenciaInterna(Carta[] jogo) {
        int[] valores = Arrays.stream(jogo).mapToInt(carta -> carta.valor).sorted().toArray();
        // Verifica se há uma sequência de quatro cartas com um buraco no meio (sequência interna)
        for (int i = 0; i < valores.length - 3; i++) {
            if (valores[i + 1] == valores[i] + 2 && valores[i + 2] == valores[i + 1] + 1) {
                return true;
            }
        }
        return false;
    }

    // Verifica se a mão tem "Um par para dois pares ou trinca"
    private boolean isUmParParaDoisParesOuTrinca(Carta[] jogo) {
        // Verifica se há um par e se a mão tem potencial para evoluir para dois pares ou trinca
        boolean umPar = isUmPar(jogo);
        boolean doisParesOuTrinca = isDoisPares(jogo) || isTrinca(jogo);
        return umPar && doisParesOuTrinca;
    }

    // Verifica se a mão tem "Overcards" (cartas mais altas que as do adversário)
    private boolean isOvercards(Carta[] jogo) {
        // Considera como overcards se todas as cartas têm valor maior que 10
        return Arrays.stream(jogo).allMatch(carta -> carta.valor > 10);
    }

    // Verifica se a mão está "Comprando para um set" (tentando completar uma trinca)
    private boolean isComprandoSet(Carta[] jogo) {
        // Se há um par e outra carta que poderia completar a trinca
        return isUmPar(jogo) && Arrays.stream(jogo).anyMatch(carta -> carta.valor != Arrays.stream(jogo).mapToInt(c -> c.valor).distinct().findFirst().orElse(0));
    }

    // Verifica se a mão tem "AA, KK, QQ, JJ, TT"
    private boolean isAAKKQQJJTT(Carta[] jogo) {
        // Verifica se a mão contém um par de AA, KK, QQ, JJ ou TT
        return containsValores(jogo, new int[]{14, 13, 12, 11, 10}) && hasSameValue(jogo, 2);
    }

    // Verifica se a mão tem "Cartas conectadas, 10 ou melhor"
    private boolean isCartasConectadas10OuMelhor(Carta[] jogo) {
        // Verifica se todas as cartas têm valor de pelo menos 10
        boolean todasCartasSao10OuMelhor = Arrays.stream(jogo).allMatch(carta -> carta.valor >= 10);

        // Verifica se as cartas são conectadas (sequência)
        boolean saoConectadas = isSequencia(jogo);

        // Retorna true se as cartas forem conectadas e todas forem 10 ou melhores
        return todasCartasSao10OuMelhor && saoConectadas;
    }

    // Inicializa as odds de cada tipo de mão de poker
    private void inicializarOdds() {
        oddsMap.put("Carta Alta", 0.995);
        oddsMap.put("Um Par", 1.37);
        oddsMap.put("Dois Pares", 20.0);
        oddsMap.put("Trinca", 46.3);
        oddsMap.put("Sequência", 254.0);
        oddsMap.put("Flush", 508.0);
        oddsMap.put("Full House", 693.0);
        oddsMap.put("Quadra", 4164.0);
        oddsMap.put("Straight Flush", 72192.0);
        oddsMap.put("Royal Flush", 649739.0);
        oddsMap.put("AKs", 330.5);
        oddsMap.put("AA", 220.0);
        oddsMap.put("AKsKQsQJsJTs", 81.9);
        oddsMap.put("AK", 81.9);
        oddsMap.put("AAKKQQ", 72.7);
        oddsMap.put("AAKKQQJJ", 54.25);
        oddsMap.put("Cartas do mesmo naipe, valete ou melhor", 54.25);
        oddsMap.put("AAKKQQJJTT", 43.2);
        oddsMap.put("Cartas do mesmo naipe, 10 ou melhor", 32.2);
        oddsMap.put("Conectores do mesmo naipe", 24.5);
        oddsMap.put("Cartas conectadas, 10 ou melhor", 19.7);
        oddsMap.put("Qualquer 2 cartas com valor de pelo menos dama", 19.7);
        oddsMap.put("Qualquer 2 cartas com valor de pelo menos valete", 10.1);
        oddsMap.put("Qualquer 2 cartas com valor de pelo menos 10", 5.98);
        oddsMap.put("Cartas conectadas", 5.98);
        oddsMap.put("Qualquer 2 cartas com valor de pelo menos 9", 3.81);
        oddsMap.put("Não conectado nem adequado, pelo menos um 2-9", 0.873);
        oddsMap.put("Desenhos de sequência aberta", 4.8);
        oddsMap.put("Quatro para um flush", 4.1);
        oddsMap.put("Sequência interna", 10.5);
        oddsMap.put("Um par para dois pares ou trinca", 8.2);
        oddsMap.put("Overcards", 6.7);
        oddsMap.put("Comprando para um set", 22.0);
    }

    // Metodo para identificar a mão do jogador e atribuir as odds
    private double definirOddsMao(Carta[] jogo) {
        if (isRoyalFlush(jogo)) {
            return oddsMap.get("Royal Flush");
        }
        if (isStraightFlush(jogo)) {
            return oddsMap.get("Straight Flush");
        }
        if (isQuadra(jogo)) {
            return oddsMap.get("Quadra");
        }
        if (isFullHouse(jogo)) {
            return oddsMap.get("Full House");
        }
        if (isFlush(jogo)) {
            return oddsMap.get("Flush");
        }
        if (isSequencia(jogo)) {
            return oddsMap.get("Sequência");
        }
        if (isTrinca(jogo)) {
            return oddsMap.get("Trinca");
        }
        if (isDoisPares(jogo)) {
            return oddsMap.get("Dois Pares");
        }
        if (isUmPar(jogo)) {
            return oddsMap.get("Um Par");
        }
        if (isAKSuited(jogo)) {
            return oddsMap.get("AKs");
        }
        if (isAA(jogo)) {
            return oddsMap.get("AA");
        }
        if (isAKsKQsQJsJTs(jogo)) {
            return oddsMap.get("AKsKQsQJsJTs");
        }
        if (isAK(jogo)) {
            return oddsMap.get("AK");
        }
        if (isAAKKQQ(jogo)) {
            return oddsMap.get("AAKKQQ");
        }
        if (isAAKKQQJJ(jogo)) {
            return oddsMap.get("AAKKQQJJ");
        }
        if (isCartasMesmoNaipeValeteOuMelhor(jogo)) {
            return oddsMap.get("Cartas do mesmo naipe, valete ou melhor");
        }
        if (isAAKKQQJJTT(jogo)) {
            return oddsMap.get("AAKKQQJJTT");
        }
        if (isCartasMesmoNaipe10OuMelhor(jogo)) {
            return oddsMap.get("Cartas do mesmo naipe, 10 ou melhor");
        }
        if (isConectoresMesmoNaipe(jogo)) {
            return oddsMap.get("Conectores do mesmo naipe");
        }
        if (isCartasConectadas10OuMelhor(jogo)) {
            return oddsMap.get("Cartas conectadas, 10 ou melhor");
        }
        if (isQualquer2CartasDamaOuMelhor(jogo)) {
            return oddsMap.get("Qualquer 2 cartas com valor de pelo menos dama");
        }
        if (isQualquer2CartasValeteOuMelhor(jogo)) {
            return oddsMap.get("Qualquer 2 cartas com valor de pelo menos valete");
        }
        if (isQualquer2Cartas10OuMelhor(jogo)) {
            return oddsMap.get("Qualquer 2 cartas com valor de pelo menos 10");
        }
        if (isCartasConectadas(jogo)) {
            return oddsMap.get("Cartas conectadas");
        }
        if (isQualquer2Cartas9OuMelhor(jogo)) {
            return oddsMap.get("Qualquer 2 cartas com valor de pelo menos 9");
        }
        if (isNaoConectadoNemAdequado(jogo)) {
            return oddsMap.get("Não conectado nem adequado, pelo menos um 2-9");
        }
        if (isDesenhosSequenciaAberta(jogo)) {
            return oddsMap.get("Desenhos de sequência aberta");
        }
        if (isQuatroParaFlush(jogo)) {
            return oddsMap.get("Quatro para um flush");
        }
        if (isSequenciaInterna(jogo)) {
            return oddsMap.get("Sequência interna");
        }
        if (isUmParParaDoisParesOuTrinca(jogo)) {
            return oddsMap.get("Um par para dois pares ou trinca");
        }
        if (isOvercards(jogo)) {
            return oddsMap.get("Overcards");
        }
        if (isComprandoSet(jogo)) {
            return oddsMap.get("Comprando para um set");
        }

        return oddsMap.get("Carta Alta");  // Se nenhuma das mãos acima for identificada, é uma Carta Alta
    }
}