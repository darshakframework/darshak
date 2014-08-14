package com.darshak.packetreader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.util.Log;

import com.darshak.constants.Constants;
import com.darshak.constants.Event;
import com.darshak.modal.Packet;
import com.darshak.util.Utils;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public class PacketReader {

	private static final String LOG_TAG = PacketReader.class.getSimpleName();

	public List<Packet> generateResult(byte[] fileByteArray, int codeType,
			Event event) {
		List<PacketIdentificationDetails> sPacketIdenList = PacketConfigurator
				.getPacketsList(codeType);
		List<Packet> packets = new ArrayList<Packet>();

		int searchBeginIndex = getSearchBeginIndex(fileByteArray, codeType,
				event);

		Log.d(LOG_TAG, "Log file should be scanned from the index "
				+ searchBeginIndex);

		for (PacketIdentificationDetails packetIdentificationDetails : sPacketIdenList) {
			Set<ByteArrayWrapper> matchingBytesList = searchCodes(
					fileByteArray, searchBeginIndex, packetIdentificationDetails);

			for (ByteArrayWrapper matchedBytes : matchingBytesList) {
				String hexCode = Utils.formatHexBytes(matchedBytes
						.getByteArray());
				// Log.d(LOG_TAG, hexCode);
				Packet packet = new Packet(packetIdentificationDetails.getPacketType(),
						hexCode);
				packet.addPacketAttributes(packetIdentificationDetails
						.extractAttributes(matchedBytes.getByteArray()));
				packets.add(packet);
			}
		}
		return packets;
	}

	/**
	 * Logs begin service request, so from the bottom to top of a log file look
	 * for service request.
	 * 
	 * @return
	 */
	private int getSearchBeginIndex(byte[] fileByteArray, int codeType,
			Event event) {
		if (codeType != Constants.GSM || event == Event.NONE) {
			// begin from first byte.
			return 0;
		}
		PacketIdentificationDetails gsmServReqCodeInfo = PacketConfigurator
				.gsmInitServiceRequestCode();
		byte[] searchBytes = gsmServReqCodeInfo.getSearchBytes();

		int firstFixedByteIndex = gsmServReqCodeInfo.getFrstFxdBytIndx();
		int serBytBeginIndex = searchBytes.length - firstFixedByteIndex - 1;
		int j = fileByteArray.length - 1;
		for (; j >= searchBytes.length; j--) {
			if (fileByteArray[j - serBytBeginIndex] == searchBytes[firstFixedByteIndex]) {
				int i = serBytBeginIndex;
				while (i >= 0 && j >= 0) {
					if (fileByteArray[j] == searchBytes[i]
							|| gsmServReqCodeInfo.compareWildByteInfo(i,
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
			int searchBeginIndex, PacketIdentificationDetails packetIdentificationDetails) {
		Set<ByteArrayWrapper> matchingCodes = new HashSet<ByteArrayWrapper>();
		byte[] searchBytes = packetIdentificationDetails.getSearchBytes();

		int firstFixedByteIndex = packetIdentificationDetails.getFrstFxdBytIndx();

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
								|| packetIdentificationDetails.compareWildByteInfo(i,
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
					+ packetIdentificationDetails.getPacketType().name() + " not found.");
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