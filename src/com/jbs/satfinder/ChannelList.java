package com.jbs.satfinder;


import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class ChannelList extends TabActivity 
{
	final static String CHANNEL_ALL			= "All";
	final static String CHANNEL_FTA			= "Free";
	final static String CHANNEL_SCRAMBLE	= "Scramble";
	final static String CHANNEL_FAVORITE	= "Favorite";
	
	TabHost mTabHost;
	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.tab_host);
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup();
        //tabHost = getTabHost();
		
		// 탭 사이 간격 조정  
		//tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
        
		setupTab(new TextView(this), CHANNEL_ALL);
		setupTab(new TextView(this), CHANNEL_FAVORITE);		
		setupTab(new TextView(this), CHANNEL_FTA);
		setupTab(new TextView(this), CHANNEL_SCRAMBLE);


	}	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	private void setupTab(final View view, final String tag)
	{
		Intent intent = null;
		View tabview = createTabView(mTabHost.getContext(), tag);

		// TabSpec은 공개된 생성자가 없으므로 직접 생성할 수 없으며, TabHost의 newTabSpec메서드로 생성
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview);

		if(tag.equals(CHANNEL_ALL))
			intent = new Intent(ChannelList.this, ChannelListAll.class);
		else if(tag.equals(CHANNEL_FTA))
			intent = new Intent(ChannelList.this, ChannelListFTA.class);
		else if(tag.equals(CHANNEL_SCRAMBLE))
			intent = new Intent(ChannelList.this, ChannelListScrm.class);
		else if(tag.equals(CHANNEL_FAVORITE))
			intent = new Intent(ChannelList.this, ChannelListFav.class);

		intent.putExtra("zone2_code", 1);
		setContent.setContent(intent);
		
		mTabHost.addTab(setContent);
	}	
	//--------------------------------------------------------------------------------------------//
	//
	//--------------------------------------------------------------------------------------------//	
	// Tab에 나타날 View를 구성
    private static View createTabView(final Context context, final String text)
    {
        // layout inflater를 이용해 xml 리소스를 읽어옴
    	View view = LayoutInflater.from(context).inflate(R.layout.tab_widget, null);
        ImageView img;

        if(text.equals(CHANNEL_ALL))
        {
            img = (ImageView)view.findViewById(R.id.tabs_image);
            //img.setImageResource(R.drawable.tab_bus_selector);
            img.setImageResource(R.drawable.ico_ch_all_selector);
        }
        else if(text.equals(CHANNEL_FTA))
        {   
            img = (ImageView)view.findViewById(R.id.tabs_image);
            //img.setImageResource(R.drawable.tab_notice_selector);
            img.setImageResource(R.drawable.ico_ch_fta_selector);
        }
        else if(text.equals(CHANNEL_SCRAMBLE))
        {
            img = (ImageView)view.findViewById(R.id.tabs_image);
            //img.setImageResource(R.drawable.tab_bus_selector);
            img.setImageResource(R.drawable.ico_ch_scramble_selector);        	
        }
        else if(text.equals(CHANNEL_FAVORITE))
        {
            img = (ImageView)view.findViewById(R.id.tabs_image);
            //img.setImageResource(R.drawable.tab_bus_selector);
            img.setImageResource(R.drawable.ico_ch_fav_selector);        	
        }

        TextView tv = (TextView) view.findViewById(R.id.tabs_text);
        tv.setText(text);
        return view;
    }	
}
