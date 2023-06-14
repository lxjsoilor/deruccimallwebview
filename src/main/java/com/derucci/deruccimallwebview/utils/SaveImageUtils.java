package com.derucci.deruccimallwebview.utils;

/**
 * Author: 林雄军
 * Description: Description
 * Date: 2023/6/6
 */
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class SaveImageUtils {
    private static final String FILE_SUFFIX = ".jpg";
    private static final String RELATIVE_LOCATION = Environment.DIRECTORY_PICTURES + File.separator + "SavedPictures";
    public static final int REQUEST_FILE_CODE = 102;

    /**
     * 将Base64编码的图片保存到相册
     * @param context 上下文环境
     * @param base64Data Base64编码的图片数据
     */
    public static void saveToAlbum(Context context, String base64Data) {
        Bitmap bitmap = base64ToBitmap(base64Data);
        if (bitmap != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                saveToAlbumAboveQ(context, bitmap);
//            } else {
            if (!EasyPermissions.hasPermissions(context, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                EasyPermissions.requestPermissions((Activity) context, "请允许我们访问您的存储空间", REQUEST_FILE_CODE, perms);

//                List<String> deniedPermissions = new ArrayList<>();
//                deniedPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                deniedPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//                if (EasyPermissions.somePermissionPermanentlyDenied((Activity) context, deniedPermissions)) {
//                    new AppSettingsDialog.Builder((Activity) context).setTitle("权限申请").setRationale("为了正常使用，请允许我们访问您的存储空间").build().show();
//                }
            } else {
                saveToAlbumBelowQ(context, bitmap);
            }
        }
    }

    /**
     * 将Base64编码的图片转换为Bitmap实例
     * @param base64 Base64编码的图片数据
     * @return Bitmap实例
     */
    private static Bitmap base64ToBitmap(String base64) {
        try {
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存图片到相册 Android Q及以上版本
     * @param context 上下文环境
     * @param bitmap Bitmap实例
     */
//    private static void saveToAlbumAboveQ(Context context, Bitmap bitmap) {
//        // 设置保存的文件名
//        String fileName = System.currentTimeMillis() + FILE_SUFFIX;
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
//        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, RELATIVE_LOCATION);
//        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//        try {
//            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//            Objects.requireNonNull(outputStream);
//            outputStream.close();
//            Toast.makeText(context, "已保存到相册", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 保存图片到相册 Android Q以下版本
     * @param context 上下文环境
     * @param bitmap Bitmap实例
     */
    private static void saveToAlbumBelowQ(Context context, Bitmap bitmap) {
        String fileName = System.currentTimeMillis() + FILE_SUFFIX;
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "SavedPictures";
        File destDir = new File(path);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File file = new File(path, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            // 更新媒体库
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            Toast.makeText(context, "已保存到相册", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}