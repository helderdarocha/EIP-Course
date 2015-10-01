package br.com.argonavis.eipcourse.router.resequencer;

import java.util.Comparator;

import javax.jms.JMSException;
import javax.jms.Message;

public class MessageComparator implements Comparator<Message> {
	@Override
	public int compare(Message o1, Message o2) {
		try {
			int position1 = o1.getIntProperty(MessageSequence.POSITION_HEADER);
			int position2 = o2.getIntProperty(MessageSequence.POSITION_HEADER);
			return position1 - position2;
		} catch (JMSException e) {
			return 0;
		}
	}
}
