/*
 * Copyright (c) 2017 Razeware LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish, 
 * distribute, sublicense, create a derivative work, and/or sell copies of the 
 * Software in any work that is designed, intended, or marketed for pedagogical or 
 * instructional purposes related to programming, coding, application development, 
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works, 
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.basi.app.ar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

import com.basi.app.ar.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.face.Face;

/**
 * Draws the computer-generated images over faces in the camera images
 */
class FaceGraphic extends GraphicOverlay.Graphic {

    private static final String TAG = "FaceGraphic";

    private static final float DOT_RADIUS = 3.0f;
    private static final float TEXT_OFFSET_Y = -30.0f;

    private boolean mIsFrontFacing;

    // This variable may be written to by one of many threads. By declaring it as volatile,
    // we guarantee that when we read its contents, we're reading the most recent "write"
    // by any thread.
    private volatile FaceData mFaceData;

    private Paint mHintTextPaint;
    private Paint mHintOutlinePaint;
    private Paint mEyeWhitePaint;
    private Paint mIrisPaint;
    private Paint mEyeOutlinePaint;
    private Paint mEyelidPaint;

    private Drawable mPigNoseGraphic;
    private Drawable mMustacheGraphic;
    private Drawable mHappyStarGraphic;
    private Drawable mHatGraphic;


    FaceGraphic(GraphicOverlay overlay, Context context, boolean isFrontFacing) {
        super(overlay);
        mIsFrontFacing = isFrontFacing;
        Resources resources = context.getResources();
        initializePaints(resources);
        initializeGraphics(resources);
    }

    private void initializeGraphics(Resources resources) {
        mPigNoseGraphic = resources.getDrawable(R.drawable.pig_nose_emoji);
        mMustacheGraphic = resources.getDrawable(R.drawable.mustache);
        mHappyStarGraphic = resources.getDrawable(R.drawable.happy_star);
        mHatGraphic = resources.getDrawable(R.drawable.red_hat);
    }

    private void initializePaints(Resources resources) {
        mHintTextPaint = new Paint();
        mHintTextPaint.setColor(resources.getColor(R.color.overlayHint));
        mHintTextPaint.setTextSize(resources.getDimension(R.dimen.textSize));

        mHintOutlinePaint = new Paint();
        mHintOutlinePaint.setColor(resources.getColor(R.color.overlayHint));
        mHintOutlinePaint.setStyle(Paint.Style.STROKE);
        mHintOutlinePaint.setStrokeWidth(resources.getDimension(R.dimen.hintStroke));

        mEyeWhitePaint = new Paint();
        mEyeWhitePaint.setColor(resources.getColor(R.color.eyeWhite));
        mEyeWhitePaint.setStyle(Paint.Style.FILL);

        mIrisPaint = new Paint();
        mIrisPaint.setColor(resources.getColor(R.color.iris));
        mIrisPaint.setStyle(Paint.Style.FILL);

        mEyeOutlinePaint = new Paint();
        mEyeOutlinePaint.setColor(resources.getColor(R.color.eyeOutline));
        mEyeOutlinePaint.setStyle(Paint.Style.STROKE);
        mEyeOutlinePaint.setStrokeWidth(resources.getDimension(R.dimen.eyeOutlineStroke));

        mEyelidPaint = new Paint();
        mEyelidPaint.setColor(resources.getColor(R.color.eyelid));
        mEyelidPaint.setStyle(Paint.Style.FILL);
    }

    // 1

    /**
     * When a FaceTracker instance gets an update on a tracked face,
     * it calls its corresponding FaceGraphic instance’s update method
     * and passes it information about that face. The method saves that
     * information in mFace and then calls FaceGraphic’s parent class’ postInvalidate
     * method, which forces the graphic to redraw
     * @param faceData
     */
    public void update(FaceData faceData) {
        this.mFaceData = faceData;
        postInvalidate();
        // Trigger a redraw of the graphic (i.e. cause draw() to be called)
    }

    @Override
    public void draw(Canvas canvas) {
// 2
        /**
         * Before attempting to draw a box around the face,
         * the draw method checks to see if the face is still being tracked.
         * If it is, mFace will be non-null.
         */
        // Confirm that the face and its features are still visible
        // before drawing any graphics over it.

//        FaceData faceData = mFaceData;
        if (mFaceData == null) {
            return;
        }


        // 1
        PointF detectPosition = mFaceData.getPosition();
        PointF detectLeftEyePosition = mFaceData.getLeftEyePosition();
        PointF detectRightEyePosition = mFaceData.getRightEyePosition();
        PointF detectNoseBasePosition = mFaceData.getNoseBasePosition();
        PointF detectMouthLeftPosition = mFaceData.getMouthLeftPosition();
        PointF detectMouthBottomPosition = mFaceData.getMouthBottomPosition();
        PointF detectMouthRightPosition = mFaceData.getMouthRightPosition();
        if ((detectPosition == null) ||
                (detectLeftEyePosition == null) ||
                (detectRightEyePosition == null) ||
                (detectNoseBasePosition == null) ||
                (detectMouthLeftPosition == null) ||
                (detectMouthBottomPosition == null) ||
                (detectMouthRightPosition == null)) {
            return;
        }



        float leftEyeX = translateX(detectLeftEyePosition.x);
        float leftEyeY = translateY(detectLeftEyePosition.y);
        canvas.drawCircle(leftEyeX, leftEyeY, DOT_RADIUS, mHintOutlinePaint);
        canvas.drawText("left eye", leftEyeX, leftEyeY + TEXT_OFFSET_Y, mHintTextPaint);

        float rightEyeX = translateX(detectRightEyePosition.x);
        float rightEyeY = translateY(detectRightEyePosition.y);
        canvas.drawCircle(rightEyeX, rightEyeY, DOT_RADIUS, mHintOutlinePaint);
        canvas.drawText("right eye", rightEyeX, rightEyeY + TEXT_OFFSET_Y, mHintTextPaint);

        float noseBaseX = translateX(detectNoseBasePosition.x);
        float noseBaseY = translateY(detectNoseBasePosition.y);
        canvas.drawCircle(noseBaseX, noseBaseY, DOT_RADIUS, mHintOutlinePaint);
        canvas.drawText("nose base", noseBaseX, noseBaseY + TEXT_OFFSET_Y, mHintTextPaint);

        float mouthLeftX = translateX(detectMouthLeftPosition.x);
        float mouthLeftY = translateY(detectMouthLeftPosition.y);
        canvas.drawCircle(mouthLeftX, mouthLeftY, DOT_RADIUS, mHintOutlinePaint);
        canvas.drawText("mouth left", mouthLeftX, mouthLeftY + TEXT_OFFSET_Y, mHintTextPaint);

        float mouthRightX = translateX(detectMouthRightPosition.x);
        float mouthRightY = translateY(detectMouthRightPosition.y);
        canvas.drawCircle(mouthRightX, mouthRightY, DOT_RADIUS, mHintOutlinePaint);
        canvas.drawText("mouth right", mouthRightX, mouthRightY + TEXT_OFFSET_Y, mHintTextPaint);

        float mouthBottomX = translateX(detectMouthBottomPosition.x);
        float mouthBottomY = translateY(detectMouthBottomPosition.y);
        canvas.drawCircle(mouthBottomX, mouthBottomY, DOT_RADIUS, mHintOutlinePaint);
        canvas.drawText("mouth bottom", mouthBottomX, mouthBottomY + TEXT_OFFSET_Y, mHintTextPaint);

    }

}
