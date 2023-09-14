package com.Summer;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PrivateKey;

/**
 * Unit test for simple App.
 */

public class NativePayTest {

    private CloseableHttpClient httpClient;


    @BeforeEach
    public void setup() throws IOException {
        // 加载商户私钥（privateKey：私钥字符串）
        PrivateKey merchantPrivateKey = PemUtil
                // 私钥
                .loadPrivateKey(new ByteArrayInputStream(privateKey.getBytes("utf-8")));

        // 加载平台证书（mchId：商户号,mchSerialNo：商户证书序列号,apiV3Key：V3密钥）
        AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
                // 商户id，商户的序列号
                new WechatPay2Credentials(mchId, new PrivateKeySigner(mchSerialNo, merchantPrivateKey)), apiV3Key.getBytes("utf-8"));

        // 初始化httpClient
        httpClient = WechatPayHttpClientBuilder.create()
                // 商户的id，商户的证书
                .withMerchant(mchId, mchSerialNo, merchantPrivateKey)
                .withValidator(new WechatPay2Validator(verifier)).build();
    }

    @AfterEach
    public void after() throws IOException {
        httpClient.close();
    }


    /**
     * 下单
     * @throws Exception
     */
    public void CreateOrder() throws Exception {
        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/native");
        // 请求body参数
        String reqdata = "{"
                + "\"time_expire\":\"2018-06-08T10:34:56+08:00\","
                + "\"amount\": {"
                + "\"total\":100,"
                + "\"currency\":\"CNY\""
                + "},"
                + "\"mchid\":\"1230000109\","
                + "\"description\":\"Image形象店-深圳腾大-QQ公仔\","
                + "\"notify_url\":\"https://www.weixin.qq.com/wxpay/pay.php\","
                + "\"out_trade_no\":\"1217752501201407033233368018\","
                + "\"goods_tag\":\"WXG\","
                + "\"appid\":\"wxd678efh567hg6787\","
                + "\"attach\":\"自定义数据说明\","
                + "\"detail\": {"
                + "\"invoice_id\":\"wx123\","
                + "\"goods_detail\": ["
                + "{"
                + "\"goods_name\":\"iPhoneX 256G\","
                + "\"wechatpay_goods_id\":\"1001\","
                + "\"quantity\":1,"
                + "\"merchant_goods_id\":\"商品编码\","
                + "\"unit_price\":828800"
                + "},"
                + "{"
                + "\"goods_name\":\"iPhoneX 256G\","
                + "\"wechatpay_goods_id\":\"1001\","
                + "\"quantity\":1,"
                + "\"merchant_goods_id\":\"商品编码\","
                + "\"unit_price\":828800"
                + "}"
                + "],"
                + "\"cost_price\":608800"
                + "},"
                + "\"scene_info\": {"
                + "\"store_info\": {"
                + "\"address\":\"广东省深圳市南山区科技中一道10000号\","
                + "\"area_code\":\"440305\","
                + "\"name\":\"腾讯大厦分店\","
                + "\"id\":\"0001\""
                + "},"
                + "\"device_id\":\"013467007045764\","
                + "\"payer_client_ip\":\"14.23.150.211\""
                + "}"
                + "}";
        StringEntity entity = new StringEntity(reqdata, "utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        //完成签名并执行请求
        CloseableHttpResponse response = httpClient.execute(httpPost);

        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) { //处理成功
                System.out.println("success,return body = " + EntityUtils.toString(response.getEntity()));
            } else if (statusCode == 204) { //处理成功，无返回Body
                System.out.println("success");
            } else {
                System.out.println("failed,resp code = " + statusCode + ",return body = " + EntityUtils.toString(response.getEntity()));
                throw new IOException("request failed");
            }
        } finally {
            response.close();
        }
    }
}
