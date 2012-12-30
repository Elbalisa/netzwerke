import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FileSender implements Runnable {

	DatagramSocket daso;
	InetAddress address;
	//DatagramPacket packet;
	DatagramPacket answer;
	//	DatagramPacket sendFileNamePacket;

	byte[] buffer = new byte[AlternatingBitPacket.MAX_PAYLOAD];
	byte[] receiveBuffer = new byte[1];
	byte[] fileNameBuffer;

	//CRC32 checker = new CRC32();
	//LongToByte ltb = new LongToByte();
	FileInputStream fileInput;
	final String filePath;
	final String fileName;
	Byte b;
	int bytesRead = 0;
	private int bytesTransmitted = 0;



	//long checksum;
	int senderPort;
	int receiverPort;
	private AlternatingBitPacket packet;
	//int oneOrNull;

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

			DatagramPacket currentPacket = packet.prepareToSend(fileName.getBytes());

			do{
				daso.send(currentPacket);
				daso.receive(answer);
			}while(currentPacket.getData()[0] != answer.getData()[0]);

			bytesRead = fileInput.read(buffer, 0, buffer.length);
			bytesTransmitted = bytesRead;
			while(bytesRead != -1) {
				currentPacket = packet.prepareToSend(buffer);
				do{
					daso.send(currentPacket);
					daso.receive(answer);
				}while(currentPacket.getData()[0] != answer.getData()[0]);
				bytesTransmitted += bytesRead;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
/*		
			daso.send(prepareFileName(fileName, counter));
			counter ++;


			answer = new DatagramPacket(receiveBuffer, receiveBuffer.length);

				//oneOrNull = counter % 2;
				daso.send(packPacket(buffer));
				Thread.sleep(30);
				if(bytesRead != -1) {
				}
				daso.receive(answer);
				while(receiveBuffer[0] != (byte) oneOrNull){
					daso.send(packet);
					daso.receive(answer);
				}
				bytesRead = fileInput.read(buffer, AlternatingBitPacket.HEADER_LENGTH, buffer.length - AlternatingBitPacket.HEADER_LENGTH);
				counter ++;
			}
			System.out.println(counter + " Pakete gesendet = " + bytesTransmitted);
			daso.close();
	}

	public DatagramPacket prepareFileName(String filename, int counter) throws IOException{
		oneOrNull = counter % 2;
		byte[] tempBuffer = filename.getBytes();
		fileNameBuffer = new byte[tempBuffer.length + AlternatingBitPacket.HEADER_LENGTH];
		System.arraycopy(tempBuffer, 0, fileNameBuffer, AlternatingBitPacket.HEADER_LENGTH, tempBuffer.length);
		//System.out.println(filename.getBytes().length);
		return packPacket(fileNameBuffer);
	}

	public DatagramPacket packPacket(byte[] buffer){
		buffer[0] = (byte) oneOrNull;
		long checksum = getChecksum(buffer);
		buffer = LongToByte.toByteArray(checksum, buffer, 1);
		packet = new DatagramPacket(buffer, bytesRead + AlternatingBitPacket.HEADER_LENGTH, address, receiverPort);
		return packet;
	}

	public long getChecksum(byte[] buffer) {
		checker.reset();
		checker.update(buffer, AlternatingBitPacket.HEADER_LENGTH, buffer.length - AlternatingBitPacket.HEADER_LENGTH);
		long l = checker.getValue();
		//System.out.println("sender: " + l);
		return l;
	}

}*/