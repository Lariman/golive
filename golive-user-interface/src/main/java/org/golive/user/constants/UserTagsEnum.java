package org.golive.user.constants;

public enum UserTagsEnum {

    IS_RICH((long) Math.pow(2, 0), "是否是有钱用户", "tag_info_01"),
    IS_VIP((long) Math.pow(2, 1), "是否是VIP用户", "tag_info_02"),
    IS_OLD_USER((long) Math.pow(2, 3), "是否是老用户", "tag_info_03");

    long tag;  // 标签数值
    String desc;  // 标签描述
    String fieldName;  // 标签字段

    UserTagsEnum(long tag, String desc, String fieldName){
        this.tag = tag;
        this.desc = desc;
        this.fieldName = fieldName;
    }

    public long getTag(){
        return tag;
    }

    public String getDesc(){
        return desc;
    }

    public String getFieldName(){
        return fieldName;
    }
}
