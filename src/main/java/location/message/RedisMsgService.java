package location.message;

import redis.clients.jedis.Jedis;

import java.io.IOException;

/**
 * Created by wyf on 17-4-8.
 */
public class RedisMsgService extends AbstractMsgService {

    private Jedis jedis;
    private String redisServerIP;
    private int redisServerPort;

    private final byte[] CLIENT = {'c', 'l', 'i', 'e', 'n', 't'};
    private final byte[] SERVER = {'s', 'e', 'r', 'v', 'e', 'r'};

    public RedisMsgService(String redisServerIP, int redisServerPort, MsgManager msgMgr) {
        super(msgMgr);
        this.redisServerIP = redisServerIP;
        this.redisServerPort = redisServerPort;
    }

    protected Thread generateRecvThread() {
        return new Thread(new Runnable() {
            public void run() {
                while (true) {
                    byte[] bytes = jedis.lpop(SERVER);

                    if (bytes == null || bytes.length == 0) {
                        try {
                            Thread.currentThread().sleep(5);
                            continue;
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    System.out.println(bytes);
                    int nowCount = 0;

                    Message msg = new Message(bytes, nowCount);
                    nowCount += Message.HEAD_LENGTH;

                    if (msg.getFlag() == Message.FLAG_IMG) {
                        try {
                            msg.writeParamsBytes(bytes, nowCount, Message.PARAM_ALL_LENGTH);
                            nowCount += Message.PARAM_ALL_LENGTH;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    msg.writeFileBytes(bytes, nowCount, msg.getFileLength());
                    // TODO
                    msgMgr.pushRecv(msg);
                }
            }
        });
    }

    protected Thread generateSendThread() {
        return new Thread(new Runnable() {
            public void run() {
                while (true) {
                    Message msg = msgMgr.popSend();
                    if (msg == null) {
                        try {
                            Thread.currentThread().sleep(5);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        continue;
                    }
                    // TODO send error
                    byte[] bytes = msg.getMsgBytes();
                    Long re = jedis.rpush(CLIENT, bytes);
                }
            }
        });
    }

    @Override
    public void startService() {
        this.jedis = new Jedis(redisServerIP, redisServerPort);
        super.startService();
    }
}
