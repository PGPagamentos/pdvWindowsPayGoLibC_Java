package br.com.paygo.enums;

public enum PWPINPadFunction {
	PW_iPPDisplay ("PW_iPPDisplay"),
	PW_iPPWaitEvent ("PW_iPPWaitEvent"),
	PW_iGetOperations ("PW_iGetOperations"),
	PW_iPPGetUserData ("PW_iPPGetUserData"),
	PW_iPPGetPINBlock ("PW_iPPGetPINBlock");
	
	private final String functionName;

	PWPINPadFunction(String value) {
    	functionName = value;
    }

    public String getFunctionName() {
        return functionName;
    }
}
