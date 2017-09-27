package com.example.huangming.swipelayout;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new Adapter(this));
    }

    public class Adapter extends BaseAdapter {
        Context mContext;

        /** 当前滑出的列表项 */
        private SwipeLayout currentSwipe;

        public Adapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
            TextView tvDelete = convertView.findViewById(R.id.tvDelete);
            TextView tvEdit = convertView.findViewById(R.id.tvEdit);
            RelativeLayout secondChild =  convertView.findViewById(R.id.secondChild);
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) secondChild
                .getLayoutParams();
            if (position == 1 || position == 3) {
                p.width = getResources().getDimensionPixelSize(R.dimen.width_one_child);
                ((TextView) convertView.findViewById(R.id.tvContent)).setText("我的右边只有一个按钮");
                tvEdit.setVisibility(View.GONE);
            } else {
                p.width = getResources().getDimensionPixelSize(R.dimen.width_two_child);
                ((TextView) convertView.findViewById(R.id.tvContent)).setText("请把我滑到左边");
                tvEdit.setVisibility(View.VISIBLE);
            }
            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "你点击了删除按钮", Toast.LENGTH_SHORT).show();
                }
            });

            tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "你点击了编辑按钮", Toast.LENGTH_SHORT).show();
                }
            });
            final SwipeLayout temp = convertView.findViewById(R.id.swipeLayout);
            temp.setOnSwipelayoutOpenedListener(new SwipeLayout.OnSwipeLayoutOpenedListener() {
                @Override
                public void onSwipeLayoutOpened() {
                    if (currentSwipe != null && !currentSwipe.equals(temp)) {
                        //滑出一个item后恢复上一个已滑出的item
                        currentSwipe.close();
                    }
                    currentSwipe = temp;
                }
            });
            return convertView;
        }
    }
}
