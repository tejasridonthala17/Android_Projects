package com.example.phonebook.util;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0006\u001a\u00020\u0007\u00a8\u0006\b"}, d2 = {"Lcom/example/phonebook/util/SmsReader;", "", "()V", "readDeviceSms", "", "Lcom/example/phonebook/data/SmsEntry;", "context", "Landroid/content/Context;", "app_debug"})
public final class SmsReader {
    @org.jetbrains.annotations.NotNull()
    public static final com.example.phonebook.util.SmsReader INSTANCE = null;
    
    private SmsReader() {
        super();
    }
    
    /**
     * Reads inbox SMS via the system SMS Content Provider. Requires READ_SMS.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.phonebook.data.SmsEntry> readDeviceSms(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
}