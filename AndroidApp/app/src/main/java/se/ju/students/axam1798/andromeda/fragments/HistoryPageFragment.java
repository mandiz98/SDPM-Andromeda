package se.ju.students.axam1798.andromeda.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import se.ju.students.axam1798.andromeda.R;
import se.ju.students.axam1798.andromeda.adapters.HistoryListAdapter;
import se.ju.students.axam1798.andromeda.models.Event;

public class HistoryPageFragment extends Fragment {

    private static final String EVENTS_KEY = "EVENTS";
    private static final String TITLE_KEY = "TITLE";
    private static final String LIST_VIEW_LAYOUT_ID_KEY = "LIST_VIEW_LAYOUT_ID";

    public static HistoryPageFragment newInstance(String title, List<Event> events, int listViewLayoutId) {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();

        bundle.putString(EVENTS_KEY, gson.toJson(events));
        bundle.putString(TITLE_KEY, title);
        bundle.putInt(LIST_VIEW_LAYOUT_ID_KEY, listViewLayoutId);

        HistoryPageFragment fragment = new HistoryPageFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private String m_title;
    private List<Event> m_events;
    private int m_list_view_layout_id;
    private Gson gson;

    private ViewGroup m_rootView;
    private ListView m_listView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gson = new Gson();

        Bundle args = getArguments();
        String eventsListJson = args.getString(EVENTS_KEY, "[]");
        m_events = Arrays.asList(gson.fromJson(eventsListJson, Event[].class));
        m_title = args.getString(TITLE_KEY, "No title");
        m_list_view_layout_id = args.getInt(LIST_VIEW_LAYOUT_ID_KEY, android.R.layout.simple_list_item_1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_history_page,
                container,
                false
        );

        ((TextView)m_rootView.findViewById(R.id.history_page_title)).setText(m_title);
        HistoryListAdapter adapter = new HistoryListAdapter(
                m_rootView.getContext(),
                m_list_view_layout_id,
                m_events.toArray(new Event[0])
        );
        m_listView = m_rootView.findViewById(R.id.history_page_list_view);
        m_listView.setAdapter(adapter);

        return m_rootView;
    }

    public void setEvents(List<Event> events) {
        this.m_events = events;
        updateListViewAdapter();
    }

    public void updateListViewAdapter() {
        HistoryListAdapter adapter = new HistoryListAdapter(
                m_rootView.getContext(),
                m_list_view_layout_id,
                m_events.toArray(new Event[0])
        );
        m_listView.setAdapter(adapter);
    }

}
