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

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import es.dmoral.toasty.Toasty;

public class SeriesPopUp  extends DialogFragment {
    SeriesModel model;
    String id;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.series,container,false);
        final ImageView close=view.findViewById(R.id.close_series);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final ArrayList<String> categorynames=new ArrayList<String>();
        final Spinner spinner=view.findViewById(R.id.series_categories);
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        final CollectionReference category=db.collection("categories");

      category.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
          @Override
          public void onSuccess(QuerySnapshot queryDocumentSnapshots) {



              for(DocumentSnapshot snapshot:queryDocumentSnapshots)
              {
                  CategoryModel c=snapshot.toObject(CategoryModel.class);
                  categorynames.add(c.categoryName);

              }
              ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),

                      android.R.layout.simple_dropdown_item_1line,
                      categorynames
                      );
              spinner.setAdapter(adapter);
              if(model!=null){
                  for(int i=0;i<categorynames.size();i++)
                  {
                      if(categorynames.get(i).equals(model.seriesCategory))
                      {
                          spinner.setSelection(i);
                          break;
                      }
                  }
              }


          }
      });
        final EditText edt_seriesName=view.findViewById(R.id.series_name);
        final EditText edt_seriesImage=view.findViewById(R.id.series_image_link);
        final EditText edt_seriesVideo=view.findViewById(R.id.series_video_link);

        if(model!=null)
        {
            edt_seriesName.setText(model.seriesName);
            edt_seriesImage.setText(model.seriesImage);
            edt_seriesVideo.setText(model.seriesVideo);
        }
        Button btnsave=view.findViewById(R.id.btn_save_series);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!edt_seriesName.getText().toString().equals("") && !edt_seriesImage.getText().toString().equals("") && !edt_seriesVideo.getText().toString().equals(""))
               {
                   SeriesModel seriesModel=new SeriesModel(
                           edt_seriesName.getText().toString(),
                           edt_seriesImage.getText().toString(),
                           edt_seriesVideo.getText().toString(),
                           categorynames.get(spinner.getSelectedItemPosition())
                   );
                   FirebaseFirestore db=FirebaseFirestore.getInstance();
                   CollectionReference seriesRef=db.collection("series");
                   if(model!=null)
                   {
                       seriesRef.document(id).set(seriesModel);
                       Toasty.success(getContext(),"Update Successfully",Toasty.LENGTH_LONG).show();
                   }
                   else
                   {
                       seriesRef.add(seriesModel);
                       Toasty.success(getContext(),"Save Successfully",Toasty.LENGTH_LONG).show();
                   }
                   edt_seriesImage.setText("");
                   edt_seriesVideo.setText("");
                   edt_seriesName.setText("");

               }
               else
               {
                   Toasty.error(getContext(),"Please Fill Data!",Toasty.LENGTH_LONG).show();

               }
            }
        });

        Button btncancel=view.findViewById(R.id.btn_cancel_series);
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_seriesImage.setText("");
                edt_seriesName.setText("");
                edt_seriesVideo.setText("");
                dismiss();
            }
        });
        return view;


    }
}
