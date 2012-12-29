public class Main {

public static void main(String[] args){
	
	final int senderPort = 8000;
	final int receiverPort = 8003;

		FileReceiver fileR = new FileReceiver("/Users/Izzy/Desktop/3neu.png", "localhost", senderPort, receiverPort);
		FileSender fileS = new FileSender(senderPort, receiverPort, "3neu.png");

		new Thread (fileR).start();
		new Thread(fileS).start();

	}

}
