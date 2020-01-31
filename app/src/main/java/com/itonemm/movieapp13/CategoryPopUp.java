package com.itonemm.movieapp13;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import es.dmoral.toasty.Toasty;

public class CategoryPopUp extends DialogFragment {
    CategoryModel model;
    String id;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.category,container,false);
        ImageView img_close=view.findViewById(R.id.close_category);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        final EditText edt_category_name=view.findViewById(R.id.category_name);

        if(model!=null)
        {
            edt_category_name.setText(model.categoryName);
        }
        Button btnsave=view.findViewById(R.id.btn_save_category);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edt_category_name.getText().toString().equals(""))
                {
                    if(model!=null)
                    {
                        CategoryModel c=new CategoryModel(  edt_category_name.getText().toString());
                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                        CollectionReference category=db.collection("categories");
                        category.document(id).set(c);
                        edt_category_name.setText("");
                        Toasty.success(getContext(),"Category Update Successfully!", Toast.LENGTH_LONG).show();

                    }
                    else{
                        CategoryModel c=new CategoryModel(  edt_category_name.getText().toString());
                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                        CollectionReference category=db.collection("categories");
                        category.add(c);
                        edt_category_name.setText("");
                        Toasty.success(getContext(),"Category Save Successfully!", Toast.LENGTH_LONG).show();


                    }
                }
                else{
                    Toasty.error(getContext(),"Please Fill Data!",Toasty.LENGTH_LONG).show();
                }

            }
        });

        Button btncancel=view.findViewById(R.id.btn_cancel_category);
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_category_name.setText("");
                dismiss();
            }
        });
        return view;

    }
}
