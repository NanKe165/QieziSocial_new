package com.eggplant.qiezisocial.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.eggplant.qiezisocial.greendao.entry.ChatEntry;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CHAT_ENTRY".
*/
public class ChatEntryDao extends AbstractDao<ChatEntry, Long> {

    public static final String TABLENAME = "CHAT_ENTRY";

    /**
     * Properties of entity ChatEntry.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property UserId = new Property(0, long.class, "userId", false, "USER_ID");
        public final static Property MsgId = new Property(1, String.class, "msgId", false, "MSG_ID");
        public final static Property ChatId = new Property(2, long.class, "chatId", false, "CHAT_ID");
        public final static Property Type = new Property(3, String.class, "type", false, "TYPE");
        public final static Property Content = new Property(4, String.class, "content", false, "CONTENT");
        public final static Property Extra = new Property(5, String.class, "extra", false, "EXTRA");
        public final static Property From = new Property(6, long.class, "from", false, "FROM");
        public final static Property To = new Property(7, long.class, "to", false, "TO");
        public final static Property Created = new Property(8, String.class, "created", false, "CREATED");
        public final static Property IsShowCreated = new Property(9, boolean.class, "isShowCreated", false, "IS_SHOW_CREATED");
        public final static Property Face = new Property(10, String.class, "face", false, "FACE");
        public final static Property Nick = new Property(11, String.class, "nick", false, "NICK");
        public final static Property Sex = new Property(12, String.class, "sex", false, "SEX");
        public final static Property Birth = new Property(13, String.class, "birth", false, "BIRTH");
        public final static Property Edu = new Property(14, String.class, "edu", false, "EDU");
        public final static Property Height = new Property(15, String.class, "height", false, "HEIGHT");
        public final static Property Weight = new Property(16, String.class, "weight", false, "WEIGHT");
        public final static Property Careers = new Property(17, String.class, "careers", false, "CAREERS");
        public final static Property Label = new Property(18, String.class, "label", false, "LABEL");
        public final static Property LayoutType = new Property(19, String.class, "layoutType", false, "LAYOUT_TYPE");
        public final static Property Layout = new Property(20, String.class, "layout", false, "LAYOUT");
        public final static Property MsgStat = new Property(21, int.class, "msgStat", false, "MSG_STAT");
        public final static Property Qsid = new Property(22, long.class, "qsid", false, "QSID");
        public final static Property Gsid = new Property(23, long.class, "gsid", false, "GSID");
        public final static Property Question1 = new Property(24, String.class, "question1", false, "QUESTION1");
        public final static Property Question2 = new Property(25, String.class, "question2", false, "QUESTION2");
        public final static Property Question3 = new Property(26, String.class, "question3", false, "QUESTION3");
        public final static Property Answer1 = new Property(27, String.class, "answer1", false, "ANSWER1");
        public final static Property Answer2 = new Property(28, String.class, "answer2", false, "ANSWER2");
        public final static Property Answer3 = new Property(29, String.class, "answer3", false, "ANSWER3");
        public final static Property MsgRead = new Property(30, boolean.class, "msgRead", false, "MSG_READ");
        public final static Property Id = new Property(31, Long.class, "id", true, "_id");
    }


    public ChatEntryDao(DaoConfig config) {
        super(config);
    }
    
    public ChatEntryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CHAT_ENTRY\" (" + //
                "\"USER_ID\" INTEGER NOT NULL ," + // 0: userId
                "\"MSG_ID\" TEXT," + // 1: msgId
                "\"CHAT_ID\" INTEGER NOT NULL ," + // 2: chatId
                "\"TYPE\" TEXT," + // 3: type
                "\"CONTENT\" TEXT," + // 4: content
                "\"EXTRA\" TEXT," + // 5: extra
                "\"FROM\" INTEGER NOT NULL ," + // 6: from
                "\"TO\" INTEGER NOT NULL ," + // 7: to
                "\"CREATED\" TEXT," + // 8: created
                "\"IS_SHOW_CREATED\" INTEGER NOT NULL ," + // 9: isShowCreated
                "\"FACE\" TEXT," + // 10: face
                "\"NICK\" TEXT," + // 11: nick
                "\"SEX\" TEXT," + // 12: sex
                "\"BIRTH\" TEXT," + // 13: birth
                "\"EDU\" TEXT," + // 14: edu
                "\"HEIGHT\" TEXT," + // 15: height
                "\"WEIGHT\" TEXT," + // 16: weight
                "\"CAREERS\" TEXT," + // 17: careers
                "\"LABEL\" TEXT," + // 18: label
                "\"LAYOUT_TYPE\" TEXT," + // 19: layoutType
                "\"LAYOUT\" TEXT," + // 20: layout
                "\"MSG_STAT\" INTEGER NOT NULL ," + // 21: msgStat
                "\"QSID\" INTEGER NOT NULL ," + // 22: qsid
                "\"GSID\" INTEGER NOT NULL ," + // 23: gsid
                "\"QUESTION1\" TEXT," + // 24: question1
                "\"QUESTION2\" TEXT," + // 25: question2
                "\"QUESTION3\" TEXT," + // 26: question3
                "\"ANSWER1\" TEXT," + // 27: answer1
                "\"ANSWER2\" TEXT," + // 28: answer2
                "\"ANSWER3\" TEXT," + // 29: answer3
                "\"MSG_READ\" INTEGER NOT NULL ," + // 30: msgRead
                "\"_id\" INTEGER PRIMARY KEY );"); // 31: id
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CHAT_ENTRY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ChatEntry entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getUserId());
 
        String msgId = entity.getMsgId();
        if (msgId != null) {
            stmt.bindString(2, msgId);
        }
        stmt.bindLong(3, entity.getChatId());
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(4, type);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(5, content);
        }
 
        String extra = entity.getExtra();
        if (extra != null) {
            stmt.bindString(6, extra);
        }
        stmt.bindLong(7, entity.getFrom());
        stmt.bindLong(8, entity.getTo());
 
        String created = entity.getCreated();
        if (created != null) {
            stmt.bindString(9, created);
        }
        stmt.bindLong(10, entity.getIsShowCreated() ? 1L: 0L);
 
        String face = entity.getFace();
        if (face != null) {
            stmt.bindString(11, face);
        }
 
        String nick = entity.getNick();
        if (nick != null) {
            stmt.bindString(12, nick);
        }
 
        String sex = entity.getSex();
        if (sex != null) {
            stmt.bindString(13, sex);
        }
 
        String birth = entity.getBirth();
        if (birth != null) {
            stmt.bindString(14, birth);
        }
 
        String edu = entity.getEdu();
        if (edu != null) {
            stmt.bindString(15, edu);
        }
 
        String height = entity.getHeight();
        if (height != null) {
            stmt.bindString(16, height);
        }
 
        String weight = entity.getWeight();
        if (weight != null) {
            stmt.bindString(17, weight);
        }
 
        String careers = entity.getCareers();
        if (careers != null) {
            stmt.bindString(18, careers);
        }
 
        String label = entity.getLabel();
        if (label != null) {
            stmt.bindString(19, label);
        }
 
        String layoutType = entity.getLayoutType();
        if (layoutType != null) {
            stmt.bindString(20, layoutType);
        }
 
        String layout = entity.getLayout();
        if (layout != null) {
            stmt.bindString(21, layout);
        }
        stmt.bindLong(22, entity.getMsgStat());
        stmt.bindLong(23, entity.getQsid());
        stmt.bindLong(24, entity.getGsid());
 
        String question1 = entity.getQuestion1();
        if (question1 != null) {
            stmt.bindString(25, question1);
        }
 
        String question2 = entity.getQuestion2();
        if (question2 != null) {
            stmt.bindString(26, question2);
        }
 
        String question3 = entity.getQuestion3();
        if (question3 != null) {
            stmt.bindString(27, question3);
        }
 
        String answer1 = entity.getAnswer1();
        if (answer1 != null) {
            stmt.bindString(28, answer1);
        }
 
        String answer2 = entity.getAnswer2();
        if (answer2 != null) {
            stmt.bindString(29, answer2);
        }
 
        String answer3 = entity.getAnswer3();
        if (answer3 != null) {
            stmt.bindString(30, answer3);
        }
        stmt.bindLong(31, entity.getMsgRead() ? 1L: 0L);
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(32, id);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ChatEntry entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getUserId());
 
        String msgId = entity.getMsgId();
        if (msgId != null) {
            stmt.bindString(2, msgId);
        }
        stmt.bindLong(3, entity.getChatId());
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(4, type);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(5, content);
        }
 
        String extra = entity.getExtra();
        if (extra != null) {
            stmt.bindString(6, extra);
        }
        stmt.bindLong(7, entity.getFrom());
        stmt.bindLong(8, entity.getTo());
 
        String created = entity.getCreated();
        if (created != null) {
            stmt.bindString(9, created);
        }
        stmt.bindLong(10, entity.getIsShowCreated() ? 1L: 0L);
 
        String face = entity.getFace();
        if (face != null) {
            stmt.bindString(11, face);
        }
 
        String nick = entity.getNick();
        if (nick != null) {
            stmt.bindString(12, nick);
        }
 
        String sex = entity.getSex();
        if (sex != null) {
            stmt.bindString(13, sex);
        }
 
        String birth = entity.getBirth();
        if (birth != null) {
            stmt.bindString(14, birth);
        }
 
        String edu = entity.getEdu();
        if (edu != null) {
            stmt.bindString(15, edu);
        }
 
        String height = entity.getHeight();
        if (height != null) {
            stmt.bindString(16, height);
        }
 
        String weight = entity.getWeight();
        if (weight != null) {
            stmt.bindString(17, weight);
        }
 
        String careers = entity.getCareers();
        if (careers != null) {
            stmt.bindString(18, careers);
        }
 
        String label = entity.getLabel();
        if (label != null) {
            stmt.bindString(19, label);
        }
 
        String layoutType = entity.getLayoutType();
        if (layoutType != null) {
            stmt.bindString(20, layoutType);
        }
 
        String layout = entity.getLayout();
        if (layout != null) {
            stmt.bindString(21, layout);
        }
        stmt.bindLong(22, entity.getMsgStat());
        stmt.bindLong(23, entity.getQsid());
        stmt.bindLong(24, entity.getGsid());
 
        String question1 = entity.getQuestion1();
        if (question1 != null) {
            stmt.bindString(25, question1);
        }
 
        String question2 = entity.getQuestion2();
        if (question2 != null) {
            stmt.bindString(26, question2);
        }
 
        String question3 = entity.getQuestion3();
        if (question3 != null) {
            stmt.bindString(27, question3);
        }
 
        String answer1 = entity.getAnswer1();
        if (answer1 != null) {
            stmt.bindString(28, answer1);
        }
 
        String answer2 = entity.getAnswer2();
        if (answer2 != null) {
            stmt.bindString(29, answer2);
        }
 
        String answer3 = entity.getAnswer3();
        if (answer3 != null) {
            stmt.bindString(30, answer3);
        }
        stmt.bindLong(31, entity.getMsgRead() ? 1L: 0L);
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(32, id);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 31) ? null : cursor.getLong(offset + 31);
    }    

    @Override
    public ChatEntry readEntity(Cursor cursor, int offset) {
        ChatEntry entity = new ChatEntry( //
            cursor.getLong(offset + 0), // userId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // msgId
            cursor.getLong(offset + 2), // chatId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // type
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // content
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // extra
            cursor.getLong(offset + 6), // from
            cursor.getLong(offset + 7), // to
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // created
            cursor.getShort(offset + 9) != 0, // isShowCreated
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // face
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // nick
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // sex
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // birth
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // edu
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // height
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // weight
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // careers
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // label
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // layoutType
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // layout
            cursor.getInt(offset + 21), // msgStat
            cursor.getLong(offset + 22), // qsid
            cursor.getLong(offset + 23), // gsid
            cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24), // question1
            cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25), // question2
            cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26), // question3
            cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27), // answer1
            cursor.isNull(offset + 28) ? null : cursor.getString(offset + 28), // answer2
            cursor.isNull(offset + 29) ? null : cursor.getString(offset + 29), // answer3
            cursor.getShort(offset + 30) != 0, // msgRead
            cursor.isNull(offset + 31) ? null : cursor.getLong(offset + 31) // id
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ChatEntry entity, int offset) {
        entity.setUserId(cursor.getLong(offset + 0));
        entity.setMsgId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setChatId(cursor.getLong(offset + 2));
        entity.setType(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setContent(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setExtra(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setFrom(cursor.getLong(offset + 6));
        entity.setTo(cursor.getLong(offset + 7));
        entity.setCreated(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setIsShowCreated(cursor.getShort(offset + 9) != 0);
        entity.setFace(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setNick(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setSex(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setBirth(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setEdu(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setHeight(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setWeight(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setCareers(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setLabel(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setLayoutType(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setLayout(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setMsgStat(cursor.getInt(offset + 21));
        entity.setQsid(cursor.getLong(offset + 22));
        entity.setGsid(cursor.getLong(offset + 23));
        entity.setQuestion1(cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24));
        entity.setQuestion2(cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25));
        entity.setQuestion3(cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26));
        entity.setAnswer1(cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27));
        entity.setAnswer2(cursor.isNull(offset + 28) ? null : cursor.getString(offset + 28));
        entity.setAnswer3(cursor.isNull(offset + 29) ? null : cursor.getString(offset + 29));
        entity.setMsgRead(cursor.getShort(offset + 30) != 0);
        entity.setId(cursor.isNull(offset + 31) ? null : cursor.getLong(offset + 31));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ChatEntry entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ChatEntry entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ChatEntry entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}