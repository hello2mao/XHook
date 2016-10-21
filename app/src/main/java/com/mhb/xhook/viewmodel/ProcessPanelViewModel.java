// 
// Decompiled by Procyon v0.5.30
// 

package com.mhb.xhook.viewmodel;

import android.content.Intent;
import com.hyxbiao.nut.process.ProcessManager;
import android.view.View;
import android.view.View$OnClickListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.graphics.drawable.Drawable;
import com.hyxbiao.nutplugin.PluginManager;
import com.hyxbiao.nutplugin.RemotePluginLoader;
import com.hyxbiao.nut.process.ProcessInfo;
import com.hyxbiao.nutplugin.PluginLoader;
import android.content.Context;
import com.hyxbiao.nut.ui.PluginListAdapter;

public class ProcessPanelViewModel implements BaseViewModel
{
    public final PluginListAdapter adapter;
    private Context mContext;
    private PluginLoader mPluginLoader;
    private ProcessInfo mProcessInfo;
    
    public ProcessPanelViewModel(final Context mContext, final ProcessInfo mProcessInfo) {
        this.mContext = mContext;
        this.mProcessInfo = mProcessInfo;
        this.mPluginLoader = new RemotePluginLoader(mProcessInfo.getPackageName(), mProcessInfo.getPid());
        PluginManager.getInstance().reload();
        this.adapter = new PluginListAdapter(mContext, PluginManager.getInstance().getPluginList(), mProcessInfo, this.mPluginLoader);
    }
    
    @Override
    public void destroy() {
    }
    
    public Drawable getAppIcon() {
        final PackageManager packageManager = this.mContext.getApplicationContext().getPackageManager();
        try {
            return packageManager.getApplicationInfo(this.mProcessInfo.getPackageName(), 128).loadIcon(packageManager);
        }
        catch (PackageManager$NameNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public String getAppText() {
        return "\u8fdb\u7a0bID\uff1a" + this.mProcessInfo.getPid() + "\n\u8fdb\u7a0b\u540d\uff1a" + this.mProcessInfo.getProcessName() + "\n\u5e94\u7528\u540d\uff1a" + this.mProcessInfo.getAppName();
    }
    
    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this.mContext);
    }
    
    public String getPluginListTitle() {
        return "\u63d2\u4ef6\u5217\u8868\uff08\u4e2a\u6570\uff1a" + PluginManager.getInstance().getPluginListSize() + "\uff09";
    }
    
    public String getPluginPath() {
        return "\u63d2\u4ef6\u8def\u5f84\uff1a" + PluginManager.getInstance().getPluginDir();
    }
    
    public View$OnClickListener onAppIconClickListener() {
        return (View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                final String packageName = ProcessPanelViewModel.this.mProcessInfo.getPackageName();
                final String runningTaskClassName = ProcessManager.getRunningTaskClassName(ProcessPanelViewModel.this.mContext, packageName);
                if (runningTaskClassName != null) {
                    ProcessPanelViewModel.this.mContext.startActivity(new Intent().setClassName(packageName, runningTaskClassName).addFlags(268435456));
                }
            }
        };
    }
    
    public void onRefreshClick() {
        PluginManager.getInstance().reload();
        this.adapter.setDataList(PluginManager.getInstance().getPluginList());
        ((RecyclerView.Adapter)this.adapter).notifyDataSetChanged();
    }
    
    public View$OnClickListener onRefreshClickListener() {
        return (View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                ProcessPanelViewModel.this.onRefreshClick();
            }
        };
    }
}
