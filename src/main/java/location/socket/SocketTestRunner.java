package location.socket;

import java.util.Scanner;

public class SocketTestRunner {
	public static final int SERVER_PORT = 9091;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SocketServer socketServer = new SocketServer(9091);
		socketServer.startListening();
		System.out.println("start");
		
		Scanner sc = new Scanner(System.in);

		Float[] params = new Float[21];
		for (int i = 0; i < 21; i ++) params[i] = 10f;
		
		
		while (true) {
			String input = sc.nextLine();
			if (input.equals("send")) {
				Message img = new Message(Message.FLAG_IMG, "QWERTYUIOPASDFGHJKLZXCVBNM123456", 3, "tyiu".getBytes(), params);
				socketServer.sendMsg(img);
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
				if (msg.getFlag() == Message.FLAG_IMG) {
					for (int i = 0; i < 21; i++) {
						System.out.print(msg.getParams()[i] + " ");
					}
					System.out.println();
				}


				
			} else if (input.equals("exit")) {
				break;
			} else {
				System.out.println("jump.");
			}
			
		}
	}

}
