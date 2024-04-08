package org.golive.user.utils;

public class TagInfoUtils {

    /**
     * 判断是否存在某个标签
     * @param tagInfo 用户当前的标签值
     * @param matchTag  被查询是否匹配的标签值
     * @return
     */
    public static boolean isContain(Long tagInfo, Long matchTag){
        return tagInfo != null && matchTag != null && (tagInfo & matchTag) == matchTag;
    }

    // public static void main(String[] args) {
    //     System.out.println(TagInfoUtils.isContain(3L, 2L));
    //     System.out.println(TagInfoUtils.isContain(3L, 1L));
    //     System.out.println(TagInfoUtils.isContain(3L, 4L));
    // }
}
