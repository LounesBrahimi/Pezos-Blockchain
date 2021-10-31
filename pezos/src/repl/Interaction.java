package repl;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.stream.Stream;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.DecoderException;

//import com.sun.tools.javac.util.ArrayUtils;

import blockchaine.Block;
import connection.Connection;
import operations.HachOfOperations;
import operations.ListOperations;
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
		System.out.println("Donnez le level souhaité : ");
	    int level = myObj.nextInt();
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communacation avec le serveur
        byte[] msg = util.to2BytesArray(3);
        msg = concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 3");
        byte[] blockAsBytes3 = util.getFromSocket(174,in,"block");
        return blockAsBytes3;
	}
	
	public byte[] tag5call(DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
		Scanner myObj = new Scanner(System.in);
		System.out.println("Donnez le level souhaité : ");
	    int level = myObj.nextInt();
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communacation avec le serveur
        byte[] msg = util.to2BytesArray(5);
        msg = concatTwoArrays(msg, levelBytes);
        util.sendToSocket(msg,out,"tag 5");
        
        return util.getFromSocket(10000,in,"block");
	}

	public byte[] tag9call(DataOutputStream out, DataInputStream  in, int ErrorTag) throws org.apache.commons.codec.DecoderException, IOException {
        switch(ErrorTag){
            case 1 : //BAD PREDECESSOR
            case 2 : //BAD TIMESTAMP
            case 3 : //BAD OPERATIONS HASH
            case 4 : //BAD CONTEXT HASH
            case 5 : //BAD SIGNATURE
            default : System.out.println("wrong error tag");
            return null;
        }
    }
	
	public byte[] tag7call(DataOutputStream out, DataInputStream  in) throws org.apache.commons.codec.DecoderException, IOException {
		Scanner myObj = new Scanner(System.in);
		System.out.println("Donnez le level souhaité : ");
	    int level = myObj.nextInt();
	    byte[] levelBytes = this.util.to4BytesArray(level);
	    
	    // communacation avec le serveur
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
	 
	 public void verifyPredecessorValue(int level, byte[] PredecessorInBlock, DataOutputStream out, DataInputStream  in) throws IOException, org.apache.commons.codec.DecoderException {
	        byte[] msg = util.to2BytesArray(3);
	        msg = concatTwoArrays(msg, util.to4BytesArray(level));
	        util.sendToSocket(msg,out,"tag 3");
	        byte[] blockAsBytes = util.getFromSocket(174,in,"block");
	        Block blockAsObjet = new Block(blockAsBytes);
	        System.out.println(Arrays.areEqual(PredecessorInBlock, blockAsObjet.getHashCurrentBlock()));
	 }
	 
	 public void verifyTimeStamp(int level, long timeStamp, DataOutputStream out, DataInputStream  in) throws IOException, org.apache.commons.codec.DecoderException {
	        byte[] msg = util.to2BytesArray(3);
	        msg = concatTwoArrays(msg, util.to4BytesArray(level));
	        util.sendToSocket(msg,out,"tag 3");
	        byte[] blockAsBytes = util.getFromSocket(174,in,"block");
	        Block blockAsObjet = new Block(blockAsBytes);
	        System.out.println((timeStamp - blockAsObjet.getTimeStamp())== 600);
	 }
	 
	 public void verifyHashOperations(int level, byte[] hashInBlock, DataOutputStream out, DataInputStream  in) throws IOException, org.apache.commons.codec.DecoderException {
	        byte[] msg = util.to2BytesArray(5);
	        msg = concatTwoArrays(msg, util.to4BytesArray(level));
	        util.sendToSocket(msg,out,"tag 5");
	        byte[] reponse = util.getFromSocket(1000,in,"ops");
	        ListOperations lop = new ListOperations();
	    	lop.extractAllOperations(reponse);
	    	HachOfOperations hashOps = new HachOfOperations(lop.getListOperations());
	    	byte[] hashDesOperations = hashOps.ops_hash();
	        System.out.println(Arrays.areEqual(hashInBlock, hashDesOperations));
	 }
}