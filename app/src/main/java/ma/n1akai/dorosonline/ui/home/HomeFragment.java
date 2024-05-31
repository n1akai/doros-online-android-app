package ma.n1akai.dorosonline.ui.home;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ma.n1akai.dorosonline.R;
import ma.n1akai.dorosonline.data.model.Course;
import ma.n1akai.dorosonline.databinding.FragmentHomeBinding;
import ma.n1akai.dorosonline.ui.BaseFragment;
import ma.n1akai.dorosonline.util.State;

public class HomeFragment extends BaseFragment<FragmentHomeBinding> {

    private HomeViewModel viewModel;
    private CoursesAdapter coursesAdapter = new CoursesAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.getAllCourses();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("admin@doros.ma")) {
            getBinding().fabAdd.setVisibility(View.VISIBLE);

            getBinding().fabAdd.setOnClickListener(v -> {
                Navigation.findNavController(view)
                        .navigate(HomeFragmentDirections.actionHomeFragmentToAddUpdateCourseFragment(null));
            });

            coursesAdapter.setShowActions(true);
        }

        // Loading Dialog
        ProgressDialog d = ProgressDialog.show(requireContext(), "",
                "Chargement...", true);

        getBinding().rcCourses.setAdapter(coursesAdapter);
        getBinding().rcCourses.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));

        viewModel.courses.observe(getViewLifecycleOwner(), listUiState -> {
            d.dismiss();
            if (listUiState.getState() == State.SUCCESS) {
                List<Course> courses = listUiState.getData();
                List<SlideModel> slideModelList = new ArrayList<>();
                for (int i = 0; i < Math.min(courses.size(), 6); i++) {
                    slideModelList.add(new SlideModel(courses.get(i).getImgUrl(), ScaleTypes.CENTER_CROP));
                }
                getBinding().imageSlider.setImageList(slideModelList);
                coursesAdapter.setItems(listUiState.getData());
                getBinding().imageSlider.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onItemSelected(int i) {
                        Course course = listUiState.getData().get(i);
                        Navigation
                                .findNavController(getBinding().getRoot())
                                .navigate(HomeFragmentDirections.actionHomeFragmentToCourseFragment(course, course.getTitle()));
                    }

                    @Override
                    public void doubleClick(int i) {

                    }
                });

                coursesAdapter.setOnCourseClickListener(course -> {
                    Navigation.findNavController(view)
                            .navigate(HomeFragmentDirections.actionHomeFragmentToCourseFragment(course, course.getTitle()));
                });

                coursesAdapter.setOnEditCourseListener(course -> {
                    Navigation.findNavController(view)
                            .navigate(HomeFragmentDirections.actionHomeFragmentToAddUpdateCourseFragment(course));
                });

                coursesAdapter.setOnDeleteCourseListener((button, courseId) -> {
                    new AlertDialog.Builder(requireContext()).setTitle("Supprimer le cours")
                            .setMessage("Êtes-vous sûr de vouloir supprimer ce cours ?")
                            .setPositiveButton("Oui", (dialog, which) -> {
                                ProgressDialog dd = ProgressDialog.show(requireContext(), "",
                                        "Suppression......", true);
                                button.setEnabled(false);
                                FirebaseDatabase.getInstance().getReference("courses").child(courseId).removeValue().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Snackbar.make(getBinding().getRoot(), "Supprimé avec succès !", Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        Snackbar.make(getBinding().getRoot(), "Erreur: " + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                    dd.dismiss();
                                    button.setEnabled(true);
                                });
                            })
                            .setNegativeButton("Non", (dialog, which) -> {
                                dialog.dismiss();
                            }).create().show();

                });

            } else {
                Snackbar
                        .make(
                                getBinding().getRoot(),
                                listUiState.getMessage(),
                                Snackbar.LENGTH_SHORT
                        )
                        .show();
            }
        });

    }

    @Override
    protected FragmentHomeBinding provideBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }


}