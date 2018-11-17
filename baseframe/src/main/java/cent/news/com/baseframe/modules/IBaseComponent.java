package cent.news.com.baseframe.modules;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by bym on 2018/6/20.
 */


@Singleton
@Component(modules = BaseModule.class)
public interface IBaseComponent {

    void inject(BaseModuleManage baseModuleManage);

}
