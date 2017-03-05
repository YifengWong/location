package location.socket;

import java.io.Serializable;

public class ServerMessage implements Serializable {
	public static final byte FLAG_FAIL = 0;
	public static final byte FLAG_BEGIN = 1;
	public static final byte FLAG_NEED = 2;

	private byte flag;
	private String userUuid;
	private Integer fileNum;
	private Integer fileLength;
	private byte[] fileBytes;
	
	
	public ServerMessage(byte flag, String userUuid, Integer fileNum, Integer fileLength, byte[] fileBytes) {
		super();
		this.flag = flag;
		this.userUuid = userUuid;
		this.fileNum = fileNum;
		this.fileLength = fileLength;
		this.fileBytes = fileBytes;
	}
	
	private byte[] getUserUuidBytes() {
		return userUuid.getBytes();
	}
	
	private byte[] getFileNumBytes() {
		byte[] numBytes = new byte[4];
		Integer temp = fileNum;
		for (int i = 0; i < numBytes.length; i++) {
			numBytes[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return numBytes;
	}
	
	private byte[] getFileLengthBytes() {
		byte[] lenBytes = new byte[4];
		Integer temp1 = fileLength;
		for (int i = 0; i < lenBytes.length; i++) {
			lenBytes[i] = new Integer(temp1 & 0xff).byteValue();
			temp1 = temp1 >> 8;
		}
		return lenBytes;
	}
	
	public byte[] getMsgBytes() {
		byte[] allBytes = new byte[42+fileLength];
		byte[] userUuidBytes = getUserUuidBytes();
		byte[] numBytes = getFileNumBytes();
		byte[] lenBytes = getFileLengthBytes();
		
		allBytes[0] = flag;
		for (int i = 0; i < 32; i++) {
			allBytes[i+1] = userUuidBytes[i];
		}
		allBytes[33] = 0;
		for (int i = 0; i < 4; i++) {
			allBytes[i+34] = numBytes[i];
		}
		
		for (int i = 0; i < 4; i++) {
			allBytes[i+38] = lenBytes[i];
		}
		
		for (int i = 0; i < fileLength; i++) {
			allBytes[42+i] = fileBytes[i];
		}
		
		return allBytes;
		
	}

	
}
