package com.eggplant.qiezisocial.greendao.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.eggplant.qiezisocial.greendao.entry.ChatEntry;
import com.eggplant.qiezisocial.greendao.gen.ChatEntryDao;
import com.eggplant.qiezisocial.greendao.gen.DaoMaster;
import com.eggplant.qiezisocial.greendao.gen.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by Administrator on 2018/11/23.
 */

public class ChatDBManager {
    private final static String dbName = "social_db_chat";
    private static ChatDBManager mInstance;
    private MySQLiteOpenHelper openHelper;
    private Context context;

    public ChatDBManager(Context context) {
        this.context = context;
        openHelper = new MySQLiteOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static ChatDBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ChatDBManager.class) {
                if (mInstance == null) {
                    mInstance = new ChatDBManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new MySQLiteOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new MySQLiteOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }


    /**
     * 插入一条记录
     *
     * @param user
     */
    public boolean insertUser(ChatEntry user) {
//        Log.e("test_chat", "insertUser: " + user.getContent());

        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatEntryDao chatEntryDao = daoSession.getChatEntryDao();

        ChatEntry unique = chatEntryDao.queryBuilder().where(ChatEntryDao.Properties.MsgId.eq(user.getMsgId())).unique();
        if (unique == null) {
            Log.i("chatdbmanager"," chaentry is  empty");
            boolean flag = chatEntryDao.insert(user) == -1 ? false : true;
        } else {
            Log.i("chatdbmanager"," chaentry is  not empty");
            return true;
        }
        return true;
    }


    /**
     * 插入用户集合
     *
     * @param users
     */
    public void insertUserList(List<ChatEntry> users) {
        if (users == null || users.isEmpty()) {
            return;
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatEntryDao chatEntryDao = daoSession.getChatEntryDao();
        chatEntryDao.insertInTx(users);
    }

    /**
     * 删除一条记录
     *
     * @param user
     */
    public void deleteUser(ChatEntry user) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatEntryDao userDao = daoSession.getChatEntryDao();
        if (user != null)
            userDao.delete(user);
    }

    /**
     * 删除多条记录
     *
     * @param list
     */
    public void deleteUser(List<ChatEntry> list) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatEntryDao userDao = daoSession.getChatEntryDao();
        for (ChatEntry entry : list) {
            userDao.delete(entry);
        }
    }

    /**
     * 删除所有记录
     */

    public void deleteAll(Class entryClass) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        daoSession.deleteAll(entryClass);
    }

    /**
     * 更新一条记录
     *
     * @param user
     */
    public void updateUser(ChatEntry user) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatEntryDao userDao = daoSession.getChatEntryDao();

        userDao.update(user);
    }

    /**
     * 查询用户列表
     */
//    public List<ChatEntry> queryUserList(Context context,long chatId) {
//        SharedPreferences so = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
//        int uid = so.getInt("uid", 0);
//        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        ChatEntryDao userDao = daoSession.getChatEntryDao();
//        QueryBuilder<ChatEntry> qb = userDao.queryBuilder()
//                .where(ChatEntryDao.Properties.ChatId.eq(chatId))
//                .where(ChatEntryDao.Properties.UserId.eq(uid))
//                .orderAsc(ChatEntryDao.Properties.Id);
////        QueryBuilder<ChatEntry> qb = userDao.queryBuilder();
//        List<ChatEntry> list = qb.list();
//        return list;
//    }


    /**
     * 查询用户列表
     */
    public List<ChatEntry> queryUserList(long chatId, int startPositon) {
        SharedPreferences so = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE);
        int uid = so.getInt("uid", 0);
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatEntryDao userDao = daoSession.getChatEntryDao();
        QueryBuilder<ChatEntry> qb = userDao.queryBuilder()
                .where(ChatEntryDao.Properties.ChatId.eq(chatId))
                .where(ChatEntryDao.Properties.UserId.eq(uid))
                .where(ChatEntryDao.Properties.Qsid.eq(0))
                .where(ChatEntryDao.Properties.Gsid.eq(0))
                .orderDesc(ChatEntryDao.Properties.Id)
                .limit(45)
                .offset(startPositon);
