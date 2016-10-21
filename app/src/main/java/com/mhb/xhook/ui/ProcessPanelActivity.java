// 
// Decompiled by Procyon v0.5.30
// 

package com.mhb.xhook.ui;

import android.view.MenuItem;
import android.view.Menu;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import java.io.Serializable;
import android.content.Intent;
import com.hyxbiao.nut.process.ProcessInfo;
import android.content.Context;
import android.widget.Toast;
import android.util.Log;
import com.hyxbiao.nut.process.Shell;
import android.os.Build$VERSION;
import com.hyxbiao.nut.viewmodel.ProcessPanelViewModel;
import com.hyxbiao.nut.databinding.ActivityProcessPanelBinding;
import android.app.Activity;

public class ProcessPanelActivity extends Activity
{
    private static final String KEY_PROCESSINFO = "ProcessInfo";
    private static final String TAG = "InjectProcessActivity";
    private ActivityProcessPanelBinding mBinding;
    private ProcessPanelViewModel mViewModel;
    
    private void grantPermission() {
        final String packageName = this.getPackageName();
        if (this.getPackageManager().checkPermission("android.permission.READ_LOGS", packageName) != 0 && Build$VERSION.SDK_INT >= 16) {
            try {
                if (Shell.runAsRoot(String.format("pm grant %s android.permission.READ_LOGS", packageName)) != 0) {
                    throw new Exception("failed to become root");
                }
            }
            catch (Exception ex) {
                Log.d("InjectProcessActivity", "error: " + ex);
                Toast.makeText((Context)this, (CharSequence)"Failed to obtain READ_LOGS permission", 1).show();
            }
        }
    }
    
    public static void start(final Context context, final ProcessInfo processInfo) {
        final Intent intent = new Intent();
        intent.setClass(context, (Class)ProcessPanelActivity.class);
        intent.putExtra("ProcessInfo", (Serializable)processInfo);
        context.startActivity(intent);
    }
    
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.mBinding = DataBindingUtil.setContentView(this, 2130968605);
        final ProcessInfo processInfo = (ProcessInfo)this.getIntent().getSerializableExtra("ProcessInfo");
        if (processInfo == null) {
            Log.w("InjectProcessActivity", "no ProcessInfo");
            return;
        }
        this.mViewModel = new ProcessPanelViewModel((Context)this, processInfo);
        this.mBinding.setViewModel(this.mViewModel);
        this.setTitle((CharSequence)processInfo.getAppName());
        this.grantPermission();
    }
    
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.getMenuInflater().inflate(2131623939, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            default: {
                return super.onOptionsItemSelected(menuItem);
            }
            case 2131558522: {
                this.mViewModel.onRefreshClick();
                break;
            }
            case 16908332: {
                this.finish();
                break;
            }
        }
        return true;
    }
}
