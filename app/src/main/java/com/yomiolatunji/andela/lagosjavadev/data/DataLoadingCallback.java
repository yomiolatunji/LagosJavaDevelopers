package com.yomiolatunji.andela.lagosjavadev.data;

/**
 * Created by Oluwayomi on 3/8/2017.
 */

public interface DataLoadingCallback<T> {
    void onResponse(T data);
    void onFailure(String message);
}
