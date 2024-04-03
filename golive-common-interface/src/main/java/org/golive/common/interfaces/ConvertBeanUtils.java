package org.golive.common.interfaces;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ConvertBeanUtils {
    /*
    * 将一个对象转成目标对象
    * */
    public static <T> T convert(Object source, Class<T> targetClasses){
        if(source == null){  // source为源对象,targetClasses为需要转成的对象
            return null;
        }
        T t = newInstance(targetClasses);
        BeanUtils.copyProperties(source, t);
        return t;
    }

    /*
    * 将List对象转换成目标对象,实现的是ArrayList
    * */
    public static <K, T> List<T> convertList(List<K> sourceList, Class<T> targetClass){
        if(sourceList == null){
            return null;
        }
        List targetList = new ArrayList((int)(sourceList.size()/0.75) + 1);
        for(K source : sourceList){
            targetList.add(convert(source, targetClass));
        }
        return targetList;
    }


    /*
    * 构建新实例
    * */
    private static <T> T newInstance(Class<T> targetClass){
        try{
            return targetClass.newInstance();
        }catch (Exception e){
            throw new BeanInstantiationException(targetClass, "instantiation error", e);
        }
    }

}
