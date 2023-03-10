package com.jobtick.android.utils;

import android.app.Activity;
import android.content.Intent;

import com.jobtick.android.R;
import com.mapbox.api.geocoding.v5.models.CarmenContext;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;

import java.util.Objects;

public class SuburbAutoComplete {

    private final Intent intent;
    private static String postCode = null;
    private static String state = null;

    public SuburbAutoComplete(Activity activity) {
        intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Mapbox.getAccessToken())
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(activity.getResources().getColor(R.color.backgroundLightGrey))
                        .limit(10)
                        .hint("Suburb")
                        .geocodingTypes("locality", "place")
                        .country("AU")
                        .language("en")
                        .build(PlaceOptions.MODE_FULLSCREEN))
                .build(activity);
    }

    public Intent getIntent() {
        return intent;
    }


    public static String getLatitude(Intent data) {
        return Double.toString(getLatitudeDouble(data));
    }

    public static String getLongitude(Intent data) {
        return Double.toString(getLongitudeDouble(data));
    }

    public static double getLatitudeDouble(Intent data) {
        CarmenFeature carmenFeature = PlacePicker.getPlace(data);
        assert carmenFeature != null;
        return Objects.requireNonNull(carmenFeature.center()).latitude();
    }

    public static double getLongitudeDouble(Intent data) {
        CarmenFeature carmenFeature = PlacePicker.getPlace(data);
        assert carmenFeature != null;
        return Objects.requireNonNull(carmenFeature.center()).longitude();
    }

    public static String getSuburbName(Intent data) {
        postCode = null;
        state = null;
        CarmenFeature carmenFeature = PlacePicker.getPlace(data);
        assert carmenFeature != null;
        System.out.println("SuburbAutoComplete: =======> " + carmenFeature.toJson());

        StringBuilder suburb = new StringBuilder();
        int length = Objects.requireNonNull(carmenFeature.context()).size();
        //we build data like
        // 1- Suburb, city name, state name
        // 2- city name, sate name
        // 3- suburb name (village name), state name

        CarmenContext postCodeOfCity = Objects.requireNonNull(carmenFeature.context().get(0));
        if (isInteger(postCodeOfCity.text())) {
            postCode = postCodeOfCity.text();
        }

        if (length >= 3) {
            CarmenContext stateOfCountry = Objects.requireNonNull(carmenFeature.context().get(length - 2));
            CarmenContext cityOfState = Objects.requireNonNull(carmenFeature.context().get(length - 3));
            state = stateOfCountry.shortCode().substring(3);
            suburb.append(cityOfState.text()).append(", ").append(state);
        } else if (length == 2) {
            CarmenContext stateOfCountry = Objects.requireNonNull(carmenFeature.context().get(length - 2));
            state = stateOfCountry.shortCode().substring(3);
            suburb.append(state);
        } else if (length < 2) {
            throw new IllegalStateException("context of suburb must have at least two part!");
        }

        //if we don't have enough information in suburb, so we append extra data from text

        if (carmenFeature.text() != null && !carmenFeature.text().isEmpty()) {
            String detail = carmenFeature.text();
            if (!suburb.toString().toLowerCase().contains(detail.toLowerCase())) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(detail).append(", ").append(suburb.toString());
                suburb = stringBuilder;
            }
        }
        return suburb.toString();
    }

    public static String getPostCode() {
        return postCode;
    }

    public static void setPostCode(String postCode) {
        SuburbAutoComplete.postCode = postCode;
    }

    public static String getState() {
        return state;
    }

    public static void setState(String state) {
        SuburbAutoComplete.state = state;
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (Exception e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}
