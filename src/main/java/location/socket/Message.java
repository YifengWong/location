package location.socket;

import java.io.*;
import java.util.Arrays;

public class Message implements Serializable {
	public static final byte FLAG_FAIL = 0;
	public static final byte FLAG_IMG = 1;
	public static final byte FLAG_NEED = 2;
	public static final byte FLAG_RESULT = 3;
	public static final byte FLAG_HIGH = 3;
	public static final int HEAD_LENGTH = 42;

	public static final int PARAM_NUM = 7;
	public static final int PARAM_NUM_EACH = 3;
	// All params length in byte
	public static final int PARAM_ALL_LENGTH = 4 * PARAM_NUM_EACH * PARAM_NUM;

	public static final int LINEAR_ACCELERATION = 0;
	public static final int ACCELEROMETER = 3;
	public static final int GRAVITY = 6;
	public static final int ORIENTAION = 9;
	public static final int GYROSCOPE = 12;
	public static final int MAGNETIC_FIELD = 15;
	public static final int PRESSURE = 18;


	private byte flag;
	private String userUuid;
	private Integer fileNum;
	private Integer fileLength;

	private Float[] params = null;

	private byte[] fileBytes;
	private int fileBytesCount;


	/*
	* Create an empty message with head buffer
	* */
	public Message(final byte[] buffer) throws IOException {
		super();
		if (buffer.length < HEAD_LENGTH) return;
		this.flag = buffer[0];
		this.userUuid = new String(buffer, 1, 32);
		this.fileNum = buffer[34] & 0xFF | (buffer[35] & 0xFF) << 8 |
				(buffer[36] & 0xFF) << 16 | (buffer[37] & 0xFF) << 24;
		this.fileLength = buffer[38] & 0xFF | (buffer[39] & 0xFF) << 8 |
				(buffer[40] & 0xFF) << 16 | (buffer[41] & 0xFF) << 24;

		this.fileBytes = null;
		this.fileBytesCount = 0;

		if (this.flag > FLAG_HIGH) {
			// TODO
		}
	}

	public Message(byte flag, String userUuid, Integer fileNum, byte[] fileBytes, Float[] params) {
		super();
		this.flag = flag;
		this.userUuid = userUuid;
		this.fileNum = fileNum;
		this.fileLength = fileBytes.length;
		this.fileBytes = fileBytes;
		this.fileBytesCount = this.fileLength;
		this.params = params;
	}


	public byte getFlag() { return flag; }

	public String getUserUuid() { return userUuid; }

	public Integer getFileNum() { return fileNum; }

	public Integer getFileLength() { return fileLength; }

	public byte[] getFileBytes() { return fileBytes; }

	public Float[] getParams() { return params; }

//	private byte[] getUserUuidBytes() { return userUuid.getBytes(); }

//	private byte[] getFileNumBytes() {
//		byte[] numBytes = new byte[4];
//		Integer temp = fileNum;
//		for (int i = 0; i < numBytes.length; i++) {
//			numBytes[i] = new Integer(temp & 0xff).byteValue();
//			temp = temp >> 8;
//		}
//		return numBytes;
//	}
//
//	private byte[] getFileLengthBytes() {
//		byte[] lenBytes = new byte[4];
//		Integer temp1 = fileLength;
//		for (int i = 0; i < lenBytes.length; i++) {
//			lenBytes[i] = new Integer(temp1 & 0xff).byteValue();
//			temp1 = temp1 >> 8;
//		}
//		return lenBytes;
	//	}
	private byte[] getNumBytes(Object num) {
		byte[] numBytes = new byte[4];
		Integer temp = null;
		if (num instanceof Integer) {
			temp = (Integer) num;
		} else if (num instanceof Float) {
			temp = Float.floatToIntBits(((Float) num).floatValue());
		}

		if (temp != null) {
			for (int i = 0; i < numBytes.length; i++) {
				numBytes[i] = new Integer(temp & 0xff).byteValue();
				temp = temp >> 8;
			}
		}

		return numBytes;
	}

	public byte[] getMsgBytes() {
		int allLength = HEAD_LENGTH + fileLength;
		if (flag == FLAG_IMG) allLength += PARAM_ALL_LENGTH;

		byte[] allBytes = new byte[allLength];
		allBytes[0] = flag;
		for (int i = 0; i < 32; i++) allBytes[i+1] = userUuid.getBytes()[i];
		allBytes[33] = 0;

		byte[] numBytes = getNumBytes(fileNum);
		for (int i = 0; i < 4; i++) allBytes[i+34] = numBytes[i];

		numBytes = getNumBytes(fileLength);
		for (int i = 0; i < 4; i++) allBytes[i+38] = numBytes[i];

		int count = 42;
		if (flag == FLAG_IMG) {
			for (int i = 0; i < PARAM_NUM * PARAM_NUM_EACH; i++, count+=4) {
				numBytes = getNumBytes(params[i]);
				for (int j = 0; j < 4; j++) allBytes[count + j] = numBytes[j];
			}
		}

		for (int i = 0; i < fileLength; i++) {
			allBytes[count + i] = fileBytes[i];
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

	public void writeParamsBytes(final byte[] buffer, int length) throws IOException {
		if (length != PARAM_ALL_LENGTH) throw new IOException("params length wrong");
		else {
			if (params == null) params = new Float[PARAM_NUM_EACH * PARAM_NUM];
			int temp;
			for (int i = 0; i < PARAM_NUM_EACH * PARAM_NUM; i++) {
				temp = buffer[i*4 + 0] & 0xFF | (buffer[i*4 + 1] & 0xFF) << 8 |
						(buffer[i*4 + 2] & 0xFF) << 16 | (buffer[i*4 + 3] & 0xFF) << 24;
				params[i] = Float.intBitsToFloat(temp);
			}
//
//			ByteArrayInputStream bIntput = new ByteArrayInputStream(buffer, 0, length);
//			DataInputStream dIntput = new DataInputStream(bIntput);
//			params = new Float[PARAM_NUM_EACH * PARAM_NUM];
//			for (int i = 0; i < PARAM_NUM_EACH * PARAM_NUM; i++) {
//				params[i] = dIntput.readFloat();
//			}
		}
	}
	
}
