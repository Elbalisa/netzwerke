import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.zip.CRC32;

public class FileSender implements Runnable {

	public static final int HEADER_LENGTH = 9;

	DatagramSocket daso;
	InetAddress address;
	DatagramPacket packet;
	DatagramPacket answer;
	DatagramPacket sendFileName;

	byte[] buffer = new byte[1400];
	byte[] receiveBuffer = new byte[1];
	byte[] fileNameBuffer;
	
	CRC32 checker = new CRC32();
	LongToByte ltb = new LongToByte();
	FileInputStream fileInput;
	final String fileName;
	Byte b;
	int bytesRead = 0;
	private int bytesTransmitted = 0;

	int senderPort;
	int receiverPort;
	int oneOrNull;

	public FileSender(int senderPort, int receiverPort, String fileName){
		this.senderPort = senderPort;
		this.receiverPort = receiverPort;
		this.fileName = fileName;
	}

	public void run(){

		try {
			address = InetAddress.getByName("localhost");
			daso = new DatagramSocket(senderPort);
			fileInput = new FileInputStream("/Users/Izzy/Desktop/3.png");
			int counter = 0;
			
			//counter = prepareFileName(fileName, counter);


			//TODO dateiname mitschicken!
			//buffer[9] = b.parseByte(fileName);
			bytesRead = fileInput.read(buffer, HEADER_LENGTH, buffer.length - HEADER_LENGTH);//TODO check end condition
			bytesTransmitted = bytesRead;
			answer = new DatagramPacket(receiveBuffer, receiveBuffer.length);

			while(bytesRead != -1) {

				//TODO buffer[1] = dateiname
				oneOrNull = counter % 2;
				buffer[0] = (byte) oneOrNull;
				long checksum = getChecksum(buffer);
				buffer = LongToByte.toByteArray(checksum, buffer, 1);
				packet = new DatagramPacket(buffer, bytesRead + HEADER_LENGTH, address, receiverPort);
				daso.send(packet);
				Thread.sleep(30);
				if(bytesRead != -1) {
					bytesTransmitted += bytesRead;
				}
				daso.receive(answer);
				while(receiveBuffer[0] != (byte) oneOrNull){
					daso.send(packet);
					daso.receive(answer);
				}
				bytesRead = fileInput.read(buffer, HEADER_LENGTH, buffer.length - HEADER_LENGTH);
				counter ++;
			}
			System.out.println(counter + " Pakete gesendet = " + bytesTransmitted);
			daso.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public int prepareFileName(String filename, int counter) throws IOException{
		fileNameBuffer =  new byte[filename.length() + HEADER_LENGTH];
		oneOrNull = counter % 2;
		fileNameBuffer[0] = (byte) oneOrNull;
		//filename.getChars(0, filename.length(), fileNameBuffer, HEADER_LENGTH);
		sendFileName = new DatagramPacket(fileNameBuffer, fileNameBuffer.length, address, receiverPort);
		daso.send(sendFileName);
		return counter++;
	}

	public long getChecksum(byte[] buffer) {
		checker.reset();
		checker.update(buffer, 2, buffer.length-2);
		long l = checker.getValue();
		return l;
	}

}