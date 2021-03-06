package location.message;

import java.io.IOException;
import java.io.Serializable;

/*
 * The message use for communication.
 * */
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

	// The postion of each param in the params array.
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
	public Message(final byte[] buffer, int begin) {
		super();
		if (buffer.length < HEAD_LENGTH) return;
		this.flag = buffer[begin+0];
		this.userUuid = new String(buffer, begin+1, 32);
		this.fileNum = buffer[begin+34] & 0xFF | (buffer[begin+35] & 0xFF) << 8 |
				(buffer[begin+36] & 0xFF) << 16 | (buffer[begin+37] & 0xFF) << 24;
		this.fileLength = buffer[begin+38] & 0xFF | (buffer[begin+39] & 0xFF) << 8 |
				(buffer[begin+40] & 0xFF) << 16 | (buffer[begin+41] & 0xFF) << 24;

		this.fileBytes = null;
		this.fileBytesCount = 0;

		if (this.flag > FLAG_HIGH) {
			// TODO
		}
	}

	/*
	* Create a message with its attributes
	* */
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

	/*
	 * Get the flag in the message.
	 * */
	public byte getFlag() { return flag; }

	/*
	* Get the request uuid in the message.
	* */
	public String getUserUuid() { return userUuid; }

	/*
     * Get the file num, to check the message order.
     * */
	public Integer getFileNum() { return fileNum; }

	/*
     * Get the file length, to make it binary-safe.
     * */
	public Integer getFileLength() { return fileLength; }

	/*
     * Get the file bytes.
     * */
	public byte[] getFileBytes() { return fileBytes; }

	/*
     * return the params array.
     * use such as: linear_acceleration_x
     *              - getParams[LINEAR_ACCELERATION+0]
     *          	gravity_y
     *              - getParams[GRAVITY+1]
     * */
	public Float[] getParams() { return params; }

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

	/*
     * Get the message all bytes.
     * */
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

	/*
     * Get the message bytes all length, to make it binary-safe.
     * */
	public int getMsgLength() {
		return HEAD_LENGTH + fileLength;
	}

	/*
     * Write the bytes into the message file.
     * */
	public void writeFileBytes(final byte[] buffer, int begin, int length) {
		if (fileBytes == null) fileBytes = new byte[fileLength];
		if (length <= fileLength - fileBytesCount) {
			for (int i = 0; i < length; ) {
				fileBytes[fileBytesCount++] = buffer[begin+i++];
			}
		}
	}

	/*
     * Write the bytes into the message params.
     * */
	public void writeParamsBytes(final byte[] buffer, int begin, int length) throws IOException {
		if (length != PARAM_ALL_LENGTH) throw new IOException("params length wrong");
		else {
			if (params == null) params = new Float[PARAM_NUM_EACH * PARAM_NUM];
			int temp;
			for (int i = 0; i < PARAM_NUM_EACH * PARAM_NUM; i++) {
				temp = buffer[begin+i*4 + 0] & 0xFF | (buffer[begin+i*4 + 1] & 0xFF) << 8 |
						(buffer[begin+i*4 + 2] & 0xFF) << 16 | (buffer[begin+i*4 + 3] & 0xFF) << 24;
				params[i] = Float.intBitsToFloat(temp);
			}
		}
	}

	public String getResultString() {
		if (flag == FLAG_RESULT) {
			return new String(fileBytes);
		}
		return null;
	}
	
}
