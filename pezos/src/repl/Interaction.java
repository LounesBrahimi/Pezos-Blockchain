package repl;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
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

/*
 * Class permettant l'interaction avec l'utilisateur
 * */
public class Interaction {

	private Utils util;	
	private int tempsCorrect;
	
	public Interaction(int tempsCorrect) {
		this.util = new Utils();
		this.tempsCorrect = tempsCorrect;
	}
	
	/*
	 * Methode permettant d'envoyer un tag particuliere au serveur, de recevoir
	 * la reponse et de la retournee
	 * */
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


	/*
	 * Methode qui detecte l'erreur du block et envoie un message au serveur pour la correction
	 * */
	public void verifyErrors( Block block, DataOutputStream out, DataInputStream in, String pk, String sk) throws IOException, org.apache.commons.codec.DecoderException, InvalidKeyException, SignatureException, InvalidKeySpecException, NoSuchAlgorithmException, DataLengthException, CryptoException{
		byte[] operationContent = null;
		Block predecessor = new Block(tag3call(block.getLevel()-1, out, in));
		
		//Etat
		byte [] currentState = tag7call(block.getLevel(),out, in);
		State state = new State();
		state.extractState(currentState);

		//TimeStamp
		byte[] correctPredecessorTimestamp = state.getPredecessorTimestamp();
		long differenceTimestampsInSeconds = util.toLong(block.getTimeStampBytes())-util.toLong(correctPredecessorTimestamp);
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
		if(differenceTimestampsInSeconds != tempsCorrect){
			System.out.println("======\n #Verification TimeStamp :# false \n======");
			long correctedTimeStamp = util.toLong(correctPredecessorTimestamp) + tempsCorrect;
			operationContent = tag9Content(out, 2, util.to8BytesArray(correctedTimeStamp));
			block.setTimeStamp(util.to8BytesArray(correctedTimeStamp));
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
		//verifSignature ne marche que la premi??re fois
		if(!verifySignature(block,state,out, in)) {
			System.out.println("======\nVerification signature : \n false \n===========");
			operationContent = tag9ContentSign(out, 5);
		}

		//Si on a trouv?? une erreur, on envoie le tag 9 de correction
		if(operationContent != null){
		    tag9Call(operationContent, pk, sk, out);
		} else {
			System.err.println("no error on this block");
		}

		//affichage de notre ??tat
		System.out.println("My account = "+state.getAccount("b8b606dba2410e1f3c3486e0d548a3053ba3f907860fada6fab2835fb27b3f21").toString());
	 }
	
	/*
	 * Methode permettant de faire l'appel du tag 3 avec une interaction avec l'utilisateur pour detecter le level
	 * */
	public byte[] tag3call(DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
		Scanner myObj = new Scanner(System.in);
		System.out.println("Donnez le level souhaitee : ");
	    int level = myObj.nextInt();
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communication avec le serveur
        byte[] msg = util.to2BytesArray(3);
        msg = concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 3");
        byte[] blockAsBytes3 = util.getFromSocket(174,in,"block");
		myObj.close();
        return blockAsBytes3;
	}

	/*
	 * Methode permettant de faire l'appel du tag 3 avec le level donnee en parametre
	 * */
	public byte[] tag3call(int level,DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
	    byte[] levelBytes = util.to4BytesArray(level);
	    
	    // communication avec le serveur
        byte[] msg = util.to2BytesArray(3);
        msg = concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 3");
        byte[] blockAsBytes3 = util.getFromSocket(174,in,"block");
        return blockAsBytes3;
	}
	
	/*
	 * Methode permettant de faire l'appel du tag 5 avec une interaction avec l'utilisateur pour detecter le level
	 * */
	public byte[] tag5call(DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
		Scanner myObj = new Scanner(System.in);
		System.out.println("Donnez le level souhait??? : ");
	    int level = myObj.nextInt();
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communication avec le serveur
        byte[] msg = util.to2BytesArray(5);
        msg = concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 5");

		myObj.close();

		 //extraction des 4 premiers bytes reponse (le tag et la taille des operations)
		byte[] tag = util.getFromSocket(2,in,"tag retour 6");
		byte[] tailleOperations = util.getFromSocket(2, in, "taille des op??rations du bloc souhait??");
		int tailleOP = new BigInteger(tailleOperations).intValue();
 
		 //retour de la valeur
		 return util.getFromSocket(tailleOP,in,"operations");
	}

	/*
	 * Methode permettant de faire l'appel du tag 5 avec le level donnee en parametre
	 * */
	public byte[] tag5call(int level, DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communication avec le serveur
        byte[] msg = util.to2BytesArray(5);
        msg = concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 5");

		byte[] tag = util.getFromSocket(2,in,"tag retour 6");
		byte[] tailleOperations = util.getFromSocket(2, in, "taille des op??rations du bloc souhait??");
		int tailleOP = new BigInteger(tailleOperations).intValue();
 
		 //retour de la valeur
		return util.getFromSocket(tailleOP,in,"operations");
	}
	
	/*
	 * Methode permettant de faire l'appel du tag 7 avec une interaction avec l'utilisateur pour detecter le level
	 * */
	public byte[] tag7call(DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
		Scanner myObj = new Scanner(System.in);
		System.out.println("Donnez le level souhaitee : ");
	    int level = myObj.nextInt();
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communication avec le serveur
        byte[] msg = util.to2BytesArray(7);
        msg = concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 7");
		myObj.close();

		
		//extraction des premiers bytes reponse (le tag, clee publique du Dictateur, timestamp du predecesseur, et la taille de la s??quence d'??tat)
		byte[] infos = util.getFromSocket(42, in, "tag+dictatorKey+predTimeStamp");

		//on recupere la taille s??par??ment pour extraire la taille qu'il nous faut
        byte[] tailleAccounts = util.getFromSocket(4, in, "taille de la sequence des comptes");
		infos = concatTwoArrays(infos, tailleAccounts);

        int tailleSequenceComptes = new BigInteger(tailleAccounts).intValue();

        System.out.println("taille : "+tailleSequenceComptes);
        return concatTwoArrays(infos, util.getFromSocket(tailleSequenceComptes,in,"accounts"));
	}

	/*
	 * Methode permettant de faire l'appel du tag 7 avec le level donnee en parametre
	 * */
	public byte[] tag7call(int level, DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communication avec le serveur
        byte[] msg = util.to2BytesArray(7);
        msg = concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 7");

		//extraction des premiers bytes r??ponse (le tag, cl?? publique du Dictateur, timestamp du pr??d??cesseur, et la taille de la s??quence d'??tat)
		byte[] infos = util.getFromSocket(42, in, "tag+dictatorKey+predTimeStamp");

		//on r??cup??re la taille s??par??ment pour extraire la taille qu'il nous faut
        byte[] tailleAccounts = util.getFromSocket(4, in, "taille de la s??quence des comptes");
		infos = concatTwoArrays(infos, tailleAccounts);

        int tailleSequenceComptes = new BigInteger(tailleAccounts).intValue();

        System.out.println("taille : "+tailleSequenceComptes);
        return concatTwoArrays(infos, util.getFromSocket(tailleSequenceComptes,in,"accounts"));
	}
	
	/*
	 * Methode permettant d'effectuer l'envoie du tag 9 pour la correction
	 */
	public void tag9Call(byte[] content, String pk, String sk, DataOutputStream out) throws DataLengthException, org.apache.commons.codec.DecoderException, CryptoException, IOException{
		byte[] pkBytes = util.toBytesArray(pk);

		// Creation de la signature
		byte[] signature = util.signature(util.hash(concatTwoArrays(content, pkBytes),32), sk);

		// Ajout de la clee publique
		content = concatTwoArrays(content,pkBytes);

		// ajout de la signature
		content = concatTwoArrays(content, signature);
		content = concatTwoArrays(util.to2BytesArray(9), content);

		//envoi du message "Content+publicKey+Signature"
		util.sendToSocket(content, out);
	}
	
	/*
	 * Methode permettant de verifier la signature
	 * */
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
	
	/*
	 *  renvoie le contenue d'un message pour envoyer une operation de type (1,2,3,4) avec le tag 9
	 */
	public byte[] tag9Content(DataOutputStream out, int ErrorTag, byte[] correctedData) throws org.apache.commons.codec.DecoderException, IOException {
			byte[] msg = util.to2BytesArray(ErrorTag);
			msg = concatTwoArrays(msg, correctedData);
			return msg;
		}

	/*
	 *  renvoie le contenue d'un message pour envoyer une operation signature avec le tag9
	 */
	public byte[] tag9ContentSign(DataOutputStream out, int ErrorTag){
		byte [] msg = util.to2BytesArray(ErrorTag);
		return msg;
	}
	
	/*
	 * Methode permettant de concateneee deux tableaux de bytes
	 * */
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
}