package location.message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by wyf on 17-4-7.
 */
public class SocketMsgService extends AbstractMsgService {
    public static final int BUFFER_LENGTH = 1024;

    private Socket client;
    private int serverPort;
    private ServerSocket server = null;

    public SocketMsgService(final int serverPort, final MsgManager msgMgr) {
        super(msgMgr);
        this.serverPort = serverPort;
    }

    @Override
    protected Thread generateRecvThread() {
        return new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (client == null || client.isClosed()) {
                        break;
                    }
                    // recv a head
                    byte[] headBytes = new byte[Message.HEAD_LENGTH];
                    int headCount = 0;
                    while (headCount != Message.HEAD_LENGTH) {
                        try {
                            headCount += client.getInputStream().read(
                                    headBytes, headCount, Message.HEAD_LENGTH - headCount);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    // create a message
                    Message msg = null;
                    msg = new Message(headBytes, 0);

                    // recv the params
                    if (msg.getFlag() == Message.FLAG_IMG) {
                        byte[] paramsBytes = new byte[Message.PARAM_ALL_LENGTH];
                        int paramCount = 0;
                        try {
                            while (paramCount != Message.PARAM_ALL_LENGTH) {
                                paramCount += client.getInputStream().read(
                                        paramsBytes, paramCount, Message.PARAM_ALL_LENGTH - paramCount);
                            }
                            msg.writeParamsBytes(paramsBytes, 0, paramCount);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // recv the file
                    byte[] fileBytes = new byte[msg.getFileLength()];
                    int fileCount = 0;
                    try {
                        while (fileCount != msg.getFileLength()) {
                            fileCount += client.getInputStream().read(
                                    fileBytes, fileCount, msg.getFileLength() - fileCount);
                        }
                        msg.writeFileBytes(fileBytes, 0, fileCount);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // TODO
                    msgMgr.pushRecv(msg);
                }
            }});
    }

    @Override
    protected Thread generateSendThread() {
        return new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (client == null || client.isClosed()) {
                        break;
                    }

                    Message msg = msgMgr.popSend();
                    if (msg == null) {
                        try {
                            Thread.currentThread().sleep(500);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        continue;
                    }
                    // TODO send error
                    byte[] bytes = msg.getMsgBytes();
                    try {
                        client.getOutputStream().write(bytes, 0, bytes.length);
                        client.getOutputStream().flush();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }});
    }

    @Override
    public void startService() {
        try {
            this.server = new ServerSocket(serverPort);

            Thread t = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (client == null || client.isClosed()) {
                            try {
                                client = server.accept();
                                SocketMsgService.super.startService();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }});
            t.start();
        } catch (IOException e) {
            System.out.println("启动失败");
            e.printStackTrace();
        }

    }

}
