import org.junit.Test;

import java.util.regex.Pattern;

/**
 *  
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/11/29 10:43 
 *  @Version  V1.0   
 */
public class TestDemo {

    @Test
    public void chineseTest() {
        Pattern chinesePattern = Pattern.compile("[\u4e00-\u9fa5]+");
        String text = "上海市徐汇区天平路91弄2号";

        System.out.println("text：【" + text + "】,result：【" + chinesePattern.matcher(text).find() + "】");
    }
}
