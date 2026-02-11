package com.alenic.greenmeet.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.repositories.ActRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

public class ActViewModel extends ViewModel {

    private final ActRepository repository;

    private final MutableLiveData<List<Act>> acts = new MutableLiveData<>();
    private final MutableLiveData<Act> selectedAct = new MutableLiveData<>();
    private final MutableLiveData<String> state = new MutableLiveData<>();

    public ActViewModel() {
        repository = new ActRepository();
    }

    public LiveData<List<Act>> getActs() { return acts; }

    public LiveData<Act> getSelectedAct() { return selectedAct; }

    public LiveData<String> getState() { return state; }

    public void loadActs() {

        repository.getAllActs(new ActRepository.ActCallback<List<Act>>() {
            @Override
            public void onSuccess(List<Act> result) {
                acts.setValue(result);
            }

            @Override
            public void onError(String error) {
                state.setValue(error);
            }
        });
    }

    public void selectAct(Act act) {
        selectedAct.setValue(act);
    }
}
