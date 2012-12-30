import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class FileSender implements Runnable {

	DatagramSocket daso;
	InetAddress address;
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
	private final boolean isPacketPuncher;
	private int bitError;
	private int loosePacket;
	private int dublicatePacket;

	public FileSender(int senderPort, int receiverPort, String filePath, String fileName){
		isPacketPuncher = false;
		this.senderPort = senderPort;
		this.receiverPort = receiverPort;
		this.filePath = filePath;
		this.fileName = fileName;
	}

	public FileSender(int senderPort, int receiverPort, String filePath, String fileName, 
			int bitError, int loosePacket, int dublicatePacket){
		isPacketPuncher = true;
		this.senderPort = senderPort;
		this.receiverPort = receiverPort;
		this.filePath = filePath;
		this.fileName = fileName;
		this.bitError = bitError;
		this.loosePacket = loosePacket;
		this.dublicatePacket = dublicatePacket;

	}

	public void run(){

		try {
			address = InetAddress.getByName("localhost");
			if(isPacketPuncher){
				daso = new PacketPuncher(senderPort, bitError, loosePacket, dublicatePacket);
			}else{
				daso = new DatagramSocket(senderPort);
			}

			fileInput = new FileInputStream(filePath);

			packet = new AlternatingBitPacket(address, senderPort, receiverPort);

			currentPacket = packet.prepareToSend(fileName.getBytes());
			sendWithTimeout(currentPacket);

			bytesRead = fileInput.read(buffer, 0, buffer.length);
			bytesTransmitted = bytesRead;
			while(bytesRead != -1) {
				currentPacket = packet.prepareToSend(buffer);
				sendWithTimeout(currentPacket);
				bytesTransmitted += bytesRead;
				bytesRead = fileInput.read(buffer, 0, buffer.length);
			}
			System.out.println(bytesTransmitted);
			daso.close();
			fileInput.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void sendWithTimeout(DatagramPacket currentPacket) throws IOException, SocketException {
		DatagramPacket answer = new DatagramPacket(receiveBuffer, receiveBuffer.length);
		boolean timeout = false;

		do{
			daso.send(currentPacket);
			timeout = false;
			try{
				daso.setSoTimeout(300);
				daso.receive(answer);
			} catch (SocketTimeoutException e) {
				timeout = true;
			}

		}while(currentPacket.getData()[0] != answer.getData()[0] || timeout);
	}
}

