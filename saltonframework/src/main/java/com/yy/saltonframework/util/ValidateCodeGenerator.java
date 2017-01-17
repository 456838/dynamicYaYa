package com.yy.saltonframework.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2015/12/24 16:22
 * Time: 16:22
 * Description:
 */
public class ValidateCodeGenerator {

   /* private static final char[] CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };*/

    private static final char[] CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'

    };
    //default settings
    private static final int DEFAULT_CODE_LENGTH = 4;
    private static final int DEFAULT_FONT_SIZE = 20;
    private static final int DEFAULT_LINE_NUMBER = 0;
    private static final int BASE_PADDING_LEFT = 20, RANGE_PADDING_LEFT = 20, BASE_PADDING_TOP = 20, RANGE_PADDING_TOP = 20;
    private static final int DEFAULT_WIDTH = 200, DEFAULT_HEIGHT = 50;

    //variables
    private static String value;
    private static int padding_left, padding_top;
    private static Random random = new Random();

    public static Bitmap createBitmap(int width, int height) {
        padding_left = 0;
        //创建画布
        Bitmap bp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bp);

        //随机生成验证码字符
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < DEFAULT_CODE_LENGTH; i++) {
            buffer.append(CHARS[random.nextInt(CHARS.length)]);
        }
        value = buffer.toString();

        //设置颜色
        c.drawColor(Color.WHITE);

        //设置画笔大小
        Paint paint = new Paint();
        paint.setTextSize(DEFAULT_FONT_SIZE);
        for (int i = 0; i < value.length(); i++) {
            //随机样式
            randomTextStyle(paint);
            padding_left += BASE_PADDING_LEFT + random.nextInt(RANGE_PADDING_LEFT);
            padding_top = BASE_PADDING_TOP + random.nextInt(RANGE_PADDING_TOP);
            c.drawText(value.charAt(i) + "", padding_left, padding_top, paint);
        }
//        for (int i = 0; i < DEFAULT_LINE_NUMBER; i++) {
//            drawLine(c, paint);
//        }
        //保存
        c.save(Canvas.ALL_SAVE_FLAG);
        c.restore();

        return bp;
    }

    public static String getCode() {
        return value;
    }

    private static void randomTextStyle(Paint paint) {
        int color = randomColor(1);
        paint.setColor(Color.rgb(0, 0, 0));
        paint.setFakeBoldText(random.nextBoolean());//true为粗体，false为非粗体
        float skewX = random.nextInt(11) / 10;
        skewX = random.nextBoolean() ? skewX : -skewX;
        paint.setTextSkewX(skewX); //float类型参数，负数表示右斜，整数左斜
        paint.setUnderlineText(true); //true为下划线，false为非下划线
        paint.setStrikeThruText(true); //true为删除线，false为非删除线
    }

    private static void drawLine(Canvas canvas, Paint paint) {
        int color = randomColor(1);
        int startX = random.nextInt(DEFAULT_WIDTH);
        int startY = random.nextInt(DEFAULT_HEIGHT);
        int stopX = random.nextInt(DEFAULT_WIDTH);
        int stopY = random.nextInt(DEFAULT_HEIGHT);
        paint.setStrokeWidth(1);
        paint.setColor(color);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    private static int randomColor(int rate) {
        int red = random.nextInt(256) / rate;
        int green = random.nextInt(256) / rate;
        int blue = random.nextInt(256) / rate;
        return Color.rgb(red, green, blue);
    }
}
