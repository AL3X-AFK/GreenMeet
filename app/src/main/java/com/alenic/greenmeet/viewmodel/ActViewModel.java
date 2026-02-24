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

    // LiveData para que la UI observe listas de actividades
    private final MutableLiveData<List<Act>> actsByFecha = new MutableLiveData<>();
    private final MutableLiveData<List<Act>> actsByCreate = new MutableLiveData<>();
    private final MutableLiveData<List<Act>> actsProximos = new MutableLiveData<>();
    private final MutableLiveData<List<Act>> actsRealizadas = new MutableLiveData<>();

    // LiveData para la actividad seleccionada en detalle
    private final MutableLiveData<Act> selectedAct = new MutableLiveData<>();

    // LiveData que indica si el usuario está apuntado a una actividad
    private final MutableLiveData<Boolean> estaApuntado = new MutableLiveData<>();

    // Lista local para poder filtrar sin hacer llamadas al repositorio
    private List<Act> allActs = new ArrayList<>();

    public ActViewModel() {
        repository = new ActRepository();
    }

    // Getters
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
            public void onError(String error) {}
        });
    }

    // Cargar las actividades ordenadas por fecha
    public void loadActsByFecha() {
        repository.getAllActsByDate(new ActRepository.ActCallback<>() {
            @Override
            public void onSuccess(List<Act> result) {
                actsByFecha.setValue(result);
            }

            @Override
            public void onError(String error) {}
        });
    }

    //Carga las siguientes actividades a las que esta apuntado
    public void loadActsProximos() {
        repository.getNextActsForUser(new ActRepository.ActCallback<>() {
            @Override
            public void onSuccess(List<Act> result) {
                actsProximos.setValue(result);
            }

            @Override
            public void onError(String error) {}
        });
    }

    //Carga las actividades realizadas a las que esta apuntado
    public void loadActsRealizadas() {
        repository.getPastActsForUser(new ActRepository.ActCallback<>() {
            @Override
            public void onSuccess(List<Act> result) {
                actsRealizadas.setValue(result);
            }

            @Override
            public void onError(String error) {}
        });
    }

    // Filtrar actividades locales por título sin llamar al repositorio
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

    //Detalle
    public void selectAct(Act act) {
        selectedAct.setValue(act); // notificar UI
    }

    //Apuntarse a la actividad
    public void apuntarse(Act act) {
        repository.apuntarseActividad(act, new ActRepository.ActCallback<Void>() {
            @Override
            public void onSuccess(Void result) { estaApuntado.setValue(true); }
            @Override
            public void onError(String error) {}
        });
    }

    //Desapuntarse a la actividad
    public void desapuntarse(Act act) {
        repository.desapuntarseActividad(act, new ActRepository.ActCallback<Void>() {
            @Override
            public void onSuccess(Void result) { estaApuntado.setValue(false); }
            @Override
            public void onError(String error) {}
        });
    }

    //Comprobar si esta apuntado
    public void comprobarSiEstaApuntado(Act act) {
        repository.isUserApuntado(act, new ActRepository.ActCallback<>() {
            @Override
            public void onSuccess(Boolean result) { estaApuntado.setValue(result); }
            @Override
            public void onError(String error) {}
        });
    }
}
