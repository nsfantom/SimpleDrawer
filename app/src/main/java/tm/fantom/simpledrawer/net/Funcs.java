package tm.fantom.simpledrawer.net;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({
        Funcs.TEXT, Funcs.IMAGE, Funcs.URL
})
@Retention(RetentionPolicy.CLASS)
public @interface Funcs {
    String TEXT = "text";
    String IMAGE = "image";
    String URL = "url";
}

