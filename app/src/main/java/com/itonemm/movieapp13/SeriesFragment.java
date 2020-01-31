package com.itonemm.movieapp13;


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


/**
 * A simple {@link Fragment} subclass.
 */
public class SeriesFragment extends Fragment {

    ListView listView;
    ArrayList<String>documentIds=new ArrayList<String>();
    public SeriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_series, container, false);
        FloatingActionButton add_series=view.findViewById(R.id.add_series);
        add_series.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeriesPopUp popUp=new SeriesPopUp();
                popUp.show(getFragmentManager(),"Add Series");
            }
        });
         listView=view.findViewById(R.id.series_list);

        final EditText search=view.findViewById(R.id.edt_search_series);
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
                else
                {
                    FirebaseFirestore db=FirebaseFirestore.getInstance();
                    CollectionReference series=db.collection("series");
                    series.whereEqualTo("seriesName",search.getText().toString().trim()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            ArrayList<SeriesModel> seriesModels=new ArrayList<SeriesModel>();
                            documentIds.clear();;
                            for(DocumentSnapshot s: queryDocumentSnapshots)
                            {
                                SeriesModel model=s.toObject(SeriesModel.class);
                                seriesModels.add(model);
                                documentIds.add(s.getId());

                            }
                            SeriesAdapter adapter=new SeriesAdapter(seriesModels);
                            listView.setAdapter(adapter);
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
        db.collection("series")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<SeriesModel> seriesModels=new ArrayList<SeriesModel>();
                documentIds.clear();;
                for(DocumentSnapshot s: queryDocumentSnapshots)
                {
                    SeriesModel model=s.toObject(SeriesModel.class);
                    seriesModels.add(model);
                    documentIds.add(s.getId());

                }
                SeriesAdapter adapter=new SeriesAdapter(seriesModels);
                listView.setAdapter(adapter);
            }
        });
    }

    private class SeriesAdapter extends BaseAdapter{
        ArrayList<SeriesModel> seriesModels=new ArrayList<SeriesModel>();
        //Alt+insert
        public SeriesAdapter(ArrayList<SeriesModel> seriesModels) {
            this.seriesModels = seriesModels;
        }

        // Alt+insert => implentmehtod
        @Override
        public int getCount() {
            return seriesModels.size();
        }

        @Override
        public Object getItem(int position) {
            return seriesModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=getLayoutInflater();
            View view=inflater.inflate(R.layout.serieslist,null);

            TextView txtsr=view.findViewById(R.id.itemsr);
            TextView txtname=view.findViewById(R.id.itemname);
            final ImageView imageView=view.findViewById(R.id.itemimage);
            txtsr.setText(position+1+"");
            txtname.setText(seriesModels.get(position).seriesName);
            Glide.with(getContext())

                    .load(seriesModels.get(position).seriesImage)
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu=new PopupMenu(getContext(),imageView);
                    menu.getMenuInflater().inflate(R.menu.popmenu,menu.getMenu());
                    menu.show();
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId()==R.id.delete_menu)
                            {
                                FirebaseFirestore db=FirebaseFirestore.getInstance();
                                CollectionReference reference=db.collection("series");
                                reference.document(documentIds.get(position)).delete();
                                 reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    ArrayList<SeriesModel> seriesModels=new ArrayList<SeriesModel>();
                                    documentIds.clear();;
                                    for(DocumentSnapshot s: queryDocumentSnapshots)
                                    {
                                        SeriesModel model=s.toObject(SeriesModel.class);
                                        seriesModels.add(model);
                                        documentIds.add(s.getId());

                                    }
                                    SeriesAdapter adapter=new SeriesAdapter(seriesModels);
                                    listView.setAdapter(adapter);
                                }
                            });
                            }

                            if(item.getItemId()==R.id.edit_menu)
                            {
                                SeriesPopUp popUp=new SeriesPopUp() ;
                                popUp.model=seriesModels.get(position);
                                popUp.id=documentIds.get(position);
                                popUp.show(getFragmentManager(),"Show Fragment");

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
