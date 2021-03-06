package com.android.example.studyStation.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.example.studyStation.DefinedData.CourseLabel;
import com.android.example.studyStation.DefinedData.UserInfo;
import com.android.example.studyStation.R;
import com.android.example.studyStation.appLogic.Logic;
import com.android.example.studyStation.ui.uiSupport.courseListRecyclerAdapter;

import java.util.List;

import static com.android.example.studyStation.DefinedData.UserInfo.token;

/**
 * Created by AmmarRabie on 25/10/2017.
 */

public class CourseListFragment extends Fragment {

    private getCourseListTask mTask = null;

    private RecyclerView mRecyclerView;

    private String[] DataError = {null, null};

    public CourseListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_course_list, container, false);
        mRecyclerView = root.findViewById(R.id.fragmentCourseList_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        if (mTask == null) {
            mTask = new getCourseListTask(root.getContext());
            mTask.execute(UserInfo.email);
        }
        return root;
    }

    class getCourseListTask extends AsyncTask<String, Void, List<CourseLabel>> implements courseListRecyclerAdapter.OnItemClickListener {

        private Context mContext;
        private courseListRecyclerAdapter mCourseListRecyclerAdapter = null;

        public getCourseListTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected List<CourseLabel> doInBackground(String... strings) {
            return Logic.getCourseList(DataError, token);
        }

        @Override
        protected void onPostExecute(List<CourseLabel> returnedCourseList) {
            super.onPostExecute(returnedCourseList);
            mTask = null;

            if (DataError[0] == null && returnedCourseList == null) {
                Toast.makeText(getContext(), DataError[1] + "\ncan't reach server", Toast.LENGTH_SHORT).show();
                return;
            }
            if (DataError[0] != null) {
                Toast.makeText(getContext(), DataError[0], Toast.LENGTH_SHORT).show();
                return;
            }
            mCourseListRecyclerAdapter = new courseListRecyclerAdapter(mContext, returnedCourseList);
            mCourseListRecyclerAdapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(mCourseListRecyclerAdapter);
        }


        @Override
        public void onItemClick(int positionInTheList, RecyclerView.ViewHolder clickedViewHolder) {
            // if there is a task operates wait and dont perform the click
            if (mTask != null)
                return;
            Intent playCourseNotes = new Intent(getContext(), com.android.example.studyStation.ui.activities.PlayCourseActivity.class);
            String depName = ((String) clickedViewHolder.itemView.getTag(1));
            String uniName = ((String) clickedViewHolder.itemView.getTag(2));


            String code = ((String) clickedViewHolder.itemView.getTag(3));


            playCourseNotes.putExtra("depName", depName);
            playCourseNotes.putExtra("uniName", uniName);
            playCourseNotes.putExtra("code", code);

            startActivity(playCourseNotes);
        }
    }

}
