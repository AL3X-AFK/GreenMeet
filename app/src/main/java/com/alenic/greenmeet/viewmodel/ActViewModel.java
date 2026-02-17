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
    private final MutableLiveData<Boolean> estaApuntado = new MutableLiveData<>();


    private List<Act> allActs = new ArrayList<>();

    public ActViewModel() {
        repository = new ActRepository();
    }

    public LiveData<List<Act>> getActs() { return acts; }

    public LiveData<Act> getSelectedAct() { return selectedAct; }

    public LiveData<String> getState() { return state; }

    public LiveData<Boolean> getEstaApuntado() {
        return estaApuntado;
    }


    public void loadActs() {

        repository.getAllActs(new ActRepository.ActCallback<List<Act>>() {
            @Override
            public void onSuccess(List<Act> result) {
                allActs = result;
                acts.setValue(result);
            }

            @Override
            public void onError(String error) {
                state.setValue(error);
            }
        });
    }

    public void filterActs(String query) {

        if (query == null || query.isEmpty()) {
            acts.setValue(allActs);
            return;
        }

        List<Act> filteredList = new ArrayList<>();

        for (Act act : allActs) {
            if (act.getTitulo() != null &&
                    act.getTitulo().toLowerCase().contains(query.toLowerCase())) {

                filteredList.add(act);
            }
        }

        acts.setValue(filteredList);
    }

    public void selectAct(Act act) {
        selectedAct.setValue(act);
    }

    public void apuntarse(Act act) {

        repository.apuntarseActividad(act, new ActRepository.ActCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                estaApuntado.setValue(true);
                state.setValue("APUNTADO_OK");
            }

            @Override
            public void onError(String error) {
                state.setValue(error);
            }
        });
    }

    public void desapuntarse(Act act) {

        repository.desapuntarseActividad(act, new ActRepository.ActCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                estaApuntado.setValue(false);
                state.setValue("DESAPUNTADO_OK");
            }

            @Override
            public void onError(String error) {
                state.setValue(error);
            }
        });
    }

    public void comprobarSiEstaApuntado(Act act) {

        repository.isUserApuntado(act, new ActRepository.ActCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                estaApuntado.setValue(result);
            }

            @Override
            public void onError(String error) {
                state.setValue(error);
            }
        });
    }
}
