package cn.ucai.fulicenter;

public interface I {
	String SERVER_URL = "http://10.0.2.2:8080/SuperWeChatServer/Server";
	interface User {
		String TABLE_NAME							=		"t_superwechat_user";
		String USER_NAME 							= 		"m_user_name";					//用户账号
		String PASSWORD 							= 		"m_user_password";				//用户密码
		String NICK 								= 		"m_user_nick";					//用户昵称
	}
	
	interface Contact {
		String TABLE_NAME 							= 		"t_superwechat_contact";
		String CONTACT_ID 							= 		"m_contact_id";					//主键
		String USER_NAME 							= 		"m_contact_user_name";			//用户账号
		String CU_NAME 								= 		"m_contact_cname";				//好友账号
	}
	
	interface Group {
		String TABLE_NAME 							= 		"t_superwechat_group";
		String GROUP_ID 							= 		"m_group_id";					// 主键
		String HX_ID 								= 		"m_group_hxid";					//环信群组id
		String NAME 								= 		"m_group_name";					//群组名称
		String DESCRIPTION 							= 		"m_group_description";			//群组简介
		String OWNER 								= 		"m_group_owner";				//群组所有者－用户账号
		String MODIFIED_TIME 						= 		"m_group_last_modified_time";	//最后修改时间
		String MAX_USERS 							= 		"m_group_max_users";			//最大人数
		String AFFILIATIONS_COUNT 					= 		"m_group_affiliations_count";	//群组人数
		String IS_PUBLIC 							= 		"m_group_is_public";			//群组是否公开
		String ALLOW_INVITES 						= 		"m_group_allow_invites";		//是否可以邀请
	}
	
	interface Member {
		String TABLE_NAME 							= 		"t_superwechat_member";
		String MEMBER_ID 							= 		"m_member_id";					//主键
		String USER_NAME 							= 		"m_member_user_name";			//用户账号
		String GROUP_ID 							= 		"m_member_group_id";			//群组id
		String GROUP_HX_ID 							= 		"m_member_group_hxid";			//群组环信id
		String PERMISSION 							= 		"m_member_permission";			//用户对群组的权限\n0:普通用户\n1:群组所有者
	}
	
	interface Avatar {
		String TABLE_NAME 							= 		"t_superwechat_avatar";
		String AVATAR_ID 							= 		"m_avatar_id";					//主键
		String USER_NAME 							= 		"m_avatar_user_name";			//用户账号或者群组账号
		String AVATAR_PATH 							= 		"m_avatar_path";				//保存路径
		String AVATAR_TYPE 							= 		"m_avatar_type";				//头像类型：\n0:用户头像\n1:群组头像
		String UPDATE_TIME 							= 		"m_avatar_last_update_time";	//最后更新时间
	}
	
