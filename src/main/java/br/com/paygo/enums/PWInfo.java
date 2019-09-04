package br.com.paygo.enums;

import br.com.paygo.exception.InvalidInfoType;

import java.util.Arrays;

/**
 * Tipos de dados que podem ser informados pela Automação
 */
public enum PWInfo {
    OPERATION(2),    // Tipo de transação (PWOPER_xxx). Consultar os valores possíveis na descrição da função PW_iNewTransac
    POSID(17),       // Identificador do Ponto de Captura.
    AUTNAME(21),     // Nome do aplicativo de Automação
    AUTVER(22),      // Versão do aplicativo de Automação
    AUTDEV(23),      // Empresa desenvolvedora do aplicativo de Automação.
    DESTTCPIP(27),   // Endereço TCP/IP para comunicação com a infraestrutura Pay&Go Web), no formato <endereço IP>:<porta TCP> ou <nome do servidor>:<porta TCP>
    MERCHCNPJCPF(28), // CNPJ (ou CPF) do Estabelecimento), sem formatação. No caso de estarem sendo utilizadas afiliações de mais de um estabelecimento), este dado pode ser adicionado pela automação para selecionar previamente o estabelecimento a ser utilizado para determinada transação. Caso este dado não seja informado), será solicitada a exibição de um menu para a escolha dentre os vários estabelecimentos disponíveis.
    AUTCAP(36),       // Capacidades da Automação (soma dos valores abaixo): 1: funcionalidade de troco/saque; 2: funcionalidade de desconto; 4: valor fixo), sempre incluir; 8: impressão das vias diferenciadas do comprovante para Cliente/Estabelecimento; 16: impressão do cupom reduzido. 32: utilização de saldo total do voucher para abatimento do valor da compra.
    TOTAMNT(37),      // Valor total da operação), considerando CURREXP (em centavos se igual a 2)), incluindo desconto), saque), gorjeta), taxa de embarque), etc.
    CURRENCY(38),     // Moeda (padrão ISO4217), 986 para o Real)
    CURREXP(39),      // Expoente da moeda (2 para centavos)
    FISCALREF(40),    // Identificador do documento fiscal
    CARDTYPE(41),     // Tipo de cartão utilizado (PW_iGetResult)), ou tipos de cartão aceitos (soma dos valores abaixo), PW_iAddParam): 1: crédito 2: débito 4: voucher/PAT 8: outros
    PRODUCTNAME(42),  // Nome/tipo do produto utilizado), na nomenclatura do Provedor.
    DATETIME(49),     // Data e hora local da transação), no formato “AAAAMMDDhhmmss”
    REQNUM(50),       // Referência local da transação
    AUTHSYST(53),     // Nome do Provedor: “ELAVON”; “FILLIP”; “LIBERCARD”; “RV”; etc
    VIRTMERCH(54),    // Identificador do Estabelecimento
    AUTMERCHID(56),   // Identificador do estabelecimento para o Provedor (código de afiliação).
    PHONEFULLNO(58),  // Número do telefone), com o DDD (10 ou 11 dígitos).
    FINTYPE(59),      // Modalidade de financiamento da transação: 1: à vista 2: parcelado pelo emissor 4: parcelado pelo estabelecimento 8: pré-datado
    INSTALLMENTS(60), // Quantidade de parcelas
    INSTALLMDATE(61), // Data de vencimento do pré-datado), ou da primeira parcela. Formato “DDMMAA
    PRODUCTID(62),    // Identificação do produto utilizado), de acordo com a nomenclatura do Provedor.
    RESULTMSG(66),    // Mensagem descrevendo o resultado final da transação), seja esta bem ou mal sucedida (conforme “4.3.Interface com o usuário”), página 8
    CNFREQ(67),       // Necessidade de confirmação: 0: não requer confirmação; 1: requer confirmação.
    AUTLOCREF(68),    // Referência da transação para a infraestrutura Pay&Go Web
    AUTEXTREF(69),    // Referência da transação para o Provedor (NSU host).
    AUTHCODE(70),     // Código de autorização
    AUTRESPCODE(71),  // Código de resposta da transação (campo ISO8583:39)
    AUTDATETIME(72),  // Data/hora da transação para o Provedor), formato “AAAAMMDDhhmmss”.
    DISCOUNTAMT(73),  // Valor do desconto concedido pelo Provedor), considerando CURREXP), já deduzido em TOTAMNT
    CASHBACKAMT(74),  // Valor do saque/troco), considerando CURREXP), já incluído em TOTAMNT
    CARDNAME(75),     // Nome do cartão ou do emissor do cartão
    ONOFF(76),        // Modalidade da transação: 1: online 2: off-line
    BOARDINGTAX(77),  // Valor da taxa de embarque), considerando CURREXP), já incluído em TOTAMNT
    TIPAMOUNT(78),    // Valor da taxa de serviço (gorjeta)), considerando CURREXP), já incluído em TOTAMNT
    INSTALLM1AMT(79), // Valor da entrada para um pagamento parcelado), considerando CURREXP), já incluído em TOTAMNT
    INSTALLMAMNT(80), // Valor da parcela), considerando CURREXP), já incluído em TOTAMNT
    RCPTFULL(82),     // Comprovante para impressão – Via completa. Até 40 colunas), quebras de linha identificadas pelo caractere 0Dh
    RCPTMERCH(83),    // Comprovante para impressão – Via diferenciada para o Estabelecimento. Até 40 colunas), quebras de linha identificadas pelo caractere 0Dh.
    RCPTCHOLDER(84),  // Comprovante para impressão – Via diferenciada para o Cliente. Até 40 colunas), quebras de linha identificadas pelo caractere 0Dh.
    RCPTCHSHORT(85),  // Comprovante para impressão – Cupom reduzido (para o Cliente). Até 40 colunas), quebras de linha identificadas pelo caractere 0Dh
    TRNORIGDATE(87),  // Data da transação original), no caso de um cancelamento ou uma confirmação de pré-autorização (formato “DDMMAA”).
    TRNORIGNSU(88),  // NSU da transação original), no caso de um cancelamento ou uma confirmação de pré-autorizaçã
    SALDOVOUCHER(89), // Saldo do cartão voucher recebido do autorizador
    TRNORIGAMNT(96),  // Valor da transação original), no caso de um cancelamento ou uma confirmação de pré-autorização.
    TRNORIGAUTH(98),  // Código de autorização da transação original), no caso de um cancelamento ou uma confirmação de pré-autorização
    LANGUAGE(108),    // Idioma a ser utilizado para a interface com o cliente: 0: Português 1: Inglês 2: Espanhol
    PROCESSMSG(111),  // Mensagem a ser exibida para o cliente durante o processamento da transação
    TRNORIGREQNUM(114), // Número da solicitação da transação original), no caso de um cancelamento ou uma confirmação de pré-autorização
    TRNORIGTIME(115),   // Hora da transação original), no caso de um cancelamento ou uma confirmação de pré-autorização (formato “HHMMSS”).
    CNCDSPMSG(116),     // Mensagem a ser exibida para o operador no terminal no caso da transação ser abortada (cancelamento ou timeout).
    CNCPPMSG(117),      // Mensagem a ser exibida para o portador no PIN-pad no caso da transação ser abortada (cancelamento ou timeout).
    CARDENTMODE(192),   // Modo(s) de entrada do cartão: 1: digitado 2: tarja magnética 4: chip com contato 16: fallback de chip para tarja 32: chip sem contato simulando tarja (cliente informa tipo efetivamente utilizado) 64: chip sem contato EMV (cliente informa tipo efetivamente utilizado) 256: fallback de tarja para digitado
    CARDFULLPAN(193),   // Número do cartão completo), para transação digitada. Este dado não pode ser recuperado pela função PW_iGetResult
    CARDEXPDATE(194),   // Data de vencimento do cartão (formato “MMAA”).
    CARDNAMESTD(196),   // Descrição do produto bandeira padrão relacionado ao BIN.
    CARDPARCPAN(200),   // Número do cartão), truncado ou mascarado
    CHOLDVERIF(207),    // Verificação do portador), soma dos seguintes valores: “1”: Assinatura do portador em papel. “2”: Senha verificada off-line. “4”: Senha off-line bloqueada no decorrer desta transação. “8”: Senha verificada online
    AID(216),           // Aplicação do cartão utilizada durante a transação
    BARCODENTMODE(233), // Modo(s) de entrada do código de barras: 1:  digitado; 2:  lido através de dispositivo eletrônico.
    BARCODE(234),       // Código de barras completo), lido ou digitado
    MERCHADDDATA1(240), // Dados adicionais relevantes para a Automação (#1)
    MERCHADDDATA2(241), // Dados adicionais relevantes para a Automação (#2)
    MERCHADDDATA3(242), // Dados adicionais relevantes para a Automação (#3)
    MERCHADDDATA4(243), // Dados adicionais relevantes para a Automação (#4)
    RCPTPRN(244),       // Indica quais vias de comprovante devem ser impressas: 0: não há comprovante 1: imprimir somente a via do Cliente 2: imprimir somente a via do Estabelecimento 3: imprimir ambas as vias do Cliente e do Estabelecimento
    AUTHMNGTUSER(245),  // Identificador do usuário autenticado com a senha do lojista
    AUTHTECHUSER(246),  // Identificador do usuário autenticado com a senha técnica.
    PAYMNTTYPE(7969),   // Modalidade de pagamento: 1: cartão 2: dinheiro 3: cheque
    USINGPINPAD(32513), // Indica se o ponto de captura faz ou não o uso de PIN-pad: 0: Não utiliza PIN-pad; 1: Utiliza PIN-pad.
    PPCOMMPORT(32514),  // Número da porta serial à qual o PIN-pad está conectado. O valor 0 (zero) indica uma busca automática desta porta
    IDLEPROCTIME(32516), // Próxima data e horário em que a função PW_iIdleProc deve ser chamada pela Automação. Formato “AAMMDDHHMMSS”
    PNDAUTHSYST(32517),  // Nome do provedor para o qual existe uma transação pendente.
    PNDVIRTMERCH(32518), // Identificador do Estabelecimento para o qual existe uma transação pendente
    PNDREQNUM(32519),    // Referência local da transação que está pendente.
    PNDAUTLOCREF(32520), // Referência para a infraestrutura Pay&Go Web da transação que está pendente.
    PNDAUTEXTREF(32521), // Referência para o Provedor da transação que está pendente
    LOCALINFO1(32522),   // Texto exibido para um item de menu selecionado pelo usuário
    SERVERPND(32523),    // Indica se o ponto de captura possui alguma pendência a ser resolvida com o Pay&Go Web: 0: não possui pendência; 1: possui pendência
    PPINFO(0x7F15),    // Informações do PIN-pad conectado), seguindo o padrão posição/informação abaixo: 001-020 / Nome do fabricante do PIN-pad. 021-039 / Modelo/versão do hardware. 040 / Se o PIN-pad suporta cartão com chip sem contato), este campo deve conter a letra “C”), caso contrário um espaço em branco. 041-060 / Versão do software básico/firmware. 061-064 / Versão da especificação), no formato “V.VV”. 065-080 / Versão da aplicação básica), no formato “VVV.VV AAMMDD” (com 3 espaços à direita). 081-100 / Número de série do PIN-pad (com espaços à direita)
    DUEAMNT(0xBF06),   // Valor devido pelo usuário), considerando CURREXP), já deduzido em TOTAMNT
    READJUSTEDAMNT(0xBF09), // Valor total da transação reajustado), este campo será utilizado caso o autorizador), por alguma regra de negócio específica dele), resolva alterar o valor total que foi solicitado para a transação
    STATUS((short)0x6F);

    private final int value;

    PWInfo(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PWInfo valueOf(short value) throws InvalidInfoType {
        return Arrays.stream(values()).filter(pwInfo -> pwInfo.value == value).findFirst().orElseThrow(() -> new InvalidInfoType("Dado do tipo PWInfo não mapeado (" + value + ")."));
    }
}
