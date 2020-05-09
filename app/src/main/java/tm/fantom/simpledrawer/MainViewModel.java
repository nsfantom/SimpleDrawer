package tm.fantom.simpledrawer;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tm.fantom.simpledrawer.net.HttpException;
import tm.fantom.simpledrawer.net.NetMenu;
import tm.fantom.simpledrawer.net.NetMenuItem;

public class MainViewModel extends ViewModel {

    private HandlerThread thread = new HandlerThread("network");
    private Handler handler;

    private MutableLiveData<List<NetMenuItem>> menuItems = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<NetMenuItem> lastSelected = new MutableLiveData<>();

    public MainViewModel() {
        thread.start();
        handler = new Handler(thread.getLooper());
        getMenuItems();
    }

    LiveData<List<NetMenuItem>> getNetMenuItems() {
        return menuItems;
    }

    boolean onClickMenu(int index) {
        final List<NetMenuItem> items = menuItems.getValue();
        if (items != null && items.size() > 0 && index < items.size()) {
            lastSelected.setValue(items.get(index));
            return true;
        }
        return false;
    }

    LiveData<NetMenuItem> getSelected() {
        return lastSelected;
    }

    private void getMenuItems() {
        handler.post(() -> {
            try {
                List<NetMenuItem> list = NetMenu.getMenuItems();
                if (lastSelected.getValue() == null && list.size() > 0) {
                    lastSelected.postValue(list.get(0));
                }
                menuItems.postValue(list);
            } catch (JSONException | IOException | HttpException e) {
                Log.e("NET", "ERROR: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected void onCleared() {
        thread.quit();
        super.onCleared();
    }
}
