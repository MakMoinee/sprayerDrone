package com.thesis.sprayerdrone.services;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.github.MakMoinee.library.common.MapForm;
import com.github.MakMoinee.library.interfaces.DefaultBaseListener;
import com.github.MakMoinee.library.services.HashPass;
import com.thesis.sprayerdrone.models.Users;
import com.thesis.sprayerdrone.repository.MyLocalSQL;

public class UserService {
    private static final String TABLE_USER = "users";
    MyLocalSQL localSQL;
    Context mContext;
    HashPass hashPass = new HashPass();

    public UserService(Context mContext) {
        this.mContext = mContext;
        this.localSQL = new MyLocalSQL(mContext);
    }

    /**
     * @param users
     * @param listener
     */
    @SuppressLint("Range")
    public void login(Users users, DefaultBaseListener listener) {
        Users newUsers = null;
        SQLiteDatabase db = localSQL.getWritableDatabase();
        String[] columns = {"id", "firstName", "middleName", "lastName", "username", "password"};
        String selection = "username=?";
        String[] selectionArgs = {users.getUsername()};
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String uname = cursor.getString(cursor.getColumnIndex("username"));
                    String pass = cursor.getString(cursor.getColumnIndex("password"));
                    String firstName = cursor.getString(cursor.getColumnIndex("firstName"));
                    String middleName = cursor.getString(cursor.getColumnIndex("middleName"));
                    String lastName = cursor.getString(cursor.getColumnIndex("lastName"));
                    Log.e("retrieve_n", lastName);
                    if (hashPass.verifyPassword(users.getPassword(), pass)) {
                        newUsers = new Users.UserBuilder()
                                .setId(id)
                                .setFirstName(firstName)
                                .setMiddleName(middleName)
                                .setLastName(lastName)
                                .setUsername(uname)
                                .build();
                        break;
                    }


                } while (cursor.moveToNext());
            } else {

                listener.onError(new Error("Invalid username or password."));
            }

        } catch (Exception e) {
            listener.onError(new Error("Error checking user: " + e.getMessage()));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();

            if (newUsers != null) {
                listener.onSuccess(newUsers);
            } else {
                listener.onError(new Error("empty"));
            }
        }
    }

    public void insertUser(Users users, DefaultBaseListener listener) {
        SQLiteDatabase db = localSQL.getWritableDatabase();
        ContentValues values = MapForm.toContentValues(users);
        values.remove("id");
        try {
            long count = db.insert(TABLE_USER, null, values);
            if (count != -1) {
                listener.onSuccess("success add");
            } else {
                listener.onError(new Error("failed to add error"));
            }
        } catch (Exception e) {
            Log.e("error_insert", e.getLocalizedMessage());
            listener.onError(new Error(e.getMessage()));
        } finally {
            db.close();
        }
    }

    public void insertUniqueUser(Users users, DefaultBaseListener listener) {
        this.checkUser(users, new DefaultBaseListener() {
            @Override
            public <T> void onSuccess(T t) {
                listener.onError(new Error("user exist"));
            }

            @Override
            public void onError(Error error) {
                insertUser(users, new DefaultBaseListener() {
                    @Override
                    public <T> void onSuccess(T t) {
                        listener.onSuccess("success add user");
                    }

                    @Override
                    public void onError(Error error) {
                        listener.onError(new Error("failed to add user"));
                    }
                });
            }
        });
    }

    @SuppressLint("Range")
    public void checkUser(Users users, DefaultBaseListener listener) {
        SQLiteDatabase db = localSQL.getWritableDatabase();
        String[] columns = {"username", "password"};
        String selection = "username=?";
        String[] selectionArgs = {users.getUsername()};
        String result = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex("username"));
                if (result != null) {
                    listener.onSuccess("user exist");
                }
            } else {

                listener.onError(new Error("Invalid username or password."));
            }

        } catch (Exception e) {
            listener.onError(new Error("Error checking user: " + e.getMessage()));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

    }
}
