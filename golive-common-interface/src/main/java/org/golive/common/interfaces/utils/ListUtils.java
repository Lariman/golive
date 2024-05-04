package org.golive.common.interfaces.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    /**
     * 将一个大的List集合拆解为多个子List集合
     *
     * @param list
     * @param subNum
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int subNum) {
        List<List<T>> resultList = new ArrayList<>();
        int priIndex = 0;
        int lastIndex = 0;
        int insertTimes = list.size() / subNum;
        List<T> subList;
        for (int i = 0; i <= insertTimes; i++) {
            priIndex = subNum * i;
            lastIndex = priIndex + subNum;
            if(i != insertTimes){
                subList = list.subList(priIndex, lastIndex);
            } else {
                subList = list.subList(priIndex, list.size());
            }
            if(subList.size() > 0){
                resultList.add(subList);
            }
        }
        return resultList;
    }

    /*public static void main(String[] args) {
        List<Integer> idList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            idList.add(i);
        }
        System.out.println(ListUtils.splitList(idList, 10));
    }*/
}
