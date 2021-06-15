package util;

import java.security.SecureRandom;

import javax.crypto.spec.IvParameterSpec;

public class IVHelper {

	public static IvParameterSpec createIV() {
		
		byte[] iv1 = new byte[16];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(iv1);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv1);
		return ivParameterSpec;
	}
}
