package blockchaine;
import tools.Utils;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
public class Block {

	private int    level; 
	private byte[] predecessor;
	private byte[] timestamp; 
	private byte[] operationsHash;
	private byte[] stateHash;
	private byte[] signature;
	private byte[] hashCurrentBlock;
	//private byte[] receivedMessage; // tmp
	private Utils util;
	
	public byte[] encodeBlockWithoutSignature() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(util.to4BytesArray(level));
		outputStream.write(predecessor); 
		outputStream.write(timestamp);
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
        this.hashCurrentBlock = util.hash(this.encodeToBytes(),32);
        //this.receivedMessage = Arrays.copyOfRange(receivedMessage,0,174);
    }
	
	/*
	 * Encode le block structur� par les diff�rent attributs en une suite de bytes
	 * */
	public byte[] encodeToBytes() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(util.to4BytesArray(level));
		outputStream.write(predecessor); 
		outputStream.write(timestamp);
		outputStream.write(operationsHash);
		outputStream.write(stateHash);
		outputStream.write(signature);
		return outputStream.toByteArray();
	}

	public String toString() {
			try {
				return "BLOCK:"+
					 "\n  level:            "+level+ " (or "+util.toStringOfHex(level) +" as Hex)"+
					 "\n  predecessor:      "+util.toHexString(predecessor)+
					 "\n  timestamp:        "+(util.toDateAsString(util.toLong(timestamp))+" (or "+util.toLong(timestamp)+" seconds, or "+util.toHexString(timestamp)+" as Hex)")+
					 "\n  operations hash:  "+util.toHexString(operationsHash)+
					 "\n  state hash:       "+util.toHexString(stateHash)+
					 "\n  signature:        "+util.toHexString(signature)+
					 "\n  encoded block:    "+util.toHexString(this.encodeToBytes())+
					 "\n  hash of the block:"+util.toHexString(hashCurrentBlock);
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

	public void setTimeStamp(byte[] newTimeStamp){
		this.timestamp = newTimeStamp;
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
	
	public byte[] getSignature() {
		return this.signature;
	}
}