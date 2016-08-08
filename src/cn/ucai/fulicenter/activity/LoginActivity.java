/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.fulicenter.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMCallBack;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import cn.ucai.fulicenter.Constant;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.db.UserDao;
import cn.ucai.fulicenter.domain.User;
import cn.ucai.fulicenter.task.DownCollectCountTask;
import cn.ucai.fulicenter.task.DownContactListTask;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.UserUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * 登陆页面
 * 
 */
public class LoginActivity extends BaseActivity {
	private static final String TAG = "LoginActivity";
	public static final int REQUEST_CODE_SETNICK = 1;
	private EditText usernameEditText;
	private EditText passwordEditText;

	private boolean progressShow;
	private boolean autoLogin = false;

	private String currentUsername;
	private String currentPassword;
	ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 如果用户名密码都有，直接进入主页面
		if (DemoHXSDKHelper.getInstance().isLogined()) {
			autoLogin = true;
			startActivity(new Intent(LoginActivity.this, MainActivity.class));

			return;
		}
		setContentView(R.layout.activity_login);

		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		DisplayUtils.initBack(this);

		setListener();

		if (FuLiCenterApplication.getInstance().getUserName() != null) {
			usernameEditText.setText(FuLiCenterApplication.getInstance().getUserName());
		}
	}

	private void setListener() {
		// 如果用户名改变，清空密码
		usernameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				passwordEditText.setText(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	/**
	 * 登录
	 * 
	 * @param view
	 */
	public void login(View view) {
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}
		currentUsername = usernameEditText.getText().toString().trim();
		currentPassword = passwordEditText.getText().toString().trim();

		if (TextUtils.isEmpty(currentUsername)) {
			Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(currentPassword)) {
			Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}

		progressShow = true;
		pd = new ProgressDialog(LoginActivity.this);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				progressShow = false;
			}
		});
		pd.setMessage(getString(R.string.Is_landing));
		pd.show();

		final long start = System.currentTimeMillis();
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

			@Override
			public void onSuccess() {
				if (!progressShow) {
					return;
				}
				loginAppServer();
			}

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int code, final String message) {
				if (!progressShow) {
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void loginAppServer() {
		final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
		utils.setRequestUrl(I.REQUEST_LOGIN)
				.addParam(I.User.USER_NAME,currentUsername)
				.addParam(I.User.PASSWORD,currentPassword)
				.targetClass(String.class)
				.execute(new OkHttpUtils2.OnCompleteListener<String>() {
					@Override
					public void onSuccess(String s) {
						Log.e(TAG, "s="+s);
						Result result=Utils.getResultFromJson(s, UserAvatar.class);
						Log.e(TAG, "result=" + result);
						if (result != null && result.isRetMsg()) {
							UserAvatar user = (UserAvatar) result.getRetData();
							Log.e(TAG, "user="+user);
							if (user != null) {
								downloadUserAvatar();
								saveUserToDb(user);
								loginSuccess(user);
							}
						} else {
							pd.dismiss();
							Toast.makeText(getApplicationContext(), R.string.login_failure_failed+ Utils.getResourceString(LoginActivity.this,result.getRetCode()),
									Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onError(String error) {
						Log.e(TAG, "error=" + error);
						pd.dismiss();
						DemoHXSDKHelper.getInstance().logout(true,null);
						Toast.makeText(getApplicationContext(), R.string.login_failure_failed, Toast.LENGTH_LONG).show();
					}
				});
	}

	private void downloadUserAvatar() {
		final OkHttpUtils2<Message> utils = new OkHttpUtils2<Message>();
		utils.url(UserUtils.getUserAvatarPath(currentUsername))
				.targetClass(Message.class)
				.doInBackground(new Callback() {
					@Override
					public void onFailure(Request request, IOException e) {
						Log.e(TAG, "IOException=" + e.getMessage());
					}

					@Override
					public void onResponse(Response response) throws IOException {
						byte[] data = response.body().bytes();
						final String avatarUrl = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().uploadUserAvatar(data);
						Log.e(TAG, "avatarUrl=" + avatarUrl);
					}
				})
				.execute(new OkHttpUtils2.OnCompleteListener<Message>() {
					@Override
					public void onSuccess(Message result) {
						Log.e(TAG, "result=" + result);
					}

					@Override
					public void onError(String error) {
						Log.e(TAG, "error=" + error);
					}
				});
	}

	private void saveUserToDb(UserAvatar user) {
			// 存入db
			UserDao dao = new UserDao(LoginActivity.this);
			dao.saveUserAvatar(user);
	}

	private void loginSuccess(UserAvatar user) {
		// 登陆成功，保存用户名密码
		FuLiCenterApplication.getInstance().setUserName(currentUsername);
		FuLiCenterApplication.getInstance().setPassword(currentPassword);
		FuLiCenterApplication.getInstance().setUser(user);
		FuLiCenterApplication.currentUserNick = user.getMUserNick();

		new DownContactListTask(currentUsername,LoginActivity.this).execute();
		new DownCollectCountTask(currentUsername,LoginActivity.this).execute();
		try {
			// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
			// ** manually load all local groups and
			EMGroupManager.getInstance().loadAllGroups();
			EMChatManager.getInstance().loadAllConversations();
			// 处理好友和群组
			initializeContacts();
		} catch (Exception e) {
			e.printStackTrace();
			// 取好友或者群聊失败，不让进入主页面
			runOnUiThread(new Runnable() {
				public void run() {
					pd.dismiss();
					DemoHXSDKHelper.getInstance().logout(true,null);
					Toast.makeText(getApplicationContext(), R.string.login_failure_failed, Toast.LENGTH_LONG).show();
				}
			});
			return;
		}
		// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
		boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
				FuLiCenterApplication.currentUserNick.trim());
		if (!updatenick) {
			Log.e("LoginActivity", "update current user nick fail");
		}
		if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
			pd.dismiss();
		}
		// 进入主页面
		Intent intent = new Intent(LoginActivity.this,
				FuliCenterMainActivity.class);
		startActivity(intent);

		finish();
	}

	private void initializeContacts() {
		Map<String, User> userlist = new HashMap<String, User>();
		// 添加user"申请与通知"
		User newFriends = new User();
		newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
		String strChat = getResources().getString(
				R.string.Application_and_notify);
		newFriends.setNick(strChat);

		userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
		// 添加"群聊"
		User groupUser = new User();
		String strGroup = getResources().getString(R.string.group_chat);
		groupUser.setUsername(Constant.GROUP_USERNAME);
		groupUser.setNick(strGroup);
		groupUser.setHeader("");
		userlist.put(Constant.GROUP_USERNAME, groupUser);
		
		// 添加"Robot"
		/*User robotUser = new User();
		String strRobot = getResources().getString(R.string.robot_chat);
		robotUser.setUsername(Constant.CHAT_ROBOT);
		robotUser.setNick(strRobot);
		robotUser.setHeader("");
		userlist.put(Constant.CHAT_ROBOT, robotUser);*/
		
		// 存入内存
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
		// 存入db
		UserDao dao = new UserDao(LoginActivity.this);
		List<User> users = new ArrayList<User>(userlist.values());
		dao.saveContactList(users);
	}
	
	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		startActivityForResult(new Intent(this, RegisterActivity.class), 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (autoLogin) {
			return;
		}
	}
}
