package com.benbenTaxi.v1;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.benbenTaxi.R;
import com.benbenTaxi.v1.function.DataPreference;
import com.benbenTaxi.v1.function.EquipmentId;
import com.benbenTaxi.v1.function.GetInfoTask;

import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	//private String TAG = RegisterActivity.class.getName();
	private EditText mEmailView, mPasswordView, mPasswordConfirmView, mLicenseView;
	private TextView mLoginStatusMessageView;
	private String mPassword, mEmail, mHost, mLicense;
	private DataPreference mData;
	private EquipmentId mEquipmentId;
	
	private View mLoginStatusView, mLoginFormView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		Spinner spinner 					= (Spinner) findViewById(R.id.tenant_item);
		ArrayAdapter<CharSequence> adapter 	= ArrayAdapter.createFromResource(this,
		        R.array.tenants_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		Toast.makeText(this, spinner.getSelectedItem().toString(),Toast.LENGTH_LONG).show();
		buttonBind();

		mData = new DataPreference(this);
		mEmailView = (EditText) findViewById(R.id.register_mobile);
		mPasswordView = (EditText) findViewById(R.id.register_password);
		mPasswordConfirmView = (EditText) findViewById(R.id.register_password_confirm);
		mLicenseView = (EditText) findViewById(R.id.register_license);
		
		mLoginStatusMessageView = (TextView) findViewById(R.id.register_status_message);
		mLoginFormView = findViewById(R.id.register_form);
		mLoginStatusView = findViewById(R.id.register_progress);
		
		mHost = mData.LoadString("host");
		mEquipmentId = new EquipmentId(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_register, menu);
		return true;
	}
	
	private void buttonBind()
	{
		Button button = (Button)findViewById(R.id.register_button);
		button.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						attemptLogin(true, true, UserLoginTask.LOGINTYPE_CREATE);
					}
		});
		button 		  = (Button)findViewById(R.id.goback_button);
		button.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
						startActivity(loginIntent);
					}
		});
	}
	
	public void attemptLogin(boolean saveFlag, boolean isDriver, int type) {
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		String passConfirm  = mPasswordConfirmView.getText().toString();
		mLicense = mLicenseView.getText().toString();

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
		if (TextUtils.isEmpty(mEmail) || mEmail.length()<11 ) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			//mEmailView.setError(getString(R.string.error_invalid_email));
			//focusView = mEmailView;
			//cancel = true;
		}
		
		if ( ! mPassword.equals(passConfirm) ) {
			mPasswordView.setError(getString(R.string.password_confirm_invalid));
			focusView = mPasswordView;
			cancel = true;
		}
		
		if ( mLicense.length() < 3 ) {
			mLicenseView.setError(getString(R.string.error_field_licence_required));
			focusView = mLicenseView;
			cancel = true;
		}
		
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			String usertype = UserLoginTask.TYPE_PASSENGER;
			if ( isDriver == true ) {
				usertype = UserLoginTask.TYPE_DRIVER;
			}
			
			// 保存账号信息
			mData.SaveData("user", mEmail);
			mData.SaveData("license", mLicense);
			if ( saveFlag ) {
				mData.SaveData("pass", mPassword);
			} else {
				mData.SaveData("pass", "");
			}
			mData.SaveData("savePass", saveFlag);
			mData.SaveData("isdriver", isDriver);
			
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			if ( type == UserLoginTask.LOGINTYPE_CREATE ) {
				mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
				showProgress(true);
				UserLoginTask authTask = new UserLoginTask();
				authTask.doCreate(mEmail, mPassword, mHost, mEquipmentId.getId(), usertype);
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

	private class UserLoginTask extends GetInfoTask {
		public static final int LOGINTYPE_CREATE = 1;	
		public static final String TYPE_DRIVER = "driver";
		public static final String TYPE_PASSENGER = "passenger";
		
		private String _useragent;
		private JSONObject _json_data;
		
		public void doCreate(String name, String pass, String host, String ua, String type) {
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
		protected void onPostExecPost(Boolean succ) {
			if ( succ ) {
				JSONTokener jsParser = new JSONTokener(this.toString());
				JSONObject ret = null;

				try {
					ret = (JSONObject)jsParser.nextValue();
					showProgress(false);
					
					// 保存cookie
					mData.SaveData("token_key", ret.getString("token_key"));
					mData.SaveData("token_value", ret.getString("token_value"));
					mData.SaveData("useragent",  mEquipmentId.getId());

					Intent yunjianIntent = new Intent(RegisterActivity.this,BenbenLocationMain.class);					
					startActivity(yunjianIntent);
				} catch (JSONException e) {
					try {
						JSONObject err = ret.getJSONObject("errors");
						_errmsg = err.getJSONArray("base").getString(0);
						succ = false;
					} catch (Exception ee) {
						_errmsg = "数据通信异常，请检查云服务器配置，或联系服务商: "+ret.toString();
						succ = false;
					}
				} catch (Exception e) {
					_errmsg = "网络错误，请检查云服务器配置，并确认网络正常后再试";
					succ = false;
				}
				
			}
			
			if ( succ == false ) {
				showProgress(false);
				mEmailView.setError(_errmsg);
				mEmailView.requestFocus();
			}
		}
	}
}
