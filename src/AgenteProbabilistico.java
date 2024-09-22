import java.util.HashMap;
import java.util.Map;

public class AgenteProbabilistico implements Agente {
    private final double stack;  // Stack do jogador (fichas restantes)
    private double valorParaPagar;  // Valor que o jogador precisa pagar na rodada
    private double poteAtual;  // Tamanho atual do pote
    private double oddsMao;  // Odds da mão (oportunidade de ganhar com a mão atual)

    // Mapa de odds para diferentes mãos de póquer
    private final Map<String, Double> oddsMap = new HashMap<>();

    private MaoDePoker maoDePoker = new MaoDePoker();

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
    this.poteAtual = 100;  // Supondo o valor do pote na primeira rodada
    this.valorParaPagar = 10;  // Mínimo da primeira aposta é 10

    // Calcula as pot odds
    double potOdds = calcularPotOdds(valorParaPagar, poteAtual);
    System.out.println("Pot Odds: " + potOdds);

    // Decide se deve apostar com base nas odds da mão e nas pot odds
    if (deveApostar(oddsMao, potOdds)) {
        // Determina o fator da aposta com base nas odds da mão
        double fatorAposta = determinarFatorAposta(oddsMao);
        
        // Calcula o valor da aposta com base no fator determinado
        double valorAposta = calcularValorAposta(poteAtual, fatorAposta);
        System.out.println("Você deve apostar: " + valorAposta);
        
        // Aposta o valor calculado, mas garante que não aposta mais do que o stack
        return (int) Math.min(valorAposta, 400.0);
    } else {
        // Se as odds da mão e as pot odds não são favoráveis, aposta o mínimo
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
        if (maoDePoker.isRoyalFlush(jogo)) {
            return oddsMap.get("Royal Flush");
        }
        if (maoDePoker.isStraightFlush(jogo)) {
            return oddsMap.get("Straight Flush");
        }
        if (maoDePoker.isQuadra(jogo)) {
            return oddsMap.get("Quadra");
        }
        if (maoDePoker.isFullHouse(jogo)) {
            return oddsMap.get("Full House");
        }
        if (maoDePoker.isFlush(jogo)) {
            return oddsMap.get("Flush");
        }
        if (maoDePoker.isSequencia(jogo)) {
            return oddsMap.get("Sequência");
        }
        if (maoDePoker.isTrinca(jogo)) {
            return oddsMap.get("Trinca");
        }
        if (maoDePoker.isDoisPares(jogo)) {
            return oddsMap.get("Dois Pares");
        }
        if (maoDePoker.isUmPar(jogo)) {
            return oddsMap.get("Um Par");
        }
        if (maoDePoker.isAKSuited(jogo)) {
            return oddsMap.get("AKs");
        }
        if (maoDePoker.isAA(jogo)) {
            return oddsMap.get("AA");
        }
        if (maoDePoker.isAKsKQsQJsJTs(jogo)) {
            return oddsMap.get("AKsKQsQJsJTs");
        }
        if (maoDePoker.isAK(jogo)) {
            return oddsMap.get("AK");
        }
        if (maoDePoker.isAAKKQQ(jogo)) {
            return oddsMap.get("AAKKQQ");
        }
        if (maoDePoker.isAAKKQQJJ(jogo)) {
            return oddsMap.get("AAKKQQJJ");
        }
        if (maoDePoker.isCartasMesmoNaipeValeteOuMelhor(jogo)) {
            return oddsMap.get("Cartas do mesmo naipe, valete ou melhor");
        }
        if (maoDePoker.isAAKKQQJJTT(jogo)) {
            return oddsMap.get("AAKKQQJJTT");
        }
        if (maoDePoker.isCartasMesmoNaipe10OuMelhor(jogo)) {
            return oddsMap.get("Cartas do mesmo naipe, 10 ou melhor");
        }
        if (maoDePoker.isConectoresMesmoNaipe(jogo)) {
            return oddsMap.get("Conectores do mesmo naipe");
        }
        if (maoDePoker.isCartasConectadas10OuMelhor(jogo)) {
            return oddsMap.get("Cartas conectadas, 10 ou melhor");
        }
        if (maoDePoker.isQualquer2CartasDamaOuMelhor(jogo)) {
            return oddsMap.get("Qualquer 2 cartas com valor de pelo menos dama");
        }
        if (maoDePoker.isQualquer2CartasValeteOuMelhor(jogo)) {
            return oddsMap.get("Qualquer 2 cartas com valor de pelo menos valete");
        }
        if (maoDePoker.isQualquer2Cartas10OuMelhor(jogo)) {
            return oddsMap.get("Qualquer 2 cartas com valor de pelo menos 10");
        }
        if (maoDePoker.isCartasConectadas(jogo)) {
            return oddsMap.get("Cartas conectadas");
        }
        if (maoDePoker.isQualquer2Cartas9OuMelhor(jogo)) {
            return oddsMap.get("Qualquer 2 cartas com valor de pelo menos 9");
        }
        if (maoDePoker.isNaoConectadoNemAdequado(jogo)) {
            return oddsMap.get("Não conectado nem adequado, pelo menos um 2-9");
        }
        if (maoDePoker.isDesenhosSequenciaAberta(jogo)) {
            return oddsMap.get("Desenhos de sequência aberta");
        }
        if (maoDePoker.isQuatroParaFlush(jogo)) {
            return oddsMap.get("Quatro para um flush");
        }
        if (maoDePoker.isSequenciaInterna(jogo)) {
            return oddsMap.get("Sequência interna");
        }
        if (maoDePoker.isUmParParaDoisParesOuTrinca(jogo)) {
            return oddsMap.get("Um par para dois pares ou trinca");
        }
        if (maoDePoker.isOvercards(jogo)) {
            return oddsMap.get("Overcards");
        }
        if (maoDePoker.isComprandoSet(jogo)) {
            return oddsMap.get("Comprando para um set");
        }

        return oddsMap.get("Carta Alta");  // Se nenhuma das mãos acima for identificada, é uma Carta Alta
    }
}