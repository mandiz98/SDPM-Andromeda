package se.ju.students.axam1798.andromeda.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import se.ju.students.axam1798.andromeda.R;
import se.ju.students.axam1798.andromeda.models.Event;
import se.ju.students.axam1798.andromeda.models.User;

public class HistoryListAdapter extends ArrayAdapter<Event> {

    private Context m_context;
    private Event[] m_events;
    private int m_resourceLayout;

    public HistoryListAdapter(@NonNull Context context, int resource, @NonNull Event[] objects) {
        super(context, resource, objects);
        this.m_context = context;
        this.m_events = objects;
        this.m_resourceLayout = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(m_context);
            view = layoutInflater.inflate(m_resourceLayout, null);
        }

        Event event = getItem(position);
        if(event == null)
            return view;

        if(m_resourceLayout == android.R.layout.simple_list_item_1) {
            TextView text1 = view.findViewById(android.R.id.text1);
            switch(event.getEventKey()) {
                case 4010: {
                    text1.setText("");
                    break;
                }
            }
        }

        return view;
    }
}
