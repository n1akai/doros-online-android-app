package ma.n1akai.dorosonline.ui.home.course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import ma.n1akai.dorosonline.R;
import ma.n1akai.dorosonline.data.model.Course;
import ma.n1akai.dorosonline.databinding.FragmentAddUpdateCourseBinding;
import ma.n1akai.dorosonline.ui.BaseFragment;
import ma.n1akai.dorosonline.ui.home.MainActivity;

public class AddUpdateCourseFragment extends BaseFragment<FragmentAddUpdateCourseBinding> {

    private String title, about, youtubeUrl, imgUrl, pdfName, pdfUrl;
    private Uri imgUri;
    private Uri pdfUri;
    private Course course;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        course = AddUpdateCourseFragmentArgs.fromBundle(getArguments()).getCourse();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (course != null) {
            getBinding().etTitle.setText(course.getTitle());
            getBinding().etAbout.setText(course.getAbout());
            getBinding().etYoutube.setText(course.getYoutubeUrl());
            Glide.with(getBinding().getRoot())
                    .load(course.getImgUrl()).into(getBinding().ivUploadImage);
            ((MainActivity) getActivity()).binding.toolbar
                    .setTitle("Mise à jour: " + course.getTitle());
            if (course.getPdfFileName() != null) {
                getBinding().tvPdfName.setText(course.getPdfFileName());
            }

        }

        getBinding().btnSubmit.setOnClickListener(v -> {
            title = getBinding().etTitle.getText().toString();
            about = getBinding().etAbout.getText().toString();
            youtubeUrl = getBinding().etYoutube.getText().toString();
            getBinding().btnSubmit.setEnabled(false);
            // Loading Dialog
            progressDialog = ProgressDialog.show(requireContext(), "",
                    "", true);
            uploadPdf();
        });

        courseImageListener();
        coursePdfListener();
    }

    private void uploadPdf() {
        if (pdfUri != null) {
            progressDialog.setMessage("Téléchargement du fichier...");
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(pdfName + "-" + new Date().getTime());
            mStorageRef.putFile(pdfUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mStorageRef.getDownloadUrl().addOnCompleteListener(task1 -> {
                        pdfUrl = task1.getResult().toString();
                        uploadImage();
                    });
                }
            });
        } else {
            uploadImage();
        }
    }

    private void uploadImage() {
        if (imgUri != null) {
            progressDialog.setMessage("Téléchargement de l'image...");
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("c-" + new Date().getTime());
            mStorageRef.putFile(imgUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mStorageRef.getDownloadUrl().addOnCompleteListener(task1 -> {
                        imgUrl = task1.getResult().toString();
                        if (course != null) {
                            updateCourse();
                        } else {
                            addCourse();
                        }
                    });
                }
            });
        } else {
            if (course != null) {
                updateCourse();
            } else {
                addCourse();
            }
        }
    }

    private void addCourse() {
        progressDialog.setMessage("Chargement...");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("courses");
        String id = mRef.push().getKey();
        Course course = new Course(id, title, about, youtubeUrl, imgUrl, sdf.format(new Date()), pdfUrl, pdfName);
        mRef.child(id).setValue(course).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Snackbar.make(getBinding().getRoot(), "Ajouté avec succès !", Snackbar.LENGTH_SHORT).show();
                Navigation.findNavController(getBinding().getRoot()).popBackStack();
            } else {
                Snackbar.make(getBinding().getRoot(), "Erreur: " + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
            getBinding().btnSubmit.setEnabled(true);
        });
    }

    private void updateCourse() {
        progressDialog.setMessage("Chargement...");
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("courses").child(course.getId());
        if (imgUrl == null) {
            imgUrl = course.getImgUrl();
        }
        if (pdfUrl == null && pdfName == null) {
            pdfUrl = course.getPdfUrl();
            pdfName = course.getPdfFileName();
        }
        Course updatedCourse = new Course(course.getId(), title, about, youtubeUrl, imgUrl, course.getDate(), pdfUrl, pdfName);
        mRef.setValue(updatedCourse).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Snackbar.make(getBinding().getRoot(), "Mis à jour avec succés !", Snackbar.LENGTH_SHORT).show();
                Navigation.findNavController(getBinding().getRoot()).popBackStack();
            } else {
                Snackbar.make(getBinding().getRoot(), "Erreur: " + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
            getBinding().btnSubmit.setEnabled(true);
        });
    }

    private void courseImageListener() {
        getBinding().ivUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 201);
        });
    }

    private void coursePdfListener() {
        getBinding().ivUploadPdf.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, 301);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 201 && data != null) {
            imgUri = data.getData();
            getBinding().ivUploadImage.setImageURI(imgUri);
        } else if(requestCode == 301 && data != null) {
            pdfUri = data.getData();
            String uriString = pdfUri.toString();
            File myFile = new File(uriString);

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = getActivity().getContentResolver().query(pdfUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int col = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        pdfName = cursor.getString(col);

                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                pdfName = myFile.getName();
            }
            getBinding().tvPdfName.setText(pdfName);
        }
    }


    @Override
    protected FragmentAddUpdateCourseBinding provideBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentAddUpdateCourseBinding.inflate(inflater, container, false);
    }
}