package com.duc.chatting.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.duc.chatting.R;
import com.duc.chatting.home.adapters.RecentConservationAdapter;

public class SwipeHelper  extends ItemTouchHelper.SimpleCallback{

    private final Context context;
    private final Drawable iconLeft;
    private final Drawable iconRight;
    private final Paint backgroundPaint;
    private final SwipeListener listener;
    public SwipeHelper(Context context, SwipeListener listener) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.context = context;
        this.listener = listener;
        this.iconLeft = ContextCompat.getDrawable(context, R.drawable.baseline_report_24); // Icon chỉnh sửa
        this.iconRight = ContextCompat.getDrawable(context, R.drawable.baseline_report_24); // Icon xóa

        backgroundPaint = new Paint();
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();

        if (direction == ItemTouchHelper.RIGHT) {
            listener.onEdit(position); // Xử lý khi vuốt sang phải (Edit)
        } else if (direction == ItemTouchHelper.LEFT) {
            listener.onReport(position); // Xử lý khi vuốt sang trái (Delete)
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        int iconMargin = (itemView.getHeight() - iconLeft.getIntrinsicHeight()) / 2;
        int maxSwipeDistance = itemView.getWidth() / 4; // Vuốt tối đa 1/4 chiều rộng
        if (dX > maxSwipeDistance) dX = maxSwipeDistance;
        if (dX < -maxSwipeDistance) dX = -maxSwipeDistance;
        backgroundPaint.setColor(ContextCompat.getColor(context, R.color.AcliceBlue));
//        if (dX > 0) { // Vuốt sang phải
//            canvas.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom(), backgroundPaint);
//
//            int iconLeftPos = itemView.getLeft() + iconMargin;
//            int iconRightPos = iconLeftPos + iconLeft.getIntrinsicWidth();
//            iconLeft.setBounds(iconLeftPos, itemView.getTop() + iconMargin, iconRightPos, itemView.getBottom() - iconMargin);
//            iconLeft.draw(canvas);
//        }
         if (dX < 0) { // Vuốt sang trái

            canvas.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), backgroundPaint);

            int iconRightPos = itemView.getRight() - iconMargin;
            int iconLeftPos = iconRightPos - iconRight.getIntrinsicWidth();
            iconRight.setBounds(iconLeftPos, itemView.getTop() + iconMargin, iconRightPos, itemView.getBottom() - iconMargin);
            iconRight.draw(canvas);
        }

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

    }
    public interface SwipeListener {
        void onEdit(int position);
        void onReport(int position);
    }
}
