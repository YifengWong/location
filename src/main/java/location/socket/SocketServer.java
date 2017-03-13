package location.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
	public static final int BUFFER_LENGTH = 1024;
	
	private Socket client;
	private byte[] buffer = new byte[1024];
	private int serverPort;
	private ServerSocket server = null;
	
	private MsgManager recvMsgManager;
	private MsgManager sendMsgManager;
	
	private Thread recvThread;
	private Thread sendThread;
	
	public SocketServer(final int serverPort) {
		super();
		this.serverPort = serverPort;
		recvMsgManager = new MsgManager();
		sendMsgManager = new MsgManager();
	}
	
	public void startListening() {
		try {
			this.server = new ServerSocket(serverPort);
			Thread t = new Thread(new Runnable() {  
	            public void run() {
	            	while (true) {
	            		if (client == null || client.isClosed()) {
	            			if (recvThread != null) {
	            				recvThread.interrupt();
	            			} 
	            			if (sendThread != null) {
	            				sendThread.interrupt();
	            			}
		        			try {
		        				client = server.accept();
		        				beginRecvLoop();
		        				beginSendLoop();
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
	
	public void sendMsg(Message msg) {
		sendMsgManager.push(msg);
	}
	
	public Message getNextMsg() {
		return recvMsgManager.pop();
	}
	
	private void beginRecvLoop() {
		recvThread = new Thread(new Runnable() {  
            public void run() {  
            	while (true) {
            		if (client == null || client.isClosed()) {
            			break;
            		}
            		// recv a head
            		int headCount = 0;
            		while (headCount != Message.HEAD_LENGTH) {
            			try {
							headCount += client.getInputStream().read(
									buffer, headCount, Message.HEAD_LENGTH - headCount);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            		}
            		Message msg = new Message(buffer);
            		// recv the file 
            		int times = msg.getFileLength() / BUFFER_LENGTH;
            		for (int i = 0; i < times; i++) {
            			int count = 0;
            			while (count != BUFFER_LENGTH) {
            				try {
								count += client.getInputStream().read(buffer, count, BUFFER_LENGTH - count);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
            			}
            			msg.writeFileBytes(buffer, count);
            		}
            		int last = msg.getFileLength() % BUFFER_LENGTH;
            		int lastCount = 0;
            		while (lastCount != last) {
            			try {
							lastCount += client.getInputStream().read(buffer, lastCount, last - lastCount);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            		}
            		msg.writeFileBytes(buffer, lastCount);
            		// TODO
            		recvMsgManager.push(msg);
            	}
            }});  
		recvThread.start();
        
	}
	
	private void beginSendLoop() {
		sendThread = new Thread(new Runnable() {  
            public void run() {  
            	while (true) {
            		if (client == null || client.isClosed()) {
            			break;
            		}
            		Message msg = sendMsgManager.pop();
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
            			System.out.println(msg.getFileNum());
						client.getOutputStream().write(bytes, 0, bytes.length);
						client.getOutputStream().flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			
            	}
            }});  
		sendThread.start();
	}

}
