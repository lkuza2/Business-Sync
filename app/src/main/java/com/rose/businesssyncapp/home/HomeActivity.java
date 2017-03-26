package com.rose.businesssyncapp.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rose.businesssyncapp.LoginActivity;
import com.rose.businesssyncapp.R;
import com.rose.businesssyncapp.card.RegisterCardFragment;
import com.rose.businesssyncapp.contacts.AddContactFragment;
import com.rose.businesssyncapp.contacts.ConfirmContactActivity;
import com.rose.businesssyncapp.contacts.ViewContactsFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AddContactFragment.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        String email  = auth.getCurrentUser().getEmail();
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.login_email)).setText(email);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.login_name)).setText(auth.getCurrentUser().getDisplayName());
        database = FirebaseDatabase.getInstance().getReference();

        database.child("Users").child(auth.getCurrentUser().getUid()).child("email").setValue(email);

    }

    private void setFragment(Fragment fragment){
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // Create a new Fragment to be placed in the activity layout

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            fragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (id == R.id.nav_profile) {
            setFragment(new ProfileFragment());
            fab.hide();
        } else if (id == R.id.nav_register_card) {
            setFragment(new RegisterCardFragment());
            fab.hide();
        } else if (id == R.id.nav_add_contact) {
            setFragment(new ViewContactsFragment());
            fab.show();
        } else if (id == R.id.nav_sign_out) {
            auth.signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
