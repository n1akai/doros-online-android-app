package ma.n1akai.dorosonline.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ma.n1akai.dorosonline.data.model.Course;
import ma.n1akai.dorosonline.databinding.DorosListItemBinding;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesViewHolder> {

    private List<Course> items = new ArrayList<>();
    private Context context;
    private boolean showActions = false;
    private OnCourseClickListener onCourseClickListener;
    private OnDeleteCourseListener onDeleteCourseListener;
    private OnEditCourseListener onEditCourseListener;

    public void setItems(List<Course> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setShowActions(boolean showActions) {
        this.showActions = showActions;
    }

    public void setOnCourseClickListener(OnCourseClickListener onCourseClickListener) {
        this.onCourseClickListener = onCourseClickListener;
    }

    public void setOnDeleteCourseListener(OnDeleteCourseListener onDeleteCourseListener) {
        this.onDeleteCourseListener = onDeleteCourseListener;
    }

    public void setOnEditCourseListener(OnEditCourseListener onEditCourseListener) {
        this.onEditCourseListener = onEditCourseListener;
    }

    @NonNull
    @Override
    public CoursesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CoursesViewHolder(DorosListItemBinding
                .inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CoursesViewHolder holder, int position) {
        Course course = items.get(position);
        holder.binding.getRoot().setOnClickListener(v -> onCourseClickListener.onClick(course));
        Glide.with(context).load(course.getImgUrl()).into(holder.binding.ivImg);
        holder.binding.tvTitle.setText(course.getTitle());
        holder.binding.tvDescription.setText(course.getAbout());
        holder.binding.tvDate.setText(course.getDate());
        if (showActions) {
            holder.binding.btnEdit.setOnClickListener(v -> onEditCourseListener.onEditCourse(course));
            holder.binding.btnDelete.setOnClickListener(v -> onDeleteCourseListener.onDeleteCourse((Button) v, course.getId()));
        } else {
            holder.binding.btnEdit.setVisibility(View.GONE);
            holder.binding.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class CoursesViewHolder extends RecyclerView.ViewHolder {

        DorosListItemBinding binding;

        public CoursesViewHolder(@NonNull DorosListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    interface OnCourseClickListener {
        void onClick(Course course);
    }

    interface OnDeleteCourseListener {
        void onDeleteCourse(Button button, String id);
    }

    interface OnEditCourseListener {
        void onEditCourse(Course course);
    }

}
