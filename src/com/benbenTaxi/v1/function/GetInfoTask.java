package com.benbenTaxi.v1.function;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

public abstract class GetInfoTask extends AsyncTask<String, Integer, Boolean> {
	public final static String TYPE_GET = "get";
	public final static String TYPE_POST = "post";
	
	public final static int REQUEST_SEND = 10;
	public final static int REQUEST_DONE = 100;
	
	protected String _errmsg;
	protected List<NameValuePair> sess_params;
	protected String post_param;
	protected HttpContext hcon;
	public byte[] result;
	protected String _type;
	protected CookieStore cs;
	private List<Header> _headers;
	protected HttpResponse _httpResp;
	
	protected GetInfoTask() {
		cs = new BasicCookieStore();
		sess_params = new ArrayList<NameValuePair>();
		post_param = new String();
		_headers = new ArrayList<Header>();
		result = new byte[1];
	}
	
	protected void initPostValues() {
		//sess_params.add(new BasicNameValuePair("session[name]",mEmail));
	}
	
	@SuppressLint("SimpleDateFormat")
	protected void initCookies(String key, String val, String domain) {
		BasicClientCookie bc1 = new BasicClientCookie(key, val);
		bc1.setVersion(0);
        bc1.setDomain(domain);
        bc1.setPath("/");
        
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			final Date ed = df.parse("2050-04-23");
	        bc1.setExpiryDate(ed);
	        cs.addCookie(bc1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	protected void initHeaders(String Key, String Val) {
		_headers.add(new BasicHeader(Key, Val));
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		// attempt authentication against a network service.
		String urlstr = params[0];
		String useragent = params[1];
		_type = new String(params[2]);		
		_errmsg = new String();
		
		try {
			hcon = new BasicHttpContext();
			hcon.setAttribute(ClientContext.COOKIE_STORE, cs);
			
			HttpParams httpparam = new BasicHttpParams();
			HttpProtocolParams.setUserAgent(httpparam, useragent);
			
			
			if ( _type.equals(TYPE_GET) ) {
				HttpGet httpRequest = new HttpGet(urlstr);
				_httpResp = new DefaultHttpClient(httpparam).execute(httpRequest, hcon);
				for ( Header hh : _headers ) {
					httpRequest.setHeader(hh);
				}
				
				result = EntityUtils.toByteArray(_httpResp.getEntity());
			} else {
				initPostValues();
				HttpPost httpRequest = new HttpPost(urlstr);
				for ( Header hh : _headers ) {
					httpRequest.setHeader(hh);
				}
				if ( sess_params.size() > 0 ) {
					httpRequest.setEntity(new UrlEncodedFormEntity(sess_params,"UTF-8"));
				} else if ( post_param.length() > 0 ) {
					httpRequest.setEntity(new StringEntity(post_param,"UTF-8"));
				}
				
				_httpResp = new DefaultHttpClient(httpparam).execute(httpRequest, hcon);
				publishProgress(REQUEST_SEND);
				result = EntityUtils.toByteArray(_httpResp.getEntity());
				publishProgress(REQUEST_DONE);
			}

		} catch ( Exception e ) {
			_errmsg = "网络错误，请检查网络是否正常"; //"stage 3: "+e.toString();
			return false;
		}

		/*
		for (String credential : DUMMY_CREDENTIALS) {
			String[] pieces = credential.split(":");
			if (pieces[0].equals(mEmail)) {
				// Account exists, return true if the password matches.
				return pieces[1].equals(mPassword);
			}
		}
		*/

		// TODO: register the new account here.
		return true;
	}

	@Override
	protected void onPostExecute(final Boolean succ) {
		//showProgress(false);
		// 只处理返回值200的情况
		if ( _type.equals(TYPE_GET) && getHttpCode()==200 ) {
			onPostExecGet(succ);
			// TODO: get data
			//Bundle sess_data = new Bundle();
			//sess_data.putString("html", result);
			//Intent yunjianIntent = new Intent(LoginActivity.this,NetAppActivity.class);
			//yunjianIntent.putExtras(sess_data);
			
			//startActivity(yunjianIntent);
		} else if ( getHttpCode()==200 ){
			onPostExecPost(succ);
			// TODO: get data faild
		} else {
			onPostExecError(_type, getHttpCode() );
		}
	}

	@Override
	protected void onCancelled() {
		//showProgress(false);
	}
	
	abstract protected void onPostExecGet( Boolean succ );
	abstract protected void onPostExecPost( Boolean succ );
	abstract protected void onPostExecError( String type, int code );
	protected String getApiUrl() {
		return null;
	}
	
	public String toString() {
		return new String(result);
	}
	
	public byte[] toByte() {
		return result;
	}
	
	public String getErrorMsg() {
		return _errmsg;
	}
	
	public int getHttpCode() {
		if ( _httpResp != null ) {
			return _httpResp.getStatusLine().getStatusCode();
		} else {
			return -1;
		}
	}
	
	public void executePOST() {
		initHeaders("Content-Type", "application/json");
		execute(getApiUrl(), Configure.getUserAgent(), GetInfoTask.TYPE_POST);
	}
	
	public void executeGET() {
		execute(getApiUrl(), Configure.getUserAgent(), GetInfoTask.TYPE_GET);
	}
}
