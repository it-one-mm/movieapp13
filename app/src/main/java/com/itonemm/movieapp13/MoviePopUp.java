package com.itonemm.movieapp13;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Array;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import es.dmoral.toasty.Toasty;

public class MoviePopUp extends DialogFragment {

    MovieModel movieModel;
    String id;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
     View view=inflater.inflate(R.layout.movie,container,false);
        ImageView close=view.findViewById(R.id.close_movie);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        final ArrayList<String> categorNames=new ArrayList<String>();
        final Spinner spCategory=view.findViewById(R.id.movie_categories);
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference category=db.collection("categories");
        category.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
             categorNames.clear();;
              for(DocumentSnapshot s:queryDocumentSnapshots)
              {
                  categorNames.add(s.toObject(CategoryModel.class).categoryName);
              }
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,categorNames);
              spCategory.setAdapter(adapter);
              if(movieModel!=null)
              {
                  for(int i=0;i<categorNames.size();i++)
                  {
                      if(categorNames.get(i).equals(movieModel.movieCategory))
                      {
                          spCategory.setSelection(i);
                          break;
                      }
                  }
              }
            }
        });

        final ArrayList<String> seriesNames=new ArrayList<String>();
        final Spinner spSeries=view.findViewById(R.id.movie_seires);
        CollectionReference seriesRef=db.collection("series");
        seriesRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                seriesNames.clear();
                for(DocumentSnapshot snapshot: queryDocumentSnapshots)
                {
                    seriesNames.add(snapshot.toObject(SeriesModel.class).seriesName);
                }
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,seriesNames);
                spSeries.setAdapter(adapter);

                if(movieModel!=null)
                {
                    for(int i=0;i<seriesNames.size();i++)
                    {
                            if(seriesNames.get(i).equals(movieModel.movieSeries))
                            {
                                spSeries.setSelection(i);
                                break;
                            }
                    }
                }
            }
        });



        Button btnsave=view.findViewById(R.id.btn_save_movie);
        final EditText name=view.findViewById(R.id.movie_name);
        final EditText image=view.findViewById(R.id.movie_image_link);
        final EditText video=view.findViewById(R.id.movie_video_link);

        if(movieModel!=null)
        {
            name.setText(movieModel.movieName);
            image.setText(movieModel.movieImage);
            video.setText(movieModel.movieVideo);
        }

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!name.getText().toString().equals("") && !image.getText().toString().equals("") && !video.getText().toString().equals("")){
                   if(movieModel!=null)
                   {
                       MovieModel movieModel=new MovieModel(
                               name.getText().toString(),
                               image.getText().toString(),
                               video.getText().toString(),
                               categorNames.get(spCategory.getSelectedItemPosition()),
                               seriesNames.get(spSeries.getSelectedItemPosition())
                       );
                       FirebaseFirestore db=FirebaseFirestore.getInstance();
                       db.collection("movies").document(id).set(movieModel);
                       Toasty.success(getContext(),"Update Successfully",Toasty.LENGTH_LONG).show();
                   }
                   else
                   {
                       MovieModel movieModel=new MovieModel(
                               name.getText().toString(),
                               image.getText().toString(),
                               video.getText().toString(),
                               categorNames.get(spCategory.getSelectedItemPosition()),
                               seriesNames.get(spSeries.getSelectedItemPosition())
                       );
                       FirebaseFirestore db=FirebaseFirestore.getInstance();
                       db.collection("movies").add(movieModel);
                       Toasty.success(getContext(),"Save Successfully",Toasty.LENGTH_LONG).show();
                   }

                   name.setText("");
                   video.setText("");
                   image.setText("");
                }
                else
                {
                    Toasty.success(getContext(),"Please Fill Data",Toasty.LENGTH_LONG).show();
                }
            }
        });


        Button btncacel=view.findViewById(R.id.btn_cancel_movie);
        btncacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setText("");
                video.setText("");
                image.setText("");
                dismiss();
            }
        });
     return view;
    }
}
