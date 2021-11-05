package connection;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

import org.apache.commons.codec.DecoderException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;

import blockchaine.Block;
import operations.HachOfOperations;
import operations.ListOperations;
import repl.Interaction;
import state.ListAccounts;
import state.State;
import tools.Utils;

/*
 * Communication with the server
 * */

public class Connection {
	
	private DataOutputStream out;
	private DataInputStream  in;
	private Socket socket;
	
	public Connection(String hostname, int port, String pkString, String skString) throws UnknownHostException, IOException, DecoderException, DataLengthException, CryptoException, InterruptedException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
	
			Socket socket = new Socket(hostname, port); 
			this.socket = socket;
			this.in	 = new DataInputStream (new BufferedInputStream (socket.getInputStream ()));
			this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			
			Utils util = new Utils();
			
			// recupere le 1er message : seed
			byte[] seed = util.getFromSocket(24,this.in,"seed"); 

			// envoie la cl� public : pk
			util.sendToSocket (pkString,this.out,"pk");

			// envoie du 3eme message "la graine hash� et sign�"
			byte[] hashSeed = util.hash(seed, 32);
			byte[] signature = util.signature(hashSeed, skString);
			util.sendToSocket(signature,this.out,"signature");
			
		} 

	public void manualInteraction (String pkString, String skString) throws org.bouncycastle.util.encoders.DecoderException, IOException, DecoderException, InvalidKeyException, DataLengthException, SignatureException, InvalidKeySpecException, NoSuchAlgorithmException, CryptoException{
		Utils util = new Utils();
		Interaction inter = new Interaction();

			Scanner myObj = new Scanner(System.in);
			System.out.println("Donnez le tag : ");
		    int tag = myObj.nextInt();
		    
		    byte[] reponse = inter.tagCall(tag, this.out, this.in);
		    
		    if (tag == 1) {
		    	Block blockAsObjet = new Block(reponse);
		    	System.out.println(blockAsObjet);
				inter.verifyErrors(blockAsObjet, out, in, pkString, skString);
			}   else if (tag == 3) {
				Block blockAsObjet = new Block(reponse);
				System.out.println(blockAsObjet);
		    }  else if (tag == 5) {
		    	ListOperations lop = new ListOperations();
		    	lop.extractAllOperations(reponse);
		    	HachOfOperations hashOps = new HachOfOperations(lop.getListOperations());
		    	byte[] hashDesOperations = hashOps.ops_hash();
		    	System.out.println("hash calcul operations : "+ util.toHexString(hashDesOperations));
		    }  else if (tag == 7) {
		    	State state = new State();
		    	state.extractState(reponse);
		    	System.out.println("hash calcul state "+ util.toHexString(state.hashTheState()));

		    	ListAccounts lAccounts = new ListAccounts();
		    	lAccounts.extractAllAccounts(state.getAccountsBytes());
		    }
			myObj.close();
	}

	public DataOutputStream getOut() {
		return out;
	}

	public DataInputStream getIn() {
		return in;
	}
	
	public void closeConnection() throws IOException {
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