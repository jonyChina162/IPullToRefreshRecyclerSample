package cn.pwrd.ipulltorefreshrecyclersample.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	private RecyclerView mRecycler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mRecycler = (RecyclerView) findViewById(R.id.recycler);
		mRecycler.setAdapter(new Adapter(this));
		mRecycler.setLayoutManager(new LinearLayoutManager(this));
	}

	private static class Adapter extends RecyclerView.Adapter {
		private Context context;
		private List<String> data = new ArrayList<>();
		private View.OnClickListener listener;

		public Adapter(final Context context) {
			data.add("refresh");
			this.context = context;
			listener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					switch ((String) v.getTag()) {
					case "refresh":
						context.startActivity(IPullToRefreshRecyclerLinearActivity.getStartIntent(context));
						break;
					default:
						// nothing
						break;
					}
				}
			};
		}

		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
			return new Holder(LayoutInflater.from(context).inflate(R.layout.recycler_item_text, viewGroup, false));
		}

		@Override
		public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
			TextView textView = ((Holder) viewHolder).textView;
			textView.setText(data.get(i));
			textView.setTag(data.get(i));
			textView.setOnClickListener(listener);
		}

		@Override
		public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
			holder.itemView.setOnClickListener(null);
			super.onViewDetachedFromWindow(holder);
		}

		@Override
		public int getItemCount() {
			return data.size();
		}

		private class Holder extends BaseViewHolder {
			TextView textView;

			public Holder(View itemView) {
				super(itemView);
				textView = (TextView) itemView.findViewById(R.id.text);

			}
		}
	}

}
