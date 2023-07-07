package com.google.gson;

/* loaded from: classes.dex */
public final class JsonNull extends JsonElement {
    public static final JsonNull INSTANCE = new JsonNull();

    @Override // com.google.gson.JsonElement
    /* renamed from: deepCopy  reason: collision with other method in class */
    public JsonNull mo314deepCopy() {
        return INSTANCE;
    }

    public int hashCode() {
        return JsonNull.class.hashCode();
    }

    public boolean equals(Object other) {
        return this == other || (other instanceof JsonNull);
    }
}