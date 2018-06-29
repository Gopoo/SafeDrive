/*
 * Copyright (c) 2016 ByteDance Inc. All rights reserved.
 */

package com.didi.safedrive.utils;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.didi.safedrive.BaseApplication;

public class VUIUtils {
    private static DisplayMetrics sDisplayMetrics = BaseApplication.getInst().getResources().getDisplayMetrics();

    public static final String TAG = "VUIUtils";

    private static final int TYPE_LEFT = 1;
    private static final int TYPE_TOP = 2;
    private static final int TYPE_RIGHT = 3;
    private static final int TYPE_BOTTOM = 4;

    private static int mScreenType = -1;
    private static final int NORMAL_SCREEN = 0;  //正常规则屏幕类型
    private static final int CONCAVE_SCREEN = 1;  //凹形屏类型

    /**
     * 获得top margin, 如果view或者LayoutParams无效则返回0
     *
     * @param view
     * @return
     */
    public static int getTopMargin(View view) {
        if (view == null) {
            return 0;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            return ((ViewGroup.MarginLayoutParams) layoutParams).topMargin;
        } else {
            return 0;
        }
    }

    /**
     * 获得bottom margin, 如果view或者LayoutParams无效则返回0
     *
     * @param view
     * @return
     */
    public static int getBottomMargin(View view) {
        if (view == null) {
            return 0;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            return ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
        } else {
            return 0;
        }
    }


    private static int getPositionInternal(int type, View rootView, View targetView) {
        if (rootView == null || targetView == null) {
            return 0;
        }
        ViewParent parent = null;
        int value = 0;
        switch (type) {
            case TYPE_RIGHT:
                value += targetView.getWidth();
                break;
            case TYPE_BOTTOM:
                value += targetView.getHeight();
                break;
            default:
                break;
        }
        while (parent != rootView) {
            switch (type) {
                case TYPE_LEFT:
                case TYPE_RIGHT:
                    value -= targetView.getScrollX();
                    value += targetView.getLeft();
                    break;
                case TYPE_TOP:
                case TYPE_BOTTOM:
                    value -= targetView.getScrollY();
                    value += targetView.getTop();
                    break;
                default:
                    break;
            }
            parent = targetView.getParent();
            if (parent instanceof View) {
                targetView = (View) parent;
            } else {
                // 一直找到ViewRootImpl还没有找到, 证明targetView不在rootView中。直接返回0;
                value = 0;
                break;
            }
        }
        return value;
    }

    /**
     * 获得一个targetView相对于rootView的坐标
     */
    public static void getPosition(int[] pos, View rootView, View targetView) {
        if (pos == null || pos.length < 2) {
            throw new IllegalArgumentException("location must be an array of two integers");
        }
        pos[0] = pos[1] = 0;
        if (rootView == null || targetView == null) {
            return;
        }
        ViewParent parent = null;
        while (parent != rootView) {
            pos[0] -= targetView.getScrollX();
            pos[0] += targetView.getLeft();
            pos[1] -= targetView.getScrollY();
            pos[1] += targetView.getTop();
            parent = targetView.getParent();
            if (parent instanceof View) {
                targetView = (View) parent;
            } else {
                // 一直找到ViewRootImpl还没有找到, 证明targetView不在rootView中。直接返回0;
                pos[0] = pos[1] = 0;
                break;
            }
        }
    }

