package certificate;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Klasa koja sluzi za citanje sertifikata iz fajla
 *
 */
public class CertificateReader {

	/**
	 * Metoda sluzi za citanje sertifikata iz Base64 enkodovanog formata.
	 * 
	 * @param filePath - putanja do fajla
	 * 
	 * @return Lista sertifikata koji su procitani
	 * 
	 * cita se sertifikat po sertifikat i vrsi se pozicioniranje na pocetak sledeceg.
	 * svaki certifikat se nalazi izmedju
	 * -----BEGIN CERTIFICATE-----,
	 * i
	 * -----END CERTIFICATE-----.
	 */
	public List<Certificate> getCertificatesFromBase64EncFile(String filePath) {
		List<Certificate> certificates = new ArrayList<>();
		
		try {
			FileInputStream fis = new FileInputStream(filePath);
			BufferedInputStream bis = new BufferedInputStream(fis);

			// instanciranje factory objekta i postavljamo tip sertifikata da je X509.
			CertificateFactory cf = CertificateFactory.getInstance("X.509");

			// iteracije dokle god ima bajtova u stream-u koji nisu procitani
			while (bis.available() > 0) {
				// kreiranje sertifikata od bajtova
				Certificate certificate = cf.generateCertificate(bis);
				
				// dodavanje sertifikata u listu
				certificates.add(certificate);
			}
		} catch (CertificateException | IOException e) {
			e.printStackTrace();
		}
		
		return certificates;
	}

	
	/**
	 * Metoda sluzi za citanje sertifikata iz binarnog formata.
	 * 
	 * @param filePath - putanja do fajla
	 * 
	 * @return Lista sertifikata koji su procitani
	 */
	public List<Certificate> getCertificatesFromBinEncFile(String filePath) {
		List<Certificate> certificates = new ArrayList<>();
		
		try {
			FileInputStream fis = new FileInputStream(filePath);
			
			// instanciranje factory objekta i postavljamo tip sertifikata da je X509.
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			
			// vade se svi sertifikati iz bajtovs i dodaju se u kolekciju
			Collection<?> c = cf.generateCertificates(fis);
			
			Iterator<?> i = c.iterator();
			while (i.hasNext()) {
				// kastovanje u tip koji nam treba
				Certificate certificate = (Certificate) i.next();
				
				// dodavanje sertifikata u listu
				certificates.add(certificate);
			}
		} catch (FileNotFoundException | CertificateException e) {
			e.printStackTrace();
		}
		
		return certificates;
	}
}
