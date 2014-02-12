package com.jbs.satfinder;


import java.util.ArrayList;
import java.util.Iterator;

import com.jbs.satfinder.data.Channel;
import com.jbs.satfinder.data.DbManager;
import com.jbs.satfinder.data.Satellite;
import com.jbs.satfinder.data.Transponder;
import com.jbs.satfinder.data.Transponders;




import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class SatEdit extends Activity 
{
	static final int COLOR_SEL		= Color.CYAN;
	static final int COLOR_UNSEL	= Color.TRANSPARENT;
	
	//--------------------------------------------------
	String[] mArrLnbF = {"5150", "9750/10550", "9750/10600", "9750/10700","5150", 
						"9750/10550", "9750/10600", "9750/10700",};
	int mIdxLnbF = -1;
	
	//--------------------------------------------------
	String[] mArrLnbP = {"Auto", "On", "Off"};
	int mIdxLnbP = -1;
	
	//--------------------------------------------------
	String[] mArrTp = null;
	int mIdxTp = -1;
	ArrayList<Transponder> mArrTransponders = null;
	
	//--------------------------------------------------
	String[] mArr22KHz = {"Auto", "On", "Off"};
	int mIdx22KHz = -1;	
	
	//--------------------------------------------------
	String[] mArrDiSEcQ = {"DiSEqC 1.0", "DiSEqC 1.1", "DiSEqC 1.2", "USALS"};
	int mIdxDiSEqC = -1;
	
	//--------------------------------------------------
	String[] mArrPort = {"Port 0", "Port 1", "Port 2", "Port 3"};
	int mIdxPort = -1;	
	

	
	
	//--------------------------------------------------
	private Satellite mSat = null;
	private int mSatId = -1;
	
	
	TextView tvLnbF = null;
	TextView tvLnbP = null;
	TextView tvTp = null;
	TextView tv22KHz = null;
	TextView tvDiSEqC = null;
	TextView tvPort = null;
	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sat_edit);
		
		Intent intent = getIntent();
		mSatId = intent.getIntExtra("sat_id", -1);
		
		//----------------------------------------------------------------------------------------//
		mSat = DbManager.getSatInfoBySatId(mSatId);
		mArrTransponders = (new Transponders()).getTransponders(mSatId);
		
		updateSatData();

		//----------------------------------------------------------------------------------------//
		setTouchListenerLnbF();
		setTouchListenerLnbP();
		setTouchListenerTp();
		setTouchListener22KHz();
		setTouchListenerDiSEqC();
		setTouchListenerPort();
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
    public void onStart()
    {
    	super.onStart();
    }
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
    public void onPause()
    {
    	super.onPause();
    }		
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	protected void updateSatData()
	{
		View v = null;

		
		//----------------------------------------------------------------------------------------//
		v = findViewById(R.id.loSatInfo);
		
		TextView tvSatName = (TextView)v.findViewById(R.id.tvSatName);
		tvSatName.setText(mSat.getName());
		
		//----------------------------------------------------------------------------------------//
		TextView tvSatPosition = (TextView)v.findViewById(R.id.tvPosition);
		double satPos = mSat.getSatPos();
		
		if(satPos > 1800)
			tvSatPosition.setText( (satPos - 1800) / 10 + " W");
		else
			tvSatPosition.setText( satPos / 10 + " E");
		
		//----------------------------------------------------------------------------------------//
		v = findViewById(R.id.LnbF);
		v = v.findViewById(R.id.loLnbF);
		TextView tvLnbF = (TextView)v.findViewById(R.id.tvLnbF);
		tvLnbF.setText("" + mSat.getLnbfHi());
		
		//----------------------------------------------------------------------------------------//
		v = findViewById(R.id.LnbP);
		v = v.findViewById(R.id.loLnbP);
		TextView tvLnbP = (TextView)v.findViewById(R.id.tvLnbP);
		tvLnbP.setText("" + mSat.getLnbp());
		
		//----------------------------------------------------------------------------------------//
		v = findViewById(R.id.Tp);
		v = v.findViewById(R.id.loTp);
		TextView tvTp = (TextView)v.findViewById(R.id.tvTp);
		
		Transponder tp = findTpByTpId(mSat.getKeyTp());
		String strTp =  tp.getFreq() + " / " + tp.getPolar() + " / " + tp.getSym();
		tvTp.setText(strTp);
		
		//----------------------------------------------------------------------------------------//
		v = findViewById(R.id._22KHz);
		v = v.findViewById(R.id.lo22KHz);
		TextView tv22KHz = (TextView)v.findViewById(R.id.tv22KHz);
		tv22KHz.setText("" + mSat.get22Khz());
		
		//----------------------------------------------------------------------------------------//
		v = findViewById(R.id.DiSEqC);
		v = v.findViewById(R.id.loDiSEqC);
		TextView tvDiSEqC = (TextView)v.findViewById(R.id.tvDiSEqC);
		tvDiSEqC.setText("" + mSat.getDiseqc());
		
		//----------------------------------------------------------------------------------------//
		v = findViewById(R.id.Port);
		v = v.findViewById(R.id.loPort);
		TextView tvPort = (TextView)v.findViewById(R.id.tvPort);
		tvPort.setText("" + mSat.getDiseqcPort());
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected Transponder findTpByTpId(int tpId)
	{
		Iterator iter = mArrTransponders.iterator();
		
		while(iter.hasNext())
		{
			Transponder tp = (Transponder)iter.next();
			
			if( tp.getRfId() == tpId )
				return tp;
		}
		
		return null;
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected void setTouchListenerLnbF()
	{
		//----------------------------------------------------------------------------------------//
		View view = findViewById(R.id.LnbF);
		view = view.findViewById(R.id.loLnbF);
		tvLnbF = (TextView)view.findViewById(R.id.tvLnbF);

		tvLnbF.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if(tvLnbF.getClass() == v.getClass())
					{
						tvLnbF.setTextColor(Color.RED);
						tvLnbF.setBackgroundColor(COLOR_SEL);
					}
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					if(tvLnbF.getClass() == v.getClass())
					{
						tvLnbF.setTextColor(Color.BLACK);
						tvLnbF.setBackgroundColor(COLOR_UNSEL);
						
						//------------------------------------------------------------------------//
						new AlertDialog.Builder(SatEdit.this)
						.setTitle("LNB Frequency")
						//.setIcon(R.drawable.icon)
						.setSingleChoiceItems(mArrLnbF, mIdxLnbF, 
								new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog, int which) 
							{
								mIdxLnbF = which;
							}
						})
						.setPositiveButton("OK", new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog, int whichButton) 
							{
//								String[] foods = getResources().getStringArray(R.array.foods);
//								TextView text = (TextView)findViewById(R.id.text);
//								text.setText("선택한 음식 = " + foods[mSelect]);
							}
						})
						.setNegativeButton("Cancel", null)
						.show();							
					}
				}
				return true;
			}
		});			
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected void setTouchListenerLnbP()
	{
		//----------------------------------------------------------------------------------------//
		View view = findViewById(R.id.LnbP);
		view = view.findViewById(R.id.loLnbP);
		tvLnbP = (TextView)view.findViewById(R.id.tvLnbP);

		tvLnbP.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if(tvLnbP.getClass() == v.getClass())
					{
						tvLnbP.setTextColor(Color.RED);
						tvLnbP.setBackgroundColor(COLOR_SEL);
					}
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					if(tvLnbP.getClass() == v.getClass())
					{
						tvLnbP.setTextColor(Color.BLACK);
						tvLnbP.setBackgroundColor(COLOR_UNSEL);
						
						//------------------------------------------------------------------------//
						new AlertDialog.Builder(SatEdit.this)
						.setTitle("LNB Power")
						//.setIcon(R.drawable.icon)
						.setSingleChoiceItems(mArrLnbP, mIdxLnbP, 
								new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog, int which) 
							{
								mIdxLnbP = which;
							}
						})
						.setPositiveButton("OK", new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog, int whichButton) 
							{
//								String[] foods = getResources().getStringArray(R.array.foods);
//								TextView text = (TextView)findViewById(R.id.text);
//								text.setText("선택한 음식 = " + foods[mSelect]);
							}
						})
						.setNegativeButton("Cancel", null)
						.show();
					}
				}
				return true;
			}
		});			
	}	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected void setTouchListenerTp()
	{
		//----------------------------------------------------------------------------------------//
		View view = findViewById(R.id.Tp);
		view = view.findViewById(R.id.loTp);
		tvTp = (TextView)view.findViewById(R.id.tvTp);

		tvTp.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if(tvTp.getClass() == v.getClass())
					{
						tvTp.setTextColor(Color.RED);
						tvTp.setBackgroundColor(COLOR_SEL);
					}
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					if(tvTp.getClass() == v.getClass())
					{
						tvTp.setTextColor(Color.BLACK);
						tvTp.setBackgroundColor(COLOR_UNSEL);
						
						//------------------------------------------------------------------------//
						mArrTp = new String[mArrTransponders.size()];
						Iterator iter = mArrTransponders.iterator();
						
						for(int idx = 0; idx < mArrTransponders.size(); idx++)
						{
							mArrTp[idx] = mArrTransponders.get(idx).getFreq() + " / " 
										+ mArrTransponders.get(idx).getPolar() + " / "
										+mArrTransponders.get(idx).getSym();
						}
						
						//------------------------------------------------------------------------//
						new AlertDialog.Builder(SatEdit.this)
						.setTitle("Trnaponders")
						//.setIcon(R.drawable.icon)
						.setSingleChoiceItems(mArrTp, mIdxTp, new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog, int which) 
							{
								mIdxTp = which;
							}
						})
						.setPositiveButton("OK", new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog, int whichButton) 
							{
								mSat.setKeyTp(mArrTransponders.get(mIdxTp).getRfId());
								updateSatData();
							}
						})
						.setNegativeButton("Cancel", null)
						.show();	
					}
				}
				return true;
			}
		});			
	}	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected void setTouchListener22KHz()
	{
		//----------------------------------------------------------------------------------------//
		View view = findViewById(R.id._22KHz);
		view = view.findViewById(R.id.lo22KHz);
		tv22KHz = (TextView)view.findViewById(R.id.tv22KHz);

		tv22KHz.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if(tv22KHz.getClass() == v.getClass())
					{
						tv22KHz.setTextColor(Color.RED);
						tv22KHz.setBackgroundColor(COLOR_SEL);
					}
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					if(tv22KHz.getClass() == v.getClass())
					{
						tv22KHz.setTextColor(Color.BLACK);
						tv22KHz.setBackgroundColor(COLOR_UNSEL);
						
						//------------------------------------------------------------------------//
						new AlertDialog.Builder(SatEdit.this)
						.setTitle("22KHz")
						//.setIcon(R.drawable.icon)
						.setSingleChoiceItems(mArr22KHz, mIdx22KHz, 
								new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog, int which) 
							{
								mIdx22KHz = which;
							}
						})
						.setPositiveButton("OK", new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog, int whichButton) 
							{
//								String[] foods = getResources().getStringArray(R.array.foods);
//								TextView text = (TextView)findViewById(R.id.text);
//								text.setText("선택한 음식 = " + foods[mSelect]);
							}
						})
						.setNegativeButton("Cancel", null)
						.show();						
					}
				}
				return true;
			}
		});			
	}	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected void setTouchListenerDiSEqC()
	{
		//----------------------------------------------------------------------------------------//
		View view = findViewById(R.id.DiSEqC);
		view = view.findViewById(R.id.loDiSEqC);
		tvDiSEqC = (TextView)view.findViewById(R.id.tvDiSEqC);

		tvDiSEqC.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if(tvDiSEqC.getClass() == v.getClass())
					{
						tvDiSEqC.setTextColor(Color.RED);
						tvDiSEqC.setBackgroundColor(COLOR_SEL);
					}
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					if(tvDiSEqC.getClass() == v.getClass())
					{
						tvDiSEqC.setTextColor(Color.BLACK);
						tvDiSEqC.setBackgroundColor(COLOR_UNSEL);
						
						//------------------------------------------------------------------------//
						new AlertDialog.Builder(SatEdit.this)
						.setTitle("DiSEqC")
						//.setIcon(R.drawable.icon)
						.setSingleChoiceItems(mArrDiSEcQ, mIdxDiSEqC, 
								new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog, int which) 
							{
								mIdxDiSEqC = which;
							}
						})
						.setPositiveButton("OK", new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog, int whichButton) 
							{
//								String[] foods = getResources().getStringArray(R.array.foods);
//								TextView text = (TextView)findViewById(R.id.text);
//								text.setText("선택한 음식 = " + foods[mSelect]);
							}
						})
						.setNegativeButton("Cancel", null)
						.show();
					}
				}
				return true;
			}
		});			
	}
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	protected void setTouchListenerPort()
	{
		//----------------------------------------------------------------------------------------//
		View view = findViewById(R.id.Port);
		view = view.findViewById(R.id.loPort);
		tvPort = (TextView)view.findViewById(R.id.tvPort);

		tvPort.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if(tvPort.getClass() == v.getClass())
					{
						tvPort.setTextColor(Color.RED);
						tvPort.setBackgroundColor(COLOR_SEL);
					}
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					if(tvPort.getClass() == v.getClass())
					{
						tvPort.setTextColor(Color.BLACK);
						tvPort.setBackgroundColor(COLOR_UNSEL);
						
						//------------------------------------------------------------------------//
						new AlertDialog.Builder(SatEdit.this)
						.setTitle("Port")
						//.setIcon(R.drawable.icon)
						.setSingleChoiceItems(mArrPort, mIdxPort, 
								new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog, int which) 
							{
								mIdxPort = which;
							}
						})
						.setPositiveButton("OK", new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog, int whichButton) 
							{
//								String[] foods = getResources().getStringArray(R.array.foods);
//								TextView text = (TextView)findViewById(R.id.text);
//								text.setText("선택한 음식 = " + foods[mSelect]);
							}
						})
						.setNegativeButton("Cancel", null)
						.show();
					}
				}
				return true;
			}
		});			
	}		
}
