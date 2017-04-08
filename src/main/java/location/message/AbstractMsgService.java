package location.message;

/**
 * Created by wyf on 17-4-7.
 */
public abstract class AbstractMsgService {


    protected MsgManager msgMgr;

    private Thread recvThread;
    private Thread sendThread;

    public AbstractMsgService(MsgManager msgMgr) {
        this.msgMgr = msgMgr;
    }

    /*
    * Send a msg to algorithm
    * */
    public void sendMsg(Message msg) {
        msgMgr.pushSend(msg);
    }

    /*
    * Get Next recv message
    * */
    public Message getNextMsg() {
        return msgMgr.popRecv();
    }

    /*
    * Try to get a result with user uuid, return null with none
    * */
    public String getResult(String userUuid) {
        if (!msgMgr.checkFinished(userUuid)) return null;
        return msgMgr.getFinished(userUuid);
    }

    /*
    * Generate a recv loop thread
    * */
    protected abstract Thread generateRecvThread();

    /*
    * Generate a send loop thread
    * */
    protected abstract Thread generateSendThread();

    /*
    * Start the service.
    * It should be run at last.
    * */
    public void startService() {
        recvThread = generateRecvThread();
        sendThread = generateSendThread();
        if (recvThread != null && sendThread != null) {
            recvThread.start();
            sendThread.start();
        }

    }


}
