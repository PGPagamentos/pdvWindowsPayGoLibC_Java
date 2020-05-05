package br.com.paygo.helper;

import br.com.paygo.enums.PWValidDataEntry;
import br.com.paygo.interop.Menu;
import br.com.paygo.ui.UserInterface;

import java.util.ArrayList;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * Classe responsável por solicitar e validar os inputs do usuário com base nas regras de validação informadas pela biblioteca PGWebLib.
 */
public class UserInputHandler {

    public static String getTypedData(UserInterface userInterface, String promptMessage, int maxSize, int minSize, PWValidDataEntry validDataEntry) {
        return getTypedData(userInterface, promptMessage, (byte) maxSize, (byte) minSize, validDataEntry, "", "", (byte) 0, (long) 0, "", (long) 0, "", (byte) 0, (byte) 0, (byte) 0, "");
    }

    public static String getTypedData(UserInterface userInterface, String promptMessage, byte maxSize, byte minSize, PWValidDataEntry validDataEntry, String initialValue, String mascaraCaptura,
    byte validacaoDado, long valorMinimo, String msgDadoMenor, long valorMaximo, String msgValorMaximo, byte ocultarDadosDigitados, byte iniciaPelaEsquerda, byte alinhaDireita, String msgConfirmacao) {
        boolean isValid;
        String typedData;

        do {
            typedData = requestUserInput(userInterface, promptMessage, mascaraCaptura, initialValue, ocultarDadosDigitados, iniciaPelaEsquerda, alinhaDireita);

            if (typedData.equals("-1")) {
                return typedData;
            }

            isValid = validateUserInput(userInterface, typedData, maxSize, minSize, validDataEntry, initialValue, validacaoDado, valorMinimo, valorMaximo, msgDadoMenor, msgValorMaximo, msgConfirmacao, mascaraCaptura);
        } while (!isValid);

        return typedData;
    }

    public static String requestSelectionFromMenu(UserInterface userInterface, Menu menu, String promptMessage) {
        int userInput;
        Map<String, String> menuOptions = menu.build();
        String message = promptMessage ;
        if (message.compareTo("") == 0) {
            message = "Selecione uma opção";
        }


        do {
            userInput = userInterface.requestSelection(message, new ArrayList<>(menuOptions.values()), menu.getDafaultSelection());

            if (userInput == -1) {
                return "-1";
            }

        } while (userInput > menu.getSize() || userInput < 0);

        return (String) menuOptions.keySet().toArray()[userInput];
    }

    private static String requestUserInput(UserInterface userInterface, String promptMessage, String mask, String initialValue, byte ocultarDadosDigitados, byte iniciaPelaEsquerda, byte alinhaDireita) {
        return userInterface.requestParam("Informação Requerida", promptMessage, mask,  initialValue, ocultarDadosDigitados, iniciaPelaEsquerda, alinhaDireita);
    }

    private static boolean validateUserInput(UserInterface userInterface, String typedData, byte maxSize, byte minSize,
                                             PWValidDataEntry validDataEntry, String initialValue, byte validacaoDado, long valorMinimo, long valorMaximo, 
                                             String msgDadoMenor,  String msgValorMaximo, String msgConfirmacao, String mascaraCaptura) {
        if (typedData.length() > maxSize) {
            userInterface.alert("Tamanho maior que o maximo permitido (" + maxSize + ").\nTente novamente...");
            System.out.println();
        } else if (typedData.length() < minSize) {
            userInterface.alert("Tamanho menor que o mínimo permitido (" + minSize + ").\nTente novamente...");
            System.out.println();
        } else {
            /* Primeiro faz as validações de tamanho */ 
            if (valorMinimo > 0) {
                if (Long.parseLong(typedData) < valorMinimo) {
                    userInterface.alert(msgDadoMenor);
                    userInterface.alert("Valor menor do que o mínimo permitido (" + valorMinimo + ").\nTente novamente...");
                    System.out.println();
                    return false;
                }
            }

            if (valorMaximo > 0) {
                if (Long.parseLong(typedData) > valorMaximo) {
                    userInterface.alert(msgValorMaximo);
                    userInterface.alert("Valor maior do que o mínimo permitido (" + valorMaximo + ").\nTente novamente...");
                    System.out.println();
                    return false;
                }
            }

            /* Validacoes requeridas pela LIB */ 
            switch (validacaoDado) {
                /* Dado não pode ser vazio*/ 
                case 1:
                    if (typedData.isEmpty()) {
                        userInterface.alert("Dado capturado não pode ser vazio.\nTente novamente...");
                        System.out.println();
                        return false;
                    }
                break;
                /* Validacao digito verificador mod10 */
                case 2:
                    if (!DVValido(typedData)) {
                        userInterface.alert("Dígito verificador inválido.\nTente novamente...");
                        System.out.println();
                        return false;
                    }
                break;

                case 3:
                if (!CPFCNPJValido(typedData)) {
                    userInterface.alert("CPF/CNPJ inválido.\nTente novamente...");
                    System.out.println();
                    return false;
                }
                break;

                case 4:
                    if (!DataValida(typedData, "yyMM")) {
                        userInterface.alert("Data inválida.\nTente novamente...");
                        System.out.println();
                        return false;
                    }
                break;
                case 5:
                    if (!DataValida(typedData, "ddMMyy")){
                        userInterface.alert("Data inválida.\nTente novamente...");
                        System.out.println();
                        return false;
                    }
                break;
                case 6:
                    /*Faz nova captura, solicitando confirmação */ 
                    String typedConfirmation = requestUserInput(userInterface, msgConfirmacao, mascaraCaptura, typedData, (byte) 0, (byte) 0, (byte) 0);
                    if (!typedData.equals(typedConfirmation)){
                        userInterface.alert("Valores digitados não são iguais.\nTente novamente...");
                        System.out.println();
                        return false;
                    }
                break;

            }

            switch (validDataEntry) {
                // O que isso deveria fazer ?
                //case INITIAL_VALUE:
                //    System.out.println("entrada digitada está sendo sobrescrita por: " + initialValue);
                //    return true;
                case NUMERIC:
                    if(typedData.matches("[0-9]+")) {
                        return true;
                    }
                    userInterface.alert("Digite apenas números");
                    break;
                case ALPHABETIC:
                    if(typedData.matches("[a-zA-Z]+")) {
                        return true;
                    }

                    userInterface.alert("Digite apenas alfabéticos");
                    break;
                case ALPHANUMERIC:
                    if(typedData.matches("[0-9a-zA-Z]+")) {
                        return true;
                    }

                    userInterface.alert("Digite apenas alfanuméricos");
                    break;
                case ALL:
                    return true;
                default:
                    return true;
            }
        }

        return false;
    }

