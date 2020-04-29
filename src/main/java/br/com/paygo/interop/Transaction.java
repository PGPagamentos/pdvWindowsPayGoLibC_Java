package br.com.paygo.interop;

import br.com.paygo.PGWeb;
import br.com.paygo.enums.*;
import br.com.paygo.exceptions.InvalidDataType;
import br.com.paygo.exceptions.MandatoryParamException;
import br.com.paygo.helper.TextFormatter;
import br.com.paygo.helper.UserInputHandler;
import br.com.paygo.ui.UserInterface;
import com.sun.jna.ptr.ShortByReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe responsável por executar todas as etapas de uma transação através da biblioteca PGWebLib
 */
public class Transaction {

    @SuppressWarnings("serial")
	private static final Map<PWInfo, String> mandatoryParams = new HashMap<PWInfo, String>() {{
        put(PWInfo.AUTDEV, "AUTOMACAO DE SISTEMAS"); // Automação deve ALTERAR esse campo!!
        put(PWInfo.AUTVER, ApplicationProperties.INSTANCE.getAppVersion()); // Automação deve ALTERAR esse campo!!
        put(PWInfo.AUTNAME, ApplicationProperties.INSTANCE.getAppName()); // Automação deve ALTERAR esse campo!!
        put(PWInfo.AUTCAP, "28"); // Automação deve ALTERAR esse campo!!
        put(PWInfo.AUTHTECHUSER, "PAYGOTESTE"); // Automação deve ALTERAR esse campo!!
    }};

    @SuppressWarnings("serial")
	private static final Map<PWInfo, String> saleParams = new HashMap<PWInfo, String>() {{
        put(PWInfo.CURRENCY, "986");
        put(PWInfo.CURREXP, "2");
    }};

    private final UserInterface userInterface;
    private final ShortByReference numParams;
    private PWOper operation;
    private PWGetData[] getData;
    private HashMap<PWInfo, String> externalParams;
    private byte[] displayMessage;
    private byte[] value;

    public Transaction(PWOper operation, UserInterface userInterface) {
        this.operation = operation;
        this.userInterface = userInterface;
        this.numParams = new ShortByReference((short)9);
        this.getData = (PWGetData[]) new PWGetData().toArray(numParams.getValue());
        this.externalParams = new HashMap<>();
        this.displayMessage = new byte[128];
        this.value = new byte[1000];
    }

    /**
     * Método responsável por iniciar um tipo de transação, refletindo diretamento ao
     * PW_iNewTransac da PGWebLib.
     * */
    private PWRet start() throws Exception {
    	//Executa a função de nova transação da biblioteca de pagamento PGWebLib
        PWRet ret = LibFunctions.newTransaction(this.operation);

        // Caso a chamada de inicio de nova transação deu certo, adiciona os parâmetros
        // informados antes do inicio da transação. Caso exista, por exemplo:
        // Tipo de cartão, tipo de financiamento, etc...        
        if (ret == PWRet.OK) {
            userInterface.logInfo("=> PW_iNewTransac: " + ret + "(" + ret.getValue() + ")");
            // Adiciona os parâmetros mandatórios da transação
            this.addMandatoryParams();

            // Adiciona os parâmetros pré informados antes da transação.
            externalParams = userInterface.getParams();
            
			if (externalParams != null && !externalParams.isEmpty()) 
			{			
				Map.Entry<PWInfo, String> entry = externalParams.entrySet().iterator().next();
				while (entry != null && !externalParams.isEmpty()) 
				{
					this.addParam(entry.getKey(), entry.getValue());
					externalParams.remove(entry.getKey());
					if (externalParams != null && !externalParams.isEmpty())
						entry = externalParams.entrySet().iterator().next();
				}
			}
		}

        return ret;
    }
    
