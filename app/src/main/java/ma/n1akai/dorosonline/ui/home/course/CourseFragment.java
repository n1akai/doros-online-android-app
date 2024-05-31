package ma.n1akai.dorosonline.ui.home.course;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import java.util.List;

import ma.n1akai.dorosonline.R;
import ma.n1akai.dorosonline.data.model.Course;
import ma.n1akai.dorosonline.databinding.FragmentCourseBinding;
import ma.n1akai.dorosonline.ui.BaseFragment;
import ma.n1akai.dorosonline.util.Helpers;
import ma.n1akai.dorosonline.util.UiState;

public class CourseFragment extends BaseFragment<FragmentCourseBinding> {

    private static final int PERMISSION_REQUEST_CODE = 1;

    Course course;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        course = CourseFragmentArgs.fromBundle(getArguments()).getCourse();
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Glide.with(getBinding().getRoot()).load(course.getImgUrl()).into(getBinding().ivImg);
        getBinding().tvAbout.setText(course.getAbout());
        getBinding().youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(Helpers.getVideoId(course.getYoutubeUrl()), 0);
            }
        });
        if (course.getPdfUrl() == null) {
            getBinding().btnDownloadPdf.setVisibility(View.GONE);
        } else {
            getBinding().btnDownloadPdf.setOnClickListener(v -> {
                prepareDownloading();
            });
        }
    }

    private void prepareDownloading() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            startDownload();
        }

        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(course.getPdfUrl());
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        manager.enqueue(request);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("admin@doros.ma")) {
            inflater.inflate(R.menu.course_menu, menu);
        } else {
            inflater.inflate(R.menu.course_menu_normal_user, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit) {
            Navigation.findNavController(getBinding().getRoot()).navigate(CourseFragmentDirections.actionCourseFragmentToAddUpdateCourseFragment(course));
        } else if (item.getItemId() == R.id.delete) {
            new AlertDialog.Builder(requireContext()).setTitle("Supprimer le cours")
                    .setMessage("Êtes-vous sûr de vouloir supprimer ce cours ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        ProgressDialog d = ProgressDialog.show(requireContext(), "",
                                "Suppression...", true);
                        d.show();
                        FirebaseDatabase.getInstance().getReference("courses").child(course.getId()).removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Snackbar.make(getBinding().getRoot(), "Supprimé avec succès !", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(getBinding().getRoot(), "Erreur: " + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                            d.dismiss();
                            dialog.dismiss();
                            Navigation.findNavController(getBinding().getRoot()).popBackStack();
                        });
                    })
                    .setNegativeButton("Non", (dialog, which) -> {
                        dialog.dismiss();
                    }).create().show();
            return true;
        } else if (item.getItemId() == R.id.download) {
            prepareDownloading();
            return true;
        }
        return false;
    }

    private void startDownload() {
        String url = course.getPdfUrl();
        String fileName = course.getPdfFileName();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(fileName);
        request.setDescription("Téléchargement du fichier...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        Snackbar.make(getBinding().getRoot(), "Téléchargement commencé...", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownload();
            } else {
                Snackbar.make(getBinding().getRoot(), "Permission refusée", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected FragmentCourseBinding provideBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCourseBinding.inflate(inflater, container, false);
    }
}