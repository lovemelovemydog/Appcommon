package com.common.app.test;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.common.app.R;
import com.common.app.activity.BaseActivity;
import com.common.app.common.AppCommonStatic;
import com.common.app.entity.Item;
import com.common.app.http.HttpClientHelper;
import com.common.app.image.ImageCache;
import com.common.app.image.ImageFetcher;
import com.common.app.image.ImageWorker;
import com.common.app.image.Utils;
import com.common.app.view.MyListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * this is a test activity that you can study
 *
 * how to use ImageFetcher to load image
 *
 * @google bitmapfun
 *
 */
public class ImageFetcherActivity extends BaseActivity {
    MyListView listView = null;
    ImageFetcherActivity  context = null;
    DataAdapter adapter = null;
    ImageFetcher mImageFetcher = null;

    //图片测试接口
    String url = "http://localhost:8080/snapshotMovie?action=getSnapshotMovie&start={start}&rows=20";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listView = (MyListView)findViewById(R.id.listview);
        listView.addFooterView(View.inflate(context, R.layout.foot_view, null));
        adapter = new DataAdapter(context, 0, new ArrayList<Item>());
        listView.setAdapter(adapter);
        listView.setFooterDividersEnabled(false);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
            }
        });


        ImageCache.ImageCacheParams cacheParams =
        new ImageCache.ImageCacheParams(this, AppCommonStatic.IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, 400, 400);//弱化程度
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);
        mImageFetcher.setLoadingImage(R.drawable.ic_launcher);

        getData ();
    }


    void getData () {
        new AsyncTask<Void, Void, List<Item>>() {
            @Override
            protected List<Item> doInBackground(Void... params) {
                int start = adapter.getCount();
                if (start > 0)
                    start--;
                System.out.println("adapter.getCount()="+start);
                String requestUrl = url.replace("{start}", start+"");
                try {
                    List<Item> list = new ArrayList<Item>();
                    String resultJson = HttpClientHelper.getStringFromGet(requestUrl);
                    JSONObject jsonObject = new JSONObject(resultJson);
                    int resultCode = jsonObject.has("resultCode") ? jsonObject.getInt("resultCode") : 0;
                    if(resultCode != 1){
                        return list;
                    }
//                    int resultTotal = jsonObject.has("resultTotal") ? jsonObject.getInt("resultTotal") : 0;
//                    //回复的总数量
//                    if(resultTotal == 0) {
//                        return list;
//                    }
                    String resultMsg = jsonObject.has("resultMsg") ? jsonObject.getString("resultMsg") : "";
                    if(resultMsg.equals("")){
                        return list;
                    }
                    JSONArray jsonArray = jsonObject.has("resultData") ? jsonObject.getJSONArray("resultData") : null;
                    if(jsonArray != null && jsonArray.length() > 0){
                        Item item = null;
                        for(int i = 0;i < jsonArray.length();++i) {
                            item = new Item();
                            JSONObject eachItem = (JSONObject) jsonArray.get(i);
                            if (eachItem.has("pic"))
                                item.setImageUrl(resultMsg+eachItem.getString("pic"));
                            if (eachItem.has("picwidth"))
                                item.setWidth(eachItem.getInt("picwidth"));
                            if (eachItem.has("picheigth"))
                                item.setHeight(eachItem.getInt("picheigth"));



                            list.add(item);
                        }
                    }
                    return list;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(List<Item> items) {
                super.onPostExecute(items);
                if (items == null)
                    return;

                for (Item item : items) {
                    adapter.add(item);
                }
                adapter.notifyDataSetChanged();
                Toast.makeText(context, ""+adapter.getCount(), Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    class DataAdapter extends ArrayAdapter<Item> {

        DataAdapter(Context context, int resource, List<Item> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item, null);

                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView)convertView.findViewById(R.id.imageView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            Item item = getItem(position);
            ViewGroup.LayoutParams lp =  viewHolder.imageView.getLayoutParams();
            lp.width = screenWidth/2;
            lp.height = (int) (lp.width *  item.getHeight() / item.getWidth());
            viewHolder.imageView.setLayoutParams(lp);

            mImageFetcher.loadImage(item.getImageUrl(), viewHolder.imageView);

            /**
             * you can do some thing in callback method
             */
            mImageFetcher.setOnLoadCallBackListenering(new ImageWorker.OnLoadCallBackListenering() {
                @Override
                public void onPostExecute(BitmapDrawable value, ImageView imageView) {
                    if (value != null && imageView != null) {
//                        hideprogressbar();
                        System.out.println("callback--"+position);
                    }
                }
            });
            if (position == getCount() - 1) {
                getData ();
            }
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
        }
    }
}
