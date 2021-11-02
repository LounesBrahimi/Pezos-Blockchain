package repl;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.stream.Stream;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.DecoderException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.net.InetAddress;

import com.google.common.io.BaseEncoding;

import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import blockchaine.Block;
import connection.Connection;
import operations.HachOfOperations;
import operations.ListOperations;
import state.State;
import tools.Utils;

public class Interaction {

	private Utils util;	
	
	public Interaction() {
		this.util = new Utils();
	}
	
	public byte[] concatTwoArrays(byte[] a, byte[] b) {
		int sizeA = a.length;
		int sizeB = b.length;
		byte[] res = new byte[sizeA + sizeB];
		for (int i = 0; i < a.length; i++) {
			res[i] = a[i];
		}
		for (int i = a.length, j = 0; j < b.length && i  < res.length; i++, j++) {
			res[i] = b[j];
		}
		return res;
	}
	
	public byte[] tag3call(DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
		Scanner myObj = new Scanner(System.in);
		System.out.println("Donnez le level souhait� : ");
	    int level = myObj.nextInt();
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communication avec le serveur
        byte[] msg = util.to2BytesArray(3);
        msg = concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 3");
        byte[] blockAsBytes3 = util.getFromSocket(174,in,"block");
        return blockAsBytes3;
	}

	public byte[] tag3call(int level,DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communication avec le serveur
        byte[] msg = util.to2BytesArray(3);
        msg = concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 3");
        byte[] blockAsBytes3 = util.getFromSocket(174,in,"block");
        return blockAsBytes3;
	}
	
	public byte[] tag5call(DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
		Scanner myObj = new Scanner(System.in);
		System.out.println("Donnez le level souhait� : ");
	    int level = myObj.nextInt();
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communication avec le serveur
        byte[] msg = util.to2BytesArray(5);
        msg = concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 5");
        
        return util.getFromSocket(10000,in,"block");
	}

	public byte[] tag5call(int level, DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communication avec le serveur
        byte[] msg = util.to2BytesArray(5);
        msg = concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 5");
        
        return util.getFromSocket(10000,in,"block");
	}
	
	public byte[] tag7call(DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
		Scanner myObj = new Scanner(System.in);
		System.out.println("Donnez le level souhait� : ");
	    int level = myObj.nextInt();
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communication avec le serveur
        byte[] msg = util.to2BytesArray(7);
        msg = concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 7");
        return util.getFromSocket(1000,in,"block");
	}

	public byte[] tag7call(int level, DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communication avec le serveur
        byte[] msg = util.to2BytesArray(7);
        msg = concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 7");
        return util.getFromSocket(1000,in,"block");
	}
	
	 public byte[] tagCall (int tag, DataOutputStream out, DataInputStream  in) throws IOException, DecoderException, org.apache.commons.codec.DecoderException{
			switch(tag){
	            case 1 :
	                byte[] msg = util.to2BytesArray(1);
	                util.sendToSocket(msg,out,"tag 1");
	                byte[] blockAsBytes = util.getFromSocket(174,in,"block");
	                return blockAsBytes;
	            case 3 :
	            	return tag3call(out, in);
	            case 5 :
	            	return tag5call(out, in);
	            case 7 :
	            	return tag7call(out, in);
	            default : System.out.println("error, wrong tag");
	            return null;
	        }
	    }
	 

		//Vérifications
	
		public void tag9call(DataOutputStream out, int ErrorTag, byte[] wrongData) throws org.apache.commons.codec.DecoderException, IOException {
			// A TESTER
			byte[] msg = util.to2BytesArray(9);
			msg = concatTwoArrays(msg, util.to2BytesArray(ErrorTag));
			msg = concatTwoArrays(msg, wrongData);
			util.sendToSocket(msg, out);
			System.out.println("message d'erreur envoyé");
			/* Pas besoin?
			switch(ErrorTag){
				case 1 : //BAD PREDECESSOR 
				// A TESTER
					msg = concatTwoArrays(msg, util.to2BytesArray(ErrorTag));
					msg = concatTwoArrays(msg, wrongData);
					util.sendToSocket(msg, out);
					break;
				case 2 : //BAD TIMESTAMP
				// A TESTER
					msg = concatTwoArrays(msg, util.to2BytesArray(ErrorTag));
					msg = concatTwoArrays(msg, wrongData);
					util.sendToSocket(msg, out);
					break;
				case 3 : //BAD OPERATIONS HASH
				// A TESTER
					msg = concatTwoArrays(msg, util.to2BytesArray(errorTag));
					msg = concatTwoArrays(msg, wrongData);
				case 4 : //BAD CONTEXT HASH
				case 5 : //BAD SIGNATURE
				default : System.out.println("wrong error tag");
			}*/
		}


