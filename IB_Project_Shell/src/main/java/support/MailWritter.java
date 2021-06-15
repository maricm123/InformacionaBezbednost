package support;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

// ...

public class MailWritter {

  // ...

  /**
   * Send an email from the user's mailbox to its recipient.
   *
   * @param service Authorized Gmail API instance.
   * @param userId User's email address. The special value "me"
   * can be used to indicate the authenticated user.
   * @param email Email to be sent.
   * @throws MessagingException
   * @throws IOException
   */
  public static void sendMessage(Gmail service, String userId, MimeMessage email)
      throws MessagingException, IOException {
    
	Message message = createMessageWithEmail(email);
    message = service.users().messages().send(userId, message).execute();

    System.out.println("Message id: " + message.getId());
    System.out.println(message.toPrettyString());
  }

  /**
   * Create a Message from an email
   *
   * @param email Email to be set to raw of message
   * @return Message containing base64url encoded email.
   * @throws IOException
   * @throws MessagingException
   */
  private static Message createMessageWithEmail(MimeMessage email)
      throws MessagingException, IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    email.writeTo(baos);
    String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
    Message message = new Message();
    message.setRaw(encodedEmail);
    return message;
  }

  // ...

}