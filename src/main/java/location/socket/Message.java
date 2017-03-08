package location.socket;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
	public static final byte FLAG_FAIL = 0;
	public static final byte FLAG_BEGIN = 1;
	public static final byte FLAG_NEED = 2;
	public static final byte FLAG_RESULT = 3;
	public static final byte FLAG_HIGH = 3;
	public static final int HEAD_LENGTH = 42;

	private byte flag;
	private String userUuid;
	private Integer fileNum;
	private Integer fileLength;
	private byte[] fileBytes;
	private int fileBytesCount;

	public Message(final byte[] buffer) {
		super();
		this.flag = buffer[0];
		this.userUuid = new String(buffer, 1, 33);
		this.fileNum = buffer[37] & 0xFF | (buffer[36] & 0xFF) << 8 |  
	            (buffer[35] & 0xFF) << 16 | (buffer[34] & 0xFF) << 24; 
		this.fileLength = buffer[41] & 0xFF | (buffer[40] & 0xFF) << 8 |  
	            (buffer[39] & 0xFF) << 16 | (buffer[38] & 0xFF) << 24;
		
		this.fileBytes = null;
		this.fileBytesCount = 0;
		
		if (this.flag > FLAG_HIGH) {
			// TODO
		}
	}
	
	public Message(byte flag, String userUuid, Integer fileNum, byte[] fileBytes) {
		super();
		this.flag = flag;
		this.userUuid = userUuid;
		this.fileNum = fileNum;
		this.fileLength = fileBytes.length;
		this.fileBytes = fileBytes;
		this.fileBytesCount = this.fileLength;
	}
	
		
	public byte getFlag() {
		return flag;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public Integer getFileNum() {
		return fileNum;
	}

	public Integer getFileLength() {
		return fileLength;
	}

	public byte[] getFileBytes() {
		return fileBytes;
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
	
	public void writeFileBytes(final byte[] buffer, int length) {
		if (fileBytes == null) fileBytes = new byte[fileLength];
		if (length <= fileLength - fileBytesCount) {
			for (int i = 0; i < length; ) {
				fileBytes[fileBytesCount++] = buffer[i++];
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\"flag\":\"");
		builder.append(flag);
		builder.append("\",\"userUuid\":\"");
		builder.append(userUuid);
		builder.append("\",\"fileNum\":\"");
		builder.append(fileNum);
		builder.append("\",\"fileLength\":\"");
		builder.append(fileLength);
		builder.append("\",\"fileBytes\":\"");
		builder.append(Arrays.toString(fileBytes));
		builder.append("\",\"fileBytesCount\":\"");
		builder.append(fileBytesCount);
		builder.append("\"}");
		return builder.toString();
	}
	
	
}
