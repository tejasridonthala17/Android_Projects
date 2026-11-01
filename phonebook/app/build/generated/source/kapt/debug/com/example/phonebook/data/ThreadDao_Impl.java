package com.example.phonebook.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ThreadDao_Impl implements ThreadDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ConversationThread> __insertionAdapterOfConversationThread;

  private final EntityDeletionOrUpdateAdapter<ConversationThread> __deletionAdapterOfConversationThread;

  private final EntityDeletionOrUpdateAdapter<ConversationThread> __updateAdapterOfConversationThread;

  public ThreadDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfConversationThread = new EntityInsertionAdapter<ConversationThread>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `threads` (`threadId`,`contactName`,`phoneNumber`,`lastMessage`,`lastMessageTime`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ConversationThread entity) {
        statement.bindLong(1, entity.getThreadId());
        if (entity.getContactName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getContactName());
        }
        if (entity.getPhoneNumber() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getPhoneNumber());
        }
        if (entity.getLastMessage() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getLastMessage());
        }
        statement.bindLong(5, entity.getLastMessageTime());
      }
    };
    this.__deletionAdapterOfConversationThread = new EntityDeletionOrUpdateAdapter<ConversationThread>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `threads` WHERE `threadId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ConversationThread entity) {
        statement.bindLong(1, entity.getThreadId());
      }
    };
    this.__updateAdapterOfConversationThread = new EntityDeletionOrUpdateAdapter<ConversationThread>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `threads` SET `threadId` = ?,`contactName` = ?,`phoneNumber` = ?,`lastMessage` = ?,`lastMessageTime` = ? WHERE `threadId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ConversationThread entity) {
        statement.bindLong(1, entity.getThreadId());
        if (entity.getContactName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getContactName());
        }
        if (entity.getPhoneNumber() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getPhoneNumber());
        }
        if (entity.getLastMessage() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getLastMessage());
        }
        statement.bindLong(5, entity.getLastMessageTime());
        statement.bindLong(6, entity.getThreadId());
      }
    };
  }

  @Override
  public Object insert(final ConversationThread thread,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfConversationThread.insertAndReturnId(thread);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final ConversationThread thread,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfConversationThread.handle(thread);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ConversationThread thread,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfConversationThread.handle(thread);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<List<ConversationThread>> getAll() {
    final String _sql = "SELECT * FROM threads ORDER BY lastMessageTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"threads"}, false, new Callable<List<ConversationThread>>() {
      @Override
      @Nullable
      public List<ConversationThread> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfThreadId = CursorUtil.getColumnIndexOrThrow(_cursor, "threadId");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfLastMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMessage");
          final int _cursorIndexOfLastMessageTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMessageTime");
          final List<ConversationThread> _result = new ArrayList<ConversationThread>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ConversationThread _item;
            final long _tmpThreadId;
            _tmpThreadId = _cursor.getLong(_cursorIndexOfThreadId);
            final String _tmpContactName;
            if (_cursor.isNull(_cursorIndexOfContactName)) {
              _tmpContactName = null;
            } else {
              _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            }
            final String _tmpPhoneNumber;
            if (_cursor.isNull(_cursorIndexOfPhoneNumber)) {
              _tmpPhoneNumber = null;
            } else {
              _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            }
            final String _tmpLastMessage;
            if (_cursor.isNull(_cursorIndexOfLastMessage)) {
              _tmpLastMessage = null;
            } else {
              _tmpLastMessage = _cursor.getString(_cursorIndexOfLastMessage);
            }
            final long _tmpLastMessageTime;
            _tmpLastMessageTime = _cursor.getLong(_cursorIndexOfLastMessageTime);
            _item = new ConversationThread(_tmpThreadId,_tmpContactName,_tmpPhoneNumber,_tmpLastMessage,_tmpLastMessageTime);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object findByPhone(final String phone,
      final Continuation<? super ConversationThread> $completion) {
    final String _sql = "SELECT * FROM threads WHERE phoneNumber = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (phone == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, phone);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ConversationThread>() {
      @Override
      @Nullable
      public ConversationThread call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfThreadId = CursorUtil.getColumnIndexOrThrow(_cursor, "threadId");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfLastMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMessage");
          final int _cursorIndexOfLastMessageTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMessageTime");
          final ConversationThread _result;
          if (_cursor.moveToFirst()) {
            final long _tmpThreadId;
            _tmpThreadId = _cursor.getLong(_cursorIndexOfThreadId);
            final String _tmpContactName;
            if (_cursor.isNull(_cursorIndexOfContactName)) {
              _tmpContactName = null;
            } else {
              _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            }
            final String _tmpPhoneNumber;
            if (_cursor.isNull(_cursorIndexOfPhoneNumber)) {
              _tmpPhoneNumber = null;
            } else {
              _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            }
            final String _tmpLastMessage;
            if (_cursor.isNull(_cursorIndexOfLastMessage)) {
              _tmpLastMessage = null;
            } else {
              _tmpLastMessage = _cursor.getString(_cursorIndexOfLastMessage);
            }
            final long _tmpLastMessageTime;
            _tmpLastMessageTime = _cursor.getLong(_cursorIndexOfLastMessageTime);
            _result = new ConversationThread(_tmpThreadId,_tmpContactName,_tmpPhoneNumber,_tmpLastMessage,_tmpLastMessageTime);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id, final Continuation<? super ConversationThread> $completion) {
    final String _sql = "SELECT * FROM threads WHERE threadId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ConversationThread>() {
      @Override
      @Nullable
      public ConversationThread call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfThreadId = CursorUtil.getColumnIndexOrThrow(_cursor, "threadId");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfLastMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMessage");
          final int _cursorIndexOfLastMessageTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMessageTime");
          final ConversationThread _result;
          if (_cursor.moveToFirst()) {
            final long _tmpThreadId;
            _tmpThreadId = _cursor.getLong(_cursorIndexOfThreadId);
            final String _tmpContactName;
            if (_cursor.isNull(_cursorIndexOfContactName)) {
              _tmpContactName = null;
            } else {
              _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            }
            final String _tmpPhoneNumber;
            if (_cursor.isNull(_cursorIndexOfPhoneNumber)) {
              _tmpPhoneNumber = null;
            } else {
              _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            }
            final String _tmpLastMessage;
            if (_cursor.isNull(_cursorIndexOfLastMessage)) {
              _tmpLastMessage = null;
            } else {
              _tmpLastMessage = _cursor.getString(_cursorIndexOfLastMessage);
            }
            final long _tmpLastMessageTime;
            _tmpLastMessageTime = _cursor.getLong(_cursorIndexOfLastMessageTime);
            _result = new ConversationThread(_tmpThreadId,_tmpContactName,_tmpPhoneNumber,_tmpLastMessage,_tmpLastMessageTime);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
