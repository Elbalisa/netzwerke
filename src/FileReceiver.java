import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FileReceiver implements Runnable {

	public String fileName;
	public String hostName;
	public int port;

	DatagramSocket daso;
	InetAddress address;
	DatagramPacket receivePacket;
	DatagramPacket sendPacket;
	byte[] buffer = new byte[1400];
	byte[] ackBuffer = new byte[1];
	FileOutputStream fileOut;
	LongToByte ltb = new LongToByte();

	public FileReceiver(String fileName, String hostName, int port){

		this.fileName = fileName;
		this.hostName = hostName;
		this.port = port;
		
	}

	public void run(){
		int bytesReceived = 0;
		try {
			daso = new DatagramSocket(port);
			receivePacket = new DatagramPacket(buffer, buffer.length);
			sendPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
			fileOut = new FileOutputStream(fileName, true);

			while(true){
				daso.receive(receivePacket);
				long l = ltb.toLong(buffer, 1);
				//TODO check checksum
				fileOut.write(buffer, FileSender.HEADER_LENGTH, receivePacket.getLength()-FileSender.HEADER_LENGTH);
				System.err.println(receivePacket.getLength());
				bytesReceived += buffer.length - FileSender.HEADER_LENGTH;
				if(receivePacket.getLength() < 1390){
					fileOut.flush();
					fileOut.close();
					break;
				}
				//daso.send(sendPacket);
				System.out.println(bytesReceived);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}