package com.mabeijianxi.jianxiexpression.core;

import com.mabeijianxi.jianxiexpression.R;

import java.util.HashMap;

/**
 * Created by jian on 2016/6/23.
 * mabeijianxi@gmail.com
 */
public class ExpressionCache {
    //    TODO 每页数据资源，可自己定义，自己更改
//    public static final String[] page_1 = new String[]{
//            "[qz]aa_keai[/qz]", "[qz]ab_haha[/qz]", "[qz]ac_xixi[/qz]",
//            "[qz]ad_xiaoku[/qz]", "[qz]ae_sikao[/qz]", "[qz]af_numa[/qz]",
//            "[qz]ag_xu[/qz]", "[qz]ah_guzhang[/qz]", "[qz]ai_zuohengheng[/qz]",
//            "[qz]aj_youhengheng[/qz]", "[qz]ak_qinqin[/qz]", "[qz]al_yiwen[/qz]",
//            "[qz]am_baibai[/qz]", "[qz]an_hehe[/qz]", "[qz]ao_yinxian[/qz]",
//            "[qz]ap_haixiu[/qz]", "[qz]aq_jiyan[/qz]", "[qz]ar_dalian[/qz]",
//            "[qz]as_nu[/qz]", "[qz]at_lei[/qz]", ""
//
//    };
//    public static final String[] page_2 = new String[]{
//            "[qz]ba_bishi[/qz]", "[qz]bb_chanzui[/qz]", "[qz]bc_chijing[/qz]",
//            "[qz]bd_dahaqi[/qz]", "[qz]be_beishang[/qz]", "[qz]bf_bizui[/qz]",
//            "[qz]bg_ding[/qz]", "[qz]bh_ganmao[/qz]", "[qz]bi_han[/qz]",
//            "[qz]bj_aini[/qz]", "[qz]bk_heixian[/qz]", "[qz]bl_heng[/qz]",
//            "[qz]bm_huaxin[/qz]", "[qz]bn_kelian[/qz]", "[qz]bo_ku[/qz]",
//            "[qz]bp_kun[/qz]", "[qz]bq_landelini[/qz]", "[qz]br_qian[/qz]",
//            "[qz]bs_shayan[/qz]", "[qz]bt_shengbing[/qz]", ""
//    };
//    public static final String[] page_3 = new String[]{
//            "[qz]ca_shiwang[/qz]", "[qz]cb_shuijiao[/qz]", "[qz]cc_zhuakuang[/qz]",
//            "[qz]cd_taikaixin[/qz]", "[qz]ce_touxiao[/qz]", "[qz]cf_tu[/qz]",
//            "[qz]cg_wabishi[/qz]", "[qz]ch_weiqu[/qz]", "[qz]ci_yun[/qz]",
//            "[qz]cj_shuai[/qz]", "[qz]ck_doge[/qz]", "[qz]cl_miao[/qz]",
//            "[qz]cm_xiongmao[/qz]", "[qz]cn_tuzi[/qz]", "[qz]co_zhutou[/qz]",
//            "[qz]cp_shenshou[/qz]", "[qz]cq_nanhaier[/qz]", "[qz]cr_nvhaier[/qz]",
//            "[qz]cs_feizao[/qz]", "[qz]ct_aoteman[/qz]", ""
//    };
//    public static final String[] page_4 = new String[]{
//            "[qz]da_geili[/qz]", "[qz]db_jiong[/qz]", "[qz]dc_meng[/qz]",
//            "[qz]dd_shenma[/qz]", "[qz]de_v5[/qz]", "[qz]df_xi[/qz]",
//            "[qz]dg_zhi[/qz]", "[qz]dh_buyao[/qz]", "[qz]di_good[/qz]",
//            "[qz]dj_haha[/qz]", "[qz]dk_lai[/qz]", "[qz]dl_ok[/qz]",
//            "[qz]dm_ruo[/qz]", "[qz]dn_woshou[/qz]", "[qz]do_ye[/qz]",
//            "[qz]dp_zan[/qz]", "[qz]dq_zuoyi[/qz]", "[qz]dr_shangxin[/qz]",
//            "[qz]ds_xin[/qz]", "[qz]dt_dangao[/qz]", ""
//    };
//    public static final String[] page_5 = new String[]{
//            "[qz]ea_feiji[/qz]", "[qz]eb_ganbei[/qz]", "[qz]ec_huatong[/qz]",
//            "[qz]ed_lazhu[/qz]", "[qz]ee_liwu[/qz]", "[qz]ef_lvsidai[/qz]",
//            "[qz]eg_weibo[/qz]", "[qz]eh_weiguan[/qz]", "[qz]ei_yinyue[/qz]",
//            "[qz]ej_zhaoxiangji[/qz]", "[qz]ek_zhong[/qz]", "[qz]el_weifeng[/qz]",
//            "[qz]em_xianhua[/qz]", "[qz]en_taiyang[/qz]", "[qz]eo_yueliang[/qz]",
//            "[qz]ep_fuyun[/qz]", "[qz]eq_xiayu[/qz]", "[qz]er_shachenbao[/qz]",
//            "", "", ""
//    };
    public static final String[] page_6 = new String[]{
            "[qzxs0]", "[qzxs1]", "[qzxs2]",
            "[qzxs3]", "[qzxs4]", "[qzxs5]",
            "[qzxs6]", "[qzxs7]", "[qzxs8]",
            "[qzxs9]", "[qzxs10]", "[qzxs11]",
            "[qzxs12]", "[qzxs13]", "[qzxs14]",
            "[qzxs15]", "[qzxs16]", "[qzxs17]",
            "[qzxs18]", "[qzxs19]", ""
    };
    public static final String[] page_7 = new String[]{
            "[qzxs20]", "[qzxs21]", "[qzxs22]",
            "[qzxs23]", "[qzxs24]", "[qzxs25]",
            "[qzxs26]", "[qzxs27]", "[qzxs28]",
            "[qzxs29]", "[qzxs30]", "[qzxs31]",
            "[qzxs32]", "[qzxs33]", "[qzxs34]",
            "[qzxs35]", "[qzxs36]", "[qzxs37]",
            "[qzxs38]", "[qzxs39]", ""
    };

