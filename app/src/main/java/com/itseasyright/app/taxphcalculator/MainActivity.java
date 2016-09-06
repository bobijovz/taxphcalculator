package com.itseasyright.app.taxphcalculator;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Toast;

import com.itseasyright.app.taxphcalculator.databinding.ActivityMainBinding;
import com.itseasyright.app.taxphcalculator.databinding.ListItemContentBinding;
import com.itseasyright.app.taxphcalculator.databinding.ListItemHeaderBinding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    private ActivityMainBinding binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ExpandableMenuAdapter adapter = new ExpandableMenuAdapter(this);
        binder.listviewDetailsMenu.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        binder.btnCalculate.setOnClickListener(this);
        binder.btnReset.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_calculate:
                Toast.makeText(this,"try",Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_reset:

                break;
        }
    }

    public class ExpandableMenuAdapter extends BaseExpandableListAdapter {
        private String[] listDataHeader;
        private Integer[] listDataChild = new Integer[4];
        private Context context;

        public ExpandableMenuAdapter(Context context){
            this.context = context;
            this.listDataHeader = getResources().getStringArray(R.array.string_array_menu);
            this.listDataChild[0] = R.layout.fragment_basic_details;
            this.listDataChild[1] = R.layout.fragment_misc_details;
            this.listDataChild[1] = R.layout.fragment_allowance_details;
        }
/*
        public class ViewHolder {

            public ViewHolder(View itemView) {
            }
        }*/

        @Override
        public int getGroupCount() {
            return listDataHeader.length;
        }

        @Override
        public int getChildrenCount(int i) {
            return listDataChild.length;
        }

        @Override
        public Object getGroup(int i) {
            return listDataHeader[i];
        }

        @Override
        public Object getChild(int i, int i1) {
            return listDataChild[i1];
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            String headerTitle = (String) getGroup(i);
            ListItemHeaderBinding headerBinder;
            if (view == null){
                headerBinder = DataBindingUtil.inflate(getLayoutInflater(),R.layout.list_item_header,viewGroup,false);
            } else {
                headerBinder = DataBindingUtil.getBinding(view);
            }
            headerBinder.textHeader.setText(headerTitle);


            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            Integer layout = (Integer) getChild(i,i1);
            ListItemContentBinding contentBinder;
            if (view == null){
                contentBinder = DataBindingUtil.inflate(getLayoutInflater(), layout,viewGroup,false);
            } else {
                contentBinder = DataBindingUtil.getBinding(view);
            }

            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }








    }
}
