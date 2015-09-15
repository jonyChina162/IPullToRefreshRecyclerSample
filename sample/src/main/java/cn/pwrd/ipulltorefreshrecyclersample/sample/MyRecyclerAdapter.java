package cn.pwrd.ipulltorefreshrecyclersample.sample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.handmark.pulltorefresh.library.recycler.SimpleLoadMoreAdapter;

import java.util.List;

public class MyRecyclerAdapter extends SimpleLoadMoreAdapter<RecyclerView.ViewHolder, String> {
	private Context mContext;
	private List<String> mContents;

	public MyRecyclerAdapter(Context mContext) {
		this.mContext = mContext;
		mContents = getContents();
		init();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item_text, viewGroup, false);
		return new Holder(view);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
		((Holder) viewHolder).textView.setText(mContents.get(i));
	}

	@Override
	public int getItemCount() {
		return mContents.size();
	}

	public void init() {
		for (int i = 0; i < 5; i++) {
			mContents.add("RecyclerView_" + i);
		}
	}

	private class Holder extends BaseViewHolder {
		TextView textView;

		public Holder(View itemView) {
			super(itemView);
			textView = (TextView) itemView.findViewById(R.id.text);
		}
	}
}
