package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import java.util.Objects;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ROW_ID = "id";
    private static final String SCROLL_POS = "scroll_save";
    private long objId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.share_action_message), Snackbar.LENGTH_LONG).show();
            }
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                objId = ItemsContract.Items.getItemId(getIntent().getData());
            }
        }else{
            objId = savedInstanceState.getLong(ROW_ID);
        }
        Bundle bundle = new Bundle();
        bundle.putLong(ROW_ID, objId );
        getLoaderManager().initLoader(0, bundle, this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putLong(ROW_ID, objId);
        outState.putIntArray(SCROLL_POS,
                new int[]{ findViewById(R.id.mybook).getScrollX(), findViewById(R.id.mybook).getScrollY()}
                );
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if( savedInstanceState.getIntArray(SCROLL_POS) != null) {
            findViewById(R.id.mybook).scrollTo(
                    savedInstanceState.getIntArray(SCROLL_POS)[0], savedInstanceState.getIntArray(SCROLL_POS)[1]
            );
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        long id = bundle.getLong(ROW_ID);
        return ArticleLoader.newInstanceForItemId(this, id);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        if( cursor != null && cursor.moveToFirst() ) {
            final String title  = cursor.getString(ArticleLoader.Query.TITLE);
            String date   = cursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            String author = cursor.getString(ArticleLoader.Query.AUTHOR);
            String body   = cursor.getString(ArticleLoader.Query.BODY);
            String image  = cursor.getString(ArticleLoader.Query.PHOTO_URL);

            //todo, is this the final layout ? any other info from cursor ?
            // LOOK UP
            TextView tvBody = findViewById(R.id.body);
            final ImageView imageView = findViewById(R.id.image);

            // SET VALUES
            Objects.requireNonNull(getSupportActionBar()).setTitle(title);
            tvBody.setText(body);
            ImageLoader helper = ImageLoaderHelper.getInstance(this).getImageLoader();
            helper.get(image, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    imageView.setImageBitmap(imageContainer.getBitmap());
                    imageView.setContentDescription(title);
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    //todo network error
                }
            });
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {}

}
