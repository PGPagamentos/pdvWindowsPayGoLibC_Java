package br.com.paygo.enums;

import br.com.paygo.exception.InvalidReturnTypeException;

import java.util.Arrays;

/**
 * Códigos de Erro de Retorno da Biblioteca
 */
public enum PWRet {
    OK((short)0),                        // Operação bem sucedida
    FROMHOSTPENDTRN((short)-2599),       // Existe uma transação pendente), é necessário confirmar ou desfazer essa transação através de PW_iConfirmation.
    FROMHOSTPOSAUTHERR((short)-2598),    // Falha de autenticação do ponto de captura com a infraestrutura do Pay&Go Web.
    FROMHOSTUSRAUTHERR((short)-2597),    // Falha de autenticação do usuário
    FROMHOST((short)-2596),              // Erro retornado pela infraestrutura do Pay&Go Web. Verificar a mensagem (PWINFO_RESULTMSG) para mais informações
    TLVERR((short)-2595),                // Falha de comunicação com a infraestrutura do Pay&Go Web (codificação da mensagem).
    SRVINVPARAM((short)-2594),           // Falha de comunicação com a infraestrutura do Pay&Go Web (parâmetro inválido).
    REQPARAM((short)-2593),              // Falha de comunicação com a infraestrutura do Pay&Go Web (falta parâmetro obrigatório).
    HOSTCONNUNK((short)-2592),           // Erro interno da biblioteca (conexão ao host).
    INTERNALERR((short)-2591),           // Erro interno da biblioteca
    BLOCKED((short)-2590),               // O ponto de captura foi bloqueado para uso
    FROMHOSTTRNNFOUND((short)-2589),     // A transação referenciada (cancelamento), confirmação), etc.) não foi encontrada.
    PARAMSFILEERR((short)-2588),         // Inconsistência dos parâmetros de operação recebidos da infraestrutura do Pay&Go Web
    NOCARDENTMODE((short)-2587),         // O Ponto de Captura não tem a capacidade de efetuar a captura do cartão através dos tipos de entrada especificados pelo Pay&Go Web
    INVALIDVIRTMERCH((short)-2586),      // Falha de comunicação com a infraestrutura do Pay&Go Web (código de afiliação inválido).
    HOSTTIMEOUT((short)-2585),           // Falha de comunicação com a infraestrutura do Pay&Go Web (tempo de resposta esgotado).
    CONFIGREQUIRED((short)-2584),        // Erro de configuração. É necessário acionar a função de configuração.
    HOSTCONNERR((short)-2583),           // Falha de conexão à infraestrutura do Pay&Go Web
    HOSTCONNLOST((short)-2582),          // A conexão com a infraestrutura do Pay&Go Web foi interrompida
    FILEERR((short)-2581),               // Falha no acesso aos arquivos da biblioteca de integração
    PINPADERR((short)-2580),             // Falha de comunicação com o PIN-pad (aplicação).
    MAGSTRIPEERR((short)-2579),          // Formato de tarja magnética não reconhecido
    PPCRYPTERR((short)-2578),            // Falha de comunicação com o PIN-pad (comunicação segura).
    SSLCERTERR((short)-2577),            // Falha no certificado SSL
    SSLNCONN((short)-2576),              // Falha ao tentar estabelecer conexão SSL
    GPRSATTACHFAILED((short)-2575),      // Falha no registro GPRS.
    INVPARAM((short)-2499),              // Parâmetro inválido passado à função
    NOTINST((short)-2498),               // Ponto de Captura não instalado. É necessário acionar a função de Instalação.
    MOREDATA((short)-2497),              // Ainda existem dados que precisam ser capturados para a transação poder ser realizada
    NODATA((short)-2496),                // A informação solicitada não está disponível.
    DISPLAY((short)-2495),               // A Automação deve apresentar uma mensagem para o operador
    INVCALL((short)-2494),               // Função chamada no momento incorreto
    NOTHING((short)-2493),               // Nada a fazer), continuar o processamento
    BUFOVFLW((short)-2492),              // O tamanho da área de memória informado é insuficiente.
    CANCEL((short)-2491),                // Operação cancelada pelo operador
    TIMEOUT((short)-2490),               // Tempo limite excedido para ação do operador
    PPNOTFOUND((short)-2489),            // PIN-pad não encontrado na busca efetuada.
    TRNNOTINIT((short)-2488),            // Não foi chamada a função PW_iNewTransac
    DLLNOTINIT((short)-2487),            // Não foi chamada a função PW_iInit
    FALLBACK((short)-2486),              // Ocorreu um erro no cartão magnético), passar a aceitar o cartão digitado), caso já não esteja sendo aceito
    WRITERR((short)-2485),               // Falha de gravação no diretório de trabalho.
    PPCOMERR((short)-2484),              // Falha na comunicação com o PIN-pad (protocolo).
    NOMANDATORY((short)-2483),           // Algum dos parâmetros obrigatórios não foi adicionado
    INVALIDTRN((short)-2482),            // A transação informada para confirmação não existe ou já foi confirmada anteriormente.
    PPS_XXX((short)-2200),               // Erros retornados pelo PIN-pad), conforme seção 10.2
    PPS_MAX ((short)-2100),
    PPS_MIN ((short)-2000);

    private final short value;

    PWRet(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }

    public static PWRet valueOf(short value) throws InvalidReturnTypeException {
        return Arrays.stream(values()).filter(pwRet -> pwRet.value == value).findFirst().orElseThrow(() -> new InvalidReturnTypeException("O valor retornado (" + value + ") não foi mapeado."));
    }
}
