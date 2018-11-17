package cent.news.com.baseframe.view.adapter.recyclerView;

import android.support.v7.widget.RecyclerView;

public interface OrientationProvider {

    int getOrientation(RecyclerView recyclerView);

    boolean isReverseLayout(RecyclerView recyclerView);

}
