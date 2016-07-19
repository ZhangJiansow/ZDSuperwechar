package cn.ucai.superwechat.bean;

public class UserAvatar {
	private String muserName;
//	private String muserPassword;
	private String muserNick;
//	private Integer muserUnreadMsgCount;
	private Integer mavatarId;
//	private String mavatarUserName;
	private String mavatarPath;
	private Integer mavatarType;
	private String mavatarLastUpdateTime;
	
	public UserAvatar() {
		super();
	}

	public UserAvatar(String muserName/*, String muserPassword*/, String muserNick/*, Integer muserUnreadMsgCount*/,
			Integer mavatarId/*, String mavatarUserName*/, String mavatarPath, Integer mavatarType,
			String mavatarLastUpdateTime) {
		super();
		this.muserName = muserName;
//		this.muserPassword = muserPassword;
		this.muserNick = muserNick;
//		this.muserUnreadMsgCount = muserUnreadMsgCount;
		this.mavatarId = mavatarId;
//		this.mavatarUserName = mavatarUserName;
		this.mavatarPath = mavatarPath;
		this.mavatarType = mavatarType;
		this.mavatarLastUpdateTime = mavatarLastUpdateTime;
	}

	public String getMUserName() {
		return muserName;
	}

	public void setMUserName(String muserName) {
		this.muserName = muserName;
	}
/*
	public String getMUserPassword() {
		return muserPassword;
	}

	public void setMUserPassword(String muserPassword) {
		this.muserPassword = muserPassword;
	}
*/
	public String getMUserNick() {
		return muserNick;
	}

	public void setMUserNick(String muserNick) {
		this.muserNick = muserNick;
	}
/*
	public Integer getMUserUnreadMsgCount() {
		return muserUnreadMsgCount;
	}

	public void setMUserUnreadMsgCount(Integer muserUnreadMsgCount) {
		this.muserUnreadMsgCount = muserUnreadMsgCount;
	}
*/
	public Integer getMAvatarId() {
		return mavatarId;
	}

	public void setMAvatarId(Integer mavatarId) {
		this.mavatarId = mavatarId;
	}
/*
	public String getMAvatarUserName() {
		return mavatarUserName;
	}

	public void setMAvatarUserName(String mavatarUserName) {
		this.mavatarUserName = mavatarUserName;
	}
*/
	public String getMAvatarPath() {
		return mavatarPath;
	}

	public void setMAvatarPath(String mavatarPath) {
		this.mavatarPath = mavatarPath;
	}

	public Integer getMAvatarType() {
		return mavatarType;
	}

	public void setMAvatarType(Integer mavatarType) {
		this.mavatarType = mavatarType;
	}

	public String getMAvatarLastUpdateTime() {
		return mavatarLastUpdateTime;
	}

	public void setMAvatarLastUpdateTime(String mavatarLastUpdateTime) {
		this.mavatarLastUpdateTime = mavatarLastUpdateTime;
	}

	@Override
	public String toString() {
		return "UserAvatar [muserName=" + muserName + ", muserNick=" + muserNick + ", mavatarId=" + mavatarId
				+ ", mavatarPath=" + mavatarPath + ", mavatarType=" + mavatarType + ", mavatarLastUpdateTime="
				+ mavatarLastUpdateTime + "]";
	}
}
