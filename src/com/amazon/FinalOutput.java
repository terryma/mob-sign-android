package com.amazon;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.Gson;

public class FinalOutput implements Parcelable {
	public String msg;
	public List<Tuple> auth_requests;

	public static final Parcelable.Creator<FinalOutput> CREATOR = new Parcelable.Creator<FinalOutput>() {
		public FinalOutput createFromParcel(Parcel in) {
			Gson gson = new Gson();
			return gson.fromJson(in.readString(), FinalOutput.class);
		}

		public FinalOutput[] newArray(int size) {
			return new FinalOutput[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Gson gson = new Gson();
		dest.writeString(gson.toJson(this));
	}

}
