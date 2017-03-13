package location.socket;

import java.util.Scanner;

public class SocketTestRunner {
	public static final int SERVER_PORT = 9091;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SocketServer socketServer = new SocketServer(9091);
		socketServer.startListening();
//		socketServer.send(new Message("123"));
		System.out.println("start");
		
		Scanner sc = new Scanner(System.in);
		
		
		while (true) {
			String input = sc.nextLine();
			if (input.equals("send")) {
				Message msg = new Message((byte)1, "QWERTYUIOPASDFGHJKLZXCVBNM123456", 3, "tyiu".getBytes());
				socketServer.sendMsg(msg);
				System.out.println("send finish");
			} else if (input.equals("recv")) {
				Message msg = socketServer.getNextMsg();
				if (msg == null) {
					System.out.println("nothing.");
					continue;
				}
				
				System.out.println((int)msg.getFlag());
				System.out.println(msg.getFileNum());
				System.out.println(msg.getFileLength());
				
				for (int i = 0; i < msg.getFileLength(); i++) {
					byte[] msgBytes = msg.getFileBytes();
					System.out.print((char)msgBytes[i]);
				}
				System.out.println();
				
			} else if (input.equals("exit")) {
				break;
			} else {
				System.out.println("jump.");
			}
			
		}
	}

}
