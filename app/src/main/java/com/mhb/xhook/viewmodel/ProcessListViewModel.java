package com.mhb.xhook.viewmodel;


import android.content.Context;
import android.widget.CompoundButton;

import com.mhb.xhook.ui.ProcessListAdapter;

import java.util.Collections;

public class ProcessListViewModel implements BaseViewModel
{
    public final ProcessListAdapter adapter;
    private Context mContext;
    private List<ProcessInfo> mDataList;
    private DataListener mDataListener;
    public final ObservableBoolean refreshing;
    
    public ProcessListViewModel(final Context mContext, final DataListener mDataListener) {
        this.refreshing = new ObservableBoolean(false);
        this.mContext = mContext;
        this.mDataListener = mDataListener;
        this.adapter = new ProcessListAdapter();
        this.mDataList = Collections.emptyList();
    }
    
    @Override
    public void destroy() {
    }
    
    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this.mContext);
    }
    
    public boolean isShowFloatWindow() {
        return TimerTaskService.isRunning();
    }
    
    public CompoundButton$OnCheckedChangeListener onCheckedChangeListener() {
        return (CompoundButton$OnCheckedChangeListener)new CompoundButton$OnCheckedChangeListener() {
            public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
                if (b) {
                    TimerTaskService.start(ProcessListViewModel.this.mContext);
                    return;
                }
                TimerTaskService.stop(ProcessListViewModel.this.mContext);
            }
        };
    }
    
    public SwipeRefreshLayout.OnRefreshListener onRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ProcessListViewModel.this.refresh();
            }
        };
    }
    
    public void refresh() {
        this.refreshing.set(true);
        this.mDataList.clear();
        this.mDataList = ProcessManager.getRunningApps(this.mContext);
        this.adapter.setProcessInfos(this.mDataList);
        ((RecyclerView.Adapter)this.adapter).notifyDataSetChanged();
        this.refreshing.set(false);
        this.mDataListener.onProcessInfosChanged(this.mDataList);
    }
    
    public interface DataListener
    {
        void onProcessInfosChanged(final List<ProcessInfo> p0);
    }
}
