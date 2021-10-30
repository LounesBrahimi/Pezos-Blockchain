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
	private long   timestamp; 
	private byte[] operationsHash;
	private byte[] stateHash;
	private byte[] signature;
	private Utils util;
	
	public Block(int level, byte[] predecessor, long timestamp, byte[] operationsHash, byte[] stateHash, byte[] signature) {
		this.util = new Utils();
		this.level          = level;
		this.predecessor    = predecessor;
		this.timestamp      = timestamp;
		this.operationsHash = operationsHash;
		this.stateHash      = stateHash;
		this.signature      = signature;
	}
	
	public Block(int level, String predecessor, String dateAsString, String operationsHash, String stateHash, String signature) throws DecoderException, ParseException {
		this.util = new Utils();
		this.level          = level;
		this.predecessor    = util.toBytesArray(predecessor); 
		this.timestamp      = util.toDateAsSeconds(dateAsString);
		this.operationsHash = util.toBytesArray(operationsHash);
		this.stateHash      = util.toBytesArray(stateHash);
		this.signature      = util.toBytesArray(signature);
	}
	
	/*
	 * Constuit un block depuis le message reçu
	 * */
	public Block(byte[] receivedMessage) { 
		this.util = new Utils();
        this.level          = util.toInt(Arrays.copyOfRange(receivedMessage,2,6)); 
        this.predecessor    = Arrays.copyOfRange(receivedMessage,6,38); 
        this.timestamp      = util.toLong(Arrays.copyOfRange(receivedMessage,38,46));
        this.operationsHash = Arrays.copyOfRange(receivedMessage,46,78);
        this.stateHash      = Arrays.copyOfRange(receivedMessage,78,110);
        this.signature      = Arrays.copyOfRange(receivedMessage,110,174);
    }
	
	/*
	 * Encode le block structuré par les différent attributs en une suite de bytes
	 * */
	public byte[] encodeToBytes() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(util.to4BytesArray(level));
		outputStream.write(predecessor); 
		outputStream.write(util.to8BytesArray(timestamp));
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
					 "\n  timestamp:       "+(util.toDateAsString(timestamp)+" (="+timestamp+" sec)")+
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
		return this.timestamp;
	}
}