import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;

import ove.crypto.digest.Blake2b;

public class Utils {
	///////// crypto

	public static byte[] hash(byte[] valeurToHash, int hashParamNbBytes) { // TME 1
		Blake2b.Param param = new Blake2b.Param().setDigestLength(hashParamNbBytes);
		final Blake2b blake2b = Blake2b.Digest.newInstance(param);        
		return blake2b.digest(valeurToHash);
	}
	
	public static byte[] signature(byte[] msgToSign, String skString) throws DecoderException, DataLengthException, CryptoException {
		byte[] skBytes = Utils.toBytesArray(skString);
		Ed25519PrivateKeyParameters sk2 = new Ed25519PrivateKeyParameters(skBytes);
		Signer signer = new Ed25519Signer();
		signer.init(true, sk2);
		signer.update(msgToSign, 0, 32);
		byte[] signature = null;
		signature = signer.generateSignature();
		return signature;
	}

	static Blake2b.Param param = new Blake2b.Param().setDigestLength(32);
	final static Blake2b blake2b = Blake2b.Digest.newInstance(param);        
	static byte[] concat_hash(byte[] hash1, byte[] hash2) throws IOException { // TME 2, Ex 1
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(hash1);
		outputStream.write(hash2);
		byte twoHashes[] = outputStream.toByteArray();
		byte hashOfTwoHashes[] = blake2b.digest(twoHashes);
		return hashOfTwoHashes;
	}	

	public static MerkleTree witness(String str, MerkleTree resultTree) throws IOException {
		return witness(Utils.blake2b.digest(str.getBytes()),resultTree);
	}

	public static MerkleTree witness(byte[] wantedHash, MerkleTree tree) throws IOException { // TME 2, Ex 4
		MerkleTree resultTree = tree.clone();
		witnessRecours(wantedHash,resultTree);
		return resultTree;
	}
	
	public static byte[] witnessRecours(byte[] wantedHash, MerkleTree resultTree) throws IOException { 
		if(resultTree.left==null || resultTree.right==null) { 
			// the leaves (of the witness) have been already verified from theirs parent nodes // mais on ne rentre pas dans les feuilles ?
			return wantedHash;
		}
		else if(Arrays.equals(resultTree.left.hash,wantedHash)) {
			// left child == wantedHash
			resultTree.right.left=null;
			resultTree.right.right=null;
			return Utils.concat_hash(resultTree.left.hash, resultTree.right.hash);
		}
		else if(Arrays.equals(resultTree.right.hash,wantedHash)) {
			// right child == wantedHash
			resultTree.left.left=null;
			resultTree.left.right=null;
			return Utils.concat_hash(resultTree.left.hash, resultTree.right.hash);
		}
		else if(resultTree.left.left==null || resultTree.left.right==null || resultTree.right.left==null || resultTree.right.right==null) { 
			// is a parent of a leaf, no child == wantedHash
			resultTree.left=null;
			resultTree.right=null;
			return wantedHash;
		}
		else { 
			// isn't a leaf, isn't a parent of a leaf
			byte[] potentialNewWantedHashLeft = witnessRecours(wantedHash,resultTree.left);
			if(!Arrays.equals(potentialNewWantedHashLeft,wantedHash)) {
				resultTree.right.left=null;
				resultTree.right.right=null;
				return potentialNewWantedHashLeft;
			}
			byte[] potentialNewWantedHashRight = witnessRecours(wantedHash,resultTree.right);
			if(!Arrays.equals(potentialNewWantedHashRight,wantedHash)) {
				resultTree.left.left=null;
				resultTree.left.right=null;
				return potentialNewWantedHashRight;
			}
		}
		return wantedHash; // null?
	}

	public boolean verify(MerkleTree witness, byte[] rootHash) { // TME 2, Ex 5
		return Arrays.equals(calculateRootHash(witness),rootHash);
	}

	public byte[] calculateRootHash(MerkleTree witness) {
		byte[] f=null;
		return f;
	}

	//////// to/from socket
	static byte[] getFromSocket(int nbBytesWanted, DataInputStream in, String comment) throws IOException {
		byte[] result = new byte[nbBytesWanted];
		int nbBytesReceived = in.read(result,0,nbBytesWanted); 
		System.out.println((comment==""?"":comment+" ")+"received: "+ nbBytesReceived+ " bytes " + new String(Hex.encodeHex(result)));
		return result;
	}	
	
	static byte[] getFromSocket(int nbBytesWanted, DataInputStream in) throws IOException {
		return getFromSocket(nbBytesWanted,in,"");
	}
	
	static void sendToSocket(String stringToSend, DataOutputStream out) throws IOException, DecoderException {
		sendToSocket(toBytesArray(stringToSend),out,"");
	}
	
	static void sendToSocket(String stringToSend, DataOutputStream out, String comment) throws IOException, DecoderException {
		sendToSocket(toBytesArray(stringToSend),out,comment);
	}

	static void sendToSocket(byte[] bytesArrayToSend, DataOutputStream out) throws IOException, DecoderException {
		sendToSocket(bytesArrayToSend,out,"");
	}
	
	static void sendToSocket(byte[] bytesArrayToSend, DataOutputStream out, String comment) throws IOException, DecoderException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(to2BytesArray(bytesArrayToSend.length));
		outputStream.write(bytesArrayToSend);
		bytesArrayToSend = outputStream.toByteArray(); 
		out.write(bytesArrayToSend); 
		out.flush(); // binome !
		System.out.println((comment==""?"":comment+" ")+"sent: "+toHexString(bytesArrayToSend));
	}

	
	////// converters
	static byte[] to2BytesArray(int int2bytes) { 
		ByteBuffer convertedToBytes = ByteBuffer.allocate(2);
		convertedToBytes.putShort((short)int2bytes);
		return convertedToBytes.array();
	}
	
	static byte[] to4BytesArray(int int4bytes) { // = encode_entier TME 1
		ByteBuffer convertedToBytes = ByteBuffer.allocate(4);
		convertedToBytes.putInt(int4bytes);
		return convertedToBytes.array();
	}

	static byte[] to8BytesArray(long long64bits) { 
		ByteBuffer convertedToBytes = ByteBuffer.allocate(8);
		convertedToBytes.putLong(long64bits);
		return convertedToBytes.array();
	}
	
	static byte[] toBytesArray(char[] charArray) throws DecoderException {
		return Hex.decodeHex(charArray);
	}
	
	static byte[] toBytesArray(String str) throws DecoderException {;
		return Hex.decodeHex(str.toCharArray());
	}

	static char[] toCharArray(byte[] bytes) {
		return Hex.encodeHex(bytes);
	}
	
	static int toInt(byte[] bytes) {
	    return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}

	static long toLong(byte[] bytes) {
	    return ByteBuffer.wrap(bytes).getLong();
	}
	
	static String toStringOfHex(int n) {
		return toHexString(to4BytesArray(n));
	}
	
	static String toHexString(byte[] bytes) {
		if(bytes==null || bytes.length==0) return "";
		final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
	    byte[] hexChars = new byte[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars, StandardCharsets.UTF_8);
	}
	
	static long toDateAsSeconds(String dateAsString) throws ParseException { 
		DateTimeFormatter formatter     = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC")); 
		LocalDateTime     localDateTime = LocalDateTime.parse(dateAsString, formatter);
		return localDateTime.atZone(ZoneId.of("UTC")).toEpochSecond(); 
	}	

	static String toDateAsString(long seconds) { 
		LocalDateTime     dateTime      = LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC);
		DateTimeFormatter formatter     = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String            formattedDate = dateTime.format(formatter);
		return formattedDate.toString();
	}
}