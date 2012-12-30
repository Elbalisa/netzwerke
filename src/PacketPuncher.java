import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class PacketPuncher extends DatagramSocket{
	
	private int bitError;
	private int loosePacket;
	private int copyPacket;
	private int counter;
	
	
	public PacketPuncher(int bitError, int loosePackt, int copyPacket) throws IOException{
		this.bitError = bitError;
		this.loosePacket = loosePackt;
		this.copyPacket = copyPacket;
	}
	
	public void punch(DatagramPacket packet) throws IOException{
		if((counter % bitError) == 0){
			receive(packet);
			packet.getData()[13] = (byte) 321;
		}else if((counter % loosePacket) == 0){
			receive(packet);
		}else if((counter % copyPacket) == 0){
			
		}
		counter ++;
	}

}
