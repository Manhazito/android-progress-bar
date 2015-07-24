/* Copyright (C) 2015 Lookatitude IT Services & Consulting - All Rights Reserved.
* Unauthorized copying of this file, via any medium is strictly prohibited.
*
* Proprietary and confidential.
*/

package org.gamaworks.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * [description]
 *
 * @author Filipe Ramos
 * @version 1.0
 */
public class ProgressBar extends View implements View.OnTouchListener {
    @SuppressWarnings("unused")
    private static final String TAG = ProgressBar.class.getSimpleName();

    private int numberOfPositions;
    private int numberOfDents;
    private int numberOfDivisions;
    private int[] orderOfPositions;
    private Bitmap[] orderOfDrawableBitmaps;
    private float[] possiblePositionsX;
    private int progressStage;
    private float density = getResources().getDisplayMetrics().density;
    private Paint lightBasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint lightDentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint darkBasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint darkDentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int baseStrokeWidth = 4;
    private int dentStrokeWidth = 2;
    private boolean markerCenteredOnBase;
    private float markerRelativeVerticalDeviation;
    private float baseStartPadding;
    private float baseEndPadding;
    private float baseY;
    private float markerBaseY;
    private Path lightPath = new Path();
    private Path darkPath = new Path();
    private boolean showDivisionDents = true;
    private boolean showSubdivisionDents = false;
    private boolean subdivisionDentsSmaller = true;

    private boolean isBeingDragged = false;
    private boolean canSetManually = false;

    public ProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        int lightColor = Color.argb(102, 0, 166, 193);
        int darkColor = Color.argb(255, 0, 166, 193);

        if (isInEditMode()) {
            progressStage = 2;
            numberOfDivisions = 4;
            numberOfDents = numberOfDivisions + 1;
            numberOfPositions = (2 * numberOfDivisions) + 1;
            markerCenteredOnBase = true;
            markerRelativeVerticalDeviation = 0;
            orderOfPositions = new int[]{0, 0, 2, 4, 6, 7, 8, 8, 8, 8};
            orderOfDrawableBitmaps = new Bitmap[]{
                    BitmapFactory.decodeResource(getResources(), R.drawable.progress_marker_blue),
                    BitmapFactory.decodeResource(getResources(), R.drawable.progress_marker_blue),
                    BitmapFactory.decodeResource(getResources(), R.drawable.progress_marker_blue),
                    BitmapFactory.decodeResource(getResources(), R.drawable.progress_marker_blue),
                    BitmapFactory.decodeResource(getResources(), R.drawable.progress_marker_blue),
                    BitmapFactory.decodeResource(getResources(), R.drawable.progress_marker_blue),
                    BitmapFactory.decodeResource(getResources(), R.drawable.progress_marker_blue),
                    BitmapFactory.decodeResource(getResources(), R.drawable.progress_marker_blue),
                    BitmapFactory.decodeResource(getResources(), R.drawable.progress_marker_blue),
                    BitmapFactory.decodeResource(getResources(), R.drawable.progress_marker_blue)
            };
        } else {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressBar, 0, 0);
            progressStage = typedArray.getInteger(R.styleable.ProgressBar_initialStage, 0);
            lightColor = typedArray.getColor(R.styleable.ProgressBar_lightColor, lightColor);
            darkColor = typedArray.getColor(R.styleable.ProgressBar_darkColor, darkColor);
            numberOfDivisions = typedArray.getInt(R.styleable.ProgressBar_divisions, 4);
            numberOfDents = numberOfDivisions + 1;
            numberOfPositions = (2 * numberOfDivisions) + 1;
            markerCenteredOnBase = typedArray.getBoolean(R.styleable.ProgressBar_markerCentered, true);
            markerRelativeVerticalDeviation = typedArray.getFloat(R.styleable.ProgressBar_markerRelativeVerticalDeviation, 0f);
            int orderOfPositionsResourceId = typedArray.getResourceId(R.styleable.ProgressBar_orderOfPositions, 0);
            if (orderOfPositionsResourceId > 0) orderOfPositions = getResources().getIntArray(orderOfPositionsResourceId);
            else generateOrderOfPositions();
            orderOfDrawableBitmaps = new Bitmap[orderOfPositions.length];
            TypedArray orderOfDrawables = null;
            int orderOfDrawablesResourceId = typedArray.getResourceId(R.styleable.ProgressBar_orderOfDrawables, 0);
            if (orderOfDrawablesResourceId > 0) {
                orderOfDrawables = getResources().obtainTypedArray(orderOfDrawablesResourceId);
                for (int i = 0; i < orderOfDrawableBitmaps.length; i++) {
                    orderOfDrawableBitmaps[i] = BitmapFactory.decodeResource(getResources(),
                            orderOfDrawables.getResourceId(i, 0));
                }
            }
            showDivisionDents = typedArray.getBoolean(R.styleable.ProgressBar_showDivisionDents, true);
            showSubdivisionDents = typedArray.getBoolean(R.styleable.ProgressBar_showSubdivisionDents, false);
            subdivisionDentsSmaller = typedArray.getBoolean(R.styleable.ProgressBar_subdivisionDentsSmaller, true);

