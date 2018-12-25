import com.wanfang.datacleaning.handler.util.business.YangZhouDevZoneUtils;
import org.junit.Test;

/**
 * @author yifei
 * @date 2018/12/16
 */
public class DemoTest {

    @Test
    public void isInPolygon() {
        boolean inFlag = YangZhouDevZoneUtils.isInDevZone(119.3381040624, 32.3284646961);
        System.out.println("isInDevZone结果:" + inFlag);
    }
}
