package ma.n1akai.dorosonline.ui.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import ma.n1akai.dorosonline.util.State;
import ma.n1akai.dorosonline.util.UiState;

public class AuthViewModel extends ViewModel {

    private final MutableLiveData<UiState> _register = new MutableLiveData<>();
    public LiveData<UiState> register = _register;

    private final MutableLiveData<UiState> _login = new MutableLiveData<>();
    public LiveData<UiState> login = _login;

    public void register(String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                _register.setValue(new UiState(State.SUCCESS, "Inscrit avec succès !"));
            } else {
                _register.setValue(new UiState(State.FAILURE, "Erreur: " + Objects.requireNonNull(task.getException()).getMessage()));
            }
        });
    }

    public void login(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                _login.setValue(new UiState(State.SUCCESS, "Connexion réussie !"));
            } else {
                _login.setValue(new UiState(State.FAILURE, "Erreur: " + Objects.requireNonNull(task.getException()).getMessage()));
            }
        });
    }

}
