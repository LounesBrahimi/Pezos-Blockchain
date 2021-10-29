import java.io.ByteArrayOutputStream;
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
		if(!Arrays.equals(Arrays.copyOfRange(receivedMessage,0,2),new byte[] {(byte)0x00,(byte)0x02})) {
			System.out.println(); // throw exception ?
			return;
		}
		this.level          = Utils.toInt(Arrays.copyOfRange(receivedMessage,2,6)); 
		this.predecessor    = Arrays.copyOfRange(receivedMessage,6,38); 
		this.timestamp      = Utils.toLong(Arrays.copyOfRange(receivedMessage,38,46));
		this.operationsHash = Arrays.copyOfRange(receivedMessage,46,78);
		this.stateHash      = Arrays.copyOfRange(receivedMessage,78,110);
		this.signature      = Arrays.copyOfRange(receivedMessage,110,174);
	}
	
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
					 "\nlevel:           "+level+ " (or "+Utils.toStringOfHex(level) +" as Hex)"+
					 "\npredecessor:     "+Utils.toHexString(predecessor)+
					 "\ntimestamp:       "+(Utils.toDateAsString(timestamp)+" (="+timestamp+" sec)")+
					 "\noperations hash: "+Utils.toHexString(operationsHash)+
					 "\nstate hash:      "+Utils.toHexString(stateHash)+
					 "\nsignature:       "+Utils.toHexString(signature)+
					 "\nencoded block:   "+Utils.toHexString(this.encodeToBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
	}
}