package com.kevinj.doghouse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;



public class HttpTextConnection implements Runnable
{
	private String _url;
	private Handler _handle;
	
	public HttpTextConnection(Handler handler)
	{
		_handle = handler;
	}
	
	public void create(String url)
	{
		_url = url;
		ConnectionManager.getInstance().push(this);
	}
	
	public void run()
	{
		int BUFFER_SIZE = 2000;
		InputStream in = null;
		Message msg = Message.obtain();
		try{
			in = openHttpConnection(_url);
			if (in != null)
			{
				InputStreamReader isr = new InputStreamReader(in);
				int charRead;
				String text = "";
				char[] inputBuffer = new char[BUFFER_SIZE];
				while ((charRead = isr.read(inputBuffer))>0)
				{
					String readString = String.copyValueOf(inputBuffer,0,charRead);
					text += readString;
					inputBuffer = new char[BUFFER_SIZE];
				}
				Bundle b = new Bundle();
				b.putString("text", text);
				msg.setData(b);
				in.close();
			}
		} catch (IOException e){
			e.printStackTrace();
		}
		_handle.sendMessage(msg);
		_finished.sendEmptyMessage(0);
	}
	
	private Handler _finished = new Handler()
    {
    	public void handleMessage(Message msg)
    	{
    		super.handleMessage(msg);
    		finish();
    	}
    };
    
    private void finish()
    {
		ConnectionManager.getInstance().didComplete(this);
    }
	
	private InputStream openHttpConnection(String urlStr)
    {
    	InputStream in = null;
    	int resCode = -1;
    	
    	try{
    		URL url = new URL(urlStr);
    		URLConnection urlConn = url.openConnection();
    		
    		if (!(urlConn instanceof HttpURLConnection))
    		{
    			throw new IOException ("URL is not Http URL");
    		}
    		
    		HttpURLConnection httpConn = (HttpURLConnection) urlConn;
    		httpConn.setAllowUserInteraction(false);
    		httpConn.setInstanceFollowRedirects(true);
    		httpConn.setRequestMethod("GET");
    		httpConn.connect();
    		
    		resCode = httpConn.getResponseCode();
    		
    		if (resCode == HttpURLConnection.HTTP_OK)
    		{
    			in = httpConn.getInputStream();
    		}
    	} catch (MalformedURLException e){
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
	return in;
    }
}