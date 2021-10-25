import java.io.ByteArrayOutputStream;
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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import ove.crypto.digest.Blake2b;

public class Utils {
	public static byte[] hash(byte[] valeurToHash, int hashParamNbBytes) { // TME 1
		Blake2b.Param param = new Blake2b.Param().setDigestLength(hashParamNbBytes);
		final Blake2b blake2b = Blake2b.Digest.newInstance(param);        
		return blake2b.digest(valeurToHash);
	}
	
	public static byte[] addLength16bits(byte[] bytesArray) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(getBytes(bytesArray.length));
		outputStream.write(bytesArray);
		return outputStream.toByteArray();
	}

	////// converters
	static byte[] getBytes(int int32bits) { // = encode_entier TME 1
		ByteBuffer intConvertedToBytes = ByteBuffer.allocate(4);
		intConvertedToBytes.putInt(int32bits);
		return intConvertedToBytes.array();
	}

	static int getInt(byte[] bytes) {
	     return ByteBuffer.wrap(bytes).getInt();
	}

	static byte[] getBytes(long long64bits) { 
		ByteBuffer longConvertedToBytes = ByteBuffer.allocate(8);
		longConvertedToBytes.putLong(long64bits);
		return longConvertedToBytes.array();
	}
	
	static long getLong(byte[] bytes) {
	     return ByteBuffer.wrap(bytes).getInt();
	}
	
	static byte[] getBytes(char[] charArray) throws DecoderException {
		return Hex.decodeHex(charArray);
	}
	
	static char[] getCharArray(byte[] bytes) {
		return Hex.encodeHex(bytes);
	}
	
	static byte[] getBytes(String str) throws DecoderException {;
		return Hex.decodeHex(str.toCharArray());
	}

	static String getAsStringOfHex(byte[] bytes) {
		final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
	    byte[] hexChars = new byte[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars, StandardCharsets.UTF_8);
	}
	
	// проблемные java.util.Date .Calendar java.text.SimpleDateForma
    // Timestamp(int year, int month, int date, int hour, int minute, int second, int nano) deprecated, use Timestamp(long millis)
	// java.time OK
	
	static long getDateAsSeconds(String dateAsString) throws ParseException { 
		DateTimeFormatter formatter     = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC")); 
		LocalDateTime     localDateTime = LocalDateTime.parse(dateAsString, formatter);
		//System.out.println("localDateTime="+localDateTime.atZone(ZoneId.of("UTC"))+"="+localDateTime.atZone(ZoneId.of("UTC")).toEpochSecond());
		return localDateTime.atZone(ZoneId.of("UTC")).toEpochSecond(); 
	}	

	static String getDateAsString(long seconds) { 
		LocalDateTime     dateTime      = LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC);
		DateTimeFormatter formatter     = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String            formattedDate = dateTime.format(formatter);
		return formattedDate.toString();
	}
}