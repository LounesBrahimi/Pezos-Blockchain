package operations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import tools.Utils;

public class Operation {
	private byte[] pubkey;
	private byte[] signature;
	private byte[] tagOperation;
	private long time;
	private byte[] hash;
	private Utils util;
	
	public Operation() {
		this.util = new Utils();
	}
	
	public void extractFirstOperation(byte[] receivedOperation){
		this.tagOperation = Arrays.copyOfRange(receivedOperation,4,6);
		this.pubkey = Arrays.copyOfRange(receivedOperation,6,38);
		this.signature = Arrays.copyOfRange(receivedOperation,38,102);
		if ((typeOfTag() == 1) || (typeOfTag() == 3) || (typeOfTag() == 4)) {
			this.hash = Arrays.copyOfRange(receivedOperation,102,134);
			System.out.println("tag : "+ util.toHexString(this.tagOperation));
			System.out.println("pubKey : "+ util.toHexString(this.pubkey));
			System.out.println("signature : "+ util.toHexString(this.signature));
			System.out.println("hash : "+ util.toHexString(this.hash));
		} else if (typeOfTag() == 2) {
			this.time = util.toLong(Arrays.copyOfRange(receivedOperation,102,110));
			System.out.println("tag : "+ util.toHexString(this.tagOperation));
			System.out.println("pubKey : "+ util.toHexString(this.pubkey));
			System.out.println("signature : "+ util.toHexString(this.signature));
			System.out.println("time : "+this.time+" seconds");
		} else if (typeOfTag() == 5) {
			System.out.println("tag : "+ util.toHexString(this.tagOperation));
			System.out.println("pubKey : "+ util.toHexString(this.pubkey));
			System.out.println("signature : "+ util.toHexString(this.signature));
		}
	}
	
	public void extractOperation(byte[] receivedOperation) {
		this.tagOperation = Arrays.copyOfRange(receivedOperation,0,2);
		this.pubkey = Arrays.copyOfRange(receivedOperation,2,34);
		this.signature = Arrays.copyOfRange(receivedOperation,34,98);
		if ((typeOfTag() == 1) || (typeOfTag() == 3) || (typeOfTag() == 4)) {
			this.hash = Arrays.copyOfRange(receivedOperation,98,130);
			System.out.println("tag : "+ util.toHexString(this.tagOperation));
			System.out.println("pubKey : "+ util.toHexString(this.pubkey));
			System.out.println("signature : "+ util.toHexString(this.signature));
			System.out.println("hash : "+ util.toHexString(this.hash));
		} else if (typeOfTag() == 2) {
			this.time = util.toLong(Arrays.copyOfRange(receivedOperation,98,106));
			System.out.println("tag : "+ util.toHexString(this.tagOperation));
			System.out.println("pubKey : "+ util.toHexString(this.pubkey));
			System.out.println("signature : "+ util.toHexString(this.signature));
			System.out.println("time : "+this.time+" seconds");
		} else if (typeOfTag() == 5) {
			System.out.println("tag : "+ util.toHexString(this.tagOperation));
			System.out.println("pubKey : "+ util.toHexString(this.pubkey));
			System.out.println("signature : "+ util.toHexString(this.signature));
		}
	}
	
	public int typeOfTag() {
		return this.tagOperation[1] ;
	}
	
	public byte[] getHash() {
		return this.hash;
	}
	
	public byte[] getTime() {
		return util.to8BytesArray(this.time);
	}
	
	public byte[] getContent() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(this.tagOperation);
		outputStream.write(this.pubkey);
		outputStream.write(this.signature);
		if ((typeOfTag() == 1) || (typeOfTag() == 3) || (typeOfTag() == 4)) {
			outputStream.write(this.tagOperation);
			return outputStream.toByteArray();
		} else if (typeOfTag() == 2) {
			outputStream.write(getTime());
			return outputStream.toByteArray();
		} else if (typeOfTag() == 5) {
			return outputStream.toByteArray();
		} else {
			return null;
		}
	}
}
