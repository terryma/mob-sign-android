package com.amazon;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class Tuple implements Parcelable {

	String uid;
	String site;

	public static final Parcelable.Creator<Tuple> CREATOR = new Parcelable.Creator<Tuple>() {
		public Tuple createFromParcel(Parcel in) {
			Gson gson = new Gson();
			return gson.fromJson(in.readString(), Tuple.class);
		}

		public Tuple[] newArray(int size) {
			return new Tuple[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Gson gson = new Gson();
		// TODO Auto-generated method stub
		dest.writeString(gson.toJson(this));
	}
}