	interface Location {
		String TABLE_NAME 							= 		"t_superwechat_location";
		String LOCATION_ID 							= 		"m_location_id";				//主键
		String USER_NAME 							= 		"m_location_user_name";			//用户账号
		String LATITUDE 							= 		"m_location_latitude";			//纬度
		String LONGITUDE 							= 		"m_location_longitude";			//经度
		String IS_SEARCHED 							= 		"m_location_is_searched";		//是否可以被搜索到
		String UPDATE_TIME 							= 		"m_location_last_update_time";	//最后更新时间
	}

//	String AVATAR_PATH 								= 		"E:/test/";
	String ISON8859_1 								= 		"iso8859-1";
	String UTF_8 									= 		"utf-8";
	String PAGE_ID 									= 		"page_id";						//分页的起始下标
	String PAGE_SIZE 								= 		"page_size";					//分页的每页数量
	int DEFAULT_DISTANCE = 100; // 查找附近的人时，默认距离为100KM
	int ID_DEFAULT									=		0;								//ID默认值
	int UN_READ_MSG_COUNT_DEFAULT					=		0;								//未读消息数量默认值
	int GROUP_MAX_USERS_DEFAULT 					= 		-1;								//群组最大人数默认值
	int GROUP_AFFILIATIONS_COUNT_DEFAULT 			= 		1;								//群组人数默认值
	int PERMISSION_NORMAL							= 		0;								//普通用户群组权限
	int PERMISSION_OWNER							= 		1;								//群组所有者群组权限
	int AVATAR_TYPE_USER							=		0;								//用户头像
	int AVATAR_TYPE_GROUP							=		1;								//群组头像
	int GROUP_PUBLIC								=		1;								//公开群组
	int GROUP_NO_PUBLIC								=		0;								//非公开群组
	String AVATAR_TYPE_USER_PATH					= 		"user_avatar";					//用户头像保存目录
	String AVATAR_TYPE_GROUP_PATH 					=		"group_icon";					//群组头像保存目录
	String AVATAR_SUFFIX_PNG						=		".png";							//PNG图片后缀名
	String AVATAR_SUFFIX_JPG						=		".jpg";							//JPG图片后缀名
	String QUESTION 								=		"?";							//问号
	String EQU										=		"=";							//等号
	String AND										=		"&";							//&符号
	String MSG_PREFIX_MSG							=		"msg_";							//消息码前缀
	int LOCATION_IS_SEARCH_ALLOW					=		1;								//可以被搜索到地理位置
	int LOCATION_IS_SEARCH_INHIBIT					=		0;								//禁止被搜索到地理位置
	int MSG_SUCCESS						            =  		0;							    //默认成功
	int MSG_REGISTER_USERNAME_EXISTS				=		101;							//账号已经存在
	int MSG_REGISTER_FAIL							=		102;							//注册失败
	int MSG_UNREGISTER_FAIL							=		103;							//解除注册失败
	int MSG_USER_SEARCH_FAIL		    			=		104;							// 查找用户失败
	int MSG_LOGIN_UNKNOW_USER						=		105;							//账户不存在
	int MSG_LOGIN_ERROR_PASSWORD					=		106;							//账户密码错误
	int MSG_LOGIN_SUCCESS							=		107;							//登陆成功
	int MSG_USER_SAME_NICK							=		108;							//昵称未修改
	int MSG_USER_UPDATE_NICK_FAIL					=		109;							//昵称修改失败
	int MSG_USER_SAME_PASSWORD						=		110;							//昵称未修改
	int MSG_USER_UPDATE_PASSWORD_FAIL				=		111;							//昵称修改失败
	int MSG_LOCATION_UPLOAD_FAIL					=		112;							//用户上传地理位置失败
	int MSG_LOCATION_UPDATE_FAIL					=		113;							//用户更新地理位置失败
	int MSG_REGISTER_UPLOAD_AVATAR_FAIL				=		201;							//上传头像失败
	int MSG_UPLOAD_AVATAR_FAIL						=		202;							//更新头像失败
	int MSG_CONTACT_FIRENDED						=		301;							//已经是好友关系
	int MSG_CONTACT_ADD_FAIL						=		302;							//好友关系添加失败
	int MSG_CONTACT_DEL_FAIL						=		303;							//好友关系删除失败
	int MSG_GET_CONTACT_ALL_FAIL					=		304;							// 获取全部好友列表失败
	int MSG_GET_CONTACT_PAGES_FAIL					=		305;							// 分页获取好友列表失败
	int MSG_GROUP_HXID_EXISTS						=		401;							//群组环信ID已经存在
	int MSG_GROUP_CREATE_FAIL						=		402;							//创建群组失败
	int MSG_GROUP_ADD_MEMBER_FAIL					=		403;							//添加群组成员失败
	int MSG_GROUP_GET_MEMBERS_FAIL					=		404;							//获取群成员失败
	int MSG_GROUP_UNKONW							=		405;							//群组不存在
	int MSG_GROUP_SAME_NAME							=		406;							//有相同群组名称
	int MSG_GROUP_UPDATE_NAME_FAIL					=		407;							//群组名称修改失败
	int MSG_GROUP_DELETE_MEMBER_FAIL				=		408;							//删除群组成员失败
	int MSG_GROUP_DELETE_MEMBERS_FAIL				=		409;							//删除多群组成员失败
	int MSG_GROUP_DELETE_FAIL						=		410;							//删除群组失败
	int MSG_GROUP_FIND_BY_GOURP_ID_FAIL				=		411;							//根据群组id查找群组失败
	int MSG_GROUP_FIND_BY_HX_ID_FAIL				=		412;							//根据环信id查找群组失败
	int MSG_GROUP_FIND_BY_USER_NAME_FAIL			=		413;							//查找用户名称查找查找群组失败
	int MSG_GROUP_FIND_BY_GROUP_NAME_FAIL			=		414;							//查找群组名称查找查找群组失败
	int MSG_PUBLIC_GROUP_FAIL						=		415;							//查找公开群失败
	int MSG_LOCATION_GET_FAIL						=		501;							// 获取附近的人失败
	int MSG_UNKNOW									=		999;							//未知错误
	int MSG_ILLEGAL_REQUEST							=		-1;							//非法请求


