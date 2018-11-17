package cent.news.com.baseframe.screen;

import android.os.Bundle;

/**
 * Created by bym on 2018/6/18.
 */

public class BaseActivityTransporter {

    private Class<?>	toClazz;

    private Bundle bundle;

    public BaseActivityTransporter(Class<?> toClazz) {
        this.toClazz = toClazz;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public Class<?> toClazz() {
        return toClazz;
    }

    public Bundle getBundle() {
        return this.bundle;
    }


}
