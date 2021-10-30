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
		this.level          = Utils.toInt(Arrays.copyOfRange(receivedMessage,2,6)); 
		this.predecessor    = Arrays.copyOfRange(receivedMessage,6,38); 
		this.timestamp      = Utils.toLong(Arrays.copyOfRange(receivedMessage,38,46));
		this.operationsHash = Arrays.copyOfRange(receivedMessage,46,78);
		this.stateHash      = Arrays.copyOfRange(receivedMessage,78,110);
		this.signature      = Arrays.copyOfRange(receivedMessage,110,174);
	}
	

	//////// 
	public void verify(DataInputStream in, DataOutputStream out) throws IOException, DecoderException {
		byte[] potentiallyFixedPredecessorFiled = verifyPredecessor(in,out);
		System.out.println("*** field predecessor "+(potentiallyFixedPredecessorFiled!=null?"fixed = "+Utils.toHexString(potentiallyFixedPredecessorFiled):"is ok"));
	}
	
	public byte[] verifyPredecessor(DataInputStream in, DataOutputStream out) throws IOException, DecoderException {
		byte[] predecessorAsBytes = Utils.getBlockOfThisLevelFromSocket(in,out,this.level-1);
		System.out.println("predecessor = "+new Block(predecessorAsBytes));
		byte[] hashPredecessor = Utils.hash(predecessorAsBytes,32);
		if(!Arrays.equals(this.predecessor,hashPredecessor)) {
			return hashPredecessor;
		}
		return null;
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