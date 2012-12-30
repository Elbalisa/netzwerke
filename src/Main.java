public class Main {

public static void main(String[] args){

	final int senderPort = 8000;
	final int receiverPort = 8003;

		FileReceiver fileR = new FileReceiver("/Users/Izzy/Desktop/", "localhost", senderPort, receiverPort);
//		FileSender fileS = new FileSender(senderPort, receiverPort, "/Users/Izzy/Pictures/takeAWookiee.jpg", "takeAWookiee.jpg");
		FileSender fileS = new FileSender(senderPort, receiverPort, "/Users/Izzy/Pictures/takeAWookiee.jpg", "takeAWookiee.jpg", 
				5, 6, 10);

		new Thread (fileR).start();
		new Thread(fileS).start();

	}

}