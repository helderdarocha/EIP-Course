package br.com.argonavis.eipcourse.endpoint.gateway;

public class ExampleMessagingClient {
	public static void main(String[] args) {
		try {
			SimpleMessagingGateway gateway = new JMSMessagingGateway();
			SimpleMessage message = new SimpleMessage("<message>Hello World!</message>");
			message.setHeader("Type", "xml");
			message.setHeader("Length", "12");
			
			// Configured in provider specific file (jndi.properties for JMS)
			SimpleChannel fromChannel = new SimpleChannel("in-channel");
			
			gateway.register(new MessagingEventHandler() {
				@Override
				public void process(SimpleMessage m) {
					System.out.println("Received: " + m);
				}
			}, fromChannel);
			
			System.out.println("Sending message.");
			gateway.send(fromChannel, message);
			
		} catch (MessagingException e) {
			e.printStackTrace();
		} 
	}
}
