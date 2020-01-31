package com.itonemm.movieapp13;


import android.graphics.PointF;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class    MovieFragment extends Fragment {
     ListView list;
    ArrayList<String>documentIds=new ArrayList<String>();
    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_movie, container, false);

        FloatingActionButton add_movie=view.findViewById(R.id.add_movie);
        add_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoviePopUp moviePopUp=new MoviePopUp();
                moviePopUp.show(getFragmentManager(),"Add Movie");
            }
        });

       list=view.findViewById(R.id.movielist);

        final EditText search=view.findViewById(R.id.edt_search_movies);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if(search.getText().toString().equals(""))
                    {
                        loadData();
                    }
                    else{
                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                        CollectionReference reference=db.collection("movies");
                        reference.whereEqualTo("movieName",search.getText().toString().trim()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                ArrayList<MovieModel> movieModels=new ArrayList<MovieModel>();
                                documentIds.clear();
                                for(DocumentSnapshot snapshot: queryDocumentSnapshots)
                                {
                                    documentIds.add(snapshot.getId());

                                    MovieModel model=snapshot.toObject(MovieModel.class);
                                    movieModels.add(model);

                                }
                                MovieAdapter adapter=new MovieAdapter(movieModels);
                                list.setAdapter(adapter);
                            }
                        });
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
       loadData();
        return view;
        }

        public void loadData()
        {
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            CollectionReference reference=db.collection("movies");
            reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    ArrayList<MovieModel> movieModels=new ArrayList<MovieModel>();
                    documentIds.clear();
                    for(DocumentSnapshot snapshot: queryDocumentSnapshots)
                    {
                        documentIds.add(snapshot.getId());

                        MovieModel model=snapshot.toObject(MovieModel.class);
                        movieModels.add(model);

                    }
                    MovieAdapter adapter=new MovieAdapter(movieModels);
                    list.setAdapter(adapter);
                }
            });
        }
       private class MovieAdapter extends BaseAdapter{
        ArrayList<MovieModel> arrayList=new ArrayList<MovieModel>();

           public MovieAdapter(ArrayList<MovieModel> arrayList) {
               this.arrayList = arrayList;
           }

           @Override
           public int getCount() {
               return arrayList.size();
           }

           @Override
           public Object getItem(int position) {
               return arrayList.get(position);
           }

           @Override
           public long getItemId(int position) {
               return position;
           }

           @Override
           public View getView(final int position, View convertView, ViewGroup parent) {
               final LayoutInflater inflater=getLayoutInflater();
               View view=inflater.inflate(R.layout.serieslist,null);
               TextView txsr=view.findViewById(R.id.itemsr);
               TextView txtname=view.findViewById(R.id.itemname);
               final ImageView txtimage=view.findViewById(R.id.itemimage);
               txsr.setText((position+1)+"");
               txtname.setText(arrayList.get(position).movieName);
               Glide.with(getContext())
                       .load(arrayList.get(position).movieImage)
                       .into(txtimage);
               txtimage.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       PopupMenu pMenu=new PopupMenu(getContext(),txtimage);
                       pMenu.getMenuInflater().inflate(R.menu.popmenu,pMenu.getMenu());
                       pMenu.show();;

                       pMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                           @Override
                           public boolean onMenuItemClick(MenuItem item) {
                               if(item.getItemId()==R.id.delete_menu)
                               {

                                   FirebaseFirestore db=FirebaseFirestore.getInstance();
                                   CollectionReference ref=db.collection("movies");
                                   ref.document(documentIds.get(position)).delete();
                                   ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                       @Override
                                       public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                           ArrayList<MovieModel> movieModels=new ArrayList<MovieModel>();
                                           documentIds.clear();
                                           for(DocumentSnapshot snapshot: queryDocumentSnapshots)
                                           {
                                               documentIds.add(snapshot.getId());

                                               MovieModel model=snapshot.toObject(MovieModel.class);
                                               movieModels.add(model);

                                           }
                                           MovieAdapter adapter=new MovieAdapter(movieModels);
                                           list.setAdapter(adapter);
                                       }
                                   });
                               }

                               if(item.getItemId()==R.id.edit_menu)
                               {
                                   MoviePopUp popUp=new MoviePopUp();
                                   popUp.movieModel=arrayList.get(position);
                                   popUp.id=documentIds.get(position);
                                   popUp.show(getFragmentManager(),"Edit Movie");
                               }
                               return true;
                           }
                       });
                   }
               });
               return view;
           }
       }

}
