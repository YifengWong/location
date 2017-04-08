package location.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
	public static final int BUFFER_LENGTH = 1024;
	
	private Socket client;
	private int serverPort;
	private ServerSocket server = null;

	private MsgManager msgMgr;
	
	private Thread recvThread;
	private Thread sendThread;
	
	public SocketServer(final int serverPort) {
		super();
		this.serverPort = serverPort;
		msgMgr = new MsgManager();
	}
	
	public void startListening() {
		try {
			this.server = new ServerSocket(serverPort);
			Thread t = new Thread(new Runnable() {  
	            public void run() {
	            	while (true) {
	            		if (client == null || client.isClosed()) {
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

	/*
	* Send a message to algorithm system
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

	private void beginRecvLoop() {
		recvThread = new Thread(new Runnable() {
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
					try {
						msg = new Message(headBytes);
					} catch (IOException e) {
						e.printStackTrace();
					}

					// recv the params
					if (msg.getFlag() == Message.FLAG_IMG) {
						byte[] paramsBytes = new byte[Message.PARAM_ALL_LENGTH];
						int paramCount = 0;
						try {
							while (paramCount != Message.PARAM_ALL_LENGTH) {
								paramCount += client.getInputStream().read(
										paramsBytes, paramCount, Message.PARAM_ALL_LENGTH - paramCount);
							}
							msg.writeParamsBytes(paramsBytes, paramCount);
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
						msg.writeFileBytes(fileBytes, fileCount);
					} catch (IOException e) {
						e.printStackTrace();
					}

            		// TODO
					msgMgr.pushRecv(msg);
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
		sendThread.start();
	}

}