            typedArray.recycle();
            if (orderOfDrawables != null) orderOfDrawables.recycle();

            clipOrderOfPositions();
            completeBitmapArray();
        }

        lightBasePaint.setStrokeWidth(baseStrokeWidth * density);
        lightBasePaint.setColor(lightColor);
        lightBasePaint.setStyle(Paint.Style.STROKE);
        lightBasePaint.setStrokeCap(Paint.Cap.BUTT);

        lightDentPaint.setStrokeWidth(dentStrokeWidth * density);
        lightDentPaint.setColor(lightColor);
        lightDentPaint.setStyle(Paint.Style.STROKE);
        lightDentPaint.setStrokeCap(Paint.Cap.BUTT);

        darkBasePaint.setStrokeWidth(baseStrokeWidth * density);
        darkBasePaint.setColor(darkColor);
        darkBasePaint.setStyle(Paint.Style.STROKE);
        darkBasePaint.setStrokeCap(Paint.Cap.BUTT);

        darkDentPaint.setStrokeWidth(dentStrokeWidth * density);
        darkDentPaint.setColor(darkColor);
        darkDentPaint.setStyle(Paint.Style.STROKE);
        darkDentPaint.setStrokeCap(Paint.Cap.BUTT);

        this.setOnTouchListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        baseY = height + getPaddingTop() - baseStrokeWidth / 2
                - orderOfDrawableBitmaps[progressStage].getHeight() / 2;
        if (markerCenteredOnBase) {
            float bottomPadding = orderOfDrawableBitmaps[progressStage].getHeight() * markerRelativeVerticalDeviation;
            markerBaseY = baseY - orderOfDrawableBitmaps[progressStage].getHeight() / 2 - bottomPadding;
        } else {
            markerBaseY = baseY - orderOfDrawableBitmaps[progressStage].getHeight();
        }

        baseStartPadding = getPaddingRight() +
                orderOfDrawableBitmaps[progressStage].getWidth() / 2;
        baseEndPadding = getPaddingLeft() +
                orderOfDrawableBitmaps[progressStage].getWidth() / 2;

        float baseWidth = getWidth() - baseStartPadding - baseEndPadding;

        if (possiblePositionsX == null)
            possiblePositionsX = new float[2 * numberOfDivisions + 1];

        for (int i = 0; i < possiblePositionsX.length; i++) {
            possiblePositionsX[i] = baseStartPadding
                    + (i * baseWidth / (2 * numberOfDivisions))
                    + (density * dentStrokeWidth / 2) -
                    ((dentStrokeWidth * density) * i / (2 * numberOfDivisions));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int desiredHeight = orderOfDrawableBitmaps[progressStage].getHeight() + baseStrokeWidth +
                (int) Math.abs(Math.ceil(orderOfDrawableBitmaps[progressStage].getHeight() * markerRelativeVerticalDeviation));
        int height;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int desiredWidth = 2 * numberOfDivisions * orderOfDrawableBitmaps[progressStage].getWidth();
        int width;

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (lightPath != null && darkPath != null) {
            int divisionDentsHeight = 4;
            int subdivisionDentsHeight = subdivisionDentsSmaller ? divisionDentsHeight / 2 : divisionDentsHeight;
            int stepIndex = orderOfPositions[progressStage];

            // Base line
            canvas.drawLine(baseStartPadding, baseY, getWidth() - baseEndPadding, baseY, lightBasePaint);

            // Major dents
            if (showDivisionDents) {
                for (int i = 0; i < possiblePositionsX.length; i += 2) {
                    float startY = baseY - ((baseStrokeWidth / 2) * density);
                    float endY = startY - (divisionDentsHeight * density);
                    canvas.drawLine(possiblePositionsX[i], startY,
                            possiblePositionsX[i], endY, lightDentPaint);
                }
            }

            // Minor dents
            if (showSubdivisionDents) {
                for (int i = 1; i < possiblePositionsX.length; i += 2) {
                    float startY = baseY - ((baseStrokeWidth / 2) * density);
                    float endY = startY - (subdivisionDentsHeight * density);
                    canvas.drawLine(possiblePositionsX[i], startY,
                            possiblePositionsX[i], endY, lightDentPaint);
                }
            }

            // Base line (dark)
            float endX = possiblePositionsX[stepIndex] - (density * dentStrokeWidth / 2);
            canvas.drawLine(baseStartPadding, baseY, endX, baseY, darkBasePaint);

            // Major dents (dark)
            if (showDivisionDents) {
                for (int i = 0; i <= stepIndex; i += 2) {
                    if (i == numberOfDents) continue;
                    float startY = baseY - ((baseStrokeWidth / 2) * density);
                    float endY = startY - (divisionDentsHeight * density);
                    canvas.drawLine(possiblePositionsX[i], startY,
                            possiblePositionsX[i], endY, darkDentPaint);
                }
            }

            // Minor dents (dark)
            if (showSubdivisionDents) {
                for (int i = 1; i <= stepIndex; i += 2) {
                    float startY = baseY - ((baseStrokeWidth / 2) * density);
                    float endY = startY - (subdivisionDentsHeight * density);
                    canvas.drawLine(possiblePositionsX[i], startY,
                            possiblePositionsX[i], endY, darkDentPaint);
                }
            }

            // Marker
            float bmpX = possiblePositionsX[stepIndex] -
                    (orderOfDrawableBitmaps[progressStage].getWidth() / 2);
            canvas.drawBitmap(orderOfDrawableBitmaps[progressStage], bmpX, markerBaseY, null);
        }
    }

    private void generateOrderOfPositions() {
        orderOfPositions = new int[numberOfPositions];
        for (int i = 0; i < orderOfPositions.length; i++) orderOfPositions[i] = i;
    }

    private void clipOrderOfPositions() {
        int[] tmpArray = new int[numberOfPositions];
        System.arraycopy(orderOfPositions, 0, tmpArray, 0, orderOfPositions.length);
        orderOfPositions = new int[numberOfPositions];
        System.arraycopy(tmpArray, 0, orderOfPositions, 0, orderOfPositions.length);
        for (int i = 0; i < orderOfPositions.length; i++) {
            if (orderOfPositions[i] < 0) orderOfPositions[i] = 0;
            if (orderOfPositions[i] > (2 * numberOfDivisions)) orderOfPositions[i] = 2 * numberOfDivisions;
        }
    }

    private void completeBitmapArray() {
        Bitmap[] tmpArray = new Bitmap[numberOfPositions];
        System.arraycopy(orderOfDrawableBitmaps, 0, tmpArray, 0, orderOfDrawableBitmaps.length);
        orderOfDrawableBitmaps = new Bitmap[numberOfPositions];
        System.arraycopy(tmpArray, 0, orderOfDrawableBitmaps, 0, orderOfDrawableBitmaps.length);
        for (int i = 0; i < orderOfDrawableBitmaps.length; i++) {
            if (orderOfDrawableBitmaps[i] == null) {
                orderOfDrawableBitmaps[i] = BitmapFactory.decodeResource(getResources(), R.drawable.progress_marker_blue);
            }
        }
    }

    public void setProgressStage(int stage) {
        if (stage < 0) stage = 0;
        if (stage > numberOfPositions - 1) stage = numberOfPositions - 1;

        progressStage = stage;
        if (getContext() instanceof ProgressBarListener) ((ProgressBarListener) getContext()).progressChanged();
        invalidate();
    }

    public int getProgressStage() {
        return progressStage;
    }

    public void goForward() {
        int stage = progressStage + 1;
        setProgressStage(stage);
    }

    public void goBackward() {
        int stage = progressStage - 1;
        setProgressStage(stage);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!canSetManually) return false;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isBeingDragged = true;
            setProgressStage(getClosestStage(event.getRawX()));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isBeingDragged = false;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (isBeingDragged) {
                setProgressStage(getClosestStage(event.getRawX()));
                if (getContext() instanceof ProgressBarListener) ((ProgressBarListener) getContext()).progressChanged();
                invalidate();
            }
        }

        return true;
    }

    public void canSetManually(boolean canSet) {
        canSetManually = canSet;
    }

    private int getClosestStage(float x) {
        float distance = Float.MAX_VALUE;
        int position = 0;

        for (int i = 0; i < orderOfPositions.length; i++) {
            float posX = possiblePositionsX[orderOfPositions[i]];
            float deltaX = Math.abs(x - posX);
            if (deltaX < distance) {
                distance = deltaX;
                position = i;
            }
        }

        return position;
    }

    public interface ProgressBarListener {
        void progressChanged();
    }
}
