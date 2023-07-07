package me.goldze.mvvmhabit.http.download;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/* loaded from: classes.dex */
public class DownLoadStateBean implements Serializable, Parcelable {
    public static final Parcelable.Creator<DownLoadStateBean> CREATOR = new Parcelable.Creator<DownLoadStateBean>() { // from class: me.goldze.mvvmhabit.http.download.DownLoadStateBean.1
        @Override // android.os.Parcelable.Creator
        public DownLoadStateBean createFromParcel(Parcel source) {
            return new DownLoadStateBean(source);
        }

        @Override // android.os.Parcelable.Creator
        public DownLoadStateBean[] newArray(int size) {
            return new DownLoadStateBean[size];
        }
    };
    long bytesLoaded;
    String tag;
    long total;

    public DownLoadStateBean(long total, long bytesLoaded) {
        this.total = total;
        this.bytesLoaded = bytesLoaded;
    }

    public DownLoadStateBean(long total, long bytesLoaded, String tag) {
        this.total = total;
        this.bytesLoaded = bytesLoaded;
        this.tag = tag;
    }

    public long getTotal() {
        return this.total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getBytesLoaded() {
        return this.bytesLoaded;
    }

    public void setBytesLoaded(long bytesLoaded) {
        this.bytesLoaded = bytesLoaded;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.total);
        dest.writeLong(this.bytesLoaded);
        dest.writeString(this.tag);
    }

    protected DownLoadStateBean(Parcel in) {
        this.total = in.readLong();
        this.bytesLoaded = in.readLong();
        this.tag = in.readString();
    }
}
