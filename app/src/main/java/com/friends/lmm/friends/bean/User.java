package com.friends.lmm.friends.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

public class User extends BmobUser {
	private BmobRelation collect;
    private String sex;
    private String img_url;
	private BmobFile photo;
	private BmobRelation myPost;
	private String words;
	private String head;

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}



	public BmobFile getPhoto() {
		return photo;
	}

	public void setPhoto(BmobFile photo) {
		this.photo = photo;
	}


	public BmobRelation getCollect() {
		return collect;
	}
	public void setCollect(BmobRelation collect) {
		this.collect = collect;
	}
	public BmobRelation getMyPost() {
		return myPost;
	}
	public void setMyPost(BmobRelation myPost) {
		this.myPost = myPost;
	}
	
	
}
