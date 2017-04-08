package location.message;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class MsgManager {
	private final Queue<Message> recvMsgQ = new LinkedList<Message>();
	private final Queue<Message> sendMsgQ = new LinkedList<Message>();
	private final Map<String, LinkedList<Message>> msgCache = new HashMap<String, LinkedList<Message>>();
	private final Map<String, Integer> msgCacheCount = new HashMap<String, Integer>();
	private final Map<String, String> finished = new HashMap<String, String>();

	private void cleanMsgCache() {
		synchronized (this.msgCache) {
			msgCache.clear();
		}
	}

	private void cleanMsgCacheCount() {
		synchronized (this.msgCacheCount) {
			msgCacheCount.clear();
		}
	}

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
		cleanRecvMsgQueue();
		cleanSendMsgQueue();
		cleanMsgCache();
		cleanMsgCacheCount();
		cleanFinished();
	}

	synchronized public void pushSend(Message msg) {
		String userUuid = msg.getUserUuid();
		if (!checkFinished(userUuid)) {
			synchronized (this.msgCacheCount) {
				Integer count = msgCacheCount.get(userUuid);
				if (count == null) count = 0;

				if (msg.getFileNum().equals(count)) {
					synchronized (this.sendMsgQ) {
						sendMsgQ.offer(msg);

						count++;
						synchronized (this.msgCache) {
							LinkedList<Message> list = msgCache.get(userUuid);
							if (list != null) {
								while (list.size() > 0 && list.get(0).getFileNum().equals(count)) {
									count++;
									sendMsgQ.offer(list.remove(0));
								}
							}
						}
					}

					msgCacheCount.put(userUuid, count);
				} else {
					synchronized (this.msgCache) {
						LinkedList<Message> list = msgCache.get(userUuid);
						if (list == null) {
							list = new LinkedList<Message>();
							msgCache.put(userUuid, list);
						}
						boolean insertFlag = false;
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getFileNum() > msg.getFileNum()) {
								insertFlag = true;
								list.add(i, msg);
								break;
							}
						}
						if (!insertFlag) list.add(msg);
					}
				}

			}
		}
	}

	public void pushRecv(Message msg) {
		if (msg.getFlag() == location.socket.Message.FLAG_RESULT) setFinished(msg);
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
		synchronized (this.msgCacheCount) {
			msgCacheCount.remove(msg.getUserUuid());
		}
		synchronized (this.msgCache) {
			msgCache.remove(msg.getUserUuid());
		}
	}

	public boolean checkFinished(String userUuid) {
		synchronized (this.finished) {
			return finished.containsKey(userUuid);
		}
	}
	//	The taks will be removed
	public String getFinished(String userUuid) {
		synchronized (this.finished) {
			return finished.remove(userUuid);
		}
	}

}
