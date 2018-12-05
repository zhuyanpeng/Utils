package com.study.www;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author  THINK.zhuyanpeng
 * @Description 富文本内容保存
 * @date Create in  2018/11/20- 16:41
 * @Version V1.0
 **/
public class RickTextContent {
    /**
     * @Fields  dirFormat 生成的文件夹的路劲的格式
     */
    public  static String dirFormat = "yyyy/MM/dd/HHmmssSSS";
    /**
     * @Fields  fixedThreadPool 定义一个定长为10的线程池用来进行图片等资源的远程下载
     * */
    private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);

    private static final  String     REGEX_IMG = "(http|https):\"?(.*?)(\"|>|\\s+)?(\\.jpg|\\.bmp|\\.eps|\\.gif|" +
            "\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic|tp=webp)";

    private static final String REGEXIMAGES= "\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpeg|\\.ico";
    //强制转换的格式
    private static HashMap<String,String> suffixComplues = new HashMap<>();

    static {
        suffixComplues.put(".webp",".jpg");

    }

    /**
     * Deprecated 原因： 其对特殊后缀的图片处理会有问题 例如 webp 请使用uploadLocalImageNow
     * @Description 图片保存到本地文件夹，运用于图片等本地保存时使用
     * @param preFix 本地文件夹前缀eg: /home/images/ OR  E:/
     * @param content 内容
     * @param imageServerFix 图片服务器地址 若为空将会以preFix 作为路劲  eg: 118.129.11.12:8080/images
     * @param isNowDir 是否允许根据录入时间创建二三级文件夹，默认true 建议开启 ，方便文件夹的查看和管理,默认格式 yyyy/MM/dd/HHmmss
     *                若需变更请修改变量 dirFormat
     * @return String 被本地保存的路劲
     */
    @Deprecated
    public static String uploadLocalImage(String content,String preFix,String imageServerFix,Boolean isNowDir,boolean isAsync){
        if (isNowDir == null){
            isNowDir = true;
        }
        /*正文图片*/
        Pattern pattern = Pattern.compile(REGEX_IMG, Pattern.CASE_INSENSITIVE);
        Matcher matcher= pattern.matcher(content);
        //作为image的结尾标识
        int increment = 0;
        //单独作为一个文件夹去存当前的富文本的资源信息
        String[] fixValidate = fixValidate(preFix, imageServerFix, isNowDir);
        while (matcher.find()){
            increment++;
            String group = matcher.group();
            /*文件重新命名*/
            String suffix = group.substring(group.lastIndexOf(".") + 1);
            if (suffixComplues.containsKey(suffix)){
                suffix =suffixComplues.get(suffix);
            }
            if (StringUtils.isBlank(suffix) || (!REGEX_IMG.contains(suffix))){
                suffix = "jpg";
            }
            String newFileName = DateUtils.getDateFormat(new Date(), "HHmmss"+increment) + "." + suffix;
            /*替换读出文本*/
            content = content.replace(group, fixValidate[1]+newFileName);
            // log.warn("当前下载任务====>"+group+"转存目录===>"+fixValidate[1]+newFileName);
            if (isAsync){
                fixedThreadPool.execute(new UploadPicLocal(group,fixValidate[0]+newFileName));
            }else{
                new UploadPicLocal(group,fixValidate[0]+newFileName).run();
            }
        }
        return content;
    }
    /**
     * @Description 图片保存到本地文件夹，运用于图片等本地保存时使用
     * @param preFix 本地文件夹前缀eg: /home/images/ OR  E:/
     * @param content 内容
     * @param imageServerFix 图片服务器地址 若为空将会以preFix 作为路劲  eg: 118.129.11.12:8080/images
     * @param isNowDir 是否允许根据录入时间创建二三级文件夹，默认true 建议开启 ，方便文件夹的查看和管理,默认格式 yyyy/MM/dd/HHmmss
     *                若需变更请修改变量 dirFormat
     * @return String 被本地保存的路劲
     */
    public static String uploadLocalImageNow(String content,String preFix,String imageServerFix,Boolean isNowDir,boolean isAsync){
        if (isNowDir == null){
            isNowDir = true;
        }
        int increment = 0;
        //待下载的url
        List<String> imagesList = new ArrayList<>();
        //单独作为一个文件夹去存当前的富文本的资源信息
        String[] fixValidate = fixValidate(preFix, imageServerFix, isNowDir);
        //处理后缀的正则
        Pattern suffixPattern = Pattern.compile(REGEXIMAGES, Pattern.CASE_INSENSITIVE);
        //获得链接的正则
        String regImgSrc = "src[\\s]*=[\\\\]*[\"\"']+[^\"\"']*?[\\\\]*[\"\"']+";
        Pattern httpPattern = Pattern.compile(regImgSrc, Pattern.CASE_INSENSITIVE);
        // 处理img图 <img *****
        String regImg = "<(img|IMG)(.*?)(/>|></img>|>)";
        Pattern pattern = Pattern.compile(regImg, Pattern.CASE_INSENSITIVE);
        Matcher mc = pattern.matcher(content);
        while (mc.find()) {
            {
                String imgStr = mc.group();
                Matcher matcher = httpPattern.matcher(imgStr);
                while (matcher.find()){
                    String imgSrc = matcher.group();
                    int pIndex = imgSrc.indexOf("?");
                    if (pIndex >= 0) {
                        imgSrc = imgSrc.substring(0, pIndex);
                    }
                    if (imgSrc.contains("http://") || imgSrc.contains("https://")) {
                        imagesList.add(imgSrc);
                    }
                }
            }
        }
        // 处理背景图   vertical-align: top; background-image：*******
        String regBackImg = "background-image:[^)]*?\\);";
        Pattern re = Pattern.compile(regBackImg, Pattern.CASE_INSENSITIVE);
        Matcher backImgMc = re.matcher(content);
        while (backImgMc.find()){
            String m = backImgMc.group();
            String backImgUrl = m.replace("background-image:", "").replace("&quot;", "").replace("url", "").
                    replace("(", "").replace(")", "").replace(";", "").trim();
            int pIndex = backImgUrl.indexOf("?");
            if (pIndex >= 0)
            {
                backImgUrl = backImgUrl.substring(0, pIndex);
            }
            if (backImgUrl.contains("http://") || backImgUrl.contains("https://"))
            {
               imagesList.add(backImgUrl);
            }
        }
        //图片下载
        for (String image : imagesList) {
            increment++;
            String imageUrl = regImgUrl(image);
            String suffix = ".jpg";
            Matcher tempMatcher= suffixPattern.matcher(imageUrl);
            while (tempMatcher.find()){
                suffix = "." +imageUrl.substring(imageUrl.lastIndexOf(".") + 1);
                String[] suffixSplit = suffix.split("\"");
                //取掉头尾特殊字符 取出最长的那段
                suffix = "";
                for (String s : suffixSplit) {
                    if (suffix.length() < s.length()){
                        suffix = s;
                    }
                }
                //替换目标的后缀
                imageUrl=imageUrl.substring(0,imageUrl.lastIndexOf("."))+suffix;
            }
            String newFileName = DateUtils.getDateFormat(new Date(), "HHmmss"+increment) +suffix;
            /*替换读出文本*/
            content = content.replace(imageUrl, fixValidate[1]+newFileName);
            UploadPicLocal uploadPicLocal = new UploadPicLocal(imageUrl, fixValidate[0] + newFileName);
            if (isAsync){
                fixedThreadPool.execute(uploadPicLocal);
            }else{
                uploadPicLocal.run();
            }
        }
        return content;
    }
    /**
     * 删除文件服务器中的图片
     * @param content :内容
     * @param preFix :内地文件夹前缀eg: /home/images/ OR  E:/
     * @param imageServerFix: 图片服务器地址 若为空将会以preFix 作为路劲  eg: 118.129.11.12:8080/images
     * @param dirBankNum: 根据<tt>preFix</tt>删除最后的空文件夹 默认为1,根据实际进行填写
     */
    public static void removeContent(String content,String preFix,String imageServerFix,Integer dirBankNum){
        if (dirBankNum == null){
            dirBankNum = 1;
        }
        /*正文图片*/
        Pattern pattern = Pattern.compile(REGEX_IMG, Pattern.CASE_INSENSITIVE);
        Matcher matcher= pattern.matcher(content);
        int i = StringUtils.lastIndexOfAny(imageServerFix, "/", "\\");
        if (i != imageServerFix.length() -1){
            imageServerFix = imageServerFix+ "/";
        }
         i = StringUtils.lastIndexOfAny(preFix, "/", "\\");
        if (i != preFix.length() -1){
            preFix = preFix+ "/";
        }
        //获得文件夹进行删除用
        Set<String> removeDir = new HashSet<String>();
        //作为image的结尾标识
        while (matcher.find()){
            String group = matcher.group();
            String localImagePath = preFix + group.split(imageServerFix)[1];
            File file = new File(localImagePath);
            if (file.exists()){
                file.delete();
            }
            //删除preFix的级别文件夹 为空则删除
            if (dirBankNum > 0){
                String[] split = localImagePath.split("/|\\\\");
                int length = split.length;
                for (int j = 1; j <= dirBankNum; j++) {
                    removeDir.add(localImagePath.split(split[length - j])[0]);
                }
            }
        }
        for (String dir : removeDir) {
            dir =dir.substring(0,dir.length()-1);
            File tem = new File(dir);
            File[] files = tem.listFiles();
            if (files.length == 0 ){
                tem.delete();
            }
        }
    }

    /**
     * 将内容中的url 提取出来,只会提取一个
     * @param str
     * @return
     */
    private static String regImgUrl(String str){
        Pattern pattern = Pattern.compile("[a-zA-z]+://[^\\s]*", Pattern.CASE_INSENSITIVE);
        Matcher matcher= pattern.matcher(str);
        while (matcher.find()){
            return matcher.group();
        }
        return  null;
    }

    /**
     * 新路劲的生成,会返回新生成的路劲 本地路劲    ： 服务器路劲
     */
    private static String[] fixValidate(String preFix,String imageServerFix,Boolean isNowDir){
        StringBuilder preFixBuilder = new StringBuilder();
        if (StringUtils.isBlank(imageServerFix)){
            imageServerFix = preFix;
        }
        StringBuilder imageServerBuilder = new StringBuilder();
        int i = StringUtils.lastIndexOfAny(preFix, "/", "\\");
        preFixBuilder.append(preFix);
        if (i != preFix.length() -1){
            preFixBuilder.append("/");
        }
        i = StringUtils.lastIndexOfAny(imageServerFix, "/", "\\");
        imageServerBuilder.append(imageServerFix);
        if (i != imageServerFix.length() -1){
            imageServerBuilder.append("/");
        }
        String dateFormat = DateUtils.getDateFormat(new Date(), dirFormat);
        if (isNowDir){
            preFixBuilder.append(dateFormat+"/");
            imageServerBuilder.append(dateFormat+"/");
        }
        String preFixPath = preFixBuilder.toString();
        String imageServerPath = imageServerBuilder.toString();
        File dir = new File(preFixPath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        return new String[]{preFixPath,imageServerPath};
    }

}

class UploadPicLocal implements Runnable{

    public UploadPicLocal(String netWorkPath, String savePath) {
        this.netWorkPath = netWorkPath;
        this.savePath = savePath;
    }

    // 网络路劲
    private String netWorkPath;
    // 保存路劲
    private String savePath;
    public void run() {
        try {
            // 构造URL
            URL url = new URL(netWorkPath);
            // 打开连接
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            //设置请求超时为5s
            con.setRequestMethod("GET");
            con.setConnectTimeout(5*1000);
            // 输入流
            InputStream is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024*2];
            // 读取到的数据长度
            int len;
            // 输出的文件流
            File sf=new File(savePath);
            if (!sf.getParentFile().exists()) {
                sf.getParentFile().mkdirs();
            }
            OutputStream os = new FileOutputStream(sf);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完毕，关闭所有链接
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
