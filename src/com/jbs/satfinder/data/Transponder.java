package com.jbs.satfinder.data;

public class Transponder 
{
	int		mRfId;
	int		mFreq;
	int		mSym;
	int		mOrgNetId;
	int		mNetId;
	int		mTsId;
	String	mPolar;
	String	mSystem;
	String	mModulation;
	int		mHasCach;
	int		mSatId;
	String	mAnswer;
	int		mStrength;
	int		mSnr;
	int 	mBer;
	int 	mUnc;
	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public Transponder(int rfId, int freq, int sym, int orgNetId, int netId, int tsId, String polar,
						String system, String modulation, int hasCach, int satId)
	{
		mRfId 		= rfId;
		mFreq		= freq;
		mSym		= sym;
		mOrgNetId	= orgNetId;
		mNetId		= netId;
		mTsId		= tsId;
		mPolar		= polar;
		mSystem		= system;
		mModulation	= modulation;
		mHasCach	= hasCach;
		mSatId		= satId;
		mAnswer		= "false";
		mStrength	= 0;
		mSnr		= 0;
		mBer		= 0;
		mUnc		= 0;
	}
	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public Transponder(int rfId, int freq, int sym, int orgNetId, int netId, int tsId, String polar,
						String system, String modulation, 
						int hasCach, int satId, String answer, int strength, int snr, int ber, int unc)
	{
		mRfId 		= rfId;
		mFreq		= freq;
		mSym		= sym;
		mOrgNetId	= orgNetId;
		mNetId		= netId;
		mTsId		= tsId;
		mPolar		= polar;
		mSystem		= system;
		mModulation	= modulation;
		mHasCach	= hasCach;
		mSatId		= satId;
		mAnswer		= answer;
		mStrength	= strength;
		mSnr		= snr;
		mBer		= ber;
		mUnc		= unc;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	public int		getRfId()		{ return mRfId; }
	public int 		getFreq() 		{ return mFreq; }
	public int	 	getSym() 		{ return mSym; }
	public int	 	getOrgNetId() 	{ return mOrgNetId; }
	public int 		getNetId()	 	{ return mNetId; }
	public int	 	getTsId() 		{ return mTsId; }
	public String 	getPolar() 		{ return mPolar; }
	public String 	getSystem() 	{ return mSystem; }
	public String 	getModulation()	{ return mModulation; }
	public int	 	getHasCach() 	{ return mHasCach; }
	public int	 	getSatId()	 	{ return mSatId; }
	
	public String	getAnswer()	 	{ return mAnswer; }
	public int	 	getStrength()	{ return mStrength; }
	public int	 	getSnr()	 	{ return mSnr; }
	public int	 	getBer()	 	{ return mBer; }
	public int	 	getUnc()	 	{ return mUnc; }
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public String toString()
	{
		String str = "";
		str += "mRfId = " + mRfId + ", mFreq = " + mFreq + ", mSym = " + mSym 
				+ ", mPolar = " + mPolar
				+ ", mSatId = " + mSatId; 
				
		return str;
	}	
}
