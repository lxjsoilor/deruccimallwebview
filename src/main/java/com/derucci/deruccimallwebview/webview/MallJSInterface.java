package com.derucci.deruccimallwebview.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.derucci.deruccimallwebview.R;
import com.derucci.deruccimallwebview.config.KeyConfig;
import com.derucci.deruccimallwebview.utils.LocationUtils;
import com.derucci.deruccimallwebview.utils.SaveImageUtils;
import com.derucci.deruccimallwebview.utils.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Author: 林雄军
 * Description: Description
 * Date: 2023/6/8
 */
public class MallJSInterface {
    public static int REQUEST_LOGIN_TOKEN_CODE = 110;
    private final MallWebViewActivity activity;
    private final WebView webView;
    public MallJSInterface(MallWebViewActivity activity, WebView webView) {
        this.activity = activity;
        this.webView = webView;
    }

    //APP转发微信小程序到微信好友：
    @JavascriptInterface
    public void  shareAppMessage(String jsonStr){
        Log.i("调用原生方法","APP转发微信小程序到微信好友:"+ jsonStr);
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            String userName = jsonObj.getString("userName");
            String path = jsonObj.getString("path");
            String title = jsonObj.getString("title");
            String imageUrl = jsonObj.getString("imageUrl");
            shareToMiniProgram(
                    userName,
                    path,
                    title,
                    imageUrl
            );
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private void shareToMiniProgram(String userName, String path, String title, String imageUrl) throws IOException {
        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
        miniProgramObj.miniprogramType = WXMiniProgramObject.MINIPROGRAM_TYPE_PREVIEW;
        miniProgramObj.webpageUrl = "http://www.qq.com";
        miniProgramObj.userName = userName;
        miniProgramObj.path = path;
        WXMediaMessage wxMediaMessage = new WXMediaMessage(miniProgramObj);
        wxMediaMessage.title = title;
        wxMediaMessage.description = "";
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "miniProgram";
        wxMediaMessage.thumbData = getBytesByImageUrl(imageUrl);;
        req.message = wxMediaMessage;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        IWXAPI wxApi = WXAPIFactory.createWXAPI(activity, activity.getAppId(), true);
        wxApi.sendReq(req);
    }

    private byte[] getBytesByImageUrl(String imageUrl) throws IOException {
        URL url = new URL(imageUrl + "?x-oss-process=image/resize,h_200,w_250");
        InputStream is = url.openStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        return byteArrayOutputStream.toByteArray();
    }

    //原生实现海报转发
    @JavascriptInterface
    public void sharePosterMessage(String jsonStr){
        Log.i("调用原生方法","原生实现海报转发:"+ jsonStr);
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //原生实现海报转发
    @JavascriptInterface
    public void openMapApp(String jsonStr){
        Log.i("调用原生方法","跳转到高德地图APP导航:" + jsonStr);
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            String shopName = jsonObj.getString("shopName");
            double latitude = jsonObj.getDouble("latitude");
            double longitude = jsonObj.getDouble("longitude");
            String app = jsonObj.getString("app");
            if (app.equals("amap")) {
                openGaoDeMap(latitude, longitude, shopName);
            }
            if (app.equals("baidu")) {
                openBaiduMap(latitude, longitude, shopName);
            }
            if (app.equals("tencent")) {
                openTencentMap(latitude, longitude, shopName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openGaoDeMap(double latitude, double longitude, String shopName) {
        if (checkMapAppsIsExist(activity, "com.autonavi.minimap")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.autonavi.minimap");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("androidamap://route?sourceApplication=" + R.string.app_name
                    + "&sname=我的位置&dlat=" + latitude
                    + "&dlon=" + longitude
                    + "&dname=" + shopName
                    + "&dev=0&m=0&t=0"));
            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, "高德地图未安装", Toast.LENGTH_LONG).show();
        }
    }

    private void openBaiduMap(double latitude, double longitude, String shopName) {
        if (checkMapAppsIsExist(activity, "com.baidu.BaiduMap")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("baidumap://map/direction?origin=我的位置&destination=name:"
                    + shopName
                    + "|latlng:" + latitude + "," + longitude
                    + "&mode=driving&sy=3&index=0&target=1"));
            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, "百度地图未安装", Toast.LENGTH_LONG).show();
        }
    }

