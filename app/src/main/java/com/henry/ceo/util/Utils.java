package com.henry.ceo.util;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Administrator on 2016/9/18.
 */
public class Utils {
    public static Bitmap creatQr(int width, int height, String contents, String filePath, Bitmap logo){
        Bitmap bitmap = null;
        HashMap<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(contents, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            if (logo != null) {
                int logoWidth = logo.getWidth();
                int logoHeight = logo.getHeight();
                if (logoWidth != 0 && logoHeight != 0) {
                    float scaleFactor = width * 1.0f / 5 / logoWidth;
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    canvas.scale(scaleFactor, scaleFactor, width / 2, height / 2);
                    canvas.drawBitmap(logo, (width - logoWidth) / 2, (height - logoHeight) / 2, null);

                    canvas.save(Canvas.ALL_SAVE_FLAG);
                    canvas.restore();
                }
            }

            if (filePath != null){
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath))){
                    return BitmapFactory.decodeFile(filePath);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String decodeQr(Bitmap image){
        final int width = image.getWidth(), height = image.getHeight();
        final int[] pixels = new int[width * height];
        image.getPixels(pixels, 0, width, 0, 0, width, height);
        final RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        final BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        try {
            Result rawResult = multiFormatReader.decodeWithState(bitmap);
            return rawResult.getText();
        } catch (NotFoundException re) {
            return null;
        }finally {
            multiFormatReader.reset();
        }
    }

    public static void shareImage(Bitmap bitmap, Context mContext) {
        try {
            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            Uri uriToImage = Uri.parse(MediaStore.Images.Media.insertImage(
                    mContext.getContentResolver(), bitmap, null, null));
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
//            shareIntent.putExtra(Intent.EXTRA_TEXT,"哈哈哈");
            shareIntent.setType("image/*");
            mContext.startActivity(Intent.createChooser(shareIntent,"Select app to share"));
            if (true) return;
            // 遍历所有支持发送图片的应用。找到需要的应用
            PackageManager packageManager = mContext.getPackageManager();
            List<ResolveInfo> resolveInfoList = packageManager
                    .queryIntentActivities(shareIntent,
                             PackageManager.MATCH_DEFAULT_ONLY);
            ComponentName componentName = null;
            int i = 0;
//            Dialog dialog = new Dialog(mContext);
//            RelativeLayout relativeLayout = new RelativeLayout(mContext);
            Log.i("sysout","size="+resolveInfoList.size());
            for (; i < resolveInfoList.size(); i++) {
                Intent targeted = new Intent();
                targeted.setAction(Intent.ACTION_SEND);
                targeted.putExtra(Intent.EXTRA_STREAM, uriToImage);
                targeted.putExtra(Intent.EXTRA_TEXT,"哈哈哈");
                targeted.setType("image/*");
                Log.i("sysout",i+":"+resolveInfoList.get(i).activityInfo.name);
//                ImageView imageView = new ImageView(mContext);
//                imageView.setImageDrawable (resolveInfoList.get(i).loadIcon(packageManager));
//                relativeLayout.addView(imageView);
//                if (TextUtils.equals(
//                        resolveInfoList.get(i).activityInfo.packageName,
//                        "com.tencent.mm")) {
//                    componentName = new ComponentName(
//                            resolveInfoList.get(i).activityInfo.packageName,
//                            resolveInfoList.get(i).activityInfo.name);
//                    break;
//                }
                componentName = new ComponentName(
                            resolveInfoList.get(i).activityInfo.packageName,
                            resolveInfoList.get(i).activityInfo.name);
                targeted.setComponent(componentName);
                targetedShareIntents.add(targeted);

            }
            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(2), "Select app to share");
            if (chooserIntent == null) {
                return;
            }
            // A Parcelable[] of Intent or LabeledIntent objects as set with
            // putExtra(String, Parcelable[]) of additional activities to place
            // a the front of the list of choices, when shown to the user with a
            // ACTION_CHOOSER.
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[] {}));
            mContext.startActivity(chooserIntent);
            // 已安装**
//            if (null != componentName) {
//                shareIntent.setComponent(componentName);
//                mContext.startActivity(shareIntent);
//            } else {
//                Toast.makeText(mContext,"请先安装"+resolveInfoList.get(i).activityInfo.name, Toast.LENGTH_SHORT).show();
//            }
        } catch (Exception e) {
            Toast.makeText(mContext,"分享图片到失败", Toast.LENGTH_SHORT).show();
        }
    }
}
