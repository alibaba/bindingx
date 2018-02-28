/**
 * Copyright 2018 Alibaba Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.android.bindingx.playground.weex;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.qrcode.extension.core.HistoryManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends BaseNavigationActivity {
    private RecyclerView mList;
    private Adapter mAdapter;
    private HistoryManager mHistoryManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        setupToolbar((Toolbar) findViewById(R.id.my_toolbar));

        setActionBarTitle("Scan History");

        mHistoryManager = new HistoryManager(this);

        mList = (RecyclerView) findViewById(R.id.list);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Adapter(this, mHistoryManager);
        mList.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
    }

    void fetchData() {
        new AsyncTask<Void,Void,List<HistoryManager.HistoryItem>>() {

            @Override
            protected List<HistoryManager.HistoryItem> doInBackground(Void... params) {
                List<HistoryManager.HistoryItem> items = new ArrayList<>();
                if(mHistoryManager != null) {
                    items.addAll(mHistoryManager.getAllItems());
                }
                return items;
            }

            @Override
            protected void onPostExecute(List<HistoryManager.HistoryItem> historyItems) {
                if(historyItems.isEmpty()) {
                    Toast.makeText(HistoryActivity.this, "No records found", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mAdapter != null) {
                    Collections.reverse(historyItems);
                    mAdapter.addAll(historyItems);
                }
            }
        }.execute();
    }

    static class Adapter extends RecyclerView.Adapter<ViewHolder> implements OnItemLongClickListener {

        private List<HistoryManager.HistoryItem> mItems;
        private HistoryManager manager;
        private Context mContext;
        Adapter(Context context, HistoryManager manager) {
            mItems = new ArrayList<>();
            this.manager = manager;
            this.mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan_history,parent, false), this);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            HistoryManager.HistoryItem item = mItems.get(position);
            holder.bind(position, item);
        }

        void addAll(List<HistoryManager.HistoryItem> items) {
            this.mItems.clear();
            this.mItems.addAll(items);
            this.notifyDataSetChanged();
        }

        void remove(int position) {
            if(mItems == null) {
                return;
            }
            if(position >= 0 && position <= mItems.size()-1) {
                mItems.remove(position);
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public void onItemLongClick(final int position, final HistoryManager.HistoryItem item) {
            new AlertDialog.Builder(mContext)
                    .setTitle("Notice")
                    .setMessage("Do you want to delete this record?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            performDelete(position,item);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }


        void performDelete(int position, HistoryManager.HistoryItem item) {
            if(item != null && !TextUtils.isEmpty(item.item)) {
                String itemDetail = item.item;
                if(manager != null) {
                    if(manager.removeItem(itemDetail)) {
                        this.remove(position);
                        this.notifyItemRemoved(position);
                    } else {
                        Toast.makeText(mContext, "Delete Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mItemName;
        private int position;
        private HistoryManager.HistoryItem item;

        ViewHolder(View itemView, final OnItemLongClickListener listener) {
            super(itemView);
            mItemName = (TextView) itemView.findViewById(R.id.item);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(listener != null) {
                        listener.onItemLongClick(position, item);
                    }
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(item != null && !TextUtils.isEmpty(item.item)) {
                        WXActivity.start(v.getContext(),item.item);
                    }
                }
            });
        }

        void bind(int position, HistoryManager.HistoryItem item) {
            this.item = item;
            this.position = position;
            mItemName.setText(item.item);
        }
    }

    interface OnItemLongClickListener {
        void onItemLongClick(int position, HistoryManager.HistoryItem item);
    }
}
