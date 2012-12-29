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
	DatagramPacket sendFileNamePacket;

	byte[] buffer = new byte[1400];
	byte[] receiveBuffer = new byte[1];
	byte[] fileNameBuffer;

	CRC32 checker = new CRC32();
	LongToByte ltb = new LongToByte();
	FileInputStream fileInput;
	final String filePath;
	final String fileName;
	Byte b;
	int bytesRead = 0;
	private int bytesTransmitted = 0;

	long checksum;
	int senderPort;
	int receiverPort;
	int oneOrNull;

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
			int counter = 0;

			daso.send(prepareFileName(fileName, counter));
			counter ++;
			

			bytesRead = fileInput.read(buffer, HEADER_LENGTH, buffer.length - HEADER_LENGTH);
			bytesTransmitted = bytesRead;
			answer = new DatagramPacket(receiveBuffer, receiveBuffer.length);

			while(bytesRead != -1) {

				oneOrNull = counter % 2;
				daso.send(packPacket(buffer));
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

	public DatagramPacket prepareFileName(String filename, int counter) throws IOException{
		oneOrNull = counter % 2;
		byte[] tempBuffer = filename.getBytes();
		fileNameBuffer = new byte[tempBuffer.length + HEADER_LENGTH];
		System.arraycopy(tempBuffer, 0, fileNameBuffer, HEADER_LENGTH, tempBuffer.length);
//		for(int i = 0; i < tempBuffer.length; i++){
//			fileNameBuffer[i] = tempBuffer[i + HEADER_LENGTH];
//		}
		//System.out.println(filename.getBytes().length);
		return packPacket(fileNameBuffer);
	}

	public DatagramPacket packPacket(byte[] buffer){
		buffer[0] = (byte) oneOrNull;
		long checksum = getChecksum(buffer);
		buffer = LongToByte.toByteArray(checksum, buffer, 1);
		packet = new DatagramPacket(buffer, bytesRead + HEADER_LENGTH, address, receiverPort);
		return packet;
	}

	public long getChecksum(byte[] buffer) {
		checker.reset();
		checker.update(buffer, HEADER_LENGTH, buffer.length - HEADER_LENGTH);
		long l = checker.getValue();
		//System.out.println("sender: " + l);
		return l;
	}

}