package cn.pwrd.ipulltorefreshrecyclersample.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import cn.pwrd.ipulltorefreshrecyclersample.sample.util.DividerItemDecoration;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.recycler.LoadMoreListener;
import com.handmark.pulltorefresh.library.recycler.PullToRefreshVerticalRecycler;
import com.handmark.pulltorefresh.library.recycler.animators.AnimatorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class IPullToRefreshRecyclerLinearActivity extends AppCompatActivity {
	public static Intent getStartIntent(Context context) {
		return new Intent(context, IPullToRefreshRecyclerLinearActivity.class);
	}

	private PullToRefreshVerticalRecycler<String> mRefreshRecycler;
	private Toolbar mTool;
	private DrawerLayout mDrawer;
	private NavigationView mNav;

	private MyRecyclerAdapter mAdapter;
	private int mLoadCount = 0;

	@Override
    @SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_refresh_recycler);
		mRefreshRecycler = (PullToRefreshVerticalRecycler) findViewById(R.id.pull_recycler);
		mTool = (Toolbar) findViewById(R.id.appbar);
		mDrawer = (DrawerLayout) findViewById(R.id.drawerLayout);
		mNav = (NavigationView) findViewById(R.id.drawerNav);
		initAppBar();

		initDrawer();

		initRecycler();
	}

	private void initDrawer() {
		mNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(MenuItem menuItem) {
				if (menuItem.getItemId() == R.id.pull) {
					mRefreshRecycler.setLoadMoreMode(PullToRefreshVerticalRecycler.LoadMoreMode.PULL);
				} else if (menuItem.getItemId() == R.id.load_more) {
					mRefreshRecycler.setLoadMoreMode(PullToRefreshVerticalRecycler.LoadMoreMode.INNER_REFRESH);
				} else {
					mRefreshRecycler.setAnimator(AnimatorUtil.getAnimator(menuItem.getTitle().toString()));
				}
				mDrawer.closeDrawer(GravityCompat.START);
				return true;
			}
		});

		ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, /*
																			 * host
																			 * Activity
																			 */
		mDrawer, /* DrawerLayout object */
		mTool,/* 绑定toolbar */
		R.string.open, /* "open drawer" description for accessibility */
		R.string.close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
			}

			public void onDrawerOpened(View drawerView) {

			}
		};
		mDrawerToggle.syncState();
		mDrawer.setDrawerListener(mDrawerToggle);
	}

	private void initRecycler() {
		mAdapter = new MyRecyclerAdapter(this);
		mRefreshRecycler.setAdapterAndLayoutManager(mAdapter, new LinearLayoutManager(this));
		mRefreshRecycler.setLoadMoreMode(PullToRefreshVerticalRecycler.LoadMoreMode.INNER_REFRESH);
		mRefreshRecycler.setCanMove(true);
		mRefreshRecycler.setCanSwipe(true);
		mRefreshRecycler.getRefreshableView().addItemDecoration(
				new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
		mRefreshRecycler.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
				mLoadCount = 0;
				mRefreshRecycler.clear();
				mAdapter.init();
				mRefreshRecycler.setHasNoMore(false);
				mRefreshRecycler.onRefreshComplete();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
				if (mLoadCount++ < 3) {
					List<String> list = new ArrayList<>(3);
					for (int j = 0; j < 5; j++)
						list.add("recycler_add_" + j);

					mRefreshRecycler.addList(list);
				} else {
					mRefreshRecycler.setHasNoMore(true);
				}

				mRefreshRecycler.onRefreshComplete();
			}
		}, new LoadMoreListener<String>() {
			@Override
			public List<String> getMoreDataList() {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
                    //do nothing
                    Thread.currentThread().interrupt();
				}

				if (mLoadCount++ < 3) {
					List<String> list = new ArrayList<>(3);

					for (int j = 0; j < 3; j++) {
						list.add("recycler_add_" + mLoadCount + j);
					}

					return list;
				}
				return null;
			}
		});
	}

	private void initAppBar() {
		setSupportActionBar(mTool);

		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
		}
	}

}
