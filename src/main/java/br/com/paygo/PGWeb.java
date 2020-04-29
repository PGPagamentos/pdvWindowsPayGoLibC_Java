package br.com.paygo;

import br.com.paygo.enums.*;
import br.com.paygo.helper.TextFormatter;
import br.com.paygo.interop.Confirmation;
import br.com.paygo.interop.EventLoop;
import br.com.paygo.interop.LibFunctions;
import br.com.paygo.interop.PWGetData;
import br.com.paygo.interop.PWOperations;
import br.com.paygo.interop.Transaction;
import br.com.paygo.ui.UserInterface;

import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.ShortByReference;

/**
 * Classe responsável por fazer a interface entre os métodos da biblioteca
 * PGWebLib e a interface de usuário
 */
public class PGWeb {

	private final UserInterface userInterface;
	private static final String PATH = ".";
	private Transaction transaction;

	public static LinkedHashMap<PWInfo, String> confirmData = new LinkedHashMap<>();

	public PGWeb(UserInterface userInterface) {
		this.userInterface = userInterface;
	}

	public void init() {
		try {
			PWRet returnedCode = LibFunctions.init(PATH);
			if (returnedCode != PWRet.OK)
				userInterface.showException("Erro ao iniciar a biblioteca: " + returnedCode, true);
		} catch (Exception e) {
			userInterface.showException("Erro ao iniciar a comunicação.", true);
		}
	}

	public void version() {
		userInterface.logInfo("\n=== VERIFICAÇÃO DE VERSÃO DA DLL ===\n");
		transaction = new Transaction(PWOper.VERSION, userInterface);
		PWRet returnedCode = transaction.executeOperation();

		if (returnedCode == PWRet.OK) {
			userInterface.logInfo("\n=== VERIFICAÇÃO DE VERSÃO DA DLL CONCLUÍDA ===\n");
		}
	}

	public void install() {
		userInterface.logInfo("\n=== INSTALAÇÃO ===\n");
		transaction = new Transaction(PWOper.INSTALL, userInterface);
		PWRet returnedCode = transaction.executeOperation();

		if (returnedCode == PWRet.OK) {
			userInterface.logInfo("\n=== INSTALAÇÃO CONCLUÍDA ===\n");
		}

		transaction.printResultParams();
	}

	public void admin() {
		transaction = new Transaction(PWOper.ADMIN, userInterface);
		PWRet returnedCode = transaction.executeOperation();

		if (returnedCode == PWRet.OK) {
			userInterface.logInfo("\n=== ADMINISTRATIVO CONCLUÍDO ===\n");
		}
	}

	public void sale() {
		userInterface.logInfo("\n=== VENDA ===\n");

		transaction = new Transaction(PWOper.SALE, userInterface);
		PWRet returnedCode = transaction.executeOperation();

		if (returnedCode == PWRet.OK) {
			userInterface.logInfo("\n=> VENDA CONCLUÍDA <=\n");
		}
	}

	public void reprint() {
		userInterface.logInfo("\n=== REIMPRESSÃO ===\n");
		transaction = new Transaction(PWOper.REPRINT, userInterface);
		PWRet returnedCode = transaction.executeOperation();

		if (returnedCode == PWRet.OK) {
			userInterface.logInfo("\n=== REIMPRESSÃO CONCLUÍDA ===\n");
		}

		transaction.printResultParams();
	}

	public void saleVoid() {
		userInterface.logInfo("\n=== CANCELAMENTO DE VENDA ===\n");
		transaction = new Transaction(PWOper.SALEVOID, userInterface);
		PWRet returnedCode = transaction.executeOperation();

		if (returnedCode == PWRet.OK) {
			userInterface.logInfo("\n=== CANCELAMENTO DE VENDA CONCLUÍDO ===\n");
		}

		transaction.printResultParams();
	}

	public void checkPendingConfirmation() {
		userInterface.logInfo("\n=== CONFIRMAÇÃO ===\n");

		logConfirmData();

		try {
			Confirmation confirmation = new Confirmation(userInterface, PGWeb.confirmData);
			PWRet ret = confirmation.executeConfirmationProcess();
			byte[] value = new byte[1000];

			LibFunctions.getResult(PWInfo.RESULTMSG, value);
			userInterface.logInfo(PWInfo.RESULTMSG + "<0X" + Integer.toHexString(PWInfo.CNFREQ.getValue()) + "> = "
					+ TextFormatter.formatByteMessage(value));

			if (ret == PWRet.OK) {
				userInterface.logInfo("Confirmação OK");
			}
		} catch (Exception e) {
			userInterface.showException(e.getMessage(), false);
		}

		userInterface.logInfo("\n=== CONFIRMAÇÃO CONCLUÍDA ===\n");
	}

