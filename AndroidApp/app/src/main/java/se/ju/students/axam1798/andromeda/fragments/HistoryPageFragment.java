package se.ju.students.axam1798.andromeda.fragments;

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_history_page,
                container,
                false
        );

        ((TextView)rootView.findViewById(R.id.history_page_title)).setText(m_title);
        ArrayAdapter<Event> adapter = new ArrayAdapter<>(
                rootView.getContext(),
                m_list_view_layout_id,
                m_events
        );
        ((ListView)rootView.findViewById(R.id.history_page_list_view)).setAdapter(adapter);

        return rootView;
    }

}
