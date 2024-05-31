package ma.n1akai.dorosonline.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ma.n1akai.dorosonline.data.model.Course;
import ma.n1akai.dorosonline.util.State;
import ma.n1akai.dorosonline.util.UiState;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<UiState<List<Course>>> _courses = new MutableLiveData<>();
    public final LiveData<UiState<List<Course>>> courses = _courses;

    public void getAllCourses() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("courses");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Course> courseList = new ArrayList<>();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    courseList.add(ds.getValue(Course.class));
                }
                _courses.setValue(new UiState<>(State.SUCCESS, courseList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                _courses.setValue(new UiState<>(State.FAILURE, error.getMessage()));
            }
        });
    }

}