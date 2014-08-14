package com.darshak.modal;

import java.io.Serializable;

import com.darshak.db.DatabaseSchema;

/**
 * 
 * @author Swapnil Udar & Ravishankar Borgaonkar
 *
 */
public class PaginationDetails implements Serializable {

	private static final long serialVersionUID = 8498133553200382061L;
	
	public static final int NUM_OF_RECORDS = 10;

	public static final long HIGHEST_POSIBLE_UID = 9223372036854775807l;

	private long sStartUid;

	private boolean sNext;

	private boolean sPrev;

	private boolean sMovingFwd;

	private long sEndUid;

	private String sWhereClause = null;

	private String sSortOrder = null;

	public PaginationDetails(long startUid, boolean next, boolean prev) {
		super();
		this.sStartUid = startUid;
		this.sNext = next;
		this.sPrev = prev;
		this.sMovingFwd = true;
		this.sEndUid = startUid;
		setQueryOnNext();
		setSortOrderOnNext();
	}

	public PaginationDetails(boolean next, boolean prev) {
		this(HIGHEST_POSIBLE_UID, next, prev);
	}

	public long getStartUid() {
		return sStartUid;
	}

	public void setStartUid(long startUid) {
		this.sStartUid = startUid;
	}

	public boolean isNext() {
		return sNext;
	}

	public void setNext(boolean next) {
		this.sNext = next;
	}

	public void setQueryOnNext() {
		sWhereClause = DatabaseSchema.LogEntrySchema.UID + " < "
				+ String.valueOf(getEndUid());
	}

	public void setSortOrderOnNext() {
		sSortOrder = DatabaseSchema.LogEntrySchema.TIME + " DESC ";
	}

	public boolean isPrev() {
		return sPrev;
	}

	public void setPrev(boolean prev) {
		this.sPrev = prev;
	}

	public void setQueryOnPrev() {
		sWhereClause = DatabaseSchema.LogEntrySchema.UID + " >= "
				+ String.valueOf(getStartUid());
	}

	public void setSortOrderOnPrev() {
		sSortOrder = DatabaseSchema.LogEntrySchema.TIME + " ASC ";
	}

	public String getWhereClause() {
		return sWhereClause;
	}

	public String getSortOrder() {
		return sSortOrder;
	}

	public boolean isMovingFwd() {
		return sMovingFwd;
	}

	public void setMovingFwd(boolean isMovingFwd) {
		this.sMovingFwd = isMovingFwd;
	}

	public long getEndUid() {
		return sEndUid;
	}

	public void setEndUid(long endUid) {
		this.sEndUid = endUid;
	}

	public void reset() {
		this.sStartUid = HIGHEST_POSIBLE_UID;
		this.sNext = false;
		this.sPrev = false;
		this.sMovingFwd = true;
		this.sEndUid = this.sStartUid;
		setQueryOnNext();
		setSortOrderOnNext();
	}

	@Override
	public String toString() {
		return "PaginationDetails [sStartUid=" + sStartUid + ", sNext="
				+ sNext + ", sPrev=" + sPrev + ", sMovingFwd=" + sMovingFwd
				+ ", sEndUid=" + sEndUid + ", sWhereClause=" + sWhereClause
				+ ", sSortOrder=" + sSortOrder + "]";
	}
}