    public static final String[] page_8 = new String[]{
            "[qzxs40]", "[qzxs41]", "[qzxs42]",
            "[qzxs43]", "[qzxs44]", "[qzxs45]",
            "[qzxs46]", "[qzxs47]", "[qzxs48]",
            "[qzxs49]", "[qzxs50]", "[qzxs51]",
            "[qzxs52]", "[qzxs53]", "[qzxs54]",
            "[qzxs55]", "[qzxs56]", "[qzxs57]",
            "[qzxs58]", "[qzxs59]", ""
    };
    public static final String[] page_9 = new String[]{
            "[qzxs60]", "[qzxs61]", "[qzxs62]",
            "[qzxs63]", "[qzxs64]", "[qzxs65]",
            "[qzxs66]", "[qzxs67]", "[qzxs68]",
            "[qzxs69]", "[qzxs70]", "[qzxs71]",
            "[qzxs72]", "[qzxs73]", "[qzxs74]",
            "[qzxs75]", "[qzxs76]", "[qzxs77]",
            "[qzxs78]", "[qzxs79]", ""
    };
    public static final String[] page_10 = new String[]{
            "[qzxs80]", "[qzxs81]", "[qzxs82]",
            "[qzxs83]", "[qzxs84]", "[qzxs85]",
            "[qzxs86]", "[qzxs87]", "[qzxs88]",
            "[qzxs89]", "[qzxs90]", "[qzxs91]",
            "[qzxs92]", "[qzxs93]", "[qzxs94]",
            "[qzxs95]", "[qzxs96]", "[qzxs97]",
            "[qzxs98]", "[qzxs99]", ""
    };
    public static final String[] page_11 = new String[]{
            "[qzxs100]", "[qzxs101]", "[qzxs102]",
            "[qzxs103]", "[qzxs104]", "[qzxs105]",
            "[qzxs106]", "[qzxs107]", "[qzxs108]",
            "[qzxs109]", "[qzxs110]", "[qzxs111]",
            "[qzxs112]", "[qzxs113]", "[qzxs114]",
            "[qzxs115]", "[qzxs116]", "[qzxs117]",
            "[qzxs118]", "[qzxs119]", ""
    };
    public static final String[] page_12 = new String[]{
            "[qzxs120]", "[qzxs121]", "[qzxs122]",
            "[qzxs123]", "[qzxs124]", "[qzxs125]",
            "[qzxs126]", "[qzxs127]", "[qzxs128]",
            "[qzxs129]", "[qzxs130]", "[qzxs131]",
            "[qzxs132]", "[qzxs133]", "[qzxs134]",
            "[qzxs135]", "[qzxs136]", "[qzxs137]",
            "[qzxs138]", "[qzxs139]", ""
    };

