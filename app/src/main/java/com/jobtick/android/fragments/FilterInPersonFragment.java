package com.jobtick.android.fragments;

import android.os.Bundle;
import com.jobtick.android.models.FilterModel;
import com.jobtick.android.utils.Constant;


public class FilterInPersonFragment extends AbstractFilterFragment {

    private FragmentCallbackFilterInPerson fragmentCallbackFilterInPerson;

    public static FilterInPersonFragment newInstance(FragmentCallbackFilterInPerson fragmentCallbackFilterInPerson,
                                                     FilterModel filterModel) {

        Bundle args = new Bundle();
        args.putParcelable(Constant.FILTER, filterModel);
        FilterInPersonFragment fragment = new FilterInPersonFragment();
        fragment.fragmentCallbackFilterInPerson = fragmentCallbackFilterInPerson;
        fragment.setArguments(args);
        return fragment;
    }

    public FilterInPersonFragment() {
        // Required empty public constructor
    }

    @Override
    public void fragmentCallback(FilterModel filterModel) {
        fragmentCallbackFilterInPerson.getInPersonData(filterModel);
    }

    @Override
    public int getFilterType() {
        return FilterType.IN_PERSON;
    }

    public interface FragmentCallbackFilterInPerson {
        void getInPersonData(FilterModel filterModel);
    }
}
