package com.jbs.satfinder.data;

import java.util.ArrayList;

public class SystemInfo 
{
	String	mMyIp;
	String	mModelName;
	String	mHWVersion;
	String 	mSWVersion;
	int 	mCount;
	ArrayList<Frontend> mFrontends;
	
	public SystemInfo(String myIp, String modelName, String hwVersion, String swVersion, int count)
	{
		mMyIp = myIp;
		mModelName = modelName;
		mHWVersion = hwVersion;
		mSWVersion = swVersion;
		mCount = count;
	}
	
	public boolean addFrontend(int feId, String path, String type, String modelName)
	{
		if(mFrontends == null)
			mFrontends = new ArrayList<Frontend>();
		
		Frontend fe = new Frontend(feId, path, type, modelName);
		mFrontends.add(fe);
		return true;
	}
	
	public boolean addFrontend(Frontend fe)
	{
		if(mFrontends == null)
			mFrontends = new ArrayList<Frontend>();
		
		mFrontends.add(fe);
		return true;
	}
	
	public String	getMyIp()			{ return mMyIp;	}
	public String	getModelName()		{ return mModelName; }
	public String 	getHWVersion()		{ return mHWVersion; }
	public String 	getSWVersion()		{ return mSWVersion; }
	public int 		getCount()			{ return mCount; }
	public Frontend	getFrontendAt(int index)	{ return mFrontends.get(index); }
}


