import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.zip.CRC32;

public class FileSender implements Runnable {

	public static final int HEADER_LENGTH = 10;

	DatagramSocket daso;
	InetAddress address;
	DatagramPacket packet;
	DatagramPacket answer;

	byte[] buffer = new byte[1400];
	byte[] receiveBuffer = new byte[1400];
	CRC32 checker = new CRC32();
	LongToByte ltb = new LongToByte();
	FileInputStream fileInput;
	final String fileName = "takeAWookiee.jpg";
	Byte b;
	int bytesRead = 0;
	private int bytesTransmitted = 0;

	int senderPort;
	int receiverPort;
	int oneOrNull;

	public FileSender(int senderPort, int receiverPort){
		this.senderPort = senderPort;
		this.receiverPort = receiverPort;
	}

	public void run(){

		try {
			address = InetAddress.getByName("localhost");
			daso = new DatagramSocket(senderPort);
			fileInput = new FileInputStream("/Users/Izzy/Desktop/3.png");
			int counter = 0;


			//TODO dateiname mitschicken!
			buffer = LongToByte.toByteArry(getChecksum(buffer), buffer, 1);
			//buffer[9] = b.parseByte(fileName);
			bytesRead = fileInput.read(buffer, HEADER_LENGTH, buffer.length - HEADER_LENGTH);//TODO check end condition
			bytesTransmitted = bytesRead;
			answer = new DatagramPacket(receiveBuffer, receiveBuffer.length);

			while(bytesRead != -1){

				oneOrNull = counter % 2;
				buffer[0] = (byte) oneOrNull;
				packet = new DatagramPacket(buffer, bytesRead +HEADER_LENGTH, address, receiverPort);
				daso.send(packet);
				Thread.sleep(30);
				bytesRead = fileInput.read(buffer, HEADER_LENGTH, buffer.length - HEADER_LENGTH);
				if(bytesRead != -1) bytesTransmitted += bytesRead;
				/*daso.receive(answer);
					while(receiveBuffer[0] != (byte) oneOrNull){
						daso.send(packet);
						daso.receive(answer);
					}*/                                                                                                                
				counter ++;
			}
			System.out.println(counter + " Pakete gesendet = " + bytesTransmitted);
			daso.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long getChecksum(byte[] buffer){
		//TODO Updaten prüfen RESETTEN
		checker.reset();
		checker.update(buffer, 2, buffer.length-2);
		long l = checker.getValue();
		return l;
	}

}