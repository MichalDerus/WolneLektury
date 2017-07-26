package com.derus.wolnelektury;


import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.derus.wolnelektury.fragment.AuthorListFragment;
import com.derus.wolnelektury.fragment.BookDetailFragment;
import com.derus.wolnelektury.fragment.BookListFragment;
import com.derus.wolnelektury.fragment.EpochListFragment;
import com.derus.wolnelektury.receiver.ConnectivityReceiver;

import static com.derus.wolnelektury.R.id.toolbar;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Context context;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String TAG_AUTHOR_LIST_FRAGMENT = "author";
    public static final String TAG_BOOK_LIST_FRAGMENT = "book_list";
    public static final String TAG_BOOK_DETAIL_FRAGMENT = "book_detail";
    public static final String TAG_EPOCHS_LIST_FRAGMENT = "epochs_list";

    private ConnectivityReceiver connectivityReceiver;

    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private AppBarLayout actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(toolbar);
        setSupportActionBar(mToolbar);

        actionBar = (AppBarLayout) findViewById(R.id.app_bar_main);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        context = getApplicationContext();

        initActionBar();
        
        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putString(BookListFragment.ARGUMENT_FROM, BookListFragment.BUNDLE_DATABASE);

            Fragment fragment = new BookListFragment();
            fragment.setArguments(args);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment, TAG_BOOK_LIST_FRAGMENT)
                    .commit();
            clearBackStack();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityReceiver = new ConnectivityReceiver();
        this.registerReceiver(connectivityReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (connectivityReceiver != null) {
            this.unregisterReceiver(connectivityReceiver);
        }
    }

    @Override
    public void onBackPressed() {
        actionBar.setExpanded(true, true);
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    mNavigationView.getMenu().getItem(0).setChecked(true); //gdy wracamy do ekranu głównego zaznacza w drawer "moje ksiązki"
                }
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_save).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        String fragmentTag = null;
        clearBackStack();
        Bundle args;

        switch (id) {
            case R.id.nav_saved_books:
                args = new Bundle();
                args.putString(BookListFragment.ARGUMENT_FROM, BookListFragment.BUNDLE_DATABASE);
                fragment = new BookListFragment();
                fragment.setArguments(args);
                fragmentTag = TAG_BOOK_LIST_FRAGMENT;
                break;

            case R.id.nav_literature:
                args = new Bundle();
                args.putString(BookListFragment.ARGUMENT_FROM, BookListFragment.BUNDLE_ALL);
                fragment = new BookListFragment();
                fragment.setArguments(args);
                fragmentTag = TAG_BOOK_LIST_FRAGMENT;
                break;

            case R.id.nav_authors:
                fragment = new AuthorListFragment();
                fragmentTag = TAG_AUTHOR_LIST_FRAGMENT;
                break;

            case R.id.nav_epochs:
                fragment = new EpochListFragment();
                fragmentTag = TAG_EPOCHS_LIST_FRAGMENT;
                break;

        }
        replaceFragment(fragment, fragmentTag);
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragment(Fragment fragment, String fragmentTag) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
/*            if (fragment instanceof MyBooksFragment) { //nie dodaje go do backStacka
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                        .replace(R.id.frame_container, fragment, fragmentTag)
                        .commit();*/
            //} else {
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
                        .replace(R.id.frame_container, fragment, fragmentTag)
                        .addToBackStack(fragmentTag)
                        .commit();

            //}
        }
    }

    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void initActionBar() {
        //Sprawdzamy czy po zmianie orientacji ekranu jest wyświetlony BookDetailFragment
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (fragment != null && fragment instanceof BookDetailFragment) {
            showBackButton();
        }

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_BOOK_DETAIL_FRAGMENT);
                if (fragment != null && fragment instanceof BookDetailFragment && fragment.isVisible()) {
                    showBackButton();
                } else {
                    showDrawerMenuButton();
                }
            }
        });
    }

    private void showBackButton() {
        mToggle.setDrawerIndicatorEnabled(false);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// show back button
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void showDrawerMenuButton() {
        mToggle.setDrawerIndicatorEnabled(true);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mToggle.syncState();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });
    }
    public void showToolbar() {
        actionBar.setExpanded(true, true);
    }
}