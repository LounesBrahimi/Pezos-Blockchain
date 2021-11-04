package tools;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import ove.crypto.digest.Blake2b;
import java.nio.charset.StandardCharsets;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.crypto.*;

public class Utils {
	
	public Utils() {
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
	

	/*
	 * Recupere un message depuis le serveur
	 * */
	public byte[] getFromSocket(int nbBytesWanted, DataInputStream in, String comment) throws IOException {
		byte[] result = new byte[nbBytesWanted];
		int nbBytesReceived = in.read(result,0,nbBytesWanted); 
		if (nbBytesReceived != nbBytesWanted) {
			byte[] result2 = new byte[nbBytesReceived];
			for (int i = 0; i < result2.length; i++) {
				result2[i] = result[i];
			}
			return  result2;
		}
		System.out.println("*** SOCKET "+comment+" get: ["+toHexString(Arrays.copyOfRange(result,0,2))+"]"+toHexString(Arrays.copyOfRange(result,2,result.length))); 
		return result;
	}	
	
	/*
	 * Convertie un tableau de caracteres en un tableau de bytess
	 * */
	public byte[] toBytesArray(char[] charArray) throws DecoderException {
		return Hex.decodeHex(charArray);
	}
	
	/*
	 * Convertie un String en un tableau de bytess
	 * */
	public byte[] toBytesArray(String str) throws DecoderException {
		return Hex.decodeHex(str.toCharArray());
	}
	
	/*
	 * Permet d'envoyer un message sous format String vers le serveur
	 * */
	public void sendToSocket(String stringToSend, DataOutputStream out) throws IOException, DecoderException {
		sendToSocket(toBytesArray(stringToSend),out,"");
	}
	
	/*
	 * Permet d'envoyer un tableau de byte vers le serveur
	 * */
	public void sendToSocket(byte[] bytesArrayToSend, DataOutputStream out) throws IOException, DecoderException {
		sendToSocket(bytesArrayToSend,out,"");
	}
	
	public void sendToSocket(String stringToSend, DataOutputStream out, String comment) throws IOException, DecoderException {
		sendToSocket(toBytesArray(stringToSend),out,comment);
	}
	
	public void sendToSocket(byte[] bytesArrayToSend, DataOutputStream out, String comment) throws IOException, DecoderException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(to2BytesArray(bytesArrayToSend.length));
		outputStream.write(bytesArrayToSend);
		bytesArrayToSend = outputStream.toByteArray(); 
		out.write(bytesArrayToSend); 
		out.flush(); // binome !
		System.out.println("*** SOCKET "+(comment==""?"":comment+" ")+"sent: ("+toHexString(Arrays.copyOfRange(bytesArrayToSend,0,2))+")["+toHexString(Arrays.copyOfRange(bytesArrayToSend,2,4))+"]"+toHexString(Arrays.copyOfRange(bytesArrayToSend,4,bytesArrayToSend.length)));
	}
	
	/*
	 * Convertue un entier en bytes
	 * */
	public byte[] to2BytesArray(int int2bytes) { 
		ByteBuffer convertedToBytes = ByteBuffer.allocate(2);
		convertedToBytes.putShort((short)int2bytes);
		return convertedToBytes.array();
	}
	
	/*
	 * 32 bytes of 0
	 * */
	public byte[] Bytes32s() { 
		ByteBuffer convertedToBytes = ByteBuffer.allocate(32);
		int x = 0 ;
		convertedToBytes.putInt(x);
		return convertedToBytes.array();
	}
	
	/*
	 * Convertie un tableau de byte en string hexadecimal
	 * */
	public  String toHexString(byte[] bytes) {
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
	
	/*
	 * Crypte en blake2b
	 * */
	public byte[] hash(byte[] valeurToHash, int hashParamNbBytes) {
		Blake2b.Param param = new Blake2b.Param().setDigestLength(hashParamNbBytes);
		final Blake2b blake2b = Blake2b.Digest.newInstance(param);        
		return blake2b.digest(valeurToHash);
	}
	
	/*
	 * Signature en Ed25519
	 * */
	public byte[] signature(byte[] msgToSign, String skString) throws DecoderException, DataLengthException, CryptoException {
		byte[] skBytes = toBytesArray(skString);
		Ed25519PrivateKeyParameters sk2 = new Ed25519PrivateKeyParameters(skBytes);
		Signer signer = new Ed25519Signer();
		signer.init(true, sk2);
		signer.update(msgToSign, 0, 32);
		byte[] signature = null;
		signature = signer.generateSignature();
		return signature;
	}
	
	/*
	 * Convert String Date to a long of seconds
	 * */
	public long toDateAsSeconds(String dateAsString) throws ParseException { 
		DateTimeFormatter formatter     = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC")); 
		LocalDateTime     localDateTime = LocalDateTime.parse(dateAsString, formatter);
		return localDateTime.atZone(ZoneId.of("UTC")).toEpochSecond(); 
	}
	
	public long currentDateTimeAsSeconds() {
		return LocalDateTime.now(ZoneId.of("UTC")).atZone(ZoneId.of("UTC")).toEpochSecond(); 
	}
	
	/*
	 * Convertie un tableau de bytes en entier
	 * */
	public int toInt(byte[] bytes) {
	    return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
	}

	
	/*
	 * Convertie un tableau de bytes en long
	 * */
	public long toLong(byte[] bytes) {
	    return ByteBuffer.wrap(bytes).getLong();
	}
	
	/*
	 *  Convertie un entier en un tableau de 4 bytes
	 * */
	public byte[] to4BytesArray(int int4bytes) {
		ByteBuffer convertedToBytes = ByteBuffer.allocate(4);
		convertedToBytes.putInt(int4bytes);
		return convertedToBytes.array();
	}
	
	/*
	 *  Convertie un entier en un tableau de 8 bytes
	 * */
	public byte[] to8BytesArray(long long64bits) { 
		ByteBuffer convertedToBytes = ByteBuffer.allocate(8);
		convertedToBytes.putLong(long64bits);
		return convertedToBytes.array();
	}
	
	/*
	 * Convertie un entier en hexadecimale
	 * */
	public String toStringOfHex(int n) {
		return toHexString(to4BytesArray(n));
	}
	
	/*
	 * onvertie des secondes sous format entier long en String sous format
	 * yyyy-MM-dd HH:mm:ss.
	 * */
	public String toDateAsString(long seconds) { 
		LocalDateTime     dateTime      = LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC);
		DateTimeFormatter formatter     = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String            formattedDate = dateTime.format(formatter);
		return formattedDate.toString();
	}
}