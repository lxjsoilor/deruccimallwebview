package com.derucci.deruccimallwebview.webview;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Author: 林雄军
 * Description: Description
 * Date: 2023/5/29
 */
public class MallWebChromeClient extends WebChromeClient {
    // 相册/拍照请求码
    static public final int REQUEST_FILE_CAMERA_CODE = 100;
    // 相册/拍照权限请求码
    static public final int REQUEST_FILE_CAMERA_CODE_PERMISSIONS = 101;
    Activity activity;
    Uri imageUri;
    MallWebChromeClient(Activity activity) {
        this.activity = activity;
    }
    private ValueCallback<Uri> valueCallback;
    private ValueCallback<Uri[]> valueCallbackList;
    private OpenFileChooserCallBack openFileChooserCallBack;
    private CreateWindowCallBack createWindowCallBack;

    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, "");
    }

    //For Android 3.0 - 4.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        valueCallback = uploadMsg;
        openFileChooserByCameraOrFile();
    }


    // For Android 4.0 - 5.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooser(uploadMsg, acceptType);
    }

    // For Android > 5.0
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (valueCallbackList != null) {
            valueCallbackList.onReceiveValue(null);
        }
        valueCallbackList = filePathCallback;
        openFileChooserByCameraOrFile();
        return true;
    }

    private void openFileChooserByCameraOrFile() {
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(activity, "设备无摄像头", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!EasyPermissions.hasPermissions(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        )) {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            EasyPermissions.requestPermissions(activity, "请允许我们访问您的存储空间和相机功能", REQUEST_FILE_CAMERA_CODE_PERMISSIONS, perms);
            onReceiveNullValue();
        } else {
            openFileChooserDialog();
        }
    }

    // 打开相册或相机弹框
    public void openFileChooserDialog() {
        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        Intent Photo = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooserIntent = Intent.createChooser(Photo, "选择照片");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
        activity.startActivityForResult(chooserIntent, REQUEST_FILE_CAMERA_CODE);
    }

    public void setOpenFileChooserCallBack(OpenFileChooserCallBack callBack) {
        openFileChooserCallBack = callBack;
    }

    public void setCreateWindowCallBack(CreateWindowCallBack callBack) {
        createWindowCallBack = callBack;
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        if (createWindowCallBack != null) {
            createWindowCallBack.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }
        return true;
    }

    //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
    @Override
    public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
        localBuilder.setMessage(message).setPositiveButton("确定",null);
        localBuilder.setCancelable(false);
        localBuilder.create().show();
        result.confirm();
        return true;
    }

    //获取网页标题
    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
//        onReceivedTitleChange.run(title);
    }

    //加载进度回调
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
    }

    public void onReceiveNullValue() {
        if (valueCallbackList!= null) {
            valueCallbackList.onReceiveValue(null);
            valueCallbackList = null;
        }
    }

    public void onReceiveValue(@Nullable Intent data) {
        if (imageUri == null) {
            if (valueCallbackList!= null) {
                valueCallbackList.onReceiveValue(null);
                valueCallbackList = null;
            }
            return;
        }
        if (data != null) {
            // 处理选择的照片
            Uri uriData = data.getData();
            if (uriData != null) {
                try {
                    valueCallbackList.onReceiveValue(new Uri[]{uriData});
                    valueCallbackList = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                valueCallbackList.onReceiveValue(null);
                valueCallbackList = null;
            }
        } else {
            // 处理拍照的照片
            try {
                valueCallbackList.onReceiveValue(new Uri[]{imageUri});
                valueCallbackList = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public interface OpenFileChooserCallBack {

        void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType);

        void showFileChooserCallBack(ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams);
    }

    public interface CreateWindowCallBack {
        void onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg);
    }


    public interface IOnReceivedTitleChange {
        void run(String title);
    }
}
