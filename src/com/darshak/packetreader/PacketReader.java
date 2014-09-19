package com.darshak.packetreader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.util.Log;

import com.darshak.constants.Constants;
import com.darshak.constants.PacketType;
import com.darshak.db.DarshakDBHelper;
import com.darshak.modal.Packet;
import com.darshak.modal.SentinelPacket;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class PacketReader {

	private static final String LOG_TAG = PacketReader.class.getSimpleName();

	private Context sContext;

	private DarshakDBHelper sDBHelper;

	public PacketReader(Context context, DarshakDBHelper dbHelper) {
		this.sContext = context;
		this.sDBHelper = dbHelper;
	}

/*	public PacketReader() {
		this.sContext = null;
		this.sDBHelper = null;
	}*/

	public List<Packet> generateResult(byte[] fileByteArray, int scanType) {
		List<PacketIdentificationDetails> sPacketIdenList = PacketConfigurator
				.getPacketsList(scanType, sContext);
		List<Packet> packets = new ArrayList<Packet>();

		int searchBeginIndex = getSearchBeginIndex(fileByteArray,
				getSentPktIdenDetails(scanType));

		/* int searchBeginIndex = getSearchBeginIndex(fileByteArray, null); */

		Log.d(LOG_TAG, "Log file should be scanned from the index "
				+ searchBeginIndex);

		for (PacketIdentificationDetails packetIdentificationDetails : sPacketIdenList) {
			Set<ByteArrayWrapper> matchingBytesList = searchCodes(
					fileByteArray, searchBeginIndex,
					packetIdentificationDetails);

			for (ByteArrayWrapper matchedBytes : matchingBytesList) {
				Packet packet = packetIdentificationDetails.getPacketType()
						.format(matchedBytes.getByteArray());
				if (packet != null) {
					packets.add(packet);
				}
			}
		}
		updateSentinelPktSequence(scanType, fileByteArray);
		return packets;
	}

	private PacketIdentificationDetails getSentPktIdenDetails(int scanType) {
		SentinelPacket sentinelPacket = sDBHelper.getSentinelPacket(scanType);
		if (sentinelPacket == null) {
			if (scanType == Constants._3G) {
				// unlike GSM there is no guarantee that every transaction will
				// begin with CM service request
				// return PacketConfigurator.cmServiceRequest();
				return null;
			}
			if (scanType == Constants.GSM) {
				return PacketConfigurator.gsmInitServiceRequestCode();
			}
			return null;
		}
		byte[] sentinelBytes = sentinelPacket.getByteSequence();

		PacketIdentificationDetails idendetails = new PacketIdentificationDetails(
				PacketType.START_SCAN, sentinelBytes, new ArrayList<Integer>(),
				0, sentinelBytes.length - 1);
		return idendetails;
	}

	public void updateSentinelPktSequence(int scanType, byte[] fileByteArray) {
		if (fileByteArray != null && fileByteArray.length != 0) {
			int numOfBytes = fileByteArray.length;
			byte[] sentinelBytes = Arrays.copyOfRange(fileByteArray, numOfBytes
					- Constants.NUM_OF_TERMINATING_BYTES, numOfBytes);

			if (null == sDBHelper.getSentinelPacket(scanType)) {
				sDBHelper.insertSentinelPacket(scanType, sentinelBytes);
			} else {
				sDBHelper.updateSentinelPacket(new SentinelPacket(scanType,
						sentinelBytes));
			}
		}
	}

	/**
	 * Logs begin service request, so from the bottom to top of a log file look
	 * for service request.
	 * 
	 * @return
	 */
	private int getSearchBeginIndex(byte[] fileByteArray,
			PacketIdentificationDetails sentinelPacket) {
		if (sentinelPacket == null) {
			return 0;
		}

		byte[] searchBytes = sentinelPacket.getSearchBytes();

		int firstFixedByteIndex = sentinelPacket.getFrstFxdBytIndx();
		int serBytBeginIndex = searchBytes.length - firstFixedByteIndex - 1;
		int j = fileByteArray.length - 1;
		for (; j >= searchBytes.length; j--) {
			if (fileByteArray[j - serBytBeginIndex] == searchBytes[firstFixedByteIndex]) {
				int i = serBytBeginIndex;
				while (i >= 0 && j >= 0) {
					if (fileByteArray[j] == searchBytes[i]
							|| sentinelPacket.compareWildByteInfo(i,
									fileByteArray[j])) {
						i--;
						j--;
					} else {
						break;
					}
				}
				// All search bytes matched.
				if (i == -1) {
					return j;
				}
			}
		}
		return 0;
	}

	private Set<ByteArrayWrapper> searchCodes(byte[] fileByteArray,
			int searchBeginIndex,
			PacketIdentificationDetails packetIdentificationDetails) {
		Set<ByteArrayWrapper> matchingCodes = new HashSet<ByteArrayWrapper>();
		byte[] searchBytes = packetIdentificationDetails.getSearchBytes();

		int firstFixedByteIndex = packetIdentificationDetails
				.getFrstFxdBytIndx();

		for (int j = searchBeginIndex; j < fileByteArray.length; j++) {
			if (fileByteArray[j] == searchBytes[firstFixedByteIndex]) {
				int tmp = j - firstFixedByteIndex;
				if (tmp < 0) {
					continue;
				}
				int i = 0;
				while (true) {
					if (i < searchBytes.length && tmp < fileByteArray.length) {
						if (fileByteArray[tmp] == searchBytes[i]
								|| packetIdentificationDetails
										.compareWildByteInfo(i,
												fileByteArray[tmp])) {
							i++;
							tmp++;
						} else {
							break;
						}
					} else {
						break;
					}
				}
				if (i == (searchBytes.length)) {
					matchingCodes.add(extractMatchingBytes(fileByteArray, tmp,
							searchBytes.length));
					j = tmp;
					continue;
				}
			}
		}
		if (matchingCodes.size() == 0) {
			Log.d(LOG_TAG, "Byte sequence matching packet structure "
					+ packetIdentificationDetails.getPacketType().name()
					+ " not found.");
		}
		return matchingCodes;
	}

	private ByteArrayWrapper extractMatchingBytes(byte[] fileByteArray,
			int endIndex, int length) {
		byte[] extractedBytes = new byte[length];
		int startIndex = endIndex - length;
		for (int i = startIndex, j = 0; i < endIndex; i++, j++) {
			extractedBytes[j] = fileByteArray[i];
		}
		return new ByteArrayWrapper(extractedBytes);
	}

	private class ByteArrayWrapper {

		private byte[] sByteArray;

		public ByteArrayWrapper(byte[] byteArray) {
			this.sByteArray = byteArray;
		}

		private PacketReader getOuterType() {
			return PacketReader.this;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + Arrays.hashCode(sByteArray);
			return result;
		}

		public byte[] getByteArray() {
			return sByteArray;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ByteArrayWrapper other = (ByteArrayWrapper) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (!Arrays.equals(sByteArray, other.sByteArray))
				return false;
			return true;
		}
	}
}