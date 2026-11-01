package com.example.phonebook.ui.contacts;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002JP\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\b26\u0010\t\u001a2\u0012\u0013\u0012\u00110\u000b\u00a2\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\r\u0012\u0013\u0012\u00110\u000b\u00a2\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u00020\u00040\n\u00a8\u0006\u000f"}, d2 = {"Lcom/example/phonebook/ui/contacts/AddEditContactDialog;", "", "()V", "show", "", "context", "Landroid/content/Context;", "existing", "Lcom/example/phonebook/data/Contact;", "onSave", "Lkotlin/Function2;", "", "Lkotlin/ParameterName;", "name", "phone", "app_debug"})
public final class AddEditContactDialog {
    @org.jetbrains.annotations.NotNull()
    public static final com.example.phonebook.ui.contacts.AddEditContactDialog INSTANCE = null;
    
    private AddEditContactDialog() {
        super();
    }
    
    /**
     * Pass existing = null for "add", or a Contact for "edit".
     */
    public final void show(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    com.example.phonebook.data.Contact existing, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.String, ? super java.lang.String, kotlin.Unit> onSave) {
    }
}