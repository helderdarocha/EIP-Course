package br.com.argonavis.eipcourse.endpoint.durable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;

public class DurableSubscriberExample {

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx
				.lookup("ConnectionFactory");
		Topic topic = (Topic) ctx.lookup("durable-topic");
		Connection con = factory.createConnection();
		String clientID = "Sub1";
		con.setClientID(clientID);
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

		MessageProducer producer = session.createProducer(topic);
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(new Runnable() {
			int mcount = 0;

			public void run() {
				try {
					while (mcount < 15) {
						TextMessage m = session.createTextMessage("Message "
								+ ++mcount);
						producer.send(m);
						Thread.sleep(2000);
					}
					System.out.println("Done sending messages!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		MessageConsumer consumer1 = session.createDurableSubscriber(topic,
				clientID);
		consumer1.setMessageListener(new MessageListener() {
			int messageCount = 0;

			@Override
			public void onMessage(Message msg) {
				try {
					TextMessage message = (TextMessage) msg;
					messageCount++;
					System.out.println("Consumer1 (Durable) received: "
							+ message.getText());

					if (messageCount == 3) { // stop after 3 messages
						consumer1.close();
						messageCount++;
					}

				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});

		con.start();
		Thread.sleep(10000); // wait 10 seconds for messages
		con.stop();

		MessageConsumer consumer2 = session.createConsumer(topic);
		consumer2.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message msg) {
				try {
					TextMessage message = (TextMessage) msg;
					System.out.println("Consumer2 (Non-Durable) received: "
							+ message.getText());

				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});

		con.start();
		Thread.sleep(10000); // wait 10 seconds for messages
		con.stop();

		MessageConsumer consumer3 = session.createDurableSubscriber(topic,
				clientID);
		consumer3.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message msg) {
				try {
					TextMessage message = (TextMessage) msg;
					System.out.println("Consumer3 (Durable) received: "
							+ message.getText());

				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
		con.start();
		
		executorService.shutdown();
		
		Thread.sleep(10000); // wait 10 seconds for messages
		con.stop();
		con.close();
		
	}
}
