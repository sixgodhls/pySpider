package com.goldze.mvvmhabit.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class MovieEntity implements Parcelable {
    public static final Parcelable.Creator<MovieEntity> CREATOR = new Parcelable.Creator<MovieEntity>() { // from class: com.goldze.mvvmhabit.entity.MovieEntity.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        /* renamed from: createFromParcel */
        public MovieEntity mo305createFromParcel(Parcel parcel) {
            return new MovieEntity(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        /* renamed from: newArray */
        public MovieEntity[] mo306newArray(int i) {
            return new MovieEntity[i];
        }
    };
    @SerializedName("alias")
    private String alias;
    @SerializedName("cover")
    private String cover;
    @SerializedName("drama")
    private String drama;
    @SerializedName("id")

    /* renamed from: id */
    private int f66id;
    @SerializedName("minute")
    private int minute;
    @SerializedName("name")
    private String name;
    @SerializedName("published_at")
    private String publishedAt;
    @SerializedName("score")
    private float score;
    @SerializedName("categories")
    private List<String> categories = new ArrayList();
    @SerializedName("regions")
    private List<String> regions = new ArrayList();

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public MovieEntity(Parcel parcel) {
        this.f66id = parcel.readInt();
        this.name = parcel.readString();
        this.alias = parcel.readString();
        this.publishedAt = parcel.readString();
        this.cover = parcel.readString();
        this.drama = parcel.readString();
        parcel.readList(this.categories, String.class.getClassLoader());
        parcel.readList(this.regions, String.class.getClassLoader());
        this.score = parcel.readFloat();
        this.minute = parcel.readInt();
    }

    public String getCategories() {
        return TextUtils.join("、", this.categories);
    }

    public void setCategories(List<String> list) {
        this.categories = list;
    }

    public List<String> getRegions() {
        return this.regions;
    }

    public void setRegions(List<String> list) {
        this.regions = list;
    }

    public String getPublishedAt() {
        return this.publishedAt;
    }

    public void setPublishedAt(String str) {
        this.publishedAt = str;
    }

    public String getDrama() {
        return this.drama;
    }

    public void setDrama(String str) {
        this.drama = str;
    }

    public int getId() {
        return this.f66id;
    }

    public void setId(int i) {
        this.f66id = i;
    }

    public String getCover() {
        return this.cover;
    }

    public void setCover(String str) {
        this.cover = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getMinute() {
        return String.valueOf(this.minute);
    }

    public void setMinute(int i) {
        this.minute = i;
    }

    public String getScore() {
        return String.valueOf(this.score);
    }

    public void setScore(float f) {
        this.score = f;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String str) {
        this.alias = str;
    }

    @NonNull
    public String toString() {
        return String.format("MovieEntity{id=%s, name=%s, alias=%s, publishedAt=%s, cover=%s, drama=%s, categories=%s, regions=%s, score=%s, minute=%s}", Integer.valueOf(this.f66id), this.name, this.alias, this.publishedAt, this.cover, this.drama, this.categories, this.regions, Float.valueOf(this.score), Integer.valueOf(this.minute));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.f66id);
        parcel.writeString(this.name);
        parcel.writeString(this.alias);
        parcel.writeString(this.publishedAt);
        parcel.writeString(this.cover);
        parcel.writeString(this.drama);
        parcel.writeList(this.categories);
        parcel.writeList(this.regions);
        parcel.writeFloat(this.score);
        parcel.writeInt(this.minute);
    }
}