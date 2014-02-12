package com.jbs.satfinder.data;

import android.content.Context;
import android.os.Handler;

public class DataTaskParam 
{
	//--------------------------------------------------------------------------------------------//
	public static final int OP_UNKNOWEN				= 0;
	public static final int OP_CONNECTING_SERVER	= OP_UNKNOWEN				+ 1;
	public static final int OP_ALIVE				= OP_CONNECTING_SERVER		+ 1;
	public static final int OP_GET_SYSTEMINFO		= OP_ALIVE					+ 1;
	public static final int OP_GET_SAT_LIST			= OP_GET_SYSTEMINFO			+ 1;
	public static final int OP_GET_TP_LIST			= OP_GET_SAT_LIST			+ 1;
	public static final int OP_TRY_LOCK				= OP_GET_TP_LIST			+ 1;
	public static final int OP_SCAN					= OP_TRY_LOCK				+ 1;
	
	public static final int OP_START_STREAM			= OP_SCAN					+ 1;
	public static final int OP_STOP_STREAM			= OP_START_STREAM					+ 1;
	
	//--------------------------------------------------------------------------------------------//
	public static final int OP_TP_UPDATE			= 1;
	public static final int OP_TP_ADD			= 1 + OP_TP_UPDATE;
	public static final int OP_TP_DELETE		= 1 + OP_TP_ADD;

	//--------------------------------------------------------------------------------------------//
	public static final int RET_ERROR				= 0;
	public static final int RET_SERVER_ALIVE		= 1;
	public static final int RET_SERVER_ALIVE_FAIL	= 2;
	
	//--------------------------------------------------------------------------------------------//
	public int opType	= OP_UNKNOWEN;
	public String strUrl = null;
	public int retCode = 0;
	public Context	mContext = null;
	public Handler	handler = null;
	
	public Satellite mSat = null;
	public Transponder mTp = null;
}
