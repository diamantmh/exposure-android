package io.github.getExposure.maps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.RectF;
import android.view.View;

/**
 * Basic class to create a custom info window for pins on the map.
 * Most of the methods are callbacks and are used to draw the components
 * on the info window.
 *
 * Author: Michael Shintaku
 */

//TODO: Most of the implementation, currently not being used
public class MapsInfoWindowView extends View {

    private Picture picture;
    private String text;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public MapsInfoWindowView(Context context, String text/*, Picture picture*/) {
        super(context);
        this.text = text;
        this.picture = picture;
    }


    /**
     * Draw the picture on the info box
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        int color = 3; // idk what this is
        Paint paint = new Paint(); // paint to be used, aka type of font
        paint.setColor(4);
        RectF dst = new RectF(0, 30, 30, 0);
        canvas.drawRect(dst, paint);
       /* canvas.drawPicture(picture, dst);*/
        canvas.drawColor(color);
        canvas.drawText(text, 45, 0, paint);
    }


    /**
     *
     * @param widthMeasureSpec the horizontal space requirements as per parent
     * @param heightMeasureSpec the vertical space requirements as per parent
     */
    /*
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }
*/

}
