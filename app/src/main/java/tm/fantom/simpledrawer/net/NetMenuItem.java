package tm.fantom.simpledrawer.net;

import org.json.JSONException;
import org.json.JSONObject;

public final class NetMenuItem {

    private String name;
    private String function;
    private String param;

    public String getName() {
        return name;
    }

    public NetMenuItem setName(String name) {
        this.name = name;
        return this;
    }

    public String getFunction() {
        return function;
    }

    public NetMenuItem setFunction(String function) {
        this.function = function;
        return this;
    }

    public String getParam() {
        return param;
    }

    public NetMenuItem setParam(String param) {
        this.param = param;
        return this;
    }

    public static NetMenuItem fromJsonObj(JSONObject jsonObject) throws JSONException {
        NetMenuItem mi = new NetMenuItem();
        if (jsonObject.has("name"))
            mi.setName(jsonObject.getString("name"));
        if (jsonObject.has("function"))
            mi.setFunction(jsonObject.getString("function"));
        if (jsonObject.has("param"))
            mi.setParam(jsonObject.getString("param"));
        return mi;
    }
}
