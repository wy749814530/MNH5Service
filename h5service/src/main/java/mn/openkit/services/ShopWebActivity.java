package mn.openkit.services;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import mn.openkit.key.Parameter;
import mn.openkit.utils.StatusUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import mn.openkit.services.R;
/**
 * Created by WIN on 2017/12/19.
 */

public class ShopWebActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout rlTitleLay;
    ProgressBar webPro;
    LinearLayout layAdd;
    TextView tvTitle;
    ImageView ivBack;
    TextView webClose;
    TextView tvRights;


    private WebView webView;

    // 内部处理参数
    private String UrlTip = "";
    private String FailUrl = "";
    private String SuccUrl = "";
    private String tipMall = "";
    private String tipPay = "";
    private String tipUrl = "";
    private String mUrl = "";
    private String argument;
    private String argument1 = "";
    private String language = "us";
    private String loaclDeviceId = "";
    private boolean isPro = false;
    private boolean isgoFirst = false;

    // 外部传入参数
    private String deviceId;
    private boolean isFourG = false;
    private boolean isMobile = false;
    private boolean isReceive = false; // 是否是领取免费云存，false：已经有云存套餐
    private boolean isSupport24Record = false; // 是否支持24小时云存储
    private boolean isNetworkMonitoring = false;// 是否是4G网络监测
    private int serviceType = 0;// 云存储：1，4G：2 ，人脸库3
    private int receiveType = 0;
    private String H5_HOST = "";
    private String APP_KEY = "";
    private String APP_SECRET = "";
    private String APP_ACCESS_TOKEN = "";
    private String USER_ID = "";
    private String DEVICE_ID = "";
    private String DEVICE_NAME = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_web);
        rlTitleLay = findViewById(R.id.rl_title_lay);
        webPro = findViewById(R.id.web_pro);
        layAdd = findViewById(R.id.lay_add);
        tvTitle = findViewById(R.id.tv_title);
        ivBack = findViewById(R.id.iv_back);
        webClose = findViewById(R.id.web_close);
        tvRights = findViewById(R.id.tv_rights);

        StatusUtils.setFullScreenStatur(this);
        StatusUtils.setLightStatusBarIcon(this, true);
        StatusUtils.setPaddingSmart(this, rlTitleLay);

        ivBack.setOnClickListener(this);
        webClose.setOnClickListener(this);
        tvRights.setOnClickListener(this);
        initData();
        initUrl();
        initWebView();
    }

    private void initData() {
        deviceId = getIntent().getStringExtra(Parameter.STR_DEVICE_ID); // 设备ID
        loaclDeviceId = deviceId;
        serviceType = getIntent().getIntExtra(Parameter.INT_SERVICE_TYPE, 0);
        isFourG = getIntent().getBooleanExtra(Parameter.B_IS_4G_TRAFFIC_QUERY, false);//
        isMobile = getIntent().getBooleanExtra(Parameter.B_IS_MOBILE_CENTER, false);//
        isSupport24Record = getIntent().getBooleanExtra(Parameter.B_IS_SUPPORT_24_CLOUD, false);//
        isReceive = getIntent().getBooleanExtra(Parameter.B_IS_RECEIVE_CLOUD, false);
        isNetworkMonitoring = getIntent().getBooleanExtra(Parameter.B_IS_NETWORK_MONOTORING, false);
        receiveType = getIntent().getIntExtra(Parameter.INT_RECEIVE_TYPE, 0);

        H5_HOST = getIntent().getStringExtra(Parameter.STR_H5_HOST);
        APP_KEY = getIntent().getStringExtra(Parameter.STR_APP_KEY);
        APP_SECRET = getIntent().getStringExtra(Parameter.STR_APP_SECRET);
        APP_ACCESS_TOKEN = getIntent().getStringExtra(Parameter.STR_APP_ACCESS_TOKEN);
        USER_ID = getIntent().getStringExtra(Parameter.STR_USER_ID);
        DEVICE_ID = getIntent().getStringExtra(Parameter.STR_DEVICE_ID);
        DEVICE_NAME = getIntent().getStringExtra(Parameter.STR_DEVICE_NAME);

        if (isFourG) {
            setTvTitle(getString(R.string.four_g));
            setRight(getString(R.string.dev_fourg_total));
        } else if (isMobile) {
            setRight(getString(R.string.web_order));
            setTvTitle(getString(R.string.tool_mobile));
        } else if (isNetworkMonitoring) {
            setRightVisibility(View.GONE);
            setTvTitle(getString(R.string.network_monitoring_4G));
        } else {
            setRight(getString(R.string.web_order));
            setTvTitle(getString(R.string.web_title));
        }
    }

    private void initUrl() {
        UrlTip = H5_HOST + "/appv2";//新(备注2.0用v2)
        FailUrl = UrlTip + "/index.html#/fail";
        SuccUrl = UrlTip + "/index.html#/paysuccess";
        tipMall = UrlTip + "/index.html#/";
        tipPay = UrlTip + "/index.html?language=cn#/mobile/pay/";

        language = Locale.getDefault().getLanguage();
        if (language.equals("zh")) {
            mUrl = UrlTip + "/index.html?language=cn";
        } else if (language.equals("de")) {
            mUrl = UrlTip + "/index.html?language=de";
        } else {
            mUrl = UrlTip + "/index.html?language=us";
        }
        tipUrl = mUrl;
    }

    private void initWebView() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView = new WebView(getApplicationContext());
        webView.setLayoutParams(params);
        layAdd.addView(webView);
        //支持Js
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //设置默认编码
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        //开启数据库功能
        webView.getSettings().setDatabaseEnabled(true);
        //开启web缓存功能
        webView.getSettings().setAppCacheEnabled(true);
        // 设置缓冲大小，我设的是8M
        webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        //设置数据库db文件目录
        // webView.getSettings().setDatabasePath(extStorageAppBasePath.toString()); // API 19 deprecated
        //设置缓存路径
        //webView.getSettings().setAppCachePath(extStorageAppCachePath.toString());
        //设置是否开启DOM存储API权限，默认false，未开启，设置为true，WebView能够使用DOM storage API
        webView.getSettings().setDomStorageEnabled(true);
        // 是否可访问Content Provider的资源，默认值 true
        webView.getSettings().setAllowContentAccess(true);
        //设置在WebView内部是否允许访问文件，默认允许访问。
        webView.getSettings().setAllowFileAccess(true);
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        //设置缓存模式
        if (isNetworkAvailable()) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        } else {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//没网，则从本地获取，即离线加载
        }
        //设置WebView是否使用viewport，当该属性被设置为false时，加载页面的宽度总是适应WebView控件宽度；
        //当被设置为true，当前页面包含viewport属性标签，在标签中指定宽度值生效，如果页面不包含viewport标签，
        //无法提供一个宽度值，这个时候该方法将被使用。
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        //必须设置setWebviewclient  自定义的继承于webviewclient的类就是用来拦截url处理一些与H5交互的时候的逻辑
        webView.setWebChromeClient(new MyWebViewClient());
        webView.loadUrl(mUrl);
        //传给js的参数，第一个页面加载结束之后进行js调用进行注册
        getArgument();
        webView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = VERSION_CODES.KITKAT)
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                isPro = false;
                isgoFirst = false;
                try {
                    if (url.contains("manniu:///closeWebView")) {//点了完成就关闭H5
                        finish();
                        return true;
                    }
                    if (url.contains("setFirstPage:title:///1")) {//网页首页
                        if (isNetworkMonitoring) {
                            setRightVisibility(View.GONE);
                        } else {
                            setRightVisibility(View.VISIBLE);
                        }
                        setRight(getString(R.string.web_order));
                        String s = url.split("///")[3];
                        try {
                            String decode = URLDecoder.decode(s, "UTF-8");
                            setTvTitle(decode);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        //  setTvTitle(getString(R.string.web_title));
                        return true;
                    }
                    if (url.contains("manniu:///setFirstPage:title:///2") || url.contains("manniu:///setFirstPage:title:///3")) {//manniu:///setFirstPage:title:///2///%E6%94%AF%E4%BB%98%E8%AF%A6%E6%83%85
                        //做处理跳到首页
                        isgoFirst = true;
                        setRightVisibility(View.GONE);
                        String s = url.split("///")[3];
                        try {
                            String decode = URLDecoder.decode(s, "UTF-8");
                            setTvTitle(decode);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    if (url.contains("///setFirstPage:title:///0")) {//网页其他页面
                        // manniu:///setFirstPage:title:///1///%E4%BA%91%E5%AD%98%E5%82%A8%E6%9C%8D%E5%8A%A1
                        setRightVisibility(View.GONE);
                        String s = url.split("///")[3];
                        try {
                            String decode = URLDecoder.decode(s, "UTF-8");
                            setTvTitle(decode);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    if (url.contains("manniu:///activateCamera///iccid")) {
                        goRQ();
                        return true;
                    }
                    if (url.contains("manniu:///activateCamera///QRCode")) {
                        goRQ();
                        return true;
                    }
                    //支付宝支付
                    if (url.startsWith("https://mclient.alipay.com/cashier/mobilepay.htm?")) {//这个才是手机网站支付的请求URL，截取，能够帮助Native-H5混合App以极低的接入成本极大地提升支付成功率
                        final PayTask task = new PayTask(ShopWebActivity.this);
                        boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
                            @Override
                            public void onPayResult(H5PayResultModel h5PayResultModel) {
                                // 支付结果返回
                                final String returnUrl = h5PayResultModel.getReturnUrl();
                                final String resultCode = h5PayResultModel.getResultCode();
                                /*
                                 * 返回码，标识支付状态，含义如下：
                                 * 9000——订单支付成功
                                 * 8000——正在处理中
                                 * 4000——订单支付失败
                                 * 5000——重复请求
                                 * 6001——用户中途取消
                                 * 6002——网络连接出错
                                 */
                                if (!TextUtils.isEmpty(returnUrl)) {
                                    ShopWebActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            toastBottomShow(getString(R.string.pay_succ));
                                            view.loadUrl(SuccUrl);//跳到支付成功页面（H5)
                                        }
                                    });
                                } else {
                                    ShopWebActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (resultCode.equals("6001")) {
                                                toastBottomShow(getString(R.string.pay_faild));
                                                view.loadUrl(FailUrl);//跳到支付失败页面（H5)
                                            } else if (resultCode.equals("4000")) {
                                                toastBottomShow(getString(R.string.pay_dngdanfied));
                                                view.loadUrl(FailUrl);
                                            } else if (resultCode.equals("5000")) { //重复请求
                                                view.loadUrl(FailUrl);
                                            } else if (resultCode.equals("6002")) {
                                                toastBottomShow(getString(R.string.pay_netpoor));
                                                view.loadUrl(FailUrl);
                                            } else if (resultCode.equals("9000")) {
                                                toastBottomShow(getString(R.string.pay_succ));
                                                view.loadUrl(SuccUrl);
                                            }
                                        }
                                    });

                                }
                            }
                        });
                        if (!isIntercepted) {
                            view.loadUrl(url);
                        }
                        return true;
                    }
                    //支付宝支付，唤起支付宝客户端支付页面（防止没有被拦截继续走这里）
                    if (url.startsWith("alipays") || url.startsWith("alipay")) {
                        try {
                            Uri uri = Uri.parse(url);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    //微信支付
                    if (url.startsWith("https://wx.tenpay.com/")) {
                        if (("4.4.3".equals(VERSION.RELEASE))
                                || ("4.4.4".equals(VERSION.RELEASE))) {
                            //兼容这两个版本设置referer无效的问题
                            view.loadDataWithBaseURL(H5_HOST,
                                    "<script>window.location.href=\"" + url + "\";</script>",
                                    "textml", "utf-8", null);
                        } else {
                            Map<String, String> extraHeaders = new HashMap<String, String>();
                            extraHeaders.put("Referer", H5_HOST);
                            view.loadUrl(url, extraHeaders);
                        }
                        return true;
                    }
                    //唤起微信客户端支付页面
                    if (url.startsWith("weixin://wap/pay?")) {
                        Uri uri = Uri.parse(url);

                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        return true;
                    }
                    if (!(url.startsWith("http") || url.startsWith("https"))) {
                        return true;
                    }
                    if (url.startsWith("https") || (url.startsWith("http"))) {
                        view.loadUrl(url);
                        mUrl = url;
                        return true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                isPro = true;
            }

            @RequiresApi(api = VERSION_CODES.KITKAT)
            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("mobile/pay/1")) {
                    deviceId = loaclDeviceId;
                    getArgument();
                }
                if (url.contains("http")) {
                    mUrl = url;//为了刷新此处需要定位当前url
                }
                if (url.equals(tipUrl + "#/")) {//一些信息传给js，获取信息订单
                    getArgument();
                    if (VERSION.SDK_INT < 18) {
                        webView.loadUrl(argument);
                    } else {
                        webView.evaluateJavascript(argument, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {

                            }
                        });
                    }
                }
                super.onPageFinished(view, url);
                if (isPro) {
                    webPro.setVisibility(View.GONE);
                }
            }
        });
    }


    private String getArgument() {
        if (isReceive) {
            int type = isSupport24Record ? 2 : 1;
            if (receiveType == 5) {
                argument = "$_free_package(" + "'" + APP_KEY + "'" + "," + "'"
                        + APP_SECRET + "'" + "," + "'"
                        + APP_ACCESS_TOKEN + "'" + "," + "'"
                        + DEVICE_ID + "'" + "," + "'"
                        + type + "'" + "," + "'"
                        + receiveType + "'" + ")";
            } else {
                argument = "$_free_package(" + "'" + APP_KEY + "'" + "," + "'"
                        + APP_SECRET + "'" + "," + "'"
                        + APP_ACCESS_TOKEN + "'" + "," + "'"
                        + DEVICE_ID + "'" + "," + "'"
                        + type + "'" + ")";
            }

        } else if (isFourG) {
            argument = "activateTrafficDetail(" + "'" + APP_KEY + "'" + "," + "'"
                    + APP_SECRET + "'" + "," + "'"
                    + APP_ACCESS_TOKEN + "'" + "," + "'"
                    + USER_ID + "'" + "," + "'"
                    + DEVICE_ID + "'" + "," + "'"
                    + DEVICE_NAME + "'" + ")";
        } else if (isMobile) {
            argument = "$_mobile_center(" + "'" + APP_KEY + "'" + "," + "'"
                    + APP_SECRET + "'" + "," + "'"
                    + APP_ACCESS_TOKEN + "'" + "," + "'"
                    + USER_ID + "'" + ")";

        } else if (isNetworkMonitoring) {
            argument = "$_card_network(" + "'" + APP_KEY + "'" + "," + "'"
                    + APP_SECRET + "'" + "," + "'"
                    + APP_ACCESS_TOKEN + "'" + ")";
        } else {
            if (deviceId == null || TextUtils.isEmpty(deviceId) || "".equals(deviceId)) {
                argument = "contactapp(" + "'" + APP_KEY + "'" + "," + "'"
                        + APP_SECRET + "'" + "," + "'"
                        + APP_ACCESS_TOKEN + "'" + "," + "'"
                        + USER_ID + "'" + "," + "''"
                        + "," + "'"
                        + serviceType + "'" + ")";
            } else {//空的就不带给前端了
                argument = "contactapp(" + "'" + APP_KEY + "'" + "," + "'"
                        + APP_SECRET + "'" + "," + "'"
                        + APP_ACCESS_TOKEN + "'" + ","
                        + "'" + USER_ID + "'" + "," + "'"
                        + deviceId + "'" + "," + "'" + serviceType + "'" + ")";
            }
        }

        return argument;
    }


    //去二维码
    private void goRQ() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1000);
        } else {
            Intent intent = new Intent(ShopWebActivity.this, ScanQRcodeActivity.class);
            intent.putExtra("gotype", 1);
            startActivityForResult(intent, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(ShopWebActivity.this, ScanQRcodeActivity.class);
                intent.putExtra("gotype", 1);
                startActivityForResult(intent, 100);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1000);
            }
        }
    }


    //二维码扫描之后的参数交互刷新webView的最新界面
    private String getQrArgument(String ccid) {
        /* $_mobile_center(appkey,appsecret,accesstoken,用户名,iccid卡号)*/
        if (isNetworkMonitoring) {
            argument1 = "$_card_network(" + "'" + APP_KEY + "'" + "," + "'"
                    + APP_SECRET + "'" + "," + "'"
                    + APP_ACCESS_TOKEN + "'" + "," + "'"
                    + ccid + "'" + ")";
        } else {
            argument1 = "$_mobile_center(" + "'" + APP_KEY + "'" + "," + "'"
                    + APP_SECRET + "'" + "," + "'"
                    + APP_ACCESS_TOKEN + "'" + "," + "'"
                    + USER_ID + "'" + "," + "'"
                    + ccid + "'" + ")";
        }

        return argument1;
    }

    @RequiresApi(api = VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 100) {
            String snResult = data.getStringExtra("snResult");
            String qrArgument = getQrArgument(snResult);
            if (VERSION.SDK_INT < 18) {
                webView.loadUrl(qrArgument);
            } else {
                webView.evaluateJavascript(qrArgument, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            CookieSyncManager.createInstance(ShopWebActivity.this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            CookieSyncManager.getInstance().sync();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearCache(true);
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();

    }


    //监听手机物理返回键并设置
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            //webview调用goback后会返回到上一次的浏览页面，跟PC端浏览器的后腿是一样的道理
            if (isGtotal) {
                isGtotal = false;
                setTvTitle(getString(R.string.four_g));
                setRight(getString(R.string.dev_fourg_total));
                setRightVisibility(View.VISIBLE);
                webView.goBack();
            } else if (isFourG && getTitleTv().equals(getString(R.string.tool_mobile))) {
                webView.goBack();
                isFourG = false;
            } else if (getTitleTv().equals(getString(R.string.web_title))
                    || getTitleTv().equals(getString(R.string.four_g))
                    || getTitleTv().equals(getString(R.string.network_monitoring_4G))
                    || getTitleTv().equals(getString(R.string.tool_mobile))
                    || getTitleTv().equals(getString(R.string.dev_freeget))) {
                finish();
            } else if (isgoFirst || mUrl.contains(tipMall)) {
                if (language.equals("zh")) {
                    webView.loadUrl(UrlTip + "/index.html?language=cn");
                } else {
                    webView.loadUrl(UrlTip + "/index.html?language=us");
                }
            } else {
                deviceId = null;
                if (mUrl.contains(tipPay)) {
                    finish();
                }
                webView.goBack();
            }
            return true;
        } else {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    boolean isGtotal;

    public void onRightTitleItemClock() {
        if (isFourG) {//4G-->我的套餐
            if (getString(R.string.four_g).equals(getTitleTv())) {
                webView.loadUrl(UrlTip + "/#/traffic/pkg?device_id=" + DEVICE_ID);
                isGtotal = true;
                setRightVisibility(View.GONE);
                setTvTitle(getString(R.string.dev_fourg_total));
            } else if (VERSION.SDK_INT > 18) {
                webView.post(new Runnable() {
                    @RequiresApi(api = VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        webView.evaluateJavascript("orderlook()", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {

                            }
                        });
                    }
                });
            } else {
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("orderlook()");
                    }
                });

            }
        } else if (VERSION.SDK_INT > 18) {
            webView.post(new Runnable() {
                @RequiresApi(api = VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    webView.evaluateJavascript("orderlook()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                        }
                    });
                }
            });
        } else {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl("orderlook()");
                }
            });

        }
    }

    public void onBackClick() {
        //webview调用goback后会返回到上一次的浏览页面，跟PC端浏览器的后腿是一样的道理
        if (isGtotal) {
            isGtotal = false;
            setTvTitle(getString(R.string.four_g));
            setRight(getString(R.string.dev_fourg_total));
            setRightVisibility(View.VISIBLE);
            webView.goBack();
        } else if (isFourG && getTitleTv().equals(getString(R.string.tool_mobile))) {
            isFourG = false;
            webView.goBack();
        } else if (getTitleTv().equals(getString(R.string.web_title))
                || getTitleTv().equals(getString(R.string.four_g))
                || getTitleTv().equals(getString(R.string.network_monitoring_4G))
                || getTitleTv().equals(getString(R.string.tool_mobile))
                || getTitleTv().equals(getString(R.string.dev_freeget))) {
            finish();
        } else if (isgoFirst || mUrl.contains(tipMall)) {
            if (language.equals("zh")) {
                webView.loadUrl(UrlTip + "/index.html?language=cn");
            } else {
                webView.loadUrl(UrlTip + "/index.html?language=us");
            }
        } else {
            deviceId = null;
            if (mUrl.contains(tipPay)) {
                finish();
            }
            webView.goBack();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_back) {
            onBackClick();
        } else if (i == R.id.web_close) {
            finish();
        } else if (i == R.id.tv_rights) {
            onRightTitleItemClock();
        }
    }

    public void setTvTitle(String str) {
        tvTitle.setText(str);
    }

    public String getTitleTv() {
        String text = tvTitle.getText().toString();
        return text;
    }

    public void setRight(String str) {
        tvRights.setText(str);
    }

    public void setRightVisibility(int visibility) {
        tvRights.setVisibility(visibility);
    }

    Toast mToast = null;

    public void toastBottomShow(String msg) {
        try {

            if (mToast == null) {
                //防止内存泄露所使用的context和（通过判断是否为空）防止多次new对象增加堆内存和多次弹出显示问题
                mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
                mToast.setText(msg);
            } else {
                mToast.setText(msg);
            }
            mToast.setGravity(Gravity.BOTTOM, 0, 175);
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //自定义的webviewclient，用来拦截url处理一些与H5交互的时候的逻辑等
    class MyWebViewClient extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                webPro.setVisibility(View.GONE);
            } else {
                if (webPro.getVisibility() == View.GONE) {
                    webPro.setVisibility(View.VISIBLE);
                }
                webPro.setProgress(newProgress);
            }
        }
    }

    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager == null) {
                return false;
            }
            NetworkInfo netinfo = manager.getActiveNetworkInfo();
            if (netinfo != null) {
                if (netinfo.isConnected()) {
                    if (netinfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        return true;
                    } else if (netinfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
