package com.eggplant.qiezisocial.greendao.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.eggplant.qiezisocial.greendao.entry.MainInfoBean;
import com.eggplant.qiezisocial.greendao.gen.DaoMaster;
import com.eggplant.qiezisocial.greendao.gen.DaoSession;
import com.eggplant.qiezisocial.greendao.gen.MainInfoBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Administrator on 2018/11/22.
 */

public class MainDBManager {
    private final static String dbName = "social_db";
    private static MainDBManager mInstance;
    private MySQLiteOpenHelper openHelper;
    private Context context;

    public MainDBManager(Context context) {
        this.context = context;
        openHelper = new MySQLiteOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static MainDBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (MainDBManager.class) {
                if (mInstance == null) {
                    mInstance = new MainDBManager(context);
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
//    public boolean hasUser(MainInfoBean user){
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        MainInfoBeanDao userDao = daoSession.getMainInfoBeanDao();
//        MainInfoBean unique = userDao.queryBuilder().where(MainInfoBeanDao.Properties.Uid.notEq(user.getUid())).unique();
//        if (unique != null) {
//           return true;
//        }
//        return false;
//    }


    /**
     * 插入一条记录
     *
     * @param user
     */
    public boolean insertUser(MainInfoBean user) {
        if (user.getQsid() != 0) {
            return inserQsUser(user);
        } else if (user.getGsid() != 0) {
            return inserGsUser(user);
        }

        SharedPreferences userInfo = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE);
        int uid = userInfo.getInt("uid", 0);
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MainInfoBeanDao userDao = daoSession.getMainInfoBeanDao();
        userDao.detachAll();//清除缓存
        MainInfoBean unique = userDao.queryBuilder()
                .where(MainInfoBeanDao.Properties.UserId.eq(uid))
                .where(MainInfoBeanDao.Properties.Uid.eq(user.getUid()))
                .where(MainInfoBeanDao.Properties.Gsid.eq(0))
                .where(MainInfoBeanDao.Properties.Qsid.eq(0))
                .unique();
        if (unique == null) {
            boolean b = userDao.insert(user) == -1 ? true : false;
        } else {
            unique.updateUser(user);
            updateUser(unique);
            return false;
        }
        return true;
    }

    private boolean inserGsUser(MainInfoBean user) {
        SharedPreferences userInfo = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE);
        int uid = userInfo.getInt("uid", 0);
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MainInfoBeanDao userDao = daoSession.getMainInfoBeanDao();
        userDao.detachAll();//清除缓存
        MainInfoBean unique = userDao.queryBuilder()
                .where(MainInfoBeanDao.Properties.UserId.eq(uid))
                .where(MainInfoBeanDao.Properties.Gsid.eq(user.getGsid()))
                .unique();
        if (unique == null) {
            boolean b = userDao.insert(user) == -1 ? true : false;
        } else {
            unique.updateUser(user);
            updateUser(unique);
            return false;
        }
        return true;
    }

    private boolean inserQsUser(MainInfoBean user) {
        SharedPreferences userInfo = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE);
        int uid = userInfo.getInt("uid", 0);
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MainInfoBeanDao userDao = daoSession.getMainInfoBeanDao();
        userDao.detachAll();//清除缓存
        MainInfoBean unique = userDao.queryBuilder()
                .where(MainInfoBeanDao.Properties.UserId.eq(uid))
                .where(MainInfoBeanDao.Properties.Uid.eq(user.getUid()))
                .where(MainInfoBeanDao.Properties.Qsid.eq(user.getQsid()))
                .unique();
        if (unique == null) {
            boolean b = userDao.insert(user) == -1 ? true : false;
        } else {
            unique.updateUser(user);
            updateUser(unique);
            return false;
        }
        return true;
    }

    /**
     * 插入用户集合
     *
     * @param users
     */
    public void insertUserList(List<MainInfoBean> users) {
        if (users == null || users.isEmpty()) {
            return;
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MainInfoBeanDao userDao = daoSession.getMainInfoBeanDao();
        userDao.insertInTx(users);
    }

    /**
     * 删除一条记录
     *
     * @param user
     */
    public void deleteUser(MainInfoBean user) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MainInfoBeanDao userDao = daoSession.getMainInfoBeanDao();
        if (user != null)
            userDao.delete(user);
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
    public void updateUser(MainInfoBean user) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MainInfoBeanDao userDao = daoSession.getMainInfoBeanDao();
//        QueryBuilder<MainInfoBean> qb = userDao.queryBuilder();
        userDao.update(user);
    }

