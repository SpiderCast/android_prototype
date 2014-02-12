package com.jbs.satfinder.data;

//------------------------------------------------------------------------------------------------//
//
//------------------------------------------------------------------------------------------------//
public class Satellite extends Object
{
	int		mSatId;
	String	mName;
	String	mLnb;
	int		mLnbfLow;
	int		mLnbfHi;
	int		mLnbfRef;
	String	mLnbp;
	String	m22Khz;
	String	mDiseqc;
	int		mDiseqcPort;
	int		mSatPos;
	int		mKeyTp;

	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public Satellite(int satId, String name, String lnb, int lnbfLow, int lnbfHi, int lnbRef, String lnbp,
					String _22Khz, String diseqc, int diseqcPort, int satPos, int keyTp)
	{
		mSatId 		= satId;
		mName 		= name;
		mLnb		= lnb;
		mLnbfLow	= lnbfLow;
		mLnbfHi		= lnbfHi;
		mLnbp		= lnbp;
		m22Khz		= _22Khz;
		mDiseqc		= diseqc;
		mDiseqcPort	= diseqcPort;
		mSatPos		= satPos;
		mKeyTp		= keyTp;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	public int		getSatId()		{ return mSatId; }
	public String 	getName() 		{ return mName; }
	public String 	getLnb() 		{ return mLnb; }
	public int	 	getLnbfLow() 	{ return mLnbfLow; }
	public int 		getLnbfHi() 	{ return mLnbfHi; }
	public int	 	getLnbfRef() 	{ return mLnbfRef; }
	public String 	getLnbp() 		{ return mLnbp; }
	public String 	get22Khz() 		{ return m22Khz; }
	public String 	getDiseqc() 	{ return mDiseqc; }
	public int	 	getDiseqcPort()	{ return mDiseqcPort; }
	public int 		getSatPos() 	{ return mSatPos; }
	public int	 	getKeyTp() 		{ return mKeyTp; }
	
	
	//--------------------------------------------------------------------------------------------//
	public void		setKeyTp(int keyTp)		{ mKeyTp = keyTp; }
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public String toString()
	{
		String str = "";
		str += "SatId = " + mSatId + ", name = " + mName + ", Pos = " + mSatPos 
				+ ", Lnb = " + mLnb + ", LnbfLow = " + mLnbfLow + ", LnbfHi = " + mLnbfHi
				+ ", LnbP = " + mLnbp + ", 22KHz = " + m22Khz + ", DiSEqC = " + mDiseqc
				+ ", Port = " + mDiseqcPort + ", Tp = " + mKeyTp;
		return str;
	}
}
//------------------------------------------------------------------------------------------------//
//
//------------------------------------------------------------------------------------------------//

