package com.jbs.satfinder.data;


/*
 * 		query += "ch_id 		INTEGER PRIMARY KEY NOT NULL UNIQUE, ";
		query += "name 			CHAR NOT NULL, ";
		query += "fe_type 		CARR NOT NULL, ";
		query += "vid_pid		INTEGER DEFAULT -1, ";
		query += "aud_pid 		INTEGER DEFAULT -1, ";
		query += "pcr_pid 		INTEGER DEFAULT -1, ";
		query += "vid_type 		INTEGER DEFAULT -1, ";
		query += "aud_type 		INTEGER DEFAULT -1, ";
		query += "service_id 	INTEGER DEFAULT -1, ";
		query += "cas_id		INTEGER DEFAULT -1, ";
		query += "rf_id  		INTEGER DEFAULT -1, ";
 */
public class Channel 
{
	int mChId;
	String mName;
	String mFeType;
	int mVidPid;
	int mAudPid;
	int mPcrPid;
	int mVidType;
	int mAudType;
	int mServiceId;
	int mCasId;
	int mRfId;

	public Channel(int chId, String name, String feType, int vidPid, int audPid, int pcrPid, 
			int vidType, int audType, int serviceId, int casId, int rfId)
	{
		mChId 		= chId;
		mName 		= name;
		mFeType 	= feType;
		mVidPid 	= vidPid;
		mAudPid 	= audPid;
		mPcrPid 	= pcrPid;
		mVidType 	= vidType;
		mAudType 	= audType;
		mServiceId 	= serviceId;
		mCasId 		= casId;
		mRfId 		= rfId;
	}
	
	public int		getChId()		{ return mChId; }
	public String	getName()		{ return mName; }
	public String	getFeType()		{ return mFeType; }
	public int		getVidPid()		{ return mVidPid; }
	public int		getAudPid()		{ return mAudPid; }
	public int		getPcrPid()		{ return mPcrPid; }
	public int		getVidType()	{ return mVidType; }
	public int		getAudtype()	{ return mAudType; }
	public int		getServiceId()	{ return mServiceId; }
	public int		getCasId()		{ return mCasId; }
	public int		getRfId()		{ return mRfId; }
}