    /**
     * Método responsável pelo processamento da transação, ou seja, ele realizará todas as capturas de dados,
     * impressões de comprovantes, obtenção de dados da transação e confirmação/desfazimento da transação.
     */
	public PWRet executeOperation() {
		PWRet returnedCode;
		boolean abort = false;

		try {
			/* Inicia a transação PW_iNewTransac */
			returnedCode = this.start();

			/* Caso ocorreu algum erro, retorna. */
			if (returnedCode != PWRet.OK) {
				this.getResult(PWInfo.RESULTMSG);
				userInterface.logInfo("\n\tRESPOSTA: " + getValue(true));
				return returnedCode;
			}

			/*
			 * Loop principal de captura de dados (função PW_iExecTransac)
			 * Toda captura de dados digitados, menus, interação com PIN-pad (PP_iEventLoop)
			 * serão feitas nessa sessão.
			 * */
			do {
				/* Chama o PW_iExecTransac da biblioteca de pagamento PGWebLib */
				returnedCode = this.executeTransaction();
				userInterface.logInfo("=> PW_iExecTransac: " + returnedCode + "(" + returnedCode.getValue() + ")");

				/* Verifica se precisam ser informados mais parâmetros para a transação */
				if (returnedCode == PWRet.MOREDATA) {
					/* Faz a captura dos dados solicitados pela PGWebLib */
					PWRet moreDataRet = this.retrieveMoreData();
					/* Caso ocorra algum erro ou usuário cancele a captura */
					if (moreDataRet != PWRet.OK)
						return moreDataRet;
				}
				/* Caso não deva ser feita nenhuma ação, chama novamente o iExecTransac */
				else if(returnedCode == PWRet.NOTHING)
					continue;
			} while (returnedCode == PWRet.MOREDATA && !abort);
			
			/* Caso exista pendências */
			if (returnedCode == PWRet.FROMHOSTPENDTRN) {
				pendingTransaction();
				return returnedCode;
			}
			
			/* Caso a transaçao foi concluída, imprime os comprovantes */
			else if (returnedCode == PWRet.OK) {
				this.printReceipt();
			}
			
			/* Exibe todos os parâmetros da transação que a biblioteca retornou */
			printResultParams();
			
			/* Verifica se a transação necessita de confirmação e realiza */
			returnedCode = confirmationTransaction();
			if(returnedCode != PWRet.OK)
				return returnedCode;
			
			/* Verificação e execução, caso necessário da função IdleProc */
			executeIdleProc();
			
			return returnedCode;

		} catch (Exception e) {
			userInterface.showException("Ocorreu um erro: {"+e.getMessage()+"}", true);
			return PWRet.INTERNALERR;
		}
	}
	
	/**
	 * Método responsável pela verificação e execução do IdleProc
	 * */
	private void executeIdleProc() {
		// TODO Auto-generated method stub
		
	}

