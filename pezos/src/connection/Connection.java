package connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.DecoderException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;

import blockchaine.Block;
import repl.Interaction;
import tools.Utils;

/*
 * Communication with the server
 * */

public class Connection {
	
	private DataOutputStream out;
	private DataInputStream  in;
	
	public Connection(String hostname, int port, String skString, String pkString) throws UnknownHostException, IOException, DecoderException, DataLengthException, CryptoException, InterruptedException {

			this.in = in;
			this.out = out;
			
			Socket socket = new Socket(hostname, port); 
			this.in	= new DataInputStream (new BufferedInputStream (socket.getInputStream ()));
			this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			
			Utils util = new Utils();
			
			// recupere le 1er message : seed
			byte[] seed = util.getFromSocket(24,this.in,"seed"); 

			// envoie la clé public : pk
			util.sendToSocket (pkString,this.out,"pk");

			// envoie du 3eme message "la graine hashé et signé"
			byte[] hashSeed = util.hash(seed, 32);
			byte[] signature = util.signature(hashSeed, skString);

			util.sendToSocket(signature,this.out,"signature");
			
			Interaction inter = new Interaction();
			Scanner myObj = new Scanner(System.in);
			System.out.println("Donnez le tag souhaité : ");
		    int tag = myObj.nextInt();
		    byte[] reponse = inter.tagCall(tag, this.out, this.in);
		    Block blockAsObjet = new Block(reponse);
			System.out.println(blockAsObjet);
			System.out.println("=======Verif predecessor============");
			//inter.verifyPredecessorValue(blockAsObjet.getLevel()-1, blockAsObjet.getPredecessor(), this.out, this.in);
			inter.verifyTimeStamp(blockAsObjet.getLevel()-1, blockAsObjet.getTimeStamp(), out, in);
			System.out.println("========================");
			
		//s	byte[] msg = util.to2BytesArray(3);
			//util.sendToSocket (msg,out,"tag 12");
			//byte[] blockAsBytes = util.getFromSocket(174,this.in,"block");
			
			//TimeUnit.MINUTES.sleep(1);
			this.in.close();
			this.out.close();
			Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
			    try {
			        socket.close();
			        System.out.println("The server is shut down!");
			    } catch (IOException e) {  }
				}});
		} 
	// fermer a la fin les ports et flux.

	public DataOutputStream getOut() {
		return out;
	}

	public DataInputStream getIn() {
		return in;
	}
	
	public void closeConnection(Socket socket) {
		Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
	    try {
	        socket.close();
	        System.out.println("The server is shut down!");
	    } catch (IOException e) {  }
		}});
	}
	
	
}