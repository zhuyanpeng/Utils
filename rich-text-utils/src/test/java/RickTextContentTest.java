import com.study.www.RickTextContent;
import org.junit.Test;

/**
 * @author : THINK.zhuyanpeng
 * @Description: :
 * @date :Create in  2018/11/20- 17:03
 * @Version: V1.0
 * @Modified By:
 **/
public class RickTextContentTest {


    @Test
    public void fixValidateTest() {
        String uploadDir="E:\\tomcat\\apache-tomcat-7.0.70\\webapps\\images\\";
        String imageServce="http://127.0.0.1:8080/images";
     /*   String content ="       <section class=\"V5\" style=\"position: static; box-sizing: border-box;\" powered-by=\"xiumi.us\">\n" +
                "        <section style=\"text-align: center; margin-top: 10px; margin-bottom: 10px; position: static; box-sizing: border-box;\">\n" +
                "            <section style=\"max-width: 100%; vertical-align: middle; display: inline-block; width: 10%; box-sizing: border-box;\">\n" +
                "                <img src=\"http://img.xiumi.us/xmi/ua/1rorG/i/a8f23f68320db17bc5c9835d88a2bf7c-sz_88814.gif\" style=\"vertical-align: middle; max-width: 100%; width: 100%; box-sizing: border-box;\" data-ratio=\"1\" data-w=\"240\" _width=\"100%\"/>\n" +
                "            </section>\n" +
                "        </section>\n" +
                "    </section>";*/
        String content ="<img class=\"\" src=\"https://mmbiz.qpic.cn/mmbiz_jpg/YWFS85NvLBjtwQxcWA1Px3In8FW8yz8LjjkdocWl7ibr3UWXAxu4OsYNVYa3VtSqBwISnxibKC8jdaLPFsVicM4BQ/640?wx_fmt=jpeg&amp;tp=webp&amp;wxfrom=5&amp;wx_lazy=1&amp;wx_co=1\" width=\"617\" height=\"373\" _src=\"https://mmbiz.qpic.cn/mmbiz_jpg/YWFS85NvLBjtwQxcWA1Px3In8FW8yz8LjjkdocWl7ibr3UWXAxu4OsYNVYa3VtSqBwISnxibKC8jdaLPFsVicM4BQ/640?wx_fmt=jpeg&amp;tp=webp&amp;wxfrom=5&amp;wx_lazy=1&amp;wx_co=1\">";
       // RickTextContent.removeContent(contentRemove,uploadDir,imageServce,2);
        //String s = RickTextContent.uploadLocalImage(content, uploadDir, imageServce, true, false);
            String s = RickTextContent.uploadLocalImageNow(content, uploadDir, imageServce, true, false);
        System.out.println(s);
    }

}
