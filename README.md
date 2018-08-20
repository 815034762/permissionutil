# permissionutil
一个可以在任意类捕捉到android 6.0之后的权限申请回调工具类，这样就可以不用局限于在activity和fragment里面做回调了，
比如用户在一个adapter里面做了权限申请，但是最终权限回调是在Activity里面或者是在Fragment里面，那么有时候就会很麻烦，
这里就提供了一个很好的解决方法。同时在权限回调的时候还可以传递参数。还做了用户勾选"不再提示"的处理。

### 用法 | Usage

1. 添加依赖
`compile 'com.cn.zty:permission:1.0.0'` 
例子:我们要在一个adapter的item里面请求一个录音的权限，如果成功了就把那个Item的值拿过来，一般情况下我们请求后，就不能和adapter点击的item有关系了。
因为中间有申请回调。而这时用我的这个框架就可以实现申请权限成功后，传递参数了。具体用法如下:
```   
public class MyRecycerviewAdapter extends RecyclerView.Adapter<MyRecycerviewAdapter.ViewHolder> {
    private Activity mContext;
    public MyRecycerviewAdapter(Activity mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String,Object> param = new HashMap<>();
                param.put("key","这是一个附带参数的权限请求");
             PermissionHelper.with(mContext).requestCode(PermissionConstants.CAMERA).executeObj(MyRecycerviewAdapter.this).setParam(param).requestPermission(Manifest.permission.CAMERA).request();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }

    @PermissionSucceed(requestCode = PermissionConstants.CAMERA)
    private void callStorageWrite(HashMap<String,Object> param) {
        Toast.makeText(mContext,"Adapter回调您已经同意了这个请求,请求参数是: " + param.get("key"),Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = PermissionConstants.CAMERA)
    private void callStorageWriteFail(HashMap<String,Object> param) {
        Toast.makeText(mContext,"Adapter回调您已经拒绝了这个请求,请求参数是: "+ param.get("key"),Toast.LENGTH_SHORT).show();
    }
}

```
然后再activity里面重写申请权限的回调
```
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
                    PermissionHelper.requestPermissionsResult(this, requestCode, permissions,this);
                    break;
                case PermissionConstants.CAMERA:
                    PermissionHelper.requestPermissionsResult(this, requestCode, permissions, adapter);
                    break;
            }
        }
    }
```
