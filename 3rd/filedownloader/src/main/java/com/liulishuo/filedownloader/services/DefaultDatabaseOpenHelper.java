/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liulishuo.filedownloader.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.liulishuo.filedownloader.model.FileDownloadModel;


/**
 * The default opener of the filedownloader database helper.
 */
class DefaultDatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "filedownloader.db";
    private static final int DATABASE_VERSION = 2;

    public DefaultDatabaseOpenHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                DefaultDatabaseImpl.TABLE_NAME + "( " +
                FileDownloadModel.ID + " INTEGER PRIMARY KEY, " + // id
                FileDownloadModel.URL + " VARCHAR, " + // url
                FileDownloadModel.PATH + " VARCHAR, " + // path
                FileDownloadModel.STATUS + " TINYINT(7), " + // status ,ps SQLite will auto change to integer.
                FileDownloadModel.SOFAR + " INTEGER, " +// so far
                FileDownloadModel.TOTAL + " INTEGER, " +// total
                FileDownloadModel.ERR_MSG + " VARCHAR, " + // error message
                FileDownloadModel.ETAG + " VARCHAR, " +// e tag
                FileDownloadModel.PATH_AS_DIRECTORY + " TINYINT(1) DEFAULT 0, " +// path as directory
                FileDownloadModel.FILENAME + " VARCHAR" +// path as directory
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            String addAsDirectoryColumn = "ALTER TABLE " + DefaultDatabaseImpl.TABLE_NAME +
                    " ADD COLUMN " + FileDownloadModel.PATH_AS_DIRECTORY +
                    " TINYINT(1) DEFAULT 0";
            db.execSQL(addAsDirectoryColumn);

            String addFilenameColumn = "ALTER TABLE " + DefaultDatabaseImpl.TABLE_NAME +
                    " ADD COLUMN " + FileDownloadModel.FILENAME +
                    " VARCHAR";
            db.execSQL(addFilenameColumn);
        }
    }
}
