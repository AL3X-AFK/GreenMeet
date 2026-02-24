package com.alenic.greenmeet.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alenic.greenmeet.data.Act;
import com.alenic.greenmeet.repositories.ActRepository;


import java.util.ArrayList;
import java.util.List;

public class ActViewModel extends ViewModel {

    private final ActRepository repository;

    private final MutableLiveData<List<Act>> actsByFecha = new MutableLiveData<>();
    private final MutableLiveData<List<Act>> actsByCreate = new MutableLiveData<>();
    private final MutableLiveData<List<Act>> actsProximos = new MutableLiveData<>();
    private final MutableLiveData<List<Act>> actsRealizadas = new MutableLiveData<>();
    private final MutableLiveData<Act> selectedAct = new MutableLiveData<>();
    private final MutableLiveData<String> state = new MutableLiveData<>();
    private final MutableLiveData<Boolean> estaApuntado = new MutableLiveData<>();


    private List<Act> allActs = new ArrayList<>();

    public ActViewModel() {
        repository = new ActRepository();
    }

    public LiveData<List<Act>> getActsByFecha() {
        return actsByFecha;
    }

    // LiveData para las actividades ordenadas por fecha de creación
    public LiveData<List<Act>> getActsByCreate() {
        return actsByCreate;
    }

    public LiveData<Act> getSelectedAct() {
        return selectedAct;
    }

    public LiveData<List<Act>> getActsProximos() {
        return actsProximos;
    }

    public LiveData<List<Act>> getActsRealizadas() {
        return actsRealizadas;
    }

    public LiveData<Boolean> getEstaApuntado() {
        return estaApuntado;
    }


    // Cargar todas las actividades
    public void loadActsByCreate() {
        repository.getAllActsByCreate(new ActRepository.ActCallback<>() {
            @Override
            public void onSuccess(List<Act> result) {
                allActs = new ArrayList<>(result);
                actsByCreate.setValue(result);
            }

            @Override
            public void onError(String error) {
                state.setValue(error);
            }
        });
    }

    // Cargar solo las actividades limitadas a 5 y ordenadas por fecha (para HomeFragment)
    public void loadActsByFecha() {
        repository.getAllActsByDate(new ActRepository.ActCallback<List<Act>>() {
            @Override
            public void onSuccess(List<Act> result) {
                actsByFecha.setValue(result);
            }

            @Override
            public void onError(String error) {
                state.setValue(error);
            }
        });
    }

    public void loadActsProximos() {
        repository.getNextActsForUser(new ActRepository.ActCallback<List<Act>>() {
            @Override
            public void onSuccess(List<Act> result) {
                actsProximos.setValue(result);
            }

            @Override
            public void onError(String error) {
                state.setValue(error);
            }
        });
    }

    public void loadActsRealizadas() {
        repository.getPastActsForUser(new ActRepository.ActCallback<List<Act>>() {
            @Override
            public void onSuccess(List<Act> result) {
                actsRealizadas.setValue(result);
            }

            @Override
            public void onError(String error) {
                state.setValue(error);
            }
        });
    }

    public void filterActs(String query) {

        if (query == null || query.isEmpty()) {
            actsByCreate.setValue(allActs);
            return;
        }

        List<Act> filteredList = new ArrayList<>();

        for (Act act : allActs) {
            if (act.getTitulo() != null && act.getTitulo().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(act);
            }
        }

        actsByCreate.setValue(filteredList);
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
