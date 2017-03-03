package location.web.services;

import java.io.IOException;
import java.net.ServerSocket;

import org.springframework.stereotype.Service;

@Service
public class SocketService {
	
	public static final int SERVER_PORT = 9091;
	private ServerSocket server = null;
	
	public SocketService(){
		super();
		startSocketListening();
	}
	
	private void startSocketListening() {
		try {
			this.server = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			System.out.println("启动失败");
			e.printStackTrace();
		}
	}
}
