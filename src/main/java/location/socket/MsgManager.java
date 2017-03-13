package location.socket;

import java.util.LinkedList;
import java.util.Queue;

public class MsgManager {
	private Queue<Message> msgQ;
	
	private void cleanQueue() {
		synchronized (this.msgQ) {
			while (!msgQ.isEmpty()) {
				msgQ.poll();
			}
		}
	}
	
	public MsgManager() {
		super();
		msgQ = new LinkedList<Message>();
		cleanQueue();
	}
	
	public void push(Message msg) {
		synchronized (this.msgQ) {
			msgQ.offer(msg);
		}
	}
	
	public Message pop() {
		synchronized (this.msgQ) {
			return msgQ.poll();
		}
	}

}
