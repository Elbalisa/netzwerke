import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.zip.CRC32;

public class AlternatingBitPacket {
	
	public static final int HEADER_LENGTH = 9;
	public static final int MAX_PAYLOAD = 1400 - HEADER_LENGTH;

	private int counter = 0;
	private long checksum;
	private CRC32 checksumCalculator = new CRC32();
	private DatagramPacket packet;
	private int senderPort;
	private int receiverPort;
	private InetAddress address;
	private byte[] buffer;
	
	public AlternatingBitPacket(InetAddress address, int senderPort, int receiverPort){
		this.address = address;
		this.senderPort = senderPort;
		this.receiverPort = receiverPort;
	}
	
	public DatagramPacket getAck() {
		DatagramPacket currentPacket = new DatagramPacket(new byte[]{(byte) getOneOrNull()}, 1, address, senderPort);
		counter ++;
		return currentPacket;
	}
	
	public DatagramPacket getNak(){
		return new DatagramPacket(new byte[]{ (byte) ((getOneOrNull() ^ 1) & 0x1)}, 1, address, senderPort);
	}
	
	public long getChecksum(byte[] payload){
		checksumCalculator.reset();
		checksumCalculator.update(payload, 0, payload.length);
		checksum = checksumCalculator.getValue();
		return checksum;
	}
	
	public long extractChecksum(DatagramPacket packet){
		return LongToByte.toLong(packet.getData(), 1);
	}
	
	public DatagramPacket prepareToSend(byte[] payload) {
		this.buffer = new byte[HEADER_LENGTH + payload.length];
		buffer[0] = (byte) getOneOrNull();
		LongToByte.toByteArray(getChecksum(payload), buffer, 1);
		System.arraycopy(payload, 0, buffer, HEADER_LENGTH, payload.length);
		packet = new DatagramPacket(buffer, buffer.length, address, receiverPort);
		counter ++;
		return packet;
	}
	
	public byte[] getPayload(DatagramPacket packet) {
		byte[] payload = new byte[packet.getLength() - HEADER_LENGTH];
		System.arraycopy(packet.getData(), HEADER_LENGTH, payload, 0, payload.length);
		return payload;
		
	}
	
	public int getOneOrNull() {
		return (counter % 2);
	}

}
