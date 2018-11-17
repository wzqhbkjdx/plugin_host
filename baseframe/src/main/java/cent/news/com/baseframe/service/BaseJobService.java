package cent.news.com.baseframe.service;

import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;

import cent.news.com.baseframe.BaseHelper;
import cent.news.com.baseframe.core.IBaseBiz;
import cent.news.com.baseframe.display.BaseIDisplay;
import cent.news.com.baseframe.modules.structure.BaseStructureModel;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class BaseJobService<B extends IBaseBiz> extends JobService {

    private BaseStructureModel baseStructureModel;

    @Override
    public void onCreate() {
        super.onCreate();
        baseStructureModel = new BaseStructureModel(this, null);
        BaseHelper.structureHelper().attach(baseStructureModel);
    }

    public <D extends BaseIDisplay> D display(Class<D> eClass) {
        return BaseHelper.display(eClass);
    }

    public B biz() {
        return (B) baseStructureModel.getBaseProxy().proxy;
    }

    public <C extends IBaseBiz> C biz(Class<C> service) {
        if(baseStructureModel.getService().equals(service)) {
            return (C) baseStructureModel.getBaseProxy().proxy;
        }
        return BaseHelper.structureHelper().biz(service);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaseHelper.structureHelper().detach(baseStructureModel);
    }
}
