package br.com.paygo.enums;

/**
 * Códigos de retorno das transações
 */
public enum PWOper {
    NULL(0),         // Testa comunicação com a infraestrutura do Pay&Go Web
    INSTALL(1),      // Registra o Ponto de Captura perante a infraestrutura do Pay&Go Web), para que seja autorizado a realizar transações
    PARAMUPD(2),     // Obtém da infraestrutura do Pay&Go Web os parâmetros de operação atualizados do Ponto de Captura.
    REPRINT(16),     // Obtém o último comprovante gerado por uma transação
    RPTTRUNC(17),    // Obtém um relatório sintético das transações realizadas desde a última obtenção deste relatório
    RPTDETAIL(18),   // Relatório detalhado das transações realizadas na data informada), ou data atual.
    ADMIN(32),       // Acessa qualquer transação que não seja disponibilizada pelo comando SALE. Um menu é apresentado para o operador selecionar a transação desejada.
    SALE(33),        // (Venda) Realiza o pagamento de mercadorias e/ou serviços vendidos pelo Estabelecimento ao Cliente (tipicamente), com cartão de crédito/débito)), transferindo fundos entre as respectivas contas.
    SALEVOID(34),    // (Cancelamento de venda) Cancela uma transação SALE), realizando a transferência de fundos inversa
    PREPAID(35),     // Realiza a aquisição de créditos pré-pagos (por exemplo), recarga de celular).
    CHECKINQ(36),    // Consulta a validade de um cheque papel
    RETBALINQ(37),   // Consulta o saldo/limite do Estabelecimento (tipicamente), limite de crédito para venda de créditos pré-pagos).
    CRDBALINQ(38),   // Consulta o saldo do cartão do Cliente
    INITIALIZ(39),   // (Inicialização/abertura) Inicializa a operação junto ao Provedor e/ou obtém/atualiza os parâmetros de operação mantidos por este
    SETTLEMNT(40),   // (Fechamento/finalização) Finaliza a operação junto ao Provedor
    PREAUTH(41),     // (Pré-autorização) Reserva o valor correspondente a uma venda no limite do cartão de crédito de um Cliente), porém sem efetivar a transferência de fundos.
    PREAUTVOID(42),  // (Cancelamento de pré-autorização) Cancela uma transação PREAUTH), liberando o valor reservado no limite do cartão de crédito
    CASHWDRWL(43),   // (Saque) Registra a retirada de um valor em espécie pelo Cliente no Estabelecimento), para transferência de fundos nas respectivas contas
    LOCALMAINT(44),  // (Baixa técnica) Registra uma intervenção técnica no estabelecimento perante o Provedor.
    FINANCINQ(45),   // Consulta as taxas de financiamento referentes a uma possível venda parcelada), sem efetivar a transferência de fundos ou impactar o limite de crédito do Cliente
    ADDRVERIF(46),   // Verifica junto ao Provedor o endereço do Cliente
    SALEPRE(47),     // Efetiva uma pré-autorização (PREAUTH)), previamente realizada), realizando a transferência de fundos entre as contas do Estabelecimento e do Cliente
    LOYCREDIT(48),   // Registra o acúmulo de pontos pelo Cliente), a partir de um programa de fidelidade.
    LOYCREDVOID(49), // Cancela uma transação LOYCREDIT
    LOYDEBIT(50),    // Registra o resgate de pontos/prêmio pelo Cliente), a partir de um programa de fidelidade.
    LOYDEBVOID(51),  // Cancela uma transação LOYDEBIT
    VOID(57),        // Exibe um menu com os cancelamentos disponíveis), caso só exista um tipo), este é selecionado automaticamente
    VERSION(252),    // (Versão) Permite consultar a versão da biblioteca atualmente em uso.
    CONFIG(253),     // (Configuração) Visualiza e altera os parâmetros de operação locais da biblioteca
    MAINTENANCE(254),// (Manutenção) Apaga todas as configurações do Ponto de Captura), devendo ser novamente realizada uma transação de Instalação.

    // Adicionados para exibição no menu da interface gráfica
    PNDCNF(3),      // (Confirmação) Verifica se existe alguma transação pendente
    SELFSERV(4);    // (Venda) Auto atendimento para realização de uma venda

    private final int value;

    PWOper(int value)
    {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
