package com.kevinj.doghouse;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class DoghouseDiaries extends Activity 
{
	private String ComicURL = "http://www.thedoghousediaries.com/";
	private String curURL = "";
	private String previousStr = "";
	private String nextStr = "";
	private String imageStr = "";
	private String altText = "";
	private String beginningStr = "http://www.thedoghousediaries.com/?p=34";
	private String title = "";
	private String desc = "";
	private Bitmap comic = null;
	ProgressDialog progressDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent x = this.getIntent();
        String url = x.getStringExtra("url");
        
        Button beginning = (Button)findViewById(R.id.beginning);
        beginning.setText("Beginning");
        beginning.setOnClickListener(new OnClickListener()
        {
        	@Override
        	public void onClick(View v)
        	{
        		if (!previousStr.equals(""))
        		{
        			downloadText(beginningStr); 		
                }
        	}
        });
        
        Button previous = (Button)findViewById(R.id.prev);
        previous.setText("Previous");
        previous.setOnClickListener(new OnClickListener()
        {
        	@Override
        	public void onClick(View v)
        	{
        		if (!previousStr.equals(""))
        		{
        			downloadText(previousStr); 		
        		}
        	}
        });
        
        Button next = (Button)findViewById(R.id.next);
        next.setText("Next");
        next.setOnClickListener(new OnClickListener()
        {
        	@Override
        	public void onClick(View v)
        	{
        		if (!nextStr.equals(""))
        		{
        			downloadText(nextStr); 		
        		}
        	}
        });
        
        Button latest = (Button)findViewById(R.id.latest);
        latest.setText("Latest");
        latest.setOnClickListener(new OnClickListener()
        {
        	@Override
        	public void onClick(View v)
        	{
    			downloadText(ComicURL); 		
        	}
        });
        
        downloadText(ComicURL);
    }
    
    private void goAbout()
    {
    AlertDialog.Builder aboutBuild = new AlertDialog.Builder(this);
	aboutBuild.setCancelable(true);
    
    String aboutText = getString(R.string.about);
    TextView message = new TextView(this);
    final SpannableString s = new SpannableString(aboutText);
    Linkify.addLinks(s,Linkify.ALL);
    
    message.setText(s);
    message.setAutoLinkMask(1);
	
	aboutBuild.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
	{
        public void onClick(DialogInterface dialog, int id) 
        {
        	dialog.cancel();
        }
    });
	
	message.setMovementMethod(LinkMovementMethod.getInstance());
	aboutBuild.setView(message);
    AlertDialog aboutDialog = aboutBuild.create();
    
    aboutDialog.show();
    }    
    
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0,0, "About");
        return true;
    }
    
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case 0:
        	goAbout();
            return true;
        }        
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void downloadText(String urlStr)
    {
    	progressDialog = ProgressDialog.show(this, "", "Loading...");
    	curURL = urlStr;
    	if (urlStr != null)
		{
			HttpTextConnection textCon = new HttpTextConnection(textHandler);
			textCon.create(urlStr);
		}
    }
    
    private Handler textHandler = new Handler()
    {
    	public void handleMessage(Message msg)
    	{
    		super.handleMessage(msg);
    		String text = msg.getData().getString("text");
    		if (text != null)
    			processComic(text);
    	}
    };
    
    private void processComic(String text)
    {
    	AdView adView = (AdView)findViewById(R.id.adView);
    	adView.loadAd(new AdRequest());
    	
    	desc = "";
    	title = "";
    	
    	int start = text.indexOf("div class=\"object\"");
    	int imageStart = text.indexOf("<img src=",start)+10;
    	int imageEnd = text.indexOf(".png",imageStart)+4;
    	int altStart = text.indexOf("title=",imageEnd)+7;
    	int altEnd = text.indexOf("class=",imageEnd) - 2;
    	int temp = text.indexOf("previous-comic-link");
    	int previousStart = text.lastIndexOf("href=",temp)+6;
    	int previousEnd = text.indexOf("\" ",previousStart);
    	
    	temp = text.indexOf("next-comic-link");
    	int nextStart = text.lastIndexOf("href=",temp)+6;
    	int nextEnd = text.indexOf("\" ",nextStart);
    	
    	int titleStart = text.indexOf("<title>")+7;
    	int titleEnd = text.indexOf("</title>");
    	
    	temp = text.indexOf("<div class=\"entry");
    	int descriptionStart = text.indexOf("<p>",temp) + 3;
    	int descriptionEnd = text.indexOf("</p>",descriptionStart);
    	

    	altText = text.substring(altStart,altEnd);
    	
    	if (imageStart > 0 && imageStart < imageEnd && imageEnd < text.length())
			imageStr = text.substring(imageStart,imageEnd);
    	
		if (previousStart > 0 && previousStart < previousEnd && previousEnd < text.length())
		{
			previousStr = text.substring(previousStart,previousEnd);
			((Button)findViewById(R.id.prev)).setVisibility(View.VISIBLE);
		}
		else
			((Button)findViewById(R.id.prev)).setVisibility(View.GONE);
		
    	if (nextStart > 0 && nextStart < nextEnd && nextEnd < text.length())
    	{
    		nextStr = text.substring(nextStart,nextEnd);
			((Button)findViewById(R.id.prev)).setVisibility(View.VISIBLE);
    	}    		
		else
			((Button)findViewById(R.id.next)).setVisibility(View.GONE);
    	
    	if (titleStart > 0 && titleStart < titleEnd && titleEnd < text.length())
			title = text.substring(titleStart,titleEnd);
    	
    	if (descriptionStart > 0 && descriptionStart < descriptionEnd && descriptionEnd < text.length())
			desc = text.substring(descriptionStart,descriptionEnd); 
			
    	title = title.replaceAll("&#8217;", "'");
    	title = title.replaceAll("&#8211;", "-");
    	title = title.replaceAll("&#8212;", "--");
    	title = title.replaceAll("&#8220;", "\"");
    	title = title.replaceAll("&#8221;", "\"");
    	title = title.replaceAll("&#8230;", "...");
    	title = title.replaceAll("&quot;", "\"");

    	desc = desc.replaceAll("&#8217;", "'");
    	desc = desc.replaceAll("&#8211;", "-");
    	desc = desc.replaceAll("&#8212;", "--");
    	desc = desc.replaceAll("&#8220;", "\"");
    	desc = desc.replaceAll("&#8221;", "\"");
    	desc = desc.replaceAll("&#8230;", "...");
    	desc = desc.replaceAll("&quot;", "\"");
    	desc = desc.replaceAll("<br/>", "");
    	
    	downloadImage(imageStr);
    }
    
    private void downloadImage(String image)
    {
    	if (image != null)
		{
			HttpImageConnection imageCon = new HttpImageConnection(imageHandler);
			imageCon.create(image);
		}
    }
    
    AlertDialog imageDialog = null;
    private void showImage()
    {
    	AlertDialog.Builder imageBuild = new AlertDialog.Builder(this);
    	imageBuild.setCancelable(true);
    	
    	WebView image = new WebView(this);
    	image.getSettings().setBuiltInZoomControls(true);
    	image.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
    	image.loadUrl(imageStr);
    	
    	imageBuild.setView(image);
    	
    	imageDialog = imageBuild.create();
    	imageDialog.setCanceledOnTouchOutside(true);
        
    	imageDialog.show();
    }
    
    private Handler imageHandler = new Handler()
    {
    	public void handleMessage(Message msg)
    	{
    		super.handleMessage(msg);
    		switch (msg.what){
    		case 1:
    			comic = (Bitmap)(msg.getData().getParcelable("bitmap"));
    			ImageView imageV = (ImageView)findViewById(R.id.comic);
    			imageV.setImageBitmap(comic);
    			imageV.setOnClickListener(new OnClickListener()
    	        {
    	        	@Override
    	        	public void onClick(View v)
    	        	{
    	    			showImage();
    	        	}
    	        });
    			
    	    	TextView titleT = (TextView)findViewById(R.id.title);
    	    	titleT.setText(title);
    	    	titleT.setTextColor(Color.BLACK);
    	    	titleT.setTextSize(25);
    			titleT.setPadding(0, 20, 0, 20);
    			
    			TextView alt = (TextView)findViewById(R.id.alt);
    			alt.setText("\n" + altText + "\n\n");
    			alt.setTextColor(Color.BLACK);
    			
    			TextView descV = (TextView)findViewById(R.id.description);
    			descV.setTextColor(Color.BLACK);
    			descV.setText(desc);
    			
    			try{
    			if (progressDialog.isShowing())
    				progressDialog.dismiss();
    			}catch (Exception e){}
    			break;
    		}
    	}
    };
}