package model.keystore;

import java.security.PrivateKey;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;

public class IssuerData {
	
	private X500Name x500name;
	private PrivateKey privateKey;
	
	public IssuerData() {
	}
	
	public IssuerData (String CN, String O, String OU, String C, String E, String UID, PrivateKey privateKey){
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		builder.addRDN(BCStyle.CN, CN);
		builder.addRDN(BCStyle.O, O);
		builder.addRDN(BCStyle.OU, OU);
		builder.addRDN(BCStyle.C, C);
		builder.addRDN(BCStyle.E, E);
		builder.addRDN(BCStyle.UID, UID);
		
		this.x500name = builder.build();
		this.privateKey = privateKey;
	}
	
	public IssuerData(PrivateKey privateKey, X500Name x500name) {
		this.privateKey = privateKey;
		this.x500name = x500name;
	}

	public X500Name getX500name() {
		return x500name;
	}

	public void setX500name(X500Name x500name) {
		this.x500name = x500name;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}
	
	
	

}
