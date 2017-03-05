package location.socket;

import java.io.IOException;
import java.net.Socket;

public class SocketClient {

	private Socket client;
	private String name;
	
	public SocketClient(Socket client, String name) {
		super();
		this.client = client;
		this.name = name;
	}
	public Socket getClient() {
		return client;
	}
	public String getName() {
		return name;
	}
	public void beginRead() {
		Thread t = new Thread(new Runnable() {  
            public void run() {  
            	while (true) {
        			try {
        				System.out.println("receive from " + name + " : " + (byte)client.getInputStream().read());
        				ServerMessage msg = new ServerMessage((byte)1, "479724219cce437a9d4030b5f279a18c", 1, 0, null);
        				
        				write(msg.getMsgBytes());
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
            }});
        t.start();
	}
	
	synchronized public void write(byte[] bytes) {
		try {
			client.getOutputStream().write(bytes);
			client.getOutputStream().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public boolean isClosed() {
		return client.isClosed();
	}
	
}
