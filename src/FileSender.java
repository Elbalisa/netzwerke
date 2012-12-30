import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class FileSender implements Runnable {

	DatagramSocket daso;
	InetAddress address;
	DatagramPacket answer;
	DatagramPacket currentPacket;

	byte[] buffer = new byte[AlternatingBitPacket.MAX_PAYLOAD];
	byte[] receiveBuffer = new byte[1];
	byte[] fileNameBuffer;

	FileInputStream fileInput;
	final String filePath;
	final String fileName;
	Byte b;
	int bytesRead = 0;
	private int bytesTransmitted = 0;

	int senderPort;
	int receiverPort;
	private AlternatingBitPacket packet;

	public FileSender(int senderPort, int receiverPort, String filePath, String fileName){
		this.senderPort = senderPort;
		this.receiverPort = receiverPort;
		this.filePath = filePath;
		this.fileName = fileName;
	}

	public void run(){

		try {
			address = InetAddress.getByName("localhost");
			daso = new DatagramSocket(senderPort);
			fileInput = new FileInputStream(filePath);

			packet = new AlternatingBitPacket(address, senderPort, receiverPort);

			currentPacket = packet.prepareToSend(fileName.getBytes());
			answer = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			
			do{
				daso.send(currentPacket);
				//daso.setSoTimeout(30);
				daso.receive(answer);
			}while(currentPacket.getData()[0] != answer.getData()[0]);

			bytesRead = fileInput.read(buffer, 0, buffer.length);
			bytesTransmitted = bytesRead;
			while(bytesRead != -1) {
				currentPacket = packet.prepareToSend(buffer);
				do{
					daso.send(currentPacket);
					System.out.println("PACKET sent");
					daso.receive(answer);
				}while(currentPacket.getData()[0] != answer.getData()[0]);
				bytesTransmitted += bytesRead;
				bytesRead = fileInput.read(buffer, 0, buffer.length);
			}
			System.out.println(bytesTransmitted);
			daso.close();
			fileInput.close();

		} catch (IOException e) {
			if(e instanceof SocketTimeoutException){
				try {
					daso.send(currentPacket);
					return;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
		
	}
}