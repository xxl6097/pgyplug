package com.pgyplug;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

/**
 * Created by 80010814 on 2016/10/11.
 */

public class AppUpdate {
    //static AlertDialog myDialog = null;

    public static void init(Context context) {
        PgyCrashManager.register(context);
    }

    public static void checkUpdateVersion(final Activity activity) {
        /*if (myDialog == null) {
            myDialog = new AlertDialog.Builder(activity).create();
        }*/
//         版本检测方式2：带更新回调监听
        PgyUpdateManager.register(activity,
                new UpdateManagerListener() {
                    @Override
                    public void onUpdateAvailable(final String result) {
                        if(!activity.isFinishing())
                            return;
                        final AppBean appBean = getAppBeanFromString(result);
                        int currentVersoin = getAppVersionCode(activity);
                        int remoteVersion = 0;
                        if (isNum(appBean.getVersionCode())) {
                            remoteVersion = Integer.valueOf(appBean.getVersionCode());
                        }
                        if (remoteVersion <= currentVersoin) {
                            Toast.makeText(activity, activity.getString(R.string.current_version) + getAppVersionName(activity),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        StringBuffer sb = new StringBuffer();
                        sb.append(activity.getString(R.string.current_version));
                        sb.append(getAppVersionName(activity));
                        sb.append(activity.getString(R.string.last_version));
                        sb.append(appBean.getVersionName());
                        sb.append(activity.getString(R.string.update_content));
                        sb.append("\r\n");
                        sb.append(appBean.getReleaseNote());
                       final AlertDialog myDialog = new AlertDialog.Builder(activity).create();;
                        if (!myDialog.isShowing()) {
                            myDialog.show();
                            myDialog.getWindow().setContentView(R.layout.updatedlg);
                            TextView content = (TextView) myDialog.getWindow().findViewById(R.id.content);
                            content.setText(sb.toString());
                            myDialog.getWindow().findViewById(R.id.ok)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startDownloadTask(activity,
                                                    appBean.getDownloadURL());
                                            myDialog.dismiss();
                                        }
                                    });
                            myDialog.getWindow().findViewById(R.id.cancel)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            myDialog.dismiss();
                                        }
                                    });
                        }

                    }

                    @Override
                    public void onNoUpdateAvailable() {
//                        Toast.makeText(activity, "已经是最新版本",
//                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 获取App版本名.
     *
     * @param context 上下文信息
     * @return App版本名
     */
    public static String getAppVersionName(final Context context) {
        String strAppVersionName = "0.0.1";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            strAppVersionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return strAppVersionName;
    }

    public static int getAppVersionCode(final Context context) {
        int iAppVersionCode = 0;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            iAppVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return iAppVersionCode;
    }

    /**
     * 判断参数是否为数�?
     *
     * @param strNum 待判断的数字参数
     * @return true表示参数为数字，false表示参数非数�?
     */
    public static boolean isNum(final String strNum) {
        return strNum.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    public final static String README = "在工程的AndridManifest.xml的application标签内添加如下代码：" +
            "<meta-data\n" +
            "            android:name=\"PGYER_APPID\"\n" +
            "            android:value=\"dbdb28ba4b51cae9e23eee34a142e87c \" />" +
            "其中value字段必须更换你自己的appid";
}
