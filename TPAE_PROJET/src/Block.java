import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import org.apache.commons.codec.DecoderException;

public class Block {
	int    level; 
	byte[] predecessor;
	long   timestamp; // long?
	byte[] operationsHash;
	byte[] stateHash;
	byte[] signature;
	
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
		this.predecessor    = Utils.getBytes(predecessor); 
		this.timestamp      = Utils.getDateAsSeconds(dateAsString);
		this.operationsHash = Utils.getBytes(operationsHash);
		this.stateHash      = Utils.getBytes(stateHash);
		this.signature      = Utils.getBytes(signature);
	}

	public Block(byte[] blockInBytes) {
		this.level          = Utils.getInt(Arrays.copyOfRange(blockInBytes,0,3)); // const ?
		this.predecessor    = Arrays.copyOfRange(blockInBytes,4,35); 
		this.timestamp      = Utils.getLong(Arrays.copyOfRange(blockInBytes,36,43));
		this.operationsHash = Arrays.copyOfRange(blockInBytes,44,75);
		this.stateHash      = Arrays.copyOfRange(blockInBytes,76,107);
		this.signature      = Arrays.copyOfRange(blockInBytes,108,171);

	}
	
	public byte[] encodeToBytes() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(Utils.getBytes(level));
		outputStream.write(predecessor);
		outputStream.write(Utils.getBytes(timestamp));
		outputStream.write(operationsHash);
		outputStream.write(stateHash);
		outputStream.write(signature);
		return outputStream.toByteArray();
	}

	public String toString() {
		try {
			return "level:           "+level+
				 "\npredecessor:     "+Utils.getAsStringOfHex(predecessor)+
				 "\ntimestamp:       "+Utils.getDateAsString(timestamp)+" (="+timestamp+" sec)"+
				 "\noperations hash: "+Utils.getAsStringOfHex(operationsHash)+
				 "\nstate hash:      "+Utils.getAsStringOfHex(stateHash)+
				 "\nsignature:       "+Utils.getAsStringOfHex(signature)+
				 "\nencoded block:   "+Utils.getAsStringOfHex(this.encodeToBytes());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}