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
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.google.common.io.BaseEncoding;

import blockchaine.Block;
import operations.HachOfOperations;
import operations.ListOperations;
import operations.Operation;
import repl.Interaction;
import state.Account;
import state.ListAccounts;
import state.State;
import tools.Utils;

//import com.google.common.io.BaseEncoding;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

//import org.bouncycastle.jce.provider.BouncyCastleProvider

/*
 * Communication with the server
 * */

public class Connection {
	
	private DataOutputStream out;
	private DataInputStream  in;
	
	public Connection(String hostname, int port, String skString, String pkString) throws UnknownHostException, IOException, DecoderException, DataLengthException, CryptoException, InterruptedException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {

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
		    	inter.verifyTimeStamp(blockAsObjet.getLevel()-1, blockAsObjet.getTimeStamp(), out, in);
		    	System.out.println(blockAsObjet);
		    	
		    	//---------------------------------
		    	inter.verifyStateHash(blockAsObjet.getLevel(), blockAsObjet.getStateHash(), out, in);
		    	//---------------------------------
		    }  else if (tag == 5) {
		    	ListOperations lop = new ListOperations();
		    	lop.extractAllOperations(reponse);
		    	HachOfOperations hashOps = new HachOfOperations(lop.getListOperations());
		    	byte[] hashDesOperations = hashOps.ops_hash();
		    	System.out.println("hash calculé : "+ util.toHexString(hashDesOperations));
		    }  else if (tag == 7) {
		    	State state = new State();
		    	state.extractState(reponse);
		    	System.out.println("hash calculé state "+util.toHexString(state.hashTheState()));

		    	ListAccounts lAccounts = new ListAccounts();
		    	lAccounts.extractAllAccounts(state.getAccountsBytes());
		    	//---------------------------------
				// "Verification de la signature marche ok !"
		    	byte[] blockSouh = inter.tag3call(out, in);
		    	Block block = new Block(blockSouh);
				byte[] hashBlock = util.hash(block.encodeBlockWithoutSignature(), 32);

		    	BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
		    	Signature signature2 = Signature.getInstance("Ed25519", bouncyCastleProvider);
		    	
		    	byte[] pubKeyBytes = state.getDictPK();
		    	SubjectPublicKeyInfo pubKeyInfo = new SubjectPublicKeyInfo(
		                new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519), pubKeyBytes);
		    	X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKeyInfo.getEncoded());
		    	KeyFactory keyFactory = KeyFactory.getInstance("Ed25519", bouncyCastleProvider);
		    	PublicKey pk = keyFactory.generatePublic(keySpec);
		    	signature2.initVerify(pk);
		        signature2.update(hashBlock);
		        System.out.println("======\nVérification signature : \n"+signature2.verify(block.getSignature())+"\n===========");
		    	//---------------------------------
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