package com.eggplant.qiezisocial.entry;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2021/10/9.
 */

public class MovieEntry implements Serializable {


    /**
     * PlayAuth : eyJTZWN1cml0eVRva2VuIjoiQ0FJU2h3TjFxNkZ0NUIyeWZTaklyNWZQSE4zTzJPWjE3cWlkVVhIVzAyOGZYOXB0cEl2SmdEejJJSGhKZVhOdkJPMGV0ZjQrbVdCWTdQY1lsck1xR3NRY0hoMmZNSlVwdE1nS3FGdjdKcExGc3QySjZyOEpqc1U1OC9vU3BWbXBzdlhKYXNEVkVmbDJFNVhFTWlJUi8wMGU2TC8rY2lyWXBUWEhWYlNDbFo5Z2FQa09Rd0M4ZGtBb0xkeEtKd3hrMnQxNFVtWFdPYVNDUHdMU2htUEJMVXhtdldnR2wyUnp1NHV5M3ZPZDVoZlpwMXI4eE80YXhlTDBQb1AyVjgxbExacGxlc3FwM0k0U2M3YmFnaFpVNGdscjhxbHg3c3BCNVN5Vmt0eVdHVWhKL3phTElvaXQ3TnBqZmlCMGVvUUFQb3BGcC9YNmp2QWF3UExVbTliWXhncGhCOFIrWGo3RFpZYXV4N0d6ZW9XVE84MCthS3p3TmxuVXo5bUxMZU9WaVE0L1ptOEJQdzQ0RUxoSWFGMElVRUJ6RUc2Q2QvWDRvZ21hT2xmeUZaTG9pdjltamNCSHFIeno1c2VQS2xTMVJMR1U3RDBWSUpkVWJUbHpiME5MaHpPOEwvZGRLVjBSSXdNOVZ1eVBNYXgzYlFGRHI1M3ZzVGJiWHpaYjBtcHR1UG56ZHp0ZlB4elo3Q09WR29BQlZHa2F3bW1xOEV4WUFhK1VTeXNSY2lSYnBOdE95Q3lTdkIrVTlKKzZiZWNXS3U4UWJUeDhXRFkwRnBqTjF2VDNuNjlRUDhhbG4wTVNGUlJkenY0UEs0Sk9iTUt2Nnd6NWV4eDVoeDVGRURFYzhxNE9aamltWnlMdmx1WjBmS2ZsU2xqSWJYc1c1TXVBV1dlVzZHTzFaWm4vQWllaWk3RXZ3Sm5SSE9wMVBzND0iLCJBdXRoSW5mbyI6IntcIkNJXCI6XCJ0aVZCVnB1T2FvL3dmS2FIOEozOEdldnlQL2pNdldSSUNUaC8vWmJZVWl3TmlnbWF6elJ4RXFKY3JQdVpqOUxXUmZkY0lSbzRBeVVLa0JmSWdSV1lnNk9rZStJYlYrWjh2ZzdVeVFqQlI3Zz1cIixcIkNhbGxlclwiOlwiQ0JNc2x2c0F5WlBORmV1YTBWaWEzUGtsWlV1bUZwTmZpcGtQZHpLRlNKZz1cIixcIkV4cGlyZVRpbWVcIjpcIjIwMjEtMTAtMTFUMDE6NDg6MDVaXCIsXCJNZWRpYUlkXCI6XCIzMTE4Njg4YzU4ZjM0YWFlYmMxYjJlOWZhYzYyNWU3YlwiLFwiUGxheURvbWFpblwiOlwidm9kLnFpZXppeHVhbnNoYW5nLm5ldFwiLFwiU2lnbmF0dXJlXCI6XCJESXFIYWNxMk5rUVpIeWxnUm5yTm00dHBtUkk9XCJ9IiwiVmlkZW9NZXRhIjp7IlN0YXR1cyI6Ik5vcm1hbCIsIlZpZGVvSWQiOiIzMTE4Njg4YzU4ZjM0YWFlYmMxYjJlOWZhYzYyNWU3YiIsIlRpdGxlIjoi5pS+54mb54+t55qE5pil5aSpIiwiQ292ZXJVUkwiOiJodHRwOi8vdm9kLnFpZXppeHVhbnNoYW5nLm5ldC9pbWFnZS9jb3Zlci9FRDQzMDI2OTIxMTA0NTE2QTU3RjA4RDA4RTVBOTk1Qi02LTIucG5nP2F1dGhfa2V5PTE2MzM5MTY3ODUtZjc1NGRkNDU4YmY5NDBlNDgxNDgzZjQ1NzdkZWIzOGMtMC0xZjQwMmUwZDg0OWYxN2Y4MzBmZjU4ZGM4NTM0YTZiNiIsIkR1cmF0aW9uIjo1NTU0LjB9LCJBY2Nlc3NLZXlJZCI6IlNUUy5OVHpXZ3o1OVRZanZTV2c3b0pTVUFLTmJiIiwiUGxheURvbWFpbiI6InZvZC5xaWV6aXh1YW5zaGFuZy5uZXQiLCJBY2Nlc3NLZXlTZWNyZXQiOiJEcUQ0UGVnNGVxV1pMdm5NQWFtQzRlSjd4YTJ3Q0ZDVFk5NnlDa0tCYkdTTCIsIlJlZ2lvbiI6ImNuLXNoYW5naGFpIiwiQ3VzdG9tZXJJZCI6MTkzMTM5MzU2MTI0NTEwMn0=
     * VideoMeta : {"Status":"Normal","VideoId":"3118688c58f34aaebc1b2e9fac625e7b","Title":"放牛班的春天","CoverURL":"http://vod.qiezixuanshang.net/image/cover/ED43026921104516A57F08D08E5A995B-6-2.png?auth_key=1633916785-f754dd458bf940e481483f4577deb38c-0-1f402e0d849f17f830ff58dc8534a6b6","Duration":5554}
     * RequestId : 2F828F40-DCFF-5AFC-9564-40943813B555
     * mid : 3118688c58f34aaebc1b2e9fac625e7b
     * time : 2187
     * note : 法语《Les Choristes》是2004年3月17日上映的一部法国音乐电影，由克里斯托夫·巴拉蒂执导。
     */

