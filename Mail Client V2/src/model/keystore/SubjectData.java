package model.keystore;

import java.security.PublicKey;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;

/**
 * Klasa koja sadrzi podatke o licu kojem je sertifikat izdat, serijski broj sertifikata, period vazenja sertifikata i 
 * pjavni kljuc koji je dodeljen.
 *
 */
public class SubjectData {

	private PublicKey publicKey;
	private X500Name x500name;
	private String serialNumber;
	private Date startDate;
	private Date endDate;

	public SubjectData() {
	}

	public SubjectData(PublicKey publicKey, X500Name x500name, String serialNumber, Date startDate, Date endDate) {
		this.publicKey = publicKey;
		this.x500name = x500name;
		this.serialNumber = serialNumber;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public SubjectData(PublicKey publicKey, String CN, String O, String OU, String C, String E, String UID,
			String serialNumber, Date startDate, Date endDate) {
		
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		
		builder.addRDN(BCStyle.CN, CN);
		builder.addRDN(BCStyle.O, O);
		builder.addRDN(BCStyle.OU, OU);
		builder.addRDN(BCStyle.C, C);
		builder.addRDN(BCStyle.E, E);
		builder.addRDN(BCStyle.UID, UID);

		this.x500name = builder.build();

		this.publicKey = publicKey;
		this.serialNumber = serialNumber;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public X500Name getX500name() {
		return x500name;
	}

	public void setX500name(X500Name x500name) {
		this.x500name = x500name;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "SubjectData [x500name=" + x500name + ", serialNumber=" + serialNumber
				+ ", startDate=" + startDate + ", endDate=" + endDate + ", publicKey=" + publicKey + " + ]";
	}
}
