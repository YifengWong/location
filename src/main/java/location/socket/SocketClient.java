package location.socket;

import java.io.IOException;
import java.net.Socket;

public class SocketClient {
	public static final int BUFFER_LENGTH = 1024;

	private Socket client;
	private String name;
	private byte[] buffer = new byte[1024];
	
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
        				readLoop();
        				System.out.println("receive");
        			} catch (Exception e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
            }});
        t.start();
	}
	
	synchronized public void write(byte[] bytes) {
		try {
			int times = bytes.length / BUFFER_LENGTH;
			int last = bytes.length % BUFFER_LENGTH;
			for (int i = 0; i < times; i++) {
				client.getOutputStream().write(bytes, i*BUFFER_LENGTH, BUFFER_LENGTH);
				client.getOutputStream().flush();
			}
			client.getOutputStream().write(bytes, times*BUFFER_LENGTH, last);
			client.getOutputStream().flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void readLoop() throws Exception {
		int headCount = 0;
		while (headCount != Message.HEAD_LENGTH) {
			headCount += client.getInputStream().read(
					buffer, headCount, Message.HEAD_LENGTH - headCount);
		}
		Message msg = new Message(buffer);
		
		int times = msg.getFileLength() / BUFFER_LENGTH;
		for (int i = 0; i < times; i++) {
			int count = 0;
			while (count != BUFFER_LENGTH) {
				count += client.getInputStream().read(buffer, count, BUFFER_LENGTH - count);
			}
			msg.writeFileBytes(buffer, count);
		}
		
		int last = msg.getFileLength() % BUFFER_LENGTH;
		int lastCount = 0;
		while (lastCount != last) {
			lastCount += client.getInputStream().read(buffer, lastCount, last - lastCount);
		}
		msg.writeFileBytes(buffer, lastCount);
		
		System.out.println(msg);
	}
	
	public boolean isClosed() {
		return client.isClosed();
	}
	
}
