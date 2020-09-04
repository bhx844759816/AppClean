package com.appclean.main.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.fragment.YwBaseFragment;
import com.app.presenter.Presenter;
import com.appclean.main.R;
import com.appclean.main.presenter.AppMemoryPresenter;
import com.appclean.main.view.IAppMemoryView;

/**
 * APP廋身的Fragment
 */
public class AppMemoryFragment extends YwBaseFragment implements IAppMemoryView {
    @Override
    protected Presenter getPresenter() {
        return new AppMemoryPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_memory_layout,container,false);
        return view;
    }
}