	/**
     * Método responsável por tratar a captura de dados da PGWebLib 
     */
	private PWRet retrieveMoreData() throws InvalidDataType {
		PWRet response = PWRet.OK;
		PWRet eventLoopResponse = PWRet.OK;

		/*
		 * A Biblioteca pode retornar mais de uma captura por interação, por isso existe
		 * o loop abaixo. Assim, mais de um dado, por exemplo um dado digita e uma
		 * captura de menu poderão ser solicitados em uma única interação.
		 */
		for (short index = 0; index < this.numParams.getValue(); index++) {
			PWGetData pwGetData = this.getData[index];
			PWInfo identifier = pwGetData.getIdentificador();

			// Em qual tag será informado o dado captura e qual o tipo de dado solicitado
			userInterface.logInfo("=> Dado requisitado = " + identifier + " | Tipo de Dado = " + pwGetData.getTipoDeDado());

			switch (pwGetData.getTipoDeDado()) {
			case MENU:
				String optionSelected;

				Menu menu = new Menu(pwGetData);
				/* TODO: A montagem do menu deve considerar os parâmetros: 
				 * bTeclasDeAtalho -> Caso esteja sinalizado, o menu deverá suportar Hotkeys (atalhos)
				 * bItemInicial -> Opção inicial no menu que deve ser destacada.
				*/
				optionSelected = UserInputHandler.requestSelectionFromMenu(userInterface, menu);

				if (optionSelected.equals("-1")) {
					response = PWRet.CANCEL;
				} else {
					this.addParam(identifier, optionSelected);
				}

				break;
			case USERAUTH:
				String password = UserInputHandler.getTypedData(userInterface, "Digite a senha:", 20, 4,
						PWValidDataEntry.ALPHANUMERIC);
				// Esse tipo de captura não é validado pela PGWebLib e sim pela automação. Portanto, caso a automação veja necessidade
				// Ela poderá validar a senha inserida pelo operador nesse momento.
				this.addParam(identifier, password);
				break;
			case TYPED:
				String typedData;
				
				/*
				 * TODO: Os seguintes parâmetros devem ser considerados:
				 * bOcultarDadosDigitados -> Dados devem ser exibidos mascarados.
				 * 
				 * # Caso o tipo de entrada for numérico:
				 * ulValorMinimo, ulValorMaximo e as mensagens szMsgDadoMenor e szMsgDadoMaior 
				 * bValidacaoDado -> Caso falhe, apresentar szMsgValidacao
				 * 
				 * Se a bValidacaoDado == 6, iniciando com szValorInicial, verificar que os dois dados são iguais
				 * na segunda solicitação deve substituir o szPrompt por szMsgConfirmacao
				 * 
				 * iniciarPelaEsquerda -> Inicia preenchimento do dado pela esquerda. 
                 * alinharDireita -> Alinha o dado capturado a direita.
				 * */
				typedData = UserInputHandler.getTypedData(userInterface, pwGetData.getPrompt(),
						pwGetData.getTamanhoMaximo(), pwGetData.getTamanhoMinimo(), pwGetData.getTipoEntradaPermitido(),
						pwGetData.getValorInicial(), pwGetData.getMascaraDeCaptura());

				if (typedData.equals("-1")) {
					response = PWRet.CANCEL;
				} else {
					this.addParam(identifier, typedData);
				}
				break;
			case PPREMCRD:				
				PWRet ret = LibFunctions.removeCardFromPINPad();
				userInterface.logInfo("=> PW_iPPRemoveCard: " + ret + "(" + ret.getValue() + ")");

				eventLoopResponse = executeEventLoop();
				response = eventLoopResponse;
				break;
			case CARDINF:
				/*
				 * TODO: Todo as mensagens de display estãos endo mostradas na caixa de log da UI
				 * Hoje, caso a captura do cartão não for somente digitado, ou seja, ele aceita chip e digitado
				 * o usuário não tem como realizar a transação digitada.
				 * 
				 * Deveria haver uma caixa de texto (exemplo) com a mensagem de processando e, no caso dessa captura,
				 * caso ocorresse um evento de tecla, o PIN-pad seria abortado e feita a captura digitada.
				 * */
				userInterface.logInfo(pwGetData.getPrompt());
				if (pwGetData.getTipoEntradaCartao() == 1) { // digitado
					System.out.println("Digite o numero do cartão");

					String cardNumber = UserInputHandler.getTypedData(userInterface, "Digite o numero do cartão:", 20,
							16, PWValidDataEntry.NUMERIC);

					addParam(PWInfo.CARDFULLPAN, cardNumber);
				} else { // pin-pad
					userInterface.logInfo("CAPTURA DE DADOS DO PIN-PAD");
					ret = LibFunctions.getCardFromPINPad(index);
					userInterface.logInfo("=> PW_iPPGetCard: " + ret + "(" + ret.getValue() + ")");
					eventLoopResponse = executeEventLoop();
					response = eventLoopResponse;
				}
				break;
			case CARDOFF:
				ret = LibFunctions.offlineCardProcessing(index);
				userInterface.logInfo("=> PW_iPPGoOnChip: " + ret + "(" + ret.getValue() + ")");
				eventLoopResponse = executeEventLoop();
				response = eventLoopResponse;
				break;
			case CARDONL:
				ret = LibFunctions.finishOfflineProcessing(index);
				userInterface.logInfo("=> PW_iPPFinishChip: " + ret + "(" + ret.getValue() + ")");
				eventLoopResponse = executeEventLoop();
				response = eventLoopResponse;
				break;
			case PPENCPIN:
				ret = LibFunctions.getPIN(index);
				userInterface.logInfo("=> PW_iPPGetPIN: " + ret + "(" + ret.getValue() + ")");
				eventLoopResponse = executeEventLoop();
				response = eventLoopResponse;
			case BARCODE:
				// Caso seja de escopo da automação, aqui é a interface onde é capturado o código
				// de barra, podendo ser digitada.
				break;
			case PPCONF:
				ret = LibFunctions.ppConfirmationData(index);
				userInterface.logInfo("=> PW_iPPConfirmData: " + ret + "(" + ret.getValue() + ")");
				eventLoopResponse = executeEventLoop();
				response = eventLoopResponse;
				break;
			case PPDATAPOSCNF:
				ret = LibFunctions.ppPositiveConfirmation(index);
				userInterface.logInfo("=> PW_iPPPositiveConfirmation: " + ret + "(" + ret.getValue() + ")");
				eventLoopResponse = executeEventLoop();
				response = eventLoopResponse;
				break;
			case PPENTRY:
				ret = LibFunctions.ppGetData(index);
				userInterface.logInfo("=> PW_iPPGetData: " + ret + "(" + ret.getValue() + ")");
				eventLoopResponse = executeEventLoop();
				response = eventLoopResponse;
				break;
			case PPGENCMD:
				ret = LibFunctions.ppGenericCommand(index);
				userInterface.logInfo("=> PW_iPPGenericCMD: " + ret + "(" + ret.getValue() + ")");
				eventLoopResponse = executeEventLoop();
				response = eventLoopResponse;
				break;
			default:
				break;
			}

		}

		return response;
	}
	
