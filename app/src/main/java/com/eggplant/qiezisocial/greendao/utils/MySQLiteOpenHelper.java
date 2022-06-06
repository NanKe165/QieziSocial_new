package com.eggplant.qiezisocial.greendao.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.eggplant.qiezisocial.greendao.gen.ChatEntryDao;
import com.eggplant.qiezisocial.greendao.gen.DaoMaster;
import com.eggplant.qiezisocial.greendao.gen.MainInfoBeanDao;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Administrator on 2018/12/17.
 */

public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {


    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, ChatEntryDao.class, MainInfoBeanDao.class);
    }
}
