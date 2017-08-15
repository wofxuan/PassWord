package com.mx.android.password.entity;

/**
 * Created by mxuan on 2016-07-11.
 */
public class Account{
    private String guidPW;//ID

    private int rowIndex;//行号
    private String accountType;//密码所属的分组
    private String title;//标题，注明该账号为哪个网站的账号
    private String userName;//用户名
    private String passWord;//密码
    private String time;//创建的时间,或者修改时间
    private String memoInfo;//备忘信息
    private byte[] img;

    public Account() {
    }

    public Account(String accountType, String title, String userName, String passWord, String time, String memoInfo) {
        this.accountType = accountType;
        this.title = title;
        this.userName = userName;
        this.passWord = passWord;
        this.time = time;
        this.memoInfo = memoInfo;
    }

    public String getGuidPW() {
        return guidPW;
    }

    public void setGuidPW(String guidPW) {
        this.guidPW = guidPW;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMemoInfo() {
        return memoInfo;
    }

    public void setMemoInfo(String memoInfo) {
        this.memoInfo = memoInfo;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }
}
