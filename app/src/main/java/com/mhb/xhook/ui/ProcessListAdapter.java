// 
// Decompiled by Procyon v0.5.30
// 

package com.mhb.xhook.ui;

import com.hyxbiao.nut.viewmodel.ProcessItemViewModel;
import android.view.View;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import com.hyxbiao.nut.databinding.ProcessListItemBinding;
import android.view.ViewGroup;
import java.util.Collections;
import com.hyxbiao.nut.process.ProcessInfo;
import java.util.List;
import android.support.v7.widget.RecyclerView;

public class ProcessListAdapter extends Adapter<ViewHolder>
{
    private List<ProcessInfo> mProcessInfos;
    
    public ProcessListAdapter() {
        this.mProcessInfos = Collections.emptyList();
    }
    
    @Override
    public int getItemCount() {
        return this.mProcessInfos.size();
    }
    
    public void onBindViewHolder(final ViewHolder viewHolder, final int n) {
        viewHolder.bindViewModel(this.mProcessInfos.get(n));
    }
    
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), 2130968619, viewGroup, false));
    }
    
    public void setProcessInfos(final List<ProcessInfo> mProcessInfos) {
        this.mProcessInfos = mProcessInfos;
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        final ProcessListItemBinding binding;
        
        public ViewHolder(final ProcessListItemBinding binding) {
            super((View)binding.itemView);
            this.binding = binding;
        }
        
        void bindViewModel(final ProcessInfo processInfo) {
            if (this.binding.getViewModel() == null) {
                this.binding.setViewModel(new ProcessItemViewModel(this.itemView.getContext(), processInfo));
            }
            else {
                this.binding.getViewModel().setProcessInfo(processInfo);
            }
            this.binding.executePendingBindings();
        }
    }
}
