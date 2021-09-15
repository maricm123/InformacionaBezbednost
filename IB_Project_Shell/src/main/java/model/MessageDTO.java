package model;



import java.util.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import org.w3c.dom.*;

public class MessageDTO {
	
	private String emailAddress;
	private String sender;
	private String subject;
	private String content;
	
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	@Override
	public String toString() {
		return "MessageDTO [emailAddress=" + emailAddress + ", sender=" + sender + ", subject=" + subject + ", content="
				+ content + "]";
	}
	public static MessageDTO loadFromDom(Node node) {
		Element element=(Element) node;
		MessageDTO m=new MessageDTO();
		String posiljaoc=element.getAttribute("posiljaoc");
		m.setSender(posiljaoc);
		String primalac=element.getAttribute("primalac");
		m.setEmailAddress(primalac);
		String subject=element.getElementsByTagName("subject").item(0).getTextContent();
		m.setSubject(subject);
		String body=element.getElementsByTagName("body").item(0).getTextContent();
		m.setContent(body);
		return m;
	}
}
