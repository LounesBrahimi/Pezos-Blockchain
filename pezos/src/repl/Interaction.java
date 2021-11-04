package repl;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.DecoderException;

import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import blockchaine.Block;
import operations.HachOfOperations;
import operations.ListOperations;
import state.State;
import tools.Utils;

public class Interaction {

	private Utils util;	
	
	public Interaction() {
		this.util = new Utils();
	}
	
	public byte[] tag3call(DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
		Scanner myObj = new Scanner(System.in);
		System.out.println("Donnez le level souhait� : ");
	    int level = myObj.nextInt();
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communication avec le serveur
        byte[] msg = util.to2BytesArray(3);
        msg = util.concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 3");
        byte[] blockAsBytes3 = util.getFromSocket(174,in,"block");
		myObj.close();
        return blockAsBytes3;
	}

	public byte[] tag3call(int level,DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communication avec le serveur
        byte[] msg = util.to2BytesArray(3);
        msg = util.concatTwoArrays(msg, levelBytes);
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
        msg = util.concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 5");
        myObj.close();
        return util.getFromSocket(10000,in,"block");
	}

	public byte[] tag5call(int level, DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communication avec le serveur
        byte[] msg = util.to2BytesArray(5);
        msg = util.concatTwoArrays(msg, levelBytes);
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
        msg = util.concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 7");
		myObj.close();
        return util.getFromSocket(1000,in,"block");
	}

	public byte[] tag7call(int level, DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communication avec le serveur
        byte[] msg = util.to2BytesArray(7);
        msg = util.concatTwoArrays(msg, levelBytes);
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
	
	public byte[] tag9Content(DataOutputStream out, int ErrorTag, byte[] correctedData) throws org.apache.commons.codec.DecoderException, IOException {
			// A TESTER
			//byte[] msg = util.to2BytesArray(9);
			byte[] msg = util.to2BytesArray(ErrorTag);
			msg = util.concatTwoArrays(msg, correctedData);
			return msg;
		}

	//Version pour la signature
	public byte[] tag9ContentSign(DataOutputStream out, int ErrorTag){
		//byte[] msg = util.to2BytesArray(9);
		byte [] msg = util.to2BytesArray(ErrorTag);
		return msg;
	}

	public void tag9Call(byte[] content, String pk, String sk, DataOutputStream out) throws DataLengthException, org.apache.commons.codec.DecoderException, CryptoException, IOException{
		byte[] pkBytes = util.toBytesArray(pk);

		// Création de la signature
		byte[] signature = util.signature(util.hash(util.concatTwoArrays(content, pkBytes),32), sk);

		// Ajout de la clé publique
		content = util.concatTwoArrays(content,pkBytes);

		// ajout de la signature
		content = util.concatTwoArrays(content, signature);
		content = util.concatTwoArrays(util.to2BytesArray(9), content);

		//envoi du message "Content+publicKey+Signature"
		util.sendToSocket(content, out);
	}


	public void verifyErrors( Block block, DataOutputStream out, DataInputStream in, String pk, String sk) throws IOException, org.apache.commons.codec.DecoderException, InvalidKeyException, SignatureException, InvalidKeySpecException, NoSuchAlgorithmException, DataLengthException, CryptoException{
		byte[] operationContent = null;
		//Predecessor
		Block predecessor = new Block(tag3call(block.getLevel()-1, out, in));

		//Etat
		byte [] currentState = tag7call(block.getLevel(),out, in);
		State state = new State();
		state.extractState(currentState);
		
		//TimeStamp
		byte[] correctPredecessorTimestamp = state.getPredecessorTimestamp();
		long differenceTimestampsInSeconds = util.toLong(block.getTimeStampBytes())-util.toLong(correctPredecessorTimestamp);
		System.out.println("difference timestamp : "+differenceTimestampsInSeconds);

		//Operations
		ListOperations lop = new ListOperations();
	    lop.extractAllOperations(tag5call(block.getLevel(),out, in));
	    HachOfOperations hashOps = new HachOfOperations(lop.getListOperations());
	    byte[] hashDesOperations = hashOps.ops_hash();
		
		//VerifPred
		if(!Arrays.areEqual(block.getPredecessor(), predecessor.getHashCurrentBlock())){
			System.out.println("======\n #Verification Predecessor :# false \n======");
			operationContent = tag9Content(out, 1, predecessor.getHashCurrentBlock());
		}
		//VerifTimeStamp
		if(differenceTimestampsInSeconds != 600){
			System.out.println("======\n #Verification TimeStamp :# false \n======");
			long correctedTimestamp = util.toLong(correctPredecessorTimestamp) + 600;
			operationContent = tag9Content(out, 2, util.to8BytesArray(correctedTimestamp));
		}
		//VerifOperations
		if(!Arrays.areEqual(block.getOperationsHash(), hashDesOperations)){
			System.out.println("======\n#Verification Operations : # false \n======");
			operationContent = tag9Content(out, 3, hashDesOperations);
		}
		//VerifState
		if(!Arrays.areEqual(state.hashTheState(), block.getStateHash())){
			System.out.println("======\n#Verification State : # false \n======");
			operationContent = tag9Content(out, 4,state.hashTheState());
		}
		//verifSignature ne marche que la première fois
		if(!verifySignature(block,state,out, in)) {
			System.out.println("======\nVerification signature : \n false \n===========");
			operationContent = tag9ContentSign(out, 5);
		}

		//Si on a trouvé une erreur, on envoie le tag 9 de correction
		if(operationContent != null){
			System.out.println("oui");
		    tag9Call(operationContent, pk, sk, out);
		} else {
			System.err.println("no error on this block");
		}

		System.out.println("My account = "+state.getAccount("b8b606dba2410e1f3c3486e0d548a3053ba3f907860fada6fab2835fb27b3f21").toString());
	 }

	public boolean verifySignature(Block block, State state, DataOutputStream out, DataInputStream in) throws InvalidKeyException, SignatureException, InvalidKeySpecException, NoSuchAlgorithmException, IOException, org.apache.commons.codec.DecoderException{
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