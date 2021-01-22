package com.jobtick.android.adapers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jobtick.android.activities.ProfileActivity;
import com.jobtick.android.activities.ReportActivity;
import com.jobtick.android.utils.ConstantKey;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.jobtick.android.R;
import android.annotation.SuppressLint;

import com.jobtick.android.models.CommentModel;
import com.jobtick.android.models.OfferModel;
import com.jobtick.android.models.QuestionModel;
import com.jobtick.android.utils.Constant;
import com.jobtick.android.utils.ImageUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.jobtick.android.utils.ConstantKey.KEY_COMMENT_REPORT;


public class PublicChatListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    /*
     * Note:
     * In OfferChatList
     * i am added OfferListModel Model Class
     * because reply btn i want work both side
     * */
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;


    private final Context context;
    private Boolean isInPublicChat;
    private OnItemClickListener mOnItemClickListener;
    private OfferModel offerModel;
    private QuestionModel questionModel;
    private boolean isOfferModel = true;


    public void addExtraItems(OfferModel item, boolean isOfferModel) {
        this.offerModel = item;
        this.isOfferModel = isOfferModel;
    }

    public void addExtraItems(QuestionModel item, boolean isOfferModel) {
        this.questionModel = item;
        this.isOfferModel = isOfferModel;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, OfferModel obj, int position, String action);

        void onItemClick(View view, QuestionModel obj, int position, String action);

        void onItemClick(View view, CommentModel obj, int position, String action);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    private boolean isLoaderVisible = false;
    private final List<CommentModel> mItems;

    public PublicChatListAdapter(Context context, List<CommentModel> mItems,Boolean isInPublicChat) {
        this.mItems = mItems;
        this.context = context;
        this.isInPublicChat=isInPublicChat;
    }
    public PublicChatListAdapter(Context context, List<CommentModel> mItems) {
        this.mItems = mItems;
        this.context = context;
        this.isInPublicChat=false;
    }
    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer_chat, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == mItems.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public void addItems(List<CommentModel> mItems) {
        this.mItems.addAll(mItems);
        notifyDataSetChanged();
    }

    public void addItem(CommentModel mItem) {
        this.mItems.add(mItem);
        notifyDataSetChanged();

        notifyItemInserted(mItems.size());
    }

    public void addLoading() {
        isLoaderVisible = true;
        this.mItems.add(new CommentModel());
        notifyItemInserted(this.mItems.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = mItems.size() - 1;
        CommentModel item = getItem(position);
        if (item != null) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    private CommentModel getItem(int position) {
        return mItems.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_avatar)
        CircularImageView imgAvatar;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_name)
        TextView txtName;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_created_date)
        TextView txtCreatedDate;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_message)
        TextView txtMessage;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_more_less)
        TextView txtMoreLess;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_file)
        ImageView imgFile;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.lyt_btn_reply)
        LinearLayout lytBtnReply;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.lyt_btn_more)
        LinearLayout lytBtnMore;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.card_img_file)
        CardView cardImgFile;