	/**
	 * Método responsável pela confirmação da transação caso haja necessidade.
	 * */
	private PWRet confirmationTransaction() {
		this.value = new byte[1000];
		PWRet returnedCode = PWRet.OK;
		
		/* Verifica se a transação necessita de confirmação
		 * 0 - Não necessita
		 * 1 - Necessita */
		getResult(PWInfo.CNFREQ);
		if (new String(this.value).trim().equals("1")) 
		{
			// Obtém os dados da última transação efetuada pela biblioteca de pagamento
			PGWeb.confirmData = Confirmation.getConfirmationData(userInterface, false);
			
			// Inicia o processo de confirmação
			returnedCode = confirmTransaction();
		}		
		return returnedCode;
	}
	
	/**
	 * Método responsável por resolver a pendência de confirmação/desfazimento entre
	 * transação do ponto de captura e o servidor.
	 * */
	private void pendingTransaction() {
		// Caso haja uma transação pendente de confirmação, captura os dados e envia.
		userInterface.showException("Existe uma transação pendente", false);
		userInterface.logInfo("===========================================\n"
				+ "== ERRO - EXISTE UMA TRANSAÇÃO PENDENTE ==\n" + "===========================================");

		if (PGWeb.confirmData.isEmpty()) {
			PGWeb.confirmData = Confirmation.getConfirmationData(userInterface, true);
		}

		if (confirmTransaction() == PWRet.OK) {
			PGWeb.confirmData.clear();
		}
	}    

    private PWRet getResult(PWInfo param) {
        return LibFunctions.getResult(param, value);
    }
    
    public String getValue(boolean formatted) {
        return formatted ? TextFormatter.formatByteMessage(this.value) : new String(this.value);
    }

    /**
     * Método responsável por cancelar uma transação em execução
     */
    public void abort(boolean isPINPad) {
        if (isPINPad) {
            PWRet ret = LibFunctions.abortTransaction();
            userInterface.logInfo("=> PW_iPPAbort: " + ret + "(" + ret.getValue() + ")");            
            executeEventLoop();
        }
        userInterface.showException("Transação cancelada", false);
        userInterface.logInfo("\n\t--------- OPERAÇÃO CANCELADA ---------\n");
    }