    private void openTencentMap(double latitude, double longitude, String shopName) {
        if (checkMapAppsIsExist(activity, "com.tencent.map")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("qqmap://map/routeplan?type=drive&from=我的位置&fromcoord=0,0"
                    + "&to=" + shopName
                    + "&tocoord=" + latitude + "," + longitude
                    + "&policy=1&referer=myapp"));
            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, "腾讯地图未安装", Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkMapAppsIsExist(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (Exception e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    //原生实现海报保存到相册
    @JavascriptInterface
    public void savePosterToAlbum(String jsonStr){
        Log.i("调用原生方法","原生实现海报保存到相册:"+ jsonStr);
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            SaveImageUtils.saveToAlbum(activity, jsonObj.getString("base64"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //跳转微信小程序
    @JavascriptInterface
    public void  navigateToMiniProgram(String jsonStr){
        Log.i("调用原生方法","跳转微信小程序:"+ jsonStr);
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            String path = jsonObj.getString("path");
            String userName = jsonObj.getString("userName");
            String appId = activity.getAppId();
            IWXAPI api = WXAPIFactory.createWXAPI(activity, appId);
            WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
            req.userName = userName;
            req.path = path;
            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW;
            api.sendReq(req);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // H5获取当前经纬度
    @JavascriptInterface
    public void getLocation(){
        Log.i("调用原生方法","获取经纬度:");
        LatLng latLng = LocationUtils.getLatLng(activity);
        setLocation(latLng);
    }

    // 设置定位
    private void setLocation(LatLng latLng) {
        if(latLng == null) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(() -> webView.loadUrl("javascript:setLocation({" +
            "latitude:" + latLng.latitude + "," +
            "longitude:" + latLng.longitude +
        "})"));
    }


    // 设置空定位
    public void setEmptyLocation() {
        new Handler(Looper.getMainLooper()).post(() -> webView.loadUrl("javascript:setLocation({" +
                "latitude:''," +
                "longitude:''," +
            "})"));
    }

    // H5获取当前经纬度
    @JavascriptInterface
    public void jumpToNativeLogin(){
        Log.i("调用原生方法","调用原生登录:");
        activity.startActivityForResult(activity.getLoginIntent(), REQUEST_LOGIN_TOKEN_CODE);
    }

    // 获取系统的一些参数
    @JavascriptInterface
    public void getSystemInfo() {
        int[] screenSize = UiUtils.getScreenSize(activity);
        Log.i("调用原生方法","获取系统的一些参数:");
        new Handler(Looper.getMainLooper()).post(() -> webView.loadUrl("javascript:setSystemInfo({" +
            "navHeight:" + UiUtils.dpToPx(activity, 46) + "," +
            "statusBarHeight:" + UiUtils.getStatusBarHeight(activity) + "," +
            "windowWidth:" + screenSize[0] + "," +
            "windowHeight:" + screenSize[1] + "," +
            "safeArea:" + UiUtils.dpToPx(activity, 15) +
        "})"));
    }

    // 设置状态栏文字颜色 只支持 #ffffff #000000
    @JavascriptInterface
    public void setNavigationBarColor(String jsonStr) {
        Log.i("调用原生方法","设置状态栏颜色:" + jsonStr);
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            String color = jsonObj.getString("color");
            if (color.equals("#ffffff")) {
                new Handler(Looper.getMainLooper()).post(() -> UiUtils.setNavigationBarWhite(activity));
            }
            if (color.equals("#000000")) {
                new Handler(Looper.getMainLooper()).post(() -> UiUtils.setNavigationBarBlack(activity));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 获取软键盘高度
    @JavascriptInterface
    public void getSoftKeyboardHeight() {
        int softKeyboardHeight = UiUtils.getSoftKeyboardHeight(activity);
        Log.i("调用原生方法","获取软键盘高度:" + softKeyboardHeight);
        new Handler(Looper.getMainLooper()).post(() -> webView.loadUrl("javascript:setSoftKeyboardHeight(" + softKeyboardHeight + ")"));
    }

}
