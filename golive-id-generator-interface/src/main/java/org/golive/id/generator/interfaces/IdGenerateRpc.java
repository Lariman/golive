package org.golive.id.generator.interfaces;


public interface IdGenerateRpc {

    /*
    * 根据本地步长来生成唯一ID(区间性递增)
    * */
    Long getSeqId(Integer id);

    /*
    * 生成非连续性ID
    * */
    Long getUnSeqId(Integer id);
}
