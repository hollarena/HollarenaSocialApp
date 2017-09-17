package hollarena.bernard.com.hollarena;

//get current user interest from firebase
//set in card view ====> actually intead of showing interest  show list articles on the interest
//on click perticular card open dialogue to read articles

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ReadArticlesActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    RecyclerView recyclerView;
    RARecyclerViewAdapter recyclerViewAdapter;
    private List<String> articles = new ArrayList<>();
    String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_articles);

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mDatabase.getReference().keepSynced(true);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.e(TAG, "onCreate: firebase user"+firebaseUser.getDisplayName() );

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewArticles);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        updateList();
        initializeAdapter();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initializeAdapter() {
        recyclerViewAdapter = new RARecyclerViewAdapter(ReadArticlesActivity.this,articles);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

        private void updateList() {
            mReference.child("users").child(firebaseUser.getDisplayName()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    UserModel userModel = new UserModel();

                    Log.e(TAG, "onChildAdded: key " + dataSnapshot.getKey());
                    Log.e(TAG, "onChildAdded: value " + dataSnapshot.getValue());

                    if (dataSnapshot.getKey().equals("Interest")){
                        String json = dataSnapshot.getValue().toString();
                        try {
                            JSONArray jsonResponse = new JSONArray(json);
                            for (int i = 0; i < jsonResponse.length(); i++) {
                                Log.e(TAG, "onChildAdded: "+jsonResponse.getInt(i));
                                switch (jsonResponse.getInt(i)) {
                                    case 0:
                                        articles.add("Cars");
                                        break;
                                    case 1:
                                        articles.add("Fashion");
                                        break;
                                    case 2:
                                        articles.add("Home Decor");
                                        break;
                                    case 3:
                                        articles.add("music");
                                        break;
                                    case 4:
                                        articles.add("Food");
                                        break;
                                    case 5:
                                        articles.add("Tattoos");
                                        break;
                                    case 6:
                                        articles.add("wedding");
                                        break;
                                    default:
                                        articles.add("Interesting topics");
                                }
                            }

                        recyclerViewAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
//                    userModel.setUserName(name);


////                    String json = dataSnapshot.getValue().toString();
////
////                    JSONObject jsonResponse;
////                    try {
////
////                        jsonResponse = new JSONObject(json);
////                        if (name.equals(firebaseUser.getDisplayName())) {
////                            JSONArray characters = jsonResponse.getJSONArray("Interest");
////                            for (int j = 0; j < characters.length(); j++) {
////                                switch (characters.getInt(j)) {
////                                    case 0:
////                                        articles.add("Cars");
////                                        break;
////                                    case 1:
////                                        articles.add("Fashion");
////                                        break;
////                                    case 2:
////                                        articles.add("Home Decor");
////                                        break;
////                                    case 3:
////                                        articles.add("music");
////                                        break;
////                                    case 4:
////                                        articles.add("Food");
////                                        break;
////                                    case 5:
////                                        articles.add("Tattoos");
////                                        break;
////                                    case 6:
////                                        articles.add("wedding");
////                                        break;
////                                    default:
////                                        articles.add("Interesting topics");
////                                }
////
////                            }
////                        }
//                        recyclerViewAdapter.notifyDataSetChanged();
//
//                    } catch (JSONException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.e(TAG, "onDataChange: key " + dataSnapshot.getKey());

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    private void initViews() {

        mDatabase = FirebaseDatabase.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }




}
