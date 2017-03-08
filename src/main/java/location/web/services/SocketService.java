package location.web.services;


import org.springframework.stereotype.Service;

import location.socket.SocketServer;

@Service
public class SocketService {
	
	public static final int SERVER_PORT = 9091;
	private SocketServer socketServer;
	
	public SocketService(){
		super();
		socketServer = new SocketServer(9091);
//		socketServer.startSocketListening();
	}
	


}
