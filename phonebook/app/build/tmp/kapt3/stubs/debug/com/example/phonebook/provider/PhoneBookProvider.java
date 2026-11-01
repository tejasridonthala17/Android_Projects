package com.example.phonebook.provider;

/**
 * Custom Content Provider exposing the contacts table.
 * Other components (e.g. ComposeActivity) access data ONLY via ContentResolver.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 $2\u00020\u0001:\u0001$B\u0005\u00a2\u0006\u0002\u0010\u0002J1\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0010\u0010\u000f\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000e\u0018\u00010\u0010H\u0016\u00a2\u0006\u0002\u0010\u0011J\u0012\u0010\u0012\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u001c\u0010\u0013\u001a\u0004\u0018\u00010\f2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0016J\b\u0010\u0016\u001a\u00020\u0017H\u0016JO\u0010\u0018\u001a\u0004\u0018\u00010\u00192\u0006\u0010\u000b\u001a\u00020\f2\u0010\u0010\u001a\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000e\u0018\u00010\u00102\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0010\u0010\u000f\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000e\u0018\u00010\u00102\b\u0010\u001b\u001a\u0004\u0018\u00010\u000eH\u0016\u00a2\u0006\u0002\u0010\u001cJ;\u0010\u001d\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\u0014\u001a\u0004\u0018\u00010\u00152\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0010\u0010\u000f\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000e\u0018\u00010\u0010H\u0016\u00a2\u0006\u0002\u0010\u001eJ\u0014\u0010\u001f\u001a\u00020 *\u00020!2\u0006\u0010\"\u001a\u00020#H\u0002R\u001b\u0010\u0003\u001a\u00020\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006%"}, d2 = {"Lcom/example/phonebook/provider/PhoneBookProvider;", "Landroid/content/ContentProvider;", "()V", "db", "Lcom/example/phonebook/data/AppDatabase;", "getDb", "()Lcom/example/phonebook/data/AppDatabase;", "db$delegate", "Lkotlin/Lazy;", "delete", "", "uri", "Landroid/net/Uri;", "selection", "", "selectionArgs", "", "(Landroid/net/Uri;Ljava/lang/String;[Ljava/lang/String;)I", "getType", "insert", "values", "Landroid/content/ContentValues;", "onCreate", "", "query", "Landroid/database/Cursor;", "projection", "sortOrder", "(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;", "update", "(Landroid/net/Uri;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I", "addContact", "", "Landroid/database/MatrixCursor;", "c", "Lcom/example/phonebook/data/Contact;", "Companion", "app_debug"})
public final class PhoneBookProvider extends android.content.ContentProvider {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String AUTHORITY = "com.example.phonebook.provider";
    @org.jetbrains.annotations.NotNull()
    private static final android.net.Uri CONTENT_URI = null;
    private static final int CONTACTS = 1;
    private static final int CONTACT_ID = 2;
    @org.jetbrains.annotations.NotNull()
    private static final android.content.UriMatcher matcher = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String[] COLUMNS = {"contactId", "name", "phoneNumber", "source"};
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy db$delegate = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.phonebook.provider.PhoneBookProvider.Companion Companion = null;
    
    public PhoneBookProvider() {
        super();
    }
    
    private final com.example.phonebook.data.AppDatabase getDb() {
        return null;
    }
    
    @java.lang.Override()
    public boolean onCreate() {
        return false;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public android.database.Cursor query(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.Nullable()
    java.lang.String[] projection, @org.jetbrains.annotations.Nullable()
    java.lang.String selection, @org.jetbrains.annotations.Nullable()
    java.lang.String[] selectionArgs, @org.jetbrains.annotations.Nullable()
    java.lang.String sortOrder) {
        return null;
    }
    
    private final void addContact(android.database.MatrixCursor $this$addContact, com.example.phonebook.data.Contact c) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.String getType(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public android.net.Uri insert(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.Nullable()
    android.content.ContentValues values) {
        return null;
    }
    
    @java.lang.Override()
    public int update(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.Nullable()
    android.content.ContentValues values, @org.jetbrains.annotations.Nullable()
    java.lang.String selection, @org.jetbrains.annotations.Nullable()
    java.lang.String[] selectionArgs) {
        return 0;
    }
    
    @java.lang.Override()
    public int delete(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.Nullable()
    java.lang.String selection, @org.jetbrains.annotations.Nullable()
    java.lang.String[] selectionArgs) {
        return 0;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0006\u00a2\u0006\n\n\u0002\u0010\t\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\n\u001a\u00020\u000bX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u000bX\u0082T\u00a2\u0006\u0002\n\u0000R\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/example/phonebook/provider/PhoneBookProvider$Companion;", "", "()V", "AUTHORITY", "", "COLUMNS", "", "getCOLUMNS", "()[Ljava/lang/String;", "[Ljava/lang/String;", "CONTACTS", "", "CONTACT_ID", "CONTENT_URI", "Landroid/net/Uri;", "getCONTENT_URI", "()Landroid/net/Uri;", "matcher", "Landroid/content/UriMatcher;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.net.Uri getCONTENT_URI() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String[] getCOLUMNS() {
            return null;
        }
    }
}