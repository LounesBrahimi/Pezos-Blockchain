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
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
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
	
	public byte[] tag9Content(DataOutputStream out, int ErrorTag, byte[] wrongData) throws org.apache.commons.codec.DecoderException, IOException {
			// A TESTER
			byte[] msg = util.to2BytesArray(9);
			msg = concatTwoArrays(msg, util.to2BytesArray(ErrorTag));
			msg = concatTwoArrays(msg, wrongData);
			return msg;
		}
	//Version pour la signature
	public byte[] tag9ContentSign(DataOutputStream out, int ErrorTag){
		byte[] msg = util.to2BytesArray(9);
		msg = concatTwoArrays(msg, util.to2BytesArray(ErrorTag));
		return msg;
	}

	public void tag9Call(byte[] content, String pk, String sk, DataOutputStream out) throws DataLengthException, org.apache.commons.codec.DecoderException, CryptoException, IOException{
		byte[] msg = content;
		byte[] pkBytes = util.toBytesArray(pk);

		// Ajout de la clé publique
		content = concatTwoArrays(content,pkBytes);
		// Création de la signature
		byte[] signature = util.signature(concatTwoArrays(content, pkBytes), sk);
		// ajout de la signature
		concatTwoArrays(content, signature);
		//envoi du message "Content+publicKey+Signature"
		util.sendToSocket(content, out);
	}


	public void verifyErrors( Block block, DataOutputStream out, DataInputStream in, String pk, String sk) throws IOException, org.apache.commons.codec.DecoderException, InvalidKeyException, SignatureException, InvalidKeySpecException, NoSuchAlgorithmException, DataLengthException, CryptoException{
		byte[] operationContent = null;
		if(!verifyPredecessorValue(block.getLevel(), block.getPredecessor(), out, in)){
			operationContent = tag9Content(out, 1, block.getPredecessor());
		}
		else if(!verifyTimeStamp(block.getLevel(), block.getTimeStampBytes(), out, in)){
		operationContent = tag9Content(out, 2, block.getTimeStampBytes());
		}
		else if(!verifyHashOperations(block.getLevel(),block.getOperationsHash() , out, in)){
		operationContent = tag9Content(out, 3, block.getOperationsHash());
		}
		 //verifyStateHash(block.getLevel(), block.getOperationsHash(), out, in);
		else if(!verifySignature(block,out, in)) {
			System.out.println("======\nVerification signature : \n false \n===========");
			operationContent = tag9ContentSign(out, 5);
		}

		if(operationContent != null){
		tag9Call(operationContent, pk, sk, out);
		} else {
			System.err.println("no error on this block");
		}
	 }
	 public boolean verifyPredecessorValue(int level, byte[] PredecessorInBlock, DataOutputStream out, DataInputStream  in) throws IOException, org.apache.commons.codec.DecoderException {
	        
	        byte[] blockAsBytes = tag3call(level-1, out, in);
	        Block blockAsObjet = new Block(blockAsBytes);
	        System.out.println("======\n #Verification Predecessor :#" +Arrays.areEqual(PredecessorInBlock, blockAsObjet.getHashCurrentBlock()));
			return Arrays.areEqual(PredecessorInBlock, blockAsObjet.getHashCurrentBlock());
	 }
	 
	 public boolean verifyTimeStamp(int level, byte[] timestampToVerify, DataOutputStream out, DataInputStream  in) throws IOException, org.apache.commons.codec.DecoderException {
		byte[] receivedMessage = tag7call(level,out,in);
		//System.out.println("receivedMessage="+util.toHexString(receivedMessage));
		// if(tag==2) c'est le broadcast regulier, traiter le block broadcasté et lire le message suivant à tag 8)

		State state = new State();
		state.extractState(receivedMessage);
		byte[] correctPredecessorTimestamp = state.getPredecessorTimestamp();
		//System.out.println("correctPredecessorTimestamp="+util.toLong(correctPredecessorTimestamp));

		long diffenenceTimestampsInSeconds = util.toLong(timestampToVerify)-util.toLong(correctPredecessorTimestamp);//(predecessorTimestampToVerify-correctPredecessorTimestamp) >= 600;
		//System.out.println("diffenenceTimestampsInSeconds="+diffenenceTimestampsInSeconds);

		if(diffenenceTimestampsInSeconds>=600){
			// cf. anoncé : pour être valide, le temps du block doit être au moins espacé de 10 minutes par rapport au bloc précédent
			System.out.println("======\n #Verification Predecessor :# True");
			return true;
			}
		else {
			System.out.println("======\n #Verification Predecessor :# False");
			return false;
		}
   }
	 
	 public boolean verifyHashOperations(int level, byte[] hashInBlock, DataOutputStream out, DataInputStream  in) throws IOException, org.apache.commons.codec.DecoderException {
	        byte[] msg = util.to2BytesArray(5);
	        msg = concatTwoArrays(msg, util.to4BytesArray(level));
	        util.sendToSocket(msg,out,"tag 5");
	        
	        byte[] reponse = util.getFromSocket(1000,in,"ops");
	      //  byte[] reponse = tag5call(out, in);
	        ListOperations lop = new ListOperations();
	    	lop.extractAllOperations(reponse);
	    	HachOfOperations hashOps = new HachOfOperations(lop.getListOperations());
	    	byte[] hashDesOperations = hashOps.ops_hash();
	        System.out.println("======\n#Verification Operations : # "+ Arrays.areEqual(hashInBlock, hashDesOperations));
			return Arrays.areEqual(hashInBlock, hashDesOperations);
	 }
	 
	 public boolean verifyStateHash(int level, byte[] hashStateInBlock,  DataOutputStream out, DataInputStream  in) throws IOException, org.apache.commons.codec.DecoderException {
	        byte[] reponse = tag7call(level, out, in);
	        State state = new State();
	        state.extractState(reponse);
	        System.out.println("======\n #Verification State : # "+ Arrays.areEqual(state.hashTheState(), hashStateInBlock));
			return Arrays.areEqual(state.hashTheState(), hashStateInBlock);

	 }

	 public boolean verifySignature(Block block, DataOutputStream out, DataInputStream in) throws InvalidKeyException, SignatureException, InvalidKeySpecException, NoSuchAlgorithmException, IOException, org.apache.commons.codec.DecoderException{

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
		return signature2.verify(block.getSignature());
	 }
}