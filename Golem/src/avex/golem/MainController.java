package avex.golem;

import java.sql.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class MainController {

	private boolean 
	isInitialLoad = false
	,isDailyMaint = false,isCustomerMaint = false,isOrderMaint = false,hasOrderDate = false,isAthleteMaint = false;
	private Date orderDate;
	
	
	public void GetArguments(String[] arguments)
	{
		for (int i = 0; i < arguments.length; i++) {
		    if (arguments[i].toLowerCase() == "/customermaint")
		    {
		    	setCustomerMaint(true);
		    }
		    else if (arguments[i].toLowerCase() == "/ordermaint")
		    {
		    	setOrderMaint(true);
		    }
		    else if (arguments[i].toLowerCase() == "/athletemaint")
		    {
		    	setAthleteMaint(true);
		    }
		}
	}

	public boolean isInitialLoad() {
		return isInitialLoad;
	}

	public void setInitialLoad(boolean isInitialLoad) {
		this.isInitialLoad = isInitialLoad;
	}

	public boolean isDailyMaint() {
		return isDailyMaint;
	}

	public void setDailyMaint(boolean isDailyMaint) {
		this.isDailyMaint = isDailyMaint;
	}

	public boolean isCustomerMaint() {
		return isCustomerMaint;
	}

	public void setCustomerMaint(boolean isCustomerMaint) {
		this.isCustomerMaint = isCustomerMaint;
	}

	public boolean isOrderMaint() {
		return isOrderMaint;
	}

	public void setOrderMaint(boolean isOrderMaint) {
		this.isOrderMaint = isOrderMaint;
	}

	public boolean isHasOrderDate() {
		return hasOrderDate;
	}

	public void setHasOrderDate(boolean hasOrderDate) {
		this.hasOrderDate = hasOrderDate;
	}

	public boolean isAthleteMaint() {
		return isAthleteMaint;
	}

	public void setAthleteMaint(boolean isAthleteMaint) {
		this.isAthleteMaint = isAthleteMaint;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	
	public void SendEmail(String recipient,String messageInfo, String subject){
		
		  try{
			     // Get system properties
		      Properties properties = System.getProperties();

		      // Setup mail server
		      properties.setProperty("mail.smtp.host", Program.EMAIL_HOST);

		      // Get the default Session object.
		      Session session = Session.getDefaultInstance(properties);
			  
		         // Create a default MimeMessage object.
		         MimeMessage message = new MimeMessage(session);

		         // Set From: header field of the header.
		         message.setFrom(new InternetAddress(Program.EMAIL_COMPANY));

		         // Set To: header field of the header.
		         message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

		         // Set Subject: header field
		         message.setSubject(subject);

		         // Now set the actual message
		         message.setText(messageInfo);

		         // Send message
		         Transport.send(message);
		         System.out.println("Sent message successfully....");
		      }catch (MessagingException mex) {
		         mex.printStackTrace();
		      }
		
	}
	
}
