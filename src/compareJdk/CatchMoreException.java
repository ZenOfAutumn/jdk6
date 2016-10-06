package compareJdk;

import java.io.*;
import java.nio.Buffer;

/**
 * Created by liuda on 2016/10/4.
 */
public class CatchMoreException {
    public static void main(String[] args)throws Exception{
        try {
            BufferedReader in = new BufferedReader(new FileReader("D://test.txt"));
            System.out.println(in.readLine());
        }catch(IOException ex){ //jdk7ø… π”√£∫catch(IOException |NullPointerException ex){
            if(ex.equals("ioException")){
                throw new IOException();
            }else{
                throw new NullPointerException();
            }
        }
    }
}
