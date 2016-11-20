package app.androidhive.info.realm.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import app.androidhive.info.realm.app.Prefs;
import app.androidhive.info.realm.R;
import app.androidhive.info.realm.adapters.BooksAdapter;
import app.androidhive.info.realm.adapters.RealmBooksAdapter;
import app.androidhive.info.realm.model.Book;
import app.androidhive.info.realm.realm.RealmController;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private BooksAdapter adapter;
    private Realm realm;
    private LayoutInflater inflater;
    private FloatingActionButton fab;
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        final boolean cameraGranted = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        final boolean audioGranted = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        final boolean accessNetworkStateGranted = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
        final boolean readPhoneStateGranted = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        final boolean readGServicesGranted = ContextCompat.checkSelfPermission(getApplicationContext(), "com.google.android.providers.gsf.permission.READ_GSERVICES") == PackageManager.PERMISSION_GRANTED;
        final boolean writeExternalStorageGranted = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        final boolean accessFineLocation = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        final boolean accessCoarseLocation = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        ArrayList<String> permissions= new ArrayList<>();
        String[] perms = null;
        if (!cameraGranted) {
            permissions.add(Manifest.permission.CAMERA);
            //perms = new String[]{Manifest.permission.CAMERA};
        }
        if (!audioGranted) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
            //perms = new String[]{Manifest.permission.RECORD_AUDIO};
        }
        if (!accessNetworkStateGranted) {
            permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
            //perms = new String[]{Manifest.permission.ACCESS_NETWORK_STATE};
        }
        if (!readPhoneStateGranted) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
            //perms = new String[]{Manifest.permission.READ_PHONE_STATE};
        }
        if (!readGServicesGranted) {
            permissions.add("com.google.android.providers.gsf.permission.READ_GSERVICES");
            //perms = new String[]{"com.google.android.providers.gsf.permission.READ_GSERVICES"};
        }
        if (!writeExternalStorageGranted) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            //perms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
        if (!accessCoarseLocation) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            //perms = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
        }
        if (!accessFineLocation) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            //perms = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        }

        if(permissions.size()>0){
            perms = new String[permissions.size()];
            for (int i=0;i<permissions.size();i++){
                perms[i] = permissions.get(i);
            }
        }


        if (perms != null) {
            ActivityCompat.requestPermissions(this, perms, 1000);
            //mRequestingPermission = true;
        } else {
            //showInitialRecorder();
        }


        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        recycler = (RecyclerView) findViewById(R.id.recycler);

        //get realm instance
        this.realm = RealmController.with(this).getRealm();

        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupRecycler();

        //setRealmData();
        if (!Prefs.with(this).getPreLoad()) {
            //setRealmData();
        }
        RealmResults<Book> results = realm.where(Book.class).equalTo("id",10000).findAll();

        for (Book book:results){
            Log.e("resultado:::::::",book.getAuthor());
        }results.clear();


        // refresh the realm instance
        RealmController.with(this).refresh();
        // get all persisted objects
        // create the helper adapter and notify data set changes
        // changes will be reflected automatically
        setRealmAdapter(RealmController.with(this).getBooks());

        Toast.makeText(this, "Press card item for edit, long press to remove item", Toast.LENGTH_LONG).show();

        //add new item
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inflater = MainActivity.this.getLayoutInflater();
                View content = inflater.inflate(R.layout.edit_item, null);
                final EditText editTitle = (EditText) content.findViewById(R.id.title);
                final EditText editAuthor = (EditText) content.findViewById(R.id.author);
                final EditText editThumbnail = (EditText) content.findViewById(R.id.thumbnail);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(content)
                        .setTitle("Add book")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Book book = new Book();
                                //book.setId(RealmController.getInstance().getBooks().size() + 1);

                                Log.e("lectura",RealmController.getInstance().getBooks().size()+"");

                                book.setId(RealmController.getInstance().getBooks().size() + System.currentTimeMillis());
                                book.setTitle(editTitle.getText().toString());
                                book.setAuthor(editAuthor.getText().toString());
                                book.setImageUrl(editThumbnail.getText().toString());

                                if (editTitle.getText() == null || editTitle.getText().toString().equals("") || editTitle.getText().toString().equals(" ")) {
                                    Toast.makeText(MainActivity.this, "Entry not saved, missing title", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Persist your data easily
                                    realm.beginTransaction();
                                    realm.copyToRealm(book);
                                    realm.commitTransaction();

                                    adapter.notifyDataSetChanged();

                                    // scroll the recycler view to bottom
                                    recycler.scrollToPosition(RealmController.getInstance().getBooks().size() - 1);
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void setRealmAdapter(RealmResults<Book> books) {

        RealmBooksAdapter realmAdapter = new RealmBooksAdapter(this.getApplicationContext(), books, true);
        // Set the data and tell the RecyclerView to draw
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
    }

    private void setupRecycler() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recycler.setHasFixedSize(true);

        // use a linear layout manager since the cards are vertically scrollable
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);

        // create an empty adapter and add it to the recycler view
        adapter = new BooksAdapter(this);
        recycler.setAdapter(adapter);
    }

    private void setRealmData() {



        for (int i = 1001; i < 1000000; i++) {
            Log.e("num",i+"");
            Book book = new Book();
            book.setId(i);
            book.setAuthor("Reto Meier");
            book.setTitle("Android 4 Application Development");
            book.setImageUrl("http://api.androidhive.info/images/realm/1.png");

            // Persist your data easily
            realm.beginTransaction();
            realm.copyToRealm(book);
            realm.commitTransaction();
        }

        Prefs.with(this).setPreLoad(true);

    }
}