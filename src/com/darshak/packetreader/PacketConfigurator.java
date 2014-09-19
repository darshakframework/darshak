package com.darshak.packetreader;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.darshak.constants.PacketType.GSM_INIT_AUTH_REQ;
import static com.darshak.constants.PacketType.GSM_INIT_CIPHER_MODE;
import static com.darshak.constants.PacketType.GSM_INIT_SERV_REQ;
import static com.darshak.constants.PacketType.SILENT_SMS;
import static com.darshak.constants.PacketType.SYS_INFO_3;
import static com.darshak.constants.PacketType.TMSI_RELOCATION;
import static com.darshak.constants.PacketType._3G_AUTH_RES;
import static com.darshak.constants.PacketType._3G_INIT_AUTH_REQ;
import static com.darshak.constants.PacketType._3G_SEC_MODE_CMD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.darshak.constants.Constants;
import com.darshak.constants.PacketType;
import com.darshak.util.Utils;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 * 
 */
public class PacketConfigurator {

	private static final String LOG_TAG = PacketConfigurator.class
			.getSimpleName();

	private static final List<PacketIdentificationDetails> s3GPacketList = new ArrayList<PacketIdentificationDetails>();

	private static final List<PacketIdentificationDetails> sGSMPacketList = new ArrayList<PacketIdentificationDetails>();

	private static final List<PacketIdentificationDetails> sSilentSMSPacketList = new ArrayList<PacketIdentificationDetails>();

	private static final List<PacketIdentificationDetails> sProfilePacketList = new ArrayList<PacketIdentificationDetails>();

	static {
		// 3G
		s3GPacketList.add(cmServiceRequest());
		s3GPacketList.add(securityModeCommand1());
		s3GPacketList.add(securityModeCommand2());
		s3GPacketList.add(securityModeCommand3());
		s3GPacketList.add(securityModeCommand4());
		s3GPacketList.add(securityModeCommand5());
		s3GPacketList.add(securityModeCommand6());
		s3GPacketList.add(authenticationRequest());
		s3GPacketList.add(authenticationResponse());

		// GSM
		sGSMPacketList.add(gsmInitServiceRequestCode());
		sGSMPacketList.add(gsmInitCipheringModeCode());
		sGSMPacketList.add(gsmInitAuthenticationRequestCode());
		sGSMPacketList.add(tmsiRelocationCommand());

		// Silent SMS
		sSilentSMSPacketList.add(initPing3SilentSMSCodes());
		sSilentSMSPacketList.add(initPing4SilentSMSCodes());
		sSilentSMSPacketList.add(initSilentSMSTypeZero());
		sSilentSMSPacketList.add(initSilentSMSTypeZero_sender10());
	}

	public static List<PacketIdentificationDetails> getPacketsList(
			int codeType, Context context) {
		switch (codeType) {
		case Constants.GSM:
			return sGSMPacketList;
		case Constants._3G:
			return s3GPacketList;
		case Constants.SMS:
			return sSilentSMSPacketList;
		case Constants.PROFILE_PARAMS:
			sProfilePacketList.clear();
			sProfilePacketList.add(sysInfoTypeThree(context));
			return sProfilePacketList;
		default:
			return null;
		}
	}

	public static PacketIdentificationDetails cmServiceRequest() {
		byte[] cmSerReqBytSeq = { (byte) 0x05, (byte) 0x24, (byte) 0x00,
				(byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x05, (byte) 0xf4, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00 };
		List<Integer> anythingAllowedBytes = Arrays.asList(2, 4, 5, 6, 9, 10,
				11, 12);
		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				PacketType._3G_CM_SERV_REQ, cmSerReqBytSeq,
				anythingAllowedBytes, 0, 8);
		return packetIdentificationDetails;
	}

	// TODO
	/*
	 * private static PacketIdentificationDetails securityModeCommand() { byte
	 * initSecurityModeBytes[] = { (byte) 0x00, (byte) 0x0c, (byte) 0x00, (byte)
	 * 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
	 * (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte)
	 * 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
	 * 
	 * List<Integer> anythingAllowedBytes = Arrays.asList(0, 2, 3, 4, 5, 7, 8,
	 * 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
	 * 
	 * PacketIdentificationDetails packetIdentificationDetails = new
	 * PacketIdentificationDetails( _3G_SEC_MODE_CMD, initSecurityModeBytes,
	 * anythingAllowedBytes, -1, -1);
	 * 
	 * PacketIdentificationDetails.WildByteInfo wildByteInfo =
	 * packetIdentificationDetails.new WildByteInfo( 1, (byte) 0x0c, (byte)
	 * 0x0d, (byte) 0x20);
	 * 
	 * PacketIdentificationDetails.WildByteInfo wildByteInfo2 =
	 * packetIdentificationDetails.new WildByteInfo( 6, (byte) 0x4a, (byte)
	 * 0x28);
	 * 
	 * PacketIdentificationDetails.WildByteInfo[] wildBytes = { wildByteInfo,
	 * wildByteInfo2 }; packetIdentificationDetails.addWildBytes(wildBytes);
	 * 
	 * return packetIdentificationDetails; }
	 */

