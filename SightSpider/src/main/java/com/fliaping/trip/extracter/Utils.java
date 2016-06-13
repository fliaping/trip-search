package com.fliaping.trip.extracter;


import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Payne on 5/31/16.
 */
public class Utils {

    /**
     * 将文本文件中的内容读入到buffer中
     * @param buffer buffer
     * @param filePath 文件路径
     * @throws IOException 异常
     */
    public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        is.close();
    }

    /**
     * 读取文本文件内容
     * @param filePath 文件所在路径
     * @return 文本内容
     * @throws IOException 异常
     */
    public static String readFile(String filePath) throws IOException {
        StringBuffer sb = new StringBuffer();
        Utils.readToBuffer(sb, filePath);
        return sb.toString();
    }

    public static String arrayToString(String array[]){
        if(array == null) return "";
        String string = "";
        for (int i = 0; i < array.length; i++) {
            if(i == array.length -1){
                string += array[i];
            }else {
                string += array[i]+"^";
            }
        }
        return string;
    }

    public static String[] listToArray(List<String> list){
        String[] string = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            string[i] = list.get(i);
        }
        return string;
    }

    public static String filterEmoji(String source) {
        if(source != null)
        {
            Pattern emoji = Pattern.compile ("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",Pattern.UNICODE_CASE | Pattern . CASE_INSENSITIVE ) ;
            Matcher emojiMatcher = emoji.matcher(source);
            if ( emojiMatcher.find())
            {
                source = emojiMatcher.replaceAll("");
                return source ;
            }
            return source;
        }
        return source;
    }
}
