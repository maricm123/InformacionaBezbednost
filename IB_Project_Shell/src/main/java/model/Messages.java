package model;



import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Messages {

	private List<MessageDTO> messages=new ArrayList<MessageDTO>();
	private boolean signature;

	public boolean isSignature() {
		return signature;
	}

	public void setSignature(boolean signature) {
		this.signature = signature;
	}

	public List<MessageDTO> getMessages() {
		return messages;
	}

	public void setMessageDTO(List<MessageDTO> messages) {
		this.messages = messages;
	}

	@Override
	public String toString() {
		return "Messages [messages=" + messages + "]";
	}
	
	public static Messages loadFromDom(Node node,String primalac) {
		Messages messages=new Messages();
		Element element=(Element) node;
		NodeList nodeList=element.getElementsByTagName("poruka");
		for(int i=0; i<nodeList.getLength(); i++) {
			Node n=nodeList.item(i);
			MessageDTO message=MessageDTO.loadFromDom(n);
			messages.getMessages().add(message);
		}
		Messages mess=new Messages();
		for (MessageDTO m : messages.getMessages()) {
			if(m.getEmailAddress().equals(primalac)) {
				mess.getMessages().add(m);;
			}
		}
		return mess;
	}
}
