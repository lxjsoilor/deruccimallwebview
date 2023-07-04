package com.derucci.deruccimallwebview.webview;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.derucci.deruccimallwebview.R;
import com.derucci.deruccimallwebview.config.KeyConfig;
import com.derucci.deruccimallwebview.utils.LocationUtils;
import com.derucci.deruccimallwebview.utils.Logger;
import com.derucci.deruccimallwebview.utils.SaveImageUtils;
import com.derucci.deruccimallwebview.utils.SoftKeyBoardListener;
import com.derucci.deruccimallwebview.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Author: 林雄军
 * Description: Description
 * Date: 2023/6/8
 */
public abstract class MallWebViewActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private MallWebView webView;
    FrameLayout mallBox;
    FrameLayout webViewBox;

    private MallWebChromeClient mallWebChromeClient = new MallWebChromeClient(this) {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mallNavBar.setTitle(title);
            if(title.contains("全屏")) {
                setFullScreen();
            } else {
                setNormalScreen();
                UiUtils.setNavigationBarBlack(activity);
            }
            if (webView.canGoBack()) {
                mallNavBar.setBackArrowVisibility(View.VISIBLE);
            } else {
                mallNavBar.setBackArrowVisibility(View.GONE);
            }
        }
    };

    private MallNavBar mallNavBar;
    private final WebViewClient webViewClient = new MallWebViewClient(this);
    private MallJSInterface mallJSInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_webview);
        initTheme();
        initView();
        initListener();
        initWebViewUrl();
        initListenerKeyBoard();
    }

    private void initListenerKeyBoard() {
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                Logger.i("键盘显示");
                mallJSInterface.setKeyBoardStatus("show");
            }

            @Override
            public void keyBoardHide(int height) {
                mallJSInterface.setKeyBoardStatus("hide");
                Logger.i("键盘隐藏");
            }
        });
    }

    private void initWebViewUrl() {
        String token = getToken();
        webView.loadUrl(getH5Url() + "?token=" + token +  "&env=" + getENVText());
    }

    // 初始化主题
    private void initTheme() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void initView() {
        webView = findViewById(R.id.webview);
        mallNavBar = findViewById(R.id.nav_bar);
        mallBox = findViewById(R.id.mall_activity_box);
        webViewBox = findViewById(R.id.webview_box);
    }

    private void initListener() {
        mallNavBar.setOnBackArrowClick(() -> webView.goBack());
        mallNavBar.setOnCloseClick(this::finish);
        webView.setWebChromeClient(mallWebChromeClient);
        webView.setWebViewClient(webViewClient);
        mallJSInterface = new MallJSInterface(this, webView);
        webView.addJavascriptInterface(mallJSInterface, KeyConfig.JS_CANAL);
    }

    // 设置沉浸式布局
    private void setFullScreen() {
        mallNavBar.setTitle("");
        mallNavBar.setBackground(Color.TRANSPARENT);
        mallNavBar.setPaddingTop(UiUtils.getStatusBarHeight(this));
        mallBox.setPadding(0,0,0,0);
        webViewBox.setPadding(0,0,0,0);
    };

    // 设置普通布局
    private void setNormalScreen() {
        mallNavBar.setBackground(Color.WHITE);
        mallNavBar.setPaddingTop(0);
        mallBox.setPadding(0,UiUtils.getStatusBarHeight(this),0,0);
        webViewBox.setPadding(0,UiUtils.dpToPx(this, 46),0,0);
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 侧滑返回操作
        if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

//    onRequestPermissionsResult


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // 本地存储和拍照
        if (requestCode == MallWebChromeClient.REQUEST_FILE_CAMERA_CODE_PERMISSIONS) {
            mallWebChromeClient.openFileChooserDialog();
        }
        // 保存图片到相册
        if (requestCode == SaveImageUtils.REQUEST_FILE_CODE) {
        }
        // 用户定位
        if (requestCode == LocationUtils.REQUEST_LOCATION_CODE) {
            mallJSInterface.getLocation();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // 本地存储和拍照
        if (requestCode == MallWebChromeClient.REQUEST_FILE_CAMERA_CODE_PERMISSIONS) {
            List<String> deniedPermissions = new ArrayList<>();
            deniedPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            deniedPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            deniedPermissions.add(Manifest.permission.CAMERA);
            if (EasyPermissions.somePermissionPermanentlyDenied(this, deniedPermissions)) {
                new AppSettingsDialog.Builder(this).setTitle("权限申请").setRationale("为了正常使用，请允许我们访问您的存储空间和相机功能").build().show();
            }
        }
        // 保存图片到相册
        if (requestCode == SaveImageUtils.REQUEST_FILE_CODE) {
            List<String> deniedPermissions = new ArrayList<>();
            deniedPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            deniedPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (EasyPermissions.somePermissionPermanentlyDenied(this, deniedPermissions)) {
                new AppSettingsDialog.Builder(this).setTitle("权限申请").setRationale("为了正常使用，请允许我们访问您的存储空间").build().show();
            }
        }
        // 用户定位
        if (requestCode == LocationUtils.REQUEST_LOCATION_CODE) {
            mallJSInterface.setEmptyLocation();
            List<String> deniedPermissions = new ArrayList<>();
            deniedPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            deniedPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (EasyPermissions.somePermissionPermanentlyDenied(this, deniedPermissions)) {
                new AppSettingsDialog.Builder(this).setTitle("权限申请").setRationale("为了正常使用，请允许我们访问您的位置信息").build().show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MallWebChromeClient.REQUEST_FILE_CAMERA_CODE) {
            if(resultCode == 0) {
                // 取消选择文件
                mallWebChromeClient.onReceiveNullValue();
            } else {
                // 选择文件
                mallWebChromeClient.onReceiveValue(data);
            }
        }
        // 获取token的回调
        if(requestCode == MallJSInterface.REQUEST_LOGIN_TOKEN_CODE) {
//            if (data == null) {
//                new Handler(Looper.getMainLooper()).post(() -> {
//                    webView.loadUrl("javascript:__setAppToken('')");
//                });
//                return;
//            }
//            String token = data.getStringExtra(KeyConfig.TOKEN_KEY);
//            new Handler(Looper.getMainLooper()).post(() -> {
//                webView.loadUrl("javascript:__setAppToken('" + token + "')");
//            });
        }
    }

    protected void setToken(String token) {
        if(!token.equals(KeyConfig.NULL_TOKEN)) {
            new Handler(Looper.getMainLooper()).post(() -> {
                webView.loadUrl("javascript:__setAppToken('" + token + "')");
            });
        }
    }

    // 获取登录界面
    protected abstract Intent getLoginIntent();

    // 获取token
    protected abstract String getToken();

    // 微信开放平台的appid
    protected abstract String getAppId();

    // 微信开放平台的appId
    protected abstract KeyConfig.ENV getENV();


    @Override
    protected void onResume() {
        setToken(getToken());
        super.onResume();
    }

    private String getENVText() {
        KeyConfig.ENV env = getENV();
        if (env == KeyConfig.ENV.DEV) {
            return "mall4Development";
        }
        if (env == KeyConfig.ENV.TEST) {
            return "test";
        }
        if (env == KeyConfig.ENV.UAT) {
            return "uat";
        }
        if (env == KeyConfig.ENV.PROD) {
            return "production";
        }
        return "";
    }
    private String getH5Url() {
        KeyConfig.ENV env = getENV();
        if (env == KeyConfig.ENV.DEV) {
            return KeyConfig.H5_URL_DEV;
        }

        if (env == KeyConfig.ENV.TEST) {
            return KeyConfig.H5_URL_TEST;
        }
        if (env == KeyConfig.ENV.UAT) {
            return KeyConfig.H5_URL_UAT;
        }
        return KeyConfig.H5_URL;
    }
}
