package com.iyangcong.reader.fragment;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.iyangcong.reader.R;
import com.iyangcong.reader.base.BaseFragment;
import com.orhanobut.logger.Logger;

import org.geometerplus.android.fbreader.ZLTreeAdapter;
import org.geometerplus.android.util.ViewUtil;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.tree.ZLTree;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TocFragment extends BaseFragment {

    private static final int PROCESS_TREE_ITEM_ID = 0;
    private static final int READ_BOOK_ITEM_ID = 1;

    private ZLTree<?> mySelectedItem;

    private TOCAdapter myAdapter;

    @BindView(R.id.lv_toc)
    ListView lvToc;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_toc, container, false);
        ButterKnife.bind(this, view);

        final FBReaderApp fbreader = (FBReaderApp) ZLApplication.Instance();
        final TOCTree root = fbreader.Model.TOCTree;
        myAdapter = new TOCAdapter(root);
        TOCTree treeToSelect = fbreader.getCurrentTOCElement();
        myAdapter.selectItem(treeToSelect);
        mySelectedItem = treeToSelect;
        lvToc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final TOCTree tree = (TOCTree) myAdapter.getItem(i);
                Logger.d("sdsadda");
                myAdapter.runTreeItem(tree);
            }
        });
        return view;
    }

    @Override
    protected void initData() {

    }

    private final class TOCAdapter extends ZLTreeAdapter {
        TOCAdapter(TOCTree root) {
            super(lvToc, root);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            final int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
            final TOCTree tree = (TOCTree) getItem(position);
            if (tree.hasChildren()) {
                menu.setHeaderTitle(tree.getText());
                final ZLResource resource = ZLResource.resource("tocView");
                menu.add(0, PROCESS_TREE_ITEM_ID, 0, resource.getResource(isOpen(tree) ? "collapseTree" : "expandTree").getValue());
                menu.add(0, READ_BOOK_ITEM_ID, 0, resource.getResource("readText").getValue());
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = (convertView != null) ? convertView :
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.toc_tree_item, parent, false);
            final TOCTree tree = (TOCTree) getItem(position);
            view.setBackgroundColor(tree == mySelectedItem ? 0xebebeb : 0);
            setIcon(ViewUtil.findImageView(view, R.id.toc_tree_item_icon), tree);
            String headTitle = tree.getText();
            headTitle = headTitle.replaceAll("</.*>", "");
            headTitle = headTitle.replaceAll("<.*>", "");
            String headTitleTmp = headTitle.replaceAll("#","");
            int textNum = headTitle.length()-headTitleTmp.length();
            switch (textNum){
                case 0:
                    //一级目录
                    ViewUtil.findTextView(view, R.id.toc_tree_item_text).setTextColor(getResources().getColor(R.color.text_color_black));
                    break;
                case 1:
                    ViewUtil.findTextView(view, R.id.toc_tree_item_text).setTextColor(getResources().getColor(R.color.text_color));
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                    ViewUtil.findTextView(view, R.id.toc_tree_item_text).setTextColor(getResources().getColor(R.color.text_color_gray));
                    break;
            }
            ViewUtil.findTextView(view, R.id.toc_tree_item_text).setTextSize(18-textNum*1);
            ViewUtil.findTextView(view, R.id.toc_tree_item_text).setPadding(textNum*35,0,0,0);
            ViewUtil.findTextView(view, R.id.toc_tree_item_text).setText(headTitleTmp);
            return view;
        }

        void openBookText(TOCTree tree) {
            final TOCTree.Reference reference = tree.getReference();
            if (reference != null) {
                getActivity().finish();
                final FBReaderApp fbreader = (FBReaderApp) ZLApplication.Instance();
                fbreader.addInvisibleBookmark();
                fbreader.BookTextView.gotoPosition(reference.ParagraphIndex, 0, 0);
                fbreader.showBookTextView();
                fbreader.storePosition();
            }
        }

        @Override
        protected boolean runTreeItem(ZLTree<?> tree) {
            if (super.runTreeItem(tree)) {
                return true;
            }
            openBookText((TOCTree) tree);
            return true;
        }
    }

}
