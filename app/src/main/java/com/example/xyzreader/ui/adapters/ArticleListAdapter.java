package com.example.xyzreader.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.ui.ImageLoaderHelper;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ViewHolder> {

    private Cursor cursor;
    private Context context;

    public ArticleListAdapter(Context context, Cursor cursor) {

        this.cursor = cursor;
        this.context = context;

    }

    @Override
    public long getItemId(int position) {

        cursor.moveToPosition(position);
        return cursor.getLong(ArticleLoader.Query._ID);

    }

    @Override
    public ArticleListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.list_item_article, parent, false);
        return new ViewHolder(row);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final String title  = cursor.getString(ArticleLoader.Query.TITLE);
        final String author = cursor.getString(ArticleLoader.Query.AUTHOR);
        final String image  = cursor.getString(ArticleLoader.Query.THUMB_URL);
        final long id       = cursor.getLong(ArticleLoader.Query._ID);

        cursor.moveToPosition(position);

        holder.titleView.setText(title);
        holder.subtitleView.setText(author);
        holder.book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        ItemsContract.Items.buildItemUri(id)
                );
                context.startActivity(intent);
            }
        });

        ImageLoader helper = ImageLoaderHelper.getInstance(context).getImageLoader();
        helper.get(image, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                holder.thumbnailView.setImageBitmap(imageContainer.getBitmap());
                holder.thumbnailView.setContentDescription(title);
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //todo network error
            }
        });

    }

    @Override
    public int getItemCount() {

        return cursor.getCount();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;
        public View book;
    
        public ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
            book = view.findViewById(R.id.book);
        }

    }

}
