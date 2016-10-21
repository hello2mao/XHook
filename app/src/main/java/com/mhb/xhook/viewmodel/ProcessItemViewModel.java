// 
// Decompiled by Procyon v0.5.30
// 

package com.mhb.xhook.viewmodel;

import rx.functions.Action1;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import com.hyxbiao.nut.NutApplication;
import rx.Observable;
import android.view.View;
import android.databinding.Bindable;
import android.content.pm.PackageManager$NameNotFoundException;
import android.graphics.drawable.Drawable;
import com.hyxbiao.nut.ui.ProcessPanelActivity;
import com.hyxbiao.nut.inject.InjectManager;
import android.widget.Toast;
import com.hyxbiao.nut.inject.InjectProcess;
import rx.Subscription;
import com.hyxbiao.nut.process.ProcessInfo;
import android.content.pm.PackageManager;
import android.content.Context;
import android.databinding.BaseObservable;

public class ProcessItemViewModel extends BaseObservable implements BaseViewModel
{
    private boolean mClickEnabled;
    private Context mContext;
    private PackageManager mPackageManager;
    private ProcessInfo mProcessInfo;
    private Subscription mSubscription;
    
    public ProcessItemViewModel(final Context mContext, final ProcessInfo mProcessInfo) {
        this.mContext = mContext;
        this.mProcessInfo = mProcessInfo;
        this.mPackageManager = mContext.getApplicationContext().getPackageManager();
        this.mClickEnabled = true;
    }
    
    @Override
    public void destroy() {
        if (this.mSubscription != null && !this.mSubscription.isUnsubscribed()) {
            this.mSubscription.unsubscribe();
        }
        this.mSubscription = null;
        this.mContext = null;
    }
    
    public Drawable getAppIcon() {
        try {
            return this.mPackageManager.getApplicationInfo(this.mProcessInfo.getPackageName(), 128).loadIcon(this.mPackageManager);
        }
        catch (PackageManager$NameNotFoundException ex) {
            return null;
        }
    }
    
    public String getName() {
        return this.mProcessInfo.getAppName() + " [" + this.mProcessInfo.getPid() + "]";
    }
    
    @Bindable
    public boolean isClickEnabled() {
        return this.mClickEnabled;
    }
    
    public int isClickVisibility() {
        if (this.mProcessInfo.getPackageName().equals(this.mContext.getPackageName())) {
            return 8;
        }
        return 0;
    }
    
    public void onEnterClick(final View view) {
        this.setClickEnabled(false);
        this.mSubscription = rx.Observable.just(this.mProcessInfo).observeOn(NutApplication.get(this.mContext).defaultSubscribeScheduler()).map((Func1<? super ProcessInfo, ?>)ProcessItemViewModel$$Lambda$1.lambdaFactory$(this)).observeOn(AndroidSchedulers.mainThread()).subscribe(ProcessItemViewModel$$Lambda$4.lambdaFactory$(this));
    }
    
    public void setClickEnabled(final boolean mClickEnabled) {
        this.mClickEnabled = mClickEnabled;
        this.notifyPropertyChanged(1);
    }
    
    public void setProcessInfo(final ProcessInfo mProcessInfo) {
        this.mProcessInfo = mProcessInfo;
        this.notifyChange();
    }
}
