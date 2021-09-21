package keystore;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import model.keystore.IssuerData;
import model.keystore.SubjectData;

/**
 * Klasa koja sluzi za citanje iz KeyStore fajla
 *
 */
public class KeyStoreReader {
	
	/**
	 * Metoda sluzi za ucitavanje KeyStore-a sa zadate putanje
	 * 
	 * @param keyStoreFilePath - putanja do KeyStore fajla
	 * @param password - sifra za otvaranje KeyStore fajla
	 * 
	 * @return Instanca KeyStore objekta
	 */
	public KeyStore readKeyStore(String keyStoreFilePath, char[] password) {
		KeyStore keyStore = null;
		try {
			// kreiramo instancu KeyStore objekta. 
			// prvi parametar getInstance metode je tip KeyStore-a, drugi parametar je provider
			// npr. ako zelimo da kreiramo PKCS12 KeyStore -> KeyStore.getInstance("PKCS12")
			keyStore = KeyStore.getInstance("JKS", "SUN");
			
			// VAZNO:
			// pre nego sto instanca keyStore moze da se koristi, mora da se ucita pozivom load() metode. KeyStore instance se uglavnom cuvaju
			// na disku. Zbog toga klasa KeyStore je definisano tako da se podaci sa diska prvo moraju procitati. Ukoliko ne zelimo da ucitamo
			// nikakve podatke u keyStore instancu, kao prvi parametar load() metode prosledjujemo null vrednost -> keyStore.load(null, password);
			// load() metoda se uvek mora pozvati! KeyStore instanca se mora inicijalizovati uvek, bilo sa nekim podacima ili sa null. Ukoliko se
			// ovaj korak preskoci i metoda load() ne pozove, imacemo neinicijalizovan KeyStore objekat, i svaki poziv bilo koje metode
			// nad ovim objektom ce izazvati Exception!
			
			// ucitavamo podatke
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFilePath));
			keyStore.load(in, password);
		} catch (KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException | CertificateException | IOException e) {
			e.printStackTrace();
			System.err.println("\n[KeyStoreReader - readKeyStore] Greska prilikom ucitavanja KeyStore-a. Proveriti da li je putanja ispravna i da li je prosledjen dobra sifra za otvaranje KeyStore-a!\n");			
		}
		
		return keyStore;
	}
	
	/**
	 * Metoda sluzi za citanje sertifikata iz KeyStore-a
	 * 
	 * @param keyStore - referenca na KeyStore
	 * @param alias - kljuc pod kojim se sertifikat koji zelimo da preuzmemo cuva u KeyStore-u
	 * 
	 * @return Sertifikat
	 */
	public Certificate getCertificateFromKeyStore(KeyStore keyStore, String alias) {
		Certificate certificate = null;
		try {
			certificate = keyStore.getCertificate(alias);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		
		if (certificate == null) {
			System.err.println("\n[KeyStoreReader - getCertificateFromKeyStore] Sertifikat je null. Proveriti da li je alias ispravan!\n");
		}
		
		return certificate;
	}
	
	/**
	 * Metoda sluzi za citanje privatnog kljuca iz KeyStore-a
	 * 
	 * @param keyStore - referenca na KeyStore
	 * @param alias - kljuc pod kojim se privatni kljuc koji zelimo da preuzmemo cuva u KeyStore-u
	 * @param keyPass - sifra za pristup privatnom kljucu u okviru KeyStore-a
	 *  
	 * @return Privatni kljuc
	 */
	public PrivateKey getPrivateKeyFromKeyStore(KeyStore keyStore, String alias, char[] keyPass) {
		PrivateKey privateKey = null;
		try {
			privateKey = (PrivateKey) keyStore.getKey(alias, keyPass);
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		if (privateKey == null) {
			System.err.println("\n[KeyStoreReader - getPrivateKeyFromKeyStore] Privatni kljuc je null. Proveriti da li su ispravni alias i sifra za privatni kljuc!\n");
		}
		
		return privateKey;
	}
	
	/**
	 * Metoda sluzi za citanje javnog kljuca iz sertifikata
	 * 
	 * @param certificate - sertifikat iz kojeg zelimo da procitamo javni kljuc
	 * @return
	 */
	public PublicKey getPublicKeyFromCertificate(Certificate certificate) {
		return certificate.getPublicKey();
	}

	/**
	 * Metoda sluzi za citanje podataka o izdavacu sertifikata
	 * 
	 * @param certificate - sertifikat iz kojeg zelimo da procitamo podatke
	 * @param privateKey - privatni kljuc izdavaca sertifikata
	 * 
	 * @return IssuerData objekat koji sadrzi podatke o izdavacu sertifikata
	 */
	public IssuerData getIssuerFromCertificate(Certificate certificate, PrivateKey privateKey) {
		try {
			X509Certificate x509Certificate = (X509Certificate) certificate;
			JcaX509CertificateHolder certificateHolder = new JcaX509CertificateHolder(x509Certificate);
			
			X500Name issuerName = certificateHolder.getIssuer();
			return new IssuerData(privateKey, issuerName);
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Metoda sluzi za citanje podataka o licu kojem je sertifikat izdat
	 * 
	 * @param certificate - sertifikat iz kojeg zelimo da procitamo podatke
	 * 
	 * @return SubjectData objekat koji sadrzi podatke o licu kojem je sertifikat izdat
	 */
	public SubjectData getSubjectFromCertificate(Certificate certificate) {
		try {
			X509Certificate x509Certificate = (X509Certificate) certificate;
			JcaX509CertificateHolder certificateHolder = new JcaX509CertificateHolder(x509Certificate);
			
			PublicKey publicKey = getPublicKeyFromCertificate(certificate);
			X500Name subjectName = certificateHolder.getSubject();
			String serialNumber = certificateHolder.getSerialNumber().toString();
			Date startDate = certificateHolder.getNotBefore();
			Date endDate = certificateHolder.getNotAfter();
			
			return new SubjectData(publicKey, subjectName, serialNumber, startDate, endDate);
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
