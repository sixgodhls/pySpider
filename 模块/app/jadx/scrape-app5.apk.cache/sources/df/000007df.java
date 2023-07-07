package com.tbruyelle.rxpermissions2;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import java.util.List;

/* loaded from: classes.dex */
public class Permission {
    public final boolean granted;
    public final String name;
    public final boolean shouldShowRequestPermissionRationale;

    public Permission(String name, boolean granted) {
        this(name, granted, false);
    }

    public Permission(String name, boolean granted, boolean shouldShowRequestPermissionRationale) {
        this.name = name;
        this.granted = granted;
        this.shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale;
    }

    public Permission(List<Permission> permissions) {
        this.name = combineName(permissions);
        this.granted = combineGranted(permissions).booleanValue();
        this.shouldShowRequestPermissionRationale = combineShouldShowRequestPermissionRationale(permissions).booleanValue();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Permission that = (Permission) o;
        if (this.granted != that.granted || this.shouldShowRequestPermissionRationale != that.shouldShowRequestPermissionRationale) {
            return false;
        }
        return this.name.equals(that.name);
    }

    public int hashCode() {
        int result = this.name.hashCode();
        return (((result * 31) + (this.granted ? 1 : 0)) * 31) + (this.shouldShowRequestPermissionRationale ? 1 : 0);
    }

    public String toString() {
        return "Permission{name='" + this.name + "', granted=" + this.granted + ", shouldShowRequestPermissionRationale=" + this.shouldShowRequestPermissionRationale + '}';
    }

    private String combineName(List<Permission> permissions) {
        return ((StringBuilder) Observable.fromIterable(permissions).map(new Function<Permission, String>() { // from class: com.tbruyelle.rxpermissions2.Permission.2
            @Override // io.reactivex.functions.Function
            /* renamed from: apply  reason: avoid collision after fix types in other method */
            public String mo401apply(Permission permission) throws Exception {
                return permission.name;
            }
        }).collectInto(new StringBuilder(), new BiConsumer<StringBuilder, String>() { // from class: com.tbruyelle.rxpermissions2.Permission.1
            @Override // io.reactivex.functions.BiConsumer
            public void accept(StringBuilder s, String s2) throws Exception {
                if (s.length() == 0) {
                    s.append(s2);
                    return;
                }
                s.append(", ");
                s.append(s2);
            }
        }).blockingGet()).toString();
    }

    private Boolean combineGranted(List<Permission> permissions) {
        return Observable.fromIterable(permissions).all(new Predicate<Permission>() { // from class: com.tbruyelle.rxpermissions2.Permission.3
            @Override // io.reactivex.functions.Predicate
            public boolean test(Permission permission) throws Exception {
                return permission.granted;
            }
        }).blockingGet();
    }

    private Boolean combineShouldShowRequestPermissionRationale(List<Permission> permissions) {
        return Observable.fromIterable(permissions).any(new Predicate<Permission>() { // from class: com.tbruyelle.rxpermissions2.Permission.4
            @Override // io.reactivex.functions.Predicate
            public boolean test(Permission permission) throws Exception {
                return permission.shouldShowRequestPermissionRationale;
            }
        }).blockingGet();
    }
}