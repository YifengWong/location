package location.socket;

public class SocketTestRunner {
	public static final int SERVER_PORT = 9091;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SocketServer socketServer = new SocketServer(9091);
		socketServer.startSocketListening();
//		socketServer.send(new Message("123"));
		System.out.println("start");
	}

}
