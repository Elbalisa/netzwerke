import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class PacketPuncher extends DatagramSocket{
	
	private int bitError;
	private int loosePacket;
	private int duplicatePacket;
	private int counter = 0;
	
	
	public PacketPuncher(int serverPort, int bitError, int loosePackt, int copyPacket) throws IOException{
		super(serverPort);
		this.bitError = bitError;
		this.loosePacket = loosePackt;
		this.duplicatePacket = copyPacket;
	}
	
	public void send(DatagramPacket packet) throws IOException {
		counter ++;
		if((counter % bitError) == 0) {
			packet.getData()[13] = (byte)(packet.getData()[13] ^ 10);
		} else if((counter % loosePacket) == 0)  {
			return;
		} else if((counter % duplicatePacket) == 0){
			super.send(packet);
		}
		super.send(packet);
	}
}
