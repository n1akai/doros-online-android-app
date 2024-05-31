package ma.n1akai.dorosonline.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import ma.n1akai.dorosonline.databinding.FragmentLoginBinding;
import ma.n1akai.dorosonline.ui.BaseFragment;
import ma.n1akai.dorosonline.ui.home.MainActivity;
import ma.n1akai.dorosonline.util.State;

public class LoginFragment extends BaseFragment<FragmentLoginBinding> {

    private AuthViewModel viewModel;
    private NavController navController;

    private String email, password;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        getBinding().btnRegister.setOnClickListener(v -> {
            navController.navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment());
        });


        getBinding().btnLogin.setOnClickListener(v -> {
            if (validate()) {
                viewModel.login(email, password);
                getBinding().btnLogin.setEnabled(false);
                progressDialog = ProgressDialog.show(requireContext(), "",
                        "Chargement...", true);
            }
        });

        viewModel.login.observe(getViewLifecycleOwner(), s -> {
            progressDialog.dismiss();
            Snackbar.make(getBinding().getRoot(), s.getMessage(), Snackbar.LENGTH_SHORT).show();
            getBinding().btnLogin.setEnabled(true);
            if (s.getState() == State.SUCCESS) {
                startActivity(new Intent(requireContext(), MainActivity.class));
                requireActivity().finish();
            }
        });
    }

    public boolean validate() {
        email = getBinding().etEmail.getText().toString();
        password = getBinding().etPassword.getText().toString();

        // TODO: Validation...

        return true;
    }

    @Override
    protected FragmentLoginBinding provideBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentLoginBinding.inflate(inflater, container, false);
    }
}