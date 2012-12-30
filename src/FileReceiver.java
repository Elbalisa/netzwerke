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
	//DatagramPacket sendPacket;
	byte[] buffer = new byte[1400];
	//byte[] ackBuffer = new byte[1];
	byte[] payload;
	//byte[] fileNameBuffer = new byte[100];
	FileOutputStream fileOut;
	LongToByte ltb = new LongToByte();
	//CRC32 checker = new CRC32();

	AlternatingBitPacket packet;

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
			packet = new AlternatingBitPacket(address, senderPort, receiverPort);


			receivePacket = new DatagramPacket(buffer, buffer.length);
			daso.receive(receivePacket);

			while(!checksumIsRight()){
				daso.send(packet.getNak());
				daso.receive(receivePacket);
			}

			savePath += new String(payload);
			fileOut = new FileOutputStream(savePath, true);


			while(receivePacket.getLength() >= 1390){
				receivePacket = new DatagramPacket(buffer, buffer.length);
				do{
					daso.receive(receivePacket);
					daso.send(packet.getAck());
					while(!checksumIsRight()){
						daso.send(packet.getNak());
						daso.receive(receivePacket);
					}
					fileOut.write(packet.getPayload(receivePacket));
				}while(checksumIsRight());

			}
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
/*			
			//savePath += getFilename();

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

				fileOut.write(buffer, AlternatingBitPacket.HEADER_LENGTH, receivePacket.getLength()- AlternatingBitPacket.HEADER_LENGTH);
				//System.err.println(receivePacket.getLength());
				bytesReceived += buffer.length - AlternatingBitPacket.HEADER_LENGTH;
				ackBuffer[0] = buffer[0];
				sendPacket = new DatagramPacket(ackBuffer, ackBuffer.length, address, senderPort);
				daso.send(sendPacket);
				if(receivePacket.getLength() < 1390){
					fileOut.flush();
					fileOut.close();
					break;
				}
			}


	}

	private String getFilename() throws IOException{
		byte[] tempBuffer = new byte[buffer.length-AlternatingBitPacket.HEADER_LENGTH];
		for(int i = 0; i < tempBuffer.length; i++){
			tempBuffer[i] = buffer[i + AlternatingBitPacket.HEADER_LENGTH];
		}
		return new String(tempBuffer);
	}

	private long getChecksum(byte[] buffer){
		checker.reset();
		checker.update(buffer, AlternatingBitPacket.HEADER_LENGTH, buffer.length - AlternatingBitPacket.HEADER_LENGTH);
		long l = checker.getValue();
		//System.out.println("receiver: " + l);
		return l;
	}

	private boolean checksumIsRight(byte[] buffer){
		long sentChecksum = ltb.toLong(buffer, 1);
		long thisChecksum = getChecksum(buffer);
		return sentChecksum == thisChecksum;
	}
}*/