	private static PacketIdentificationDetails securityModeCommand1() {
		byte initSecurityModeBytes[] = { (byte) 0x00, (byte) 0x0c, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x4a,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00 };

		List<Integer> secModeCmdAnyAllwdByt = Arrays.asList(0, 2, 3, 4, 5, 7,
				8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);

		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				_3G_SEC_MODE_CMD, initSecurityModeBytes, secModeCmdAnyAllwdByt,
				1, 6);
		return packetIdentificationDetails;
	}

	private static PacketIdentificationDetails securityModeCommand2() {
		byte initSecurityModeBytes[] = { (byte) 0x00, (byte) 0x0c, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x28,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00 };

		List<Integer> secModeCmdAnyAllwdByt = Arrays.asList(0, 2, 3, 4, 5, 7,
				8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);

		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				_3G_SEC_MODE_CMD, initSecurityModeBytes, secModeCmdAnyAllwdByt,
				1, 6);
		return packetIdentificationDetails;
	}

	private static PacketIdentificationDetails securityModeCommand3() {
		byte initSecurityModeBytes[] = { (byte) 0x00, (byte) 0x0d, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x4a,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00 };

		List<Integer> secModeCmdAnyAllwdByt = Arrays.asList(0, 2, 3, 4, 5, 7,
				8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);

		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				_3G_SEC_MODE_CMD, initSecurityModeBytes, secModeCmdAnyAllwdByt,
				1, 6);
		return packetIdentificationDetails;
	}

	private static PacketIdentificationDetails securityModeCommand4() {
		byte initSecurityModeBytes[] = { (byte) 0x00, (byte) 0x0d, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x28,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00 };

		List<Integer> secModeCmdAnyAllwdByt = Arrays.asList(0, 2, 3, 4, 5, 7,
				8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);

		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				_3G_SEC_MODE_CMD, initSecurityModeBytes, secModeCmdAnyAllwdByt,
				1, 6);
		return packetIdentificationDetails;
	}

	private static PacketIdentificationDetails securityModeCommand5() {
		byte initSecurityModeBytes[] = { (byte) 0x00, (byte) 0x20, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x4a,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00 };

		List<Integer> secModeCmdAnyAllwdByt = Arrays.asList(0, 2, 3, 4, 5, 7,
				8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);

		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				_3G_SEC_MODE_CMD, initSecurityModeBytes, secModeCmdAnyAllwdByt,
				1, 6);
		return packetIdentificationDetails;
	}

	private static PacketIdentificationDetails securityModeCommand6() {
		byte initSecurityModeBytes[] = { (byte) 0x00, (byte) 0x20, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x28,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00 };

		List<Integer> secModeCmdAnyAllwdByt = Arrays.asList(0, 2, 3, 4, 5, 7,
				8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);

		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				_3G_SEC_MODE_CMD, initSecurityModeBytes, secModeCmdAnyAllwdByt,
				1, 6);
		return packetIdentificationDetails;
	}

	private static PacketIdentificationDetails authenticationRequest() {
		byte packetBytes[] = { (byte) 0x05, (byte) 0x12, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x20, (byte) 0x10, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00 };

		List<Integer> anythingAllowedBytes = Arrays.asList(2, 3, 4, 5, 6, 7, 8,
				9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 21, 22, 23, 24, 25, 26,
				27, 28, 29, 30, 31, 32, 33, 34, 35, 36);

		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				_3G_INIT_AUTH_REQ, packetBytes, anythingAllowedBytes, 0, 20);

		return packetIdentificationDetails;
	}

	private static PacketIdentificationDetails authenticationResponse() {
		byte[] packetBytes = { (byte) 0x05, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x21,
				(byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
		List<Integer> anythingAllowedBytes = Arrays.asList(1, 2, 3, 4, 5, 8, 9,
				10, 11);

		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				_3G_AUTH_RES, packetBytes, anythingAllowedBytes, 0, 7);

		return packetIdentificationDetails;
	}

	public static PacketIdentificationDetails gsmInitServiceRequestCode() {
		byte packetBytes[] = { (byte) 0x01, (byte) 0x3f, (byte) 0x35,
				(byte) 0x05, (byte) 0x24, (byte) 0x11, (byte) 0x03,
				(byte) 0x53, (byte) 0x19, (byte) 0x92, (byte) 0x05,
				(byte) 0xf4, (byte) 0xa4, (byte) 0x5c, (byte) 0xe1, (byte) 0x40 };
		List<Integer> anythingAllowedBytes = Arrays.asList(1, 5, 7, 9, 12, 13,
				14, 15);

		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				GSM_INIT_SERV_REQ, packetBytes, anythingAllowedBytes, 0, 11);

		return packetIdentificationDetails;
	}

	private static PacketIdentificationDetails gsmInitCipheringModeCode() {
		byte packetBytes[] = { (byte) 0x03, (byte) 0x00, (byte) 0x0d,
				(byte) 0x06, (byte) 0x35, (byte) 0x00 };

		List<Integer> anythingAllowedBytes = Arrays.asList(1, 5);

		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				GSM_INIT_CIPHER_MODE, packetBytes, anythingAllowedBytes, 0, 4);

		return packetIdentificationDetails;
	}

	private static PacketIdentificationDetails gsmInitAuthenticationRequestCode() {
		byte packetBytes[] = { (byte) 0x05, (byte) 0x12, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20 };

		List<Integer> anythingAllowedBytes = Arrays.asList(2, 3, 4, 5, 6, 7, 8,
				9, 10, 11, 12, 13, 14, 15, 16, 17, 18);

		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				GSM_INIT_AUTH_REQ, packetBytes, anythingAllowedBytes, 0, 19);

		return packetIdentificationDetails;
	}

	private static PacketIdentificationDetails tmsiRelocationCommand() {
		byte packetBytes[] = { (byte) 0x03, (byte) 0x86, (byte) 0x35,
				(byte) 0x05, (byte) 0x1a, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x05,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x2b };

		List<Integer> anythingAllowedBytes = Arrays.asList(5, 6, 7, 8, 9, 11,
				12, 13, 14, 15);

		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				TMSI_RELOCATION, packetBytes, anythingAllowedBytes, 0, 16);

		return packetIdentificationDetails;
	}

	private static PacketIdentificationDetails initPing3SilentSMSCodes() {
		byte packetBytes[] = { (byte) 0x64, (byte) 0x00, (byte) 0x91,
				(byte) 0x94, (byte) 0x61, (byte) 0x90, (byte) 0x90,
				(byte) 0x85, (byte) 0x19, (byte) 0xf6, (byte) 0x00,
				(byte) 0x04, (byte) 0x41, (byte) 0x10, (byte) 0x82,
				(byte) 0x22, (byte) 0x21, (byte) 0x05, (byte) 0x40,
				(byte) 0x08, (byte) 0x06, (byte) 0x05, (byte) 0x04,
				(byte) 0x0b, (byte) 0x84, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

		List<Integer> anythingAllowedBytes = Arrays.asList(2, 3, 4, 5, 6, 7, 8,
				9, 12, 13, 14, 15, 16, 17, 18, 27);

		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				SILENT_SMS, packetBytes, anythingAllowedBytes, 0, 19);

		PacketIdentificationDetails.WildByteInfo wildByteInfo = packetIdentificationDetails.new WildByteInfo(
				1, (byte) 0x0b, (byte) 0x0c, (byte) 0x0d);
		PacketIdentificationDetails.WildByteInfo[] wildBytes = { wildByteInfo };
		packetIdentificationDetails.addWildBytes(wildBytes);

		return packetIdentificationDetails;
	}

	// (PID 64)
	private static PacketIdentificationDetails initSilentSMSTypeZero() {
		byte packetBytes[] = { (byte) 0x24, (byte) 0x00, (byte) 0x91,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

		List<Integer> anythingAllowedBytes = Arrays.asList(3, 4, 5, 6, 7, 8,
				11, 12, 13, 14, 15, 16, 17, 18, 19);

		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				SILENT_SMS, packetBytes, anythingAllowedBytes, 0, 10);

		PacketIdentificationDetails.WildByteInfo wildByteInfo = packetIdentificationDetails.new WildByteInfo(
				1, (byte) 0x0b, (byte) 0x0c, (byte) 0x0d);
		PacketIdentificationDetails.WildByteInfo[] wildBytes = { wildByteInfo };
		packetIdentificationDetails.addWildBytes(wildBytes);

		return packetIdentificationDetails;
	}

	private static PacketIdentificationDetails initSilentSMSTypeZero_sender10() {
		byte packetBytes[] = { (byte) 0x24, (byte) 0x0a, (byte) 0x91,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

		List<Integer> anythingAllowedBytes = Arrays.asList(3, 4, 5, 6, 7, 10,
				11, 12, 13, 14, 15, 16, 17, 18);

		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				SILENT_SMS, packetBytes, anythingAllowedBytes, 0, 9);

		return packetIdentificationDetails;
	}

	private static PacketIdentificationDetails initPing4SilentSMSCodes() {
		byte packetBytes[] = { (byte) 0x64, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x7c, (byte) 0x06, (byte) 0x03, (byte) 0xbe,
				(byte) 0xaf, (byte) 0x84, (byte) 0x8c, (byte) 0x96,
				(byte) 0x98, (byte) 0x31, (byte) 0x32, (byte) 0x33,
				(byte) 0x34, (byte) 0x00, (byte) 0x8d, (byte) 0x93,
				(byte) 0xbe, (byte) 0x31, (byte) 0x32, (byte) 0x33,
				(byte) 0x34, (byte) 0x00 };

		List<Integer> anythingAllowedBytes = Arrays.asList(2, 3, 4, 5, 6, 7, 8,
				9, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26);

		PacketIdentificationDetails packetIdentificationDetails = new PacketIdentificationDetails(
				SILENT_SMS, packetBytes, anythingAllowedBytes, 0, 19);

		PacketIdentificationDetails.WildByteInfo wildByteInfo = packetIdentificationDetails.new WildByteInfo(
				1, (byte) 0x0b, (byte) 0x0c, (byte) 0x0d);
		PacketIdentificationDetails.WildByteInfo[] wildBytes = { wildByteInfo };
		packetIdentificationDetails.addWildBytes(wildBytes);

		return packetIdentificationDetails;
	}

	private static final byte sysInfo3Bytes[] = { (byte) 0x49, (byte) 0x06,
			(byte) 0x1b, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x2b,
			(byte) 0x2b };

	private static final List<Integer> sysInfo3AnythingAllwdByts = Arrays
			.asList(3, 4, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);

	private static final PacketIdentificationDetails.WildByteInfo[] sysInfo3WildBytes = {};

	// Get Location area identity, Cell ID
	private static PacketIdentificationDetails sysInfoTypeThree(Context context) {
		byte[] mobCountryCode = getMobCntryAndNetCode(context);
		sysInfo3Bytes[5] = mobCountryCode[0];
		sysInfo3Bytes[6] = mobCountryCode[1];

		sysInfo3Bytes[7] = mobCountryCode[2];

		PacketIdentificationDetails codeInfo = new PacketIdentificationDetails(
				SYS_INFO_3, sysInfo3Bytes, sysInfo3AnythingAllwdByts, 0, 22);

		codeInfo.addWildBytes(sysInfo3WildBytes);

		return codeInfo;
	}

	private static byte[] getMobCntryAndNetCode(Context context) {
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(TELEPHONY_SERVICE);
		String mobNetNetworkCode = telManager.getNetworkOperator();
		Log.d(LOG_TAG, "Mobile network code from telephony manager : "
				+ mobNetNetworkCode);

		byte[] strBytes = mobNetNetworkCode.getBytes();
		strBytes[0] = (byte) (strBytes[0] << 4);
		strBytes[0] = (byte) (strBytes[0] >> 4);

		strBytes[1] = (byte) (strBytes[1] << 4);

		strBytes[2] = (byte) (strBytes[2] << 4);
		strBytes[2] = (byte) (strBytes[2] >> 4);

		strBytes[3] = (byte) (strBytes[3] << 4);
		strBytes[3] = (byte) (strBytes[3] >> 4);

		strBytes[4] = (byte) (strBytes[4] << 4);

		byte resul1 = (byte) (strBytes[0] | strBytes[1]);
		byte resul2 = (byte) (strBytes[2] | (byte) 0xF0);
		byte resul3 = (byte) (strBytes[3] | strBytes[4]);

		Log.d(LOG_TAG,
				"Mobile country code and network code "
						+ Utils.formatHexBytes(new byte[] { resul1, resul2,
								resul3 }));
		return new byte[] { resul1, resul2, resul3 };
	}
}