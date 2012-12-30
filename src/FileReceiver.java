import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FileReceiver implements Runnable {

	public String savePath;
	public String hostName;
	public int senderPort;
	public int receiverPort;

	DatagramSocket daso;
	InetAddress address;
	DatagramPacket receivePacket;
	byte[] buffer = new byte[1400];
	byte[] payload;
	FileOutputStream fileOut;

	AlternatingBitPacket packet;

	public FileReceiver(String savePath, String hostName, int senderPort, int receiverPort) {

		this.savePath = savePath;
		this.hostName = hostName;
		this.senderPort = senderPort;
		this.receiverPort = receiverPort;

	}

	public void run(){
//		int bytesReceived = 0;

		try {
			address = InetAddress.getByName(hostName);
			daso = new DatagramSocket(receiverPort);
			packet = new AlternatingBitPacket(address, senderPort, receiverPort);


			receivePacket = new DatagramPacket(buffer, buffer.length);
			daso.receive(receivePacket);

			while(!checksumIsRight()) {
				System.err.println("Wrong checksum in filename: NAK");
				daso.send(packet.getNak());
				daso.receive(receivePacket);
			}
			daso.send(packet.getAck());
			System.err.println("ACK");

			savePath += new String(payload);
			boolean fileDeleted = new File(savePath).delete();
			System.err.println((fileDeleted ? "" : "no ") + "file deleted");
			fileOut = new FileOutputStream(savePath, true);

			do {

				receivePacket = new DatagramPacket(buffer, buffer.length);
				do{
					daso.receive(receivePacket);
					while(!checksumIsRight() || receivePacket.getData()[0] != packet.getOneOrNull()) {
						System.err.println((!checksumIsRight() ? "wrong checksum" : "") + 
						   (receivePacket.getData()[0] != packet.getOneOrNull() ? " wrong packet# " : "") + "NAK");
						daso.send(packet.getNak());
						daso.receive(receivePacket);
					}
					System.err.println("ACK");
					daso.send(packet.getAck());
					fileOut.write(packet.getPayload(receivePacket));
				}while(checksumIsRight());

			}while(receivePacket.getLength() >= 1390);

			fileOut.flush();
			fileOut.close();
			daso.close();

		}catch (IOException e) {
			e.printStackTrace();
		}

	}
	private boolean checksumIsRight(){
		long gottenChecksum = packet.extractChecksum(receivePacket);
		payload = packet.getPayload(receivePacket);
		long checksum = packet.getChecksum(payload);
		return checksum == gottenChecksum;
	}
}
