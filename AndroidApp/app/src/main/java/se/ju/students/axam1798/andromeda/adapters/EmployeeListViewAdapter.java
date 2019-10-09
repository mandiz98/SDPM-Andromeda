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
import se.ju.students.axam1798.andromeda.models.User;

public class EmployeeListViewAdapter extends ArrayAdapter<User> {

    private Context m_context;
    private User[] m_users;
    private int m_resourceLayout;

    public EmployeeListViewAdapter(@NonNull Context context, int resource, @NonNull User[] objects) {
        super(context, resource, objects);
        this.m_context = context;
        this.m_users = objects;
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

        User user = getItem(position);
        if(user == null)
            return view;

        if(m_resourceLayout == android.R.layout.simple_list_item_1) {
            TextView text1 = view.findViewById(android.R.id.text1);
            text1.setText("("+user.getId()+") " + user.getRFID());
            text1.setTextColor(view.getResources().getColor(R.color.colorWhite));
        }

        return view;
    }
}
