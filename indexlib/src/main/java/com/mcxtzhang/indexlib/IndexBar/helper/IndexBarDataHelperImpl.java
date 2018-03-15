package com.mcxtzhang.indexlib.IndexBar.helper;

import com.mcxtzhang.indexlib.HanziToPinyin;
import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 介绍：IndexBar 的 数据相关帮助类 实现
 * * 1 将汉语转成拼音(利用tinyPinyin)
 * 2 填充indexTag (取拼音首字母)
 * 3 排序源数据源
 * 4 根据排序后的源数据源->indexBar的数据源
 * 作者：zhangxutong
 * 邮箱：mcxtzhang@163.com
 * 主页：http://blog.csdn.net/zxt0601
 * 时间： 2016/11/28.
 */

public class IndexBarDataHelperImpl implements IIndexBarDataHelper {
    /**
     * 如果需要，
     * 字符->拼音，
     *
     * @param datas
     */
    @Override
    public IIndexBarDataHelper convert(List<? extends BaseIndexPinyinBean> datas) {
        if (null == datas || datas.isEmpty()) {
            return this;
        }
        int size = datas.size();
        for (int i = 0; i < size; i++) {
            BaseIndexPinyinBean indexPinyinBean = datas.get(i);
            StringBuilder pySb = new StringBuilder();
            if (indexPinyinBean.isNeedToPinyin()) {
                String target = indexPinyinBean.getTarget();//取出需要被拼音化的字段
                ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(target);
                for (HanziToPinyin.Token token : tokens) {
                    pySb.append(token.target);
                }
                indexPinyinBean.setBaseIndexPinyin(pySb.toString());//设置城市名全拼音
            }
        }
        return this;
    }

    private String toPinyin(char c) {
        return String.valueOf(c);
    }

    /**
     * 如果需要取出，则
     * 取出首字母->tag,或者特殊字母 "#".
     * 否则，用户已经实现设置好
     *
     * @param datas
     */
    @Override
    public IIndexBarDataHelper fillInexTag(List<? extends BaseIndexPinyinBean> datas) {
        if (null == datas || datas.isEmpty()) {
            return this;
        }
        int size = datas.size();
        for (int i = 0; i < size; i++) {
            BaseIndexPinyinBean indexPinyinBean = datas.get(i);
            if (indexPinyinBean.isNeedToPinyin()) {
                //以下代码设置城市拼音首字母
                String tagString = indexPinyinBean.getBaseIndexPinyin().toString().substring(0, 1);
                if (tagString.matches("[A-Z]")) {//如果是A-Z字母开头
                    indexPinyinBean.setBaseIndexTag(tagString);
                } else {//特殊字母这里统一用#处理
                    indexPinyinBean.setBaseIndexTag("#");
                }
            }
        }
        return this;
    }

    @Override
    public IIndexBarDataHelper sortSourceDatas(List<? extends BaseIndexPinyinBean> datas) {
        if (null == datas || datas.isEmpty()) {
            return this;
        }
        convert(datas);
        fillInexTag(datas);
        //对数据源进行排序
        Collections.sort(datas, new Comparator<BaseIndexPinyinBean>() {
            @Override
            public int compare(BaseIndexPinyinBean lhs, BaseIndexPinyinBean rhs) {
                if (!lhs.isNeedToPinyin()) {
                    return 0;
                } else if (!rhs.isNeedToPinyin()) {
                    return 0;
                } else if (lhs.getBaseIndexTag().equals("#")) {
                    return 1;
                } else if (rhs.getBaseIndexTag().equals("#")) {
                    return -1;
                } else {
                    return lhs.getBaseIndexPinyin().compareTo(rhs.getBaseIndexPinyin());
                }
            }
        });
        return this;
    }

    @Override
    public IIndexBarDataHelper getSortedIndexDatas(List<? extends BaseIndexPinyinBean> sourceDatas, List<String> indexDatas) {
        if (null == sourceDatas || sourceDatas.isEmpty()) {
            return this;
        }
        //按数据源来 此时sourceDatas 已经有序
        int size = sourceDatas.size();
        String baseIndexTag;
        for (int i = 0; i < size; i++) {
            baseIndexTag = sourceDatas.get(i).getBaseIndexTag();
            if (!indexDatas.contains(baseIndexTag)) {//则判断是否已经将这个索引添加进去，若没有则添加
                indexDatas.add(baseIndexTag);
            }
        }
        return this;
    }
}