    public String PlayAuth;
    public VideoMetaBean VideoMeta;
    public String RequestId;
    public String mid;
    public long time;
    public String note;
    public Infor infor;
    @Override
    public String toString() {
        return "playAuth:" + PlayAuth + " requestid:" + RequestId + " \n\t mid:" + mid + " \n\t time:" + time + " \n\tnote:" + note+" \n\t videoMeta:"+VideoMeta.toString();
    }


    public static class VideoMetaBean {
        /**
         * Status : Normal
         * VideoId : 3118688c58f34aaebc1b2e9fac625e7b
         * Title : 放牛班的春天
         * CoverURL : http://vod.qiezixuanshang.net/image/cover/ED43026921104516A57F08D08E5A995B-6-2.png?auth_key=1633916785-f754dd458bf940e481483f4577deb38c-0-1f402e0d849f17f830ff58dc8534a6b6
         * Duration : 5554
         */

        public String Status;
        public String VideoId;
        public String Title;
        public String CoverURL;
        public float Duration;

        @Override
        public String toString() {
            return "status:"+Status+"  \n\t videoId:"+VideoId+"  \n\t title:"+Title+"   \n\t coverurl:"+CoverURL+" \n\t dura:"+Duration;
        }

    }
    public static class Infor{
        public String RequestId;
        public Video Video;
    }
    public static class Video{
      
        /**
         * Status : Normal
         * ModifyTime : 2021-11-26 16:40:50
         * Description : Jaychou
         * VideoId : 76d0a96f62694c51bb6c2adab0bc0d90
         * Size : 7403346
         * CreateTime : 2021-11-25 11:48:35
         * DownloadSwitch : on
         * Title : Mojito
         * ModificationTime : 2021-11-26T08:40:50Z
         * Duration : 185.0514
         * PreprocessStatus : UnPreprocess
         * CreationTime : 2021-11-25T03:48:35Z
         * RegionId : cn-shanghai
         * StorageLocation : out-941db3e85b3c11e8a0a600163e1c60dc.oss-cn-shanghai.aliyuncs.com
         * Snapshots : {"Snapshot":[]}
         * Tags : 周杰伦
         * TemplateGroupId : 7a1216f13a466a89dfc9546f1a4da4e6
         */
        public String Status;
        public String ModifyTime;
        public String Description;
        public String VideoId;
        public int Size;
        public String CreateTime;
        public String DownloadSwitch;
        public String Title;
        public String ModificationTime;
        public double Duration;
        public String PreprocessStatus;
        public String CreationTime;
        public String RegionId;
        public String StorageLocation;
        public SnapshotsBean Snapshots;
        public String Tags;
        public String TemplateGroupId;

    }
    public static class SnapshotsBean {
        public List<?> Snapshot;
    }

}
