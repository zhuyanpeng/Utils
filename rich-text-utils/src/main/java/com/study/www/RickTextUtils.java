package com.study.www;

import org.apache.commons.lang3.StringUtils;

/**
 * 对外提供的服务
 * @author : THINK.zhuyanpeng
 * @Description: :
 * @date :Create in  2018/11/22- 16:00
 * @Version: V1.0
 * @Modified By:
 **/
public class RickTextUtils  {
    private static String uploadDir="/root/daijiashan/tomcat-images-server/webapps/images/";
    private static String imageServce="http://127.0.0.1:8080/";

    public static String updateRickText(String oldBody, String newBody) {
        if (!StringUtils.equals(oldBody,newBody)){
            if (StringUtils.isBlank(newBody)){
                return newBody;
            }
            //提交当前的
            newBody= RickTextContent.uploadLocalImageNow(newBody, uploadDir+"ricktext/", imageServce + "images/ricktext", true,false);
            if (StringUtils.isNotBlank(oldBody)){
                try {
                    RickTextContent.removeContent(oldBody,uploadDir+"ricktext/",imageServce + "images/ricktext",3);
                } catch (Exception e) {
                }
            }
        }
        return newBody;
    }

    public static void removeRickText(String body) {
        if (StringUtils.isNotBlank(body)) {
            RickTextContent.removeContent(body, uploadDir + "ricktext/", imageServce + "images/ricktext", 3);
        }
    }

    public static String uploadLocalImage(String body) {
        if (StringUtils.isNotBlank(body)){
            body= RickTextContent.uploadLocalImageNow(body, uploadDir+"ricktext/", imageServce + "images/ricktext", true,true);
        }
        return  body;
    }
}
