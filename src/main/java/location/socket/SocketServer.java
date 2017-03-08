package location.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
	private int serverPort;
	private ServerSocket server = null;
	private SocketClient socketClient = null;
	
	public SocketServer(final int serverPort) {
		super();
		this.serverPort = serverPort;
	}

	public void startSocketListening() {
		try {
			this.server = new ServerSocket(serverPort);
			
			Thread t = new Thread(new Runnable() {  
	            public void run() {  
	            	
	            	while (true) {
	            		if (socketClient == null || socketClient.isClosed()) {
		        			try {
		        				Socket client = server.accept();
		        				String remoteName = client.getInetAddress().getHostAddress();
		        				SocketClient socketClient = new SocketClient(client, remoteName);
		        				socketClient.beginRead();
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
	
	public void send(Message msg) {
		if (socketClient != null && !socketClient.isClosed()) {
			socketClient.write(msg.toString().getBytes());
		}
		
	}
	
}
