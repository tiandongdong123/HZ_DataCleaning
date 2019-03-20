package com.wanfang.datacleaning.handler.util.business;

import com.wanfang.datacleaning.handler.constant.LoggerEnum;
import com.wanfang.datacleaning.handler.model.bo.UsCreditCodeBO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/12 15:54 
 *  @Version  V1.0   
 */
public class UsCreditCodeDataUtils {

    private static final Logger abnormalDataLogger = LoggerFactory.getLogger(LoggerEnum.ABNORMAL_CODE_DATA.getValue());

    private static Map<String, String> usCreditCodeMap = new HashMap<>(16);

    /**
     * 正则表达式：统一社会信用代码
     */
    private static final String REGEX_UNIFIED_SOCIAL_CREDIT_CODE = "^[A-Z0-9]{18}$";

    private UsCreditCodeDataUtils() {
    }

    /**
     * 缓存统一社会信用代码信息
     *
     * @param creditCodeBOList
     */
    public static void cacheUsCreditCodeMap(List<UsCreditCodeBO> creditCodeBOList) {
        if (creditCodeBOList != null && creditCodeBOList.size() > 0) {
            String pripId;
            String usCreditCode;
            for (UsCreditCodeBO creditCodeBO : creditCodeBOList) {
                // 判断主体身份代码是否为空
                if (creditCodeBO == null || StringUtils.isBlank(creditCodeBO.getPripid())) {
                    abnormalDataLogger.warn("creditCodeBO：【{}】，主体身份代码为空", creditCodeBO);
                    continue;
                }
                // 判断主体身份代码是否已存在
                pripId = StringUtils.deleteWhitespace(creditCodeBO.getPripid());
                if (usCreditCodeMap.containsKey(pripId)) {
                    abnormalDataLogger.warn("creditCodeBO：【{}】，主体身份代码已存在", creditCodeBO);
                    continue;
                }
                // 判断统一社会信用代码是否符合标准格式
                usCreditCode = StringUtils.deleteWhitespace(creditCodeBO.getUsCreditCode());
                if (!isUnifiedSocialCreditCode(usCreditCode)) {
                    abnormalDataLogger.warn("creditCodeBO：【{}】，统一社会信用代码不符合标准格式", creditCodeBO);
                    continue;
                }

                usCreditCodeMap.put(pripId, usCreditCode);
            }
        }
    }

    /**
     * 获取缓存统一社会信用代码信息数量
     *
     * @return int
     */
    public static int getCacheUsCreditCodeSize() {
        return usCreditCodeMap.size();
    }

    /**
     * 通过主体身份代码获取缓存的统一社会信用代码信息
     *
     * @param pripId 主体身份代码
     * @return String
     */
    public static String getCacheUsCreditCode(String pripId) {
        return usCreditCodeMap.get(pripId);
    }

    /**
     * 通过统一社会信用代码获取组织结构代码
     *
     * @param usCreditCode 统一社会信用代码
     * @return String 若usCreditCode为null,则返回null
     */
    public static String getOrgCodeByUsCreditCode(String usCreditCode) {
        return StringUtils.substring(usCreditCode, 8, 17);
    }

    /**
     * 判断是否为统一社会信用代码
     *
     * @param uniSocialCreditCode 统一社会信用代码
     * @return boolean
     */
    public static boolean isUnifiedSocialCreditCode(String uniSocialCreditCode) {
        return Pattern.matches(REGEX_UNIFIED_SOCIAL_CREDIT_CODE, uniSocialCreditCode);
    }
}
