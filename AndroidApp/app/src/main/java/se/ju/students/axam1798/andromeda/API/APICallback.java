package se.ju.students.axam1798.andromeda.API;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class APICallback<T> implements Callback<T> {

    private Context m_context;
    private AlertDialog.Builder m_alertDialogBuilder;
    private AlertDialog m_loadingDialog;

    public APICallback(Context context) {
        this.m_context = context;

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(m_context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(m_context);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(m_context);
        tvText.setText("Loading ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        this.m_alertDialogBuilder = new AlertDialog.Builder(m_context)
                .setCancelable(false)
                .setView(ll);

        this.m_loadingDialog = m_alertDialogBuilder.create();
        this.m_loadingDialog.show();
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if(response.isSuccessful())
            this.onSuccess(call, response, response.body());
        else
            this.onError(
                    call,
                    response,
                    response.errorBody() == null ? null : APIClient.decodeError(response.errorBody())
            );
        this.m_loadingDialog.dismiss();
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        this.m_loadingDialog.dismiss();
    }

    public abstract void onSuccess(Call<T> call, Response<T> response, T decodedBody);
    public abstract void onError(Call<T> call, Response<T> response, APIError error);
}
