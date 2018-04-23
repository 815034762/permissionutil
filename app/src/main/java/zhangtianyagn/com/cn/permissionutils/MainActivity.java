package zhangtianyagn.com.cn.permissionutils;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import permission.PermissionConstants;
import permission.PermissionFail;
import permission.PermissionHelper;
import permission.PermissionSucceed;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private MyRecycerviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new MyRecycerviewAdapter(MainActivity.this);

        recyclerView.setAdapter(adapter);


        findViewById(R.id.tv_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PermissionHelper.with(MainActivity.this).executeObj(MainActivity.this).requestCode(PermissionConstants.STORATE_WRITE).requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).request();
            }
        });

    }


    /****
     * 权限申请的权限结果，这个必须写
     ****/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        boolean hasAllGranted = true;
        for (int i = 0; i < grantResults.length; ++i) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                hasAllGranted = false;
                //在用户已经拒绝授权的情况下，如果shouldShowRequestPermissionRationale返回false则
                // 可以推断出用户选择了“不在提示”选项，在这种情况下需要引导用户至设置页手动授权
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                    //解释原因，并且引导用户至设置页手动授权
                    new AlertDialog.Builder(this)
                            .setMessage("【用户选择了不在提示按钮，或者系统默认不在提示（如MIUI）。" +
                                    "引导用户到应用设置页去手动授权,注意提示用户具体需要哪些权限】\r\n" +
                                    "获取相关权限失败:xxxxxx,将导致部分功能无法正常使用，需要到设置页面手动授权")
                            .setPositiveButton("去授权", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //引导用户至设置页手动授权
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //引导用户手动授权，权限请求失败
                                }
                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            //引导用户手动授权，权限请求失败
                        }
                    }).show();

                } else {
                    //权限请求失败，但未选中“不再提示”选项
                }
                break;
            }
        }
        if (hasAllGranted) {
            //权限请求成功
            switch (requestCode) {
                case PermissionConstants.STORATE_WRITE:
                    PermissionHelper.requestPermissionsResult(this, requestCode, permissions);
                    break;
                case PermissionConstants.CAMERA:
                    PermissionHelper.requestPermissionsResult(this, requestCode, permissions,adapter);
                    break;
            }
        }
    }

    /*************
     * Android 6.0的权限申请
     ************/
    @PermissionSucceed(requestCode = PermissionConstants.STORATE_WRITE)
    private void callStorageWrite() {

        Toast.makeText(MainActivity.this,"您已经同意了这个请求",Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = PermissionConstants.STORATE_WRITE)
    private void callStorageWriteFail() {

        Toast.makeText(MainActivity.this,"您已经拒绝了这个请求",Toast.LENGTH_SHORT).show();
    }

    /****************************************************end在线更新*************************************************/

}
