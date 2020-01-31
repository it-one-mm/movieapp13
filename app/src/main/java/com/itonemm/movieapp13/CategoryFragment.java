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
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

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
public class CategoryFragment extends Fragment {


    ArrayList<String>documentids=new ArrayList<String>();
    public CategoryFragment() {
        // Required empty public constructor
    }

    ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_category, container, false);
        FloatingActionButton floatingActionButton=view.findViewById(R.id.add_category);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryPopUp popUp=new CategoryPopUp();
                popUp.show(getFragmentManager(),"Add Category");
            }
        });

        final EditText edtsearch=view.findViewById(R.id.edt_search_category);
        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(edtsearch.getText().toString().equals(""))
                {
                    loadData();
                }
                else
                {
                    String  query=edtsearch.getText().toString().trim();
                    FirebaseFirestore db=FirebaseFirestore.getInstance();
                    CollectionReference Ref=db.collection("categories");
                    Ref.whereEqualTo("categoryName",query).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            ArrayList<CategoryModel> categoryModels=new ArrayList<>();
                            for(DocumentSnapshot snapshot:queryDocumentSnapshots)
                            {
                                categoryModels.add(snapshot.toObject(CategoryModel.class));
                            }
                            CategoryAdatper adatper=new CategoryAdatper(categoryModels);
                            listView.setAdapter(adatper);
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

          listView=view.findViewById(R.id.lstview);
          loadData();

        return  view;
    }


    public void loadData()
    {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference category=db.collection("categories");
        category.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<CategoryModel> categoryModels=new ArrayList<>();
                documentids.clear();;
                for(DocumentSnapshot s : queryDocumentSnapshots)
                {
                    CategoryModel categoryModel=s.toObject(CategoryModel.class);
                    categoryModels.add(categoryModel);
                    documentids.add(s.getId());
                }
                CategoryAdatper adatper=new CategoryAdatper(categoryModels);
                listView.setAdapter(adatper);
            }
        });
    }

    private class CategoryAdatper extends BaseAdapter{

        ArrayList<CategoryModel> categoryModels=new ArrayList<>();

        public CategoryAdatper(ArrayList<CategoryModel> categoryModels) {
            this.categoryModels = categoryModels;
        }

        @Override
        public int getCount() {
            return categoryModels.size();
        }

        @Override
        public Object getItem(int position) {
            return categoryModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final LayoutInflater inflater=getLayoutInflater();
            View view=inflater.inflate(R.layout.categorylist,null);

            TextView txtsr=view.findViewById(R.id.categorySr);
            final TextView txtcategoryName=view.findViewById(R.id.categoryName);
            txtsr.setText(String.valueOf(position+1));
            txtcategoryName.setText(categoryModels.get(position).categoryName);
                txtcategoryName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PopupMenu menu=new PopupMenu(getContext(),txtcategoryName);
                        menu.getMenuInflater().inflate(R.menu.popmenu,menu.getMenu());
                        menu.show();;
                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if(item.getItemId()==R.id.delete_menu){
                                    FirebaseFirestore db=FirebaseFirestore.getInstance();
                                    db.collection("categories").document(documentids.get(position)).delete();
                                   db.collection("categories").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            ArrayList<CategoryModel> categoryModels=new ArrayList<>();
                                            documentids.clear();;
                                            for(DocumentSnapshot s : queryDocumentSnapshots)
                                            {
                                                CategoryModel categoryModel=s.toObject(CategoryModel.class);
                                                categoryModels.add(categoryModel);
                                                documentids.add(s.getId());
                                            }
                                            CategoryAdatper adatper=new CategoryAdatper(categoryModels);
                                            listView.setAdapter(adatper);
                                        }
                                    });
                                }

                                if(item.getItemId()==R.id.edit_menu)
                                {
                                    CategoryPopUp popUp=new CategoryPopUp() ;
                                    popUp.model=categoryModels.get(position);
                                    popUp.id=documentids.get(position);
                                    popUp.show(getFragmentManager(),"Category Edit");

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