    private void printReceipt() {
    	// Tamanho do comprovante pode ser até 32000 
    	this.value = new byte[32000];
    	PWRet returnedCode;
    	// Caso exista via do estabelecimento, imprime!
    	returnedCode = getResult(PWInfo.RCPTMERCH);		
		if (returnedCode == PWRet.OK) {
			userInterface.logInfo("\n\t------ VIA ESTABELECIMENTO ------");
			userInterface.logInfo(this.getValue(false));
			userInterface.logInfo("\n\t---- FIM VIA ESTABELECIMENTO ----");
		}
		
		// Caso exista via do estabelecimento, imprime!
		returnedCode = getResult(PWInfo.RCPTCHOLDER);
		if (returnedCode == PWRet.OK) {
			userInterface.logInfo("\n\t------ VIA CLIENTE ------");			
			userInterface.logInfo(this.getValue(false));
			userInterface.logInfo("\n\t---- FIM VIA CLIENTE ----");
		}
    }

    /**
     * Busca todas as informações de resultados disponíveis no momento
     */
    public void printResultParams() {
        PWRet ret;
        byte[] tempValue;

        userInterface.logInfo("============== PARAMETROS ==============");

        /* Loga todos os parâmetros que a PGWebLib devolveu para a transação
         * Esse parâmetros poderão ser utilizados pela automação caso forem úteis*/
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            tempValue = new byte[1000];

            ret = LibFunctions.getResult(i, tempValue);

            if (ret == PWRet.OK) {
                userInterface.logInfo(PWInfo.valueOf((short)i) + "<0X" + Integer.toHexString(i) + "> = " + TextFormatter.formatByteMessage(tempValue));
            }
        }
        userInterface.logInfo("========================================\n");
    }

    /**
     * Método responsável por enviar para a biblioteca PGWebLib os parâmetros obrigatórios para uma transação
     */
    private void addMandatoryParams() throws MandatoryParamException {
        userInterface.logInfo("\nAdicionando parâmetros obrigatórios: ");

        // Adiciona os parâmetros mandatórios
        for (Map.Entry<PWInfo, String> entry : mandatoryParams.entrySet()) {
            PWRet ret = addParam(entry.getKey(), entry.getValue());

            if (ret != PWRet.OK) {
                throw new MandatoryParamException("Parâmetro obrigatório {" + entry.getKey() + ", " + entry.getValue() + "} inválido.");
            }
        }

        // Adiciona os parâmetros de moeda e expoente da moeda
        if (this.operation == PWOper.SALE || this.operation == PWOper.SALEVOID ||
        		this.operation == PWOper.PREAUTH || this.operation == PWOper.SALEPRE|| this.operation == PWOper.CASHWDRWL) {
            for (Map.Entry<PWInfo, String> entry : saleParams.entrySet()) {
                PWRet ret = addParam(entry.getKey(), entry.getValue());

                if (ret != PWRet.OK) {
                    throw new MandatoryParamException("Parâmetro obrigatório {" + entry.getKey() + ", " + entry.getValue() + "} inválido.");
                }
            }
        }

        userInterface.logInfo("");
    }

    private PWRet addParam(PWInfo param, String data) {
        PWRet returnedCode = LibFunctions.addParam(param, data);
        userInterface.logInfo("=> PW_iAddParam (" + param.getValue() + " - " + param + "): " + data);
        return returnedCode;
    }

    private PWRet executeTransaction() {
        return LibFunctions.executeTransaction(getData, numParams);
    }    

    private PWRet executeEventLoop() {
        return EventLoop.execute(userInterface, this.displayMessage);
    }
    
    private PWRet confirmTransaction() {
        Confirmation confirmation = new Confirmation(userInterface, PGWeb.confirmData);
        PWRet returnedCode = confirmation.executeConfirmationProcess();

        if (returnedCode != PWRet.OK) {
        	userInterface.showException("Erro no processo de confirmação: " + returnedCode, false);
        }
        else {
        	PGWeb.confirmData.clear();        	
        }        
        return returnedCode;
    }
}