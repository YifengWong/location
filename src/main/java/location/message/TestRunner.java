package location.message;

import java.util.Scanner;

/**
 * Created by wyf on 17-4-8.
 */
public class TestRunner {
    public static void main(String[] args) {
        AbstractMsgService msgService = new SocketMsgService(9091, new MsgManager());
        msgService.startService();
        System.out.println("start");

        Scanner sc = new Scanner(System.in);

        Float[] params = new Float[21];
        for (int i = 0; i < 21; i ++) params[i] = 10f;


        while (true) {
            String input = sc.nextLine();
            if (input.equals("send")) {
                for (int i = 0; i < 5; i++) {
                    Message img = new Message(Message.FLAG_IMG, "QWERTYUIOPASDFGHJKLZXCVBNM123456", i, "tyiu".getBytes(), params);
                    msgService.sendMsg(img);
                }

                for (int i = 0; i < 6; i++) {
                    Message img = new Message(Message.FLAG_IMG, "123456UIOPASDFGHJKLZXCVBNM123456", i, "tyiu".getBytes(), params);
                    msgService.sendMsg(img);
                }
                System.out.println("send finish");
            } else if (input.equals("recv")) {
                Message msg = msgService.getNextMsg();
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
            } else if (input.equals("result")) {
                System.out.println(msgService.getResult("QWERTYUIOPASDFGHJKLZXCVBNM123456"));
                System.out.println(msgService.getResult("123456UIOPASDFGHJKLZXCVBNM123456"));
            } else {
                System.out.println("jump.");
            }

        }
    }
}
