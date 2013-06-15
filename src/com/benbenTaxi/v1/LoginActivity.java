package com.benbenTaxi.v1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.benbenTaxi.R;
import com.benbenTaxi.demo.LocationOverlayDemo;
import com.benbenTaxi.v1.function.DataPreference;
import com.benbenTaxi.v1.function.EquipmentId;
import com.benbenTaxi.v1.function.GetInfoTask;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
	private static final String mTestHost = "42.121.55.211:8081";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	//private String mCode, mCookie, mToken;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private CheckBox mSavePass, mIsDriver;
	private View mLoginFormView;
	private View mFillView;
	private View mLoginStatusView;
	private Button mSignInBtn;
	private Button mCreateBtn;
	private TextView mLoginStatusMessageView;
	private EquipmentId mEquipmentId;
	//private UrlConfigure mUrlConf;
	
	private long exitTime;
	private DataPreference mData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);
		
		mSavePass = (CheckBox) findViewById(R.id.checkBox_save);
		mIsDriver = (CheckBox) findViewById(R.id.checkBox_driver);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin(mSavePass.isChecked(), mIsDriver.isChecked(), UserLoginTask.LOGINTYPE_LOGIN);
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		mSignInBtn = (Button)findViewById(R.id.sign_in_button);
		mSignInBtn.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						attemptLogin(mSavePass.isChecked(), mIsDriver.isChecked(), UserLoginTask.LOGINTYPE_LOGIN);
					}
				});
		
		mCreateBtn = (Button)findViewById(R.id.create_button);
		mCreateBtn.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						attemptLogin(mSavePass.isChecked(), mIsDriver.isChecked(), UserLoginTask.LOGINTYPE_CREATE);
					}
				});
		
		if ( this.getIntent().getExtras() != null ) {
			String getClass = this.getIntent().getExtras().getString("class");
			if ( getClass != null ) {
				Toast.makeText(this, "再按一次返回退出程序", Toast.LENGTH_SHORT).show();
			}
		}
		
		exitTime = 0;
		mEquipmentId = new EquipmentId(this);
		//mUrlConf = new UrlConfigure(this, null);
		
		mFillView = findViewById(R.id.tv_fill);
		mFillView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// 设置焦点，消除输入框的error信息
				mPasswordView.requestFocus();
			}
		});
		
		mData = new DataPreference(this.getApplicationContext());
		Toast.makeText(this, "点击菜单键进行参数配置", Toast.LENGTH_SHORT).show();
	}

	//@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		//getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// 载入账号信息
		mEmailView.setText(mData.LoadString("user"));
		mPasswordView.setText(mData.LoadString("pass"));
		mSavePass.setChecked(mData.LoadBool("savePass"));
		mIsDriver.setChecked(mData.LoadBool("isdriver"));
	}
	
	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event ) {
		if(keyCode == KeyEvent.KEYCODE_BACK && 
				event.getAction() == KeyEvent.ACTION_DOWN && exitTime == 0) {
			// 这里不需要再按两次，一次退出
			//if((System.currentTimeMillis()-exitTime) > 2000) {
	        //    Toast.makeText(getApplicationContext(), "准备退出...", Toast.LENGTH_SHORT).show();
	        //    exitTime = System.currentTimeMillis();
			//} else {
			Toast.makeText(getApplicationContext(), "退出中...", Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
			/*
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
	        finish();
	        //System.exit(0);
			//}
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin(boolean saveFlag, boolean isDriver, int type) {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			//mPasswordView.setError(getString(R.string.error_invalid_password));
			//focusView = mPasswordView;
			//cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			//mEmailView.setError(getString(R.string.error_invalid_email));
			//focusView = mEmailView;
			//cancel = true;
		}

		// 保存账号信息
		mData.SaveData("user", mEmail);
		if ( saveFlag ) {
			mData.SaveData("pass", mPassword);
		} else {
			mData.SaveData("pass", "");
		}
		mData.SaveData("savePass", saveFlag);
		mData.SaveData("isdriver", isDriver);
		
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			String usertype = UserLoginTask.TYPE_PASSENGER;
			if ( isDriver == true ) {
				usertype = UserLoginTask.TYPE_DRIVER;
			}
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			if ( type == UserLoginTask.LOGINTYPE_LOGIN ) {
				mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
				showProgress(true);
				mAuthTask = new UserLoginTask();
				mAuthTask.doLogin(mEmail, mPassword, mTestHost, mEquipmentId.getId(), usertype);
			} else if ( type == UserLoginTask.LOGINTYPE_CREATE ) {
				mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
				showProgress(true);
				mAuthTask = new UserLoginTask();
				mAuthTask.doCreate(mEmail, mPassword, mTestHost, mEquipmentId.getId(), usertype);
			}
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	private class UserLoginTask extends GetInfoTask {
		public static final int LOGINTYPE_LOGIN = 0;
		public static final int LOGINTYPE_CREATE = 1;
		//private final static String _url = "http://peterwolf.cn.mu/zone_supervisor/sessions.json";
		//private final static String _url = "http://v2.365check.net/api/v1/sessions";
		//private final static String _testagent = "351554052661692@460018882023767@0.14@android42";
		
		public static final String TYPE_DRIVER = "driver";
		public static final String TYPE_PASSENGER = "passenger";
		
		private String _useragent;
		private JSONObject _json_data;
		private int _type;
		
		public void doLogin(String name, String pass, String host, String ua, String type) {
			_type = LOGINTYPE_LOGIN;
			_useragent = ua;
			
			initHeaders("Content-Type", "application/json");
			_json_data = new JSONObject();
			try {
				//{"session":{"account":"sh_0000","password":"8"}}
				JSONObject sess = new JSONObject();
				sess.put("mobile", name);
				sess.put("password", pass);
				_json_data.put("session", sess);
			} catch (JSONException e) {
				//_info.append("form json error: "+e.toString());
			}

			String url =  "http://"+host+"/api/v1/sessions/"+type+"_signin";
			execute(url, _useragent, GetInfoTask.TYPE_POST);
		}
		
		public void doCreate(String name, String pass, String host, String ua, String type) {
			_type = LOGINTYPE_CREATE;
			_useragent = ua;
			
			initHeaders("Content-Type", "application/json");
			_json_data = new JSONObject();
			try {
				//{"session":{"account":"sh_0000","password":"8"}}
				JSONObject sess = new JSONObject();
				sess.put("mobile", name);
				sess.put("password", pass);
				sess.put("password_confirmation", pass);
				_json_data.put("user", sess);
			} catch (JSONException e) {
				//_info.append("form json error: "+e.toString());
			}

			String url =  "http://"+host+"/api/v1/users/create_"+type;
			execute(url, _useragent, GetInfoTask.TYPE_POST);
		}
		
		@Override
		protected void initPostValues() {
			//sess_params.add(new BasicNameValuePair("","{\"session\":{\"name\":\"ceshi001\",\"password\":\"8\"}}"));
			//post_param = "{\"session\":{\"name\":\"ceshi_ning\",\"password\":\"8\"}}";
			if ( _json_data != null ) {
				post_param = _json_data.toString();
			}
		}
		
		@Override
		protected void onPostExecGet(Boolean succ) {
			//_info.setText("Get "+this.getHttpCode()+"\n");
			if ( succ ) {
				String data = this.toString();
				//_info.append("get result: \n"+data);
				JSONTokener jsParser = new JSONTokener(data);
				JSONObject ret = null;
				try {
					ret = (JSONObject)jsParser.nextValue();
					int total = ret.getInt("total_entries");
					if ( total > 0 ) {
						//JSONArray orglst = ret.getJSONArray("zones");
						//_info.append("zone_id: "+orglst.getJSONObject(0).getInt("zone_admin_id")+"\n");
						//_info.append("name: "+orglst.getJSONObject(0).getString("name")+"\n");
						//_info.append("des: "+orglst.getJSONObject(0).getString("des")+"\n");
					}
				} catch (JSONException e) {
					//e.printStackTrace();
					try {
						JSONObject err = ret.getJSONObject("errors");
						//_info.append("errmsg \""+err.getJSONArray("base").getString(0)+"\"");
						//_info.append("\ncookies: "+_sess_key.getName()+" "+_sess_key.getValue()+"\n");
					} catch (JSONException ee) {
						//_info.append("json error: "+ee.toString()+"\n"+"ret: "+data);
					}
				}
			} else {
				//_info.append("get errmsg: \n"+_errmsg);
			}
		}
		
		@Override
		protected void onPostExecPost(Boolean succ) {
			//_info.setText("Post "+this.getHttpCode()+"\n");
			if ( succ ) {
				//_info.append("result: "+this.getHttpCode()+"\n"+this.toString());
				JSONTokener jsParser = new JSONTokener(this.toString());
				JSONObject ret = null;

				try {
					ret = (JSONObject)jsParser.nextValue();
					//_info.append("result \n"+ret.getString("token_key")+": "+ret.getString("token_value"));
					//_sess_key = new BasicNameValuePair(ret.getString("token_key"), ret.getString("token_value"));
					
					mAuthTask = null;
					showProgress(false);
					
					// 保存cookie
					mData.SaveData("token_key", ret.getString("token_key"));
					mData.SaveData("token_value", ret.getString("token_value"));
					mData.SaveData("useragent",  mEquipmentId.getId());
					//Bundle sess_data = new Bundle();
					//sess_data.putString("token_key", ret.getString("token_key"));
					//sess_data.putString("token_value", ret.getString("token_value"));
					Intent yunjianIntent = new Intent(LoginActivity.this,LocationOverlayDemo.class);
					//yunjianIntent.putExtras(sess_data);
					
					startActivity(yunjianIntent);
				} catch (JSONException e) {
					//e.printStackTrace();
					try {
						JSONObject err = ret.getJSONObject("errors");
						//_info.append("errmsg \""+err.getJSONArray("base").getString(0)+"\"");
						_errmsg = err.getJSONArray("base").getString(0);
						succ = false;
					} catch (Exception ee) {
						//_info.append("json error: "+ee.toString()+"\n");
						//_info.append("to json: "+_json_data.toString());
						_errmsg = "数据通信异常，请检查云服务器配置，或联系服务商: "+ret.toString();
						succ = false;
					}
				} catch (Exception e) {
					_errmsg = "网络错误，请检查云服务器配置，并确认网络正常后再试";
					succ = false;
				}
				
			}
			
			if ( succ == false ) {
				//_info.append("errmsg: \n"+_errmsg);
				mAuthTask = null;
				showProgress(false);
				mEmailView.setError(_errmsg);
				mEmailView.requestFocus();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		switch ( item.getItemId() ) {
		case R.id.menu_setup:
			new SysSetting(this,null).getSettingDialog().show();
			break;
		case R.id.menu_about:
		default:
			// show about dialog
			String content = this.getString(R.string.app_name)+"@"+this.getString(R.string.author_name);
			content += "\n"+this.getString(R.string.app_ver);
			new IdShow(this.getString(R.string.menu_about), content, this).getIdDialog().show();
			break;
		}
		*/
		return super.onOptionsItemSelected(item);
	}
}