	private void logConfirmData() {
		userInterface.logInfo("\n=== PARAM CONFIRMACAO ===");

		for (Map.Entry<PWInfo, String> entry : confirmData.entrySet()) {
			userInterface.logInfo(entry.getKey() + " = " + entry.getValue());
		}

		userInterface.logInfo("=========================\n");
	}

	public void reportTrunc() {
		this.generateReport(PWOper.RPTTRUNC);
	}

	public void reportDetail() {
		this.generateReport(PWOper.RPTDETAIL);
	}

	private void generateReport(PWOper reportType) {
		transaction = new Transaction(reportType, userInterface);
		PWRet returnedCode = transaction.executeOperation();

		if (returnedCode == PWRet.OK) {
			userInterface.logInfo("\n=== RELATÓRIO ===\n");
			byte[] value = new byte[1000];
			LibFunctions.getResult(PWInfo.RCPTFULL, value);

			userInterface.logInfo(new String(value));

			userInterface.logInfo("\n=== RELATÓRIO CONCLUÍDO ===\n");
		}

		transaction.printResultParams();
	}

	public String executeNonTransacionFunction(PWPINPadFunction functionSelecte, PWUserDataMessage message, int minSize,
			int maxSize, String msgDisplayPP, LongByReference eventCapturaPP) {
		
		ShortByReference numParams = new ShortByReference((short) 100);
		PWOperations[] pwOpers = (PWOperations[]) new PWOperations().toArray(numParams.getValue());
		
		PWRet ret = PWRet.OK;
		String testExampleKey = "12345678901234567890123456789012";
		String promptMessage = "TESTE DE CAPTURA\nDE PIN BLOCK";
		String resultAux = new String();
		byte[] response = new byte[1000];
		byte[] displayMessage = new byte[50];
		byte value;
		int iAux = 0;

		try {
			switch (functionSelecte) {

			/* Função para a exibição de uma mensagem no PIN-pad */
			case PW_iPPDisplay:
				ret = LibFunctions.showMessageOnPINPad(msgDisplayPP);
				if (ret != PWRet.OK)
					break;

				ret = EventLoop.execute(null, displayMessage);
				if (ret != PWRet.OK)
					break;

				return new String("Mensagem {" + msgDisplayPP + "} exibida com sucesso no PIN-pad");

			/* Função para listar todas as operações administrativas e de venda do ponto de captura	*/
			case PW_iGetOperations:
				// Define o value como 3, ou seja, PWOPTYPE_ADMIN + PWOPTYPE_SALE
				value = 3;
				
				ret = LibFunctions.pwGetOperations(value, pwOpers, numParams);
				if(ret != PWRet.OK)
					break;
							
				//Monta lista de operações recebidas na biblioteca
				iAux = 0;
				for(PWOperations operAux : pwOpers) {
					if(iAux >= numParams.getValue())
						break;
					
					resultAux = resultAux + "{" + operAux.getTexto() + "} {" + operAux.getValor() + "}\n";
					iAux++;
				}
				
				return resultAux;
				
			/* Função para obter o PIN Block */
			case PW_iPPGetPINBlock:
				if (minSize < 4)
					throw new Exception("Tamanho mínimo deve ser maior que 4!");

				ret = LibFunctions.getPINBlock(testExampleKey, minSize, maxSize, 30, promptMessage, response);
				break;
				
			/* Função para obter algum dado pré definido do usuário no PIN-pad (ABECS) */
			case PW_iPPGetUserData:
				ret = LibFunctions.getUserDataOnPINPad(message, minSize, maxSize, 30, response);
				break;
				
				/*Função para receber o event do PIN-pad, seja de tecla, magnético, chip ou sem contato */
			case PW_iPPWaitEvent:
				ret = LibFunctions.waitEventOnPINPad(eventCapturaPP);
				if (ret != PWRet.OK)
					break;

				ret = EventLoop.execute(null, displayMessage);
				if (ret != PWRet.OK)
					break;

				return new String("Evento recebido: " + eventCapturaPP.getValue());
			default:
				ret = PWRet.CANCEL;
				break;

			}

			if (ret != PWRet.OK) {
				userInterface.showException("Erro ao solicitar dado via PIN-pad", false);
			}
		} catch (Exception e) {
			userInterface.showException(e.getMessage(), false);
		}

		return new String(response);
	}

	public void abort() {
		this.transaction.abort(false);
		userInterface.logInfo("\n\n EXECUÇÃO CANCELADA PELO USUÁRIO! \n\n");
	}
}
