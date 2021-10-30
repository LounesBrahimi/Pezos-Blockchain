import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import org.apache.commons.codec.DecoderException;

public class Block {
	int    level=0; 
	byte[] predecessor = null;
	long   timestamp=0; 
	byte[] operationsHash = null;
	byte[] stateHash = null;
	byte[] signature = null;
	
	public Block(int level, byte[] predecessor, long timestamp, byte[] operationsHash, byte[] stateHash, byte[] signature) {
		this.level          = level;
		this.predecessor    = predecessor; // Array copy?
		this.timestamp      = timestamp;
		this.operationsHash = operationsHash;
		this.stateHash      = stateHash;
		this.signature      = signature;
	}
	
	public Block(int level, String predecessor, String dateAsString, String operationsHash, String stateHash, String signature) throws DecoderException, ParseException {
		this.level          = level;
		this.predecessor    = Utils.toBytesArray(predecessor); 
		this.timestamp      = Utils.toDateAsSeconds(dateAsString);
		this.operationsHash = Utils.toBytesArray(operationsHash);
		this.stateHash      = Utils.toBytesArray(stateHash);
		this.signature      = Utils.toBytesArray(signature);
	}

	public Block(byte[] receivedMessage) { 
		if(receivedMessage.length==174) {
			this.level          = Utils.toInt(Arrays.copyOfRange(receivedMessage,2,6)); 
			this.predecessor    = Arrays.copyOfRange(receivedMessage,6,38); 
			this.timestamp      = Utils.toLong(Arrays.copyOfRange(receivedMessage,38,46));
			this.operationsHash = Arrays.copyOfRange(receivedMessage,46,78);
			this.stateHash      = Arrays.copyOfRange(receivedMessage,78,110);
			this.signature      = Arrays.copyOfRange(receivedMessage,110,174);
		}
		else if(receivedMessage.length==172) {
			this.level          = Utils.toInt(Arrays.copyOfRange(receivedMessage,0,4)); 
			this.predecessor    = Arrays.copyOfRange(receivedMessage,4,36); 
			this.timestamp      = Utils.toLong(Arrays.copyOfRange(receivedMessage,36,44));
			this.operationsHash = Arrays.copyOfRange(receivedMessage,44,76);
			this.stateHash      = Arrays.copyOfRange(receivedMessage,76,108);
			this.signature      = Arrays.copyOfRange(receivedMessage,108,172);
		}
	}
	

	//////// 
	public void verify(DataInputStream in, DataOutputStream out) throws IOException, DecoderException {
		byte[] potentiallyFixedPredecessorFiled = verifyPredecessor(in,out); // null or the fixed value
		System.out.println("*** field predecessor "+(potentiallyFixedPredecessorFiled!=null?"to fix -> "+Utils.toHexString(potentiallyFixedPredecessorFiled):"is ok"));
		long potentiallyFixedTimestamp = verifyTimestamp(in,out);
		System.out.println("*** field timestamp "+(potentiallyFixedTimestamp!=0?"to fix -> "+Utils.toDateAsString(potentiallyFixedTimestamp):"is ok"));
	}
	
	public byte[] verifyPredecessor(DataInputStream in, DataOutputStream out) throws IOException, DecoderException {
		byte[] predecessorAsBytes = Utils.getBlockOfThisLevelFromSocket(in,out,this.level-1);
		System.out.println("predecessor = "+new Block(predecessorAsBytes));
		byte[] hashOfReceivedPredecessor  = Utils.hash(predecessorAsBytes,32);
		if(!Arrays.equals(this.predecessor,hashOfReceivedPredecessor)) 
			return hashOfReceivedPredecessor;
		return null; // no correction
	}

	public long verifyTimestamp(DataInputStream in, DataOutputStream out) throws IOException, DecoderException {
		byte[] predecessorAsBytes = Utils.getBlockOfThisLevelFromSocket(in,out,this.level-1);
		Block predecessor = new Block(predecessorAsBytes);
		long receivedTimestamp = predecessor.timestamp;
		if(this.timestamp-receivedTimestamp<600) // 10 minutes 
			return this.timestamp-600;
		return 0; // no correction
	}

	/////// utils
	public byte[] encodeToBytes() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(Utils.to4BytesArray(level));
		outputStream.write(predecessor); 
		outputStream.write(Utils.to8BytesArray(timestamp));
		outputStream.write(operationsHash);
		outputStream.write(stateHash);
		outputStream.write(signature);
		return outputStream.toByteArray();
	}

	public String toString() {
			try {
				return "BLOCK:"+
					 "\n  level:               "+level+ " (or "+Utils.toStringOfHex(level) +" as Hex)"+
					 "\n  predecessor:         "+Utils.toHexString(predecessor)+
					 "\n  timestamp:           "+(Utils.toDateAsString(timestamp)+" (="+timestamp+" sec)")+
					 "\n  operations hash:     "+Utils.toHexString(operationsHash)+
					 "\n  state hash:          "+Utils.toHexString(stateHash)+
					 "\n  signature:           "+Utils.toHexString(signature)+
					 "\n  encoded block:       "+Utils.toHexString(this.encodeToBytes())+
					 "\n  hash of this block:  "+Utils.toHexString(Utils.hash(this.encodeToBytes(),32));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "";
	}
}