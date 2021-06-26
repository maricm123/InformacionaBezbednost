package keystore;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class KeyStoreWriter {
	
	/**
	 * Metoda sluzi za ucitavanje KeyStore-a sa zadate putanje
	 * 
	 * @param keyStoreFilePath - putanja do KeyStore fajla. Ako je null kreira se novi KeyStore.
	 * @param password - sifra za otvaranje KeyStore fajla
	 * 
	 * @return Instanca KeyStore objekta
	 */
	public KeyStore loadKeyStore(String keyStoreFilePath, char[] password) {
		KeyStore keyStore = null;
		try {
			// kreiramo instancu KeyStore objekta. 
			// parametar getInstance metode je tip KeyStore-a
			// npr. ako zelimo da kreiramo PKCS12 KeyStore -> KeyStore.getInstance("PKCS12")
			keyStore = KeyStore.getInstance("JKS");
			
			// VAZNO:
			// pre nego sto instanca keyStore moze da se koristi, mora da se ucita pozivom load() metode. KeyStore instance se uglavnom cuvaju
			// na disku. Zbog toga klasa KeyStore je definisano tako da se podaci sa diska prvo moraju procitati. Ukoliko ne zelimo da ucitamo
			// nikakve podatke u keyStore instancu, kao prvi parametar load() metode prosledjujemo null vrednost -> keyStore.load(null, password);
			// load() metoda se uvek mora pozvati! KeyStore instanca se mora inicijalizovati uvek, bilo sa nekim podacima ili sa null. Ukoliko se
			// ovaj korak preskoci i metoda load() ne pozove, imacemo neinicijalizovan KeyStore objekat, i svaki poziv bilo koje metode
			// nad ovim objektom ce izazvati Exception!
			
			// ako smo prosledili putanju do KeyStore-a
			if (keyStoreFilePath != null)  {
				keyStore.load(new FileInputStream(keyStoreFilePath), password);
			}
			// ako nema postojeceg KeyStore fajla u koji cemo dodavati, vec se kreira novi,
			// mora da se pozove load metoda, pri cemu je prvi parametar null
			else {
				keyStore.load(null, password);
			}
		} catch (NoSuchAlgorithmException | CertificateException | KeyStoreException | IOException e) {
			e.printStackTrace();
			System.err.println("\n[KeyStoreWriter - readKeyStore] Greska prilikom ucitavanja KeyStore-a. Proveriti da li je putanja ispravna i da li je prosledjen dobra sifra za otvaranje KeyStore-a!\n");			
		}
		
		return keyStore;
	}
	
	/**
	 * Metoda sluzi za snimanje KeyStore objekta na fajl sistem.
	 * 
	 * @param keyStore - referenca na KeyStore objekat koji treba da se sacuva
	 * @param keyStoreFilePath - putanja do KeyStore fajla
	 * @param password - sifra za otvaranje KeyStore fajla
	 */
	public void saveKeyStore(KeyStore keyStore, String keyStoreFilePath, char[] password) {
		try {
			// cuvanje KeyStore-a na disku
			keyStore.store(new FileOutputStream(keyStoreFilePath), password);
		} catch (NoSuchAlgorithmException | CertificateException | KeyStoreException | IOException e) {
			e.printStackTrace();
			System.err.println("\n[KeyStoreWriter - saveKeyStore] Greska prilikom snimanja KeyStore-a. Proveriti da li je putanja ispravna i da li je prosledjen dobra sifra za otvaranje KeyStore-a!\n");			
		}
	}
	
	/**
	 * Metoda sluzi za dodavanje novog para (sertifikat, javni kljuc) u KeyStore
	 * 
	 * @param keyStore - referenca na KeyStore objekat u koji se upisuju nove vrednosti
	 * @param alias - kljuc pod kojim ce se pristupati sertifikatu i privatnom kljucu koji se upisuju
	 * @param privateKey - referenca na privatni kljuc koji se upisuje u KeyStore
	 * @param password - sifra za pristup sertifikatu i javnom kljucu, koristi se kada treba procitati njihove vrednosti
	 * @param certificate - referenca na sertifikat koji se upisuje u KeyStore
	 */
	public void addToKeyStore(KeyStore keyStore, String alias, PrivateKey privateKey, char[] password, Certificate certificate) {
		try {
			// postavljanje novog unosa u KeyStore pod zeljenim aliasom
			keyStore.setKeyEntry(alias, privateKey, password, new Certificate[] {certificate});
		} catch (KeyStoreException e) {
			e.printStackTrace();
			System.err.println("\n[KeyStoreWriter - addToKeyStore] Greska prilikom snimanja KeyStore-a. Proveriti da li su svi prosledjeni parametri ispravni!\n");			
		}
	}
}
