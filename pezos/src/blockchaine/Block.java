package blockchaine;

import tools.Utils;
import java.text.ParseException;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class Block {

	private int    level; 
	private byte[] predecessor;
	private byte[]   timestamp; 
	private byte[] operationsHash;
	private byte[] stateHash;
	private byte[] signature;
	private byte[] hashCurrentBlock;
	private Utils util;
	private byte[] receivedMessage;
	
	public byte[] getSignature() {
		return this.signature;
	}
	
	public byte[] encodeBlockWithoutSignature() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(util.to4BytesArray(level));
		outputStream.write(predecessor); 
		outputStream.write(util.to8BytesArray(util.toLong(timestamp)));
		outputStream.write(operationsHash);
		outputStream.write(stateHash);
		return outputStream.toByteArray();
	}
	
	
	/*
	 * Constuit un block depuis le message re�u
	 * */
	public Block(byte[] receivedMessage) throws IOException { 
		this.util = new Utils();
        this.level          = util.toInt(Arrays.copyOfRange(receivedMessage,2,6)); 
        this.predecessor    = Arrays.copyOfRange(receivedMessage,6,38); 
        this.timestamp      = Arrays.copyOfRange(receivedMessage,38,46);
        this.operationsHash = Arrays.copyOfRange(receivedMessage,46,78);
        this.stateHash      = Arrays.copyOfRange(receivedMessage,78,110);
        this.signature      = Arrays.copyOfRange(receivedMessage,110,174);
        this.hashCurrentBlock = util.hash(this.encodeToBytes(), 32);
        this.receivedMessage = receivedMessage;
    }
	
	/*
	 * Encode le block structur� par les diff�rent attributs en une suite de bytes
	 * */
	public byte[] encodeToBytes() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(util.to4BytesArray(level));
		outputStream.write(predecessor); 
		outputStream.write(util.to8BytesArray(util.toLong(timestamp)));
		outputStream.write(operationsHash);
		outputStream.write(stateHash);
		outputStream.write(signature);
		return outputStream.toByteArray();
	}

	public String toString() {
			try {
				return "BLOCK:"+
					 "\n  level:           "+level+ " (or "+util.toStringOfHex(level) +" as Hex)"+
					 "\n  predecessor:     "+util.toHexString(predecessor)+
					 "\n  timestamp:       "+(util.toDateAsString(util.toLong(timestamp))+" (="+timestamp+" sec)")+
					 "\n  operations hash: "+util.toHexString(operationsHash)+
					 "\n  state hash:      "+util.toHexString(stateHash)+
					 "\n  signature:       "+util.toHexString(signature)+
					 "\n  encoded block:   "+util.toHexString(this.encodeToBytes())+
					 "\n  le has du block:   "+util.toHexString(util.hash(this.encodeToBytes(), 32));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	public byte[] getPredecessor() {
		return this.predecessor;
	}
	
	public long getTimeStamp() {
		return util.toLong(timestamp);
	}

	public byte[] getTimeStampBytes(){
		return this.timestamp;
	}
	
	public byte[] getHashCurrentBlock() {
		return this.hashCurrentBlock;
	}

	public byte[] getOperationsHash(){
		return this.operationsHash;
	}
	
	public byte[] getStateHash() {
		return this.stateHash;
	}
}