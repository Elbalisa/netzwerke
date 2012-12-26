package altes;
/**
 * @author Isabella Böttcher
 * @author Jasmin Brückmann
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

public class DurchsatzMessServer extends Thread{

	static final int PACKET_LENGTH = 1400;
	static final int SERVER_PORT = 8001;
	static final int CLIENT_PORT = 8008;
	//bei messung ueber netzwerk muss inTCP static sein und in der main ohne getter aufgerufen werden
	private boolean inTCP = true;

	public boolean getInTCP() {
		return inTCP;
	}

	int millis;
	long packetCounter = 0;
	private int lostPackets = 0;
	boolean goOn = true;
	byte[] buffer = new byte[PACKET_LENGTH];

	//Zur berechnung der Zeiten
	private long startTime;
	private long stopTime;
	private double time;
	private double flow;

	//Bekommt ein Paket von einem Client
	DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
	DatagramSocket daso;
	InetAddress inetAddress;

	//Für die TCP Verbindung
	ServerSocket seso;
	Socket acceptSocket;

	public DurchsatzMessServer(int millis, boolean inTCP){
		this.millis = millis;
		this.inTCP = inTCP;
	}

	/**
	 * Berechnet den Durchsatz.
	 * UDP wird direkt hier berechnet.
	 * TCP in einer extra Methode(tcp())
	 */
	public void run(){
		//Sendet über UDP oder TCP
		if(inTCP == true){
			tcp();
		}else{
			try {

				daso = new DatagramSocket(SERVER_PORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Speicherung der aktuelle Zeit 
			startTime = System.currentTimeMillis();
			while(goOn){

				try {
					daso.setSoTimeout(millis);
					daso.receive(packet);
					//Fuer den Fall das ein oder mehrere Pakete verloren gehen
					if(LongToByte.toLong(buffer) != packetCounter){
						long currentLoss = (LongToByte.toLong(buffer) - packetCounter); 
						//Fuer den Fall das ein Paket zu spaet kommt wird es hier wieder als Angekommn mit aufgenommen
						//und der packetCounter angepasst
						if(currentLoss < 0){
							lostPackets --;
							packetCounter --;
							//Verluste werden aufsummiert und der Counter auf die Nummer des nächsten zu erwartende
							//Packetes weiter gesetzt 
						}else{
							lostPackets += currentLoss;
							packetCounter = LongToByte.toLong(buffer);
						}
					}	
					//Pakete werden weiter gezählt
					packetCounter++;
				} catch (IOException e) {
					//Wenn der Timeout kommt wird die Zeit genommen und die Empfangsschleife abgebrochen
					if(e instanceof SocketTimeoutException){
						stopTime = System.currentTimeMillis();
						goOn = false;
					}else{
						e.printStackTrace();
					}
				}
				//Die echte Empfangszeit wird berechnet aus start-stop-Zeit - Wartezeit in der kein Paket mehr kommt
				time = ((stopTime - startTime) - millis) / 1000.;
			}
			daso.close();
			//Der Durchsatz wird berechnet in kbit pro Sekunde
			flow = ((packetCounter - lostPackets)*1400*8/1000) / time;
		}
		System.out.println("Empfangsrate = " + flow + " kbit pro Sekunde");
		System.out.println("Verlorene Packete:" + lostPackets);
	}
	
	/**
	 * Berechnet den Durchsatz für TCP Übertragung
	 */
	public void tcp(){

		try {
			int in = 0;
			//Verbindug wird aufgebaut
			seso = new ServerSocket(SERVER_PORT);
			acceptSocket = seso.accept();
			//Zeit wird genommen
			startTime = System.currentTimeMillis();
			//Schleife läuft bin endOfFile kommt 
			while(in != -1){
				InputStream input = acceptSocket.getInputStream();
				in = input.read(buffer);
				//Zählt hier bytes und nicht Pakete
				packetCounter += in;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Zeit wird nochmal genommen
		stopTime = System.currentTimeMillis();
		//Empfangszeit wird berechnet
		time = (stopTime - startTime) / 1000.;
		//Berechnung des Durchsatzes in kbit pro Sekunde
		flow = (packetCounter*8/1000) / time;
	}

	public static void main(String[] args) {
//beim messen uebers netzwerk muessen die entsprechenden zeilen auskommentiert weren
		DurchsatzMessServer ds = new DurchsatzMessServer(10000, false);
		ds.start();
		DurchsatzmessClient client = new DurchsatzmessClient(100,20,10, ds.getInTCP());
		client.start();

	}

}

