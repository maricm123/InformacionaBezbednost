package model.mailclient;

import util.Base64;

public class MailBody {
	
	private String encMessage;
	private String IV1;
	private String IV2;
	private String encKey;
	private String signature;
	
	public MailBody(String encMessage, String iV1, String iV2, String encKey) {
		super();
		this.encMessage = encMessage;
		this.IV1 = iV1;
		this.IV2 = iV2;
		this.encKey = encKey;
	}
	
	public MailBody(String encMessage, String iV1, String iV2, String encKey, String signature) {
		super();
		this.encMessage = encMessage;
		this.IV1 = iV1;
		this.IV2 = iV2;
		this.encKey = encKey;
		this.signature = signature;
	}
	
	public MailBody(byte[] encMessage, byte[] iV1, byte[] iV2, byte[] encKey) {
		super();
		this.encMessage = Base64.encodeToString(encMessage);
		this.IV1 = Base64.encodeToString(iV1);
		this.IV2 = Base64.encodeToString(iV2);
		this.encKey = Base64.encodeToString(encKey);
	}
	
	public MailBody(byte[] encMessage, byte[] iV1, byte[] iV2, byte[] encKey, byte[] signature) {
		super();
		this.encMessage = Base64.encodeToString(encMessage);
		this.IV1 = Base64.encodeToString(iV1);
		this.IV2 = Base64.encodeToString(iV2);
		this.encKey = Base64.encodeToString(encKey);
		this.signature = Base64.encodeToString(signature);
	}
	
	public MailBody(String mailBodyCSV) {
		String[] parts = mailBodyCSV.split(",");
		
		switch(parts.length) {
			case 5: this.signature = parts[4];
			case 4: this.encKey = parts[3];
			case 3: this.IV2 = parts[2];
			case 2: this.IV1 = parts[1];
			case 1: this.encMessage = parts[0];
		}
		
	}
	
	public String toCSV() {
		String csv = "";
		
		if(this.signature != null) {
			csv = this.signature;
		}
		if(this.encKey != null) {
			csv = this.encKey +  "," + csv;
		}
		if(this.IV2 != null) {
			csv = this.IV2 +  "," + csv;
		}
		if(this.IV1 != null) {
			csv = this.IV1 +  "," + csv;
		}
		if(this.encMessage != null) {
			csv = this.encMessage +  "," + csv;
		}
		
		return csv;
	}

	public String getEncMessage() {
		return encMessage;
	}

	public String getIV1() {
		return IV1;
	}

	public String getIV2() {
		return IV2;
	}

	public String getEncKey() {
		return encKey;
	}
	
	public String getSignature() {
		return signature;
	}

	public byte[] getEncMessageBytes() {
		return Base64.decode(encMessage);
	}
	
	public byte[] getIV1Bytes() {
		return Base64.decode(IV1);
	}
	
	public byte[] getIV2Bytes() {
		return Base64.decode(IV2);
	}
	
	public byte[] getEncKeyBytes() {
		return Base64.decode(encKey);
	}
	
	public byte[] getSignatureBytes() {
		return Base64.decode(signature);
	}
}
