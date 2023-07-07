package me.goldze.mvvmhabit.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/* loaded from: classes.dex */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile ViewModelFactory INSTANCE;
    private final Application mApplication;

    public static ViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(application);
                }
            }
        }
        return INSTANCE;
    }

    private ViewModelFactory(Application application) {
        this.mApplication = application;
    }

    @Override // android.arch.lifecycle.ViewModelProvider.NewInstanceFactory, android.arch.lifecycle.ViewModelProvider.Factory
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BaseViewModel.class)) {
            return new BaseViewModel(this.mApplication);
        }
        try {
            String className = modelClass.getCanonicalName();
            Class<?> classViewModel = Class.forName(className);
            Constructor<?> cons = classViewModel.getConstructor(Application.class);
            return (T) cons.newInstance(this.mApplication);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        } catch (InstantiationException e3) {
            e3.printStackTrace();
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        } catch (NoSuchMethodException e4) {
            e4.printStackTrace();
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
}