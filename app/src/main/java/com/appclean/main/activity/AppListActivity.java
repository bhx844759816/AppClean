package com.appclean.main.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.activity.YWBaseActivity;
import com.app.utils.PermissionReq;
import com.appclean.main.R;
import com.appclean.main.common.AppInfo;
import com.appclean.main.message.AppInfoMessage;
import com.appclean.main.manager.FileManager;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class AppListActivity extends YWBaseActivity {
    private RecyclerView mRecyclerView;
    private List<AppInfo> mAppInfoList;

    @Override
    protected void onAfterCreate(Bundle savedInstanceState) {
        super.onAfterCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        mRecyclerView = findViewById(R.id.recyclerView);
        EventBus.getDefault().register(this);
        PermissionReq.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onGetAppInfoList(AppInfoMessage appInfoMessage){
        mAppInfoList = appInfoMessage.mAppInfoList;
        mRecyclerView.setAdapter(new MyAdapter());
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(AppListActivity.this).inflate(R.layout.adapter_app_list_item, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
            AppInfo appInfo = mAppInfoList.get(i);
            myViewHolder.appName.setText(appInfo.getAppName());
            Glide.with(AppListActivity.this).load(appInfo.getAppIcon()).into(myViewHolder.appIcon);
            myViewHolder.appSize.setText("应用大小=" + appInfo.getAppSize());
            myViewHolder.appSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        unInstallApp(appInfo);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mAppInfoList.size();
        }
    }

    private void unInstallApp(AppInfo appInfo) {
        new AlertDialog.Builder(this).setMessage("是否删除这个APP")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileManager.getInstance().unInstallApp(AppListActivity.this, appInfo.getPkgName());
//                        FileManager.getInstance().unInstall(appInfo.getPkgName());
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView appSize;
        CheckBox appSelect;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.ivAppIcon);
            appName = itemView.findViewById(R.id.tvAppName);
            appSize = itemView.findViewById(R.id.tvAppSize);
            appSelect = itemView.findViewById(R.id.cbSelect);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}