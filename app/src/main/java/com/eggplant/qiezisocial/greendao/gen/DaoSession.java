package com.eggplant.qiezisocial.greendao.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.eggplant.qiezisocial.greendao.entry.ChatEntry;
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean;

import com.eggplant.qiezisocial.greendao.gen.ChatEntryDao;
import com.eggplant.qiezisocial.greendao.gen.MainInfoBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig chatEntryDaoConfig;
    private final DaoConfig mainInfoBeanDaoConfig;

    private final ChatEntryDao chatEntryDao;
    private final MainInfoBeanDao mainInfoBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        chatEntryDaoConfig = daoConfigMap.get(ChatEntryDao.class).clone();
        chatEntryDaoConfig.initIdentityScope(type);

        mainInfoBeanDaoConfig = daoConfigMap.get(MainInfoBeanDao.class).clone();
        mainInfoBeanDaoConfig.initIdentityScope(type);

        chatEntryDao = new ChatEntryDao(chatEntryDaoConfig, this);
        mainInfoBeanDao = new MainInfoBeanDao(mainInfoBeanDaoConfig, this);

        registerDao(ChatEntry.class, chatEntryDao);
        registerDao(MainInfoBean.class, mainInfoBeanDao);
    }
    
    public void clear() {
        chatEntryDaoConfig.clearIdentityScope();
        mainInfoBeanDaoConfig.clearIdentityScope();
    }

    public ChatEntryDao getChatEntryDao() {
        return chatEntryDao;
    }

    public MainInfoBeanDao getMainInfoBeanDao() {
        return mainInfoBeanDao;
    }

}
