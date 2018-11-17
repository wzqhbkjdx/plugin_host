package cent.news.com.baseframe.core;

import cent.news.com.baseframe.modules.structure.BaseStructureModel;

/**
 * Created by bym on 2018/6/19.
 */

public interface IBaseBiz {

    void initUI(BaseStructureModel baseView);

    void initBundle();

    void detach();

    <T> void refreshAdapter(T t);

}
