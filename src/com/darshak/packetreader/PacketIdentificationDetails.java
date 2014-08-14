package com.darshak.packetreader;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.darshak.constants.PacketType;
import com.darshak.constants.PacketAttributeType;
import com.darshak.modal.PacketAttribute;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class PacketIdentificationDetails {

	private static final byte[] ANYTHING = new byte[0];

	private static final String LOG_TAG = PacketIdentificationDetails.class
			.getSimpleName();

	private PacketType sPacketType;
	private byte sSearchBytes[];
	private List<Integer> sAnythingAllowedBytes;
	private List<WildByteInfo> sWildByteInfoList = new ArrayList<WildByteInfo>();
	private List<PacketAttributeDetails> sPacketAttributeDetailsList = new ArrayList<PacketIdentificationDetails.PacketAttributeDetails>();
	private int sFrstFxdBytIndx;
	private int sLastFxdBytIndx;
	
	public PacketIdentificationDetails(PacketType codeKind,
			byte[] sSearchBytes, List<Integer> anythingAllowedBytes,
			int firstFxdBytIndx, int lastFxdBytIndx) {
		super();
		this.sSearchBytes = sSearchBytes;
		this.sPacketType = codeKind;
		this.sAnythingAllowedBytes = anythingAllowedBytes;
		this.sFrstFxdBytIndx = firstFxdBytIndx;
		this.sLastFxdBytIndx = lastFxdBytIndx;		
	}

	public int getFrstFxdBytIndx() {
		return sFrstFxdBytIndx;
	}

	public int getLastFxdBytIndx() {
		return sLastFxdBytIndx;
	}

	public PacketType getPacketType() {
		return sPacketType;
	}

	public byte[] getSearchBytes() {
		return sSearchBytes;
	}

	public boolean compareWildByteInfo(int index, byte value) {
		if (sAnythingAllowedBytes.contains(index)) {
			return true;
		}
		for (WildByteInfo wildByteInfo : sWildByteInfoList) {
			if (wildByteInfo.compare(index, value)) {
				return true;
			}
		}
		return false;
	}

	public void addWildBytes(WildByteInfo... wildBytes) {
		for (WildByteInfo wildByte : wildBytes) {
			sWildByteInfoList.add(wildByte);
		}
	}

	public void addPacketAttributeDetails(
			PacketAttributeDetails... packetAttributes) {
		for (PacketAttributeDetails packetAttr : packetAttributes) {
			sPacketAttributeDetailsList.add(packetAttr);
		}
	}

	public List<PacketAttribute> extractAttributes(byte[] matchedBytes) {
		List<PacketAttribute> packetAttributes = new ArrayList<PacketAttribute>();
		for (PacketAttributeDetails packetAttr : sPacketAttributeDetailsList) {
			if (packetAttr.compare(matchedBytes)) {
				Log.d(LOG_TAG, packetAttr.toString());
				packetAttributes.addAll(packetAttr
						.formatPacketAttributes(matchedBytes));
			}
		}
		return packetAttributes;
	}

	public abstract class PacketAttributeDetails {
		protected int sCodeStartByte;
		protected int sCodeEndByte;
		protected PacketAttributeType sPacketAttrType;

		public PacketAttributeDetails(int codeStartByte, int codeEndByte,
				PacketAttributeType packetAttrType) {
			super();
			this.sCodeStartByte = codeStartByte;
			this.sCodeEndByte = codeEndByte;
			this.sPacketAttrType = packetAttrType;
		}

		protected byte[] extract(byte[] matchedBytes) {
			byte[] extractedCodes = new byte[sCodeEndByte - sCodeStartByte];
			for (int i = sCodeStartByte; i < sCodeEndByte; i++) {
				extractedCodes[i - sCodeStartByte] = matchedBytes[i];
			}
			return extractedCodes;
		}

		public List<PacketAttribute> formatPacketAttributes(
				byte[] matchedBytes) {
			return sPacketAttrType.format(extract(matchedBytes));
		}

		public int getPacketAttrTypeId() {
			return sPacketAttrType.getTypeId();
		}

		public abstract boolean compare(byte[] matchedBytes);

	}

	public class ByteLevelPacketAttributeDetails extends PacketAttributeDetails {
		private byte[] sStandardBytes;

		public ByteLevelPacketAttributeDetails(int codeStartByte,
				int codeEndByte, PacketAttributeType codeDisplayText) {
			this(codeStartByte, codeEndByte, ANYTHING, codeDisplayText);
		}

		public ByteLevelPacketAttributeDetails(int codeStartByte,
				int codeEndByte, byte[] standardBytes,
				PacketAttributeType codeDisplayText) {
			super(codeStartByte, codeEndByte, codeDisplayText);
			this.sStandardBytes = standardBytes;
		}

		public boolean compare(byte[] matchedBytes) {
			if (sStandardBytes == ANYTHING) {
				return true;
			} else {
				byte[] extractedBytes = extract(matchedBytes);
				if (extractedBytes.length != sStandardBytes.length) {
					return false;
				}
				for (int i = 0; i < extractedBytes.length; i++) {
					if (extractedBytes[i] != sStandardBytes[i]) {
						return false;
					}
				}
				return true;
			}
		}

	}

	public class BitLevelPacketAttributeDetails extends PacketAttributeDetails {
		private boolean[] sBitSequence;
		private int sBitStartIndex;
		private int sNumberOfBits;

		public BitLevelPacketAttributeDetails(int sCodeStartByte,
				int bitStartIndex, int numberOfBits, boolean[] bitSequence,
				PacketAttributeType codeDisplayText) {
			super(sCodeStartByte, sCodeStartByte + 1, codeDisplayText);
			this.sBitSequence = bitSequence;
			this.sBitStartIndex = bitStartIndex;
			this.sNumberOfBits = numberOfBits;
		}

		@Override
		public boolean compare(byte[] matchedBytes) {
			byte[] extractedBytes = extract(matchedBytes);
			if (extractedBytes != null && extractedBytes.length == 1) {
				for (int i = 0; i < sNumberOfBits; i++) {
					byte tmpByte = extractedBytes[0];
					byte singleByte = (byte) (tmpByte << (7 - (sBitStartIndex - i)));
					singleByte = (byte) (singleByte >> 7);
					int byteInInt = singleByte;
					if (sBitSequence[i] && (byteInInt == 1 || byteInInt == -1)) {
						continue;
					} else if (!sBitSequence[i] && byteInInt == 0) {
						continue;
					} else {
						return false;
					}
				}
			} else {
				return false;
			}
			return true;
		}
	}

	public class WildByteInfo {

		private byte[] sAllowedBytes;
		private int sByteIndex;

		public WildByteInfo(int byteIndex, byte... allowedBytes) {
			super();
			this.sByteIndex = byteIndex;
			this.sAllowedBytes = allowedBytes;
		}

		public boolean compare(int index, byte value) {
			if (sByteIndex != index) {
				return false;
			}
			// Anything is accepted
			if (sAllowedBytes.length == 0) {
				return true;
			}
			// If value is specified then it should match
			for (int i = 0; i < sAllowedBytes.length; i++) {
				if (value == sAllowedBytes[i]) {
					return true;
				}
			}
			return false;
		}
	}
}