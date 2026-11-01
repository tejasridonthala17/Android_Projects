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
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
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
public final class MessageDao_Impl implements MessageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Message> __insertionAdapterOfMessage;

  private final EntityDeletionOrUpdateAdapter<Message> __deletionAdapterOfMessage;

  private final EntityDeletionOrUpdateAdapter<Message> __updateAdapterOfMessage;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMessagesForThread;

  public MessageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMessage = new EntityInsertionAdapter<Message>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `messages` (`messageId`,`threadId`,`senderOrRecipient`,`messageText`,`timestamp`,`direction`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Message entity) {
        statement.bindLong(1, entity.getMessageId());
        statement.bindLong(2, entity.getThreadId());
        if (entity.getSenderOrRecipient() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getSenderOrRecipient());
        }
        if (entity.getMessageText() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getMessageText());
        }
        statement.bindLong(5, entity.getTimestamp());
        if (entity.getDirection() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getDirection());
        }
      }
    };
    this.__deletionAdapterOfMessage = new EntityDeletionOrUpdateAdapter<Message>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `messages` WHERE `messageId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Message entity) {
        statement.bindLong(1, entity.getMessageId());
      }
    };
    this.__updateAdapterOfMessage = new EntityDeletionOrUpdateAdapter<Message>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `messages` SET `messageId` = ?,`threadId` = ?,`senderOrRecipient` = ?,`messageText` = ?,`timestamp` = ?,`direction` = ? WHERE `messageId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Message entity) {
        statement.bindLong(1, entity.getMessageId());
        statement.bindLong(2, entity.getThreadId());
        if (entity.getSenderOrRecipient() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getSenderOrRecipient());
        }
        if (entity.getMessageText() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getMessageText());
        }
        statement.bindLong(5, entity.getTimestamp());
        if (entity.getDirection() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getDirection());
        }
        statement.bindLong(7, entity.getMessageId());
      }
    };
    this.__preparedStmtOfDeleteMessagesForThread = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM messages WHERE threadId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Message message, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMessage.insert(message);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Message message, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfMessage.handle(message);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Message message, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMessage.handle(message);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMessagesForThread(final long threadId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMessagesForThread.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, threadId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteMessagesForThread.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<List<Message>> getMessagesForThread(final long threadId) {
    final String _sql = "SELECT * FROM messages WHERE threadId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, threadId);
    return __db.getInvalidationTracker().createLiveData(new String[] {"messages"}, false, new Callable<List<Message>>() {
      @Override
      @Nullable
      public List<Message> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMessageId = CursorUtil.getColumnIndexOrThrow(_cursor, "messageId");
          final int _cursorIndexOfThreadId = CursorUtil.getColumnIndexOrThrow(_cursor, "threadId");
          final int _cursorIndexOfSenderOrRecipient = CursorUtil.getColumnIndexOrThrow(_cursor, "senderOrRecipient");
          final int _cursorIndexOfMessageText = CursorUtil.getColumnIndexOrThrow(_cursor, "messageText");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final List<Message> _result = new ArrayList<Message>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Message _item;
            final long _tmpMessageId;
            _tmpMessageId = _cursor.getLong(_cursorIndexOfMessageId);
            final long _tmpThreadId;
            _tmpThreadId = _cursor.getLong(_cursorIndexOfThreadId);
            final String _tmpSenderOrRecipient;
            if (_cursor.isNull(_cursorIndexOfSenderOrRecipient)) {
              _tmpSenderOrRecipient = null;
            } else {
              _tmpSenderOrRecipient = _cursor.getString(_cursorIndexOfSenderOrRecipient);
            }
            final String _tmpMessageText;
            if (_cursor.isNull(_cursorIndexOfMessageText)) {
              _tmpMessageText = null;
            } else {
              _tmpMessageText = _cursor.getString(_cursorIndexOfMessageText);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpDirection;
            if (_cursor.isNull(_cursorIndexOfDirection)) {
              _tmpDirection = null;
            } else {
              _tmpDirection = _cursor.getString(_cursorIndexOfDirection);
            }
            _item = new Message(_tmpMessageId,_tmpThreadId,_tmpSenderOrRecipient,_tmpMessageText,_tmpTimestamp,_tmpDirection);
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
  public Object getMessagesForThreadSync(final long threadId,
      final Continuation<? super List<Message>> $completion) {
    final String _sql = "SELECT * FROM messages WHERE threadId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, threadId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Message>>() {
      @Override
      @NonNull
      public List<Message> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMessageId = CursorUtil.getColumnIndexOrThrow(_cursor, "messageId");
          final int _cursorIndexOfThreadId = CursorUtil.getColumnIndexOrThrow(_cursor, "threadId");
          final int _cursorIndexOfSenderOrRecipient = CursorUtil.getColumnIndexOrThrow(_cursor, "senderOrRecipient");
          final int _cursorIndexOfMessageText = CursorUtil.getColumnIndexOrThrow(_cursor, "messageText");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final List<Message> _result = new ArrayList<Message>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Message _item;
            final long _tmpMessageId;
            _tmpMessageId = _cursor.getLong(_cursorIndexOfMessageId);
            final long _tmpThreadId;
            _tmpThreadId = _cursor.getLong(_cursorIndexOfThreadId);
            final String _tmpSenderOrRecipient;
            if (_cursor.isNull(_cursorIndexOfSenderOrRecipient)) {
              _tmpSenderOrRecipient = null;
            } else {
              _tmpSenderOrRecipient = _cursor.getString(_cursorIndexOfSenderOrRecipient);
            }
            final String _tmpMessageText;
            if (_cursor.isNull(_cursorIndexOfMessageText)) {
              _tmpMessageText = null;
            } else {
              _tmpMessageText = _cursor.getString(_cursorIndexOfMessageText);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpDirection;
            if (_cursor.isNull(_cursorIndexOfDirection)) {
              _tmpDirection = null;
            } else {
              _tmpDirection = _cursor.getString(_cursorIndexOfDirection);
            }
            _item = new Message(_tmpMessageId,_tmpThreadId,_tmpSenderOrRecipient,_tmpMessageText,_tmpTimestamp,_tmpDirection);
            _result.add(_item);
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
  public Object getAllMessages(final Continuation<? super List<Message>> $completion) {
    final String _sql = "SELECT * FROM messages ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Message>>() {
      @Override
      @NonNull
      public List<Message> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMessageId = CursorUtil.getColumnIndexOrThrow(_cursor, "messageId");
          final int _cursorIndexOfThreadId = CursorUtil.getColumnIndexOrThrow(_cursor, "threadId");
          final int _cursorIndexOfSenderOrRecipient = CursorUtil.getColumnIndexOrThrow(_cursor, "senderOrRecipient");
          final int _cursorIndexOfMessageText = CursorUtil.getColumnIndexOrThrow(_cursor, "messageText");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final List<Message> _result = new ArrayList<Message>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Message _item;
            final long _tmpMessageId;
            _tmpMessageId = _cursor.getLong(_cursorIndexOfMessageId);
            final long _tmpThreadId;
            _tmpThreadId = _cursor.getLong(_cursorIndexOfThreadId);
            final String _tmpSenderOrRecipient;
            if (_cursor.isNull(_cursorIndexOfSenderOrRecipient)) {
              _tmpSenderOrRecipient = null;
            } else {
              _tmpSenderOrRecipient = _cursor.getString(_cursorIndexOfSenderOrRecipient);
            }
            final String _tmpMessageText;
            if (_cursor.isNull(_cursorIndexOfMessageText)) {
              _tmpMessageText = null;
            } else {
              _tmpMessageText = _cursor.getString(_cursorIndexOfMessageText);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpDirection;
            if (_cursor.isNull(_cursorIndexOfDirection)) {
              _tmpDirection = null;
            } else {
              _tmpDirection = _cursor.getString(_cursorIndexOfDirection);
            }
            _item = new Message(_tmpMessageId,_tmpThreadId,_tmpSenderOrRecipient,_tmpMessageText,_tmpTimestamp,_tmpDirection);
            _result.add(_item);
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
  public Object getLastMessageForThread(final long threadId,
      final Continuation<? super Message> $completion) {
    final String _sql = "SELECT * FROM messages WHERE threadId = ? ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, threadId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Message>() {
      @Override
      @Nullable
      public Message call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMessageId = CursorUtil.getColumnIndexOrThrow(_cursor, "messageId");
          final int _cursorIndexOfThreadId = CursorUtil.getColumnIndexOrThrow(_cursor, "threadId");
          final int _cursorIndexOfSenderOrRecipient = CursorUtil.getColumnIndexOrThrow(_cursor, "senderOrRecipient");
          final int _cursorIndexOfMessageText = CursorUtil.getColumnIndexOrThrow(_cursor, "messageText");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
          final Message _result;
          if (_cursor.moveToFirst()) {
            final long _tmpMessageId;
            _tmpMessageId = _cursor.getLong(_cursorIndexOfMessageId);
            final long _tmpThreadId;
            _tmpThreadId = _cursor.getLong(_cursorIndexOfThreadId);
            final String _tmpSenderOrRecipient;
            if (_cursor.isNull(_cursorIndexOfSenderOrRecipient)) {
              _tmpSenderOrRecipient = null;
            } else {
              _tmpSenderOrRecipient = _cursor.getString(_cursorIndexOfSenderOrRecipient);
            }
            final String _tmpMessageText;
            if (_cursor.isNull(_cursorIndexOfMessageText)) {
              _tmpMessageText = null;
            } else {
              _tmpMessageText = _cursor.getString(_cursorIndexOfMessageText);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpDirection;
            if (_cursor.isNull(_cursorIndexOfDirection)) {
              _tmpDirection = null;
            } else {
              _tmpDirection = _cursor.getString(_cursorIndexOfDirection);
            }
            _result = new Message(_tmpMessageId,_tmpThreadId,_tmpSenderOrRecipient,_tmpMessageText,_tmpTimestamp,_tmpDirection);
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
