package com.henry.ceo.zxing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

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
import java.util.HashMap;


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
        String retStr = null;
        final int width = image.getWidth(), height = image.getHeight();
        final int[] pixels = new int[width * height];
        image.getPixels(pixels, 0, width, 0, 0, width, height);
        final RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        final BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        try {
            Result rawResult = multiFormatReader.decodeWithState(bitmap);
            retStr = rawResult.getText();
        } catch (NotFoundException re) {
            return null;
        }finally {
            multiFormatReader.reset();
        }
        return retStr;
    }
}
