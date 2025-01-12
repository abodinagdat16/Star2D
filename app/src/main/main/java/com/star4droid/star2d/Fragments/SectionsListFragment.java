package com.star4droid.star2d.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.LinearLayout;
import com.star4droid.star2d.evo.R;
import java.util.HashMap;
import java.util.ArrayList;
import androidx.viewpager2.widget.ViewPager2;

public class SectionsListFragment extends Fragment {
	ViewPager2 viewPager2;
	ArrayList<HashMap<String, Object>> list= new ArrayList<>();
	LinearLayout linear;
	public SectionsListFragment(){}
	
	
	public SectionsListFragment(ViewPager2 pager2){
		super();
		this.viewPager2 = pager2;
		push("Properties",R.drawable.properties);
		push("Events",R.drawable.mouse_click);
		push("Joints",R.drawable.collision_end_icon);
		
	}
	
	public void push(String name,int icon){
		HashMap<String,Object> hashMap= new HashMap<>();
		hashMap.put("name",name);
		hashMap.put("icon",icon);
		list.add(hashMap);
	}
	
	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	}
	
	@Override
	public void onAttach(Context arg0) {
		super.onAttach(arg0);
		wrap();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		wrap();
	}
	
	@Override
	public View onCreateView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		RecyclerView recyclerView= new RecyclerView(getContext());
		linear = new LinearLayout(getContext());
		if(viewPager2==null) {
			viewPager2 = ((Activity)getContext()).findViewById(R.id.viewPager);
			if(viewPager2==null) return linear;
		}
		recyclerView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
		//linear.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,99999));
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setAdapter(new RecyclerAdapter(list,viewPager2));
		linear.addView(recyclerView);
		wrap();
		return linear;
	}
	
	public void wrap(){
		try {
			if(linear!=null)
				linear.setMinimumHeight(getContext().getResources().getDisplayMetrics().heightPixels*5);
		} catch(Exception ex){}
	}
	
	public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
		
		ArrayList<HashMap<String, Object>> _data;
		Context context;
		ViewPager2 viewPager2;
		
		public RecyclerAdapter(ArrayList<HashMap<String, Object>> _arr,ViewPager2 pager2) {
			_data = _arr;
			context = pager2.getContext();
			viewPager2 = pager2;
		}
		
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater _inflater = LayoutInflater.from(context);
			View _v = _inflater.inflate(R.layout.sections_list_csv, null);
			RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			_v.setLayoutParams(_lp);
			return new ViewHolder(_v);
		}
		
		@Override
		public void onBindViewHolder(ViewHolder _holder,  int pos) {
			View _view = _holder.itemView;
	    	int _position = _holder.getAdapterPosition();
			final LinearLayout linear1 = _view.findViewById(R.id.linear1);
			final ImageView icon = _view.findViewById(R.id.icon);
			final LinearLayout linear3 = _view.findViewById(R.id.linear3);
			final TextView name = _view.findViewById(R.id.name);
			icon.setImageDrawable(context.getDrawable((int)(_data.get(_position).get("icon"))));
			name.setText(_data.get(_position).get("name").toString());
			_view.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					viewPager2.setCurrentItem(_position+1);
				}
			});
		}
		
		@Override
		public int getItemCount() {
			return _data.size();
		}
		
		public class ViewHolder extends RecyclerView.ViewHolder {
			public ViewHolder(View v) {
				super(v);
			}
		}
	}
	
}