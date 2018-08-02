package zhangtianyagn.com.cn.permissionutils;

import android.Manifest;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.zhangtianyang.PermissionFail;
import com.cn.zhangtianyang.PermissionHelper;
import com.cn.zhangtianyang.PermissionSucceed;

import java.util.HashMap;

import permission.PermissionConstants;

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
