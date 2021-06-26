package signature;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

/**
 * Klasa za generisanje i proveravu digitalnog potpisa
 * 
 */
public class SignatureManager {
	
	/**
	 * Metoda koja generise par kljuceva - privatni i javni kljuc.
	 * 
	 * @return Par kljuceva privatni i javni
	 * 
	 * NAPOMENA: kljucevi se uvek moraju koristiti u paru! Ovo znaci da ako potpisemo privatnim kljucem, jedino javni kljuc 
	 * koji odgovara tom privatnom kljucu (kreiran u istom trenutku i pripadaju istom KeyPair objektu) moze da se koristi za verifikaciju
	 */
	public KeyPair generateKeys() {
		try {
			// postavljanje algoritma
			// parametar getInstance metode je algoritam koji se koristi za potpisivanje. Na primer. vrednost moze biti i "DSA"
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			
			// generator pseudo slucajnih brojeva
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			
			// inicijalizacija generatora, postavljamo 1024 bitni kljuc i generator psude slucajnih brojeva
			keyGen.initialize(1024, random);

			// generisae par kljuceva
			return keyGen.generateKeyPair();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
			System.err.println("\n[SignatureExample - generateKeys] Problem prilikom generisanja kljuceva. Proverite da li se koristi algoritam koji postoji!\n");
		}
		
		return null;
	}
	
	/**
	 * Metoda za potpisivanje sadrzaja.
	 * 
	 * @param data - podaci koji treba digitalno da se potpisu
	 * @param privateKey - privatni kljuc sa kojim se podaci digitalno potpisuju
	 * 
	 * @return Digitalni potpis
	 */
	public byte[] sign(byte[] data, PrivateKey privateKey) {
		try {
			// kreiramo objekat za potpisivanje
			// parametar getInstance metode je algoritam koji se koristi za potpisivanje. Na primer. vrednost moze biti i "SHA1withDSA"
			// voditi racuna da se koristi algoritam za potpisivanje i za proveru potpisa!
			Signature signature = Signature.getInstance("SHA1withRSA");
			
			// inicijalizacija privatnim kljucem - POTPISUJE SE PRIVATNIM KLJCEM!!!
			signature.initSign(privateKey);
			
			// postavljamo podatke koje potpisujemo
			signature.update(data);

			// vrsimo potpisivanje
			return signature.sign();
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Metoda za verifikaciju digitalnog potpisa.
	 * 
	 * @param data - originalni podaci
	 * @param digitalSignature - digitalni potpis podataka
	 * @param publicKey - javni kljuc sa kojim se vrsi provera digitalnog potpisa
	 * 
	 * @return Boolean vrednost koja oznacava da li je digitalni potpis validan, odnosno da podaci nisu u medjuvremenu menjani
	 * 
	 * NAPOMENA: metoda ce vratiti true samo ukoliko originalni podaci nisu menjanu i ukoliko se za verifikaciju koristi javni kljuc
	 * koji je par sa privatnim kljucem sa kojim su podaci potpisani ž
	 */
	public boolean verify(byte[] data, byte[] digitalSignature, PublicKey publicKey) {
		try {
			// kreiramo objekat za potpisivanje
			// parametar getInstance metode je algoritam koji se koristi za potpisivanje. Na primer. vrednost moze biti i "SHA1withDSA"
			// voditi racuna da se koristi algoritam za potpisivanje i za proveru potpisa!
			Signature signature = Signature.getInstance("SHA1withRSA");
						
			// inicijalizacija privatnim kljucem - POTPIS SE VERIFIKUJE JAVNIM KLJUCEM!!!
			signature.initVerify(publicKey);
			
			// postavljamo podatke koje potpisujemo
			signature.update(data);

			// vrsimo proveru potpisa
			return signature.verify(digitalSignature);
		} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
