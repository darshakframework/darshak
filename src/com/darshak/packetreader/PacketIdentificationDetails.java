package com.darshak.packetreader;

import java.util.ArrayList;
import java.util.List;

import com.darshak.constants.PacketType;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class PacketIdentificationDetails {

	private static final WildByteInfo[] WILD_BYTES_DEFAULT = {};

	private PacketType sPacketType;
	private byte sSearchBytes[];
	private List<Integer> sAnythingAllowedBytes;
	private List<WildByteInfo> sWildByteInfoList = new ArrayList<WildByteInfo>();
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
		wildBytes = (wildBytes == null) ? WILD_BYTES_DEFAULT : wildBytes;
		for (WildByteInfo wildByte : wildBytes) {
			sWildByteInfoList.add(wildByte);
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