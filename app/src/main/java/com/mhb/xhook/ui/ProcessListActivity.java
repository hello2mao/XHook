package com.mhb.xhook.ui;


import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.mhb.xhook.base.activities.BaseActivity;
import com.mhb.xhook.viewmodel.ProcessListViewModel;

public class ProcessListActivity extends BaseActivity implements ProcessListViewModel.DataListener {

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final String TAG = "ProcessListActivity";
    private ActivityProcessListBinding mBinding;
    private ProcessListViewModel mViewModel;

    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
//        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.mBinding = DataBindingUtil.setContentView(this, 2130968604);
        this.mViewModel = new ProcessListViewModel((Context)this, (ProcessListViewModel.DataListener)this);
        this.mBinding.setViewModel(this.mViewModel);
        if (ContextCompat.checkSelfPermission((Context)this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            PluginManager.getInstance().init((Context)this);
            return;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            return;
        }
        ActivityCompat.requestPermissions(this, new String[] { "android.permission.WRITE_EXTERNAL_STORAGE" }, 112);
    }

    
    public void onProcessInfosChanged(final List<ProcessInfo> list) {
    }
    
    public void onRequestPermissionsResult(final int n, final String[] array, final int[] array2) {
        super.onRequestPermissionsResult(n, array, array2);
        switch (n) {
            default: {}
            case 112: {
                if (array2.length > 0 && array2[0] == 0) {
                    PluginManager.getInstance().init((Context)this);
                    return;
                }
                Toast.makeText((Context)this, (CharSequence)"\u65e0\u6cd5\u83b7\u53d6WRITE_EXTERNAL_STORAGE\u6743\u9650", 1).show();
            }
        }
    }
    
    protected void onResume() {
        super.onResume();
        this.mViewModel.refresh();
    }
}
