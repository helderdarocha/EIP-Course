package br.com.argonavis.eipcourse.mgmt.controlbus;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * This example is adapted from the book Enterprise Integration Patterns,
 * chapter 6.
 */
public class ProductServiceActivator implements MessageListener, ManagedComponent {

	private Session session;
	private MessageProducer replyProducer;
	private MessageConsumer requestConsumer;
	private Destination requestQueue;

	private boolean managed = false;
	private ManagedEndpoint<ProductServiceActivator> managedEndpoint;

	public ProductServiceActivator(Connection con, Destination requestQueue)
			throws JMSException {
		this.requestQueue = requestQueue;
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		requestConsumer = session.createConsumer(requestQueue);
		requestConsumer.setMessageListener(this);
	}
	
	public void setManaged(Connection con, Destination controlTopic) throws JMSException {
		this.managed = true;
		managedEndpoint = new ManagedEndpoint<ProductServiceActivator>(this, requestQueue, null); 
		managedEndpoint.initControl(con, controlTopic);
	}
	
	public void unsetManaged() {
		this.managed = false;
		managedEndpoint = null;
	}
	
	public boolean isManaged() {
		return this.managed;
	}

	public void onMessage(Message message) {
		try {
			String command = message.getStringProperty("Command");
			System.out.println("Message received: " + command);

			if (command.equals("getProduct")) {
				Long pk = message.getLongProperty("ProductID");
				String payload = null;
				try {
					Product p = new ProductService().select(pk);

					StringWriter writer = new StringWriter();
					JAXBContext jctx = JAXBContext.newInstance(Product.class);
					Marshaller m = jctx.createMarshaller();
					m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
					m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
					m.marshal(p, writer);
					payload = writer.toString();
				} catch (ProductNotFoundException e) {
					message.setObjectProperty("Exception", e);
					payload = "<exception>" + e.getClass().getName()
							+ "</exception>";
				}

				Destination replyDestination = message.getJMSReplyTo();
				replyProducer = session.createProducer(replyDestination);
				TextMessage replyMessage = session.createTextMessage();
				replyMessage.setText(payload);
				replyMessage.setJMSCorrelationID(message.getJMSMessageID());
				
				if(managed) {
					managedEndpoint.saveHistory(this, message, replyMessage, replyDestination);
					System.out.println("History for replyMessage: " + replyMessage.getStringProperty("MessageHistory"));
				}
				
				replyProducer.send(replyMessage);

				System.out.println("getProduct() reply was sent: "
						+ replyMessage.getText());

			} else if (command.equals("addProduct")) {
				TextMessage textMessage = (TextMessage) message;
				
				if(managed) {
					System.out.println("History for addProduct: " + message.getStringProperty("MessageHistory"));
				}

				StringReader reader = new StringReader(textMessage.getText());
				JAXBContext jctx = JAXBContext.newInstance(Product.class);
				Unmarshaller m = jctx.createUnmarshaller();
				Product p = (Product) m.unmarshal(reader);

				new ProductService().persist(p);
				
				System.out.println("Product " + p + " was added!");
			} else {
				System.out.println("Send to invalid message channel!");
			}
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		Destination requestQueue = (Destination) ctx.lookup("request-queue");
		Destination controlTopic = (Destination) ctx.lookup("control-topic");

		ConnectionFactory factory = (ConnectionFactory) ctx
				.lookup("ConnectionFactory");
		Connection con = factory.createConnection();

		ProductServiceActivator activator = new ProductServiceActivator(con, requestQueue);
		activator.setManaged(con, controlTopic);

		con.start();
		System.out.println("Service Activator started.");

		Thread.sleep(30000);
		// close the connections
		System.out.println("Done.");
		con.close();
	}

}