//        QueryBuilder<ChatEntry> qb = userDao.queryBuilder();
        List<ChatEntry> list = qb.list();
        return list;
    }
    /**
     * 查询用户列表
     */
    public List<ChatEntry> queryAllUserList(long chatId) {
        SharedPreferences so = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE);
        int uid = so.getInt("uid", 0);
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatEntryDao userDao = daoSession.getChatEntryDao();
        QueryBuilder<ChatEntry> qb = userDao.queryBuilder()
                .where(ChatEntryDao.Properties.ChatId.eq(chatId))
                .where(ChatEntryDao.Properties.UserId.eq(uid))
                .where(ChatEntryDao.Properties.Qsid.eq(0))
                .where(ChatEntryDao.Properties.Gsid.eq(0))
                .orderDesc(ChatEntryDao.Properties.Id);
//        QueryBuilder<ChatEntry> qb = userDao.queryBuilder();
        List<ChatEntry> list = qb.list();
        return list;
    }

    /**
     * 查询问题私聊用户列表
     */
    public List<ChatEntry> queryQsUserList(long chatId, long qsid, int startPositon) {
        SharedPreferences so = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE);
        int uid = so.getInt("uid", 0);
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatEntryDao userDao = daoSession.getChatEntryDao();
        QueryBuilder<ChatEntry> qb = userDao.queryBuilder()
                .where(ChatEntryDao.Properties.ChatId.eq(chatId))
                .where(ChatEntryDao.Properties.UserId.eq(uid))
                .where(ChatEntryDao.Properties.Qsid.eq(qsid))
                .orderDesc(ChatEntryDao.Properties.Id)
                .limit(45)
                .offset(startPositon);
//        QueryBuilder<ChatEntry> qb = userDao.queryBuilder();
        List<ChatEntry> list = qb.list();
        return list;
    }

    /**
     * 查询问题群聊用户列表
     */
    public List<ChatEntry> queryGsUserList(long gsid, int startPositon) {
        SharedPreferences so = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE);
        int uid = so.getInt("uid", 0);
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatEntryDao userDao = daoSession.getChatEntryDao();
        QueryBuilder<ChatEntry> qb = userDao.queryBuilder()
                .where(ChatEntryDao.Properties.UserId.eq(uid))
                .where(ChatEntryDao.Properties.Gsid.eq(gsid))
                .orderDesc(ChatEntryDao.Properties.Id)
                .limit(45)
                .offset(startPositon);
//        QueryBuilder<ChatEntry> qb = userDao.queryBuilder();
        List<ChatEntry> list = qb.list();
        return list;
    }


    /**
     * 查询用户列表
     */
    public List<ChatEntry> queryUserAllData(long chatId) {
        SharedPreferences so = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE);
        int uid = so.getInt("uid", 0);
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatEntryDao userDao = daoSession.getChatEntryDao();
        QueryBuilder<ChatEntry> qb = userDao.queryBuilder()
                .where(ChatEntryDao.Properties.ChatId.eq(chatId))
                .where(ChatEntryDao.Properties.UserId.eq(uid));
//        QueryBuilder<ChatEntry> qb = userDao.queryBuilder();
        List<ChatEntry> list = qb.list();
        return list;
    }

    public List<ChatEntry> queryNewUser(long chatId, int startPositon) {
        SharedPreferences so = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE);
        int uid = so.getInt("uid", 0);
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatEntryDao userDao = daoSession.getChatEntryDao();
        QueryBuilder<ChatEntry> qb = userDao.queryBuilder()
                .where(ChatEntryDao.Properties.ChatId.eq(chatId))
                .where(ChatEntryDao.Properties.UserId.eq(uid))
                .orderDesc(ChatEntryDao.Properties.Id)
                .limit(5)
                .offset(startPositon);
//        QueryBuilder<ChatEntry> qb = userDao.queryBuilder();
        List<ChatEntry> list = qb.list();
        return list;
    }

    /**
     * 查询用户
     */
    public ChatEntry queryUser(String msgid) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChatEntryDao userDao = daoSession.getChatEntryDao();
        QueryBuilder<ChatEntry> qb = userDao.queryBuilder();
        qb.where(ChatEntryDao.Properties.MsgId.eq(msgid));
        ChatEntry list = qb.unique();
        return list;
    }
}
