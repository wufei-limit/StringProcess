package com.jiaokang.progress;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiaokang on 2022/2/25
 */
public class LocalConstants {
    public static final String AR = "ar";
    public static final String DE = "de";
    public static final String EN = "en";
    public static final String ES = "es";
    public static final String FA = "fa";
    public static final String FR = "fr";
    public static final String HI = "hi";
    public static final String IN = "in";
    public static final String IT = "it";
    public static final String JA = "ja";
    public static final String KO = "ko";
    public static final String PT = "pt";
    public static final String PT_RBR = "pt-rBR";
    public static final String RU = "ru";
    public static final String TH = "th";
    public static final String TR = "tr";
    public static final String UK = "uk";
    public static final String VI = "vi";
    public static final String ZH = "zh";
    public static final String ZH_CN = "zh-rCN";
    public static final String ZH_TW = "zh-rTW";
    public static final String NAME = "name";
    //未找到区域的数据，存放在这里
    public static final String QUOTA = "quota";

    /**
     * 缺失翻译的excel表头
     */
    public static final String[] NEED_TRANSLATE_SHEET = {"name","en","miss_local"};
    /**
     * 生成的excel 表头
     */
    public static final List<String> COL_LIST = new ArrayList<>(22);
    static {
        COL_LIST.add(NAME);
        COL_LIST.add(AR);
        COL_LIST.add(DE);
        COL_LIST.add(EN);
        COL_LIST.add(ES);
        COL_LIST.add(FA);
        COL_LIST.add(FR);
        COL_LIST.add(HI);
        COL_LIST.add(IN);
        COL_LIST.add(IT);
        COL_LIST.add(JA);
        COL_LIST.add(KO);
        COL_LIST.add(PT);
        COL_LIST.add(PT_RBR);
        COL_LIST.add(RU);
        COL_LIST.add(TH);
        COL_LIST.add(TR);
        COL_LIST.add(UK);
        COL_LIST.add(VI);
        COL_LIST.add(ZH);
        COL_LIST.add(ZH_CN);
        COL_LIST.add(ZH_TW);
        COL_LIST.add(QUOTA);
    }
}
