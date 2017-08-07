package br.com.argonavis.eipcourse.messaging.jms.translator;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import br.com.argonavis.eipcourse.messaging.jms.pipesfilters.FilterReceiverExample;

public class TweetTransformer implements MessageListener {

	@Override
	public void onMessage(Message message) {
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
			Queue queue = (Queue) ctx.lookup(TransformerExample.OUTBOUND_CHANNEL);
			
			Connection con = factory.createConnection();
			con.start();
			Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			Message newMessage = new TranslateTweetToHtml().translate(message, session);
			
			MessageProducer sender = session.createProducer(queue);
			sender.send(newMessage);
			
			con.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
