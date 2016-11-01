package com.mx.android.password.entity;

import com.mx.android.password.utils.Utils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mxuan on 2016-07-11.
 */
public class God extends RealmObject {

    /**
     * 密码所属的分组
     */
    private int godType;

    /**
     * 标题，注明该账号为哪个网站的账号
     */
    @PrimaryKey
    private String title;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String passWord;

    /**
     * 创建的时间,或者修改时间
     */
    private long time;

    /**
     * 备忘信息
     */
    private String memoInfo;

    private byte[] img;

    public God() {
    }

    public God(int godType, String title, String userName, String passWord, long time, String memoInfo) {
        this.godType = godType;
        this.title = Utils.EncData(title);
        this.userName = Utils.EncData(userName);
        this.passWord = Utils.EncData(passWord);
        this.time = time;
        this.memoInfo = Utils.EncData(memoInfo);
    }

    public int getGodType() {
        return godType;
    }

    public void setGodType(int godType) {
        this.godType = godType;
    }

    public String getUserName() {
        return Utils.DecData(userName);
    }

    public void setUserName(String userName) {
        this.userName = Utils.EncData(userName);
    }

    public String getPassWord() {
        return Utils.DecData(passWord);
    }

    public void setPassWord(String passWord) {
        this.passWord = Utils.EncData(passWord);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return Utils.DecData(title);
    }

    public void setTitle(String title) {
        this.title = Utils.EncData(title);
    }

    public String getMemoInfo() {
        return Utils.DecData(memoInfo);
    }

    public void setMemoInfo(String memoInfo) {
        this.memoInfo = Utils.EncData(memoInfo);
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }
}
