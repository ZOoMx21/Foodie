package com.example.foodie2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodie2.Adapters.RandomRecipeAdapter;
import com.example.foodie2.Adapters.RecipesByIngredientsAdapter;
import com.example.foodie2.Listeners.RandomRecipeResponseListener;
import com.example.foodie2.Listeners.RecipeClickListener;
import com.example.foodie2.Listeners.RecipesByIngredientsResponseListener;
import com.example.foodie2.Models.RandomRecipeApiResponse;
import com.example.foodie2.Models.SearchByIngredientsApiResponse;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;

    // private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private String country;
    boolean[] checkedItems;
    MediaPlayer player;

    ProgressDialog dialog;
    RequestManager manager;
    RandomRecipeAdapter randomRecipeAdapter;
    RecipesByIngredientsAdapter recipesByIngredientsAdapter;
    RecyclerView recyclerView;
    //Spinner spinner;
    List<String> tags = new ArrayList<>();
    public String searchWord, inc_ingreds, email;
    String[] Ingredients;
    ImageView imageView_filter;
    SearchView searchView;
    private DrawerLayout drawerLayout;
    private ImageButton btnMenu, topright_menu;
    MenuBuilder menuBuilder;
    FirebaseAuth auth;
    FirebaseUser usr;
    ArrayList<Integer> usrItems;
    private TextView headerEmail, headerLocation;
    NavigationView navigationView;
    View headerView;
    MenuItem signIn_menu, signOut_menu, musicOn, musicOff;
    private SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint({"RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        usrItems = new ArrayList<>();
        Ingredients = getResources().getStringArray(R.array.Ingredients);
        checkedItems = new boolean[Ingredients.length];
        auth = FirebaseAuth.getInstance();
        usr = auth.getCurrentUser();
        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        headerEmail = headerView.findViewById(R.id.headerEmail);
        headerLocation = headerView.findViewById(R.id.headerLocation);
        drawerLayout = findViewById(R.id.drawer_layout);
        btnMenu = findViewById(R.id.btn_menu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                manager.getRandomRecipes(randomRecipeResponseListener, tags);
            }
        });

        topright_menu = findViewById(R.id.topright_menu);
        menuBuilder = new MenuBuilder(this);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.top_right_menu, menuBuilder);
        signIn_menu = menuBuilder.findItem(R.id.signIn_menu);
        signOut_menu = menuBuilder.findItem(R.id.signOut_menu);
        musicOn = menuBuilder.findItem(R.id.musicOn);
        musicOff = menuBuilder.findItem(R.id.musicOff);
        if (player==null) {
            musicOn.setVisible(true);
            musicOff.setVisible(false);
        }
        if (player!=null) {
            musicOn.setVisible(false);
            musicOff.setVisible(true);
        }
        if (usr != null) {
            email = usr.getEmail();
            headerEmail.setText(email);
            signIn_menu.setVisible(false);
            signOut_menu.setVisible(true);
        }
        else {
            signIn_menu.setVisible(true);
            signOut_menu.setVisible(false);
        }
        topright_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuPopupHelper toprightMenu = new MenuPopupHelper(MainActivity.this, menuBuilder, v);
                toprightMenu.setForceShowIcon(true);
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case (R.id.signIn_menu): {
                                Intent intent1 = new Intent(MainActivity.this, SignInActivity.class);
                                startActivity(intent1);
                                finish();
                                break;
                            }
                            case (R.id.signOut_menu): {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent2 = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(intent2);
                                finish();
                                break;
                            }
                            case (R.id.musicOn):{
                                if (player==null) {
                                    player = MediaPlayer.create(MainActivity.this, R.raw.lofi);}
                                    player.start();
                                    musicOn.setVisible(false);
                                    musicOff.setVisible(true);
                                break;
                            }
                            case (R.id.musicOff):{
                                if (player!=null) {
                                    player.pause();
                                    musicOn.setVisible(true);
                                    musicOff.setVisible(false);
                                }
                                break;
                            }
                        }
                        return true;
                    }
                    @Override
                    public void onMenuModeChange(@NonNull MenuBuilder menu) {
                    }
                });
                toprightMenu.show();
            }
        });

        imageView_filter = findViewById(R.id.imageView_filter);
        dialog = new ProgressDialog(this, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert);
        dialog.setTitle("Loading..");
        recyclerView = findViewById(R.id.recycler_random);
        // spinner = findViewById(R.id.spinner_tags);
        searchView = findViewById(R.id.searchView_home);
        manager = new RequestManager(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                manager.getRecipesByIngredients(recipesByIngredientsResponseListener, query, inc_ingreds);
                dialog.show();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchWord = newText;
                return true;
            }
        });

        imageView_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAlertDialog();
            }
        });
        manager.getRandomRecipes(randomRecipeResponseListener, tags);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String s = item.toString();
                tags.clear();
                tags.add(s);

                if (s.equalsIgnoreCase("All")) {
                    tags.clear();
                }

                manager.getRandomRecipes(randomRecipeResponseListener, tags);
                dialog.show();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (hasLocationPermission()) {
            requestLocationUpdates();
        } else {
            requestLocationPermission();
        }
    }

    //------------------------------- end of onCreate -----------------------------------------------------------

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }
    private void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            String country = address.getCountryName();
                            String city = address.getAdminArea();
                            String district = address.getSubLocality();

                            StringBuilder locationText = new StringBuilder();
                            if (country != null) {
                                locationText.append(country);
                            }
                            if (city != null) {
                                locationText.append(", ").append(city);
                            }
                            if (district != null) {
                                locationText.append(", ").append(district);
                            }

                            headerLocation.setText(locationText.toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        if (hasLocationPermission()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            }
        }
    }

    private final RandomRecipeResponseListener randomRecipeResponseListener = new RandomRecipeResponseListener() {
        @Override
        public void didFetch(RandomRecipeApiResponse response, String message) {
            dialog.dismiss();
            recyclerView = findViewById(R.id.recycler_random);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
            randomRecipeAdapter = new RandomRecipeAdapter(MainActivity.this, response.recipes, recipeClickListener);
            recyclerView.setAdapter(randomRecipeAdapter);
            swipeRefreshLayout.setRefreshing(false);
        }
        @Override
        public void didError(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };
    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(MainActivity.this, RecipeDeatailsActivity.class)
                    .putExtra("id", id));
        }
    };
    private final RecipesByIngredientsResponseListener recipesByIngredientsResponseListener = new RecipesByIngredientsResponseListener() {
        @Override
        public void didFetch(SearchByIngredientsApiResponse response, String message) {
            dialog.dismiss();
            recyclerView = findViewById(R.id.recycler_random);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
            recipesByIngredientsAdapter = new RecipesByIngredientsAdapter(MainActivity.this, response.results, recipeClickListener);
            recyclerView.setAdapter(recipesByIngredientsAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private void CreateAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);

        builder.setTitle("Select Ingredients");
        builder.setMultiChoiceItems(Ingredients, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                if (isChecked)
                {
                    usrItems.add(which);
                }
                else
                {
                    usrItems.remove(Integer.valueOf(which));
                }
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inc_ingreds = "";
                for (int i = 0; i<usrItems.size();i++)
                {
                    inc_ingreds = inc_ingreds + Ingredients[usrItems.get(i)];
                    if (i != usrItems.size()-1) inc_ingreds = inc_ingreds + ", ";

                }
                if (inc_ingreds.length()>0){
                    manager = new RequestManager(MainActivity.this);
                    manager.getRecipesByIngredients(recipesByIngredientsResponseListener,searchWord, inc_ingreds);
                    Toast.makeText(MainActivity.this, inc_ingreds, Toast.LENGTH_SHORT).show();
                }
                else{
                    tags.clear();
                    manager.getRandomRecipes(randomRecipeResponseListener,tags);
                }
            }
        });
        builder.setNeutralButton("Clear", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog filterDialog = builder.create();
        filterDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btn =((AlertDialog) filterDialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i=0; i<checkedItems.length; i++)
                        {
                            filterDialog.getListView().setItemChecked(i,false);
                            checkedItems[i]= false;
                        }
                        usrItems.clear();
                        inc_ingreds = "";
                    }
                });

            }
        });
        filterDialog.show();

    }
    private void fetchRecipesByCategory(String category) {
        tags.clear();
        tags.add(category);
        manager.getRandomRecipes(randomRecipeResponseListener, tags);
        dialog.show();
    }


}



