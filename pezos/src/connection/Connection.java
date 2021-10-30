package connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * Communication with the server
 * */

public class Connection {
	
	public Connection(String hostname, int port, String skString, String pkString) throws UnknownHostException, IOException {

			Socket socket = new Socket(hostname, port); 
			DataInputStream  in	= new DataInputStream (new BufferedInputStream (socket.getInputStream ()));
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			
			////// 1st message = seed
			byte[] seed = Utils.getFromSocket(24,this.in,"seed"); 
			//seed = Utils.toBytesArray("12230025a6118122e9eac8785c74193819441fe57fec4845"); System.out.println("seed = "+Utils.toStringOfHex(seed));

			/////// 2nd message = pk
			Utils.sendToSocket (pkString,this.out,"pk");

			/////// 3d message = signature of hashSeed
			byte[] hashSeed = Utils.hash(seed, 32);
			byte[] signature = Utils.signature(hashSeed, skString);
			//System.out.println("hashSeed  = " + Utils.toStringOfHex(hashSeed ));
			//System.out.println("signature = " + Utils.toStringOfHex(signature));
			Utils.sendToSocket(signature,this.out,"signature");
		} 
	
	// fermer a la fin les ports et flux.
}