	String KEY_REQUEST 								= 		"request";
	/** 上传图片的类型：user_avatar或group_icon */
	String AVATAR_TYPE 								= 		"avatarType";
	/** 用户姓名或hxid */
	String NAME_OR_HXID                             =       "name_or_hxid";
	/** 服务器状态的请求 */
	String REQUEST_SERVERSTATUS 					= 		"server_status";
	/** 客户端发送的注册请求 */
	String REQUEST_REGISTER		 					= 		"register";
	/**  发送取消注册的请求 */
	String REQUEST_UNREGISTER 						= 		"unregister";
	/** 客户端上传头像的请求 */
	String REQUEST_UPLOAD_AVATAR 					= 		"upload_avatar";
	/** 客户端更新用户昵称的请求 */
	String REQUEST_UPDATE_USER_NICK 				= 		"update_nick";
	/** 客户端修改密码的请求 */
	String REQUEST_UPDATE_USER_PASSWORD 			= 		"update_password";
	/** 客户端发送的登陆请求 */
	String REQUEST_LOGIN 							= 		"login";
	/** 客户端发送的下载用户头像请求 */
	String REQUEST_DOWNLOAD_AVATAR	 				= 		"download_avatar";
	/** 客户端发送的下载联系人所有集合请求 */
	String REQUEST_DOWNLOAD_CONTACT_ALL_LIST 		= 		"download_contact_all_list";
	/** 客户端发送的下载联系人集合请求 */
	String REQUEST_DOWNLOAD_CONTACT_PAGE_LIST 		= 		"download_contact_page_list";
	/** 客户端发送的删除联系人请求 */
	String REQUEST_DELETE_CONTACT 					= 		"delete_contact";
	/** 客户端发送的添加联系人请求 */
	String REQUEST_ADD_CONTACT 						= 		"add_contact";
	/** 客户端发送的查找用户请求 */
	String REQUEST_FIND_USER 						= 		"find_user";
	/** 客户端发送的根据用户或昵称模糊查找用户请求 */
	String REQUEST_FIND_USERS_FOR_SEARCH			= 		"find_users_for_search";
	/** 客户端发送的上传位置请求 */
	String REQUEST_UPLOAD_LOCATION 					= 		"upload_location";
	/** 客户端发送的更新位置请求 */
	String REQUEST_UPDATE_LOCATION 					= 		"update_location";
	/** 客户端发送的下载位置请求 */
	String REQUEST_DOWNLOAD_LOCATION 				= 		"download_location";
	/** 客户端发送的创建群组请求 */
	String REQUEST_CREATE_GROUP			 			= 		"create_group";
	/** 客户端发送的添加群成员请求 */
	String REQUEST_ADD_GROUP_MEMBER 				= 		"add_group_member";
	/** 客户端发送的添加多个群成员请求 */
	String REQUEST_ADD_GROUP_MEMBERS		 		= 		"add_group_members";
	/** 客户端发送的更新群名称请求 */
	String REQUEST_UPDATE_GROUP_NAME 				= 		"update_group_name";
	/** 客户端发送的下载多个群成员请求 */
	String REQUEST_DOWNLOAD_GROUP_MEMBERS 			= 		"download_group_members_by_groupid";
	/** 客户端发送的下载多个群成员请求 */
	String REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_LIMIT 	= 		"download_group_members_by_limit";
	/** 客户端发送的下载多个群成员请求 */
	String REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_HXID 	= 		"download_group_members_by_hxid";
	/** 客户端发送的下载多个群成员请求 */
	String REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_HXID_LIMIT 	= 		"download_group_members_by_hxid_limit";
	/** 客户端发送的删除群成员请求 */
	String REQUEST_DELETE_GROUP_MEMBER 				= 		"delete_group_member";
	/** 客户端发送的删除多个群成员请求 */
	String REQUEST_DELETE_GROUP_MEMBERS 			= 		"delete_group_members";
	/** 客户端发送的删除群组请求 */
	String REQUEST_DELETE_GROUP 					= 		"delete_group";
	/** 客户端发送的下载公开裙请求 */
	String REQUEST_FIND_PUBLIC_GROUPS 				= 		"download_public_groups";
	/** 客户端发送的根据群组名称模糊查找群组请求 */
	String REQUEST_FIND_GROUP_BY_GROUP_NAME 						= 		"find_group_by_group_name";
	/** 客户端发送的用户姓名查找用户所在的群组请求 */
	String REQUEST_FIND_GROUP_BY_USER_NAME 						= 		"find_group_by_user_name";
	/** 客户端发送的根据群组账号查找群组请求 */
	String REQUEST_FIND_GROUP_BY_ID					= 		"find_group_by_group_id";
	/** 客户端发送的根据群组环信id查找群组请求 */
	String REQUEST_FIND_GROUP_BY_HXID 				= 		"find_group_by_group_hxid";
	/** 客户端发送的根据群组环信id查找公开群组请求 */
	String REQUEST_FIND_PUBLIC_GROUP_BY_HXID 		= 		"find_public_group_by_group_hxid";
}