    private static int getMod11(String num){    
  
        //variáveis de instancia  
        int soma = 0;  
        int resto = 0;  
        int dv = 0;  
        String[] numeros = new String[num.length()+1];  
        int multiplicador = 2;  
          
        for (int i = num.length(); i > 0; i--) {               
            //Multiplica da direita pra esquerda, incrementando o multiplicador de 2 a 9  
            //Caso o multiplicador seja maior que 9 o mesmo recomeça em 2  
            if(multiplicador > 9){  
                // pega cada numero isoladamente    
                multiplicador = 2;  
                numeros[i] = String.valueOf(Integer.valueOf(num.substring(i-1,i))*multiplicador);  
                multiplicador++;  
            }else{  
                numeros[i] = String.valueOf(Integer.valueOf(num.substring(i-1,i))*multiplicador);  
                multiplicador++;  
            }  
        }    
  
        //Realiza a soma de todos os elementos do array e calcula o digito verificador  
        //na base 11 de acordo com a regra.       
        for(int i = numeros.length; i > 0 ; i--){  
            if(numeros[i-1] != null){  
                soma += Integer.valueOf(numeros[i-1]);  
            }  
        }  
        resto = soma%11;  
        dv = 11 - resto;  
        if(dv > 9 || dv == 0 ){
           dv = 0; 
        }
          
        //retorna o digito verificador  
        return dv;  
    }  

    private static int getMod10(String num) {
        //variáveis de instancia
		int soma = 0;
		int resto = 0;
		int dv = 0;
	    String[] numeros = new String[num.length()+1];
	    int multiplicador = 2;
	    String aux;
	    String aux2;
	    String aux3;
	    
	    for (int i = num.length(); i > 0; i--) {  	    	
	    	//Multiplica da direita pra esquerda, alternando os algarismos 2 e 1
	    	if(multiplicador%2 == 0){
		    	// pega cada numero isoladamente  
	    		numeros[i] = String.valueOf(Integer.valueOf(num.substring(i-1,i))*2);
	    		multiplicador = 1;
	    	}else{
	    		numeros[i] = String.valueOf(Integer.valueOf(num.substring(i-1,i))*1);
	    		multiplicador = 2;
	    	}
	    }  
	    
	    // Realiza a soma dos campos de acordo com a regra
	    for(int i = (numeros.length-1); i > 0; i--){
	    	aux = String.valueOf(Integer.valueOf(numeros[i]));
	    	
	    	if(aux.length()>1){
	    		aux2 = aux.substring(0,aux.length()-1);	    		
	    		aux3 = aux.substring(aux.length()-1,aux.length());
	    		numeros[i] = String.valueOf(Integer.valueOf(aux2) + Integer.valueOf(aux3));	    		
	    	}
	    	else{
	    		numeros[i] = aux;    		
	    	}
	    }
	    
	    //Realiza a soma de todos os elementos do array e calcula o digito verificador
	    //na base 10 de acordo com a regra.	    
	    for(int i = numeros.length; i > 0 ; i--){
	    	if(numeros[i-1] != null){
	    		soma += Integer.valueOf(numeros[i-1]);
	    	}
	    }
	    resto = soma%10;
    	dv = 10 - resto;
	    
    	//retorna o digito verificador
	    return dv;
	}
    
    private static boolean CPFCNPJValido(String typedData) {
        /* determina se deve validar CPF ou CNPJ */ 
        if (typedData.length() == 11) {
            /* Valida CPF*/ 
            int digito10 = getMod11(typedData.substring(0, 10));
            int digito11 = getMod11(typedData.substring(0, 11));
            int digito = digito10*10 + digito11;
            /* Compara com digito informado */
            if (Integer.toString(digito).compareTo(typedData.substring(9, 11)) == 0) {
                return false;
            }
            else {
                return true;
            }

        }
        else if (typedData.length() == 14) {

            int digito10 = getMod11(typedData.substring(0, 12));
            int digito11 = getMod11(typedData.substring(0, 13));
            int digito = digito10*10 + digito11;
            /* Compara com digito informado */
            if (Integer.toString(digito).compareTo(typedData.substring(9, 11)) == 0) {
                return false;
            }
            else {
                return true;
            }

        }
        return false;
    }

    private static boolean DataValida(String date, String format) {
        SimpleDateFormat formatator = new SimpleDateFormat(format);
        formatator.setLenient(false);

        try {
            formatator.parse(date);
            return true;
        }
        catch (ParseException e) {
            return false;
        }
    }


    private static boolean DVValido(String data) {
        int dv = getMod10(data.substring(0, data.length() - 1));
        if (Integer.toString(dv).compareTo(data.substring(data.length() -1)) == 0) {
            return true;
        }
        return false;
    }
}
