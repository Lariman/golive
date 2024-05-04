package org.golive.msg.provider.config;

public enum SmsTemplateIDEnum {

    SMS_LOGIN_CODE_TEMPLATE("1", "登陆验证短信模板");

    String templatedId;
    String desc;

    SmsTemplateIDEnum(String templatedId, String desc) {
        this.templatedId = templatedId;
        this.desc = desc;
    }

    public String getTemplatedId() {
        return templatedId;
    }

    public String getDesc() {
        return desc;
    }
}