/*        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.textViewOptions)
        TextView textViewOptions;*/

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.ivFlag)
        LinearLayout ivFlag;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        protected void clear() {
        }


        @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
        public void onBind(int position) {
            super.onBind(position);
            CommentModel item = mItems.get(position);
            if (item.getUser().getAvatar() != null)
                ImageUtil.displayImage(imgAvatar, item.getUser().getAvatar().getThumbUrl(), null);
            txtName.setText(item.getUser().getName());
            txtMessage.setText(item.getCommentText());
            txtCreatedDate.setText(item.getCreatedAt());


            txtMessage.post(() -> {
                int lineCount = txtMessage.getLineCount();
                Timber.e(String.valueOf(lineCount));
                if (lineCount > Constant.MAX_LINE_TEXTVIEW_MORE_4) {
                    // view.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE);
                    lytBtnMore.setVisibility(View.VISIBLE);
                    txtMoreLess.setText(item.getStrMore());
                    mItems.get(getAdapterPosition()).setIsUserPrefrenceToMore(true);
                    if (item.getIsUserPrefrenceToMore()) {
                        txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE_4);
                    }
                } else {
                    lytBtnMore.setVisibility(View.GONE);
                }
            });


        /*    txtMessage.setOnLayoutListener(new TextViewRegular.OnLayoutListener()

                {
                    @Override
                    public void onLayouted (TextView view){
                    int lineCount = view.getLineCount();
                    Log.e("valueline", "Number of lines is " + lineCount);
                    if (lineCount > Constant.MAX_LINE_TEXTVIEW_MORE) {
                       // view.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE);
                        lytBtnMore.setVisibility(View.VISIBLE);
                        txtMoreLess.setText(item.getStrMore());
                        mItems.get(getAdapterPosition()).setIsUserPrefrenceToMore(true);
                        if (item.getIsUserPrefrenceToMore()) {
                     //       txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE);
                        }
                    } else {
                        lytBtnMore.setVisibility(View.GONE);
                    }

                }
                });*/


            if (item.getAttachments() != null && item.getAttachments().

                    size() != 0) {
                cardImgFile.setVisibility(View.VISIBLE);
                ImageUtil.displayImage(imgFile, item.getAttachments().get(0).getModalUrl(), null);
            } else {
                cardImgFile.setVisibility(View.GONE);
            }
            if(isInPublicChat){
                lytBtnReply.setVisibility(View.GONE);
            }else {
                lytBtnReply.setVisibility(View.VISIBLE);
            }

            ivFlag.setOnClickListener(view -> {

               /* //creating a popup menutextViewOptions
                PopupMenu popup = new PopupMenu(context, ivFlag);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_report);
                //adding click listener
                popup.setOnMenuItemClickListener(item1 -> {
                    switch (item1.getItemId()) {
                        case R.id.action_report:

                            break;
                    }
                    return false;
                });
                //displaying the popup
                popup.show();*/
                Bundle bundle = new Bundle();
                Intent intent = new Intent(context, ReportActivity.class);
                bundle.putString("key", KEY_COMMENT_REPORT);
                bundle.putInt(ConstantKey.commentId, item.getId());
                intent.putExtras(bundle);
                context.startActivity(intent);


            });

            lytBtnReply.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    if (isOfferModel) {
                        mOnItemClickListener.onItemClick(v, offerModel, getAdapterPosition(), "reply");
                    } else {
                        mOnItemClickListener.onItemClick(v, questionModel, getAdapterPosition(), "reply");
                    }
                }
            });

            imgAvatar.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("id",item.getUser().getId());
                context.startActivity(intent);
            });

            txtName.setOnClickListener(v -> {
                imgAvatar.performClick();
            });

            lytBtnMore.setOnClickListener(v -> {
                if (item.getStrMore().equalsIgnoreCase("More")) {
                    txtMessage.setMaxLines(Integer.MAX_VALUE);
                    lytBtnMore.setVisibility(View.VISIBLE);
                    txtMoreLess.setText("Less");
                    mItems.get(getAdapterPosition()).setStrMore("Less");
                    item.setStrMore("Less");
                } else {
                    txtMessage.setMaxLines(Constant.MAX_LINE_TEXTVIEW_MORE_4);
                    lytBtnMore.setVisibility(View.VISIBLE);
                    txtMoreLess.setText("More");
                    mItems.get(getAdapterPosition()).setStrMore("More");
                    item.setStrMore("More");
                }
            });
        }
    }

   /* private String toggleLayoutExpand(String show, View view) {
        toggleArrow(show, view);
       /* if (show.equalsIgnoreCase("Less")) {

            ViewAnimation.expand(lyt_expand);
        } else {
            ViewAnimation.collapse(lyt_expand);
        }*/
  //      return show;
  //  }


    public void toggleArrow(String show, View view) {
        toggleArrow(show, view, true);
    }

    public void toggleArrow(String show, View view, boolean delay) {
        if (show.equalsIgnoreCase("Less")) {
            view.animate().setDuration(delay ? 200 : 0).rotation(180);
        } else {
            view.animate().setDuration(delay ? 200 : 0).rotation(0);
        }
    }


    public static class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void clear() {
        }
    }
}