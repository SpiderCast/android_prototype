package com.jbs.satfinder.data;

public class Frontend 
{
	int		mFeId;
	String	mPath;
	String	mType;
	String	mModelName;
	
	public Frontend(int feId, String path, String type, String modelName)
	{
		mFeId = feId;
		mPath = path;
		mType = type;
		mModelName = modelName;
	}
	
	public int 		getFeId()		{ return mFeId; }
	public String 	getPaht()		{ return mPath; }
	public String 	getType()		{ return mType; }
	public String 	getModelName()	{ return mModelName; }
}
