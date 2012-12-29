import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.zip.CRC32;


public class AlternatingBitPacket {
	
	public static final int HEADER_LENGTH = 9;
	public static final int MAX_PAYLOAD = 1400 - HEADER_LENGTH;

	public int counter;
	public int OneOrNull;
	public long checksum;
	private CRC32 checksumCalculator = new CRC32();
	private DatagramPacket packet;
	public int port;
	private InetAddress address;
	private int totalBytesInBuffer;
	private byte[] buffer;
	
	public AlternatingBitPacket(InetAddress address, int port){
		this.address = address;
		this.port = port;
	}
	
	public AlternatingBitPacket() {
		// TODO Auto-generated constructor stub
	}

	public DatagramPacket getAck() {
		return new DatagramPacket(new byte[]{(byte) getOneOrNull()}, 1, address, port);
	}
	
	public DatagramPacket getNak(){
		return new DatagramPacket(new byte[]{(byte) ((getOneOrNull() ^ 1) & 0x1)}, 1, address, port);
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
		packet = new DatagramPacket(buffer, totalBytesInBuffer, address, port);
		counter ++;
		return packet;
	}
	
//	public DatagramPacket prepareAfterReceived(byte[] buffer, int receiveLength){
//	}
	
	public byte[] getPayload(DatagramPacket packet) {
		byte[] payload = new byte[packet.getLength() - HEADER_LENGTH];
		System.arraycopy(packet.getData(), HEADER_LENGTH, payload, 0, payload.length);
		return payload;
		
	}
	
	public int getOneOrNull() {
		return (counter % 2);
	}

}