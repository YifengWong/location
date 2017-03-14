package location.socket;


import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.HashMap;

public class MsgManager {
	private Queue<Message> recvMsgQ;
	private Queue<Message> sendMsgQ;
	private Map<String, String> finished;
	
	private void cleanRecvMsgQueue() {
		synchronized (this.recvMsgQ) {
			recvMsgQ.clear();
		}
	}

	private void cleanSendMsgQueue() {
		synchronized (this.sendMsgQ) {
			sendMsgQ.clear();
		}
	}

	private void cleanFinished() {
		synchronized (this.finished) {
			finished.clear();
		}
	}
	
	public MsgManager() {
		super();
		recvMsgQ = new LinkedList<Message>();
		sendMsgQ = new LinkedList<Message>();
		finished = new HashMap<String, String>();
		cleanRecvMsgQueue();
		cleanSendMsgQueue();
		cleanFinished();
	}

	public void pushSend(Message msg) {
		if (!checkFinished(msg.getUserUuid())) {
			synchronized (this.sendMsgQ) {
				sendMsgQ.offer(msg);
			}
		}
	}

	public void pushRecv(Message msg) {
		if (msg.getFlag() == Message.FLAG_RESULT) setFinished(msg);
		else {
			synchronized (this.recvMsgQ) {
				recvMsgQ.offer(msg);
			}
		}
	}

	public Message popSend() {
		synchronized (this.sendMsgQ) {
			return sendMsgQ.poll();
		}
	}

	public Message popRecv() {
		synchronized (this.sendMsgQ) {
			return recvMsgQ.poll();
		}
	}

	private void setFinished(Message msg) {
		synchronized (this.finished) {
			finished.put(msg.getUserUuid(), msg.getResultString());
		}
	}

	public boolean checkFinished(String userUuid) {
		synchronized (this.finished) {
			return finished.containsKey(userUuid);
		}
	}

	public String getFinished(String userUuid) {
		synchronized (this.finished) {
			return finished.remove(userUuid);
		}
	}

}