    /**
     * 判断指定的View是否是指定rootView的子view
     *
     * @param rootView
     * @param child
     * @return
     */
    public static boolean isChildView(View rootView, View child) {
        if (rootView == null || child == null) {
            return false;
        }
        child.getRootView();
        boolean result = true;
        ViewParent parent = null;
        while (parent != rootView) {
            parent = child.getParent();
            if (parent instanceof View) {
                child = (View) parent;
            } else {
                // 一直找到ViewRootImpl还没有找到或者parent为null, 证明targetView不在rootView中.
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * 设置View的background的alpha值
     *
     * @param view
     * @param alpha
     */
    public static void setBackgroundAlpha(View view, int alpha) {
        if (view == null) {
            return;
        }
        Drawable drawable = view.getBackground();
        if (drawable == null) {
            return;
        }
        drawable.setAlpha(alpha);
    }

    private static Animatable getAnimatable(View view) {
        if (view == null) {
            return null;
        }

        Animatable animatable = null;

        Drawable drawable = view.getBackground();
        if (Animatable.class.isInstance(drawable)) {
            animatable = (Animatable) drawable;
        } else if (ImageView.class.isInstance(view)) {
            drawable = ((ImageView) view).getDrawable();
            if (Animatable.class.isInstance(drawable)) {
                animatable = (Animatable) drawable;
            }
        }
        return animatable;
    }

    /**
     * 执行Animatable
     */
    public static void startAnimatable(View view) {
        final Animatable animatable = getAnimatable(view);
        if (animatable != null && !animatable.isRunning()) {
            animatable.start();
        }
    }

    /**
     * 停止Animatable
     */
    public static void stopAnimatable(View view) {
        final Animatable animatable = getAnimatable(view);
        if (animatable != null && animatable.isRunning()) {
            animatable.stop();
        }
    }

    /**
     * 将oldView替换为newView
     *
     * @param oldView
     * @param newView
     * @return 替换后的View，替换成功，返回newView，替换失败，返回oldView
     */
    public static View replaceView(View oldView, View newView) {
        if (oldView == null || newView == null || oldView == newView) {
            return oldView;
        }
        final ViewParent parent = oldView.getParent();
        if (!(parent instanceof ViewGroup)) {
            return oldView;
        }
        final int index = ((ViewGroup) parent).indexOfChild(oldView);
        if (index < 0) {
            return oldView;
        }
        final int id = oldView.getId();
        if (id != View.NO_ID) {
            newView.setId(id);
        }
        ViewGroup.LayoutParams layoutParams = oldView.getLayoutParams();
        ((ViewGroup) parent).removeView(oldView);
        ((ViewGroup) parent).addView(newView, index, layoutParams);
        return newView;
    }


    /**
     * 获得View中的bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap getBitmap(View view) {
        if (view == null) {
            return null;
        }
        Drawable drawable = null;
        if (view instanceof ImageView) {
            drawable = ((ImageView) view).getDrawable();
        }
        if (drawable == null) {
            drawable = view.getBackground();
        }
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                return bitmap;
            }
        }
        return null;
    }

    /**
     * 判断当前view是否在父view的最上层
     *
     * @param view
     * @return
     */
    public static boolean isFront(View view) {
        if (view == null) {
            return false;
        }
        if (view.getParent() instanceof ViewGroup) {
            final ViewGroup parent = (ViewGroup) view.getParent();
            return parent.indexOfChild(view) == parent.getChildCount() - 1;
        }
        return false;
    }

    /**
     * 将view移到父view的最上层
     *
     * @param view
     */
    public static void bringToFront(View view) {
        if (view == null) {
            return;
        }
        if (!isFront(view)) {
            view.bringToFront();
        }
    }

    /**
     * dp转px
     */
    public static int dp2px(float dp) {
        return (int) (sDisplayMetrics.density * dp + 0.5f);
    }

    /**
     * 给LinearLayout添加divider
     *
     * @param linearLayout
     * @param color
     * @param size
     * @param showDividers
     */
    public static void addLinearLayoutDivider(LinearLayout linearLayout, @ColorRes int color, int size, int showDividers) {
        if (linearLayout == null) {
            return;
        }
        PaintDrawable dividerDrawable = new PaintDrawable(linearLayout.getResources().getColor(color));
        dividerDrawable.setIntrinsicHeight(size);
        dividerDrawable.setIntrinsicWidth(size);
        linearLayout.setDividerDrawable(dividerDrawable);
        linearLayout.setShowDividers(showDividers);
    }

    public static void setOnTouchBackground(View v) {
        if (v == null) {
            return;
        }
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setAlpha(0.6f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.setAlpha(1.0f);
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 为RelativeLayout的子view添加一些规则, {@link RelativeLayout.LayoutParams#addRule(int, int)}
     */
    public static void addRuleForRelativeLayoutChild(View view, int... args) {
        if (view == null) {
            return;
        }
        int length = args != null ? args.length : -1;
        if (length <= 0 || length % 2 != 0) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (!(layoutParams instanceof RelativeLayout.LayoutParams)) {
            return;
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layoutParams;
        for (int i = 0; i + 1 < args.length; i += 2) {
            lp.addRule(args[i], args[i + 1]);
        }
        view.setLayoutParams(lp);
    }

    public static void executeImmediatelyOrOnPreDraw(@NonNull final View view, @Nullable final Runnable action) {
        if (view == null) {
            return;
        }
        if (view.getWidth() > 0 && view.getHeight() > 0) {
            if (action != null) {
                action.run();
            }
        } else {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (action != null) {
                        action.run();
                    }
                    return true;
                }
            });
        }
    }

    public static void executeOnPreDraw(@NonNull final View view, @Nullable final Runnable action) {
        if (view == null) {
            return;
        }
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                if (action != null) {
                    action.run();
                }
                return true;
            }
        });
    }

    public static int getScreenRotation(@NonNull Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return Integer.MIN_VALUE;
        }

        Display display = wm.getDefaultDisplay();
        if (display == null) {
            return Integer.MIN_VALUE;
        }

        return display.getRotation();
    }

    /**
     * @return true if Orientation is {@link ActivityInfo#SCREEN_ORIENTATION_LANDSCAPE}
     * or {@link ActivityInfo#SCREEN_ORIENTATION_REVERSE_LANDSCAPE}
     */
    public static boolean isScreenHorizontal(@NonNull Context context) {
        int rotation = getScreenRotation(context);
        return rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270;
    }

    /**
     * @return true if Orientation is {@link ActivityInfo#SCREEN_ORIENTATION_PORTRAIT}
     * or {@link ActivityInfo#SCREEN_ORIENTATION_REVERSE_PORTRAIT}
     */
    public static boolean isScreenVertical(@NonNull Context context) {
        int rotation = getScreenRotation(context);
        return rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180;
    }

    public static void expandClickRegion(@NonNull final View view, final int expandValue) {
        if (view == null) {
            return;
        }
        view.post(new Runnable() {
            @Override
            public void run() {
                Rect delegateArea = new Rect();
                view.getHitRect(delegateArea);
                delegateArea.top -= expandValue;
                delegateArea.bottom += expandValue;
                delegateArea.left -= expandValue;
                delegateArea.right += expandValue;
                TouchDelegate expandedArea = new TouchDelegate(delegateArea, view);
                // give the delegate to an ancestor of the view we're delegating
                // the area to
                if (View.class.isInstance(view.getParent())) {
                    ((View) view.getParent()).setTouchDelegate(expandedArea);
                }
            }
        });
    }
}
