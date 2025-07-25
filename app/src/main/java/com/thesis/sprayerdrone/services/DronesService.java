package com.thesis.sprayerdrone.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.github.MakMoinee.library.interfaces.DefaultBaseListener;
import com.thesis.sprayerdrone.models.Drones;
import com.thesis.sprayerdrone.repository.MyLocalSQL;

import java.util.ArrayList;
import java.util.List;

public class DronesService {
    private static final String TABLE_DRONES = "drones";
    Context mContext;
    MyLocalSQL localSQL;

    public DronesService(Context mContext) {
        this.mContext = mContext;
        this.localSQL = new MyLocalSQL(mContext);
    }

    @SuppressLint("Range")
    public void getAllDronesByUserID(int uid, DefaultBaseListener listener) {
        if (uid != 0) {
            SQLiteDatabase db = localSQL.getReadableDatabase();
            String[] columns = {"id", "userID", "droneName", "ip", "status"};
            String selection = "userID=?";
            String[] selectionArgs = {Integer.toString(uid)};

            Cursor cursor = null;
            List<Drones> droneList = new ArrayList<>();

            try {
                cursor = db.query(TABLE_DRONES, columns, selection, selectionArgs, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex("id"));
                        int userID = cursor.getInt(cursor.getColumnIndex("userID"));
                        String droneName = cursor.getString(cursor.getColumnIndex("droneName"));
                        String ip = cursor.getString(cursor.getColumnIndex("ip"));
                        String status = cursor.getString(cursor.getColumnIndex("status"));

                        Drones drone = new Drones.DroneBuilder()
                                .setId(id)
                                .setUserID(userID)
                                .setDeviceName(droneName)
                                .setDeviceIP(ip)
                                .setStatus(status)
                                .build();

                        droneList.add(drone);
                    } while (cursor.moveToNext());
                }

                if (!droneList.isEmpty()) {
                    listener.onSuccess(droneList);
                } else {
                    listener.onError(new Error("No drones found"));
                }

            } catch (Exception e) {
                Log.e("getDroneList_error", e.getLocalizedMessage());
                listener.onError(new Error(e.getMessage()));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                db.close();
            }
        } else {
            listener.onError(new Error("Invalid drone filter"));
        }
    }
}
