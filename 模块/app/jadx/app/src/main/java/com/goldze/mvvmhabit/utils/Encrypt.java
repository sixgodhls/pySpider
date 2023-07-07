package com.goldze.mvvmhabit.utils;

import android.text.TextUtils;
import android.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class Encrypt {
    public static String shaEncrypt(String str) {
        byte[] bytes = str.getBytes();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(bytes);
            return bytes2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException unused) {
            return null;
        }
    }

    public static String bytes2Hex(byte[] bArr) {
        String str = "";
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                str = str + "0";
            }
            str = str + hexString;
        }
        return str;
    }

    public static String encrypt(List<String> list) {
        String valueOf = String.valueOf(new Timestamp(System.currentTimeMillis()).getTime() / 1000);
        list.add(valueOf);
        String shaEncrypt = shaEncrypt(TextUtils.join(",", list));
        ArrayList arrayList = new ArrayList();
        arrayList.add(shaEncrypt);
        arrayList.add(valueOf);
        return Base64.encodeToString(TextUtils.join(",", arrayList).getBytes(), 0);
    }
}
