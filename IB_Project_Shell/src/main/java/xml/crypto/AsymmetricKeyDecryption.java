package xml.crypto;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.encryption.XMLCipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//Dekriptuje tajni kljuc privatnim kljucem
//Tajnim kljucem dekriptuje podatke
public class AsymmetricKeyDecryption {

	private static final String IN_FILE = "./data/";
	private static final String OUT_FILE = "./data/";
	private static final String KEY_STORE_FILE = "./data/primer.jks";

	static {
		// staticka inicijalizacija
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();
	}

	public Document testIt(String inFile,PrivateKey pk,String outFile) {
		
		try {
			// ucitava se dokument
			Document doc = loadDocument(inFile);

			// ucitava se privatni kljuc
			//PrivateKey pk = readPrivateKey();

			// kriptuje se dokument
			System.out.println("Decrypting....");
			doc = decrypt(doc, pk);

			// snima se dokument
			//saveDocument(doc, outFile);
			System.out.println("Encryption done");
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Kreira DOM od XML dokumenta
	 */
	private Document loadDocument(String file) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new File(file));

			return document;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Snima DOM u XML fajl
	 */
	private void saveDocument(Document doc, String fileName) {
		try {
			File outFile = new File(fileName);
			FileOutputStream f = new FileOutputStream(outFile);

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(f);

			transformer.transform(source, result);

			f.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ucitava privatni kljuc is KS fajla alias primer
	 */
	private PrivateKey readPrivateKey() {
		try {
			// kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");

			// ucitavamo podatke
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE_FILE));
			ks.load(in, "primer".toCharArray());

			if (ks.isKeyEntry("primer")) {
				PrivateKey pk = (PrivateKey) ks.getKey("primer", "primer".toCharArray());
				return pk;
			} else
				return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Kriptuje sadrzaj prvog elementa odsek
	 */
	private Document decrypt(Document doc, PrivateKey privateKey) {

		try {
			// cipher za dekritpovanje XML-a
			XMLCipher xmlCipher = XMLCipher.getInstance();

			// inicijalizacija za dekriptovanje
			xmlCipher.init(XMLCipher.DECRYPT_MODE, null);

			// postavlja se kljuc za dekriptovanje tajnog kljuca
			xmlCipher.setKEK(privateKey);

			// trazi se prvi EncryptedData element
			NodeList encDataList = doc.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedData");
			Element encData = (Element) encDataList.item(0);

			// dekriptuje se
			// pri cemu se prvo dekriptuje tajni kljuc, pa onda njime podaci
			xmlCipher.doFinal(doc, encData);

			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

//	public static void main(String[] args) {
//		AsymmetricKeyDecryption decrypt = new AsymmetricKeyDecryption();
//		decrypt.testIt();
//	}
}
