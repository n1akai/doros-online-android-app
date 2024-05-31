package ma.n1akai.dorosonline.ui.auth;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import ma.n1akai.dorosonline.R;
import ma.n1akai.dorosonline.databinding.FragmentRegisterBinding;
import ma.n1akai.dorosonline.ui.BaseFragment;
import ma.n1akai.dorosonline.ui.home.MainActivity;
import ma.n1akai.dorosonline.util.State;

public class RegisterFragment extends BaseFragment<FragmentRegisterBinding> {

    private AuthViewModel viewModel;
    private String email;
    private String password;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBinding().btnRegister.setOnClickListener(v -> {
            if ((validate())) {
                viewModel.register(email, password);
                getBinding().btnRegister.setEnabled(false);
                progressDialog = ProgressDialog.show(requireContext(), "",
                        "Chargement...", true);
            }
        });

        viewModel.register.observe(getViewLifecycleOwner(), s -> {
            Snackbar.make(getBinding().getRoot(), s.getMessage(), Snackbar.LENGTH_SHORT).show();
            getBinding().btnRegister.setEnabled(true);
            if (s.getState() == State.SUCCESS) {
                startActivity(new Intent(requireContext(), MainActivity.class));
                requireActivity().finish();
            }
            progressDialog.dismiss();
        });
    }

    public boolean validate() {
        email = getBinding().etEmail.getText().toString();
        password = getBinding().etPassword.getText().toString();

        // TODO: Validation...

        return true;
    }

    @Override
    protected FragmentRegisterBinding provideBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentRegisterBinding.inflate(inflater, container, false);
    }


}