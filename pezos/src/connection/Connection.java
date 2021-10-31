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

import blockchaine.Account;
import blockchaine.Block;
import blockchaine.State;
import operations.HachOfOperations;
import operations.ListOperations;
import operations.Operation;
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
			
			// interaction avec l'utilisateur ( REPL 
			Interaction inter = new Interaction();
			Scanner myObj = new Scanner(System.in);
			System.out.println("Donnez le tag souhaité : ");
		    int tag = myObj.nextInt();
		    
		    byte[] reponse = inter.tagCall(tag, this.out, this.in);
		    
		    if (tag < 5) {
		    	Block blockAsObjet = new Block(reponse);
		    	System.out.println(blockAsObjet);
		    }  else if (tag == 5) {
		    	ListOperations lop = new ListOperations();
		    	lop.extractAllOperations(reponse);
		    	HachOfOperations hashOps = new HachOfOperations(lop.getListOperations());
		    	byte[] hashDesOperations = hashOps.ops_hash();
		    }  else if (tag == 7) {
		    	State state = new State();
		    	state.extractState(reponse);
		    	Account account = new Account();
		    	account.extractAccount(state.getAccountsBytes());
		    }

			this.closeConnection(socket);
		} 

	public DataOutputStream getOut() {
		return out;
	}

	public DataInputStream getIn() {
		return in;
	}
	
	public void closeConnection(Socket socket) throws IOException {
		this.in.close();
		this.out.close();
		Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
	    try {
	        socket.close();
	        System.out.println("The server is shut down!");
	    } catch (IOException e) {  }
		}});
	}
	
	
}