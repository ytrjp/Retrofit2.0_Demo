package com.example.zenglb.retrofittest.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.zenglb.retrofittest.R;
import com.example.zenglb.retrofittest.base.BaseActivity;
import com.example.zenglb.retrofittest.http.HttpCall;
import com.example.zenglb.retrofittest.http.HttpCallBack;
import com.example.zenglb.retrofittest.http.HttpResponse;
import com.example.zenglb.retrofittest.http.param.LoginParams;
import com.example.zenglb.retrofittest.http.result.EasyResult;
import com.example.zenglb.retrofittest.http.result.LoginResult;
import com.example.zenglb.retrofittest.http.result.Modules;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * 登录,登出，token 过期，刷新token,重新登录
 * 形成一个闭环的处理
 * <p>
 * Http 的错误统一处理
 * <p>
 * <p>
 * anylife.zlb@gamil.com
 */
public class MainActivity extends BaseActivity {
	private final String TAG = MainActivity.class.getSimpleName();
	private TextView message;
	private TextView textViewLogin;
	public  static String refreshToken;
	private RecyclerView mRecyclerView = null;
	private MyAdapter myAdapter;
	private List<String> data = new ArrayList<>();

//	private boolean isTokenNotAvailable=true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		message = (TextView) findViewById(R.id.message);
		textViewLogin = (TextView) findViewById(R.id.login);

		checkNumber();

		textViewLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				testLogin();
			}

		});

		mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
		myAdapter = new MyAdapter(this, data);
		mRecyclerView.setAdapter(myAdapter);
		myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				switch (position) {
					case 0:
						refreshToken();
						break;
					case 1:
						requestModules();
						break;
					case 2:
						logout();
						break;
					case 3:
						killToken();
						break;
					case 4:
						killRefreshToken();
						break;
					case 5:

						break;
				}
			}

			@Override
			public void onItemLongClick(View view, int position) {

			}
		});
	}

	/**
	 * 模拟登出操作
	 */
	private void logout() {
		refreshToken = "refreshToken set logout,is null le";
		HttpCall.setToken("Bearer TokenSet-Logout");
	}


	/**
	 * 模拟Token失效(过期啊，在其他地方登录)
	 */
	private void killToken() {
		String tempToken = getOsDisplay("Bearer TokenSet-killToken");
		try {
			URLDecoder.decode(tempToken, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpCall.setToken(tempToken);
	}


	/**
	 * 模拟Token失效(过期啊，在其他地方登录)
	 */
	private void killRefreshToken() {
		refreshToken = "refreshToken - killRefreshToken";
	}


	private String getOsDisplay(String string) {
		try {
			return URLEncoder.encode(string, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}


	/**
	 * 登录成功后刷新信息，登出后隐藏
	 */
	private void updateFuncs() {
		if (data.size() == 0) {
			String[] temp = this.getResources().getStringArray(R.array.languages);
			for (int i = 0; i < temp.length; i++) {
				data.add(temp[i]);
			}
			myAdapter.notifyDataSetChanged();
		}
	}


	/**
	 * 刷新Token
	 */
	private void refreshToken() {
		//1.参数的封装
		LoginParams loginParams = new LoginParams();
		loginParams.setClient_id("5e96eac06151d0ce2dd9554d7ee167ce");
		loginParams.setClient_secret("aCE34n89Y277n3829S7PcMN8qANF8Fh");
		loginParams.setGrant_type("refresh_token");
		loginParams.setRefresh_token(refreshToken);

		//2.实例化Http的请求。Call 语法看起来很繁琐，但是这也是Java的基础
		Call<HttpResponse<LoginResult>> refreshTokenCall = HttpCall.getApiService(this).refreshToken(loginParams, "refreshToken"); //刷新Token
		refreshTokenCall.enqueue(new HttpCallBack<HttpResponse<LoginResult>>(this) {
			@Override
			public void onSuccess(HttpResponse<LoginResult> loginResultHttpResponse) {
				Log.e(TAG, loginResultHttpResponse.getResult().toString());
				message.setText(loginResultHttpResponse.getResult().toString());
				HttpCall.setToken("Bearer " + loginResultHttpResponse.getResult().getAccessToken());
				refreshToken = loginResultHttpResponse.getResult().getRefreshToken();
			}

			@Override
			public void onFailure(int code, String messageStr) {
				super.onFailure(code, messageStr);
				message.setText(code + "  ! refreshToken !  " + messageStr);
			}
		});
	}

	/**
	 * 测试登录
	 */
	private void testLogin() {
		//登录前一定这两个值都是空的值
		refreshToken = "";
		HttpCall.setToken("");

		//良好的互联网环境需要大家的共同努力，对于不正当使用带来的法律责任，由事由者承担
		//1.参数的封装
		LoginParams loginParams = new LoginParams();
		loginParams.setClient_id("5e96eac06151d0ce2dd9554d7ee167ce");
		loginParams.setClient_secret("aCE34n89Y277n3829S7PcMN8qANF8Fh");

		loginParams.setGrant_type("password");
		loginParams.setUsername("18826562075");
		loginParams.setPassword("zxcv1234");

		//2.实例化Http的请求。Call 语法看起来很繁琐，但是这也是Java的基础
		Call<HttpResponse<LoginResult>> loginCall = HttpCall.getApiService(this).goLogin(loginParams); //尝试登陆
		loginCall.enqueue(new HttpCallBack<HttpResponse<LoginResult>>(this) {
			@Override
			public void onSuccess(HttpResponse<LoginResult> loginResultHttpResponse) {
				Log.e(TAG, loginResultHttpResponse.getResult().toString());
				message.setText(loginResultHttpResponse.getResult().toString());
				HttpCall.setToken("Bearer " + loginResultHttpResponse.getResult().getAccessToken());
				refreshToken = loginResultHttpResponse.getResult().getRefreshToken();

				updateFuncs();

//				isTokenNotAvailable;
			}

			@Override
			public void onFailure(int code, String messageStr) {
				super.onFailure(code, messageStr);
				message.setText(code + "  !loginCall!  " + messageStr);
			}
		});
	}

	/**
	 * 请求可以使用的模块信息
	 */
	private void requestModules() {
		Call<HttpResponse<Modules>> getModulesCall = HttpCall.getApiService(this).getModules(); //尝试登陆
		getModulesCall.enqueue(new HttpCallBack<HttpResponse<Modules>>(this) {
			@Override
			public void onSuccess(HttpResponse<Modules> getModulesCallResponse) {
				Log.e(TAG, getModulesCallResponse.getResult().toString());
				message.setText(getModulesCallResponse.getResult().toString());
			}

			@Override
			public void onFailure(int code, String failedText) {
				super.onFailure(code, failedText);
				message.setText(code + "@@getModulesCall@@" + failedText);
			}
		});
	}


	/**
	 * 检查号码是否被注册了。
	 */
	private void checkNumber() {
		//2.实例化Http的请求。
		//这个时候的response 是一个没有用的response啊！
		Call<HttpResponse<EasyResult>> checkMobileCall = HttpCall.getApiService(this).checkMobile("188265672076"); //尝试登陆
		checkMobileCall.enqueue(new HttpCallBack<HttpResponse<EasyResult>>(this) {
			@Override
			public void onSuccess(HttpResponse<EasyResult> checkMobileHttpResponse) {
				Log.e(TAG, checkMobileHttpResponse.getResult().toString());
				message.setText(checkMobileHttpResponse.getResult().toString());
			}

			@Override
			public void onFailure(int code, String messageStr) {
				super.onFailure(code, messageStr);
				message.setText(code + "@@checkMobileCall@@" + messageStr);      //
			}
		});
	}
}
