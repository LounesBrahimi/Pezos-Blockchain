package connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.codec.DecoderException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;

import tools.Utils;

/*
 * Communication with the server
 * */

public class Connection {
	
	public Connection(String hostname, int port, String skString, String pkString) throws UnknownHostException, IOException, DecoderException, DataLengthException, CryptoException {

			Socket socket = new Socket(hostname, port); 
			DataInputStream  in	= new DataInputStream (new BufferedInputStream (socket.getInputStream ()));
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			
			Utils util = new Utils();
			
			// recupere le 1er message : seed
			byte[] seed = util.getFromSocket(24,in,"seed"); 

			// envoie la clé public : pk
			util.sendToSocket (pkString,out,"pk");

			// envoie du 3eme message "la graine hashé et signé"
			byte[] hashSeed = util.hash(seed, 32);
			byte[] signature = util.signature(hashSeed, skString);

			util.sendToSocket(signature,out,"signature");
		} 
	// fermer a la fin les ports et flux.
}
