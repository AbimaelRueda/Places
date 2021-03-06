package com.applications.asm.domain.use_cases;

import static com.applications.asm.domain.entities.Constants.DEFAULT_LATITUDE;
import static com.applications.asm.domain.entities.Constants.DEFAULT_LONGITUDE;

import com.applications.asm.domain.entities.Place;
import com.applications.asm.domain.exception.ConnectionServer;
import com.applications.asm.domain.executor.PostExecutionThread;
import com.applications.asm.domain.executor.ThreadExecutor;
import com.applications.asm.domain.repository.PlacesRepository;
import com.applications.asm.domain.use_cases.base.UseCase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import jdk.internal.org.jline.utils.Log;

public class GetPlacesUc extends UseCase<List<Place>, GetPlacesUc.Params> {
    private final PlacesRepository placesRepository;
    private final String TAG = "GetPlacesUc";

    public static class Params {
        private final String placeToFind;
        private final Double latitude;
        private final Double longitude;
        private final Integer radius;
        private final List<String> categories;

        private Params(String placeToFind, Double latitude, Double longitude, Integer radius, List<String> categories) {
            this.placeToFind = placeToFind;
            this.latitude = latitude;
            this.longitude = longitude;
            this.radius = radius;
            this.categories = categories;
        }

        public static Params forFilterPlaces(String placeToFind, Double latitude, Double longitude, Integer radius, List<String> categories) {
            return new Params(placeToFind, latitude, longitude, radius, categories);
        }
    }

    public GetPlacesUc(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread, PlacesRepository placesRepository) {
        super(threadExecutor, postExecutionThread);
        this.placesRepository = placesRepository;
    }

    @Override
    public Observable<List<Place>> buildUseCaseObservable(Params params) {
        return Observable.fromCallable(() -> getPlaces(params.placeToFind, params.latitude, params.longitude, params.radius, params.categories));
    }

    private List<Place> getPlaces(String placeToFind, Double latitude, Double longitude, Integer radius, List<String> categories) {
        try {
            if((latitude == null || latitude < -90 || latitude > 90) || (longitude == null || longitude < -180 || longitude > 180)) {
                latitude = DEFAULT_LATITUDE;
                longitude = DEFAULT_LONGITUDE;
            }
            if(radius == null) radius = 0;
            return placesRepository.getPlaces(placeToFind, longitude, latitude, radius, categories);
        } catch (ConnectionServer connectionServer) {
            Log.error(TAG + " : " + connectionServer.getMessage());
            return new ArrayList<>();
        }
    }
}