    /**
     * 查询用户列表
     */
    public List<MainInfoBean> queryMainUserList() {
        SharedPreferences userInfo = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE);
        int uid = userInfo.getInt("uid", 0);
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MainInfoBeanDao userDao = daoSession.getMainInfoBeanDao();
        QueryBuilder<MainInfoBean> qb = userDao.queryBuilder()
                .where(MainInfoBeanDao.Properties.UserId.eq(uid))
                .orderDesc(MainInfoBeanDao.Properties.NewMsgTime)
                .orderDesc(MainInfoBeanDao.Properties.Online)
                .orderDesc(MainInfoBeanDao.Properties.Created);
        List<MainInfoBean> list = qb.list();
        return list;
    }

    /**
     * 查询用户列表
     */
    public List<MainInfoBean> queryMainUserListWithMsg() {
        SharedPreferences userInfo = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE);
        int uid = userInfo.getInt("uid", 0);
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MainInfoBeanDao userDao = daoSession.getMainInfoBeanDao();
        QueryBuilder<MainInfoBean> qb = userDao.queryBuilder()
                .where(MainInfoBeanDao.Properties.UserId.eq(uid))
                .orderDesc(MainInfoBeanDao.Properties.MsgNum)
                .orderDesc(MainInfoBeanDao.Properties.Online)
                .orderDesc(MainInfoBeanDao.Properties.NewMsgTime)
                .orderDesc(MainInfoBeanDao.Properties.Created);
        List<MainInfoBean> list = qb.list();
        return list;
    }
    public void upDataAllUser(List<MainInfoBean> list) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MainInfoBeanDao userDao = daoSession.getMainInfoBeanDao();
        userDao.updateInTx(list);
    }

    public List<MainInfoBean> queryThrowBallList() {
        SharedPreferences userInfo = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE);
        int uid = userInfo.getInt("uid", 0);
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MainInfoBeanDao userDao = daoSession.getMainInfoBeanDao();
        QueryBuilder<MainInfoBean> qb = userDao.queryBuilder()
                .where(MainInfoBeanDao.Properties.UserId.eq(uid))
                .where(MainInfoBeanDao.Properties.Type.eq("gballfriendlist"))
                .orderDesc(MainInfoBeanDao.Properties.NewMsgTime)
                .orderDesc(MainInfoBeanDao.Properties.Created);
        List<MainInfoBean> list = qb.list();
        return list;
    }

    /**
     * 查询用户
     */
    public MainInfoBean queryMainUser(String uid) {
        SharedPreferences userInfo = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE);
        int userId = userInfo.getInt("uid", 0);
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MainInfoBeanDao userDao = daoSession.getMainInfoBeanDao();
        QueryBuilder<MainInfoBean> qb = userDao.queryBuilder();
        qb.where(MainInfoBeanDao.Properties.Uid.eq(uid)).where(MainInfoBeanDao.Properties.UserId.eq(userId)).where(MainInfoBeanDao.Properties.Qsid.eq(0)).where(MainInfoBeanDao.Properties.Gsid.eq(0));
        MainInfoBean list = qb.unique();
        return list;
    }

    @Nullable
    public MainInfoBean queryMainUserWithQsid(@Nullable String uid, long qsid) {
        SharedPreferences userInfo = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE);
        int userId = userInfo.getInt("uid", 0);
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MainInfoBeanDao userDao = daoSession.getMainInfoBeanDao();
        QueryBuilder<MainInfoBean> qb = userDao.queryBuilder();
        qb.where(MainInfoBeanDao.Properties.Uid.eq(uid)).where(MainInfoBeanDao.Properties.UserId.eq(userId)).where(MainInfoBeanDao.Properties.Qsid.eq(qsid));
        MainInfoBean list = qb.unique();
        return list;
    }


    @Nullable
    public MainInfoBean queryMainUserWithGsId(long gsid) {
        SharedPreferences userInfo = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE);
        int userId = userInfo.getInt("uid", 0);
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MainInfoBeanDao userDao = daoSession.getMainInfoBeanDao();
        QueryBuilder<MainInfoBean> qb = userDao.queryBuilder();
        qb.where(MainInfoBeanDao.Properties.Gsid.eq(gsid)).where(MainInfoBeanDao.Properties.UserId.eq(userId));
        MainInfoBean list = qb.unique();
        return list;
    }

    /**
     * 查询用户
     */
    public List<MainInfoBean> queryMainUserbyNick(String nick) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        MainInfoBeanDao userDao = daoSession.getMainInfoBeanDao();
        QueryBuilder<MainInfoBean> qb = userDao.queryBuilder().where(MainInfoBeanDao.Properties.Nick.like(nick + "%"));
        List<MainInfoBean> list = qb.list();
        return list;
    }
}
