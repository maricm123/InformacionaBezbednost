package certificate;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CRLEntryHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;

@SuppressWarnings("deprecation")
public class CRLManager {
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Metoda kreira praznu CRL listu poptpisanu privatnim kljucem izdavaca sertifikata.
	 * 
	 * @param certificate - sertifikat izdavaca sertifikata koji se nalaze u CRL listi (CA Certificate)
	 * @param privateKey - privatni kljuc izdavaca sertifikata koji se nalaze u CRL listi (CA Private Key)
	 * 
	 * @return Kreiran CRL Holder
	 */
	public X509CRLHolder createCRL(X509Certificate certificate, PrivateKey privateKey) {

		try {
			Date dateNow = new Date();

			// preuzimanje informacija o CA iz sertifikata
			X500Name x500Name =  new X500Name(certificate.getSubjectDN().getName());
			
			// postavljanje CA kao vlasnika CRL liste i postavljanje datuma kreiranja na trenutni
			X509v2CRLBuilder crlGenerator = new X509v2CRLBuilder(x500Name, dateNow);

			// trebalo bi da se azurira za 30 dana
			Date nextUpdate = new Date(dateNow.getTime() + 30 * 24 * 60 * 60 * 1000);
			crlGenerator.setNextUpdate(nextUpdate);

			// kreiramo novu CRL listu pa joj se postavlja za id broj 1
			// addExtension(SN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value)
			crlGenerator.addExtension(X509Extension.cRLNumber, false, new CRLNumber(BigInteger.valueOf(1)));

			// potpisivanje privatnim kljucem CA
			ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(privateKey);
			
			// kreiranje potpisane CRL liste
			return crlGenerator.build(contentSigner);
		} catch (SecurityException | CertIOException | OperatorCreationException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Metoda sluzi za azuriranje CRL liste. Azuriranje CRL <=> povlacenje sertifikata iz nje
	 * 
	 * @param crl - CRL lista koja se menja
	 * @param certificate - CA sertifikat (vlasnika CRL liste)
	 * @param privateKey - CA privatni kljuc (vlasnika CRL liste)
	 * @param serialNumber - serijski broj sertifikata koji se povlaci
	 * @param reason - razlog povlacenja
	 * 
	 * Moguci razlozi povlacenja sertifikata: https://docs.oracle.com/javase/7/docs/api/java/security/cert/CRLReason.html
	 * 
	 * @return Izmenjen CRL Holder
	 */
	public X509CRLHolder updateCRL(X509CRLHolder crl, X509Certificate certificate, PrivateKey privateKey, BigInteger serialNumber, int reason) {
		try {
			Date dateNow = new Date();
			
			// postavljanje postojeceg kreatore kao vlasnika CRL liste i postavljanje datuma kreiranja na trenutni
			X509v2CRLBuilder crlGenerator = new X509v2CRLBuilder(crl.getIssuer(), dateNow);
			
			// trebalo bi da se azurira za 30 dana
			Date nextUpdate = new Date(dateNow.getTime() + 30 * 24 * 60 * 60 * 1000);
			crlGenerator.setNextUpdate(nextUpdate);
			
			// dodavanje postojece CRL liste u novu listu
			crlGenerator.addCRL(crl);

			// odavanje serijskog broja sertifikata koji se povlaci uz navodjenje razloga povlacenja kao i trenutka povlacenja
			crlGenerator.addCRLEntry(serialNumber, dateNow, reason);

			// azuriranje broja CRL liste - inkrementiranje broja prethodne CRL liste za 1
			BigInteger oldCRLNumber = new BigInteger(crl.getExtension(X509Extension.cRLNumber).getParsedValue().toString());
			BigInteger newCRLNumber = oldCRLNumber.add(BigInteger.ONE);
			
			// postavljanje novog broja CRL liste
			crlGenerator.addExtension(X509Extension.cRLNumber, false, new CRLNumber(newCRLNumber));
			
			// postavljanje sertifikata CA u CRL listu
			crlGenerator.addExtension(X509Extension.authorityKeyIdentifier, false,
					new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(certificate));
			
			// potpisivanje privatnim kljucem CA
			ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(privateKey);
						
			// kreiranje potpisane CRL liste
			return crlGenerator.build(contentSigner);
		} catch (CertificateEncodingException | CertIOException | NoSuchAlgorithmException | OperatorCreationException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Metoda provera da li je CRL lista ispravna, tj. da li je potpisana prosledjenim CA sertifikatom.
	 * 
	 * @param crl - CRL lista cija validnost se proverava
	 * @param caCertificate - sertifikat CA
	 * 
	 * @return Boolean vrednost koja oznacava da li je CRL potpisana prosledjenim CA sertifikatom ili nije
	 */
	public boolean isCRLValid(X509CRLHolder crl, X509Certificate caCertificate) {
		try {
			return crl.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider("BC").build(caCertificate));
		} catch (OperatorCreationException | CertException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * Metoda kreira X509CRL listu iz X509CRLHolder-a
	 * 
	 * @param crlHolder - holder iz kojeg se preuzima CRL lista
	 * 
	 * @return CRL lista
	 */
	public X509CRL CRLFromCrlHolder(X509CRLHolder crlHolder) {
		// instanciranje konvertera
		JcaX509CRLConverter crlConverter = new JcaX509CRLConverter().setProvider("BC");
		
		try {
			return crlConverter.getCRL(crlHolder);
		} catch (CRLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Metoda vraca sertifikat koji je povucen.
	 * 
	 * @param crl - CRL lista iz koje se cita sertifikat
	 * @param serailNumber - serijski broj sertifikata
	 * 
	 * @return Sertifikat koji je povucen. Ako sertifikat sa prosledjenim serijski brojem nije povucen, metoda vraca null
	 */
	public X509CRLEntryHolder getRevokedCertificate(X509CRLHolder crl, BigInteger serailNumber) {
		return crl.getRevokedCertificate(serailNumber);
	}
	
	/**
	 * Metoda proverava da li je sertifikat povucen.
	 * 
	 * @param crl - CRL lista iz koje se cita sertifikat 
	 * @param serialNumber - serijski broj sertifikata
	 * 
	 * @return Boolean vrednost koja oznacava da li je sertifikat sa prosledjenim serijskim brojem povucen ili nije
	 */
	public boolean isCertificateRevoked(X509CRLHolder crl, BigInteger serialNumber) {
		return getRevokedCertificate(crl, serialNumber) != null;
	}
}