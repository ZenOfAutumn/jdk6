package thinkInJava;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by liuda on 2016/10/6.
 */
public class Chapter16_IO {
    public static String filePath = "D:\\JD";
    public static void main(String[] args){
        test_directoryListUtil();
    }
    //目录列表器
    public static void test_directoryListUtil(){
        File path = new File(".");
        String[] list;
        list = path.list(new DirFilter(filePath)); //File对象.list(文件名过滤器)
        Arrays.sort(list, String.CASE_INSENSITIVE_ORDER); //文件名排序
        for(String dirItem : list){
            System.out.println(dirItem);
        }
    }
}
//过滤器
class DirFilter implements FilenameFilter{       //jdk自带接口，FilenameFilter
    private Pattern pattern;
    public DirFilter(String regex){
        pattern = Pattern.compile(regex);
    }
    public boolean accept(File dir, String name){
        return pattern.matcher(name).matches();
    }
}

