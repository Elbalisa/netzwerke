import java.net.DatagramPacket;

public class PacketPuncher {
	
	private int bitError;
	private int loosePacket;
	private int copyPacket;
	
	
	public PacketPuncher(int bitError, int loosePackt, int copyPacket){
		this.bitError = bitError;
		this.loosePacket = loosePackt;
		this.copyPacket = copyPacket;
	}
	
	public void punch(){
	}

}