    /**
     * 所有的表情资源id缓存，增加效率
     */
    private static HashMap<String, Integer> allExpressionTable = new HashMap<String, Integer>();

    static {


        allExpressionTable.put(page_6[0], R.drawable.emoji1
        );
        allExpressionTable.put(page_6[1], R.drawable.emoji2
        );
        allExpressionTable.put(page_6[2], R.drawable.emoji3
        );
        allExpressionTable.put(page_6[3], R.drawable.emoji4
        );
        allExpressionTable.put(page_6[4], R.drawable.emoji5
        );
        allExpressionTable.put(page_6[5], R.drawable.emoji6
        );
        allExpressionTable.put(page_6[6], R.drawable.emoji7
        );
        allExpressionTable.put(page_6[7], R.drawable.emoji8
        );
        allExpressionTable.put(page_6[8], R.drawable.emoji9
        );
        allExpressionTable.put(page_6[9], R.drawable.emoji10
        );
        allExpressionTable.put(page_6[10], R.drawable.emoji11
        );
        allExpressionTable.put(page_6[11], R.drawable.emoji12
        );
        allExpressionTable.put(page_6[12], R.drawable.emoji13
        );
        allExpressionTable.put(page_6[13], R.drawable.emoji14
        );
        allExpressionTable.put(page_6[14], R.drawable.emoji15
        );
        allExpressionTable.put(page_6[15], R.drawable.emoji16
        );
        allExpressionTable.put(page_6[16], R.drawable.emoji17
        );
        allExpressionTable.put(page_6[17], R.drawable.emoji18
        );
        allExpressionTable.put(page_6[18], R.drawable.emoji19
        );
        allExpressionTable.put(page_6[19], R.drawable.emoji20
        );


        allExpressionTable.put(page_7[0], R.drawable.emoji21
        );
        allExpressionTable.put(page_7[1], R.drawable.emoji22
        );
        allExpressionTable.put(page_7[2], R.drawable.emoji23
        );
        allExpressionTable.put(page_7[3], R.drawable.emoji24
        );
        allExpressionTable.put(page_7[4], R.drawable.emoji25
        );
        allExpressionTable.put(page_7[5], R.drawable.emoji26
        );
        allExpressionTable.put(page_7[6], R.drawable.emoji27
        );
        allExpressionTable.put(page_7[7], R.drawable.emoji28
        );
        allExpressionTable.put(page_7[8], R.drawable.emoji29
        );
        allExpressionTable.put(page_7[9], R.drawable.emoji30
        );
        allExpressionTable.put(page_7[10], R.drawable.emoji31
        );
        allExpressionTable.put(page_7[11], R.drawable.emoji32
        );
        allExpressionTable.put(page_7[12], R.drawable.emoji33
        );
        allExpressionTable.put(page_7[13], R.drawable.emoji34
        );
        allExpressionTable.put(page_7[14], R.drawable.emoji35
        );
        allExpressionTable.put(page_7[15], R.drawable.emoji36
        );
        allExpressionTable.put(page_7[16], R.drawable.emoji37
        );
        allExpressionTable.put(page_7[17], R.drawable.emoji38
        );
        allExpressionTable.put(page_7[18], R.drawable.emoji39
        );
        allExpressionTable.put(page_7[19], R.drawable.emoji40
        );


        allExpressionTable.put(page_8[0], R.drawable.emoji41
        );
        allExpressionTable.put(page_8[1], R.drawable.emoji42
        );
        allExpressionTable.put(page_8[2], R.drawable.emoji43
        );
        allExpressionTable.put(page_8[3], R.drawable.emoji44
        );
        allExpressionTable.put(page_8[4], R.drawable.emoji45
        );
        allExpressionTable.put(page_8[5], R.drawable.emoji46
        );
        allExpressionTable.put(page_8[6], R.drawable.emoji47
        );
        allExpressionTable.put(page_8[7], R.drawable.emoji48
        );
        allExpressionTable.put(page_8[8], R.drawable.emoji49
        );
        allExpressionTable.put(page_8[9], R.drawable.emoji50
        );
        allExpressionTable.put(page_8[10], R.drawable.emoji51
        );
        allExpressionTable.put(page_8[11], R.drawable.emoji52
        );
        allExpressionTable.put(page_8[12], R.drawable.emoji53
        );
        allExpressionTable.put(page_8[13], R.drawable.emoji54
        );
        allExpressionTable.put(page_8[14], R.drawable.emoji55
        );
        allExpressionTable.put(page_8[15], R.drawable.emoji56
        );
        allExpressionTable.put(page_8[16], R.drawable.emoji57
        );
        allExpressionTable.put(page_8[17], R.drawable.emoji58
        );
        allExpressionTable.put(page_8[18], R.drawable.emoji59
        );
        allExpressionTable.put(page_8[19], R.drawable.emoji60
        );


        allExpressionTable.put(page_9[0], R.drawable.emoji61
        );
        allExpressionTable.put(page_9[1], R.drawable.emoji62
        );
        allExpressionTable.put(page_9[2], R.drawable.emoji63
        );
        allExpressionTable.put(page_9[3], R.drawable.emoji64
        );
        allExpressionTable.put(page_9[4], R.drawable.emoji65
        );
        allExpressionTable.put(page_9[5], R.drawable.emoji66
        );
        allExpressionTable.put(page_9[6], R.drawable.emoji67
        );
        allExpressionTable.put(page_9[7], R.drawable.emoji68
        );
        allExpressionTable.put(page_9[8], R.drawable.emoji69
        );
        allExpressionTable.put(page_9[9], R.drawable.emoji70
        );
        allExpressionTable.put(page_9[10], R.drawable.emoji71
        );
        allExpressionTable.put(page_9[11], R.drawable.emoji72
        );
        allExpressionTable.put(page_9[12], R.drawable.emoji73
        );
        allExpressionTable.put(page_9[13], R.drawable.emoji74
        );
        allExpressionTable.put(page_9[14], R.drawable.emoji75
        );
        allExpressionTable.put(page_9[15], R.drawable.emoji76
        );
        allExpressionTable.put(page_9[16], R.drawable.emoji77
        );
        allExpressionTable.put(page_9[17], R.drawable.emoji78
        );
        allExpressionTable.put(page_9[18], R.drawable.emoji79
        );
        allExpressionTable.put(page_9[19], R.drawable.emoji80
        );


        allExpressionTable.put(page_10[0], R.drawable.emoji81
        );
        allExpressionTable.put(page_10[1], R.drawable.emoji82
        );
        allExpressionTable.put(page_10[2], R.drawable.emoji83
        );
        allExpressionTable.put(page_10[3], R.drawable.emoji84
        );
        allExpressionTable.put(page_10[4], R.drawable.emoji85
        );
        allExpressionTable.put(page_10[5], R.drawable.emoji86
        );
        allExpressionTable.put(page_10[6], R.drawable.emoji87
        );
        allExpressionTable.put(page_10[7], R.drawable.emoji88
        );
        allExpressionTable.put(page_10[8], R.drawable.emoji89
        );
        allExpressionTable.put(page_10[9], R.drawable.emoji90
        );
        allExpressionTable.put(page_10[10], R.drawable.emoji91
        );
        allExpressionTable.put(page_10[11], R.drawable.emoji92
        );
        allExpressionTable.put(page_10[12], R.drawable.emoji93
        );
        allExpressionTable.put(page_10[13], R.drawable.emoji94
        );
        allExpressionTable.put(page_10[14], R.drawable.emoji95
        );
        allExpressionTable.put(page_10[15], R.drawable.emoji96
        );
        allExpressionTable.put(page_10[16], R.drawable.emoji97
        );
        allExpressionTable.put(page_10[17], R.drawable.emoji98
        );
        allExpressionTable.put(page_10[18], R.drawable.emoji99
        );
        allExpressionTable.put(page_10[19], R.drawable.emoji100
        );


        allExpressionTable.put(page_11[0], R.drawable.emoji101
        );
        allExpressionTable.put(page_11[1], R.drawable.emoji102
        );
        allExpressionTable.put(page_11[2], R.drawable.emoji103
        );
        allExpressionTable.put(page_11[3], R.drawable.emoji104
        );
        allExpressionTable.put(page_11[4], R.drawable.emoji105
        );
        allExpressionTable.put(page_11[5], R.drawable.emoji106
        );
        allExpressionTable.put(page_11[6], R.drawable.emoji107
        );
        allExpressionTable.put(page_11[7], R.drawable.emoji108
        );
        allExpressionTable.put(page_11[8], R.drawable.emoji109
        );
        allExpressionTable.put(page_11[9], R.drawable.emoji110
        );
        allExpressionTable.put(page_11[10], R.drawable.emoji111
        );
        allExpressionTable.put(page_11[11], R.drawable.emoji112
        );
        allExpressionTable.put(page_11[12], R.drawable.emoji113
        );
        allExpressionTable.put(page_11[13], R.drawable.emoji114
        );
        allExpressionTable.put(page_11[14], R.drawable.emoji115
        );
        allExpressionTable.put(page_11[15], R.drawable.emoji116
        );
        allExpressionTable.put(page_11[16], R.drawable.emoji117
        );
        allExpressionTable.put(page_11[17], R.drawable.emoji118
        );
        allExpressionTable.put(page_11[18], R.drawable.emoji119
        );
        allExpressionTable.put(page_11[19], R.drawable.emoji120
        );



        allExpressionTable.put(page_12[0], R.drawable.emoji121
        );
        allExpressionTable.put(page_12[1], R.drawable.emoji122
        );
        allExpressionTable.put(page_12[2], R.drawable.emoji123
        );
        allExpressionTable.put(page_12[3], R.drawable.emoji124
        );
        allExpressionTable.put(page_12[4], R.drawable.emoji125
        );
        allExpressionTable.put(page_12[5], R.drawable.emoji126
        );
        allExpressionTable.put(page_12[6], R.drawable.emoji127
        );
        allExpressionTable.put(page_12[7], R.drawable.emoji128
        );
        allExpressionTable.put(page_12[8], R.drawable.emoji129
        );
        allExpressionTable.put(page_12[9], R.drawable.emoji130
        );
        allExpressionTable.put(page_12[10], R.drawable.emoji131
        );
        allExpressionTable.put(page_12[11], R.drawable.emoji132
        );
        allExpressionTable.put(page_12[12], R.drawable.emoji133
        );
        allExpressionTable.put(page_12[13], R.drawable.emoji134
        );
        allExpressionTable.put(page_12[14], R.drawable.emoji135
        );
        allExpressionTable.put(page_12[15], R.drawable.emoji136
        );
        allExpressionTable.put(page_12[16], R.drawable.emoji137
        );
        allExpressionTable.put(page_12[17], R.drawable.emoji138
        );
        allExpressionTable.put(page_12[18], R.drawable.emoji139
        );
        allExpressionTable.put(page_12[19], R.drawable.emoji140
        );
    }

    /**
     * 最近使用表情缓存
     */
    private static String[] recentExpression = new String[21];
    /**
     * tab
     */
    private static String[] pageTitle;

    /**
     * 得到每类表情的标签，可拓展
     *
     * @return
     */
    public static String[] getPageTitle() {
        String category_1 = "最近";
        String category_2 = "表情";
        String category_3 = "表二";
        if (pageTitle == null) {
            pageTitle = new String[]{category_1, category_2, category_3};
        }
        return pageTitle;
    }

    public static String[] getRecentExpression() {
        return recentExpression;
    }

    public static HashMap<String, Integer> getAllExpressionTable() {
        return allExpressionTable;
    }
}