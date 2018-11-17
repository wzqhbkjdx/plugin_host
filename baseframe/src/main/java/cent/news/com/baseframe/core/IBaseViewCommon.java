package cent.news.com.baseframe.core;

/**
 * Created by bym on 2018/6/20.
 */

public interface IBaseViewCommon {

    int layoutLoading();

    int layoutEmpty();

    int layoutBizError();

    int layoutHttpError();

    IBaseViewCommon BASE_VIEW_COMMON = new IBaseViewCommon() {
        @Override
        public int layoutLoading() {
            return 0;
        }

        @Override
        public int layoutEmpty() {
            return 0;
        }

        @Override
        public int layoutBizError() {
            return 0;
        }

        @Override
        public int layoutHttpError() {
            return 0;
        }
    };


}
