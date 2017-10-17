package com.zhanghuaming.myserver.plugin;

import java.sql.Date;

public class PluginApp {
	private int num;
	private String packageName;
	private String appName;
	private String pluginVersion;
	private String zipName;
	private String iconName;
	private String info;
	private String funtion;
	private Date date;
	private String owner;
	private String other;
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getPluginVersion() {
		return pluginVersion;
	}
	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}
	public String getZipName() {
		return zipName;
	}
	public void setZipName(String zipName) {
		this.zipName = zipName;
	}
	public String getIconName() {
		return iconName;
	}
	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getFuntion() {
		return funtion;
	}
	public void setFuntion(String funtion) {
		this.funtion = funtion;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	@Override
	public String toString() {
		return "PluginApp [num=" + num + ", packageName=" + packageName + ", appName=" + appName + ", pluginVersion="
				+ pluginVersion + ", zipName=" + zipName + ", iconName=" + iconName + ", info=" + info + ", funtion="
				+ funtion + ", date=" + date + ", owner=" + owner + ", other=" + other + "]";
	}

	
}