	 public void verifyErrors( Block block, DataOutputStream out, DataInputStream in) throws IOException, org.apache.commons.codec.DecoderException, InvalidKeyException, SignatureException, InvalidKeySpecException, NoSuchAlgorithmException{
		 verifyPredecessorValue(block.getLevel(), block.getPredecessor(), out, in);
		 verifyTimeStamp(block.getLevel(), block.getTimeStamp(), out, in);
		 verifyHashOperations(block.getLevel(),block.getOperationsHash() , out, in);
		 verifyStateHash(block.getLevel(), block.getOperationsHash(), out, in);
		 verifySignature(block,out, in);
	 }
	 public void verifyPredecessorValue(int level, byte[] PredecessorInBlock, DataOutputStream out, DataInputStream  in) throws IOException, org.apache.commons.codec.DecoderException {
	        
	        byte[] blockAsBytes = tag3call(level-1, out, in);
	        Block blockAsObjet = new Block(blockAsBytes);
	        System.out.println("#Verification Predecessor :#" +Arrays.areEqual(PredecessorInBlock, blockAsObjet.getHashCurrentBlock()));
			//appel au tag 9 de correction
			/*if(!(Arrays.areEqual(PredecessorInBlock, blockAsObjet.getHashCurrentBlock()))){
				System.out.println("error found at predecessor");
				tag9call(out, 1, PredecessorInBlock);
			}*/
	 }
	 
	 public void verifyTimeStamp(int level, long timeStamp, DataOutputStream out, DataInputStream  in) throws IOException, org.apache.commons.codec.DecoderException {
	        byte[] blockAsBytes = tag3call(level-1,out, in);
	        Block blockAsObjet = new Block(blockAsBytes);
	        System.out.println("#Verification TimeStamp : #" + ((timeStamp - blockAsObjet.getTimeStamp())== 600));

			//appel au tag 9 de correction
			/*if((timeStamp - blockAsObjet.getTimeStamp()) == 600){
				System.out.println("no error on timestamp");
			}
			else {
				System.out.println("error on timestamp");
				tag9call(out, 2, util.to8BytesArray(timeStamp));
			}*/
	 }
	 
	 public void verifyHashOperations(int level, byte[] hashInBlock, DataOutputStream out, DataInputStream  in) throws IOException, org.apache.commons.codec.DecoderException {
	        byte[] msg = util.to2BytesArray(5);
	        msg = concatTwoArrays(msg, util.to4BytesArray(level));
	        util.sendToSocket(msg,out,"tag 5");
	        
	        byte[] reponse = util.getFromSocket(1000,in,"ops");
	      //  byte[] reponse = tag5call(out, in);
	        ListOperations lop = new ListOperations();
	    	lop.extractAllOperations(reponse);
	    	HachOfOperations hashOps = new HachOfOperations(lop.getListOperations());
	    	byte[] hashDesOperations = hashOps.ops_hash();
	        System.out.println("#Verification Operations : # "+ Arrays.areEqual(hashInBlock, hashDesOperations));

			//appel au tag 9 de correction
			/*if(Arrays.areEqual(hashInBlock, hashDesOperations)){
				System.out.println("no error on operations hash");
			}
			else{
				System.out.println("error on operations hash");
				tag9call(out, 3, hashInBlock);
			}*/
	 }
	 
	 public void verifyStateHash(int level, byte[] hashStateInBlock,  DataOutputStream out, DataInputStream  in) throws IOException, org.apache.commons.codec.DecoderException {
	        byte[] reponse = tag7call(level, out, in);
	        State state = new State();
	        state.extractState(reponse);
	        System.out.println("#Verification State : # "+ Arrays.areEqual(state.hashTheState(), hashStateInBlock));
			/*if(!Arrays.areEqual(state.hashTheState(), hashStateInBlock)){
				System.out.println("appel tag 9");
				tag9call(out, 4, hashStateInBlock);
			}*/
	 }

	 public void verifySignature(Block block, DataOutputStream out, DataInputStream in) throws InvalidKeyException, SignatureException, InvalidKeySpecException, NoSuchAlgorithmException, IOException, org.apache.commons.codec.DecoderException{

		State state = new State();
		state.extractState(tag7call(block.getLevel(), out, in));
		byte[] hashBlock = util.hash(block.encodeBlockWithoutSignature(), 32);
		BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
		Signature signature2 = Signature.getInstance("Ed25519", bouncyCastleProvider);
		
		byte[] pubKeyBytes = state.getDictPK();
		SubjectPublicKeyInfo pubKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519), pubKeyBytes);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKeyInfo.getEncoded());
		KeyFactory keyFactory = KeyFactory.getInstance("Ed25519", bouncyCastleProvider);
		PublicKey pk = keyFactory.generatePublic(keySpec);
		signature2.initVerify(pk);
		signature2.update(hashBlock);
		System.out.println("======\nVerification signature : \n"+signature2.verify(block.getSignature())+"\n===========");
	 }
}