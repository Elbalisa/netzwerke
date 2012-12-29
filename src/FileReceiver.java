import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.zip.CRC32;

public class FileReceiver implements Runnable {

	public String savePath;
	public String hostName;
	public int senderPort;
	public int receiverPort;

	DatagramSocket daso;
	InetAddress address;
	DatagramPacket receivePacket;
	DatagramPacket sendPacket;
	byte[] buffer = new byte[1400];
	byte[] ackBuffer = new byte[1];
	FileOutputStream fileOut;
	LongToByte ltb = new LongToByte();
	CRC32 checker = new CRC32();

	public FileReceiver(String savePath, String hostName, int senderPort, int receiverPort){

		this.savePath = savePath;
		this.hostName = hostName;
		this.senderPort = senderPort;
		this.receiverPort = receiverPort;

	}

	public void run(){
		int bytesReceived = 0;
		try {
			daso = new DatagramSocket(receiverPort);
			receivePacket = new DatagramPacket(buffer, buffer.length);
			daso.receive(receivePacket);
			savePath += getFilename();
			fileOut = new FileOutputStream(savePath, true);

			while(true){

				receivePacket = new DatagramPacket(buffer, buffer.length);
				address = InetAddress.getByName("localhost");
				daso.receive(receivePacket);
				while(!checksumIsRight(buffer)){
					ackBuffer[0] = (byte) ((buffer[0] + 1) %2);
					sendPacket = new DatagramPacket(ackBuffer, ackBuffer.length, address, senderPort);
					daso.send(sendPacket);
					daso.receive(receivePacket);
				}

				fileOut.write(buffer, FileSender.HEADER_LENGTH, receivePacket.getLength()-FileSender.HEADER_LENGTH);
				//System.err.println(receivePacket.getLength());
				bytesReceived += buffer.length - FileSender.HEADER_LENGTH;
				ackBuffer[0] = buffer[0];
				sendPacket = new DatagramPacket(ackBuffer, ackBuffer.length, address, senderPort);
				daso.send(sendPacket);
				if(receivePacket.getLength() < 1390){
					fileOut.flush();
					fileOut.close();
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String getFilename() throws IOException{
		byte[] tempBuffer = new byte[buffer.length-FileSender.HEADER_LENGTH];
		for(int i = 0; i < tempBuffer.length; i++){
			tempBuffer[i] = buffer[i + FileSender.HEADER_LENGTH];
		}
		return new String(tempBuffer);
	}

	private long getChecksum(byte[] buffer){
		checker.reset();
		checker.update(buffer, FileSender.HEADER_LENGTH, buffer.length - FileSender.HEADER_LENGTH);
		long l = checker.getValue();
		//System.out.println("receiver: " + l);
		return l;
	}

	private boolean checksumIsRight(byte[] buffer){
		long sentChecksum = ltb.toLong(buffer, 1);
		long thisChecksum = getChecksum(buffer);
		return sentChecksum == thisChecksum;
	}
}