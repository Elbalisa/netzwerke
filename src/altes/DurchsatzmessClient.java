package altes;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;

public class DurchsatzmessClient extends Thread{

	DatagramSocket daso;
	DatagramPacket sendPacket;

	byte[] sendBuffer = new byte[DurchsatzMessServer.PACKET_LENGTH];

	//Verzoegerung nach n Paketen.
	private long k;

	//Anzahl der Pakete, nachdenen gewartet wird.
	private int n;

	//Zeitraum, ueber welchen Pakete verschickt werden sollen, in Sekunden.
	private int limit;

	//Zaehlt die gesendeten Packete
	private long packetCounter;

	//Zum stoppen der Anfangs- und Endzeit
	private long startTime;
	private long stopTime;

	//Die Adresse auf die die Sockets sich verbinden
	InetAddress address;

	//Fuer die TCP Übertragung
	Socket socket;
	
	//Legt fest ob über TCP oder UDP gesendet wird
	private boolean inTCP;
	
	//Senderate
	private double flow;

	public DurchsatzmessClient(int n, int k, int l, boolean inTCP) {
		this.inTCP = inTCP;
		this.k = k;
		this.n = n;
		limit = l;

	}

	/**
	 *Sendet Pakete.
	 */
	public void run(){

		if(inTCP == true){
			tcp();
		}else{

			try {
				//Aufbau der Verbindung
				daso = new DatagramSocket(DurchsatzMessServer.CLIENT_PORT);
				address = InetAddress.getByName("localhost");
				//Konvertierung von der Long Packetnummer in Byte
				LongToByte.toByteArry(packetCounter, sendBuffer);
				//Senden des Pakets
				sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, DurchsatzMessServer.SERVER_PORT);
				//Zeitnahme
				startTime = System.currentTimeMillis();
				//Berechnung der Stopzeit
				stopTime = startTime + limit*1000;
				//Schleife läuft bis die Stopzeit erreicht ist 
				while(startTime < stopTime){
					//Die eingegebene Verzoegerung wird erzeugt
					if((packetCounter % n) == 0){
						sleep(k);
					}
					//Uebertragugn des Pakets
					daso.send(sendPacket);
					packetCounter ++;
					//Erneute Zeitnahme für die Pruefung ob Stpopzeit erreicht
					startTime = System.currentTimeMillis();
					//Konvertierung der Byte Paketnummer in Long
					LongToByte.toByteArry(packetCounter, sendBuffer);
					System.out.println(sendBuffer[0]);
					//Das naechste Paket für die uebertragugn 
					sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, DurchsatzMessServer.SERVER_PORT);
					flow = (packetCounter*1400*8/1000.) / (limit);
				}
				daso.close();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Senderate = " + flow + "kbit pro Sekunde");
	}

	/**
	 *Senden der Pakete ueber TCP
	 */
	public void tcp(){

		try {
			//Aufbau der Verbindung
			InetAddress addr = InetAddress.getByName("localhost");
			socket = new Socket(addr, DurchsatzMessServer.SERVER_PORT);
			OutputStream output = socket.getOutputStream();
			//Zeitnahme und berechnung der Stopzeit
			startTime = System.currentTimeMillis();
			stopTime = startTime + limit*1000;
			while(startTime < stopTime){
				//Die eingegebene Verzoegerung wird erzeugt
				if((packetCounter % n) == 0){
					sleep(k);
				}
				//Paket wird gesendet
				output.write(sendBuffer);
				packetCounter ++;
				//Zeit wird aktualisiert
				startTime = System.currentTimeMillis();
			}
			flow = (packetCounter*8*1400/1000.) / limit;
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}