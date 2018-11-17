package cent.news.com.baseframe.utils;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class BaseKeyboardUtils {

    /***
     * 隐藏键盘
     *
     * @param acitivity
     *            参数
     */
    public static void hideSoftInput(Activity acitivity) {
        if (acitivity == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) acitivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (acitivity.getWindow() == null || acitivity.getWindow().getDecorView() == null) {
            return;
        }
        imm.hideSoftInputFromWindow(acitivity.getWindow().getDecorView().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /***
     * 显示键盘
     *
     * @param acitivity
     *            参数
     * @param et
     *            参数
     */
    public static void showSoftInput(Activity acitivity, EditText et) {
        if (et == null) return;
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.RESULT_UNCHANGED_SHOWN);

    }

    /***
     * 延迟300毫秒-显示键盘 说明：延迟会解决 有时弹不出键盘的问题
     *
     * @param acitivity
     *            参数
     * @param et
     *            参数
     */
    public static void showSoftInputDelay(final Activity acitivity, final EditText et) {
        et.postDelayed(new Runnable() {

            @Override public void run() {
                showSoftInput(acitivity, et);
            }
        }, 300);
    }

    /**
     * 判断是否显示
     *
     * @param activity
     *            参数
     * @return 返回值
     */
    public static boolean isSoftInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    /**
     * 判断键盘是否显示 如果是显示就隐藏
     *
     * @param v
     *            参数
     * @param event
     *            参数
     * @return 返回值
     */
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return true;
    }

    /**
     * 键盘自动关闭
     *
     * @param ev
     *            参数
     * @param activiy
     *            参数
     */
    public static void keyBoardAutoHidden(MotionEvent ev, Activity activiy) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = activiy.getCurrentFocus();
            if (BaseKeyboardUtils.isShouldHideInput(v, ev)) {
                BaseKeyboardUtils.hideSoftInput(activiy);
            }
        }
    }



}
